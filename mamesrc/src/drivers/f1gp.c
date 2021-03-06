/***************************************************************************

F-1 Grand Prix       (c) 1991 Video System Co.

driver by Nicola Salmoria

Notes:
- The ROZ layer generator is a Konami 053936.
- f1gp2's hardware is very similar to Lethal Crash Race, main difference
  being an extra 68000.

TODO:
f1gp:
- gfxctrl register not understood - handling of fg/sprite priority to fix
  "continue" screen is just a kludge.
f1gp2:
- sprite lag noticeable in the animation at the end of a race (the wheels
  of the car are sprites while the car is the fg tilemap)

***************************************************************************/

#include "driver.h"
#include "vidhrdw/konamiic.h"
#include "f1gp.h"



static data16_t *sharedram;

static READ16_HANDLER( sharedram_r )
{
	return sharedram[offset];
}

static WRITE16_HANDLER( sharedram_w )
{
	COMBINE_DATA(&sharedram[offset]);
}

static READ16_HANDLER( extrarom_r )
{
	data8_t *rom = memory_region(REGION_USER1);

	offset *= 2;

	return rom[offset] | (rom[offset+1] << 8);
}

static READ16_HANDLER( extrarom2_r )
{
	data8_t *rom = memory_region(REGION_USER2);

	offset *= 2;

	return rom[offset] | (rom[offset+1] << 8);
}

static WRITE_HANDLER( f1gp_sh_bankswitch_w )
{
	data8_t *rom = memory_region(REGION_CPU3) + 0x10000;

	cpu_setbank(1,rom + (data & 0x01) * 0x8000);
}


static int pending_command;

static WRITE16_HANDLER( sound_command_w )
{
	if (ACCESSING_LSB)
	{
		pending_command = 1;
		soundlatch_w(offset,data & 0xff);
		cpu_set_irq_line(2, IRQ_LINE_NMI, PULSE_LINE);
	}
}

static READ16_HANDLER( command_pending_r )
{
	return (pending_command ? 0xff : 0);
}

static WRITE_HANDLER( pending_command_clear_w )
{
	pending_command = 0;
}



static MEMORY_READ16_START( f1gp_readmem1 )
	{ 0x000000, 0x01ffff, MRA16_ROM },
	{ 0x100000, 0x2fffff, extrarom_r },
	{ 0xa00000, 0xbfffff, extrarom2_r },
	{ 0xc00000, 0xc3ffff, f1gp_zoomdata_r },
	{ 0xd00000, 0xd01fff, f1gp_rozvideoram_r },
	{ 0xd02000, 0xd03fff, f1gp_rozvideoram_r },	/* mirror */
	{ 0xd04000, 0xd05fff, f1gp_rozvideoram_r },	/* mirror */
	{ 0xd06000, 0xd07fff, f1gp_rozvideoram_r },	/* mirror */
	{ 0xe00000, 0xe03fff, MRA16_RAM },
	{ 0xe04000, 0xe07fff, MRA16_RAM },
	{ 0xf00000, 0xf003ff, MRA16_RAM },
	{ 0xf10000, 0xf103ff, MRA16_RAM },
	{ 0xff8000, 0xffbfff, MRA16_RAM },
	{ 0xffc000, 0xffcfff, sharedram_r },
	{ 0xffd000, 0xffdfff, MRA16_RAM },
	{ 0xffe000, 0xffefff, MRA16_RAM },
	{ 0xfff000, 0xfff001, input_port_0_word_r },
//	{ 0xfff002, 0xfff003,  },	analog wheel?
	{ 0xfff004, 0xfff005, input_port_1_word_r },
	{ 0xfff006, 0xfff007, input_port_2_word_r },
	{ 0xfff008, 0xfff009, command_pending_r },
	{ 0xfff050, 0xfff051, input_port_3_word_r },
MEMORY_END

static MEMORY_WRITE16_START( f1gp_writemem1 )
	{ 0x000000, 0x01ffff, MWA16_ROM },
	{ 0xc00000, 0xc3ffff, f1gp_zoomdata_w },
	{ 0xd00000, 0xd01fff, f1gp_rozvideoram_w, &f1gp_rozvideoram },					// BACK VRAM
	{ 0xd02000, 0xd03fff, f1gp_rozvideoram_w },	/* mirror */
	{ 0xd04000, 0xd05fff, f1gp_rozvideoram_w },	/* mirror */
	{ 0xd06000, 0xd07fff, f1gp_rozvideoram_w },	/* mirror */
	{ 0xe00000, 0xe03fff, MWA16_RAM, &f1gp_spr1cgram, &f1gp_spr1cgram_size },		// SPR-1 CG RAM
	{ 0xe04000, 0xe07fff, MWA16_RAM, &f1gp_spr2cgram, &f1gp_spr2cgram_size },		// SPR-2 CG RAM
	{ 0xf00000, 0xf003ff, MWA16_RAM, &f1gp_spr1vram },								// SPR-1 VRAM
	{ 0xf10000, 0xf103ff, MWA16_RAM, &f1gp_spr2vram },								// SPR-2 VRAM
	{ 0xff8000, 0xffbfff, MWA16_RAM },												// WORK RAM-1
	{ 0xffc000, 0xffcfff, sharedram_w, &sharedram },								// DUAL RAM
	{ 0xffd000, 0xffdfff, f1gp_fgvideoram_w, &f1gp_fgvideoram },					// CHARACTER
	{ 0xffe000, 0xffefff, paletteram16_xRRRRRGGGGGBBBBB_word_w, &paletteram16 },	// PALETTE
	{ 0xfff000, 0xfff001, f1gp_gfxctrl_w },
	{ 0xfff002, 0xfff005, f1gp_fgscroll_w },
	{ 0xfff008, 0xfff009, sound_command_w },
	{ 0xfff040, 0xfff05f, MWA16_RAM, &K053936_0_ctrl },
MEMORY_END

static MEMORY_READ16_START( f1gp2_readmem1 )
	{ 0x000000, 0x03ffff, MRA16_ROM },
	{ 0x100000, 0x2fffff, extrarom_r },
	{ 0xa00000, 0xa07fff, MRA16_RAM },
	{ 0xd00000, 0xd01fff, f1gp_rozvideoram_r },
	{ 0xe00000, 0xe00fff, MRA16_RAM },
	{ 0xff8000, 0xffbfff, MRA16_RAM },
	{ 0xffc000, 0xffcfff, sharedram_r },
	{ 0xffd000, 0xffdfff, MRA16_RAM },
	{ 0xffe000, 0xffefff, MRA16_RAM },
	{ 0xfff000, 0xfff001, input_port_0_word_r },
//	{ 0xfff002, 0xfff003,  },	analog wheel?
	{ 0xfff004, 0xfff005, input_port_1_word_r },
	{ 0xfff006, 0xfff007, input_port_2_word_r },
	{ 0xfff008, 0xfff009, command_pending_r },
	{ 0xfff00a, 0xfff00b, input_port_3_word_r },
MEMORY_END

static MEMORY_WRITE16_START( f1gp2_writemem1 )
	{ 0x000000, 0x03ffff, MWA16_ROM },
	{ 0xa00000, 0xa07fff, MWA16_RAM, &f1gp2_sprcgram },								// SPR-1 CG RAM + SPR-2 CG RAM
	{ 0xd00000, 0xd01fff, f1gp_rozvideoram_w, &f1gp_rozvideoram },					// BACK VRAM
	{ 0xe00000, 0xe00fff, MWA16_RAM, &f1gp2_spritelist },							// not checked + SPR-1 VRAM + SPR-2 VRAM
	{ 0xff8000, 0xffbfff, MWA16_RAM },												// WORK RAM-1
	{ 0xffc000, 0xffcfff, sharedram_w, &sharedram },								// DUAL RAM
	{ 0xffd000, 0xffdfff, f1gp_fgvideoram_w, &f1gp_fgvideoram },					// CHARACTER
	{ 0xffe000, 0xffefff, paletteram16_xRRRRRGGGGGBBBBB_word_w, &paletteram16 },	// PALETTE
	{ 0xfff000, 0xfff001, f1gp2_gfxctrl_w },
	{ 0xfff008, 0xfff009, sound_command_w },
	{ 0xfff020, 0xfff02f, MWA16_RAM, &K053936_0_ctrl },
	{ 0xfff044, 0xfff047, f1gp_fgscroll_w },
MEMORY_END

static MEMORY_READ16_START( readmem2 )
	{ 0x000000, 0x01ffff, MRA16_ROM },
	{ 0xff8000, 0xffbfff, MRA16_RAM },
	{ 0xffc000, 0xffcfff, sharedram_r },
MEMORY_END

static MEMORY_WRITE16_START( writemem2 )
	{ 0x000000, 0x01ffff, MWA16_ROM },
	{ 0xff8000, 0xffbfff, MWA16_RAM },
	{ 0xffc000, 0xffcfff, sharedram_w },
MEMORY_END

static MEMORY_READ_START( sound_readmem )
	{ 0x0000, 0x77ff, MRA_ROM },
	{ 0x7800, 0x7fff, MRA_RAM },
	{ 0x8000, 0xffff, MRA_BANK1 },
MEMORY_END

static MEMORY_WRITE_START( sound_writemem )
	{ 0x0000, 0x77ff, MWA_ROM },
	{ 0x7800, 0x7fff, MWA_RAM },
	{ 0x8000, 0xffff, MWA_ROM },
MEMORY_END

static PORT_READ_START( sound_readport )
	{ 0x14, 0x14, soundlatch_r },
	{ 0x18, 0x18, YM2610_status_port_0_A_r },
	{ 0x1a, 0x1a, YM2610_status_port_0_B_r },
PORT_END

static PORT_WRITE_START( sound_writeport )
	{ 0x00, 0x00, f1gp_sh_bankswitch_w },	// f1gp
	{ 0x0c, 0x0c, f1gp_sh_bankswitch_w },	// f1gp2
	{ 0x14, 0x14, pending_command_clear_w },
	{ 0x18, 0x18, YM2610_control_port_0_A_w },
	{ 0x19, 0x19, YM2610_data_port_0_A_w },
	{ 0x1a, 0x1a, YM2610_control_port_0_B_w },
	{ 0x1b, 0x1b, YM2610_data_port_0_B_w },
PORT_END



INPUT_PORTS_START( f1gp )
	PORT_START
	PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_2WAY )
	PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY )
	PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON2 )
	PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON1 )
	PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_COIN1 )
	PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_COIN2 )
	PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_START1 )
	PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_SERVICE1 )
	PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN )

	PORT_START
	PORT_DIPNAME( 0x0100, 0x0100, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0100, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0e00, 0x0e00, DEF_STR( Coin_A ) )
	PORT_DIPSETTING(      0x0a00, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(      0x0c00, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(      0x0e00, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(      0x0800, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(      0x0600, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(      0x0400, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(      0x0200, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( 1C_6C ) )
	PORT_DIPNAME( 0x7000, 0x7000, DEF_STR( Coin_B ) )
	PORT_DIPSETTING(      0x5000, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(      0x6000, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(      0x7000, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(      0x4000, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(      0x3000, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(      0x2000, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(      0x1000, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( 1C_6C ) )
	PORT_DIPNAME( 0x8000, 0x8000, "2 to Start, 1 to Cont." )	// Other desc. was too long !
	PORT_DIPSETTING(      0x8000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0003, 0x0003, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(      0x0002, "Easy" )
	PORT_DIPSETTING(      0x0003, "Normal" )
	PORT_DIPSETTING(      0x0001, "Hard" )
	PORT_DIPSETTING(      0x0000, "Hardest" )
	PORT_DIPNAME( 0x0004, 0x0004, "Game Mode" )
	PORT_DIPSETTING(      0x0004, "Single" )
	PORT_DIPSETTING(      0x0000, "Multiple" )
	PORT_DIPNAME( 0x0008, 0x0008, "Multi Player" )
	PORT_DIPSETTING(      0x0008, "Type 1" )
	PORT_DIPSETTING(      0x0000, "Type 2" )
	PORT_DIPNAME( 0x0010, 0x0010, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0010, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0020, 0x0020, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0020, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0040, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0080, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )

	PORT_START
	PORT_SERVICE( 0x0100, IP_ACTIVE_LOW )
	PORT_DIPNAME( 0x0200, 0x0200, DEF_STR( Flip_Screen ) )
	PORT_DIPSETTING(      0x0200, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0400, 0x0000, DEF_STR( Demo_Sounds ) )
	PORT_DIPSETTING(      0x0400, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0800, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x1000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x2000, 0x2000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x2000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x4000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x8000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )

	PORT_START
	PORT_DIPNAME( 0x001f, 0x0010, "Country" )
	PORT_DIPSETTING(      0x0010, "World" )
	PORT_DIPSETTING(      0x0001, "USA & Canada" )
	PORT_DIPSETTING(      0x0000, "Japan" )
	PORT_DIPSETTING(      0x0002, "Korea" )
	PORT_DIPSETTING(      0x0004, "Hong Kong" )
	PORT_DIPSETTING(      0x0008, "Taiwan" )
	/* all other values are invalid */
INPUT_PORTS_END


/* the same as f1gp, but with an extra button */
INPUT_PORTS_START( f1gp2 )
	PORT_START
	PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_2WAY )
	PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY )
	PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON2 )
	PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON1 )
	PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 )
	PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_COIN1 )
	PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_COIN2 )
	PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_START1 )
	PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_SERVICE1 )
	PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN )

	PORT_START
	PORT_DIPNAME( 0x0100, 0x0100, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0100, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0e00, 0x0e00, DEF_STR( Coin_A ) )
	PORT_DIPSETTING(      0x0a00, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(      0x0c00, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(      0x0e00, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(      0x0800, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(      0x0600, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(      0x0400, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(      0x0200, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( 1C_6C ) )
	PORT_DIPNAME( 0x7000, 0x7000, DEF_STR( Coin_B ) )
	PORT_DIPSETTING(      0x5000, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(      0x6000, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(      0x7000, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(      0x4000, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(      0x3000, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(      0x2000, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(      0x1000, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( 1C_6C ) )
	PORT_DIPNAME( 0x8000, 0x8000, "2 to Start, 1 to Cont." )	// Other desc. was too long !
	PORT_DIPSETTING(      0x8000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0003, 0x0003, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(      0x0002, "Easy" )
	PORT_DIPSETTING(      0x0003, "Normal" )
	PORT_DIPSETTING(      0x0001, "Hard" )
	PORT_DIPSETTING(      0x0000, "Hardest" )
	PORT_DIPNAME( 0x0004, 0x0004, "Game Mode" )
	PORT_DIPSETTING(      0x0004, "Single" )
	PORT_DIPSETTING(      0x0000, "Multiple" )
	PORT_DIPNAME( 0x0008, 0x0008, "Multi Player" )
	PORT_DIPSETTING(      0x0008, "Type 1" )
	PORT_DIPSETTING(      0x0000, "Type 2" )
	PORT_DIPNAME( 0x0010, 0x0010, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0010, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0020, 0x0020, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0020, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0040, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0080, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )

	PORT_START
	PORT_SERVICE( 0x0100, IP_ACTIVE_LOW )
	PORT_DIPNAME( 0x0200, 0x0200, DEF_STR( Flip_Screen ) )
	PORT_DIPSETTING(      0x0200, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0400, 0x0000, DEF_STR( Demo_Sounds ) )
	PORT_DIPSETTING(      0x0400, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0800, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x1000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x2000, 0x2000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x2000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x4000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x8000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )

	PORT_START
	PORT_DIPNAME( 0x0001, 0x0001, "Country" )
	PORT_DIPSETTING(      0x0001, "World" )
	PORT_DIPSETTING(      0x0000, "Japan" )
INPUT_PORTS_END



static struct GfxLayout charlayout =
{
	8,8,
	RGN_FRAC(1,1),
	8,
	{ 0, 1, 2, 3, 4, 5, 6, 7 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
	{ 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64 },
	64*8
};

static struct GfxLayout tilelayout =
{
	16,16,
	RGN_FRAC(1,1),
	4,
	{ 0, 1, 2, 3 },
	{ 2*4, 3*4, 0*4, 1*4, 6*4, 7*4, 4*4, 5*4,
			10*4, 11*4, 8*4, 9*4, 14*4, 15*4, 12*4, 13*4 },
	{ 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
			8*64, 9*64, 10*64, 11*64, 12*64, 13*64, 14*64, 15*64 },
	64*16
};

static struct GfxLayout spritelayout =
{
	16,16,
	RGN_FRAC(1,1),
	4,
	{ 0, 1, 2, 3 },
	{ 1*4, 0*4, 3*4, 2*4, 5*4, 4*4, 7*4, 6*4,
			9*4, 8*4, 11*4, 10*4, 13*4, 12*4, 15*4, 14*4 },
	{ 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
			8*64, 9*64, 10*64, 11*64, 12*64, 13*64, 14*64, 15*64 },
	128*8
};

static struct GfxDecodeInfo f1gp_gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0, &charlayout,   0x000,  1 },
	{ REGION_GFX2, 0, &spritelayout, 0x100, 16 },
	{ REGION_GFX3, 0, &spritelayout, 0x200, 16 },
	{ REGION_GFX4, 0, &tilelayout,   0x300, 16 },	/* changed at runtime */
	{ -1 } /* end of array */
};

static struct GfxDecodeInfo f1gp2_gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0, &charlayout,   0x000,  1 },
	{ REGION_GFX2, 0, &spritelayout, 0x200, 32 },
	{ REGION_GFX3, 0, &tilelayout,   0x100, 16 },
	{ -1 } /* end of array */
};



static void irqhandler(int irq)
{
	cpu_set_irq_line(2,0,irq ? ASSERT_LINE : CLEAR_LINE);
}

static struct YM2610interface ym2610_interface =
{
	1,
	8000000,	/* 8 MHz??? */
	{ 25 },
	{ 0 },
	{ 0 },
	{ 0 },
	{ 0 },
	{ irqhandler },
	{ REGION_SOUND1 },
	{ REGION_SOUND2 },
	{ YM3012_VOL(100,MIXER_PAN_LEFT,100,MIXER_PAN_RIGHT) }
};



static MACHINE_DRIVER_START( f1gp )

	/* basic machine hardware */
	MDRV_CPU_ADD_TAG("main",M68000,10000000)	/* 10 MHz ??? */
	MDRV_CPU_MEMORY(f1gp_readmem1,f1gp_writemem1)
	MDRV_CPU_VBLANK_INT(irq1_line_hold,1)

	MDRV_CPU_ADD(M68000,10000000)	/* 10 MHz ??? */
	MDRV_CPU_MEMORY(readmem2,writemem2)
	MDRV_CPU_VBLANK_INT(irq1_line_hold,1)

	MDRV_CPU_ADD(Z80,8000000/2)	/* 4 MHz ??? */
	MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
	MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	MDRV_CPU_PORTS(sound_readport,sound_writeport)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_INTERLEAVE(100) /* 100 CPU slices per frame */

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
	MDRV_SCREEN_SIZE(64*8, 32*8)
	MDRV_VISIBLE_AREA(0*8, 40*8-1, 1*8, 31*8-1)
	MDRV_GFXDECODE(f1gp_gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(2048)

	MDRV_VIDEO_START(f1gp)
	MDRV_VIDEO_UPDATE(f1gp)

	/* sound hardware */
	MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
	MDRV_SOUND_ADD(YM2610, ym2610_interface)
MACHINE_DRIVER_END

static MACHINE_DRIVER_START( f1gp2 )

	/* basic machine hardware */
	MDRV_IMPORT_FROM(f1gp)
	MDRV_CPU_MODIFY("main")
	MDRV_CPU_MEMORY(f1gp2_readmem1,f1gp2_writemem1)

	/* video hardware */
	MDRV_GFXDECODE(f1gp2_gfxdecodeinfo)
	MDRV_VISIBLE_AREA(0*8, 40*8-1, 0*8, 28*8-1)

	MDRV_VIDEO_START(f1gp2)
	MDRV_VIDEO_UPDATE(f1gp2)
MACHINE_DRIVER_END



ROM_START( f1gp )
	ROM_REGION( 0x20000, REGION_CPU1, 0 )	/* 68000 code */
	ROM_LOAD16_WORD_SWAP( "rom1-a.3",     0x000000, 0x20000, 0x2d8f785b )

	ROM_REGION( 0x200000, REGION_USER1, 0 )	/* extra ROMs mapped at 100000 */
	ROM_LOAD16_BYTE( "rom11-a.2",    0x000000, 0x40000, 0x53df8ea1 )
	ROM_LOAD16_BYTE( "rom10-a.1",    0x000001, 0x40000, 0x46a289fb )
	ROM_LOAD16_BYTE( "rom13-a.4",    0x080000, 0x40000, 0x7d92e1fa )
	ROM_LOAD16_BYTE( "rom12-a.3",    0x080001, 0x40000, 0xd8c1bcf4 )
	ROM_LOAD16_BYTE( "rom6-a.6",     0x100000, 0x40000, 0x6d947a3f )
	ROM_LOAD16_BYTE( "rom7-a.5",     0x100001, 0x40000, 0x7a014ba6 )
	ROM_LOAD16_BYTE( "rom9-a.8",     0x180000, 0x40000, 0x49286572 )
	ROM_LOAD16_BYTE( "rom8-a.7",     0x180001, 0x40000, 0x0ed783c7 )

	ROM_REGION( 0x200000, REGION_USER2, 0 )	/* extra ROMs mapped at a00000 */
											/* containing gfx data for the 053936 */
	ROM_LOAD( "rom2-a.06",    0x000000, 0x100000, 0x747dd112 )
	ROM_LOAD( "rom3-a.05",    0x100000, 0x100000, 0x264aed13 )

	ROM_REGION( 0x20000, REGION_CPU2, 0 )	/* 68000 code */
	ROM_LOAD16_WORD_SWAP( "rom4-a.4",     0x000000, 0x20000, 0x8e811d36 )

	ROM_REGION( 0x30000, REGION_CPU3, 0 )	/* 64k for the audio CPU + banks */
	ROM_LOAD( "rom5-a.8",     0x00000, 0x08000, 0x9ea36e35 )
	ROM_CONTINUE(             0x10000, 0x18000 )

	ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "rom3-b.07",    0x000000, 0x100000, 0xffb1d489 )
	ROM_LOAD( "rom2-b.04",    0x100000, 0x100000, 0xd1b3471f )

	ROM_REGION( 0x100000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD32_WORD( "rom5-b.2",     0x000000, 0x80000, 0x17572b36 )
	ROM_LOAD32_WORD( "rom4-b.3",     0x000002, 0x80000, 0x72d12129 )

	ROM_REGION( 0x080000, REGION_GFX3, ROMREGION_DISPOSE )
	ROM_LOAD32_WORD( "rom7-b.17",    0x000000, 0x40000, 0x2aed9003 )
	ROM_LOAD32_WORD( "rom6-b.16",    0x000002, 0x40000, 0x6789ef12 )

	ROM_REGION( 0x40000, REGION_GFX4, 0 )	/* gfx data for the 053936 */
	/* RAM, not ROM - handled at run time */

	ROM_REGION( 0x100000, REGION_SOUND1, 0 ) /* sound samples */
	ROM_LOAD( "rom14-a.09",   0x000000, 0x100000, 0xb4c1ac31 )

	ROM_REGION( 0x100000, REGION_SOUND2, 0 ) /* sound samples */
	ROM_LOAD( "rom17-a.08",   0x000000, 0x100000, 0xea70303d )
ROM_END

ROM_START( f1gp2 )
	ROM_REGION( 0x40000, REGION_CPU1, 0 )	/* 68000 code */
	ROM_LOAD16_BYTE( "rom12.v1",     0x000000, 0x20000, 0xc5c5f199 )
	ROM_LOAD16_BYTE( "rom14.v2",     0x000001, 0x20000, 0xdd5388e2 )

	ROM_REGION( 0x200000, REGION_USER1, 0 )	/* extra ROMs mapped at 100000 */
	ROM_LOAD( "rom2",         0x100000, 0x100000, 0x3b0cfa82 )
	ROM_CONTINUE(             0x000000, 0x100000 )

	ROM_REGION( 0x20000, REGION_CPU2, 0 )	/* 68000 code */
	ROM_LOAD16_WORD_SWAP( "rom13.v3",     0x000000, 0x20000, 0xc37aa303 )

	ROM_REGION( 0x30000, REGION_CPU3, 0 )	/* 64k for the audio CPU + banks */
	ROM_LOAD( "rom5.v4",      0x00000, 0x08000, 0x6a9398a1 )
	ROM_CONTINUE(             0x10000, 0x18000 )

	ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "rom1",         0x000000, 0x200000, 0xf2d55ad7 )

	ROM_REGION( 0x200000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD( "rom15",        0x000000, 0x200000, 0x1ac03e2e )

	ROM_REGION( 0x400000, REGION_GFX3, ROMREGION_DISPOSE )
	ROM_LOAD( "rom11",        0x000000, 0x100000, 0xb22a2c1f )
	ROM_LOAD( "rom10",        0x100000, 0x100000, 0x43fcbe23 )
	ROM_LOAD( "rom9",         0x200000, 0x100000, 0x1bede8a1 )
	ROM_LOAD( "rom8",         0x300000, 0x100000, 0x98baf2a1 )

	ROM_REGION( 0x080000, REGION_SOUND1, 0 ) /* sound samples */
	ROM_LOAD( "rom4",         0x000000, 0x080000, 0xc2d3d7ad )

	ROM_REGION( 0x100000, REGION_SOUND2, 0 ) /* sound samples */
	ROM_LOAD( "rom3",         0x000000, 0x100000, 0x7f8f066f )
ROM_END



GAMEX( 1991, f1gp,  0, f1gp,  f1gp,  0, ROT90, "Video System Co.", "F-1 Grand Prix",         GAME_NO_COCKTAIL )
GAMEX( 1992, f1gp2, 0, f1gp2, f1gp2, 0, ROT90, "Video System Co.", "F-1 Grand Prix Part II", GAME_NO_COCKTAIL )
