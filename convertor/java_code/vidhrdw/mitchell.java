/***************************************************************************

 Pang Video Hardware

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class mitchell
{
	
	
	/* Globals */
	size_t pang_videoram_size;
	unsigned char *pang_videoram;
	unsigned char *pang_colorram;
	
	/* Private */
	static unsigned char *pang_objram;           /* Sprite RAM */
	
	static struct tilemap *bg_tilemap;
	static int flipscreen;
	
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static void get_tile_info(int tile_index)
	{
		unsigned char attr = pang_colorram[tile_index];
		int code = pang_videoram[2*tile_index] + (pang_videoram[2*tile_index+1] << 8);
		SET_TILE_INFO(
				0,
				code,
				attr & 0x7f,
				(attr & 0x80) ? TILE_FLIPX : 0)
	}
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	VIDEO_START( pang )
	{
		pang_objram=NULL;
		paletteram=NULL;
	
	
		bg_tilemap = tilemap_create(get_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,64,32);
	
		if (bg_tilemap == 0)
			return 1;
	
		tilemap_set_transparent_pen(bg_tilemap,15);
	
		/*
			OBJ RAM
		*/
		pang_objram=auto_malloc(pang_videoram_size);
		if (pang_objram == 0)
			return 1;
		memset(pang_objram, 0, pang_videoram_size);
	
		/*
			Palette RAM
		*/
		paletteram = auto_malloc(2*Machine->drv->total_colors);
		if (paletteram == 0)
			return 1;
		memset(paletteram, 0, 2*Machine->drv->total_colors);
	
		return 0;
	}
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	/***************************************************************************
	  OBJ / CHAR RAM HANDLERS (BANK 0 = CHAR, BANK 1=OBJ)
	***************************************************************************/
	
	static int video_bank;
	
	public static WriteHandlerPtr pang_video_bank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* Bank handler (sets base pointers for video write) (doesn't apply to mgakuen) */
		video_bank = data;
	} };
	
	public static WriteHandlerPtr mgakuen_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (pang_videoram[offset] != data)
		{
			pang_videoram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap,offset/2);
		}
	} };
	
	public static ReadHandlerPtr mgakuen_videoram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return pang_videoram[offset];
	} };
	
	public static WriteHandlerPtr mgakuen_objram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		pang_objram[offset]=data;
	} };
	
	public static ReadHandlerPtr mgakuen_objram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return pang_objram[offset];
	} };
	
	public static WriteHandlerPtr pang_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (video_bank) mgakuen_objram_w(offset,data);
		else mgakuen_videoram_w(offset,data);
	} };
	
	public static ReadHandlerPtr pang_videoram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (video_bank) return mgakuen_objram_r(offset);
		else return mgakuen_videoram_r(offset);
	} };
	
	/***************************************************************************
	  COLOUR RAM
	****************************************************************************/
	
	public static WriteHandlerPtr pang_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (pang_colorram[offset] != data)
		{
			pang_colorram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap,offset);
		}
	} };
	
	public static ReadHandlerPtr pang_colorram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return pang_colorram[offset];
	} };
	
	/***************************************************************************
	  PALETTE HANDLERS (COLOURS: BANK 0 = 0x00-0x3f BANK 1=0x40-0xff)
	****************************************************************************/
	
	static int paletteram_bank;
	
	public static WriteHandlerPtr pang_gfxctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	logerror("PC %04x: pang_gfxctrl_w %02x\n",activecpu_get_pc(),data);
	{
		char baf[40];
		sprintf(baf,"%02x",data);
	//	usrintf_showmessage(baf);
	}
	
		/* bit 0 is unknown (used, maybe back color enable?) */
	
		/* bit 1 is coin counter */
		coin_counter_w(0,data & 2);
	
		/* bit 2 is flip screen */
		if (flipscreen != (data & 0x04))
		{
			flipscreen = data & 0x04;
			tilemap_set_flip(ALL_TILEMAPS,flipscreen ? (TILEMAP_FLIPY | TILEMAP_FLIPX) : 0);
		}
	
		/* bit 3 is unknown (used, e.g. marukin pulses it on the title screen) */
	
		/* bit 4 selects OKI M6295 bank */
		OKIM6295_set_bank_base(0, (data & 0x10) ? 0x40000 : 0x00000);
	
		/* bit 5 is palette RAM bank selector (doesn't apply to mgakuen) */
		paletteram_bank = data & 0x20;
	
		/* bits 6 and 7 are unknown, used in several places. At first I thought */
		/* they were bg and sprites enable, but this screws up spang (screen flickers */
		/* every time you pop a bubble). However, not using them as enable bits screws */
		/* up marukin - you can see partially built up screens during attract mode. */
	} };
	
	public static WriteHandlerPtr pang_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (paletteram_bank) paletteram_xxxxRRRRGGGGBBBB_w(offset + 0x800,data);
		else paletteram_xxxxRRRRGGGGBBBB_w(offset,data);
	} };
	
	public static ReadHandlerPtr pang_paletteram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (paletteram_bank) return paletteram_r(offset + 0x800);
		return paletteram_r(offset);
	} };
	
	public static WriteHandlerPtr mgakuen_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		paletteram_xxxxRRRRGGGGBBBB_w(offset,data);
	} };
	
	public static ReadHandlerPtr mgakuen_paletteram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return paletteram_r(offset);
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites(struct mame_bitmap *bitmap,const struct rectangle *cliprect)
	{
		int offs,sx,sy;
	
		/* the last entry is not a sprite, we skip it otherwise spang shows a bubble */
		/* moving diagonally across the screen */
		for (offs = 0x1000-0x40;offs >= 0;offs -= 0x20)
		{
			int code = pang_objram[offs];
			int attr = pang_objram[offs+1];
			int color = attr & 0x0f;
			sx = pang_objram[offs+3] + ((attr & 0x10) << 4);
			sy = ((pang_objram[offs+2] + 8) & 0xff) - 8;
			code += (attr & 0xe0) << 3;
			if (flipscreen)
			{
				sx = 496 - sx;
				sy = 240 - sy;
			}
			drawgfx(bitmap,Machine->gfx[1],
					 code,
					 color,
					 flipscreen,flipscreen,
					 sx,sy,
					 cliprect,TRANSPARENCY_PEN,15);
		}
	}
	
	VIDEO_UPDATE( pang )
	{
		fillbitmap(bitmap,Machine->pens[0],cliprect);
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
		draw_sprites(bitmap,cliprect);
	}
}
