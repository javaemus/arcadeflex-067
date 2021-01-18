/***************************************************************************

Driver by Tomasz Slanina  dox@space.pl

***************************************************************************

RAM :
	1 x GM76c28-10 (6116) RAM
	3 x 2114  - VRAM (only 10 bits are used )

ROM:
  27256 + 27128 for code/data
  3x2764 for gfx

PROM:
 82S123 32x8
 Used for system control
 	(d0 - connected to ROM5 /CS , d1 - ROM4 /CS, d2 - RAM /CS , d3 - to some logic(gfx control), and Z80 WAIT )

Memory Map :
  0x0000 - 0xbfff - ROM
  0xc000 - 0xcfff - RAM
  0xd000 - 0xdfff - VRAM mirrored write,
  		tilemap offset = address & 0x3ff
  		tile number =  bits 0-7 = data, bits 8,9  = address bits 10,11

Video :
	No scrolling , no sprites.
	32x32 Tilemap stored in VRAM (10 bits/tile (tile numebr 0-1023))

	3 gfx ROMS
	ROM1 - R component (ROM ->(parallel in) shift register 74166 (serial out) -> jamma output
	ROM2 - B component
	ROM3 - G component

	Default MAME color palette is used

Sound :
 AY 3 8910

 sound_control :

  bit 0 - BC1
  bit 1 - BC2
  bit 2 - BDIR

  bits 3-7 - not connected

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class _4enraya
{
	
	VIDEO_START( 4enraya );
	VIDEO_UPDATE( 4enraya );
	
	
	static int soundlatch;
	
	public static WriteHandlerPtr sound_data_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch = data;
	} };
	
	public static WriteHandlerPtr sound_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		static int last;
		if ((last & 0x04) == 0x04 && (data & 0x4) == 0x00)
		{
			if (last & 0x01)
				AY8910_control_port_0_w(0,soundlatch);
			else
				AY8910_write_port_0_w(0,soundlatch);
		}
		last=data;
	} };
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xcfff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xffff, MRA_NOP ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xcfff, MWA_RAM ),
		new Memory_WriteAddress( 0xd000, 0xdfff, fenraya_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x23, 0x23, sound_data_w ),
		new IO_WritePort( 0x33, 0x33, sound_control_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_4enraya = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x01, "Easy" );
		PORT_DIPSETTING(    0x00, "Hard" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "Pieces" );
		PORT_DIPSETTING(    0x04, "30" );
		PORT_DIPSETTING(    0x00, "16" );
		PORT_DIPNAME( 0x08, 0x08, "Speed" );
		PORT_DIPSETTING(    0x08, "Slow" );
		PORT_DIPSETTING(    0x00, "Fast" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );			// "drop" ("down")
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );			// "drop" ("down")
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );			// "fire" ("shot")
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );			// "fire" ("shot")
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0*1024*8*8, 2*1024*8*8, 1*1024*8*8 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0, 8 ),
		new GfxDecodeInfo( -1 )	/* end of array */
	};
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,
		8000000/4,	/* guess */
		new int[] { 30,},
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	static MACHINE_DRIVER_START( 4enraya )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,8000000/2)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,4)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(4enraya)
		MDRV_VIDEO_UPDATE(4enraya)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_4enraya = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "5.bin",   0x0000, 0x8000, 0xcf1cd151 );
		ROM_LOAD( "4.bin",   0x8000, 0x4000, 0xf9ec1be7 );
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "1.bin",   0x0000, 0x2000, 0x87f92552 );
		ROM_LOAD( "2.bin",   0x2000, 0x2000, 0x2b0a3793 );
		ROM_LOAD( "3.bin",   0x4000, 0x2000, 0xf6940836 );
	
		ROM_REGION( 0x0020,  REGION_PROMS, 0 );
		ROM_LOAD( "1.bpr",   0x0000, 0x0020, 0x0dcbd2352 );/* not used */
	ROM_END(); }}; 
	
	public static GameDriver driver_4enraya	   = new GameDriver("1990"	,"4enraya"	,"_4enraya.java"	,rom_4enraya,null	,machine_driver_4enraya	,input_ports_4enraya	,null	,ROT0	,	"IDSA", "4 En Raya" )
}
