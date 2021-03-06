/* Hit Me driver by the EMUL8, led by Dan Boris

   It doesn't work?  should the timer stuff have changed?

*/

/*

	Hit Me  (c) Ramtek  1976
---------------------------------------

	Memory map

	0000-07ff r    Rom
	0c00-0eff w    Video Ram
	1000-13ff r/w  Scratch Ram


*/

#include "driver.h"

static float timeout_time;
static int timeout_counter;
static const float tock = .0189;
data8_t *hitme_vidram;

WRITE_HANDLER( hitme_vidram_w );
VIDEO_START (hitme);
VIDEO_START (brickyrd);
VIDEO_UPDATE (hitme);
PALETTE_INIT( hitme );

INPUT_PORTS_START( hitme )
	PORT_START
	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 ) /* Start button */
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Random bit generator Based on Hblank */
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 ) /* P1 Stand button */
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 ) /* P1 Hit button */
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 ) /* P1 Bet button */
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (*TO) */

	PORT_START
	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Aux 2 dipswitch - Unused */
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 ) /* P2 Stand button */
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 ) /* P2 Hit button */
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 ) /* P2 Bet button */
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (*TO) */

	PORT_START
	PORT_DIPNAME( 0x01, 0x00, "Extra Hand On Natural" ) /* Aux 1 dipswitch */
	PORT_DIPSETTING(    0x00, DEF_STR ( Off ) )
	PORT_DIPSETTING(    0x01, DEF_STR ( On )  )
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Random bit generator Based on Hblank */
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 ) /* P3 Stand button */
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 ) /* P3 Hit button */
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER3 ) /* P3 Bet button */
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (*TO) */

	PORT_START
	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (TOC1) */
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Aux 2 dipswitch - Unused*/
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 ) /* P4 Stand button */
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 ) /* P4 Hit button */
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER4 ) /* P4 Bet button */
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (*TO) */

	PORT_START
	PORT_DIPNAME( 0x07, 0x07, "Number of Chips" )
	PORT_DIPSETTING(    0x00, "5 Chips" )
	PORT_DIPSETTING(    0x01, "10 Chips" )
	PORT_DIPSETTING(    0x02, "15 Chips" )
	PORT_DIPSETTING(    0x03, "20 Chips" )
	PORT_DIPSETTING(    0x04, "25 Chips" )
	PORT_DIPSETTING(    0x05, "30 Chips" )
	PORT_DIPSETTING(    0x06, "35 Chips" )
	PORT_DIPSETTING(    0x07, "40 Chips" )

	PORT_START
	PORT_DIPNAME( 0x07, 0x00, "Number of Hands" )
	PORT_DIPSETTING(    0x00, "5 Hands" )
	PORT_DIPSETTING(    0x01, "10 Hands" )
	PORT_DIPSETTING(    0x02, "15 Hands" )
	PORT_DIPSETTING(    0x03, "20 Hands" )
	PORT_DIPSETTING(    0x04, "25 Hands" )
	PORT_DIPSETTING(    0x05, "30 Hands" )
	PORT_DIPSETTING(    0x06, "35 Hands" )
	PORT_DIPSETTING(    0x07, "40 Hands" )
INPUT_PORTS_END

INPUT_PORTS_START( brickyrd )
	PORT_START
    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 ) /* Start button */
    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Random bit generator Based on Hblank */
    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN )
    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT )
    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT )
    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_UP )
    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (*TO) */

	PORT_START
    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 )
    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Aux 2 dipswitch - Unused */
   PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER3  )
   PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER3  )
   PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER3  )
   PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER3  )
    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (*TO) */

	PORT_START
    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* ??? */
    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Random bit generator Based on Hblank */
   PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER4  )
   PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER4  )
   PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER4  )
   PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER4  )
    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (*TO) */

	PORT_START
    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (TOC1) */
    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Always high */
    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Aux 2 dipswitch - Unused*/
   PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2  )
   PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2  )
   PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2  )
   PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2  )
    PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ) /* Time out counter (*TO) */

   /* On the flyer it says that barricade has both user adjustable points per
		game, and speed. From experimenting it looks like points per game is the
		same dipswitch as hitme's chips, and speed is hitme's hands. The flyer
      says 1-7 points per games, but it really can go to 8. */

	PORT_START
	PORT_DIPNAME( 0x07, 0x07, "Points Per Game" )
	PORT_DIPSETTING(    0x00, "1 Point" )
	PORT_DIPSETTING(    0x01, "2 Points" )
	PORT_DIPSETTING(    0x02, "3 Points" )
	PORT_DIPSETTING(    0x03, "4 Points" )
	PORT_DIPSETTING(    0x04, "5 Points" )
	PORT_DIPSETTING(    0x05, "6 Points" )
	PORT_DIPSETTING(    0x06, "7 Points" )
	PORT_DIPSETTING(    0x07, "8 Points" )

	/* These are like lives, you lose a point if you crash. The last person with
		points wins the game. */

	PORT_START
	PORT_DIPNAME( 0x07, 0x00, "Game Speed" )
	PORT_DIPSETTING(    0x00, "Fast Fast" )
	PORT_DIPSETTING(    0x01, "7" )
	PORT_DIPSETTING(    0x02, "6" )
	PORT_DIPSETTING(    0x03, "5" )
	PORT_DIPSETTING(    0x04, "4" )
	PORT_DIPSETTING(    0x05, "3" )
	PORT_DIPSETTING(    0x06, "2" )
	PORT_DIPSETTING(    0x07, "Slow Slow" )
INPUT_PORTS_END

static struct GfxLayout charlayout =
{
	8,8, /* 8*8 characters */
	64, /* 64 characters */
	1, /* 1 bit per pixel */
	{ 0 },
	{ 0, 1, 2, 3, 4, 5, 6, 7 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
	8*8 /* every char takes 8 consecutive bytes */
};

static struct GfxDecodeInfo gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0, &charlayout,   0, 1  },
	{ -1 } /* end of array */
};

static READ_HANDLER ( hitme_port_0_r )
{
	if ((timer_get_time() - timeout_time) > (timeout_counter * tock))
	{
		return input_port_0_r (offset) - ((rand()%2) << 2) - 0x80;
	}
	else
		return input_port_0_r (offset) - ((rand()%2) << 2);
}

static READ_HANDLER ( hitme_port_1_r )
{
	if ((timer_get_time() - timeout_time) > (timeout_counter * tock))
	{
		return input_port_1_r (offset) - 0x80;
	}
	else
		return input_port_1_r (offset);
}

static READ_HANDLER ( hitme_port_2_r )
{
	if ((timer_get_time() - timeout_time) > (timeout_counter * tock))
	{
		return input_port_2_r (offset) - ((rand()%2) << 2) - 0x80;
	}
	else
		return input_port_2_r (offset) - ((rand()%2) << 2);
}

static READ_HANDLER ( hitme_port_3_r )
{
	if ((timer_get_time() - timeout_time) > (timeout_counter * tock))
	{
		return input_port_3_r (offset) - 0x80;
	}
	else
		return input_port_3_r (offset);
}

static WRITE_HANDLER ( output_port_0_w )
{
	timeout_counter = (data);
	timeout_time = timer_get_time();
}

#if 0
static READ_HANDLER ( hitme_unknown_r )
{
	return 0x00;
}
#endif

static MEMORY_READ_START( hitme_readmem )
	{ 0x0000, 0x07ff, MRA_ROM },
	{ 0x0c00, 0x0eff, MRA_RAM },
	{ 0x1000, 0x13ff, MRA_RAM },
	/* guesswork, probably wrong but it reads from these addresses */
	{ 0x1420, 0x1420, hitme_port_0_r },
	{ 0x1520, 0x1520, hitme_port_1_r },
	{ 0x1620, 0x1620, hitme_port_2_r },
	{ 0x1720, 0x1720, hitme_port_3_r },
MEMORY_END

static MEMORY_WRITE_START( hitme_writemem )
	{ 0x0000, 0x07ff, MWA_ROM },
	{ 0x0c00, 0x0eff, hitme_vidram_w, &hitme_vidram },
	{ 0x1000, 0x13ff, MWA_RAM },
MEMORY_END

static PORT_READ_START( hitme_readport )
	{ 0x14, 0x14, hitme_port_0_r },
	{ 0x15, 0x15, hitme_port_1_r },
	{ 0x16, 0x16, hitme_port_2_r },
	{ 0x17, 0x17, hitme_port_3_r },
	{ 0x18, 0x18, input_port_4_r },
	{ 0x19, 0x19, input_port_5_r },
PORT_END

static PORT_WRITE_START( hitme_writeport )
	{ 0x1d, 0x1d, output_port_0_w }, /* OUT0 */
//	{ 0x1e, 0x1e, output_port_1_r }, /* OUT1 */
PORT_END

static MACHINE_DRIVER_START( hitme )

	/* basic machine hardware */
	MDRV_CPU_ADD(8080, 8945000/16 )	/* .559 MHz */
	MDRV_CPU_MEMORY(hitme_readmem,hitme_writemem)
	MDRV_CPU_PORTS(hitme_readport,hitme_writeport)
	/* interrupts not used */

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER )
	MDRV_SCREEN_SIZE(40*8, 19*8)
	MDRV_VISIBLE_AREA( 0*8, 40*8-1, 0*8, 19*8-1 )
	MDRV_GFXDECODE(gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(2)

	MDRV_PALETTE_INIT(hitme)
	MDRV_VIDEO_START(hitme)
	MDRV_VIDEO_UPDATE(hitme)
	/* sound hardware */
MACHINE_DRIVER_END

	/*	The Barricade rom is using a resolution of 32x24 which suggests slightly
   	different hardware from HitMe (40x19) however the screenshot on the arcade
      flyer is using a 40x19 resolution. So is this a different version of
      Barricade or is the resolution set by a dip switch?

      */


static MACHINE_DRIVER_START( brickyrd )

	/* basic machine hardware */
	MDRV_CPU_ADD(8080, 8945000/16 )	/* .559 MHz */
	MDRV_CPU_MEMORY(hitme_readmem,hitme_writemem)
	MDRV_CPU_PORTS(hitme_readport,hitme_writeport)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER )
	MDRV_SCREEN_SIZE(32*8, 24*8)
	MDRV_VISIBLE_AREA( 0*8, 32*8-1, 0*8, 24*8-1 )
	MDRV_GFXDECODE(gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(2)

	MDRV_PALETTE_INIT(hitme)
	MDRV_VIDEO_START(brickyrd)
	MDRV_VIDEO_UPDATE(hitme)
	/* sound hardware */
MACHINE_DRIVER_END

ROM_START( hitme )
	ROM_REGION( 0x10000, REGION_CPU1, ROMREGION_INVERT ) /* 64k for code */
	ROM_LOAD( "hm0.b7", 0x0000, 0x0200, 0x6c48c50f )
	ROM_LOAD( "hm2.c7", 0x0200, 0x0200, 0x25d47ba4 )
	ROM_LOAD( "hm4.d7", 0x0400, 0x0200, 0xf8bfda8d )
	ROM_LOAD( "hm6.e7", 0x0600, 0x0200, 0x8aa87118 )

	ROM_REGION( 0x0400, REGION_GFX1, 0 )
    ROM_LOAD( "hmcg.h7", 0x0000, 0x0200, 0x818f5fbe )
ROM_END

ROM_START( barricad )
   ROM_REGION( 0x10000, REGION_CPU1, ROMREGION_INVERT ) /* 64k for code */
   ROM_LOAD( "550806.7b",   0x0000, 0x0200, 0xea7f5da7 )
   ROM_LOAD( "550807.7c",   0x0200, 0x0200, 0x0afef174 )
   ROM_LOAD( "550808.7d",   0x0400, 0x0200, 0x6e02d260 )
   ROM_LOAD( "550809.7e",   0x0600, 0x0200, 0xd834a63f )

   ROM_REGION( 0x0400, REGION_GFX1, 0 )
   ROM_LOAD( "550805.7h",   0x0000, 0x0200, 0x35197599 )
ROM_END

ROM_START( brickyrd )
   ROM_REGION( 0x10000, REGION_CPU1, ROMREGION_INVERT ) /* 64k for code */
   ROM_LOAD( "550806.7b",   0x0000, 0x0200, 0xea7f5da7 )
   ROM_LOAD( "barricad.7c", 0x0200, 0x0200, 0x94e1d1c0 )
   ROM_LOAD( "550808.7d",   0x0400, 0x0200, 0x6e02d260 )
   ROM_LOAD( "barricad.7e", 0x0600, 0x0200, 0x2b1d914f )

   ROM_REGION( 0x0400, REGION_GFX1, 0 )
   ROM_LOAD( "barricad.7h", 0x0000, 0x0200, 0xc676fd22 )
ROM_END

GAMEX( 1976, hitme,    0,        hitme,    hitme,    0, ROT0, "RamTek", "Hit Me", GAME_NO_SOUND | GAME_IMPERFECT_GRAPHICS )
GAMEX( 1976, barricad, 0,        brickyrd, brickyrd, 0, ROT0, "RamTek", "Barricade", GAME_NO_SOUND  )
GAMEX( 1976, brickyrd, barricad, brickyrd, brickyrd, 0, ROT0, "RamTek", "Brickyard", GAME_NO_SOUND  )
