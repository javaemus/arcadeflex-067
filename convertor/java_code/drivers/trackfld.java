/***************************************************************************

Konami games memory map (preliminary)

Based on drivers from Juno First emulator by Chris Hardy (chrish@kcbbs.gen.nz)

Track'n'Field

MAIN BOARD:
0000-17ff RAM
1800-183f Sprite RAM Pt 1
1C00-1C3f Sprite RAM Pt 2
3800-3bff Color RAM
3000-33ff Video RAM
6000-ffff ROM
1200-12ff IO

Notes:

- There are a few kludges to support cocktail mode in mastkin; it might be broken in
  the real game.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class trackfld
{
	
	
	
	
	extern unsigned char *trackfld_scroll;
	extern unsigned char *trackfld_scroll2;
	PALETTE_INIT( trackfld );
	VIDEO_START( trackfld );
	VIDEO_START( mastkin );
	VIDEO_UPDATE( trackfld );
	
	
	
	extern struct SN76496interface konami_sn76496_interface;
	extern struct DACinterface konami_dac_interface;
	extern struct ADPCMinterface hyprolyb_adpcm_interface;
	
	
	MACHINE_INIT( mastkin )
	{
		flip_screen_set(0);
	}
	
	
	/* handle fake button for speed cheat */
	public static ReadHandlerPtr konami_IN1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int res;
		static int cheat = 0;
		static int bits[] = { 0xee, 0xff, 0xbb, 0xaa };
	
		res = readinputport(1);
	
		if ((res & 0x80) == 0)
		{
			res |= 0x55;
			res &= bits[cheat];
			cheat = (cheat+1)%4;
		}
		return res;
	} };
	
	/* There is no read from 0x1282 (where player 2 inputs should be).
	   However, there is a code for "Cocktail mode" support at 0x813b.
	   Thus this read handler based on the screen flip status.
	*/
	public static ReadHandlerPtr mastkin_IN1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (flip_screen ? readinputport(2) : readinputport(1));
	} };
	
	
	
	/*
	 Track'n'Field has 1k of battery backed RAM which can be erased by setting a dipswitch
	*/
	static unsigned char *nvram;
	static size_t nvram_size;
	static int we_flipped_the_switch;
	
	static NVRAM_HANDLER( trackfld )
	{
		if (read_or_write)
		{
			mame_fwrite(file,nvram,nvram_size);
	
			if (we_flipped_the_switch)
			{
				struct InputPort *in;
	
	
				/* find the dip switch which resets the high score table, and set it */
				/* back to off. */
				in = Machine->input_ports;
	
				while (in->type != IPT_END)
				{
					if (in->name != NULL && in->name != IP_NAME_DEFAULT &&
							strcmp(in->name,"World Records") == 0)
					{
						if (in->default_value == 0)
							in->default_value = in->mask;
						break;
					}
	
					in++;
				}
	
				we_flipped_the_switch = 0;
			}
		}
		else
		{
			if (file)
			{
				mame_fread(file,nvram,nvram_size);
				we_flipped_the_switch = 0;
			}
			else
			{
				struct InputPort *in;
	
	
				/* find the dip switch which resets the high score table, and set it on */
				in = Machine->input_ports;
	
				while (in->type != IPT_END)
				{
					if (in->name != NULL && in->name != IP_NAME_DEFAULT &&
							strcmp(in->name,"World Records") == 0)
					{
						if (in->default_value == in->mask)
						{
							in->default_value = 0;
							we_flipped_the_switch = 1;
						}
						break;
					}
	
					in++;
				}
			}
		}
	}
	
	static NVRAM_HANDLER( mastkin )
	{
		if (read_or_write)
			mame_fwrite(file,nvram,nvram_size);
		else
		{
			if (file)
				mame_fread(file,nvram,nvram_size);
		}
	}
	
	public static WriteHandlerPtr flip_screen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		flip_screen_set(data);
	} };
	
	public static WriteHandlerPtr coin_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_counter_w(offset,data & 1);
	} };
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x1200, 0x1200, input_port_4_r ), /* DIP 2 */
		new Memory_ReadAddress( 0x1280, 0x1280, input_port_0_r ), /* IO Coin */
	//	new Memory_ReadAddress( 0x1281, 0x1281, input_port_1_r ), /* P1 IO */
		new Memory_ReadAddress( 0x1281, 0x1281, konami_IN1_r ),	/* P1 IO and handle fake button for cheating */
		new Memory_ReadAddress( 0x1282, 0x1282, input_port_2_r ), /* P2 IO */
		new Memory_ReadAddress( 0x1283, 0x1283, input_port_3_r ), /* DIP 1 */
		new Memory_ReadAddress( 0x1800, 0x1fff, MRA_RAM ),
		new Memory_ReadAddress( 0x2800, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x1000, 0x1000, watchdog_reset_w ),
		new Memory_WriteAddress( 0x1080, 0x1080, flip_screen_w ),
		new Memory_WriteAddress( 0x1081, 0x1081, konami_sh_irqtrigger_w ),  /* cause interrupt on audio CPU */
		new Memory_WriteAddress( 0x1083, 0x1084, coin_w ),
		new Memory_WriteAddress( 0x1087, 0x1087, interrupt_enable_w ),
		new Memory_WriteAddress( 0x1100, 0x1100, soundlatch_w ),
		new Memory_WriteAddress( 0x1800, 0x183f, MWA_RAM, spriteram_2 ),
		new Memory_WriteAddress( 0x1840, 0x185f, MWA_RAM, trackfld_scroll ),
		new Memory_WriteAddress( 0x1860, 0x1bff, MWA_RAM ),
		new Memory_WriteAddress( 0x1c00, 0x1c3f, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x1c40, 0x1c5f, MWA_RAM, trackfld_scroll2 ),
		new Memory_WriteAddress( 0x1c60, 0x1fff, MWA_RAM ),
		new Memory_WriteAddress( 0x2800, 0x2bff, MWA_RAM ),
		new Memory_WriteAddress( 0x2c00, 0x2fff, MWA_RAM, nvram, nvram_size ),
		new Memory_WriteAddress( 0x3000, 0x37ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x3800, 0x3fff, colorram_w, colorram ),
		new Memory_WriteAddress( 0x6000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress mastkin_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x1200, 0x1200, input_port_4_r ), /* DIP 2 */
		new Memory_ReadAddress( 0x1280, 0x1280, input_port_0_r ), /* IO Coin */
		new Memory_ReadAddress( 0x1281, 0x1281, mastkin_IN1_r ), /* P1 and P2 IO */
	//	new Memory_ReadAddress( 0x1282, 0x1282, input_port_2_r ), /* unused */
		new Memory_ReadAddress( 0x1283, 0x1283, input_port_3_r ), /* DIP 1 */
		new Memory_ReadAddress( 0x1800, 0x1fff, MRA_RAM ),
		new Memory_ReadAddress( 0x2800, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mastkin_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x1000, 0x1000, watchdog_reset_w ),
		new Memory_WriteAddress( 0x10b0, 0x10b0, flip_screen_w ),
		new Memory_WriteAddress( 0x10b1, 0x10b1, konami_sh_irqtrigger_w ),
		new Memory_WriteAddress( 0x1083, 0x1084, coin_w ),
		new Memory_WriteAddress( 0x1087, 0x1087, interrupt_enable_w ),
		new Memory_WriteAddress( 0x1100, 0x1100, soundlatch_w ),
		new Memory_WriteAddress( 0x1800, 0x183f, MWA_RAM, spriteram_2 ),
		new Memory_WriteAddress( 0x1840, 0x185f, MWA_RAM, trackfld_scroll ),
		new Memory_WriteAddress( 0x1860, 0x1bff, MWA_RAM ),
		new Memory_WriteAddress( 0x1c00, 0x1c3f, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x1c40, 0x1c5f, MWA_RAM, trackfld_scroll2 ),
		new Memory_WriteAddress( 0x1c60, 0x1fff, MWA_RAM ),
		new Memory_WriteAddress( 0x2800, 0x2bff, MWA_RAM ),
		new Memory_WriteAddress( 0x2c00, 0x2fff, MWA_RAM, nvram, nvram_size ),
		new Memory_WriteAddress( 0x3000, 0x37ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x3800, 0x3fff, colorram_w, colorram ),
		new Memory_WriteAddress( 0x6000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, soundlatch_r ),
		new Memory_ReadAddress( 0x8000, 0x8000, trackfld_sh_timer_r ),
		new Memory_ReadAddress( 0xe002, 0xe002, trackfld_speech_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xa000, SN76496_0_w ),	/* Loads the snd command into the snd latch */
		new Memory_WriteAddress( 0xc000, 0xc000, MWA_NOP ),		/* This address triggers the SN chip to read the data port. */
		new Memory_WriteAddress( 0xe000, 0xe000, DAC_0_data_w ),
	/* There are lots more addresses which are used for setting a two bit volume
		controls for speech and music
	
		Currently these are un-supported by Mame
	*/
		new Memory_WriteAddress( 0xe001, 0xe001, MWA_NOP ), /* watch dog ? */
		new Memory_WriteAddress( 0xe004, 0xe004, VLM5030_data_w ),
		new Memory_WriteAddress( 0xe000, 0xefff, trackfld_sound_w, ), /* e003 speech control */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress hyprolyb_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, soundlatch_r ),
		new Memory_ReadAddress( 0x8000, 0x8000, trackfld_sh_timer_r ),
		new Memory_ReadAddress( 0xe002, 0xe002, hyprolyb_speech_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hyprolyb_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xa000, SN76496_0_w ),	/* Loads the snd command into the snd latch */
		new Memory_WriteAddress( 0xc000, 0xc000, MWA_NOP ),		/* This address triggers the SN chip to read the data port. */
		new Memory_WriteAddress( 0xe000, 0xe000, DAC_0_data_w ),
	/* There are lots more addresses which are used for setting a two bit volume
		controls for speech and music
	
		Currently these are un-supported by Mame
	*/
		new Memory_WriteAddress( 0xe001, 0xe001, MWA_NOP ), /* watch dog ? */
		new Memory_WriteAddress( 0xe004, 0xe004, hyprolyb_ADPCM_data_w ),
		new Memory_WriteAddress( 0xe000, 0xefff, MWA_NOP ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_trackfld = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	//	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
		/* Fake button to press buttons 1 and 3 impossibly fast. Handle via konami_IN1_r */
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_CHEAT | IPF_PLAYER1, "Run Like Hell Cheat", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER3 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER4 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, "Disabled" );
	/* 0x00 disables Coin 2. It still accepts coins and makes the sound, but
	   it doesn't give you any credit */
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x01, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPNAME( 0x02, 0x00, "After Last Event" );
		PORT_DIPSETTING(    0x02, "Game Over" );
		PORT_DIPSETTING(    0x00, "Game Continues" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "None" );
		PORT_DIPSETTING(    0x00, "100000" );
		PORT_DIPNAME( 0x10, 0x10, "World Records" );
		PORT_DIPSETTING(    0x10, "Don't Erase" );
		PORT_DIPSETTING(    0x00, "Erase on Reset" );
		PORT_DIPNAME( 0x60, 0x60, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x60, "Easy" );
		PORT_DIPSETTING(    0x40, "Normal" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x00, "Difficult" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mastkin = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* FAKE IN2 - Read via mastkin_IN1_r */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, "Allow Continue" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Timer Speed" );
		PORT_DIPSETTING(    0x02, "Normal" );
		PORT_DIPSETTING(    0x00, "Fast" );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Difficulty") );	// "Damage"
		PORT_DIPSETTING(    0x0c, "Easy" );			//   0x03
		PORT_DIPSETTING(    0x04, "Normal" );		//   0x07
		PORT_DIPSETTING(    0x08, "Hard" );			//   0x0b
		PORT_DIPSETTING(    0x00, "Very Hard" );		//   0x0f
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x20, 0x00, "Internal speed" );	// Check code at 0x8576
		PORT_DIPSETTING(    0x20, "Slow" );			//   0x0c00
		PORT_DIPSETTING(    0x00, "Fast" );			//   0x0a00
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );		// Stored at 0x284e but not read back
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x0f, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_4C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_5C") );
	//	PORT_DIPSETTING(    0x0b, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x0d, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x0e, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0xf0, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_4C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_5C") );
	//	PORT_DIPSETTING(    0xb0, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0xd0, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0xe0, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0xf0, DEF_STR( "1C_1C") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,2),
		4,
		new int[] { RGN_FRAC(1,2)+4, RGN_FRAC(1,2)+0, 4, 0 },
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 24*8+0, 24*8+1, 24*8+2, 24*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8 },
		64*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,       0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout, 16*16, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	struct VLM5030interface trackfld_vlm5030_interface =
	{
		3580000,    /* master clock  */
		100,        /* volume        */
		REGION_SOUND1,	/* memory region  */
		0           /* memory size    */
	};
	
	
	
	static MACHINE_DRIVER_START( trackfld )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", M6809, 2048000)        /* 1.400 MHz ??? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80,14318180/4)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* Z80 Clock is derived from a 14.31818 MHz crystal */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_NVRAM_HANDLER(trackfld)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(16*16+16*16)
	
		MDRV_PALETTE_INIT(trackfld)
		MDRV_VIDEO_START(trackfld)
		MDRV_VIDEO_UPDATE(trackfld)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, konami_dac_interface)
		MDRV_SOUND_ADD(SN76496, konami_sn76496_interface)
		MDRV_SOUND_ADD(VLM5030, trackfld_vlm5030_interface)
	MACHINE_DRIVER_END
	
	/* same as the original, but uses ADPCM instead of VLM5030 */
	/* also different memory handlers do handle that */
	static MACHINE_DRIVER_START( hyprolyb )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6809, 2048000)        /* 1.400 MHz ??? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80,14318180/4)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* Z80 Clock is derived from a 14.31818 MHz crystal */
		MDRV_CPU_MEMORY(hyprolyb_sound_readmem,hyprolyb_sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_NVRAM_HANDLER(trackfld)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(16*16+16*16)
	
		MDRV_PALETTE_INIT(trackfld)
		MDRV_VIDEO_START(trackfld)
		MDRV_VIDEO_UPDATE(trackfld)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, konami_dac_interface)
		MDRV_SOUND_ADD(SN76496, konami_sn76496_interface)
		MDRV_SOUND_ADD(ADPCM, hyprolyb_adpcm_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mastkin )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(trackfld)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mastkin_readmem,mastkin_writemem)
	
		MDRV_NVRAM_HANDLER(mastkin)
	
		MDRV_MACHINE_INIT(mastkin)
	
		/* video hardware */
		MDRV_VIDEO_START(mastkin)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_trackfld = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 );    /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "a01_e01.bin",  0x6000, 0x2000, 0x2882f6d4 );
		ROM_LOAD( "a02_e02.bin",  0x8000, 0x2000, 0x1743b5ee );
		ROM_LOAD( "a03_k03.bin",  0xa000, 0x2000, 0x6c0d1ee9 );
		ROM_LOAD( "a04_e04.bin",  0xc000, 0x2000, 0x21d6c448 );
		ROM_LOAD( "a05_e05.bin",  0xe000, 0x2000, 0xf08c7b7e );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "c2_d13.bin",   0x0000, 0x2000, 0x95bf79b6 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "h16_e12.bin",  0x0000, 0x2000, 0x50075768 );
		ROM_LOAD( "h15_e11.bin",  0x2000, 0x2000, 0xdda9e29f );
		ROM_LOAD( "h14_e10.bin",  0x4000, 0x2000, 0xc2166a5c );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "c11_d06.bin",  0x0000, 0x2000, 0x82e2185a );
		ROM_LOAD( "c12_d07.bin",  0x2000, 0x2000, 0x800ff1f1 );
		ROM_LOAD( "c13_d08.bin",  0x4000, 0x2000, 0xd9faf183 );
		ROM_LOAD( "c14_d09.bin",  0x6000, 0x2000, 0x5886c802 );
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 );
		ROM_LOAD( "tfprom.1",     0x0000, 0x0020, 0xd55f30b5 );/* palette */
		ROM_LOAD( "tfprom.3",     0x0020, 0x0100, 0xd2ba4d32 );/* sprite lookup table */
		ROM_LOAD( "tfprom.2",     0x0120, 0x0100, 0x053e5861 );/* char lookup table */
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* 64k for speech rom */
		ROM_LOAD( "c9_d15.bin",   0x0000, 0x2000, 0xf546a56b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_trackflc = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 );    /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "f01.1a",       0x6000, 0x2000, 0x4e32b360 );
		ROM_LOAD( "f02.2a",       0x8000, 0x2000, 0x4e7ebf07 );
		ROM_LOAD( "l03.3a",       0xa000, 0x2000, 0xfef4c0ea );
		ROM_LOAD( "f04.4a",       0xc000, 0x2000, 0x73940f2d );
		ROM_LOAD( "f05.5a",       0xe000, 0x2000, 0x363fd761 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "c2_d13.bin",   0x0000, 0x2000, 0x95bf79b6 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "h16_e12.bin",  0x0000, 0x2000, 0x50075768 );
		ROM_LOAD( "h15_e11.bin",  0x2000, 0x2000, 0xdda9e29f );
		ROM_LOAD( "h14_e10.bin",  0x4000, 0x2000, 0xc2166a5c );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "c11_d06.bin",  0x0000, 0x2000, 0x82e2185a );
		ROM_LOAD( "c12_d07.bin",  0x2000, 0x2000, 0x800ff1f1 );
		ROM_LOAD( "c13_d08.bin",  0x4000, 0x2000, 0xd9faf183 );
		ROM_LOAD( "c14_d09.bin",  0x6000, 0x2000, 0x5886c802 );
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 );
		ROM_LOAD( "tfprom.1",     0x0000, 0x0020, 0xd55f30b5 );/* palette */
		ROM_LOAD( "tfprom.3",     0x0020, 0x0100, 0xd2ba4d32 );/* sprite lookup table */
		ROM_LOAD( "tfprom.2",     0x0120, 0x0100, 0x053e5861 );/* char lookup table */
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* 64k for speech rom */
		ROM_LOAD( "c9_d15.bin",   0x0000, 0x2000, 0xf546a56b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hyprolym = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 );    /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "hyprolym.a01", 0x6000, 0x2000, 0x82257fb7 );
		ROM_LOAD( "hyprolym.a02", 0x8000, 0x2000, 0x15b83099 );
		ROM_LOAD( "hyprolym.a03", 0xa000, 0x2000, 0xe54cc960 );
		ROM_LOAD( "hyprolym.a04", 0xc000, 0x2000, 0xd099b1e8 );
		ROM_LOAD( "hyprolym.a05", 0xe000, 0x2000, 0x974ff815 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for the audio CPU */
		ROM_LOAD( "c2_d13.bin",   0x0000, 0x2000, 0x95bf79b6 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "hyprolym.h16", 0x0000, 0x2000, 0x768bb63d );
		ROM_LOAD( "hyprolym.h15", 0x2000, 0x2000, 0x3af0e2a8 );
		ROM_LOAD( "h14_e10.bin",  0x4000, 0x2000, 0xc2166a5c );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "c11_d06.bin",  0x0000, 0x2000, 0x82e2185a );
		ROM_LOAD( "c12_d07.bin",  0x2000, 0x2000, 0x800ff1f1 );
		ROM_LOAD( "c13_d08.bin",  0x4000, 0x2000, 0xd9faf183 );
		ROM_LOAD( "c14_d09.bin",  0x6000, 0x2000, 0x5886c802 );
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 );
		ROM_LOAD( "tfprom.1",     0x0000, 0x0020, 0xd55f30b5 );/* palette */
		ROM_LOAD( "tfprom.3",     0x0020, 0x0100, 0xd2ba4d32 );/* sprite lookup table */
		ROM_LOAD( "tfprom.2",     0x0120, 0x0100, 0x053e5861 );/* char lookup table */
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* 64k for speech rom */
		ROM_LOAD( "c9_d15.bin",   0x0000, 0x2000, 0xf546a56b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hyprolyb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 );    /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "a1.1",         0x6000, 0x2000, 0x9aee2d5a );
		ROM_LOAD( "hyprolym.a02", 0x8000, 0x2000, 0x15b83099 );
		ROM_LOAD( "a3.3",         0xa000, 0x2000, 0x2d6fc308 );
		ROM_LOAD( "hyprolym.a04", 0xc000, 0x2000, 0xd099b1e8 );
		ROM_LOAD( "hyprolym.a05", 0xe000, 0x2000, 0x974ff815 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for the audio CPU */
		ROM_LOAD( "c2_d13.bin",   0x0000, 0x2000, 0x95bf79b6 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/*  64k for the 6802 which plays ADPCM samples */
		/* this bootleg uses a 6802 to "emulate" the VLM5030 speech chip */
		/* I didn't bother to emulate the 6802, I just play the samples. */
		ROM_LOAD( "2764.1",       0x8000, 0x2000, 0xa4cddeb8 );
		ROM_LOAD( "2764.2",       0xa000, 0x2000, 0xe9919365 );
		ROM_LOAD( "2764.3",       0xc000, 0x2000, 0xc3ec42e1 );
		ROM_LOAD( "2764.4",       0xe000, 0x2000, 0x76998389 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "hyprolym.h16", 0x0000, 0x2000, 0x768bb63d );
		ROM_LOAD( "hyprolym.h15", 0x2000, 0x2000, 0x3af0e2a8 );
		ROM_LOAD( "h14_e10.bin",  0x4000, 0x2000, 0xc2166a5c );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "c11_d06.bin",  0x0000, 0x2000, 0x82e2185a );
		ROM_LOAD( "c12_d07.bin",  0x2000, 0x2000, 0x800ff1f1 );
		ROM_LOAD( "c13_d08.bin",  0x4000, 0x2000, 0xd9faf183 );
		ROM_LOAD( "c14_d09.bin",  0x6000, 0x2000, 0x5886c802 );
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 );
		ROM_LOAD( "tfprom.1",     0x0000, 0x0020, 0xd55f30b5 );/* palette */
		ROM_LOAD( "tfprom.3",     0x0020, 0x0100, 0xd2ba4d32 );/* sprite lookup table */
		ROM_LOAD( "tfprom.2",     0x0120, 0x0100, 0x053e5861 );/* char lookup table */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mastkin = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "mk3",          0x8000, 0x2000, 0x9f80d6ae );
		ROM_LOAD( "mk4",          0xa000, 0x2000, 0x99f361e7 );
		ROM_LOAD( "mk5",          0xe000, 0x2000, 0x143d76ce );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "mk1",          0x0000, 0x2000, 0x95bf79b6 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mk12",         0x0000, 0x2000, 0x8b1a19cf );
		ROM_LOAD( "mk11",         0x2000, 0x2000, 0x1a56d24d );
		ROM_LOAD( "mk10",         0x4000, 0x2000, 0xe7d05634 );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "mk6",          0x0000, 0x2000, 0x18fbe047 );
		ROM_LOAD( "mk7",          0x2000, 0x2000, 0x47dee791 );
		ROM_LOAD( "mk8",          0x4000, 0x2000, 0x9c091ead );
		ROM_LOAD( "mk9",          0x6000, 0x2000, 0x5c8ed3fe );
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 );
		ROM_LOAD( "prom.1",       0x0000, 0x0020, 0x00000000 );/* palette */
		ROM_LOAD( "prom.3",       0x0020, 0x0100, 0x00000000 );/* sprite lookup table */
		ROM_LOAD( "prom.2",       0x0120, 0x0100, 0x00000000 );/* char lookup table */
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* 64k for speech rom */
		ROM_LOAD( "mk2",          0x0000, 0x2000, 0xf546a56b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_whizquiz = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "ic9_a1.bin",   0xe000, 0x2000, 0x608e1ff3 );/* encrypted? */
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );    /* questions data */
		ROM_LOAD( "ic1_q06.bin",  0x00000, 0x8000, 0xc62f25b1 );
		ROM_LOAD( "ic2_q28.bin",  0x08000, 0x8000, 0x2bd00476 );
		ROM_LOAD( "ic3_q27.bin",  0x10000, 0x8000, 0x46d28aaf );
		ROM_LOAD( "ic4_q23.bin",  0x18000, 0x8000, 0x3f46f702 );
		ROM_LOAD( "ic5_q26.bin",  0x20000, 0x8000, 0x9d130515 );
		ROM_LOAD( "ic6_q09.bin",  0x28000, 0x8000, 0x636f89b4 );
		ROM_LOAD( "ic7_q15.bin",  0x30000, 0x8000, 0xb35332b1 );
		ROM_LOAD( "ic8_q19.bin",  0x38000, 0x8000, 0x8d152da0 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "02c.bin",      0x0000, 0x2000, 0x3daca93a );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "16h.bin",      0x0000, 0x2000, 0xe6728bda );
		ROM_LOAD( "15h.bin",      0x2000, 0x2000, 0x9c067ef4 );
		ROM_LOAD( "14h.bin",      0x4000, 0x2000, 0x3bbad920 );
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "11c.bin",      0x0000, 0x2000, 0x87d060d4 );
		ROM_LOAD( "14c.bin",      0x2000, 0x2000, 0x5bff1607 );
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 );
		ROM_LOAD( "prom.1",       0x0000, 0x0020, 0x00000000 );/* palette */
		ROM_LOAD( "prom.3",       0x0020, 0x0100, 0x00000000 );/* sprite lookup table */
		ROM_LOAD( "prom.2",       0x0120, 0x0100, 0x00000000 );/* char lookup table */
	ROM_END(); }}; 
	
	
	static DRIVER_INIT( trackfld )
	{
		konami1_decode();
	}
	
	static DRIVER_INIT( mastkin )
	{
		UINT8 *prom = memory_region(REGION_PROMS);
		int i;
	
		/* build a fake lookup table since we don't have the color PROMs */
		for (i = 0;i < 0x0200;i++)
		{
			if ((i & 0x0f) == 0) prom[i+0x20] = 0;
			else prom[i + 0x20] = (i + i/16) & 0x0f;
		}
	}
	
	
	public static GameDriver driver_trackfld	   = new GameDriver("1983"	,"trackfld"	,"trackfld.java"	,rom_trackfld,null	,machine_driver_trackfld	,input_ports_trackfld	,init_trackfld	,ROT0	,	"Konami", "Track & Field" )
	public static GameDriver driver_trackflc	   = new GameDriver("1983"	,"trackflc"	,"trackfld.java"	,rom_trackflc,driver_trackfld	,machine_driver_trackfld	,input_ports_trackfld	,init_trackfld	,ROT0	,	"Konami (Centuri license)", "Track & Field (Centuri)" )
	public static GameDriver driver_hyprolym	   = new GameDriver("1983"	,"hyprolym"	,"trackfld.java"	,rom_hyprolym,driver_trackfld	,machine_driver_trackfld	,input_ports_trackfld	,init_trackfld	,ROT0	,	"Konami", "Hyper Olympic" )
	public static GameDriver driver_hyprolyb	   = new GameDriver("1983"	,"hyprolyb"	,"trackfld.java"	,rom_hyprolyb,driver_trackfld	,machine_driver_hyprolyb	,input_ports_trackfld	,init_trackfld	,ROT0	,	"bootleg", "Hyper Olympic (bootleg)" )
	public static GameDriver driver_whizquiz	   = new GameDriver("1985"	,"whizquiz"	,"trackfld.java"	,rom_whizquiz,null	,machine_driver_trackfld	,input_ports_trackfld	,init_mastkin	,ROT0	,	"Zilec-Zenitone", "Whiz Quiz", GAME_NOT_WORKING )
	public static GameDriver driver_mastkin	   = new GameDriver("1988"	,"mastkin"	,"trackfld.java"	,rom_mastkin,null	,machine_driver_mastkin	,input_ports_mastkin	,init_mastkin	,ROT0	,	"Du Tech", "The Masters of Kin", GAME_WRONG_COLORS )
}
