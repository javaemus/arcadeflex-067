/* Angel Kids / Space Position hardware driver

 driver by David Haywood
 with some help from Steph (DSWs, Inputs, other
 bits here and there)

2 Board System, Uses Boards X090-PC-A & X090-PC-B

Both games appear to be joint Sega / Nasco efforts
(although all I see in Angel Kids is 'Exa Planning'
 but I think that has something to do with Nasco   )

Space Position is encrypted, the main processor is
D317-0005 (NEC Z80 Custom), see machine/segacrpt.c
for details on this encryption scheme

*/

/* started 23/01/2002 */

/* notes / todo:

Decrypt Space Position Somehow (not something I
can do)
Unknown Reads / Writes
Whats the Prom for? nothing important?
Clock Speeds etc.
Is the level order correct?
the progress sprite on the side of the screen re-appears at the bottom when you get
to the top, but the wrap-around is needed for other things, actual game bug?

*/

/* readme's

------------------------------------------------------------------------

Angel Kids
833-6599-01
Sega 1988

Nasco X090-PC-A  (Sega 837-6600)

 SW1   SW2


 8255

 8255

       11429 6116 Z80   YM2203 YM2203


 11424 11425 11426 11427  -  -  -  -  5M5165 11428  Z80
                                                         4MHz

                                                         6MHz


Nasco X090-PC-B

                                  2016-55
11437  11445    2016-55  2016-55             U5
11436  11444
11435  11443
11434  11442
11433  11441                  2016-55    2016-55
11432  11440
11431  11439    11446         2016-55

                                             11148
                                             11147
   2016-55 2016-55 2016-55

                                               18.432MHz

11430  11438

------------------------------------------------------------------------

Space Position (JPN Ver.)
(c)1986 Sega / Nasco
X090-PC-A 171-5383
X090-PC-B 171-5384


CPU	:D317-0005 (NEC Z80 Custom)
Sound	:NEC D780C-1
	:YM2203C x 2
OSC	:4.000MHz 6.000MHz
	:18.432MHz


EPR10120.C1	prg
EPR10121.C2	 |
EPR10122.C3	 |
EPR10123.C4	 |
EPR10124.C5	 |
EPR10125.C10	/

EPR10126.D4	snd

EPR10127.06
EPR10128.07
EPR10129.08
EPR10130.14
EPR10131.15
EPR10132.16

EPR10133.17

EPR10134.18
EPR10135.19

63S081N.U5


--- Team Japump!!! ---
http://www.rainemu.com/japump/
http://japump.i.am/
Dumped by Chackn
02/25/2000

------------------------------------------------------------------------

*/


#include "driver.h"
#include "vidhrdw/generic.h"
#include "cpu/z80/z80.h"
#include "machine/segacrpt.h"

static READ_HANDLER( angelkds_sound_r );
static WRITE_HANDLER( angelkds_sound_w );

extern data8_t *angelkds_txvideoram, *angelkds_bgtopvideoram, *angelkds_bgbotvideoram;

WRITE_HANDLER( angelkds_bgtopvideoram_w );
WRITE_HANDLER( angelkds_bgbotvideoram_w );
WRITE_HANDLER( angelkds_txvideoram_w );

WRITE_HANDLER( angelkds_bgtopbank_write );
WRITE_HANDLER( angelkds_bgtopscroll_write );
WRITE_HANDLER( angelkds_bgbotbank_write );
WRITE_HANDLER( angelkds_bgbotscroll_write );
WRITE_HANDLER( angelkds_txbank_write );

WRITE_HANDLER( angelkds_paletteram_w );
WRITE_HANDLER( angelkds_layer_ctrl_write );

VIDEO_START( angelkds );
VIDEO_UPDATE( angelkds );

/*** CPU Banking

*/

static WRITE_HANDLER ( angelkds_cpu_bank_write )
{
	int bankaddress;
	unsigned char *RAM = memory_region(REGION_USER1);

	bankaddress = data & 0x0f;
	cpu_setbank(1,&RAM[bankaddress*0x4000]);
}


/*** Fake Inputs

these make the game a bit easier for testing purposes

*/

#define FAKEINPUTS 0

#if FAKEINPUTS

static READ_HANDLER( angelkds_input_r )
{
	int fake = readinputport(6+offset);

	return ((fake & 0x01) ? fake  : readinputport(4+offset));
}

#else

static READ_HANDLER( angelkds_input_r )
{
	return readinputport(4+offset);
}

#endif

/*** Memory Structures

Angel Kids:
I would have expected f003 to be the scroll register for the bottom
part of the screen, in the attract mode this works fine, but in the
game it doesn't, so maybe it wasn't really hooked up and instead
only one of the register (f001) is used for both part?

* update, it is correct, the screen is meant to split in two when
 the kid goes what would be offscreen, just looked kinda odd

Interesting note, each Bank in the 0x8000 - 0xbfff appears to
contain a level.



*/

static MEMORY_READ_START( readmem_main )
	{ 0x0000, 0x7fff, MRA_ROM },
	{ 0x8000, 0xbfff, MRA_BANK1 },
	{ 0xc000, 0xdfff, MRA_RAM },
	{ 0xe000, 0xefff, MRA_RAM },
MEMORY_END

static MEMORY_WRITE_START( writemem_main )
	{ 0x0000, 0x7fff, MWA_ROM },
	{ 0xc000, 0xdfff, MWA_RAM },
	{ 0xe000, 0xe3ff, angelkds_bgtopvideoram_w, &angelkds_bgtopvideoram }, /* Top Half of Screen */
	{ 0xe400, 0xe7ff, angelkds_bgbotvideoram_w, &angelkds_bgbotvideoram }, /* Bottom Half of Screen */
	{ 0xe800, 0xebff, angelkds_txvideoram_w, &angelkds_txvideoram },
	{ 0xec00, 0xecff, MWA_RAM, &spriteram },
	{ 0xed00, 0xeeff, angelkds_paletteram_w, &paletteram },
	{ 0xf000, 0xf000, angelkds_bgtopbank_write },
	{ 0xf001, 0xf001, angelkds_bgtopscroll_write },
	{ 0xf002, 0xf002, angelkds_bgbotbank_write },
	{ 0xf003, 0xf003, angelkds_bgbotscroll_write },
	{ 0xf004, 0xf004, angelkds_txbank_write },
	{ 0xf005, 0xf005, angelkds_layer_ctrl_write },
MEMORY_END

static PORT_READ_START( readport_main )
	{ 0x40, 0x40, input_port_0_r },	/* "Coinage" Dip Switches */
	{ 0x41, 0x41, input_port_1_r },	/* Other Dip Switches */
	{ 0x42, 0x42, input_port_2_r },	/* Players inputs (not needed ?) */
	{ 0x80, 0x80, input_port_3_r },	/* System inputs */
	{ 0x81, 0x82, angelkds_input_r },	/* Players inputs */
PORT_END

static PORT_WRITE_START( writeport_main )
	{ 0x00, 0x00, MWA_NOP }, // 00 on start-up, not again
	{ 0x42, 0x42, angelkds_cpu_bank_write },
	{ 0x43, 0x43, MWA_NOP }, // 9a on start-up, not again
	{ 0x83, 0x83, MWA_NOP }, // 9b on start-up, not again
	{ 0xc0, 0xc3, angelkds_sound_w }, // 02 various points
PORT_END

/* sub cpu */

static MEMORY_READ_START( readmem_sub )
	{ 0x0000, 0x7fff, MRA_ROM },
	{ 0x8000, 0x87ff, MRA_RAM },
	{ 0xaaa9, 0xaaa9, MRA_NOP },
	{ 0xaaab, 0xaaab, MRA_NOP },
	{ 0xaaac, 0xaaac, MRA_NOP },
MEMORY_END

static MEMORY_WRITE_START( writemem_sub )
	{ 0x0000, 0x7fff, MWA_ROM },
	{ 0x8000, 0x87ff, MWA_RAM },
MEMORY_END

static PORT_READ_START( readport_sub )
	{ 0x00, 0x00, YM2203_status_port_0_r },
	{ 0x40, 0x40, YM2203_status_port_1_r },
	{ 0x80, 0x83, angelkds_sound_r },
PORT_END

static PORT_WRITE_START( writeport_sub)
	{ 0x00, 0x00, YM2203_control_port_0_w },
	{ 0x01, 0x01, YM2203_write_port_0_w },
	{ 0x40, 0x40, YM2203_control_port_1_w },
	{ 0x41, 0x41, YM2203_write_port_1_w },
	{ 0x80, 0x80, MWA_NOP },
	{ 0x81, 0x81, MWA_NOP },
	{ 0x82, 0x82, MWA_NOP },
	{ 0x83, 0x83, MWA_NOP },
PORT_END


/* Input Ports */

/* Here is the way to access to the different parts of the "test mode" :

     - sound  : set "Coin A" Dip Switch to "Free Play" and "Coin B" Dip Switch to "Free Play"
     - paddle : set "Coin A" Dip Switch to "3C_1C" and "Coin B" Dip Switch to "Free Play"

If use different settings, you'll only see a black screen.

I haven't found how to exit the tests. The only way seems to reset the game.
*/

#define ANGELDSK_PLAYERS_INPUT( player ) \
	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY | player ) \
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY | player ) \
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY | player ) \
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | player ) \
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP     | IPF_8WAY | player ) \
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY | player ) \
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY | player ) \
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY | player )

#define ANGELDSK_FAKE_PLAYERS_INPUT( player ) \
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | player )	/* To enter initials */ \
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN )		/* Unused */ \
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | player ) \
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | player ) \
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | player ) \
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | player ) \
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | player )	/* To shorten the rope and */ \
										/* move right in hiscores table */


INPUT_PORTS_START( angelkds )
	PORT_START		/* inport $40 */
	PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( Coin_A ) )
	PORT_DIPSETTING(	0x70, DEF_STR( 4C_1C ) )
	PORT_DIPSETTING(	0x80, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(	0x90, DEF_STR( 2C_1C ) )
//	PORT_DIPSETTING(	0x60, DEF_STR( 2C_1C ) )
//	PORT_DIPSETTING(	0x50, DEF_STR( 2C_1C ) )
//	PORT_DIPSETTING(	0x40, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(	0xf0, DEF_STR( 1C_1C ) )
//	PORT_DIPSETTING(	0x30, DEF_STR( 1C_1C ) )
//	PORT_DIPSETTING(	0x20, DEF_STR( 1C_1C ) )
//	PORT_DIPSETTING(	0x10, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(	0xe0, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(	0xd0, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(	0xc0, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(	0xb0, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(	0xa0, DEF_STR( 1C_6C ) )
	PORT_DIPSETTING(	0x00, DEF_STR( Free_Play ) )	// needed to enter "test mode" (see above)

	PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( Coin_B ) )
	PORT_DIPSETTING(	0x07, DEF_STR( 4C_1C ) )
	PORT_DIPSETTING(	0x08, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(	0x09, DEF_STR( 2C_1C ) )
//	PORT_DIPSETTING(	0x06, DEF_STR( 2C_1C ) )
//	PORT_DIPSETTING(	0x05, DEF_STR( 2C_1C ) )
//	PORT_DIPSETTING(	0x04, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(	0x0f, DEF_STR( 1C_1C ) )
//	PORT_DIPSETTING(	0x03, DEF_STR( 1C_1C ) )
//	PORT_DIPSETTING(	0x02, DEF_STR( 1C_1C ) )
//	PORT_DIPSETTING(	0x01, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(	0x0e, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(	0x0d, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(	0x0c, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(	0x0b, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(	0x0a, DEF_STR( 1C_6C ) )
	PORT_DIPSETTING(	0x00, DEF_STR( Free_Play ) )

	PORT_START		/* inport $41 */
	PORT_DIPNAME( 0x01, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(	0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(	0x01, DEF_STR( Cocktail ) )
	PORT_DIPNAME( 0x02, 0x00, "Hi Score" )
	PORT_DIPSETTING(	0x00, "3 Characters" )
	PORT_DIPSETTING(	0x02, "10 Characters" )
	PORT_DIPNAME( 0x0c, 0x08, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(	0x0c, "20k, 50k, 100k, 200k and 500k" )
	PORT_DIPSETTING(	0x08, "50k, 100k, 200k and 500k" )
	PORT_DIPSETTING(	0x04, "100k, 200k and 500k" )
	PORT_DIPSETTING(	0x00, "None" )
	PORT_DIPNAME( 0x30, 0x30, DEF_STR( Lives ) )
	PORT_DIPSETTING(	0x30, "3" )
	PORT_DIPSETTING(	0x20, "4" )
	PORT_DIPSETTING(	0x10, "5" )
	PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "99", IP_KEY_NONE, IP_JOY_NONE )
	PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( Unknown ) )	// Stored at 0xc023
	PORT_DIPSETTING(	0xc0, "4" )
	PORT_DIPSETTING(	0x40, "5" )
	PORT_DIPSETTING(	0x80, "6" )
	PORT_DIPSETTING(	0x00, "7" )

	PORT_START		/* inport $42 */
	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN )	// duplicated IPT_JOYSTICK_LEFTRIGHT  | IPF_8WAY
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )	// duplicated IPT_JOYSTICK_LEFTRIGHT  | IPF_8WAY | IPF_COCKTAIL
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )

	PORT_START		/* inport $80 */
	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 )
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 )
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 )
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 )
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_SERVICE( 0x80, IP_ACTIVE_LOW )

	PORT_START		/* inport $81 */
	ANGELDSK_PLAYERS_INPUT( IPF_PLAYER1 )

	PORT_START		/* inport $82 */
	ANGELDSK_PLAYERS_INPUT( IPF_PLAYER2 )

#if FAKEINPUTS

	/* Fake inputs to allow to play the game with 1 joystick instead of 2 */
	PORT_START
	PORT_DIPNAME( 0x01, 0x00, "FAKE (for debug) Joysticks (Player 1)" )
	PORT_DIPSETTING(	0x01, "1" )
	PORT_DIPSETTING(	0x00, "2" )
	ANGELDSK_FAKE_PLAYERS_INPUT( IPF_PLAYER1 )

	PORT_START
	PORT_DIPNAME( 0x01, 0x00, "FAKE (for debug) Joysticks (Player 2)" )
	PORT_DIPSETTING(	0x01, "1" )
	PORT_DIPSETTING(	0x00, "2" )
	ANGELDSK_FAKE_PLAYERS_INPUT( IPF_PLAYER2 )

#endif

INPUT_PORTS_END

/*** Sound Hardware

todo: verify / correct things
seems a bit strange are all the addresses really
sound related ?

*/

static UINT8 angelkds_sound[4];

static WRITE_HANDLER( angelkds_sound_w )
{
	angelkds_sound[offset]=data;
}

static READ_HANDLER( angelkds_sound_r )
{
	return angelkds_sound[offset];
}

static void irqhandler(int irq)
{
	cpu_set_irq_line(1,0,irq ? ASSERT_LINE : CLEAR_LINE);
}

static struct YM2203interface ym2203_interface =
{
	2,                      /* 2 chips */
	4000000,        /* 4 MHz ? */
	{ YM2203_VOL(15,25), YM2203_VOL(15,25) },
	{ 0 },
	{ 0 },
	{ 0 },
	{ 0 },
	{ irqhandler }
};

/*** Graphics Decoding

all the 8x8 tiles are in one format, the 16x16 sprites in another

*/

static struct GfxLayout angelkds_charlayout =
{
	8,8,
	RGN_FRAC(1,1),
	4,
	{ 0,1,2,3 },
	{ 0, 4, 8, 12, 16, 20, 24, 28 },
	{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
	8*32
};


static struct GfxLayout angelkds_spritelayout =
{
	16,16,
	RGN_FRAC(1,2),
	4,
	{ 0,4,	RGN_FRAC(1,2)+0,	RGN_FRAC(1,2)+4 },
	{ 0, 1, 2, 3, 8, 9, 10, 11, 16,17,18,19, 24,25,26,27,  },
	{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32, 8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32  },
	16*32
};

static struct GfxDecodeInfo gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0, &angelkds_charlayout,   0x30, 1  },
	{ REGION_GFX3, 0, &angelkds_charlayout,   0, 16 },
	{ REGION_GFX2, 0, &angelkds_spritelayout, 0x20, 0x0d },
	{ -1 } /* end of array */
};

/*** Machine Driver

 2 x z80 (one for game, one for sound)
 2 x YM2203 (for sound)

 all fairly straightforward

*/

static MACHINE_DRIVER_START( angelkds )
	MDRV_CPU_ADD(Z80, 8000000) /* 8MHz? 6 seems too slow? */
	MDRV_CPU_MEMORY(readmem_main,writemem_main)
	MDRV_CPU_PORTS(readport_main,writeport_main)
	MDRV_CPU_VBLANK_INT(irq0_line_hold,1)

	MDRV_CPU_ADD(Z80, 4000000) /* 8 MHz? */
	MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
	MDRV_CPU_MEMORY(readmem_sub,writemem_sub)
	MDRV_CPU_PORTS(readport_sub,writeport_sub)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	MDRV_INTERLEAVE(100)

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
	MDRV_SCREEN_SIZE(32*8, 32*8)
	MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
	MDRV_GFXDECODE(gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(0x100)

	MDRV_VIDEO_START(angelkds)
	MDRV_VIDEO_UPDATE(angelkds)

	MDRV_SOUND_ADD(YM2203, ym2203_interface)
MACHINE_DRIVER_END

/*** Rom Loading

 REGION_CPU1 for the main code
 REGION_USER1 for the banked data
 REGION_CPU2 for the sound cpu code
 REGION_GFX1 for the 8x8 Txt Layer Tiles
 REGION_GFX2 for the 16x16 Sprites
 REGION_GFX3 for the 8x8 Bg Layer Tiles
 REGION_PROMS for the Prom (same between games)

*/

ROM_START( angelkds )
	/* Nasco X090-PC-A  (Sega 837-6600) */
	ROM_REGION( 0x10000, REGION_CPU1, 0 )
	ROM_LOAD( "11428.c10",    0x00000, 0x08000, 0x90daacd2 )

	ROM_REGION( 0x20000, REGION_USER1, 0 ) /* Banked Code */
	ROM_LOAD( "11424.c1",     0x00000, 0x08000, 0xb55997f6 )
	ROM_LOAD( "11425.c2",     0x08000, 0x08000, 0x299359de )
	ROM_LOAD( "11426.c3",     0x10000, 0x08000, 0x5fad8bd3 )
	ROM_LOAD( "11427.c4",     0x18000, 0x08000, 0xef920c74 )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )
	ROM_LOAD( "11429.d4",     0x00000, 0x08000, 0x0ca50a66 )

	/* Nasco X090-PC-B */
	ROM_REGION( 0x08000, REGION_GFX1, 0 )
	ROM_LOAD( "11446",        0x00000, 0x08000, 0x45052470 )

	ROM_REGION( 0x10000, REGION_GFX2, 0 )
	ROM_LOAD( "11447.f7",     0x08000, 0x08000, 0xb3afc5b3 )
	ROM_LOAD( "11448.h7",     0x00000, 0x08000, 0x05dab626 )

	ROM_REGION( 0x80000, REGION_GFX3, 0 )
	ROM_LOAD( "11437",        0x00000, 0x08000, 0xa520b628 )
	ROM_LOAD( "11436",        0x08000, 0x08000, 0x469ab216 )
	ROM_LOAD( "11435",        0x10000, 0x08000, 0xb0f8c245 )
	ROM_LOAD( "11434",        0x18000, 0x08000, 0xcbde81f5 )
	ROM_LOAD( "11433",        0x20000, 0x08000, 0xb63fa414 )
	ROM_LOAD( "11432",        0x28000, 0x08000, 0x00dc747b )
	ROM_LOAD( "11431",        0x30000, 0x08000, 0xac2025af )
	ROM_LOAD( "11430",        0x38000, 0x08000, 0xd640f89e )
	ROM_LOAD( "11445",        0x40000, 0x08000, 0xa520b628 )
	ROM_LOAD( "11444",        0x48000, 0x08000, 0x469ab216 )
	ROM_LOAD( "11443",        0x50000, 0x08000, 0xb0f8c245 )
	ROM_LOAD( "11442",        0x58000, 0x08000, 0xcbde81f5 )
	ROM_LOAD( "11441",        0x60000, 0x08000, 0xb63fa414 )
	ROM_LOAD( "11440",        0x68000, 0x08000, 0x00dc747b )
	ROM_LOAD( "11439",        0x70000, 0x08000, 0xac2025af )
	ROM_LOAD( "11438",        0x78000, 0x08000, 0xd640f89e )

	ROM_REGION( 0x20, REGION_PROMS, 0 )
	ROM_LOAD( "63s081n.u5",	  0x00,    0x20,    0x36b98627 )
ROM_END

ROM_START( spcpostn )
	/* X090-PC-A 171-5383 */
	ROM_REGION( 2*0x10000, REGION_CPU1, 0 ) /* D317-0005 (NEC Z80 Custom) */
	ROM_LOAD( "epr10125.c10", 0x00000, 0x08000, 0xbffd38c6 ) /* encrypted */

	ROM_REGION( 0x28000, REGION_USER1, 0 ) /* Banked Code */
	ROM_LOAD( "epr10120.c1",  0x00000, 0x08000, 0xd6399f99 )
	ROM_LOAD( "epr10121.c2",  0x08000, 0x08000, 0xd4861560 )
	ROM_LOAD( "epr10122.c3",  0x10000, 0x08000, 0x7a1bff1b )
	ROM_LOAD( "epr10123.c4",  0x18000, 0x08000, 0x6aed2925 )
	ROM_LOAD( "epr10124.c5",  0x20000, 0x08000, 0xa1d7ae6b )

	ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* NEC D780C-1 */
	ROM_LOAD( "epr10126.d4",  0x00000, 0x08000, 0xab17f852 )

	/* X090-PC-B 171-5384 */
	ROM_REGION( 0x08000, REGION_GFX1, 0 )
	ROM_LOAD( "epr10133.17",  0x00000, 0x08000, 0x642e6609 )

	ROM_REGION( 0x10000, REGION_GFX2, 0 )
	ROM_LOAD( "epr10134.18",  0x00000, 0x08000, 0xc674ff88 )
	ROM_LOAD( "epr10135.19",  0x08000, 0x08000, 0x0685c4fa )

	ROM_REGION( 0x30000, REGION_GFX3, 0 )
	ROM_LOAD( "epr10127.06",  0x00000, 0x08000, 0xb68fcb36 )
	ROM_LOAD( "epr10128.07",  0x08000, 0x08000, 0xde223817 )
	ROM_LOAD( "epr10129.08",  0x10000, 0x08000, 0xa6f21023 )
	ROM_LOAD( "epr10130.14",  0x18000, 0x08000, 0xb68fcb36 )
	ROM_LOAD( "epr10131.15",  0x20000, 0x08000, 0xde223817 )
	ROM_LOAD( "epr10132.16",  0x28000, 0x08000, 0x2df8b1bd )

	ROM_REGION( 0x20, REGION_PROMS, 0 )
	ROM_LOAD( "63s081n.u5",   0x00,    0x20,    0x36b98627 )
ROM_END


static DRIVER_INIT( spcpostn )	{ spcpostn_decode(); }


GAME( 1988, angelkds, 0, angelkds, angelkds,        0,  ROT90,  "Sega / Nasco?", "Angel Kids (Japan)" ) /* Nasco not displayed but 'Exa Planning' is */
GAMEX(1986, spcpostn, 0, angelkds, angelkds, spcpostn,  ROT90,  "Sega / Nasco", "Space Position (Japan)", GAME_NOT_WORKING ) /* encrypted */
