/***************************************************************************

Space Force Memory Map

driver by Zsolt Vasvari


0000-3fff   R	ROM
4000-43ff	R/W	RAM
7000-7002	R   input ports 0-2
7000		  W sound command
7001	      W sound CPU IRQ trigger on bit 3 falling edge
7002		  W unknown
7008		  W unknown
7009		  W unknown
700a		  W unknown
700b		  W flip screen
700c		  W unknown
700d		  W unknown
700e		  W main CPU interrupt enable (it uses RST7.5)
700f		  W unknown
8000-83ff   R/W bit 0-7 of character code
9000-93ff   R/W attributes RAM
				bit 0   - bit 8 of character code
				bit 1-3 - unused
				bit 4-6 - color
				bit 7   - unused
a000-a3ff	R/W X/Y scroll position of each character (can be scrolled up
				to 7 pixels in each direction)


***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class spcforce
{
	
	
	extern unsigned char *spcforce_scrollram;
	
	VIDEO_UPDATE( spcforce );
	
	
	static int spcforce_SN76496_latch;
	static int spcforce_SN76496_select;
	
	public static WriteHandlerPtr spcforce_SN76496_latch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		spcforce_SN76496_latch = data;
	} };
	
	public static ReadHandlerPtr spcforce_SN76496_select_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return spcforce_SN76496_select;
	} };
	
	public static WriteHandlerPtr spcforce_SN76496_select_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    spcforce_SN76496_select = data;
	
		if (~data & 0x40)  SN76496_0_w(0, spcforce_SN76496_latch);
		if (~data & 0x20)  SN76496_1_w(0, spcforce_SN76496_latch);
		if (~data & 0x10)  SN76496_2_w(0, spcforce_SN76496_latch);
	} };
	
	public static ReadHandlerPtr spcforce_t0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		/* SN76496 status according to Al - not supported by MAME?? */
		return rand() & 1;
	} };
	
	
	public static WriteHandlerPtr spcforce_soundtrigger_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_set_irq_line(1, 0, (~data & 0x08) ? ASSERT_LINE : CLEAR_LINE);
	} };
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, MRA_RAM ),
		new Memory_ReadAddress( 0x7000, 0x7000, input_port_0_r ),
		new Memory_ReadAddress( 0x7001, 0x7001, input_port_1_r ),
		new Memory_ReadAddress( 0x7002, 0x7002, input_port_2_r ),
		new Memory_ReadAddress( 0x8000, 0x83ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9000, 0x93ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa3ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, MWA_RAM ),
		new Memory_WriteAddress( 0x7000, 0x7000, soundlatch_w ),
		new Memory_WriteAddress( 0x7001, 0x7001, spcforce_soundtrigger_w ),
		new Memory_WriteAddress( 0x700b, 0x700b, spcforce_flip_screen_w ),
		new Memory_WriteAddress( 0x700e, 0x700e, interrupt_enable_w ),
		new Memory_WriteAddress( 0x700f, 0x700f, MWA_NOP ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM, videoram, videoram_size ),
		new Memory_WriteAddress( 0x9000, 0x93ff, MWA_RAM, colorram ),
		new Memory_WriteAddress( 0xa000, 0xa3ff, MWA_RAM, spcforce_scrollram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( I8039_bus, I8039_bus, soundlatch_r ),
		new IO_ReadPort( I8039_p2,  I8039_p2,  spcforce_SN76496_select_r ),
		new IO_ReadPort( I8039_t0,  I8039_t0,  spcforce_t0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( I8039_p1,  I8039_p1,  spcforce_SN76496_latch_w ),
		new IO_WritePort( I8039_p2,  I8039_p2,  spcforce_SN76496_select_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_spcforce = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* DSW */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x18, 0x08, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x18, "5" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	
		PORT_START();       /* IN0 */
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL | IPF_2WAY );
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_VBLANK );
		PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_COCKTAIL | IPF_2WAY );
	INPUT_PORTS_END(); }}; 
	
	/* same as spcforce, but no cocktail mode */
	static InputPortPtr input_ports_spcforc2 = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* DSW */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x18, 0x08, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x18, "5" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );  /* probably unused */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* IN0 */
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
	
		PORT_START();       /* IN1 */
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_VBLANK );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,    /* 8*8 chars */
		512,    /* 512 characters */
		3,      /* 3 bits per pixel */
		new int[] { 2*512*8*8, 512*8*8, 0 },  /* The bitplanes are seperate */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7},
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8},
		8*8     /* every char takes 8 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	/* 1-bit RGB palette */
	static unsigned short colortable_source[] =
	{
		0, 1, 2, 3, 4, 5, 6, 7,
		0, 0, 1, 2, 3, 4, 5, 6,	 /* not sure about these, but they are only used */
		0, 7, 0, 1, 2, 3, 4, 5,  /* to change the text color. During the game,   */
		0, 6, 7, 0, 1, 2, 3, 4,  /* only color 0 is used, which is correct.      */
		0, 5, 6, 7, 0, 1, 2, 3,
		0, 4, 5, 6, 7, 0, 1, 2,
		0, 3, 4, 5, 6, 7, 0, 1,
		0, 2, 3, 4, 5, 6, 7, 0
	};
	static PALETTE_INIT( spcforce )
	{
		int i;
		for (i = 0; i < 8; i++)
			palette_set_color(i, (i & 1) * 0xff, ((i >> 1) & 1) * 0xff, ((i >> 2) & 1) * 0xff);
		memcpy(colortable,colortable_source,sizeof(colortable_source));
	}
	
	
	static SN76496interface sn76496_interface = new SN76496interface
	(
		3,		/* 3 chips */
		new int[] { 2000000, 2000000, 2000000 },	/* 8 MHz / 4 ?*/
		new int[] { 100, 100, 100 }
	);
	
	
	static MACHINE_DRIVER_START( spcforce )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(8085A, 4000000)        /* 4.00 MHz??? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq3_line_hold,1)
	
		MDRV_CPU_ADD(I8035,6144000/8)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)		/* divisor ??? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 0*8, 28*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(8)
		MDRV_COLORTABLE_LENGTH(sizeof(colortable_source) / sizeof(colortable_source[0]))
	
		MDRV_PALETTE_INIT(spcforce)
		MDRV_VIDEO_START(generic_bitmapped)
		MDRV_VIDEO_UPDATE(spcforce)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SN76496, sn76496_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr rom_spcforce = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );      /* 64k for code */
		ROM_LOAD( "m1v4f.1a",  	  0x0000, 0x0800, 0x7da0d1ed );
		ROM_LOAD( "m2v4f.1c",  	  0x0800, 0x0800, 0x25605bff );
		ROM_LOAD( "m3v5f.2a",  	  0x1000, 0x0800, 0x6f879366 );
		ROM_LOAD( "m4v5f.2c",  	  0x1800, 0x0800, 0x7fbfabfa );
								/*0x2000 empty */
		ROM_LOAD( "m6v4f.3c",  	  0x2800, 0x0800, 0x12128e9e );
		ROM_LOAD( "m7v4f.4a",  	  0x3000, 0x0800, 0x978ad452 );
		ROM_LOAD( "m8v4f.4c",  	  0x3800, 0x0800, 0xf805c3cd );
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 );	/* sound MCU */
		ROM_LOAD( "spacefor.snd", 0x0000, 0x0800, 0x8820913c );
	
		ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "rm1v2.6s",     0x0000, 0x0800, 0x8e3490d7 );
		ROM_LOAD( "rm2v1.7s",     0x0800, 0x0800, 0xfbbfa05a );
		ROM_LOAD( "gm1v2.6p",     0x1000, 0x0800, 0x4f574920 );
		ROM_LOAD( "gm2v1.7p",     0x1800, 0x0800, 0x0cd89ce2 );
		ROM_LOAD( "bm1v2.6m",     0x2000, 0x0800, 0x130869ce );
		ROM_LOAD( "bm2v1.7m",     0x2800, 0x0800, 0x472f0a9b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_spcforc2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );      /* 64k for code */
		ROM_LOAD( "spacefor.1a",  0x0000, 0x0800, 0xef6fdccb );
		ROM_LOAD( "spacefor.1c",  0x0800, 0x0800, 0x44bd1cdd );
		ROM_LOAD( "spacefor.2a",  0x1000, 0x0800, 0xfcbc7df7 );
		ROM_LOAD( "vm4", 	      0x1800, 0x0800, 0xc5b073b9 );
								/*0x2000 empty */
		ROM_LOAD( "spacefor.3c",  0x2800, 0x0800, 0x9fd52301 );
		ROM_LOAD( "spacefor.4a",  0x3000, 0x0800, 0x89aefc0a );
		ROM_LOAD( "m8v4f.4c",  	  0x3800, 0x0800, 0xf805c3cd );
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 );	/* sound MCU */
		ROM_LOAD( "spacefor.snd", 0x0000, 0x0800, 0x8820913c );
	
		ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "spacefor.6s",  0x0000, 0x0800, 0x848ae522 );
		ROM_LOAD( "rm2v1.7s",     0x0800, 0x0800, 0xfbbfa05a );
		ROM_LOAD( "spacefor.6p",  0x1000, 0x0800, 0x95446911 );
		ROM_LOAD( "gm2v1.7p",     0x1800, 0x0800, 0x0cd89ce2 );
		ROM_LOAD( "bm1v2.6m",     0x2000, 0x0800, 0x130869ce );
		ROM_LOAD( "bm2v1.7m",     0x2800, 0x0800, 0x472f0a9b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_meteor = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );      /* 64k for code */
		ROM_LOAD( "vm1", 	      0x0000, 0x0800, 0x894fe9b1 );
		ROM_LOAD( "vm2", 	      0x0800, 0x0800, 0x28685a68 );
		ROM_LOAD( "vm3", 	      0x1000, 0x0800, 0xc88fb12a );
		ROM_LOAD( "vm4", 	      0x1800, 0x0800, 0xc5b073b9 );
								/*0x2000 empty */
		ROM_LOAD( "vm6", 	      0x2800, 0x0800, 0x9969ec43 );
		ROM_LOAD( "vm7", 	      0x3000, 0x0800, 0x39f43ac2 );
		ROM_LOAD( "vm8", 	      0x3800, 0x0800, 0xa0508de3 );
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 );	/* sound MCU */
		ROM_LOAD( "vm5", 	      0x0000, 0x0800, 0xb14ccd57 );
	
		ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "rm1v",         0x0000, 0x0800, 0xd621fe96 );
		ROM_LOAD( "rm2v",         0x0800, 0x0800, 0xb3981251 );
		ROM_LOAD( "gm1v",         0x1000, 0x0800, 0xd44617e8 );
		ROM_LOAD( "gm2v",         0x1800, 0x0800, 0x0997d945 );
		ROM_LOAD( "bm1v",         0x2000, 0x0800, 0xcc97c890 );
		ROM_LOAD( "bm2v",         0x2800, 0x0800, 0x2858cf5c );
	ROM_END(); }}; 
	
	
	public static GameDriver driver_spcforce	   = new GameDriver("1980"	,"spcforce"	,"spcforce.java"	,rom_spcforce,null	,machine_driver_spcforce	,input_ports_spcforce	,null	,ROT270	,	"Venture Line", "Space Force", GAME_IMPERFECT_COLORS )
	public static GameDriver driver_spcforc2	   = new GameDriver("19??"	,"spcforc2"	,"spcforce.java"	,rom_spcforc2,driver_spcforce	,machine_driver_spcforce	,input_ports_spcforc2	,null	,ROT270	,	"Elcon (bootleg?)", "Space Force (set 2)", GAME_IMPERFECT_COLORS )
	public static GameDriver driver_meteor	   = new GameDriver("1981"	,"meteor"	,"spcforce.java"	,rom_meteor,driver_spcforce	,machine_driver_spcforce	,input_ports_spcforc2	,null	,ROT270	,	"Venture Line", "Meteoroids", GAME_IMPERFECT_COLORS )
}
