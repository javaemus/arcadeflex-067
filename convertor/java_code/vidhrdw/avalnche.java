/***************************************************************************

	Atari Avalanche hardware

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class avalnche
{
	
	
	public static WriteHandlerPtr avalnche_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		videoram.write(offset,data);
	
		if (offset >= 0x200)
		{
			int x,y,i;
	
			x = 8 * (offset % 32);
			y = offset / 32;
	
			for (i = 0;i < 8;i++)
				plot_pixel(tmpbitmap,x+7-i,y,Machine->pens[(data >> i) & 1]);
		}
	} };
	
	
	VIDEO_UPDATE( avalnche )
	{
		if (get_vh_global_attribute_changed())
		{
			int offs;
	
			for (offs = 0;offs < videoram_size; offs++)
				avalnche_videoram_w(offs,videoram.read(offs));
		}
	
		/* copy the character mapped graphics */
		copybitmap(bitmap,tmpbitmap,0,0,0,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
	}
}
