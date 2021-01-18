/***************************************************************************

	Atari Centipede hardware

	Games supported:
		* Centipede (5 sets)
		* Warlords
		* Millipede
		* Qwak (prototype)

	Known bugs:
		* are coins supposed to take over a second to register?

****************************************************************************

	Main clock: XTAL = 12.096 MHz
	6502 Clock: XTAL/8 = 1.512 MHz (0.756 when accessing playfield RAM)
	Horizontal video frequency: HSYNC = XTAL/256/3 = 15.75 kHz
	Video frequency: VSYNC = HSYNC/263 ?? = 59.88593 Hz (not sure, could be /262)
	VBlank duration: 1/VSYNC * (23/263) = 1460 us


	              Centipede Memory map and Dip Switches
	              -------------------------------------

	Memory map for Centipede directly from the Atari schematics (1981).

	 Address  R/W  D7 D6 D5 D4 D3 D2 D1 D0   Function
	--------------------------------------------------------------------------------------
	0000-03FF       D  D  D  D  D  D  D  D   RAM
	--------------------------------------------------------------------------------------
	0400-07BF       D  D  D  D  D  D  D  D   Playfield RAM
	07C0-07CF       D  D  D  D  D  D  D  D   Motion Object Picture
	07D0-07DF       D  D  D  D  D  D  D  D   Motion Object Vert.
	07E0-07EF       D  D  D  D  D  D  D  D   Motion Object Horiz.
	07F0-07FF             D  D  D  D  D  D   Motion Object Color
	--------------------------------------------------------------------------------------
	0800       R    D  D  D  D  D  D  D  D   Option Switch 1 (0 = On)
	0801       R    D  D  D  D  D  D  D  D   Option Switch 2 (0 = On)
	--------------------------------------------------------------------------------------
	0C00       R    D           D  D  D  D   Horizontal Mini-Track Ball tm Inputs
	           R       D                     VBLANK  (1 = VBlank)
	           R          D                  Self-Test  (0 = On)
	           R             D               Cocktail Cabinet  (1 = Cocktail)
	0C01       R    D  D  D                  R,C,L Coin Switches (0 = On)
	           R             D               SLAM  (0 = On)
	           R                D            Player 2 Fire Switch (0 = On)
	           R                   D         Player 1 Fire Switch (0 = On)
	           R                      D      Player 2 Start Switch (0 = On)
	           R                         D   Player 1 Start Switch (0 = On)

	0C02       R    D           D  D  D  D   Vertical Mini-Track Ball tm Inputs
	0C03       R    D  D  D  D               Player 1 Joystick (R,L,Down,Up)
	           R                D  D  D  D   Player 2 Joystick   (0 = On)
	--------------------------------------------------------------------------------------
	1000-100F R/W   D  D  D  D  D  D  D  D   Custom Audio Chip
	1404       W                D  D  D  D   Playfield Color RAM
	140C       W                D  D  D  D   Motion Object Color RAM
	--------------------------------------------------------------------------------------
	1600       W    D  D  D  D  D  D  D  D   EA ROM Address & Data Latch
	1680       W                D  D  D  D   EA ROM Control Latch
	1700       R    D  D  D  D  D  D  D  D   EA ROM Read Data
	--------------------------------------------------------------------------------------
	1800       W                             IRQ Acknowledge
	--------------------------------------------------------------------------------------
	1C00       W    D                        Left Coin Counter (1 = On)
	1C01       W    D                        Center Coin Counter (1 = On)
	1C02       W    D                        Right Coin Counter (1 = On)
	1C03       W    D                        Player 1 Start LED (0 = On)
	1C04       W    D                        Player 2 Start LED (0 = On)
	1C07       W    D                        Track Ball Flip Control (0 = Player 1)
	--------------------------------------------------------------------------------------
	2000       W                             WATCHDOG
	2400       W                             Clear Mini-Track Ball Counters
	--------------------------------------------------------------------------------------
	2000-3FFF  R                             Program ROM
	--------------------------------------------------------------------------------------

	-EA ROM is an Erasable Reprogrammable rom to save the top 3 high scores
	  and other stuff.


	 Dip switches at N9 on the PCB

	 8    7    6    5    4    3    2    1    Option
	-------------------------------------------------------------------------------------
	                              On   On    English $
	                              On   Off   German
	                              Off  On    French
	                              Off  Off   Spanish
	-------------------------------------------------------------------------------------
	                    On   On              2 lives per game
	                    On   Off             3 lives per game $
	                    Off  On              4 lives per game
	                    Off  Off             5 lives per game
	-------------------------------------------------------------------------------------
	                                         Bonus life granted at every:
	          On   On                        10,000 points
	          On   Off                       12.000 points $
	          Off  On                        15,000 points
	          Off  Off                       20,000 points
	-------------------------------------------------------------------------------------
	     On                                  Hard game difficulty
	     Off                                 Easy game difficulty $
	-------------------------------------------------------------------------------------
	On                                       1-credit minimum $
	Off                                      2-credit minimum
	-------------------------------------------------------------------------------------

	$ = Manufacturer's suggested settings


	 Dip switches at N8 on the PCB

	 8    7    6    5    4    3    2    1    Option
	-------------------------------------------------------------------------------------
	                              On   On    Free play
	                              On   Off   1 coin for 2 credits
	                              Off  On    1 coin for 1 credit $
	                              Off  Off   2 coins for 1 credit
	-------------------------------------------------------------------------------------
	                    On   On              Right coin mech X 1 $
	                    On   Off             Right coin mech X 4
	                    Off  On              Right coin mech X 5
	                    Off  Off             Right coin mech X 6
	-------------------------------------------------------------------------------------
	               On                        Left coin mech X 1 $
	               Off                       Left coin mech X 2
	-------------------------------------------------------------------------------------
	On   On   On                             No bonus coins $
	On   On   Off                            For every 2 coins inserted, game logic
	                                          adds 1 more coin
	On   Off  On                             For every 4 coins inserted, game logic
	                                          adds 1 more coin
	On   Off  Off                            For every 4 coins inserted, game logic
	                                          adds 2 more coin
	Off  On   On                             For every 5 coins inserted, game logic
	                                          adds 1 more coin
	Off  On   Off                            For every 3 coins inserted, game logic
	                                          adds 1 more coin
	-------------------------------------------------------------------------------------
	$ = Manufacturer's suggested settings

	Changes:
		30 Apr 98 LBO
		* Fixed test mode
		* Changed high score to use earom routines
		* Added support for alternate rom set

****************************************************************************

	Millipede memory map (preliminary)

	driver by Ivan Mackintosh

	0400-040F		POKEY 1
	0800-080F		POKEY 2
	1000-13BF		SCREEN RAM (8x8 TILES, 32x30 SCREEN)
	13C0-13CF		SPRITE IMAGE OFFSETS
	13D0-13DF		SPRITE HORIZONTAL OFFSETS
	13E0-13EF		SPRITE VERTICAL OFFSETS
	13F0-13FF		SPRITE COLOR OFFSETS

	2000			BIT 1-4 trackball
					BIT 5 IS P1 FIRE
					BIT 6 IS P1 START
					BIT 7 IS VBLANK

	2001			BIT 1-4 trackball
					BIT 5 IS P2 FIRE
					BIT 6 IS P2 START
					BIT 7,8 (?)

	2010			BIT 1 IS P1 RIGHT
					BIT 2 IS P1 LEFT
					BIT 3 IS P1 DOWN
					BIT 4 IS P1 UP
					BIT 5 IS SLAM, LEFT COIN, AND UTIL COIN
					BIT 6,7 (?)
					BIT 8 IS RIGHT COIN
	2030			earom read
	2480-249F		COLOR RAM
	2500-2502		Coin counters
	2503-2504		LEDs
	2505-2507		Coin door lights ??
	2600			INTERRUPT ACKNOWLEDGE
	2680			CLEAR WATCHDOG
	2700			earom control
	2780			earom write
	4000-7FFF		GAME CODE

****************************************************************************

				  Warlords Memory map and Dip Switches
				  ------------------------------------

	 Address  R/W  D7 D6 D5 D4 D3 D2 D1 D0	 Function
	--------------------------------------------------------------------------------------
	0000-03FF		D  D  D  D	D  D  D  D	 RAM
	--------------------------------------------------------------------------------------
	0400-07BF		D  D  D  D	D  D  D  D	 Screen RAM (8x8 TILES, 32x32 SCREEN)
	07C0-07CF		D  D  D  D	D  D  D  D	 Motion Object Picture
	07D0-07DF		D  D  D  D	D  D  D  D	 Motion Object Vert.
	07E0-07EF		D  D  D  D	D  D  D  D	 Motion Object Horiz.
	--------------------------------------------------------------------------------------
	0800	   R	D  D  D  D	D  D  D  D	 Option Switch 1 (0 = On) (DSW 1)
	0801	   R	D  D  D  D	D  D  D  D	 Option Switch 2 (0 = On) (DSW 2)
	--------------------------------------------------------------------------------------
	0C00	   R	D						 Cocktail Cabinet  (0 = Cocktail)
			   R	   D					 VBLANK  (1 = VBlank)
			   R		  D 				 SELF TEST
			   R			 D				 DIAG STEP (Unused)
	0C01	   R	D  D  D 				 R,C,L Coin Switches (0 = On)
			   R			 D				 Slam (0 = On)
			   R				D			 Player 4 Start Switch (0 = On)
			   R				   D		 Player 3 Start Switch (0 = On)
			   R					  D 	 Player 2 Start Switch (0 = On)
			   R						 D	 Player 1 Start Switch (0 = On)
	--------------------------------------------------------------------------------------
	1000-100F  W   D  D  D	D  D  D  D	D	 Pokey
	--------------------------------------------------------------------------------------
	1800	   W							 IRQ Acknowledge
	--------------------------------------------------------------------------------------
	1C00-1C02  W	D  D  D  D	D  D  D  D	 Coin Counters
	--------------------------------------------------------------------------------------
	1C03-1C06  W	D  D  D  D	D  D  D  D	 LEDs
	--------------------------------------------------------------------------------------
	4000	   W							 Watchdog
	--------------------------------------------------------------------------------------
	5000-7FFF  R							 Program ROM
	--------------------------------------------------------------------------------------

	Game Option Settings - J2 (DSW1)
	=========================

	8	7	6	5	4	3	2	1		Option
	------------------------------------------
							On	On		English
							On	Off 	French
							Off On		Spanish
							Off Off 	German
						On				Music at end of each game
						Off 			Music at end of game for new highscore
			On	On						1 or 2 player game costs 1 credit
			On	Off 					1 player game=1 credit, 2 player=2 credits
			Off Off 					1 or 2 player game costs 2 credits
			Off On						Not used
	-------------------------------------------


	Game Price Settings - M2 (DSW2)
	========================

	8	7	6	5	4	3	2	1		Option
	------------------------------------------
							On	On		Free play
							On	Off 	1 coin for 2 credits
							Off On		1 coin for 1 credit
							Off Off 	2 coins for 1 credit
					On	On				Right coin mech x 1
					On	Off 			Right coin mech x 4
					Off On				Right coin mech x 5
					Off Off 			Right coin mech x 6
				On						Left coin mech x 1
				Off 					Left coin mech x 2
	On	On	On							No bonus coins
	On	On	Off 						For every 2 coins, add 1 coin
	On	Off On							For every 4 coins, add 1 coin
	On	Off Off 						For every 4 coins, add 2 coins
	Off On	On							For every 5 coins, add 1 coin
	------------------------------------------

****************************************************************************

	Atari Qwak (prototype) hardware
	driver by Mike Balfour

	Known issues:
		- fix colors
		- coins seem to count twice instead of once?
		- find DIP switches (should be at $4000, I would think)
		- figure out what $1000, $2000, and $2001 are used for
		- figure out exactly what the unknown bits in the $3000 area do

****************************************************************************

	This driver is based *extremely* loosely on the Centipede driver.

	The following memory map is pure speculation:

	0000-01FF     R/W		RAM
	0200-025F     R/W		RAM?  ER2055 NOVRAM maybe?
	0300-03FF     R/W		RAM
	0400-07BF		R/W		Video RAM
	07C0-07FF		R/W		Sprite RAM
	1000			W		???
	2000			W		???
	2001			W		???
	2003          W		Start LED 1
	2004          W		Start LED 2
	3000			R		$40 = !UP			$80 = unused?
	3001			R		$40 = !DOWN			$80 = ???
	3002			R		$40 = !LEFT			$80 = ???
	3003			R		$40 = !RIGHT		$80 = unused?
	3004			R		$40 = !START1		$80 = ???
	3005			R		$40 = !START2		$80 = !COIN
	3006			R		$40 = !BUTTON1		$80 = !COIN
	3007			R		$40 = unused?		$80 = !COIN
	4000          R		???
	6000-600F		R/W		Pokey 1
	7000-700F		R/W		Pokey 2
	8000-BFFF		R		ROM

	If you have any questions about how this driver works, don't hesitate to
	ask.  - Mike Balfour (mab22@po.cwru.edu)

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class centiped
{
	
	
	
	static int oldpos[4];
	static UINT8 sign[4];
	static UINT8 dsw_select;
	
	
	/*************************************
	 *
	 *	Interrupts
	 *
	 *************************************/
	
	static void generate_interrupt(int scanline)
	{
		/* IRQ is clocked on the rising edge of 16V, equal to the previous 32V */
		if (scanline & 16)
			cpu_set_irq_line(0, 0, ((scanline - 1) & 32) ? ASSERT_LINE : CLEAR_LINE);
	
		/* call back again after 16 scanlines */
		scanline += 16;
		if (scanline >= 256)
			scanline = 0;
		timer_set(cpu_getscanlinetime(scanline), scanline, generate_interrupt);
	}
	
	
	static MACHINE_INIT( centiped )
	{
		timer_set(cpu_getscanlinetime(0), 0, generate_interrupt);
		cpu_set_irq_line(0, 0, CLEAR_LINE);
		dsw_select = 0;
	
		/* kludge: clear RAM so that magworm can be reset cleanly */
		memset(memory_region(REGION_CPU1), 0, 0x400);
	}
	
	
	public static WriteHandlerPtr irq_ack_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_set_irq_line(0, 0, CLEAR_LINE);
	} };
	
	
	
	/*************************************
	 *
	 *	Input ports
	 *
	 *************************************/
	
	/*
	 * This wrapper routine is necessary because Centipede requires a direction bit
	 * to be set or cleared. The direction bit is held until the mouse is moved
	 * again.
	 *
	 * There is a 4-bit counter, and two inputs from the trackball: DIR and CLOCK.
	 * CLOCK makes the counter move in the direction of DIR. Since DIR is latched
	 * only when a CLOCK arrives, the DIR bit in the input port doesn't change
	 * until the trackball actually moves.
	 *
	 * There is also a CLR input to the counter which could be used by the game to
	 * clear the counter, but Centipede doesn't use it (though it would be a good
	 * idea to support it anyway).
	 *
	 * The counter is read 240 times per second. There is no provision whatsoever
	 * to prevent the counter from wrapping around between reads.
	 */
	
	INLINE int read_trackball(int idx, int switch_port)
	{
		int newpos;
	
		/* adjust idx if we're cocktail flipped */
		if (centiped_flipscreen)
			idx += 2;
	
		/* if we're to read the dipswitches behind the trackball data, do it now */
		if (dsw_select)
			return (readinputport(switch_port) & 0x7f) | sign[idx];
	
		/* get the new position and adjust the result */
		newpos = readinputport(6 + idx);
		if (newpos != oldpos[idx])
		{
			sign[idx] = (newpos - oldpos[idx]) & 0x80;
			oldpos[idx] = newpos;
		}
	
		/* blend with the bits from the switch port */
		return (readinputport(switch_port) & 0x70) | (oldpos[idx] & 0x0f) | sign[idx];
	}
	
	
	public static ReadHandlerPtr centiped_IN0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return read_trackball(0, 0);
	} };
	
	
	public static ReadHandlerPtr centiped_IN2_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return read_trackball(1, 2);
	} };
	
	
	public static ReadHandlerPtr milliped_IN1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return read_trackball(1, 1);
	} };
	
	
	public static WriteHandlerPtr input_select_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		dsw_select = (~data >> 7) & 1;
	} };
	
	
	
	/*************************************
	 *
	 *	Output ports
	 *
	 *************************************/
	
	public static WriteHandlerPtr led_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		set_led_status(offset, ~data & 0x80);
	} };
	
	
	public static ReadHandlerPtr centipdb_rand_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return rand() % 0xff;
	} };
	
	
	public static WriteHandlerPtr coin_count_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_counter_w(offset, data);
	} };
	
	
	
	/*************************************
	 *
	 *	Bootleg sound
	 *
	 *************************************/
	
	public static WriteHandlerPtr centipdb_AY8910_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		AY8910_control_port_0_w(0, offset);
		AY8910_write_port_0_w(0, data);
	} };
	
	
	public static ReadHandlerPtr centipdb_AY8910_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		AY8910_control_port_0_w(0, offset);
		return AY8910_read_port_0_r(0);
	} };
	
	
	
	/*************************************
	 *
	 *	Centipede CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress centiped_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		MEMORY_ADDRESS_BITS(14)
		new Memory_ReadAddress( 0x0000, 0x03ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0400, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0800, 0x0800, input_port_4_r ),	/* DSW1 */
		new Memory_ReadAddress( 0x0801, 0x0801, input_port_5_r ),	/* DSW2 */
		new Memory_ReadAddress( 0x0c00, 0x0c00, centiped_IN0_r ),	/* IN0 */
		new Memory_ReadAddress( 0x0c01, 0x0c01, input_port_1_r ),	/* IN1 */
		new Memory_ReadAddress( 0x0c02, 0x0c02, centiped_IN2_r ),	/* IN2 */
		new Memory_ReadAddress( 0x0c03, 0x0c03, input_port_3_r ),	/* IN3 */
		new Memory_ReadAddress( 0x1000, 0x100f, pokey1_r ),
		new Memory_ReadAddress( 0x1700, 0x173f, atari_vg_earom_r ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress centiped_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		MEMORY_ADDRESS_BITS(14)
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x07bf, centiped_videoram_w, videoram ),
		new Memory_WriteAddress( 0x07c0, 0x07ff, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0x1000, 0x100f, pokey1_w ),
		new Memory_WriteAddress( 0x1400, 0x140f, centiped_paletteram_w, paletteram ),
		new Memory_WriteAddress( 0x1600, 0x163f, atari_vg_earom_w ),
		new Memory_WriteAddress( 0x1680, 0x1680, atari_vg_earom_ctrl_w ),
		new Memory_WriteAddress( 0x1800, 0x1800, irq_ack_w ),
		new Memory_WriteAddress( 0x1c00, 0x1c02, coin_count_w ),
		new Memory_WriteAddress( 0x1c03, 0x1c04, led_w ),
		new Memory_WriteAddress( 0x1c07, 0x1c07, centiped_flip_screen_w ),
		new Memory_WriteAddress( 0x2000, 0x2000, watchdog_reset_w ),
		new Memory_WriteAddress( 0x2000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress centipb2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		MEMORY_ADDRESS_BITS(15)
		new Memory_ReadAddress( 0x0000, 0x03ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0400, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0800, 0x0800, input_port_4_r ),	/* DSW1 */
		new Memory_ReadAddress( 0x0801, 0x0801, input_port_5_r ),	/* DSW2 */
		new Memory_ReadAddress( 0x0c00, 0x0c00, centiped_IN0_r ),	/* IN0 */
		new Memory_ReadAddress( 0x0c01, 0x0c01, input_port_1_r ),	/* IN1 */
		new Memory_ReadAddress( 0x0c02, 0x0c02, centiped_IN2_r ),	/* IN2 */
		new Memory_ReadAddress( 0x0c03, 0x0c03, input_port_3_r ),	/* IN3 */
		new Memory_ReadAddress( 0x1001, 0x1001, AY8910_read_port_0_r ),
		new Memory_ReadAddress( 0x1700, 0x173f, atari_vg_earom_r ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x67ff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress centipb2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		MEMORY_ADDRESS_BITS(15)
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x07bf, centiped_videoram_w, videoram ),
		new Memory_WriteAddress( 0x07c0, 0x07ff, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0x1000, 0x1000, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x1001, 0x1001, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x1400, 0x140f, centiped_paletteram_w, paletteram ),
		new Memory_WriteAddress( 0x1600, 0x163f, atari_vg_earom_w ),
		new Memory_WriteAddress( 0x1680, 0x1680, atari_vg_earom_ctrl_w ),
		new Memory_WriteAddress( 0x1800, 0x1800, irq_ack_w ),
		new Memory_WriteAddress( 0x1c00, 0x1c02, coin_count_w ),
		new Memory_WriteAddress( 0x1c03, 0x1c04, led_w ),
		new Memory_WriteAddress( 0x1c07, 0x1c07, centiped_flip_screen_w ),
		new Memory_WriteAddress( 0x2000, 0x2000, watchdog_reset_w ),
		new Memory_WriteAddress( 0x2000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x67ff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Millipede CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress milliped_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		MEMORY_ADDRESS_BITS(15)
		new Memory_ReadAddress( 0x0000, 0x03ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0400, 0x040f, pokey1_r ),
		new Memory_ReadAddress( 0x0800, 0x080f, pokey2_r ),
		new Memory_ReadAddress( 0x1000, 0x13ff, MRA_RAM ),
		new Memory_ReadAddress( 0x2000, 0x2000, centiped_IN0_r ),
		new Memory_ReadAddress( 0x2001, 0x2001, milliped_IN1_r ),
		new Memory_ReadAddress( 0x2010, 0x2010, input_port_2_r ),
		new Memory_ReadAddress( 0x2011, 0x2011, input_port_3_r ),
		new Memory_ReadAddress( 0x2030, 0x2030, atari_vg_earom_r ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress milliped_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		MEMORY_ADDRESS_BITS(15)
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x040f, pokey1_w ),
		new Memory_WriteAddress( 0x0800, 0x080f, pokey2_w ),
		new Memory_WriteAddress( 0x1000, 0x13ff, centiped_videoram_w, videoram ),
		new Memory_WriteAddress( 0x13c0, 0x13ff, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0x2480, 0x249f, milliped_paletteram_w, paletteram ),
		new Memory_WriteAddress( 0x2500, 0x2502, coin_count_w ),
		new Memory_WriteAddress( 0x2503, 0x2504, led_w ),
		new Memory_WriteAddress( 0x2505, 0x2505, input_select_w ),
	//	new Memory_WriteAddress( 0x2506, 0x2507, MWA_NOP ), /* ? */
		new Memory_WriteAddress( 0x2600, 0x2600, irq_ack_w ),
		new Memory_WriteAddress( 0x2680, 0x2680, watchdog_reset_w ),
		new Memory_WriteAddress( 0x2700, 0x2700, atari_vg_earom_ctrl_w ),
		new Memory_WriteAddress( 0x2780, 0x27bf, atari_vg_earom_w ),
		new Memory_WriteAddress( 0x4000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Warlords CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress warlords_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		MEMORY_ADDRESS_BITS(15)
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0800, 0x0800, input_port_2_r ), /* DSW1 */
		new Memory_ReadAddress( 0x0801, 0x0801, input_port_3_r ), /* DSW2 */
		new Memory_ReadAddress( 0x0c00, 0x0c00, input_port_0_r ), /* IN0 */
		new Memory_ReadAddress( 0x0c01, 0x0c01, input_port_1_r ), /* IN1 */
		new Memory_ReadAddress( 0x1000, 0x100f, pokey1_r ),
		new Memory_ReadAddress( 0x5000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress warlords_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		MEMORY_ADDRESS_BITS(15)
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x07bf, centiped_videoram_w, videoram ),
		new Memory_WriteAddress( 0x07c0, 0x07ff, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0x1000, 0x100f, pokey1_w ),
		new Memory_WriteAddress( 0x1800, 0x1800, irq_ack_w ),
		new Memory_WriteAddress( 0x1c00, 0x1c02, coin_count_w ),
		new Memory_WriteAddress( 0x1c03, 0x1c06, led_w ),
		new Memory_WriteAddress( 0x4000, 0x4000, watchdog_reset_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Qwak CPU memory handlers
	 *
	 *************************************/
	
	public static Memory_ReadAddress qwakprot_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x01ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0200, 0x025f, MRA_RAM ),
		new Memory_ReadAddress( 0x0300, 0x03ff, MRA_RAM ),
		new Memory_ReadAddress( 0x0400, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x3000, 0x3000, input_port_0_r ),
		new Memory_ReadAddress( 0x3001, 0x3001, input_port_1_r ),
		new Memory_ReadAddress( 0x3002, 0x3002, input_port_2_r ),
		new Memory_ReadAddress( 0x3003, 0x3003, input_port_3_r ),
		new Memory_ReadAddress( 0x3004, 0x3004, input_port_4_r ),
		new Memory_ReadAddress( 0x3005, 0x3005, input_port_5_r ),
		new Memory_ReadAddress( 0x3006, 0x3006, input_port_6_r ),
		new Memory_ReadAddress( 0x3007, 0x3007, input_port_7_r ),
		new Memory_ReadAddress( 0x4000, 0x4000, input_port_8_r ),
		new Memory_ReadAddress( 0x6000, 0x600f, pokey1_r ),
		new Memory_ReadAddress( 0x7000, 0x700f, pokey2_r ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress qwakprot_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x01ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0200, 0x025f, MWA_RAM ),
		new Memory_WriteAddress( 0x0300, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x07bf, centiped_videoram_w, videoram ),
		new Memory_WriteAddress( 0x07c0, 0x07ff, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0x1000, 0x1000, irq_ack_w ),
		new Memory_WriteAddress( 0x1c00, 0x1c0f, qwakprot_paletteram_w, paletteram ),
	//	new Memory_WriteAddress( 0x2000, 0x2001, coin_counter_w ),
		new Memory_WriteAddress( 0x2003, 0x2004, led_w ),
		new Memory_WriteAddress( 0x6000, 0x600f, pokey1_w ),
		new Memory_WriteAddress( 0x7000, 0x700f, pokey2_w ),
		new Memory_WriteAddress( 0x8000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	/* The input ports are identical for the real one and the bootleg one, except
	   that one of the languages is Italian in the bootleg one instead of Spanish */
	
	#define PORTS(GAMENAME, FOURTH_LANGUAGE)										\
																					\
	static InputPortPtr input_ports_GAMENAME = new InputPortPtr(){ public void handler() { 													\
		PORT_START(); 	/* IN0 */														\
		PORT_BIT( 0x0f, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball data */		\
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Cabinet") );								\
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );								\
		PORT_DIPSETTING(    0x10, DEF_STR( "Cocktail") );								\
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );										\
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_VBLANK );							\
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball sign bit */	\
																					\
		PORT_START(); 	/* IN1 */														\
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );								\
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );								\
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 );							\
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );				\
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_TILT );								\
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );								\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );								\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );								\
																					\
		PORT_START(); 	/* IN2 */														\
		PORT_BIT( 0x0f, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball data */		\
		PORT_BIT( 0x70, IP_ACTIVE_HIGH, IPT_UNKNOWN );							\
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball sign bit */	\
																					\
		PORT_START(); 	/* IN3 */														\
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );\
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );				\
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );			\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );			\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );			\
																					\
		PORT_START(); 	/* IN4 */														\
		PORT_DIPNAME( 0x03, 0x00, "Language" );									\
		PORT_DIPSETTING(    0x00, "English" );									\
		PORT_DIPSETTING(    0x01, "German" );									\
		PORT_DIPSETTING(    0x02, "French" );									\
		PORT_DIPSETTING(    0x03, FOURTH_LANGUAGE );								\
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Lives") );									\
		PORT_DIPSETTING(    0x00, "2" );											\
		PORT_DIPSETTING(    0x04, "3" );											\
		PORT_DIPSETTING(    0x08, "4" );											\
		PORT_DIPSETTING(    0x0c, "5" );											\
		PORT_DIPNAME( 0x30, 0x10, DEF_STR( "Bonus_Life") );							\
		PORT_DIPSETTING(    0x00, "10000" );										\
		PORT_DIPSETTING(    0x10, "12000" );										\
		PORT_DIPSETTING(    0x20, "15000" );										\
		PORT_DIPSETTING(    0x30, "20000" );										\
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Difficulty") );							\
		PORT_DIPSETTING(    0x40, "Easy" );										\
		PORT_DIPSETTING(    0x00, "Hard" );										\
		PORT_DIPNAME( 0x80, 0x00, "Credit Minimum" );							\
		PORT_DIPSETTING(    0x00, "1" );											\
		PORT_DIPSETTING(    0x80, "2" );											\
																					\
		PORT_START(); 	/* IN5 */														\
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Coinage") );								\
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );									\
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );									\
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );									\
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );								\
		PORT_DIPNAME( 0x0c, 0x00, "Right Coin" );								\
		PORT_DIPSETTING(    0x00, "*1" );										\
		PORT_DIPSETTING(    0x04, "*4" );										\
		PORT_DIPSETTING(    0x08, "*5" );										\
		PORT_DIPSETTING(    0x0c, "*6" );										\
		PORT_DIPNAME( 0x10, 0x00, "Left Coin" );									\
		PORT_DIPSETTING(    0x00, "*1" );										\
		PORT_DIPSETTING(    0x10, "*2" );										\
		PORT_DIPNAME( 0xe0, 0x00, "Bonus Coins" );								\
		PORT_DIPSETTING(    0x00, "None" );										\
		PORT_DIPSETTING(    0x20, "3 credits/2 coins" );							\
		PORT_DIPSETTING(    0x40, "5 credits/4 coins" );							\
		PORT_DIPSETTING(    0x60, "6 credits/4 coins" );							\
		PORT_DIPSETTING(    0x80, "6 credits/5 coins" );							\
		PORT_DIPSETTING(    0xa0, "4 credits/3 coins" );							\
																					\
		PORT_START(); 	/* IN6, fake trackball input port. */							\
		PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_X | IPF_REVERSE, 50, 10, 0, 0, IP_KEY_NONE, IP_KEY_NONE, IP_JOY_NONE, IP_JOY_NONE );\
																					\
		PORT_START(); 	/* IN7, fake trackball input port. */							\
		PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_Y, 50, 10, 0, 0, IP_KEY_NONE, IP_KEY_NONE, IP_JOY_NONE, IP_JOY_NONE );\
																					\
		PORT_START(); 	/* IN8, fake trackball input port. */							\
		PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_X | IPF_COCKTAIL, 50, 10, 0, 0, IP_KEY_NONE, IP_KEY_NONE, IP_JOY_NONE, IP_JOY_NONE );\
																					\
		PORT_START(); 	/* IN9, fake trackball input port. */							\
		PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_Y | IPF_COCKTAIL | IPF_REVERSE, 50, 10, 0, 0, IP_KEY_NONE, IP_KEY_NONE, IP_JOY_NONE, IP_JOY_NONE );\
	INPUT_PORTS_END(); }}; 
	
	PORTS(centiped, "Spanish")
	PORTS(centipdb, "Italian")
	
	
	static InputPortPtr input_ports_centtime = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0f, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball data */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball sign bit */
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x0f, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball data */
		PORT_BIT( 0x70, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball sign bit */
	
		PORT_START(); 	/* IN3 */
		PORT_BIT( 0x0f, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
	
		PORT_START(); 	/* IN4 */
		PORT_DIPNAME( 0x03, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x01, "German" );
		PORT_DIPSETTING(    0x02, "French" );
		PORT_DIPSETTING(    0x03, "Spanish" );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x08, "4" );
		PORT_DIPSETTING(    0x0c, "5" );
		PORT_DIPNAME( 0x30, 0x10, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x10, "12000" );
		PORT_DIPSETTING(    0x20, "15000" );
		PORT_DIPSETTING(    0x30, "20000" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x40, "Easy" );
		PORT_DIPSETTING(    0x00, "Hard" );
		PORT_DIPNAME( 0x80, 0x00, "Credit Minimum" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x80, "2" );
	
		PORT_START(); 	/* IN5 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x1c, 0x00, "Game Time" );
		PORT_DIPSETTING(    0x00, "Untimed" );
		PORT_DIPSETTING(    0x04, "1 Minute" );
		PORT_DIPSETTING(    0x08, "2 Minutes" );
		PORT_DIPSETTING(    0x0c, "3 Minutes" );
		PORT_DIPSETTING(    0x10, "4 Minutes" );
		PORT_DIPSETTING(    0x14, "5 Minutes" );
		PORT_DIPSETTING(    0x18, "6 Minutes" );
		PORT_DIPSETTING(    0x1c, "7 Minutes" );
		PORT_DIPNAME( 0xe0, 0x00, "Bonus Coins" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x20, "3 credits/2 coins" );
		PORT_DIPSETTING(    0x40, "5 credits/4 coins" );
		PORT_DIPSETTING(    0x60, "6 credits/4 coins" );
		PORT_DIPSETTING(    0x80, "6 credits/5 coins" );
		PORT_DIPSETTING(    0xa0, "4 credits/3 coins" );
	
		PORT_START(); 	/* IN6, fake trackball input port. */
		PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_X | IPF_REVERSE, 50, 10, 0, 0, IP_KEY_NONE, IP_KEY_NONE, IP_JOY_NONE, IP_JOY_NONE );
	
		PORT_START(); 	/* IN7, fake trackball input port. */
		PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_Y, 50, 10, 0, 0, IP_KEY_NONE, IP_KEY_NONE, IP_JOY_NONE, IP_JOY_NONE );
	
		PORT_START(); 	/* IN8, place for cocktail trackball (not used) */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* IN9, place for cocktail trackball (not used) */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_magworm = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0f, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball data */
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball sign bit */
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x0f, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball data */
		PORT_BIT( 0x70, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball sign bit */
	
		PORT_START(); 	/* IN3 */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN4 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x00, "Right Coin" );
		PORT_DIPSETTING(    0x00, "*3" );
		PORT_DIPSETTING(    0x04, "*7" );
		PORT_DIPSETTING(    0x08, "*1/2" );
		PORT_DIPSETTING(    0x0c, "*6" );
		PORT_DIPNAME( 0x30, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x10, "German" );
		PORT_DIPSETTING(    0x20, "French" );
		PORT_DIPSETTING(    0x30, "Spanish" );
		PORT_DIPNAME( 0xc0, 0x40, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x40, "3" );
		PORT_DIPSETTING(    0x80, "4" );
		PORT_DIPSETTING(    0xc0, "5" );
	
		PORT_START(); 	/* IN5 */
		PORT_DIPNAME( 0x01, 0x00, "Left Coin" );
		PORT_DIPSETTING(    0x00, "*1" );
		PORT_DIPSETTING(    0x01, "*2" );
		PORT_DIPNAME( 0x0e, 0x00, "Bonus Coins" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x02, "3 credits/2 coins" );
		PORT_DIPSETTING(    0x04, "5 credits/4 coins" );
		PORT_DIPSETTING(    0x06, "6 credits/4 coins" );
		PORT_DIPSETTING(    0x08, "6 credits/5 coins" );
		PORT_DIPSETTING(    0x0a, "4 credits/3 coins" );
		PORT_DIPNAME( 0x30, 0x10, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x10, "12000" );
		PORT_DIPSETTING(    0x20, "15000" );
		PORT_DIPSETTING(    0x30, "20000" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x40, "Easy" );
		PORT_DIPSETTING(    0x00, "Hard" );
		PORT_DIPNAME( 0x80, 0x00, "Credit Minimum" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x80, "2" );
	
		PORT_START(); 	/* IN6, fake trackball input port. */
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_X | IPF_REVERSE, 50, 10, 0, 0 );
	
		PORT_START(); 	/* IN7, fake trackball input port. */
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_Y, 50, 10, 0, 0 );
	
		PORT_START(); 	/* IN8, fake trackball input port. */
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_X | IPF_COCKTAIL, 50, 10, 0, 0 );
	
		PORT_START(); 	/* IN9, fake trackball input port. */
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_Y | IPF_COCKTAIL | IPF_REVERSE, 50, 10, 0, 0 );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_milliped = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 $2000 */ /* see port 6 for x trackball */
		PORT_DIPNAME(0x03, 0x00, "Language" );
		PORT_DIPSETTING(   0x00, "English" );
		PORT_DIPSETTING(   0x01, "German" );
		PORT_DIPSETTING(   0x02, "French" );
		PORT_DIPSETTING(   0x03, "Spanish" );
		PORT_DIPNAME(0x0c, 0x04, "Bonus" );
		PORT_DIPSETTING(   0x00, "0" );
		PORT_DIPSETTING(   0x04, "0 1x" );
		PORT_DIPSETTING(   0x08, "0 1x 2x" );
		PORT_DIPSETTING(   0x0c, "0 1x 2x 3x" );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x40, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT ( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball sign bit */
	
		PORT_START(); 	/* IN1 $2001 */ /* see port 7 for y trackball */
		PORT_DIPNAME(0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x01, DEF_STR( "On") );
		PORT_DIPNAME(0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x02, DEF_STR( "On") );
		PORT_DIPNAME(0x04, 0x00, "Credit Minimum" );
		PORT_DIPSETTING(   0x00, "1" );
		PORT_DIPSETTING(   0x04, "2" );
		PORT_DIPNAME(0x08, 0x00, "Coin Counters" );
		PORT_DIPSETTING(   0x00, "1" );
		PORT_DIPSETTING(   0x08, "2" );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );/* trackball sign bit */
	
		PORT_START(); 	/* IN2 $2010 */
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	
		PORT_START(); 	/* IN3 $2011 */
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* 4 */ /* DSW1 $0408 */
		PORT_DIPNAME(0x01, 0x00, "Millipede Head" );
		PORT_DIPSETTING(   0x00, "Easy" );
		PORT_DIPSETTING(   0x01, "Hard" );
		PORT_DIPNAME(0x02, 0x00, "Beetle" );
		PORT_DIPSETTING(   0x00, "Easy" );
		PORT_DIPSETTING(   0x02, "Hard" );
		PORT_DIPNAME(0x0c, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(   0x00, "2" );
		PORT_DIPSETTING(   0x04, "3" );
		PORT_DIPSETTING(   0x08, "4" );
		PORT_DIPSETTING(   0x0c, "5" );
		PORT_DIPNAME(0x30, 0x10, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(   0x00, "12000" );
		PORT_DIPSETTING(   0x10, "15000" );
		PORT_DIPSETTING(   0x20, "20000" );
		PORT_DIPSETTING(   0x30, "None" );
		PORT_DIPNAME(0x40, 0x00, "Spider" );
		PORT_DIPSETTING(   0x00, "Easy" );
		PORT_DIPSETTING(   0x40, "Hard" );
		PORT_DIPNAME(0x80, 0x00, "Starting Score Select" );
		PORT_DIPSETTING(   0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(   0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* 5 */ /* DSW2 $0808 */
		PORT_DIPNAME(0x03, 0x02, DEF_STR( "Coinage") );
		PORT_DIPSETTING(   0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(   0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(   0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(   0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME(0x0c, 0x00, "Right Coin" );
		PORT_DIPSETTING(   0x00, "*1" );
		PORT_DIPSETTING(   0x04, "*4" );
		PORT_DIPSETTING(   0x08, "*5" );
		PORT_DIPSETTING(   0x0c, "*6" );
		PORT_DIPNAME(0x10, 0x00, "Left Coin" );
		PORT_DIPSETTING(   0x00, "*1" );
		PORT_DIPSETTING(   0x10, "*2" );
		PORT_DIPNAME(0xe0, 0x00, "Bonus Coins" );
		PORT_DIPSETTING(   0x00, "None" );
		PORT_DIPSETTING(   0x20, "3 credits/2 coins" );
		PORT_DIPSETTING(   0x40, "5 credits/4 coins" );
		PORT_DIPSETTING(   0x60, "6 credits/4 coins" );
		PORT_DIPSETTING(   0x80, "6 credits/5 coins" );
		PORT_DIPSETTING(   0xa0, "4 credits/3 coins" );
		PORT_DIPSETTING(   0xc0, "Demo mode" );
	
		PORT_START(); 	/* IN6, fake trackball input port. */
		PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_X | IPF_REVERSE, 50, 10, 0, 0, IP_KEY_NONE, IP_KEY_NONE, IP_JOY_NONE, IP_JOY_NONE );
	
		PORT_START(); 	/* IN7, fake trackball input port. */
		PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_Y, 50, 10, 0, 0, IP_KEY_NONE, IP_KEY_NONE, IP_JOY_NONE, IP_JOY_NONE );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_warlords = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0f, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x10, 0x00, "Diag Step" ); /* Not referenced */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, "Upright (no overlay); )
		PORT_DIPSETTING(    0x00, "Cocktail (overlay); )
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	
		PORT_START(); 	/* IN2 */
		PORT_DIPNAME( 0x03, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x01, "French" );
		PORT_DIPSETTING(    0x02, "Spanish" );
		PORT_DIPSETTING(    0x03, "German" );
		PORT_DIPNAME( 0x04, 0x00, "Music" );
		PORT_DIPSETTING(    0x00, "End of game" );
		PORT_DIPSETTING(    0x04, "High score only" );
		PORT_BIT( 0xc8, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x30, 0x00, "Credits" );
		PORT_DIPSETTING(    0x00, "1p/2p = 1 credit" );
		PORT_DIPSETTING(    0x10, "1p = 1, 2p = 2" );
		PORT_DIPSETTING(    0x20, "1p/2p = 2 credits" );
	
		PORT_START(); 	/* IN3 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x00, "Right Coin" );
		PORT_DIPSETTING(    0x00, "*1" );
		PORT_DIPSETTING(    0x04, "*4" );
		PORT_DIPSETTING(    0x08, "*5" );
		PORT_DIPSETTING(    0x0c, "*6" );
		PORT_DIPNAME( 0x10, 0x00, "Left Coin" );
		PORT_DIPSETTING(    0x00, "*1" );
		PORT_DIPSETTING(    0x10, "*2" );
		PORT_DIPNAME( 0xe0, 0x00, "Bonus Coins" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x20, "3 credits/2 coins" );
		PORT_DIPSETTING(    0x40, "5 credits/4 coins" );
		PORT_DIPSETTING(    0x60, "6 credits/4 coins" );
		PORT_DIPSETTING(    0x80, "6 credits/5 coins" );
	
		/* IN4-7 fake to control player paddles */
		PORT_START(); 
		PORT_ANALOG( 0xff, 0x80, IPT_PADDLE | IPF_PLAYER1, 50, 10, 0x1d, 0xcb );
	
		PORT_START(); 
		PORT_ANALOG( 0xff, 0x80, IPT_PADDLE | IPF_PLAYER2, 50, 10, 0x1d, 0xcb );
	
		PORT_START(); 
		PORT_ANALOG( 0xff, 0x80, IPT_PADDLE | IPF_PLAYER3, 50, 10, 0x1d, 0xcb );
	
		PORT_START(); 
		PORT_ANALOG( 0xff, 0x80, IPT_PADDLE | IPF_PLAYER4, 50, 10, 0x1d, 0xcb );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_qwakprot = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_UP );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );		/* ??? */
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_VBLANK );		/* ??? */
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_VBLANK );		/* ??? */
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );		/* ??? */
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );		/* ??? */
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START();       /* IN6 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();       /* IN7 */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );		/* ??? */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );
	
		PORT_START();       /* IN8 */
		PORT_DIPNAME( 0x01, 0x00, "DIP 1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, "DIP 2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, "DIP 3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "DIP 4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, "DIP 5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "DIP 6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "DIP 7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "DIP 8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Graphics layouts: Centipede/Millipede
	 *
	 *************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,2),
		2,
		new int[] { RGN_FRAC(1,2), 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		8,16,
		RGN_FRAC(1,2),
		2,
		new int[] { RGN_FRAC(1,2), 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		16*8
	);
	
	static GfxDecodeInfo centiped_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0, 1 ),
		new GfxDecodeInfo( REGION_GFX1, 0, spritelayout,   4, 4*4*4 ),
		new GfxDecodeInfo( -1 )
	};
	
	static GfxDecodeInfo milliped_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0, 4 ),
		new GfxDecodeInfo( REGION_GFX1, 0, spritelayout, 4*4, 4*4*4*4 ),
		new GfxDecodeInfo( -1 )
	};
	
	
	
	/*************************************
	 *
	 *	Graphics layouts: Warlords
	 *
	 *************************************/
	
	static GfxLayout warlords_charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,4),
		2,
		new int[] { RGN_FRAC(1,2), 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxDecodeInfo warlords_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x000, warlords_charlayout, 0,   8 ),
		new GfxDecodeInfo( REGION_GFX1, 0x200, warlords_charlayout, 8*4, 8*4 ),
		new GfxDecodeInfo( -1 )
	};
	
	
	
	/*************************************
	 *
	 *	Graphics layouts: Qwak
	 *
	 *************************************/
	
	static GfxLayout qwakprot_charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(3,4), RGN_FRAC(2,4), RGN_FRAC(1,4), 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout qwakprot_spritelayout = new GfxLayout
	(
		8,16,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(3,4), RGN_FRAC(2,4), RGN_FRAC(1,4), 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		16*8
	);
	
	static GfxDecodeInfo qwakprot_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, qwakprot_charlayout,   0, 1 ),
		new GfxDecodeInfo( REGION_GFX1, 0, qwakprot_spritelayout, 0, 1 ),
		new GfxDecodeInfo( -1 )
	};
	
	
	
	/*************************************
	 *
	 *	Sound interfaces
	 *
	 *************************************/
	
	static POKEYinterface centiped_pokey_interface = new POKEYinterface
	(
		1,
		12096000/8,
		new int[] { 100 },
		/* The 8 pot handlers */
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		/* The allpot handler */
		new ReadHandlerPtr[] { 0 },
	);
	
	
	static AY8910interface centipdb_ay8910_interface = new AY8910interface
	(
		1,
		12096000/8,
		new int[] { 50 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	static AY8910interface centipb2_ay8910_interface = new AY8910interface
	(
		1,
		12096000/8,
		new int[] { 100 },
		new ReadHandlerPtr[] { centipdb_rand_r },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	static POKEYinterface milliped_pokey_interface = new POKEYinterface
	(
		2,
		12096000/8,
		new int[] { 50, 50 },
		/* The 8 pot handlers */
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		/* The allpot handler */
		new ReadHandlerPtr[] { input_port_4_r, input_port_5_r },
	);
	
	
	static POKEYinterface warlords_pokey_interface = new POKEYinterface
	(
		1,
		12096000/8,
		new int[] { 100 },
		/* The 8 pot handlers */
		new ReadHandlerPtr[] { input_port_4_r },
		new ReadHandlerPtr[] { input_port_5_r },
		new ReadHandlerPtr[] { input_port_6_r },
		new ReadHandlerPtr[] { input_port_7_r },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		/* The allpot handler */
		new ReadHandlerPtr[] { 0 },
	);
	
	
	static POKEYinterface qwakprot_pokey_interface = new POKEYinterface
	(
		2,
		12096000/8,
		new int[] { 50, 50 },
		/* The 8 pot handlers */
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		/* The allpot handler */
		new ReadHandlerPtr[] { 0, 0 },
	);
	
	
	
	/*************************************
	 *
	 *	Machine drivers
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( centiped )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", M6502, 12096000/8)	/* 1.512 MHz (slows down to 0.75MHz while accessing playfield RAM) */
		MDRV_CPU_MEMORY(centiped_readmem,centiped_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(1460)
	
		MDRV_MACHINE_INIT(centiped)
		MDRV_NVRAM_HANDLER(atari_vg)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 0*8, 30*8-1)
		MDRV_GFXDECODE(centiped_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(4+4)
		MDRV_COLORTABLE_LENGTH(4+4*4*4*4)
	
		MDRV_PALETTE_INIT(centiped)
		MDRV_VIDEO_START(centiped)
		MDRV_VIDEO_UPDATE(centiped)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("pokey", POKEY, centiped_pokey_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( centipdb )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(centiped)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("pokey", AY8910, centipdb_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( centipb2 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(centiped)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(centipb2_readmem,centipb2_writemem)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("pokey", AY8910, centipb2_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( magworm )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(centiped)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("pokey", AY8910, centipb2_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( milliped )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(centiped)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(milliped_readmem,milliped_writemem)
	
		/* video hardware */
		MDRV_GFXDECODE(milliped_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(4*4+4*4)
		MDRV_COLORTABLE_LENGTH(4*4+4*4*4*4*4)
	
		MDRV_PALETTE_INIT(milliped)
		MDRV_VIDEO_START(milliped)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("pokey", POKEY, milliped_pokey_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( warlords )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(centiped)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(warlords_readmem,warlords_writemem)
	
		/* video hardware */
		MDRV_GFXDECODE(warlords_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(128)
		MDRV_COLORTABLE_LENGTH(8*4+8*4)
	
		MDRV_PALETTE_INIT(warlords)
		MDRV_VIDEO_START(warlords)
		MDRV_VIDEO_UPDATE(warlords)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("pokey", POKEY, warlords_pokey_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( qwakprot )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(centiped)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(qwakprot_readmem,qwakprot_writemem)
	
		/* video hardware */
		MDRV_GFXDECODE(qwakprot_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(16)
		MDRV_COLORTABLE_LENGTH(0)
	
		MDRV_PALETTE_INIT(NULL)
		MDRV_VIDEO_START(qwakprot)
		MDRV_VIDEO_UPDATE(qwakprot)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("pokey", POKEY, qwakprot_pokey_interface)
	MACHINE_DRIVER_END
	
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadPtr rom_centiped = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "centiped.307", 0x2000, 0x0800, 0x5ab0d9de );
		ROM_LOAD( "centiped.308", 0x2800, 0x0800, 0x4c07fd3e );
		ROM_LOAD( "centiped.309", 0x3000, 0x0800, 0xff69b424 );
		ROM_LOAD( "centiped.310", 0x3800, 0x0800, 0x44e40fa4 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "centiped.211", 0x0000, 0x0800, 0x880acfb9 );
		ROM_LOAD( "centiped.212", 0x0800, 0x0800, 0xb1397029 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_centipd2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "centiped.207", 0x2000, 0x0800, 0xb2909e2f );
		ROM_LOAD( "centiped.208", 0x2800, 0x0800, 0x110e04ff );
		ROM_LOAD( "centiped.209", 0x3000, 0x0800, 0xcc2edb26 );
		ROM_LOAD( "centiped.210", 0x3800, 0x0800, 0x93999153 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "centiped.211", 0x0000, 0x0800, 0x880acfb9 );
		ROM_LOAD( "centiped.212", 0x0800, 0x0800, 0xb1397029 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_centtime = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "cent_d1.bin",  0x2000, 0x0800, 0xc4d995eb );
		ROM_LOAD( "cent_e1.bin",  0x2800, 0x0800, 0xbcdebe1b );
		ROM_LOAD( "cent_fh1.bin", 0x3000, 0x0800, 0x66d7b04a );
		ROM_LOAD( "cent_j1.bin",  0x3800, 0x0800, 0x33ce4640 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "centiped.211", 0x0000, 0x0800, 0x880acfb9 );
		ROM_LOAD( "centiped.212", 0x0800, 0x0800, 0xb1397029 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_centipdb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "olympia.c28",  0x2000, 0x0800, 0x8a744e57 );
		ROM_LOAD( "olympia.c29",  0x2800, 0x0800, 0xbb897b10 );
		ROM_LOAD( "olympia.c30",  0x3000, 0x0800, 0x2297c2ac );
		ROM_LOAD( "olympia.c31",  0x3800, 0x0800, 0xcc529d6b );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "olympia.c32",  0x0000, 0x0800, 0xd91b9724 );
		ROM_LOAD( "olympia.c33",  0x0800, 0x0800, 0x1a6acd02 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_centipb2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "d1",  		  0x2000, 0x0800, 0xb17b8e0b );
		ROM_LOAD( "e1",  		  0x2800, 0x0800, 0x7684398e );
		ROM_LOAD( "h1",  		  0x3000, 0x0800, 0x74580fe4 );
		ROM_LOAD( "j1",  		  0x3800, 0x0800, 0x84600161 );
		ROM_RELOAD( 	  		  0x7800, 0x0800 );
		ROM_LOAD( "k1",  		  0x6000, 0x0800, 0xf1aa329b );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "centiped.211", 0x0000, 0x0800, 0x880acfb9 );
		ROM_LOAD( "centiped.212", 0x0800, 0x0800, 0xb1397029 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_magworm = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "magworm.0",	  0x2000, 0x0800, 0xa88e970a );
		ROM_LOAD( "magworm.1",	  0x2800, 0x0800, 0x7a04047e );
		ROM_LOAD( "magworm.2",	  0x3000, 0x0800, 0xf127f1c3 );
		ROM_LOAD( "magworm.3",	  0x3800, 0x0800, 0x478d92b4 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "magworm.4",    0x0000, 0x0800, 0xcea64e1a );
		ROM_LOAD( "magworm.5",    0x0800, 0x0800, 0x24558ea5 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_milliped = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "milliped.104", 0x4000, 0x1000, 0x40711675 );
		ROM_LOAD( "milliped.103", 0x5000, 0x1000, 0xfb01baf2 );
		ROM_LOAD( "milliped.102", 0x6000, 0x1000, 0x62e137e0 );
		ROM_LOAD( "milliped.101", 0x7000, 0x1000, 0x46752c7d );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "milliped.107", 0x0000, 0x0800, 0x68c3437a );
		ROM_LOAD( "milliped.106", 0x0800, 0x0800, 0xf4468045 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_warlords = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "037154.1m",    0x5000, 0x0800, 0x18006c87 );
		ROM_LOAD( "037153.1k",    0x5800, 0x0800, 0x67758f4c );
		ROM_LOAD( "037158.1j",    0x6000, 0x0800, 0x1f043a86 );
		ROM_LOAD( "037157.1h",    0x6800, 0x0800, 0x1a639100 );
		ROM_LOAD( "037156.1e",    0x7000, 0x0800, 0x534f34b4 );
		ROM_LOAD( "037155.1d",    0x7800, 0x0800, 0x23b94210 );
	
		ROM_REGION( 0x0800, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "037159.6e",    0x0000, 0x0800, 0xff979a08 );
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 );
		/* Only the first 0x80 bytes are used by the hardware. A7 is grounded. */
		/* Bytes 0x00-0x3f are used fore the color cocktail version. */
		/* Bytes 0x40-0x7f are for the upright version of the cabinet with a */
		/* mirror and painted background. */
		ROM_LOAD( "warlord.clr",  0x0000, 0x0100, 0xa2c5c277 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_qwakprot = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "qwak8000.bin", 0x8000, 0x1000, 0x4d002d8a );
		ROM_LOAD( "qwak9000.bin", 0x9000, 0x1000, 0xe0c78fd7 );
		ROM_LOAD( "qwaka000.bin", 0xa000, 0x1000, 0xe5770fc9 );
		ROM_LOAD( "qwakb000.bin", 0xb000, 0x1000, 0x90771cc0 );
		ROM_RELOAD(               0xf000, 0x1000 );/* for the reset and interrupt vectors */
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "qwakgfx0.bin", 0x0000, 0x1000, 0xbed2c067 );
		ROM_LOAD( "qwakgfx1.bin", 0x1000, 0x1000, 0x73a31d28 );
		ROM_LOAD( "qwakgfx2.bin", 0x2000, 0x1000, 0x07fd9e80 );
		ROM_LOAD( "qwakgfx3.bin", 0x3000, 0x1000, 0xe8416f2b );
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Driver initialization
	 *
	 *************************************/
	
	static DRIVER_INIT( centipdb )
	{
		install_mem_write_handler(0, 0x1000, 0x100f, centipdb_AY8910_w);
		install_mem_read_handler(0, 0x1000, 0x100f, centipdb_AY8910_r);
		install_mem_read_handler(0, 0x1780, 0x1780, centipdb_rand_r);
	}
	
	
	static DRIVER_INIT( magworm )
	{
		install_mem_write_handler(0, 0x1001, 0x1001, AY8910_control_port_0_w);
		install_mem_write_handler(0, 0x1003, 0x1003, AY8910_write_port_0_w);
		install_mem_read_handler(0, 0x1003, 0x1003, AY8910_read_port_0_r);
	}
	
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_centiped	   = new GameDriver("1980"	,"centiped"	,"centiped.java"	,rom_centiped,null	,machine_driver_centiped	,input_ports_centiped	,null	,ROT270	,	"Atari", "Centipede (revision 3)" )
	public static GameDriver driver_centipd2	   = new GameDriver("1980"	,"centipd2"	,"centiped.java"	,rom_centipd2,driver_centiped	,machine_driver_centiped	,input_ports_centiped	,null	,ROT270	,	"Atari", "Centipede (revision 2)" )
	public static GameDriver driver_centtime	   = new GameDriver("1980"	,"centtime"	,"centiped.java"	,rom_centtime,driver_centiped	,machine_driver_centiped	,input_ports_centtime	,null	,ROT270	,	"Atari", "Centipede (1 player, timed)" )
	public static GameDriver driver_centipdb	   = new GameDriver("1980"	,"centipdb"	,"centiped.java"	,rom_centipdb,driver_centiped	,machine_driver_centipdb	,input_ports_centipdb	,init_centipdb	,ROT270	,	"bootleg", "Centipede (bootleg set 1)" )
	public static GameDriver driver_centipb2	   = new GameDriver("1980"	,"centipb2"	,"centiped.java"	,rom_centipb2,driver_centiped	,machine_driver_centipb2	,input_ports_centiped	,null	,ROT270	,	"bootleg", "Centipede (bootleg set 2)" )
	public static GameDriver driver_magworm	   = new GameDriver("1980"	,"magworm"	,"centiped.java"	,rom_magworm,driver_centiped	,machine_driver_magworm	,input_ports_magworm	,init_magworm	,ROT270	,	"bootleg", "Magic Worm (bootleg)" )
	public static GameDriver driver_milliped	   = new GameDriver("1982"	,"milliped"	,"centiped.java"	,rom_milliped,null	,machine_driver_milliped	,input_ports_milliped	,null	,ROT270	,	"Atari", "Millipede" )
	
	public static GameDriver driver_warlords	   = new GameDriver("1980"	,"warlords"	,"centiped.java"	,rom_warlords,null	,machine_driver_warlords	,input_ports_warlords	,null	,ROT0	,	"Atari", "Warlords" )
	public static GameDriver driver_qwakprot	   = new GameDriver("1982"	,"qwakprot"	,"centiped.java"	,rom_qwakprot,null	,machine_driver_qwakprot	,input_ports_qwakprot	,null	,ROT270	,	"Atari", "Qwak (prototype)", GAME_NO_COCKTAIL )
}
