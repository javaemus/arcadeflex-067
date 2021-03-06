/*** DRIVER INFO **************************************************************

Grand Striker, V Goal Soccer, World Cup '94
driver by Farfetch and David Haywood

Grand Striker (c)199?  Human
V Goal Soccer (c)199?  Tecmo (2 sets)
World Cup '94 (c)1994? Tecmo

******************************************************************************

	Hardware notes

Both games seem to be similar hardware, V Goal Soccer doesn't work.
the hardware is also quite similar to several other Video System games.

In particular, the sound hardware is identical to aerofgt (including the
memory mapping of the Z80, it's really just a romswap), and the sprite chip
(Fujitsu CG10103) is the same used in several Video System games (see the notes
in the vidhrdw).

Grand Striker has an IRQ2 which is probably network related.

DSWs need correctly mapping, they're just commented for the moment.

TODO:
Finish hooking up the inputs
Tilemap scrolling/rotation/zooming or whatever effect it needs
Priorities are wrong. I suspect they need sprite orthogonality
Missing mixer registers (mainly layer enable/disable)
Merge with other Video System games ?

******************************************************************************/

#include "driver.h"
#include "gstriker.h"

/*** README INFO **************************************************************

*** ROMSET: gstriker

Grand Striker
Human 1993

This game runs on Video Systems h/w.

PCB Nos: TW-107 94V-0
         LD01-A
CPU    : MC68000P10
SND    : Zilog Z0840006PSC (Z80), YM2610, YM3016-D
OSC    : 14.31818 MHz, 20.000MHz
XTAL   : 8.000MHz
DIPs   : 8 position (x2)
RAM    : 6264 (x12), 62256 (x4), CY7C195 (x1), 6116 (x3)
PALs   : 16L8 labelled S204A (near Z80)
         16L8 labelled S205A (near VS920A)
         16L8 labelled S201A \
                       S202A  |
                       S203A /  (Near 68000)


Other  :

MC68B50P (located next to 68000)
Fujitsu MB3773 (8 pin DIP)
Fujitsu MB605E53U (160 pin PQFP, located near U2 & U4) (screen tilemap)
Fujitsu CG10103 145 (160 pin PQFP, located near U25) (sprites)
VS9209 (located near DIPs)
VS920A (located near U79) (score tilemap)

ROMs:
human-1.u58	27C240	 - Main Program
human-2.u79	27C1024  - ? (near VS920A)
human-3.u87	27C010   - Sound Program
human-4.u6      27C240   - ?, maybe region specific gfx
scrgs101.u25    23C16000 - GFX
scrgs102.u24    23C16000 - GFX
scrgs103.u23    23C16000 - GFX
scrgs104.u22    23C16000 - GFX
scrgs105.u2     23C16000 - GFX   \
scrgs105.u4     23C16000 - GFX   / note, contents of these are identical.
scrgs106.u93	232001	 - Sounds
scrgs107.u99	23c8000  - Sounds

*** ROMSET: vgoalsoc

V Goal Soccer
Tecmo 199x?

This game runs on Video Systems h/w.

PCB No: VSIS-20V3, Tecmo No. VG63
CPU: MC68HC000P16
SND: Zilog Z0840006PSC (Z80), YM2610, YM3016-D
OSC: 14.31818 MHz (Near Z80), 32.000MHz (Near 68000), 20.000MHz (Near MCU)
DIPs: 8 position (x2)
RAM: LH5168 (x12), KM62256 (x4), CY7C195 (x1), LH5116 (x3)
PALs: 16L8 labelled S2032A (near Z80)
      16L8 labelled S2036A (near U104)
 4 x  16L8 labelled S2031A \
                    S2033A  |
                    S2034A  |  (Near 68000)
                    S2035A /


Other:

Hitachi H8/325  HD6473258P10 (Micro-controller, located next to 68000)
Fujitsu MB3773 (8 pin DIP)
Fujitsu MB605E53U (160 pin PQFP, located near U17 & U20)
Fujitsu CG10103 145 (160 pin PQFP, located next to VS9210)
VS9210 (located near U11 & U12)
VS9209 (located near DIPs)
VS920A (located near U48) (score tilemap)

ROMs:
c16_u37.u37	27C4002	 - Main Program
c16_u48.u48	27C1024  - ?
c16_u65.u65	27C2001  - Sound Program
c13_u86.u86	HN62302	 - Sounds
c13_u104.104	HN624116 - Sounds
c13_u20.u20     HN62418  - GFX   \
c13_u17.u17     HN62418  - GFX   / note, contents of these are identical.
c13_u11.u11     HN624116 - GFX
c13_u12.u12     HN624116 - GFX

              Screenshots and board pics are available here...
              http://unemulated.emuunlim.com/shopraid/index.html
              More info reqd? Email me...
              theguru@emuunlim.com

*** ROMSET: vgoalsca

Tecmo V Goal Soccer �1994? Tecmo

CPU: 68000, Z80
Sound: YM2610
Other: VS9209, VS920A, VS9210, VS920B, HD6473258P10, CG10103, CY7C195,

X1: 20
X2: 32
X3: 14.31818

Note: Same hardware as Tecmo World Cup '94, minus one VS9209 chip.

*** ROMSET: worldc94

World Cup 94
Tecmo 1994

VSIS-20V3

   6264
   6264            H8/320         SW    SW
   6264            20MHz  13  6264
   6264   ?        68000-16   6264
   6264
   6264    ?
   6264
   6264
   6264
   6264

   U11         6264
   U12         6264
   U13
   U14         11


   U17-20                U104
           6264 6264
                      U86
   U17-20    ?                 YM2610
                   12   Z80


******************************************************************************/


data16_t *gs_videoram3;
data16_t *gs_mixer_regs;

/* in vidhrdw */
WRITE16_HANDLER( gsx_videoram3_w );
VIDEO_UPDATE( gstriker );
VIDEO_START( gstriker );


/*** MISC READ / WRITE HANDLERS **********************************************/

static READ16_HANDLER(dmmy_8f)
{
	static int ret = 0xFFFF;
	ret = ~ret;
	return ret;
}

/*** SOUND RELATED ***********************************************************/

static int pending_command;

static WRITE16_HANDLER( sound_command_w )
{
	if (ACCESSING_LSB)
	{
		pending_command = 1;
		soundlatch_w(offset,data & 0xff);
		cpu_set_irq_line(1, IRQ_LINE_NMI, PULSE_LINE);
	}
}

#if 0
static READ16_HANDLER( pending_command_r )
{
	return pending_command;
}
#endif

static WRITE_HANDLER( gs_sh_pending_command_clear_w )
{
	pending_command = 0;
}

static WRITE_HANDLER( gs_sh_bankswitch_w )
{
	unsigned char *RAM = memory_region(REGION_CPU2);
	int bankaddress;

	bankaddress = 0x10000 + (data & 0x03) * 0x8000;
	cpu_setbank(1,&RAM[bankaddress]);
}

/*** GFX DECODE **************************************************************/

static struct GfxLayout gs_8x8x4_layout =
{
	8,8,
	RGN_FRAC(1,1),
	4,
	{ 0,1,2,3 },
	{ 4, 0, 12, 8, 20, 16, 28, 24 },
	{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
	8*32
};


static struct GfxLayout gs_16x16x4_layout =
{
	16,16,
	RGN_FRAC(1,1),
	4,
	{ 0,1,2,3 },
	{ 0, 4, 8, 12, 16, 20, 24, 28,
	32+0,32+4,32+8,32+12,32+16,32+20,32+24,32+28
	},

	{ 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
	 8*64,9*64,10*64,11*64,12*64,13*64,14*64,15*64
	},
	16*64
};

static struct GfxDecodeInfo gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0, &gs_8x8x4_layout,     0, 256 },
	{ REGION_GFX2, 0, &gs_16x16x4_layout,   0, 256 },
	{ REGION_GFX3, 0, &gs_16x16x4_layout,   0, 256 },

	{ -1 },
};

/*** MORE SOUND RELATED ******************************************************/

static void gs_ym2610_irq(int irq)
{
	if (irq)
		cpu_set_irq_line(1, 0, ASSERT_LINE);
	else
		cpu_set_irq_line(1, 0, CLEAR_LINE);
}

static struct YM2610interface ym2610_interface =
{
	1,
	8000000,	/* 8 MHz */
	{ 25 },
	{ 0 },
	{ 0 },
	{ 0 },
	{ 0 },
	{ gs_ym2610_irq },
	{ REGION_SOUND1 },
	{ REGION_SOUND2 },
	{ YM3012_VOL(100,MIXER_PAN_LEFT,100,MIXER_PAN_RIGHT) }
};

/*** MEMORY LAYOUTS **********************************************************/

static MEMORY_READ16_START( readmem )
	{ 0x000000, 0x0fffff, MRA16_ROM },
	{ 0x100000, 0x103fff, MRA16_RAM },
	{ 0x140000, 0x141fff, MRA16_RAM },
	{ 0x180000, 0x181fff, MRA16_RAM },
	{ 0x1c0000, 0x1c0fff, MRA16_RAM },
	{ 0xffc000, 0xffffff, MRA16_RAM },

//	{ 0x200060, 0x200061, dmmy },
	{ 0x200080, 0x200081, input_port_1_word_r },
	{ 0x200082, 0x200083, input_port_2_word_r },
	{ 0x200084, 0x200085, input_port_0_word_r },
	{ 0x200086, 0x200087, input_port_3_word_r },
	{ 0x200088, 0x200089, input_port_4_word_r },
	{ 0x20008e, 0x20008f, dmmy_8f },
MEMORY_END

static MEMORY_WRITE16_START( writemem )
	{ 0x000000, 0x0fffff, MWA16_ROM },
	{ 0x100000, 0x101fff, MB60553_0_vram_w, &MB60553_0_vram },
	{ 0x102000, 0x103fff, gsx_videoram3_w, &gs_videoram3 },
	{ 0x140000, 0x141fff, MWA16_RAM, &CG10103_0_vram },
	{ 0x180000, 0x181fff, VS920A_0_vram_w, &VS920A_0_vram },
	{ 0x1c0000, 0x1c0fff, paletteram16_xRRRRRGGGGGBBBBB_word_w, &paletteram16 },
	{ 0x200000, 0x20000f, MB60553_0_regs_w },
	{ 0x200040, 0x20005f, MWA16_RAM, &gs_mixer_regs },
	{ 0x2000a0, 0x2000a1, sound_command_w },
	{ 0xffc000, 0xffffff, MWA16_RAM },
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
	{ 0x00, 0x00, YM2610_status_port_0_A_r },
	{ 0x02, 0x02, YM2610_status_port_0_B_r },
	{ 0x0c, 0x0c, soundlatch_r },
PORT_END

static PORT_WRITE_START( sound_writeport )
	{ 0x00, 0x00, YM2610_control_port_0_A_w },
	{ 0x01, 0x01, YM2610_data_port_0_A_w },
	{ 0x02, 0x02, YM2610_control_port_0_B_w },
	{ 0x03, 0x03, YM2610_data_port_0_B_w },
	{ 0x04, 0x04, gs_sh_bankswitch_w },
	{ 0x08, 0x08, gs_sh_pending_command_clear_w },
PORT_END

/*** INPUT PORTS *************************************************************/

INPUT_PORTS_START( gstriker )
	PORT_START
	PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_COIN1 )
	PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_COIN2 )
	PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_START1 )
	PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_START2 )
	PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_SERVICE2 )				// "Test"
	PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_TILT )
	PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_SERVICE1 )
	PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN) // vbl?
	PORT_BIT( 0xff00, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* probably unused */

	PORT_START      /* IN1 */
	PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 )
	PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 )
	PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 )
	PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER1 )	// "Spare"
	PORT_BIT( 0xff00, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* probably unused */

	PORT_START
	PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 )
	PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 )
	PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 )
	PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER2 )	// "Spare"
	PORT_BIT( 0xff00, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* probably unused */

	PORT_START
	PORT_DIPNAME( 0x0003, 0x0003, DEF_STR( Coin_A ) )
	PORT_DIPSETTING(      0x0001, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(      0x0002, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(      0x0003, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( 1C_2C ) )
	PORT_DIPNAME( 0x000c, 0x000c, DEF_STR( Coin_B ) )
	PORT_DIPSETTING(      0x0004, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(      0x0008, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(      0x000c, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( 1C_2C ) )
	PORT_DIPNAME( 0x0010, 0x0000, "2 Players VS CPU Game" )		// "Cooperation Coin"
	PORT_DIPSETTING(      0x0010, "1 Credit" )
	PORT_DIPSETTING(      0x0000, "2 Credits" )
	PORT_DIPNAME( 0x0020, 0x0000, "Player VS Player Game" )		// "Competitive Coin"
	PORT_DIPSETTING(      0x0020, "1 Credit" )
	PORT_DIPSETTING(      0x0000, "2 Credits" )
	PORT_DIPNAME( 0x0040, 0x0040, "New Challenger" )			/* unknown purpose */
	PORT_DIPSETTING(      0x0040, DEF_STR( No ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( Yes ) )
	PORT_DIPNAME( 0x0080, 0x0080, "Maximum Players" )			// "Cabinet Type"
	PORT_DIPSETTING(      0x0000, "1" )
	PORT_DIPSETTING(      0x0080, "2" )
	PORT_BIT( 0xff00, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* probably unused */

	PORT_START
	PORT_DIPNAME( 0x0001, 0x0001, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(      0x0001, "Normal" )
	PORT_DIPSETTING(      0x0000, "Hard" )
	PORT_DIPNAME( 0x0006, 0x0006, "Player(s) VS CPU Time" )		// "Tournament  Time"
	PORT_DIPSETTING(      0x0006, "1:30" )
	PORT_DIPSETTING(      0x0004, "2:00" )
	PORT_DIPSETTING(      0x0002, "3:00" )
	PORT_DIPSETTING(      0x0000, "4:00" )
	PORT_DIPNAME( 0x0018, 0x0018, "Player VS Player Time" )		// "Competitive Time"
	PORT_DIPSETTING(      0x0018, "2:00" )
	PORT_DIPSETTING(      0x0010, "3:00" )
	PORT_DIPSETTING(      0x0008, "4:00" )
	PORT_DIPSETTING(      0x0000, "5:00" )
	PORT_DIPNAME( 0x0020, 0x0000, DEF_STR( Demo_Sounds ) )		// "Demo Sound"
	PORT_DIPSETTING(      0x0020, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0040, 0x0040, "Communication Mode" )			// "Master/Slave"
	PORT_DIPSETTING(      0x0040, "Master" )
	PORT_DIPSETTING(      0x0000, "Slave" )
	PORT_SERVICE( 0x0080, IP_ACTIVE_LOW )					// "Self Test Mode"
	PORT_BIT( 0xff00, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* probably unused */
INPUT_PORTS_END

/*** MACHINE DRIVER **********************************************************/

static MACHINE_DRIVER_START( gstriker )
	MDRV_CPU_ADD(M68000, 10000000)
	MDRV_CPU_MEMORY(readmem,writemem)
	MDRV_CPU_VBLANK_INT(irq1_line_hold,1)

	MDRV_CPU_ADD(Z80,8000000/2)
	MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 4 MHz ??? */
	MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	MDRV_CPU_PORTS(sound_readport,sound_writeport)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(5000) /* hand-tuned, it needs a bit */

	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_AFTER_VBLANK)
	MDRV_SCREEN_SIZE(64*8, 64*8)
	MDRV_VISIBLE_AREA(0*8, 40*8-1, 0*8, 29*8-1)
	MDRV_GFXDECODE(gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(0x800)

	MDRV_VIDEO_START(gstriker)
	MDRV_VIDEO_UPDATE(gstriker)

	MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
	MDRV_SOUND_ADD(YM2610, ym2610_interface)
MACHINE_DRIVER_END

/*** ROM LOADING *************************************************************/

ROM_START( gstriker )
	ROM_REGION( 0x80000, REGION_CPU1, 0 )
	ROM_LOAD16_WORD_SWAP( "human-1.u58",  0x00000, 0x80000, 0x45cf4857 )

	ROM_REGION( 0x40000, REGION_CPU2, 0 )
	ROM_LOAD( "human-3.u87",  0x00000, 0x20000, 0x2f28c01e )
	ROM_RELOAD(               0x10000, 0x20000 )

	ROM_REGION( 0x20000, REGION_GFX1, 0 ) // score tilemap
	ROM_LOAD( "human-2.u79",  0x00000, 0x20000, 0xa981993b )

	ROM_REGION( 0x200000, REGION_GFX2, 0 ) // scroll tilemap
	ROM_LOAD( "scrgs105.u2",  0x00000, 0x200000, 0xd584b568 )
	ROM_LOAD( "scrgs105.u4",  0x00000, 0x200000, 0xd584b568 ) // same content, dif pos on board

	ROM_REGION( 0x1000000, REGION_GFX3, 0 )
	ROM_LOAD( "scrgs101.u25", 0x000000, 0x200000, 0xbecaea24 )
	ROM_LOAD( "scrgs102.u24", 0x200000, 0x200000, 0x0dae7aba )
	ROM_LOAD( "scrgs103.u23", 0x400000, 0x200000, 0x3448fe92 )
	ROM_LOAD( "scrgs104.u22", 0x600000, 0x200000, 0x0ac33e5a )
	ROM_LOAD( "human-4.u6",   0xf80000, 0x080000, 0xa990f9bb )

	ROM_REGION( 0x40000, REGION_SOUND1, 0 )
	ROM_LOAD( "scrgs106.u93", 0x00000, 0x040000, 0x93c9868c )

	ROM_REGION( 0x100000, REGION_SOUND2, 0 )
	ROM_LOAD( "scrgs107.u99", 0x00000, 0x100000, 0xecc0a01b )
ROM_END

ROM_START( vgoalsoc )
	ROM_REGION( 0x80000, REGION_CPU1, 0 )
	ROM_LOAD16_WORD_SWAP( "c16_u37.u37",  0x00000, 0x80000, 0x18c05440 )

	ROM_REGION( 0x40000, REGION_CPU2, 0 )
	ROM_LOAD( "c16_u65.u65",  0x000000, 0x040000, 0x2f7bf23c )

	ROM_REGION( 0x20000, REGION_GFX1, 0 ) // score tilemap
	ROM_LOAD( "c16_u48.u48",  0x000000, 0x020000, 0xca059e7f )

	ROM_REGION( 0x100000, REGION_GFX2, 0 ) // screen tilemap
	ROM_LOAD( "c13_u20.u20",  0x000000, 0x100000, 0xbc6e07e8 )
	ROM_LOAD( "c13_u17.u17",  0x000000, 0x100000, 0xbc6e07e8 ) // same content, dif pos on board

	ROM_REGION( 0x400000, REGION_GFX3, 0 )
	ROM_LOAD( "c13_u11.u11",  0x000000, 0x200000, 0x76d09f27 )
	ROM_LOAD( "c13_u12.u12",  0x200000, 0x200000, 0xa3874419 )

	ROM_REGION( 0x40000, REGION_SOUND1, 0 )
	ROM_LOAD( "c13_u86.u86",  0x000000, 0x040000, 0x4b76a162 )

	ROM_REGION( 0x200000, REGION_SOUND2, 0 )
	ROM_LOAD( "c13_u104.104", 0x000000, 0x200000, 0x8437b6f8 )
ROM_END

ROM_START( vgoalsca )
	ROM_REGION( 0x80000, REGION_CPU1, 0 )
	ROM_LOAD16_WORD_SWAP( "vgoalc16.u37", 0x00000, 0x80000, 0x775ef300 )

	ROM_REGION( 0x40000, REGION_CPU2, 0 )
	ROM_LOAD( "c16_u65.u65",  0x000000, 0x040000, 0x2f7bf23c )

	ROM_REGION( 0x20000, REGION_GFX1, 0 ) // fixed tile
	ROM_LOAD( "c16_u48.u48",  0x000000, 0x020000, 0xca059e7f )

	ROM_REGION( 0x200000, REGION_GFX2, 0 ) // scroll tile
	ROM_LOAD( "vgoalc16.u20", 0x000000, 0x200000, 0x2b211fb2 )
	ROM_LOAD( "vgoalc16.u17", 0x000000, 0x200000, 0x2b211fb2 ) // same content, dif pos on board

	ROM_REGION( 0x400000, REGION_GFX3, 0 )
	ROM_LOAD( "vgoalc16.u11", 0x000000, 0x200000, 0x5bc3146c )
	ROM_LOAD( "c13_u12.u12",  0x200000, 0x200000, 0xa3874419 )

	ROM_REGION( 0x40000, REGION_SOUND1, 0 )
	ROM_LOAD( "c13_u86.u86",  0x000000, 0x040000, 0x4b76a162 )

	ROM_REGION( 0x100000, REGION_SOUND2, 0 )
	ROM_LOAD( "vgoalc16.104", 0x000000, 0x100000, 0x6fb06e1b )
ROM_END

ROM_START( worldc94 )
	ROM_REGION( 0x80000, REGION_CPU1, 0 )
	ROM_LOAD16_WORD_SWAP( "13",           0x00000, 0x80000, 0x42adb463 )

	ROM_REGION( 0x40000, REGION_CPU2, 0 )
	ROM_LOAD( "12",           0x000000, 0x040000, 0xf316e7fc )

	ROM_REGION( 0x20000, REGION_GFX1, 0 ) // fixed tile
	ROM_LOAD( "11",           0x000000, 0x020000, 0x37d6dcb6 )

	ROM_REGION( 0x200000, REGION_GFX2, 0 ) // scroll tile
	ROM_LOAD( "u17",          0x000000, 0x200000, 0xa5e40a61 )
	ROM_LOAD( "u20",          0x000000, 0x200000, 0xa5e40a61 )

	ROM_REGION( 0x800000, REGION_GFX3, 0 )
	ROM_LOAD( "u11",          0x000000, 0x200000, 0xdd93fd45 )
	ROM_LOAD( "u12",          0x200000, 0x200000, 0x8e3c9bd2 )
	ROM_LOAD( "u13",          0x400000, 0x200000, 0x8db6b3a9 )
	ROM_LOAD( "u14",          0x600000, 0x200000, 0x89739c31 )

	ROM_REGION( 0x40000, REGION_SOUND1, 0 )
	ROM_LOAD( "u86",          0x000000, 0x040000, 0x775f45dc )

	ROM_REGION( 0x100000, REGION_SOUND2, 0 )
	ROM_LOAD( "u104",         0x000000, 0x100000, 0xdf07d0af )
ROM_END

/*** GAME DRIVERS ************************************************************/

GAMEX(1993, gstriker, 0,        gstriker, gstriker, 0,        ROT0, "Human", "Grand Striker", GAME_IMPERFECT_GRAPHICS )

/* Similar, but not identical hardware, appear to be protected by an MCU :-( */
GAMEX(199?, vgoalsoc, 0,        gstriker, gstriker, 0,        ROT0, "Tecmo", "V Goal Soccer", GAME_NOT_WORKING )
GAMEX(199?, vgoalsca, vgoalsoc, gstriker, gstriker, 0,        ROT0, "Tecmo", "V Goal Soccer (alt)", GAME_NOT_WORKING )
GAMEX(199?, worldc94, 0,        gstriker, gstriker, 0,        ROT0, "Tecmo", "World Cup '94", GAME_NOT_WORKING )
