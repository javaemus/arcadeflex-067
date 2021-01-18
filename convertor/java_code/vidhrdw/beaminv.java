/***************************************************************************

	Tekunon Kougyou Beam Invader hardware

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class beaminv
{
	
	
	/*************************************
	 *
	 *	Memory handlers
	 *
	 *************************************/
	
	public static WriteHandlerPtr beaminv_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UINT8 x,y;
		int i;
	
	
		videoram.write(offset,data);
	
		y = ~(offset >> 8 << 3);
		x = offset;
	
		for (i = 0; i < 8; i++)
		{
			plot_pixel(tmpbitmap, x, y, data & 0x01);
	
			y--;
			data >>= 1;
		}
	} };
}
