/* US Games - Trivia / Quiz / 'Amusement Only' Gambling Games */

/*

there is a 'Security Test' in service mode

'usg82', 'usg83' and 'usg83x' don't seem to be able to record
the changes you make in the "test mode" 8(

*/

/* readme info

US Games
Counter Top Mini Games
1987-1992

In this archive are different versions.
Version 3.2, 8.3, 18.5, and 25.2.

Hardware Specs: MC6809P, MC6845P, MB8146A x 3
Sound: AY-3-8912

*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class usgames
{
	
	/* vidhrdw */
	VIDEO_START(usg);
	PALETTE_INIT(usg);
	VIDEO_UPDATE(usg);
	extern struct tilemap *usg_tilemap;
	
	
	extern data8_t *usg_videoram,*usg_charram;
	
	
	public static WriteHandlerPtr usg_rombank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		unsigned char *RAM = memory_region(REGION_CPU1);
	
	//	logerror ("BANK WRITE? -%02x-\n",data);
	//usrintf_showmessage("%02x",data);
	
		cpu_setbank( 1,&RAM[ 0x10000 + 0x4000 * data] );
	} };
	
	public static WriteHandlerPtr lamps1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* button lamps */
		set_led_status(0,data & 0x01);
		set_led_status(1,data & 0x02);
		set_led_status(2,data & 0x04);
		set_led_status(3,data & 0x08);
		set_led_status(4,data & 0x10);
	
		/* bit 5 toggles all the time - extra lamp? */
	} };
	
	public static WriteHandlerPtr lamps2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* bit 5 toggles all the time - extra lamp? */
	} };
	
	
	
	public static Memory_ReadAddress usg_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_RAM ),
		new Memory_ReadAddress( 0x1000, 0x1fff, MRA_RAM ),
	
		new Memory_ReadAddress( 0x2000, 0x2000, input_port_1_r ),
		new Memory_ReadAddress( 0x2010, 0x2010, input_port_0_r ),
		new Memory_ReadAddress( 0x2041, 0x2041, input_port_2_r ),
		new Memory_ReadAddress( 0x2070, 0x2070, input_port_3_r ),
	
		new Memory_ReadAddress( 0x2800, 0x2fff, MRA_RAM ),
		new Memory_ReadAddress( 0x3000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress usg185_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_RAM ),
		new Memory_ReadAddress( 0x1000, 0x1fff, MRA_RAM ),
	
		new Memory_ReadAddress( 0x2400, 0x2400, input_port_1_r ),
		new Memory_ReadAddress( 0x2410, 0x2410, input_port_0_r ),
		new Memory_ReadAddress( 0x2441, 0x2441, input_port_2_r ),
		new Memory_ReadAddress( 0x2470, 0x2470, input_port_3_r ),
	
		new Memory_ReadAddress( 0x2800, 0x2fff, MRA_RAM ),
		new Memory_ReadAddress( 0x3000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress usg_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_RAM ),
		new Memory_WriteAddress( 0x1000, 0x1fff, MWA_RAM, generic_nvram, generic_nvram_size ),
	
		new Memory_WriteAddress( 0x2020, 0x2020, lamps1_w ),
		new Memory_WriteAddress( 0x2030, 0x2030, lamps2_w ),
	
		new Memory_WriteAddress( 0x2040, 0x2040, crtc6845_address_w ),
		new Memory_WriteAddress( 0x2041, 0x2041, crtc6845_register_w ),
	
		new Memory_WriteAddress( 0x2400, 0x2400, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x2401, 0x2401, AY8910_write_port_0_w ),
	
		new Memory_WriteAddress( 0x2060, 0x2060, usg_rombank_w ),
	
		new Memory_WriteAddress( 0x2800, 0x2fff, usg_charram_w, usg_charram ),
		new Memory_WriteAddress( 0x3000, 0x3fff, usg_videoram_w, usg_videoram ),
		new Memory_WriteAddress( 0x4000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress usg185_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_RAM ),
		new Memory_WriteAddress( 0x1000, 0x1fff, MWA_RAM, generic_nvram, generic_nvram_size ),
	
		new Memory_WriteAddress( 0x2420, 0x2420, lamps1_w ),
		new Memory_WriteAddress( 0x2430, 0x2430, lamps2_w ),
	
		new Memory_WriteAddress( 0x2440, 0x2440, crtc6845_address_w ),
		new Memory_WriteAddress( 0x2441, 0x2441, crtc6845_register_w ),
	
		new Memory_WriteAddress( 0x2000, 0x2000, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x2001, 0x2001, AY8910_write_port_0_w ),
	
		new Memory_WriteAddress( 0x2460, 0x2460, usg_rombank_w ),
	
		new Memory_WriteAddress( 0x2800, 0x2fff, usg_charram_w, usg_charram ),
		new Memory_WriteAddress( 0x3000, 0x3fff, usg_videoram_w, usg_videoram ),
		new Memory_WriteAddress( 0x4000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_usg = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_BUTTON1, "Button 1", KEYCODE_Z, IP_JOY_DEFAULT );
		PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_BUTTON2, "Button 2", KEYCODE_X, IP_JOY_DEFAULT );
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_BUTTON3, "Button 3", KEYCODE_C, IP_JOY_DEFAULT );
		PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_BUTTON4, "Button 4", KEYCODE_V, IP_JOY_DEFAULT );
		PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_BUTTON5, "Button 5", KEYCODE_B, IP_JOY_DEFAULT );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(0x02, IP_ACTIVE_HIGH, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
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
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_SPECIAL );// +12 Volts?
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_VBLANK );
	
		PORT_START(); 
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
	
		PORT_START(); 
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
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		0x100,
		1,
		new int[] { 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_CPU1, 0x2800, charlayout, 0, 256 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,			/* 1 chip */
		2000000,	/* 2 MHz? */
		new int[] { 30 },		/* volume */
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	
	static MACHINE_DRIVER_START( usg )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", M6809, 2000000) /* ?? */
		MDRV_CPU_MEMORY(usg_readmem,usg_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,5) /* ?? */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_NVRAM_HANDLER(generic_1fill)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(7*8, 57*8-1, 0*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(16)
		MDRV_COLORTABLE_LENGTH(2*256)
	
		MDRV_PALETTE_INIT(usg)
		MDRV_VIDEO_START(usg)
		MDRV_VIDEO_UPDATE(usg)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( usg185 )
		MDRV_IMPORT_FROM(usg)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(usg185_readmem,usg185_writemem)
	MACHINE_DRIVER_END
	
	
	
	static RomLoadPtr rom_usg32 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );
		ROM_LOAD( "usg32-0.bin", 0x08000, 0x08000, 0xbc313387 );
		/* for the banked region */
		ROM_LOAD( "usg32-1.bin", 0x18000, 0x08000, 0xbaaea800 );
		ROM_LOAD( "usg32-2.bin", 0x28000, 0x08000, 0xd73d7f48 );
		ROM_LOAD( "usg32-3.bin", 0x38000, 0x08000, 0x22747804 );
	ROM_END(); }}; 
	
	
	/* You can't change the status of "Sexy Triv I" and "Sexy Triv II" */
	static RomLoadPtr rom_usg83 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );
		ROM_LOAD( "grom08-3.rom", 0x08000, 0x08000, 0xaae84186 );
		/* for the banked region */
		ROM_LOAD( "usg83-1.bin", 0x18000, 0x08000, 0x7b520b6f );
		ROM_LOAD( "usg83-2.bin", 0x28000, 0x08000, 0x29fbb23b );
		ROM_LOAD( "grom3.rom",   0x38000, 0x10000, 0x4e110844 );
		ROM_LOAD( "usg83-4.bin", 0x48000, 0x08000, 0x437697c4 );
	ROM_END(); }}; 
	
	/* Similar to 'usg83', but you can change the status of "Sexy Triv I" */
	static RomLoadPtr rom_usg83x = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );
		ROM_LOAD( "usg83-0.bin", 0x08000, 0x08000, 0x4ad9b6e0 );
		/* for the banked region */
		ROM_LOAD( "usg83-1.bin", 0x18000, 0x08000, 0x7b520b6f );
		ROM_LOAD( "usg83-2.bin", 0x28000, 0x08000, 0x29fbb23b );
		ROM_LOAD( "usg83-3.bin", 0x38000, 0x08000, 0x41c475ac );
		ROM_LOAD( "usg83-4.bin", 0x48000, 0x08000, 0x437697c4 );
	ROM_END(); }}; 
	
	/* Similar to 'usg83', but "Sport Triv" and "Rush Hour" aren't available by default */
	static RomLoadPtr rom_usg82 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );
		ROM_LOAD( "rom0.rom",   0x08000, 0x08000, 0x09c20b78 );
		/* for the banked region */
		ROM_LOAD( "grom1.rom",   0x18000, 0x08000, 0x915a9ff4 );
		ROM_LOAD( "usg83-2.bin", 0x28000, 0x08000, 0x29fbb23b );
		ROM_LOAD( "grom3.rom",   0x38000, 0x10000, 0x4e110844 );
		ROM_LOAD( "usg83-4.bin", 0x48000, 0x08000, 0x437697c4 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_usg185 = new RomLoadPtr(){ public void handler(){  // an upgraded 182?
		ROM_REGION( 0x80000, REGION_CPU1, 0 );
		ROM_LOAD( "usg182.u12",   0x08000, 0x08000, 0x2f4ed125 );
		/* for the banked region */
		ROM_LOAD( "usg185.u36",   0x10000, 0x10000, 0xb104744d );// ROM 4
		ROM_LOAD( "usg185.u35",   0x20000, 0x10000, 0x795e71c8 );// ROM 3
		ROM_LOAD( "usg185.u28",   0x30000, 0x10000, 0xc6ba8a81 );// ROM 2
		ROM_LOAD( "usg185.u18",   0x40000, 0x10000, 0x1cfd934d );// ROM 1
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_usg252 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );
		ROM_LOAD( "usg252.u12",   0x08000, 0x08000, 0x766a855a );
		/* for the banked region */
		ROM_LOAD( "usg252.u28",   0x1c000, 0x04000, 0xd44d2ffa );// ROM 2
		ROM_CONTINUE(             0x18000, 0x04000 );
		ROM_CONTINUE(             0x14000, 0x04000 );
		ROM_CONTINUE(             0x10000, 0x04000 );
		ROM_LOAD( "usg252.u18",   0x2c000, 0x04000, 0x2fff1da2 );// ROM 1
		ROM_CONTINUE(             0x28000, 0x04000 );
		ROM_CONTINUE(             0x24000, 0x04000 );
		ROM_CONTINUE(             0x20000, 0x04000 );
		ROM_LOAD( "usg252.u36",   0x3c000, 0x04000, 0xb6d007be );// ROM 4
		ROM_CONTINUE(             0x38000, 0x04000 );
		ROM_CONTINUE(             0x34000, 0x04000 );
		ROM_CONTINUE(             0x30000, 0x04000 );
		ROM_LOAD( "usg252.u35",   0x4c000, 0x04000, 0x9542295b );// ROM 3
		ROM_CONTINUE(             0x48000, 0x04000 );
		ROM_CONTINUE(             0x44000, 0x04000 );
		ROM_CONTINUE(             0x40000, 0x04000 );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_usg32	   = new GameDriver("1987"	,"usg32"	,"usgames.java"	,rom_usg32,null	,machine_driver_usg	,input_ports_usg	,null	,ROT0	,	"U.S. Games", "Super Duper Casino (California V3.2)" )
	public static GameDriver driver_usg83	   = new GameDriver("1988"	,"usg83"	,"usgames.java"	,rom_usg83,null	,machine_driver_usg	,input_ports_usg	,null	,ROT0	,	"U.S. Games", "Super Ten V8.3" )
	public static GameDriver driver_usg83x	   = new GameDriver("1988"	,"usg83x"	,"usgames.java"	,rom_usg83x,driver_usg83	,machine_driver_usg	,input_ports_usg	,null	,ROT0	,	"U.S. Games", "Super Ten V8.3X" )
	public static GameDriver driver_usg82	   = new GameDriver("1988"	,"usg82"	,"usgames.java"	,rom_usg82,driver_usg83	,machine_driver_usg	,input_ports_usg	,null	,ROT0	,	"U.S. Games", "Super Ten V8.2" )	// "Feb.08,1988"
	public static GameDriver driver_usg185	   = new GameDriver("1991"	,"usg185"	,"usgames.java"	,rom_usg185,null	,machine_driver_usg185	,input_ports_usg	,null	,ROT0	,	"U.S. Games", "Games V18.7C" )
	public static GameDriver driver_usg252	   = new GameDriver("1992"	,"usg252"	,"usgames.java"	,rom_usg252,null	,machine_driver_usg185	,input_ports_usg	,null	,ROT0	,	"U.S. Games", "Games V25.4X" )
}
