/*************************************************************************
 Universal Cheeky Mouse Driver
 (c)Lee Taylor May/June 1998, All rights reserved.

 For use only in offical Mame releases.
 Not to be distributed as part of any commerical work.
**************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class cheekyms
{
	
	
	
	
	PALETTE_INIT( cheekyms );
	VIDEO_UPDATE( cheekyms );
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM),
		new Memory_ReadAddress( 0x3000, 0x33ff, MRA_RAM),
		new Memory_ReadAddress( 0x3800, 0x3bff, MRA_RAM),	/* screen RAM */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x3000, 0x33ff, MWA_RAM ),
		new Memory_WriteAddress( 0x3800, 0x3bff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x20, 0x3f, cheekyms_sprite_w ),
		new IO_WritePort( 0x40, 0x40, cheekyms_port_40_w ),
		new IO_WritePort( 0x80, 0x80, cheekyms_port_80_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static INTERRUPT_GEN( cheekyms_interrupt )
	{
		if (readinputport(2) & 1)	/* Coin */
			nmi_line_pulse();
		else
			irq0_line_hold();
	}
	
	
	static InputPortPtr input_ports_cheekyms = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
	//PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x40, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x40, "3000" );
		PORT_DIPSETTING(    0x80, "4500" );
		PORT_DIPSETTING(    0xc0, "6000" );
		PORT_DIPSETTING(    0x00, "None" );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
		PORT_START(); 	/* FAKE */
		/* The coin slots are not memory mapped. Coin  causes a NMI, */
		/* This fake input port is used by the interrupt */
		/* handler to be notified of coin insertions. We use IMPULSE to */
		/* trigger exactly one interrupt, without having to check when the */
		/* user releases the key. */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 16*16 sprites */
		256,	/* 64 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 256*8*8 },	/* the two bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every sprite takes 8 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 64*32*8, 0 },	/* the two bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
		new int[] { 0*16, 1*16,  2*16,  3*16,  4*16,  5*16,  6*16,  7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,   0,    32 ),
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout, 32*4, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	
	static MACHINE_DRIVER_START( cheekyms )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,5000000/2)  /* 2.5 MHz */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(cheekyms_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 4*8, 28*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(64*3)
		MDRV_COLORTABLE_LENGTH(64*3)
	
		MDRV_PALETTE_INIT(cheekyms)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(cheekyms)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_cheekyms = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "cm03.c5",       0x0000, 0x0800, 0x1ad0cb40 );
		ROM_LOAD( "cm04.c6",       0x0800, 0x0800, 0x2238f607 );
		ROM_LOAD( "cm05.c7",       0x1000, 0x0800, 0x4169eba8 );
		ROM_LOAD( "cm06.c8",       0x1800, 0x0800, 0x7031660c );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "cm01.c1",       0x0000, 0x0800, 0x26f73bd7 );
		ROM_LOAD( "cm02.c2",       0x0800, 0x0800, 0x885887c3 );
	
		ROM_REGION( 0x1000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "cm07.n5",       0x0000, 0x0800, 0x2738c88d );
		ROM_LOAD( "cm08.n6",       0x0800, 0x0800, 0xb3fbd4ac );
	
		ROM_REGION( 0x0060, REGION_PROMS, 0 );
		ROM_LOAD( "cm.m8",         0x0000, 0x0020, 0x2386bc68 ); /* Character colors \ Selected by Bit 6 of Port 0x80 */
		ROM_LOAD( "cm.m9",         0x0020, 0x0020, 0xdb9c59a5 ); /* Character colors /                                */
		ROM_LOAD( "cm.p3",         0x0040, 0x0020, 0x6ac41516 ); /* Sprite colors */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_cheekyms	   = new GameDriver("1980?"	,"cheekyms"	,"cheekyms.java"	,rom_cheekyms,null	,machine_driver_cheekyms	,input_ports_cheekyms	,null	,ROT270	,	"Universal", "Cheeky Mouse", GAME_WRONG_COLORS | GAME_IMPERFECT_SOUND )
}
