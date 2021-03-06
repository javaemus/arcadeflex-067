/***************************************************************************

D-Day

driver by Zsolt Vasvari


Convention: "sl" stands for "searchlight"

Note: This game doesn't seem to support cocktail mode, which is not too
      suprising for a gun game.

0000-3fff ROM
5000-53ff Text layer videoram
5400-57ff Foreground (vehicle) layer videoram
5800-5bff Background videoram
5c00-5fff Attributes RAM for vehicle layer
          A0-A4 seem to be ignored.
          D0 - X Flip
          D2 - Used by the software to separate area that the short shots
               cannot penetrate
          Others unknown, they don't seem to be used by this game
6000-63ff RAM

read:

6c00  Input Port #1
7000  Dip Sw #1
7400  Dip Sw #2
7800  Timer
7c00  Analog Control

write:

4000 Search light image and flip
6400 AY8910 #1 Control Port
6401 AY8910 #1 Write Port
6800 AY8910 #2 Control Port
6801 AY8910 #2 Write Port
7800 Bit 0 - Coin Counter 1
     Bit 1 - Coin Counter 2
	 Bit 2 - ??? Pulsated when the player is hit
	 Bit 3 - ??? Seems to be unused
	 Bit 4 - Tied to AY8910 RST. Used to turn off sound
	 Bit 5 - ??? Seem to be always on
	 Bit 6 - Search light enable
     Bit 7 - ???


***************************************************************************/

#include "driver.h"

extern unsigned char *dday_bgvideoram;
extern unsigned char *dday_fgvideoram;
extern unsigned char *dday_textvideoram;
extern unsigned char *dday_colorram;

PALETTE_INIT( dday );
VIDEO_START( dday );
VIDEO_UPDATE( dday );
WRITE_HANDLER( dday_bgvideoram_w );
WRITE_HANDLER( dday_fgvideoram_w );
WRITE_HANDLER( dday_textvideoram_w );
WRITE_HANDLER( dday_colorram_w );
READ_HANDLER( dday_colorram_r );
WRITE_HANDLER( dday_control_w );
WRITE_HANDLER( dday_sl_control_w );
READ_HANDLER( dday_countdown_timer_r );


static MEMORY_READ_START( readmem )
	{ 0x0000, 0x3fff, MRA_ROM },
	{ 0x5000, 0x5bff, MRA_RAM },
	{ 0x5c00, 0x5fff, dday_colorram_r },
	{ 0x6000, 0x63ff, MRA_RAM },
	{ 0x6c00, 0x6c00, input_port_0_r },
	{ 0x7000, 0x7000, input_port_1_r },
	{ 0x7400, 0x7400, input_port_2_r },
	{ 0x7800, 0x7800, dday_countdown_timer_r },
	{ 0x7c00, 0x7c00, input_port_3_r },
MEMORY_END

static MEMORY_WRITE_START( writemem )
	{ 0x0000, 0x3fff, MWA_ROM },
	{ 0x4000, 0x4000, dday_sl_control_w },
	{ 0x5000, 0x53ff, dday_textvideoram_w, &dday_textvideoram },
	{ 0x5400, 0x57ff, dday_fgvideoram_w, &dday_fgvideoram },
	{ 0x5800, 0x5bff, dday_bgvideoram_w, &dday_bgvideoram },
	{ 0x5c00, 0x5fff, dday_colorram_w, &dday_colorram },
	{ 0x6000, 0x63ff, MWA_RAM },
	{ 0x6400, 0x6400, AY8910_control_port_0_w },
	{ 0x6401, 0x6401, AY8910_write_port_0_w },
	{ 0x6402, 0x6402, AY8910_control_port_0_w },
	{ 0x6403, 0x6403, AY8910_write_port_0_w },
	{ 0x6404, 0x6404, AY8910_control_port_0_w },
	{ 0x6405, 0x6405, AY8910_write_port_0_w },
	{ 0x6406, 0x6406, AY8910_control_port_0_w },
	{ 0x6407, 0x6407, AY8910_write_port_0_w },
	{ 0x6408, 0x6408, AY8910_control_port_0_w },
	{ 0x6409, 0x6409, AY8910_write_port_0_w },
	{ 0x640a, 0x640a, AY8910_control_port_0_w },
	{ 0x640b, 0x640b, AY8910_write_port_0_w },
	{ 0x640c, 0x640c, AY8910_control_port_0_w },
	{ 0x640d, 0x640d, AY8910_write_port_0_w },
	{ 0x6800, 0x6800, AY8910_control_port_1_w },
	{ 0x6801, 0x6801, AY8910_write_port_1_w },
	{ 0x7800, 0x7800, dday_control_w },
MEMORY_END



INPUT_PORTS_START( dday )
	PORT_START      /* IN 0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON1 ) /* fire button */
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNKNOWN ) /* doesn't seem to be */
                                                  /* accessed */
	PORT_START      /* DSW 0 */
	PORT_DIPNAME( 0x03, 0x01, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "2" )
	PORT_DIPSETTING(    0x01, "3" )
	PORT_DIPSETTING(    0x02, "4" )
	PORT_DIPSETTING(    0x03, "5" )
	PORT_DIPNAME( 0x0c, 0x00, "Extended Play At" )
	PORT_DIPSETTING(    0x00, "10000" )
	PORT_DIPSETTING(    0x04, "15000" )
	PORT_DIPSETTING(    0x08, "20000" )
	PORT_DIPSETTING(    0x0c, "25000" )
	PORT_DIPNAME( 0x10, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x10, DEF_STR( On ) )
	PORT_DIPNAME( 0x20, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x20, DEF_STR( On ) )
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x40, DEF_STR( On ) )
	PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )  // has to do with lives
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x80, DEF_STR( On ) )

	PORT_START      /* DSW 1 */
	PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( Coin_A ) )
	PORT_DIPSETTING(    0x0e, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x0c, DEF_STR( 2C_2C ) )
	PORT_DIPSETTING(    0x0f, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x0a, DEF_STR( 2C_3C ) )
	PORT_DIPSETTING(    0x08, DEF_STR( 2C_4C ) )
	PORT_DIPSETTING(    0x0d, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x06, DEF_STR( 2C_5C ) )
	PORT_DIPSETTING(    0x04, DEF_STR( 2C_6C ) )
	PORT_DIPSETTING(    0x0b, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(    0x02, DEF_STR( 2C_7C ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 2C_8C ) )
	PORT_DIPSETTING(    0x09, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(    0x07, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(    0x05, DEF_STR( 1C_6C ) )
	PORT_DIPSETTING(    0x03, DEF_STR( 1C_7C ) )
	PORT_DIPSETTING(    0x01, DEF_STR( 1C_8C ) )
	PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( Coin_B ) )
	PORT_DIPSETTING(    0xe0, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0xc0, DEF_STR( 2C_2C ) )
	PORT_DIPSETTING(    0xf0, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0xa0, DEF_STR( 2C_3C ) )
	PORT_DIPSETTING(    0x80, DEF_STR( 2C_4C ) )
	PORT_DIPSETTING(    0xd0, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x60, DEF_STR( 2C_5C ) )
	PORT_DIPSETTING(    0x40, DEF_STR( 2C_6C ) )
	PORT_DIPSETTING(    0xb0, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(    0x20, DEF_STR( 2C_7C ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 2C_8C ) )
	PORT_DIPSETTING(    0x90, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(    0x70, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(    0x50, DEF_STR( 1C_6C ) )
	PORT_DIPSETTING(    0x30, DEF_STR( 1C_7C ) )
	PORT_DIPSETTING(    0x10, DEF_STR( 1C_8C ) )

	PORT_START      /* IN1 */
	PORT_ANALOG(0xff, 96, IPT_PADDLE, 20, 10, 0, 191 )
INPUT_PORTS_END

INPUT_PORTS_START( ddayc )
	PORT_START      /* IN 0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON1 ) // Fire Button
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 ) // Distance Button
	PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNKNOWN ) // Doesn't seem to be
                                                  // accessed
	PORT_START      /* DSW 0 */
	PORT_DIPNAME( 0x03, 0x01, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "2" )
	PORT_DIPSETTING(    0x01, "3" )
	PORT_DIPSETTING(    0x02, "4" )
	PORT_DIPSETTING(    0x03, "5" )
	PORT_DIPNAME( 0x0c, 0x00, "Extended Play At" )
	PORT_DIPSETTING(    0x00, "4000" )
	PORT_DIPSETTING(    0x04, "6000" )
	PORT_DIPSETTING(    0x08, "8000" )
	PORT_DIPSETTING(    0x0c, "10000" )
	PORT_DIPNAME( 0x30, 0x10, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(    0x30, "Easy" )   // Easy   - No Bombs, No Troop Carriers
	PORT_DIPSETTING(    0x20, "Normal" ) // Normal - No Bombs, Troop Carriers
	PORT_DIPSETTING(    0x10, "Hard" )   // Hard   - Bombs, Troop Carriers
//PORT_DIPSETTING(    0x00, "Hard" ) // Same as 0x10
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Unknown ) ) // Doesn't seem to be used
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x40, DEF_STR( On ) )
	PORT_DIPNAME( 0x80, 0x80, "Start with 20000 Pts" )
	PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )

	PORT_START      /* DSW 1 */
	PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( Coin_A ) )
	PORT_DIPSETTING(    0x0e, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x0c, DEF_STR( 2C_2C ) )
	PORT_DIPSETTING(    0x0f, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x0a, DEF_STR( 2C_3C ) )
	PORT_DIPSETTING(    0x08, DEF_STR( 2C_4C ) )
	PORT_DIPSETTING(    0x0d, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x06, DEF_STR( 2C_5C ) )
	PORT_DIPSETTING(    0x04, DEF_STR( 2C_6C ) )
	PORT_DIPSETTING(    0x0b, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(    0x02, DEF_STR( 2C_7C ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 2C_8C ) )
	PORT_DIPSETTING(    0x09, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(    0x07, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(    0x05, DEF_STR( 1C_6C ) )
	PORT_DIPSETTING(    0x03, DEF_STR( 1C_7C ) )
	PORT_DIPSETTING(    0x01, DEF_STR( 1C_8C ) )
	PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( Coin_B ) )
	PORT_DIPSETTING(    0xe0, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0xc0, DEF_STR( 2C_2C ) )
	PORT_DIPSETTING(    0xf0, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0xa0, DEF_STR( 2C_3C ) )
	PORT_DIPSETTING(    0x80, DEF_STR( 2C_4C ) )
	PORT_DIPSETTING(    0xd0, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x60, DEF_STR( 2C_5C ) )
	PORT_DIPSETTING(    0x40, DEF_STR( 2C_6C ) )
	PORT_DIPSETTING(    0xb0, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(    0x20, DEF_STR( 2C_7C ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 2C_8C ) )
	PORT_DIPSETTING(    0x90, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(    0x70, DEF_STR( 1C_5C ) )
	PORT_DIPSETTING(    0x50, DEF_STR( 1C_6C ) )
	PORT_DIPSETTING(    0x30, DEF_STR( 1C_7C ) )
	PORT_DIPSETTING(    0x10, DEF_STR( 1C_8C ) )

	PORT_START      /* IN1 */
	PORT_ANALOG(0xff, 96, IPT_PADDLE, 20, 10, 0, 191 )
INPUT_PORTS_END



static struct GfxLayout layout_1bpp =
{
	8,8,    		/* 8*8 characters */
	RGN_FRAC(1,2),	/* 256 characters */
	1,      		/* 1 bit per pixel */
	{ RGN_FRAC(0,1) },
	{ 7, 6, 5, 4, 3, 2, 1, 0 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
	8*8     /* every char takes 8 consecutive bytes */
};

static struct GfxLayout layout_2bpp =
{
	8,8,    		/* 8*8 characters */
	RGN_FRAC(1,2),	/* 256 characters */
	2,      		/* 2 bits per pixel */
	{ RGN_FRAC(0,2), RGN_FRAC(1,2) }, /* the bitplanes are separated */
	{ 7, 6, 5, 4, 3, 2, 1, 0 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
	8*8     /* every char takes 8 consecutive bytes */
};

static struct GfxLayout layout_3bpp =
{
	8,8,    		/* 8*8 characters */
	RGN_FRAC(1,3),	/* 256 characters */
	3,      		/* 3 bits per pixel */
	{ RGN_FRAC(0,3), RGN_FRAC(1,3), RGN_FRAC(2,3) }, /* the bitplanes are separated */
	{ 7, 6, 5, 4, 3, 2, 1, 0 },
	{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
	8*8     /* every char takes 8 consecutive bytes */
};

static struct GfxDecodeInfo gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0, &layout_3bpp, 0,       256/8 },	/* background */
	{ REGION_GFX2, 0, &layout_2bpp, 8*4,     8 },		/* foreground */
	{ REGION_GFX3, 0, &layout_2bpp, 8*4+8*4, 8 },		/* text */
	{ REGION_GFX4, 0, &layout_1bpp, 254,     1 },		/* searchlight */
	{ -1 } /* end of array */
};



static struct AY8910interface ay8910_interface =
{
	2,      /* 2 chips */
	1000000,	/* 1.0 MHz ? */
	{ 25, 25 },
	{ 0 },
	{ 0 },
	{ 0 },
	{ 0 }
};


static MACHINE_DRIVER_START( dday )

	/* basic machine hardware */
	MDRV_CPU_ADD(Z80, 2000000)     /* 2 MHz ? */
	MDRV_CPU_MEMORY(readmem,writemem)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)

	/* video hardware */
	MDRV_SCREEN_SIZE(32*8, 32*8)
	MDRV_VISIBLE_AREA(0*8, 32*8-1, 0*8, 28*8-1)
	MDRV_GFXDECODE(gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(256)
	MDRV_COLORTABLE_LENGTH(256)//8*8+8*4+8*4,
	MDRV_PALETTE_INIT(dday)

	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_HAS_SHADOWS)
	MDRV_VIDEO_START(dday)
	MDRV_VIDEO_UPDATE(dday)

	/* sound hardware */
	MDRV_SOUND_ADD(AY8910, ay8910_interface)
MACHINE_DRIVER_END



/***************************************************************************

  Game driver(s)

***************************************************************************/

ROM_START( dday )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
	ROM_LOAD( "e8_63co.bin",  0x0000, 0x1000, 0x13d53793 )
	ROM_LOAD( "e7_64co.bin",  0x1000, 0x1000, 0xe1ef2a70 )
	ROM_LOAD( "e6_65co.bin",  0x2000, 0x1000, 0xfe414a83 )
	ROM_LOAD( "e5_66co.bin",  0x3000, 0x1000, 0xfc9f7774 )

	ROM_REGION( 0x1800, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "k4_73.bin",    0x0000, 0x0800, 0xfa6237e4 )
	ROM_LOAD( "k2_71.bin",    0x0800, 0x0800, 0xf85461de )
	ROM_LOAD( "k3_72.bin",    0x1000, 0x0800, 0xfdfe88b6 )

	ROM_REGION( 0x1000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD( "j8_70co.bin",  0x0000, 0x0800, 0x0c60e94c )
	ROM_LOAD( "j9_69co.bin",  0x0800, 0x0800, 0xba341c10 )

	ROM_REGION( 0x1000, REGION_GFX3, ROMREGION_DISPOSE )
	ROM_LOAD( "k6_74o.bin",   0x0000, 0x0800, 0x66719aea )
	ROM_LOAD( "k7_75o.bin",   0x0800, 0x0800, 0x5f8772e2 )

	ROM_REGION( 0x0800, REGION_GFX4, ROMREGION_DISPOSE )
	ROM_LOAD( "d4_68.bin",    0x0000, 0x0800, 0xf3649264 )

	ROM_REGION( 0x1000, REGION_USER1, 0 )
	ROM_LOAD( "d2_67.bin",    0x0000, 0x1000, 0x2b693e42 )  /* search light map */

	ROM_REGION( 0x0300, REGION_PROMS, 0 )
	ROM_LOAD( "dday.m11",     0x0000, 0x0100, 0xaef6bbfc )  /* red component */
	ROM_LOAD( "dday.m8",      0x0100, 0x0100, 0xad3314b9 )  /* green component */
	ROM_LOAD( "dday.m3",      0x0200, 0x0100, 0xe877ab82 )  /* blue component */
ROM_END

ROM_START( ddayc )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
	ROM_LOAD( "e8_63-c.bin",  0x0000, 0x1000, 0xd4fa3ae3 )
	ROM_LOAD( "e7_64-c.bin",  0x1000, 0x1000, 0x9fb8b1a7 )
	ROM_LOAD( "e6_65-c.bin",  0x2000, 0x1000, 0x4c210686 )
	ROM_LOAD( "e5_66-c.bin",  0x3000, 0x1000, 0xe7e832f9 )

	ROM_REGION( 0x1800, REGION_GFX1, ROMREGION_DISPOSE )
	ROM_LOAD( "k4_73.bin",    0x0000, 0x0800, 0xfa6237e4 )
	ROM_LOAD( "k2_71.bin",    0x0800, 0x0800, 0xf85461de )
	ROM_LOAD( "k3_72.bin",    0x1000, 0x0800, 0xfdfe88b6 )

	ROM_REGION( 0x1000, REGION_GFX2, ROMREGION_DISPOSE )
	ROM_LOAD( "j8_70-c.bin",  0x0000, 0x0800, 0xa0c6b86b )
	ROM_LOAD( "j9_69-c.bin",  0x0800, 0x0800, 0xd352a3d6 )

	ROM_REGION( 0x1000, REGION_GFX3, ROMREGION_DISPOSE )
	ROM_LOAD( "k6_74.bin",    0x0000, 0x0800, 0xd21a3e22 )
	ROM_LOAD( "k7_75.bin",    0x0800, 0x0800, 0xa5e5058c )

	ROM_REGION( 0x0800, REGION_GFX4, ROMREGION_DISPOSE )
	ROM_LOAD( "d4_68.bin",    0x0000, 0x0800, 0xf3649264 )

	ROM_REGION( 0x1000, REGION_USER1, 0 )
	ROM_LOAD( "d2_67.bin",    0x0000, 0x1000, 0x2b693e42 )  /* search light map */

	ROM_REGION( 0x0300, REGION_PROMS, 0 )
	ROM_LOAD( "dday.m11",     0x0000, 0x0100, 0xaef6bbfc )  /* red component */
	ROM_LOAD( "dday.m8",      0x0100, 0x0100, 0xad3314b9 )  /* green component */
	ROM_LOAD( "dday.m3",      0x0200, 0x0100, 0xe877ab82 )  /* blue component */
ROM_END


GAMEX( 1982, dday,  0,    dday, dday,  0, ROT0, "Olympia", "D-Day", GAME_IMPERFECT_COLORS )
GAMEX( 1982, ddayc, dday, dday, ddayc, 0, ROT0, "Olympia (Centuri license)", "D-Day (Centuri)", GAME_IMPERFECT_COLORS )
