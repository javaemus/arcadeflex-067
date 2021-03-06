/***************************************************************************

The Simpsons (c) 1991 Konami Co. Ltd

Preliminary driver by:
Ernesto Corvi
someone@secureshell.com

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class simpsons
{
	
	/* from vidhrdw */
	VIDEO_START( simpsons );
	VIDEO_UPDATE( simpsons );
	
	/* from machine */
	MACHINE_INIT( simpsons );
	NVRAM_HANDLER( simpsons );
	
	/***************************************************************************
	
	  Memory Maps
	
	***************************************************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_BANK3 ),
		new Memory_ReadAddress( 0x1f80, 0x1f80, input_port_4_r ),
		new Memory_ReadAddress( 0x1f81, 0x1f81, simpsons_eeprom_r ),
		new Memory_ReadAddress( 0x1f90, 0x1f90, input_port_0_r ),
		new Memory_ReadAddress( 0x1f91, 0x1f91, input_port_1_r ),
		new Memory_ReadAddress( 0x1f92, 0x1f92, input_port_2_r ),
		new Memory_ReadAddress( 0x1f93, 0x1f93, input_port_3_r ),
		new Memory_ReadAddress( 0x1fc4, 0x1fc4, simpsons_sound_interrupt_r ),
		new Memory_ReadAddress( 0x1fc6, 0x1fc7, simpsons_sound_r ),	/* K053260 */
		new Memory_ReadAddress( 0x1fc8, 0x1fc9, K053246_r ),
		new Memory_ReadAddress( 0x1fca, 0x1fca, watchdog_reset_r ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_BANK4 ),
		new Memory_ReadAddress( 0x0000, 0x3fff, K052109_r ),
		new Memory_ReadAddress( 0x4856, 0x4856, simpsons_speedup2_r ),
		new Memory_ReadAddress( 0x4942, 0x4942, simpsons_speedup1_r ),
		new Memory_ReadAddress( 0x4000, 0x5fff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x7fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_BANK3 ),
		new Memory_WriteAddress( 0x1fa0, 0x1fa7, K053246_w ),
		new Memory_WriteAddress( 0x1fb0, 0x1fbf, K053251_w ),
		new Memory_WriteAddress( 0x1fc0, 0x1fc0, simpsons_coin_counter_w ),
		new Memory_WriteAddress( 0x1fc2, 0x1fc2, simpsons_eeprom_w ),
		new Memory_WriteAddress( 0x1fc6, 0x1fc7, K053260_0_w ),
		new Memory_WriteAddress( 0x2000, 0x3fff, MWA_BANK4 ),
		new Memory_WriteAddress( 0x0000, 0x3fff, K052109_w ),
		new Memory_WriteAddress( 0x4000, 0x5fff, MWA_RAM ),
		new Memory_WriteAddress( 0x6000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr z80_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		unsigned char *RAM = memory_region(REGION_CPU2);
	
		offset = 0x10000 + ( ( ( data & 7 ) - 2 ) * 0x4000 );
	
		cpu_setbank( 2, &RAM[ offset ] );
	} };
	
	#if 0
	static int nmi_enabled;
	
	static void sound_nmi_callback( int param )
	{
		cpu_set_nmi_line( 1, ( nmi_enabled ) ? CLEAR_LINE : ASSERT_LINE );
	
		nmi_enabled = 0;
	}
	#endif
	
	static void nmi_callback(int param)
	{
		cpu_set_nmi_line(1,ASSERT_LINE);
	}
	
	public static WriteHandlerPtr z80_arm_nmi_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	//	sound_nmi_enabled = 1;
		cpu_set_nmi_line(1,CLEAR_LINE);
		timer_set(TIME_IN_USEC(50),0,nmi_callback);	/* kludge until the K053260 is emulated correctly */
	} };
	
	public static Memory_ReadAddress z80_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK2 ),
		new Memory_ReadAddress( 0xf000, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf801, 0xf801, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0xfc00, 0xfc2f, K053260_0_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress z80_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf800, 0xf800, YM2151_register_port_0_w ),
		new Memory_WriteAddress( 0xf801, 0xf801, YM2151_data_port_0_w ),
		new Memory_WriteAddress( 0xfa00, 0xfa00, z80_arm_nmi_w ),
		new Memory_WriteAddress( 0xfc00, 0xfc2f, K053260_0_w ),
		new Memory_WriteAddress( 0xfe00, 0xfe00, z80_bankswitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/***************************************************************************
	
		Input Ports
	
	***************************************************************************/
	
	static InputPortPtr input_ports_simpsons = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* IN0 - Player 1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//BUTTON3 Unused
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN1 - Player 2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//BUTTON3 Unused
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 	/* IN2 - Player 3 - Used on the 4p version */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//BUTTON3 Unused
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START3 );
	
		PORT_START(); 	/* IN3 - Player 4 - Used on the 4p version */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//BUTTON3 Unused
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START4 );
	
		PORT_START();  /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );//SERVICE1 Unused
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );//SERVICE2 Unused
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//SERVICE3 Unused
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );//SERVICE4 Unused
	
		PORT_START();  /* IN5 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_simpsn2p = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* IN0 - Player 1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//BUTTON3 Unused
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN1 - Player 2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//BUTTON3 Unused
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 	/* IN2 - Player 3 - Used on the 4p version */
	//	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER3 );
	//	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER3 );
	//	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER3 );
	//	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER3 );
	//	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
	//	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
	//	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//BUTTON3 Unused
	//	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START3 );
	
		PORT_START(); 	/* IN3 - Player 4 - Used on the 4p version */
	//	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER4 );
	//	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER4 );
	//	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER4 );
	//	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER4 );
	//	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 );
	//	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 );
	//	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//BUTTON3 Unused
	//	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START4 );
	
		PORT_START();  /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );//COIN3 Unused
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );//COIN4 Unused
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );//SERVICE2 Unused
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );//SERVICE3 Unused
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );//SERVICE4 Unused
	
		PORT_START();  /* IN5 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT( 0xfe, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	
	/***************************************************************************
	
		Machine Driver
	
	***************************************************************************/
	
	static struct YM2151interface ym2151_interface =
	{
		1,			/* 1 chip */
		3579545,	/* 3.579545 MHz */
		{ YM3012_VOL(70,MIXER_PAN_CENTER,0,MIXER_PAN_CENTER) },	/* only left channel is connected */
		{ 0 }
	};
	
	static struct K053260_interface k053260_interface =
	{
		1,
		{ 3579545 },
		{ REGION_SOUND1 }, /* memory region */
		{ { MIXER(75,MIXER_PAN_LEFT), MIXER(75,MIXER_PAN_RIGHT) } },
	//	{ nmi_callback }
	};
	
	static INTERRUPT_GEN( simpsons_irq )
	{
		if (cpu_getiloops() == 0)
		{
			if (simpsons_firq_enabled && K053246_is_IRQ_enabled())
				cpu_set_irq_line(0, KONAMI_FIRQ_LINE, HOLD_LINE);
		}
		else
		{
			if (K052109_is_IRQ_enabled())
				cpu_set_irq_line(0, KONAMI_IRQ_LINE, HOLD_LINE);
		}
	}
	
	static MACHINE_DRIVER_START( simpsons )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(KONAMI, 3000000) /* ? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(simpsons_irq,2)	/* IRQ triggered by the 052109, FIRQ by the sprite hardware */
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(z80_readmem,z80_writemem)
									/* NMIs are generated by the 053260 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(simpsons)
		MDRV_NVRAM_HANDLER(simpsons)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_HAS_SHADOWS)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(14*8, (64-14)*8-1, 2*8, 30*8-1 )
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(simpsons)
		MDRV_VIDEO_UPDATE(simpsons)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(K053260, k053260_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game ROMs
	
	***************************************************************************/
	
	static RomLoadPtr rom_simpsons = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x8a000, REGION_CPU1, 0 );/* code + banked roms + banked ram */
		ROM_LOAD( "g02.16c",      0x10000, 0x20000, 0x580ce1d6 );
		ROM_LOAD( "g01.17c",      0x30000, 0x20000, 0x9f843def );
		ROM_LOAD( "j13.13c",      0x50000, 0x20000, 0xaade2abd );
	    ROM_LOAD( "j12.15c",      0x70000, 0x18000, 0x479e12f2 );
		ROM_CONTINUE(		      0x08000, 0x08000 );
	
		ROM_REGION( 0x28000, REGION_CPU2, 0 );/* Z80 code + banks */
		ROM_LOAD( "e03.6g",       0x00000, 0x08000, 0x866b7a35 );
		ROM_CONTINUE(			  0x10000, 0x18000 );
	
		ROM_REGION( 0x100000, REGION_GFX1, 0 );/* graphics ( dont dispose as the program can read them, 0 ) */
		ROM_LOAD( "simp_18h.rom", 0x000000, 0x080000, 0xba1ec910 );/* tiles */
		ROM_LOAD( "simp_16h.rom", 0x080000, 0x080000, 0xcf2bbcab );
	
		ROM_REGION( 0x400000, REGION_GFX2, 0 );/* graphics ( dont dispose as the program can read them, 0 ) */
		ROM_LOAD( "simp_3n.rom",  0x000000, 0x100000, 0x7de500ad );/* sprites */
		ROM_LOAD( "simp_8n.rom",  0x100000, 0x100000, 0xaa085093 );
		ROM_LOAD( "simp_12n.rom", 0x200000, 0x100000, 0x577dbd53 );
		ROM_LOAD( "simp_16l.rom", 0x300000, 0x100000, 0x55fab05d );
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 );/* samples for the 053260 */
		ROM_LOAD( "simp_1f.rom", 0x000000, 0x100000, 0x1397a73b );
		ROM_LOAD( "simp_1d.rom", 0x100000, 0x040000, 0x78778013 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_simpsn2p = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x8a000, REGION_CPU1, 0 );/* code + banked roms + banked ram */
		ROM_LOAD( "g02.16c",      0x10000, 0x20000, 0x580ce1d6 );
		ROM_LOAD( "simp_p01.rom", 0x30000, 0x20000, 0x07ceeaea );
		ROM_LOAD( "simp_013.rom", 0x50000, 0x20000, 0x8781105a );
	    ROM_LOAD( "simp_012.rom", 0x70000, 0x18000, 0x244f9289 );
		ROM_CONTINUE(		      0x08000, 0x08000 );
	
		ROM_REGION( 0x28000, REGION_CPU2, 0 );/* Z80 code + banks */
		ROM_LOAD( "simp_g03.rom", 0x00000, 0x08000, 0x76c1850c );
		ROM_CONTINUE(			  0x10000, 0x18000 );
	
		ROM_REGION( 0x100000, REGION_GFX1, 0 );/* graphics ( dont dispose as the program can read them, 0 ) */
		ROM_LOAD( "simp_18h.rom", 0x000000, 0x080000, 0xba1ec910 );/* tiles */
		ROM_LOAD( "simp_16h.rom", 0x080000, 0x080000, 0xcf2bbcab );
	
		ROM_REGION( 0x400000, REGION_GFX2, 0 );/* graphics ( dont dispose as the program can read them, 0 ) */
		ROM_LOAD( "simp_3n.rom",  0x000000, 0x100000, 0x7de500ad );/* sprites */
		ROM_LOAD( "simp_8n.rom",  0x100000, 0x100000, 0xaa085093 );
		ROM_LOAD( "simp_12n.rom", 0x200000, 0x100000, 0x577dbd53 );
		ROM_LOAD( "simp_16l.rom", 0x300000, 0x100000, 0x55fab05d );
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 );/* samples for the 053260 */
		ROM_LOAD( "simp_1f.rom", 0x000000, 0x100000, 0x1397a73b );
		ROM_LOAD( "simp_1d.rom", 0x100000, 0x040000, 0x78778013 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_simps2pj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x8a000, REGION_CPU1, 0 );/* code + banked roms + banked ram */
		ROM_LOAD( "072-s02.16c",  0x10000, 0x20000, 0x265f7a47 );
		ROM_LOAD( "072-t01.17c",  0x30000, 0x20000, 0x91de5c2d );
		ROM_LOAD( "072-213.13c",  0x50000, 0x20000, 0xb326a9ae );
	    ROM_LOAD( "072-212.15c",  0x70000, 0x18000, 0x584d9d37 );
		ROM_CONTINUE(		      0x08000, 0x08000 );
	
		ROM_REGION( 0x28000, REGION_CPU2, 0 );/* Z80 code + banks */
		ROM_LOAD( "simp_g03.rom", 0x00000, 0x08000, 0x76c1850c );
		ROM_CONTINUE(			  0x10000, 0x18000 );
	
		ROM_REGION( 0x100000, REGION_GFX1, 0 );/* graphics ( dont dispose as the program can read them, 0 ) */
		ROM_LOAD( "simp_18h.rom", 0x000000, 0x080000, 0xba1ec910 );/* tiles */
		ROM_LOAD( "simp_16h.rom", 0x080000, 0x080000, 0xcf2bbcab );
	
		ROM_REGION( 0x400000, REGION_GFX2, 0 );/* graphics ( dont dispose as the program can read them, 0 ) */
		ROM_LOAD( "simp_3n.rom",  0x000000, 0x100000, 0x7de500ad );/* sprites */
		ROM_LOAD( "simp_8n.rom",  0x100000, 0x100000, 0xaa085093 );
		ROM_LOAD( "simp_12n.rom", 0x200000, 0x100000, 0x577dbd53 );
		ROM_LOAD( "simp_16l.rom", 0x300000, 0x100000, 0x55fab05d );
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 );/* samples for the 053260 */
		ROM_LOAD( "simp_1f.rom", 0x000000, 0x100000, 0x1397a73b );
		ROM_LOAD( "simp_1d.rom", 0x100000, 0x040000, 0x78778013 );
	ROM_END(); }}; 
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static DRIVER_INIT( simpsons )
	{
		konami_rom_deinterleave_2(REGION_GFX1);
		konami_rom_deinterleave_4(REGION_GFX2);
	}
	
	public static GameDriver driver_simpsons	   = new GameDriver("1991"	,"simpsons"	,"simpsons.java"	,rom_simpsons,null	,machine_driver_simpsons	,input_ports_simpsons	,init_simpsons	,ROT0	,	"Konami", "The Simpsons (4 Players)" )
	public static GameDriver driver_simpsn2p	   = new GameDriver("1991"	,"simpsn2p"	,"simpsons.java"	,rom_simpsn2p,driver_simpsons	,machine_driver_simpsons	,input_ports_simpsn2p	,init_simpsons	,ROT0	,	"Konami", "The Simpsons (2 Players)" )
	public static GameDriver driver_simps2pj	   = new GameDriver("1991"	,"simps2pj"	,"simpsons.java"	,rom_simps2pj,driver_simpsons	,machine_driver_simpsons	,input_ports_simpsn2p	,init_simpsons	,ROT0	,	"Konami", "The Simpsons (2 Players Japan)" )
}
