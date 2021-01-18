/* the way I hooked up the CTC is most likely completely wrong */

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class dlair
{
	
	
	
	/*
	   Dragon's Lair has two 7 segment LEDs on the board, used to report error
	   codes.
	   The association between the bits of the port and the led segments is:
	
	    ---0---
	   |       |
	   5       1
	   |       |
	    ---6---
	   |       |
	   4       2
	   |       |
	    ---3---
	
	   bit 7 = enable (0 = display off)
	
	   Error codes for led 0:
	   1 bad CPU
	   2 bad ROM
	   3 bad RAM a000-a7ff
	   4 bad RAM c000-c7ff
	   5 bad I/O ports 0-3
	   P ?
	 */
	
	static int led0,led1;
	
	public static WriteHandlerPtr dlair_led0_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		led0 = data;
	} };
	public static WriteHandlerPtr dlair_led1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		led1 = data;
	} };
	
	VIDEO_UPDATE( dlair )
	{
		int offs;
	
	
		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size - 2;offs >= 0;offs-=2)
		{
			if (dirtybuffer[offs] || dirtybuffer[offs+1])
			{
				int sx,sy;
	
	
				dirtybuffer[offs] = 0;
				dirtybuffer[offs+1] = 0;
	
				sx = (offs/2) % 32;
				sy = (offs/2) / 32;
	
				drawgfx(tmpbitmap,Machine->gfx[0],
						videoram.read(offs+1),
						0,
						0,0,
						8*sx,16*sy,
						&Machine->visible_area,TRANSPARENCY_NONE,0);
			}
		}
	
	
		/* copy the character mapped graphics */
		copybitmap(bitmap,tmpbitmap,0,0,0,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
	
	if (led0 & 128)
	{
	if ((led0 & 1) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		8,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led0 & 2) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		16,8,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led0 & 4) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		16,24,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led0 & 8) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		8,32,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led0 & 16) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		0,24,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led0 & 32) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		0,8,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led0 & 64) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		8,16,&Machine->visible_area,TRANSPARENCY_NONE,0);
	}
	if (led1 & 128)
	{
	if ((led1 & 1) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		32+8,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led1 & 2) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		32+16,8,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led1 & 4) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		32+16,24,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led1 & 8) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		32+8,32,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led1 & 16) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		32+0,24,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led1 & 32) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		32+0,8,&Machine->visible_area,TRANSPARENCY_NONE,0);
	if ((led1 & 64) == 0) drawgfx(bitmap,Machine->uifont,'x',0,0,0,
		32+8,16,&Machine->visible_area,TRANSPARENCY_NONE,0);
	}
	}
	
	
	
	/* z80 ctc */
	static void ctc_interrupt (int state)
	{
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, Z80_VECTOR(0,state));
	}
	
	static z80ctc_interface ctc_intf =
	{
		1,                  /* 1 chip */
		{ 0 },              /* clock (filled in from the CPU 0 clock */
		{ 0 },              /* timer disables */
		{ ctc_interrupt },  /* interrupt handler */
		{ 0 },              /* ZC/TO0 callback */
		{ 0 },              /* ZC/TO1 callback */
		{ 0 }               /* ZC/TO2 callback */
	};
	
	
	MACHINE_INIT( dlair )
	{
	   /* initialize the CTC */
	   ctc_intf.baseclock[0] = Machine->drv->cpu[0].cpu_clock;
	   z80ctc_init(&ctc_intf);
	}
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa000, 0xa7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa000, 0xa7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xc000, 0xc3ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xc400, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe000, dlair_led0_w ),
		new Memory_WriteAddress( 0xe008, 0xe008, dlair_led1_w ),
		new Memory_WriteAddress( 0xe030, 0xe030, watchdog_reset_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static unsigned char pip[4];
	public static ReadHandlerPtr pip_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	logerror("PC %04x: read I/O port %02x\n",activecpu_get_pc(),offset);
		return pip[offset];
	} };
	public static WriteHandlerPtr pip_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	logerror("PC %04x: write %02x to I/O port %02x\n",activecpu_get_pc(),data,offset);
		pip[offset] = data;
	z80ctc_0_w(offset,data);
	} };
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x03, pip_r ),
	//	new IO_ReadPort( 0x80, 0x83, z80ctc_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x03, pip_w ),
	//	new IO_WritePort( 0x80, 0x83, z80ctc_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_dlair = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,16,
		512,
		1,
		new int[] { 0 },
		new int[] { 7, 6, 5, 4, 3, 2, 1, 0 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		16*8	/* every char takes 8 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, charlayout,  0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static Z80_DaisyChain daisy_chain[] =
	{
		{ z80ctc_reset, z80ctc_interrupt, z80ctc_reti, 0 }, /* CTC number 0 */
		{ 0,0,0,-1} 		/* end mark */
	};
	
	
	
	static MACHINE_DRIVER_START( dlair )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3072000)	/* 3.072 MHz ? */
		MDRV_CPU_CONFIG(daisy_chain)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(dlair)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 0*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(8)
	
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(dlair)
	
		/* sound hardware */
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_dlair = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "u45",          0x0000, 0x2000, 0x329b354a );
		ROM_LOAD( "u46",          0x2000, 0x2000, 0x8479612b );
		ROM_LOAD( "u47",          0x4000, 0x2000, 0x6a66f6b4 );
		ROM_LOAD( "u48",          0x6000, 0x2000, 0x36575106 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "u33",          0x0000, 0x2000, 0xe7506d96 );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_dlair	   = new GameDriver("1983"	,"dlair"	,"dlair.java"	,rom_dlair,null	,machine_driver_dlair	,input_ports_dlair	,null	,ROT0	,	"Cinematronics", "Dragon's Lair", GAME_NOT_WORKING | GAME_NO_SOUND )
	
}
