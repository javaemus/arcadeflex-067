/***************************************************************************

	  Poly-Play
	  (c) 1985 by VEB Polytechnik Karl-Marx-Stadt

	  driver by Martin Buchholz (buchholz@mail.uni-greifswald.de)

	  Very special thanks to the following people, each one of them spent
	  some of their spare time to make this driver working:
	  - Juergen Oppermann and Volker Hann for electronical assistance,
	    repair work and ROM dumping.
	  - Jan-Ole Christian from the Videogamemuseum in Berlin, which houses
	    one of the last existing Poly-Play arcade automatons. He also
	    provided me with schematics and service manuals.


memory map:

0000 - 03ff OS ROM
0400 - 07ff Game ROM (used for Abfahrtslauf)
0800 - 0cff Menu Screen ROM

0d00 - 0fff work RAM

1000 - 4fff GAME ROM (pcb 2 - Abfahrtslauf          (1000 - 1bff)
                              Hirschjagd            (1c00 - 27ff)
                              Hase und Wolf         (2800 - 3fff)
                              Schmetterlingsfang    (4000 - 4fff)
5000 - 8fff GAME ROM (pcb 1 - Schiessbude           (5000 - 5fff)
                              Autorennen            (6000 - 73ff)
                              opto-akust. Merkspiel (7400 - 7fff)
                              Wasserrohrbruch       (8000 - 8fff)

e800 - ebff character ROM (chr 00..7f) 1 bit per pixel
ec00 - f7ff character RAM (chr 80..ff) 3 bit per pixel
f800 - ffff video RAM

I/O ports:

read:

83        IN1
          used as hardware random number generator

84        IN0
          bit 0 = fire button
          bit 1 = right
          bit 2 = left
          bit 3 = up
          bit 4 = down
          bit 5 = unused
          bit 6 = Summe Spiele
          bit 7 = coinage (+IRQ to make the game acknowledge it)

85        bit 0-4 = light organ (unemulated :)) )
          bit 5-7 = sound parameter (unemulated, it's very difficult to
                    figure out how those work)

86        ???

87        PIO Control register

write:
80	      Sound Channel 1
81        Sound Channel 2
82        generates 40 Hz timer for timeout in game title screens
83        generates main 75 Hz timer interrupt

The Poly-Play has a simple bookmarking system which can be activated
setting Bit 6 of PORTA (Summe Spiele) to low. It reads a double word
from 0c00 and displays it on the screen.
I currently haven't figured out how the I/O port handling for the book-
mark system works.

Uniquely the Poly-Play has a light organ which totally confuses you whilst
playing the automaton. Bits 1-5 of PORTB control the organ but it's not
emulated now. ;)

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class polyplay
{
	
	/* video hardware access */
	extern unsigned char *polyplay_characterram;
	PALETTE_INIT( polyplay );
	VIDEO_UPDATE( polyplay );
	
	/* I/O Port handling */
	
	/* sound handling */
	void set_channel1(int active);
	void set_channel2(int active);
	static int prescale1;
	static int prescale2;
	static int channel1_active;
	static int channel1_const;
	static int channel2_active;
	static int channel2_const;
	void play_channel1(int data);
	void play_channel2(int data);
	int  polyplay_sh_start(const struct MachineSound *msound);
	
	/* timer handling */
	static void timer_callback(int param);
	static void* polyplay_timer;
	
	
	/* Polyplay Sound Interface */
	static CustomSound_interface custom_interface = new CustomSound_interface
	(
		polyplay_sh_start,
		polyplay_sh_stop,
		polyplay_sh_update
	);
	
	
	static MACHINE_INIT( polyplay )
	{
		channel1_active = 0;
		channel1_const = 0;
		channel2_active = 0;
		channel2_const = 0;
	
		set_channel1(0);
		play_channel1(0);
		set_channel2(0);
		play_channel2(0);
	
		polyplay_timer = timer_alloc(timer_callback);
	}
	
	
	static INTERRUPT_GEN( periodic_interrupt )
	{
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0x4e);
	}
	
	
	static INTERRUPT_GEN( coin_interrupt )
	{
		static int last = 0;
	
		if (readinputport(0) & 0x80)
		{
			last = 0;
		}
		else
		{
			if (last == 0)    /* coin inserted */
			{
				cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0x50);
			}
	
			last = 1;
		}
	}
	
	
	/* memory mapping */
	public static Memory_ReadAddress polyplay_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0bff, MRA_ROM ),
		new Memory_ReadAddress( 0x0c00, 0x0fff, MRA_RAM ),
		new Memory_ReadAddress( 0x1000, 0x8fff, MRA_ROM ),
		new Memory_ReadAddress( 0xe800, 0xebff, MRA_ROM ),
		new Memory_ReadAddress( 0xec00, 0xf7ff, polyplay_characterram_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, videoram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress polyplay_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0bff, MWA_ROM ),
		new Memory_WriteAddress( 0x0c00, 0x0fff, MWA_RAM ),
		new Memory_WriteAddress( 0x1000, 0x8fff, MWA_ROM ),
		new Memory_WriteAddress( 0xe800, 0xebff, MWA_ROM ),
		new Memory_WriteAddress( 0xec00, 0xf7ff, polyplay_characterram_w, polyplay_characterram ),
		new Memory_WriteAddress( 0xf800, 0xffff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	/* port mapping */
	public static IO_ReadPort readport_polyplay[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x84, 0x84, input_port_0_r ),
		new IO_ReadPort( 0x83, 0x83, polyplay_random_read ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_polyplay[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x80, 0x81, polyplay_sound_channel ),
		new IO_WritePort( 0x82, 0x82, polyplay_start_timer2 ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static InputPortPtr input_ports_polyplay = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_SERVICE, "Bookkeeping Info", KEYCODE_F2, IP_JOY_NONE );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	INPUT_PORTS_END(); }}; 
	
	
	public static WriteHandlerPtr polyplay_sound_channel = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		switch(offset) {
		case 0x00:
			if (channel1_const) {
				if (data <= 1) {
					set_channel1(0);
				}
				channel1_const = 0;
				play_channel1(data*prescale1);
	
			}
			else {
				prescale1 = (data & 0x20) ? 16 : 1;
				if (data & 0x04) {
					set_channel1(1);
					channel1_const = 1;
				}
				if ((data == 0x41) || (data == 0x65) || (data == 0x45)) {
					set_channel1(0);
					play_channel1(0);
				}
			}
			break;
		case 0x01:
			if (channel2_const) {
				if (data <= 1) {
					set_channel2(0);
				}
				channel2_const = 0;
				play_channel2(data*prescale2);
	
			}
			else {
				prescale2 = (data & 0x20) ? 16 : 1;
				if (data & 0x04) {
					set_channel2(1);
					channel2_const = 1;
				}
				if ((data == 0x41) || (data == 0x65) || (data == 0x45)) {
					set_channel2(0);
					play_channel2(0);
				}
			}
			break;
		}
	} };
	
	public static WriteHandlerPtr polyplay_start_timer2 = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (data == 0x03)
			timer_adjust(polyplay_timer, TIME_NEVER, 0, 0);
	
		if (data == 0xb5)
			timer_adjust(polyplay_timer, TIME_IN_HZ(40), 0, TIME_IN_HZ(40));
	} };
	
	public static ReadHandlerPtr polyplay_random_read  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return rand() & 0xff;
	} };
	
	/* graphic structures */
	static GfxLayout charlayout_1_bit = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		128,	/* 128 characters */
		1,  	/* 1 bit per pixel */
		new int[] { 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout charlayout_3_bit = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		128,	/* 128 characters */
		3,  	/* 3 bit per pixel */
		new int[] { 0, 128*8*8, 128*8*8 + 128*8*8 },    /* offset for each bitplane */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_CPU1, 0xe800, charlayout_1_bit, 0, 1 ),
		new GfxDecodeInfo( REGION_CPU1, 0xec00, charlayout_3_bit, 2, 1 ),
		new GfxDecodeInfo( -1 )	/* end of array */
	};
	
	
	/* the machine driver */
	
	static MACHINE_DRIVER_START( polyplay )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 9830400/4)
		MDRV_CPU_MEMORY(polyplay_readmem,polyplay_writemem)
		MDRV_CPU_PORTS(readport_polyplay,writeport_polyplay)
		MDRV_CPU_PERIODIC_INT(periodic_interrupt,75)
		MDRV_CPU_VBLANK_INT(coin_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(50)
	
		MDRV_MACHINE_INIT(polyplay)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 64*8-1, 0*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(10)
	
		MDRV_PALETTE_INIT(polyplay)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(polyplay)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END
	
	
	/* ROM loading and mapping */
	static RomLoadPtr rom_polyplay = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "cpu_0000.37",       0x0000, 0x0400, 0x87884c5f );
		ROM_LOAD( "cpu_0400.36",       0x0400, 0x0400, 0xd5c84829 );
		ROM_LOAD( "cpu_0800.35",       0x0800, 0x0400, 0x5f36d08e );
		ROM_LOAD( "2_-_1000.14",       0x1000, 0x0400, 0x950dfcdb );
		ROM_LOAD( "2_-_1400.10",       0x1400, 0x0400, 0x829f74ca );
		ROM_LOAD( "2_-_1800.6",        0x1800, 0x0400, 0xb69306f5 );
		ROM_LOAD( "2_-_1c00.2",        0x1c00, 0x0400, 0xaede2280 );
		ROM_LOAD( "2_-_2000.15",       0x2000, 0x0400, 0x6c7ad0d8 );
		ROM_LOAD( "2_-_2400.11",       0x2400, 0x0400, 0xbc7462f0 );
		ROM_LOAD( "2_-_2800.7",        0x2800, 0x0400, 0x9ccf1958 );
		ROM_LOAD( "2_-_2c00.3",        0x2c00, 0x0400, 0x21827930 );
		ROM_LOAD( "2_-_3000.16",       0x3000, 0x0400, 0xb3b3c0ec );
		ROM_LOAD( "2_-_3400.12",       0x3400, 0x0400, 0xbd416cd0 );
		ROM_LOAD( "2_-_3800.8",        0x3800, 0x0400, 0x1c470b7c );
		ROM_LOAD( "2_-_3c00.4",        0x3c00, 0x0400, 0xb8354a19 );
		ROM_LOAD( "2_-_4000.17",       0x4000, 0x0400, 0x1e01041e );
		ROM_LOAD( "2_-_4400.13",       0x4400, 0x0400, 0xfe4d8959 );
		ROM_LOAD( "2_-_4800.9",        0x4800, 0x0400, 0xc45f1d9d );
		ROM_LOAD( "2_-_4c00.5",        0x4c00, 0x0400, 0x26950ad6 );
		ROM_LOAD( "1_-_5000.30",       0x5000, 0x0400, 0x9f5e2ba1 );
		ROM_LOAD( "1_-_5400.26",       0x5400, 0x0400, 0xb5f9a780 );
		ROM_LOAD( "1_-_5800.22",       0x5800, 0x0400, 0xd973ad12 );
		ROM_LOAD( "1_-_5c00.18",       0x5c00, 0x0400, 0x9c22ea79 );
		ROM_LOAD( "1_-_6000.31",       0x6000, 0x0400, 0x245c49ca );
		ROM_LOAD( "1_-_6400.27",       0x6400, 0x0400, 0x181e427e );
		ROM_LOAD( "1_-_6800.23",       0x6800, 0x0400, 0x8a6c1f97 );
		ROM_LOAD( "1_-_6c00.19",       0x6c00, 0x0400, 0x77901dc9 );
		ROM_LOAD( "1_-_7000.32",       0x7000, 0x0400, 0x83ffbe57 );
		ROM_LOAD( "1_-_7400.28",       0x7400, 0x0400, 0xe2a66531 );
		ROM_LOAD( "1_-_7800.24",       0x7800, 0x0400, 0x1d0803ef );
		ROM_LOAD( "1_-_7c00.20",       0x7c00, 0x0400, 0x17dfa7e4 );
		ROM_LOAD( "1_-_8000.33",       0x8000, 0x0400, 0x6ee02375 );
		ROM_LOAD( "1_-_8400.29",       0x8400, 0x0400, 0x9db09598 );
		ROM_LOAD( "1_-_8800.25",       0x8800, 0x0400, 0xca2f963f );
		ROM_LOAD( "1_-_8c00.21",       0x8c00, 0x0400, 0x0c7dec2d );
		ROM_LOAD( "char.1",            0xe800, 0x0400, 0x5242dd6b );
	ROM_END(); }}; 
	
	
	static void timer_callback(int param)
	{
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0x4c);
	}
	
	/* game driver */
	public static GameDriver driver_polyplay	   = new GameDriver("1985"	,"polyplay"	,"polyplay.java"	,rom_polyplay,null	,machine_driver_polyplay	,input_ports_polyplay	,null	,ROT0	,	"VEB Polytechnik Karl-Marx-Stadt", "Poly-Play" )
}
