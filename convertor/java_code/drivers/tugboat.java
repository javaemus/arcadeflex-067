/****************************************************************************

Tug Boat
6502 hooked up + preliminary video by MooglyGuy

TODO:
- controls stop working in stage 2
- verify connections of the two PIAs. I only hooked up a couple of ports but
  there are more.
- check how the score is displayed. I'm quite sure that tugboat_score_w is
  supposed to access videoram scanning it by columns (like btime_mirrorvideoram_w),
  but the current implementation is a big kludge, and it still looks wrong.
- colors might not be entirely accurate

****************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class tugboat
{
	
	
	data8_t *tugboat_ram,*tugboat_score;
	
	
	static UINT8 hd46505_0_reg[18],hd46505_1_reg[18];
	
	
	/*  there isn't the usual resistor array anywhere near the color prom,
	    just four 1k resistors. */
	PALETTE_INIT( tugboat )
	{
		int i;
	
	
		for (i = 0;i < Machine->drv->total_colors;i++)
		{
			int r,g,b,brt;
	
	
			brt = ((color_prom.read(i)>> 3) & 0x01) ? 0xff : 0x80;
	
			r = brt * ((color_prom.read(i)>> 0) & 0x01);
			g = brt * ((color_prom.read(i)>> 1) & 0x01);
			b = brt * ((color_prom.read(i)>> 2) & 0x01);
	
			palette_set_color(i,r,g,b);
		}
	}
	
	
	
	/* see crtc6845.c. That file is only a placeholder, I process the writes here
	   because I need the start_addr register to handle scrolling */
	public static WriteHandlerPtr tugboat_hd46505_0_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		static int reg;
		if (offset == 0) reg = data & 0x0f;
		else if (reg < 18) hd46505_0_reg[reg] = data;
	} };
	public static WriteHandlerPtr tugboat_hd46505_1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		static int reg;
		if (offset == 0) reg = data & 0x0f;
		else if (reg < 18) hd46505_1_reg[reg] = data;
	} };
	
	
	
	public static WriteHandlerPtr tugboat_score_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		tugboat_ram[0x291d + 32*offset] = data ^ 0x0f;	/* ???? */
	} };
	
	static void draw_tilemap(struct mame_bitmap *bitmap,const struct rectangle *cliprect,
			int addr,int gfx0,int gfx1,int transparency)
	{
		int x,y;
	
		for (y = 0;y < 32;y++)
		{
			for (x = 0;x < 32;x++)
			{
				int code = (tugboat_ram[addr + 0x400] << 8) | tugboat_ram[addr];
				int color = (code & 0x3c00) >> 10;
				int rgn;
	
				code &=0x3ff;
				rgn = gfx0;
	
				if (code > 0x1ff)
				{
					code &= 0x1ff;
					rgn = gfx1;
				}
	
				drawgfx(bitmap,Machine->gfx[rgn],
						code,
						color,
						0,0,
						8*x,8*y,
						cliprect,transparency,7);
	
				addr = (addr & 0xfc00) | ((addr + 1) & 0x03ff);
			}
		}
	}
	
	VIDEO_UPDATE( tugboat )
	{
		int startaddr0 = hd46505_0_reg[0x0c]*256 + hd46505_0_reg[0x0d];
		int startaddr1 = hd46505_1_reg[0x0c]*256 + hd46505_1_reg[0x0d];
	
	
		draw_tilemap(bitmap,cliprect,startaddr0,0,1,TRANSPARENCY_NONE);
		draw_tilemap(bitmap,cliprect,startaddr1,2,3,TRANSPARENCY_PEN);
	}
	
	
	
	
	
	static int ctrl;
	
	public static ReadHandlerPtr tugboat_input_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (~ctrl & 0x80)
			return readinputport(0);
		else if (~ctrl & 0x40)
			return readinputport(1);
		else if (~ctrl & 0x20)
			return readinputport(2);
		else if (~ctrl & 0x10)
			return readinputport(3);
		else
			return readinputport(4);
	} };
	
	public static ReadHandlerPtr tugboat_ctrl_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return ctrl;
	} };
	
	public static WriteHandlerPtr tugboat_ctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		ctrl = data;
	} };
	
	static struct pia6821_interface pia0_intf =
	{
		/*inputs : A/B,CA/B1,CA/B2 */ tugboat_input_r, 0, 0, 0, 0, 0,
		/*outputs: A/B,CA/B2       */ 0, 0, 0, 0,
		/*irqs   : A/B             */ 0, 0,
	};
	
	static struct pia6821_interface pia1_intf =
	{
		/*inputs : A/B,CA/B1,CA/B2 */ input_port_5_r, tugboat_ctrl_r, 0, 0, 0, 0,
		/*outputs: A/B,CA/B2       */ 0,              tugboat_ctrl_w, 0, 0,
		/*irqs   : A/B             */ 0, 0
	};
	
	MACHINE_INIT( tugboat )
	{
		pia_unconfig();
		pia_config(0, PIA_STANDARD_ORDERING, &pia0_intf);
		pia_config(1, PIA_STANDARD_ORDERING, &pia1_intf);
		pia_reset();
	}
	
	INTERRUPT_GEN( tugboat_interrupt )
	{
		if (cpu_getiloops() == 0)
		{
			cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);  // vbl?
		}
		else
		{
			cpu_set_irq_line(0, 0, HOLD_LINE);  // reads inputs, drives sound?
		}
	}
	
	
	
	public static Memory_ReadAddress tugboat_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x01ff, MRA_RAM ),
		new Memory_ReadAddress( 0x11e4, 0x11e7, pia_0_r ),
		new Memory_ReadAddress( 0x11e8, 0x11eb, pia_1_r ),
		new Memory_ReadAddress( 0x2000, 0x2fff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xfff0, 0xffff, MRA_ROM ),	/* vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress tugboat_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x01ff, MWA_RAM, tugboat_ram ),
		new Memory_WriteAddress( 0x1060, 0x1060, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x1061, 0x1061, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x10a0, 0x10a1, tugboat_hd46505_0_w ),	// scrolling is performed changing the start_addr register (0C/0D)
		new Memory_WriteAddress( 0x10c0, 0x10c1, tugboat_hd46505_1_w ),
		new Memory_WriteAddress( 0x11e4, 0x11e7, pia_0_w ),
		new Memory_WriteAddress( 0x11e8, 0x11eb, pia_1_w ),
		new Memory_WriteAddress( 0x18e0, 0x18ef, tugboat_score_w ),
		new Memory_WriteAddress( 0x2000, 0x2fff, MWA_RAM ),	/* tilemap RAM */
	    new Memory_WriteAddress( 0x5000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_tugboat = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_DIPNAME( 0x30, 0x10, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x40, "5" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
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
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0x80, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, charlayout, 0x80, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0, charlayout, 0x00, 16 ),
		new GfxDecodeInfo( REGION_GFX4, 0, charlayout, 0x00, 16 ),
		new GfxDecodeInfo( -1 )
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,			/* 1 chip */
		2000000,	/* 2 MHz???? */
		new int[] { 35 },		/* volume */
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	
	static MACHINE_DRIVER_START( tugboat )
		MDRV_CPU_ADD_TAG("main", M6502, 2000000)	/* 2 MHz ???? */
		MDRV_CPU_MEMORY(tugboat_readmem,tugboat_writemem)
		MDRV_CPU_VBLANK_INT(tugboat_interrupt,2)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(tugboat)
	
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8,64*8)
		MDRV_VISIBLE_AREA(1*8,31*8-1,2*8,30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(tugboat)
		MDRV_VIDEO_UPDATE(tugboat)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	
	static RomLoadPtr rom_tugboat = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "u7.bin", 0x5000, 0x1000, 0xe81d7581 );
		ROM_LOAD( "u8.bin", 0x6000, 0x1000, 0x7525de06 );
		ROM_LOAD( "u9.bin", 0x7000, 0x1000, 0xaa4ae687 );
		ROM_RELOAD(         0xf000, 0x1000 );/* for the vectors */
	
		ROM_REGION( 0x1800, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT  );
		ROM_LOAD( "u67.bin",  0x0000, 0x0800, 0x601c425b );
		ROM_FILL(             0x0800, 0x0800, 0xff );
		ROM_FILL(             0x1000, 0x0800, 0xff );
	
		ROM_REGION( 0x3000, REGION_GFX2, ROMREGION_DISPOSE | ROMREGION_INVERT  );
		ROM_LOAD( "u68.bin", 0x0000, 0x1000, 0xd5835182 );
		ROM_LOAD( "u69.bin", 0x1000, 0x1000, 0xe6d25878 );
		ROM_LOAD( "u70.bin", 0x2000, 0x1000, 0x34ce2850 );
	
		ROM_REGION( 0x1800, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "u168.bin", 0x0000, 0x0800, 0x279042fd );/* labeled u-167 */
		ROM_FILL(             0x0800, 0x0800, 0x00 );
		ROM_FILL(             0x1000, 0x0800, 0x00 );
	
		ROM_REGION( 0x1800, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "u170.bin", 0x0000, 0x0800, 0x64d9f4d7 );/* labeled u-168 */
		ROM_LOAD( "u169.bin", 0x0800, 0x0800, 0x1a636296 );/* labeled u-169 */
		ROM_LOAD( "u167.bin", 0x1000, 0x0800, 0xb9c9b4f7 );/* labeled u-170 */
	
		ROM_REGION( 0x0100, REGION_PROMS, ROMREGION_DISPOSE );
		ROM_LOAD( "nt2_u128.clr", 0x0000, 0x0100, 0x236672bf );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_tugboat	   = new GameDriver("1982"	,"tugboat"	,"tugboat.java"	,rom_tugboat,null	,machine_driver_tugboat	,input_ports_tugboat	,null	,ROT90	,	"ETM", "Tugboat", GAME_NOT_WORKING | GAME_IMPERFECT_GRAPHICS )
}
