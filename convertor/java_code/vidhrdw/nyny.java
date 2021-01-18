/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class nyny
{
	
	extern unsigned char *nyny_videoram ;
	extern unsigned char *nyny_colourram ;
	
	static struct mame_bitmap *tmpbitmap1;
	static struct mame_bitmap *tmpbitmap2;
	
	
	/* used by nyny and spiders */
	PALETTE_INIT( nyny )
	{
		int i;
	
		for (i = 0;i < Machine->drv->total_colors;i++)
		{
			palette_set_color(i,((i >> 0) & 1) * 0xff,((i >> 1) & 1) * 0xff,((i >> 2) & 1) * 0xff);
		}
	}
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	VIDEO_START( nyny )
	{
		if ((tmpbitmap1 = auto_bitmap_alloc(Machine->drv->screen_width,Machine->drv->screen_height)) == 0)
			return 1;
	
		if ((tmpbitmap2 = auto_bitmap_alloc(Machine->drv->screen_width,Machine->drv->screen_height)) == 0)
			return 1;
	
		nyny_videoram = auto_malloc(0x4000);
		nyny_colourram = auto_malloc(0x4000);
	
		return 0;
	}
	
	/***************************************************************************
	  Stop the video hardware emulation.
	***************************************************************************/
	
	public static WriteHandlerPtr nyny_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		flip_screen_set(data);
	} };
	
	public static ReadHandlerPtr nyny_videoram0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return( nyny_videoram[offset] ) ;
	} };
	
	public static ReadHandlerPtr nyny_videoram1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return( nyny_videoram[offset+0x2000] ) ;
	} };
	
	public static ReadHandlerPtr nyny_colourram0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return( nyny_colourram[offset] ) ;
	} };
	
	public static ReadHandlerPtr nyny_colourram1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return( nyny_colourram[offset+0x2000] ) ;
	} };
	
	public static WriteHandlerPtr nyny_colourram0_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int x,y,z,d,v,c;
		nyny_colourram[offset] = data;
		v = nyny_videoram[offset] ;
	
		x = offset & 0x1f ;
		y = offset >> 5 ;
	
		d = data & 7 ;
		for ( z=0; z<8; z++ )
		{
			c = v & 1 ;
		  	plot_pixel( tmpbitmap1, x*8+z, y, Machine->pens[c*d]);
			v >>= 1 ;
		}
	} };
	
	public static WriteHandlerPtr nyny_videoram0_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int x,y,z,c,d;
		nyny_videoram[offset] = data;
		d = nyny_colourram[offset] & 7 ;
	
		x = offset & 0x1f ;
		y = offset >> 5 ;
	
		for ( z=0; z<8; z++ )
		{
			c = data & 1 ;
	  		plot_pixel( tmpbitmap1, x*8+z, y, Machine->pens[c*d]);
			data >>= 1 ;
		}
	} };
	
	public static WriteHandlerPtr nyny_colourram1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int x,y,z,d,v,c;
		nyny_colourram[offset+0x2000] = data;
		v = nyny_videoram[offset+0x2000] ;
	
		x = offset & 0x1f ;
		y = offset >> 5 ;
	
		d = data & 7 ;
		for ( z=0; z<8; z++ )
		{
			c = v & 1 ;
		  	plot_pixel( tmpbitmap2, x*8+z, y, Machine->pens[c*d]);
			v >>= 1 ;
		}
	
	} };
	
	public static WriteHandlerPtr nyny_videoram1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int x,y,z,c,d;
		nyny_videoram[offset+0x2000] = data;
		d = nyny_colourram[offset+0x2000] & 7 ;
	
		x = offset & 0x1f ;
		y = offset >> 5 ;
	
		for ( z=0; z<8; z++ )
		{
			c = data & 1 ;
		  	plot_pixel( tmpbitmap2, x*8+z, y, Machine->pens[c*d]);
			data >>= 1 ;
		}
	} };
	
	VIDEO_UPDATE( nyny )
	{
		copybitmap(bitmap,tmpbitmap2,flip_screen,flip_screen,0,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
		copybitmap(bitmap,tmpbitmap1,flip_screen,flip_screen,0,0,&Machine->visible_area,TRANSPARENCY_COLOR,0);
	}
}
