/***************************************************************************

  Vigilante

If you have any questions about how this driver works, don't hesitate to
ask.  - Mike Balfour (mab22@po.cwru.edu)

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class vigilant
{
	
	/* vidhrdw/vigilant.c */
	VIDEO_START( vigilant );
	VIDEO_UPDATE( vigilant );
	VIDEO_UPDATE( kikcubic );
	
	
	public static WriteHandlerPtr vigilant_bank_select_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int bankaddress;
		unsigned char *RAM = memory_region(REGION_CPU1);
	
		bankaddress = 0x10000 + (data & 0x07) * 0x4000;
		cpu_setbank(1,&RAM[bankaddress]);
	} };
	
	/***************************************************************************
	 vigilant_out2_w
	 **************************************************************************/
	public static WriteHandlerPtr vigilant_out2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* D0 = FILP = Flip screen? */
		/* D1 = COA1 = Coin Counter A? */
		/* D2 = COB1 = Coin Counter B? */
	
		/* The hardware has both coin counters hooked up to a single meter. */
		coin_counter_w(0,data & 0x02);
		coin_counter_w(1,data & 0x04);
	} };
	
	public static WriteHandlerPtr kikcubic_coin_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* bits 0 is flip screen */
	
		/* bit 1 is used but unknown */
	
		/* bits 4/5 are coin counters */
		coin_counter_w(0,data & 0x10);
		coin_counter_w(1,data & 0x20);
	} };
	
	
	
	public static Memory_ReadAddress vigilant_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc020, 0xc0df, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xcfff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xdfff, videoram_r ),
		new Memory_ReadAddress( 0xe000, 0xefff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress vigilant_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc020, 0xc0df, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xc800, 0xcfff, vigilant_paletteram_w, paletteram ),
		new Memory_WriteAddress( 0xd000, 0xdfff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xe000, 0xefff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort vigilant_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort( 0x03, 0x03, input_port_3_r ),
		new IO_ReadPort( 0x04, 0x04, input_port_4_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort vigilant_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, m72_sound_command_w ),  /* SD */
		new IO_WritePort( 0x01, 0x01, vigilant_out2_w ), /* OUT2 */
		new IO_WritePort( 0x04, 0x04, vigilant_bank_select_w ), /* PBANK */
		new IO_WritePort( 0x80, 0x81, vigilant_horiz_scroll_w ), /* HSPL, HSPH */
		new IO_WritePort( 0x82, 0x83, vigilant_rear_horiz_scroll_w ), /* RHSPL, RHSPH */
		new IO_WritePort( 0x84, 0x84, vigilant_rear_color_w ), /* RCOD */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress kikcubic_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xc0ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xcaff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xdfff, videoram_r ),
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress kikcubic_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc0ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xc800, 0xcaff, vigilant_paletteram_w, paletteram ),
		new Memory_WriteAddress( 0xd000, 0xdfff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xe000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort kikcubic_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_3_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_4_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_0_r ),
		new IO_ReadPort( 0x03, 0x03, input_port_1_r ),
		new IO_ReadPort( 0x04, 0x04, input_port_2_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort kikcubic_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, kikcubic_coin_w ),	/* also flip screen, and...? */
		new IO_WritePort( 0x04, 0x04, vigilant_bank_select_w ),
		new IO_WritePort( 0x06, 0x06, m72_sound_command_w ),
	//	new IO_WritePort( 0x07, 0x07, IOWP_NOP ),	/* ?? */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, YM2151_status_port_0_r ),
		new IO_ReadPort( 0x80, 0x80, soundlatch_r ),	/* SDRE */
		new IO_ReadPort( 0x84, 0x84, m72_sample_r ),	/* S ROM C */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, YM2151_register_port_0_w ),
		new IO_WritePort( 0x01, 0x01, YM2151_data_port_0_w ),
		new IO_WritePort( 0x80, 0x81, vigilant_sample_addr_w ),	/* STL / STH */
		new IO_WritePort( 0x82, 0x82, m72_sample_w ),			/* COUNT UP */
		new IO_WritePort( 0x83, 0x83, m72_sound_irq_ack_w ),	/* IRQ clear */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_vigilant = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT(0xF0, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
		PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
		PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP );
		PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_BUTTON2 );
	
		PORT_START(); 
		PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_COCKTAIL );
		PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_COCKTAIL );
		PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_COCKTAIL );
		PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x02, "2" );
		PORT_DIPSETTING(	0x03, "3" );
		PORT_DIPSETTING(	0x01, "4" );
		PORT_DIPSETTING(	0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(	0x04, "Normal" );
		PORT_DIPSETTING(	0x00, "Hard" );
		PORT_DIPNAME( 0x08, 0x08, "Decrease of Energy" );
		PORT_DIPSETTING(	0x08, "Slow" );
		PORT_DIPSETTING(	0x00, "Fast" );
		/* TODO: support the different settings which happen in Coin Mode 2 */
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0xa0, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(	0xb0, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(	0xc0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0xd0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x10, DEF_STR( "8C_3C") );
		PORT_DIPSETTING(	0xe0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x20, DEF_STR( "5C_3C") );
		PORT_DIPSETTING(	0x30, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(	0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x40, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(	0x90, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x70, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0x60, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0x50, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Cocktail") );
	/* This activates a different coin mode. Look at the dip switch setting schematic */
		PORT_DIPNAME( 0x04, 0x04, "Coin Mode" );
		PORT_DIPSETTING(	0x04, "Mode 1" );
		PORT_DIPSETTING(	0x00, "Mode 2" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Allow Continue" );
		PORT_DIPSETTING(	0x00, DEF_STR( "No") );
		PORT_DIPSETTING(	0x10, DEF_STR( "Yes") );
		/* In stop mode, press 2 to stop and 1 to restart */
		PORT_BITX   ( 0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Stop Mode", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(	0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_kikcubic = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT_IMPULSE( 0x40, IP_ACTIVE_LOW, IPT_COIN3, 19 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x02, "Easy" );
		PORT_DIPSETTING(    0x03, "Medium" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		/* TODO: support the different settings which happen in Coin Mode 2 */
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0xa0, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(	0xb0, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(	0xc0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0xd0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0xe0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x70, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x60, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x50, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0x40, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0x30, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
	//	PORT_DIPSETTING(	0x10, "Undefined" );
	//	PORT_DIPSETTING(	0x20, "Undefined" );
	//	PORT_DIPSETTING(	0x80, "Undefined" );
	//	PORT_DIPSETTING(	0x90, "Undefined" );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Cocktail") );
	/* This activates a different coin mode. Look at the dip switch setting schematic */
		PORT_DIPNAME( 0x04, 0x04, "Coin Mode" );
		PORT_DIPSETTING(	0x04, "Mode 1" );
		PORT_DIPSETTING(	0x00, "Mode 2" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_BITX(    0x10, 0x10, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Level Select" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Player Adding" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout text_layout = new GfxLayout
	(
		8,8, /* tile size */
		4096, /* number of tiles */
		4, /* bits per pixel */
		new int[] {64*1024*8,64*1024*8+4,0,4}, /* plane offsets */
		new int[] { 0,1,2,3, 64+0,64+1,64+2,64+3 }, /* x offsets */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 }, /* y offsets */
		128
	);
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,	/* tile size */
		4096,	/* number of sprites ($1000) */
		4,		/* bits per pixel */
		new int[] {0x40000*8,0x40000*8+4,0,4}, /* plane offsets */
		new int[] { /* x offsets */
			0x00*8+0,0x00*8+1,0x00*8+2,0x00*8+3,
			0x10*8+0,0x10*8+1,0x10*8+2,0x10*8+3,
			0x20*8+0,0x20*8+1,0x20*8+2,0x20*8+3,
			0x30*8+0,0x30*8+1,0x30*8+2,0x30*8+3
		},
		new int[] { /* y offsets */
			0x00*8, 0x01*8, 0x02*8, 0x03*8,
			0x04*8, 0x05*8, 0x06*8, 0x07*8,
			0x08*8, 0x09*8, 0x0A*8, 0x0B*8,
			0x0C*8, 0x0D*8, 0x0E*8, 0x0F*8
		},
		0x40*8
	);
	
	static GfxLayout back_layout = new GfxLayout
	(
		32,1, /* tile size */
		3*512*8, /* number of tiles */
		4, /* bits per pixel */
		new int[] {0,2,4,6}, /* plane offsets */
		new int[] { 0*8+1, 0*8,  1*8+1, 1*8, 2*8+1, 2*8, 3*8+1, 3*8, 4*8+1, 4*8, 5*8+1, 5*8,
		6*8+1, 6*8, 7*8+1, 7*8, 8*8+1, 8*8, 9*8+1, 9*8, 10*8+1, 10*8, 11*8+1, 11*8,
		12*8+1, 12*8, 13*8+1, 13*8, 14*8+1, 14*8, 15*8+1, 15*8 }, /* x offsets */
		new int[] { 0 }, /* y offsets */
		16*8
	);
	
	static GfxDecodeInfo vigilant_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, text_layout,   256, 16 ),	/* colors 256-511 */
		new GfxDecodeInfo( REGION_GFX2, 0, sprite_layout,   0, 16 ),	/* colors   0-255 */
		new GfxDecodeInfo( REGION_GFX3, 0, back_layout,   512,  2 ),	/* actually the background uses colors */
														/* 256-511, but giving it exclusive */
														/* pens we can handle it more easily. */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo kikcubic_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, text_layout,   0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, sprite_layout, 0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static struct YM2151interface ym2151_interface =
	{
		1,			/* 1 chip */
		3579645,	/* 3.579645 MHz */
		{ YM3012_VOL(55,MIXER_PAN_LEFT,55,MIXER_PAN_RIGHT) },
		{ m72_ym2151_irq_handler },
		{ 0 }
	};
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	
	
	static MACHINE_DRIVER_START( vigilant )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3579645)		   /* 3.579645 MHz */
		MDRV_CPU_MEMORY(vigilant_readmem,vigilant_writemem)
		MDRV_CPU_PORTS(vigilant_readport,vigilant_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 3579645)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)		   /* 3.579645 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1 */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(55)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(16*8, (64-16)*8-1, 0*8, 32*8-1 )
		MDRV_GFXDECODE(vigilant_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512+32)	/* 512 real palette, 32 virtual palette */
	
		MDRV_VIDEO_START(vigilant)
		MDRV_VIDEO_UPDATE(vigilant)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( kikcubic )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3579645)		   /* 3.579645 MHz */
		MDRV_CPU_MEMORY(kikcubic_readmem,kikcubic_writemem)
		MDRV_CPU_PORTS(kikcubic_readport,kikcubic_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 3579645)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)		   /* 3.579645 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1 */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(55)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(8*8, (64-8)*8-1, 0*8, 32*8-1 )
		MDRV_GFXDECODE(kikcubic_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(vigilant)
		MDRV_VIDEO_UPDATE(kikcubic)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game ROMs
	
	***************************************************************************/
	
	static RomLoadPtr rom_vigilant = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );/* 64k for code + 128k for bankswitching */
		ROM_LOAD( "g07_c03.bin",  0x00000, 0x08000, 0x9dcca081 );
		ROM_LOAD( "j07_c04.bin",  0x10000, 0x10000, 0xe0159105 );
		/* 0x20000-0x2ffff empty */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound */
		ROM_LOAD( "g05_c02.bin",  0x00000, 0x10000, 0x10582b2d );
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "f05_c08.bin",  0x00000, 0x10000, 0x01579d20 );
		ROM_LOAD( "h05_c09.bin",  0x10000, 0x10000, 0x4f5872f0 );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "n07_c12.bin",  0x00000, 0x10000, 0x10af8eb2 );
		ROM_LOAD( "k07_c10.bin",  0x10000, 0x10000, 0x9576f304 );
		ROM_LOAD( "o07_c13.bin",  0x20000, 0x10000, 0xb1d9d4dc );
		ROM_LOAD( "l07_c11.bin",  0x30000, 0x10000, 0x4598be4a );
		ROM_LOAD( "t07_c16.bin",  0x40000, 0x10000, 0xf5425e42 );
		ROM_LOAD( "p07_c14.bin",  0x50000, 0x10000, 0xcb50a17c );
		ROM_LOAD( "v07_c17.bin",  0x60000, 0x10000, 0x959ba3c7 );
		ROM_LOAD( "s07_c15.bin",  0x70000, 0x10000, 0x7f2e91c5 );
	
		ROM_REGION( 0x30000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "d01_c05.bin",  0x00000, 0x10000, 0x81b1ee5c );
		ROM_LOAD( "e01_c06.bin",  0x10000, 0x10000, 0xd0d33673 );
		ROM_LOAD( "f01_c07.bin",  0x20000, 0x10000, 0xaae81695 );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "d04_c01.bin",  0x00000, 0x10000, 0x9b85101d );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_vigilntu = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );/* 64k for code + 128k for bankswitching */
		ROM_LOAD( "a-8h",  0x00000, 0x08000, 0x8d15109e );
		ROM_LOAD( "a-8l",  0x10000, 0x10000, 0x7f95799b );
		/* 0x20000-0x2ffff empty */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound */
		ROM_LOAD( "g05_c02.bin",  0x00000, 0x10000, 0x10582b2d );
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "f05_c08.bin",  0x00000, 0x10000, 0x01579d20 );
		ROM_LOAD( "h05_c09.bin",  0x10000, 0x10000, 0x4f5872f0 );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "n07_c12.bin",  0x00000, 0x10000, 0x10af8eb2 );
		ROM_LOAD( "k07_c10.bin",  0x10000, 0x10000, 0x9576f304 );
		ROM_LOAD( "o07_c13.bin",  0x20000, 0x10000, 0xb1d9d4dc );
		ROM_LOAD( "l07_c11.bin",  0x30000, 0x10000, 0x4598be4a );
		ROM_LOAD( "t07_c16.bin",  0x40000, 0x10000, 0xf5425e42 );
		ROM_LOAD( "p07_c14.bin",  0x50000, 0x10000, 0xcb50a17c );
		ROM_LOAD( "v07_c17.bin",  0x60000, 0x10000, 0x959ba3c7 );
		ROM_LOAD( "s07_c15.bin",  0x70000, 0x10000, 0x7f2e91c5 );
	
		ROM_REGION( 0x30000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "d01_c05.bin",  0x00000, 0x10000, 0x81b1ee5c );
		ROM_LOAD( "e01_c06.bin",  0x10000, 0x10000, 0xd0d33673 );
		ROM_LOAD( "f01_c07.bin",  0x20000, 0x10000, 0xaae81695 );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "d04_c01.bin",  0x00000, 0x10000, 0x9b85101d );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_vigilntj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );/* 64k for code + 128k for bankswitching */
		ROM_LOAD( "vg_a-8h.rom",  0x00000, 0x08000, 0xba848713 );
		ROM_LOAD( "vg_a-8l.rom",  0x10000, 0x10000, 0x3b12b1d8 );
		/* 0x20000-0x2ffff empty */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound */
		ROM_LOAD( "g05_c02.bin",  0x00000, 0x10000, 0x10582b2d );
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "f05_c08.bin",  0x00000, 0x10000, 0x01579d20 );
		ROM_LOAD( "h05_c09.bin",  0x10000, 0x10000, 0x4f5872f0 );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "n07_c12.bin",  0x00000, 0x10000, 0x10af8eb2 );
		ROM_LOAD( "k07_c10.bin",  0x10000, 0x10000, 0x9576f304 );
		ROM_LOAD( "o07_c13.bin",  0x20000, 0x10000, 0xb1d9d4dc );
		ROM_LOAD( "l07_c11.bin",  0x30000, 0x10000, 0x4598be4a );
		ROM_LOAD( "t07_c16.bin",  0x40000, 0x10000, 0xf5425e42 );
		ROM_LOAD( "p07_c14.bin",  0x50000, 0x10000, 0xcb50a17c );
		ROM_LOAD( "v07_c17.bin",  0x60000, 0x10000, 0x959ba3c7 );
		ROM_LOAD( "s07_c15.bin",  0x70000, 0x10000, 0x7f2e91c5 );
	
		ROM_REGION( 0x30000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "d01_c05.bin",  0x00000, 0x10000, 0x81b1ee5c );
		ROM_LOAD( "e01_c06.bin",  0x10000, 0x10000, 0xd0d33673 );
		ROM_LOAD( "f01_c07.bin",  0x20000, 0x10000, 0xaae81695 );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "d04_c01.bin",  0x00000, 0x10000, 0x9b85101d );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_kikcubic = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );/* 64k for code + 128k for bankswitching */
		ROM_LOAD( "mqj-p0",       0x00000, 0x08000, 0x9cef394a );
		ROM_LOAD( "mqj-b0",       0x10000, 0x10000, 0xd9bcf4cd );
		ROM_LOAD( "mqj-b1",       0x20000, 0x10000, 0x54a0abe1 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound */
		ROM_LOAD( "mqj-sp",       0x00000, 0x10000, 0xbbcf3582 );
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mqj-c0",       0x00000, 0x10000, 0x975585c5 );
		ROM_LOAD( "mqj-c1",       0x10000, 0x10000, 0x49d9936d );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "mqj-00",       0x00000, 0x40000, 0x7fb0c58f );
		ROM_LOAD( "mqj-10",       0x40000, 0x40000, 0x3a189205 );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "mqj-v0",       0x00000, 0x10000, 0x54762956 );
	
		ROM_REGION( 0x0140, REGION_PROMS, 0 );
		ROM_LOAD( "8d",           0x0000, 0x0100, 0x7379bb12 );/* unknown (timing?) */
		ROM_LOAD( "6h",           0x0100, 0x0020, 0xface0cbb );/* unknown (bad read?) */
		ROM_LOAD( "7s",           0x0120, 0x0020, 0xface0cbb );/* unknown (bad read?) */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_vigilant	   = new GameDriver("1988"	,"vigilant"	,"vigilant.java"	,rom_vigilant,null	,machine_driver_vigilant	,input_ports_vigilant	,null	,ROT0	,	"Irem", "Vigilante (World)", GAME_NO_COCKTAIL )
	public static GameDriver driver_vigilntu	   = new GameDriver("1988"	,"vigilntu"	,"vigilant.java"	,rom_vigilntu,driver_vigilant	,machine_driver_vigilant	,input_ports_vigilant	,null	,ROT0	,	"Irem (Data East USA license)", "Vigilante (US)", GAME_NO_COCKTAIL )
	public static GameDriver driver_vigilntj	   = new GameDriver("1988"	,"vigilntj"	,"vigilant.java"	,rom_vigilntj,driver_vigilant	,machine_driver_vigilant	,input_ports_vigilant	,null	,ROT0	,	"Irem", "Vigilante (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_kikcubic	   = new GameDriver("1988"	,"kikcubic"	,"vigilant.java"	,rom_kikcubic,null	,machine_driver_kikcubic	,input_ports_kikcubic	,null	,ROT0	,	"Irem", "Meikyu Jima (Japan)", GAME_NO_COCKTAIL )	/* English title is Kickle Cubicle */
}
