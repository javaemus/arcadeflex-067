/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"


/***************************************************************************

  Convert the color PROMs into a more useable format.

***************************************************************************/

PALETTE_INIT( hanaawas )
{
	int i;
	#define TOTAL_COLORS(gfxn) (Machine->gfx[gfxn]->total_colors * Machine->gfx[gfxn]->color_granularity)
	#define COLOR(gfxn,offs) (colortable[Machine->drv->gfxdecodeinfo[gfxn].color_codes_start + offs])


	for (i = 0;i < Machine->drv->total_colors;i++)
	{
		int bit0,bit1,bit2,r,g,b;


		/* red component */
		bit0 = (*color_prom >> 0) & 0x01;
		bit1 = (*color_prom >> 1) & 0x01;
		bit2 = (*color_prom >> 2) & 0x01;
		r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
		/* green component */
		bit0 = (*color_prom >> 3) & 0x01;
		bit1 = (*color_prom >> 4) & 0x01;
		bit2 = (*color_prom >> 5) & 0x01;
		g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
		/* blue component */
		bit0 = 0;
		bit1 = (*color_prom >> 6) & 0x01;
		bit2 = (*color_prom >> 7) & 0x01;
		b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;

		palette_set_color(i,r,g,b);
		color_prom++;
	}

	color_prom += 0x10;
	/* color_prom now points to the beginning of the lookup table */


	/* character lookup table.  The 1bpp tiles really only use colors 0-0x0f and the
	   3bpp ones 0x10-0x1f */

	for (i = 0;i < TOTAL_COLORS(0)/8 ;i++)
	{
		COLOR(0,i*8+0) = color_prom[i*4+0x00] & 0x0f;
		COLOR(0,i*8+1) = color_prom[i*4+0x01] & 0x0f;
		COLOR(0,i*8+2) = color_prom[i*4+0x02] & 0x0f;
		COLOR(0,i*8+3) = color_prom[i*4+0x03] & 0x0f;
		COLOR(0,i*8+4) = color_prom[i*4+0x80] & 0x0f;
		COLOR(0,i*8+5) = color_prom[i*4+0x81] & 0x0f;
		COLOR(0,i*8+6) = color_prom[i*4+0x82] & 0x0f;
		COLOR(0,i*8+7) = color_prom[i*4+0x83] & 0x0f;
	}
}


WRITE_HANDLER( hanaawas_colorram_w )
{
	offs_t offs2;


	colorram[offset] = data;

	/* dirty both current and next offsets */
	offs2 = (offset + (flip_screen ? -1 : 1)) & 0x03ff;

	dirtybuffer[offset] = 1;
	dirtybuffer[offs2 ] = 1;
}


WRITE_HANDLER( hanaawas_portB_w )
{
	/* bit 7 is flip screen */
	flip_screen_set(~data & 0x80);
}

/***************************************************************************

  Draw the game screen in the given mame_bitmap.
  Do NOT call osd_update_display() from this function, it will be called by
  the main emulation engine.

***************************************************************************/

VIDEO_UPDATE( hanaawas )
{
	int offs,offs_adj;


	if (get_vh_global_attribute_changed())
	{
		memset(dirtybuffer, 1, videoram_size);
	}



	offs_adj = flip_screen ? 1 : -1;

	for (offs = videoram_size - 1; offs >= 0; offs--)
	{
		if (dirtybuffer[offs])
		{
			int sx,sy,col,code,bank,offs2;

			dirtybuffer[offs] = 0;
            sx = offs % 32;
			sy = offs / 32;

			if (flip_screen)
			{
				sx = 31 - sx;
				sy = 31 - sy;
			}

			/* the color is determined by the current color byte, but the bank is via the
			   previous one!!! */
			offs2 = (offs + offs_adj) & 0x03ff;

			col  = colorram[offs] & 0x1f;
			code = videoram[offs] + ((colorram[offs2] & 0x20) << 3);
			bank = (colorram[offs2] & 0x40) >> 6;

			drawgfx(tmpbitmap,Machine->gfx[bank],
					code,col,
					flip_screen,flip_screen,
					sx*8,sy*8,
					&Machine->visible_area,TRANSPARENCY_NONE,0);
        }
	}
	copybitmap(bitmap,tmpbitmap,0,0,0,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
}
