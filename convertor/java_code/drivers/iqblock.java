/***************************************************************************

IQ Block   (c) 1992 IGS

Driver by Nicola Salmoria and Ernesto Corvi

TODO:
- Who generates IRQ and NMI? How many should there be per frame?

- Sound chip is a UM3567. Is this compatible to something already in MAME? yes, YM2413

- Coin 2 doesn't work? DIP switch setting?

- Protection:
  I can see it reading things like the R register here and there, so it might
  be cycle-dependant or something.

  'Crash 1' checks I was able to see:
  PC = $52FA
  PC = $507F

  'Crash 2' checks I was able to see:
  PC = $54E6

Stephh's notes :

  - Coin 2 as well as buttons 2 to 4 for each player are only read in "test mode".
    Same issue for Dip Siwtches 0-7 and 1-2 to 1-6.
    Some other games on the same hardware might use them.
  - Dip Switch 0 is stored at 0xf0ac and Dip Switch 1 is stored at 0xf0ad.
    However they are both read back at the same time with "ld   hl,($F0AC)" instructions.
  - Dip Switches 0-0 and 0-1 are read via code at 0x9470.
    This routine is called when you made a "line" after the routine that checks the score
    for awarding extra help and/or changing background.
    Data is coming from 4 possible tables (depending on them) which seem to be 0x84 bytes wide.
    Table 0 offset is 0xeaf7.
    IMO, this has something to do with difficulty but there is no confirmation about that !
  - Dip Switch 1-0 is read only once after the P.O.S.T. via code at 0xa200.
    It changes (or not) the contents of 0xf0db.w which can get these 2 possible values
    at start : 0x47a3 (when OFF) or 0x428e (when ON) which seem to be tables.
    If you set a WP to 0xf0db, you'll notice that it's called more often in the "demo mode"
    when the Dip Switch is ON, so, as it implies writes to outport 0x50b0, I think it has
    something to do with "Demo Sounds".
    I can't tell however if setting the Dip Switch to OFF means "Demo Sounds" OFF or ON !

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class iqblock
{
	
	
	public static WriteHandlerPtr prot_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    UINT8 *mem = memory_region( REGION_CPU1 );
	
	    mem[0xfe26] = data;
	    mem[0xfe27] = data;
	    mem[0xfe1c] = data;
	} };
	
	
	static INTERRUPT_GEN( iqblock_interrupt )
	{
		if (cpu_getiloops() & 1)
			cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);	/* ???? */
		else
			cpu_set_irq_line(0, 0, ASSERT_LINE);			/* ???? */
	}
	
	public static WriteHandlerPtr iqblock_irqack_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_set_irq_line(0, 0, CLEAR_LINE);
	} };
	
	public static ReadHandlerPtr extrarom_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return memory_region(REGION_USER1)[offset];
	} };
	
	
	public static WriteHandlerPtr port_C_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* bit 4 unknown; it is pulsed at the end of every NMI */
	
		/* bit 5 seems to be 0 during screen redraw */
		iqblock_videoenable = data & 0x20;
	
		/* bit 6 is coin counter */
		coin_counter_w(0,data & 0x40);
	
		/* bit 7 could be a second coin counter, but coin 2 doesn't seem to work... */
	} };
	
	static ppi8255_interface ppi8255_intf =
	{
		1, 							/* 1 chip */
		{ input_port_0_r },			/* Port A read */
		{ input_port_1_r },			/* Port B read */
		{ input_port_2_r },			/* Port C read */
		{ 0 },						/* Port A write */
		{ 0 },						/* Port B write */
		{ port_C_w },				/* Port C write */
	};
	
	MACHINE_INIT( iqblock )
	{
		ppi8255_init(&ppi8255_intf);
	}
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xfe26, 0xfe26, prot_w ),	/* protection workaround */
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x5080, 0x5083, ppi8255_0_r ),
		new IO_ReadPort( 0x5090, 0x5090, input_port_3_r ),
		new IO_ReadPort( 0x50a0, 0x50a0, input_port_4_r ),
		new IO_ReadPort( 0x7000, 0x7fff, iqblock_bgvideoram_r ),
		new IO_ReadPort( 0x8000, 0xffff, extrarom_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x2000, 0x23ff, paletteram_xBBBBBGGGGGRRRRR_split1_w ),
		new IO_WritePort( 0x2800, 0x2bff, paletteram_xBBBBBGGGGGRRRRR_split2_w ),
		new IO_WritePort( 0x6000, 0x603f, iqblock_fgscroll_w ),
		new IO_WritePort( 0x6800, 0x69ff, iqblock_fgvideoram_w ),	/* initialized up to 6fff... bug or larger tilemap? */
		new IO_WritePort( 0x7000, 0x7fff, iqblock_bgvideoram_w ),
		new IO_WritePort( 0x5080, 0x5083, ppi8255_0_w ),
		new IO_WritePort( 0x50b0, 0x50b0, YM2413_register_port_0_w ), // UM3567_register_port_0_w
		new IO_WritePort( 0x50b1, 0x50b1, YM2413_data_port_0_w ), // UM3567_data_port_0_w
		new IO_WritePort( 0x50c0, 0x50c0, iqblock_irqack_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_iqblock = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 );			// "test mode" only
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN2 );				// "test mode" only
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );// "test mode" only
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON3 );			// "test mode" only
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON4 );			// "test mode" only
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL );// "test mode" only
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_COCKTAIL );// "test mode" only
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, "Unknown SW 0-0&1" );// Difficulty ? Read notes above
		PORT_DIPSETTING(    0x03, "0" );
		PORT_DIPSETTING(    0x02, "1" );
		PORT_DIPSETTING(    0x01, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPNAME( 0x0c, 0x0c, "Helps" );
		PORT_DIPSETTING(    0x0c, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x70, 0x70, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x00, "Demo Sounds?" );// To be confirmed ! Read notes above
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout tilelayout1 = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,3),
		6,
		new int[] { 8, 0, RGN_FRAC(1,3)+8, RGN_FRAC(1,3)+0, RGN_FRAC(2,3)+8, RGN_FRAC(2,3)+0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		8*16
	);
	
	static GfxLayout tilelayout2 = new GfxLayout
	(
		8,32,
		RGN_FRAC(1,2),
		4,
		new int[] { 8, 0, RGN_FRAC(1,2)+8, RGN_FRAC(1,2)+0 },
		new int[] {	0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] {	0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
			8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16,
			16*16, 17*16, 18*16, 19*16, 20*16, 21*16, 22*16, 23*16,
			24*16, 25*16, 26*16, 27*16, 28*16, 29*16, 30*16, 31*16 },
		32*16
	);
	
	static GfxLayout tilelayout3 = new GfxLayout
	(
		8,32,
		RGN_FRAC(1,3),
		6,
		new int[] { 8, 0, RGN_FRAC(1,3)+8, RGN_FRAC(1,3)+0, RGN_FRAC(2,3)+8, RGN_FRAC(2,3)+0 },
		new int[] {	0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] {	0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
			8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16,
			16*16, 17*16, 18*16, 19*16, 20*16, 21*16, 22*16, 23*16,
			24*16, 25*16, 26*16, 27*16, 28*16, 29*16, 30*16, 31*16 },
		32*16
	);
	
	static GfxDecodeInfo gfxdecodeinfo_iqblock[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tilelayout1, 0, 16 ),	/* only odd color codes are used */
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout2, 0,  4 ),	/* only color codes 0 and 3 used */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo gfxdecodeinfo_cabaret[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tilelayout1, 0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout3, 0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static struct YM2413interface ym2413_interface =
	{
		1,
		3579545,    /* 3.579545 MHz */
		{ YM2413_VOL(100,MIXER_PAN_CENTER,100,MIXER_PAN_CENTER) }
	};
	
	
	static MACHINE_DRIVER_START( iqblock )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,12000000/2)	/* 6 MHz */
		MDRV_CPU_FLAGS(CPU_16BIT_PORT)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(iqblock_interrupt,16)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(iqblock)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER|VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 64*8-1, 0*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo_iqblock)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(iqblock)
		MDRV_VIDEO_UPDATE(iqblock)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2413, ym2413_interface) // UM3567
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( cabaret )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,12000000/2)	/* 6 MHz */
		MDRV_CPU_FLAGS(CPU_16BIT_PORT)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(iqblock_interrupt,16)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(iqblock)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER|VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 64*8-1, 0*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo_cabaret)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(iqblock)
		MDRV_VIDEO_UPDATE(iqblock)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2413, ym2413_interface) // UM3567
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_iqblock = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );/* 64k for code + 64K for extra RAM */
		ROM_LOAD( "u7.v5",        0x0000, 0x10000, 0x811f306e );
	
		ROM_REGION( 0x8000, REGION_USER1, 0 );
		ROM_LOAD( "u8.6",         0x0000, 0x8000, 0x2651bc27 );/* background maps, read by the CPU */
	
		ROM_REGION( 0x60000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "u28.1",        0x00000, 0x20000, 0xec4b64b4 );
		ROM_LOAD( "u27.2",        0x20000, 0x20000, 0x74aa3de3 );
		ROM_LOAD( "u26.3",        0x40000, 0x20000, 0x2896331b );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "u25.4",        0x0000, 0x4000, 0x8fc222af );
		ROM_LOAD( "u24.5",        0x4000, 0x4000, 0x61050e1e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_cabaret = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );/* 64k for code + 64K for extra RAM */
		/* 0000-7fff missing??? */
		ROM_LOAD( "cabaret.008",  0x8000, 0x8000, 0x8ed8066c );
	
		ROM_REGION( 0x8000, REGION_USER1, 0 );
		ROM_LOAD( "cabaret.007",  0x0000, 0x8000, 0xb93ae6f8 );/* background maps, read by the CPU */
	
		ROM_REGION( 0x60000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "cabaret.004",  0x00000, 0x20000, 0xe509f50a );
		ROM_LOAD( "cabaret.005",  0x20000, 0x20000, 0xe2cbf489 );
		ROM_LOAD( "cabaret.006",  0x40000, 0x20000, 0x4f2fced7 );
	
		ROM_REGION( 0xc000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "cabaret.001",  0x0000, 0x4000, 0x7dee8b1f );
		ROM_LOAD( "cabaret.002",  0x4000, 0x4000, 0xce8dea39 );
		ROM_LOAD( "cabaret.003",  0x8000, 0x4000, 0x7e1f821f );
	ROM_END(); }}; 
	
	
	
	static DRIVER_INIT( iqblock )
	{
		UINT8 *rom = memory_region(REGION_CPU1);
		int i;
	
		/* decrypt the program ROM */
		for (i = 0;i < 0xf000;i++)
		{
			if ((i & 0x0282) != 0x0282) rom[i] ^= 0x01;
			if ((i & 0x0940) == 0x0940) rom[i] ^= 0x02;
			if ((i & 0x0090) == 0x0010) rom[i] ^= 0x20;
		}
	
		/* initialize pointers for I/O mapped RAM */
		paletteram         = rom + 0x12000;
		paletteram_2       = rom + 0x12800;
		iqblock_fgvideoram = rom + 0x16800;
		iqblock_bgvideoram = rom + 0x17000;
	}
	
	static DRIVER_INIT( cabaret )
	{
		UINT8 *rom = memory_region(REGION_CPU1);
		int i;
	
		/* decrypt the program ROM */
		for (i = 0;i < 0xf000;i++)
		{
			if ((i & 0xb206) == 0xa002) rom[i] ^= 0x01;	// could be (i & 0x3206) == 0x2002
		}
	
		/* initialize pointers for I/O mapped RAM */
		paletteram         = rom + 0x12000;
		paletteram_2       = rom + 0x12800;
		iqblock_fgvideoram = rom + 0x16800;
		iqblock_bgvideoram = rom + 0x17000;
	}
	
	
	
	public static GameDriver driver_iqblock	   = new GameDriver("1993"	,"iqblock"	,"iqblock.java"	,rom_iqblock,null	,machine_driver_iqblock	,input_ports_iqblock	,init_iqblock	,ROT0	,	"IGS", "IQ-Block" )
	
	public static GameDriver driver_cabaret	   = new GameDriver("19??"	,"cabaret"	,"iqblock.java"	,rom_cabaret,null	,machine_driver_cabaret	,input_ports_iqblock	,init_cabaret	,ROT0	,	"IGS", "Cabaret", GAME_NOT_WORKING | GAME_NO_SOUND )
}
