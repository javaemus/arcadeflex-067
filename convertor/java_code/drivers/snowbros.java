/***************************************************************************

  Snow Brothers (Toaplan) / SemiCom Hardware
  uses Kaneko's Pandora sprite chip (also used in DJ Boy, Air Buster ..)

  driver by Mike Coates

  Hyper Pacman addition by David Haywood
   + some bits by Nicola Salmoria


Stephh's notes (hyperpac):

  - According to the "Language" Dip Switch, this game is a Korean game.
     (although the Language Dipswitch doesn't affect language, but yes
      I believe SemiCom to be a Korean Company)
  - There is no "cocktail mode", nor way to flip the screen.

todo:

make the originals work.
they're probably all this hardware or a varation on it, they don't work
(most point the interrupt vectors directly at a small area of ram which I'd
guess is shared with the Philips 87c52 mcu, more more plus doesn't point the
vectors there but does have a jump there in the code). See hyperpac for an
example, the protection data for that game was extracted from the bootleg.

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class snowbros
{
	
	
	WRITE16_HANDLER( snowbros_flipscreen_w );
	VIDEO_UPDATE( snowbros );
	VIDEO_UPDATE( wintbob );
	VIDEO_UPDATE( snowbro3 );
	
	static data16_t *hyperpac_ram;
	
	static INTERRUPT_GEN( snowbros_interrupt )
	{
		cpu_set_irq_line(0, cpu_getiloops() + 2, HOLD_LINE);	/* IRQs 4, 3, and 2 */
	}
	
	
	/* Sound Routines */
	
	READ16_HANDLER( snowbros_68000_sound_r )
	{
		int ret;
	
		/* If the sound CPU is running, read the YM3812 status, otherwise
		   just make it pass the test */
		if (Machine->sample_rate != 0)
		{
			ret = soundlatch_r(offset);
		}
		else
		{
			ret = 3;
		}
	
		return ret;
	}
	
	
	static WRITE16_HANDLER( snowbros_68000_sound_w )
	{
		if (ACCESSING_LSB)
		{
			soundlatch_w(offset,data & 0xff);
			cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
		}
	}
	
	static WRITE16_HANDLER( semicom_soundcmd_w )
	{
		if (ACCESSING_LSB) soundlatch_w(0,data & 0xff);
	}
	
	
	/* Snow Bros Memory Map */
	
	static MEMORY_READ16_START( readmem )
		{ 0x000000, 0x03ffff, MRA16_ROM },
		{ 0x100000, 0x103fff, MRA16_RAM },
		{ 0x300000, 0x300001, snowbros_68000_sound_r },
		{ 0x500000, 0x500001, input_port_0_word_r },
		{ 0x500002, 0x500003, input_port_1_word_r },
		{ 0x500004, 0x500005, input_port_2_word_r },
		{ 0x600000, 0x6001ff, MRA16_RAM },
		{ 0x700000, 0x701fff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( writemem )
		{ 0x000000, 0x03ffff, MWA16_ROM },
		{ 0x100000, 0x103fff, MWA16_RAM },
		{ 0x200000, 0x200001, watchdog_reset16_w },
		{ 0x300000, 0x300001, snowbros_68000_sound_w },
		{ 0x400000, 0x400001, snowbros_flipscreen_w },
		{ 0x600000, 0x6001ff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x700000, 0x701fff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x800000, 0x800001, MWA16_NOP },	/* IRQ 4 acknowledge? */
		{ 0x900000, 0x900001, MWA16_NOP },	/* IRQ 3 acknowledge? */
		{ 0xa00000, 0xa00001, MWA16_NOP },	/* IRQ 2 acknowledge? */
	MEMORY_END
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x02, 0x02, YM3812_status_port_0_r ),
		new IO_ReadPort( 0x04, 0x04, soundlatch_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x02, 0x02, YM3812_control_port_0_w ),
		new IO_WritePort( 0x03, 0x03, YM3812_write_port_0_w ),
		new IO_WritePort( 0x04, 0x04, soundlatch_w ),	/* goes back to the main CPU, checked during boot */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/* SemiCom Memory Map
	
	the SemiCom games have slightly more ram and are protected
	sound hardware is also different
	
	*/
	
	static MEMORY_READ16_START( hyperpac_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x10ffff, MRA16_RAM },
	
		{ 0x500000, 0x500001, input_port_0_word_r },
		{ 0x500002, 0x500003, input_port_1_word_r },
		{ 0x500004, 0x500005, input_port_2_word_r },
	
		{ 0x600000, 0x6001ff, MRA16_RAM },
		{ 0x700000, 0x701fff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( hyperpac_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x10ffff, MWA16_RAM, &hyperpac_ram },
		{ 0x300000, 0x300001, semicom_soundcmd_w },
	//	{ 0x400000, 0x400001,  }, ???
		{ 0x600000, 0x6001ff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x700000, 0x701fff, MWA16_RAM, &spriteram16, &spriteram_size },
	
		{ 0x800000, 0x800001, MWA16_NOP },	/* IRQ 4 acknowledge? */
		{ 0x900000, 0x900001, MWA16_NOP },	/* IRQ 3 acknowledge? */
		{ 0xa00000, 0xa00001, MWA16_NOP },	/* IRQ 2 acknowledge? */
	MEMORY_END
	
	public static Memory_ReadAddress hyperpac_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xcfff, MRA_ROM ),
		new Memory_ReadAddress( 0xd000, 0xd7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf001, 0xf001, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0xf008, 0xf008, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hyperpac_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xcfff, MWA_ROM ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf000, 0xf000, YM2151_register_port_0_w ),
		new Memory_WriteAddress( 0xf001, 0xf001, YM2151_data_port_0_w ),
		new Memory_WriteAddress( 0xf002, 0xf002, OKIM6295_data_0_w ),
	//	new Memory_WriteAddress( 0xf006, 0xf006,  ), ???
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/* Snow Bros 3
	close to SemiCom
	
	*/
	
	static MEMORY_READ16_START( readmem3 )
		{ 0x000000, 0x03ffff, MRA16_ROM },
		{ 0x100000, 0x103fff, MRA16_RAM },
	//	{ 0x300000, 0x300001, OKIM6295_status_0_msb_r }, // ?
		{ 0x500000, 0x500001, input_port_0_word_r },
		{ 0x500002, 0x500003, input_port_1_word_r },
		{ 0x500004, 0x500005, input_port_2_word_r },
		{ 0x600000, 0x6003ff, MRA16_RAM },
		{ 0x700000, 0x7021ff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( writemem3 )
		{ 0x000000, 0x03ffff, MWA16_ROM },
		{ 0x100000, 0x103fff, MWA16_RAM },
		{ 0x200000, 0x200001, watchdog_reset16_w },
	//	{ 0x300000, 0x300001, OKIM6295_data_0_msb_w }, // ?
		{ 0x400000, 0x400001, snowbros_flipscreen_w },
		{ 0x600000, 0x6003ff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x700000, 0x7021ff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x800000, 0x800001, MWA16_NOP },	/* IRQ 4 acknowledge? */
		{ 0x900000, 0x900001, MWA16_NOP },	/* IRQ 3 acknowledge? */
		{ 0xa00000, 0xa00001, MWA16_NOP },	/* IRQ 2 acknowledge? */
	MEMORY_END
	
	static InputPortPtr input_ports_snowbros = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* 500001 */
		PORT_DIPNAME( 0x01, 0x00, "Country (Affects Coinage); )
		PORT_DIPSETTING(    0x00, "Europe" );
		PORT_DIPSETTING(    0x01, "America (Romstar license); )
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
	/* Better to implement a coin mode 1-2 stuff later */
		PORT_DIPNAME( 0x30, 0x30, "Coin A Europe/America" );
		PORT_DIPSETTING(    0x00, "4C/1C 2C/3C" );
		PORT_DIPSETTING(    0x10, "3C/1C 2C/1C" );
		PORT_DIPSETTING(    0x20, "2C/1C 1C/2C" );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0xc0, 0xc0, "Coin B Europe/America" );
		PORT_DIPSETTING(    0xc0, "1C/2C 1C/1C" );
		PORT_DIPSETTING(    0x80, "1C/3C 1C/2C" );
		PORT_DIPSETTING(    0x40, "1C/4C 2C/1C" );
		PORT_DIPSETTING(    0x00, "1C/6C 2C/3C" );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x8000, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* Must be low or game stops! */
														/* probably VBlank */
	
		PORT_START(); 	/* 500003 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x02, "Easy" );
		PORT_DIPSETTING(    0x03, "Normal" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x04, "100k and every 200k " );
		PORT_DIPSETTING(    0x0c, "100k Only" );
		PORT_DIPSETTING(    0x08, "200k Only" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x20, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x30, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Yes") );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x8000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* 500005 */
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_snowbroj = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* 500001 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x8000, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* Must be low or game stops! */
														/* probably VBlank */
	
		PORT_START(); 	/* 500003 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x02, "Easy" );
		PORT_DIPSETTING(    0x03, "Normal" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x04, "100k and every 200k " );
		PORT_DIPSETTING(    0x0c, "100k Only" );
		PORT_DIPSETTING(    0x08, "200k Only" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x20, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x30, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Yes") );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x8000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* 500005 */
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_hyperpac = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* 500000.w */
		PORT_DIPNAME( 0x0001, 0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002, 0x0002, DEF_STR( "Lives") );	// "Language" in the "test mode"
		PORT_DIPSETTING(      0x0002, "3" );				// "Korean"
		PORT_DIPSETTING(      0x0000, "5" );				// "English"
		PORT_DIPNAME( 0x001c, 0x001c, DEF_STR( "Coinage") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x0004, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x000c, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x001c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0014, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0x0018, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0060, 0x0060, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0000, "Easy" );
		PORT_DIPSETTING(      0x0060, "Normal" );
		PORT_DIPSETTING(      0x0040, "Hard" );
		PORT_DIPSETTING(      0x0020, "Hardest" );		// "Very Hard"
		PORT_SERVICE( 0x0080, IP_ACTIVE_LOW );
		PORT_BIT( 0x0100, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0200, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0400, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0800, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER1 );// jump
		PORT_BIT( 0x2000, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER1 );// fire
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER1 );// test mode only?
		PORT_BIT( 0x8000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* 500002.w */
		PORT_DIPNAME( 0x0001, 0x0001, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002, 0x0002, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0004, 0x0004, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0004, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0008, 0x0008, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0010, 0x0010, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0020, 0x0020, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0020, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_BIT( 0x0100, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );// jump
		PORT_BIT( 0x2000, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );// fire
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER2 );// test mode only?
		PORT_BIT( 0x8000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* 500004.w */
		PORT_BIT( 0x0100, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x0800, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	/* SnowBros */
	
	static GfxLayout tilelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { STEP8(0,4), STEP8(8*32,4) },
		new int[] { STEP8(0,32), STEP8(16*32,32) },
		32*32
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tilelayout,  0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/* Winter Bobble */
	
	static GfxLayout tilelayout_wb = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { STEP4(3*4,-4), STEP4(7*4,-4), STEP4(11*4,-4), STEP4(15*4,-4) },
		new int[] { STEP16(0,64) },
		16*64
	);
	
	static GfxDecodeInfo gfxdecodeinfo_wb[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tilelayout_wb,  0, 16 ),
		new GfxDecodeInfo( -1 )
	};
	
	/* SemiCom */
	
	static GfxLayout hyperpac_tilelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 4, 0, 8*32+4, 8*32+0, 20,16, 8*32+20, 8*32+16,
		  12, 8, 8*32+12, 8*32+8, 28, 24, 8*32+28, 8*32+24 },
		new int[] { 0*32, 2*32, 1*32, 3*32, 16*32+0*32, 16*32+2*32, 16*32+1*32, 16*32+3*32,
		  4*32, 6*32, 5*32, 7*32, 16*32+4*32, 16*32+6*32, 16*32+5*32, 16*32+7*32 },
		32*32
	);
	
	static GfxDecodeInfo hyperpac_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, hyperpac_tilelayout,  0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/* snow bros 3 */
	
	static GfxLayout sb3_tilebglayout = new GfxLayout
	(
	 	16,16,
	 	RGN_FRAC(1,1),
	 	8,
	 	new int[] {8, 9,10, 11, 0, 1, 2, 3  },
	 	new int[] { 0, 4, 16, 20, 32, 36, 48, 52,
	 	512+0,512+4,512+16,512+20,512+32,512+36,512+48,512+52},
	 	new int[] { 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
	 	1024+0*16,1024+1*64,1024+2*64,1024+3*64,1024+4*64,1024+5*64,1024+6*64,1024+7*64},
	 	32*64
	);
	
	
	static GfxDecodeInfo sb3_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tilelayout,  0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, sb3_tilebglayout,  0, 2 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/* handler called by the 3812/2151 emulator when the internal timers cause an IRQ */
	static void irqhandler(int irq)
	{
		cpu_set_irq_line(1,0,irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	/* SnowBros Sound */
	
	static struct YM3812interface ym3812_interface =
	{
		1,			/* 1 chip */
		3579545,	/* 3.579545 MHz ? (hand tuned) */
		{ 100 },	/* volume */
		{ irqhandler },
	};
	
	/* SemiCom Sound */
	
	static struct YM2151interface ym2151_interface =
	{
		1,
		4000000,	/* 4 MHz??? */
		{ YM3012_VOL(10,MIXER_PAN_LEFT,10,MIXER_PAN_RIGHT) },
		{ irqhandler }
	};
	
	static struct OKIM6295interface okim6295_interface =
	{
		1,			/* 1 chip */
		{ 7575 },		/* 7575Hz playback? */
		{ REGION_SOUND1 },
		{ 100 }
	};
	
	
	static MACHINE_DRIVER_START( snowbros )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", M68000, 8000000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(snowbros_interrupt,3)
	
		MDRV_CPU_ADD_TAG("sound", Z80, 3600000) /* 3.6 MHz ??? */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_UPDATE(snowbros)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("3812", YM3812, ym3812_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( wintbob )
		/* basic machine hardware */
		MDRV_IMPORT_FROM(snowbros)
		MDRV_CPU_REPLACE("main", M68000, 10000000) /* faster cpu on bootleg? otherwise the gfx break up */
	
		/* video hardware */
		MDRV_GFXDECODE(gfxdecodeinfo_wb)
		MDRV_VIDEO_UPDATE(wintbob)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( hyperpac )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(snowbros)
		MDRV_CPU_REPLACE("main", M68000, 16000000) /* 16mhz or 12mhz ? */
		MDRV_CPU_MEMORY(hyperpac_readmem,hyperpac_writemem)
	
		MDRV_CPU_REPLACE("sound", Z80, 4000000) /* 4.0 MHz ??? */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(hyperpac_sound_readmem,hyperpac_sound_writemem)
	
		MDRV_GFXDECODE(hyperpac_gfxdecodeinfo)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("3812",YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( snowbro3 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000) /* 16mhz or 12mhz ? */
		MDRV_CPU_MEMORY(readmem3,writemem3)
		MDRV_CPU_VBLANK_INT(snowbros_interrupt,3)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(sb3_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_UPDATE(snowbro3)
	
		/* sound hardware */
		/* oki for sound */
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_snowbros = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x40000, REGION_CPU1, 0 );/* 6*64k for 68000 code */
		ROM_LOAD16_BYTE( "sn6.bin",  0x00000, 0x20000, 0x4899ddcf );
		ROM_LOAD16_BYTE( "sn5.bin",  0x00001, 0x20000, 0xad310d3f );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for z80 sound code */
		ROM_LOAD( "snowbros.4",   0x0000, 0x8000, 0xe6eab4e4 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ch0",          0x00000, 0x20000, 0x36d84dfe );
		ROM_LOAD( "ch1",          0x20000, 0x20000, 0x76347256 );
		ROM_LOAD( "ch2",          0x40000, 0x20000, 0xfdaa634c );
		ROM_LOAD( "ch3",          0x60000, 0x20000, 0x34024aef );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_snowbroa = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x40000, REGION_CPU1, 0 );/* 6*64k for 68000 code */
		ROM_LOAD16_BYTE( "snowbros.3a",  0x00000, 0x20000, 0x10cb37e1 );
		ROM_LOAD16_BYTE( "snowbros.2a",  0x00001, 0x20000, 0xab91cc1e );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for z80 sound code */
		ROM_LOAD( "snowbros.4",   0x0000, 0x8000, 0xe6eab4e4 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ch0",          0x00000, 0x20000, 0x36d84dfe );
		ROM_LOAD( "ch1",          0x20000, 0x20000, 0x76347256 );
		ROM_LOAD( "ch2",          0x40000, 0x20000, 0xfdaa634c );
		ROM_LOAD( "ch3",          0x60000, 0x20000, 0x34024aef );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_snowbrob = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x40000, REGION_CPU1, 0 );/* 6*64k for 68000 code */
		ROM_LOAD16_BYTE( "sbros3-a",     0x00000, 0x20000, 0x301627d6 );
		ROM_LOAD16_BYTE( "sbros2-a",     0x00001, 0x20000, 0xf6689f41 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for z80 sound code */
		ROM_LOAD( "snowbros.4",   0x0000, 0x8000, 0xe6eab4e4 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ch0",          0x00000, 0x20000, 0x36d84dfe );
		ROM_LOAD( "ch1",          0x20000, 0x20000, 0x76347256 );
		ROM_LOAD( "ch2",          0x40000, 0x20000, 0xfdaa634c );
		ROM_LOAD( "ch3",          0x60000, 0x20000, 0x34024aef );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_snowbroj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x40000, REGION_CPU1, 0 );/* 6*64k for 68000 code */
		ROM_LOAD16_BYTE( "snowbros.3",   0x00000, 0x20000, 0x3f504f9e );
		ROM_LOAD16_BYTE( "snowbros.2",   0x00001, 0x20000, 0x854b02bc );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for z80 sound code */
		ROM_LOAD( "snowbros.4",   0x0000, 0x8000, 0xe6eab4e4 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		/* The gfx ROM (snowbros.1) was bad, I'm using the ones from the other sets. */
		ROM_LOAD( "ch0",          0x00000, 0x20000, 0x36d84dfe );
		ROM_LOAD( "ch1",          0x20000, 0x20000, 0x76347256 );
		ROM_LOAD( "ch2",          0x40000, 0x20000, 0xfdaa634c );
		ROM_LOAD( "ch3",          0x60000, 0x20000, 0x34024aef );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_wintbob = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x40000, REGION_CPU1, 0 );/* 6*64k for 68000 code */
		ROM_LOAD16_BYTE( "wb03.bin", 0x00000, 0x10000, 0xdf56e168 );
		ROM_LOAD16_BYTE( "wb01.bin", 0x00001, 0x10000, 0x05722f17 );
		ROM_LOAD16_BYTE( "wb04.bin", 0x20000, 0x10000, 0x53be758d );
		ROM_LOAD16_BYTE( "wb02.bin", 0x20001, 0x10000, 0xfc8e292e );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for z80 sound code */
		ROM_LOAD( "wb05.bin",     0x0000, 0x10000, 0x53fe59df );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		/* probably identical data to Snow Bros, in a different format */
		ROM_LOAD16_BYTE( "wb13.bin",     0x00000, 0x10000, 0x426921de );
		ROM_LOAD16_BYTE( "wb06.bin",     0x00001, 0x10000, 0x68204937 );
		ROM_LOAD16_BYTE( "wb12.bin",     0x20000, 0x10000, 0xef4e04c7 );
		ROM_LOAD16_BYTE( "wb07.bin",     0x20001, 0x10000, 0x53f40978 );
		ROM_LOAD16_BYTE( "wb11.bin",     0x40000, 0x10000, 0x41cb4563 );
		ROM_LOAD16_BYTE( "wb08.bin",     0x40001, 0x10000, 0x9497b88c );
		ROM_LOAD16_BYTE( "wb10.bin",     0x60000, 0x10000, 0x5fa22b1e );
		ROM_LOAD16_BYTE( "wb09.bin",     0x60001, 0x10000, 0x9be718ca );
	ROM_END(); }}; 
	
	/* SemiCom Games */
	
	static RomLoadPtr rom_hyperpac = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x40000, REGION_CPU1, 0 );/* 68000 Code */
		ROM_LOAD16_BYTE( "hyperpac.h12", 0x00001, 0x20000, 0x2cf0531a );
		ROM_LOAD16_BYTE( "hyperpac.i12", 0x00000, 0x20000, 0x9c7d85b8 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 Code */
		ROM_LOAD( "hyperpac.u1", 0x00000, 0x10000 , 0x03faf88e );
	
		ROM_REGION( 0x040000, REGION_SOUND1, 0 );/* Samples */
		ROM_LOAD( "hyperpac.j15", 0x00000, 0x40000, 0xfb9f468d );
	
		ROM_REGION( 0x0c0000, REGION_GFX1, 0 );/* Sprites */
		ROM_LOAD( "hyperpac.a4", 0x000000, 0x40000, 0xbd8673da );
		ROM_LOAD( "hyperpac.a5", 0x040000, 0x40000, 0x5d90cd82 );
		ROM_LOAD( "hyperpac.a6", 0x080000, 0x40000, 0x61d86e63 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hyperpcb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );/* 68000 Code */
		ROM_LOAD16_BYTE( "hpacuh12.bin", 0x00001, 0x20000, 0x633ab2c6 );
		ROM_LOAD16_BYTE( "hpacui12.bin", 0x00000, 0x20000, 0x23dc00d1 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 Code */
		ROM_LOAD( "hyperpac.u1", 0x00000, 0x10000 , 0x03faf88e );// was missing from this set, using the one from the original
	
		ROM_REGION( 0x040000, REGION_SOUND1, 0 );/* Samples */
		ROM_LOAD( "hyperpac.j15", 0x00000, 0x40000, 0xfb9f468d );
	
		ROM_REGION( 0x0c0000, REGION_GFX1, 0 );/* Sprites */
		ROM_LOAD( "hyperpac.a4", 0x000000, 0x40000, 0xbd8673da );
		ROM_LOAD( "hyperpac.a5", 0x040000, 0x40000, 0x5d90cd82 );
		ROM_LOAD( "hyperpac.a6", 0x080000, 0x40000, 0x61d86e63 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_moremorp = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );/* 68000 Code */
		ROM_LOAD16_BYTE( "mmp_u52.bin",  0x00001, 0x40000, 0x66baf9b2 );
		ROM_LOAD16_BYTE( "mmp_u74.bin",  0x00000, 0x40000, 0x7c6fede5 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 Code */
		ROM_LOAD( "mmp_u35.bin", 0x00000, 0x10000 , 0x4d098cad );
	
		ROM_REGION( 0x040000, REGION_SOUND1, 0 );/* Samples */
		ROM_LOAD( "mmp_u14.bin", 0x00000, 0x40000, 0x211a2566 );
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 );/* Sprites */
		ROM_LOAD( "mmp_u75.bin", 0x000000, 0x80000, 0xaf9e824e );
		ROM_LOAD( "mmp_u76.bin", 0x080000, 0x80000, 0xc42af064 );
		ROM_LOAD( "mmp_u77.bin", 0x100000, 0x80000, 0x1d7396e1 );
		ROM_LOAD( "mmp_u78.bin", 0x180000, 0x80000, 0x5508d80b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_3in1semi = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );/* 68000 Code */
		ROM_LOAD16_BYTE( "u52",  0x00001, 0x40000, 0xb0e4a0f7 );
		ROM_LOAD16_BYTE( "u74",  0x00000, 0x40000, 0x266862c4 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 Code */
		ROM_LOAD( "u35", 0x00000, 0x10000 , 0xe40481da );
	
		ROM_REGION( 0x040000, REGION_SOUND1, 0 );/* Samples */
		ROM_LOAD( "u14", 0x00000, 0x40000, 0xc83c11be );
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 );/* Sprites */
		ROM_LOAD( "u75", 0x000000, 0x80000, 0xb66a0db6 );
		ROM_LOAD( "u76", 0x080000, 0x80000, 0x5f4b48ea );
		ROM_LOAD( "u77", 0x100000, 0x80000, 0xd44211e3 );
		ROM_LOAD( "u78", 0x180000, 0x80000, 0xaf596afc );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_cookbib2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );/* 68000 Code */
		ROM_LOAD16_BYTE( "cookbib2.01",  0x00000, 0x40000, 0x65aafde2 );
		ROM_LOAD16_BYTE( "cookbib2.02",  0x00001, 0x40000, 0xb2909460 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 Code */
		ROM_LOAD( "cookbib2.07", 0x00000, 0x10000 , 0xf59f1c9a );
	
		ROM_REGION( 0x020000, REGION_SOUND1, 0 );/* Samples */
		ROM_LOAD( "cookbib2.06", 0x00000, 0x20000, 0x5e6f76b8 );
	
		ROM_REGION( 0x140000, REGION_GFX1, 0 );/* Sprites */
		ROM_LOAD( "cookbib2.05", 0x000000, 0x80000, 0x89fb38ce );
		ROM_LOAD( "cookbib2.04", 0x080000, 0x80000, 0xf240111f );
		ROM_LOAD( "cookbib2.03", 0x100000, 0x40000, 0xe1604821 );
	ROM_END(); }}; 
	
	/* this one could be more different hw, gfx don't decode the same */
	static RomLoadPtr rom_htchctch = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );/* 68000 Code */
		ROM_LOAD16_BYTE( "p03.b16",  0x00001, 0x20000, 0xeff14c40 );
		ROM_LOAD16_BYTE( "p04.b17",  0x00000, 0x20000, 0x6991483a );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 Code */
		ROM_LOAD( "p02.b5", 0x00000, 0x10000 , 0xc5a03186 );
	
		ROM_REGION( 0x020000, REGION_SOUND1, 0 );/* Samples */
		ROM_LOAD( "p01.c1", 0x00000, 0x20000, 0x18c06829 );
	
		ROM_REGION( 0x100000, REGION_GFX1, 0 );/* Sprites */
		ROM_LOAD( "p06srom5.bin", 0x00000, 0x40000, 0x3d2cbb0d );
		ROM_LOAD( "p07srom6.bin", 0x40000, 0x40000, 0x0207949c );
		ROM_LOAD( "p08uor1.bin",  0x80000, 0x20000, 0x6811e7b6 );
		ROM_LOAD( "p09uor2.bin",  0xa0000, 0x20000, 0x1c6549cf );
		ROM_LOAD( "p10uor3.bin",  0xc0000, 0x20000, 0x6462e6e0 );
		ROM_LOAD( "p11uor4.bin",  0xe0000, 0x20000, 0x9c511d98 );
	ROM_END(); }}; 
	
	/* cool mini, only 3 roms? */
	/* bc story, encrypted?, dif. hardware? */
	
	static RomLoadPtr rom_snowbro3 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x40000, REGION_CPU1, 0 );/* 68000 code */
		ROM_LOAD16_BYTE( "ur4",  0x00000, 0x20000, 0x19c13ffd );
		ROM_LOAD16_BYTE( "ur3",  0x00001, 0x20000, 0x3f32fa15 );
	
		/* is sound cpu code missing or is it driven by the main cpu? */
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ua5",		0x000000, 0x80000, 0x0604e385 );/* 16x16 tiles */
	
		ROM_REGION( 0x400000, REGION_GFX2, ROMREGION_DISPOSE );/* 16x16 BG Tiles */
		ROM_LOAD( "un7",		0x000000, 0x200000, 0x4a79da4c );
		ROM_LOAD( "un8",		0x200000, 0x200000, 0x7a4561a4 );
	
		ROM_REGION( 0x080000, REGION_SOUND1, 0 );/* OKIM6295 samples */
		ROM_LOAD( "us5",     0x00000, 0x80000, 0x7c6368ef );
	ROM_END(); }}; 
	
	static DRIVER_INIT( hyperpac )
	{
		/* simulate RAM initialization done by the protection MCU */
		/* not verified on real hardware */
		hyperpac_ram[0xe000/2] = 0x4ef9;
		hyperpac_ram[0xe002/2] = 0x0000;
		hyperpac_ram[0xe004/2] = 0x062c;
	
		hyperpac_ram[0xe080/2] = 0xfedc;
		hyperpac_ram[0xe082/2] = 0xba98;
		hyperpac_ram[0xe084/2] = 0x7654;
		hyperpac_ram[0xe086/2] = 0x3210;
	}
	
	static DRIVER_INIT(snowbro3)
	{
		unsigned char *buffer;
		data8_t *src = memory_region(REGION_CPU1);
		int len = memory_region_length(REGION_CPU1);
	
		/* strange order */
		if ((buffer = malloc(len)))
		{
			int i;
			for (i = 0;i < len; i++)
				buffer[i] = src[BITSWAP24(i,23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,3,4,1,2,0)];
			memcpy(src,buffer,len);
			free(buffer);
		}
	}
	
	public static GameDriver driver_snowbros	   = new GameDriver("1990"	,"snowbros"	,"snowbros.java"	,rom_snowbros,null	,machine_driver_snowbros	,input_ports_snowbros	,null	,ROT0	,	"Toaplan", "Snow Bros. - Nick & Tom (set 1)" )
	public static GameDriver driver_snowbroa	   = new GameDriver("1990"	,"snowbroa"	,"snowbros.java"	,rom_snowbroa,driver_snowbros	,machine_driver_snowbros	,input_ports_snowbros	,null	,ROT0	,	"Toaplan", "Snow Bros. - Nick & Tom (set 2)" )
	public static GameDriver driver_snowbrob	   = new GameDriver("1990"	,"snowbrob"	,"snowbros.java"	,rom_snowbrob,driver_snowbros	,machine_driver_snowbros	,input_ports_snowbros	,null	,ROT0	,	"Toaplan", "Snow Bros. - Nick & Tom (set 3)" )
	public static GameDriver driver_snowbroj	   = new GameDriver("1990"	,"snowbroj"	,"snowbros.java"	,rom_snowbroj,driver_snowbros	,machine_driver_snowbros	,input_ports_snowbroj	,null	,ROT0	,	"Toaplan", "Snow Bros. - Nick & Tom (Japan)" )
	public static GameDriver driver_wintbob	   = new GameDriver("1990"	,"wintbob"	,"snowbros.java"	,rom_wintbob,driver_snowbros	,machine_driver_wintbob	,input_ports_snowbros	,null	,ROT0	,	"bootleg", "The Winter Bobble" )
	/* SemiCom Games */
	public static GameDriver driver_hyperpac	   = new GameDriver("1995"	,"hyperpac"	,"snowbros.java"	,rom_hyperpac,null	,machine_driver_hyperpac	,input_ports_hyperpac	,init_hyperpac	,ROT0	,	"SemiCom", "Hyper Pacman" )
	public static GameDriver driver_hyperpcb	   = new GameDriver("1995"	,"hyperpcb"	,"snowbros.java"	,rom_hyperpcb,driver_hyperpac	,machine_driver_hyperpac	,input_ports_hyperpac	,null	,ROT0	,	"bootleg", "Hyper Pacman (bootleg)" )
	public static GameDriver driver_snowbro3	   = new GameDriver("2002"	,"snowbro3"	,"snowbros.java"	,rom_snowbro3,null	,machine_driver_snowbro3	,input_ports_snowbroj	,init_snowbro3	,ROT0	,	"Syrmex / hack?", "Snow Brothers 3 - Magical Adventure", GAME_NO_SOUND ) // its basically snowbros code?...
	/* the following don't work, they either point the interrupts at an area of ram probably shared by
	   some kind of mcu which puts 68k code there, or jump to the area in the interrupts */
	public static GameDriver driver_moremorp	   = new GameDriver("199?"	,"moremorp"	,"snowbros.java"	,rom_moremorp,null	,machine_driver_hyperpac	,input_ports_hyperpac	,null	,ROT0	,	"SemiCom", "More More +", GAME_UNEMULATED_PROTECTION | GAME_NOT_WORKING )
	public static GameDriver driver_3in1semi	   = new GameDriver("1997"	,"3in1semi"	,"snowbros.java"	,rom_3in1semi,null	,machine_driver_hyperpac	,input_ports_hyperpac	,null	,ROT0	,	"SemiCom", "3-in-1 (SemiCom)", GAME_UNEMULATED_PROTECTION | GAME_NOT_WORKING )
	public static GameDriver driver_cookbib2	   = new GameDriver("1996"	,"cookbib2"	,"snowbros.java"	,rom_cookbib2,null	,machine_driver_hyperpac	,input_ports_hyperpac	,null	,ROT0	,	"SemiCom", "Cookie and Bibi 2", GAME_UNEMULATED_PROTECTION | GAME_NOT_WORKING )
	public static GameDriver driver_htchctch	   = new GameDriver("1995"	,"htchctch"	,"snowbros.java"	,rom_htchctch,null	,machine_driver_hyperpac	,input_ports_hyperpac	,null	,ROT0	,	"SemiCom", "Hatch Catch", GAME_UNEMULATED_PROTECTION | GAME_NOT_WORKING )
}
