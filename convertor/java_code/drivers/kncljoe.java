/***************************************************************************

Knuckle Joe - (c) 1985 Seibu Kaihatsu ( Taito License )

driver by Ernesto Corvi

Notes:
This board seems to be an Irem design.
The sound hardware is definitely the 6803-based one used by the classic Irem
games, and the video hardware is pretty much like Irem games too. The only
strange thing is that the screen is flipped vertically.

TODO:
- you can't play anymore after you die
- lots of unknown dipswitches

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class kncljoe
{
	
	
	/* from vidhrdw */
	VIDEO_START( kncljoe );
	PALETTE_INIT( kncljoe );
	VIDEO_UPDATE( kncljoe );
	WRITE_HANDLER(kncljoe_videoram_w);
	WRITE_HANDLER(kncljoe_control_w);
	WRITE_HANDLER(kncljoe_scroll_w);
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xcfff, videoram_r ),		/* videoram */
		new Memory_ReadAddress( 0xd800, 0xd800, input_port_0_r ), /* IN 0 */
		new Memory_ReadAddress( 0xd801, 0xd801, input_port_1_r ), /* IN 1 */
		new Memory_ReadAddress( 0xd802, 0xd802, input_port_2_r ), /* IN 2 */
		new Memory_ReadAddress( 0xd803, 0xd803, input_port_3_r ),	/* DSW A */
		new Memory_ReadAddress( 0xd804, 0xd804, input_port_4_r ),	/* DSW B */
		new Memory_ReadAddress( 0xe800, 0xefff, MRA_RAM ),		/* spriteram */
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xcfff, kncljoe_videoram_w, videoram ),
		new Memory_WriteAddress( 0xd000, 0xd000, kncljoe_scroll_w ),
		new Memory_WriteAddress( 0xd800, 0xd800, irem_sound_cmd_w ),
		new Memory_WriteAddress( 0xd801, 0xd801, kncljoe_control_w ),
		new Memory_WriteAddress( 0xe800, 0xefff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/******************************************************************************/
	
	static InputPortPtr input_ports_kncljoe = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_COIN1, 4 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_COIN2, 4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN 1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN 2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_5C") );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Infinite Energy", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Free_Play") );	// Not working due to code at 0x296f
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START();  /* DSW B */
		PORT_DIPNAME( 0x01, 0x01, "Unused SW B-0" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x18, "10k and every 20k" );
		PORT_DIPSETTING(    0x10, "20k and every 40k" );
		PORT_DIPSETTING(    0x08, "30k and every 60k" );
		PORT_DIPSETTING(    0x00, "40k and every 80k" );
		PORT_DIPNAME( 0x60, 0x60, "Difficulty?" );		// Stored at 0xf018
		PORT_DIPSETTING(    0x60, "Easy" );
		PORT_DIPSETTING(    0x40, "Medium" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3), RGN_FRAC(1,3), RGN_FRAC(0,3) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3), RGN_FRAC(1,3), RGN_FRAC(0,3) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,   0x00, 16 ),	/* colors 0x00-0x7f direct mapped */
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout, 0x80, 16 ),	/* colors 0x80-0x8f with lookup table */
		new GfxDecodeInfo( REGION_GFX3, 0, spritelayout, 0x80, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static MACHINE_DRIVER_START( kncljoe )
	
		/* basic machine hardware */
	//	MDRV_CPU_ADD(Z80, 4000000) /* ? MHz */
		MDRV_CPU_ADD(Z80, 6000000) /* with this value, the game doesn't hang ! */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 0*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(128+16)
		MDRV_COLORTABLE_LENGTH(16*8+16*8)
	
		MDRV_PALETTE_INIT(kncljoe)
		MDRV_VIDEO_START(kncljoe)
		MDRV_VIDEO_UPDATE(kncljoe)
	
		/* sound hardware */
		MDRV_IMPORT_FROM(irem_audio)
	MACHINE_DRIVER_END
	
	
	
	static RomLoadPtr rom_kncljoe = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ); /* 64k for code */
		ROM_LOAD( "kj-1.bin", 0x0000, 0x4000, 0x4e4f5ff2 );
		ROM_LOAD( "kj-2.bin", 0x4000, 0x4000, 0xcb11514b );
		ROM_LOAD( "kj-3.bin", 0x8000, 0x4000, 0x0f50697b );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ); /* 64k for audio code */
		ROM_LOAD( "kj-13.bin",0xe000, 0x2000, 0x0a0be3f5 );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );/* tiles */
		ROM_LOAD( "kj-10.bin", 0x0000,  0x4000, 0x74d3ba33 );
		ROM_LOAD( "kj-11.bin", 0x4000,  0x4000, 0x8ea01455 );
		ROM_LOAD( "kj-12.bin", 0x8000,  0x4000, 0x33367c41 );
	
		ROM_REGION( 0x18000, REGION_GFX2, ROMREGION_DISPOSE );/* sprites */
		ROM_LOAD( "kj-4.bin", 0x00000,  0x8000, 0xa499ea10 );
		ROM_LOAD( "kj-6.bin", 0x08000,  0x8000, 0x815f5c0a );
		ROM_LOAD( "kj-5.bin", 0x10000,  0x8000, 0x11111759 );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );/* sprites */
		ROM_LOAD( "kj-7.bin", 0x0000,   0x4000, 0x121fcccb );
		ROM_LOAD( "kj-9.bin", 0x4000,   0x4000, 0xaffbe3eb );
		ROM_LOAD( "kj-8.bin", 0x8000,   0x4000, 0xe057e72a );
	
		ROM_REGION( 0x420, REGION_PROMS, 0 );
		ROM_LOAD( "kjclr1.bin",  0x000, 0x100, 0xc3378ac2 );/* tile red */
		ROM_LOAD( "kjclr2.bin",  0x100, 0x100, 0x2126da97 );/* tile green */
		ROM_LOAD( "kjclr3.bin",  0x200, 0x100, 0xfde62164 );/* tile blue */
		ROM_LOAD( "kjprom5.bin", 0x300, 0x020, 0x5a81dd9f );/* sprite palette */
		ROM_LOAD( "kjprom4.bin", 0x320, 0x100, 0x48dc2066 );/* sprite clut */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_kncljoea = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ); /* 64k for code */
		ROM_LOAD( "kj01.bin", 0x0000, 0x4000, 0xf251019e );
		ROM_LOAD( "kj-2.bin", 0x4000, 0x4000, 0xcb11514b );
		ROM_LOAD( "kj-3.bin", 0x8000, 0x4000, 0x0f50697b );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ); /* 64k for audio code */
		ROM_LOAD( "kj-13.bin",0xe000, 0x2000, 0x0a0be3f5 );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );/* tiles */
		ROM_LOAD( "kj-10.bin", 0x0000,  0x4000, 0x74d3ba33 );
		ROM_LOAD( "kj-11.bin", 0x4000,  0x4000, 0x8ea01455 );
		ROM_LOAD( "kj-12.bin", 0x8000,  0x4000, 0x33367c41 );
	
		ROM_REGION( 0x18000, REGION_GFX2, ROMREGION_DISPOSE );/* sprites */
		ROM_LOAD( "kj-4.bin", 0x00000,  0x8000, 0xa499ea10 );
		ROM_LOAD( "kj-6.bin", 0x08000,  0x8000, 0x815f5c0a );
		ROM_LOAD( "kj-5.bin", 0x10000,  0x8000, 0x11111759 );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );/* sprites */
		ROM_LOAD( "kj-7.bin", 0x0000,   0x4000, 0x121fcccb );
		ROM_LOAD( "kj-9.bin", 0x4000,   0x4000, 0xaffbe3eb );
		ROM_LOAD( "kj-8.bin", 0x8000,   0x4000, 0xe057e72a );
	
		ROM_REGION( 0x420, REGION_PROMS, 0 );
		ROM_LOAD( "kjclr1.bin",  0x000, 0x100, 0xc3378ac2 );/* tile red */
		ROM_LOAD( "kjclr2.bin",  0x100, 0x100, 0x2126da97 );/* tile green */
		ROM_LOAD( "kjclr3.bin",  0x200, 0x100, 0xfde62164 );/* tile blue */
		ROM_LOAD( "kjprom5.bin", 0x300, 0x020, 0x5a81dd9f );/* sprite palette */
		ROM_LOAD( "kjprom4.bin", 0x320, 0x100, 0x48dc2066 );/* sprite clut */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_bcrusher = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ); /* 64k for code */
		ROM_LOAD( "bcrush1.bin", 0x0000, 0x4000, 0xe8979196 );
		ROM_LOAD( "bcrush2.bin", 0x4000, 0x4000, 0x1be4c731 );
		ROM_LOAD( "bcrush3.bin", 0x8000, 0x4000, 0x0772d993 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ); /* 64k for audio code */
		ROM_LOAD( "kj-13.bin",0xe000, 0x2000, 0x0a0be3f5 );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );/* tiles */
		ROM_LOAD( "bcrush10.bin", 0x0000,  0x4000, 0xa62f4572 );
		ROM_LOAD( "bcrush11.bin", 0x4000,  0x4000, 0x79cc5644 );
		ROM_LOAD( "bcrush12.bin", 0x8000,  0x4000, 0x8f09641d );
	
		ROM_REGION( 0x18000, REGION_GFX2, ROMREGION_DISPOSE );/* sprites */
		ROM_LOAD( "kj-4.bin", 0x00000,  0x8000, 0xa499ea10 );
		ROM_LOAD( "kj-6.bin", 0x08000,  0x8000, 0x815f5c0a );
		ROM_LOAD( "kj-5.bin", 0x10000,  0x8000, 0x11111759 );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );/* sprites */
		ROM_LOAD( "kj-7.bin", 0x0000,   0x4000, 0x121fcccb );
		ROM_LOAD( "kj-9.bin", 0x4000,   0x4000, 0xaffbe3eb );
		ROM_LOAD( "kj-8.bin", 0x8000,   0x4000, 0xe057e72a );
	
		ROM_REGION( 0x420, REGION_PROMS, 0 );
		ROM_LOAD( "kjclr1.bin",  0x000, 0x100, 0xc3378ac2 );/* tile red */
		ROM_LOAD( "kjclr2.bin",  0x100, 0x100, 0x2126da97 );/* tile green */
		ROM_LOAD( "kjclr3.bin",  0x200, 0x100, 0xfde62164 );/* tile blue */
		ROM_LOAD( "kjprom5.bin", 0x300, 0x020, 0x5a81dd9f );/* sprite palette */
		ROM_LOAD( "kjprom4.bin", 0x320, 0x100, 0x48dc2066 );/* sprite clut */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_kncljoe	   = new GameDriver("1985"	,"kncljoe"	,"kncljoe.java"	,rom_kncljoe,null	,machine_driver_kncljoe	,input_ports_kncljoe	,null	,ROT0	,	"[Seibu Kaihatsu] (Taito license)", "Knuckle Joe (set 1)" )
	public static GameDriver driver_kncljoea	   = new GameDriver("1985"	,"kncljoea"	,"kncljoe.java"	,rom_kncljoea,driver_kncljoe	,machine_driver_kncljoe	,input_ports_kncljoe	,null	,ROT0	,	"[Seibu Kaihatsu] (Taito license)", "Knuckle Joe (set 2)" )
	public static GameDriver driver_bcrusher	   = new GameDriver("1985"	,"bcrusher"	,"kncljoe.java"	,rom_bcrusher,driver_kncljoe	,machine_driver_kncljoe	,input_ports_kncljoe	,null	,ROT0	,	"bootleg",                          "Bone Crusher" )
}
