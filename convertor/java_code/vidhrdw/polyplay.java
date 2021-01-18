/***************************************************************************

  Poly-Play
  (c) 1985 by VEB Polytechnik Karl-Marx-Stadt

  video hardware

  driver written by Martin Buchholz (buchholz@mail.uni-greifswald.de)

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class polyplay
{
	
	unsigned char *polyplay_characterram;
	static unsigned char dirtycharacter[256];
	
	
	
	PALETTE_INIT( polyplay )
	{
		palette_set_color(0,0x00,0x00,0x00);
		palette_set_color(1,0xff,0xff,0xff);
	
		palette_set_color(2,0x00,0x00,0x00);
		palette_set_color(3,0xff,0x00,0x00);
		palette_set_color(4,0x00,0xff,0x00);
		palette_set_color(5,0xff,0xff,0x00);
		palette_set_color(6,0x00,0x00,0xff);
		palette_set_color(7,0xff,0x00,0xff);
		palette_set_color(8,0x00,0xff,0xff);
		palette_set_color(9,0xff,0xff,0xff);
	}
	
	
	public static WriteHandlerPtr polyplay_characterram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (polyplay_characterram[offset] != data)
		{
			dirtycharacter[((offset / 8) & 0x7f) + 0x80] = 1;
	
			polyplay_characterram[offset] = data;
		}
	} };
	
	public static ReadHandlerPtr polyplay_characterram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return polyplay_characterram[offset];
	} };
	
	
	VIDEO_UPDATE( polyplay )
	{
		int offs;
	
	
		if (get_vh_global_attribute_changed())
		{
			memset(dirtybuffer,1,videoram_size);
		}
	
		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size - 1;offs >= 0;offs--)
		{
			int charcode;
	
	
			charcode = videoram.read(offs);
	
			if (dirtybuffer[offs] || dirtycharacter[charcode])
			{
				int sx,sy;
	
	
				/* index=0 -> 1 bit chr; index=1 -> 3 bit chr */
				if (charcode < 0x80) {
	
					/* ROM chr, no need for decoding */
	
					dirtybuffer[offs] = 0;
	
					sx = offs % 64;
					sy = offs / 64;
	
					drawgfx(tmpbitmap,Machine->gfx[0],
							charcode,
							0,
							0,0,
							8*sx,8*sy,
							&Machine->visible_area,TRANSPARENCY_NONE,0);
	
				}
				else {
					/* decode modified characters */
					if (dirtycharacter[charcode] == 1)
					{
						decodechar(Machine->gfx[1],charcode-0x80,polyplay_characterram,Machine->drv->gfxdecodeinfo[1].gfxlayout);
						dirtycharacter[charcode] = 2;
					}
	
	
					dirtybuffer[offs] = 0;
	
					sx = offs % 64;
					sy = offs / 64;
	
					drawgfx(tmpbitmap,Machine->gfx[1],
							charcode,
							0,
							0,0,
							8*sx,8*sy,
							&Machine->visible_area,TRANSPARENCY_NONE,0);
	
				}
			}
		}
		copybitmap(bitmap,tmpbitmap,0,0,0,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
	
	
		for (offs = 0;offs < 256;offs++)
		{
			if (dirtycharacter[offs] == 2) dirtycharacter[offs] = 0;
		}
	}
}
