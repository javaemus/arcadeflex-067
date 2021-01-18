/*
 * Signetics 2650 CPU Games
 *
 * Zaccaria - The Invaders
 * Zaccaria - Super Invader Attack
 * Cinematronics - Embargo
 *
 * Mame@btinternet.com
 */

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class zac2650
{
	
	extern unsigned char *s2636ram;
	
	VIDEO_START( tinvader );
	VIDEO_UPDATE( tinvader );
	
	
	
	
	#define WHITE           MAKE_ARGB(0x04,0xff,0xff,0xff)
	#define GREEN 			MAKE_ARGB(0x04,0x20,0xff,0x20)
	#define PURPLE			MAKE_ARGB(0x04,0xff,0x20,0xff)
	
	OVERLAY_START( tinv2650_overlay )
		OVERLAY_RECT(   0,   0, 240, 256, WHITE )
		OVERLAY_RECT(  16,   0,  72, 256, GREEN )
		OVERLAY_RECT(   0,  48,  16, 134, GREEN )
		OVERLAY_RECT( 192,   0, 209, 256, PURPLE )
	OVERLAY_END
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x17ff, MRA_ROM ),
	    new Memory_ReadAddress( 0x1800, 0x1bff, MRA_RAM ),
	    new Memory_ReadAddress( 0x1E80, 0x1E80, tinvader_port_0_r ),
	    new Memory_ReadAddress( 0x1E81, 0x1E81, input_port_1_r ),
	    new Memory_ReadAddress( 0x1E82, 0x1E82, input_port_2_r ),
	    new Memory_ReadAddress( 0x1D00, 0x1Dff, MRA_RAM ),
	    new Memory_ReadAddress( 0x1F00, 0x1FFF, zac_s2636_r ),			/* S2636 Chip */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x17FF, MWA_ROM ),
		new Memory_WriteAddress( 0x1800, 0x1bff, videoram_w, videoram, videoram_size ),
	    new Memory_WriteAddress( 0x1D00, 0x1dff, MWA_RAM ),
	    new Memory_WriteAddress( 0x1E80, 0x1E80, tinvader_sound_w ),
	    new Memory_WriteAddress( 0x1F00, 0x1FFF, zac_s2636_w, s2636ram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
	    new IO_ReadPort( S2650_SENSE_PORT, S2650_SENSE_PORT, input_port_3_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	static InputPortPtr input_ports_tinvader = new InputPortPtr(){ public void handler() { 
	
		PORT_START();  /* 1E80 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED  );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* Missile-Background Collision */
	
	    PORT_START();  /* 1E81 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
	    PORT_DIPNAME( 0x02, 0x00, "Lightning Speed" );/* Velocita Laser Inv */
		PORT_DIPSETTING(    0x00, "Slow" );
		PORT_DIPSETTING(    0x02, "Fast" );
		PORT_DIPNAME( 0x1C, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x0C, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x10, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0x14, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0x18, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x1C, DEF_STR( "1C_7C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "1000" );
		PORT_DIPSETTING(    0x20, "1500" );
	    PORT_DIPNAME( 0x40, 0x00, "Extended Play" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
	
		PORT_START();  /* 1E82 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* SENSE */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_VBLANK );
	
	INPUT_PORTS_END(); }}; 
	
	/* Almost identical, no number of bases selection */
	
	static InputPortPtr input_ports_sinvader = new InputPortPtr(){ public void handler() { 
	
		PORT_START();  /* 1E80 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED  );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* Missile-Background Collision */
	
	    PORT_START();  /* 1E81 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED  );
	    PORT_DIPNAME( 0x02, 0x00, "Lightning Speed" );/* Velocita Laser Inv */
		PORT_DIPSETTING(    0x00, "Slow" );
		PORT_DIPSETTING(    0x02, "Fast" );
		PORT_DIPNAME( 0x1C, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x0C, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x10, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0x14, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0x18, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x1C, DEF_STR( "1C_7C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "1000" );
		PORT_DIPSETTING(    0x20, "1500" );
	    PORT_DIPNAME( 0x40, 0x00, "Extended Play" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
	
		PORT_START();  /* 1E82 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* SENSE */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_VBLANK );
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_embargo = new InputPortPtr(){ public void handler() { 
	/* TODO */
	INPUT_PORTS_END(); }}; 
	
	static PALETTE_INIT( zac2650 )
	{
		palette_set_color(0,0x00,0x00,0x00); /* BLACK */
		palette_set_color(1,0xff,0xff,0xff); /* WHITE */
		colortable[0] = 0;
		colortable[1] = 1;
		colortable[2] = 0;
		colortable[3] = 0;
	}
	
	static GfxLayout tinvader_character = new GfxLayout
	(
		8,8,
		128,
		1,
		new int[] { 0 },
		new int[] { 0,1,2,3,4,5,6,7 },
	   	new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	
	/* These are really 6x8, but overlay an 8x8 screen  */
	/* so we stretch them slightly to occupy same space */
	
	static GfxLayout s2636_character8 = new GfxLayout
	(
		8,8,
		16,
		1,
		new int[] { 0 },
		new int[] { 0,1,1,2,3,4,5,5 },
	   	new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout s2636_character16 = new GfxLayout
	(
		16,16,
		16,
		1,
		new int[] { 0 },
		new int[] { 0,0,1,1,1,2,2,2,3,3,3,4,4,4,5,5 },
	   	new int[] { 0*8,0*8,1*8,1*8,2*8,2*8,3*8,3*8,4*8,4*8,5*8,5*8,6*8,6*8,7*8,7*8 },
		8*8
	);
	
	static GfxDecodeInfo tinvader_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tinvader_character,  0, 2 ),
	  	new GfxDecodeInfo( REGION_CPU1, 0x1F00, s2636_character8, 0, 2 ),	/* dynamic */
	  	new GfxDecodeInfo( REGION_CPU1, 0x1F00, s2636_character16, 0, 2 ),	/* dynamic */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static MACHINE_DRIVER_START( tinvader )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(S2650, 3800000/4/3)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,0)
	
		MDRV_FRAMES_PER_SECOND(55)
		MDRV_VBLANK_DURATION(1041)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(30*8, 32*8)
		MDRV_VISIBLE_AREA(0, 239, 0, 255)
		MDRV_GFXDECODE(tinvader_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2)
		MDRV_COLORTABLE_LENGTH(4)
	
		MDRV_PALETTE_INIT(zac2650)
		MDRV_VIDEO_START(tinvader)
		MDRV_VIDEO_UPDATE(tinvader)
	
		/* sound hardware */
	MACHINE_DRIVER_END
	
	public static WriteHandlerPtr tinvader_sound_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    /* sounds are NOT the same as space invaders */
	
		logerror("Register %x = Data %d\n",data & 0xfe,data & 0x01);
	
	    /* 08 = hit invader */
	    /* 20 = bonus (extra base) */
	    /* 40 = saucer */
		/* 84 = fire */
	    /* 90 = die */
	    /* c4 = hit saucer */
	} };
	
	static RomLoadPtr rom_sia2650 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x2000, REGION_CPU1, 0 );
		ROM_LOAD( "42_1.bin",   0x0000, 0x0800, 0xa85550a9 );
		ROM_LOAD( "44_2.bin",   0x0800, 0x0800, 0x48d5a3ed );
		ROM_LOAD( "46_3.bin",   0x1000, 0x0800, 0xd766e784 );
	
		ROM_REGION( 0x400, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "06_inv.bin", 0x0000, 0x0400, 0x7bfed23e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_tinv2650 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x2000, REGION_CPU1, 0 );
		ROM_LOAD( "42_1.bin",   0x0000, 0x0800, 0xa85550a9 );
		ROM_LOAD( "44_2t.bin",  0x0800, 0x0800, 0x083c8621 );
		ROM_LOAD( "46_3t.bin",  0x1000, 0x0800, 0x12c0934f );
	
		ROM_REGION( 0x400, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "06_inv.bin", 0x0000, 0x0400, 0x7bfed23e );
	ROM_END(); }}; 
	
	/*
	 * Embargo
	 *
	 */
	
	public static Memory_ReadAddress emb_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_ROM ),
	    new Memory_ReadAddress( 0x1e00, 0x3dff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress emb_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_ROM ),
		new Memory_WriteAddress( 0x1e00, 0x1fff, MWA_RAM ),
		new Memory_WriteAddress( 0x2000, 0x3dff, c8080bw_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort emb_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	static MACHINE_DRIVER_START( embargo )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(S2650, 625000)
		MDRV_CPU_MEMORY(emb_readmem,emb_writemem)
		MDRV_CPU_PORTS(emb_readport,0)
	
		MDRV_FRAMES_PER_SECOND(55)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-2, 0*8, 32*8-1)
		MDRV_PALETTE_LENGTH(2)
		MDRV_COLORTABLE_LENGTH(4)
	
		MDRV_PALETTE_INIT(zac2650)
		MDRV_VIDEO_START(generic_bitmapped)
		MDRV_VIDEO_UPDATE(8080bw)
	
		/* sound hardware */
	MACHINE_DRIVER_END
	
	static RomLoadPtr rom_embargo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x8000, REGION_CPU1, 0 );
		ROM_LOAD( "emb1", 0x0000, 0x0200, 0x00dcbc24 );
		ROM_LOAD( "emb2", 0x0200, 0x0200, 0xe7069b11 );
		ROM_LOAD( "emb3", 0x0400, 0x0200, 0x1af7a966 );
		ROM_LOAD( "emb4", 0x0600, 0x0200, 0xd9c75da0 );
		ROM_LOAD( "emb5", 0x0800, 0x0200, 0x15960b58 );
		ROM_LOAD( "emb6", 0x0a00, 0x0200, 0x7ba23058 );
		ROM_LOAD( "emb7", 0x0c00, 0x0200, 0x6d46a593 );
		ROM_LOAD( "emb8", 0x0e00, 0x0200, 0xf0b00634 );
	ROM_END(); }}; 
	
	
	
	static DRIVER_INIT( tinvader )
	{
		artwork_set_overlay(tinv2650_overlay);
	}
	
	
	public static GameDriver driver_sia2650	   = new GameDriver("19??"	,"sia2650"	,"zac2650.java"	,rom_sia2650,null	,machine_driver_tinvader	,input_ports_sinvader	,null	,ROT270	,	"Zaccaria/Zelco", "Super Invader Attack", GAME_NO_SOUND )
	public static GameDriver driver_tinv2650	   = new GameDriver("19??"	,"tinv2650"	,"zac2650.java"	,rom_tinv2650,driver_sia2650	,machine_driver_tinvader	,input_ports_tinvader	,init_tinvader	,ROT270	,	"Zaccaria/Zelco", "The Invaders", GAME_NO_SOUND )
	public static GameDriver driver_embargo	   = new GameDriver("1977"	,"embargo"	,"zac2650.java"	,rom_embargo,null	,machine_driver_embargo	,input_ports_embargo	,init_8080bw	,ROT0	,	"Cinematronics",  "Embargo", GAME_NO_SOUND )
}
