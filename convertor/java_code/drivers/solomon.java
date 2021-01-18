/***************************************************************************

Solomon's Key

driver by Mirko Buffoni

***************************************************************************/
/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class solomon
{
	
	
	extern unsigned char *solomon_bgvideoram;
	extern unsigned char *solomon_bgcolorram;
	
	VIDEO_START( solomon );
	VIDEO_UPDATE( solomon );
	
	public static WriteHandlerPtr solomon_sh_command_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch_w(offset,data);
		cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
	} };
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xcfff, MRA_RAM ),	/* RAM */
		new Memory_ReadAddress( 0xd000, 0xdfff, MRA_RAM ),	/* video + color + bg */
		new Memory_ReadAddress( 0xe000, 0xe07f, MRA_RAM ),	/* spriteram  */
		new Memory_ReadAddress( 0xe400, 0xe5ff, MRA_RAM ),	/* paletteram */
		new Memory_ReadAddress( 0xe600, 0xe600, input_port_0_r ),
		new Memory_ReadAddress( 0xe601, 0xe601, input_port_1_r ),
		new Memory_ReadAddress( 0xe602, 0xe602, input_port_2_r ),
		new Memory_ReadAddress( 0xe604, 0xe604, input_port_3_r ),	/* DSW1 */
		new Memory_ReadAddress( 0xe605, 0xe605, input_port_4_r ),	/* DSW2 */
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xcfff, MWA_RAM ),
		new Memory_WriteAddress( 0xd000, 0xd3ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0xd400, 0xd7ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xd800, 0xdbff, solomon_bgcolorram_w, solomon_bgcolorram ),
		new Memory_WriteAddress( 0xdc00, 0xdfff, solomon_bgvideoram_w, solomon_bgvideoram ),
		new Memory_WriteAddress( 0xe000, 0xe07f, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xe400, 0xe5ff, paletteram_xxxxBBBBGGGGRRRR_w, paletteram ),
		new Memory_WriteAddress( 0xe600, 0xe600, interrupt_enable_w ),
		new Memory_WriteAddress( 0xe604, 0xe604, solomon_flipscreen_w ),
		new Memory_WriteAddress( 0xe800, 0xe800, solomon_sh_command_w ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress solomon_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0x8000, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress solomon_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0xffff, 0xffff, MWA_NOP ),	/* watchdog? */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort solomon_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x10, AY8910_control_port_0_w ),
		new IO_WritePort( 0x11, 0x11, AY8910_write_port_0_w ),
		new IO_WritePort( 0x20, 0x20, AY8910_control_port_1_w ),
		new IO_WritePort( 0x21, 0x21, AY8910_write_port_1_w ),
		new IO_WritePort( 0x30, 0x30, AY8910_control_port_2_w ),
		new IO_WritePort( 0x31, 0x31, AY8910_write_port_2_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_solomon = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* COIN */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x0c, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x08, "4" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_3C") );
	
		PORT_START(); 	/* DSW2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x02, "Easy" );
		PORT_DIPSETTING(    0x00, "Normal" );
		PORT_DIPSETTING(    0x01, "Harder" );
		PORT_DIPSETTING(    0x03, "Difficult" );
		PORT_DIPNAME( 0x0c, 0x00, "Timer Speed" );
		PORT_DIPSETTING(    0x08, "Slow" );
		PORT_DIPSETTING(    0x00, "Normal" );
		PORT_DIPSETTING(    0x04, "Faster" );
		PORT_DIPSETTING(    0x0c, "Fastest" );
		PORT_DIPNAME( 0x10, 0x00, "Extra" );
		PORT_DIPSETTING(    0x00, "Normal" );
		PORT_DIPSETTING(    0x10, "Difficult" );
		PORT_DIPNAME( 0xe0, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30k 200k 500k" );
		PORT_DIPSETTING(    0x80, "100k 300k 800k" );
		PORT_DIPSETTING(    0x40, "30k 200k" );
		PORT_DIPSETTING(    0xc0, "100k 300k" );
		PORT_DIPSETTING(    0x20, "30k" );
		PORT_DIPSETTING(    0xa0, "100k" );
		PORT_DIPSETTING(    0x60, "200k" );
		PORT_DIPSETTING(    0xe0, "None" );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		2048,	/* 2048 characters */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },	/* the bitplanes are packed in one nibble */
		new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8	/* every char takes 32 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 8*8 sprites */
		512,	/* 512 sprites */
		4,		/* 4 bits per pixel */
		new int[] { 0, 512*32*8, 2*512*32*8, 3*512*32*8 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0, 8 ),	/* colors   0-127 */
		new GfxDecodeInfo( REGION_GFX2, 0, charlayout,   128, 8 ),	/* colors 128-255 */
		new GfxDecodeInfo( REGION_GFX3, 0, spritelayout,   0, 8 ),	/* colors   0-127 */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		3,	/* 3 chips */
		1500000,	/* 1.5 MHz?????? */
		new int[] { 12, 12, 12 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static MACHINE_DRIVER_START( solomon )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)	/* 4.0 MHz (?????) */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,1)
	
		MDRV_CPU_ADD(Z80, 3072000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.072 MHz (?????) */
		MDRV_CPU_MEMORY(solomon_sound_readmem,solomon_sound_writemem)
		MDRV_CPU_PORTS(0,solomon_sound_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)	/* ??? */
							/* NMIs are caused by the main CPU */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(solomon)
		MDRV_VIDEO_UPDATE(solomon)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_solomon = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "slmn_06.bin",  0x00000, 0x4000, 0xe4d421ff );
		ROM_LOAD( "slmn_07.bin",  0x08000, 0x4000, 0xd52d7e38 );
		ROM_CONTINUE(             0x04000, 0x4000 );
		ROM_LOAD( "slmn_08.bin",  0x0f000, 0x1000, 0xb924d162 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "slmn_01.bin",  0x0000, 0x4000, 0xfa6e562e );
	
		ROM_REGION( 0x10000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "slmn_12.bin",  0x00000, 0x08000, 0xaa26dfcb );/* characters */
		ROM_LOAD( "slmn_11.bin",  0x08000, 0x08000, 0x6f94d2af );
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "slmn_10.bin",  0x00000, 0x08000, 0x8310c2a1 );
		ROM_LOAD( "slmn_09.bin",  0x08000, 0x08000, 0xab7e6c42 );
	
		ROM_REGION( 0x10000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "slmn_02.bin",  0x00000, 0x04000, 0x80fa2be3 );/* sprites */
		ROM_LOAD( "slmn_03.bin",  0x04000, 0x04000, 0x236106b4 );
		ROM_LOAD( "slmn_04.bin",  0x08000, 0x04000, 0x088fe5d9 );
		ROM_LOAD( "slmn_05.bin",  0x0c000, 0x04000, 0x8366232a );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_solomon	   = new GameDriver("1986"	,"solomon"	,"solomon.java"	,rom_solomon,null	,machine_driver_solomon	,input_ports_solomon	,null	,ROT0	,	"Tecmo", "Solomon's Key (Japan)" )
}
