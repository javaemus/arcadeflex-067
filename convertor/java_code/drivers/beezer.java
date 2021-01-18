/*

  Beezer - (c) 1982 Tong Electronic

  Written by Mathis Rosenhauer

*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class beezer
{
	
	/* from vidhrdw/beezer.c */
	extern UINT8 *videoram;
	INTERRUPT_GEN( beezer_interrupt );
	VIDEO_UPDATE( beezer );
	
	/* from machine/beezer.c */
	DRIVER_INIT( beezer );
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xcfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xd000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, beezer_ram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xc000, 0xcfff, MWA_BANK1 ),
		new Memory_WriteAddress( 0xd000, 0xffff, beezer_bankswitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_sound[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_RAM ),
	//	new Memory_ReadAddress( 0x1000, 0x10ff, beezer_6840_r ),
		new Memory_ReadAddress( 0x1800, 0x18ff, via_1_r ),
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, MWA_RAM ),
	//	new Memory_WriteAddress( 0x1000, 0x10ff, beezer_6840_w ),
		new Memory_WriteAddress( 0x1800, 0x18ff, via_1_w ),
	//	new Memory_WriteAddress( 0x8000, 0x9fff, beezer_dac_w ),
		new Memory_WriteAddress( 0xe000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_beezer = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_ANALOG( 0x0f, 0x00, IPT_TRACKBALL_X | IPF_REVERSE, 20, 10, 0, 0);
		PORT_START(); 	/* IN2 */
		PORT_ANALOG( 0x0f, 0x00, IPT_TRACKBALL_Y | IPF_REVERSE, 20, 10, 0, 0);
	
		PORT_START(); 	/* IN3 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x10, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x20, "30000" );
		PORT_DIPSETTING(    0x10, "60000" );
		PORT_DIPSETTING(    0x00, "90000" );
		PORT_DIPSETTING(    0x30, DEF_STR( "No") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xc0, "Easy" );
		PORT_DIPSETTING(    0x80, "Medium" );
		PORT_DIPSETTING(    0x40, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
	INPUT_PORTS_END(); }}; 
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	static MACHINE_DRIVER_START( beezer )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6809, 1000000)        /* 1 MHz */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(beezer_interrupt,128)
	
		MDRV_CPU_ADD(M6809, 1000000)        /* 1 MHz */
		MDRV_CPU_MEMORY(readmem_sound,writemem_sound)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 384)
		MDRV_VISIBLE_AREA(0, 256-1, 16, 303)
		MDRV_PALETTE_LENGTH(16)
	
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(beezer)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_beezer = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );    /* 64k for main CPU */
		ROM_LOAD( "g1",   0x0d000, 0x1000, 0x3467a0ec );
		ROM_LOAD( "g3",   0x0e000, 0x1000, 0x9950cdf2 );
		ROM_LOAD( "g5",   0x0f000, 0x1000, 0xa4b09879 );
	
		ROM_LOAD( "f1",   0x12000, 0x2000, 0xce1b0b8b );
		ROM_LOAD( "f3",   0x14000, 0x2000, 0x6a11072a );
		ROM_LOAD( "e1",   0x16000, 0x1000, 0x21e4ca9b );
		ROM_LOAD( "e3",   0x18000, 0x1000, 0xa4f735d7 );
		ROM_LOAD( "e5",   0x1a000, 0x1000, 0x0485575b );
		ROM_LOAD( "f5",   0x1c000, 0x1000, 0x4b11f572 );
		ROM_LOAD( "f7",   0x1e000, 0x1000, 0xbef67473 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for sound CPU */
		ROM_LOAD( "d7",   0xf000, 0x1000, 0x23b0782e );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "d1.cpu", 0x000, 0x0100, 0x8db17a40 );
		ROM_LOAD( "e1.cpu", 0x100, 0x0100, 0x3c775c5e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_beezer1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );    /* 64k for main CPU */
		ROM_LOAD( "g1.32",   0x0d000, 0x1000, 0x3134cb93 );
		ROM_LOAD( "g3.32",   0x0e000, 0x1000, 0xa3cb2c2d );
		ROM_LOAD( "g5.32",   0x0f000, 0x1000, 0x5e559bf9 );
	
		ROM_LOAD( "f1.64",   0x12000, 0x2000, 0xb8a78cca );
		ROM_LOAD( "f3.32",   0x14000, 0x1000, 0xbfa023f5 );
		ROM_LOAD( "e1",      0x16000, 0x1000, 0x21e4ca9b );
		ROM_LOAD( "e3",      0x18000, 0x1000, 0xa4f735d7 );
		ROM_LOAD( "e5",      0x1a000, 0x1000, 0x0485575b );
		ROM_LOAD( "f5",      0x1c000, 0x1000, 0x4b11f572 );
		ROM_LOAD( "f7",      0x1e000, 0x1000, 0xbef67473 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for sound CPU */
		ROM_LOAD( "d7.32",   0xf000, 0x1000, 0xb11028b5 );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "d1.cpu", 0x000, 0x0100, 0x8db17a40 );
		ROM_LOAD( "e1.cpu", 0x100, 0x0100, 0x3c775c5e );
	ROM_END(); }}; 
	
	public static GameDriver driver_beezer	   = new GameDriver("1982"	,"beezer"	,"beezer.java"	,rom_beezer,null	,machine_driver_beezer	,input_ports_beezer	,init_beezer	,ORIENTATION_FLIP_X	,	"Tong Electronic", "Beezer (set 1)", GAME_IMPERFECT_SOUND )
	public static GameDriver driver_beezer1	   = new GameDriver("1982"	,"beezer1"	,"beezer.java"	,rom_beezer1,driver_beezer	,machine_driver_beezer	,input_ports_beezer	,init_beezer	,ORIENTATION_FLIP_X	,	"Tong Electronic", "Beezer (set 2)", GAME_IMPERFECT_SOUND )
}
