/***************************************************************************

The Pit/Round Up/Intrepid/Super Mouse memory map (preliminary)

Driver by Zsolt Vasvari

Main CPU:

0000-4fff ROM
8000-87ff RAM
8800-8bff Color RAM        (Not used in Intrepid/Super Mouse)
8c00-8fff Mirror for above (Not used in Intrepid/Super Mouse)
9000-93ff Video RAM
9400-97ff Mirror for above (Color RAM in Intrepid/Super Mouse)
9800-983f Attributes RAM
9840-985f Sprite RAM

Read:

a000      Input Port 0
a800      Input Port 1
b000      DIP Switches
b800      Watchdog Reset

Write:

b000      NMI Enable
b002      Coin Lockout
b003	  Sound Enable
b005      Intrepid graphics bank select
b006      Flip Screen X
b007      Flip Screen Y
b800      Sound Command


Sound CPU:

0000-0fff ROM  (0000-07ff in The Pit)
3800-3bff RAM


Port I/O Read:

8f  AY8910 Read Port


Port I/O Write:

00  Reset Sound Command
8c  AY8910 #2 Control Port    (Intrepid/Super Mouse only)
8d  AY8910 #2 Write Port	  (Intrepid/Super Mouse only)
8e  AY8910 #1 Control Port
8f  AY8910 #1 Write Port


***************************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"

extern unsigned char *thepit_attributesram;
extern unsigned char *intrepid_sprite_bank_select;
WRITE_HANDLER( thepit_attributes_w );

PALETTE_INIT( thepit );
PALETTE_INIT( suprmous );
VIDEO_UPDATE( thepit );
READ_HANDLER( thepit_input_port_0_r );
WRITE_HANDLER( thepit_sound_enable_w );
WRITE_HANDLER( intrepid_graphics_bank_select_w );

static WRITE_HANDLER( flip_screen_x_w )
{
	flip_screen_x_set(data);
}

static WRITE_HANDLER( flip_screen_y_w )
{
	flip_screen_y_set(data);
}


static MEMORY_READ_START( thepit_readmem )
	{ 0x0000, 0x4fff, MRA_ROM },
	{ 0x8000, 0x87ff, MRA_RAM },
	{ 0x8800, 0x93ff, MRA_RAM },
	{ 0x9400, 0x97ff, videoram_r },
	{ 0x9800, 0x98ff, MRA_RAM },
	{ 0xa000, 0xa000, thepit_input_port_0_r },
	{ 0xa800, 0xa800, input_port_1_r },
	{ 0xb000, 0xb000, input_port_2_r },
	{ 0xb800, 0xb800, watchdog_reset_r },
MEMORY_END

static MEMORY_WRITE_START( thepit_writemem )
	{ 0x0000, 0x4fff, MWA_ROM },
	{ 0x8000, 0x87ff, MWA_RAM },
	{ 0x8800, 0x8bff, colorram_w, &colorram },
	{ 0x8c00, 0x8fff, colorram_w },
	{ 0x9000, 0x93ff, videoram_w, &videoram, &videoram_size },
	{ 0x9400, 0x97ff, videoram_w },
	{ 0x9800, 0x983f, thepit_attributes_w, &thepit_attributesram },
	{ 0x9840, 0x985f, MWA_RAM, &spriteram, &spriteram_size },
	{ 0x9860, 0x98ff, MWA_RAM }, // Probably unused
	{ 0xa000, 0xa000, MWA_NOP }, // Not hooked up according to the schematics
	{ 0xb000, 0xb000, interrupt_enable_w },
	{ 0xb001, 0xb001, MWA_NOP }, // Unused, but initialized
	{ 0xb002, 0xb002, MWA_NOP }, // coin_lockout_w
	{ 0xb003, 0xb003, thepit_sound_enable_w },
	{ 0xb004, 0xb005, MWA_NOP }, // Unused, but initialized
	{ 0xb006, 0xb006, flip_screen_x_w },
	{ 0xb007, 0xb007, flip_screen_y_w },
	{ 0xb800, 0xb800, soundlatch_w },
MEMORY_END


static MEMORY_READ_START( intrepid_readmem )
	{ 0x0000, 0x4fff, MRA_ROM },
	{ 0x8000, 0x87ff, MRA_RAM },
	{ 0x9000, 0x98ff, MRA_RAM },
	{ 0xa000, 0xa000, thepit_input_port_0_r },
	{ 0xa800, 0xa800, input_port_1_r },
	{ 0xb000, 0xb000, input_port_2_r },
	{ 0xb800, 0xb800, watchdog_reset_r },
MEMORY_END

static MEMORY_WRITE_START( intrepid_writemem )
	{ 0x0000, 0x4fff, MWA_ROM },
	{ 0x8000, 0x87ff, MWA_RAM },
	{ 0x9000, 0x93ff, videoram_w, &videoram, &videoram_size },
	{ 0x9400, 0x97ff, colorram_w, &colorram },
	{ 0x9800, 0x983f, thepit_attributes_w, &thepit_attributesram },
	{ 0x9840, 0x985f, MWA_RAM, &spriteram, &spriteram_size },
	{ 0x9860, 0x98ff, MWA_RAM }, // Probably unused
	{ 0xb000, 0xb000, interrupt_enable_w },
	{ 0xb001, 0xb001, MWA_NOP }, // Unused, but initialized
	{ 0xb002, 0xb002, MWA_NOP }, // coin_lockout_w
	{ 0xb003, 0xb003, thepit_sound_enable_w },
	{ 0xb004, 0xb004, MWA_NOP }, // Unused, but initialized
	{ 0xb005, 0xb005, intrepid_graphics_bank_select_w },
	{ 0xb006, 0xb006, flip_screen_x_w },
	{ 0xb007, 0xb007, flip_screen_y_w },
	{ 0xb800, 0xb800, soundlatch_w },
MEMORY_END


static MEMORY_READ_START( sound_readmem )
	{ 0x0000, 0x0fff, MRA_ROM },
	{ 0x3800, 0x3bff, MRA_RAM },
MEMORY_END

static MEMORY_WRITE_START( sound_writemem )
	{ 0x3800, 0x3bff, MWA_RAM },
MEMORY_END

static PORT_READ_START( sound_readport )
	{ 0x8f, 0x8f, AY8910_read_port_0_r },
PORT_END

static PORT_WRITE_START( sound_writeport )
	{ 0x00, 0x00, soundlatch_clear_w },
	{ 0x8c, 0x8c, AY8910_control_port_1_w },
	{ 0x8d, 0x8d, AY8910_write_port_1_w },
	{ 0x8e, 0x8e, AY8910_control_port_0_w },
	{ 0x8f, 0x8f, AY8910_write_port_0_w },
PORT_END


INPUT_PORTS_START( thepit )
	PORT_START      /* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* DSW0 */
	PORT_DIPNAME( 0x03, 0x00, "Coinage P1/P2" )
	PORT_DIPSETTING(    0x00, "1Cr/2Cr" )
	PORT_DIPSETTING(    0x01, "2Cr/3Cr" )
	PORT_DIPSETTING(    0x02, "2Cr/4Cr" )
	PORT_DIPSETTING(    0x03, DEF_STR( Free_Play ) )
	PORT_DIPNAME( 0x04, 0x00, "Game Speed" )
	PORT_DIPSETTING(    0x04, "Slow" )
	PORT_DIPSETTING(    0x00, "Fast" )
	PORT_DIPNAME( 0x08, 0x00, "Time Limit" )
	PORT_DIPSETTING(    0x00, "Long" )
	PORT_DIPSETTING(    0x08, "Short" )
	PORT_DIPNAME( 0x10, 0x00, DEF_STR( Flip_Screen ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x10, DEF_STR( On ) )
	PORT_DIPNAME( 0x20, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x20, DEF_STR( Cocktail ) )
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "3" )
	PORT_DIPSETTING(    0x40, "4" )
	PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x80, DEF_STR( On ) )

	/* Since the real inputs are multiplexed, we used this fake port
	   to read the 2nd player controls when the screen is flipped */
	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )
INPUT_PORTS_END


INPUT_PORTS_START( roundup )
	PORT_START      /* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* DSW0 */
	PORT_DIPNAME( 0x03, 0x01, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x01, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x02, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x03, DEF_STR( 1C_3C ) )
	PORT_DIPNAME( 0x0c, 0x04, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "2" )
	PORT_DIPSETTING(    0x04, "3" )
	PORT_DIPSETTING(    0x08, "4" )
	PORT_DIPSETTING(    0x0c, "5" )
	PORT_DIPNAME( 0x10, 0x10, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x10, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Cocktail ) )
	PORT_DIPNAME( 0x20, 0x00, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x00, "10000" )
	PORT_DIPSETTING(    0x20, "30000" )
	PORT_DIPNAME( 0x40, 0x40, "Gly Boys Wake Up" )
	PORT_DIPSETTING(    0x40, "Slow" )
	PORT_DIPSETTING(    0x00, "Fast" )
	PORT_SERVICE( 0x80, IP_ACTIVE_HIGH )

	/* Since the real inputs are multiplexed, we used this fake port
	   to read the 2nd player controls when the screen is flipped */
	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL)
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )
INPUT_PORTS_END


INPUT_PORTS_START( fitter )
	PORT_START      /* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* DSW0 */
	PORT_DIPNAME( 0x03, 0x01, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x01, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x02, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x03, DEF_STR( 1C_3C ) )
	PORT_DIPNAME( 0x0c, 0x04, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "2" )
	PORT_DIPSETTING(    0x04, "3" )
	PORT_DIPSETTING(    0x08, "4" )
	PORT_DIPSETTING(    0x0c, "5" )
	PORT_DIPNAME( 0x10, 0x10, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x10, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Cocktail ) )
	PORT_DIPNAME( 0x20, 0x00, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x00, "10000" )
	PORT_DIPSETTING(    0x20, "30000" )
	PORT_DIPNAME( 0x40, 0x40, "Gly Boys Wake Up" )
	PORT_DIPSETTING(    0x40, "Slow" )
	PORT_DIPSETTING(    0x00, "Fast" )
	PORT_BITX(    0x80, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x80, DEF_STR( On ) )

	/* Since the real inputs are multiplexed, we used this fake port
	   to read the 2nd player controls when the screen is flipped */
	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL)
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )
INPUT_PORTS_END


INPUT_PORTS_START( intrepid )
	PORT_START      /* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* Starts a timer, which, */
  												  /* after it runs down, doesn't */
	PORT_START      /* IN2 */                     /* seem to do anything. See $0105 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* DSW0 */
	PORT_BITX(    0x01, 0x01, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE )
	PORT_DIPSETTING(    0x01, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x02, 0x02, DEF_STR( Demo_Sounds ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x02, DEF_STR( On ) )
	PORT_DIPNAME( 0x04, 0x04, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x04, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Cocktail ) )
	PORT_DIPNAME( 0x08, 0x08, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(    0x08, "Easy" )
	PORT_DIPSETTING(    0x00, "Hard" )
	PORT_DIPNAME( 0x10, 0x00, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x00, "10000" )
	PORT_DIPSETTING(    0x10, "30000" )
	PORT_DIPNAME( 0x20, 0x00, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x20, "3" )
	PORT_DIPSETTING(    0x00, "5" )
	PORT_DIPNAME( 0xc0, 0x40, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x80, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x40, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0xc0, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Free_Play ) )

	/* Since the real inputs are multiplexed, we used this fake port
	   to read the 2nd player controls when the screen is flipped */
	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )
INPUT_PORTS_END


INPUT_PORTS_START( portman )
	PORT_START      /* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* unused? */
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* unused? */
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* unused? */

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* DSW0 */
	PORT_DIPNAME( 0x03, 0x01, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x01, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x02, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x03, DEF_STR( 1C_3C ) )
	PORT_DIPNAME( 0x0c, 0x04, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "2" )
	PORT_DIPSETTING(    0x04, "3" )
	PORT_DIPSETTING(    0x08, "4" )
	PORT_DIPSETTING(    0x0c, "5" )
	PORT_DIPNAME( 0x10, 0x00, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x00, "10000" )
	PORT_DIPSETTING(    0x10, "30000" )
	PORT_DIPNAME( 0x20, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x20, DEF_STR( Cocktail ) )
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Unknown ) )	/* not used? */
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x40, DEF_STR( On ) )
	PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )	/* not used? */
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x80, DEF_STR( On ) )

	/* Since the real inputs are multiplexed, we used this fake port
	   to read the 2nd player controls when the screen is flipped */
	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL)
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )
INPUT_PORTS_END


INPUT_PORTS_START( suprmous )
	PORT_START      /* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* DSW0 */
	PORT_DIPNAME( 0x07, 0x01, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x01, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x02, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x03, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(    0x04, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(    0x05, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(    0x06, DEF_STR( 1C_6C ) )
	PORT_DIPSETTING(    0x07, DEF_STR( 1C_7C ) )
	PORT_DIPNAME( 0x18, 0x00, DEF_STR( Lives ) )  /* The game reads these together */
	PORT_DIPSETTING(    0x00, "3" )
	PORT_DIPSETTING(    0x08, "5" )
  //PORT_DIPSETTING(    0x10, "5" )
  //PORT_DIPSETTING(    0x18, "5" )
	PORT_DIPNAME( 0x20, 0x00, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x10, "5000" )
	PORT_DIPSETTING(    0x00, "10000" )
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x40, DEF_STR( Cocktail ) )
	PORT_BITX(    0x80, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x80, DEF_STR( On ) )

	/* Since the real inputs are multiplexed, we used this fake port
	   to read the 2nd player controls when the screen is flipped */
	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL)
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )
INPUT_PORTS_END


static struct GfxLayout charlayout =
{
	8,8,    /* 8*8 characters */
	256,    /* 256 characters */
	2,      /* 2 bits per pixel */
	{ 0x1000*8, 0 }, /* the two bitplanes are separated */
	{ 0, 1, 2, 3, 4, 5, 6, 7 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
	8*8     /* every char takes 8 consecutive bytes */
};


static struct GfxLayout spritelayout =
{
	16,16,  /* 16*16 sprites */
	64,     /* 64 sprites */
	2,      /* 2 bits per pixel */
	{ 0x1000*8, 0 },	/* the two bitplanes are separated */
	{ 0, 1, 2, 3, 4, 5, 6, 7,
	  8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
	  16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
	32*8    /* every sprite takes 32 consecutive bytes */
};


static struct GfxLayout suprmous_charlayout =
{
	8,8,	/* 8*8 characters */
	256,	/* 256 characters */
	3,	    /* 3 bits per pixel */
	{ 0x2000*8, 0x1000*8, 0 },	/* the three bitplanes for 4 pixels are separated */
	{ 0, 1, 2, 3, 4, 5, 6, 7 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
	8*8	    /* every char takes 8 consecutive bytes */
};


static struct GfxLayout suprmous_spritelayout =
{
	16,16,	/* 16*16 sprites */
	64,		/* 64 sprites */
	3,	    /* 3 bits per pixel */
	{ 0x2000*8, 0x1000*8, 0 },	/* the bitplanes are separated */
	{ 0, 1, 2, 3, 4, 5, 6, 7,
	  8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
	  16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
	32*8	/* every sprite takes 32 consecutive bytes */
};


static struct GfxDecodeInfo thepit_gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0, &charlayout,     0, 8 },
	{ REGION_GFX1, 0, &spritelayout,   0, 8 },
	{ -1 } /* end of array */
};

static struct GfxDecodeInfo intrepid_gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0x0000, &charlayout,     0, 8 },
	{ REGION_GFX1, 0x0000, &spritelayout,   0, 8 },
	{ REGION_GFX1, 0x0800, &charlayout,     0, 8 },
	{ REGION_GFX1, 0x0800, &spritelayout,   0, 8 },
	{ -1 } /* end of array */
};

static struct GfxDecodeInfo suprmous_gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0x0000, &suprmous_charlayout,   0, 4 },
	{ REGION_GFX1, 0x0800, &suprmous_spritelayout, 0, 4 },
	{ -1 } /* end of array */
};


static struct AY8910interface ay8910_interface =
{
	2,      /* 1 or 2 chips */
	18432000/12,     /* 1.536MHz */
	{ 25, 25 },
	{ soundlatch_r, 0 },
	{ 0, 0 },
	{ 0, 0 },
	{ 0, 0 }
};


static MACHINE_DRIVER_START( thepit )

	/* basic machine hardware */
	MDRV_CPU_ADD_TAG("main", Z80, 18432000/6)     /* 3.072 MHz */
	MDRV_CPU_MEMORY(thepit_readmem,thepit_writemem)
	MDRV_CPU_VBLANK_INT(nmi_line_pulse,1)

	MDRV_CPU_ADD(Z80, 10000000/4)     /* 2.5 MHz */
	MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
	MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	MDRV_CPU_PORTS(sound_readport,sound_writeport)
	MDRV_CPU_VBLANK_INT(irq0_line_hold,1)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)       /* frames per second, vblank duration */

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
	MDRV_SCREEN_SIZE(32*8, 32*8)
	MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
	MDRV_GFXDECODE(thepit_gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(32+8)
	MDRV_COLORTABLE_LENGTH(32)

	MDRV_PALETTE_INIT(thepit)
	MDRV_VIDEO_START(generic)
	MDRV_VIDEO_UPDATE(thepit)

	/* sound hardware */
	MDRV_SOUND_ADD(AY8910, ay8910_interface)
MACHINE_DRIVER_END


static MACHINE_DRIVER_START( intrepid )

	/* basic machine hardware */
	MDRV_IMPORT_FROM(thepit)
	MDRV_CPU_MODIFY("main")
	MDRV_CPU_MEMORY(intrepid_readmem,intrepid_writemem)

	/* video hardware */
	MDRV_GFXDECODE(intrepid_gfxdecodeinfo)
	MDRV_PALETTE_INIT(thepit)
MACHINE_DRIVER_END


static MACHINE_DRIVER_START( suprmous )

	/* basic machine hardware */
	MDRV_IMPORT_FROM(intrepid)

	/* video hardware */
	MDRV_GFXDECODE(suprmous_gfxdecodeinfo)
	MDRV_PALETTE_INIT(suprmous)
MACHINE_DRIVER_END



/***************************************************************************

  Game driver(s)

***************************************************************************/

ROM_START( thepit )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for main CPU */
	ROM_LOAD( "p38b",         0x0000, 0x1000, 0x7315e1bc )
	ROM_LOAD( "p39b",         0x1000, 0x1000, 0xc9cc30fe )
	ROM_LOAD( "p40b",         0x2000, 0x1000, 0x986738b5 )
	ROM_LOAD( "p41b",         0x3000, 0x1000, 0x31ceb0a1 )
	ROM_LOAD( "p33b",         0x4000, 0x1000, 0x614ec454 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for audio CPU */
	ROM_LOAD( "p30",          0x0000, 0x0800, 0x1b79dfb6 )

	ROM_REGION( 0x1800, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "p9",           0x0000, 0x0800, 0x69502afc )
	ROM_LOAD( "p8",           0x1000, 0x0800, 0x2ddd5045 )

	ROM_REGION( 0x0020, REGION_PROMS, 0 )
	ROM_LOAD( "pitclr.ic4",   0x0000, 0x0020, 0xa758b567 )
ROM_END

ROM_START( roundup )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for main CPU */
	ROM_LOAD( "roundup.u38",  0x0000, 0x1000, 0xd62c3b7a )
	ROM_LOAD( "roundup.u39",  0x1000, 0x1000, 0x37bf554b )
	ROM_LOAD( "roundup.u40",  0x2000, 0x1000, 0x5109d0c5 )
	ROM_LOAD( "roundup.u41",  0x3000, 0x1000, 0x1c5ed660 )
	ROM_LOAD( "roundup.u33",  0x4000, 0x1000, 0x2fa711f3 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for audio CPU */
	ROM_LOAD( "roundup.u30",  0x0000, 0x0800, 0x1b18faee )
	ROM_LOAD( "roundup.u31",  0x0800, 0x0800, 0x76cf4394 )

	ROM_REGION( 0x1800, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "roundup.u9",   0x0000, 0x0800, 0x394676a2 )
	ROM_LOAD( "roundup.u10",  0x1000, 0x0800, 0xa38d708d )

	ROM_REGION( 0x0020, REGION_PROMS, 0 )
	ROM_LOAD( "roundup.clr",  0x0000, 0x0020, 0xa758b567 )
ROM_END

ROM_START( fitter )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for main CPU */
	ROM_LOAD( "ic38.bin",     0x0000, 0x1000, 0x6bf6cca4 )
	ROM_LOAD( "roundup.u39",  0x1000, 0x1000, 0x37bf554b )
	ROM_LOAD( "ic40.bin",     0x2000, 0x1000, 0x572e2157 )
	ROM_LOAD( "roundup.u41",  0x3000, 0x1000, 0x1c5ed660 )
	ROM_LOAD( "ic33.bin",     0x4000, 0x1000, 0xab47c6c2 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for audio CPU */
	ROM_LOAD( "ic30.bin",     0x0000, 0x0800, 0x4055b5ca )
	ROM_LOAD( "ic31.bin",     0x0800, 0x0800, 0xc9d8c1cc )

	ROM_REGION( 0x1800, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "ic9.bin",      0x0000, 0x0800, 0xa6799a37 )
	ROM_LOAD( "ic8.bin",      0x1000, 0x0800, 0xa8256dfe )

	ROM_REGION( 0x0020, REGION_PROMS, 0 )
	ROM_LOAD( "roundup.clr",  0x0000, 0x0020, 0xa758b567 )
ROM_END

ROM_START( intrepid )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for main CPU */
	ROM_LOAD( "ic19.1",       0x0000, 0x1000, 0x7d927b23 )
	ROM_LOAD( "ic18.2",       0x1000, 0x1000, 0xdcc22542 )
	ROM_LOAD( "ic17.3",       0x2000, 0x1000, 0xfd11081e )
	ROM_LOAD( "ic16.4",       0x3000, 0x1000, 0x74a51841 )
	ROM_LOAD( "ic15.5",       0x4000, 0x1000, 0x4fef643d )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for audio CPU */
	ROM_LOAD( "ic22.7",       0x0000, 0x0800, 0x1a7cc392 )
	ROM_LOAD( "ic23.6",       0x0800, 0x0800, 0x91ca7097 )

	ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "ic9.9",        0x0000, 0x1000, 0x8c70d18d )
	ROM_LOAD( "ic8.8",        0x1000, 0x1000, 0x04d067d3 )

	ROM_REGION( 0x0020, REGION_PROMS, 0 )
	ROM_LOAD( "ic3.prm",      0x0000, 0x0020, 0x927ff40a )
ROM_END

ROM_START( intrepi2 )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for main CPU */
	ROM_LOAD( "intrepid.001", 0x0000, 0x1000, 0x9505df1e )
	ROM_LOAD( "intrepid.002", 0x1000, 0x1000, 0x27e9f53f )
	ROM_LOAD( "intrepid.003", 0x2000, 0x1000, 0xda082ed7 )
	ROM_LOAD( "intrepid.004", 0x3000, 0x1000, 0x60acecd9 )
	ROM_LOAD( "intrepid.005", 0x4000, 0x1000, 0x7c868725 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for audio CPU */
	ROM_LOAD( "intrepid.007", 0x0000, 0x0800, 0xf85ead07 )
	ROM_LOAD( "intrepid.006", 0x0800, 0x0800, 0x9eb6c61b )

	ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "ic9.9",        0x0000, 0x1000, 0x8c70d18d )
	ROM_LOAD( "ic8.8",        0x1000, 0x1000, 0x04d067d3 )

	ROM_REGION( 0x0020, REGION_PROMS, 0 )
	ROM_LOAD( "ic3.prm",      0x0000, 0x0020, 0x927ff40a )
ROM_END

ROM_START( zaryavos )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for main CPU */
	ROM_LOAD( "zv1.rom",      0x0000, 0x1000, 0xb7eec75d )
	ROM_LOAD( "zv2.rom",      0x1000, 0x1000, 0x000aa722 )
	ROM_LOAD( "zv3.rom",      0x2000, 0x1000, 0x9b8b431a )
	ROM_LOAD( "zv4.rom",      0x3000, 0x1000, 0x3636d5bf )
	ROM_LOAD( "zv5.rom",      0x4000, 0x1000, 0xc5d405a7 )
	ROM_LOAD( "zv6.rom",      0x5000, 0x1000, 0xd07778a1 )
	ROM_LOAD( "zv7.rom",      0x6000, 0x1000, 0x63d75e5e )
	ROM_LOAD( "zv8.rom",      0x7000, 0x1000, 0xb87a286a )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for audio CPU */
	ROM_LOAD( "ic22.7",       0x0000, 0x0800, 0x00000000 )
	ROM_LOAD( "ic23.6",       0x0800, 0x0800, 0x00000000 )

	ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "ic9.9",        0x0000, 0x1000, 0x00000000 )
	ROM_LOAD( "ic8.8",        0x1000, 0x1000, 0x00000000 )

	ROM_REGION( 0x0020, REGION_PROMS, 0 )
	ROM_LOAD( "zvprom.rom",   0x0000, 0x0020, 0x364e5700 )
ROM_END

ROM_START( portman )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for main CPU */
	ROM_LOAD( "pe1",          0x0000, 0x1000, 0xa5cf6083 )
	ROM_LOAD( "pe2",          0x1000, 0x1000, 0x0b53d48a )
	ROM_LOAD( "pe3",          0x2000, 0x1000, 0x1c923057 )
	ROM_LOAD( "pe4",          0x3000, 0x1000, 0x555c71ef )
	ROM_LOAD( "pe5",          0x4000, 0x1000, 0xf749e2d4 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for audio CPU */
	ROM_LOAD( "pe7",          0x0000, 0x0800, 0xd2094e4a )
	ROM_LOAD( "pe6",          0x0800, 0x0800, 0x1cf447f4 )

	ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "pe8",          0x0000, 0x1000, 0x4d8c2974 )
	ROM_LOAD( "pe9",          0x1000, 0x1000, 0x4e4ea162 )

	ROM_REGION( 0x0020, REGION_PROMS, 0 )
	ROM_LOAD( "ic3",          0x0000, 0x0020, 0x6440dc61 )
ROM_END

ROM_START( funnymou )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )	    /* 64k for main CPU */
	ROM_LOAD( "suprmous.x1",  0x0000, 0x1000, 0xad72b467 )
	ROM_LOAD( "suprmous.x2",  0x1000, 0x1000, 0x53f5be5e )
	ROM_LOAD( "suprmous.x3",  0x2000, 0x1000, 0xb5b8d34d )
	ROM_LOAD( "suprmous.x4",  0x3000, 0x1000, 0x603333df )
	ROM_LOAD( "suprmous.x5",  0x4000, 0x1000, 0x2ef9cbf1 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )	   /* 64k for audio CPU */
	ROM_LOAD( "sm.6",         0x0000, 0x1000, 0xfba71785 )

	ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "suprmous.x8",  0x0000, 0x1000, 0xdbef9db8 )
	ROM_LOAD( "suprmous.x9",  0x1000, 0x1000, 0x700d996e )
	ROM_LOAD( "suprmous.x7",  0x2000, 0x1000, 0xe9295071 )

	ROM_REGION( 0x0040, REGION_PROMS, 0 )
	ROM_LOAD( "smouse2.clr",  0x0000, 0x0020, 0x8c295553 )
	ROM_LOAD( "smouse1.clr",  0x0020, 0x0020, 0xd815504b )
ROM_END

ROM_START( suprmous )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )	    /* 64k for main CPU */
	ROM_LOAD( "sm.1",         0x0000, 0x1000, 0x9db2b786 )
	ROM_LOAD( "sm.2",         0x1000, 0x1000, 0x0a3d91d3 )
	ROM_LOAD( "sm.3",         0x2000, 0x1000, 0x32af6285 )
	ROM_LOAD( "sm.4",         0x3000, 0x1000, 0x46091524 )
	ROM_LOAD( "sm.5",         0x4000, 0x1000, 0xf15fd5d2 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )	   /* 64k for audio CPU */
	ROM_LOAD( "sm.6",         0x0000, 0x1000, 0xfba71785 )

	ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "sm.8",         0x0000, 0x1000, 0x2f81ab5f )
	ROM_LOAD( "sm.9",         0x1000, 0x1000, 0x8463af89 )
	ROM_LOAD( "sm.7",         0x2000, 0x1000, 0x1d476696 )

	ROM_REGION( 0x0040, REGION_PROMS, 0 )
	ROM_LOAD( "smouse2.clr",  0x0000, 0x0020, 0x8c295553 )
	ROM_LOAD( "smouse1.clr",  0x0020, 0x0020, 0xd815504b )
ROM_END

ROM_START( machomou )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )	    /* 64k for main CPU */
	ROM_LOAD( "mm1.2g",       0x0000, 0x1000, 0x91f116be )
	ROM_LOAD( "mm2.2h",       0x1000, 0x1000, 0x3aa88c9b )
	ROM_LOAD( "mm3.2i",       0x2000, 0x1000, 0x3b66b519 )
	ROM_LOAD( "mm4.2j",       0x3000, 0x1000, 0xd4f99896 )
	ROM_LOAD( "mm5.3f",       0x4000, 0x1000, 0x5bfc3874 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )	   /* 64k for audio CPU */
	ROM_LOAD( "mm6.e6",       0x0000, 0x1000, 0x20816913 )

	ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE ) /* chars and sprites */
	ROM_LOAD( "mm8.3c",       0x0000, 0x1000, 0x062e77cb )
	ROM_LOAD( "mm9.3a",       0x1000, 0x1000, 0xa2f0cfb3 )
	ROM_LOAD( "mm7.3d",       0x2000, 0x1000, 0xa6f60ed2 )

	ROM_REGION( 0x0040, REGION_PROMS, 0 )
	ROM_LOAD( "mmouse2.clr",  0x0000, 0x0020, 0x00000000 )
	ROM_LOAD( "mmouse1.clr",  0x0020, 0x0020, 0x00000000 )
ROM_END



GAME( 1981, roundup,  0,        thepit,   roundup,  0, ROT90, "Amenip/Centuri", "Round-Up" )
GAME( 1981, fitter,   roundup,  thepit,   fitter,   0, ROT90, "Taito", "Fitter" )
GAME( 1982, thepit,   0,        thepit,   thepit,   0, ROT90, "Centuri", "The Pit" )
GAME( 1982, portman,  0,        intrepid, portman,  0, ROT90, "Nova Games Ltd.", "Port Man" )
GAME( 1982, funnymou, 0,        suprmous, suprmous, 0, ROT90, "Chuo Co. Ltd", "Funny Mouse" )
GAME( 1982, suprmous, funnymou, suprmous, suprmous, 0, ROT90, "Taito", "Super Mouse" )
GAME( 1982, machomou, 0,        suprmous, suprmous, 0, ROT90, "Techstar", "Macho Mouse" )
GAME( 1983, intrepid, 0,        intrepid, intrepid, 0, ROT90, "Nova Games Ltd.", "Intrepid (set 1)" )
GAME( 1983, intrepi2, intrepid, intrepid, intrepid, 0, ROT90, "Nova Games Ltd.", "Intrepid (set 2)" )
GAMEX(1984, zaryavos, 0,        intrepid, intrepid, 0, ROT90, "Nova Games of Canada", "Zarya Vostoka", GAME_NOT_WORKING )
