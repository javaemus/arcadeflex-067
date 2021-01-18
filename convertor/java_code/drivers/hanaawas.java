/***************************************************************************

  Hana Awase driver by Zsolt Vasvari

  edge connector
  ----------------------------
      GND   |A| 1|    GND
      GND   |B| 1|  1P "3"
      +5V   |C| 3|    +5V
      NC    |D| 4|    NC
     +12V   |E| 5|   +12V
  SPEAKER(+)|F| 6|SPEAKER(-)
     SYNC   |H| 7|    NC
       B    |J| 8|SERVICE SW
       G    |K| 9|  COIN SW
       R    |L|10|    NC
      NC    |M|11| 1P,2P "10"
    2P "11" |N|12| 1P,2P "9"
    2P "4"  |P|13| 1P,2P "8"
      NC    |R|14| 1P,2P "7"
    1P "4"  |S|15| 1P,2P "5"
    1P "11" |T|16| 1P,2P "6"
      NC    |U|17|    NC
    1P "1"  |V|18|  1P "2"

TODO:

- Keys for player 2

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class hanaawas
{
	
	
	PALETTE_INIT( hanaawas );
	VIDEO_UPDATE( hanaawas );
	
	
	public static ReadHandlerPtr hanaawas_input_port_0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int i,ordinal = 0;
		UINT16 buttons;
	
		/* as to which player's jeys are read are probably selected via port 0, but
		   it's not obvious to me how */
	
		buttons = readinputport(2);
	
		/* map button pressed into 1-10 range */
	
		for (i = 0; i < 10; i++)
		{
			if (buttons & (1 << i))
			{
				ordinal = (i + 1);
				break;
			}
		}
	
		return (input_port_0_r(0) & 0xf0) | ordinal;
	} };
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x2fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x6fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x8bff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x2fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x4fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x6fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x83ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x8400, 0x87ff, hanaawas_colorram_w, colorram ),
		new Memory_WriteAddress( 0x8800, 0x8bff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, hanaawas_input_port_0_r ),
		new IO_ReadPort( 0x10, 0x10, AY8910_read_port_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x10, AY8910_control_port_0_w ),
		new IO_WritePort( 0x11, 0x11, AY8910_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_hanaawas = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x0f, IP_ACTIVE_HIGH, IPT_SPECIAL );
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x02, "1.5" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x06, "2.5" );
		PORT_DIPNAME( 0x18, 0x10, "Key Time-Out" );
		PORT_DIPSETTING(    0x00, "15 sec" );
		PORT_DIPSETTING(    0x08, "20 sec" );
		PORT_DIPSETTING(    0x10, "25 sec" );
		PORT_DIPSETTING(    0x18, "30 sec" );
		PORT_DIPNAME( 0x20, 0x00, "Time Per Coin" );
		PORT_DIPSETTING(    0x20, "50" );
		PORT_DIPSETTING(    0x00, "100" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
	
		/* fake port.  The button depressed gets converted to an integer in the 1-10 range */
		PORT_START();       /* IN2 */
		PORT_BIT( 0x001, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x001, IP_ACTIVE_HIGH, IPT_START1 );	/* same as button 1 */
		PORT_BIT( 0x002, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x002, IP_ACTIVE_HIGH, IPT_START2 );	/* same as button 2 */
		PORT_BIT( 0x004, IP_ACTIVE_HIGH, IPT_BUTTON3 );
		PORT_BIT( 0x008, IP_ACTIVE_HIGH, IPT_BUTTON4 );
		PORT_BIT( 0x010, IP_ACTIVE_HIGH, IPT_BUTTON5 );
		PORT_BIT( 0x020, IP_ACTIVE_HIGH, IPT_BUTTON6 );
		PORT_BIT( 0x040, IP_ACTIVE_HIGH, IPT_BUTTON7 );
		PORT_BIT( 0x080, IP_ACTIVE_HIGH, IPT_BUTTON8 );
		PORT_BIT( 0x100, IP_ACTIVE_HIGH, IPT_BUTTON9 );
		PORT_BIT( 0x200, IP_ACTIVE_HIGH, IPT_BUTTON10 );
	INPUT_PORTS_END(); }}; 
	
	
	#define GFX(name, offs1, offs2, offs3)				\
	static GfxLayout name = new GfxLayout\
	(													\
		8,8,    /* 8*8 chars */							\
		512,    /* 512 characters */					\
		3,      /* 3 bits per pixel */					\
		new int[] { offs1, offs2, offs3 },  /* bitplanes */		\
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },		\
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },		\
		8*16     /* every char takes 16 consecutive bytes */	\
	);
	
	GFX( charlayout_1bpp, 0x2000*8+4, 0x2000*8+4, 0x2000*8+4 )
	GFX( charlayout_3bpp, 0x2000*8,   0,          4          )
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout_1bpp, 0, 32 ),
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout_3bpp, 0, 32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,	/* 1 chip */
		18432000/12,	/* 1.5 MHz ? */
		new int[] { 50 },
		new ReadHandlerPtr[] { input_port_1_r },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { hanaawas_portB_w }
	);
	
	
	static MACHINE_DRIVER_START( hanaawas )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,18432000/6)	/* 3.072 MHz ??? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 0*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(16)
		MDRV_COLORTABLE_LENGTH(32*8)
	
		MDRV_PALETTE_INIT(hanaawas)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(hanaawas)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_hanaawas = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );      /* 64k for code */
		ROM_LOAD( "1.1e",    	0x0000, 0x2000, 0x618dc1e3 );
		ROM_LOAD( "2.3e",    	0x2000, 0x1000, 0x5091b67f );
		ROM_LOAD( "3.4e",    	0x4000, 0x1000, 0xdcb65067 );
		ROM_LOAD( "4.6e",    	0x6000, 0x1000, 0x24bee0dc );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "5.9a",		0x0000, 0x1000, 0x304ae219 );
		ROM_LOAD( "6.10a",		0x1000, 0x1000, 0x765a4e5f );
		ROM_LOAD( "7.12a",		0x2000, 0x1000, 0x5245af2d );
		ROM_LOAD( "8.13a",		0x3000, 0x1000, 0x3356ddce );
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 );
		ROM_LOAD( "13j.bpr",	0x0000, 0x0020, 0x99300d85 );/* color PROM */
		ROM_LOAD( "2a.bpr",		0x0020, 0x0100, 0xe26f21a2 );/* lookup table */
		ROM_LOAD( "6g.bpr",		0x0120, 0x0100, 0x4d94fed5 );/* I don't know what this is */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_hanaawas	   = new GameDriver("1982"	,"hanaawas"	,"hanaawas.java"	,rom_hanaawas,null	,machine_driver_hanaawas	,input_ports_hanaawas	,null	,ROT0	,	"Seta", "Hana Awase (Flower Matching)" )
}
