/***************************************************************************

	Atari Super Breakout hardware

	driver by Mike Balfour

	Games supported:
		* Sprint 1
		* Sprint 2

	Known issues:
		* none at this time

****************************************************************************

	Note:  I'm cheating a little bit with the paddle control.  The original
	game handles the paddle control as following.  The paddle is a potentiometer.
	Every VBlank signal triggers the start of a voltage ramp.  Whenever the
	ramp has the same value as the potentiometer, an NMI is generated.	In the
	NMI code, the current scanline value is used to calculate the value to
	put into location $1F in memory.  I cheat in this driver by just putting
	the paddle value directly into $1F, which has the same net result.

	If you have any questions about how this driver works, don't hesitate to
	ask.  - Mike Balfour (mab22@po.cwru.edu)

	CHANGES:
	MAB 05 MAR 99 - changed overlay support to use artwork functions

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class sbrkout
{
	
	
	/*************************************
	 *
	 *	Video overlay
	 *
	 *************************************/
	
	OVERLAY_START( sbrkout_overlay )
		OVERLAY_RECT( 208,   8, 248, 218, MAKE_ARGB(0x04,0x20,0x20,0xff) )
		OVERLAY_RECT( 176,   8, 208, 218, MAKE_ARGB(0x04,0xff,0x80,0x10) )
		OVERLAY_RECT( 144,   8, 176, 218, MAKE_ARGB(0x04,0x20,0xff,0x20) )
		OVERLAY_RECT(  96,   8, 144, 218, MAKE_ARGB(0x04,0xff,0xff,0x20) )
		OVERLAY_RECT(  16,   8,  24, 218, MAKE_ARGB(0x04,0x20,0x20,0xff) )
	OVERLAY_END
	
	
	
	/*************************************
	 *
	 *	Temporary sound hardware
	 *
	 *************************************/
	
	#define TIME_4V 4.075/4
	
	static unsigned char *sbrkout_sound;
	
	public static WriteHandlerPtr sbrkout_dac_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sbrkout_sound[offset]=data;
	} };
	
	
	static void sbrkout_tones_4V(int foo)
	{
		static int vlines=0;
	
		if ((*sbrkout_sound) & vlines)
			DAC_data_w(0,255);
		else
			DAC_data_w(0,0);
	
		vlines = (vlines+1) % 16;
	}
	
	
	static MACHINE_INIT( sbrkout )
	{
		timer_pulse(TIME_IN_MSEC(TIME_4V), 0, sbrkout_tones_4V);
	}
	
	
	/*************************************
	 *
	 *	Palette generation
	 *
	 *************************************/
	
	static PALETTE_INIT( sbrkout )
	{
		palette_set_color(0,0x00,0x00,0x00);
		palette_set_color(1,0xff,0xff,0xff);
	}
	
	
	
	/*************************************
	 *
	 *	Main CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x001f, 0x001f, input_port_6_r ), /* paddle value */
		new Memory_ReadAddress( 0x0000, 0x00ff, MRA_RAM ), /* Zero Page RAM */
		new Memory_ReadAddress( 0x0100, 0x01ff, MRA_RAM ), /* ??? */
		new Memory_ReadAddress( 0x0400, 0x077f, MRA_RAM ), /* Video Display RAM */
		new Memory_ReadAddress( 0x0828, 0x0828, sbrkout_select1_r ), /* Select 1 */
		new Memory_ReadAddress( 0x082f, 0x082f, sbrkout_select2_r ), /* Select 2 */
		new Memory_ReadAddress( 0x082e, 0x082e, input_port_5_r ), /* Serve Switch */
		new Memory_ReadAddress( 0x0830, 0x0833, sbrkout_read_DIPs_r ), /* DIP Switches */
		new Memory_ReadAddress( 0x0840, 0x0840, input_port_1_r ), /* Coin Switches */
		new Memory_ReadAddress( 0x0880, 0x0880, input_port_2_r ), /* Start Switches */
		new Memory_ReadAddress( 0x08c0, 0x08c0, input_port_3_r ), /* Self Test Switch */
		new Memory_ReadAddress( 0x0c00, 0x0c00, input_port_4_r ), /* Vertical Sync Counter */
		new Memory_ReadAddress( 0x2c00, 0x3fff, MRA_ROM ), /* PROGRAM */
		new Memory_ReadAddress( 0xfff0, 0xffff, MRA_ROM ), /* PROM8 for 6502 vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0011, 0x0011, sbrkout_dac_w, sbrkout_sound ), /* Noise Generation Bits */
		new Memory_WriteAddress( 0x0010, 0x0014, MWA_RAM, sbrkout_horiz_ram ), /* Horizontal Ball Position */
		new Memory_WriteAddress( 0x0018, 0x001d, MWA_RAM, sbrkout_vert_ram ), /* Vertical Ball Position / ball picture */
		new Memory_WriteAddress( 0x0000, 0x00ff, MWA_RAM ), /* WRAM */
		new Memory_WriteAddress( 0x0100, 0x01ff, MWA_RAM ), /* ??? */
		new Memory_WriteAddress( 0x0400, 0x07ff, videoram_w, videoram, videoram_size ), /* DISPLAY */
		new Memory_WriteAddress( 0x0c10, 0x0c11, sbrkout_serve_led_w ), /* Serve LED */
		new Memory_WriteAddress( 0x0c30, 0x0c31, sbrkout_start_1_led_w ), /* 1 Player Start Light */
		new Memory_WriteAddress( 0x0c40, 0x0c41, sbrkout_start_2_led_w ), /* 2 Player Start Light */
		new Memory_WriteAddress( 0x0c50, 0x0c51, MWA_RAM ), /* NMI Pot Reading Enable */
		new Memory_WriteAddress( 0x0c70, 0x0c71, MWA_RAM ), /* Coin Counter */
		new Memory_WriteAddress( 0x0c80, 0x0c80, MWA_NOP ), /* Watchdog */
		new Memory_WriteAddress( 0x0e00, 0x0e00, MWA_NOP ), /* IRQ Enable? */
		new Memory_WriteAddress( 0x1000, 0x1000, MWA_RAM ), /* LSB of Pot Reading */
		new Memory_WriteAddress( 0x2c00, 0x3fff, MWA_ROM ), /* PROM1-PROM8 */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	static InputPortPtr input_ports_sbrkout = new InputPortPtr(){ public void handler() { 
		PORT_START(); 		/* DSW - fake port, gets mapped to Super Breakout ports */
		PORT_DIPNAME( 0x03, 0x00, "Language" );
		PORT_DIPSETTING(	0x00, "English" );
		PORT_DIPSETTING(	0x01, "German" );
		PORT_DIPSETTING(	0x02, "French" );
		PORT_DIPSETTING(	0x03, "Spanish" );
		PORT_DIPNAME( 0x0C, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x0C, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x70, 0x00, "Extended Play" );/* P=Progressive, C=Cavity, D=Double */
		PORT_DIPSETTING(	0x10, "200P/200C/200D" );
		PORT_DIPSETTING(	0x20, "400P/300C/400D" );
		PORT_DIPSETTING(	0x30, "600P/400C/600D" );
		PORT_DIPSETTING(	0x40, "900P/700C/800D" );
		PORT_DIPSETTING(	0x50, "1200P/900C/1000D" );
		PORT_DIPSETTING(	0x60, "1600P/1100C/1200D" );
		PORT_DIPSETTING(	0x70, "2000P/1400C/1500D" );
		PORT_DIPSETTING(	0x00, "None" );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x80, "3" );
		PORT_DIPSETTING(	0x00, "5" );
	
		PORT_START(); 		/* IN0 */
		PORT_BIT ( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT ( 0x80, IP_ACTIVE_HIGH, IPT_COIN2 );
	
		PORT_START(); 		/* IN1 */
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 		/* IN2 */
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_TILT );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 		/* IN3 */
		PORT_BIT ( 0xFF, IP_ACTIVE_LOW, IPT_VBLANK );
	
		PORT_START(); 		/* IN4 */
		PORT_BIT ( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON1 );
	
		PORT_START(); 		/* IN5 */
		PORT_ANALOG( 0xff, 0x00, IPT_PADDLE | IPF_REVERSE, 50, 10, 0, 255 );
	
		PORT_START(); 		/* IN6 - fake port, used to set the game select dial */
		PORT_BITX(0x01, IP_ACTIVE_HIGH, IPT_BUTTON2, "Progressive", KEYCODE_E, IP_JOY_DEFAULT );
		PORT_BITX(0x02, IP_ACTIVE_HIGH, IPT_BUTTON3, "Double",      KEYCODE_D, IP_JOY_DEFAULT );
		PORT_BITX(0x04, IP_ACTIVE_HIGH, IPT_BUTTON4, "Cavity",      KEYCODE_C, IP_JOY_DEFAULT );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Graphics definitions
	 *
	 *************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		64,
		1,
		new int[] { 0 },
		new int[] { 4, 5, 6, 7, 0x200*8 + 4, 0x200*8 + 5, 0x200*8 + 6, 0x200*8 + 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	
	static GfxLayout balllayout = new GfxLayout
	(
		3,3,
		2,
		1,
		new int[] { 0 },
		new int[] { 0, 1, 2 },
		new int[] { 0*8, 1*8, 2*8 },
		3*8
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX2, 0, balllayout, 0, 1 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	/*************************************
	 *
	 *	Sound interfaces
	 *
	 *************************************/
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	
	
	/*************************************
	 *
	 *	Machine driver
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( sbrkout )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6502,375000) 	   /* 375 KHz? Should be 750KHz? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(sbrkout_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(sbrkout)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 28*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 0*8, 28*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2)
	
		MDRV_PALETTE_INIT(sbrkout)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(sbrkout)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadPtr rom_sbrkout = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "033453.c1",    0x2800, 0x0800, 0xa35d00e3 );
		ROM_LOAD( "033454.d1",    0x3000, 0x0800, 0xd42ea79a );
		ROM_LOAD( "033455.e1",    0x3800, 0x0800, 0xe0a6871c );
		ROM_RELOAD(               0xf800, 0x0800 );
	
		ROM_REGION( 0x0400, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "033280.p4",    0x0000, 0x0200, 0x5a69ce85 );
		ROM_LOAD( "033281.r4",    0x0200, 0x0200, 0x066bd624 );
	
		ROM_REGION( 0x0020, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "033282.k6",    0x0000, 0x0020, 0x6228736b );
	
		ROM_REGION( 0x0120, REGION_PROMS, 0 );
		ROM_LOAD( "006400.m2",    0x0000, 0x0100, 0xb8094b4c );/* sync (not used) */
		ROM_LOAD( "006401.e2",    0x0100, 0x0020, 0x857df8db );/* unknown */
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Driver initialization
	 *
	 *************************************/
	
	static DRIVER_INIT( sbrkout )
	{
		artwork_set_overlay(sbrkout_overlay);
	}
	
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_sbrkout	   = new GameDriver("1978"	,"sbrkout"	,"sbrkout.java"	,rom_sbrkout,null	,machine_driver_sbrkout	,input_ports_sbrkout	,init_sbrkout	,ROT270	,	"Atari", "Super Breakout" )
}
