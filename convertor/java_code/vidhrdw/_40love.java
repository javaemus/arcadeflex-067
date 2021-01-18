/*
*	Video Driver for Forty-Love
*/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class _40love
{
	
	/*
	*	variables
	*/
	
	unsigned char *fortyl_video_ctrl;
	
	static UINT8 fortyl_flipscreen,fortyl_pix_redraw;
	static UINT8 fortyl_xoffset = 128;
	
	static data8_t *fortyl_pixram1;
	static data8_t *fortyl_pixram2;
	
	static struct mame_bitmap *pixel_bitmap1;
	static struct mame_bitmap *pixel_bitmap2;
	
	static struct tilemap *background;
	
	int fortyl_pix_color[4];
	
	/*
	*	color prom decoding
	*/
	
	PALETTE_INIT( fortyl )
	{
		int i;
	
		for (i = 0;i < Machine->drv->total_colors;i++)
		{
			int bit0,bit1,bit2,bit3,r,g,b;
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			
			/* green component */
			bit0 = (color_prom.read(Machine->drv->total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(Machine->drv->total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(Machine->drv->total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(Machine->drv->total_colors)>> 3) & 0x01;
			g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			
			/* blue component */
			bit0 = (color_prom.read(2*Machine->drv->total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(2*Machine->drv->total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(2*Machine->drv->total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(2*Machine->drv->total_colors)>> 3) & 0x01;
			b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			
			palette_set_color(i,r,g,b);
	
			color_prom++;
		}
	}
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	/*
	colorram format (2 bytes per one tilemap character line, 8 pixels height):
	
		offset 0	x... ....	x scroll (1 MSB bit)
		offset 0	.xxx x...	tile bank (see code below for banking formula)
		offset 0	.... .xxx	tiles color (one color code per whole tilemap line)
	
		offset 1	xxxx xxxx	x scroll (8 LSB bits)
	*/
	
	static void get_bg_tile_info(int tile_index)
	{
		int tile_number = videoram.read(tile_index);
		int tile_attrib = colorram.read((tile_index/64)*2);
		int tile_h_bank = (tile_attrib&0x40)<<3;	/* 0x40->0x200 */
		int tile_l_bank = (tile_attrib&0x18)<<3;	/* 0x10->0x80, 0x08->0x40 */
	
		int code = tile_number;
		if ((tile_attrib & 0x20) && (code >= 0xc0))
			code = (code & 0x3f) | tile_l_bank | 0x100;
		code |= tile_h_bank;
	
		SET_TILE_INFO(	0,
				code,
				tile_attrib & 0x07,
				0)
	}
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	VIDEO_START( fortyl )
	{
		fortyl_pixram1 = auto_malloc(0x4000);
		fortyl_pixram2 = auto_malloc(0x4000);
	
		pixel_bitmap1 = auto_bitmap_alloc(256,256);
		pixel_bitmap2 = auto_bitmap_alloc(256,256);
	
		background  = tilemap_create(get_bg_tile_info, tilemap_scan_rows,TILEMAP_TRANSPARENT, 8,8,64,32);
	
		if (!background || !fortyl_pixram1 || !fortyl_pixram2 || !pixel_bitmap1 || !pixel_bitmap2)
			return 1;
	
		tilemap_set_scroll_rows(background,32);
		tilemap_set_transparent_pen(background,0);
	
		return 0;
	}
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	static int pixram_sel;
	
	static void fortyl_set_scroll_x(int offset)
	{
		int	i = offset & ~1;
		int x = ((colorram.read(i)& 0x80) << 1) | colorram.read(i+1);	/* 9 bits signed */
	
		if (fortyl_flipscreen)
			x += 0x51;
		else
			x -= 0x50;
	
		x &= 0x1ff;
		if (x&0x100) x -= 0x200;				/* sign extend */
	
		tilemap_set_scrollx(background, offset/2, x);
	}
	
	public static WriteHandlerPtr fortyl_pixram_sel_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int offs;
		int f = data & 0x01;
	
		pixram_sel = (data & 0x04) >> 2;
	
		if (fortyl_flipscreen != f)
		{
			fortyl_flipscreen = f;
			flip_screen_set(fortyl_flipscreen);
			fortyl_pix_redraw = 1;
	
			for (offs=0;offs<32;offs++)
				fortyl_set_scroll_x(offs*2);
		}
	} };
	
	public static ReadHandlerPtr fortyl_pixram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (pixram_sel)
			return fortyl_pixram2[offset];
		else
			return fortyl_pixram1[offset];
	} };
	
	static void fortyl_plot_pix(int offset)
	{
		int x,y,i,c,d1,d2;
	
	
		x = (offset & 0x1f)*8;
		y = (offset >> 5) & 0xff;
	
		if (pixram_sel)
		{
			d1 = fortyl_pixram2[offset];
			d2 = fortyl_pixram2[offset + 0x2000];
		}
		else
		{
			d1 = fortyl_pixram1[offset];
			d2 = fortyl_pixram1[offset + 0x2000];
		}
	
		for (i=0;i<8;i++)
		{
			c = ((d2>>i)&1) + ((d1>>i)&1)*2;
			if (pixram_sel)
				plot_pixel(pixel_bitmap2, x+i, y, fortyl_pix_color[c]);
			else
				plot_pixel(pixel_bitmap1, x+i, y, fortyl_pix_color[c]);
		}
	}
	
	public static WriteHandlerPtr fortyl_pixram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (pixram_sel)
			fortyl_pixram2[offset] = data;
		else
			fortyl_pixram1[offset] = data;
	
		fortyl_plot_pix(offset & 0x1fff);
	} };
	
	
	public static WriteHandlerPtr fortyl_bg_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if( videoram.read(offset)!=data )
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(background,offset);
		}
	} };
	public static ReadHandlerPtr fortyl_bg_videoram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return videoram.read(offset);
	} };
	
	public static WriteHandlerPtr fortyl_bg_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if( colorram.read(offset)!=data )
		{
			int i;
	
			colorram.write(offset,data);
			for (i=(offset/2)*64; i<(offset/2)*64+64; i++)
				tilemap_mark_tile_dirty(background,i);
	
			fortyl_set_scroll_x(offset);
		}
	} };
	public static ReadHandlerPtr fortyl_bg_colorram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return colorram.read(offset);
	} };
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	/*
	spriteram format (4 bytes per sprite):
	
		offset	0	xxxxxxxx	y position
	
		offset	1	x.......	flip Y
		offset	1	.x......	flip X
		offset	1	..xxxxxx	gfx code (6 LSB bits)
	
		offset	2	...xx...	gfx code (2 MSB bits)
		offset	2	.....xxx	color code
		offset	2	???.....	??? (not used, always 0)
	
		offset	3	xxxxxxxx	x position
	*/
	
	static void draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int offs;
	
		/* spriteram #1 */
		for (offs = 0; offs < spriteram_size; offs += 4)
		{
			int code,color,sx,sy,flipx,flipy;
	
			sx = spriteram.read(offs+3);
			sy = spriteram.read(offs+0)+1;
	
			if (fortyl_flipscreen)
				sx = 240 - sx;
			else
				sy = 242 - sy;
	
			code = (spriteram.read(offs+1)& 0x3f) + ((spriteram.read(offs+2)& 0x18) << 3);
			flipx = ((spriteram.read(offs+1)& 0x40) >> 6) ^ fortyl_flipscreen;
			flipy = ((spriteram.read(offs+1)& 0x80) >> 7) ^ fortyl_flipscreen;
			color = (spriteram.read(offs+2)& 0x07) + 0x08;
	
			if (spriteram.read(offs+2)& 0xe0)
				color = rand()&0xf;
	
			drawgfx(bitmap,Machine->gfx[1],
					code,
					color,
					flipx,flipy,
					sx+fortyl_xoffset,sy,
					cliprect,TRANSPARENCY_PEN,0);
		}
	
		/* spriteram #2 */
		for (offs = 0; offs < spriteram_2_size; offs += 4)
		{
			int code,color,sx,sy,flipx,flipy;
	
			sx = spriteram_2.read(offs+3);
			sy = spriteram_2.read(offs+0)+1;
	
			if (fortyl_flipscreen)
				sx = 240 - sx;
			else
				sy = 242 - sy;
	
			code = (spriteram_2.read(offs+1)& 0x3f) + ((spriteram_2.read(offs+2)& 0x18) << 3);
			flipx = ((spriteram_2.read(offs+1)& 0x40) >> 6) ^ fortyl_flipscreen;
			flipy = ((spriteram_2.read(offs+1)& 0x80) >> 7) ^ fortyl_flipscreen;
			color = (spriteram_2.read(offs+2)& 0x07) + 0x08;
	
			if (spriteram_2.read(offs+2)& 0xe0)
				color = rand()&0xf;
	
			drawgfx(bitmap,Machine->gfx[1],
					code,
					color,
					flipx,flipy,
					sx+fortyl_xoffset,sy,
					cliprect,TRANSPARENCY_PEN,0);
		}
	}
	
	static void draw_pixram( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int offs;
		int f = fortyl_flipscreen ^ 1;
	
		if (fortyl_pix_redraw)
		{
			fortyl_pix_redraw = 0;
	
			for (offs=0; offs<0x2000; offs++)
				fortyl_plot_pix(offs);
		}
	
		if (pixram_sel)
			copybitmap(bitmap,pixel_bitmap1,f,f,fortyl_xoffset,0,cliprect,TRANSPARENCY_NONE,0);
		else
			copybitmap(bitmap,pixel_bitmap2,f,f,fortyl_xoffset,0,cliprect,TRANSPARENCY_NONE,0);
	}
	
	VIDEO_UPDATE( fortyl )
	{
		draw_pixram(bitmap,cliprect);
	
		tilemap_set_scrolldy(background,-fortyl_video_ctrl[1]+1,-fortyl_video_ctrl[1]-1 );
		tilemap_draw(bitmap,cliprect,background,0,0);
	
		draw_sprites(bitmap,cliprect);
	}
}
