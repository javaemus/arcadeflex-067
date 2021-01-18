/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class bogeyman
{
	
	static int flipscreen;
	unsigned char *bogeyman_videoram;
	
	
	
	
	PALETTE_INIT( bogeyman )
	{
		int i;
	
		/* first 16 colors are RAM */
	
		for (i = 0;i < 256;i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			/* green component */
			bit0 = (color_prom.read(0)>> 3) & 0x01;
			bit1 = (color_prom.read(256)>> 0) & 0x01;
			bit2 = (color_prom.read(256)>> 1) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			/* blue component */
			bit0 = 0;
			bit1 = (color_prom.read(256)>> 2) & 0x01;
			bit2 = (color_prom.read(256)>> 3) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i+16,r,g,b);
			color_prom++;
		}
	}
	
	VIDEO_START( bogeyman )
	{
		dirtybuffer = auto_malloc(videoram_size);
		if (dirtybuffer == 0)
			return 1;
			
		memset(dirtybuffer,1,videoram_size);
		tmpbitmap = auto_bitmap_alloc(256,256);
		if (tmpbitmap == 0)
			return 1;
	
		return 0;
	}
	
	/******************************************************************************/
	
	public static WriteHandlerPtr bogeyman_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* RGB output is inverted */
		paletteram_BBGGGRRR_w(offset,~data);
	} };
	
	public static WriteHandlerPtr bogeyman_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		bogeyman_videoram[offset]=data;
		dirtybuffer[offset]=1;
	} };
	
	VIDEO_UPDATE( bogeyman )
	{
		int mx,my,offs,color,tile,bank,sx,sy,flipx,flipy,multi;
	
		flipscreen=0; /* For now */
	
		for (offs = 0; offs<0x100; offs++)
		{
			if (dirtybuffer[offs] | dirtybuffer[offs+0x100])
			{
				dirtybuffer[offs] = dirtybuffer[offs+0x100] = 0;
	
				sx=offs%16;
				sy=offs/16;
	
				bank=((bogeyman_videoram[offs+0x100] & 0x01) << 8) | bogeyman_videoram[offs];
				bank=(bank/0x80)+3;
				color=bogeyman_videoram[offs+0x100]>>1;
	
				drawgfx(tmpbitmap,Machine->gfx[bank],
						bogeyman_videoram[offs]&0x7f,
						color&7,
						flipscreen,flipscreen,
						16*sx,16*sy,
						0,TRANSPARENCY_NONE,0);
			}
		}
	
		copyscrollbitmap(bitmap,tmpbitmap,0,0,0,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
	
		/* Sprites */
		for (offs = 0;offs < spriteram_size;offs += 4)
		{
			if (spriteram.read(offs)& 0x01)
			{
				sx = 240 - spriteram.read(offs+3);
				sy = (240 - spriteram.read(offs+2)) & 0xff;
				flipx = spriteram.read(offs)& 0x04;
				flipy = 0;//spriteram.read(offs)& 0x02;
				multi=spriteram.read(offs)& 0x10;
				if (multi) sy-=16;
	
				if (1/*flipscreen*/)
				{
					sx = 240 - sx;
					//sy = 240 - sy;
					flipx = !flipx;
					//flipy = !flipy;
				}
	
				drawgfx(bitmap,Machine->gfx[2],
						spriteram.read(offs+1)+ ((spriteram.read(offs)& 0x40) << 2),	// Modified by T.Nogi 1999/10/25
						(spriteram.read(offs)& 0x08) >> 3,	// Modified by T.Nogi 1999/10/26
						flipx,flipy,
						sx,sy,
						&Machine->visible_area,TRANSPARENCY_PEN,0);
				if (multi)
					drawgfx(bitmap,Machine->gfx[2],
						spriteram.read(offs+1)+ 1 + ((spriteram.read(offs)& 0x40) << 2),	// Modified by T.Nogi 1999/10/25
						(spriteram.read(offs)& 0x08) >> 3,	// Modified by T.Nogi 1999/10/26
						flipx,flipy,
						sx,sy+16,
						&Machine->visible_area,TRANSPARENCY_PEN,0);
			}
		}
	
		/* Draw character tiles */
		for (offs = 0;offs < 0x400; offs ++)
		{
			mx=offs%32;
			my=offs/32;
			if (flipscreen) {mx=31-mx; my=31-my;}
			tile=videoram.read(offs)| ((videoram.read(offs+0x400)&3)<<8);
			bank=tile/0x200;
			if (tile == 0) continue;
	#if 0
			color=0;//(videoram.read(offs+0x400)&2)>>1;	// Modified by T.Nogi 1999/10/26
	#else
			color = 0;
			if (bank == 0)
			{
				if ((tile >= 0x002) && (tile < 0x036)) color = 1;
				if ((tile >= 0x10b) && (tile < 0x15b)) color = 1;
				if ((tile >= 0x178) && (tile < 0x1c7)) color = 1;
				if ((tile >= 0x1e0) && (tile < 0x1f3)) color = 1;
			} else {
				if ((tile >= (0x010+0x200)) && (tile < (0x17f+0x200))) color = 1;
			}
	#endif
			drawgfx(bitmap,Machine->gfx[bank],
					tile&0x1ff,
					color,
					flipscreen,flipscreen,
					8*mx,8*my,
					&Machine->visible_area,TRANSPARENCY_PEN,0);
		}
	}
}
