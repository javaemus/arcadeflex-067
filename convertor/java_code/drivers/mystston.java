/***************************************************************************

Mysterious Stones

driver by Nicola Salmoria


Known problems:

- Some dipswitches may not be mapped correctly.

Notes:
- The subtitle of the two sets is slightly different:
  "dr john s adventure" vs. "dr kick in adventure".
  The Dr John's is a bug fix. See the routine at 4376/4384 for example. The
  old set thrashes the Y register, the new one saves in on the stack. The
  newer set also resets the sound chips more often.

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class mystston
{
	
	
	
	extern unsigned char *mystston_fgvideoram;
	extern unsigned char *mystston_bgvideoram;
	extern unsigned char *mystston_scroll;
	
	VIDEO_START( mystston );
	PALETTE_INIT( mystston );
	VIDEO_UPDATE( mystston );
	
	
	
	static INTERRUPT_GEN( mystston_interrupt )
	{
		static int coin;
	
	
		if ((readinputport(0) & 0xc0) != 0xc0)
		{
			if (coin == 0)
			{
				coin = 1;
				nmi_line_pulse();
				return;
			}
		}
		else coin = 0;
	
		cpu_set_irq_line(0, 0, HOLD_LINE);
	}
	
	
	static int soundlatch;
	
	public static WriteHandlerPtr mystston_soundlatch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch = data;
	} };
	
	public static WriteHandlerPtr mystston_soundcontrol_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		static int last;
	
	
		/* bit 5 goes to 8910 #0 BDIR pin  */
		if ((last & 0x20) == 0x20 && (data & 0x20) == 0x00)
		{
			/* bit 4 goes to the 8910 #0 BC1 pin */
			if (last & 0x10)
				AY8910_control_port_0_w(0,soundlatch);
			else
				AY8910_write_port_0_w(0,soundlatch);
		}
		/* bit 7 goes to 8910 #1 BDIR pin  */
		if ((last & 0x80) == 0x80 && (data & 0x80) == 0x00)
		{
			/* bit 6 goes to the 8910 #1 BC1 pin */
			if (last & 0x40)
				AY8910_control_port_1_w(0,soundlatch);
			else
				AY8910_write_port_1_w(0,soundlatch);
		}
	
		last = data;
	} };
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x077f, MRA_RAM ),
		new Memory_ReadAddress( 0x0800, 0x0fff, MRA_RAM ),	/* work RAM? */
		new Memory_ReadAddress( 0x1000, 0x1fff, MRA_RAM ),
		new Memory_ReadAddress( 0x2000, 0x2000, input_port_0_r ),
		new Memory_ReadAddress( 0x2010, 0x2010, input_port_1_r ),
		new Memory_ReadAddress( 0x2020, 0x2020, input_port_2_r ),
		new Memory_ReadAddress( 0x2030, 0x2030, input_port_3_r ),
		new Memory_ReadAddress( 0x4000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x077f, MWA_RAM ),
		new Memory_WriteAddress( 0x0780, 0x07df, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x0800, 0x0fff, MWA_RAM ),	/* work RAM? */
		new Memory_WriteAddress( 0x1000, 0x17ff, mystston_fgvideoram_w, mystston_fgvideoram ),
		new Memory_WriteAddress( 0x1800, 0x1bff, mystston_bgvideoram_w, mystston_bgvideoram ),
		new Memory_WriteAddress( 0x1c00, 0x1fff, MWA_RAM ),	/* work RAM? This gets copied to videoram */
		new Memory_WriteAddress( 0x2000, 0x2000, mystston_2000_w ),	/* text color, flip screen  coin counters */
		new Memory_WriteAddress( 0x2010, 0x2010, watchdog_reset_w ),	/* or IRQ acknowledge maybe? */
		new Memory_WriteAddress( 0x2020, 0x2020, mystston_scroll_w ),
		new Memory_WriteAddress( 0x2030, 0x2030, mystston_soundlatch_w ),
		new Memory_WriteAddress( 0x2040, 0x2040, mystston_soundcontrol_w ),
		new Memory_WriteAddress( 0x2060, 0x2077, paletteram_BBGGGRRR_w, paletteram ),
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	
	static InputPortPtr input_ports_mystston = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT_IMPULSE( 0x40, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN2, 1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME(0x01, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(   0x01, "3" );
		PORT_DIPSETTING(   0x00, "5" );
		PORT_DIPNAME(0x02, 0x02, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(   0x02, "Easy" );
		PORT_DIPSETTING(   0x00, "Hard" );
		PORT_DIPNAME(0x04, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(   0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
		PORT_DIPNAME(0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
		PORT_DIPNAME(0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
		PORT_DIPNAME(0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
		PORT_DIPNAME(0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
		PORT_DIPNAME(0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW2 */
		PORT_DIPNAME(0x03, 0x03, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(   0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(   0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(   0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(   0x01, DEF_STR( "1C_3C") );
		PORT_DIPNAME(0x0c, 0x0c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(   0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(   0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(   0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(   0x04, DEF_STR( "1C_3C") );
		PORT_DIPNAME(0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
		PORT_DIPNAME(0x20, 0x20, DEF_STR( "Unknown") );	// flip screen according to manual? doesn't seem to work
		PORT_DIPSETTING(   0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
		PORT_DIPNAME(0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(   0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(   0x40, DEF_STR( "Cocktail") );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_VBLANK );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3), RGN_FRAC(1,3), RGN_FRAC(0,3) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3), RGN_FRAC(1,3), RGN_FRAC(0,3) },
		new int[] { 16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7,
				0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,   3*8, 4 ),
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout, 2*8, 1 ),
		new GfxDecodeInfo( REGION_GFX1, 0, spritelayout, 0*8, 2 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,      /* 2 chips */
		1500000,        /* 1.5 MHz ? */
		new int[] { 30, 30 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	
	static MACHINE_DRIVER_START( mystston )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6502, 1500000)	/* 1.5 MHz ???? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(mystston_interrupt,16)	/* ? controls music tempo */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(24+32)
	
		MDRV_PALETTE_INIT(mystston)
		MDRV_VIDEO_START(mystston)
		MDRV_VIDEO_UPDATE(mystston)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	
	
	
	/***************************************************************************
	
	  Mysterious Stones driver
	
	***************************************************************************/
	
	static RomLoadPtr rom_mystston = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "rom6.bin",     0x4000, 0x2000, 0x7bd9c6cd );
		ROM_LOAD( "rom5.bin",     0x6000, 0x2000, 0xa83f04a6 );
		ROM_LOAD( "rom4.bin",     0x8000, 0x2000, 0x46c73714 );
		ROM_LOAD( "rom3.bin",     0xa000, 0x2000, 0x34f8b8a3 );
		ROM_LOAD( "rom2.bin",     0xc000, 0x2000, 0xbfd22cfc );
		ROM_LOAD( "rom1.bin",     0xe000, 0x2000, 0xfb163e38 );
	
		ROM_REGION( 0x0c000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ms6",          0x00000, 0x2000, 0x85c83806 );
		ROM_LOAD( "ms9",          0x02000, 0x2000, 0xb146c6ab );
		ROM_LOAD( "ms7",          0x04000, 0x2000, 0xd025f84d );
		ROM_LOAD( "ms10",         0x06000, 0x2000, 0xd85015b5 );
		ROM_LOAD( "ms8",          0x08000, 0x2000, 0x53765d89 );
		ROM_LOAD( "ms11",         0x0a000, 0x2000, 0x919ee527 );
	
		ROM_REGION( 0x0c000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "ms12",         0x00000, 0x2000, 0x72d8331d );
		ROM_LOAD( "ms13",         0x02000, 0x2000, 0x845a1f9b );
		ROM_LOAD( "ms14",         0x04000, 0x2000, 0x822874b0 );
		ROM_LOAD( "ms15",         0x06000, 0x2000, 0x4594e53c );
		ROM_LOAD( "ms16",         0x08000, 0x2000, 0x2f470b0f );
		ROM_LOAD( "ms17",         0x0a000, 0x2000, 0x38966d1b );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "ic61",         0x0000, 0x0020, 0xe802d6cf );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_myststno = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "ms0",          0x4000, 0x2000, 0x6dacc05f );
		ROM_LOAD( "ms1",          0x6000, 0x2000, 0xa3546df7 );
		ROM_LOAD( "ms2",          0x8000, 0x2000, 0x43bc6182 );
		ROM_LOAD( "ms3",          0xa000, 0x2000, 0x9322222b );
		ROM_LOAD( "ms4",          0xc000, 0x2000, 0x47cefe9b );
		ROM_LOAD( "ms5",          0xe000, 0x2000, 0xb37ae12b );
	
		ROM_REGION( 0x0c000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ms6",          0x00000, 0x2000, 0x85c83806 );
		ROM_LOAD( "ms9",          0x02000, 0x2000, 0xb146c6ab );
		ROM_LOAD( "ms7",          0x04000, 0x2000, 0xd025f84d );
		ROM_LOAD( "ms10",         0x06000, 0x2000, 0xd85015b5 );
		ROM_LOAD( "ms8",          0x08000, 0x2000, 0x53765d89 );
		ROM_LOAD( "ms11",         0x0a000, 0x2000, 0x919ee527 );
	
		ROM_REGION( 0x0c000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "ms12",         0x00000, 0x2000, 0x72d8331d );
		ROM_LOAD( "ms13",         0x02000, 0x2000, 0x845a1f9b );
		ROM_LOAD( "ms14",         0x04000, 0x2000, 0x822874b0 );
		ROM_LOAD( "ms15",         0x06000, 0x2000, 0x4594e53c );
		ROM_LOAD( "ms16",         0x08000, 0x2000, 0x2f470b0f );
		ROM_LOAD( "ms17",         0x0a000, 0x2000, 0x38966d1b );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "ic61",         0x0000, 0x0020, 0xe802d6cf );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_mystston	   = new GameDriver("1984"	,"mystston"	,"mystston.java"	,rom_mystston,null	,machine_driver_mystston	,input_ports_mystston	,null	,ROT270	,	"Technos", "Mysterious Stones (set 1)" )
	public static GameDriver driver_myststno	   = new GameDriver("1984"	,"myststno"	,"mystston.java"	,rom_myststno,driver_mystston	,machine_driver_mystston	,input_ports_mystston	,null	,ROT270	,	"Technos", "Mysterious Stones (set 2)" )
}
