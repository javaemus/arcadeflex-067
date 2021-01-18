/***************************************************************************

Bank Panic memory map (preliminary)
Similar to Appoooh

driver by Nicola Salmoria


0000-dfff ROM
e000-e7ff RAM
f000-f3ff Video RAM #1
f400-f7ff Color RAM #1
f800-fbff Video RAM #2
fc00-ffff Color RAM #2

I/O
read:
00  IN0
01  IN1
02  IN2
04  DSW

write:
00  SN76496 #1
01  SN76496 #2
02  SN76496 #3
05  horizontal scroll
07  bit 0-1 = at least one of these two controls the playfield priority
    bit 2-3 = ?
    bit 4 = NMI enable
    bit 5 = flip screen
    bit 6-7 = ?

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class bankp
{
	
	
	extern unsigned char *bankp_videoram2;
	extern unsigned char *bankp_colorram2;
	PALETTE_INIT( bankp );
	VIDEO_START( bankp );
	VIDEO_UPDATE( bankp );
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf000, 0xf3ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xf400, 0xf7ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0xf800, 0xfbff, bankp_videoram2_w, bankp_videoram2 ),
		new Memory_WriteAddress( 0xfc00, 0xffff, bankp_colorram2_w, bankp_colorram2 ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),	/* IN0 */
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),	/* IN1 */
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),	/* IN2 */
		new IO_ReadPort( 0x04, 0x04, input_port_3_r ),	/* DSW */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, SN76496_0_w ),
		new IO_WritePort( 0x01, 0x01, SN76496_1_w ),
		new IO_WritePort( 0x02, 0x02, SN76496_2_w ),
		new IO_WritePort( 0x05, 0x05, bankp_scroll_w ),
		new IO_WritePort( 0x07, 0x07, bankp_out_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_bankp = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON2 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON3 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0xf8, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, "Coin A/B" );
		PORT_DIPSETTING(    0x03, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x04, 0x00, "Coin C" );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x08, "4" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "70K 200K 500K ..." );
		PORT_DIPSETTING(    0x10, "100K 400K 800K ..." );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the bitplanes are packed in one byte */
		new int[] { 8*8+3, 8*8+2, 8*8+1, 8*8+0, 3, 2, 1, 0 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout charlayout2 = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		2048,	/* 2048 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, 2048*8*8, 2*2048*8*8 },	/* the bitplanes are separated */
		new int[] { 7, 6, 5, 4, 3, 2, 1, 0 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,      0, 32 ),
		new GfxDecodeInfo( REGION_GFX2, 0, charlayout2,  32*4, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static SN76496interface sn76496_interface = new SN76496interface
	(
		3,	/* 3 chips */
		new int[] { 3867120, 3867120, 3867120 },	/* ?? the main oscillator is 15.46848 MHz */
		new int[] { 100, 100, 100 }
	);
	
	
	
	static MACHINE_DRIVER_START( bankp )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3867120)	/* ?? the main oscillator is 15.46848 MHz */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(3*8, 31*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(32*4+16*8)
	
		MDRV_PALETTE_INIT(bankp)
		MDRV_VIDEO_START(bankp)
		MDRV_VIDEO_UPDATE(bankp)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SN76496, sn76496_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_bankp = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "epr6175.bin",  0x0000, 0x4000, 0x044552b8 );
		ROM_LOAD( "epr6174.bin",  0x4000, 0x4000, 0xd29b1598 );
		ROM_LOAD( "epr6173.bin",  0x8000, 0x4000, 0xb8405d38 );
		ROM_LOAD( "epr6176.bin",  0xc000, 0x2000, 0xc98ac200 );
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "epr6165.bin",  0x0000, 0x2000, 0xaef34a93 );/* playfield #1 chars */
		ROM_LOAD( "epr6166.bin",  0x2000, 0x2000, 0xca13cb11 );
	
		ROM_REGION( 0x0c000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "epr6172.bin",  0x0000, 0x2000, 0xc4c4878b );/* playfield #2 chars */
		ROM_LOAD( "epr6171.bin",  0x2000, 0x2000, 0xa18165a1 );
		ROM_LOAD( "epr6170.bin",  0x4000, 0x2000, 0xb58aa8fa );
		ROM_LOAD( "epr6169.bin",  0x6000, 0x2000, 0x1aa37fce );
		ROM_LOAD( "epr6168.bin",  0x8000, 0x2000, 0x05f3a867 );
		ROM_LOAD( "epr6167.bin",  0xa000, 0x2000, 0x3fa337e1 );
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 );
		ROM_LOAD( "pr6177.clr",   0x0000, 0x020, 0xeb70c5ae );	/* palette */
		ROM_LOAD( "pr6178.clr",   0x0020, 0x100, 0x0acca001 );	/* charset #1 lookup table */
		ROM_LOAD( "pr6179.clr",   0x0120, 0x100, 0xe53bafdb );	/* charset #2 lookup table */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_bankp	   = new GameDriver("1984"	,"bankp"	,"bankp.java"	,rom_bankp,null	,machine_driver_bankp	,input_ports_bankp	,null	,ROT0	,	"[Sanritsu] Sega", "Bank Panic" )
}
