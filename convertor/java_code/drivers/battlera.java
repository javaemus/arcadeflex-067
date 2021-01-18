/*******************************************************************************

	Battle Rangers					(c) 1988 Data East Corporation
	Bloody Wolf						(c) 1988 Data East USA

	Emulation by Bryan McPhail, mish@tendril.co.uk

	This board is a modified PC-Engine PCB, differences from PC-Engine console:

	Input ports are different (2 dips, 2 joysticks, 1 coin port)
	_Interface_ to palette chip is different, palette data is the same.
	Extra sound chips, and extra processor to drive them.
	Twice as much VRAM.

	Todo:
	- Priority is wrong for the submarine at the end of level 1.
	- There seems to be a bug with a stuck note from the YM2203 FM channel
	  at the start of scene 3 and near the ending when your characters are
	  flying over a forest in a helicopter.

**********************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class battlera
{
	
	VIDEO_UPDATE( battlera );
	VIDEO_START( battlera );
	INTERRUPT_GEN( battlera_interrupt );
	
	
	
	static int control_port_select;
	
	/******************************************************************************/
	
	public static WriteHandlerPtr battlera_sound_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset==0) {
			soundlatch_w(0,data);
			cpu_set_irq_line(1, 0, HOLD_LINE);
		}
	} };
	
	/******************************************************************************/
	
	public static WriteHandlerPtr control_data_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		control_port_select=data;
	} };
	
	public static ReadHandlerPtr control_data_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		switch (control_port_select) {
			case 0xfe: return readinputport(0); /* Player 1 */
			case 0xfd: return readinputport(1); /* Player 2 */
			case 0xfb: return readinputport(2); /* Coins */
			case 0xf7: return readinputport(4); /* Dip 2 */
			case 0xef: return readinputport(3); /* Dip 1 */
		}
	
	    return 0xff;
	} };
	
	/******************************************************************************/
	
	public static Memory_ReadAddress battlera_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x000000, 0x0fffff, MRA_ROM ),
		new Memory_ReadAddress( 0x100000, 0x10ffff, HuC6270_debug_r ), /* Cheat to view vram data */
		new Memory_ReadAddress( 0x1f0000, 0x1f1fff, MRA_BANK8 ),
		new Memory_ReadAddress( 0x1fe000, 0x1fe001, HuC6270_register_r ),
		new Memory_ReadAddress( 0x1ff000, 0x1ff001, control_data_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress battlera_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x000000, 0x0fffff, MWA_ROM ),
		new Memory_WriteAddress( 0x100000, 0x10ffff, HuC6270_debug_w ), /* Cheat to edit vram data */
		new Memory_WriteAddress( 0x1e0800, 0x1e0801, battlera_sound_w ),
		new Memory_WriteAddress( 0x1e1000, 0x1e13ff, battlera_palette_w, paletteram ),
		new Memory_WriteAddress( 0x1f0000, 0x1f1fff, MWA_BANK8 ), /* Main ram */
		new Memory_WriteAddress( 0x1fe000, 0x1fe001, HuC6270_register_w ),
		new Memory_WriteAddress( 0x1fe002, 0x1fe003, HuC6270_data_w ),
		new Memory_WriteAddress( 0x1ff000, 0x1ff001, control_data_w ),
		new Memory_WriteAddress( 0x1ff402, 0x1ff403, H6280_irq_status_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort battlera_portwrite[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x01, HuC6270_register_w ),
		new IO_WritePort( 0x02, 0x03, HuC6270_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/******************************************************************************/
	
	public static WriteHandlerPtr YM2203_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		switch (offset) {
		case 0: YM2203_control_port_0_w(0,data); break;
		case 1: YM2203_write_port_0_w(0,data); break;
		}
	} };
	
	static int msm5205next;
	
	static void battlera_adpcm_int(int data)
	{
		static int toggle;
	
		MSM5205_data_w(0,msm5205next >> 4);
		msm5205next<<=4;
	
		toggle = 1 - toggle;
		if (toggle)
			cpu_set_irq_line(1, 1, HOLD_LINE);
	}
	
	public static WriteHandlerPtr battlera_adpcm_data_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		msm5205next=data;
	} };
	
	public static WriteHandlerPtr battlera_adpcm_reset_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		MSM5205_reset_w(0,0);
	} };
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x000000, 0x00ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x1f0000, 0x1f1fff, MRA_BANK7 ), /* Main ram */
		new Memory_ReadAddress( 0x1ff000, 0x1ff001, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
	 	new Memory_WriteAddress( 0x000000, 0x00ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x040000, 0x040001, YM2203_w ),
		new Memory_WriteAddress( 0x080000, 0x080001, battlera_adpcm_data_w ),
		new Memory_WriteAddress( 0x1fe800, 0x1fe80f, C6280_0_w ),
		new Memory_WriteAddress( 0x1f0000, 0x1f1fff, MWA_BANK7 ), /* Main ram */
		new Memory_WriteAddress( 0x1ff000, 0x1ff001, battlera_adpcm_reset_w ),
		new Memory_WriteAddress( 0x1ff402, 0x1ff403, H6280_irq_status_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/******************************************************************************/
	
	static InputPortPtr input_ports_battlera = new InputPortPtr(){ public void handler() { 
		PORT_START();   /* Player 1 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START();   /* Player 2 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 	/* Coins */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0xf8, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x01, "1" );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x08, "Easy" );
		PORT_DIPSETTING(    0x0c, "Normal" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPSETTING(    0x00, "Very Hard" );
		PORT_DIPNAME( 0x10, 0x10, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	/******************************************************************************/
	
	static GfxLayout tiles = new GfxLayout
	(
		8,8,
		4096,
		4,
		new int[] { 16*8, 16*8+8, 0, 8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8
	);
	
	static GfxLayout sprites = new GfxLayout
	(
		16,16,
		1024,
		4,
		new int[] { 96*8, 64*8, 32*8, 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		128*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tiles,       0,  16 ), /* Dynamically modified */
		new GfxDecodeInfo( REGION_GFX1, 0, sprites,   256,  16 ), /* Dynamically modified */
		new GfxDecodeInfo( REGION_GFX1, 0, tiles  ,   256,  16 ), /* Blank tile */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/******************************************************************************/
	
	static struct YM2203interface ym2203_interface =
	{
		1,
		12000000/8, /* 1.5 MHz */
		{ YM2203_VOL(40,40) },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 },
	};
	
	static struct C6280_interface c6280_interface =
	{
		1,		/* 1 chip */
		{ 60 },		/* Volume */
		{ 21477270/6 }	/* 3.579545 MHz */
	};
	
	static struct MSM5205interface msm5205_interface =
	{
		1,					/* 1 chip			 */
		384000,				/* 384KHz			 */
		{ battlera_adpcm_int },/* interrupt function */
		{ MSM5205_S48_4B},	/* 8KHz			   */
		{ 85 }
	};
	
	/******************************************************************************/
	
	static MACHINE_DRIVER_START( battlera )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(H6280,21477200/3)
		MDRV_CPU_MEMORY(battlera_readmem,battlera_writemem)
		MDRV_CPU_PORTS(0,battlera_portwrite)
		MDRV_CPU_VBLANK_INT(battlera_interrupt,256) /* 8 prelines, 232 lines, 16 vblank? */
	
		MDRV_CPU_ADD(H6280,21477200/3)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(battlera)
		MDRV_VIDEO_UPDATE(battlera)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
		MDRV_SOUND_ADD(MSM5205, msm5205_interface)
		MDRV_SOUND_ADD(C6280, c6280_interface)
	MACHINE_DRIVER_END
	
	/******************************************************************************/
	
	static RomLoadPtr rom_bldwolf = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 );/* Main cpu code */
		ROM_LOAD( "es00-1.rom", 0x00000, 0x10000, 0xff4aa252 );
		ROM_LOAD( "es01.rom",   0x10000, 0x10000, 0x9fea3189 );
		ROM_LOAD( "es02-1.rom", 0x20000, 0x10000, 0x49792753 );
		/* Rom sockets 0x30000 - 0x70000 are unused */
		ROM_LOAD( "es05.rom",   0x80000, 0x10000, 0x551fa331 );
		ROM_LOAD( "es06.rom",   0x90000, 0x10000, 0xab91aac8 );
		ROM_LOAD( "es07.rom",   0xa0000, 0x10000, 0x8d15a3d0 );
		ROM_LOAD( "es08.rom",   0xb0000, 0x10000, 0x38f06039 );
		ROM_LOAD( "es09.rom",   0xc0000, 0x10000, 0xb718c47d );
		ROM_LOAD( "es10-1.rom", 0xd0000, 0x10000, 0xd3cddc02 );
		/* Rom sockets 0xe0000 - 0x100000 are unused */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Sound CPU */
		ROM_LOAD( "es11.rom",   0x00000, 0x10000, 0xf5b29c9c );
	
		ROM_REGION( 0x80000, REGION_GFX1, 0 );
		/* Nothing */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_battlera = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 );/* Main cpu code */
		ROM_LOAD( "00_e1.bin", 0x00000, 0x10000, 0xaa1cbe69 );/* ET00 */
		ROM_LOAD( "es01.rom",  0x10000, 0x10000, 0x9fea3189 );/* ET01 */
		ROM_LOAD( "02_e4.bin", 0x20000, 0x10000, 0xcd72f580 );/* ET02, etc */
		/* Rom sockets 0x30000 - 0x70000 are unused */
		ROM_LOAD( "es05.rom",  0x80000, 0x10000, 0x551fa331 );
		ROM_LOAD( "es06.rom",  0x90000, 0x10000, 0xab91aac8 );
		ROM_LOAD( "es07.rom",  0xa0000, 0x10000, 0x8d15a3d0 );
		ROM_LOAD( "es08.rom",  0xb0000, 0x10000, 0x38f06039 );
		ROM_LOAD( "es09.rom",  0xc0000, 0x10000, 0xb718c47d );
		ROM_LOAD( "es10-1.rom",0xd0000, 0x10000, 0xd3cddc02 );
		/* Rom sockets 0xe0000 - 0x100000 are unused */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Sound CPU */
		ROM_LOAD( "es11.rom",  0x00000, 0x10000, 0xf5b29c9c );
	
		ROM_REGION( 0x80000, REGION_GFX1, 0 );
		/* Nothing */
	ROM_END(); }}; 
	
	/******************************************************************************/
	
	public static GameDriver driver_battlera	   = new GameDriver("1988"	,"battlera"	,"battlera.java"	,rom_battlera,null	,machine_driver_battlera	,input_ports_battlera	,null	,ROT0	,	"Data East Corporation", "Battle Rangers (World)", GAME_IMPERFECT_SOUND | GAME_IMPERFECT_GRAPHICS )
	public static GameDriver driver_bldwolf	   = new GameDriver("1988"	,"bldwolf"	,"battlera.java"	,rom_bldwolf,driver_battlera	,machine_driver_battlera	,input_ports_battlera	,null	,ROT0	,	"Data East USA", "Bloody Wolf (US)", GAME_IMPERFECT_SOUND | GAME_IMPERFECT_GRAPHICS )
}
