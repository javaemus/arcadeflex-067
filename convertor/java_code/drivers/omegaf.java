/***************************************************************************

Omega Fighter
----------------------
driver by Yochizo

This driver is heavily dependent on the Raine source.
Very thanks to Richard Bush and the Raine team.


Supported games :
==================
 Omega Fighter     (C) 1989 UPL
 Atomic Robokid    (C) 1988 UPL

Known issues :
================
 - Dip switch settings in Atomic Robokid may be wrong.
 - Cocktail mode has not been supported yet.
 - Omega Fighter has a input protection. Currently it is hacked instead
   of emulated.
 - I don't know if Omega Fighter uses sprite overdraw flag or not.
 - Sometimes sprites stays behind the screen in Atomic Robokid due to
   incomplete sprite overdraw emulation.
 - Currently it has not been implemented palette marking in sprite
   overdraw mode.
 - When RAM and ROM check and color test mode, the palette is overflows.
   16 bit color is needed ?

TODO :
========
 - Correct dip switch settings in Atomic Robokid
 - Support cocktail mode
 - Emulate input protection for Omega Fighter.

NOTE :
========
 - To skip dip setting display, press 1P + 2P start in Atomic Robokid.

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class omegaf
{
	
	
	/**************************************************************************
	  Variables
	**************************************************************************/
	
	extern unsigned char *omegaf_fg_videoram;
	extern size_t omegaf_fgvideoram_size;
	
	extern unsigned char *omegaf_bg0_scroll_x;
	extern unsigned char *omegaf_bg1_scroll_x;
	extern unsigned char *omegaf_bg2_scroll_x;
	extern unsigned char *omegaf_bg0_scroll_y;
	extern unsigned char *omegaf_bg1_scroll_y;
	extern unsigned char *omegaf_bg2_scroll_y;
	
	
	VIDEO_START( omegaf );
	VIDEO_START( robokid );
	VIDEO_UPDATE( omegaf );
	
	static int omegaf_bank_latch = 2;
	
	
	/**************************************************************************
	  Initializers
	**************************************************************************/
	
	static DRIVER_INIT( omegaf )
	{
		unsigned char *RAM = memory_region(REGION_CPU1);
	
		/* Hack the input protection. $00 and $01 code is written to $C005 */
		/* and $C006.                                                      */
	
		RAM[0x029a] = 0x00;
		RAM[0x029b] = 0x00;
		RAM[0x02a6] = 0x00;
		RAM[0x02a7] = 0x00;
	
		RAM[0x02b2] = 0xC9;
		RAM[0x02b5] = 0xC9;
		RAM[0x02c9] = 0xC9;
		RAM[0x02f6] = 0xC9;
	
		RAM[0x05f0] = 0x00;
		RAM[0x054c] = 0x04;
		RAM[0x0557] = 0x03;
	
	
		/* Fix ROM check */
	
		RAM[0x0b8d] = 0x00;
		RAM[0x0b8e] = 0x00;
		RAM[0x0b8f] = 0x00;
	}
	
	
	/**************************************************************************
	  Interrupts
	**************************************************************************/
	
	static INTERRUPT_GEN( omegaf_interrupt )
	{
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0xd7);	/* RST 10h */
	}
	
	
	/**************************************************************************
	  Inputs
	**************************************************************************/
	
	static InputPortPtr input_ports_omegaf = new InputPortPtr(){ public void handler() { 
		PORT_START(); 			/* Player 1 inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 			/* Player 2 inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 			/* System inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_SERVICE );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 			/* DSW 0 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x06, 0x06, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x06, "Normal" );
		PORT_DIPSETTING(    0x02, "Hard" );
		PORT_DIPSETTING(    0x04, "Hardest" );
		PORT_SERVICE( 0x08, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Cabinet"));
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0xc0, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPSETTING(    0x80, "5" );
	
		PORT_START();  			/* DSW 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "20k" );
		PORT_DIPSETTING(    0x03, "30k" );
		PORT_DIPSETTING(    0x01, "50k" );
		PORT_DIPSETTING(    0x02, "100k" );
		PORT_DIPNAME( 0x1c, 0x1c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x1c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x14, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_4C") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_robokid = new InputPortPtr(){ public void handler() { 
		PORT_START(); 			/* Player 1 inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 			/* Player 2 inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 			/* System inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_SERVICE );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 			/* DSW 0 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x02, "50k, then every 100k" );
		PORT_DIPSETTING(	0x00, "None" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x04, "Normal" );
		PORT_DIPSETTING(    0x00, "Difficult" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x80, "2" );
		PORT_DIPSETTING(    0xc0, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPSETTING(    0x00, "5" );
	
		PORT_START();  			/* DSW 1 */
		PORT_SERVICE( 0x01, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unused") );
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
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_4C") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_robokidj = new InputPortPtr(){ public void handler() { 
		PORT_START(); 			/* Player 1 inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 			/* Player 2 inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 			/* System inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_SERVICE );/* keep pressed during boot to enter service mode */
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 			/* DSW 0 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x02, "30k, then every 50k" );
		PORT_DIPSETTING(	0x00, "50k, then every 80k" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x04, "Normal" );
		PORT_DIPSETTING(    0x00, "Difficult" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x80, "2" );
		PORT_DIPSETTING(    0xc0, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPSETTING(    0x00, "5" );
	
		PORT_START();  			/* DSW 1 */
		PORT_SERVICE( 0x01, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unused") );
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
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_4C") );
	INPUT_PORTS_END(); }}; 
	
	
	/**************************************************************************
	  Memory handlers
	**************************************************************************/
	
	public static WriteHandlerPtr omegaf_bankselect_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		unsigned char *RAM = memory_region(REGION_CPU1);
		int bankaddress;
	
		if ( (data & 0x0f) != omegaf_bank_latch )
		{
			omegaf_bank_latch = data & 0x0f;
	
			if (omegaf_bank_latch < 2)
				bankaddress = omegaf_bank_latch * 0x4000;
			else
				bankaddress = 0x10000 + ( (omegaf_bank_latch - 2) * 0x4000);
			cpu_setbank( 1, &RAM[bankaddress] );	 /* Select 16 banks of 16k */
		}
	} };
	
	
	/**************************************************************************
	  Memory maps
	**************************************************************************/
	
	public static Memory_ReadAddress omegaf_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xc000, input_port_2_r ),			/* system input */
		new Memory_ReadAddress( 0xc001, 0xc001, input_port_0_r ),			/* player 1 input */
		new Memory_ReadAddress( 0xc002, 0xc002, input_port_1_r ),			/* player 2 input */
		new Memory_ReadAddress( 0xc003, 0xc003, input_port_3_r ),			/* DSW 1 input */
		new Memory_ReadAddress( 0xc004, 0xc004, input_port_4_r ),			/* DSW 2 input */
		new Memory_ReadAddress( 0xc005, 0xc005, MRA_NOP ),
		new Memory_ReadAddress( 0xc006, 0xc006, MRA_NOP ),
		new Memory_ReadAddress( 0xc100, 0xc105, MRA_RAM ),
		new Memory_ReadAddress( 0xc200, 0xc205, MRA_RAM ),
		new Memory_ReadAddress( 0xc300, 0xc305, MRA_RAM ),
		new Memory_ReadAddress( 0xc400, 0xc7ff, omegaf_bg0_videoram_r ),	/* BG0 video RAM */
		new Memory_ReadAddress( 0xc800, 0xcbff, omegaf_bg1_videoram_r ),	/* BG1 video RAM */
		new Memory_ReadAddress( 0xcc00, 0xcfff, omegaf_bg2_videoram_r ),	/* BG2 video RAM */
		new Memory_ReadAddress( 0xd000, 0xd7ff, MRA_RAM ),				/* FG RAM */
		new Memory_ReadAddress( 0xd800, 0xdfff, paletteram_r ),			/* palette RAM */
		new Memory_ReadAddress( 0xe000, 0xf9ff, MRA_RAM ),				/* RAM */
		new Memory_ReadAddress( 0xfa00, 0xffff, MRA_RAM ),				/* sprite RAM */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress omegaf_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc000, soundlatch_w ),
		new Memory_WriteAddress( 0xc001, 0xc001, MWA_NOP ),
		new Memory_WriteAddress( 0xc002, 0xc002, omegaf_bankselect_w ),
		new Memory_WriteAddress( 0xc003, 0xc003, omegaf_sprite_overdraw_w ),
		new Memory_WriteAddress( 0xc004, 0xc004, MWA_NOP ),							/* input protection */
		new Memory_WriteAddress( 0xc005, 0xc005, MWA_NOP ),							/* input protection */
		new Memory_WriteAddress( 0xc006, 0xc006, MWA_NOP ),							/* input protection */
		new Memory_WriteAddress( 0xc100, 0xc101, omegaf_bg0_scrollx_w, omegaf_bg0_scroll_x ),
		new Memory_WriteAddress( 0xc102, 0xc103, omegaf_bg0_scrolly_w, omegaf_bg0_scroll_y ),
		new Memory_WriteAddress( 0xc104, 0xc104, omegaf_bg0_enabled_w ),				/* BG0 enabled */
		new Memory_WriteAddress( 0xc105, 0xc105, omegaf_bg0_bank_w ),					/* BG0 bank select */
		new Memory_WriteAddress( 0xc200, 0xc201, omegaf_bg1_scrollx_w, omegaf_bg1_scroll_x ),
		new Memory_WriteAddress( 0xc202, 0xc203, omegaf_bg1_scrolly_w, omegaf_bg1_scroll_y ),
		new Memory_WriteAddress( 0xc204, 0xc204, omegaf_bg1_enabled_w ),				/* BG1 enabled */
		new Memory_WriteAddress( 0xc205, 0xc205, omegaf_bg1_bank_w ),					/* BG1 bank select */
		new Memory_WriteAddress( 0xc300, 0xc301, omegaf_bg2_scrollx_w, omegaf_bg2_scroll_x ),
		new Memory_WriteAddress( 0xc302, 0xc303, omegaf_bg2_scrolly_w, omegaf_bg2_scroll_y ),
		new Memory_WriteAddress( 0xc304, 0xc304, omegaf_bg2_enabled_w ),				/* BG2 enabled */
		new Memory_WriteAddress( 0xc305, 0xc305, omegaf_bg2_bank_w ),					/* BG2 bank select */
		new Memory_WriteAddress( 0xc400, 0xc7ff, omegaf_bg0_videoram_w ),				/* BG0 video RAM */
		new Memory_WriteAddress( 0xc800, 0xcbff, omegaf_bg1_videoram_w ),				/* BG1 video RAM */
		new Memory_WriteAddress( 0xcc00, 0xcfff, omegaf_bg2_videoram_w ),				/* BG2 video RAM */
		new Memory_WriteAddress( 0xd000, 0xd7ff, omegaf_fgvideoram_w, omegaf_fg_videoram ),
		new Memory_WriteAddress( 0xd800, 0xdfff, paletteram_RRRRGGGGBBBBxxxx_swap_w, paletteram ),
		new Memory_WriteAddress( 0xe000, 0xf9ff, MWA_RAM ),							/* RAM */
		new Memory_WriteAddress( 0xfa00, 0xffff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress robokid_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, paletteram_r ),			/* paletrte RAM */
		new Memory_ReadAddress( 0xc800, 0xcfff, MRA_RAM ),				/* FG RAM */
		new Memory_ReadAddress( 0xd000, 0xd3ff, omegaf_bg2_videoram_r ),
		new Memory_ReadAddress( 0xd400, 0xd7ff, omegaf_bg1_videoram_r ),
		new Memory_ReadAddress( 0xd800, 0xdbff, omegaf_bg0_videoram_r ),
		new Memory_ReadAddress( 0xdc00, 0xdc00, input_port_2_r ),			/* system input */
		new Memory_ReadAddress( 0xdc01, 0xdc01, input_port_0_r ),			/* player 1 input */
		new Memory_ReadAddress( 0xdc02, 0xdc02, input_port_1_r ),			/* player 2 input */
		new Memory_ReadAddress( 0xdc03, 0xdc03, input_port_3_r ),			/* DSW 1 input */
		new Memory_ReadAddress( 0xdc04, 0xdc04, input_port_4_r ),			/* DSW 2 input */
		new Memory_ReadAddress( 0xdd00, 0xdd05, MRA_RAM ),
		new Memory_ReadAddress( 0xde00, 0xde05, MRA_RAM ),
		new Memory_ReadAddress( 0xdf00, 0xdf05, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xf9ff, MRA_RAM ),				/* RAM */
		new Memory_ReadAddress( 0xfa00, 0xffff, MRA_RAM ),				/* sprite RAM */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress robokid_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, paletteram_RRRRGGGGBBBBxxxx_swap_w, paletteram ),
		new Memory_WriteAddress( 0xc800, 0xcfff, omegaf_fgvideoram_w, omegaf_fg_videoram ),
		new Memory_WriteAddress( 0xd000, 0xd3ff, robokid_bg2_videoram_w ),				/* BG2 video RAM */
		new Memory_WriteAddress( 0xd400, 0xd7ff, robokid_bg1_videoram_w ),				/* BG1 video RAM */
		new Memory_WriteAddress( 0xd800, 0xdbff, robokid_bg0_videoram_w ),				/* BG0 video RAM */
		new Memory_WriteAddress( 0xdc00, 0xdc00, soundlatch_w ),
		new Memory_WriteAddress( 0xdc02, 0xdc02, omegaf_bankselect_w ),
		new Memory_WriteAddress( 0xdc03, 0xdc03, omegaf_sprite_overdraw_w ),
		new Memory_WriteAddress( 0xdd00, 0xdd01, omegaf_bg0_scrollx_w, omegaf_bg0_scroll_x ),
		new Memory_WriteAddress( 0xdd02, 0xdd03, omegaf_bg0_scrolly_w, omegaf_bg0_scroll_y ),
		new Memory_WriteAddress( 0xdd04, 0xdd04, omegaf_bg0_enabled_w ),				/* BG0 enabled */
		new Memory_WriteAddress( 0xdd05, 0xdd05, omegaf_bg0_bank_w ),					/* BG0 bank select */
		new Memory_WriteAddress( 0xde00, 0xde01, omegaf_bg1_scrollx_w, omegaf_bg1_scroll_x ),
		new Memory_WriteAddress( 0xde02, 0xde03, omegaf_bg1_scrolly_w, omegaf_bg1_scroll_y ),
		new Memory_WriteAddress( 0xde04, 0xde04, omegaf_bg1_enabled_w ),				/* BG1 enabled */
		new Memory_WriteAddress( 0xde05, 0xde05, omegaf_bg1_bank_w ),					/* BG1 bank select */
		new Memory_WriteAddress( 0xdf00, 0xdf01, omegaf_bg2_scrollx_w, omegaf_bg2_scroll_x ),
		new Memory_WriteAddress( 0xdf02, 0xdf03, omegaf_bg2_scrolly_w, omegaf_bg2_scroll_y ),
		new Memory_WriteAddress( 0xdf04, 0xdf04, omegaf_bg2_enabled_w ),				/* BG2 enabled */
		new Memory_WriteAddress( 0xdf05, 0xdf05, omegaf_bg2_bank_w ),					/* BG2 bank select */
		new Memory_WriteAddress( 0xe000, 0xf9ff, MWA_RAM ),							/* RAM */
		new Memory_WriteAddress( 0xfa00, 0xffff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe000, soundlatch_r ),
		new Memory_ReadAddress( 0xefee, 0xefee, MRA_NOP ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe000, MWA_NOP ),
		new Memory_WriteAddress( 0xeff5, 0xeff6, MWA_NOP ),	/* sample frequency ??? */
		new Memory_WriteAddress( 0xefee, 0xefee, MWA_NOP ),	/* chip command ?? */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0x0000, YM2203_status_port_0_r ),
		new IO_ReadPort( 0x0001, 0x0001, YM2203_read_port_0_r ),
		new IO_ReadPort( 0x0080, 0x0080, YM2203_status_port_1_r ),
		new IO_ReadPort( 0x0081, 0x0081, YM2203_read_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0x0000, YM2203_control_port_0_w ),
		new IO_WritePort( 0x0001, 0x0001, YM2203_write_port_0_w ),
		new IO_WritePort( 0x0080, 0x0080, YM2203_control_port_1_w ),
		new IO_WritePort( 0x0081, 0x0081, YM2203_write_port_1_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/**************************************************************************
	  GFX decoding
	**************************************************************************/
	
	static GfxLayout omegaf_charlayout = new GfxLayout
	(
		8, 8,	/* 8x8 characters */
		1024,	/* 1024 characters */
		4,		/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28 },
		new int[] { 32*0, 32*1, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7 },
		8*32
	);
	
	static GfxLayout omegaf_spritelayout = new GfxLayout
	(
		16, 16,	/* 16x16 characters */
		1024,
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28,
			64*8+0, 64*8+4, 64*8+8, 64*8+12, 64*8+16, 64*8+20, 64*8+24, 64*8+28 },
		new int[] { 32*0, 32*1, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7,
			32*8, 32*9, 32*10, 32*11, 32*12, 32*13, 32*14, 32*15 },
		16*64
	);
	
	static GfxLayout omegaf_bigspritelayout = new GfxLayout
	(
		32, 32,	/* 32x32 characters */
		256,
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] {	0, 4, 8, 12, 16, 20, 24, 28,
			64*8+0, 64*8+4, 64*8+8, 64*8+12, 64*8+16, 64*8+20, 64*8+24, 64*8+28,
			128*16+0, 128*16+4, 128*16+8, 128*16+12, 128*16+16, 128*16+20, 128*16+24, 128*16+28,
			128*16+64*8+0, 128*16+64*8+4, 128*16+64*8+8, 128*16+64*8+12, 128*16+64*8+16, 128*16+64*8+20, 128*16+64*8+24, 128*16+64*8+28 },
		new int[] { 32*0, 32*1, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7,
			32*8, 32*9, 32*10, 32*11, 32*12, 32*13, 32*14, 32*15,
			64*16+32*0, 64*16+32*1, 64*16+32*2, 64*16+32*3, 64*16+32*4, 64*16+32*5, 64*16+32*6, 64*16+32*7,
			64*16+32*8, 64*16+32*9, 64*16+32*10, 64*16+32*11, 64*16+32*12, 64*16+32*13, 64*16+32*14, 64*16+32*15, 64*16+32*16 },
		16*64*4
	);
	
	static GfxLayout omegaf_bglayout = new GfxLayout
	(
		16, 16,	/* 16x16 characters */
		4096,
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28,
			64*8+0, 64*8+4, 64*8+8, 64*8+12, 64*8+16, 64*8+20, 64*8+24, 64*8+28 },
		new int[] { 32*0, 32*1, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7,
			32*8, 32*9, 32*10, 32*11, 32*12, 32*13, 32*14, 32*15 },
		16*64
	);
	
	static GfxLayout robokid_spritelayout = new GfxLayout
	(
		16, 16,	/* 16x16 characters */
		2048,
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28,
			64*8+0, 64*8+4, 64*8+8, 64*8+12, 64*8+16, 64*8+20, 64*8+24, 64*8+28 },
		new int[] { 32*0, 32*1, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7,
			32*8, 32*9, 32*10, 32*11, 32*12, 32*13, 32*14, 32*15 },
		16*64
	);
	
	static GfxLayout robokid_bigspritelayout = new GfxLayout
	(
		32, 32,	/* 32x32 characters */
		512,
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] {	0, 4, 8, 12, 16, 20, 24, 28,
			64*8+0, 64*8+4, 64*8+8, 64*8+12, 64*8+16, 64*8+20, 64*8+24, 64*8+28,
			128*16+0, 128*16+4, 128*16+8, 128*16+12, 128*16+16, 128*16+20, 128*16+24, 128*16+28,
			128*16+64*8+0, 128*16+64*8+4, 128*16+64*8+8, 128*16+64*8+12, 128*16+64*8+16, 128*16+64*8+20, 128*16+64*8+24, 128*16+64*8+28 },
		new int[] { 32*0, 32*1, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7,
			32*8, 32*9, 32*10, 32*11, 32*12, 32*13, 32*14, 32*15,
			64*16+32*0, 64*16+32*1, 64*16+32*2, 64*16+32*3, 64*16+32*4, 64*16+32*5, 64*16+32*6, 64*16+32*7,
			64*16+32*8, 64*16+32*9, 64*16+32*10, 64*16+32*11, 64*16+32*12, 64*16+32*13, 64*16+32*14, 64*16+32*15, 64*16+32*16 },
		16*64*4
	);
	
	static GfxDecodeInfo omegaf_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, omegaf_bglayout,         0*16, 16),
		new GfxDecodeInfo( REGION_GFX2, 0, omegaf_bglayout,         0*16, 16),
		new GfxDecodeInfo( REGION_GFX3, 0, omegaf_bglayout,         0*16, 16),
		new GfxDecodeInfo( REGION_GFX4, 0, omegaf_spritelayout,    32*16, 16),
		new GfxDecodeInfo( REGION_GFX4, 0, omegaf_bigspritelayout, 32*16, 16),
		new GfxDecodeInfo( REGION_GFX5, 0, omegaf_charlayout,      48*16, 16),
		new GfxDecodeInfo( -1) /* end of array */
	};
	
	static GfxDecodeInfo robokid_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, omegaf_bglayout,          0*16, 16),
		new GfxDecodeInfo( REGION_GFX2, 0, omegaf_bglayout,          0*16, 16),
		new GfxDecodeInfo( REGION_GFX3, 0, omegaf_bglayout,          0*16, 16),
		new GfxDecodeInfo( REGION_GFX4, 0, robokid_spritelayout,    32*16, 16),
		new GfxDecodeInfo( REGION_GFX4, 0, robokid_bigspritelayout, 32*16, 16),
		new GfxDecodeInfo( REGION_GFX5, 0, omegaf_charlayout,       48*16, 16),
		new GfxDecodeInfo( -1) /* end of array */
	};
	
	
	/**************************************************************************
	  Machine drivers
	**************************************************************************/
	
	static struct YM2203interface ym2203_interface =
	{
		2,	 /* 2 chips */
		12000000/8,
		{ YM2203_VOL(35, 35), YM2203_VOL(35, 35)},
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 }
	};
	
	static MACHINE_DRIVER_START( omegaf )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,12000000/2)		/* 12000000/2 ??? */
		MDRV_CPU_MEMORY(omegaf_readmem,omegaf_writemem)	/* very sensitive to these settings */
		MDRV_CPU_VBLANK_INT(omegaf_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)		/* 12000000/3 ??? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)					/* number of slices per frame */
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(128*16, 32*16)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 4*8, 28*8-1)
		MDRV_GFXDECODE(omegaf_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(omegaf)
		MDRV_VIDEO_UPDATE(omegaf)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( robokid )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,12000000/2)		/* 12000000/2 ??? */
		MDRV_CPU_MEMORY(robokid_readmem,robokid_writemem)	/* very sensitive to these settings */
		MDRV_CPU_VBLANK_INT(omegaf_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)		/* 12000000/3 ??? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)					/* number of slices per frame */
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 4*8, 28*8-1)
		MDRV_GFXDECODE(robokid_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(robokid)
		MDRV_VIDEO_UPDATE(omegaf)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END
	
	
	/**************************************************************************
	  ROM loaders
	**************************************************************************/
	
	static RomLoadPtr rom_omegaf = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );					/* main CPU */
		ROM_LOAD( "1.5",          0x00000, 0x08000, 0x57a7fd96 );
		ROM_CONTINUE(             0x10000, 0x18000 );
		ROM_LOAD( "6.4l",         0x28000, 0x20000, 0x6277735c );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );					/* sound CPU */
		ROM_LOAD( "7.7m",         0x00000, 0x10000, 0xd40fc8d5 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );	/* BG0 */
		ROM_LOAD( "2back1.27b",   0x00000, 0x80000, 0x21f8a32e );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );	/* BG1 */
		ROM_LOAD( "1back2.15b",   0x00000, 0x80000, 0x6210ddcc );
	
		ROM_REGION( 0x80000, REGION_GFX3, ROMREGION_DISPOSE );	/* BG2 */
		ROM_LOAD( "3back3.5f",    0x00000, 0x80000, 0xc31cae56 );
	
		ROM_REGION( 0x20000, REGION_GFX4, ROMREGION_DISPOSE );	/* sprite */
		ROM_LOAD( "8.23m",        0x00000, 0x20000, 0x0bd2a5d1 );
	
		ROM_REGION( 0x08000, REGION_GFX5, ROMREGION_DISPOSE );	/* FG */
		ROM_LOAD( "4.18h",        0x00000, 0x08000, 0x9e2d8152 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_omegafs = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );					/* main CPU */
		ROM_LOAD( "5.3l",         0x00000, 0x08000, 0x503a3e63 );
		ROM_CONTINUE(             0x10000, 0x18000 );
		ROM_LOAD( "6.4l",         0x28000, 0x20000, 0x6277735c );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );					/* sound CPU */
		ROM_LOAD( "7.7m",         0x00000, 0x10000, 0xd40fc8d5 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );	/* BG0 */
		ROM_LOAD( "2back1.27b",   0x00000, 0x80000, 0x21f8a32e );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );	/* BG1 */
		ROM_LOAD( "1back2.15b",   0x00000, 0x80000, 0x6210ddcc );
	
		ROM_REGION( 0x80000, REGION_GFX3, ROMREGION_DISPOSE );	/* BG2 */
		ROM_LOAD( "3back3.5f",    0x00000, 0x80000, 0xc31cae56 );
	
		ROM_REGION( 0x20000, REGION_GFX4, ROMREGION_DISPOSE );	/* sprite */
		ROM_LOAD( "8.23m",        0x00000, 0x20000, 0x0bd2a5d1 );
	
		ROM_REGION( 0x08000, REGION_GFX5, ROMREGION_DISPOSE );	/* FG */
		ROM_LOAD( "4.18h",        0x00000, 0x08000, 0x9e2d8152 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_robokid = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );					/* main CPU */
		ROM_LOAD( "robokid1.18j", 0x00000, 0x08000, 0x378c21fc );
		ROM_CONTINUE(             0x10000, 0x08000 );
		ROM_LOAD( "robokid2.18k", 0x18000, 0x10000, 0xddef8c5a );
		ROM_LOAD( "robokid3.15k", 0x28000, 0x10000, 0x05295ec3 );
		ROM_LOAD( "robokid4.12k", 0x38000, 0x10000, 0x3bc3977f );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );					/* sound CPU */
		ROM_LOAD( "robokid.k7",   0x00000, 0x10000, 0xf490a2e9 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );	/* BG0 */
		ROM_LOAD( "robokid.19c",  0x00000, 0x10000, 0x02220421 );
		ROM_LOAD( "robokid.20c",  0x10000, 0x10000, 0x02d59bc2 );
		ROM_LOAD( "robokid.17d",  0x20000, 0x10000, 0x2fa29b99 );
		ROM_LOAD( "robokid.18d",  0x30000, 0x10000, 0xae15ce02 );
		ROM_LOAD( "robokid.19d",  0x40000, 0x10000, 0x784b089e );
		ROM_LOAD( "robokid.20d",  0x50000, 0x10000, 0xb0b395ed );
		ROM_LOAD( "robokid.19f",  0x60000, 0x10000, 0x0f9071c6 );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );	/* BG1 */
		ROM_LOAD( "robokid.12c",  0x00000, 0x10000, 0x0ab45f94 );
		ROM_LOAD( "robokid.14c",  0x10000, 0x10000, 0x029bbd4a );
		ROM_LOAD( "robokid.15c",  0x20000, 0x10000, 0x7de67ebb );
		ROM_LOAD( "robokid.16c",  0x30000, 0x10000, 0x53c0e582 );
		ROM_LOAD( "robokid.17c",  0x40000, 0x10000, 0x0cae5a1e );
		ROM_LOAD( "robokid.18c",  0x50000, 0x10000, 0x56ac7c8a );
		ROM_LOAD( "robokid.15d",  0x60000, 0x10000, 0xcd632a4d );
		ROM_LOAD( "robokid.16d",  0x70000, 0x10000, 0x18d92b2b );
	
		ROM_REGION( 0x80000, REGION_GFX3, ROMREGION_DISPOSE );	/* BG2 */
		ROM_LOAD( "robokid.12a",  0x00000, 0x10000, 0xe64d1c10 );
		ROM_LOAD( "robokid.14a",  0x10000, 0x10000, 0x8f9371e4 );
		ROM_LOAD( "robokid.15a",  0x20000, 0x10000, 0x469204e7 );
		ROM_LOAD( "robokid.16a",  0x30000, 0x10000, 0x4e340815 );
		ROM_LOAD( "robokid.17a",  0x40000, 0x10000, 0xf0863106 );
		ROM_LOAD( "robokid.18a",  0x50000, 0x10000, 0xfdff7441 );
	
		ROM_REGION( 0x40000, REGION_GFX4, ROMREGION_DISPOSE );	/* sprite */
		ROM_LOAD( "robokid.15f",  0x00000, 0x10000, 0xba61f5ab );
		ROM_LOAD( "robokid.16f",  0x10000, 0x10000, 0xd9b399ce );
		ROM_LOAD( "robokid.17f",  0x20000, 0x10000, 0xafe432b9 );
		ROM_LOAD( "robokid.18f",  0x30000, 0x10000, 0xa0aa2a84 );
	
		ROM_REGION( 0x08000, REGION_GFX5, ROMREGION_DISPOSE );	/* FG */
		ROM_LOAD( "robokid.b9",   0x00000, 0x08000, 0xfac59c3f );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_robokidj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );					/* main CPU */
		ROM_LOAD( "1.29",         0x00000, 0x08000, 0x59a1e2ec );
		ROM_CONTINUE(             0x10000, 0x08000 );
		ROM_LOAD( "2.30",         0x18000, 0x10000, 0xe3f73476 );
		ROM_LOAD( "robokid3.15k", 0x28000, 0x10000, 0x05295ec3 );
		ROM_LOAD( "robokid4.12k", 0x38000, 0x10000, 0x3bc3977f );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );					/* sound CPU */
		ROM_LOAD( "robokid.k7",   0x00000, 0x10000, 0xf490a2e9 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );	/* BG0 */
		ROM_LOAD( "robokid.19c",  0x00000, 0x10000, 0x02220421 );
		ROM_LOAD( "robokid.20c",  0x10000, 0x10000, 0x02d59bc2 );
		ROM_LOAD( "robokid.17d",  0x20000, 0x10000, 0x2fa29b99 );
		ROM_LOAD( "robokid.18d",  0x30000, 0x10000, 0xae15ce02 );
		ROM_LOAD( "robokid.19d",  0x40000, 0x10000, 0x784b089e );
		ROM_LOAD( "robokid.20d",  0x50000, 0x10000, 0xb0b395ed );
		ROM_LOAD( "robokid.19f",  0x60000, 0x10000, 0x0f9071c6 );
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE );	/* BG1 */
		ROM_LOAD( "robokid.12c",  0x00000, 0x10000, 0x0ab45f94 );
		ROM_LOAD( "robokid.14c",  0x10000, 0x10000, 0x029bbd4a );
		ROM_LOAD( "robokid.15c",  0x20000, 0x10000, 0x7de67ebb );
		ROM_LOAD( "robokid.16c",  0x30000, 0x10000, 0x53c0e582 );
		ROM_LOAD( "robokid.17c",  0x40000, 0x10000, 0x0cae5a1e );
		ROM_LOAD( "robokid.18c",  0x50000, 0x10000, 0x56ac7c8a );
		ROM_LOAD( "robokid.15d",  0x60000, 0x10000, 0xcd632a4d );
		ROM_LOAD( "robokid.16d",  0x70000, 0x10000, 0x18d92b2b );
	
		ROM_REGION( 0x80000, REGION_GFX3, ROMREGION_DISPOSE );	/* BG2 */
		ROM_LOAD( "robokid.12a",  0x00000, 0x10000, 0xe64d1c10 );
		ROM_LOAD( "robokid.14a",  0x10000, 0x10000, 0x8f9371e4 );
		ROM_LOAD( "robokid.15a",  0x20000, 0x10000, 0x469204e7 );
		ROM_LOAD( "robokid.16a",  0x30000, 0x10000, 0x4e340815 );
		ROM_LOAD( "robokid.17a",  0x40000, 0x10000, 0xf0863106 );
		ROM_LOAD( "robokid.18a",  0x50000, 0x10000, 0xfdff7441 );
	
		ROM_REGION( 0x40000, REGION_GFX4, ROMREGION_DISPOSE );	/* sprite */
		ROM_LOAD( "robokid.15f",  0x00000, 0x10000, 0xba61f5ab );
		ROM_LOAD( "robokid.16f",  0x10000, 0x10000, 0xd9b399ce );
		ROM_LOAD( "robokid.17f",  0x20000, 0x10000, 0xafe432b9 );
		ROM_LOAD( "robokid.18f",  0x30000, 0x10000, 0xa0aa2a84 );
	
		ROM_REGION( 0x08000, REGION_GFX5, ROMREGION_DISPOSE );	/* FG */
		ROM_LOAD( "robokid.b9",   0x00000, 0x08000, 0xfac59c3f );
	ROM_END(); }}; 
	
	
	/*   ( YEAR  NAME      PARENT   MACHINE  INPUT    INIT      MONITOR COMPANY  FULLNAME                 FLAGS ) */
	public static GameDriver driver_robokid	   = new GameDriver("1988"	,"robokid"	,"omegaf.java"	,rom_robokid,null	,machine_driver_robokid	,input_ports_robokid	,null	,ROT0	,	"UPL",  "Atomic Robo-kid",         GAME_NO_COCKTAIL )
	public static GameDriver driver_robokidj	   = new GameDriver("1988"	,"robokidj"	,"omegaf.java"	,rom_robokidj,driver_robokid	,machine_driver_robokid	,input_ports_robokidj	,null	,ROT0	,	"UPL",  "Atomic Robo-kid (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_omegaf	   = new GameDriver("1989"	,"omegaf"	,"omegaf.java"	,rom_omegaf,null	,machine_driver_omegaf	,input_ports_omegaf	,init_omegaf	,ROT270	,	"UPL",  "Omega Fighter",          GAME_NO_COCKTAIL )
	public static GameDriver driver_omegafs	   = new GameDriver("1989"	,"omegafs"	,"omegaf.java"	,rom_omegafs,driver_omegaf	,machine_driver_omegaf	,input_ports_omegaf	,init_omegaf	,ROT270	,	"UPL",  "Omega Fighter Special",  GAME_NO_COCKTAIL )
}
