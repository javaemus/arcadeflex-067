/***************************************************************************

	Atari Cloak & Dagger hardware

	Games supported:
		* Cloak & Dagger

	Known issues:
		* none at this time

****************************************************************************

	Master processor

	IRQ: 4 IRQ's per frame at even intervals, 4th IRQ is at start of VBLANK

	000-3FF	   Working RAM
	400-7FF    Playfield RAM
	800-FFF    Communication RAM (shared with slave processor)

	1000-100F  Pokey 1
	1008 (R)   bit 7 = Start 2 players
	   	   bit 6 = Start 1 player

	1800-180F  Pokey 2
	1808(R)	   Dipswitches

	2000 (R):  Joysticks
	2200 (R):  Player 2 joysticks (for cocktail version)

	2400 (R)   bit 0: Vertical Blank
		   bit 1: Self test switch
		   bit 2: Left Coin
		   bit 3: Right Coin
		   bit 4: Cocktail mode
		   bit 5: Aux Coin
		   bit 6: Player 2 Igniter button
		   bit 7: Player 1 Igniter button

	2600 (W) Custom Write (this has something to do with positioning of the display out, I ignore it)

	2800-29FF: (R/W) non-volatile RAM
	3000-30FF: (R/W) Motion RAM
	3200-327F: (W) Color RAM, Address bit 6 becomes the 9th bit of color RAM

	3800: (W) Right Coin Counter
	3801: (W) Left Coint Counter
	3803: (W) Cocktail Output
	3806: (W) Start 2 LED
	3807: (W) Start 1 LED

	3A00: (W) Watchdog reset
	3C00: (W) Reset IRQ
	3E00: (W) bit 0: Enable NVRAM

	4000 - FFFF ROM
		4000-5FFF  136023.501
		6000-7FFF  136023.502
		8000-BFFF  136023.503
		C000-FFFF  136023.504


	Slave processor

	IRQ: 1 IRQ per frame at start of VBLANK

	0000-0007: Working RAM
	0008-000A, 000C-000E: (R/W) bit 0,1,2: Store to/Read From Bit Map RAM

	0008: Decrement X/Increment Y
	0009: Decrement Y
	000A: Decrement X
	000B: Set bitmap X coordinate
	000C: Increment X/Increment Y  <-- Yes this is correct
	000D: Increment Y
	000E: Increment X
	000F: Set bitmap Y coordinate

	0010-07FF: Working RAM
	0800-0FFF: Communication RAM (shared with master processor)

	1000 (W): Reset IRQ
	1200 (W):  bit 0: Swap bit maps
		   bit 1: Clear bit map
	1400 (W): Custom Write (this has something to do with positioning of the display out, I ignore it)

	2000-FFFF: Program ROM
		2000-3FFF: 136023.509
		4000-5FFF: 136023.510
		6000-7FFF: 136023.511
		8000-9FFF: 136023.512
		A000-BFFF: 136023.513
		C000-DFFF: 136023.514
		E000-EFFF: 136023.515


	Motion object ROM: 136023.307,136023.308
	Playfield ROM: 136023.306,136023.305

****************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class cloak
{
	
	static unsigned char *enable_nvRAM;
	static unsigned char *cloak_sharedram;
	
	
	
	/*************************************
	 *
	 *	Shared RAM I/O
	 *
	 *************************************/
	
	public static ReadHandlerPtr cloak_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cloak_sharedram[offset];
	} };
	
	
	public static WriteHandlerPtr cloak_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cloak_sharedram[offset] = data;
	} };
	
	
	
	/*************************************
	 *
	 *	Output ports
	 *
	 *************************************/
	
	public static WriteHandlerPtr cloak_led_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		set_led_status(1 - offset,~data & 0x80);
	} };
	
	public static WriteHandlerPtr cloak_coin_counter_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		set_led_status(offset,data);
	} };
	
	
	
	/*************************************
	 *
	 *	Main CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0800, 0x0fff, cloak_sharedram_r ),
		new Memory_ReadAddress( 0x2800, 0x29ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1000, 0x100f, pokey1_r ),		/* DSW0 also */
		new Memory_ReadAddress( 0x1800, 0x180f, pokey2_r ),		/* DSW1 also */
		new Memory_ReadAddress( 0x2000, 0x2000, input_port_0_r ),	/* IN0 */
		new Memory_ReadAddress( 0x2200, 0x2200, input_port_1_r ),	/* IN1 */
		new Memory_ReadAddress( 0x2400, 0x2400, input_port_2_r ),	/* IN2 */
		new Memory_ReadAddress( 0x3000, 0x30ff, MRA_RAM ),
		new Memory_ReadAddress( 0x3800, 0x3807, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x07ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x0800, 0x0fff, cloak_sharedram_w, cloak_sharedram ),
		new Memory_WriteAddress( 0x1000, 0x100f, pokey1_w ),
		new Memory_WriteAddress( 0x1800, 0x180f, pokey2_w ),
		new Memory_WriteAddress( 0x2800, 0x29ff, MWA_RAM, generic_nvram, generic_nvram_size ),
		new Memory_WriteAddress( 0x3000, 0x30ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x3200, 0x327f, cloak_paletteram_w ),
		new Memory_WriteAddress( 0x3800, 0x3801, cloak_coin_counter_w ),
		new Memory_WriteAddress( 0x3802, 0x3805, MWA_RAM ),
		new Memory_WriteAddress( 0x3806, 0x3807, cloak_led_w ),
		new Memory_WriteAddress( 0x3a00, 0x3a00, watchdog_reset_w ),
		new Memory_WriteAddress( 0x3e00, 0x3e00, MWA_RAM, enable_nvRAM ),
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Slave CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress readmem2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0007, MRA_RAM ),
		new Memory_ReadAddress( 0x0008, 0x000f, graph_processor_r ),
		new Memory_ReadAddress( 0x0010, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0800, 0x0fff, cloak_sharedram_r ),
		new Memory_ReadAddress( 0x2000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress writemem2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0007, MWA_RAM ),
		new Memory_WriteAddress( 0x0008, 0x000f, graph_processor_w ),
		new Memory_WriteAddress( 0x0010, 0x07ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0800, 0x0fff, cloak_sharedram_w ),
		new Memory_WriteAddress( 0x1200, 0x1200, cloak_clearbmp_w ),
		new Memory_WriteAddress( 0x2000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	static InputPortPtr input_ports_cloak = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP     | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP     | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_VBLANK );
		PORT_SERVICE( 0x02, IP_ACTIVE_LOW );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
		PORT_START(); 	/* DSW0 */
		PORT_BIT( 0x3f, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1 );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x02, "Credits" );
		PORT_DIPSETTING(    0x02, "*1" );
		PORT_DIPSETTING(    0x01, "*2" );
		PORT_DIPSETTING(    0x03, "/2" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Allow Freeze" );/* when active, press button 1 to freeze */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Graphics definitions
	 *
	 *************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8, 
		256,
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0x1000*8+0, 0x1000*8+4, 0, 4, 0x1000*8+8, 0x1000*8+12, 8, 12 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8
	);
	
	
	static GfxLayout spritelayout = new GfxLayout
	(
		8,16,
		128,
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0x1000*8+0, 0x1000*8+4, 0, 4, 0x1000*8+8, 0x1000*8+12, 8, 12 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
			8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		16*16
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0,  1 ),
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout,  32,  1 ),
		new GfxDecodeInfo( -1 )
	};
	
	
	
	/*************************************
	 *
	 *	Sound interfaces
	 *
	 *************************************/
	
	static POKEYinterface pokey_interface = new POKEYinterface
	(
		2,	/* 2 chips */
		1500000,	/* 1.5 MHz??? */
		new int[] { 50, 50 },
		/* The 8 pot handlers */
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		/* The allpot handler */
		new ReadHandlerPtr[] { input_port_3_r, input_port_4_r }
	);
	
	
	
	/*************************************
	 *
	 *	Machine driver
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( cloak )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6502,1000000)		/* 1 MHz ???? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,4)
	
		MDRV_CPU_ADD(M6502,1250000)		/* 1.25 MHz ???? */
		MDRV_CPU_MEMORY(readmem2,writemem2)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(5)
		
		MDRV_NVRAM_HANDLER(generic_0fill)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 3*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(64)
	
		MDRV_VIDEO_START(cloak)
		MDRV_VIDEO_UPDATE(cloak)
	
		/* sound hardware */
		MDRV_SOUND_ADD(POKEY, pokey_interface)
	MACHINE_DRIVER_END
	
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadPtr rom_cloak = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "136023.501",   0x4000, 0x2000, 0xc2dbef1b );
		ROM_LOAD( "136023.502",   0x6000, 0x2000, 0x316d0c7b );
		ROM_LOAD( "136023.503",   0x8000, 0x4000, 0xb9c291a6 );
		ROM_LOAD( "136023.504",   0xc000, 0x4000, 0xd014a1c0 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for code */
		ROM_LOAD( "136023.509",   0x2000, 0x2000, 0x46c021a4 );
		ROM_LOAD( "136023.510",   0x4000, 0x2000, 0x8c9cf017 );
		ROM_LOAD( "136023.511",   0x6000, 0x2000, 0x66fd8a34 );
		ROM_LOAD( "136023.512",   0x8000, 0x2000, 0x48c8079e );
		ROM_LOAD( "136023.513",   0xa000, 0x2000, 0x13f1cbab );
		ROM_LOAD( "136023.514",   0xc000, 0x2000, 0x6f8c7991 );
		ROM_LOAD( "136023.515",   0xe000, 0x2000, 0x835438a0 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "136023.305",   0x0000, 0x1000, 0xee443909 );
		ROM_LOAD( "136023.306",   0x1000, 0x1000, 0xd708b132 );
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "136023.307",   0x0000, 0x1000, 0xc42c84a4 );
		ROM_LOAD( "136023.308",   0x1000, 0x1000, 0x4fe13d58 );
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_cloak	   = new GameDriver("1983"	,"cloak"	,"cloak.java"	,rom_cloak,null	,machine_driver_cloak	,input_ports_cloak	,null	,ROT0	,	"Atari", "Cloak & Dagger", GAME_NO_COCKTAIL )
}
