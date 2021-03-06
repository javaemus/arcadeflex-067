/***************************************************************************

	Leland Ataxx-era driver

	driver by Aaron Giles and Paul Leaman

	Games supported:
		* Ataxx
		* World Soccer Finals
		* Danny Sullivan's Indy Heat
		* Brute Force
		* Asylum (prototype)

****************************************************************************

	To enter service mode in Ataxx and Brute Force, press 1P start and
	then press the service switch (F2).

	For World Soccer Finals, press the 1P button B and then press the
	service switch.

	For Indy Heat, press the red turbo button (1P button 1) and then
	press the service switch.

***************************************************************************/


/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class ataxx
{
	
	
	
	/*************************************
	 *
	 *	Master CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress master_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x9fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xa000, 0xdfff, MRA_BANK2 ),
		new Memory_ReadAddress( 0xe000, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xffff, ataxx_paletteram_and_misc_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress master_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x9fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa000, 0xdfff, ataxx_battery_ram_w ),
		new Memory_WriteAddress( 0xe000, 0xf7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf800, 0xffff, ataxx_paletteram_and_misc_w, paletteram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort master_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
	    new IO_ReadPort( 0x04, 0x04, leland_i86_response_r ),
	    new IO_ReadPort( 0x20, 0x20, ataxx_eeprom_r ),
	    new IO_ReadPort( 0xd0, 0xef, ataxx_mvram_port_r ),
	    new IO_ReadPort( 0xf0, 0xff, ataxx_master_input_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_WritePort master_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
	    new IO_WritePort( 0x05, 0x05, leland_i86_command_hi_w ),
	    new IO_WritePort( 0x06, 0x06, leland_i86_command_lo_w ),
	    new IO_WritePort( 0x0c, 0x0c, ataxx_i86_control_w ),
	    new IO_WritePort( 0x20, 0x20, ataxx_eeprom_w ),
	    new IO_WritePort( 0xd0, 0xef, ataxx_mvram_port_w ),
	    new IO_WritePort( 0xf0, 0xff, ataxx_master_output_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	
	/*************************************
	 *
	 *	Slave CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress slave_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x9fff, MRA_BANK3 ),
		new Memory_ReadAddress( 0xa000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xefff, MRA_RAM ),
		new Memory_ReadAddress( 0xfffe, 0xfffe, leland_raster_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress slave_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xefff, MWA_RAM ),
		new Memory_WriteAddress( 0xfffc, 0xfffd, leland_slave_video_addr_w ),
		new Memory_WriteAddress( 0xffff, 0xffff, ataxx_slave_banksw_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort slave_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x60, 0x7f, ataxx_svram_port_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_WritePort slave_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x60, 0x7f, ataxx_svram_port_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	/* Helps document the input ports. */
	#define IPT_SLAVEHALT 	IPT_SPECIAL
	#define IPT_EEPROM_DATA	IPT_SPECIAL
	
	
	static InputPortPtr input_ports_ataxx = new InputPortPtr(){ public void handler() { 
		PORT_START(); 		/* 0xF6 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* huh? affects trackball movement */
	    PORT_SERVICE_NO_TOGGLE( 0x08, IP_ACTIVE_LOW )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	
		PORT_START(); 		/* 0xF7 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SLAVEHALT );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_VBLANK );
		PORT_BIT( 0xfc, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 		/* 0x20 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_EEPROM_DATA );
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 		/* 0x00 - analog X */
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_X | IPF_PLAYER1, 100, 10, 0, 255 );
		PORT_START(); 		/* 0x01 - analog Y */
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_Y | IPF_PLAYER1, 100, 10, 0, 255 );
		PORT_START(); 		/* 0x02 - analog X */
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_X | IPF_PLAYER2, 100, 10, 0, 255 );
		PORT_START(); 		/* 0x03 - analog Y */
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_Y | IPF_PLAYER2, 100, 10, 0, 255 );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_wsf = new InputPortPtr(){ public void handler() { 
		PORT_START(); 		/* 0xF6 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN4 );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 );
	
		PORT_START(); 		/* 0xF7 */
	    PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SLAVEHALT );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_VBLANK );
	    PORT_BIT( 0xfc, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 		/* 0x20 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_EEPROM_DATA );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_SERVICE_NO_TOGGLE( 0x04, IP_ACTIVE_LOW )
		PORT_BIT( 0xf8, IP_ACTIVE_LOW, IPT_UNUSED );
	
	    PORT_START(); 		/* 0x0D */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
	
	    PORT_START(); 		/* 0x0E */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER4 );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER4 );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER4 );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER4 );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER3 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER3 );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER3 );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER3 );
	
	    PORT_START(); 		/* 0x0F */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START4 );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_indyheat = new InputPortPtr(){ public void handler() { 
		PORT_START(); 		/* 0xF6 */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
	    PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_HIGH, IPT_COIN2, 1 );
	    PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_HIGH, IPT_COIN3, 1 );
		PORT_BIT( 0x70, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	
		PORT_START(); 		/* 0xF7 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SLAVEHALT );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_VBLANK );
	    PORT_BIT( 0xfc, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 		/* 0x20 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_EEPROM_DATA );
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();       /* Analog wheel 1 */
		PORT_ANALOG( 0xff, 0x80, IPT_DIAL | IPF_PLAYER1, 100, 10, 0, 255 );
		PORT_START();       /* Analog wheel 2 */
		PORT_ANALOG( 0xff, 0x80, IPT_DIAL | IPF_PLAYER2, 100, 10, 0, 255 );
		PORT_START();       /* Analog wheel 3 */
		PORT_ANALOG( 0xff, 0x80, IPT_DIAL | IPF_PLAYER3, 100, 10, 0, 255 );
		PORT_START();       /* Analog pedal 1 */
		PORT_ANALOG( 0xff, 0x00, IPT_PEDAL | IPF_PLAYER1, 100, 10, 0, 255 );
		PORT_START();       /* Analog pedal 2 */
		PORT_ANALOG( 0xff, 0x00, IPT_PEDAL | IPF_PLAYER2, 100, 10, 0, 255 );
		PORT_START();       /* Analog pedal 3 */
		PORT_ANALOG( 0xff, 0x00, IPT_PEDAL | IPF_PLAYER3, 100, 10, 0, 255 );
	
	    PORT_START(); 		/* 0x0D */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNUSED );
	
	    PORT_START(); 		/* 0x0E */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNUSED );
	
	    PORT_START(); 		/* 0x0F */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
		PORT_BIT( 0x7e, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_SERVICE_NO_TOGGLE( 0x80, IP_ACTIVE_LOW )
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_brutforc = new InputPortPtr(){ public void handler() { 
		PORT_START(); 		/* 0xF6 */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_HIGH, IPT_COIN2, 1 );
	    PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
	    PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_HIGH, IPT_COIN3, 1 );
	    PORT_BIT( 0x70, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_SERVICE_NO_TOGGLE( 0x80, IP_ACTIVE_LOW )
	
		PORT_START(); 		/* 0xF7 */
	    PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SLAVEHALT );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_VBLANK );
	    PORT_BIT( 0xfc, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 		/* 0x20 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_EEPROM_DATA );
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNUSED );
	
	    PORT_START(); 		/* 0x0D */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
	    PORT_START(); 		/* 0x0E */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
	    PORT_START(); 		/* 0x0F */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER3 );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER3 );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER3 );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER3 );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );
	    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Graphics definitions
	 *
	 *************************************/
	
	static GfxLayout bklayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,6),
		6,
		new int[] { RGN_FRAC(5,6), RGN_FRAC(4,6), RGN_FRAC(3,6), RGN_FRAC(2,6), RGN_FRAC(1,6), RGN_FRAC(0,6) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, bklayout, 0, 1 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	/*************************************
	 *
	 *	Sound definitions
	 *
	 *************************************/
	
	static struct YM2151interface ym2151_interface =
	{
		1,
		4000000,
		{ YM3012_VOL(40,MIXER_PAN_LEFT,40,MIXER_PAN_RIGHT) },
		{ 0 }
	};
	
	static CustomSound_interface i186_custom_interface = new CustomSound_interface
	(
	    leland_i186_sh_start
	);
	
	
	
	/*************************************
	 *
	 *	Machine driver
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( ataxx )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("master", Z80, 6000000)
		MDRV_CPU_MEMORY(master_readmem,master_writemem)
		MDRV_CPU_PORTS(master_readport,master_writeport)
	
		MDRV_CPU_ADD_TAG("slave", Z80, 6000000)
		MDRV_CPU_MEMORY(slave_readmem,slave_writemem)
		MDRV_CPU_PORTS(slave_readport,slave_writeport)
	
		MDRV_CPU_ADD_TAG("sound", I186, 16000000/2)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(leland_i86_readmem,leland_i86_writemem)
		MDRV_CPU_PORTS(leland_i86_readport,ataxx_i86_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION((1000000*16)/(256*60))
	
		MDRV_MACHINE_INIT(ataxx)
		MDRV_NVRAM_HANDLER(ataxx)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(40*8, 30*8)
		MDRV_VISIBLE_AREA(0*8, 40*8-1, 0*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(ataxx)
		MDRV_VIDEO_EOF(leland)
		MDRV_VIDEO_UPDATE(ataxx)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, i186_custom_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( wsf )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(ataxx)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
	MACHINE_DRIVER_END
	
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadPtr rom_ataxx = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "ataxx.038",   0x00000, 0x20000, 0x0e1cf6236 );
		ROM_RELOAD(              0x10000, 0x20000 );
	
		ROM_REGION( 0x60000, REGION_CPU2, 0 );
		ROM_LOAD( "ataxx.111",  0x00000, 0x20000, 0x09a3297cc );
		ROM_LOAD( "ataxx.112",  0x20000, 0x20000, 0x07e7c3e2f );
		ROM_LOAD( "ataxx.113",  0x40000, 0x20000, 0x08cf3e101 );
	
		ROM_REGION( 0x100000, REGION_CPU3, 0 );
		ROM_LOAD16_BYTE( "ataxx.015",  0x20001, 0x20000, 0x08bb3233b );
		ROM_LOAD16_BYTE( "ataxx.001",  0x20000, 0x20000, 0x0728d75f2 );
		ROM_LOAD16_BYTE( "ataxx.016",  0x60001, 0x20000, 0x0f2bdff48 );
		ROM_RELOAD(                    0xc0001, 0x20000 );
		ROM_LOAD16_BYTE( "ataxx.002",  0x60000, 0x20000, 0x0ca06a394 );
		ROM_RELOAD(                    0xc0000, 0x20000 );
	
		ROM_REGION( 0xc0000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ataxx.098",  0x00000, 0x20000, 0x059d0f2ae );
		ROM_LOAD( "ataxx.099",  0x20000, 0x20000, 0x06ab7db25 );
		ROM_LOAD( "ataxx.100",  0x40000, 0x20000, 0x02352849e );
		ROM_LOAD( "ataxx.101",  0x60000, 0x20000, 0x04c31e02b );
		ROM_LOAD( "ataxx.102",  0x80000, 0x20000, 0x0a951228c );
		ROM_LOAD( "ataxx.103",  0xa0000, 0x20000, 0x0ed326164 );
	
		ROM_REGION( 0x00001, REGION_USER1, 0 );/* X-ROM (data used by main processor) */
	    /* Empty / not used */
	
		ROM_REGION( LELAND_BATTERY_RAM_SIZE + ATAXX_EXTRA_TRAM_SIZE, REGION_USER2, 0 );/* extra RAM regions */
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_ataxxa = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "u38",   0x00000, 0x20000, 0x3378937d );
		ROM_RELOAD(        0x10000, 0x20000 );
	
		ROM_REGION( 0x60000, REGION_CPU2, 0 );
		ROM_LOAD( "ataxx.111",  0x00000, 0x20000, 0x09a3297cc );
		ROM_LOAD( "ataxx.112",  0x20000, 0x20000, 0x07e7c3e2f );
		ROM_LOAD( "ataxx.113",  0x40000, 0x20000, 0x08cf3e101 );
	
		ROM_REGION( 0x100000, REGION_CPU3, 0 );
		ROM_LOAD16_BYTE( "ataxx.015",  0x20001, 0x20000, 0x08bb3233b );
		ROM_LOAD16_BYTE( "ataxx.001",  0x20000, 0x20000, 0x0728d75f2 );
		ROM_LOAD16_BYTE( "ataxx.016",  0x60001, 0x20000, 0x0f2bdff48 );
		ROM_RELOAD(                    0xc0001, 0x20000 );
		ROM_LOAD16_BYTE( "ataxx.002",  0x60000, 0x20000, 0x0ca06a394 );
		ROM_RELOAD(                    0xc0000, 0x20000 );
	
		ROM_REGION( 0xc0000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ataxx.098",  0x00000, 0x20000, 0x059d0f2ae );
		ROM_LOAD( "ataxx.099",  0x20000, 0x20000, 0x06ab7db25 );
		ROM_LOAD( "ataxx.100",  0x40000, 0x20000, 0x02352849e );
		ROM_LOAD( "ataxx.101",  0x60000, 0x20000, 0x04c31e02b );
		ROM_LOAD( "ataxx.102",  0x80000, 0x20000, 0x0a951228c );
		ROM_LOAD( "ataxx.103",  0xa0000, 0x20000, 0x0ed326164 );
	
		ROM_REGION( 0x00001, REGION_USER1, 0 );/* X-ROM (data used by main processor) */
	    /* Empty / not used */
	
		ROM_REGION( LELAND_BATTERY_RAM_SIZE + ATAXX_EXTRA_TRAM_SIZE, REGION_USER2, 0 );/* extra RAM regions */
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_ataxxj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "ataxxj.038", 0x00000, 0x20000, 0x513fa7d4 );
		ROM_RELOAD(             0x10000, 0x20000 );
	
		ROM_REGION( 0x60000, REGION_CPU2, 0 );
		ROM_LOAD( "ataxx.111",  0x00000, 0x20000, 0x09a3297cc );
		ROM_LOAD( "ataxx.112",  0x20000, 0x20000, 0x07e7c3e2f );
		ROM_LOAD( "ataxx.113",  0x40000, 0x20000, 0x08cf3e101 );
	
		ROM_REGION( 0x100000, REGION_CPU3, 0 );
		ROM_LOAD16_BYTE( "ataxxj.015", 0x20001, 0x20000, 0xdb266d3f );
		ROM_LOAD16_BYTE( "ataxxj.001", 0x20000, 0x20000, 0xd6db2724 );
		ROM_LOAD16_BYTE( "ataxxj.016", 0x60001, 0x20000, 0x2b127f56 );
		ROM_RELOAD(                    0xc0001, 0x20000 );
		ROM_LOAD16_BYTE( "ataxxj.002", 0x60000, 0x20000, 0x1b63b882 );
		ROM_RELOAD(                    0xc0000, 0x20000 );
	
		ROM_REGION( 0xc0000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ataxx.098",  0x00000, 0x20000, 0x059d0f2ae );
		ROM_LOAD( "ataxx.099",  0x20000, 0x20000, 0x06ab7db25 );
		ROM_LOAD( "ataxx.100",  0x40000, 0x20000, 0x02352849e );
		ROM_LOAD( "ataxx.101",  0x60000, 0x20000, 0x04c31e02b );
		ROM_LOAD( "ataxx.102",  0x80000, 0x20000, 0x0a951228c );
		ROM_LOAD( "ataxx.103",  0xa0000, 0x20000, 0x0ed326164 );
	
		ROM_REGION( 0x00001, REGION_USER1, 0 );/* X-ROM (data used by main processor) */
	    /* Empty / not used */
	
		ROM_REGION( LELAND_BATTERY_RAM_SIZE + ATAXX_EXTRA_TRAM_SIZE, REGION_USER2, 0 );/* extra RAM regions */
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_wsf = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000, REGION_CPU1, 0 );
		ROM_LOAD( "30022-03.u64",  0x00000, 0x20000, 0x2e7faa96 );
		ROM_RELOAD(                0x10000, 0x20000 );
		ROM_LOAD( "30023-03.u65",  0x30000, 0x20000, 0x7146328f );
	
		ROM_REGION( 0x100000, REGION_CPU2, 0 );
		ROM_LOAD( "30001-01.151",  0x00000, 0x20000, 0x31c63af5 );
		ROM_LOAD( "30002-01.152",  0x20000, 0x20000, 0xa53e88a6 );
		ROM_LOAD( "30003-01.153",  0x40000, 0x20000, 0x12afad1d );
		ROM_LOAD( "30004-01.154",  0x60000, 0x20000, 0xb8b3d59c );
		ROM_LOAD( "30005-01.155",  0x80000, 0x20000, 0x505724b9 );
		ROM_LOAD( "30006-01.156",  0xa0000, 0x20000, 0xc86b5c4d );
		ROM_LOAD( "30007-01.157",  0xc0000, 0x20000, 0x451321ae );
		ROM_LOAD( "30008-01.158",  0xe0000, 0x20000, 0x4d23836f );
	
		ROM_REGION( 0x100000, REGION_CPU3, 0 );
		ROM_LOAD16_BYTE( "30017-01.u3",  0x20001, 0x20000, 0x39ec13c1 );
		ROM_LOAD16_BYTE( "30020-01.u6",  0x20000, 0x20000, 0x532c02bf );
		ROM_LOAD16_BYTE( "30018-01.u4",  0x60001, 0x20000, 0x1ec16735 );
		ROM_RELOAD(                      0xc0001, 0x20000 );
		ROM_LOAD16_BYTE( "30019-01.u5",  0x60000, 0x20000, 0x2881f73b );
		ROM_RELOAD(                      0xc0000, 0x20000 );
	
		ROM_REGION( 0x60000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "30011-02.145",  0x00000, 0x10000, 0x6153569b );
		ROM_LOAD( "30012-02.146",  0x10000, 0x10000, 0x52d65e21 );
		ROM_LOAD( "30013-02.147",  0x20000, 0x10000, 0xb3afda12 );
		ROM_LOAD( "30014-02.148",  0x30000, 0x10000, 0x624e6c64 );
		ROM_LOAD( "30015-01.149",  0x40000, 0x10000, 0x5d9064f2 );
		ROM_LOAD( "30016-01.150",  0x50000, 0x10000, 0xd76389cd );
	
		ROM_REGION( 0x20000, REGION_USER1, 0 );/* X-ROM (data used by main processor) */
		ROM_LOAD( "30009-01.u68",  0x00000, 0x10000, 0xf2fbfc15 );
		ROM_LOAD( "30010-01.u69",  0x10000, 0x10000, 0xb4ed2d3b );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* externally clocked DAC data */
		ROM_LOAD( "30021-01.u8",   0x00000, 0x20000, 0xbb91dc10 );
	
		ROM_REGION( LELAND_BATTERY_RAM_SIZE + ATAXX_EXTRA_TRAM_SIZE, REGION_USER2, 0 );/* extra RAM regions */
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_indyheat = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x90000, REGION_CPU1, 0 );
		ROM_LOAD( "u64_27c.010",   0x00000, 0x20000, 0x2b97a347 );
		ROM_RELOAD(                0x10000, 0x20000 );
		ROM_LOAD( "u65_27c.010",   0x30000, 0x20000, 0x71301d74 );
		ROM_LOAD( "u66_27c.010",   0x50000, 0x20000, 0xc9612072 );
		ROM_LOAD( "u67_27c.010",   0x70000, 0x20000, 0x4c4b25e0 );
	
		ROM_REGION( 0x160000, REGION_CPU2, 0 );
		ROM_LOAD( "u151_27c.010",  0x00000, 0x20000, 0x2622dfa4 );
		ROM_LOAD( "u152_27c.020",  0x20000, 0x20000, 0xad40e4e2 );
		ROM_CONTINUE(             0x120000, 0x20000 );
		ROM_LOAD( "u153_27c.020",  0x40000, 0x20000, 0x1e3803f7 );
		ROM_CONTINUE(             0x140000, 0x20000 );
		ROM_LOAD( "u154_27c.010",  0x60000, 0x20000, 0x76d3c235 );
		ROM_LOAD( "u155_27c.010",  0x80000, 0x20000, 0xd5d866b3 );
		ROM_LOAD( "u156_27c.010",  0xa0000, 0x20000, 0x7fe71842 );
		ROM_LOAD( "u157_27c.010",  0xc0000, 0x20000, 0xa6462adc );
		ROM_LOAD( "u158_27c.010",  0xe0000, 0x20000, 0xd6ef27a3 );
	
		ROM_REGION( 0x100000, REGION_CPU3, 0 );
		ROM_LOAD16_BYTE( "u3_27c.010",  0x20001, 0x20000, 0x97413818 );
		ROM_LOAD16_BYTE( "u6_27c.010",  0x20000, 0x20000, 0x15a89962 );
		ROM_LOAD16_BYTE( "u4_27c.010",  0x60001, 0x20000, 0xfa7bfa04 );
		ROM_RELOAD(                     0xc0001, 0x20000 );
		ROM_LOAD16_BYTE( "u5_27c.010",  0x60000, 0x20000, 0x198285d4 );
		ROM_RELOAD(                     0xc0000, 0x20000 );
	
		ROM_REGION( 0xc0000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "u145_27c.010",  0x00000, 0x20000, 0x612d4bf8 );
		ROM_LOAD( "u146_27c.010",  0x20000, 0x20000, 0x77a725f6 );
		ROM_LOAD( "u147_27c.010",  0x40000, 0x20000, 0xd6aac372 );
		ROM_LOAD( "u148_27c.010",  0x60000, 0x20000, 0x5d19723e );
		ROM_LOAD( "u149_27c.010",  0x80000, 0x20000, 0x29056791 );
		ROM_LOAD( "u150_27c.010",  0xa0000, 0x20000, 0xcb73dd6a );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* X-ROM (data used by main processor) */
		ROM_LOAD( "u68_27c.010",   0x00000, 0x10000, 0x9e88efb3 );
		ROM_CONTINUE(              0x20000, 0x10000 );
		ROM_LOAD( "u69_27c.010",   0x10000, 0x10000, 0xaa39fcb3 );
		ROM_CONTINUE(              0x30000, 0x10000 );
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 );/* externally clocked DAC data */
		ROM_LOAD( "u8_27c.010",  0x00000, 0x20000, 0x9f16e5b6 );
		ROM_LOAD( "u9_27c.010",  0x20000, 0x20000, 0x0dc8f488 );
	
		ROM_REGION( LELAND_BATTERY_RAM_SIZE + ATAXX_EXTRA_TRAM_SIZE, REGION_USER2, 0 );/* extra RAM regions */
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_brutforc = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x90000, REGION_CPU1, 0 );
		ROM_LOAD( "u64",   0x00000, 0x20000, 0x008ae3b8 );
		ROM_RELOAD(                 0x10000, 0x20000 );
		ROM_LOAD( "u65",   0x30000, 0x20000, 0x6036e3fa );
		ROM_LOAD( "u66",   0x50000, 0x20000, 0x7ebf0795 );
		ROM_LOAD( "u67",   0x70000, 0x20000, 0xe3cbf8b4 );
	
		ROM_REGION( 0x100000, REGION_CPU2, 0 );
		ROM_LOAD( "u151",  0x00000, 0x20000, 0xbd3b677b );
		ROM_LOAD( "u152",  0x20000, 0x20000, 0x5f4434e7 );
		ROM_LOAD( "u153",  0x40000, 0x20000, 0x20f7df53 );
		ROM_LOAD( "u154",  0x60000, 0x20000, 0x69ce2329 );
		ROM_LOAD( "u155",  0x80000, 0x20000, 0x33d92e25 );
		ROM_LOAD( "u156",  0xa0000, 0x20000, 0xde7eca8b );
		ROM_LOAD( "u157",  0xc0000, 0x20000, 0xe42b3dba );
		ROM_LOAD( "u158",  0xe0000, 0x20000, 0xa0aa3220 );
	
		ROM_REGION( 0x100000, REGION_CPU3, 0 );
		ROM_LOAD16_BYTE( "u3",  0x20001, 0x20000, 0x9984906c );
		ROM_LOAD16_BYTE( "u6",  0x20000, 0x20000, 0xc9c5a413 );
		ROM_LOAD16_BYTE( "u4",  0x60001, 0x20000, 0xca8ab3a6 );
		ROM_RELOAD(             0xc0001, 0x20000 );
		ROM_LOAD16_BYTE( "u5",  0x60000, 0x20000, 0xcbdb914b );
		ROM_RELOAD(             0xc0000, 0x20000 );
	
		ROM_REGION( 0x180000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "u145",  0x000000, 0x40000, 0xc3d20d24 );
		ROM_LOAD( "u146",  0x040000, 0x40000, 0x43e9dd87 );
		ROM_LOAD( "u147",  0x080000, 0x40000, 0xfb855ce8 );
		ROM_LOAD( "u148",  0x0c0000, 0x40000, 0xe4b54eae );
		ROM_LOAD( "u149",  0x100000, 0x40000, 0xcf48401c );
		ROM_LOAD( "u150",  0x140000, 0x40000, 0xca9e1e33 );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* X-ROM (data used by main processor) */
		ROM_LOAD( "u68",   0x00000, 0x10000, 0x77c8de62 );
		ROM_CONTINUE(      0x20000, 0x10000 );
		ROM_LOAD( "u69",   0x10000, 0x10000, 0x113aa6d5 );
		ROM_CONTINUE(      0x30000, 0x10000 );
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 );/* externally clocked DAC data */
		ROM_LOAD( "u8",  0x00000, 0x20000, 0x1e0ead72 );
		ROM_LOAD( "u9",  0x20000, 0x20000, 0x3195b305 );
		ROM_LOAD( "u10", 0x40000, 0x20000, 0x1dc5f375 );
		ROM_LOAD( "u11", 0x60000, 0x20000, 0x5ed4877f );
	
		ROM_REGION( LELAND_BATTERY_RAM_SIZE + ATAXX_EXTRA_TRAM_SIZE, REGION_USER2, 0 );/* extra RAM regions */
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_asylum = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x90000, REGION_CPU1, 0 );
		ROM_LOAD( "asy-m0.64",   0x00000, 0x20000, 0xf5ca36fd );
		ROM_RELOAD(              0x10000, 0x20000 );
		ROM_LOAD( "asy-m1.65",   0x30000, 0x20000, 0x14d91d09 );
		ROM_LOAD( "asy-m2.66",   0x50000, 0x20000, 0xa34a6ef9 );
		ROM_LOAD( "asy-m3.67",   0x70000, 0x20000, 0x9db4c2b1 );
	
		ROM_REGION( 0x1e0000, REGION_CPU2, 0 );
		ROM_LOAD( "asy-sp0.151",  0x00000, 0x20000, 0x5ad5e3b0 );
		ROM_LOAD( "asy-sp2.152",  0x20000, 0x20000, 0x6d2997ec );
		ROM_CONTINUE(            0x120000, 0x20000 );
		ROM_LOAD( "asy-sp4.153",  0x40000, 0x20000, 0x7c61973c );
		ROM_CONTINUE(            0x140000, 0x20000 );
		ROM_LOAD( "asy-sp6.154",  0x60000, 0x20000, 0xf0a4f9d3 );
		ROM_CONTINUE(            0x160000, 0x20000 );
		ROM_LOAD( "asy-sp8.155",  0x80000, 0x20000, 0x2ad0640e );
		ROM_CONTINUE(            0x180000, 0x20000 );
		ROM_LOAD( "asy-spa.156",  0xa0000, 0x20000, 0x9d584fb4 );
		ROM_CONTINUE(            0x1a0000, 0x20000 );
		ROM_LOAD( "asy-spc.157",  0xc0000, 0x20000, 0x8485e48c );
		ROM_CONTINUE(            0x1c0000, 0x20000 );
		ROM_LOAD( "asy-spe.158",  0xe0000, 0x20000, 0x49d19520 );
	
		ROM_REGION( 0x100000, REGION_CPU3, 0 );
		ROM_LOAD16_BYTE( "asy-65.3",  0x20001, 0x20000, 0x709bdc78 );
		ROM_LOAD16_BYTE( "asy-65.6",  0x20000, 0x20000, 0xd019fb2e );
		ROM_LOAD16_BYTE( "asy-65.4",  0x60001, 0x20000, 0x1882c3b2 );
		ROM_RELOAD(             0xc0001, 0x20000 );
		ROM_LOAD16_BYTE( "asy-65.5",  0x60000, 0x20000, 0x5814b307 );
		ROM_RELOAD(             0xc0000, 0x20000 );
	
		ROM_REGION( 0x180000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "asy-chr0.145",  0x000000, 0x40000, 0x4dbcae49 );
		ROM_LOAD( "asy-chr1.146",  0x040000, 0x40000, 0x34e7762d );
		ROM_LOAD( "asy-chr2.147",  0x080000, 0x40000, 0xf9b0d375 );
		ROM_LOAD( "asy-chr3.148",  0x0c0000, 0x40000, 0x5efcae94 );
		ROM_LOAD( "asy-chr4.149",  0x100000, 0x40000, 0xdbc2b155 );
		ROM_LOAD( "asy-chr5.150",  0x140000, 0x40000, 0x9675e44f );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* X-ROM (data used by main processor) */
		ROM_LOAD( "asy-m4.68",   0x00000, 0x10000, 0x77c8de62 );
		ROM_CONTINUE(            0x20000, 0x10000 );
		ROM_LOAD( "asy-m5.69",   0x10000, 0x10000, 0xbfc50d6c );
		ROM_CONTINUE(            0x30000, 0x10000 );
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 );/* externally clocked DAC data */
		ROM_LOAD( "asy-65.8",  0x00000, 0x20000, 0x624ad02f );
		ROM_LOAD( "asy-65.9",  0x20000, 0x20000, 0xc92ff376 );
		ROM_LOAD( "asy-65.10", 0x40000, 0x20000, 0x744dbf25 );
		ROM_LOAD( "asy-65.11", 0x60000, 0x20000, 0x4b185d22 );
	
		ROM_REGION( LELAND_BATTERY_RAM_SIZE + ATAXX_EXTRA_TRAM_SIZE, REGION_USER2, 0 );/* extra RAM regions */
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Driver initialization
	 *
	 *************************************/
	
	static DRIVER_INIT( ataxx )
	{
		/* initialize the default EEPROM state */
		static const UINT16 ataxx_eeprom_data[] =
		{
			0x09,0x0101,
			0x0a,0x0104,
			0x0b,0x0401,
			0x0c,0x0101,
			0x0d,0x0004,
			0x13,0x0100,
			0x14,0x5a04,
			0xffff
		};
		ataxx_init_eeprom(0x00, ataxx_eeprom_data, 0x00);
	
		leland_rotate_memory(0);
		leland_rotate_memory(1);
	
		/* set up additional input ports */
		install_port_read_handler(0, 0x00, 0x03, ataxx_trackball_r);
	
		/* optimize the sound */
		leland_i86_optimize_address(0x612);
	}
	
	
	static DRIVER_INIT( ataxxj )
	{
		/* initialize the default EEPROM state */
		static const UINT16 ataxxj_eeprom_data[] =
		{
			0x09,0x0101,
			0x0a,0x0104,
			0x0b,0x0001,
			0x0c,0x0101,
			0x13,0xff00,
			0x3f,0x3c0c,
			0xffff
		};
		ataxx_init_eeprom(0x00, ataxxj_eeprom_data, 0x00);
	
		leland_rotate_memory(0);
		leland_rotate_memory(1);
	
		/* set up additional input ports */
		install_port_read_handler(0, 0x00, 0x03, ataxx_trackball_r);
	
		/* optimize the sound */
		leland_i86_optimize_address(0x612);
	}
	
	
	static DRIVER_INIT( wsf )
	{
		/* initialize the default EEPROM state */
		static const UINT16 wsf_eeprom_data[] =
		{
			0x04,0x0101,
			0x0b,0x04ff,
			0x0d,0x0500,
			0x26,0x26ac,
			0x27,0xff0a,
			0x28,0xff00,
			0xffff
		};
		ataxx_init_eeprom(0x00, wsf_eeprom_data, 0x00);
	
		leland_rotate_memory(0);
		leland_rotate_memory(1);
	
		/* set up additional input ports */
		install_port_read_handler(0, 0x0d, 0x0d, input_port_3_r);
		install_port_read_handler(0, 0x0e, 0x0e, input_port_4_r);
		install_port_read_handler(0, 0x0f, 0x0f, input_port_5_r);
	
		/* optimize the sound */
		leland_i86_optimize_address(0x612);
	}
	
	
	static DRIVER_INIT( indyheat )
	{
		/* initialize the default EEPROM state */
		static const UINT16 indyheat_eeprom_data[] =
		{
			0x2c,0x0100,
			0x2d,0x0401,
			0x2e,0x05ff,
			0x2f,0x4b4b,
			0x30,0xfa4b,
			0x31,0xfafa,
			0xffff
		};
		ataxx_init_eeprom(0x00, indyheat_eeprom_data, 0x00);
	
		leland_rotate_memory(0);
		leland_rotate_memory(1);
	
		/* set up additional input ports */
		install_port_read_handler(0, 0x00, 0x02, indyheat_wheel_r);
		install_port_read_handler(0, 0x08, 0x0b, indyheat_analog_r);
		install_port_read_handler(0, 0x0d, 0x0d, input_port_9_r);
		install_port_read_handler(0, 0x0e, 0x0e, input_port_10_r);
		install_port_read_handler(0, 0x0f, 0x0f, input_port_11_r);
	
		/* set up additional output ports */
		install_port_write_handler(0, 0x08, 0x0b, indyheat_analog_w);
	
		/* optimize the sound */
		leland_i86_optimize_address(0x613);
	}
	
	
	static DRIVER_INIT( brutforc )
	{
		/* initialize the default EEPROM state */
		static const UINT16 brutforc_eeprom_data[] =
		{
			0x27,0x0303,
			0x28,0x0003,
			0x30,0x01ff,
			0x31,0x0100,
			0x35,0x0404,
			0x36,0x0104,
			0xffff
		};
		ataxx_init_eeprom(0x00, brutforc_eeprom_data, 0x00);
	
		leland_rotate_memory(0);
		leland_rotate_memory(1);
	
		/* set up additional input ports */
		install_port_read_handler(0, 0x0d, 0x0d, input_port_3_r);
		install_port_read_handler(0, 0x0e, 0x0e, input_port_4_r);
		install_port_read_handler(0, 0x0f, 0x0f, input_port_5_r);
	
		/* optimize the sound */
		leland_i86_optimize_address(0x613);
	}
	
	
	static DRIVER_INIT( asylum )
	{
		/* initialize the default EEPROM state */
		static const UINT16 asylum_eeprom_data[] =
		{
			0x02,0x0101,
			0x03,0x0101,
			0x04,0x0101,
			0x05,0xffff,
			0x06,0x0403,
			0x07,0x0400,
			0xffff
		};
		ataxx_init_eeprom(0x00, asylum_eeprom_data, 0x00);
	
		leland_rotate_memory(0);
		leland_rotate_memory(1);
	
		/* set up additional input ports */
		install_port_read_handler(0, 0x0d, 0x0d, input_port_3_r);
		install_port_read_handler(0, 0x0e, 0x0e, input_port_4_r);
		install_port_read_handler(0, 0x0f, 0x0f, input_port_5_r);
	
		/* optimize the sound */
		leland_i86_optimize_address(0x613);
	}
	
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_ataxx	   = new GameDriver("1990"	,"ataxx"	,"ataxx.java"	,rom_ataxx,null	,machine_driver_ataxx	,input_ports_ataxx	,init_ataxx	,ROT0	,	"Leland Corp.", "Ataxx (set 1)" )
	public static GameDriver driver_ataxxa	   = new GameDriver("1990"	,"ataxxa"	,"ataxx.java"	,rom_ataxxa,driver_ataxx	,machine_driver_ataxx	,input_ports_ataxx	,init_ataxx	,ROT0	,	"Leland Corp.", "Ataxx (set 2)" )
	public static GameDriver driver_ataxxj	   = new GameDriver("1990"	,"ataxxj"	,"ataxx.java"	,rom_ataxxj,driver_ataxx	,machine_driver_ataxx	,input_ports_ataxx	,init_ataxxj	,ROT0	,	"Leland Corp.", "Ataxx (Japan)" )
	public static GameDriver driver_wsf	   = new GameDriver("1990"	,"wsf"	,"ataxx.java"	,rom_wsf,null	,machine_driver_wsf	,input_ports_wsf	,init_wsf	,ROT0	,	"Leland Corp.", "World Soccer Finals" )
	public static GameDriver driver_indyheat	   = new GameDriver("1991"	,"indyheat"	,"ataxx.java"	,rom_indyheat,null	,machine_driver_wsf	,input_ports_indyheat	,init_indyheat	,ROT0	,	"Leland Corp.", "Danny Sullivan's Indy Heat" )
	public static GameDriver driver_brutforc	   = new GameDriver("1991"	,"brutforc"	,"ataxx.java"	,rom_brutforc,null	,machine_driver_wsf	,input_ports_brutforc	,init_brutforc	,ROT0	,	"Leland Corp.", "Brute Force" )
	public static GameDriver driver_asylum	   = new GameDriver("1991"	,"asylum"	,"ataxx.java"	,rom_asylum,null	,machine_driver_wsf	,input_ports_brutforc	,init_asylum	,ROT270	,	"Leland Corp.", "Asylum (prototype)" )
}
