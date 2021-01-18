/***************************************************************************

  GUNSMOKE
  ========

  Driver provided by Paul Leaman


Stephh's notes (based on the games Z80 code and some tests) :

0) all games

  - There is some code that allows you to select your starting level
    (at 0x08dc in 'gunsmoka' and at 0x08d2 in the other sets).
    To do so, once the game has booted (after the "notice" screen),
    turn the "service" mode Dip Switch ON, and change Dip Switches
    DSW 1-0 to 1-3 (which are used by coinage). You can also set
    GUNSMOKE_HACK to 1 and change the fake "Starting Level" Dip Switch.
  - About the ingame bug at the end of level 2 : enemy's energy
    (stored at 0xf790) is in fact not infinite, but it turns back to
    0xff, so when it reaches 0 again, the boss is dead.


1) 'gunsmoke'

  - World version.
    You can enter 3 chars for your initials.


2) 'gunsmokj'

  - Japan version (but English text though).
    You can enter 8 chars for your initials.


3) 'gunsmoku'

  - US version licenced to Romstar.
    You can enter 3 chars for your initials.


4) 'gunsmoku'

  - US version licenced to Romstar.
    You can enter 3 chars for your initials.
  - This is probably a later version of the game because some code
    has been added for the "Lives" Dip Switch that replaces the
    "Demonstation" one (so demonstration is always OFF).
  - Other changes :
      * Year is 1986 instead of 1985.
      * High score is 110000 instead of 100000.
      * Levels 3 and 6 are swapped.


***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class gunsmoke
{
	
	
	#define GUNSMOKE_HACK	0
	
	
	extern MACHINE_INIT( gunsmoke );
	
	extern unsigned char *gunsmoke_bg_scrollx;
	extern unsigned char *gunsmoke_bg_scrolly;
	
	PALETTE_INIT( gunsmoke );
	VIDEO_UPDATE( gunsmoke );
	VIDEO_START( gunsmoke );
	
	
	#if GUNSMOKE_HACK
	public static ReadHandlerPtr gunsmoke_input_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if ((activecpu_get_pc() == 0x0173) || (activecpu_get_pc() == 0x0181))	// to get correct coinage
			return (readinputport(4));
	
		if ((readinputport(3) & 0x80) == 0x00)	// "debug mode" ?
			return ((readinputport(4) & 0xc0) | (readinputport(5) & 0x3f));
		else
			return (readinputport(4));
	} };
	#endif
	
	
	public static ReadHandlerPtr gunsmoke_unknown_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    static int gunsmoke_fixed_data[]={ 0xff, 0x00, 0x00 };
	    /*
	    The routine at 0x0e69 tries to read data starting at 0xc4c9.
	    If this value is zero, it interprets the next two bytes as a
	    jump address.
	
	    This was resulting in a reboot which happens at the end of level 3
	    if you go too far to the right of the screen when fighting the level boss.
	
	    A non-zero for the first byte seems to be harmless  (although it may not be
	    the correct behaviour).
	
	    This could be some devious protection or it could be a bug in the
	    arcade game.  It's hard to tell without pulling the code apart.
	    */
	    return gunsmoke_fixed_data[offset];
	} };
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xc000, input_port_0_r ),
		new Memory_ReadAddress( 0xc001, 0xc001, input_port_1_r ),
		new Memory_ReadAddress( 0xc002, 0xc002, input_port_2_r ),
		new Memory_ReadAddress( 0xc003, 0xc003, input_port_3_r ),
	#if GUNSMOKE_HACK
		new Memory_ReadAddress( 0xc004, 0xc004, gunsmoke_input_r ),
	#else
		new Memory_ReadAddress( 0xc004, 0xc004, input_port_4_r ),
	#endif
		new Memory_ReadAddress( 0xc4c9, 0xc4cb, gunsmoke_unknown_r ),
		new Memory_ReadAddress( 0xd000, 0xd3ff, videoram_r ),
		new Memory_ReadAddress( 0xd400, 0xd7ff, colorram_r ),
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_RAM ), /* Work + sprite RAM */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc800, 0xc800, soundlatch_w ),
		new Memory_WriteAddress( 0xc804, 0xc804, gunsmoke_c804_w ),	/* ROM bank switch, screen flip */
		new Memory_WriteAddress( 0xc806, 0xc806, MWA_NOP ), /* Watchdog ?? */
		new Memory_WriteAddress( 0xd000, 0xd3ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xd400, 0xd7ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0xd800, 0xd801, MWA_RAM, gunsmoke_bg_scrolly ),
		new Memory_WriteAddress( 0xd802, 0xd802, MWA_RAM, gunsmoke_bg_scrollx ),
		new Memory_WriteAddress( 0xd806, 0xd806, gunsmoke_d806_w ),	/* sprites and bg enable */
		new Memory_WriteAddress( 0xe000, 0xefff, MWA_RAM ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xc800, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe000, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0xe002, 0xe002, YM2203_control_port_1_w ),
		new Memory_WriteAddress( 0xe003, 0xe003, YM2203_write_port_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_gunsmoke = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x01, "30k, 80k then every 80k" );
		PORT_DIPSETTING(    0x03, "30k, 100k then every 100k" );
		PORT_DIPSETTING(    0x00, "30k, 100k then every 150k" );
		PORT_DIPSETTING(    0x02, "30k and 100K only");
		PORT_DIPNAME( 0x04, 0x04, "Demonstration" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x20, "Easy" );
		PORT_DIPSETTING(    0x30, "Normal" );
		PORT_DIPSETTING(    0x10, "Difficult" );
		PORT_DIPSETTING(    0x00, "Very Difficult" );
		PORT_DIPNAME( 0x40, 0x40, "Freeze" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );			// Also "debug mode"
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x38, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* Fake DSW */
		PORT_DIPNAME( 0x0f, 0x0f, "Starting Level" );
		PORT_DIPSETTING(    0x0f, "Demonstration" );
		PORT_DIPSETTING(    0x0e, "Level 1" );
		PORT_DIPSETTING(    0x0d, "Level 2" );
		PORT_DIPSETTING(    0x0c, "Level 3" );
		PORT_DIPSETTING(    0x0b, "Level 4" );
		PORT_DIPSETTING(    0x0a, "Level 5" );
		PORT_DIPSETTING(    0x09, "Level 6" );
		PORT_DIPSETTING(    0x08, "Level 7" );
		PORT_DIPSETTING(    0x07, "Level 8" );
		PORT_DIPSETTING(    0x06, "Level 9" );
		PORT_DIPSETTING(    0x05, "Level 10" );
		PORT_DIPSETTING(    0x04, "Ending message" );
	//	PORT_DIPSETTING(    0x03, "Demonstration" );
	//	PORT_DIPSETTING(    0x02, "Demonstration" );
	//	PORT_DIPSETTING(    0x01, "Demonstration" );
	//	PORT_DIPSETTING(    0x00, "Invalid Level" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x10, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	/* Same as 'gunsmoke', but "Lives" Dip Switch instead of "Demonstration" Dip Switch */
	/* And swapped starting levels 3 and 6 in the fake Dip Switch */
	static InputPortPtr input_ports_gunsmoka = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x01, "30k, 80k then every 80k" );
		PORT_DIPSETTING(    0x03, "30k, 100k then every 100k" );
		PORT_DIPSETTING(    0x00, "30k, 100k then every 150k" );
		PORT_DIPSETTING(    0x02, "30k and 100K only");
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x20, "Easy" );
		PORT_DIPSETTING(    0x30, "Normal" );
		PORT_DIPSETTING(    0x10, "Difficult" );
		PORT_DIPSETTING(    0x00, "Very Difficult" );
		PORT_DIPNAME( 0x40, 0x40, "Freeze" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );			// Also "debug mode"
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x38, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* Fake DSW */
		PORT_DIPNAME( 0x0f, 0x0f, "Starting Level" );
		PORT_DIPSETTING(    0x0f, "Demonstration" );
		PORT_DIPSETTING(    0x0e, "Level 1" );
		PORT_DIPSETTING(    0x0d, "Level 2" );
		PORT_DIPSETTING(    0x09, "Level 3" );
		PORT_DIPSETTING(    0x0b, "Level 4" );
		PORT_DIPSETTING(    0x0a, "Level 5" );
		PORT_DIPSETTING(    0x0c, "Level 6" );
		PORT_DIPSETTING(    0x08, "Level 7" );
		PORT_DIPSETTING(    0x07, "Level 8" );
		PORT_DIPSETTING(    0x06, "Level 9" );
		PORT_DIPSETTING(    0x05, "Level 10" );
		PORT_DIPSETTING(    0x04, "Ending message" );
	//	PORT_DIPSETTING(    0x03, "Demonstration" );
	//	PORT_DIPSETTING(    0x02, "Demonstration" );
	//	PORT_DIPSETTING(    0x01, "Demonstration" );
	//	PORT_DIPSETTING(    0x00, "Invalid Level" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x10, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		2,	/* 2 bits per pixel */
		new int[] { 4, 0 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8	/* every char takes 16 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		2048,	/* 2048 sprites */
		4,      /* 4 bits per pixel */
		new int[] { 2048*64*8+4, 2048*64*8+0, 4, 0 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3,
				32*8+0, 32*8+1, 32*8+2, 32*8+3, 33*8+0, 33*8+1, 33*8+2, 33*8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout tilelayout = new GfxLayout
	(
		32,32,  /* 32*32 tiles */
		512,    /* 512 tiles */
		4,      /* 4 bits per pixel */
		new int[] { 512*256*8+4, 512*256*8+0, 4, 0 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3,
				64*8+0, 64*8+1, 64*8+2, 64*8+3, 65*8+0, 65*8+1, 65*8+2, 65*8+3,
				128*8+0, 128*8+1, 128*8+2, 128*8+3, 129*8+0, 129*8+1, 129*8+2, 129*8+3,
				192*8+0, 192*8+1, 192*8+2, 192*8+3, 193*8+0, 193*8+1, 193*8+2, 193*8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16,
				16*16, 17*16, 18*16, 19*16, 20*16, 21*16, 22*16, 23*16,
				24*16, 25*16, 26*16, 27*16, 28*16, 29*16, 30*16, 31*16 },
		256*8	/* every tile takes 256 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,            0, 32 ),
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout,         32*4, 16 ), /* Tiles */
		new GfxDecodeInfo( REGION_GFX3, 0, spritelayout, 32*4+16*16, 16 ), /* Sprites */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static struct YM2203interface ym2203_interface =
	{
		2,			/* 2 chips */
		1500000,	/* 1.5 MHz (?) */
		{ YM2203_VOL(14,22), YM2203_VOL(14,22) },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 }
	};
	
	
	
	static MACHINE_DRIVER_START( gunsmoke )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)        /* 4 MHz (?) */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 3000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3 MHz (?) */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,4)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
		MDRV_COLORTABLE_LENGTH(32*4+16*16+16*16)
	
		MDRV_PALETTE_INIT(gunsmoke)
		MDRV_VIDEO_START(gunsmoke)
		MDRV_VIDEO_UPDATE(gunsmoke)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_gunsmoke = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );    /* 2*64k for code */
		ROM_LOAD( "09n_gs03.bin", 0x00000, 0x8000, 0x40a06cef );/* Code 0000-7fff */
		ROM_LOAD( "10n_gs04.bin", 0x10000, 0x8000, 0x8d4b423f );/* Paged code */
		ROM_LOAD( "12n_gs05.bin", 0x18000, 0x8000, 0x2b5667fb );/* Paged code */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "14h_gs02.bin", 0x00000, 0x8000, 0xcd7a2c38 );
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "11f_gs01.bin", 0x00000, 0x4000, 0xb61ece9b );/* Characters */
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "06c_gs13.bin", 0x00000, 0x8000, 0xf6769fc5 );/* 32x32 tiles planes 2-3 */
		ROM_LOAD( "05c_gs12.bin", 0x08000, 0x8000, 0xd997b78c );
		ROM_LOAD( "04c_gs11.bin", 0x10000, 0x8000, 0x125ba58e );
		ROM_LOAD( "02c_gs10.bin", 0x18000, 0x8000, 0xf469c13c );
		ROM_LOAD( "06a_gs09.bin", 0x20000, 0x8000, 0x539f182d );/* 32x32 tiles planes 0-1 */
		ROM_LOAD( "05a_gs08.bin", 0x28000, 0x8000, 0xe87e526d );
		ROM_LOAD( "04a_gs07.bin", 0x30000, 0x8000, 0x4382c0d2 );
		ROM_LOAD( "02a_gs06.bin", 0x38000, 0x8000, 0x4cafe7a6 );
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "06n_gs22.bin", 0x00000, 0x8000, 0xdc9c508c );/* Sprites planes 2-3 */
		ROM_LOAD( "04n_gs21.bin", 0x08000, 0x8000, 0x68883749 );/* Sprites planes 2-3 */
		ROM_LOAD( "03n_gs20.bin", 0x10000, 0x8000, 0x0be932ed );/* Sprites planes 2-3 */
		ROM_LOAD( "01n_gs19.bin", 0x18000, 0x8000, 0x63072f93 );/* Sprites planes 2-3 */
		ROM_LOAD( "06l_gs18.bin", 0x20000, 0x8000, 0xf69a3c7c );/* Sprites planes 0-1 */
		ROM_LOAD( "04l_gs17.bin", 0x28000, 0x8000, 0x4e98562a );/* Sprites planes 0-1 */
		ROM_LOAD( "03l_gs16.bin", 0x30000, 0x8000, 0x0d99c3b3 );/* Sprites planes 0-1 */
		ROM_LOAD( "01l_gs15.bin", 0x38000, 0x8000, 0x7f14270e );/* Sprites planes 0-1 */
	
		ROM_REGION( 0x8000, REGION_GFX4, 0 );/* background tilemaps */
		ROM_LOAD( "11c_gs14.bin", 0x00000, 0x8000, 0x0af4f7eb );
	
		ROM_REGION( 0x0a00, REGION_PROMS, 0 );
		ROM_LOAD( "03b_g-01.bin", 0x0000, 0x0100, 0x02f55589 );/* red component */
		ROM_LOAD( "04b_g-02.bin", 0x0100, 0x0100, 0xe1e36dd9 );/* green component */
		ROM_LOAD( "05b_g-03.bin", 0x0200, 0x0100, 0x989399c0 );/* blue component */
		ROM_LOAD( "09d_g-04.bin", 0x0300, 0x0100, 0x906612b5 );/* char lookup table */
		ROM_LOAD( "14a_g-06.bin", 0x0400, 0x0100, 0x4a9da18b );/* tile lookup table */
		ROM_LOAD( "15a_g-07.bin", 0x0500, 0x0100, 0xcb9394fc );/* tile palette bank */
		ROM_LOAD( "09f_g-09.bin", 0x0600, 0x0100, 0x3cee181e );/* sprite lookup table */
		ROM_LOAD( "08f_g-08.bin", 0x0700, 0x0100, 0xef91cdd2 );/* sprite palette bank */
		ROM_LOAD( "02j_g-10.bin", 0x0800, 0x0100, 0x0eaf5158 );/* video timing (not used) */
		ROM_LOAD( "01f_g-05.bin", 0x0900, 0x0100, 0x25c90c2a );/* priority? (not used) */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gunsmokj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );    /* 2*64k for code */
		ROM_LOAD( "gs03_9n.rom",  0x00000, 0x8000, 0xb56b5df6 );/* Code 0000-7fff */
		ROM_LOAD( "10n_gs04.bin", 0x10000, 0x8000, 0x8d4b423f );/* Paged code */
		ROM_LOAD( "12n_gs05.bin", 0x18000, 0x8000, 0x2b5667fb );/* Paged code */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "14h_gs02.bin", 0x00000, 0x8000, 0xcd7a2c38 );
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "11f_gs01.bin", 0x00000, 0x4000, 0xb61ece9b );/* Characters */
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "06c_gs13.bin", 0x00000, 0x8000, 0xf6769fc5 );/* 32x32 tiles planes 2-3 */
		ROM_LOAD( "05c_gs12.bin", 0x08000, 0x8000, 0xd997b78c );
		ROM_LOAD( "04c_gs11.bin", 0x10000, 0x8000, 0x125ba58e );
		ROM_LOAD( "02c_gs10.bin", 0x18000, 0x8000, 0xf469c13c );
		ROM_LOAD( "06a_gs09.bin", 0x20000, 0x8000, 0x539f182d );/* 32x32 tiles planes 0-1 */
		ROM_LOAD( "05a_gs08.bin", 0x28000, 0x8000, 0xe87e526d );
		ROM_LOAD( "04a_gs07.bin", 0x30000, 0x8000, 0x4382c0d2 );
		ROM_LOAD( "02a_gs06.bin", 0x38000, 0x8000, 0x4cafe7a6 );
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "06n_gs22.bin", 0x00000, 0x8000, 0xdc9c508c );/* Sprites planes 2-3 */
		ROM_LOAD( "04n_gs21.bin", 0x08000, 0x8000, 0x68883749 );/* Sprites planes 2-3 */
		ROM_LOAD( "03n_gs20.bin", 0x10000, 0x8000, 0x0be932ed );/* Sprites planes 2-3 */
		ROM_LOAD( "01n_gs19.bin", 0x18000, 0x8000, 0x63072f93 );/* Sprites planes 2-3 */
		ROM_LOAD( "06l_gs18.bin", 0x20000, 0x8000, 0xf69a3c7c );/* Sprites planes 0-1 */
		ROM_LOAD( "04l_gs17.bin", 0x28000, 0x8000, 0x4e98562a );/* Sprites planes 0-1 */
		ROM_LOAD( "03l_gs16.bin", 0x30000, 0x8000, 0x0d99c3b3 );/* Sprites planes 0-1 */
		ROM_LOAD( "01l_gs15.bin", 0x38000, 0x8000, 0x7f14270e );/* Sprites planes 0-1 */
	
		ROM_REGION( 0x8000, REGION_GFX4, 0 );/* background tilemaps */
		ROM_LOAD( "11c_gs14.bin", 0x00000, 0x8000, 0x0af4f7eb );
	
		ROM_REGION( 0x0a00, REGION_PROMS, 0 );
		ROM_LOAD( "03b_g-01.bin", 0x0000, 0x0100, 0x02f55589 );/* red component */
		ROM_LOAD( "04b_g-02.bin", 0x0100, 0x0100, 0xe1e36dd9 );/* green component */
		ROM_LOAD( "05b_g-03.bin", 0x0200, 0x0100, 0x989399c0 );/* blue component */
		ROM_LOAD( "09d_g-04.bin", 0x0300, 0x0100, 0x906612b5 );/* char lookup table */
		ROM_LOAD( "14a_g-06.bin", 0x0400, 0x0100, 0x4a9da18b );/* tile lookup table */
		ROM_LOAD( "15a_g-07.bin", 0x0500, 0x0100, 0xcb9394fc );/* tile palette bank */
		ROM_LOAD( "09f_g-09.bin", 0x0600, 0x0100, 0x3cee181e );/* sprite lookup table */
		ROM_LOAD( "08f_g-08.bin", 0x0700, 0x0100, 0xef91cdd2 );/* sprite palette bank */
		ROM_LOAD( "02j_g-10.bin", 0x0800, 0x0100, 0x0eaf5158 );/* video timing (not used) */
		ROM_LOAD( "01f_g-05.bin", 0x0900, 0x0100, 0x25c90c2a );/* priority? (not used) */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gunsmoku = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );    /* 2*64k for code */
		ROM_LOAD( "9n_gs03.bin",  0x00000, 0x8000, 0x592f211b );/* Code 0000-7fff */
		ROM_LOAD( "10n_gs04.bin", 0x10000, 0x8000, 0x8d4b423f );/* Paged code */
		ROM_LOAD( "12n_gs05.bin", 0x18000, 0x8000, 0x2b5667fb );/* Paged code */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "14h_gs02.bin", 0x00000, 0x8000, 0xcd7a2c38 );
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "11f_gs01.bin", 0x00000, 0x4000, 0xb61ece9b );/* Characters */
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "06c_gs13.bin", 0x00000, 0x8000, 0xf6769fc5 );/* 32x32 tiles planes 2-3 */
		ROM_LOAD( "05c_gs12.bin", 0x08000, 0x8000, 0xd997b78c );
		ROM_LOAD( "04c_gs11.bin", 0x10000, 0x8000, 0x125ba58e );
		ROM_LOAD( "02c_gs10.bin", 0x18000, 0x8000, 0xf469c13c );
		ROM_LOAD( "06a_gs09.bin", 0x20000, 0x8000, 0x539f182d );/* 32x32 tiles planes 0-1 */
		ROM_LOAD( "05a_gs08.bin", 0x28000, 0x8000, 0xe87e526d );
		ROM_LOAD( "04a_gs07.bin", 0x30000, 0x8000, 0x4382c0d2 );
		ROM_LOAD( "02a_gs06.bin", 0x38000, 0x8000, 0x4cafe7a6 );
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "06n_gs22.bin", 0x00000, 0x8000, 0xdc9c508c );/* Sprites planes 2-3 */
		ROM_LOAD( "04n_gs21.bin", 0x08000, 0x8000, 0x68883749 );/* Sprites planes 2-3 */
		ROM_LOAD( "03n_gs20.bin", 0x10000, 0x8000, 0x0be932ed );/* Sprites planes 2-3 */
		ROM_LOAD( "01n_gs19.bin", 0x18000, 0x8000, 0x63072f93 );/* Sprites planes 2-3 */
		ROM_LOAD( "06l_gs18.bin", 0x20000, 0x8000, 0xf69a3c7c );/* Sprites planes 0-1 */
		ROM_LOAD( "04l_gs17.bin", 0x28000, 0x8000, 0x4e98562a );/* Sprites planes 0-1 */
		ROM_LOAD( "03l_gs16.bin", 0x30000, 0x8000, 0x0d99c3b3 );/* Sprites planes 0-1 */
		ROM_LOAD( "01l_gs15.bin", 0x38000, 0x8000, 0x7f14270e );/* Sprites planes 0-1 */
	
		ROM_REGION( 0x8000, REGION_GFX4, 0 );/* background tilemaps */
		ROM_LOAD( "11c_gs14.bin", 0x00000, 0x8000, 0x0af4f7eb );
	
		ROM_REGION( 0x0a00, REGION_PROMS, 0 );
		ROM_LOAD( "03b_g-01.bin", 0x0000, 0x0100, 0x02f55589 );/* red component */
		ROM_LOAD( "04b_g-02.bin", 0x0100, 0x0100, 0xe1e36dd9 );/* green component */
		ROM_LOAD( "05b_g-03.bin", 0x0200, 0x0100, 0x989399c0 );/* blue component */
		ROM_LOAD( "09d_g-04.bin", 0x0300, 0x0100, 0x906612b5 );/* char lookup table */
		ROM_LOAD( "14a_g-06.bin", 0x0400, 0x0100, 0x4a9da18b );/* tile lookup table */
		ROM_LOAD( "15a_g-07.bin", 0x0500, 0x0100, 0xcb9394fc );/* tile palette bank */
		ROM_LOAD( "09f_g-09.bin", 0x0600, 0x0100, 0x3cee181e );/* sprite lookup table */
		ROM_LOAD( "08f_g-08.bin", 0x0700, 0x0100, 0xef91cdd2 );/* sprite palette bank */
		ROM_LOAD( "02j_g-10.bin", 0x0800, 0x0100, 0x0eaf5158 );/* video timing (not used) */
		ROM_LOAD( "01f_g-05.bin", 0x0900, 0x0100, 0x25c90c2a );/* priority? (not used) */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gunsmoka = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );    /* 2*64k for code */
		ROM_LOAD( "gs03.9n",      0x00000, 0x8000, 0x51dc3f76 );/* Code 0000-7fff */
		ROM_LOAD( "gs04.10n",     0x10000, 0x8000, 0x5ecf31b8 );/* Paged code */
		ROM_LOAD( "gs05.12n",     0x18000, 0x8000, 0x1c9aca13 );/* Paged code */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "14h_gs02.bin", 0x00000, 0x8000, 0xcd7a2c38 );
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "11f_gs01.bin", 0x00000, 0x4000, 0xb61ece9b );/* Characters */
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "06c_gs13.bin", 0x00000, 0x8000, 0xf6769fc5 );/* 32x32 tiles planes 2-3 */
		ROM_LOAD( "05c_gs12.bin", 0x08000, 0x8000, 0xd997b78c );
		ROM_LOAD( "04c_gs11.bin", 0x10000, 0x8000, 0x125ba58e );
		ROM_LOAD( "02c_gs10.bin", 0x18000, 0x8000, 0xf469c13c );
		ROM_LOAD( "06a_gs09.bin", 0x20000, 0x8000, 0x539f182d );/* 32x32 tiles planes 0-1 */
		ROM_LOAD( "05a_gs08.bin", 0x28000, 0x8000, 0xe87e526d );
		ROM_LOAD( "04a_gs07.bin", 0x30000, 0x8000, 0x4382c0d2 );
		ROM_LOAD( "02a_gs06.bin", 0x38000, 0x8000, 0x4cafe7a6 );
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "06n_gs22.bin", 0x00000, 0x8000, 0xdc9c508c );/* Sprites planes 2-3 */
		ROM_LOAD( "04n_gs21.bin", 0x08000, 0x8000, 0x68883749 );/* Sprites planes 2-3 */
		ROM_LOAD( "03n_gs20.bin", 0x10000, 0x8000, 0x0be932ed );/* Sprites planes 2-3 */
		ROM_LOAD( "01n_gs19.bin", 0x18000, 0x8000, 0x63072f93 );/* Sprites planes 2-3 */
		ROM_LOAD( "06l_gs18.bin", 0x20000, 0x8000, 0xf69a3c7c );/* Sprites planes 0-1 */
		ROM_LOAD( "04l_gs17.bin", 0x28000, 0x8000, 0x4e98562a );/* Sprites planes 0-1 */
		ROM_LOAD( "03l_gs16.bin", 0x30000, 0x8000, 0x0d99c3b3 );/* Sprites planes 0-1 */
		ROM_LOAD( "01l_gs15.bin", 0x38000, 0x8000, 0x7f14270e );/* Sprites planes 0-1 */
	
		ROM_REGION( 0x8000, REGION_GFX4, 0 );/* background tilemaps */
		ROM_LOAD( "11c_gs14.bin", 0x00000, 0x8000, 0x0af4f7eb );
	
		ROM_REGION( 0x0a00, REGION_PROMS, 0 );
		ROM_LOAD( "03b_g-01.bin", 0x0000, 0x0100, 0x02f55589 );/* red component */
		ROM_LOAD( "04b_g-02.bin", 0x0100, 0x0100, 0xe1e36dd9 );/* green component */
		ROM_LOAD( "05b_g-03.bin", 0x0200, 0x0100, 0x989399c0 );/* blue component */
		ROM_LOAD( "09d_g-04.bin", 0x0300, 0x0100, 0x906612b5 );/* char lookup table */
		ROM_LOAD( "14a_g-06.bin", 0x0400, 0x0100, 0x4a9da18b );/* tile lookup table */
		ROM_LOAD( "15a_g-07.bin", 0x0500, 0x0100, 0xcb9394fc );/* tile palette bank */
		ROM_LOAD( "09f_g-09.bin", 0x0600, 0x0100, 0x3cee181e );/* sprite lookup table */
		ROM_LOAD( "08f_g-08.bin", 0x0700, 0x0100, 0xef91cdd2 );/* sprite palette bank */
		ROM_LOAD( "02j_g-10.bin", 0x0800, 0x0100, 0x0eaf5158 );/* video timing (not used) */
		ROM_LOAD( "01f_g-05.bin", 0x0900, 0x0100, 0x25c90c2a );/* priority? (not used) */
	ROM_END(); }}; 
	
	
	public static GameDriver driver_gunsmoke	   = new GameDriver("1985"	,"gunsmoke"	,"gunsmoke.java"	,rom_gunsmoke,null	,machine_driver_gunsmoke	,input_ports_gunsmoke	,null	,ROT270	,	"Capcom", "Gun.Smoke (World)" )
	public static GameDriver driver_gunsmokj	   = new GameDriver("1985"	,"gunsmokj"	,"gunsmoke.java"	,rom_gunsmokj,driver_gunsmoke	,machine_driver_gunsmoke	,input_ports_gunsmoke	,null	,ROT270	,	"Capcom", "Gun.Smoke (Japan)" )
	public static GameDriver driver_gunsmoku	   = new GameDriver("1985"	,"gunsmoku"	,"gunsmoke.java"	,rom_gunsmoku,driver_gunsmoke	,machine_driver_gunsmoke	,input_ports_gunsmoke	,null	,ROT270	,	"Capcom (Romstar license)", "Gun.Smoke (US set 1)" )
	public static GameDriver driver_gunsmoka	   = new GameDriver("1986"	,"gunsmoka"	,"gunsmoke.java"	,rom_gunsmoka,driver_gunsmoke	,machine_driver_gunsmoke	,input_ports_gunsmoka	,null	,ROT270	,	"Capcom (Romstar license)", "Gun.Smoke (US set 2)" )
}
