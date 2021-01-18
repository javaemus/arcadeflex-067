/***************************************************************************

Warp Warp memory map (preliminary)

  Memory Map figured out by Chris Hardy (chrish@kcbbs.gen.nz)
  Initial Driver code by Mirko


0000-37FF ROM		Code
4800-4FFF ROM		Graphics rom which must be included in memory space

memory mapped ports:

read:

All Read ports only use bit 0

C000	  Coin slot 1
C001	  Fire (for player 2 in "cocktail mode")
C002	  Start P1
C003	  Start P2
C004	  Fire
C005	  Test Mode
C006	  Cabinet type
C007	  Coin Slot 2
C010	  Joystick (read like an analog one, but it's digital)
		  0->23 = DOWN
		  24->63 = UP
		  64->111 = LEFT
		  112->167 = RIGHT
		  168->255 = NEUTRAL
C020-C027 Dipswitch 1->8 in bit 0

write:
C000-C001 bullet x/y pos
C002	  Sound
C003	  WatchDog reset
C010	  Music 1
C020	  Music 2
C030-C032 lamps
C034	  coin lock out
C035	  coin counter
C036	  IRQ enable _and_ bullet enable (both on bit 0) (currently ignored)
C037	  flip screen


Stephh's notes :

  - The only difference between 'warpwarr' and 'warpwar2' is the copyright
    string on the first screen (when the scores are displayed) :

      * 'warpwarr' : "(c) 1981 ROCK-OLA MFG.CORP."  (text stored at 0x33ff to 0x3417)
      * 'warpwar2' : "(c) 1981 ROCK-OLA MFG.CO."    (text stored at 0x33ff to 0x3415)

    Note that the checksum at 0x37ff (used for checking ROM at 0x3000 to 0x37ff)
    is different of course.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class warpwarp
{
	
	
	extern unsigned char *warpwarp_bulletsram;
	PALETTE_INIT( warpwarp );
	VIDEO_UPDATE( warpwarp );
	
	/* from sndhrdw/warpwarp.c */
	
	/* Read System Inputs */
	public static ReadHandlerPtr bombbee_sys_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (offset == 4)	// to return BUTTON1 status
		{
			return (readinputport(4) >> (flip_screen & 1)) & 1;
		}
		else
			return (readinputport(0) >> offset) & 1;
	} };
	
	public static ReadHandlerPtr warpwarp_sys_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (readinputport(0) >> offset) & 1;
	} };
	
	/* Read Dipswitches */
	public static ReadHandlerPtr warpwarp_dsw_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (readinputport(1) >> offset) & 1;
	} };
	
	/* Read mux Controller Inputs */
	public static ReadHandlerPtr bombbee_mux_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return readinputport(2 + (flip_screen & 1));
	} };
	
	public static ReadHandlerPtr warpwarp_mux_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int res;
	
		res = readinputport(2 + (flip_screen & 1));
		if (res & 1) return 23;
		if (res & 2) return 63;
		if (res & 4) return 111;
		if (res & 8) return 167;
		return 255;
	} };
	
	public static WriteHandlerPtr warpwarp_leds_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		set_led_status(offset,data & 1);
	} };
	
	public static WriteHandlerPtr warpwarp_coin_counter_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_counter_w(offset,data);
	} };
	
	
	
	public static Memory_ReadAddress bombbee_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x23ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4800, 0x4fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x6007, bombbee_sys_r ),
		new Memory_ReadAddress( 0x6010, 0x6010, bombbee_mux_r ),
		new Memory_ReadAddress( 0x6020, 0x6027, warpwarp_dsw_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress bombbee_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x23ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x4400, 0x47ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0x4800, 0x4fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x6001, MWA_RAM, warpwarp_bulletsram ),
		new Memory_WriteAddress( 0x6002, 0x6002, warpwarp_sound_w ),
		new Memory_WriteAddress( 0x6003, 0x6003, watchdog_reset_w ),
		new Memory_WriteAddress( 0x6010, 0x6010, warpwarp_music1_w ),
		new Memory_WriteAddress( 0x6020, 0x6020, warpwarp_music2_w ),
		new Memory_WriteAddress( 0x6030, 0x6032, warpwarp_leds_w ),
		new Memory_WriteAddress( 0x6035, 0x6035, warpwarp_coin_counter_w ),
		new Memory_WriteAddress( 0x6037, 0x6037, warpwarp_flip_screen_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress warpwarp_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x37ff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4800, 0x4fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x83ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xc007, warpwarp_sys_r ),
		new Memory_ReadAddress( 0xc010, 0xc010, warpwarp_mux_r ),
		new Memory_ReadAddress( 0xc020, 0xc027, warpwarp_dsw_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress warpwarp_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x37ff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x4400, 0x47ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0x4800, 0x4fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new Memory_WriteAddress( 0xc000, 0xc001, MWA_RAM, warpwarp_bulletsram ),
		new Memory_WriteAddress( 0xc002, 0xc002, warpwarp_sound_w ),
		new Memory_WriteAddress( 0xc003, 0xc003, watchdog_reset_w ),
		new Memory_WriteAddress( 0xc010, 0xc010, warpwarp_music1_w ),
		new Memory_WriteAddress( 0xc020, 0xc020, warpwarp_music2_w ),
		new Memory_WriteAddress( 0xc030, 0xc032, warpwarp_leds_w ),
		new Memory_WriteAddress( 0xc035, 0xc035, warpwarp_coin_counter_w ),
		new Memory_WriteAddress( 0xc037, 0xc037, warpwarp_flip_screen_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_bombbee = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_SPECIAL );// mux BUTTON1 - see Fake Input Port
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );// acts as COINn, but doesn't affect coin counter
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x00, "3" );
		PORT_DIPSETTING(	0x04, "4" );
	//	PORT_DIPSETTING(	0x08, "4" );			// duplicated setting
		PORT_DIPSETTING(	0x0c, "5" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xe0, 0x00, "Replay" );	// awards 1 credit
		PORT_DIPSETTING(	0x00, "50000" );
		PORT_DIPSETTING(	0x20, "60000" );
		PORT_DIPSETTING(	0x40, "70000" );
		PORT_DIPSETTING(	0x60, "80000" );
		PORT_DIPSETTING(	0x80, "100000" );
		PORT_DIPSETTING(	0xa0, "120000" );
		PORT_DIPSETTING(	0xc0, "150000" );
		PORT_DIPSETTING(	0xe0, "None" );
	
		PORT_START(); 	/* Mux input - player 1 controller - handled by bombbee_mux_r */
		PORT_ANALOG( 0xff, 0x80, IPT_PADDLE | IPF_REVERSE, 30, 10, 0x14, 0xac );
	
		PORT_START(); 	/* Mux input - player 2 controller - handled by bombbee_mux_r */
		PORT_ANALOG( 0xff, 0x80, IPT_PADDLE | IPF_REVERSE | IPF_COCKTAIL , 30, 10, 0x14, 0xac );
	
		PORT_START(); 	/* Fake input port to support mux buttons - handled by bombbee_sys_r */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_cutieq = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_SPECIAL );// mux BUTTON1 - see Fake Input Port
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );// acts as COINn, but doesn't affect coin counter
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x00, "3" );
		PORT_DIPSETTING(	0x04, "4" );
	//	PORT_DIPSETTING(	0x08, "4" );			// duplicated setting
		PORT_DIPSETTING(	0x0c, "5" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xe0, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x00, "50000" );
		PORT_DIPSETTING(	0x20, "60000" );
		PORT_DIPSETTING(	0x40, "80000" );
		PORT_DIPSETTING(	0x60, "100000" );
		PORT_DIPSETTING(	0x80, "120000" );
		PORT_DIPSETTING(	0xa0, "150000" );
		PORT_DIPSETTING(	0xc0, "200000" );
		PORT_DIPSETTING(	0xe0, "None" );
	
		PORT_START(); 	/* Mux input - player 1 controller - handled by bombbee_mux_r */
		PORT_ANALOG( 0xff, 0x80, IPT_PADDLE | IPF_REVERSE, 30, 10, 0x14, 0xac );
	
		PORT_START(); 	/* Mux input - player 2 controller - handled by bombbee_mux_r */
		PORT_ANALOG( 0xff, 0x80, IPT_PADDLE | IPF_REVERSE | IPF_COCKTAIL , 30, 10, 0x14, 0xac );
	
		PORT_START(); 	/* Fake input port to support mux buttons - handled by bombbee_sys_r */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_warpwarp = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x01, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x00, "2" );
		PORT_DIPSETTING(	0x04, "3" );
		PORT_DIPSETTING(	0x08, "4" );
		PORT_DIPSETTING(	0x0c, "5" );
		/* Bonus Lives when "Lives" Dip Switch is set to "2", "3" or "4" */
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x00, "8000 30000" );
		PORT_DIPSETTING(	0x10, "10000 40000" );
		PORT_DIPSETTING(	0x20, "15000 60000" );
		PORT_DIPSETTING(	0x30, "None" );
		/* Bonus Lives when "Lives" Dip Switch is set to "5"
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x00, "30000" );
		PORT_DIPSETTING(	0x10, "40000" );
		PORT_DIPSETTING(	0x20, "60000" );
		PORT_DIPSETTING(	0x30, "None" );
		*/
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		/* when level selection is On, press 1 to increase level */
		PORT_BITX(	  0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Level Selection", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(	0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* FAKE - input port to simulate an analog stick - handled by warpwarp_mux_r */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
	
		PORT_START(); 	/* FAKE - input port to simulate an analog stick - handled by warpwarp_mux_r */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
	INPUT_PORTS_END(); }}; 
	
	/* has High Score Initials dip switch instead of rack test */
	static InputPortPtr input_ports_warpwarr = new InputPortPtr(){ public void handler() { 
		PORT_START(); 		/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 		/* DSW1 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x01, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x00, "2" );
		PORT_DIPSETTING(	0x04, "3" );
		PORT_DIPSETTING(	0x08, "4" );
		PORT_DIPSETTING(	0x0c, "5" );
		/* Bonus Lives when "Lives" Dip Switch is set to "2", "3" or "4" */
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x00, "8000 30000" );
		PORT_DIPSETTING(	0x10, "10000 40000" );
		PORT_DIPSETTING(	0x20, "15000 60000" );
		PORT_DIPSETTING(	0x30, "None" );
		/* Bonus Lives when "Lives" Dip Switch is set to "5"
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x00, "30000" );
		PORT_DIPSETTING(	0x10, "40000" );
		PORT_DIPSETTING(	0x20, "60000" );
		PORT_DIPSETTING(	0x30, "None" );
		*/
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "High Score Initials" );
		PORT_DIPSETTING(	0x80, DEF_STR( "No") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Yes") );
	
		PORT_START(); 	/* FAKE - input port to simulate an analog stick - handled by warpwarp_mux_r */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
	
		PORT_START(); 	/* FAKE - input port to simulate an analog stick - handled by warpwarp_mux_r */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
	INPUT_PORTS_END(); }}; 
	
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		1,	/* 1 bit per pixel */
		new int[] { 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8 /* every char takes 8 bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64, /* 64 sprites */
		1,	/* 1 bit per pixel */
		new int[] { 0 },
		new int[] {  0, 1, 2, 3, 4, 5, 6, 7 ,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every sprite takes 32 bytes */
	);
	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_CPU1, 0x4800, charlayout,   0, 256 ),
		new GfxDecodeInfo( REGION_CPU1, 0x4800, spritelayout, 0, 256 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static CustomSound_interface custom_interface = new CustomSound_interface
	(
		warpwarp_sh_start,
		warpwarp_sh_stop,
		warpwarp_sh_update
	);
	
	
	static MACHINE_DRIVER_START( bombbee )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", 8080, 2048000)	/* 3 MHz? */
		MDRV_CPU_MEMORY(bombbee_readmem,bombbee_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)	/* frames per second, vblank duration */
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(34*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 34*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
		MDRV_COLORTABLE_LENGTH(2*256)
	
		MDRV_PALETTE_INIT(warpwarp)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(warpwarp)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( warpwarp )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(bombbee)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(warpwarp_readmem,warpwarp_writemem)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_bombbee = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "bombbee.1k",   0x0000, 0x2000, 0x9f8cd7af );
		ROM_LOAD( "bombbee.4c",   0x4800, 0x0800, 0x5f37d569 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_cutieq = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "cutieq.1k",    0x0000, 0x2000, 0x6486cdca );
		ROM_LOAD( "cutieq.4c",    0x4800, 0x0800, 0x0e1618c9 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_warpwarp = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "g-n9601n.2r",  0x0000, 0x1000, 0xf5262f38 );
		ROM_LOAD( "g-09602n.2m",  0x1000, 0x1000, 0xde8355dd );
		ROM_LOAD( "g-09603n.1p",  0x2000, 0x1000, 0xbdd1dec5 );
		ROM_LOAD( "g-09613n.1t",  0x3000, 0x0800, 0xaf3d77ef );
		ROM_LOAD( "g-9611n.4c",   0x4800, 0x0800, 0x380994c8 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_warpwarr = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "g-09601.2r",   0x0000, 0x1000, 0x916ffa35 );
		ROM_LOAD( "g-09602.2m",   0x1000, 0x1000, 0x398bb87b );
		ROM_LOAD( "g-09603.1p",   0x2000, 0x1000, 0x6b962fc4 );
		ROM_LOAD( "g-09613.1t",   0x3000, 0x0800, 0x60a67e76 );
		ROM_LOAD( "g-9611.4c",    0x4800, 0x0800, 0x00e6a326 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_warpwar2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "g-09601.2r",   0x0000, 0x1000, 0x916ffa35 );
		ROM_LOAD( "g-09602.2m",   0x1000, 0x1000, 0x398bb87b );
		ROM_LOAD( "g-09603.1p",   0x2000, 0x1000, 0x6b962fc4 );
		ROM_LOAD( "g-09612.1t",   0x3000, 0x0800, 0xb91e9e79 );
		ROM_LOAD( "g-9611.4c",    0x4800, 0x0800, 0x00e6a326 );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_bombbee	   = new GameDriver("1979"	,"bombbee"	,"warpwarp.java"	,rom_bombbee,null	,machine_driver_bombbee	,input_ports_bombbee	,null	,ROT90	,	"Namco", "Bomb Bee" )
	public static GameDriver driver_cutieq	   = new GameDriver("1979"	,"cutieq"	,"warpwarp.java"	,rom_cutieq,null	,machine_driver_bombbee	,input_ports_cutieq	,null	,ROT90	,	"Namco", "Cutie Q" )
	public static GameDriver driver_warpwarp	   = new GameDriver("1981"	,"warpwarp"	,"warpwarp.java"	,rom_warpwarp,null	,machine_driver_warpwarp	,input_ports_warpwarp	,null	,ROT90	,	"Namco", "Warp & Warp" )
	public static GameDriver driver_warpwarr	   = new GameDriver("1981"	,"warpwarr"	,"warpwarp.java"	,rom_warpwarr,driver_warpwarp	,machine_driver_warpwarp	,input_ports_warpwarr	,null	,ROT90	,	"[Namco] (Rock-ola license)", "Warp Warp (Rock-ola set 1)" )
	public static GameDriver driver_warpwar2	   = new GameDriver("1981"	,"warpwar2"	,"warpwarp.java"	,rom_warpwar2,driver_warpwarp	,machine_driver_warpwarp	,input_ports_warpwarr	,null	,ROT90	,	"[Namco] (Rock-ola license)", "Warp Warp (Rock-ola set 2)" )
}
