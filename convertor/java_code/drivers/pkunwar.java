/***************************************************************************

Notes:
- Currently the Flip Screen dip switch only flips the screen horizontally.
  This might not be the correct behaviour. Verification of the real board
  would be necessary to emulate this accurately.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class pkunwar
{
	
	
	PALETTE_INIT( nova2001 );
	VIDEO_UPDATE( pkunwar );
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x8fff, MRA_RAM ),
		new Memory_ReadAddress( 0xa001, 0xa001, AY8910_read_port_0_r ),
		new Memory_ReadAddress( 0xa003, 0xa003, AY8910_read_port_1_r ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x8800, 0x8bff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x8c00, 0x8fff, colorram_w, colorram ),
		new Memory_WriteAddress( 0xa000, 0xa000, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0xa001, 0xa001, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0xa002, 0xa002, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0xa003, 0xa003, AY8910_write_port_1_w ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, pkunwar_flipscreen_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_pkunwar = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_VBLANK );
	
		PORT_START(); 	/* IN1 */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_SERVICE( 0x40, IP_ACTIVE_LOW );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x10, "Easy" );
		PORT_DIPSETTING(    0x30, "Medium" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		2048,	/* 2048 characters */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },
		new int[] { 0*4, 1*4, 2048*16*8, 2048*16*8 + 4, 2*4,  3*4, 2048*16*8 + 8, 2048*16*8 + 12 },
		new int[] { 0*8, 2*8, 4*8, 6*8, 8*8, 10*8, 12*8, 14*8 },
		16*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		512,	/* 512 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },
		new int[] { 0*4, 1*4, 512*64*8, 512*64*8 + 4, 2*4,  3*4, 512*64*8 + 8, 512*64*8 + 12,
				0*4+16*8, 1*4+16*8, 512*64*8+16*8, 512*64*8 + 4+16*8, 2*4+16*8,  3*4+16*8, 512*64*8 + 8 + 16*8, 512*64*8 + 12 + 16*8 },
		new int[] { 0*8, 2*8, 4*8, 6*8, 8*8, 10*8, 12*8, 14*8,
				0*8+32*8, 2*8+32*8, 4*8+32*8, 6*8+32*8, 8*8+32*8, 10*8+32*8, 12*8+32*8, 14*8 + 32*8  },
		64*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, charlayout,   16*16, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0x0000, spritelayout,     0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface(
		2,		/* 2 chips */
		12000000/8,	/* 1.5 MHz */
		new int[] { 25, 25 },
		new ReadHandlerPtr[] { input_port_0_r, input_port_2_r },
		new ReadHandlerPtr[] { input_port_1_r, input_port_3_r },
		new WriteHandlerPtr[] { 0, 0 },
		new WriteHandlerPtr[] { 0, 0 }
	);
	
	
	
	static MACHINE_DRIVER_START( pkunwar )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 12000000/4)	/* 3 MHz */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(0,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 4*8, 28*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(32*16)
	
		MDRV_PALETTE_INIT(nova2001)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(pkunwar)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_pkunwar = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "pkwar.01r",    0x0000, 0x4000, 0xce2d2c7b );
		ROM_LOAD( "pkwar.02r",    0x4000, 0x4000, 0xabc1f661 );
		ROM_LOAD( "pkwar.03r",    0xe000, 0x2000, 0x56faebea );
	
		ROM_REGION( 0x10000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "pkwar.01y",    0x0000, 0x2000, 0x428d3b92 );
		ROM_CONTINUE(             0x8000, 0x2000 );
		ROM_LOAD( "pkwar.02y",    0x2000, 0x2000, 0xce1da7bc );
		ROM_CONTINUE(             0xa000, 0x2000 );
		ROM_LOAD( "pkwar.03y",    0x4000, 0x2000, 0x63204400 );
		ROM_CONTINUE(             0xc000, 0x2000 );
		ROM_LOAD( "pkwar.04y",    0x6000, 0x2000, 0x061dfca8 );
		ROM_CONTINUE(             0xe000, 0x2000 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "pkwar.col",    0x0000, 0x0020, 0xaf0fc5e2 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_pkunwarj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "pgunwar.6",    0x0000, 0x4000, 0x357f3ef3 );
		ROM_LOAD( "pgunwar.5",    0x4000, 0x4000, 0x0092e49e );
		ROM_LOAD( "pkwar.03r",    0xe000, 0x2000, 0x56faebea );
	
		ROM_REGION( 0x10000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "pkwar.01y",    0x0000, 0x2000, 0x428d3b92 );
		ROM_CONTINUE(             0x8000, 0x2000 );
		ROM_LOAD( "pkwar.02y",    0x2000, 0x2000, 0xce1da7bc );
		ROM_CONTINUE(             0xa000, 0x2000 );
		ROM_LOAD( "pgunwar.2",    0x4000, 0x2000, 0xa2a43443 );
		ROM_CONTINUE(             0xc000, 0x2000 );
		ROM_LOAD( "pkwar.04y",    0x6000, 0x2000, 0x061dfca8 );
		ROM_CONTINUE(             0xe000, 0x2000 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "pkwar.col",    0x0000, 0x0020, 0xaf0fc5e2 );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_pkunwar	   = new GameDriver("1985?"	,"pkunwar"	,"pkunwar.java"	,rom_pkunwar,null	,machine_driver_pkunwar	,input_ports_pkunwar	,null	,ROT0	,	"UPL", "Penguin-Kun Wars (US)" )
	public static GameDriver driver_pkunwarj	   = new GameDriver("1985?"	,"pkunwarj"	,"pkunwar.java"	,rom_pkunwarj,driver_pkunwar	,machine_driver_pkunwar	,input_ports_pkunwar	,null	,ROT0	,	"UPL", "Penguin-Kun Wars (Japan)" )
}
