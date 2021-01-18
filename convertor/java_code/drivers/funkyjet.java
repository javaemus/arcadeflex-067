/***************************************************************************

  Funky Jet                               (c) 1992 Mitchell Corporation
  Sotsugyo Shousho	                      (c) 1995 Mitchell Corporation

  But actually a Data East pcb...  Hardware is pretty close to Super Burger
  Time but with a different graphics chip.

  Emulation by Bryan McPhail, mish@tendril.co.uk


Stephh's notes :

0) 'sotsugyo'

  - COIN2 doesn't work due to code at 0x0001f0 :

        0001F0: 1228 004A                move.b  ($4a,A0), D1

    It should be (as code in 0x00019e) :

        0001F0: 3228 004A                move.w  ($4a,A0), D1

  - SERVICE1 is very strange : instead of adding 1 credit, EACH time it is
    pressed, n credits are added depending on the "Coinage A" Dip Switch :

        Coin_A    n

         3C_1C    1
         2C_1C    1
         1C_1C    1
         1C_2C    2
         1C_3C    3
         1C_4C    4
         1C_5C    5
         1C_6C    6

  - When the "Unused" Dip Switch is ON, the palette is modified.


***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class funkyjet
{
	
	
	VIDEO_START( funkyjet );
	VIDEO_UPDATE( funkyjet );
	
	/******************************************************************************/
	
	static MEMORY_READ16_START( funkyjet_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x120000, 0x1207ff, MRA16_RAM },
		{ 0x140000, 0x143fff, MRA16_RAM },
		{ 0x160000, 0x1607ff, MRA16_RAM },
		{ 0x180000, 0x1807ff, deco16_146_funkyjet_prot_r },
		{ 0x320000, 0x321fff, MRA16_RAM },
		{ 0x322000, 0x323fff, MRA16_RAM },
		{ 0x340000, 0x340bff, MRA16_RAM },
		{ 0x342000, 0x342bff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( funkyjet_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x120000, 0x1207ff, paletteram16_xxxxBBBBGGGGRRRR_word_w, &paletteram16 },
		{ 0x140000, 0x143fff, MWA16_RAM },
		{ 0x160000, 0x1607ff, MWA16_RAM, &spriteram16 },
		{ 0x180000, 0x1807ff, deco16_146_funkyjet_prot_w, &deco16_prot_ram },
		{ 0x184000, 0x184001, MWA16_NOP },
		{ 0x188000, 0x188001, MWA16_NOP },
		{ 0x300000, 0x30000f, MWA16_RAM, &deco16_pf12_control },
		{ 0x320000, 0x321fff, deco16_pf1_data_w, &deco16_pf1_data },
		{ 0x322000, 0x323fff, deco16_pf2_data_w, &deco16_pf2_data },
		{ 0x340000, 0x340bff, MWA16_RAM, &deco16_pf1_rowscroll },
		{ 0x342000, 0x342bff, MWA16_RAM, &deco16_pf2_rowscroll },
	MEMORY_END
	
	/******************************************************************************/
	
	/* Physical memory map (21 bits) */
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x000000, 0x00ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x100000, 0x100001, MRA_NOP ),
		new Memory_ReadAddress( 0x110000, 0x110001, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0x120000, 0x120001, OKIM6295_status_0_r ),
		new Memory_ReadAddress( 0x130000, 0x130001, MRA_NOP ), /* This board only has 1 oki chip */
		new Memory_ReadAddress( 0x140000, 0x140001, soundlatch_r ),
		new Memory_ReadAddress( 0x1f0000, 0x1f1fff, MRA_BANK8 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x000000, 0x00ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x100000, 0x100001, MWA_NOP ), /* YM2203 - this board doesn't have one */
		new Memory_WriteAddress( 0x110000, 0x110001, YM2151_word_0_w ),
		new Memory_WriteAddress( 0x120000, 0x120001, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0x130000, 0x130001, MWA_NOP ),
		new Memory_WriteAddress( 0x1f0000, 0x1f1fff, MWA_BANK8 ),
		new Memory_WriteAddress( 0x1fec00, 0x1fec01, H6280_timer_w ),
		new Memory_WriteAddress( 0x1ff402, 0x1ff403, H6280_irq_status_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/******************************************************************************/
	
	static InputPortPtr input_ports_funkyjet = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* Player 1 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );/* Button 3 only in "test mode" */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* Player 2 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );/* Button 3 only in "test mode" */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 	/* System Inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_VBLANK );
	
		/* Dips seem inverted with respect to other Deco games */
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x1c, 0x1c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x1c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x14, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x01, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x80, "1" );
		PORT_DIPSETTING(    0xc0, "2" );
		PORT_DIPSETTING(    0x40, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x10, "Easy" );
		PORT_DIPSETTING(    0x30, "Normal" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x08, 0x08, "Freeze" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Yes") );
	  	PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_sotsugyo = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* Player 1 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );/* only in "test mode" */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* Player 2 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );/* only in "test mode" */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 	/* System Inputs */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );	// Not working - see notes
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );	// See notes
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_VBLANK );
	
		/* Dips seem inverted with respect to other Deco games */
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x1c, 0x1c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x1c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x14, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x01, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x80, 0x80, "Freeze" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x20, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x30, "Easy" );
		PORT_DIPSETTING(    0x20, "Normal" );
		PORT_DIPSETTING(    0x10, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x04, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unused") );		// See notes
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	/******************************************************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,2),
		4,
		new int[] { RGN_FRAC(1,2)+8, RGN_FRAC(1,2)+0, 8, 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,2),
		4,
		new int[] { RGN_FRAC(1,2)+8, RGN_FRAC(1,2)+0, 8, 0 },
		new int[] { 32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
				0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		64*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,  256, 32 ),	/* Characters 8x8 */
		new GfxDecodeInfo( REGION_GFX1, 0, tile_layout, 256, 32 ),	/* Tiles 16x16 */
		new GfxDecodeInfo( REGION_GFX2, 0, tile_layout,   0, 16 ),	/* Sprites 16x16 */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/******************************************************************************/
	
	static struct OKIM6295interface okim6295_interface =
	{
		1,          /* 1 chip */
		{ 7757 },	/* Frequency */
		{ REGION_SOUND1 },      /* memory region */
		{ 50 }
	};
	
	static void sound_irq(int state)
	{
		cpu_set_irq_line(1,1,state); /* IRQ 2 */
	}
	
	static struct YM2151interface ym2151_interface =
	{
		1,
		32220000/9,
		{ YM3012_VOL(45,MIXER_PAN_LEFT,45,MIXER_PAN_RIGHT) },
		{ sound_irq }
	};
	
	static MACHINE_DRIVER_START( funkyjet )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 14000000) /* 28 MHz crystal */
		MDRV_CPU_MEMORY(funkyjet_readmem,funkyjet_writemem)
		MDRV_CPU_VBLANK_INT(irq6_line_hold,1)
	
		MDRV_CPU_ADD(H6280,32220000/8)	/* Custom chip 45, Audio section crystal is 32.220 MHz */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(529)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(40*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 40*8-1, 1*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(funkyjet)
		MDRV_VIDEO_UPDATE(funkyjet)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	/******************************************************************************/
	
	static RomLoadPtr rom_funkyjet = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );/* 68000 code */
		ROM_LOAD16_BYTE( "jk00.12f", 0x00000, 0x40000, 0x712089c1 );
		ROM_LOAD16_BYTE( "jk01.13f", 0x00001, 0x40000, 0xbe3920d7 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Sound CPU */
		ROM_LOAD( "jk02.16f",    0x00000, 0x10000, 0x748c0bd8 );
	
		ROM_REGION( 0x080000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mat02", 0x000000, 0x80000, 0xe4b94c7e );/* Encrypted chars */
	
		ROM_REGION( 0x100000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "mat01", 0x000000, 0x80000, 0x24093a8d );/* sprites */
	  	ROM_LOAD( "mat00", 0x080000, 0x80000, 0xfbda0228 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* ADPCM samples */
	  	ROM_LOAD( "jk03.15h",    0x00000, 0x20000, 0x69a0eaf7 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_sotsugyo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );/* 68000 code */
		ROM_LOAD16_BYTE( "03.12f", 0x00000, 0x40000, 0xd175dfd1 );
		ROM_LOAD16_BYTE( "04.13f", 0x00001, 0x40000, 0x2072477c );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Sound CPU */
		ROM_LOAD( "sb020.16f",    0x00000, 0x10000, 0xbaf5ec93 );
	
		ROM_REGION( 0x080000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "02.2f", 0x000000, 0x80000, 0x337b1451 );/* chars */
	
		ROM_REGION( 0x100000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "01.4a", 0x000000, 0x80000, 0xfa10dd54 );/* sprites */
	  	ROM_LOAD( "00.2a", 0x080000, 0x80000, 0xd35a14ef );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* ADPCM samples */
	  	ROM_LOAD( "sb030.15h",    0x00000, 0x20000, 0x1ea43f48 );
	ROM_END(); }}; 
	
	static DRIVER_INIT( funkyjet )
	{
		deco74_decrypt(REGION_GFX1);
	}
	
	/******************************************************************************/
	
	public static GameDriver driver_funkyjet	   = new GameDriver("1992"	,"funkyjet"	,"funkyjet.java"	,rom_funkyjet,null	,machine_driver_funkyjet	,input_ports_funkyjet	,init_funkyjet	,ROT0	,	"[Data East] (Mitchell license)", "Funky Jet" )
	public static GameDriver driver_sotsugyo	   = new GameDriver("1995"	,"sotsugyo"	,"funkyjet.java"	,rom_sotsugyo,null	,machine_driver_funkyjet	,input_ports_sotsugyo	,init_funkyjet	,ROT0	,	"Mitchell Corporation (Atlus license)", "Sotsugyo Shousho" )
}
