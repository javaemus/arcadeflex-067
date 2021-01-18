/***************************************************************************

Atari Triple Hunt Driver

  Calibrate controls in service mode the first time you run this game.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class triplhnt
{
	
	extern VIDEO_START( triplhnt );
	extern VIDEO_UPDATE( triplhnt );
	
	extern UINT8* triplhnt_playfield_ram;
	extern UINT8* triplhnt_vpos_ram;
	extern UINT8* triplhnt_hpos_ram;
	extern UINT8* triplhnt_code_ram;
	extern UINT8* triplhnt_orga_ram;
	
	
	static UINT8 triplhnt_cmos[16];
	static UINT8 triplhnt_da_latch;
	static UINT8 triplhnt_misc_flags;
	static UINT8 triplhnt_cmos_latch;
	static UINT8 triplhnt_hit_code;
	
	
	static DRIVER_INIT( triplhnt )
	{
		generic_nvram = triplhnt_cmos;
		generic_nvram_size = sizeof triplhnt_cmos;
	}
	
	
	void triplhnt_hit_callback(int code)
	{
		triplhnt_hit_code = code;
	
		cpu_set_irq_line(0, 0, HOLD_LINE);
	}
	
	
	static void triplhnt_update_misc(int offset)
	{
		UINT8 bit = offset >> 1;
	
		/* BIT0 => UNUSED      */
		/* BIT1 => LAMP        */
		/* BIT2 => SCREECH     */
		/* BIT3 => LOCKOUT     */
		/* BIT4 => SPRITE ZOOM */
		/* BIT5 => CMOS WRITE  */
		/* BIT6 => TAPE CTRL   */
		/* BIT7 => SPRITE BANK */
	
		if (offset & 1)
		{
			triplhnt_misc_flags |= 1 << bit;
	
			if (bit == 5)
			{
				triplhnt_cmos[triplhnt_cmos_latch] = triplhnt_da_latch;
			}
		}
		else
		{
			triplhnt_misc_flags &= ~(1 << bit);
		}
	
		triplhnt_sprite_zoom = (triplhnt_misc_flags >> 4) & 1;
		triplhnt_sprite_bank = (triplhnt_misc_flags >> 7) & 1;
	
		set_led_status(0, triplhnt_misc_flags & 0x02);
	
		coin_lockout_w(0, !(triplhnt_misc_flags & 0x08));
		coin_lockout_w(1, !(triplhnt_misc_flags & 0x08));
	}
	
	
	public static WriteHandlerPtr triplhnt_misc_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		triplhnt_update_misc(offset);
	} };
	
	
	public static WriteHandlerPtr triplhnt_zeropage_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		memory_region(REGION_CPU1)[offset & 0xff] = data;
	} };
	
	
	public static ReadHandlerPtr triplhnt_zeropage_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return memory_region(REGION_CPU1)[offset & 0xff];
	} };
	
	
	public static ReadHandlerPtr triplhnt_cmos_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		triplhnt_cmos_latch = offset;
	
		return triplhnt_cmos[triplhnt_cmos_latch] ^ 15;
	} };
	
	
	public static ReadHandlerPtr triplhnt_input_port_4_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		watchdog_reset_w(0, 0);
	
		return readinputport(4);
	} };
	
	
	public static ReadHandlerPtr triplhnt_misc_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		triplhnt_update_misc(offset);
	
		return readinputport(7) | triplhnt_hit_code;
	} };
	
	
	public static ReadHandlerPtr triplhnt_da_latch_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int cross_x = readinputport(8);
		int cross_y = readinputport(9);
	
		triplhnt_da_latch = offset;
	
		/* the following is a slight simplification */
	
		return (offset & 1) ? cross_x : cross_y;
	} };
	
	
	public static Memory_ReadAddress triplhnt_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x00ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0100, 0x03ff, triplhnt_zeropage_r ),
		new Memory_ReadAddress( 0x0c00, 0x0c00, input_port_0_r ),
		new Memory_ReadAddress( 0x0c08, 0x0c08, input_port_1_r ),
		new Memory_ReadAddress( 0x0c09, 0x0c09, input_port_2_r ),
		new Memory_ReadAddress( 0x0c0a, 0x0c0a, input_port_3_r ),
		new Memory_ReadAddress( 0x0c0b, 0x0c0b, triplhnt_input_port_4_r ),
		new Memory_ReadAddress( 0x0c10, 0x0c1f, triplhnt_da_latch_r ),
		new Memory_ReadAddress( 0x0c20, 0x0c2f, triplhnt_cmos_r ),
		new Memory_ReadAddress( 0x0c30, 0x0c3f, triplhnt_misc_r ),
		new Memory_ReadAddress( 0x0c40, 0x0c40, input_port_5_r ),
		new Memory_ReadAddress( 0x0c48, 0x0c48, input_port_6_r ),
		new Memory_ReadAddress( 0x7000, 0x7fff, MRA_ROM ), /* program */
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_ROM ), /* program mirror */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress triplhnt_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x00ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0100, 0x03ff, triplhnt_zeropage_w ),
		new Memory_WriteAddress( 0x0400, 0x04ff, MWA_RAM, triplhnt_playfield_ram ),
		new Memory_WriteAddress( 0x0800, 0x080f, MWA_RAM, triplhnt_vpos_ram ),
		new Memory_WriteAddress( 0x0810, 0x081f, MWA_RAM, triplhnt_hpos_ram ),
		new Memory_WriteAddress( 0x0820, 0x082f, MWA_RAM, triplhnt_orga_ram ),
		new Memory_WriteAddress( 0x0830, 0x083f, MWA_RAM, triplhnt_code_ram ),
		new Memory_WriteAddress( 0x0c30, 0x0c3f, triplhnt_misc_w ),
		new Memory_WriteAddress( 0x7000, 0x7fff, MWA_ROM ), /* program */
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_ROM ), /* program mirror */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_triplhnt = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* 0C00 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 );
	
		PORT_START();  /* 0C08 */
		PORT_DIPNAME( 0xc0, 0x00, "Play Time" );
		PORT_DIPSETTING( 0x00, "32 seconds / 16 raccoons" );
		PORT_DIPSETTING( 0x40, "64 seconds / 32 raccoons" );
		PORT_DIPSETTING( 0x80, "96 seconds / 48 raccoons" );
		PORT_DIPSETTING( 0xc0, "128 seconds / 64 raccoons" );
	
		PORT_START();  /* 0C09 */
		PORT_DIPNAME( 0xc0, 0x40, "Game Select" );
		PORT_DIPSETTING( 0x00, "Hit the Bear" );
		PORT_DIPSETTING( 0x40, "Witch Hunt" );
		PORT_DIPSETTING( 0xc0, "Raccoon Hunt" );
	
		PORT_START();  /* 0C0A */
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING( 0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING( 0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING( 0x80, DEF_STR( "1C_2C") );
	
		PORT_START();  /* 0C0B */
		PORT_DIPNAME( 0x80, 0x00, "Extended Play" );
		PORT_DIPSETTING( 0x80, DEF_STR( "Off") );
		PORT_DIPSETTING( 0x00, DEF_STR( "On") );
	
		PORT_START();  /* 0C40 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN2 );
	
		PORT_START();  /* 0C48 */
	// default to service enabled to make users calibrate gun
	//	PORT_SERVICE( 0x40, IP_ACTIVE_LOW );
		PORT_BITX(    0x40, 0x00, IPT_DIPSWITCH_NAME | IPF_TOGGLE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
		PORT_START(); 
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_VBLANK );
	
		PORT_START(); 
		PORT_ANALOG( 0xff, 0x80, IPT_LIGHTGUN_X, 25, 15, 0x00, 0xff);
	
		PORT_START(); 
		PORT_ANALOG( 0xff, 0x78, IPT_LIGHTGUN_Y, 25, 15, 0x00, 0xef);
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout triplhnt_small_sprite_layout = new GfxLayout
	(
		32, 32,   /* width, height */
		16,       /* total         */
		2,        /* planes        */
		          /* plane offsets */
		new int[] { 0x0000, 0x4000 },
		new int[] {
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
			0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
			0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
			0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F
		},
		new int[] {
			0x000, 0x020, 0x040, 0x060, 0x080, 0x0A0, 0x0C0, 0x0E0,
			0x100, 0x120, 0x140, 0x160, 0x180, 0x1A0, 0x1C0, 0x1E0,
			0x200, 0x220, 0x240, 0x260, 0x280, 0x2A0, 0x2C0, 0x2E0,
			0x300, 0x320, 0x340, 0x360, 0x380, 0x3A0, 0x3C0, 0x3E0
		},
		0x400     /* increment */
	);
	
	
	static GfxLayout triplhnt_large_sprite_layout = new GfxLayout
	(
		64, 64,   /* width, height */
		16,       /* total         */
		2,        /* planes        */
		          /* plane offsets */
		new int[] { 0x0000, 0x4000 },
		new int[] {
			0x00, 0x00, 0x01, 0x01, 0x02, 0x02, 0x03, 0x03,
			0x04, 0x04, 0x05, 0x05, 0x06, 0x06, 0x07, 0x07,
			0x08, 0x08, 0x09, 0x09, 0x0A, 0x0A, 0x0B, 0x0B,
			0x0C, 0x0C, 0x0D, 0x0D, 0x0E, 0x0E, 0x0F, 0x0F,
			0x10, 0x10, 0x11, 0x11, 0x12, 0x12, 0x13, 0x13,
			0x14, 0x14, 0x15, 0x15, 0x16, 0x16, 0x17, 0x17,
			0x18, 0x18, 0x19, 0x19, 0x1A, 0x1A, 0x1B, 0x1B,
			0x1C, 0x1C, 0x1D, 0x1D, 0x1E, 0x1E, 0x1F, 0x1F
		},
		new int[] {
			0x000, 0x000, 0x020, 0x020, 0x040, 0x040, 0x060, 0x060,
			0x080, 0x080, 0x0A0, 0x0A0, 0x0C0, 0x0C0, 0x0E0, 0x0E0,
			0x100, 0x100, 0x120, 0x120, 0x140, 0x140, 0x160, 0x160,
			0x180, 0x180, 0x1A0, 0x1A0, 0x1C0, 0x1C0, 0x1E0, 0x1E0,
			0x200, 0x200, 0x220, 0x220, 0x240, 0x240, 0x260, 0x260,
			0x280, 0x280, 0x2A0, 0x2A0, 0x2C0, 0x2C0, 0x2E0, 0x2E0,
			0x300, 0x300, 0x320, 0x320, 0x340, 0x340, 0x360, 0x360,
			0x380, 0x380, 0x3A0, 0x3A0, 0x3C0, 0x3C0, 0x3E0, 0x3E0
		},
		0x400     /* increment */
	);
	
	
	static GfxLayout triplhnt_tile_layout = new GfxLayout
	(
		16, 16,   /* width, height */
		64,       /* total         */
		1,        /* planes        */
		new int[] { 0 },    /* plane offsets */
		new int[] {
			0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7
		},
		new int[] {
			0x00, 0x00, 0x08, 0x08, 0x10, 0x10, 0x18, 0x18,
			0x20, 0x20, 0x28, 0x28, 0x30, 0x30, 0x38, 0x38
		},
		0x40      /* increment */
	);
	
	
	static GfxDecodeInfo triplhnt_gfx_decode_info[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, triplhnt_small_sprite_layout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX1, 0, triplhnt_large_sprite_layout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX2, 0, triplhnt_tile_layout, 4, 2 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static PALETTE_INIT( triplhnt )
	{
		palette_set_color(0, 0xAF, 0xAF, 0xAF);  /* sprites */
		palette_set_color(1, 0x00, 0x00, 0x00);
		palette_set_color(2, 0xFF, 0xFF, 0xFF);
		palette_set_color(3, 0x50, 0x50, 0x50);
		palette_set_color(4, 0x00, 0x00, 0x00);  /* tiles */
		palette_set_color(5, 0x3F, 0x3F, 0x3F);
		palette_set_color(6, 0x00, 0x00, 0x00);
		palette_set_color(7, 0x3F, 0x3F, 0x3F);
	}
	
	
	static MACHINE_DRIVER_START( triplhnt )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6800, 800000)
		MDRV_CPU_MEMORY(triplhnt_readmem, triplhnt_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold, 1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION((int) ((22. * 1000000) / (262. * 60) + 0.5))
	
		MDRV_NVRAM_HANDLER(generic_0fill)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 262)
		MDRV_VISIBLE_AREA(0, 255, 0, 239)
		MDRV_GFXDECODE(triplhnt_gfx_decode_info)
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(triplhnt)
		MDRV_VIDEO_START(triplhnt)
		MDRV_VIDEO_UPDATE(triplhnt)
	
		/* sound hardware */
	MACHINE_DRIVER_END
	
	
	static RomLoadPtr rom_triplhnt = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD_NIB_HIGH( "8404.f1", 0x7000, 0x400, 0xabc8acd5 );
		ROM_LOAD_NIB_LOW ( "8408.f2", 0x7000, 0x400, 0x77fcdd3f );
		ROM_LOAD_NIB_HIGH( "8403.e1", 0x7400, 0x400, 0x8d756fa1 );
		ROM_LOAD_NIB_LOW ( "8407.e2", 0x7400, 0x400, 0xde268f4b );
		ROM_LOAD_NIB_HIGH( "8402.d1", 0x7800, 0x400, 0xeb75c936 );
		ROM_LOAD_NIB_LOW ( "8406.d2", 0x7800, 0x400, 0xe7ab1186 );
		ROM_LOAD_NIB_HIGH( "8401.c1", 0x7C00, 0x400, 0x7461b05e );
		ROM_RELOAD(                   0xFC00, 0x400 );
		ROM_LOAD_NIB_LOW ( "8405.c2", 0x7C00, 0x400, 0xba370b97 );
		ROM_RELOAD(                   0xFC00, 0x400 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE ); /* sprites */
		ROM_LOAD( "8423.n1", 0x0000, 0x800, 0x9937d0da );
		ROM_LOAD( "8422.r1", 0x0800, 0x800, 0x803621dd );
	
		ROM_REGION( 0x200, REGION_GFX2, ROMREGION_DISPOSE );  /* tiles */
		ROM_LOAD_NIB_HIGH( "8409.l3", 0x0000, 0x200, 0xec304172 );
		ROM_LOAD_NIB_LOW ( "8410.m3", 0x0000, 0x200, 0xf75a1b08 );
	ROM_END(); }}; 
	
	
	public static GameDriver driver_triplhnt	   = new GameDriver("1977"	,"triplhnt"	,"triplhnt.java"	,rom_triplhnt,null	,machine_driver_triplhnt	,input_ports_triplhnt	,init_triplhnt	,0	,	"Atari", "Triple Hunt", GAME_NO_SOUND )
}
