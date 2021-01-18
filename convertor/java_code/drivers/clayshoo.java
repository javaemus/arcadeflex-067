/***************************************************************************

	Atari Clay Shoot hardware

	driver by Zsolt Vasvari

	Games supported:
		* Clay Shoot

	Known issues:
		* none at this time

****************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class clayshoo
{
	
	
	
	/*************************************
	 *
	 *	Main CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x23ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_ROM ),
		new Memory_ReadAddress( 0xc800, 0xc800, clayshoo_analog_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x23ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x97ff, clayshoo_videoram_w ),	 /* 6k of video ram according to readme */
		new Memory_WriteAddress( 0x9800, 0xa800, MWA_NOP ),				 /* not really mapped, but cleared */
		new Memory_WriteAddress( 0xc800, 0xc800, clayshoo_analog_reset_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x20, 0x23, ppi8255_0_r ),
		new IO_ReadPort( 0x30, 0x33, ppi8255_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, watchdog_reset_w ),
		new IO_WritePort( 0x20, 0x23, ppi8255_0_w ),
		new IO_WritePort( 0x30, 0x33, ppi8255_1_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	static InputPortPtr input_ports_clayshoo = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "Free_Play") );
		PORT_BIT( 0x3c, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* doesn't appear to be used */
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );	/* not 100% positive */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );		/* used */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x07, 0x01, "Time/Bonus 1P-2P" );
		PORT_DIPSETTING(    0x00, "60/6k-90/6k" );
		PORT_DIPSETTING(    0x01, "60/6k-120/8k" );
		PORT_DIPSETTING(    0x02, "90/9.5k-150/9.5k" );
		PORT_DIPSETTING(    0x03, "90/9.5k-190/11k" );
		PORT_DIPSETTING(    0x04, "60/8k-90/8k" );
		PORT_DIPSETTING(    0x05, "60/8k-120/10k" );
		PORT_DIPSETTING(    0x06, "90/11.5k-150/11.5k" );
		PORT_DIPSETTING(    0x07, "90/11.5k-190/13k" );
		PORT_BIT( 0xf8, IP_ACTIVE_LOW, IPT_UNKNOWN );/* doesn't appear to be used */
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x03, IP_ACTIVE_LOW, IPT_SPECIAL );/* amateur/expert/pro Player 2 */
		PORT_BIT( 0x0c, IP_ACTIVE_LOW, IPT_SPECIAL );/* amateur/expert/pro Player 1 */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
	
		PORT_START(); 	/* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();   /* IN4 - Fake analog control.  Visible in $c800 bit 1 */
		PORT_ANALOG( 0x0f, 0x08, IPT_AD_STICK_Y | IPF_REVERSE | IPF_PLAYER1, 10, 10, 0, 0x0f);
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();   /* IN5 - Fake analog control.  Visible in $c800 bit 0 */
		PORT_ANALOG( 0x0f, 0x08, IPT_AD_STICK_Y | IPF_REVERSE | IPF_PLAYER2, 10, 10, 0, 0x0f);
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN6 - Fake.  Visible in IN2 bits 0-1 and 2-3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_TOGGLE | IPF_PLAYER2 );/* Amateur */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_TOGGLE | IPF_PLAYER2 );/* Expert */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON4 | IPF_TOGGLE | IPF_PLAYER2 );/* Pro */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_TOGGLE | IPF_PLAYER1 );/* Amateur */
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_TOGGLE | IPF_PLAYER1 );/* Expert */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON4 | IPF_TOGGLE | IPF_PLAYER1 );/* Pro */
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	/*************************************
	 *
	 *	Machine driver
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( clayshoo )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,5068000/4)		/* 5.068/4 Mhz (divider is a guess) */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(clayshoo)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 192)
		MDRV_VISIBLE_AREA(8, 247, 8, 183)
		MDRV_PALETTE_LENGTH(2)
	
		MDRV_PALETTE_INIT(clayshoo)
		MDRV_VIDEO_START(generic_bitmapped)
		MDRV_VIDEO_UPDATE(generic_bitmapped)
	MACHINE_DRIVER_END
	
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadPtr rom_clayshoo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "0",      0x0000, 0x0800, 0x9df9d9e3 );
		ROM_LOAD( "1",      0x0800, 0x0800, 0x5134a631 );
		ROM_LOAD( "2",      0x1000, 0x0800, 0x5b5a67f6 );
		ROM_LOAD( "3",      0x1800, 0x0800, 0x7eda8e44 );
		ROM_LOAD( "4",      0x4000, 0x0800, 0x3da16196 );
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_clayshoo	   = new GameDriver("1979"	,"clayshoo"	,"clayshoo.java"	,rom_clayshoo,null	,machine_driver_clayshoo	,input_ports_clayshoo	,null	,ROT0	,	"Allied Leisure", "Clay Shoot", GAME_NO_SOUND )
}
