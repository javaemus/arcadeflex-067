/***************************************************************************

Find Out    (c) 1987

driver by Nicola Salmoria

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class findout
{
	
	
	
	VIDEO_UPDATE( findout )
	{
		copybitmap(bitmap,tmpbitmap,0,0,0,0,cliprect,TRANSPARENCY_NONE,0);
	}
	
	
	static data8_t drawctrl[3];
	
	public static WriteHandlerPtr findout_drawctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		drawctrl[offset] = data;
	} };
	
	public static WriteHandlerPtr findout_bitmap_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int sx,sy;
		int fg,bg,mask,bits;
	
		fg = drawctrl[0] & 7;
		bg = 2;
		mask = 0xff;//drawctrl[2];
		bits = drawctrl[1];
	
		sx = 8*(offset % 64);
		sy = offset / 64;
	
	//if (mask != bits)
	//	usrintf_showmessage("color %02x bits %02x mask %02x\n",fg,bits,mask);
	
		if (mask & 0x80) plot_pixel(tmpbitmap,sx+0,sy,(bits & 0x80) ? fg : bg);
		if (mask & 0x40) plot_pixel(tmpbitmap,sx+1,sy,(bits & 0x40) ? fg : bg);
		if (mask & 0x20) plot_pixel(tmpbitmap,sx+2,sy,(bits & 0x20) ? fg : bg);
		if (mask & 0x10) plot_pixel(tmpbitmap,sx+3,sy,(bits & 0x10) ? fg : bg);
		if (mask & 0x08) plot_pixel(tmpbitmap,sx+4,sy,(bits & 0x08) ? fg : bg);
		if (mask & 0x04) plot_pixel(tmpbitmap,sx+5,sy,(bits & 0x04) ? fg : bg);
		if (mask & 0x02) plot_pixel(tmpbitmap,sx+6,sy,(bits & 0x02) ? fg : bg);
		if (mask & 0x01) plot_pixel(tmpbitmap,sx+7,sy,(bits & 0x01) ? fg : bg);
	} };
	
	
	public static ReadHandlerPtr portC_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 4;
	//	return (rand()&2);
	} };
	
	public static WriteHandlerPtr lamps_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		set_led_status(0,data & 0x01);
		set_led_status(1,data & 0x02);
		set_led_status(2,data & 0x04);
		set_led_status(3,data & 0x08);
		set_led_status(4,data & 0x10);
	} };
	
	public static WriteHandlerPtr sound_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* bit 3 used but unknown */
	
		/* bit 6 enables NMI */
		interrupt_enable_w(0,data & 0x40);
	
		/* bit 7 goes directly to the sound amplifier */
		DAC_data_w(0,((data & 0x80) >> 7) * 255);
	
	//	logerror("%04x: sound_w %02x\n",activecpu_get_pc(),data);
	//	usrintf_showmessage("%02x",data);
	} };
	
	static ppi8255_interface ppi8255_intf =
	{
		2, 									/* 2 chips */
		{ input_port_0_r, input_port_2_r },	/* Port A read */
		{ input_port_1_r, NULL },			/* Port B read */
		{ NULL,           portC_r },		/* Port C read */
		{ NULL,           NULL },			/* Port A write */
		{ NULL,           lamps_w },		/* Port B write */
		{ sound_w,        NULL },			/* Port C write */
	};
	
	MACHINE_INIT( findout )
	{
		ppi8255_init(&ppi8255_intf);
	}
	
	
	public static ReadHandlerPtr catchall  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int pc = activecpu_get_pc();
	
		if (pc != 0x3c74 && pc != 0x0364 && pc != 0x036d)	/* weed out spurious blit reads */
			logerror("%04x: unmapped memory read from %04x\n",pc,offset);
	
		return 0xff;
	} };
	
	public static WriteHandlerPtr banksel_main_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_setbank(1,memory_region(REGION_CPU1) + 0x8000);
	} };
	public static WriteHandlerPtr banksel_1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_setbank(1,memory_region(REGION_CPU1) + 0x10000);
	} };
	public static WriteHandlerPtr banksel_2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_setbank(1,memory_region(REGION_CPU1) + 0x18000);
	} };
	public static WriteHandlerPtr banksel_3_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_setbank(1,memory_region(REGION_CPU1) + 0x20000);
	} };
	public static WriteHandlerPtr banksel_4_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_setbank(1,memory_region(REGION_CPU1) + 0x28000);
	} };
	public static WriteHandlerPtr banksel_5_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_setbank(1,memory_region(REGION_CPU1) + 0x30000);
	} };
	
	
	/* This signature is used to validate the question ROMs. Simple protection check? */
	static int signature_answer,signature_pos;
	
	public static ReadHandlerPtr signature_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return signature_answer;
	} };
	
	public static WriteHandlerPtr signature_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (data == 0) signature_pos = 0;
		else
		{
			static data8_t signature[8] = { 0xff, 0x01, 0xfd, 0x05, 0xf5, 0x15, 0xd5, 0x55 };
	
			signature_answer = signature[signature_pos++];
	
			signature_pos &= 7;	/* safety; shouldn't happen */
		}
	} };
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4800, 0x4803, ppi8255_0_r ),
		new Memory_ReadAddress( 0x5000, 0x5003, ppi8255_1_r ),
		new Memory_ReadAddress( 0x6400, 0x6400, signature_r ),
		new Memory_ReadAddress( 0x7800, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_BANK1 ),
		new Memory_ReadAddress( 0x0000, 0xffff, catchall ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM, generic_nvram, generic_nvram_size ),
		new Memory_WriteAddress( 0x4800, 0x4803, ppi8255_0_w ),
		new Memory_WriteAddress( 0x5000, 0x5003, ppi8255_1_w ),
		/* banked ROMs are enabled by low 6 bits of the address */
		new Memory_WriteAddress( 0x603e, 0x603e, banksel_1_w ),
		new Memory_WriteAddress( 0x603d, 0x603d, banksel_2_w ),
		new Memory_WriteAddress( 0x603b, 0x603b, banksel_3_w ),
		new Memory_WriteAddress( 0x6037, 0x6037, banksel_4_w ),
		new Memory_WriteAddress( 0x602f, 0x602f, banksel_5_w ),
		new Memory_WriteAddress( 0x601f, 0x601f, banksel_main_w ),
		new Memory_WriteAddress( 0x6200, 0x6200, signature_w ),
		new Memory_WriteAddress( 0x7800, 0x7fff, MWA_ROM ),	/* space for diagnostic ROM? */
		new Memory_WriteAddress( 0x8000, 0x8002, findout_drawctrl_w ),
		new Memory_WriteAddress( 0xc000, 0xffff, findout_bitmap_w ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),	/* overlapped by the above */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_findout = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x07, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x07, DEF_STR( "7C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x08, 0x00, "Ripetizione gioco" );
		PORT_DIPSETTING(    0x08, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x10, 0x10, "Orientation" );
		PORT_DIPSETTING(    0x10, "Horizontal" );
		PORT_DIPSETTING(    0x00, "Vertical" );
		PORT_DIPNAME( 0x20, 0x20, "Acquisto lettera" );
		PORT_DIPSETTING(    0x20, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x40, "Lettera iniziale" );
		PORT_DIPSETTING(    0x40, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x80, "Lettera al bonus" );
		PORT_DIPSETTING(    0x80, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
	
		PORT_START(); 
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_LOW, IPT_COIN1, 2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_SERVICE( 0x08, IP_ACTIVE_LOW );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON5 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	
	
	static MACHINE_DRIVER_START( findout )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,4000000)	/* 4 MHz ?????? (affects sound pitch) */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(findout)
		MDRV_NVRAM_HANDLER(generic_0fill)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER|VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_SCREEN_SIZE(512, 256)
		MDRV_VISIBLE_AREA(48, 511-48, 16, 255-16)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(generic_bitmapped)
		MDRV_VIDEO_UPDATE(findout)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_findout = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x38000, REGION_CPU1, 0 );
		ROM_LOAD( "12.bin",       0x00000, 0x4000, 0x21132d4c );
		ROM_LOAD( "11.bin",       0x08000, 0x2000, 0x0014282c );/* banked */
		ROM_LOAD( "13.bin",       0x10000, 0x8000, 0xcea91a13 );/* banked ROMs for solution data */
		ROM_LOAD( "14.bin",       0x18000, 0x8000, 0x2a433a40 );
		ROM_LOAD( "15.bin",       0x20000, 0x8000, 0xd817b31e );
		ROM_LOAD( "16.bin",       0x28000, 0x8000, 0x143f9ac8 );
		ROM_LOAD( "17.bin",       0x30000, 0x8000, 0xdd743bc7 );
	
		ROM_REGION( 0x0200, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "82s147.bin",   0x0000, 0x0200, 0xf3b663bb );/* unknown */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_findout	   = new GameDriver("1987"	,"findout"	,"findout.java"	,rom_findout,null	,machine_driver_findout	,input_ports_findout	,null	,ROT0	,	"Elettronolo", "Find Out", GAME_WRONG_COLORS | GAME_IMPERFECT_SOUND )
}
