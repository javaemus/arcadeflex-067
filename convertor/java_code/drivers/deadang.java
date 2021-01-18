/***************************************************************************

	Dead Angle							(c) 1988 Seibu Kaihatsu
	Gang Hunter							(c) 1988 Seibu Kaihatsu

TODO:
- verify dips
- hook up samples

***************************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"
#include "sndhrdw/seibu.h"

static data8_t *deadang_shared_ram;
extern data8_t *deadang_video_data,*deadang_scroll_ram;

static READ_HANDLER( deadang_shared_r ) { return deadang_shared_ram[offset]; }
static WRITE_HANDLER( deadang_shared_w ) { deadang_shared_ram[offset]=data; }

WRITE_HANDLER( deadang_foreground_w );
WRITE_HANDLER( deadang_text_w );
WRITE_HANDLER( deadang_bank_w );
VIDEO_START( deadang );
VIDEO_UPDATE( deadang );

/******************************************************************************/

static MEMORY_READ_START( readmem )
	{ 0x00000, 0x03fff, MRA_RAM },
	{ 0x04000, 0x04fff, deadang_shared_r },
	{ 0x06000, 0x0600f, seibu_main_v30_r },
	{ 0x0a000, 0x0a000, input_port_1_r },
	{ 0x0a001, 0x0a001, input_port_2_r },
	{ 0x0a002, 0x0a002, input_port_3_r },
	{ 0x0a003, 0x0a003, input_port_4_r },
	{ 0xc0000, 0xfffff, MRA_ROM },
MEMORY_END

static MEMORY_WRITE_START( writemem )
	{ 0x00000, 0x037ff, MWA_RAM },
	{ 0x03800, 0x03fff, MWA_RAM, &spriteram },
	{ 0x04000, 0x04fff, deadang_shared_w, &deadang_shared_ram },
	{ 0x06000, 0x0600f, seibu_main_v30_w },
	{ 0x08000, 0x087ff, deadang_text_w, &videoram },
	{ 0x0c000, 0x0cfff, paletteram_xxxxBBBBGGGGRRRR_w, &paletteram },
	{ 0x0e000, 0x0e0ff, MWA_RAM, &deadang_scroll_ram },
	{ 0xc0000, 0xfffff, MWA_ROM },
MEMORY_END

static MEMORY_READ_START( sub_readmem )
	{ 0x00000, 0x037ff, MRA_RAM },
	{ 0x03800, 0x03fff, MRA_RAM },
	{ 0x04000, 0x04fff, deadang_shared_r },
	{ 0xe0000, 0xfffff, MRA_ROM },
MEMORY_END

static MEMORY_WRITE_START( sub_writemem )
	{ 0x00000, 0x037ff, MWA_RAM },
	{ 0x03800, 0x03fff, deadang_foreground_w, &deadang_video_data },
	{ 0x04000, 0x04fff, deadang_shared_w },
	{ 0x08000, 0x08000, deadang_bank_w },
	{ 0x0c000, 0x0c000, MWA_NOP }, /* VBL ack? */
	{ 0xe0000, 0xfffff, MWA_ROM },
MEMORY_END

/******************************************************************************/

INPUT_PORTS_START( deadang )
	SEIBU_COIN_INPUTS	/* Must be port 0: coin inputs read through sound cpu */

	PORT_START	/* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 )
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 )
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )

	PORT_START	/* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 )
	PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 )
	PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 )

	PORT_START	/* Dip switch A */
	PORT_DIPNAME( 0x07, 0x07, DEF_STR( Coin_A ) )
	PORT_DIPSETTING(    0x00, DEF_STR( 5C_1C ) )
	PORT_DIPSETTING(    0x04, DEF_STR( 4C_1C ) )
	PORT_DIPSETTING(    0x02, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(    0x06, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x07, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x03, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x05, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(    0x01, DEF_STR( 1C_5C ) )
	PORT_DIPNAME( 0x18, 0x18, DEF_STR( Coin_B ) )
//	PORT_DIPSETTING(    0x10, DEF_STR( 2C_1C ) )
//	PORT_DIPSETTING(    0x00, DEF_STR( 2C_3C ) )
	PORT_DIPSETTING(    0x18, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x08, DEF_STR( 1C_2C ) )
	PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_SERVICE | IPF_TOGGLE, DEF_STR( Service_Mode ), KEYCODE_F2, IP_JOY_NONE )
	PORT_DIPNAME( 0x40, 0x40, DEF_STR( Flip_Screen ) )
	PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )

	PORT_START	/* Dip switch B */
	PORT_DIPNAME( 0x01, 0x01, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x01, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x02, 0x02, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x02, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x04, 0x04, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x04, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x08, 0x08, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x08, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x30, 0x30, "Difficulty?" )
	PORT_DIPSETTING(    0x30, "Normal" )
	PORT_DIPSETTING(    0x20, "Easy" )
	PORT_DIPSETTING(    0x10, "Hard" )
	PORT_DIPSETTING(    0x00, "Very Hard" )
	PORT_DIPNAME( 0x40, 0x40, "Continue?" )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x40, DEF_STR( On ) )
	PORT_DIPNAME( 0x80, 0x80, DEF_STR( Demo_Sounds ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x80, DEF_STR( On ) )
INPUT_PORTS_END

/******************************************************************************/

static struct GfxLayout deadang_charlayout =
{
	8,8,		/* 8*8 characters */
	RGN_FRAC(1,2),
	4,			/* 4 bits per pixel */
	{ 0x4000*8, 0x4000*8+4, 0, 4 },
	{ 3,2,1,0,11,10,9,8 },
	{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
	128
};

static struct GfxLayout deadang_spritelayout =
{
	16,16,	/* 16*16 tiles */
	RGN_FRAC(1,1),
	4,		/* 4 bits per pixel */
	{ 8,12,0,4},
	{ 3,2,1,0, 19,18,17,16, 512+3,512+2,512+1,512+0, 512+11+8,512+10+8,512+9+8,512+8+8},
	{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
	8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32,
	},
 	1024
};

static struct GfxDecodeInfo gfxdecodeinfo[] =
{
	{ REGION_GFX1, 0x000000, &deadang_charlayout,    512, 16 },
	{ REGION_GFX2, 0x000000, &deadang_spritelayout,  768, 16 },
	{ REGION_GFX3, 0x000000, &deadang_spritelayout, 1024, 16 },
	{ REGION_GFX4, 0x000000, &deadang_spritelayout,  256, 16 },
	{ REGION_GFX5, 0x000000, &deadang_spritelayout,    0, 16 },
	{ -1 } /* end of array */
};

/******************************************************************************/

SEIBU_SOUND_SYSTEM_YM2203_HARDWARE(14318180/4)

static INTERRUPT_GEN( deadang_interrupt )
{
	if (cpu_getiloops())
		cpu_set_irq_line_and_vector(cpu_getactivecpu(), 0, HOLD_LINE, 0xc8/4);	/* VBL */
	else
		cpu_set_irq_line_and_vector(cpu_getactivecpu(), 0, HOLD_LINE, 0xc4/4);	/* VBL */
}

static MACHINE_DRIVER_START( deadang )

	/* basic machine hardware */
	MDRV_CPU_ADD(V20,16000000/2) /* Sony 8623h9 CXQ70116D-8 (V20 compatible) */
	MDRV_CPU_MEMORY(readmem,writemem)
	MDRV_CPU_VBLANK_INT(deadang_interrupt,2)

	MDRV_CPU_ADD(V20,16000000/2) /* Sony 8623h9 CXQ70116D-8 (V20 compatible) */
	MDRV_CPU_MEMORY(sub_readmem,sub_writemem)
	MDRV_CPU_VBLANK_INT(deadang_interrupt,2)

	SEIBU3_SOUND_SYSTEM_CPU(14318180/4)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	MDRV_INTERLEAVE(200)

	MDRV_MACHINE_INIT(seibu_sound_2)

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_BUFFERS_SPRITERAM)
	MDRV_SCREEN_SIZE(32*8, 32*8)
	MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
	MDRV_GFXDECODE(gfxdecodeinfo)
	MDRV_PALETTE_LENGTH(2048)

	MDRV_VIDEO_START(deadang)
	MDRV_VIDEO_UPDATE(deadang)

	/* sound hardware */
	SEIBU_SOUND_SYSTEM_YM2203_INTERFACE
MACHINE_DRIVER_END

/******************************************************************************/

ROM_START( deadang )
	ROM_REGION( 0x100000, REGION_CPU1, 0 ) /* v20 main cpu */
	ROM_LOAD16_BYTE("2.18h",   0x0c0000, 0x10000, 0x1bc05b7e )
	ROM_LOAD16_BYTE("4.22h",   0x0c0001, 0x10000, 0x5751d4e7 )
	ROM_LOAD16_BYTE("1.18f",   0x0e0000, 0x10000, 0x8e7b15cc )
	ROM_LOAD16_BYTE("3.21f",   0x0e0001, 0x10000, 0xe784b1fa )

	ROM_REGION( 0x100000, REGION_CPU2, 0 ) /* v20 sub cpu */
	ROM_LOAD16_BYTE("5.6bh",   0x0e0000, 0x10000, 0x9c69eb35 )
	ROM_LOAD16_BYTE("6.9b",    0x0e0001, 0x10000, 0x34a44ce5 )

	ROM_REGION( 0x20000*2, REGION_CPU3, 0 ) /* sound Z80 */
	ROM_LOAD( "13.b1", 0x000000, 0x02000, 0x13b956fb ) /* Encrypted */
	ROM_LOAD( "14.c1", 0x010000, 0x10000, 0x98837d57 ) /* Banked */

	ROM_REGION( 0x08000, REGION_GFX1, ROMREGION_DISPOSE ) /* Chars */
	ROM_LOAD( "7.21j", 0x000000, 0x4000, 0xfe615fcd )
	ROM_LOAD( "8.21l", 0x004000, 0x4000, 0x905d6b27 )

	ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE ) /* Sprites */
	ROM_LOAD( "l12", 0x000000, 0x80000, 0xc94d5cd2 )

	ROM_REGION( 0x100000, REGION_GFX3, ROMREGION_DISPOSE ) /* pf1 layer */
	ROM_LOAD( "16n", 0x000000, 0x80000, 0xfd70e1a5 ) // bank 0 (0x1000 tiles)
	ROM_LOAD( "16r", 0x080000, 0x80000, 0x92f5e382 ) // bank 1 (0x1000 tiles)

	ROM_REGION( 0x40000, REGION_GFX4, ROMREGION_DISPOSE ) // pf2 layer
	ROM_LOAD( "11m", 0x000000, 0x40000, 0xa366659a ) // fixed (0x800 tiles)

	ROM_REGION( 0x40000, REGION_GFX5, ROMREGION_DISPOSE ) // pf3 layer
	ROM_LOAD( "11k", 0x000000, 0x40000, 0x9cf5bcc7 ) // fixed (0x800 tiles)

	ROM_REGION( 0x10000, REGION_GFX6, 0 )	/* background map data */
	ROM_LOAD16_BYTE( "10.6l",  0x00000, 0x8000, 0xca99176b )
	ROM_LOAD16_BYTE( "9.6m",   0x00001, 0x8000, 0x51d868ca )

	ROM_REGION( 0x10000, REGION_GFX7, 0 )	/* background map data */
	ROM_LOAD16_BYTE( "12.6j",  0x00000, 0x8000, 0x2674d23f )
	ROM_LOAD16_BYTE( "11.6k",  0x00001, 0x8000, 0x3dd4d81d )

	ROM_REGION( 0x20000, REGION_SOUND1, 0 )
	ROM_LOAD( "15.b11", 0x000000, 0x10000, 0xfabd74f2 )
	ROM_LOAD( "16.11a", 0x010000, 0x10000, 0xa8d46fc9 )
ROM_END

ROM_START( ghunter )
	ROM_REGION( 0x100000, REGION_CPU1, 0 ) /* v20 main cpu */
	ROM_LOAD16_BYTE("ggh-2.h18",   0x0c0000, 0x10000, 0x7ccc6fee )
	ROM_LOAD16_BYTE("ggh-4.h22",   0x0c0001, 0x10000, 0xd1f23ad7 )
	ROM_LOAD16_BYTE("ggh-1.f18",   0x0e0000, 0x10000, 0x0d6ff111 )
	ROM_LOAD16_BYTE("ggh-3.f22",   0x0e0001, 0x10000, 0x66dec38d )

	ROM_REGION( 0x100000, REGION_CPU2, 0 ) /* v20 sub cpu */
	ROM_LOAD16_BYTE("ggh-5.b6",   0x0e0000, 0x10000, 0x1f612f3b )
	ROM_LOAD16_BYTE("ggh-6.b10",  0x0e0001, 0x10000, 0x63e18e56 )

	ROM_REGION( 0x20000*2, REGION_CPU3, 0 ) /* sound Z80 */
	ROM_LOAD( "13.b1", 0x000000, 0x02000, 0x13b956fb ) /* Encrypted */
	ROM_LOAD( "14.c1", 0x010000, 0x10000, 0x98837d57 ) /* Banked */

	ROM_REGION( 0x08000, REGION_GFX1, ROMREGION_DISPOSE ) /* Chars */
	ROM_LOAD( "7.21j", 0x000000, 0x4000, 0xfe615fcd )
	ROM_LOAD( "8.21l", 0x004000, 0x4000, 0x905d6b27 )

	ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE ) /* Sprites */
	ROM_LOAD( "l12", 0x000000, 0x80000, 0xc94d5cd2 )

	ROM_REGION( 0x100000, REGION_GFX3, ROMREGION_DISPOSE ) /* pf1 layer */
	ROM_LOAD( "16n", 0x000000, 0x80000, 0xfd70e1a5 ) // bank 0 (0x1000 tiles)
	ROM_LOAD( "16r", 0x080000, 0x80000, 0x92f5e382 ) // bank 1 (0x1000 tiles)

	ROM_REGION( 0x40000, REGION_GFX4, ROMREGION_DISPOSE ) // pf2 layer
	ROM_LOAD( "11m", 0x000000, 0x40000, 0xa366659a ) // fixed (0x800 tiles)

	ROM_REGION( 0x40000, REGION_GFX5, ROMREGION_DISPOSE ) // pf3 layer
	ROM_LOAD( "11k", 0x000000, 0x40000, 0x9cf5bcc7 ) // fixed (0x800 tiles)

	ROM_REGION( 0x10000, REGION_GFX6, 0 )	/* background map data */
	ROM_LOAD16_BYTE( "10.6l",  0x00000, 0x8000, 0xca99176b )
	ROM_LOAD16_BYTE( "9.6m",   0x00001, 0x8000, 0x51d868ca )

	ROM_REGION( 0x10000, REGION_GFX7, 0 )	/* background map data */
	ROM_LOAD16_BYTE( "12.6j",  0x00000, 0x8000, 0x2674d23f )
	ROM_LOAD16_BYTE( "11.6k",  0x00001, 0x8000, 0x3dd4d81d )

	ROM_REGION( 0x20000, REGION_SOUND1, 0 )
	ROM_LOAD( "15.b11", 0x000000, 0x10000, 0xfabd74f2 )
	ROM_LOAD( "16.11a", 0x010000, 0x10000, 0xa8d46fc9 )
ROM_END

/******************************************************************************/

static DRIVER_INIT( deadang )
{
	seibu_sound_decrypt(REGION_CPU3,0x2000);
}

GAMEX( 1988, deadang, 0,       deadang, deadang, deadang, ROT0, "Seibu Kaihatsu", "Dead Angle", GAME_IMPERFECT_SOUND )
GAMEX( 1988, ghunter, deadang, deadang, deadang, deadang, ROT0, "Seibu Kaihatsu (Segasa/Sonic license)", "Gang Hunter (Spain)", GAME_IMPERFECT_SOUND )