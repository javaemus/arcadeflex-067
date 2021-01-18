/***************************************************************************

	Atari Asteroids hardware

	Games supported:
		* Asteroids
		* Asteroids Deluxe
		* Lunar Lander

	Known bugs:
		* sound emu isn't perfect - sometimes explosions don't register in Asteroids
		* the low background thrust in Lunar Lander isn't emulated
		* the ERROR message in Asteroids Deluxe self test is related to a pokey problem

****************************************************************************

	Asteroids Memory Map (preliminary)

	Asteroids settings:

	0 = OFF  1 = ON  X = Don't Care  $ = Atari suggests


	8 SWITCH DIP
	87654321
	--------
	XXXXXX11   English
	XXXXXX10   German
	XXXXXX01   French
	XXXXXX00   Spanish
	XXXXX1XX   4-ship game
	XXXXX0XX   3-ship game
	11XXXXXX   Free Play
	10XXXXXX   1 Coin  for 2 Plays
	01XXXXXX   1 Coin  for 1 Play
	00XXXXXX   2 Coins for 1 Play

	Asteroids Deluxe settings:

	0 = OFF  1 = ON  X = Don't Care  $ = Atari suggests


	8 SWITCH DIP (R5)
	87654321
	--------
	XXXXXX11   English $
	XXXXXX10   German
	XXXXXX01   French
	XXXXXX00   Spanish
	XXXX11XX   2-4 ships
	XXXX10XX   3-5 ships $
	XXXX01XX   4-6 ships
	XXXX00XX   5-7 ships
	XXX1XXXX   1-play minimum $
	XXX0XXXX   2-play minimum
	XX1XXXXX   Easier gameplay for first 30000 points +
	XX0XXXXX   Hard gameplay throughout the game	  +
	11XXXXXX   Bonus ship every 10,000 points $ !
	10XXXXXX   Bonus ship every 12,000 points !
	01XXXXXX   Bonus ship every 15,000 points !
	00XXXXXX   No bonus ships (adds one ship at game start)

	+ only with the newer romset
	! not "every", but "at", e.g. only once.

	Thanks to Gregg Woodcock for the info.

	8 SWITCH DIP (L8)
	87654321
	--------
	XXXXXX11   Free Play
	XXXXXX10   1 Coin = 2 Plays
	XXXXXX01   1 Coin = 1 Play
	XXXXXX00   2 Coins = 1 Play $
	XXXX11XX   Right coin mech * 1 $
	XXXX10XX   Right coin mech * 4
	XXXX01XX   Right coin mech * 5
	XXXX00XX   Right coin mech * 6
	XXX1XXXX   Center coin mech * 1 $
	XXX0XXXX   Center coin mech * 2
	111XXXXX   No bonus coins
	110XXXXX   For every 2 coins inserted, game logic adds 1 more coin
	101XXXXX   For every 4 coins inserted, game logic adds 1 more coin
	100XXXXX   For every 4 coins inserted, game logic adds 2 more coins $
	011XXXXX   For every 5 coins inserted, game logic adds 1 more coin

****************************************************************************

	Lunar Lander Memory Map (preliminary)

	Lunar Lander settings:

	0 = OFF  1 = ON  x = Don't Care  $ = Atari suggests


	8 SWITCH DIP (P8) with -01 ROMs on PCB
	87654321
	--------
	11xxxxxx   450 fuel units per coin
	10xxxxxx   600 fuel units per coin
	01xxxxxx   750 fuel units per coin	$
	00xxxxxx   900 fuel units per coin
	xxx0xxxx   Free play
	xxx1xxxx   Coined play as determined by toggles 7 & 8  $
	xxxx00xx   German instructions
	xxxx01xx   Spanish instructions
	xxxx10xx   French instructions
	xxxx11xx   English instructions  $
	xxxxxx11   Right coin == 1 credit/coin	$
	xxxxxx10   Right coin == 4 credit/coin
	xxxxxx01   Right coin == 5 credit/coin
	xxxxxx00   Right coin == 6 credit/coin
			   (Left coin always registers 1 credit/coin)


	8 SWITCH DIP (P8) with -02 ROMs on PCB
	87654321
	--------
	11x1xxxx   450 fuel units per coin
	10x1xxxx   600 fuel units per coin
	01x1xxxx   750 fuel units per coin	$
	00x1xxxx   900 fuel units per coin
	11x0xxxx   1100 fuel units per coin
	10x0xxxx   1300 fuel units per coin
	01x0xxxx   1550 fuel units per coin
	00x0xxxx   1800 fuel units per coin
	xx0xxxxx   Free play
	xx1xxxxx   Coined play as determined by toggles 5, 7, & 8  $
	xxxx00xx   German instructions
	xxxx01xx   Spanish instructions
	xxxx10xx   French instructions
	xxxx11xx   English instructions  $
	xxxxxx11   Right coin == 1 credit/coin	$
	xxxxxx10   Right coin == 4 credit/coin
	xxxxxx01   Right coin == 5 credit/coin
	xxxxxx00   Right coin == 6 credit/coin
			   (Left coin always registers 1 credit/coin)

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class asteroid
{
	
	
	
	/*************************************
	 *
	 *	Coin counters
	 *
	 *************************************/
	
	public static WriteHandlerPtr astdelux_coin_counter_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_counter_w(offset,data);
	} };
	
	
	
	/*************************************
	 *
	 *	Land Lander LEDs/lamps
	 *
	 *************************************/
	
	public static WriteHandlerPtr llander_led_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		static const char *lampname[] =
		{
			"lamp0", "lamp1", "lamp2", "lamp3", "lamp4"
		};
	    int i;
	
	    for (i = 0; i < 5; i++)
			artwork_show(lampname[i], (data >> (4 - i)) & 1);
	} };
	
	
	
	
	/*************************************
	 *
	 *	Lunar Lander mirrors page 0 & 1
	 *
	 *************************************/
	
	static data8_t *llander_zeropage;
	
	public static ReadHandlerPtr llander_zeropage_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return llander_zeropage[offset & 0xff];
	} };
	
	
	public static WriteHandlerPtr llander_zeropage_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		llander_zeropage[offset & 0xff] = data;
	} };
	
	
	
	/*************************************
	 *
	 *	Main CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress asteroid_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x03ff, MRA_RAM ),
		new Memory_ReadAddress( 0x2000, 0x2007, asteroid_IN0_r ), /* IN0 */
		new Memory_ReadAddress( 0x2400, 0x2407, asteroid_IN1_r ), /* IN1 */
		new Memory_ReadAddress( 0x2800, 0x2803, asteroid_DSW1_r ), /* DSW1 */
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x57ff, MRA_ROM ), /* vector rom */
		new Memory_ReadAddress( 0x6800, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_ROM ), /* for the reset / interrupt vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress asteroid_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x3000, 0x3000, avgdvg_go_w ),
		new Memory_WriteAddress( 0x3200, 0x3200, asteroid_bank_switch_w ),
		new Memory_WriteAddress( 0x3400, 0x3400, watchdog_reset_w ),
		new Memory_WriteAddress( 0x3600, 0x3600, asteroid_explode_w ),
		new Memory_WriteAddress( 0x3a00, 0x3a00, asteroid_thump_w ),
		new Memory_WriteAddress( 0x3c00, 0x3c05, asteroid_sounds_w ),
		new Memory_WriteAddress( 0x3e00, 0x3e00, asteroid_noise_reset_w ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM, vectorram, vectorram_size ),
		new Memory_WriteAddress( 0x5000, 0x57ff, MWA_ROM ), /* vector rom */
		new Memory_WriteAddress( 0x6800, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress astdelux_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x03ff, MRA_RAM ),
		new Memory_ReadAddress( 0x2000, 0x2007, asteroid_IN0_r ), /* IN0 */
		new Memory_ReadAddress( 0x2400, 0x2407, asteroid_IN1_r ), /* IN1 */
		new Memory_ReadAddress( 0x2800, 0x2803, asteroid_DSW1_r ), /* DSW1 */
		new Memory_ReadAddress( 0x2c00, 0x2c0f, pokey1_r ),
		new Memory_ReadAddress( 0x2c40, 0x2c7f, atari_vg_earom_r ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4800, 0x57ff, MRA_ROM ), /* vector rom */
		new Memory_ReadAddress( 0x6000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_ROM ), /* for the reset / interrupt vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress astdelux_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x2c00, 0x2c0f, pokey1_w ),
		new Memory_WriteAddress( 0x3000, 0x3000, avgdvg_go_w ),
		new Memory_WriteAddress( 0x3200, 0x323f, atari_vg_earom_w ),
		new Memory_WriteAddress( 0x3400, 0x3400, watchdog_reset_w ),
		new Memory_WriteAddress( 0x3600, 0x3600, asteroid_explode_w ),
		new Memory_WriteAddress( 0x3a00, 0x3a00, atari_vg_earom_ctrl_w ),
		new Memory_WriteAddress( 0x3c00, 0x3c01, astdelux_led_w ),
		new Memory_WriteAddress( 0x3c03, 0x3c03, astdelux_sounds_w ),
		new Memory_WriteAddress( 0x3c04, 0x3c04, astdelux_bank_switch_w ),
		new Memory_WriteAddress( 0x3c05, 0x3c07, astdelux_coin_counter_w ),
		new Memory_WriteAddress( 0x3e00, 0x3e00, asteroid_noise_reset_w ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM, vectorram, vectorram_size ),
		new Memory_WriteAddress( 0x4800, 0x57ff, MWA_ROM ), /* vector rom */
		new Memory_WriteAddress( 0x6000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress llander_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x01ff, llander_zeropage_r ),
		new Memory_ReadAddress( 0x2000, 0x2000, llander_IN0_r ), /* IN0 */
		new Memory_ReadAddress( 0x2400, 0x2407, asteroid_IN1_r ), /* IN1 */
		new Memory_ReadAddress( 0x2800, 0x2803, asteroid_DSW1_r ), /* DSW1 */
		new Memory_ReadAddress( 0x2c00, 0x2c00, input_port_3_r ), /* IN3 */
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4800, 0x5fff, MRA_ROM ), /* vector rom */
		new Memory_ReadAddress( 0x6000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_ROM ), /* for the reset / interrupt vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress llander_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x01ff, llander_zeropage_w, llander_zeropage ),
		new Memory_WriteAddress( 0x3000, 0x3000, avgdvg_go_w ),
		new Memory_WriteAddress( 0x3200, 0x3200, llander_led_w ),
		new Memory_WriteAddress( 0x3400, 0x3400, watchdog_reset_w ),
		new Memory_WriteAddress( 0x3c00, 0x3c00, llander_sounds_w ),
		new Memory_WriteAddress( 0x3e00, 0x3e00, llander_snd_reset_w ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM, vectorram, vectorram_size ),
		new Memory_WriteAddress( 0x4800, 0x5fff, MWA_ROM ), /* vector rom */
		new Memory_WriteAddress( 0x6000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	static InputPortPtr input_ports_asteroid = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		/* Bit 2 and 3 are handled in the machine dependent part. */
			/* Bit 2 is the 3 KHz source and Bit 3 the VG_HALT bit	  */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON3 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BITX(0x20, IP_ACTIVE_HIGH, IPT_SERVICE, "Diagnostic Step", KEYCODE_F1, IP_JOY_NONE );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	
		PORT_START();  /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, "Language" );
		PORT_DIPSETTING (	0x00, "English" );
		PORT_DIPSETTING (	0x01, "German" );
		PORT_DIPSETTING (	0x02, "French" );
		PORT_DIPSETTING (	0x03, "Spanish" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING (	0x04, "3" );
		PORT_DIPSETTING (	0x00, "4" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING (	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING (	0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING (	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING (	0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING (	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING (	0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x80, DEF_STR( "Coinage") );
		PORT_DIPSETTING (	0xc0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING (	0x80, DEF_STR( "1C_1C") );
		PORT_DIPSETTING (	0x40, DEF_STR( "1C_2C") );
		PORT_DIPSETTING (	0x00, DEF_STR( "Free_Play") );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_asteroib = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );/* resets */
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );/* resets */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		/* Bit 7 is VG_HALT, handled in the machine dependant part */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();  /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, "Language" );
		PORT_DIPSETTING (	0x00, "English" );
		PORT_DIPSETTING (	0x01, "German" );
		PORT_DIPSETTING (	0x02, "French" );
		PORT_DIPSETTING (	0x03, "Spanish" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING (	0x04, "3" );
		PORT_DIPSETTING (	0x00, "4" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING (	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING (	0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING (	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING (	0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING (	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING (	0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x80, DEF_STR( "Coinage") );
		PORT_DIPSETTING (	0xc0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING (	0x80, DEF_STR( "1C_1C") );
		PORT_DIPSETTING (	0x40, DEF_STR( "1C_2C") );
		PORT_DIPSETTING (	0x00, DEF_STR( "Free_Play") );
	
		PORT_START();  /* hyperspace */
		PORT_BIT( 0x7f, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON3 );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_astdelux = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		/* Bit 2 and 3 are handled in the machine dependent part. */
		/* Bit 2 is the 3 KHz source and Bit 3 the VG_HALT bit	  */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON3 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_SERVICE, "Diagnostic Step", KEYCODE_F1, IP_JOY_NONE );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	
		PORT_START();  /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
	
		PORT_START();  /* DSW 1 */
		PORT_DIPNAME( 0x03, 0x00, "Language" );
		PORT_DIPSETTING (	0x00, "English" );
		PORT_DIPSETTING (	0x01, "German" );
		PORT_DIPSETTING (	0x02, "French" );
		PORT_DIPSETTING (	0x03, "Spanish" );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING (	0x00, "2-4" );
		PORT_DIPSETTING (	0x04, "3-5" );
		PORT_DIPSETTING (	0x08, "4-6" );
		PORT_DIPSETTING (	0x0c, "5-7" );
		PORT_DIPNAME( 0x10, 0x00, "Minimum plays" );
		PORT_DIPSETTING (	0x00, "1" );
		PORT_DIPSETTING (	0x10, "2" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING (	0x00, "Hard" );
		PORT_DIPSETTING (	0x20, "Easy" );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING (	0x00, "10000" );
		PORT_DIPSETTING (	0x40, "12000" );
		PORT_DIPSETTING (	0x80, "15000" );
		PORT_DIPSETTING (	0xc0, "None" );
	
		PORT_START();  /* DSW 2 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Coinage") );
		PORT_DIPSETTING (	0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING (	0x01, DEF_STR( "1C_1C") );
		PORT_DIPSETTING (	0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING (	0x03, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x0c, "Right Coin" );
		PORT_DIPSETTING (	0x00, "*6" );
		PORT_DIPSETTING (	0x04, "*5" );
		PORT_DIPSETTING (	0x08, "*4" );
		PORT_DIPSETTING (	0x0c, "*1" );
		PORT_DIPNAME( 0x10, 0x10, "Center Coin" );
		PORT_DIPSETTING (	0x00, "*2" );
		PORT_DIPSETTING (	0x10, "*1" );
		PORT_DIPNAME( 0xe0, 0x80, "Bonus Coins" );
		PORT_DIPSETTING (	0x60, "1 each 5" );
		PORT_DIPSETTING (	0x80, "2 each 4" );
		PORT_DIPSETTING (	0xa0, "1 each 4" );
		PORT_DIPSETTING (	0xc0, "1 each 2" );
		PORT_DIPSETTING (	0xe0, "None" );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_llander = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* IN0 */
		/* Bit 0 is VG_HALT, handled in the machine dependant part */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_SERVICE( 0x02, IP_ACTIVE_LOW );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_TILT );
		/* Of the rest, Bit 6 is the 3KHz source. 3,4 and 5 are unknown */
		PORT_BIT( 0x78, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_SERVICE, "Diagnostic Step", KEYCODE_F1, IP_JOY_NONE );
	
		PORT_START();  /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BITX(0x10, IP_ACTIVE_HIGH, IPT_START2, "Select Game", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BITX(0x20, IP_ACTIVE_HIGH, IPT_BUTTON1, "Abort", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x03, 0x01, "Right Coin" );
		PORT_DIPSETTING (	0x00, "*1" );
		PORT_DIPSETTING (	0x01, "*4" );
		PORT_DIPSETTING (	0x02, "*5" );
		PORT_DIPSETTING (	0x03, "*6" );
		PORT_DIPNAME( 0x0c, 0x00, "Language" );
		PORT_DIPSETTING (	0x00, "English" );
		PORT_DIPSETTING (	0x04, "French" );
		PORT_DIPSETTING (	0x08, "Spanish" );
		PORT_DIPSETTING (	0x0c, "German" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING (	0x00, "Normal" );
		PORT_DIPSETTING (	0x20, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xd0, 0x80, "Fuel units" );
		PORT_DIPSETTING (	0x00, "450" );
		PORT_DIPSETTING (	0x40, "600" );
		PORT_DIPSETTING (	0x80, "750" );
		PORT_DIPSETTING (	0xc0, "900" );
		PORT_DIPSETTING (	0x10, "1100" );
		PORT_DIPSETTING (	0x50, "1300" );
		PORT_DIPSETTING (	0x90, "1550" );
		PORT_DIPSETTING (	0xd0, "1800" );
	
		/* The next one is a potentiometer */
		PORT_START();  /* IN3 */
		PORT_ANALOGX( 0xff, 0x00, IPT_PADDLE|IPF_REVERSE, 100, 10, 0, 255, KEYCODE_UP, KEYCODE_DOWN, JOYCODE_1_UP, JOYCODE_1_DOWN );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_llander1 = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* IN0 */
		/* Bit 0 is VG_HALT, handled in the machine dependant part */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_SERVICE( 0x02, IP_ACTIVE_LOW );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_TILT );
		/* Of the rest, Bit 6 is the 3KHz source. 3,4 and 5 are unknown */
		PORT_BIT( 0x78, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_SERVICE, "Diagnostic Step", KEYCODE_F1, IP_JOY_NONE );
	
		PORT_START();  /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BITX(0x10, IP_ACTIVE_HIGH, IPT_START2, "Select Game", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BITX(0x20, IP_ACTIVE_HIGH, IPT_BUTTON1, "Abort", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x03, 0x01, "Right Coin" );
		PORT_DIPSETTING (	0x00, "*1" );
		PORT_DIPSETTING (	0x01, "*4" );
		PORT_DIPSETTING (	0x02, "*5" );
		PORT_DIPSETTING (	0x03, "*6" );
		PORT_DIPNAME( 0x0c, 0x00, "Language" );
		PORT_DIPSETTING (	0x00, "English" );
		PORT_DIPSETTING (	0x04, "French" );
		PORT_DIPSETTING (	0x08, "Spanish" );
		PORT_DIPSETTING (	0x0c, "German" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING (	0x00, "Normal" );
		PORT_DIPSETTING (	0x10, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xc0, 0x80, "Fuel units" );
		PORT_DIPSETTING (	0x00, "450" );
		PORT_DIPSETTING (	0x40, "600" );
		PORT_DIPSETTING (	0x80, "750" );
		PORT_DIPSETTING (	0xc0, "900" );
	
		/* The next one is a potentiometer */
		PORT_START();  /* IN3 */
		PORT_ANALOGX( 0xff, 0x00, IPT_PADDLE|IPF_REVERSE, 100, 10, 0, 255, KEYCODE_UP, KEYCODE_DOWN, JOYCODE_1_UP, JOYCODE_1_DOWN );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Sound interfaces
	 *
	 *************************************/
	
	static POKEYinterface pokey_interface = new POKEYinterface
	(
		1,	/* 1 chip */
		1500000,	/* 1.5 MHz??? */
		new int[] { 100 },
		/* The 8 pot handlers */
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		/* The allpot handler */
		new ReadHandlerPtr[] { input_port_3_r }
	);
	
	
	
	/*************************************
	 *
	 *	Machine drivers
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( asteroid )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", M6502, 1500000)
		MDRV_CPU_MEMORY(asteroid_readmem,asteroid_writemem)
		MDRV_CPU_VBLANK_INT(asteroid_interrupt,4)	/* 250 Hz */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_MACHINE_INIT(asteroid)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_VECTOR | VIDEO_RGB_DIRECT)
		MDRV_SCREEN_SIZE(400,300)
		MDRV_VISIBLE_AREA(0, 1040, 70, 950)
		MDRV_PALETTE_LENGTH(32768)
	
		MDRV_PALETTE_INIT(avg_white)
		MDRV_VIDEO_START(dvg)
		MDRV_VIDEO_UPDATE(vector)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("disc", DISCRETE, asteroid_sound_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( astdelux )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(asteroid)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(astdelux_readmem,astdelux_writemem)
	
		MDRV_NVRAM_HANDLER(atari_vg)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("disc", DISCRETE, astdelux_sound_interface)
		MDRV_SOUND_ADD(POKEY, pokey_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( llander )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(asteroid)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(llander_readmem,llander_writemem)
		MDRV_CPU_VBLANK_INT(llander_interrupt,6)	/* 250 Hz */
	
		MDRV_FRAMES_PER_SECOND(40)
		MDRV_MACHINE_INIT(NULL)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0, 1050, 0, 900)
	
		MDRV_PALETTE_INIT(avg_white)
		MDRV_VIDEO_START(dvg)
		MDRV_VIDEO_UPDATE(vector)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("disc", DISCRETE, llander_sound_interface)
	MACHINE_DRIVER_END
	
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadPtr rom_asteroid = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "035145.02",    0x6800, 0x0800, 0x0cc75459 );
		ROM_LOAD( "035144.02",    0x7000, 0x0800, 0x096ed35c );
		ROM_LOAD( "035143.02",    0x7800, 0x0800, 0x312caa02 );
		ROM_RELOAD( 		   0xf800, 0x0800 );/* for reset/interrupt vectors */
		/* Vector ROM */
		ROM_LOAD( "035127.02",    0x5000, 0x0800, 0x8b71fd9e );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_asteroi1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "035145.01",    0x6800, 0x0800, 0xe9bfda64 );
		ROM_LOAD( "035144.01",    0x7000, 0x0800, 0xe53c28a9 );
		ROM_LOAD( "035143.01",    0x7800, 0x0800, 0x7d4e3d05 );
		ROM_RELOAD( 		   0xf800, 0x0800 );/* for reset/interrupt vectors */
		/* Vector ROM */
		ROM_LOAD( "035127.01",    0x5000, 0x0800, 0x99699366 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_asteroib = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "035145ll.bin", 0x6800, 0x0800, 0x605fc0f2 );
		ROM_LOAD( "035144ll.bin", 0x7000, 0x0800, 0xe106de77 );
		ROM_LOAD( "035143ll.bin", 0x7800, 0x0800, 0x6b1d8594 );
		ROM_RELOAD( 		   0xf800, 0x0800 );/* for reset/interrupt vectors */
		/* Vector ROM */
		ROM_LOAD( "035127.02",    0x5000, 0x0800, 0x8b71fd9e );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_astdelux = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "036430.02",    0x6000, 0x0800, 0xa4d7a525 );
		ROM_LOAD( "036431.02",    0x6800, 0x0800, 0xd4004aae );
		ROM_LOAD( "036432.02",    0x7000, 0x0800, 0x6d720c41 );
		ROM_LOAD( "036433.03",    0x7800, 0x0800, 0x0dcc0be6 );
		ROM_RELOAD( 			  0xf800, 0x0800 );/* for reset/interrupt vectors */
		/* Vector ROM */
		ROM_LOAD( "036800.02",    0x4800, 0x0800, 0xbb8cabe1 );
		ROM_LOAD( "036799.01",    0x5000, 0x0800, 0x7d511572 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_astdelu1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "036430.01",    0x6000, 0x0800, 0x8f5dabc6 );
		ROM_LOAD( "036431.01",    0x6800, 0x0800, 0x157a8516 );
		ROM_LOAD( "036432.01",    0x7000, 0x0800, 0xfdea913c );
		ROM_LOAD( "036433.02",    0x7800, 0x0800, 0xd8db74e3 );
		ROM_RELOAD( 			  0xf800, 0x0800 );/* for reset/interrupt vectors */
		/* Vector ROM */
		ROM_LOAD( "036800.01",    0x4800, 0x0800, 0x3b597407 );
		ROM_LOAD( "036799.01",    0x5000, 0x0800, 0x7d511572 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_llander = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "034572.02",    0x6000, 0x0800, 0xb8763eea );
		ROM_LOAD( "034571.02",    0x6800, 0x0800, 0x77da4b2f );
		ROM_LOAD( "034570.01",    0x7000, 0x0800, 0x2724e591 );
		ROM_LOAD( "034569.02",    0x7800, 0x0800, 0x72837a4e );
		ROM_RELOAD( 		   0xf800, 0x0800 );/* for reset/interrupt vectors */
		/* Vector ROM */
		ROM_LOAD( "034599.01",    0x4800, 0x0800, 0x355a9371 );
		ROM_LOAD( "034598.01",    0x5000, 0x0800, 0x9c4ffa68 );
		/* This _should_ be the rom for international versions. */
		/* Unfortunately, is it not currently available. */
		ROM_LOAD( "034597.01",    0x5800, 0x0800, 0x00000000 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_llander1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "034572.01",    0x6000, 0x0800, 0x2aff3140 );
		ROM_LOAD( "034571.01",    0x6800, 0x0800, 0x493e24b7 );
		ROM_LOAD( "034570.01",    0x7000, 0x0800, 0x2724e591 );
		ROM_LOAD( "034569.01",    0x7800, 0x0800, 0xb11a7d01 );
		ROM_RELOAD( 		   0xf800, 0x0800 );/* for reset/interrupt vectors */
		/* Vector ROM */
		ROM_LOAD( "034599.01",    0x4800, 0x0800, 0x355a9371 );
		ROM_LOAD( "034598.01",    0x5000, 0x0800, 0x9c4ffa68 );
		/* This _should_ be the rom for international versions. */
		/* Unfortunately, is it not currently available. */
		ROM_LOAD( "034597.01",    0x5800, 0x0800, 0x00000000 );
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Driver initialization
	 *
	 *************************************/
	
	static DRIVER_INIT( asteroib )
	{
		install_mem_read_handler(0, 0x2000, 0x2000, asteroib_IN0_r);
		install_mem_read_handler(0, 0x2003, 0x2003, input_port_3_r);
	}
	
	
	static DRIVER_INIT( astdelux )
	{
		OVERLAY_START( astdelux_overlay )
			OVERLAY_RECT( 0.0, 0.0, 1.0, 1.0, MAKE_ARGB(0x04,0x88,0xff,0xff) )
		OVERLAY_END
	
		artwork_set_overlay(astdelux_overlay);
	}
	
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_asteroid	   = new GameDriver("1979"	,"asteroid"	,"asteroid.java"	,rom_asteroid,null	,machine_driver_asteroid	,input_ports_asteroid	,null	,ROT0	,	"Atari", "Asteroids (rev 2)" )
	public static GameDriver driver_asteroi1	   = new GameDriver("1979"	,"asteroi1"	,"asteroid.java"	,rom_asteroi1,driver_asteroid	,machine_driver_asteroid	,input_ports_asteroid	,null	,ROT0	,	"Atari", "Asteroids (rev 1)" )
	public static GameDriver driver_asteroib	   = new GameDriver("1979"	,"asteroib"	,"asteroid.java"	,rom_asteroib,driver_asteroid	,machine_driver_asteroid	,input_ports_asteroib	,init_asteroib	,ROT0	,	"bootleg", "Asteroids (bootleg on Lunar Lander hardware)" )
	public static GameDriver driver_astdelux	   = new GameDriver("1980"	,"astdelux"	,"asteroid.java"	,rom_astdelux,null	,machine_driver_astdelux	,input_ports_astdelux	,init_astdelux	,ROT0	,	"Atari", "Asteroids Deluxe (rev 2)" )
	public static GameDriver driver_astdelu1	   = new GameDriver("1980"	,"astdelu1"	,"asteroid.java"	,rom_astdelu1,driver_astdelux	,machine_driver_astdelux	,input_ports_astdelux	,init_astdelux	,ROT0	,	"Atari", "Asteroids Deluxe (rev 1)" )
	public static GameDriver driver_llander	   = new GameDriver("1979"	,"llander"	,"asteroid.java"	,rom_llander,null	,machine_driver_llander	,input_ports_llander	,null	,ROT0	,	"Atari", "Lunar Lander (rev 2)" )
	public static GameDriver driver_llander1	   = new GameDriver("1979"	,"llander1"	,"asteroid.java"	,rom_llander1,driver_llander	,machine_driver_llander	,input_ports_llander1	,null	,ROT0	,	"Atari", "Lunar Lander (rev 1)" )
	
}
