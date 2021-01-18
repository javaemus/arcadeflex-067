/***************************************************************************
  Munch Mobile
  (C) 1982 SNK

  2 Z80s
  2 AY-8910s
  15 MHz crystal

  Known Issues:
	- sprite priority problems
	- it's unclear if mirroring the videoram chunks is correct behavior
	- several unmapped registers
	- sustained sounds (when there should be silence)

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class munchmo
{
	
	
	extern UINT8 *mnchmobl_vreg;
	extern UINT8 *mnchmobl_status_vram;
	extern UINT8 *mnchmobl_sprite_xpos;
	extern UINT8 *mnchmobl_sprite_attr;
	extern UINT8 *mnchmobl_sprite_tile;
	
	PALETTE_INIT( mnchmobl );
	VIDEO_START( mnchmobl );
	VIDEO_UPDATE( mnchmobl );
	
	
	/***************************************************************************/
	
	static int mnchmobl_nmi_enable = 0;
	
	public static WriteHandlerPtr mnchmobl_nmi_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		mnchmobl_nmi_enable = data;
	} };
	
	static INTERRUPT_GEN( mnchmobl_interrupt )
	{
		static int which;
		which = !which;
		if( which ) cpu_set_irq_line(0, 0, HOLD_LINE);
		else if( mnchmobl_nmi_enable ) cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
	}
	
	public static WriteHandlerPtr mnchmobl_soundlatch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch_w( offset, data );
		cpu_set_irq_line( 1, 0, HOLD_LINE );
	} };
	
	public static WriteHandlerPtr sound_nmi_ack_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_set_nmi_line(1, CLEAR_LINE);
	} };
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x83ff, MRA_RAM ), /* working RAM */
		new Memory_ReadAddress( 0xa000, 0xa3ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa400, 0xa7ff, mnchmobl_sprite_xpos_r ), /* mirrored */
		new Memory_ReadAddress( 0xa800, 0xabff, MRA_RAM ),
		new Memory_ReadAddress( 0xac00, 0xafff, mnchmobl_sprite_tile_r ), /* mirrored */
		new Memory_ReadAddress( 0xb000, 0xb3ff, MRA_RAM ),
		new Memory_ReadAddress( 0xb400, 0xb7ff, mnchmobl_sprite_attr_r ), /* mirrored */
		new Memory_ReadAddress( 0xb800, 0xb8ff, MRA_RAM ),
		new Memory_ReadAddress( 0xb900, 0xb9ff, mnchmobl_videoram_r ),	/* mirrored */
		new Memory_ReadAddress( 0xbe02, 0xbe02, input_port_3_r ), /* DSW1 */
		new Memory_ReadAddress( 0xbe03, 0xbe03, input_port_4_r ), /* DSW2 */
		new Memory_ReadAddress( 0xbf01, 0xbf01, input_port_0_r ), /* coin, start */
		new Memory_ReadAddress( 0xbf02, 0xbf02, input_port_1_r ), /* P1 controls */
		new Memory_ReadAddress( 0xbf03, 0xbf03, input_port_2_r ), /* P2 controls */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
	 	new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM ), /* working RAM */
		new Memory_WriteAddress( 0xa000, 0xa3ff, MWA_RAM, mnchmobl_sprite_xpos ),
		new Memory_WriteAddress( 0xa400, 0xa7ff, mnchmobl_sprite_xpos_w ),
		new Memory_WriteAddress( 0xa800, 0xabff, MWA_RAM, mnchmobl_sprite_tile ),
		new Memory_WriteAddress( 0xac00, 0xafff, mnchmobl_sprite_tile_w ),
		new Memory_WriteAddress( 0xb000, 0xb3ff, MWA_RAM, mnchmobl_sprite_attr ),
		new Memory_WriteAddress( 0xb400, 0xb7ff, mnchmobl_sprite_attr_w ),
		new Memory_WriteAddress( 0xb800, 0xb9ff, mnchmobl_videoram_w, videoram ),
		new Memory_WriteAddress( 0xba00, 0xbbff, MWA_RAM ),
		new Memory_WriteAddress( 0xbc00, 0xbc7f, MWA_RAM, mnchmobl_status_vram ),
		new Memory_WriteAddress( 0xbe00, 0xbe00, mnchmobl_soundlatch_w ),
		new Memory_WriteAddress( 0xbe01, 0xbe01, mnchmobl_palette_bank_w ),
		new Memory_WriteAddress( 0xbe11, 0xbe11, MWA_RAM ), /* ? */
		new Memory_WriteAddress( 0xbe21, 0xbe21, MWA_RAM ), /* ? */
		new Memory_WriteAddress( 0xbe31, 0xbe31, MWA_RAM ), /* ? */
		new Memory_WriteAddress( 0xbe41, 0xbe41, mnchmobl_flipscreen_w ),
		new Memory_WriteAddress( 0xbe61, 0xbe61, mnchmobl_nmi_enable_w ), /* ENI 1-10C */
		new Memory_WriteAddress( 0xbf00, 0xbf07, MWA_RAM, mnchmobl_vreg ), /* MY0 1-8C */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_sound[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x2000, soundlatch_r ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x4000, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x5000, 0x5000, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x6000, 0x6000, AY8910_write_port_1_w ),
		new Memory_WriteAddress( 0x7000, 0x7000, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0x8000, 0x8000, MWA_NOP ), /* ? */
		new Memory_WriteAddress( 0xa000, 0xa000, MWA_NOP ), /* ? */
		new Memory_WriteAddress( 0xc000, 0xc000, sound_nmi_ack_w ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,	/* 2 chips */
		1500000,	/* 1.5 MHz? */
		new int[] { 50, 50 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static InputPortPtr input_ports_mnchmobl = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN2 );/* service */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();  /* P1 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT | IPF_2WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_2WAY );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* P2 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* DSW1 0xbe02 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x1e, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x14, DEF_STR( "3C_1C") );
	//	PORT_DIPSETTING(    0x12, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x16, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x1e, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x1c, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x1a, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x18, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_8C") );
		PORT_DIPNAME( 0xe0, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "No Bonus" );
		PORT_DIPSETTING(    0x20, "70000" );
		PORT_DIPSETTING(    0x40, "60000" );
		PORT_DIPSETTING(    0x60, "50000" );
		PORT_DIPSETTING(    0x80, "40000" );
		PORT_DIPSETTING(    0xa0, "30000" );
		PORT_DIPSETTING(    0xc0, "20000" );
		PORT_DIPSETTING(    0xe0, "10000" );
	
		PORT_START(); 	/* DSW2 0xbe03 */
		PORT_DIPNAME( 0x03, 0x00, "Second Bonus Life" );
		PORT_DIPSETTING(    0x00, "No Bonus?" );
		PORT_DIPSETTING(    0x01, "100000?" );
		PORT_DIPSETTING(    0x02, "40000?" );
		PORT_DIPSETTING(    0x03, "30000?" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x0c, "5" );
		PORT_DIPNAME( 0x10, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout char_layout = new GfxLayout
	(
		8,8,
		256,
		4,
		new int[] { 0, 8, 256*128,256*128+8 },
		new int[] { 7,6,5,4,3,2,1,0 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		128
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		8,8,
		0x100,
		4,
		new int[] { 8,12,0,4 },
		new int[] { 0,0,1,1,2,2,3,3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		128
	);
	
	static GfxLayout sprite_layout1 = new GfxLayout
	(
		32,32,
		128,
		3,
		new int[] { 0x4000*8,0x2000*8,0 },
		new int[] {
			7,7,6,6,5,5,4,4,3,3,2,2,1,1,0,0,
			0x8000+7,0x8000+7,0x8000+6,0x8000+6,0x8000+5,0x8000+5,0x8000+4,0x8000+4,
			0x8000+3,0x8000+3,0x8000+2,0x8000+2,0x8000+1,0x8000+1,0x8000+0,0x8000+0
		},
		new int[] {
			 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
			 8*8, 9*8,10*8,11*8,12*8,13*8,14*8,15*8,
			16*8,17*8,18*8,19*8,20*8,21*8,22*8,23*8,
			24*8,25*8,26*8,27*8,28*8,29*8,30*8,31*8
		},
		256
	);
	
	static GfxLayout sprite_layout2 = new GfxLayout
	(
		32,32,
		128,
		3,
		new int[] { 0,0,0 },
		new int[] {
			7,7,6,6,5,5,4,4,3,3,2,2,1,1,0,0,
			0x8000+7,0x8000+7,0x8000+6,0x8000+6,0x8000+5,0x8000+5,0x8000+4,0x8000+4,
			0x8000+3,0x8000+3,0x8000+2,0x8000+2,0x8000+1,0x8000+1,0x8000+0,0x8000+0
		},
		new int[] {
			 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
			 8*8, 9*8,10*8,11*8,12*8,13*8,14*8,15*8,
			16*8,17*8,18*8,19*8,20*8,21*8,22*8,23*8,
			24*8,25*8,26*8,27*8,28*8,29*8,30*8,31*8
		},
		256
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0,      char_layout,      0,  4 ),	/* colors   0- 63 */
		new GfxDecodeInfo( REGION_GFX2, 0x1000, tile_layout,     64,  4 ),	/* colors  64-127 */
		new GfxDecodeInfo( REGION_GFX3, 0,      sprite_layout1, 128, 16 ),	/* colors 128-255 */
		new GfxDecodeInfo( REGION_GFX4, 0,      sprite_layout2, 128, 16 ),	/* colors 128-255 */
		new GfxDecodeInfo( -1 )
	};
	
	static MACHINE_DRIVER_START( munchmo )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3750000) /* ? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(mnchmobl_interrupt,2)
	
		MDRV_CPU_ADD(Z80, 3750000) /* ? */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(readmem_sound,writemem_sound)
		MDRV_CPU_VBLANK_INT(nmi_line_assert,1)
	
		MDRV_FRAMES_PER_SECOND(57)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256+32+32, 256)
		MDRV_VISIBLE_AREA(0, 255+32+32,0, 255-16)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(mnchmobl)
		MDRV_VIDEO_START(mnchmobl)
		MDRV_VIDEO_UPDATE(mnchmobl)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static RomLoadPtr rom_joyfulr = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for CPUA */
		ROM_LOAD( "m1j.10e", 0x0000, 0x2000, 0x1fe86e25 );
		ROM_LOAD( "m2j.10d", 0x2000, 0x2000, 0xb144b9a6 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound CPU */
		ROM_LOAD( "mu.2j",	 0x0000, 0x2000, 0x420adbd4 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "s1.10a",	 0x0000, 0x1000, 0xc0bcc301 );/* characters */
		ROM_LOAD( "s2.10b",	 0x1000, 0x1000, 0x96aa11ca );
	
		ROM_REGION( 0x2000, REGION_GFX2, 0 );
		ROM_LOAD( "b1.2c",	 0x0000, 0x1000, 0x8ce3a403 );/* tile layout */
		ROM_LOAD( "b2.2b",	 0x1000, 0x1000, 0x0df28913 );/* 4x8 tiles */
	
		ROM_REGION( 0x6000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "f1j.1g",	 0x0000, 0x2000, 0x93c3c17e );/* sprites */
		ROM_LOAD( "f2j.3g",	 0x2000, 0x2000, 0xb3fb5bd2 );
		ROM_LOAD( "f3j.5g",	 0x4000, 0x2000, 0x772a7527 );
	
		ROM_REGION( 0x2000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "h",		 0x0000, 0x2000, 0x332584de );/* monochrome sprites */
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 );
		ROM_LOAD( "a2001.clr", 0x0000, 0x0100, 0x1b16b907 );/* color prom */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mnchmobl = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for CPUA */
		ROM_LOAD( "m1.10e",	 0x0000, 0x2000, 0xa4bebc6a );
		ROM_LOAD( "m2.10d",	 0x2000, 0x2000, 0xf502d466 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound CPU */
		ROM_LOAD( "mu.2j",	 0x0000, 0x2000, 0x420adbd4 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "s1.10a",	 0x0000, 0x1000, 0xc0bcc301 );/* characters */
		ROM_LOAD( "s2.10b",	 0x1000, 0x1000, 0x96aa11ca );
	
		ROM_REGION( 0x2000, REGION_GFX2, 0 );
		ROM_LOAD( "b1.2c",	 0x0000, 0x1000, 0x8ce3a403 );/* tile layout */
		ROM_LOAD( "b2.2b",	 0x1000, 0x1000, 0x0df28913 );/* 4x8 tiles */
	
		ROM_REGION( 0x6000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "f1.1g",	 0x0000, 0x2000, 0xb75411d4 );/* sprites */
		ROM_LOAD( "f2.3g",	 0x2000, 0x2000, 0x539a43ba );
		ROM_LOAD( "f3.5g",	 0x4000, 0x2000, 0xec996706 );
	
		ROM_REGION( 0x2000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "h",		 0x0000, 0x2000, 0x332584de );/* monochrome sprites */
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 );
		ROM_LOAD( "a2001.clr", 0x0000, 0x0100, 0x1b16b907 );/* color prom */
	ROM_END(); }}; 
	
	
	public static GameDriver driver_joyfulr	   = new GameDriver("1983"	,"joyfulr"	,"munchmo.java"	,rom_joyfulr,null	,machine_driver_munchmo	,input_ports_mnchmobl	,null	,ROT270	,	"SNK", "Joyful Road (Japan)", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
	public static GameDriver driver_mnchmobl	   = new GameDriver("1983"	,"mnchmobl"	,"munchmo.java"	,rom_mnchmobl,driver_joyfulr	,machine_driver_munchmo	,input_ports_mnchmobl	,null	,ROT270	,	"SNK (Centuri license)", "Munch Mobile (US)", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
}
