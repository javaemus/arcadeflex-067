/* Flower (c)1986 Komax
 - Driver by InsideOutBoy

todo:

cleanups
colors, probably missing proms
fix sound
improve interrupts
sprite flipping is incorrect for one of the enemies so its probably wrong
screenshots look like the game has sprite zooming
http://emustatus.rainemu.com/games/flower.htm

*/

/*

        FLOWER   CHIP PLACEMENT

USES THREE Z80 CPU'S

CHIP #  POSITION   TYPE
------------------------
1        5J         27256   CONN BD
2        5F         27256    "
3        D9         27128    "
4        12A        27128    "
5        16A        27256    "
6        7E         2764    BOTTOM BD
15       9E          "       "
8        10E         "       "
9        12E         "       "
10       13E         "       "
11       14E         "       "
12       16E         "       "
13       17E         "       "
14       19E         "       "
*/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class flower
{
	
	data8_t *flower_sharedram;
	
	VIDEO_UPDATE( flower );
	VIDEO_START( flower );
	
	
	extern data8_t *flower_soundregs1,*flower_soundregs2;
	int flower_sh_start(const struct MachineSound *msound);
	
	
	
	
	public static WriteHandlerPtr flower_irq_ack = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_set_irq_line(0, 0, CLEAR_LINE);
	} };
	
	
	static int sn_irq_enable,sn_nmi_enable;
	
	public static WriteHandlerPtr sn_irq_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sn_irq_enable = data & 1;
	
		cpu_set_irq_line(2, 0, CLEAR_LINE);
	} };
	
	static INTERRUPT_GEN( sn_irq )
	{
		if (sn_irq_enable)
			cpu_set_irq_line(2, 0, ASSERT_LINE);
	}
	
	public static WriteHandlerPtr sn_nmi_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sn_nmi_enable = data & 1;
	} };
	
	public static WriteHandlerPtr sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch_w(0,data);
		if (sn_nmi_enable)
			cpu_set_nmi_line(2, PULSE_LINE);
	} };
	
	
	public static Memory_ReadAddress flower_mn_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa102, 0xa102, input_port_0_r ),
		new Memory_ReadAddress( 0xa103, 0xa103, input_port_1_r ),
		new Memory_ReadAddress( 0xc000, 0xffff, flower_sharedram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress flower_mn_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa000, 0xa000, MWA_NOP ),	//watchdog?
		new Memory_WriteAddress( 0xa001, 0xa001, MWA_NOP ),	//flip screen - check code at 0x759f
		new Memory_WriteAddress( 0xa002, 0xa002, flower_irq_ack ),	//irq ack / enable, maybe?
		new Memory_WriteAddress( 0xa004, 0xa004, MWA_NOP ),	//nmi enable (routine is empty)
		new Memory_WriteAddress( 0xc000, 0xffff, flower_sharedram_w, flower_sharedram ),	//c23b-c62a cleared for something
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress flower_sl_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa100, 0xa100, input_port_2_r ),
		new Memory_ReadAddress( 0xa101, 0xa101, input_port_3_r ),
		new Memory_ReadAddress( 0xc000, 0xffff, flower_sharedram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress flower_sl_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa003, 0xa003, MWA_NOP ),	//irq enable
		new Memory_WriteAddress( 0xa005, 0xa005, MWA_NOP ),	//nmi enable (routine is empty)
		new Memory_WriteAddress( 0xa400, 0xa400, sound_command_w ),
		new Memory_WriteAddress( 0xc000, 0xffff, flower_sharedram_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress flower_sn_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x6000, soundlatch_r ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress flower_sn_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x4000, sn_irq_enable_w ),
		new Memory_WriteAddress( 0x4001, 0x4001, sn_nmi_enable_w ),
		new Memory_WriteAddress( 0x8000, 0x803f, flower_sound1_w, flower_soundregs1 ),
		new Memory_WriteAddress( 0xa000, 0xa03f, flower_sound2_w, flower_soundregs2 ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_flower = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 (CPU0) */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1  );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x08, "Easy" );
		PORT_DIPSETTING(    0x00, "Hard" );
		PORT_BITX(    0x10, 0x10, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Keep Items on Life Loss" );// check code at 0x74a2
		PORT_DIPSETTING(    0x20, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Shot Range" );		// check code at 0x75f9
		PORT_DIPSETTING(    0x80, "Medium" );
		PORT_DIPSETTING(    0x00, "Long" );
	
		PORT_START(); 	/* IN1 (CPU0) */
		PORT_DIPNAME( 0x07, 0x05, DEF_STR( "Lives") );		// what should be the default value ?
		PORT_DIPSETTING(    0x07, "1" );
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPSETTING(    0x05, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPSETTING(    0x02, "6" );
		PORT_DIPSETTING(    0x01, "7" );
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", 0, 0 );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );		// check code at 0x759f
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x80, "30k, 80k then every 50k" );
		PORT_DIPSETTING(    0x00, "50k, 130k then every 80k" );
	
		PORT_START(); 	/* IN0 (CPU1) */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );		// Fire (normal or laser)
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );		// Missile
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 );		// Cutter
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 (CPU1) */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );// Fire (normal or laser)
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );// Missile
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL );// Cutter
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout flower_charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		2,
		new int[] { 0, 4 },
		new int[] { STEP4(0,1), STEP4(8,1) },
		new int[] { STEP8(0,16) },
		8*8*2
	);
	
	static GfxLayout flower_spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,2),
		4,
		new int[] { 0, 4, RGN_FRAC(1,2), RGN_FRAC(1,2)+4 },
		new int[] { STEP4(0,1), STEP4(8,1), STEP4(8*8*2,1), STEP4(8*8*2+8,1) },
		new int[] { STEP8(0,16), STEP8(8*8*4,16) },
		16*16*2
	);
	
	static GfxDecodeInfo flower_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, flower_charlayout,   0,  8 ),
		new GfxDecodeInfo( REGION_GFX2, 0, flower_spritelayout, 0,  8 ),
		new GfxDecodeInfo( REGION_GFX3, 0, flower_spritelayout, 0,  8 ),
		new GfxDecodeInfo( -1 )
	};
	
	
	
	static CustomSound_interface custom_interface = new CustomSound_interface
	(
		flower_sh_start,
		flower_sh_stop,
		0
	);
	
	
	
	static MACHINE_DRIVER_START( flower )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,8000000)
		MDRV_CPU_MEMORY(flower_mn_readmem,flower_mn_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,10)
	//	MDRV_CPU_VBLANK_INT(nmi_line_pulse,1) //nmis stuff up the writes to shared ram
	
		MDRV_CPU_ADD(Z80,8000000)
		MDRV_CPU_MEMORY(flower_sl_readmem,flower_sl_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	//	MDRV_CPU_VBLANK_INT(nmi_line_pulse,1)
	
		MDRV_CPU_ADD(Z80,8000000)
		MDRV_CPU_MEMORY(flower_sn_readmem,flower_sn_writemem)
		MDRV_CPU_PERIODIC_INT(sn_irq,120)	/* periodic interrupt, don't know about the frequency */
	
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(34*8, 33*8)
		MDRV_VISIBLE_AREA(0*8, 34*8-1, 0*8, 28*8-1)
		MDRV_GFXDECODE(flower_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(0x8000)
	
		MDRV_VIDEO_START(flower)
		MDRV_VIDEO_UPDATE(flower)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END
	
	
	static RomLoadPtr rom_flower = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main cpu */
		ROM_LOAD( "1.5j",   0x0000, 0x8000, 0xa4c3af78 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* sub cpu */
		ROM_LOAD( "2.5f",   0x0000, 0x8000, 0x7c7ee2d8 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* sound cpu */
		ROM_LOAD( "3.d9",   0x0000, 0x4000, 0x8866c2b0 );// 1xxxxxxxxxxxxx = 0xFF
	
		ROM_REGION( 0x2000, REGION_GFX1, 0 );/* tx layer */
		ROM_LOAD( "10.13e", 0x0000, 0x2000, 0x62f9b28c );// FIRST AND SECOND HALF IDENTICAL
	
		ROM_REGION( 0x8000, REGION_GFX2, 0 );/* sprites */
		ROM_LOAD( "12.16e", 0x0000, 0x2000, 0xe3779f7f );
		ROM_LOAD( "11.14e", 0x2000, 0x2000, 0x8801b34f );
		ROM_LOAD( "14.19e", 0x4000, 0x2000, 0x11b491c5 );
		ROM_LOAD( "13.17e", 0x6000, 0x2000, 0xea743986 );
	
		ROM_REGION( 0x8000, REGION_GFX3, 0 );/* bg layers */
		ROM_LOAD( "8.10e",  0x0000, 0x2000, 0xf85eb20f );
		ROM_LOAD( "6.7e",   0x2000, 0x2000, 0x3e97843f );
		ROM_LOAD( "9.12e",  0x4000, 0x2000, 0xf1d9915e );
		ROM_LOAD( "15.9e",  0x6000, 0x2000, 0x1cad9f72 );
	
		ROM_REGION( 0x8000, REGION_SOUND1, 0 );
		ROM_LOAD( "4.12a",  0x0000, 0x8000, 0x851ed9fd );/* 8-bit samples */
	
		ROM_REGION( 0x4000, REGION_SOUND2, 0 );
		ROM_LOAD( "5.16a",  0x0000, 0x4000, 0x42fa2853 );/* volume tables? */
	
		ROM_REGION( 0x100, REGION_PROMS, 0 );
		ROM_LOAD( "proms",  0x0000, 0x0100, 0x00000000 );
	ROM_END(); }}; 
	
	
	public static GameDriver driver_flower	   = new GameDriver("1986"	,"flower"	,"flower.java"	,rom_flower,null	,machine_driver_flower	,input_ports_flower	,null	,ROT0	,	"Komax", "Flower", GAME_WRONG_COLORS | GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
}
