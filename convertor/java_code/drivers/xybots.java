/***************************************************************************

	Atari Xybots hardware

	driver by Aaron Giles

	Games supported:
		* Xybots (1987)

	Known bugs:
		* none at this time

****************************************************************************

	Memory map (TBA)

***************************************************************************/


/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class xybots
{
	
	
	
	/*************************************
	 *
	 *	Initialization & interrupts
	 *
	 *************************************/
	
	static void update_interrupts(void)
	{
		int newstate = 0;
	
		if (atarigen_video_int_state)
			newstate = 1;
		if (atarigen_sound_int_state)
			newstate = 2;
	
		if (newstate)
			cpu_set_irq_line(0, newstate, ASSERT_LINE);
		else
			cpu_set_irq_line(0, 7, CLEAR_LINE);
	}
	
	
	static MACHINE_INIT( xybots )
	{
		atarigen_eeprom_reset();
		atarigen_slapstic_reset();
		atarigen_interrupt_reset(update_interrupts);
		atarijsa_reset();
	}
	
	
	
	/*************************************
	 *
	 *	I/O handlers
	 *
	 *************************************/
	
	static READ16_HANDLER( special_port1_r )
	{
		static int h256 = 0x0400;
	
		int result = readinputport(1);
	
		if (atarigen_cpu_to_sound_ready) result ^= 0x0200;
		result ^= h256 ^= 0x0400;
		return result;
	}
	
	
	
	/*************************************
	 *
	 *	Main CPU memory handlers
	 *
	 *************************************/
	
	static MEMORY_READ16_START( main_readmem )
		{ 0x000000, 0x03ffff, MRA16_ROM },
		{ 0xff8000, 0xffbfff, MRA16_RAM },
		{ 0xffc000, 0xffc7ff, MRA16_RAM },
		{ 0xffd000, 0xffdfff, atarigen_eeprom_r },
		{ 0xffe000, 0xffe0ff, atarigen_sound_r },
		{ 0xffe100, 0xffe1ff, input_port_0_word_r },
		{ 0xffe200, 0xffe2ff, special_port1_r },
	MEMORY_END
	
	
	static MEMORY_WRITE16_START( main_writemem )
		{ 0x000000, 0x03ffff, MWA16_ROM },
		{ 0xff8000, 0xff8fff, atarigen_alpha_w, &atarigen_alpha },
		{ 0xff9000, 0xffadff, MWA16_RAM },
		{ 0xffae00, 0xffafff, atarimo_0_spriteram_w, &atarimo_0_spriteram },
		{ 0xffb000, 0xffbfff, atarigen_playfield_w, &atarigen_playfield },
		{ 0xffc000, 0xffc7ff, paletteram16_IIIIRRRRGGGGBBBB_word_w, &paletteram16 },
		{ 0xffd000, 0xffdfff, atarigen_eeprom_w, &atarigen_eeprom, &atarigen_eeprom_size },
		{ 0xffe800, 0xffe8ff, atarigen_eeprom_enable_w },
		{ 0xffe900, 0xffe9ff, atarigen_sound_w },
		{ 0xffea00, 0xffeaff, watchdog_reset16_w },
		{ 0xffeb00, 0xffebff, atarigen_video_int_ack_w },
		{ 0xffee00, 0xffeeff, atarigen_sound_reset_w },
	MEMORY_END
	
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	static InputPortPtr input_ports_xybots = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* ffe100 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BITX(0x0004, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2, "P2 Twist Right", KEYCODE_W, IP_JOY_DEFAULT );
		PORT_BITX(0x0008, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2, "P2 Twist Left", KEYCODE_Q, IP_JOY_DEFAULT );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 | IPF_8WAY );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER2 | IPF_8WAY );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_PLAYER2 | IPF_8WAY );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_PLAYER2 | IPF_8WAY );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BITX(0x0400, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1, "P1 Twist Right", KEYCODE_X, IP_JOY_DEFAULT );
		PORT_BITX(0x0800, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1, "P1 Twist Left", KEYCODE_Z, IP_JOY_DEFAULT );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER1 | IPF_8WAY );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER1 | IPF_8WAY );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_PLAYER1 | IPF_8WAY );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_PLAYER1 | IPF_8WAY );
	
		PORT_START(); 	/* ffe200 */
		PORT_BIT( 0x00ff, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_SERVICE( 0x0100, IP_ACTIVE_LOW );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );	/* /AUDBUSY */
		PORT_BIT( 0x0400, IP_ACTIVE_HIGH, IPT_UNUSED );/* 256H */
		PORT_BIT( 0x0800, IP_ACTIVE_HIGH, IPT_VBLANK );/* VBLANK */
		PORT_BIT( 0xf000, IP_ACTIVE_LOW, IPT_UNUSED );
	
		JSA_I_PORT_SWAPPED	/* audio port */
	INPUT_PORTS_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Graphics definitions
	 *
	 *************************************/
	
	static GfxLayout anlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		2,
		new int[] { 0, 4 },
		new int[] { 0, 1, 2, 3, 8, 9, 10, 11 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		8*16
	);
	
	
	static GfxLayout pfmolayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28 },
		new int[] { 0*8, 4*8, 8*8, 12*8, 16*8, 20*8, 24*8, 28*8 },
		32*8
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, pfmolayout,    512, 16 ),		/* playfield */
		new GfxDecodeInfo( REGION_GFX2, 0, pfmolayout,    256, 48 ),		/* sprites */
		new GfxDecodeInfo( REGION_GFX3, 0, anlayout,        0, 64 ),		/* characters 8x8 */
		new GfxDecodeInfo( -1 )
	};
	
	
	
	/*************************************
	 *
	 *	Machine driver
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( xybots )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, ATARI_CLOCK_14MHz/2)
		MDRV_CPU_MEMORY(main_readmem,main_writemem)
		MDRV_CPU_VBLANK_INT(atarigen_video_int_gen,1)
		
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		
		MDRV_MACHINE_INIT(xybots)
		MDRV_NVRAM_HANDLER(atarigen)
		
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_NEEDS_6BITS_PER_GUN | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(42*8, 30*8)
		MDRV_VISIBLE_AREA(0*8, 42*8-1, 0*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
		
		MDRV_VIDEO_START(xybots)
		MDRV_VIDEO_UPDATE(xybots)
		
		/* sound hardware */
		MDRV_IMPORT_FROM(jsa_i_stereo_swapped)
	MACHINE_DRIVER_END
	
	
	
	/*************************************
	 *
	 *	ROM definition(s)
	 *
	 *************************************/
	
	static RomLoadPtr rom_xybots = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x90000, REGION_CPU1, 0 );/* 8*64k for 68000 code */
		ROM_LOAD16_BYTE( "2112.c17",     0x00000, 0x10000, 0x16d64748 );
		ROM_LOAD16_BYTE( "2113.c19",     0x00001, 0x10000, 0x2677d44a );
		ROM_LOAD16_BYTE( "2114.b17",     0x20000, 0x08000, 0xd31890cb );
		ROM_LOAD16_BYTE( "2115.b19",     0x20001, 0x08000, 0x750ab1b0 );
	
		ROM_REGION( 0x14000, REGION_CPU2, 0 );/* 64k for 6502 code */
		ROM_LOAD( "xybots.snd",   0x10000, 0x4000, 0x3b9f155d );
		ROM_CONTINUE(             0x04000, 0xc000 );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "2102.l13",     0x00000, 0x08000, 0xc1309674 );
		ROM_RELOAD(               0x08000, 0x08000 );
		ROM_LOAD( "2103.l11",     0x10000, 0x10000, 0x907c024d );
		ROM_LOAD( "2117.l7",      0x30000, 0x10000, 0x0cc9b42d );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "1105.de1",     0x00000, 0x10000, 0x315a4274 );
		ROM_LOAD( "1106.e1",      0x10000, 0x10000, 0x3d8c1dd2 );
		ROM_LOAD( "1107.f1",      0x20000, 0x10000, 0xb7217da5 );
		ROM_LOAD( "1108.fj1",     0x30000, 0x10000, 0x77ac65e1 );
		ROM_LOAD( "1109.j1",      0x40000, 0x10000, 0x1b482c53 );
		ROM_LOAD( "1110.k1",      0x50000, 0x10000, 0x99665ff4 );
		ROM_LOAD( "1111.kl1",     0x60000, 0x10000, 0x416107ee );
	
		ROM_REGION( 0x02000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "1101.c4",      0x00000, 0x02000, 0x59c028a2 );
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Driver initialization
	 *
	 *************************************/
	
	static DRIVER_INIT( xybots )
	{
		atarigen_eeprom_default = NULL;
		atarigen_slapstic_init(0, 0x008000, 107);
		atarijsa_init(1, 2, 1, 0x0100);
		atarigen_init_6502_speedup(1, 0x4157, 0x416f);
	}
	
	
	
	/*************************************
	 *
	 *	Game driver(s)
	 *
	 *************************************/
	
	public static GameDriver driver_xybots	   = new GameDriver("1987"	,"xybots"	,"xybots.java"	,rom_xybots,null	,machine_driver_xybots	,input_ports_xybots	,init_xybots	,ROT0	,	"Atari Games", "Xybots" )
}
