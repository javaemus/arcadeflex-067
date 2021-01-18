/****************************************************************************

Some Dynax games using the first version of their blitter

driver by Nicola Salmoria, blitter support based on work by Luca Elia

CPU:	Z80-A
Sound:	YM2203C
		M5205
OSC:	20.0000MHz
Video:	HD46505SP

---------------------------------------
Year + Game					Board
---------------------------------------
87 Hana Yayoi				D0208298L1
87 Hana Fubuki				D0602048
87 Untouchable				D0806298
---------------------------------------

Notes:
- In service mode, press "analyzer" (0) and "test" (F1) to see a gfx test

- hnfubuki doesn't have a service mode dip, press "analyzer" instead

- untoucha doesn't have it either; press "test" during boot for one kind
  of service menu, "analyzer" at any other time for another menu (including
  dip switch settings)

TODO:
- dips/inputs for all games

****************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class hnayayoi
{
	
	
	VIDEO_START( hnayayoi );
	VIDEO_START( untoucha );
	VIDEO_UPDATE( hnayayoi );
	
	
	
	
	static int keyb;
	
	public static ReadHandlerPtr keyboard_0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int res = 0x3f;
		int i;
	
		for (i = 0;i < 5;i++)
			if (~keyb & (1 << i)) res &= readinputport(4+i);
	
		return res;
	} };
	
	public static ReadHandlerPtr keyboard_1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		/* Player 2 not supported */
		return 0x3f;
	} };
	
	public static WriteHandlerPtr keyboard_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		keyb = data;
	} };
	
	
	public static WriteHandlerPtr adpcm_data_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		MSM5205_data_w(0,data);
	} };
	
	public static WriteHandlerPtr adpcm_vclk_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		MSM5205_vclk_w(0,data & 1);
	} };
	
	public static WriteHandlerPtr adpcm_reset_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		MSM5205_reset_w(0,data & 1);
	} };
	
	public static WriteHandlerPtr adpcm_reset_inv_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		MSM5205_reset_w(0,~data & 1);
	} };
	
	static MACHINE_INIT( hnayayoi )
	{
		/* start with the MSM5205 reset */
		MSM5205_reset_w(0,1);
	}
	
	
	
	public static Memory_ReadAddress hnayayoi_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x77ff, MRA_ROM ),
		new Memory_ReadAddress( 0x7800, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hnayayoi_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x77ff, MWA_ROM ),
		new Memory_WriteAddress( 0x7800, 0x7fff, MWA_RAM, generic_nvram, generic_nvram_size ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort hnayayoi_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x02, 0x02, YM2203_status_port_0_r ),
		new IO_ReadPort( 0x03, 0x03, YM2203_read_port_0_r ),
		new IO_ReadPort( 0x04, 0x04, input_port_2_r ),
		new IO_ReadPort( 0x41, 0x41, keyboard_0_r ),
		new IO_ReadPort( 0x42, 0x42, keyboard_1_r ),
		new IO_ReadPort( 0x43, 0x43, input_port_3_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort hnayayoi_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, YM2203_control_port_0_w ),
		new IO_WritePort( 0x01, 0x01, YM2203_write_port_0_w ),
		new IO_WritePort( 0x06, 0x06, adpcm_data_w ),
	//	new IO_WritePort( 0x08, 0x08, IOWP_NOP ),	// CRT Controller
	//	new IO_WritePort( 0x09, 0x09, IOWP_NOP ),	// CRT Controller
		new IO_WritePort( 0x0a, 0x0a, dynax_blitter_rev1_start_w ),
		new IO_WritePort( 0x0c, 0x0c, dynax_blitter_rev1_clear_w ),
		new IO_WritePort( 0x23, 0x23, adpcm_vclk_w ),
		new IO_WritePort( 0x24, 0x24, adpcm_reset_w ),
		new IO_WritePort( 0x40, 0x40, keyboard_w ),
		new IO_WritePort( 0x60, 0x61, hnayayoi_palbank_w ),
		new IO_WritePort( 0x62, 0x67, dynax_blitter_rev1_param_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress hnfubuki_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x77ff, MRA_ROM ),
		new Memory_ReadAddress( 0x7800, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0xfeff, MRA_ROM ),
		new Memory_ReadAddress( 0xff02, 0xff02, YM2203_status_port_0_r ),
		new Memory_ReadAddress( 0xff03, 0xff03, YM2203_read_port_0_r ),
		new Memory_ReadAddress( 0xff04, 0xff04, input_port_2_r ),
		new Memory_ReadAddress( 0xff41, 0xff41, keyboard_0_r ),
		new Memory_ReadAddress( 0xff42, 0xff42, keyboard_1_r ),
		new Memory_ReadAddress( 0xff43, 0xff43, input_port_3_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hnfubuki_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x77ff, MWA_ROM ),
		new Memory_WriteAddress( 0x7800, 0x7fff, MWA_RAM, generic_nvram, generic_nvram_size ),
		new Memory_WriteAddress( 0x8000, 0xfeff, MWA_ROM ),
		new Memory_WriteAddress( 0xff00, 0xff00, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0xff01, 0xff01, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0xff06, 0xff06, adpcm_data_w ),
	//	new Memory_WriteAddress( 0xff08, 0xff08, IOWP_NOP ),	// CRT Controller
	//	new Memory_WriteAddress( 0xff09, 0xff09, IOWP_NOP ),	// CRT Controller
		new Memory_WriteAddress( 0xff0a, 0xff0a, dynax_blitter_rev1_start_w ),
		new Memory_WriteAddress( 0xff0c, 0xff0c, dynax_blitter_rev1_clear_w ),
		new Memory_WriteAddress( 0xff23, 0xff23, adpcm_vclk_w ),
		new Memory_WriteAddress( 0xff24, 0xff24, adpcm_reset_inv_w ),
		new Memory_WriteAddress( 0xff40, 0xff40, keyboard_w ),
		new Memory_WriteAddress( 0xff60, 0xff61, hnayayoi_palbank_w ),
		new Memory_WriteAddress( 0xff62, 0xff67, dynax_blitter_rev1_param_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort hnfubuki_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort hnfubuki_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress untoucha_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x77ff, MRA_ROM ),
		new Memory_ReadAddress( 0x7800, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress untoucha_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x77ff, MWA_ROM ),
		new Memory_WriteAddress( 0x7800, 0x7fff, MWA_RAM, generic_nvram, generic_nvram_size ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort untoucha_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x11, 0x11, YM2203_status_port_0_r ),
		new IO_ReadPort( 0x51, 0x51, YM2203_read_port_0_r ),
		new IO_ReadPort( 0x16, 0x16, keyboard_0_r ),	// bit 7 = blitter busy flag
		new IO_ReadPort( 0x15, 0x15, keyboard_1_r ),
		new IO_ReadPort( 0x14, 0x14, input_port_3_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort untoucha_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x10, YM2203_control_port_0_w ),
		new IO_WritePort( 0x50, 0x50, YM2203_write_port_0_w ),
		new IO_WritePort( 0x13, 0x13, adpcm_data_w ),
	//	new IO_WritePort( 0x12, 0x12, IOWP_NOP ),	// CRT Controller
	//	new IO_WritePort( 0x52, 0x52, IOWP_NOP ),	// CRT Controller
		new IO_WritePort( 0x28, 0x28, dynax_blitter_rev1_start_w ),
		new IO_WritePort( 0x20, 0x20, dynax_blitter_rev1_clear_w ),
		new IO_WritePort( 0x31, 0x31, adpcm_vclk_w ),
		new IO_WritePort( 0x32, 0x32, adpcm_reset_inv_w ),
		new IO_WritePort( 0x17, 0x17, keyboard_w ),
		new IO_WritePort( 0x18, 0x19, hnayayoi_palbank_w ),
		new IO_WritePort( 0x1a, 0x1f, dynax_blitter_rev1_param_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_hnayayoi = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SPECIAL );// blitter busy flag
		PORT_SERVICE( 0x02, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* COIN */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_SERVICE, "Test", KEYCODE_F1, IP_JOY_NONE );/* Test */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );/* Analizer (Statistics) */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );/* "Note" ("Paper Money") = 10 Credits */
		PORT_BIT_IMPULSE( 0x40, IP_ACTIVE_LOW, IPT_COIN1, 2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	
		PORT_START(); 	/* P1 keyboard */
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 A",            KEYCODE_A,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 E",            KEYCODE_E,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 I",            KEYCODE_I,         IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 M",            KEYCODE_M,         IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Kan",          KEYCODE_LCONTROL,  IP_JOY_NONE );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 B",            KEYCODE_B,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 F",            KEYCODE_F,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 J",            KEYCODE_J,         IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 N",            KEYCODE_N,         IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Reach",        KEYCODE_LSHIFT,    IP_JOY_NONE );
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 Bet",          KEYCODE_3,         IP_JOY_NONE );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 C",            KEYCODE_C,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 G",            KEYCODE_G,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 K",            KEYCODE_K,         IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Chi",          KEYCODE_SPACE,     IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Ron",          KEYCODE_Z,         IP_JOY_NONE );
		PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 D",            KEYCODE_D,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 H",            KEYCODE_H,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 L",            KEYCODE_L,         IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Pon",          KEYCODE_LALT,      IP_JOY_NONE );
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 Last Chance",  KEYCODE_RALT,      IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Take Score",   KEYCODE_RCONTROL,  IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Double Up",    KEYCODE_RSHIFT,    IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Flip Flop",    KEYCODE_X,         IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Big",          KEYCODE_ENTER,     IP_JOY_NONE );
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 Small",        KEYCODE_BACKSPACE, IP_JOY_NONE );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_hnfubuki = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SPECIAL );// blitter busy flag
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* COIN */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_SERVICE, "Test", KEYCODE_F1, IP_JOY_NONE );/* Test */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );/* Analizer (Statistics) */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );/* "Note" ("Paper Money") = 10 Credits */
		PORT_BIT_IMPULSE( 0x40, IP_ACTIVE_LOW, IPT_COIN1, 2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	
		PORT_START(); 	/* P1 keyboard */
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 A",            KEYCODE_A,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 E",            KEYCODE_E,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 I",            KEYCODE_I,         IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 M",            KEYCODE_M,         IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Kan",          KEYCODE_LCONTROL,  IP_JOY_NONE );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 B",            KEYCODE_B,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 F",            KEYCODE_F,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 J",            KEYCODE_J,         IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 N",            KEYCODE_N,         IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Reach",        KEYCODE_LSHIFT,    IP_JOY_NONE );
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 Bet",          KEYCODE_3,         IP_JOY_NONE );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 C",            KEYCODE_C,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 G",            KEYCODE_G,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 K",            KEYCODE_K,         IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Chi",          KEYCODE_SPACE,     IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Ron",          KEYCODE_Z,         IP_JOY_NONE );
		PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 D",            KEYCODE_D,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 H",            KEYCODE_H,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 L",            KEYCODE_L,         IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Pon",          KEYCODE_LALT,      IP_JOY_NONE );
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 Last Chance",  KEYCODE_RALT,      IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Take Score",   KEYCODE_RCONTROL,  IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Double Up",    KEYCODE_RSHIFT,    IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Flip Flop",    KEYCODE_X,         IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Big",          KEYCODE_ENTER,     IP_JOY_NONE );
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 Small",        KEYCODE_BACKSPACE, IP_JOY_NONE );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_untoucha = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* not used */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* COIN */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_SERVICE, "Test", KEYCODE_F1, IP_JOY_NONE );/* Test */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );/* Analizer (Statistics) */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );/* "Note" ("Paper Money") = 10 Credits */
		PORT_BIT_IMPULSE( 0x40, IP_ACTIVE_LOW, IPT_COIN1, 2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	
		PORT_START(); 	/* P1 keyboard */
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 Hold 1",       KEYCODE_Z,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Hold 3",       KEYCODE_C,         IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Hold 5",       KEYCODE_B,         IP_JOY_NONE );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Bet",          KEYCODE_3,         IP_JOY_NONE );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 Take Score",   KEYCODE_RCONTROL,  IP_JOY_NONE );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 Hold 2",       KEYCODE_X,         IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Hold 4",       KEYCODE_V,         IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Cancel",       KEYCODE_N,         IP_JOY_NONE );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Deal",         KEYCODE_1,         IP_JOY_NONE );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Double Up",    KEYCODE_RSHIFT,    IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Flip Flop",    KEYCODE_F,         IP_JOY_NONE );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	
	static void irqhandler(int irq)
	{
	usrintf_showmessage("irq");
	//	cpu_set_irq_line(2,0,irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	
	static struct YM2203interface ym2203_interface =
	{
		1,			/* 1 chip */
		20000000/8,	/* 2.5 MHz???? */
		{ YM2203_VOL(80,25) },
		{ input_port_0_r },
		{ input_port_1_r },
		{ 0 },
		{ 0 },
		{ irqhandler }
	};
	
	struct MSM5205interface msm5205_interface =
	{
		1,
		384000,					/* ???? */
		{ 0 },					/* IRQ handler */
		{ MSM5205_SEX_4B },
		{ 100 }
	};
	
	
	
	static MACHINE_DRIVER_START( hnayayoi )
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", Z80, 20000000/4 )        /* 5 MHz ???? */
		MDRV_CPU_MEMORY(hnayayoi_readmem,hnayayoi_writemem)
		MDRV_CPU_PORTS(hnayayoi_readport,hnayayoi_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
		MDRV_CPU_PERIODIC_INT(nmi_line_pulse,8000)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(hnayayoi)
	
		MDRV_NVRAM_HANDLER(generic_0fill)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER|VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_SCREEN_SIZE(512, 256)
		MDRV_VISIBLE_AREA(0, 512-1, 0, 256-1)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(hnayayoi)
		MDRV_VIDEO_UPDATE(hnayayoi)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
		MDRV_SOUND_ADD(MSM5205, msm5205_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( hnfubuki )
		MDRV_IMPORT_FROM(hnayayoi)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(hnfubuki_readmem,hnfubuki_writemem)
		MDRV_CPU_PORTS(hnfubuki_readport,hnfubuki_writeport)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( untoucha )
		MDRV_IMPORT_FROM(hnayayoi)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(untoucha_readmem,untoucha_writemem)
		MDRV_CPU_PORTS(untoucha_readport,untoucha_writeport)
	
		MDRV_VIDEO_START(untoucha)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_hnayayoi = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "021.4a",     0x00000, 0x08000, 0xd9734da4 );
		ROM_LOAD( "022.3a",     0x08000, 0x08000, 0xe6be5af4 );
	
		ROM_REGION( 0x38000, REGION_GFX1, 0 );/* blitter data */
		ROM_LOAD( "023.8f",     0x00000, 0x08000, 0x81ae7317 );
		ROM_LOAD( "024.9f",     0x08000, 0x08000, 0x413ab77a );
		ROM_LOAD( "025.10f",    0x10000, 0x08000, 0x56d16426 );
		ROM_LOAD( "026.12f",    0x18000, 0x08000, 0xa99779d9 );
		ROM_LOAD( "027.8d",     0x20000, 0x08000, 0x209c149a );
		ROM_LOAD( "028.9d",     0x28000, 0x08000, 0x6981b043 );
		ROM_LOAD( "029.10d",    0x30000, 0x08000, 0xa266f1eb );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "r.16b",      0x0000, 0x0100, 0xb6e9ac04 );
		ROM_LOAD( "g.17b",      0x0100, 0x0100, 0xa595f310 );
		ROM_LOAD( "b.17c",      0x0200, 0x0100, 0xe33bd9ea );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hnfubuki = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "s1.s2c",     0x00000, 0x10000, 0xafe3179c );
	
		ROM_REGION( 0x40000, REGION_GFX1, 0 );/* blitter data */
		ROM_LOAD( "062.8f",     0x00000, 0x10000, 0x0d96a540 );
		ROM_LOAD( "063.9f",     0x10000, 0x10000, 0x14250093 );
		ROM_LOAD( "064.10f",    0x20000, 0x10000, 0x41546fb9 );
		ROM_LOAD( "0652.12f",   0x30000, 0x10000, 0xe7b54ea3 );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "r-16b",      0x0000, 0x0100, 0xe6fd8f5d );
		ROM_LOAD( "g-17b",      0x0100, 0x0100, 0x3f425f67 );
		ROM_LOAD( "b-17c",      0x0200, 0x0100, 0xd1f912e5 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_untoucha = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "b4.10b",     0x00000, 0x10000, 0x4df04e41 );
	
		ROM_REGION( 0x90000, REGION_GFX1, 0 );/* blitter data */
		ROM_LOAD( "081.10f",    0x00000, 0x10000, 0x36ba990d );
		ROM_LOAD( "082.12f",    0x10000, 0x10000, 0x2beb6277 );
		ROM_LOAD( "083.13f",    0x20000, 0x10000, 0xc3fed8ff );
		ROM_LOAD( "084.14f",    0x30000, 0x10000, 0x10de3aae );
		ROM_LOAD( "085.16f",    0x40000, 0x10000, 0x527e5879 );
		ROM_LOAD( "086.10h",    0x50000, 0x10000, 0xbe3f0a2e );
		ROM_LOAD( "087.12h",    0x60000, 0x10000, 0x35e072b7 );
		ROM_LOAD( "088.13h",    0x70000, 0x10000, 0x742cf3c0 );
		ROM_LOAD( "089.14h",    0x80000, 0x10000, 0xff497db1 );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "08r.9f",     0x0000, 0x0100, 0x308e65b4 );
		ROM_LOAD( "08g.8f",     0x0100, 0x0100, 0x349c3de3 );
		ROM_LOAD( "08b.7f",     0x0200, 0x0100, 0x2007435a );
	ROM_END(); }}; 
	
	
	static DRIVER_INIT( hnfubuki )
	{
		UINT8 *rom = memory_region(REGION_GFX1);
		int len = memory_region_length(REGION_GFX1);
		int i,j;
	
		/* interestingly, the blitter data has a slight encryption */
	
		/* swap address bits 4 and 5 */
		for (i = 0;i < len;i += 0x40)
		{
			for (j = 0;j < 0x10;j++)
			{
				UINT8 t = rom[i + j + 0x10];
				rom[i + j + 0x10] = rom[i + j + 0x20];
				rom[i + j + 0x20] = t;
			}
		}
	
		/* swap data bits 0 and 1 */
		for (i = 0;i < len;i++)
		{
			rom[i] = BITSWAP8(rom[i],7,6,5,4,3,2,0,1);
		}
	}
	
	
	public static GameDriver driver_hnayayoi	   = new GameDriver("1987"	,"hnayayoi"	,"hnayayoi.java"	,rom_hnayayoi,null	,machine_driver_hnayayoi	,input_ports_hnayayoi	,null	,ROT0	,	"Dyna Electronics", "Hana Yayoi (Japan)" )
	public static GameDriver driver_hnfubuki	   = new GameDriver("1987"	,"hnfubuki"	,"hnayayoi.java"	,rom_hnfubuki,driver_hnayayoi	,machine_driver_hnfubuki	,input_ports_hnfubuki	,init_hnfubuki	,ROT0	,	"Dynax", "Hana Fubuki [BET] (Japan)" )
	public static GameDriver driver_untoucha	   = new GameDriver("1987"	,"untoucha"	,"hnayayoi.java"	,rom_untoucha,null	,machine_driver_untoucha	,input_ports_untoucha	,null	,ROT0	,	"Dynax", "Untouchable (Japan)" )
}
