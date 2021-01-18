/*
Super Cross II (JPN Ver.)
(c)1986 GM Shoji

C2-00172-D
CPU  :Z80B
Sound:SN76489 x3

SCS-24.4E
SCS-25.4C
SCS-26.4B
SCS-27.5K
SCS-28.5J
SCS-29.5H
SCS-30.5F

SC-62.3A
SC-63.3B
SC-64.6A

C2-00171-D
CPU  :Z80B
OSC  :10.000MHz

SCM-00.10L
SCM-01.10K
SCM-02.10J
SCM-03.10G
SCM-20.5K
SCM-21.5G
SCM-22.5E
SCM-23.5B

SC-60.4K
SC-61.5A

Notes:

- sprites pop in at the wrong place sometimes before entering the screen

- correct drawing/animation of bg is very sensitive to cpu speed/interrupts/
  interleave, current settings aren't correct but don't think there's any
  visible problems

- engine rev sound may not be completely correct

- bg not using second half of prom, of interest is this half is identical to
  the second half of a bankp/appoooh prom, hardware is similar to bankp/appoooh
  in a few ways, there's also an unused SEGA logo in the bg graphics

- fg not using odd colours, shouldn't matter as the colours are duplicated

- sprite priorities are wrong when bikes are jumping as they are ordered on
  vertical position only, assume this is original behaviour
*/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class sprcros2
{
	
	extern data8_t *sprcros2_fgvideoram, *sprcros2_spriteram, *sprcros2_bgvideoram;
	extern size_t sprcros2_spriteram_size;
	
	
	PALETTE_INIT( sprcros2 );
	VIDEO_START( sprcros2 );
	VIDEO_UPDATE( sprcros2 );
	static data8_t *sprcros2_sharedram;
	int sprcros2_m_port7 = 0;
	static int sprcros2_s_port3 = 0;
	
	public static ReadHandlerPtr sprcros2_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return sprcros2_sharedram[offset];
	} };
	
	public static WriteHandlerPtr sprcros2_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sprcros2_sharedram[offset]=data;
	} };
	
	public static WriteHandlerPtr sprcros2_m_port7_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		unsigned char *RAM = memory_region(REGION_CPU1);
	
		//76543210
		//x------- unused
		//-x------ bankswitch halves of scm-01.10k into c000-dfff
		//--xx---- unused
	    //----x--- irq enable
		//-----x-- ?? off with title flash and screen clears, possibly layer/sprite enable
		//------x- flip screen
		//-------x nmi enable
	
		if((sprcros2_m_port7^data)&0x40)
			cpu_setbank(1,&RAM[0x10000+((data&0x40)<<7)]);
	
		tilemap_set_flip( ALL_TILEMAPS,data&0x02?(TILEMAP_FLIPX|TILEMAP_FLIPY):0 );
	
		sprcros2_m_port7 = data;
	} };
	
	public static WriteHandlerPtr sprcros2_s_port3_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		unsigned char *RAM = memory_region(REGION_CPU2);
	
		//76543210
		//xxxx---- unused
		//----x--- bankswitch halves of scs-27.5k into c000-dfff
		//-----xx- unused
		//-------x nmi enable
	
		if((sprcros2_s_port3^data)&0x08)
			cpu_setbank(2,&RAM[0x10000+((data&0x08)<<10)]);
	
		sprcros2_s_port3 = data;
	} };
	
	public static Memory_ReadAddress sprcros2_m_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xe000, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),						//shared with slave cpu
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sprcros2_m_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_BANK1 ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, sprcros2_fgvideoram_w, sprcros2_fgvideoram ),
		new Memory_WriteAddress( 0xe800, 0xe817, MWA_RAM ),						//always zero
		new Memory_WriteAddress( 0xe818, 0xe83f, MWA_RAM, sprcros2_spriteram, sprcros2_spriteram_size ),
		new Memory_WriteAddress( 0xe840, 0xefff, MWA_RAM ),						//always zero
		new Memory_WriteAddress( 0xf000, 0xf7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM, sprcros2_sharedram ),	//shared with slave cpu
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sprcros2_m_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort( 0x04, 0x04, input_port_3_r ),
		new IO_ReadPort( 0x05, 0x05, input_port_4_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sprcros2_m_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, SN76496_0_w ),
		new IO_WritePort( 0x01, 0x01, SN76496_1_w ),
		new IO_WritePort( 0x02, 0x02, SN76496_2_w ),
		new IO_WritePort( 0x07, 0x07, sprcros2_m_port7_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sprcros2_s_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_BANK2 ),
		new Memory_ReadAddress( 0xe000, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xffff, sprcros2_sharedram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sprcros2_s_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_BANK2 ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, sprcros2_bgvideoram_w, sprcros2_bgvideoram ),
		new Memory_WriteAddress( 0xe800, 0xefff, MWA_RAM ),						//always zero
		new Memory_WriteAddress( 0xf000, 0xf7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf800, 0xffff, sprcros2_sharedram_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sprcros2_s_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, sprcros2_bgscrollx_w ),
		new IO_WritePort( 0x01, 0x01, sprcros2_bgscrolly_w ),
		new IO_WritePort( 0x03, 0x03, sprcros2_s_port3_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static InputPortPtr input_ports_sprcros2 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN3 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x07, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_6C") );
		PORT_BIT( 0xf8, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN4 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x0e, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x70, IP_ACTIVE_HIGH, IPT_UNUSED );		//unused coinage bits
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout sprcros2_bglayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(0,3), RGN_FRAC(1,3), RGN_FRAC(2,3) },
		new int[] { STEP8(0,1) },
		new int[] { STEP8(0,8) },
		8*8
	);
	
	static GfxLayout sprcros2_spritelayout = new GfxLayout
	(
		32,32,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(0,3), RGN_FRAC(1,3), RGN_FRAC(2,3) },
		new int[] { STEP8(0,1), STEP8(256,1), STEP8(512,1), STEP8(768,1) },
		new int[] { STEP16(0,8), STEP16(128,8) },
		32*32
	);
	
	static GfxLayout sprcros2_fglayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		2,
		new int[] { 0, 4 },
		new int[] { STEP4(64,1), STEP4(0,1) },
		new int[] { STEP8(0,8) },
		8*8*2
	);
	
	static GfxDecodeInfo sprcros2_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, sprcros2_bglayout,     0,   16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, sprcros2_spritelayout, 256, 6  ),
		new GfxDecodeInfo( REGION_GFX3, 0, sprcros2_fglayout,     512, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static SN76496interface sprcros2_sn76496_interface = new SN76496interface
	(
		3,
		new int[] { 10000000/4, 10000000/4, 10000000/4 },
		new int[] { 50, 50, 50 }
	);
	
	static INTERRUPT_GEN( sprcros2_m_interrupt )
	{
		if (cpu_getiloops() == 0)
		{
			if(sprcros2_m_port7&0x01)
				cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
		}
		else
		{
			if(sprcros2_m_port7&0x08)
				cpu_set_irq_line(0, 0, HOLD_LINE);
		}
	}
	
	static INTERRUPT_GEN( sprcros2_s_interrupt )
	{
		if(sprcros2_s_port3&0x01)
			cpu_set_irq_line(1, IRQ_LINE_NMI, PULSE_LINE);
	}
	
	static MACHINE_DRIVER_START( sprcros2 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,10000000/2)
		MDRV_CPU_MEMORY(sprcros2_m_readmem,sprcros2_m_writemem)
		MDRV_CPU_PORTS(sprcros2_m_readport,sprcros2_m_writeport)
		MDRV_CPU_VBLANK_INT(sprcros2_m_interrupt,2)	//1 nmi + 1 irq
	
		MDRV_CPU_ADD(Z80,10000000/2)
		MDRV_CPU_MEMORY(sprcros2_s_readmem,sprcros2_s_writemem)
		MDRV_CPU_PORTS(0,sprcros2_s_writeport)
		MDRV_CPU_VBLANK_INT(sprcros2_s_interrupt,2)	//2 nmis
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(sprcros2_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(18)
		MDRV_COLORTABLE_LENGTH(768)
	
		MDRV_PALETTE_INIT(sprcros2)
		MDRV_VIDEO_START(sprcros2)
		MDRV_VIDEO_UPDATE(sprcros2)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SN76496, sprcros2_sn76496_interface)
	MACHINE_DRIVER_END
	
	static DRIVER_INIT( sprcros2 )
	{
		state_save_register_int("main", 0, "m_cpu_port7", &sprcros2_m_port7);
		state_save_register_int("main", 0, "s_cpu_port3", &sprcros2_s_port3);
	}
	
	static RomLoadPtr rom_sprcros2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x14000, REGION_CPU1, 0 );
		ROM_LOAD( "scm-03.10g", 0x00000, 0x4000, 0xb9757908 );
		ROM_LOAD( "scm-02.10j", 0x04000, 0x4000, 0x849c5c87 );
		ROM_LOAD( "scm-01.10k", 0x08000, 0x4000, 0x385a62de );
	
		ROM_LOAD( "scm-00.10l", 0x10000, 0x4000, 0x13fa3684 );//banked into c000-dfff
	
		ROM_REGION( 0x14000, REGION_CPU2, 0 );
		ROM_LOAD( "scs-30.5f",  0x00000, 0x4000, 0xc0a40e41 );
		ROM_LOAD( "scs-29.5h",  0x04000, 0x4000, 0x83d49fa5 );
		ROM_LOAD( "scs-28.5j",  0x08000, 0x4000, 0x480d351f );
	
		ROM_LOAD( "scs-27.5k",  0x10000, 0x4000, 0x2cf720cb );//banked into c000-dfff
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );//bg
		ROM_LOAD( "scs-26.4b",   0x0000, 0x4000, 0xf958b56d );
		ROM_LOAD( "scs-25.4c",   0x4000, 0x4000, 0xd6fd7ba5 );
		ROM_LOAD( "scs-24.4e",   0x8000, 0x4000, 0x87783c36 );
	
		ROM_REGION( 0xc000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "scm-23.5b",   0x0000, 0x4000, 0xab42f8e3 );//sprites
		ROM_LOAD( "scm-22.5e",   0x4000, 0x4000, 0x0cad254c );
		ROM_LOAD( "scm-21.5g",   0x8000, 0x4000, 0xb6b68998 );
	
		ROM_REGION( 0x4000, REGION_GFX3, ROMREGION_DISPOSE );//fg
		ROM_LOAD( "scm-20.5k",   0x0000, 0x4000, 0x67a099a6 );
	
		ROM_REGION( 0x0420, REGION_PROMS, 0 );
		ROM_LOAD( "sc-64.6a",    0x0000, 0x0020, 0x336dd1c0 );//palette
		ROM_LOAD( "sc-63.3b",    0x0020, 0x0100, 0x9034a059 );//bg clut lo nibble
		ROM_LOAD( "sc-62.3a",    0x0120, 0x0100, 0x3c78a14f );//bg clut hi nibble
		ROM_LOAD( "sc-61.5a",    0x0220, 0x0100, 0x2f71185d );//sprite clut
		ROM_LOAD( "sc-60.4k",    0x0320, 0x0100, 0xd7a4e57d );//fg clut
	ROM_END(); }}; 
	
	public static GameDriver driver_sprcros2	   = new GameDriver("1986"	,"sprcros2"	,"sprcros2.java"	,rom_sprcros2,null	,machine_driver_sprcros2	,input_ports_sprcros2	,init_sprcros2	,ROT0	,	"GM Shoji", "Super Cross 2 (Japan)" )
}
