/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class aeroboto
{
	
	
	data8_t *aeroboto_videoram;
	data8_t *aeroboto_hscroll, *aeroboto_vscroll, *aeroboto_tilecolor;
	data8_t *aeroboto_starx, *aeroboto_stary, *aeroboto_starcolor;
	
	static int aeroboto_charbank;
	
	static struct tilemap *bg_tilemap;
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static void get_tile_info(int tile_index)
	{
		unsigned char code = aeroboto_videoram[tile_index];
		SET_TILE_INFO(
				0,
				code + (aeroboto_charbank << 8),
				aeroboto_tilecolor[code],
				0)
	}
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	VIDEO_START( aeroboto )
	{
		bg_tilemap = tilemap_create(get_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,64);
	
		if (bg_tilemap == 0)
			return 1;
	
		tilemap_set_transparent_pen(bg_tilemap,0);
	
		tilemap_set_scroll_rows(bg_tilemap,64);
	
		return 0;
	}
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static ReadHandlerPtr aeroboto_in0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return readinputport(flip_screen ? 1 : 0);
	} };
	
	public static WriteHandlerPtr aeroboto_3000_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* bit 0 selects both flip screen and player1/player2 controls */
		flip_screen_set(data & 0x01);
	
		/* bit 1 = char bank select */
		if (aeroboto_charbank != ((data & 0x02) >> 1))
		{
			tilemap_mark_all_tiles_dirty(bg_tilemap);
			aeroboto_charbank = (data & 0x02) >> 1;
		}
	
		/* bit 2 = star field enable? */
	} };
	
	public static WriteHandlerPtr aeroboto_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (aeroboto_videoram[offset] != data)
		{
			aeroboto_videoram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap,offset);
		}
	} };
	
	public static WriteHandlerPtr aeroboto_tilecolor_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (aeroboto_tilecolor[offset] != data)
		{
			aeroboto_tilecolor[offset] = data;
			tilemap_mark_all_tiles_dirty(bg_tilemap);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
	{
		int offs;
	
		for (offs = 0;offs < spriteram_size;offs += 4)
		{
			int x = spriteram.read(offs+3);
			int y = 240 - spriteram.read(offs);
	
			if (flip_screen)
			{
				x = 248 - x;
				y = 240 - y;
			}
	
			drawgfx(bitmap, Machine->gfx[2],
					spriteram.read(offs+1),
					spriteram.read(offs+2)& 0x07,
					flip_screen, flip_screen,
					((x + 8) & 0xff) - 8, y,
					cliprect, TRANSPARENCY_PEN, 0);
		}
	}
	
	
	VIDEO_UPDATE( aeroboto )
	{
		int y;
	
	#if 0
		// draw star map (total guesswork)
		{
			int xoffs, yoffs, xend, yend, xdisp, ydisp, i, j;
			static unsigned int lx=0, ly=0, x,sx=0, sy=0;
			struct GfxElement *gfx;
			gfx = Machine->gfx[1];
	
			x = *aeroboto_starx;
			i = x - lx;
			lx = x;
			if (i<-128) i+=0x100; else if (i>127) i-=0x100;
			sx += i;
			i = (sx >> 3) & 0xff;
			xdisp = -(i & 7);
			xoffs = i >> 3;
			xend = xdisp + 256;
	
			y = *aeroboto_stary;
			i = y - ly;
			ly = y;
			if (i<-128) i+=0x100; else if (i>127) i-=0x100;
			if (*aeroboto_vscroll != 0xff) sy += i;
			i = (sy >> 3) & 0xff;
			j = (i >> 3) + 3;
			ydisp = 24 -(i & 7);
			yoffs = (j<<5) & 0x1ff;
			yend = ydisp + 224;
	
			j = *aeroboto_starcolor;
	
			for (y=ydisp; y<yend; y+=8)
			{
				for (x=xdisp; x<xend; xoffs++, x+=8)
				{
					i = yoffs + (xoffs & 0x1f);
					drawgfx(bitmap, gfx, i, j,
							0, 0, x, y, cliprect, TRANSPARENCY_NONE, 0);
				}
				yoffs = (yoffs + 32) & 0x1ff;
			}
		}
	#endif
	
		for (y = 0;y < 64; y++)
			tilemap_set_scrollx(bg_tilemap,y,aeroboto_hscroll[y]);
	
		tilemap_set_scrolly(bg_tilemap,0,*aeroboto_vscroll);
		tilemap_draw(bitmap,cliprect,bg_tilemap,TILEMAP_IGNORE_TRANSPARENCY,0);
	
		draw_sprites(bitmap,cliprect);
	
		/* note that the same tilemap is used twice with different vertical scroll */
		tilemap_set_scrolly(bg_tilemap,0,0);
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
	}
}
