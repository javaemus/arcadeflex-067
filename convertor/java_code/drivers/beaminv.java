/***************************************************************************

	Tekunon Kougyou Beam Invader hardware

	driver by Zsolt Vasvari

	Games supported:
		* Beam Invader

	Known issues:
		* Port 0 might be a analog port select

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class beaminv
{
	
	
	/****************************************************************
	 *
	 *	Special port handler - doesn't warrant its own 'machine file
	 *
	 ****************************************************************/
	
	public static ReadHandlerPtr beaminv_input_port_3_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (input_port_3_r(offset) & 0xfe) | ((cpu_getscanline() >> 7) & 0x01);
	} };
	
	
	/*************************************
	 *
	 *	Memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x17ff, MRA_ROM ),
		new Memory_ReadAddress( 0x1800, 0x1fff, MRA_RAM ),
		new Memory_ReadAddress( 0x2400, 0x2400, input_port_0_r ),
		new Memory_ReadAddress( 0x2800, 0x28ff, input_port_1_r ),
		new Memory_ReadAddress( 0x3400, 0x3400, input_port_2_r ),
		new Memory_ReadAddress( 0x3800, 0x3800, beaminv_input_port_3_r ),
		new Memory_ReadAddress( 0x4000, 0x5fff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x17ff, MWA_ROM ),
		new Memory_WriteAddress( 0x1800, 0x1fff, MWA_RAM ),
		new Memory_WriteAddress( 0x4000, 0x5fff, beaminv_videoram_w, videoram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	/*************************************
	 *
	 *	Port handlers
	 *
	 *************************************/
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	static InputPortPtr input_ports_beaminv = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "1000" );
		PORT_DIPSETTING(    0x04, "2000" );
		PORT_DIPSETTING(    0x08, "3000" );
		PORT_DIPSETTING(    0x0c, "4000" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );		/* probably unused */
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x60, 0x40, "Faster Bombs At" );
		PORT_DIPSETTING(    0x00, "49 Enemies" );
		PORT_DIPSETTING(    0x20, "39 Enemies" );
		PORT_DIPSETTING(    0x40, "29 Enemies" );
		PORT_DIPSETTING(    0x60, "Never" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );		/* probably unused */
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();       /* IN2 */
		PORT_ANALOG( 0xff, 0x00, IPT_PADDLE, 20, 10, 0x00, 0xff);
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SPECIAL ); /* should be V128, using VBLANK slows game down */
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNUSED );
	
	INPUT_PORTS_END(); }}; 
	
	
	/*************************************
	 *
	 *	Machine drivers
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( beaminv )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 2000000)	/* 2 MHz ? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(0,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(0)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(16, 223, 16, 247)
		MDRV_PALETTE_LENGTH(2)
		MDRV_PALETTE_INIT(black_and_white)
		MDRV_VIDEO_START(generic_bitmapped)
		MDRV_VIDEO_UPDATE(generic_bitmapped)
	MACHINE_DRIVER_END
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadPtr rom_beaminv = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "0a", 0x0000, 0x0400, 0x17503086 );
		ROM_LOAD( "1a", 0x0400, 0x0400, 0xaa9e1666 );
		ROM_LOAD( "2a", 0x0800, 0x0400, 0xebaa2fc8 );
		ROM_LOAD( "3a", 0x0c00, 0x0400, 0x4f62c2e6 );
		ROM_LOAD( "4a", 0x1000, 0x0400, 0x3eebf757 );
		ROM_LOAD( "5a", 0x1400, 0x0400, 0xec08bc1f );
	ROM_END(); }}; 
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_beaminv	   = new GameDriver("19??"	,"beaminv"	,"beaminv.java"	,rom_beaminv,null	,machine_driver_beaminv	,input_ports_beaminv	,null	,ROT0	,	"Tekunon Kougyou", "Beam Invader", GAME_NO_SOUND)
}
