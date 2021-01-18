/*****************************************************************************

Quiz DNA no Hanran (c) 1992 Face
Quiz Gakuen Paradise (c) 1991 NMK
Quiz Gekiretsu Scramble (Gakuen Paradise 2) (c) 1993 Face

	Driver by Uki

*****************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class quizdna
{
	
	#define MCLK 16000000
	
	VIDEO_START( quizdna );
	VIDEO_UPDATE( quizdna );
	
	
	
	
	public static WriteHandlerPtr quizdna_rombank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data8_t *ROM = memory_region(REGION_CPU1);
		cpu_setbank(1,&ROM[0x10000+0x4000*(data & 0x3f)]);
	} };
	
	public static WriteHandlerPtr gekiretu_rombank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data8_t *ROM = memory_region(REGION_CPU1);
		cpu_setbank(1,&ROM[0x10000+0x4000*((data & 0x3f) ^ 0x0a)]);
	} };
	
	/****************************************************************************/
	
	public static Memory_ReadAddress quizdna_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress quizdna_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x9fff, quizdna_fg_ram_w ),
		new Memory_WriteAddress( 0xa000, 0xbfff, quizdna_bg_ram_w ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe1ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xe200, 0xefff, MWA_RAM ),
		new Memory_WriteAddress( 0xf000, 0xffff, paletteram_xBGR_RRRR_GGGG_BBBB_w, paletteram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress gekiretu_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x9fff, quizdna_fg_ram_w ),
		new Memory_WriteAddress( 0xa000, 0xbfff, quizdna_bg_ram_w ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xefff, paletteram_xBGR_RRRR_GGGG_BBBB_w, paletteram ),
		new Memory_WriteAddress( 0xf000, 0xf1ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xf200, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort quizdna_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x80, 0x80, input_port_2_r ),
		new IO_ReadPort( 0x81, 0x81, input_port_3_r ),
		new IO_ReadPort( 0x90, 0x90, input_port_4_r ),
		new IO_ReadPort( 0x91, 0x91, input_port_5_r ),
		new IO_ReadPort( 0xe0, 0xe0, YM2203_status_port_0_r ),
		new IO_ReadPort( 0xe1, 0xe1, YM2203_read_port_0_r ),
		new IO_ReadPort( 0xf0, 0xf0, OKIM6295_status_0_r ),
	
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort quizdna_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x02, 0x03, quizdna_bg_xscroll_w ),
		new IO_WritePort( 0x04, 0x04, quizdna_bg_yscroll_w ),
		new IO_WritePort( 0x05, 0x06, IOWP_NOP ), /* unknown */
		new IO_WritePort( 0xc0, 0xc0, quizdna_rombank_w ),
		new IO_WritePort( 0xd0, 0xd0, quizdna_screen_ctrl_w ),
		new IO_WritePort( 0xe0, 0xe0, YM2203_control_port_0_w ),
		new IO_WritePort( 0xe1, 0xe1, YM2203_write_port_0_w ),
		new IO_WritePort( 0xf0, 0xf0, OKIM6295_data_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort gakupara_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x01, quizdna_bg_xscroll_w ),
		new IO_WritePort( 0x02, 0x02, quizdna_bg_yscroll_w ),
		new IO_WritePort( 0x03, 0x04, IOWP_NOP ), /* unknown */
		new IO_WritePort( 0xc0, 0xc0, quizdna_rombank_w ),
		new IO_WritePort( 0xd0, 0xd0, quizdna_screen_ctrl_w ),
		new IO_WritePort( 0xe0, 0xe0, YM2203_control_port_0_w ),
		new IO_WritePort( 0xe1, 0xe1, YM2203_write_port_0_w ),
		new IO_WritePort( 0xf0, 0xf0, OKIM6295_data_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort gekiretu_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x02, 0x03, quizdna_bg_xscroll_w ),
		new IO_WritePort( 0x04, 0x04, quizdna_bg_yscroll_w ),
		new IO_WritePort( 0x05, 0x06, IOWP_NOP ), /* unknown */
		new IO_WritePort( 0xc0, 0xc0, gekiretu_rombank_w ),
		new IO_WritePort( 0xd0, 0xd0, quizdna_screen_ctrl_w ),
		new IO_WritePort( 0xe0, 0xe0, YM2203_control_port_0_w ),
		new IO_WritePort( 0xe1, 0xe1, YM2203_write_port_0_w ),
		new IO_WritePort( 0xf0, 0xf0, OKIM6295_data_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/****************************************************************************/
	
	static InputPortPtr input_ports_quizdna = new InputPortPtr(){ public void handler() { 
		PORT_START();   /* sw2 */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "9C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "8C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "7C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Unknown 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START();   /* sw3 */
		PORT_DIPNAME( 0x03, 0x02, "Timer" );
		PORT_DIPSETTING(    0x03, "Slow" );
		PORT_DIPSETTING(    0x02, "Normal" );
		PORT_DIPSETTING(    0x01, "Fast" );
		PORT_DIPSETTING(    0x00, "Very Fast" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x30, "Every 500k" );
		PORT_DIPSETTING(    0x20, "Every 1000k" );
		PORT_DIPSETTING(    0x10, "Every 2000k" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x40, 0x40, "Carat" );
		PORT_DIPSETTING(    0x40, "20" );
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPNAME( 0x80, 0x80, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Yes") );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_gakupara = new InputPortPtr(){ public void handler() { 
	    PORT_START();   /* sw2 */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "10 Coins/1 Credit" );
		PORT_DIPSETTING(    0x01, DEF_STR( "9C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "8C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "7C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x10, 0x00, "Demo Music" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x80, "Timer" );
		PORT_DIPSETTING(    0xc0, "Slow" );
		PORT_DIPSETTING(    0x80, "Normal" );
		PORT_DIPSETTING(    0x40, "Fast" );
		PORT_DIPSETTING(    0x00, "Very Fast" );
	
		PORT_START();   /* sw3 */
		PORT_SERVICE( 0x01, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x02, 0x02, "Unknown 3-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "Unknown 3-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Unknown 3-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Unknown 3-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Unknown 3-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Unknown 3-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Unknown 3-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_gekiretu = new InputPortPtr(){ public void handler() { 
		PORT_START();   /* dsw2 */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "9C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "8C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "7C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Unknown 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START();   /* dsw3 */
		PORT_DIPNAME( 0x03, 0x03, "Timer" );
		PORT_DIPSETTING(    0x03, "Slow" );
		PORT_DIPSETTING(    0x02, "Normal" );
		PORT_DIPSETTING(    0x01, "Fast" );
		PORT_DIPSETTING(    0x00, "Very Fast" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x04, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x10, 0x10, "Unknown 3-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Unknown 3-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Unknown 3-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Yes") );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	/****************************************************************************/
	
	static GfxLayout fglayout = new GfxLayout
	(
		16,8,     /* 16*8 characters */
		8192*2,   /* 16384 characters */
		1,        /* 1 bit per pixel */
		new int[] {0},
		new int[] { STEP16(0,1) },
		new int[] { STEP8(0,16) },
		16*8
	);
	
	static GfxLayout bglayout = new GfxLayout
	(
		8,8,        /* 8*8 characters */
		32768+1024, /* 32768+1024 characters */
		4,          /* 4 bits per pixel */
		new int[] {0,1,2,3},
		new int[] { STEP8(0,4) },
		new int[] { STEP8(0,32) },
		8*8*4
	);
	
	static GfxLayout objlayout = new GfxLayout
	(
		16,16,    /* 16*16 characters */
		8192+256, /* 8192+256 characters */
		4,        /* 4 bits per pixel */
		new int[] {0,1,2,3},
		new int[] { STEP16(0,4) },
		new int[] { STEP16(0,64) },
		16*16*4
	);
	
	static GfxDecodeInfo quizdna_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, fglayout,  0x7e0,  16 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0000, bglayout,  0x000, 128 ),
		new GfxDecodeInfo( REGION_GFX3, 0x0000, objlayout, 0x600,  32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static struct YM2203interface ym2203_interface =
	{
		1,
		MCLK/4,  /* 4.000 MHz */
		{ YM2203_VOL(40,10) },
		{ input_port_1_r },
		{ input_port_0_r },
		{ 0 },
		{ 0 }
	};
	
	static struct OKIM6295interface okim6295_interface =
	{
		1,
		{ MCLK/1024 },	/* 15.625KHz */
		{ REGION_SOUND1 },
		{ 30 }
	};
	
	
	static MACHINE_DRIVER_START( quizdna )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", Z80, MCLK/2) /* 8.000 MHz */
		MDRV_CPU_MEMORY(quizdna_readmem,quizdna_writemem)
		MDRV_CPU_PORTS(quizdna_readport,quizdna_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(8*8, 56*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(quizdna_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(quizdna)
		MDRV_VIDEO_UPDATE(quizdna)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( gakupara )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(quizdna)
	
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(quizdna_readport,gakupara_writeport)
	
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( gekiretu )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(quizdna)
	
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(quizdna_readmem,gekiretu_writemem)
		MDRV_CPU_PORTS(quizdna_readport,gekiretu_writeport)
	
	MACHINE_DRIVER_END
	
	
	/****************************************************************************/
	
	static RomLoadPtr rom_quizdna = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xd0000, REGION_CPU1, 0 );/* CPU */
		ROM_LOAD( "quiz2-pr.28",  0x00000,  0x08000, 0xa428ede4 );
		ROM_CONTINUE(             0x18000,  0x78000 );/* banked */
		/* empty */
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE );/* fg */
		ROM_LOAD( "quiz2.102",    0x00000,  0x20000, 0x62402ac9 );
		/* empty */
	
		ROM_REGION( 0x108000, REGION_GFX2, ROMREGION_DISPOSE );/* bg */
		ROM_LOAD( "quiz2-bg.100", 0x000000,  0x100000, 0xf1d0cac2 );
		/* empty */
	
		ROM_REGION( 0x108000, REGION_GFX3, ROMREGION_DISPOSE );/* obj */
		ROM_LOAD( "quiz2-ob.98",  0x000000,  0x100000, 0x682f19a6 );
		ROM_LOAD( "quiz2ob2.97",  0x100000,  0x008000, 0x03736b1a );
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "quiz2-sn.32",  0x000000,  0x040000, 0x1c044637 );
	
		ROM_REGION( 0x00020, REGION_USER1, 0 );/* fg control */
		ROM_LOAD( "quiz2.148",    0x000000,  0x000020, 0x91267e8a );
	
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gakupara = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xd0000, REGION_CPU1, 0 );/* CPU */
		ROM_LOAD( "u28.bin",  0x00000,  0x08000, 0x72124bb8 );
		ROM_CONTINUE(         0x18000,  0x78000 );            /* banked */
		ROM_LOAD( "u29.bin",  0x90000,  0x40000, 0x09f4948e );/* banked */
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE );/* fg */
		ROM_LOAD( "u102.bin", 0x00000,  0x20000, 0x62402ac9 );
		ROM_LOAD( "u103.bin", 0x20000,  0x20000, 0x38644251 );
	
		ROM_REGION( 0x108000, REGION_GFX2, ROMREGION_DISPOSE );/* bg */
		ROM_LOAD( "u100.bin", 0x000000,  0x100000, 0xf9d886ea );
		ROM_LOAD( "u99.bin",  0x100000,  0x008000, 0xac224d0a );
	
		ROM_REGION( 0x108000, REGION_GFX3, ROMREGION_DISPOSE );/* obj */
		ROM_LOAD( "u98.bin",  0x000000,  0x100000, 0xa6e8cb56 );
		ROM_LOAD( "u97.bin",  0x100000,  0x008000, 0x9dacd5c9 );
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "u32.bin",  0x000000,  0x040000, 0xeb03c535 );
	
		ROM_REGION( 0x00020, REGION_USER1, 0 );/* fg control */
		ROM_LOAD( "u148.bin", 0x000000,  0x000020, 0x971df9d2 );
	
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gekiretu = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xd0000, REGION_CPU1, 0 );/* CPU */
		ROM_LOAD( "quiz3-pr.28",  0x00000,  0x08000, 0xa761e86f );
		ROM_CONTINUE(             0x18000,  0x78000 );/* banked */
		/* empty */
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE );/* fg */
		ROM_LOAD( "quiz3.102",    0x00000,  0x20000, 0x62402ac9 );
		/* empty */
	
		ROM_REGION( 0x108000, REGION_GFX2, ROMREGION_DISPOSE );/* bg */
		ROM_LOAD( "quiz3-bg.100", 0x000000,  0x100000, 0xcb9272fd );
		/* empty */
	
		ROM_REGION( 0x108000, REGION_GFX3, ROMREGION_DISPOSE );/* obj */
		ROM_LOAD( "quiz3-ob.98",  0x000000,  0x100000, 0x01bed020 );
		/* empty */
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "quiz3-sn.32",  0x000000,  0x040000, 0x36dca582 );
	
		ROM_REGION( 0x00020, REGION_USER1, 0 );/* fg control */
		ROM_LOAD( "quiz3.148",    0x000000,  0x000020, 0x91267e8a );
	
	ROM_END(); }}; 
	
	public static GameDriver driver_gakupara	   = new GameDriver("1991"	,"gakupara"	,"quizdna.java"	,rom_gakupara,null	,machine_driver_gakupara	,input_ports_gakupara	,null	,ROT0	,	"NMK",  "Quiz Gakuen Paradise (Japan)" )
	public static GameDriver driver_quizdna	   = new GameDriver("1992"	,"quizdna"	,"quizdna.java"	,rom_quizdna,null	,machine_driver_quizdna	,input_ports_quizdna	,null	,ROT0	,	"Face", "Quiz DNA no Hanran (Japan)" )
	public static GameDriver driver_gekiretu	   = new GameDriver("1992"	,"gekiretu"	,"quizdna.java"	,rom_gekiretu,null	,machine_driver_gekiretu	,input_ports_gekiretu	,null	,ROT0	,	"Face", "Quiz Gekiretsu Scramble (Japan)" )
}
