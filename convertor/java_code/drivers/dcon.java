/***************************************************************************

	D-Con									(c) 1992 Success
	SD Gundam Psycho Salamander no Kyoui	(c) 1991 Banpresto/Bandai

	These games run on Seibu hardware.

	Emulation by Bryan McPhail, mish@tendril.co.uk

	Coin inputs are handled by the sound CPU, so they don't work with sound
	disabled. Use the service switch instead.

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class dcon
{
	
	WRITE16_HANDLER( dcon_gfxbank_w );
	WRITE16_HANDLER( dcon_background_w );
	WRITE16_HANDLER( dcon_foreground_w );
	WRITE16_HANDLER( dcon_midground_w );
	WRITE16_HANDLER( dcon_text_w );
	WRITE16_HANDLER( dcon_control_w );
	READ16_HANDLER( dcon_control_r );
	
	VIDEO_START( dcon );
	VIDEO_UPDATE( dcon );
	VIDEO_UPDATE( sdgndmps );
	
	extern data16_t *dcon_back_data,*dcon_fore_data,*dcon_mid_data,*dcon_scroll_ram,*dcon_textram;
	
	/***************************************************************************/
	
	static MEMORY_READ16_START( readmem )
		{ 0x00000, 0x7ffff, MRA16_ROM },
		{ 0x80000, 0x8bfff, MRA16_RAM },
		{ 0x8c000, 0x8c7ff, MRA16_RAM },
		{ 0x8c800, 0x8cfff, MRA16_RAM },
		{ 0x8d000, 0x8d7ff, MRA16_RAM },
		{ 0x8d800, 0x8e7ff, MRA16_RAM },
		{ 0x8e800, 0x8f7ff, MRA16_RAM },
		{ 0x8f800, 0x8ffff, MRA16_RAM },
		{ 0xa0000, 0xa000d, seibu_main_word_r },
		{ 0xc001c, 0xc001d, dcon_control_r },
		{ 0xe0000, 0xe0001, input_port_1_word_r },
		{ 0xe0002, 0xe0003, input_port_2_word_r },
		{ 0xe0004, 0xe0005, input_port_3_word_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( writemem )
		{ 0x00000, 0x7ffff, MWA16_ROM },
		{ 0x80000, 0x8bfff, MWA16_RAM },
		{ 0x8c000, 0x8c7ff, dcon_background_w, &dcon_back_data },
		{ 0x8c800, 0x8cfff, dcon_foreground_w, &dcon_fore_data },
		{ 0x8d000, 0x8d7ff, dcon_midground_w, &dcon_mid_data },
		{ 0x8d800, 0x8e7ff, dcon_text_w, &dcon_textram },
		{ 0x8e800, 0x8f7ff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x8f800, 0x8ffff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x9d000, 0x9d7ff, dcon_gfxbank_w },
		{ 0xa0000, 0xa000d, seibu_main_word_w },
		{ 0xc001c, 0xc001d, dcon_control_w },
		{ 0xc0020, 0xc002f, MWA16_RAM, &dcon_scroll_ram },
		{ 0xc0080, 0xc0081, MWA16_NOP },
		{ 0xc00c0, 0xc00c1, MWA16_NOP },
	MEMORY_END
	
	/******************************************************************************/
	
	#define DCON_PLAYERS_CONTROLS \
		PORT_START();  \
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER1 );\
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER1 );\
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER1 );\
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );\
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );\
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );\
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED );\
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );\
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2 );\
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2 );\
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2 );\
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );\
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );\
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );\
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNUSED );\
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNUSED );
	
	#define DCON_SYSTEM \
		PORT_START();  \
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_START1 );\
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_START2 );\
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_UNKNOWN);\
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_SERVICE1 );\
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	static InputPortPtr input_ports_dcon = new InputPortPtr(){ public void handler() { 
		SEIBU_COIN_INPUTS	/* Must be port 0: coin inputs read through sound cpu */
	
		PORT_START(); 
		PORT_DIPNAME( 0x0007, 0x0007, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0003, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0007, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0006, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0005, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0004, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x0038, 0x0038, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0018, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0038, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0030, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0028, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0020, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0100, 0x0100, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0100, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0200, 0x0200, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0200, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0400, 0x0400, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000, 0x2000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
	
		DCON_PLAYERS_CONTROLS
	
		DCON_SYSTEM
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_sdgndmps = new InputPortPtr(){ public void handler() { 
		SEIBU_COIN_INPUTS	/* Must be port 0: coin inputs read through sound cpu */
	
		PORT_START(); 
		PORT_DIPNAME( 0x000f, 0x000f, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x0004, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x000a, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_3C") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(      0x000f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x000c, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(      0x000e, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0x0007, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0006, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(      0x000b, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0003, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x000d, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(      0x0005, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(      0x0009, DEF_STR( "1C_7C") );
		PORT_DIPNAME( 0x00f0, 0x00f0, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x00a0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0020, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(      0x00f0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x00c0, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(      0x00e0, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0x0070, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0060, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(      0x00b0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0030, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x00d0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(      0x0050, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(      0x0090, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0300, 0x0300, DEF_STR( "Lives") );
		PORT_DIPSETTING(      0x0000, "6" );
		PORT_DIPSETTING(      0x0100, "4" );
		PORT_DIPSETTING(      0x0300, "3" );
		PORT_DIPSETTING(      0x0200, "2" );
		PORT_DIPNAME( 0x0400, 0x0400, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000, 0x0000, "Allow Continue" );
		PORT_DIPSETTING(      0x2000, DEF_STR( "No") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
	
		DCON_PLAYERS_CONTROLS
	
		DCON_SYSTEM
	INPUT_PORTS_END(); }}; 
	
	
	/******************************************************************************/
	
	static GfxLayout dcon_charlayout = new GfxLayout
	(
		8,8,		/* 8*8 characters */
		RGN_FRAC(1,2),
		4,			/* 4 bits per pixel */
		new int[] { 0,4,(0x10000*8)+0,0x10000*8+4,  },
		new int[] { 3,2,1,0, 11,10,9,8 ,8,9,10,11,0,1,2,3, },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		128
	);
	
	static GfxLayout dcon_tilelayout = new GfxLayout
	(
		16,16,	/* 16*16 tiles */
		RGN_FRAC(1,1),
		4,		/* 4 bits per pixel */
		new int[] { 8, 12, 0,4 },
		new int[] {
			3,2,1,0,19,18,17,16,
			512+3,512+2,512+1,512+0,
			512+11+8,512+10+8,512+9+8,512+8+8,
		},
		new int[] {
			0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
			8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32,
		},
		1024
	);
	
	static GfxDecodeInfo dcon_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, dcon_charlayout,    1024+768, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, dcon_tilelayout,    1024+0,   16 ),
		new GfxDecodeInfo( REGION_GFX3, 0, dcon_tilelayout,    1024+512, 16 ),
		new GfxDecodeInfo( REGION_GFX4, 0, dcon_tilelayout,    1024+256, 16 ),
		new GfxDecodeInfo( REGION_GFX5, 0, dcon_tilelayout,           0, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/******************************************************************************/
	
	/* Parameters: YM3812 frequency, Oki frequency, Oki memory region */
	SEIBU_SOUND_SYSTEM_YM3812_HARDWARE(4000000,8000,REGION_SOUND1);
	SEIBU_SOUND_SYSTEM_YM2151_HARDWARE(14318180/4,8000,REGION_SOUND1);
	
	static MACHINE_DRIVER_START( dcon )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq4_line_hold,1)
	
		SEIBU_SOUND_SYSTEM_CPU(4000000) /* Perhaps 14318180/4? */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(seibu_sound_1)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(40*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 40*8-1, 0*8, 28*8-1)
		MDRV_GFXDECODE(dcon_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(dcon)
		MDRV_VIDEO_UPDATE(dcon)
	
		/* sound hardware */
		SEIBU_SOUND_SYSTEM_YM3812_INTERFACE
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( sdgndmps )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq4_line_hold,1)
	
		SEIBU2_SOUND_SYSTEM_CPU(14318180/4)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(seibu_sound_1)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(40*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 40*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(dcon_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(dcon)
		MDRV_VIDEO_UPDATE(sdgndmps)
	
		/* sound hardware */
		SEIBU_SOUND_SYSTEM_YM2151_INTERFACE
	MACHINE_DRIVER_END
	
	/***************************************************************************/
	
	static RomLoadPtr rom_dcon = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE("p0-0",   0x000000, 0x20000, 0xa767ec15 );
		ROM_LOAD16_BYTE("p0-1",   0x000001, 0x20000, 0xa7efa091 );
		ROM_LOAD16_BYTE("p1-0",   0x040000, 0x20000, 0x3ec1ef7d );
		ROM_LOAD16_BYTE("p1-1",   0x040001, 0x20000, 0x4b8de320 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 ); /* 64k code for sound Z80 */
		ROM_LOAD( "fm",           0x000000, 0x08000, 0x50450faa );
		ROM_CONTINUE(             0x010000, 0x08000 );
		ROM_COPY( REGION_CPU2, 0, 0x018000, 0x08000 );
	
		ROM_REGION( 0x020000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "fix0",  0x000000, 0x10000, 0xab30061f );/* chars */
		ROM_LOAD( "fix1",  0x010000, 0x10000, 0xa0582115 );
	
		ROM_REGION( 0x080000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "bg1",   0x000000, 0x80000, 0xeac43283 );/* tiles */
	
		ROM_REGION( 0x080000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "bg3",   0x000000, 0x80000, 0x1408a1e0 );/* tiles */
	
		ROM_REGION( 0x080000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "bg2",   0x000000, 0x80000, 0x01864eb6 );/* tiles */
	
		ROM_REGION( 0x200000, REGION_GFX5, ROMREGION_DISPOSE );
		ROM_LOAD( "obj0",  0x000000, 0x80000, 0xc3af37db );/* sprites */
		ROM_LOAD( "obj1",  0x080000, 0x80000, 0xbe1f53ba );
		ROM_LOAD( "obj2",  0x100000, 0x80000, 0x24e0b51c );
		ROM_LOAD( "obj3",  0x180000, 0x80000, 0x5274f02d );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 ); /* ADPCM samples */
		ROM_LOAD( "pcm", 0x000000, 0x20000, 0xd2133b85 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_sdgndmps = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );/* 68000 code */
		ROM_LOAD16_BYTE( "911-a01.25",   0x00000, 0x20000, 0x3362915d );
		ROM_LOAD16_BYTE( "911-a02.29",   0x00001, 0x20000, 0xfbc78285 );
		ROM_LOAD16_BYTE( "911-a03.27",   0x40000, 0x20000, 0x6c24b4f2 );
		ROM_LOAD16_BYTE( "911-a04.28",   0x40001, 0x20000, 0x6ff9d716 );
	
		ROM_REGION( 0x20000*2, REGION_CPU2, 0 );/* Z80 code, banked data */
		ROM_LOAD( "911-a05.010",   0x00000, 0x08000, 0x90455406 );
		ROM_CONTINUE(              0x10000, 0x08000 );
	
		ROM_REGION( 0x020000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "911-a08.66",   0x000000, 0x10000, 0xe7e04823 );/* chars */
		ROM_LOAD( "911-a07.73",   0x010000, 0x10000, 0x6f40d4a9 );
	
		ROM_REGION( 0x080000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "911-a12.63",   0x000000, 0x080000, 0x8976bbb6 );
	
		ROM_REGION( 0x080000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "911-a11.65",   0x000000, 0x080000, 0x3f3b7810 );
	
		ROM_REGION( 0x100000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "911-a13.64",   0x000000, 0x100000, 0xf38a584a );
	
		ROM_REGION( 0x200000, REGION_GFX5, ROMREGION_DISPOSE );
		ROM_LOAD( "911-a10.73",   0x000000, 0x100000, 0x80e341fb );/* sprites */
		ROM_LOAD( "911-a09.74",   0x100000, 0x100000, 0x98f34519 );
	
		ROM_REGION( 0x040000, REGION_SOUND1, 0 );/* ADPCM samples */
		ROM_LOAD( "911-a06.97",   0x00000, 0x40000, 0x12c79440 );
	
		ROM_REGION( 512, REGION_PROMS, 0 );
		ROM_LOAD( "bnd-007.88",   0x00000, 512, 0x96f7646e );/* Priority */
	ROM_END(); }}; 
	
	/***************************************************************************/
	
	public static GameDriver driver_sdgndmps	   = new GameDriver("1991"	,"sdgndmps"	,"dcon.java"	,rom_sdgndmps,null	,machine_driver_sdgndmps	,input_ports_sdgndmps	,null	,ROT0	,	"Banpresto / Bandai", "SD Gundam Psycho Salamander no Kyoui", GAME_NO_COCKTAIL | GAME_NO_SOUND )
	public static GameDriver driver_dcon	   = new GameDriver("1992"	,"dcon"	,"dcon.java"	,rom_dcon,null	,machine_driver_dcon	,input_ports_dcon	,null	,ROT0	,	"Success",            "D-Con", GAME_NO_COCKTAIL )
}
