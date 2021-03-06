/***************************************************************************

Popeye  (c) 1982 Nintendo

driver by Marc Lafontaine

To enter service mode, reset keeping the service button pressed.

Notes:
- The main set has a protection device mapped at E000/E001. The second set
  (which is the same revision of the program code) has the protection disabled
  in a very clean way, so I don't know if it's an original (without the
  protection device to save costs), or a very well done bootleg.
- The bootleg derives from a different revision of the program code which we
  don't have.

***************************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"
#include "cpu/z80/z80.h"


extern data8_t *popeye_background_pos;
extern data8_t *popeye_palettebank;
extern data8_t *popeye_textram;
WRITE_HANDLER( popeye_backgroundram_w );
WRITE_HANDLER( popeye_bitmap_w );
WRITE_HANDLER( skyskipr_bitmap_w );
PALETTE_INIT( popeye );
PALETTE_INIT( popeyebl );
VIDEO_START( skyskipr );
VIDEO_START( popeye );
VIDEO_UPDATE( popeye );



static INTERRUPT_GEN( popeye_interrupt )
{
	/* NMIs are enabled by the I register?? How can that be? */
	if (activecpu_get_reg(Z80_I) & 1)	/* skyskipr: 0/1, popeye: 2/3 but also 0/1 */
		cpu_set_nmi_line(0, PULSE_LINE);
}


/* the protection device simply returns the last two values written shifted left */
/* by a variable amount. */
static int prot0,prot1,prot_shift;

static READ_HANDLER( protection_r )
{
	if (offset == 0)
	{
		return ((prot1 << prot_shift) | (prot0 >> (8-prot_shift))) & 0xff;
	}
	else	/* offset == 1 */
	{
		/* the game just checks if bit 2 is clear. Returning 0 seems to be enough. */
		return 0;
	}
}

static WRITE_HANDLER( protection_w )
{
	if (offset == 0)
	{
		/* this is the same as the level number (1-3) */
		prot_shift = data & 0x07;
	}
	else	/* offset == 1 */
	{
		prot0 = prot1;
		prot1 = data;
	}
}




static MEMORY_READ_START( skyskipr_readmem )
	{ 0x0000, 0x7fff, MRA_ROM },
	{ 0x8000, 0x87ff, MRA_RAM },
	{ 0x8c00, 0x8e7f, MRA_RAM },
	{ 0x8e80, 0x8fff, MRA_RAM },
	{ 0xe000, 0xe001, protection_r },
MEMORY_END

static MEMORY_WRITE_START( skyskipr_writemem )
	{ 0x0000, 0x7fff, MWA_ROM },
	{ 0x8000, 0x87ff, MWA_RAM },
	{ 0x8c00, 0x8c02, MWA_RAM, &popeye_background_pos },
	{ 0x8c03, 0x8c03, MWA_RAM, &popeye_palettebank },
	{ 0x8c04, 0x8e7f, MWA_RAM, &spriteram, &spriteram_size },
	{ 0x8e80, 0x8fff, MWA_RAM },
	{ 0xa000, 0xa7ff, MWA_RAM, &popeye_textram },
	{ 0xc000, 0xcfff, skyskipr_bitmap_w },
	{ 0xe000, 0xe001, protection_w },
MEMORY_END

static MEMORY_READ_START( popeye_readmem )
	{ 0x0000, 0x7fff, MRA_ROM },
	{ 0x8000, 0x87ff, MRA_RAM },
	{ 0x8800, 0x8bff, MRA_RAM },
	{ 0x8c00, 0x8e7f, MRA_RAM },
	{ 0x8e80, 0x8fff, MRA_RAM },
	{ 0xe000, 0xe001, protection_r },
MEMORY_END

static MEMORY_WRITE_START( popeye_writemem )
	{ 0x0000, 0x7fff, MWA_ROM },
	{ 0x8000, 0x87ff, MWA_RAM },
	{ 0x8800, 0x8bff, MWA_RAM },
	{ 0x8c00, 0x8c02, MWA_RAM, &popeye_background_pos },
	{ 0x8c03, 0x8c03, MWA_RAM, &popeye_palettebank },
	{ 0x8c04, 0x8e7f, MWA_RAM, &spriteram, &spriteram_size },
	{ 0x8e80, 0x8fff, MWA_RAM },
	{ 0xa000, 0xa7ff, MWA_RAM, &popeye_textram },
	{ 0xc000, 0xdfff, popeye_bitmap_w },
	{ 0xe000, 0xe001, protection_w },
MEMORY_END

static MEMORY_READ_START( popeyebl_readmem )
	{ 0x0000, 0x7fff, MRA_ROM },
	{ 0x8000, 0x87ff, MRA_RAM },
	{ 0x8c00, 0x8e7f, MRA_RAM },
	{ 0x8e80, 0x8fff, MRA_RAM },
	{ 0xe000, 0xe01f, MRA_ROM },
MEMORY_END

static MEMORY_WRITE_START( popeyebl_writemem )
	{ 0x0000, 0x7fff, MWA_ROM },
	{ 0x8000, 0x87ff, MWA_RAM },
	{ 0x8c00, 0x8c02, MWA_RAM, &popeye_background_pos },
	{ 0x8c03, 0x8c03, MWA_RAM, &popeye_palettebank },
	{ 0x8c04, 0x8e7f, MWA_RAM, &spriteram, &spriteram_size },
	{ 0x8e80, 0x8fff, MWA_RAM },
	{ 0xa000, 0xa7ff, MWA_RAM, &popeye_textram },
	{ 0xc000, 0xcfff, skyskipr_bitmap_w },
	{ 0xe000, 0xe01f, MWA_ROM },
MEMORY_END


static PORT_READ_START( readport )
	{ 0x00, 0x00, input_port_0_r },
	{ 0x01, 0x01, input_port_1_r },
	{ 0x02, 0x02, input_port_2_r },
	{ 0x03, 0x03, AY8910_read_port_0_r },
PORT_END

static PORT_WRITE_START( writeport )
	{ 0x00, 0x00, AY8910_control_port_0_w },
	{ 0x01, 0x01, AY8910_write_port_0_w },
PORT_END



INPUT_PORTS_START( skyskipr )
	PORT_START	/* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON2 )

	PORT_START	/* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL )

	PORT_START	/* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_SERVICE1 )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 )

	PORT_START	/* DSW0 */
	PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x03, "A 3/1 B 1/2" )
	PORT_DIPSETTING(    0x0e, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x01, "A 2/1 B 2/5" )
	PORT_DIPSETTING(    0x04, "A 2/1 B 1/3" )
	PORT_DIPSETTING(    0x07, "A 1/1 B 2/1" )
	PORT_DIPSETTING(    0x0f, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x0c, "A 1/1 B 1/2" )
	PORT_DIPSETTING(    0x0d, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x06, "A 1/2 B 1/4" )
	PORT_DIPSETTING(    0x0b, "A 1/2 B 1/5" )
	PORT_DIPSETTING(    0x02, "A 2/5 B 1/1" )
	PORT_DIPSETTING(    0x0a, "A 1/3 B 1/1" )
	PORT_DIPSETTING(    0x09, "A 1/4 B 1/1" )
	PORT_DIPSETTING(    0x05, "A 1/5 B 1/1" )
	PORT_DIPSETTING(    0x08, "A 1/6 B 1/1" )
	PORT_DIPSETTING(    0x00, DEF_STR( Free_Play ) )
	PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x20, 0x20, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x40, 0x40, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL )	/* scans DSW1 one bit at a time */

	PORT_START	/* DSW1 (FAKE - appears as bit 7 of DSW0, see code below) */
	PORT_DIPNAME( 0x03, 0x01, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x03, "1" )
	PORT_DIPSETTING(    0x02, "2" )
	PORT_DIPSETTING(    0x01, "3" )
	PORT_DIPSETTING(    0x00, "4" )
	PORT_DIPNAME( 0x04, 0x04, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x04, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x08, 0x08, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x08, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x20, 0x20, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x20, "15000" )
	PORT_DIPSETTING(    0x00, "30000" )
	PORT_SERVICE( 0x40, IP_ACTIVE_LOW )
	PORT_DIPNAME( 0x80, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x80, DEF_STR( Cocktail ) )
INPUT_PORTS_END


INPUT_PORTS_START( popeye )
	PORT_START	/* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */

	PORT_START	/* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */

	PORT_START	/* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_SERVICE1 )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 )

	PORT_START	/* DSW0 */
	PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x03, "A 3/1 B 1/2" )
	PORT_DIPSETTING(    0x0e, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x01, "A 2/1 B 2/5" )
	PORT_DIPSETTING(    0x04, "A 2/1 B 1/3" )
	PORT_DIPSETTING(    0x07, "A 1/1 B 2/1" )
	PORT_DIPSETTING(    0x0f, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x0c, "A 1/1 B 1/2" )
	PORT_DIPSETTING(    0x0d, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x06, "A 1/2 B 1/4" )
	PORT_DIPSETTING(    0x0b, "A 1/2 B 1/5" )
	PORT_DIPSETTING(    0x02, "A 2/5 B 1/1" )
	PORT_DIPSETTING(    0x0a, "A 1/3 B 1/1" )
	PORT_DIPSETTING(    0x09, "A 1/4 B 1/1" )
	PORT_DIPSETTING(    0x05, "A 1/5 B 1/1" )
	PORT_DIPSETTING(    0x08, "A 1/6 B 1/1" )
	PORT_DIPSETTING(    0x00, DEF_STR( Free_Play ) )
	PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x60, 0x40, "Copyright" )
	PORT_DIPSETTING(    0x40, "Nintendo" )
	PORT_DIPSETTING(    0x20, "Nintendo Co.,Ltd" )
	PORT_DIPSETTING(    0x60, "Nintendo of America" )
//	PORT_DIPSETTING(    0x00, "Nintendo of America" )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL )	/* scans DSW1 one bit at a time */

	PORT_START	/* DSW1 (FAKE - appears as bit 7 of DSW0, see code below) */
	PORT_DIPNAME( 0x03, 0x01, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x03, "1" )
	PORT_DIPSETTING(    0x02, "2" )
	PORT_DIPSETTING(    0x01, "3" )
	PORT_DIPSETTING(    0x00, "4" )
	PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(    0x0c, "Easy" )
	PORT_DIPSETTING(    0x08, "Medium" )
	PORT_DIPSETTING(    0x04, "Hard" )
	PORT_DIPSETTING(    0x00, "Hardest" )
	PORT_DIPNAME( 0x30, 0x30, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x30, "40000" )
	PORT_DIPSETTING(    0x20, "60000" )
	PORT_DIPSETTING(    0x10, "80000" )
	PORT_DIPSETTING(    0x00, "None" )
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Demo_Sounds ) )
	PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x80, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x80, DEF_STR( Cocktail ) )
INPUT_PORTS_END


INPUT_PORTS_START( popeyef )
	PORT_START	/* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */

	PORT_START	/* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */

	PORT_START	/* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* probably unused */
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_SERVICE1 )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 )

	PORT_START	/* DSW0 */
	PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x03, "A 3/1 B 1/2" )
	PORT_DIPSETTING(    0x0e, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x01, "A 2/1 B 2/5" )
	PORT_DIPSETTING(    0x04, "A 2/1 B 1/3" )
	PORT_DIPSETTING(    0x07, "A 1/1 B 2/1" )
	PORT_DIPSETTING(    0x0f, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x0c, "A 1/1 B 1/2" )
	PORT_DIPSETTING(    0x0d, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x06, "A 1/2 B 1/4" )
	PORT_DIPSETTING(    0x0b, "A 1/2 B 1/5" )
	PORT_DIPSETTING(    0x02, "A 2/5 B 1/1" )
	PORT_DIPSETTING(    0x0a, "A 1/3 B 1/1" )
	PORT_DIPSETTING(    0x09, "A 1/4 B 1/1" )
	PORT_DIPSETTING(    0x05, "A 1/5 B 1/1" )
	PORT_DIPSETTING(    0x08, "A 1/6 B 1/1" )
	PORT_DIPSETTING(    0x00, DEF_STR( Free_Play ) )
	PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x60, 0x40, "Copyright" )
	PORT_DIPSETTING(    0x40, "Nintendo" )
	PORT_DIPSETTING(    0x20, "Nintendo Co.,Ltd" )
	PORT_DIPSETTING(    0x60, "Nintendo of America" )
//	PORT_DIPSETTING(    0x00, "Nintendo of America" )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL )	/* scans DSW1 one bit at a time */

	PORT_START	/* DSW1 (FAKE - appears as bit 7 of DSW0, see code below) */
	PORT_DIPNAME( 0x03, 0x01, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x03, "1" )
	PORT_DIPSETTING(    0x02, "2" )
	PORT_DIPSETTING(    0x01, "3" )
	PORT_DIPSETTING(    0x00, "4" )
	PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(    0x0c, "Easy" )
	PORT_DIPSETTING(    0x08, "Medium" )
	PORT_DIPSETTING(    0x04, "Hard" )
	PORT_DIPSETTING(    0x00, "Hardest" )
	PORT_DIPNAME( 0x30, 0x30, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x30, "20000" )
	PORT_DIPSETTING(    0x20, "30000" )
	PORT_DIPSETTING(    0x10, "50000" )
	PORT_DIPSETTING(    0x00, "None" )
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Demo_Sounds ) )
	PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x80, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x80, DEF_STR( Cocktail ) )
INPUT_PORTS_END


static struct GfxLayout charlayout =
{
	16,16,	/* 16*16 characters (8*8 doubled) */
	RGN_FRAC(1,1),
	1,
	{ 0 },
	{ 7,7, 6,6, 5,5, 4,4, 3,3, 2,2, 1,1, 0,0 },
	{ 0*8,0*8, 1*8,1*8, 2*8,2*8, 3*8,3*8, 4*8,4*8, 5*8,5*8, 6*8,6*8, 7*8,7*8 },
	8*8
};

static struct GfxLayout spritelayout =
{
	16,16,
	RGN_FRAC(1,4),
	2,
	{ 0, RGN_FRAC(1,2) },
	{RGN_FRAC(1,4)+7,RGN_FRAC(1,4)+6,RGN_FRAC(1,4)+5,RGN_FRAC(1,4)+4,
	 RGN_FRAC(1,4)+3,RGN_FRAC(1,4)+2,RGN_FRAC(1,4)+1,RGN_FRAC(1,4)+0,
	 7,6,5,4,3,2,1,0 },
	{ 15*8, 14*8, 13*8, 12*8, 11*8, 10*8, 9*8, 8*8,
	  7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8, },
	16*8
};

static struct GfxDecodeInfo gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0, &charlayout,      0, 16 },	/* chars */
	{ REGION_GFX2, 0, &spritelayout, 16*2, 64 },	/* sprites */
	{ -1 } /* end of array */
};



static int dswbit;

static WRITE_HANDLER( popeye_portB_w )
{
	/* bit 0 flips screen */
	flip_screen_set(data & 1);

	/* bits 1-3 select DSW1 bit to read */
	dswbit = (data & 0x0e) >> 1;
}

static READ_HANDLER( popeye_portA_r )
{
	int res;


	res = input_port_3_r(offset);
	res |= (input_port_4_r(offset) << (7-dswbit)) & 0x80;

	return res;
}

static struct AY8910interface ay8910_interface =
{
	1,	/* 1 chip */
	8000000/4,	/* 2 MHz */
	{ 40 },
	{ popeye_portA_r },
	{ 0 },
	{ 0 },
	{ popeye_portB_w }
};



static MACHINE_DRIVER_START( skyskipr )
	/* basic machine hardware */
	MDRV_CPU_ADD_TAG("main", Z80, 8000000/2)	/* 4 MHz */
	MDRV_CPU_MEMORY(skyskipr_readmem,skyskipr_writemem)
	MDRV_CPU_PORTS(readport,writeport)
	MDRV_CPU_VBLANK_INT(popeye_interrupt,1)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
	MDRV_SCREEN_SIZE(32*16, 32*16)
	MDRV_VISIBLE_AREA(0*16, 32*16-1, 2*16, 30*16-1)
	MDRV_GFXDECODE(gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(16+16+256)
	MDRV_COLORTABLE_LENGTH(16*2+64*4)

	MDRV_PALETTE_INIT(popeye)
	MDRV_VIDEO_START(skyskipr)
	MDRV_VIDEO_UPDATE(popeye)

	/* sound hardware */
	MDRV_SOUND_ADD(AY8910, ay8910_interface)
MACHINE_DRIVER_END


static MACHINE_DRIVER_START( popeye )
	MDRV_IMPORT_FROM(skyskipr)
	MDRV_CPU_MODIFY("main")
	MDRV_CPU_MEMORY(popeye_readmem,popeye_writemem)

	MDRV_VIDEO_START(popeye)
MACHINE_DRIVER_END


static MACHINE_DRIVER_START( popeyebl )
	MDRV_IMPORT_FROM(skyskipr)
	MDRV_CPU_MODIFY("main")
	MDRV_CPU_MEMORY(popeyebl_readmem,popeyebl_writemem)

	MDRV_PALETTE_INIT(popeyebl)
	MDRV_VIDEO_START(popeye)
MACHINE_DRIVER_END



/***************************************************************************

  Game driver(s)

***************************************************************************/

ROM_START( skyskipr )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
	ROM_LOAD( "tnx1-c.2a",    0x0000, 0x1000, 0xbdc7f218 )
	ROM_LOAD( "tnx1-c.2b",    0x1000, 0x1000, 0xcbe601a8 )
	ROM_LOAD( "tnx1-c.2c",    0x2000, 0x1000, 0x5ca79abf )
	ROM_LOAD( "tnx1-c.2d",    0x3000, 0x1000, 0x6b7a7071 )
	ROM_LOAD( "tnx1-c.2e",    0x4000, 0x1000, 0x6b0c0525 )
	ROM_LOAD( "tnx1-c.2f",    0x5000, 0x1000, 0xd1712424 )
	ROM_LOAD( "tnx1-c.2g",    0x6000, 0x1000, 0x8b33c4cf )
	/* 7000-7fff empty */

	ROM_REGION( 0x0800, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "tnx1-v.3h",    0x0000, 0x0800, 0xecb6a046 )

	ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD( "tnx1-t.1e",    0x0000, 0x1000, 0x01c1120e )
	ROM_LOAD( "tnx1-t.2e",    0x1000, 0x1000, 0x70292a71 )
	ROM_LOAD( "tnx1-t.3e",    0x2000, 0x1000, 0x92b6a0e8 )
	ROM_LOAD( "tnx1-t.5e",    0x3000, 0x1000, 0xcc5f0ac3 )

	ROM_REGION( 0x0340, REGION_PROMS, 0 )
	ROM_LOAD( "tnx1-t.4a",    0x0000, 0x0020, 0x98846924 ) /* background palette */
	ROM_LOAD( "tnx1-t.1a",    0x0020, 0x0020, 0xc2bca435 ) /* char palette */
	ROM_LOAD( "tnx1-t.3a",    0x0040, 0x0100, 0x8abf9de4 ) /* sprite palette - low 4 bits */
	ROM_LOAD( "tnx1-t.2a",    0x0140, 0x0100, 0xaa7ff322 ) /* sprite palette - high 4 bits */
	ROM_LOAD( "tnx1-t.3j",    0x0240, 0x0100, 0x1c5c8dea ) /* timing for the protection ALU */
ROM_END

ROM_START( popeye )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
	ROM_LOAD( "c-7a",         0x0000, 0x2000, 0x9af7c821 )
	ROM_LOAD( "c-7b",         0x2000, 0x2000, 0xc3704958 )
	ROM_LOAD( "c-7c",         0x4000, 0x2000, 0x5882ebf9 )
	ROM_LOAD( "c-7e",         0x6000, 0x2000, 0xef8649ca )

	ROM_REGION( 0x0800, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "v-5n",         0x0000, 0x0800, 0xcca61ddd )	/* first half is empty */
	ROM_CONTINUE(             0x0000, 0x0800 )

	ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD( "v-1e",         0x0000, 0x2000, 0x0f2cd853 )
	ROM_LOAD( "v-1f",         0x2000, 0x2000, 0x888f3474 )
	ROM_LOAD( "v-1j",         0x4000, 0x2000, 0x7e864668 )
	ROM_LOAD( "v-1k",         0x6000, 0x2000, 0x49e1d170 )

	ROM_REGION( 0x0340, REGION_PROMS, 0 )
	ROM_LOAD( "prom-cpu.4a",  0x0000, 0x0020, 0x375e1602 ) /* background palette */
	ROM_LOAD( "prom-cpu.3a",  0x0020, 0x0020, 0xe950bea1 ) /* char palette */
	ROM_LOAD( "prom-cpu.5b",  0x0040, 0x0100, 0xc5826883 ) /* sprite palette - low 4 bits */
	ROM_LOAD( "prom-cpu.5a",  0x0140, 0x0100, 0xc576afba ) /* sprite palette - high 4 bits */
	ROM_LOAD( "prom-vid.7j",  0x0240, 0x0100, 0xa4655e2e ) /* timing for the protection ALU */
ROM_END

ROM_START( popeyeu )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
	ROM_LOAD( "7a",           0x0000, 0x2000, 0x0bd04389 )
	ROM_LOAD( "7b",           0x2000, 0x2000, 0xefdf02c3 )
	ROM_LOAD( "7c",           0x4000, 0x2000, 0x8eee859e )
	ROM_LOAD( "7e",           0x6000, 0x2000, 0xb64aa314 )

	ROM_REGION( 0x0800, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "v-5n",         0x0000, 0x0800, 0xcca61ddd )	/* first half is empty */
	ROM_CONTINUE(             0x0000, 0x0800 )

	ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD( "v-1e",         0x0000, 0x2000, 0x0f2cd853 )
	ROM_LOAD( "v-1f",         0x2000, 0x2000, 0x888f3474 )
	ROM_LOAD( "v-1j",         0x4000, 0x2000, 0x7e864668 )
	ROM_LOAD( "v-1k",         0x6000, 0x2000, 0x49e1d170 )

	ROM_REGION( 0x0340, REGION_PROMS, 0 )
	ROM_LOAD( "prom-cpu.4a",  0x0000, 0x0020, 0x375e1602 ) /* background palette */
	ROM_LOAD( "prom-cpu.3a",  0x0020, 0x0020, 0xe950bea1 ) /* char palette */
	ROM_LOAD( "prom-cpu.5b",  0x0040, 0x0100, 0xc5826883 ) /* sprite palette - low 4 bits */
	ROM_LOAD( "prom-cpu.5a",  0x0140, 0x0100, 0xc576afba ) /* sprite palette - high 4 bits */
	ROM_LOAD( "prom-vid.7j",  0x0240, 0x0100, 0xa4655e2e ) /* timing for the protection ALU */
ROM_END

ROM_START( popeyef )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
	ROM_LOAD( "tpp2-c_f.7a",  0x0000, 0x2000, 0x5fc5264d )
	ROM_LOAD( "tpp2-c_f.7b",  0x2000, 0x2000, 0x51de48e8 )
	ROM_LOAD( "tpp2-c_f.7c",  0x4000, 0x2000, 0x62df9647 )
	ROM_LOAD( "tpp2-c_f.7e",  0x6000, 0x2000, 0xf31e7916 )

	ROM_REGION( 0x0800, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "v-5n",         0x0000, 0x0800, 0xcca61ddd )	/* first half is empty */
	ROM_CONTINUE(             0x0000, 0x0800 )

	ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD( "v-1e",         0x0000, 0x2000, 0x0f2cd853 )
	ROM_LOAD( "v-1f",         0x2000, 0x2000, 0x888f3474 )
	ROM_LOAD( "v-1j",         0x4000, 0x2000, 0x7e864668 )
	ROM_LOAD( "v-1k",         0x6000, 0x2000, 0x49e1d170 )

	ROM_REGION( 0x0340, REGION_PROMS, 0 )
	ROM_LOAD( "prom-cpu.4a",  0x0000, 0x0020, 0x375e1602 ) /* background palette */
	ROM_LOAD( "prom-cpu.3a",  0x0020, 0x0020, 0xe950bea1 ) /* char palette */
	ROM_LOAD( "prom-cpu.5b",  0x0040, 0x0100, 0xc5826883 ) /* sprite palette - low 4 bits */
	ROM_LOAD( "prom-cpu.5a",  0x0140, 0x0100, 0xc576afba ) /* sprite palette - high 4 bits */
	ROM_LOAD( "prom-vid.7j",  0x0240, 0x0100, 0xa4655e2e ) /* timing for the protection ALU */
ROM_END

ROM_START( popeyebl )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
	ROM_LOAD( "po1",          0x0000, 0x2000, 0xb14a07ca )
	ROM_LOAD( "po2",          0x2000, 0x2000, 0x995475ff )
	ROM_LOAD( "po3",          0x4000, 0x2000, 0x99d6a04a )
	ROM_LOAD( "po4",          0x6000, 0x2000, 0x548a6514 )
	ROM_LOAD( "po_d1-e1.bin", 0xe000, 0x0020, 0x8de22998 )	/* protection PROM */

	ROM_REGION( 0x0800, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "v-5n",         0x0000, 0x0800, 0xcca61ddd )	/* first half is empty */
	ROM_CONTINUE(             0x0000, 0x0800 )

	ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD( "v-1e",         0x0000, 0x2000, 0x0f2cd853 )
	ROM_LOAD( "v-1f",         0x2000, 0x2000, 0x888f3474 )
	ROM_LOAD( "v-1j",         0x4000, 0x2000, 0x7e864668 )
	ROM_LOAD( "v-1k",         0x6000, 0x2000, 0x49e1d170 )

	ROM_REGION( 0x0240, REGION_PROMS, 0 )
	ROM_LOAD( "popeye.pr1",   0x0000, 0x0020, 0xd138e8a4 ) /* background palette */
	ROM_LOAD( "popeye.pr2",   0x0020, 0x0020, 0x0f364007 ) /* char palette */
	ROM_LOAD( "popeye.pr3",   0x0040, 0x0100, 0xca4d7b6a ) /* sprite palette - low 4 bits */
	ROM_LOAD( "popeye.pr4",   0x0140, 0x0100, 0xcab9bc53 ) /* sprite palette - high 4 bits */
ROM_END



DRIVER_INIT( skyskipr )
{
	unsigned char *buffer;
	data8_t *rom = memory_region(REGION_CPU1);
	int len = 0x10000;

	/* decrypt the program ROMs */
	if ((buffer = malloc(len)))
	{
		int i;
		for (i = 0;i < len; i++)
			buffer[i] = BITSWAP8(rom[BITSWAP16(i,15,14,13,12,11,10,8,7,0,1,2,4,5,9,3,6) ^ 0xfc],3,4,2,5,1,6,0,7);
		memcpy(rom,buffer,len);
		free(buffer);
	}
}

DRIVER_INIT( popeye )
{
	unsigned char *buffer;
	data8_t *rom = memory_region(REGION_CPU1);
	int len = 0x10000;

	/* decrypt the program ROMs */
	if ((buffer = malloc(len)))
	{
		int i;
		for (i = 0;i < len; i++)
			buffer[i] = BITSWAP8(rom[BITSWAP16(i,15,14,13,12,11,10,8,7,6,3,9,5,4,2,1,0) ^ 0x3f],3,4,2,5,1,6,0,7);
		memcpy(rom,buffer,len);
		free(buffer);
	}
}


GAME( 1981, skyskipr, 0,      skyskipr, skyskipr, skyskipr, ROT0, "Nintendo", "Sky Skipper" )
GAME( 1982, popeye,   0,      popeye,   popeye,   popeye,   ROT0, "Nintendo", "Popeye (revision D)" )
GAME( 1982, popeyeu,  popeye, popeye,   popeye,   popeye,   ROT0, "Nintendo", "Popeye (revision D not protected)" )
GAME( 1982, popeyef,  popeye, popeye,   popeyef,  popeye,   ROT0, "Nintendo", "Popeye (revision F)" )
GAME( 1982, popeyebl, popeye, popeyebl, popeye,   0,        ROT0, "bootleg",  "Popeye (bootleg)" )
