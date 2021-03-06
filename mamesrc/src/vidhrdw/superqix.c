/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"



static int gfxbank;
static unsigned char *superqix_bitmapram,*superqix_bitmapram2,*superqix_bitmapram_dirty,*superqix_bitmapram2_dirty;
static struct mame_bitmap *tmpbitmap2;
int sqix_minx,sqix_maxx,sqix_miny,sqix_maxy;
int sqix_last_bitmap;
int sqix_current_bitmap;

static struct tilemap *superqix_tilemap;
extern data8_t *superqix_tilemap_ram;

/* the tilemap */



static void get_superqix_tile_info(int tile_index)
{
	int gfxr = (superqix_tilemap_ram[tile_index+0x400] & 0x04) ? 0 : (1 + gfxbank);
	int code = superqix_tilemap_ram[tile_index] + 256 * (superqix_tilemap_ram[tile_index+0x400] & 0x03);
	int colr = (superqix_tilemap_ram[tile_index+0x400] & 0xf0) >> 4;
	int prio = (superqix_tilemap_ram[tile_index+0x400] & 0x08) >> 3;

	tile_info.priority = prio;

	SET_TILE_INFO(
			gfxr,
			code,
			colr,
			0)
}

WRITE_HANDLER( superqix_tilemap_w )
{
	if (superqix_tilemap_ram[offset] != data)
	{
		superqix_tilemap_ram[offset] = data;
		tilemap_mark_tile_dirty(superqix_tilemap,offset&0x3ff);
	}
}


/***************************************************************************

  Start the video hardware emulation.

***************************************************************************/
VIDEO_START( superqix )
{
	/* palette RAM is accessed thorough I/O ports, so we have to */
	/* allocate it ourselves */
	if ((paletteram = auto_malloc(256 * sizeof(unsigned char))) == 0)
		return 1;

	if ((superqix_bitmapram = auto_malloc(0x7000 * sizeof(unsigned char))) == 0)
		return 1;

	if ((superqix_bitmapram2 = auto_malloc(0x7000 * sizeof(unsigned char))) == 0)
		return 1;

	if ((superqix_bitmapram_dirty = auto_malloc(0x7000 * sizeof(unsigned char))) == 0)
		return 1;
	memset(superqix_bitmapram_dirty,1,0x7000);

	if ((superqix_bitmapram2_dirty = auto_malloc(0x7000 * sizeof(unsigned char))) == 0)
		return 1;
	memset(superqix_bitmapram2_dirty,1,0x7000);

	if ((tmpbitmap2 = auto_bitmap_alloc(256, 256)) == 0)
		return 1;

	sqix_minx=0;sqix_maxx=127;sqix_miny=0;sqix_maxy=223;
	sqix_last_bitmap=0;

	superqix_tilemap = tilemap_create(get_superqix_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32, 32);
	if (!superqix_tilemap) return 1;

	tilemap_set_transparent_pen(superqix_tilemap,0);

	return 0;
}



READ_HANDLER( superqix_bitmapram_r )
{
	return superqix_bitmapram[offset];
}


WRITE_HANDLER( superqix_bitmapram_w )
{
	if(data != superqix_bitmapram[offset])
	{
		int x,y;
		superqix_bitmapram[offset] = data;
		superqix_bitmapram_dirty[offset] = 1;
		x=offset%128;
		y=offset/128;
		if(x<sqix_minx) sqix_minx=x;
		if(x>sqix_maxx) sqix_maxx=x;
		if(y<sqix_miny) sqix_miny=y;
		if(y>sqix_maxy) sqix_maxy=y;
	}
}

READ_HANDLER( superqix_bitmapram2_r )
{
	return superqix_bitmapram2[offset];
}

WRITE_HANDLER( superqix_bitmapram2_w )
{
	if(data != superqix_bitmapram2[offset])
	{
		int x,y;
		superqix_bitmapram2[offset] = data;
		superqix_bitmapram2_dirty[offset] = 1;
		x=offset%128;
		y=offset/128;
		if(x<sqix_minx) sqix_minx=x;
		if(x>sqix_maxx) sqix_maxx=x;
		if(y<sqix_miny) sqix_miny=y;
		if(y>sqix_maxy) sqix_maxy=y;
	}
}



WRITE_HANDLER( superqix_0410_w )
{
	int bankaddress;
	unsigned char *RAM = memory_region(REGION_CPU1);


	/* bits 0-1 select the tile bank */
	if (gfxbank != (data & 0x03))
	{
		gfxbank = data & 0x03;
		tilemap_mark_all_tiles_dirty (superqix_tilemap);
	}

	/* bit 2 controls bitmap 1/2 */
	sqix_current_bitmap=data&4;
	if(sqix_current_bitmap !=sqix_last_bitmap)
	{
		sqix_last_bitmap=sqix_current_bitmap;
		memset(superqix_bitmapram_dirty,1,0x7000);
		memset(superqix_bitmapram2_dirty,1,0x7000);
		sqix_minx=0;sqix_maxx=127;sqix_miny=0;sqix_maxy=223;
	}

	/* bit 3 enables NMI */
	interrupt_enable_w(offset,data & 0x08);

	/* bits 4-5 control ROM bank */
	bankaddress = 0x10000 + ((data & 0x30) >> 4) * 0x4000;
	cpu_setbank(1,&RAM[bankaddress]);
}



/***************************************************************************

  Draw the game screen in the given mame_bitmap.
  Do NOT call osd_update_display() from this function, it will be called by
  the main emulation engine.

***************************************************************************/
VIDEO_UPDATE( superqix )
{
	int offs,i;
	unsigned char pens[16];

	fillbitmap(bitmap,get_black_pen(),cliprect);
	/* does TILEMAP_IGNORE_TRANSPARENCY work with priority? */
	tilemap_draw(bitmap,cliprect,superqix_tilemap,TILEMAP_IGNORE_TRANSPARENCY,0);

	for(i=1;i<16;i++)
		pens[i]=Machine->pens[i];
	pens[0]=0;

	if(sqix_current_bitmap==0)		/* Bitmap 1 */
	{
		int x,y;

		for (y = sqix_miny;y <= sqix_maxy;y++)
		{
			for (x = sqix_minx;x <= sqix_maxx;x++)
			{
				int sx,sy,d;

				if(superqix_bitmapram_dirty[y*128+x])
				{
					superqix_bitmapram_dirty[y*128+x]=0;
					d = superqix_bitmapram[y*128+x];

					sx = 2*x;
					sy = y+16;

					plot_pixel(tmpbitmap2, sx    , sy, pens[d >> 4]);
					plot_pixel(tmpbitmap2, sx + 1, sy, pens[d & 0x0f]);
				}
			}
		}
	}
	else		/* Bitmap 2 */
	{
		int x,y;

		for (y = sqix_miny;y <= sqix_maxy;y++)
		{
			for (x = sqix_minx;x <= sqix_maxx;x++)
			{
				int sx,sy,d;

				if(superqix_bitmapram2_dirty[y*128+x])
				{
					superqix_bitmapram2_dirty[y*128+x]=0;
					d = superqix_bitmapram2[y*128+x];

					sx = 2*x;
					sy = y+16;

					plot_pixel(tmpbitmap2, sx    , sy, pens[d >> 4]);
					plot_pixel(tmpbitmap2, sx + 1, sy, pens[d & 0x0f]);
				}
			}
		}
	}
	copybitmap(bitmap,tmpbitmap2,0,0,0,0,&Machine->visible_area,TRANSPARENCY_PEN,0);

	/* Draw the sprites. Note that it is important to draw them exactly in this */
	/* order, to have the correct priorities. */
	for (offs = 0;offs < spriteram_size;offs += 4)
	{
		drawgfx(bitmap,Machine->gfx[5],
				spriteram[offs] + 256 * (spriteram[offs + 3] & 0x01),
				(spriteram[offs + 3] & 0xf0) >> 4,
				spriteram[offs + 3] & 0x04,spriteram[offs + 3] & 0x08,
				spriteram[offs + 1],spriteram[offs + 2],
				&Machine->visible_area,TRANSPARENCY_PEN,0);
	}


	/* redraw characters which have priority over the bitmap */
	tilemap_draw(bitmap,cliprect,superqix_tilemap,1,0);

	sqix_minx=1000;sqix_maxx=-1;sqix_miny=1000;sqix_maxy=-1;
}
