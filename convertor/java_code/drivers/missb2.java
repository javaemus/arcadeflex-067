/* Miss Bubble 2

A rather odd bootleg of Bubble Bobble with level select, redesigned levels,
redesigned (8bpp!) graphics and different sound hardware... Crazy

*/
/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class missb2
{
	
	/* vidhrdw/bublbobl.c */
	extern unsigned char *bublbobl_objectram;
	extern size_t bublbobl_objectram_size;
	VIDEO_UPDATE( bublbobl );
	
	/* machine/bublbobl.c */
	extern unsigned char *bublbobl_sharedram1,*bublbobl_sharedram2;
	INTERRUPT_GEN( bublbobl_m68705_interrupt );
	
	
	VIDEO_UPDATE( missb2 )
	{
		int offs;
		int sx,sy,xc,yc;
		int gfx_num,gfx_attr,gfx_offs;
		const UINT8 *prom_line;
	
	
		/* Bubble Bobble doesn't have a real video RAM. All graphics (characters */
		/* and sprites) are stored in the same memory region, and information on */
		/* the background character columns is stored in the area dd00-dd3f */
	
		/* This clears & redraws the entire screen each pass */
		fillbitmap(bitmap,Machine->pens[255],&Machine->visible_area);
	
		if (bublbobl_video_enable == 0) return;
	
		sx = 0;
	
		for (offs = 0;offs < bublbobl_objectram_size;offs += 4)
	    {
			/* skip empty sprites */
			/* this is dword aligned so the UINT32 * cast shouldn't give problems */
			/* on any architecture */
			if (*(UINT32 *)(&bublbobl_objectram[offs]) == 0)
				continue;
	
			gfx_num = bublbobl_objectram[offs + 1];
			gfx_attr = bublbobl_objectram[offs + 3];
			prom_line = memory_region(REGION_PROMS) + 0x80 + ((gfx_num & 0xe0) >> 1);
	
			gfx_offs = ((gfx_num & 0x1f) * 0x80);
			if ((gfx_num & 0xa0) == 0xa0)
				gfx_offs |= 0x1000;
	
			sy = -bublbobl_objectram[offs + 0];
	
			for (yc = 0;yc < 32;yc++)
			{
				if (prom_line[yc/2] & 0x08)	continue;	/* NEXT */
	
				if (!(prom_line[yc/2] & 0x04))	/* next column */
				{
					sx = bublbobl_objectram[offs + 2];
					if (gfx_attr & 0x40) sx -= 256;
				}
	
				for (xc = 0;xc < 2;xc++)
				{
					int goffs,code,color,flipx,flipy,x,y;
	
					goffs = gfx_offs + xc * 0x40 + (yc & 7) * 0x02 +
							(prom_line[yc/2] & 0x03) * 0x10;
					code = videoram.read(goffs)+ 256 * (videoram.read(goffs + 1)& 0x03) + 1024 * (gfx_attr & 0x0f);
					color = (videoram.read(goffs + 1)& 0x3c) >> 2;
					flipx = videoram.read(goffs + 1)& 0x40;
					flipy = videoram.read(goffs + 1)& 0x80;
					x = sx + xc * 8;
					y = (sy + yc * 8) & 0xff;
	
					if (flip_screen)
					{
						x = 248 - x;
						y = 248 - y;
						flipx = !flipx;
						flipy = !flipy;
					}
	
					drawgfx(bitmap,Machine->gfx[0],
							code,
							0,
							flipx,flipy,
							x,y,
							&Machine->visible_area,TRANSPARENCY_PEN,0xff);
				}
			}
	
			sx += 16;
		}
	}
	
	
	public static Memory_ReadAddress missb2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xf7ff, bublbobl_sharedram1_r ),
		new Memory_ReadAddress( 0xf800, 0xf9ff, paletteram_r ),
		new Memory_ReadAddress( 0xfc00, 0xfcff, bublbobl_sharedram2_r ),
		new Memory_ReadAddress( 0xff00, 0xff00, input_port_0_r ),
		new Memory_ReadAddress( 0xff01, 0xff01, input_port_1_r ),
		new Memory_ReadAddress( 0xff02, 0xff02, input_port_2_r ),
		new Memory_ReadAddress( 0xff03, 0xff03, input_port_3_r ),
		new Memory_ReadAddress( 0xfd00, 0xfdff, MRA_RAM ), /* ? */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress missb2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdcff, MWA_RAM, videoram, videoram_size ),
		new Memory_WriteAddress( 0xdd00, 0xdfff, MWA_RAM, bublbobl_objectram, bublbobl_objectram_size ),
		new Memory_WriteAddress( 0xe000, 0xf7ff, bublbobl_sharedram1_w, bublbobl_sharedram1 ),
		new Memory_WriteAddress( 0xf800, 0xf9ff, paletteram_RRRRGGGGBBBBxxxx_swap_w, paletteram ),
		new Memory_WriteAddress( 0xfa00, 0xfa00, bublbobl_sound_command_w ),
		new Memory_WriteAddress( 0xfa80, 0xfa80, MWA_NOP ),
		new Memory_WriteAddress( 0xfb00, 0xfb00, bublbobl_nmitrigger_w ),	/* not used by Bubble Bobble, only by Tokio */
		new Memory_WriteAddress( 0xfb40, 0xfb40, bublbobl_bankswitch_w ),
		new Memory_WriteAddress( 0xfc00, 0xfcff, bublbobl_sharedram2_w, bublbobl_sharedram2 ),
		new Memory_WriteAddress( 0xfd00, 0xfdff, MWA_RAM ), /* ? */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress missb2_readmem2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xae00, 0xafff, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xcfff, MRA_RAM ), /* main? */
		new Memory_ReadAddress( 0xe000, 0xf7ff, bublbobl_sharedram1_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress missb2_writemem2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc1ff, MWA_RAM ),
		new Memory_WriteAddress( 0xc800, 0xcfff, MWA_RAM ), /* main? */
	
		new Memory_WriteAddress( 0xd002, 0xd002, MWA_NOP ), /* ? */
	
		new Memory_WriteAddress( 0xe000, 0xf7ff, bublbobl_sharedram1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x8fff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x8fff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_missb2 = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x01, "Japanese" );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x02, "Easy" );
		PORT_DIPSETTING(    0x03, "Medium" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "20000 80000" );
		PORT_DIPSETTING(    0x0c, "30000 100000" );
		PORT_DIPSETTING(    0x04, "40000 200000" );
		PORT_DIPSETTING(    0x00, "50000 250000" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x10, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x30, "3" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_DIPNAME( 0xc0, 0x00, "Monster Speed" );
		PORT_DIPSETTING(    0x00, "Normal" );
		PORT_DIPSETTING(    0x40, "Medium" );
		PORT_DIPSETTING(    0x80, "High" );
		PORT_DIPSETTING(    0xc0, "Very High" );
	
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_TILT );/* ?????*/
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,4),
		8,
		new int[] { RGN_FRAC(0,4)+0, RGN_FRAC(0,4)+4, RGN_FRAC(1,4)+0, RGN_FRAC(1,4)+4, RGN_FRAC(2,4)+0, RGN_FRAC(2,4)+4, RGN_FRAC(3,4)+0, RGN_FRAC(3,4)+4 },
		new int[] { 3, 2, 1, 0, 8+3, 8+2, 8+1, 8+0 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x00000, charlayout, 0, 1 ),
		new GfxDecodeInfo( -1 )	/* end of array */
	};
	
	
	
	#define MAIN_XTAL 24000000
	
	
	static MACHINE_DRIVER_START( missb2 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, MAIN_XTAL/4)	/* 6 MHz */
		MDRV_CPU_MEMORY(missb2_readmem,missb2_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, MAIN_XTAL/4)	/* 6 MHz */
		MDRV_CPU_MEMORY(missb2_readmem2,missb2_writemem2)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, MAIN_XTAL/8)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
									/* IRQs are triggered by the YM2203 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(100)	/* 100 CPU slices per frame - an high value to ensure proper */
								/* synchronization of the CPUs */
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_UPDATE(missb2)
	
		/* sound hardware */
	//	MDRV_SOUND_ADD(YM2203, ym2203_interface)
	//	MDRV_SOUND_ADD(YM3526, ym3526_interface)
	MACHINE_DRIVER_END
	
	
	
	static RomLoadPtr rom_missb2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "msbub2-u.204", 0x00000, 0x10000, 0xb633bdde );/* FIRST AND SECOND HALF IDENTICAL */
		/* ROMs banked at 8000-bfff */
		ROM_LOAD( "msbub2-u.203", 0x10000, 0x10000, 0x29fd8afe );
		/* 20000-2ffff empty */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the second CPU */
		ROM_LOAD( "msbub2-u.11",  0x0000, 0x10000, 0x003dc092 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* 64k for the third CPU */
		ROM_LOAD( "msbub2-u.211", 0x0000, 0x08000, 0x08e5d846 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );
		ROM_LOAD( "msbub2-u.14",  0x00000, 0x40000, 0xb3164b47 );
		ROM_LOAD( "msbub2-u.126", 0x40000, 0x40000, 0xb0a9a353 );
		ROM_LOAD( "msbub2-u.124", 0x80000, 0x40000, 0x4b0d8e5b );
		ROM_LOAD( "msbub2-u.125", 0xc0000, 0x40000, 0x77b710e2 );
	
		ROM_REGION( 0x200000, REGION_GFX2, ROMREGION_INVERT );/* background images, probably, but format is unclear */
		ROM_LOAD16_BYTE( "msbub2-u.ic1", 0x000000, 0x80000, 0xd621cbc3 );
		ROM_LOAD16_BYTE( "msbub2-u.ic2", 0x100000, 0x80000, 0x694c2783 );
		ROM_LOAD16_BYTE( "msbub2-u.ic3", 0x000001, 0x80000, 0x90e56035 );
		ROM_LOAD16_BYTE( "msbub2-u.ic4", 0x100001, 0x80000, 0xbe71c9f0 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "msbub2-u.13", 0x00000, 0x20000, 0x14f07386 );
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 );
		ROM_LOAD( "a71-25.bin",  0x0000, 0x0100, 0x2d0f8545 );/* video timing - taken from bublbobl */
	ROM_END(); }}; 
	
	static DRIVER_INIT( missb2 )
	{
		unsigned char *ROM = memory_region(REGION_CPU1);
	
		/* in Bubble Bobble, bank 0 has code falling from 7fff to 8000, */
		/* so I have to copy it there because bank switching wouldn't catch it */
		memcpy(ROM+0x08000,ROM+0x10000,0x4000);
	
	}
	
	public static GameDriver driver_missb2	   = new GameDriver("1986"	,"missb2"	,"missb2.java"	,rom_missb2,driver_bublbobl	,machine_driver_missb2	,input_ports_missb2	,init_missb2	,ROT0	,	"bootleg", "Miss Bubble 2", GAME_IMPERFECT_GRAPHICS | GAME_NO_SOUND )
}
