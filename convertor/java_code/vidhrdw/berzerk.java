/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class berzerk
{
	
	
	data8_t *berzerk_magicram;
	
	static data8_t magicram_control = 0xff;
	static data8_t magicram_latch = 0xff;
	static data8_t collision = 0;
	
	
	PALETTE_INIT( berzerk )
	{
		int i;
	
		/* Simple 1-bit RGBI palette */
		for (i = 0; i < 16; i++)
		{
			int bk = (i & 8) ? 0x40 : 0x00;
			int r = (i & 1) ? 0xff : bk;
			int g = (i & 2) ? 0xff : bk;
			int b = (i & 4) ? 0xff : bk;
	
			palette_set_color(i,r,g,b);
		}
	}
	
	
	INLINE void copy_byte(UINT8 x, UINT8 y, data8_t data, data8_t col)
	{
		pen_t fore, back;
	
	
		fore  = Machine->pens[col >> 4];
		back  = Machine->pens[0];
	
		plot_pixel(tmpbitmap, x  , y, (data & 0x80) ? fore : back);
		plot_pixel(tmpbitmap, x+1, y, (data & 0x40) ? fore : back);
		plot_pixel(tmpbitmap, x+2, y, (data & 0x20) ? fore : back);
		plot_pixel(tmpbitmap, x+3, y, (data & 0x10) ? fore : back);
	
		fore  = Machine->pens[col & 0x0f];
	
		plot_pixel(tmpbitmap, x+4, y, (data & 0x08) ? fore : back);
		plot_pixel(tmpbitmap, x+5, y, (data & 0x04) ? fore : back);
		plot_pixel(tmpbitmap, x+6, y, (data & 0x02) ? fore : back);
		plot_pixel(tmpbitmap, x+7, y, (data & 0x01) ? fore : back);
	}
	
	
	public static WriteHandlerPtr berzerk_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offs_t coloroffset;
		UINT8 x, y;
	
	
		videoram.write(offset,data);
	
		/* Get location of color RAM for this offset */
		coloroffset = ((offset & 0xff80) >> 2) | (offset & 0x1f);
	
		y = offset >> 5;
		x = offset << 3;
	
	    copy_byte(x, y, data, colorram.read(coloroffset));
	} };
	
	
	public static WriteHandlerPtr berzerk_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int i;
		UINT8 x, y;
	
	
		colorram.write(offset,data);
	
		/* Need to change the affected pixels' colors */
	
		y = (offset >> 3) & 0xfc;
		x = offset << 3;
	
		for (i = 0; i < 4; i++, y++)
		{
			data8_t byte = videoram.read((y << 5) | (x >> 3));
	
			copy_byte(x, y, byte, data);
		}
	} };
	
	
	public static WriteHandlerPtr berzerk_magicram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data16_t data2;
		data8_t data3;
		int shift_amount;
	
	
		/* Bits 0-2 are the shift amount */
	
		shift_amount = magicram_control & 0x06;
	
		data2 = ((data >> shift_amount) | (magicram_latch << (8 - shift_amount))) & 0x1ff;
		data2 >>= (magicram_control & 0x01);
	
	
		data3 = (data8_t)data2;		/* mask off bit 8 */
	
	
		/* Bit 3 is the flip bit */
		if (magicram_control & 0x08)
		{
			data3 = BITSWAP8(data3,0,1,2,3,4,5,6,7);
		}
	
	
		magicram_latch = data;
	
	
		/* Check for collision */
		collision |= ((data3 & videoram.read(offset)) ? 0x80 : 0);
	
	
		switch (magicram_control & 0xf0)
		{
		case 0x00: 										 break;	/* No change */
		case 0x10: data3 |=  videoram.read(offset); 			 break;
		case 0x20: data3 |= ~videoram.read(offset); 			 break;
		case 0x30: data3  = 0xff;  						 break;
		case 0x40: data3 &=  videoram.read(offset); 			 break;
		case 0x50: data3  =  videoram.read(offset); 			 break;
		case 0x60: data3  = ~(data3 ^ videoram.read(offset)); break;
		case 0x70: data3  = ~data3 | videoram.read(offset); 	 break;
		case 0x80: data3 &= ~videoram.read(offset);			 break;
		case 0x90: data3 ^=  videoram.read(offset);			 break;
		case 0xa0: data3  = ~videoram.read(offset);			 break;
		case 0xb0: data3  = ~(data3 & videoram.read(offset)); break;
		case 0xc0: data3  = 0x00; 						 break;
		case 0xd0: data3  = ~data3 & videoram.read(offset); 	 break;
		case 0xe0: data3  = ~(data3 | videoram.read(offset)); break;
		case 0xf0: data3  = ~data3; 					 break;
		}
	
		berzerk_magicram[offset] = data3;
	
		berzerk_videoram_w(offset, data3);
	} };
	
	
	public static WriteHandlerPtr berzerk_magicram_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		magicram_control = data;
		magicram_latch = 0;
		collision = 0;
	} };
	
	
	public static ReadHandlerPtr berzerk_port_4e_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return input_port_3_r(0) | collision;
	} };
}
