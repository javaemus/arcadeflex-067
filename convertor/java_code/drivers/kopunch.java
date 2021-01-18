/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class kopunch
{
	
	unsigned char *bsvideoram;
	size_t bsvideoram_size;
	
	static INT8 scroll[2];
	static int gfxbank,gfxflip;
	
	
	PALETTE_INIT( kopunch )
	{
		int i;
	
	
		color_prom+=24;	/* first 24 colors are black */
		for (i = 0;i < Machine->drv->total_colors;i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
	
			/* red component */
			bit0 = (*color_prom >> 0) & 0x01;
			bit1 = (*color_prom >> 1) & 0x01;
			bit2 = (*color_prom >> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			/* green component */
			bit0 = (*color_prom >> 3) & 0x01;
			bit1 = (*color_prom >> 4) & 0x01;
			bit2 = (*color_prom >> 5) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			/* blue component */
			bit0 = 0;
			bit1 = (*color_prom >> 6) & 0x01;
			bit2 = (*color_prom >> 7) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i,r,g,b);
			color_prom++;
		}
	}
	
	
	public static WriteHandlerPtr kopunch_scroll_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		scroll[offset] = data;
	} };
	
	public static WriteHandlerPtr kopunch_gfxbank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	//	usrintf_showmessage("bank = %02x",data);
	
		gfxbank = data & 0x07;
		gfxflip = data & 0x08;
	} };
	
	VIDEO_UPDATE( kopunch )
	{
		int offs;
	
	
		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs])
			{
				int sx,sy;
	
	
				dirtybuffer[offs] = 0;
	
				sx = offs % 32;
				sy = offs / 32;
	
				drawgfx(tmpbitmap,Machine->gfx[0],
						videoram.read(offs),
						0,
						0,0,
						8*sx,8*sy,
						&Machine->visible_area,TRANSPARENCY_NONE,0);
			}
		}
	
	
		/* copy the character mapped graphics */
		copybitmap(bitmap,tmpbitmap,0,0,0,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
	
	
		for (offs = bsvideoram_size - 1;offs >= 0;offs--)
		{
			int sx,sy;
	
	
			sx = offs % 16;
			sy = offs / 16;
	
			drawgfx(bitmap,Machine->gfx[1],
					(bsvideoram[offs] & 0x7f) + 128 * gfxbank,
					0,
					0,gfxflip,
					8*(sx+8)+scroll[0],8*(8+(gfxflip ? 15-sy : sy))+scroll[1],
					&Machine->visible_area,TRANSPARENCY_PEN,0);
		}
	}
	
	
	INTERRUPT_GEN( kopunch_interrupt )
	{
		if (cpu_getiloops() == 0)
		{
			if (~input_port_1_r(0) & 0x80)	/* coin 1 */
			{
				cpu_set_irq_line_and_vector(0,0,HOLD_LINE,0xf7);	/* RST 30h */
				return;
			}
			else if (~input_port_1_r(0) & 0x08)	/* coin 2 */
			{
				cpu_set_irq_line_and_vector(0,0,HOLD_LINE,0xef);	/* RST 28h */
				return;
			}
		}
	
		cpu_set_irq_line_and_vector(0,0,HOLD_LINE,0xff);	/* RST 38h */
	}
	
	public static ReadHandlerPtr kopunch_in_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		/* port 31 + low 3 bits of port 32 contain the punch strength */
		if (offset == 0)
			return rand();
		else
			return (rand() & 0x07) | input_port_1_r(0);
	} };
	
	public static WriteHandlerPtr kopunch_lamp_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		set_led_status(0,~data & 0x80);
	
	//	if ((data & 0x7f) != 0x7f)
	//		usrintf_showmessage("port 38 = %02x",data);
	} };
	
	public static WriteHandlerPtr kopunch_coin_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_counter_w(0,~data & 0x80);
		coin_counter_w(1,~data & 0x40);
	
	//	if ((data & 0x3f) != 0x3f)
	//		usrintf_showmessage("port 34 = %02x",data);
	} };
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x23ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x23ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6000, 0x63ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x7000, 0x70ff, MWA_RAM, bsvideoram, bsvideoram_size ),
		new Memory_WriteAddress( 0x7100, 0x73ff, MWA_RAM ),	/* more video ram? */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static ReadHandlerPtr pip_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return rand();
	} };
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x30, 0x30, input_port_0_r ),
		new IO_ReadPort( 0x31, 0x32, kopunch_in_r ),
		new IO_ReadPort( 0x3a, 0x3a, input_port_2_r ),
		new IO_ReadPort( 0x3e, 0x3e, input_port_3_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x34, 0x34, kopunch_coin_w ),
		new IO_WritePort( 0x38, 0x38, kopunch_lamp_w ),
		new IO_WritePort( 0x3c, 0x3d, kopunch_scroll_w ),
		new IO_WritePort( 0x3e, 0x3e, kopunch_gfxbank_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	
	static InputPortPtr input_ports_kopunch = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON4 );
	
		PORT_START(); 
		PORT_BIT( 0x07, IP_ACTIVE_HIGH, IPT_SPECIAL );/* punch strength (high 3 bits) */
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3), RGN_FRAC(1,3), RGN_FRAC(0,3) },
		new int[] { 7, 6, 5, 4, 3, 2, 1, 0 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX2, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static MACHINE_DRIVER_START( kopunch )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)	/* 4 MHz ???? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(kopunch_interrupt,4)	/* ??? */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(8)
	
		MDRV_PALETTE_INIT(kopunch)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(kopunch)
	
		/* sound hardware */
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_kopunch = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "epr1105.x",    0x0000, 0x1000, 0x34ef5e79 );
		ROM_LOAD( "epr1106.x",    0x1000, 0x1000, 0x25a5c68b );
	
		ROM_REGION( 0x1800, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "epr1102",      0x0000, 0x0800, 0x8a52de96 );
		ROM_LOAD( "epr1103",      0x0800, 0x0800, 0xbae5e054 );
		ROM_LOAD( "epr1104",      0x1000, 0x0800, 0x7b119a0e );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "epr1107",      0x0000, 0x1000, 0xca00244d );
		ROM_LOAD( "epr1108",      0x1000, 0x1000, 0xcc17c5ed );
		ROM_LOAD( "epr1110",      0x2000, 0x1000, 0xae0aff15 );
		ROM_LOAD( "epr1109",      0x3000, 0x1000, 0x625446ba );
		ROM_LOAD( "epr1112",      0x4000, 0x1000, 0xef6994df );
		ROM_LOAD( "epr1111",      0x5000, 0x1000, 0x28530ec9 );
	
		ROM_REGION( 0x0060, REGION_PROMS, 0 );
		ROM_LOAD( "epr1101",      0x0000, 0x0020, 0x15600f5d );/* palette */
		ROM_LOAD( "epr1099",      0x0020, 0x0020, 0xfc58c456 );/* unknown */
		ROM_LOAD( "epr1100",      0x0040, 0x0020, 0xbedb66b1 );/* unknown */
	ROM_END(); }}; 
	
	
	static DRIVER_INIT( kopunch )
	{
		unsigned char *rom = memory_region(REGION_CPU1);
	
		/* It looks like there is a security chip, that changes instruction of the form:
			0334: 3E 0C       ld   a,$0C
			0336: 30 FB       jr   nc,$0333
		   into something else (maybe just a nop) with the effect of resuming execution
		   from the operand of the JR  NC instruction (in the example above, 0337).
		   For now, I'm just patching the affected instructions. */
	
		rom[0x119] = 0;
		rom[0x336] = 0;
		rom[0x381] = 0;
		rom[0xf0b] = 0;
		rom[0xf33] = 0;
	}
	
	
	public static GameDriver driver_kopunch	   = new GameDriver("1981"	,"kopunch"	,"kopunch.java"	,rom_kopunch,null	,machine_driver_kopunch	,input_ports_kopunch	,init_kopunch	,ROT270	,	"Sega", "KO Punch", GAME_NO_SOUND )
}
