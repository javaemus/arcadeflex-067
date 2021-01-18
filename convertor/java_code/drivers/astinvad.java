/***************************************************************************

Misc early Z80 games with simple color bitmap graphics

	- Space King 2    (c) Konami
	- Kamikaze        (c) Leijac
	- Astro Invader   (c) Stern
	- Space Intruder  (c) Shoei

Space Intruder emulation by Lee Taylor (lee@defender.demon.co.uk),
	December 1998.

***************************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"


extern VIDEO_UPDATE( astinvad );
extern VIDEO_UPDATE( spaceint );

extern VIDEO_START( astinvad );
extern VIDEO_START( spcking2 );
extern VIDEO_START( spaceint );

extern WRITE_HANDLER( astinvad_sound1_w );
extern WRITE_HANDLER( astinvad_sound2_w );
extern WRITE_HANDLER( astinvad_videoram_w );
extern WRITE_HANDLER( spaceint_sound1_w );
extern WRITE_HANDLER( spaceint_sound2_w );
extern WRITE_HANDLER( spaceint_videoram_w );
extern WRITE_HANDLER( spaceint_color_w);

extern struct Samplesinterface astinvad_samples_interface;


static PALETTE_INIT( astinvad )
{
	int i;

	for (i = 0; i < 8; i++)
	{
		palette_set_color(i,
			(i & 1) ? 0xff : 0,
			(i & 4) ? 0xff : 0,
			(i & 2) ? 0xff : 0);
	}
}


static MEMORY_READ_START( astinvad_readmem )
	{ 0x0000, 0x1bff, MRA_ROM },
	{ 0x1c00, 0x3fff, MRA_RAM },
MEMORY_END

static MEMORY_WRITE_START( astinvad_writemem )
	{ 0x0000, 0x1bff, MWA_ROM },
	{ 0x1c00, 0x1fff, MWA_RAM },
	{ 0x2000, 0x3fff, astinvad_videoram_w, &videoram, &videoram_size },
	{ 0x4000, 0x4fff, MWA_NOP }, /* sloppy game code writes here */
MEMORY_END


static MEMORY_READ_START( spaceint_readmem )
	{ 0x0000, 0x17ff, MRA_ROM },
	{ 0x2000, 0x23ff, MRA_RAM },
	{ 0x4000, 0x5fff, MRA_RAM },
MEMORY_END

static MEMORY_WRITE_START( spaceint_writemem )
	{ 0x0000, 0x17ff, MWA_ROM },
	{ 0x2000, 0x23ff, MWA_RAM },
	{ 0x4000, 0x5fff, spaceint_videoram_w, &videoram, &videoram_size },
MEMORY_END


static PORT_READ_START( astinvad_readport )
	{ 0x08, 0x08, input_port_0_r },
	{ 0x09, 0x09, input_port_1_r },
	{ 0x0a, 0x0a, input_port_2_r },
PORT_END

static PORT_WRITE_START( astinvad_writeport )
	{ 0x04, 0x04, astinvad_sound1_w },
	{ 0x05, 0x05, astinvad_sound2_w },
PORT_END


static PORT_READ_START( spaceint_readport )
	{ 0x00, 0x00, input_port_0_r },
	{ 0x01, 0x01, input_port_1_r },
PORT_END

static PORT_WRITE_START( spaceint_writeport )
	{ 0x02, 0x02, spaceint_sound1_w },
	{ 0x03, 0x03, spaceint_color_w },
	{ 0x04, 0x04, spaceint_sound2_w },
PORT_END


#define COMMON_INPUT_BITS \
	PORT_START \
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 ) \
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 ) \
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 ) \
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED ) \
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 ) \
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY ) \
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY ) \
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED ) \


INPUT_PORTS_START( astinvad )
	COMMON_INPUT_BITS

	PORT_START      /* IN1 */
	PORT_DIPNAME( 0x01, 0x00, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "3" )
	PORT_DIPSETTING(    0x01, "4" )
	PORT_DIPNAME( 0x02, 0x02, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x02, "10000" )
	PORT_DIPSETTING(    0x00, "20000" )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_DIPNAME( 0x88, 0x00, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x88, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(    0x80, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x08, DEF_STR( 1C_2C ) )

	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL )

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_VBLANK )
	PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN3 */
	PORT_DIPNAME( 0xff, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0xff, DEF_STR( Cocktail ) )
INPUT_PORTS_END

INPUT_PORTS_START( kamikaze )
	COMMON_INPUT_BITS

	PORT_START      /* IN1 */
	PORT_DIPNAME( 0x03, 0x03, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x03, "3" )
	PORT_DIPSETTING(    0x01, "4" )
	PORT_DIPSETTING(    0x02, "5" )
	PORT_DIPSETTING(    0x00, "6" )
	PORT_DIPNAME( 0x04, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x04, DEF_STR( On ) )
	PORT_DIPNAME( 0x88, 0x88, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x88, "5000" )
	PORT_DIPSETTING(    0x80, "10000" )
	PORT_DIPSETTING(    0x08, "15000" )
	PORT_DIPSETTING(    0x00, "20000" )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2 )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 )

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_VBLANK )
	PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN3 */
	PORT_DIPNAME( 0xff, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0xff, DEF_STR( Cocktail ) )
INPUT_PORTS_END

INPUT_PORTS_START( spcking2 )
	COMMON_INPUT_BITS

	PORT_START      /* IN1 */
	PORT_DIPNAME( 0x03, 0x00, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "3" )
	PORT_DIPSETTING(    0x01, "4" )
	PORT_DIPSETTING(    0x02, "5" )
	PORT_DIPSETTING(    0x03, "6" )
	PORT_DIPNAME( 0x04, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x04, DEF_STR( On ) )
	PORT_DIPNAME( 0x08, 0x00, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x08, "1000" )
	PORT_DIPSETTING(    0x00, "2000" )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL )
	PORT_DIPNAME( 0x80, 0x00, "Coin Info" )
	PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_VBLANK )
	PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN3 */
	PORT_DIPNAME( 0xff, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x01, DEF_STR( Cocktail ) )
INPUT_PORTS_END

INPUT_PORTS_START( spaceint )
	PORT_START      /* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )

	PORT_START      /* IN1 */
	PORT_DIPNAME( 0x01, 0x00, DEF_STR( Unused ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x01, DEF_STR( On ) )
	PORT_DIPNAME( 0x06, 0x00, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "3" )
	PORT_DIPSETTING(    0x04, "4" )
	PORT_DIPSETTING(    0x02, "5" )
	PORT_DIPNAME( 0x08, 0x00, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x08, DEF_STR( 1C_2C ) )

	PORT_START      /* IN2 */
	PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 1 ) /* causes NMI */

	PORT_START      /* IN3 */
	PORT_DIPNAME( 0xff, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0xff, DEF_STR( Cocktail ) )
INPUT_PORTS_END


static INTERRUPT_GEN( spaceint_interrupt )
{
	if (readinputport(2) & 1)	/* coin */
		cpu_set_nmi_line(0, PULSE_LINE);

	cpu_set_irq_line(0, 0, HOLD_LINE);
}


static MACHINE_DRIVER_START( astinvad )

	/* basic machine hardware */
	MDRV_CPU_ADD(Z80, 2000000)
	MDRV_CPU_MEMORY(astinvad_readmem,astinvad_writemem)
	MDRV_CPU_PORTS(astinvad_readport,astinvad_writeport)
	MDRV_CPU_VBLANK_INT(irq0_line_hold,2)    /* two interrupts per frame */

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
	MDRV_SCREEN_SIZE(32*8, 32*8)
	MDRV_VISIBLE_AREA(0*8, 32*8-1, 4*8, 32*8-1)
	MDRV_PALETTE_LENGTH(8)

	MDRV_PALETTE_INIT(astinvad)
	MDRV_VIDEO_START(astinvad)
	MDRV_VIDEO_UPDATE(astinvad)

	/* sound hardware */
	MDRV_SOUND_ADD(SAMPLES, astinvad_samples_interface)
MACHINE_DRIVER_END


static MACHINE_DRIVER_START( spcking2 )

	/* basic machine hardware */
	MDRV_IMPORT_FROM(astinvad)

	/* video hardware */
	MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)

	MDRV_VIDEO_START( spcking2 )
MACHINE_DRIVER_END


static MACHINE_DRIVER_START( spaceint )

	/* basic machine hardware */
	MDRV_CPU_ADD(Z80, 2000000)        /* 2 MHz? */
	MDRV_CPU_MEMORY(spaceint_readmem,spaceint_writemem)
	MDRV_CPU_PORTS(spaceint_readport,spaceint_writeport)
	MDRV_CPU_VBLANK_INT(spaceint_interrupt,1)

	MDRV_FRAMES_PER_SECOND(60)

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
	MDRV_SCREEN_SIZE(32*8, 32*8)
	MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
	MDRV_PALETTE_LENGTH(8)

	MDRV_PALETTE_INIT(astinvad)
	MDRV_VIDEO_START(spaceint)
	MDRV_VIDEO_UPDATE(spaceint)

	/* sound hardware */
	MDRV_SOUND_ADD(SAMPLES, astinvad_samples_interface)
MACHINE_DRIVER_END


ROM_START( astinvad )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
	ROM_LOAD( "ai_cpu_1.rom", 0x0000, 0x0400, 0x20e3ec41 )
	ROM_LOAD( "ai_cpu_2.rom", 0x0400, 0x0400, 0xe8f1ab55 )
	ROM_LOAD( "ai_cpu_3.rom", 0x0800, 0x0400, 0xa0092553 )
	ROM_LOAD( "ai_cpu_4.rom", 0x0c00, 0x0400, 0xbe14185c )
	ROM_LOAD( "ai_cpu_5.rom", 0x1000, 0x0400, 0xfee681ec )
	ROM_LOAD( "ai_cpu_6.rom", 0x1400, 0x0400, 0xeb338863 )
	ROM_LOAD( "ai_cpu_7.rom", 0x1800, 0x0400, 0x16dcfea4 )

	ROM_REGION( 0x0400, REGION_PROMS, 0 )
	ROM_LOAD( "ai_vid_c.rom", 0x0000, 0x0400, 0xb45287ff )
ROM_END

ROM_START( kamikaze )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
	ROM_LOAD( "km01",         0x0000, 0x0800, 0x8aae7414 )
	ROM_LOAD( "km02",         0x0800, 0x0800, 0x6c7a2beb )
	ROM_LOAD( "km03",         0x1000, 0x0800, 0x3e8dedb6 )
	ROM_LOAD( "km04",         0x1800, 0x0800, 0x494e1f6d )

	ROM_REGION( 0x0400, REGION_PROMS, 0 )
	ROM_LOAD( "ai_vid_c.rom", 0x0000, 0x0400, BADCRC(0xb45287ff) )
ROM_END

ROM_START( spcking2 )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
	ROM_LOAD( "1.bin",        0x0000, 0x0400, 0x716fe9e0 )
	ROM_LOAD( "2.bin",        0x0400, 0x0400, 0x6f6d4e5c )
	ROM_LOAD( "3.bin",        0x0800, 0x0400, 0x2ab1c280 )
	ROM_LOAD( "4.bin",        0x0c00, 0x0400, 0x07ba1f21 )
	ROM_LOAD( "5.bin",        0x1000, 0x0400, 0xb084c074 )
	ROM_LOAD( "6.bin",        0x1400, 0x0400, 0xb53d7791 )

	ROM_REGION( 0x0400, REGION_PROMS, 0 )
	ROM_LOAD( "c.bin",        0x0000, 0x0400, 0xd27fe595 )
ROM_END

ROM_START( spaceint )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
	ROM_LOAD( "1",			  0x0000, 0x0400, 0x184314d2 )
	ROM_LOAD( "2",			  0x0400, 0x0400, 0x55459aa1 )
	ROM_LOAD( "3",			  0x0800, 0x0400, 0x9d6819be )
	ROM_LOAD( "4",			  0x0c00, 0x0400, 0x432052d4 )
	ROM_LOAD( "5",			  0x1000, 0x0400, 0xc6cfa650 )
	ROM_LOAD( "6",			  0x1400, 0x0400, 0xc7ccf40f )

	ROM_REGION( 0x0100, REGION_PROMS, 0 )
	ROM_LOAD( "clr",		  0x0000, 0x0100, 0x13c1803f )
ROM_END


GAME ( 1980, astinvad, 0,        astinvad, astinvad, 0, ROT270, "Stern",  "Astro Invader" )
GAME ( 1979, kamikaze, astinvad, astinvad, kamikaze, 0, ROT270, "Leijac", "Kamikaze" )
GAME ( 1979, spcking2, 0,        spcking2, spcking2, 0, ROT270, "Konami", "Space King 2" )
GAMEX( 1980, spaceint, 0,        spaceint, spaceint, 0, ROT90,  "Shoei",  "Space Intruder", GAME_WRONG_COLORS )
