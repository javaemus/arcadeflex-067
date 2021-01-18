/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/
/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class flstory
{
	
	
	static struct tilemap *tilemap;
	static int char_bank,palette_bank,flipscreen,gfxctrl;
	
	UINT8 *flstory_scrlram;
	
	
	static void get_tile_info(int tile_index)
	{
		int code = videoram.read(tile_index*2);
		int attr = videoram.read(tile_index*2+1);
		int tile_number = code + ((attr & 0xc0) << 2) + 0x400 + 0x800 * char_bank;
		int flags = ((attr & 0x08) ? TILE_FLIPX : 0) | ((attr & 0x10) ? TILE_FLIPY : 0);
	//	tile_info.priority = (attr & 0x20) >> 5;
		SET_TILE_INFO(
				0,
				tile_number,
				attr & 0x0f,
				flags)
	}
	
	
	VIDEO_START( flstory )
	{
	    tilemap = tilemap_create( get_tile_info,tilemap_scan_rows,TILEMAP_SPLIT,8,8,32,32 );
	//	tilemap_set_transparent_pen( tilemap,15 );
		tilemap_set_transmask(tilemap,0,0x3fff,0xc000);
		tilemap_set_scroll_cols(tilemap,32);
	
		paletteram = auto_malloc(0x200);
		paletteram_2 = auto_malloc(0x200);
		return video_start_generic();
	}
	
	public static WriteHandlerPtr flstory_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		videoram.write(offset,data);
		tilemap_mark_tile_dirty(tilemap,offset/2);
	} };
	
	public static WriteHandlerPtr flstory_palette_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset & 0x100)
			paletteram_xxxxBBBBGGGGRRRR_split2_w((offset & 0xff) + (palette_bank << 8),data);
		else
			paletteram_xxxxBBBBGGGGRRRR_split1_w((offset & 0xff) + (palette_bank << 8),data);
	} };
	
	public static ReadHandlerPtr flstory_palette_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (offset & 0x100)
			return paletteram_2[ (offset & 0xff) + (palette_bank << 8) ];
		else
			return paletteram  [ (offset & 0xff) + (palette_bank << 8) ];
	} };
	
	public static WriteHandlerPtr flstory_gfxctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (gfxctrl == data)
			return;
		gfxctrl = data;
	
		flipscreen = (~data & 0x01);
		char_bank = (data & 0x10) >> 4;
		palette_bank = (data & 0x20) >> 5;
	
		flip_screen_set(flipscreen);
	
	//usrintf_showmessage("%04x: gfxctrl = %02x\n",activecpu_get_pc(),data);
	
	} };
	
	public static ReadHandlerPtr flstory_scrlram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return flstory_scrlram[offset];
	} };
	
	public static WriteHandlerPtr flstory_scrlram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		flstory_scrlram[offset] = data;
		tilemap_set_scrolly(tilemap, offset, data );
	} };
	
	/***************************************************************************
	
	  Draw the game screen in the given mame_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	
	void flstory_draw_sprites(struct mame_bitmap *bitmap, const struct rectangle *cliprect, int pri)
	{
		int i;
	
		for (i = 0; i < 0x20; i++)
		{
			int pr = spriteram.read(spriteram_size-1 -i);
			int offs = (pr & 0x1f) * 4;
	
			if ((pr & 0x80) == pri)
			{
				int code,sx,sy,flipx,flipy;
	
				code = spriteram.read(offs+2)+ ((spriteram.read(offs+1)& 0x30) << 4);
				sx = spriteram.read(offs+3);
				sy = spriteram.read(offs+0);
	
				if (flipscreen)
				{
					sx = (240 - sx) & 0xff ;
					sy = sy - 1 ;
				}
				else
					sy = 240 - sy - 1 ;
	
				flipx = ((spriteram.read(offs+1)&0x40)>>6)^flipscreen;
				flipy = ((spriteram.read(offs+1)&0x80)>>7)^flipscreen;
	
				drawgfx(bitmap,Machine->gfx[1],
						code,
						spriteram.read(offs+1)& 0x0f,
						flipx,flipy,
						sx,sy,
						cliprect,TRANSPARENCY_PEN,15);
				/* wrap around */
				if (sx > 240)
					drawgfx(bitmap,Machine->gfx[1],
							code,
							spriteram.read(offs+1)& 0x0f,
							flipx,flipy,
							sx-256,sy,
							cliprect,TRANSPARENCY_PEN,15);
			}
		}
	}
	
	VIDEO_UPDATE( flstory )
	{
		int offs;
	
		tilemap_draw(bitmap,cliprect,tilemap,TILEMAP_BACK,0);
		flstory_draw_sprites(bitmap,cliprect,0x00);
		tilemap_draw(bitmap,cliprect,tilemap,TILEMAP_FRONT,0);
		flstory_draw_sprites(bitmap,cliprect,0x80);
	
	
		for (offs = videoram_size - 2;offs >= 0;offs -= 2)
		{
			if (videoram.read(offs + 1)& 0x20)
			{
				int sx,sy,code;
	
				sx = (offs/2)%32;
				sy = (offs/2)/32;
				sy = sy*8 - flstory_scrlram[sx];
				sx = sx * 8;
	
				if (flipscreen)
				{
					sx = 248-sx;
					sy = 248-sy;
				}
				code = videoram.read(offs)+ ((videoram.read(offs + 1)& 0xc0) << 2) + 0x400 + 0x800 * char_bank;
	
				drawgfx(bitmap,Machine->gfx[0],
					code,
					(videoram.read(offs + 1)& 0x0f),
					( ( videoram.read(offs + 1)& 0x08 ) >> 3 ) ^ flipscreen,
					( ( videoram.read(offs + 1)& 0x10 ) >> 4 ) ^ flipscreen,
					sx,sy & 0xff,
					cliprect,TRANSPARENCY_PEN,15);
			}
		}
	
	}
}
