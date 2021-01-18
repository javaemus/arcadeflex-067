/***************************************************************************

  IPM Invader (M10 m10 hardware)
  Sky Chuter By IREM
  Space Beam (M15 m15 hardware)
  Green Beret (?M15 ?m15 hardware)

  (c) 12/2/1998 Lee Taylor

Notes:
- Colors are close to screen shots for IPM Invader. The other games have not
  been verified.
- The bitmap strips in IPM Invader might be slightly misplaced

TODO:
- Dip switches

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class skychut
{
	
	extern UINT8* iremm15_chargen;
	
	VIDEO_UPDATE( skychut );
	VIDEO_UPDATE( iremm15 );
	
	static UINT8 *memory;
	
	
	static PALETTE_INIT( skychut )
	{
		int i;
	
		palette_set_color(0,0xff,0xff,0xff);
		palette_set_color(1,0xff,0xff,0x00);
		palette_set_color(2,0xff,0x00,0xff);
		palette_set_color(3,0xff,0x00,0x00);
		palette_set_color(4,0x00,0xff,0xff);
		palette_set_color(5,0x00,0xff,0x00);
		palette_set_color(6,0x00,0x00,0xff);
		palette_set_color(7,0x00,0x00,0x00);
	
		for (i = 0;i < 8;i++)
		{
			colortable[2*i+0] = 7;
			colortable[2*i+1] = i;
		}
	}
	
	
	public static Memory_ReadAddress skychut_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x02ff, MRA_RAM ), /* scratch ram */
		new Memory_ReadAddress( 0x1000, 0x2fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4800, 0x4bff, MRA_RAM ), /* Foreground colour  */
		new Memory_ReadAddress( 0x5000, 0x53ff, MRA_RAM ), /* BKgrnd colour ??? */
		new Memory_ReadAddress( 0xa200, 0xa200, input_port_1_r ),
		new Memory_ReadAddress( 0xa300, 0xa300, input_port_0_r ),
	/*	new Memory_ReadAddress( 0xa700, 0xa700, input_port_2_r ), */
		new Memory_ReadAddress( 0xfC00, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress skychut_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x02ff, MWA_RAM, memory ),
		new Memory_WriteAddress( 0x1000, 0x2fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x4800, 0x4bff, skychut_colorram_w, colorram ), /* foreground colour  */
		new Memory_WriteAddress( 0x5000, 0x53ff, MWA_RAM, iremm15_chargen ), /* background ????? */
	//	new Memory_WriteAddress( 0xa100, 0xa1ff, MWA_RAM ), /* Sound writes????? */
		new Memory_WriteAddress( 0xa400, 0xa400, skychut_ctrl_w ),	/* line at bottom of screen?, sound, flip screen */
		new Memory_WriteAddress( 0xfc00, 0xffff, MWA_ROM ),	/* for the reset / interrupt vectors */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress greenberet_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x02ff, MRA_RAM ), /* scratch ram */
		new Memory_ReadAddress( 0x1000, 0x33ff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4800, 0x4bff, MRA_RAM ), /* Foreground colour  */
		new Memory_ReadAddress( 0x5000, 0x57ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa000, input_port_3_r ),
		new Memory_ReadAddress( 0xa200, 0xa200, input_port_1_r ),
		new Memory_ReadAddress( 0xa300, 0xa300, input_port_0_r ),
		new Memory_ReadAddress( 0xfC00, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress greenberet_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x02ff, MWA_RAM, memory ),
		new Memory_WriteAddress( 0x1000, 0x33ff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x4800, 0x4bff, skychut_colorram_w, colorram ), /* foreground colour  */
		new Memory_WriteAddress( 0x5000, 0x57ff, MWA_RAM, iremm15_chargen ),
		new Memory_WriteAddress( 0xa100, 0xa1ff, MWA_RAM ), /* Sound writes????? */
		new Memory_WriteAddress( 0xa400, 0xa400, MWA_NOP ),	/* sound, flip screen */
		new Memory_WriteAddress( 0xfc00, 0xffff, MWA_ROM ),	/* for the reset / interrupt vectors */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	INTERRUPT_GEN( skychut_interrupt )
	{
		if (readinputport(2) & 1)	/* Left Coin */
	        cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
	    else
	    	cpu_set_irq_line(0, 0, HOLD_LINE);
	}
	
	static InputPortPtr input_ports_skychut = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL);
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_COCKTAIL );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME(0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING (  0x00, "3" );
		PORT_DIPSETTING (  0x01, "4" );
		PORT_DIPSETTING (  0x02, "5" );
	
		PORT_START(); 	/* FAKE */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_spacebeam = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME(0x03, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING (  0x00, "2" );
		PORT_DIPSETTING (  0x01, "3" );
		PORT_DIPSETTING (  0x02, "4" );
		PORT_DIPSETTING (  0x03, "5" );
		PORT_DIPNAME(0x08, 0x00, "?" );
		PORT_DIPSETTING (  0x00, DEF_STR(Off);
		PORT_DIPSETTING (  0x08, DEF_STR(On);
		PORT_DIPNAME(0x30, 0x10, DEF_STR(Coinage);
		PORT_DIPSETTING (  0x00, "Testmode" );
		PORT_DIPSETTING (  0x10, "1 Coin 1 Play" );
		PORT_DIPSETTING (  0x20, "1 Coin 2 Plays" );
	
		PORT_START(); 	/* FAKE */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
	
		PORT_START(); 	/* IN3 */
		PORT_BIT( 0x03, 0, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		1,	/* 1 bits per pixel */
		new int[] { 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, charlayout, 0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static MACHINE_DRIVER_START( skychut )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6502,20000000/8)
		MDRV_CPU_MEMORY(skychut_readmem,skychut_writemem)
		MDRV_CPU_VBLANK_INT(skychut_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(8)
		MDRV_COLORTABLE_LENGTH(2*8)
	
		MDRV_PALETTE_INIT(skychut)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(skychut)
	
		/* sound hardware */
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( greenberet )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6502,20000000/8)
		MDRV_CPU_MEMORY(greenberet_readmem,greenberet_writemem)
		MDRV_CPU_VBLANK_INT(skychut_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 2*8, 30*8-1)
		MDRV_PALETTE_LENGTH(8)
		MDRV_COLORTABLE_LENGTH(2*8)
	
		MDRV_PALETTE_INIT(skychut)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(iremm15)
	
		/* sound hardware */
	MACHINE_DRIVER_END
	
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_ipminvad = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "b1r",  0x1000, 0x0400, 0xf9a7eb9b );
		ROM_LOAD( "b2r",  0x1400, 0x0400, 0xaf11c1aa );
		ROM_LOAD( "b3r",  0x1800, 0x0400, 0xed49e481 );
		ROM_LOAD( "b4r",  0x1c00, 0x0400, 0x6d5db95b );
		ROM_RELOAD(       0xfc00, 0x0400 );/* for the reset and interrupt vectors */
		ROM_LOAD( "b5r",  0x2000, 0x0400, 0xeabba7aa );
		ROM_LOAD( "b6r",  0x2400, 0x0400, 0x3d0e7fa6 );
		ROM_LOAD( "b7r",  0x2800, 0x0400, 0xcf04864f );
	
		ROM_REGION( 0x0800, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "b9r",  0x0000, 0x0400, 0x56942cab );
		ROM_LOAD( "b10r", 0x0400, 0x0400, 0xbe4b8585 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_skychut = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "sc1d",  0x1000, 0x0400, 0x30b5ded1 );
		ROM_LOAD( "sc2d",  0x1400, 0x0400, 0xfd1f4b9e );
		ROM_LOAD( "sc3d",  0x1800, 0x0400, 0x67ed201e );
		ROM_LOAD( "sc4d",  0x1c00, 0x0400, 0x9b23a679 );
		ROM_RELOAD(        0xfc00, 0x0400 );/* for the reset and interrupt vectors */
		ROM_LOAD( "sc5a",  0x2000, 0x0400, 0x51d975e6 );
		ROM_LOAD( "sc6e",  0x2400, 0x0400, 0x617f302f );
		ROM_LOAD( "sc7",   0x2800, 0x0400, 0xdd4c8e1a );
		ROM_LOAD( "sc8d",  0x2c00, 0x0400, 0xaca8b798 );
	
		ROM_REGION( 0x0800, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "sc9d",  0x0000, 0x0400, 0x2101029e );
		ROM_LOAD( "sc10d", 0x0400, 0x0400, 0x2f81c70c );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_spacbeam = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "m1b", 0x1000, 0x0400, 0x5a1c3e0b );
		ROM_LOAD( "m2b", 0x1400, 0x0400, 0xa02bd9d7 );
		ROM_LOAD( "m3b", 0x1800, 0x0400, 0x78040843 );
		ROM_LOAD( "m4b", 0x1c00, 0x0400, 0x74705a44 );
		ROM_RELOAD(      0xfc00, 0x0400 );/* for the reset and interrupt vectors */
		ROM_LOAD( "m5b", 0x2000, 0x0400, 0xafdf1242 );
		ROM_LOAD( "m6b", 0x2400, 0x0400, 0x12afb0c2 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_greenber = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "gb1", 0x1000, 0x0400, 0x018ff672 );// ok
		ROM_LOAD( "gb2", 0x1400, 0x0400, 0xea8f2267 );// ok
		ROM_LOAD( "gb3", 0x1800, 0x0400, 0x8f337920 );// ok
		ROM_LOAD( "gb4", 0x1c00, 0x0400, 0x7eeac4eb );// ok
		ROM_RELOAD(      0xfc00, 0x0400 );/* for the reset and interrupt vectors */
		ROM_LOAD( "gb5", 0x2000, 0x0400, 0xb2f8e69a );
		ROM_LOAD( "gb6", 0x2400, 0x0400, 0x50ea8bd3 );
		ROM_LOAD( "gb7", 0x2800, 0x0400, 0x00000000 );// 2be8 entry
		ROM_LOAD( "gb8", 0x2c00, 0x0400, 0x34700b31 );
		ROM_LOAD( "gb9", 0x3000, 0x0400, 0xc27b9ba3 );// ok ?
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_ipminvad	   = new GameDriver("1979?"	,"ipminvad"	,"skychut.java"	,rom_ipminvad,null	,machine_driver_skychut	,input_ports_skychut	,null	,ROT270	,	"Irem", "IPM Invader", GAME_NO_COCKTAIL | GAME_NO_SOUND | GAME_IMPERFECT_COLORS )
	public static GameDriver driver_skychut	   = new GameDriver("1980"	,"skychut"	,"skychut.java"	,rom_skychut,null	,machine_driver_skychut	,input_ports_skychut	,null	,ROT270	,	"Irem", "Sky Chuter", GAME_NO_COCKTAIL | GAME_NO_SOUND | GAME_IMPERFECT_COLORS )
	public static GameDriver driver_spacbeam	   = new GameDriver("1979"	,"spacbeam"	,"skychut.java"	,rom_spacbeam,null	,machine_driver_greenberet	,input_ports_spacebeam	,null	,ROT270	,	"Irem", "Space Beam", GAME_NO_COCKTAIL | GAME_NO_SOUND | GAME_IMPERFECT_COLORS )
	public static GameDriver driver_greenber	   = new GameDriver("1980"	,"greenber"	,"skychut.java"	,rom_greenber,null	,machine_driver_greenberet	,input_ports_spacebeam	,null	,ROT270	,	"Irem", "Green Beret (Irem)", GAME_NO_COCKTAIL | GAME_NO_SOUND | GAME_IMPERFECT_COLORS | GAME_NOT_WORKING )
}
