/***************************************************************************

Taxi Driver  (c) 1984 Graphic Techno

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class taxidrvr
{
	
	
	
	public static WriteHandlerPtr p2a_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(0,data); } };
	public static WriteHandlerPtr p2b_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(1,data); } };
	public static WriteHandlerPtr p2c_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(2,data); } };
	public static WriteHandlerPtr p3a_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(3,data); } };
	public static WriteHandlerPtr p3b_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(4,data); } };
	public static WriteHandlerPtr p3c_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(5,data); } };
	public static WriteHandlerPtr p4a_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(6,data); } };
	public static WriteHandlerPtr p4b_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(7,data); } };
	public static WriteHandlerPtr p4c_w = new WriteHandlerPtr() {public void handler(int offset, int data) { taxidrvr_spritectrl_w(8,data); } };
	
	
	
	
	static int s1,s2,s3,s4,latchA,latchB;
	
	public static ReadHandlerPtr p0a_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return latchA;
	} };
	
	public static ReadHandlerPtr p0c_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (s1 << 7);
	} };
	
	public static WriteHandlerPtr p0b_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		latchB = data;
	} };
	
	public static WriteHandlerPtr p0c_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		s2 = data & 1;
	
		taxidrvr_bghide = data & 2;
	
		/* bit 2 toggles during gameplay */
	
		flip_screen_set(data & 8);
	
	//	usrintf_showmessage("%02x",data&0x0f);
	} };
	
	public static ReadHandlerPtr p1b_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return latchB;
	} };
	
	public static ReadHandlerPtr p1c_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (s2 << 7) | (s4 << 6) | ((readinputport(5) & 1) << 4);
	} };
	
	public static WriteHandlerPtr p1a_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		latchA = data;
	} };
	
	public static WriteHandlerPtr p1c_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		s1 = data & 1;
		s3 = (data & 2) >> 1;
	} };
	
	public static ReadHandlerPtr p8910_0a_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return latchA;
	} };
	
	public static ReadHandlerPtr p8910_1a_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return s3;
	} };
	
	/* note that a lot of writes happen with port B set as input. I think this is a bug in the
	   original, since it works anyway even if the communication is flawed. */
	public static WriteHandlerPtr p8910_0b_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		s4 = data & 1;
	} };
	
	
	static ppi8255_interface ppi8255_intf =
	{
		5, 										/* 5 chips */
		{ p0a_r, NULL,  NULL,  NULL,  NULL  },	/* Port A read */
		{ NULL,  p1b_r, NULL,  NULL,  NULL  },	/* Port B read */
		{ p0c_r, p1c_r, NULL,  NULL,  NULL  },	/* Port C read */
		{ NULL,  p1a_w, p2a_w, p3a_w, p4a_w },	/* Port A write */
		{ p0b_w, NULL,  p2b_w, p3b_w, p4b_w },	/* Port B write */
		{ p0c_w, p1c_w, p2c_w, p3c_w, p4c_w }	/* Port C write */
	};
	
	MACHINE_INIT( taxidrvr )
	{
		ppi8255_init(&ppi8255_intf);
	}
	
	
	
	public static Memory_ReadAddress readmem1[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x8fff, MRA_RAM ),
		new Memory_ReadAddress( 0x9000, 0x9fff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xafff, MRA_RAM ),
		new Memory_ReadAddress( 0xb000, 0xbfff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xd800, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xf3ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf400, 0xf403, ppi8255_0_r ),
		new Memory_ReadAddress( 0xf480, 0xf483, ppi8255_2_r ),
		new Memory_ReadAddress( 0xf500, 0xf503, ppi8255_3_r ),
		new Memory_ReadAddress( 0xf580, 0xf583, ppi8255_4_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem1[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x8fff, MWA_RAM ),	/* ??? */
		new Memory_WriteAddress( 0x9000, 0x9fff, MWA_RAM ),	/* ??? */
		new Memory_WriteAddress( 0xa000, 0xafff, MWA_RAM ),	/* ??? */
		new Memory_WriteAddress( 0xb000, 0xbfff, MWA_RAM ),	/* ??? */
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM, taxidrvr_vram4 ),	/* radar bitmap */
		new Memory_WriteAddress( 0xc800, 0xcfff, MWA_RAM, taxidrvr_vram5 ),	/* "sprite1" bitmap */
		new Memory_WriteAddress( 0xd000, 0xd7ff, MWA_RAM, taxidrvr_vram6 ),	/* "sprite2" bitmap */
		new Memory_WriteAddress( 0xd800, 0xdfff, MWA_RAM, taxidrvr_vram7 ),	/* "sprite3" bitmap */
		new Memory_WriteAddress( 0xe000, 0xe3ff, MWA_RAM, taxidrvr_vram1 ),	/* car tilemap */
		new Memory_WriteAddress( 0xe400, 0xebff, MWA_RAM, taxidrvr_vram2 ),	/* bg1 tilemap */
		new Memory_WriteAddress( 0xec00, 0xefff, MWA_RAM, taxidrvr_vram0 ),	/* fg tilemap */
		new Memory_WriteAddress( 0xf000, 0xf3ff, MWA_RAM, taxidrvr_vram3 ),	/* bg2 tilemap */
		new Memory_WriteAddress( 0xf400, 0xf403, ppi8255_0_w ),
		new Memory_WriteAddress( 0xf480, 0xf483, ppi8255_2_w ),	/* "sprite1" placement */
		new Memory_WriteAddress( 0xf500, 0xf503, ppi8255_3_w ),	/* "sprite2" placement */
		new Memory_WriteAddress( 0xf580, 0xf583, ppi8255_4_w ),	/* "sprite3" placement */
	//	new Memory_WriteAddress( 0xf780, 0xf781, MWA_RAM ),		/* more scroll registers? */
		new Memory_WriteAddress( 0xf782, 0xf787, MWA_RAM, taxidrvr_scroll ),	/* bg scroll (three copies always identical) */
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x67ff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa003, ppi8255_1_r ),
		new Memory_ReadAddress( 0xe000, 0xe000, input_port_0_r ),
		new Memory_ReadAddress( 0xe001, 0xe001, input_port_1_r ),
		new Memory_ReadAddress( 0xe002, 0xe002, input_port_2_r ),
		new Memory_ReadAddress( 0xe003, 0xe003, input_port_3_r ),
		new Memory_ReadAddress( 0xe004, 0xe004, input_port_4_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x67ff, MWA_RAM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xa003, ppi8255_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem3[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x2000, MRA_NOP ),	/* irq ack? */
		new Memory_ReadAddress( 0xfc00, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem3[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0xfc00, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport3[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, AY8910_read_port_0_r ),
		new IO_ReadPort( 0x03, 0x03, AY8910_read_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport3[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, AY8910_control_port_0_w ),
		new IO_WritePort( 0x01, 0x01, AY8910_write_port_0_w ),
		new IO_WritePort( 0x02, 0x02, AY8910_control_port_1_w ),
		new IO_WritePort( 0x03, 0x03, AY8910_write_port_1_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_taxidrvr = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x0f, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "4C_2C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "4C_2C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "Free_Play") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x10, "3" );
		PORT_DIPSETTING(    0x18, "4" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_DIPSETTING(    0x28, "6" );
		PORT_DIPSETTING(    0x30, "7" );
		PORT_DIPSETTING(    0x38, "8" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPSETTING(    0x40, "1" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x07, 0x00, "Fuel Consumption" );
		PORT_DIPSETTING(    0x00, "Slowest" );
		PORT_DIPSETTING(    0x01, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x03, "4" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPSETTING(    0x05, "6" );
		PORT_DIPSETTING(    0x06, "7" );
		PORT_DIPSETTING(    0x07, "Fastest" );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x10, "3" );
		PORT_DIPSETTING(    0x18, "4" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_DIPSETTING(    0x28, "6" );
		PORT_DIPSETTING(    0x30, "7" );
		PORT_DIPSETTING(    0x38, "8" );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, "40/30" );
		PORT_DIPSETTING(    0x40, "30/20" );
		PORT_DIPSETTING(    0x80, "20/15" );
		PORT_DIPSETTING(    0xc0, "10/10" );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );/* handled by p1c_r() */
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		4,
		new int[] { 3, 2, 1, 0 },
		new int[] { 1*4, 0*4, 3*4, 2*4, 5*4, 4*4, 7*4, 6*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8
	);
	
	static GfxLayout charlayout2 = new GfxLayout
	(
		4,4,
		RGN_FRAC(1,1),
		4,
		new int[] { 3, 2, 1, 0 },
		new int[] { 1*4, 0*4, 3*4, 2*4 },
		new int[] { 0*16, 1*16, 2*16, 3*16 },
		16*4
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX2, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX3, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX4, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX5, 0, charlayout2, 0, 1 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,	/* 2 chips */
		1250000,	/* 1.25 MHz ??? */
		new int[] { 25, 25 },
		new ReadHandlerPtr[] { p8910_0a_r, p8910_1a_r },
		new ReadHandlerPtr[] { 0, 0 },
		new WriteHandlerPtr[] { 0, 0 },
		new WriteHandlerPtr[] { p8910_0b_w, 0 }
	);
	
	
	
	static MACHINE_DRIVER_START( taxidrvr )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,4000000)	/* 4 MHz ??? */
		MDRV_CPU_MEMORY(readmem1,writemem1)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80,4000000)	/* 4 MHz ??? */
		MDRV_CPU_MEMORY(readmem2,writemem2)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)	/* ??? */
	
		MDRV_CPU_ADD(Z80,4000000)	/* 4 MHz ??? */
		MDRV_CPU_MEMORY(readmem3,writemem3)
		MDRV_CPU_PORTS(readport3,writeport3)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)	/* ??? */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(100)	/* 100 CPU slices per frame - an high value to ensure proper */
								/* synchronization of the CPUs */
		MDRV_MACHINE_INIT(taxidrvr)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 27*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(16)
	
		MDRV_VIDEO_UPDATE(taxidrvr)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_taxidrvr = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "1",            0x0000, 0x2000, 0x6b2424e9 );
		ROM_LOAD( "2",            0x2000, 0x2000, 0x15111229 );
		ROM_LOAD( "3",            0x4000, 0x2000, 0xa7782eee );
		ROM_LOAD( "4",            0x6000, 0x2000, 0x8eb0b16b );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "8",            0x0000, 0x2000, 0x9f9a3865 );
		ROM_LOAD( "9",            0x2000, 0x2000, 0xb28b766c );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );
		ROM_LOAD( "7",            0x0000, 0x2000, 0x2b4cbfe6 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "5",            0x0000, 0x2000, 0xa3aa5f2f );
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "6",            0x0000, 0x2000, 0xbfddd550 );
	
		ROM_REGION( 0x6000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "11",           0x0000, 0x2000, 0x7485eaea );
		ROM_LOAD( "14",           0x2000, 0x2000, 0x0d99a33e );
		ROM_LOAD( "15",           0x4000, 0x2000, 0x410fdf7c );
	
		ROM_REGION( 0x2000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "10",           0x0000, 0x2000, 0xc370b177 );
	
		ROM_REGION( 0x4000, REGION_GFX5, ROMREGION_DISPOSE );/* not used?? */
		ROM_LOAD( "12",           0x0000, 0x2000, 0x684b7bb0 );
		ROM_LOAD( "13",           0x2000, 0x2000, 0xd1ef110e );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_taxidrvr	   = new GameDriver("1984"	,"taxidrvr"	,"taxidrvr.java"	,rom_taxidrvr,null	,machine_driver_taxidrvr	,input_ports_taxidrvr	,null	,ROT90	,	"Graphic Techno", "Taxi Driver", GAME_IMPERFECT_GRAPHICS | GAME_NO_COCKTAIL )
}
