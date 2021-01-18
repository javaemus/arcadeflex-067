/***************************************************************************

Tank Busters memory map

driver by Jaroslaw Burczynski


Note:
	To enter the test mode:
	reset the game and keep start1 and start2 buttons pressed.

To do:
	- verify colors: prom to output mapping is unknown, resistor values are guess
	- remove the 'some_changing_input' hack (see below)
	- from time to time the game just hangs

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class tankbust
{
	
	
	VIDEO_START( tankbust );
	VIDEO_UPDATE( tankbust );
	
	extern data8_t * txt_ram;
	
	READ_HANDLER ( tankbust_background_videoram_r );
	READ_HANDLER ( tankbust_background_colorram_r );
	READ_HANDLER ( tankbust_txtram_r );
	
	
	
	//port A of ay8910#0
	static int latch;
	
	static void soundlatch_callback (int data)
	{
		latch = data;
	}
	
	public static WriteHandlerPtr tankbust_soundlatch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		timer_set(TIME_NOW,data,soundlatch_callback);
	} };
	
	public static ReadHandlerPtr tankbust_soundlatch_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return latch;
	} };
	
	//port B of ay8910#0
	static unsigned int timer1=0;
	public static ReadHandlerPtr tankbust_soundtimer_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int ret;
	
		timer1++;
		ret = timer1;
		return ret;
	} };
	
	static void soundirqline_callback (int param)
	{
	//logerror("sound_irq_line write = %2x (after CPUs synced) \n",param);
	
			if ((param&1) == 0)
				cpu_set_irq_line(1, 0, HOLD_LINE);
	}
	
	
	static int e0xx_data[8] = { 0,0,0,0, 0,0,0,0 };
	
	public static WriteHandlerPtr tankbust_e0xx_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		e0xx_data[offset] = data;
	
	#if 0
		usrintf_showmessage("e0: %x %x (%x cnt) %x %x %x %x",
			e0xx_data[0],e0xx_data[1],
			e0xx_data[2],e0xx_data[3],
			e0xx_data[4],e0xx_data[5],
			e0xx_data[6] );
	#endif
	
		switch (offset)
		{
		case 0:	/* 0xe000 interrupt enable */
			interrupt_enable_w(0,data);
		break;
	
		case 1:	/* 0xe001 (value 0 then 1) written right after the soundlatch_w */
			timer_set(TIME_NOW,data,soundirqline_callback);
		break;
	
		case 2:	/* 0xe002 coin counter */
			coin_counter_w(0, data&1);
		break;
	
		case 6:	/* 0xe006 screen disable ?? or disable screen update */
			/* program sets this to 0,
			   clears screen memory,
			   and sets this to 1 */
	
			/* ???? */
		break;
	
		case 7: /* 0xe007 bankswitch */
			/* bank 1 at 0x6000-9fff = from 0x10000 when bit0=0 else from 0x14000 */
			/* bank 2 at 0xa000-bfff = from 0x18000 when bit0=0 else from 0x1a000 */
			cpu_setbank( 1, memory_region(REGION_CPU1) + 0x10000 + ((data&1) * 0x4000) );
			cpu_setbank( 2, memory_region(REGION_CPU1) + 0x18000 + ((data&1) * 0x2000) ); /* verified (the game will reset after the "game over" otherwise) */
		break;
		}
	} };
	
	public static ReadHandlerPtr debug_output_area_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return e0xx_data[offset];
	} };
	
	
	
	
	PALETTE_INIT( tankbust )
	{
		int i;
	
		for (i = 0; i < 128; i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
	//7 6   5 4 3   2 1 0
	//bb    r r r   g g g - bad (for sure - no green for tank)
	//bb    g g g   r r r - bad (for sure - no yellow, no red)
	//gg    r r r   b b b - bad
	//gg    b b b   r r r - bad
	//rr    b b b   g g g - bad
	
	//rr    g g g   b b b - very close (green,yellow,red present)
	
	//rr    r g g   g b b - bad
	//rr    r g g   b b b - bad
	//rr    g g g   b b r - bad
	
	//rr    g g b   b x x - bad (x: unused)
	//rr    g g x   x b b - bad but still close
	//rr    g g r   g b b - bad but still close
	//rr    g g g   r b b - bad but still close
	
	
	#if 1 //close one
			/* blue component */
			bit0 = (color_prom.read(i)>> 0) & 0x01;
			bit1 = (color_prom.read(i)>> 1) & 0x01;
			bit2 = (color_prom.read(i)>> 2) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			/* green component */
			bit0 = (color_prom.read(i)>> 3) & 0x01;
			bit1 = (color_prom.read(i)>> 4) & 0x01;
			bit2 = (color_prom.read(i)>> 5) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			/* red component */
			bit0 = (color_prom.read(i)>> 6) & 0x01;
			bit1 = (color_prom.read(i)>> 7) & 0x01;
			r = 0x55 * bit0 + 0xaa * bit1;
	#endif
	
			palette_set_color(i,r,g,b);
		}
	}
	
	#if 0
	public static ReadHandlerPtr read_from_unmapped_memory  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0xff;
	} };
	#endif
	
	static int variable_data=0x11;
	public static ReadHandlerPtr some_changing_input  = new ReadHandlerPtr() { public int handler(int offset)
	{
		variable_data = (variable_data+8) & 0xff;
		return variable_data;
	} };
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x9fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xa000, 0xbfff, MRA_BANK2 ),
	
		new Memory_ReadAddress( 0xe000, 0xe007, debug_output_area_r ),
	
		new Memory_ReadAddress( 0xf000, 0xf7ff, MRA_RAM ),
	
	//new Memory_ReadAddress( 0xf800, 0xffff, read_from_unmapped_memory ),	/* a bug in game code ? */
	
		new Memory_ReadAddress( 0xe800, 0xe800, input_port_0_r ),
		new Memory_ReadAddress( 0xe801, 0xe801, input_port_1_r ),
		new Memory_ReadAddress( 0xe802, 0xe802, input_port_2_r ),
		new Memory_ReadAddress( 0xe803, 0xe803, some_changing_input ),/*unknown. Game expects this to change so this is not player input */
	
		new Memory_ReadAddress( 0xc000, 0xc7ff, tankbust_background_videoram_r ),
		new Memory_ReadAddress( 0xc800, 0xcfff, tankbust_background_colorram_r ),
		new Memory_ReadAddress( 0xd000, 0xd7ff, tankbust_txtram_r ),
		new Memory_ReadAddress( 0xd800, 0xd8ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x9fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa000, 0xbfff, MWA_ROM ),
	
		new Memory_WriteAddress( 0xf000, 0xf7ff, MWA_RAM ),
	
		new Memory_WriteAddress( 0xe000, 0xe007, tankbust_e0xx_w ),
	
		new Memory_WriteAddress( 0xe800, 0xe800, tankbust_yscroll_w ),
		new Memory_WriteAddress( 0xe801, 0xe802, tankbust_xscroll_w ),
		new Memory_WriteAddress( 0xe803, 0xe803, tankbust_soundlatch_w ),
		new Memory_WriteAddress( 0xe804, 0xe804, MWA_NOP ),	/* watchdog ? ; written in long-lasting loops */
	
		new Memory_WriteAddress( 0xc000, 0xc7ff, tankbust_background_videoram_w, videoram ),
		new Memory_WriteAddress( 0xc800, 0xcfff, tankbust_background_colorram_w, colorram ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, tankbust_txtram_w, txt_ram ),
		new Memory_WriteAddress( 0xd800, 0xd8ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport2[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0xc0, 0xc0, AY8910_read_port_0_r ),
		new IO_ReadPort( 0x30, 0x30, AY8910_read_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport2[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0xc0, 0xc0, AY8910_control_port_0_w ),
		new IO_WritePort( 0x40, 0x40, AY8910_write_port_0_w ),
		new IO_WritePort( 0x30, 0x30, AY8910_control_port_1_w ),
		new IO_WritePort( 0x10, 0x10, AY8910_write_port_1_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress readmem2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
	
		new Memory_WriteAddress( 0x2000, 0x3fff, MWA_NOP ),	/* garbage, written in initialization loop */
	//0x4000 and 0x4040-0x4045 seem to be used (referenced in the code)
		new Memory_WriteAddress( 0x4000, 0x7fff, MWA_NOP ),	/* garbage, written in initialization loop */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	
	
	static InputPortPtr input_ports_tankbust = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(	0x03, "Easy" );
		PORT_DIPSETTING(	0x02, "Hard" );
		PORT_DIPSETTING(	0x01, "Normal" );
		PORT_DIPSETTING(	0x00, "Very Hard" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Language" );
		PORT_DIPSETTING(	0x08, "English" );
		PORT_DIPSETTING(	0x00, "French" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x10, "No Bonus" );
		PORT_DIPSETTING(	0x00, "60000" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x20, "1C/1C 1C/2C 1C/6C 1C/14C" );
		PORT_DIPSETTING(	0x00, "2C/1C 1C/1C 1C/3C 1C/7C" );
		PORT_DIPNAME( 0xc0, 0x40, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0xc0, "1" );
		PORT_DIPSETTING(	0x80, "2" );
		PORT_DIPSETTING(	0x40, "3" );
		PORT_DIPSETTING(	0x00, "4" );
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout spritelayout = new GfxLayout
	(
		32,32,	/* 32*32 pixels */
		64,		/* 64 sprites */
		4,		/* 4 bits per pixel */
		new int[] { 0, 8192*8*1, 8192*8*2, 8192*8*3 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
			8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7,
			32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
			40*8+0, 40*8+1, 40*8+2, 40*8+3, 40*8+4, 40*8+5, 40*8+6, 40*8+7 },
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8,
			23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
			71*8, 70*8, 69*8, 68*8, 67*8, 66*8, 65*8, 64*8,
			87*8, 86*8, 85*8, 84*8, 83*8, 82*8, 81*8, 80*8 },
		128*8	/* every sprite takes 128 consecutive bytes */
	);
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 pixels */
		2048,	/* 2048 characters */
		3,		/* 3 bits per pixel */
		new int[] { 0, 16384*8*1, 16384*8*2 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		8*8		/* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout charlayout2 = new GfxLayout
	(
		8,8,	/* 8*8 pixels */
		256,	/* 256 characters */
		1,		/* 1 bit per pixel - the data repeats 4 times within one ROM */
		new int[] { 0 }, /* , 2048*8*1, 2048*8*2, 2048*8*3 ),*/
		{ 0, 1, 2, 3, 4, 5, 6, 7 },
		{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8		/* every char takes 8 consecutive bytes */
	};
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, spritelayout,	0x00, 2 ),	/* sprites 32x32  (2 * 16 colors) */
		new GfxDecodeInfo( REGION_GFX2, 0, charlayout,		0x20, 8 ),	/* bg tilemap characters */
		new GfxDecodeInfo( REGION_GFX3, 0, charlayout2,		0x60, 16  ),	/* txt tilemap characters*/
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,			/* 2 chips */
		2000000,	/* 2.0 MHz ??? */
		new int[] { 10,10 },
		new ReadHandlerPtr[] { tankbust_soundlatch_r, 0 },
		new ReadHandlerPtr[] { tankbust_soundtimer_r, 0 },
		new WriteHandlerPtr[] { 0, 0 },
		new WriteHandlerPtr[] { 0, 0 }
	);
	
	static MACHINE_DRIVER_START( tankbust )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)		/* 4 MHz ? */
		MDRV_CPU_MEMORY( readmem, writemem )
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 4000000)		/* 3.072 MHz ? */
		MDRV_CPU_MEMORY( readmem2, writemem2 )
		MDRV_CPU_PORTS( readport2, writeport2 )
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_INTERLEAVE(100)
	
	//MDRV_MACHINE_INIT( ... )
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES( VIDEO_TYPE_RASTER )
		MDRV_SCREEN_SIZE   ( 64*8, 32*8 )
		MDRV_VISIBLE_AREA  ( 16*8, 56*8-1, 1*8, 31*8-1 )
	//	MDRV_VISIBLE_AREA  (  0*8, 64*8-1, 1*8, 31*8-1 )
		MDRV_GFXDECODE( gfxdecodeinfo )
	
		MDRV_PALETTE_LENGTH( 128 )
		MDRV_PALETTE_INIT  ( tankbust )
	
		MDRV_VIDEO_START   ( tankbust )
		MDRV_VIDEO_UPDATE  ( tankbust )
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_tankbust = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x1c000, REGION_CPU1, 0 );
		ROM_LOAD( "a-s4-6.bin",		0x00000, 0x4000, 0x8ebe7317 );
		ROM_LOAD( "a-s7-9.bin",		0x04000, 0x2000, 0x047aee33 );
	
		ROM_LOAD( "a-s5_7.bin",		0x12000, 0x2000, 0xdd4800ca );/* banked at 0x6000-0x9fff */
		ROM_CONTINUE(                   0x10000, 0x2000);
	
		ROM_LOAD( "a-s6-8.bin",		0x16000, 0x2000, 0xf8801238 );/* banked at 0x6000-0x9fff */
		ROM_CONTINUE(                   0x14000, 0x2000);
	
	//	ROM_LOAD( "a-s5_7.bin",		0x10000, 0x4000, 0xdd4800ca );/* banked at 0x6000-0x9fff */
	//	ROM_LOAD( "a-s6-8.bin",		0x14000, 0x4000, 0xf8801238 );/* banked at 0x6000-0x9fff */
	
		ROM_LOAD( "a-s8-10.bin",	0x18000, 0x4000, 0x9e826faa );/* banked at 0xa000-0xbfff */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "a-b3-1.bin",		0x0000, 0x2000, 0xb0f56102 );
	
		ROM_REGION( 0x8000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "a-d5-2.bin",		0x0000, 0x2000, 0x0bbf3fdb );/* sprites 32x32 */
		ROM_LOAD( "a-d6-3.bin",		0x2000, 0x2000, 0x4398dc21 );
		ROM_LOAD( "a-d7-4.bin",		0x4000, 0x2000, 0xaca197fc );
		ROM_LOAD( "a-d8-5.bin",		0x6000, 0x2000, 0x1e6edc17 );
	
		ROM_REGION( 0xc000, REGION_GFX2, ROMREGION_DISPOSE | ROMREGION_INVERT );
		ROM_LOAD( "b-m4-11.bin",	0x0000, 0x4000, 0xeb88ee1f );/* background tilemap characters 8x8 */
		ROM_LOAD( "b-m5-12.bin",	0x4000, 0x4000, 0x4c65f399 );
		ROM_LOAD( "b-m6-13.bin",	0x8000, 0x4000, 0xa5baa413 );
	
		ROM_REGION( 0x2000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "b-r3-14.bin",	0x0000, 0x2000, 0x4310a815 );/* text tilemap characters 8x8 */
	
		ROM_REGION( 0x0080, REGION_PROMS, 0 );
		ROM_LOAD( "tb-prom.1s8",	0x0000, 0x0020, 0xdfaa086c );//sprites
		ROM_LOAD( "tb-prom.2r8",	0x0020, 0x0020, 0xec50d674 );//background
		ROM_LOAD( "tb-prom.3p8",	0x0040, 0x0020, 0x3e70eafd );//background palette 2 ??
		ROM_LOAD( "tb-prom.4k8",	0x0060, 0x0020, 0x624f40d2 );//text
	ROM_END(); }}; 
	
	
	public static GameDriver driver_tankbust	   = new GameDriver("1985"	,"tankbust"	,"tankbust.java"	,rom_tankbust,null	,machine_driver_tankbust	,input_ports_tankbust	,null	,ROT90	,	"Valadon Automation", "Tank Busters" )
}
