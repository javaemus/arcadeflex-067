/***************************************************************************

 Galaxian/Moon Cresta hardware


Main clock: XTAL = 18.432 MHz
Z80 Clock: XTAL/6 = 3.072 MHz
Horizontal video frequency: HSYNC = XTAL/3/192/2 = 16 kHz
Video frequency: VSYNC = HSYNC/132/2 = 60.606060 Hz
VBlank duration: 1/VSYNC * (20/132) = 2500 us


Notes:
-----

- The only code difference between 'galaxian' and 'galmidw' is that the
  'BONUS SHIP' text is printed on a different line.


TODO:
----

- Problems with Galaxian based on the observation of a real machine:

  - Starfield is incorrect.  The speed and flashing frequency is fine, but the
    stars appear in different positions.
  - Background humming is incorrect.  It's faster on a real machine
  - Explosion sound is much softer.  Filter involved?

- $4800-4bff in Streaking/Ghost Muncher

- Need valid color prom for Fantazia. Current one is slightly damaged.



Moon Cresta versions supported:
------------------------------

mooncrst    Nichibutsu     - later revision with better demo mode and
						 text for docking. Encrypted. No ROM/RAM check
mooncrsu    Nichibutsu USA - later revision with better demo mode and
						 text for docking. Unencrypted. No ROM/RAM check
mooncrsa    Nichibutsu     - older revision with better demo mode and
						 text for docking. Encrypted. No ROM/RAM check
mooncrs2    Nichibutsu     - probably first revision (no patches) and ROM/RAM check code.
                             This came from a bootleg board, with the logos erased
						 from the graphics
mooncrsg    Gremlin        - same docking text as mooncrst
mooncrsb    bootleg of mooncrs2. ROM/RAM check erased.


Notes about 'azurian' :
-----------------------

  bit 6 of IN1 is linked with bit 2 of IN2 (check code at 0x05b3) to set difficulty :

	bit 6  bit 2	contents of
	 IN1 	 IN2		  0x40f4   			consequences			difficulty

	 OFF 	 OFF		     2     		aliens move 2 frames out of 3		easy
	 ON  	 OFF		     4     		aliens move 4 frames out of 5		hard
	 OFF 	 ON 		     3     		aliens move 3 frames out of 4		normal
	 ON  	 ON 		     5     		aliens move 5 frames out of 6		very hard

  aliens movements is handled by routine at 0x1d59 :

    - alien 1 moves when 0x4044 != 0 else contents of 0x40f4 is stored at 0x4044
    - alien 2 moves when 0x4054 != 0 else contents of 0x40f4 is stored at 0x4054
    - alien 3 moves when 0x4064 != 0 else contents of 0x40f4 is stored at 0x4064


Notes about 'smooncrs' :
------------------------

  Due to code at 0x2b1c and 0x3306, the game ALWAYS checks the inputs for player 1
  (even for player 2 when "Cabinet" Dip Switch is set to "Cocktail")


Notes about 'scorpnmc' :
-----------------------

  As the START buttons are also the buttons for player 1, how should I map them ?
  I've coded this the same way as in 'checkman', but I'm not sure this is correct.

  I can't tell if it's a bug, but if you reset the game when the screen is flipped,
  the screens remains flipped (the "flip screen" routine doesn't seem to be called) !



Notes about 'frogg' :
---------------------

  If bit 5 of IN0 or bit 5 of IN1 is HIGH, something strange occurs (check code
  at 0x3580) : each time you press START2 a counter at 0x47da is incremented.
  When this counter reaches 0x2f, each next time you press START2, it acts as if
  you had pressed COIN2, so credits are added !
  Bit 5 of IN0 is tested if "Cabinet" Dip Switch is set to "Upright" and
  bit 5 of IN1 is tested if "Cabinet" Dip Switch is set to "Cocktail".



TO DO :
-------

  - smooncrs : fix read/writes at/to unmapped memory (when player 2, "cocktail" mode)
               fix the ?#! bug with "bullets" (when player 2, "cocktail" mode)
  - zigzag   : full Dip Switches and Inputs
  - zigzag2  : full Dip Switches and Inputs
  - jumpbug  : full Dip Switches and Inputs
  - jumpbugb : full Dip Switches and Inputs
  - levers   : full Dip Switches and Inputs
  - kingball : full Dip Switches and Inputs
  - kingbalj : full Dip Switches and Inputs
  - frogg    : fix read/writes at/to unmapped/wrong memory
  - scprpng  : fix read/writes at/to unmapped/wrong memory

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class galaxian
{
	
	
	DRIVER_INIT( cclimbrj );
	
	extern struct AY8910interface cclimber_ay8910_interface;
	extern struct AY8910interface swimmer_ay8910_interface;
	extern struct CustomSound_interface cclimber_custom_interface;
	
	
	/* Send sound data to the sound cpu and cause an nmi */
	public static WriteHandlerPtr checkman_sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch_w (0,data);
		cpu_set_irq_line (1, IRQ_LINE_NMI, PULSE_LINE);
	} };
	
	
	public static Memory_ReadAddress galaxian_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x53ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5400, 0x57ff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x5800, 0x58ff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, input_port_0_r ),
		new Memory_ReadAddress( 0x6800, 0x6800, input_port_1_r ),
		new Memory_ReadAddress( 0x7000, 0x7000, input_port_2_r ),
		new Memory_ReadAddress( 0x7800, 0x78ff, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress galaxian_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x5000, 0x53ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5800, 0x583f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5840, 0x585f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5860, 0x587f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5880, 0x58ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6000, 0x6001, galaxian_leds_w ),
		new Memory_WriteAddress( 0x6002, 0x6002, galaxian_coin_lockout_w ),
		new Memory_WriteAddress( 0x6003, 0x6003, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0x6004, 0x6007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0x6800, 0x6802, galaxian_background_enable_w ),
		new Memory_WriteAddress( 0x6803, 0x6803, galaxian_noise_enable_w ),
		new Memory_WriteAddress( 0x6805, 0x6805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0x6806, 0x6807, galaxian_vol_w ),
		new Memory_WriteAddress( 0x7001, 0x7001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x7004, 0x7004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0x7006, 0x7006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x7007, 0x7007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x7800, 0x7800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress gmgalax_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_BANK1 ),	/* banked code */
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x53ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5400, 0x57ff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x5800, 0x58ff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, gmgalax_input_port_0_r ),
		new Memory_ReadAddress( 0x6800, 0x6800, gmgalax_input_port_1_r ),
		new Memory_ReadAddress( 0x7000, 0x7000, gmgalax_input_port_2_r ),
		new Memory_ReadAddress( 0x7800, 0x78ff, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress mooncrst_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9000, 0x93ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9400, 0x97ff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x9800, 0x98ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa000, input_port_0_r ),
		new Memory_ReadAddress( 0xa800, 0xa800, input_port_1_r ),
		new Memory_ReadAddress( 0xb000, 0xb000, input_port_2_r ),
		new Memory_ReadAddress( 0xb800, 0xb800, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mooncrst_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x9880, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa003, 0xa003, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0xa004, 0xa007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0xa800, 0xa802, galaxian_background_enable_w ),
		new Memory_WriteAddress( 0xa803, 0xa803, galaxian_noise_enable_w ),
		new Memory_WriteAddress( 0xa805, 0xa805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0xa806, 0xa807, galaxian_vol_w ),
		new Memory_WriteAddress( 0xb000, 0xb000, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xb004, 0xb004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0xb800, 0xb800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress mshuttle_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x4fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x9880, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xa000, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xa001, 0xa001, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xa002, 0xa002, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0xa004, 0xa004, cclimber_sample_trigger_w ),
		new Memory_WriteAddress( 0xa800, 0xa800, cclimber_sample_rate_w ),
		new Memory_WriteAddress( 0xb000, 0xb000, cclimber_sample_volume_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort mshuttle_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0c, 0x0c, AY8910_read_port_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort mshuttle_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x08, 0x08, AY8910_control_port_0_w ),
		new IO_WritePort( 0x09, 0x09, AY8910_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress skybase_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x9880, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa002, 0xa002, galaxian_gfxbank_w ),
		new Memory_WriteAddress( 0xa004, 0xa007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0xa800, 0xa802, galaxian_background_enable_w ),
		new Memory_WriteAddress( 0xa803, 0xa803, galaxian_noise_enable_w ),
		new Memory_WriteAddress( 0xa805, 0xa805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0xa806, 0xa807, galaxian_vol_w ),
		new Memory_WriteAddress( 0xb000, 0xb000, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xb004, 0xb004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0xb800, 0xb800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress scramblb_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4bff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x50ff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, input_port_0_r ),
		new Memory_ReadAddress( 0x6800, 0x6800, input_port_1_r ),
		new Memory_ReadAddress( 0x7000, 0x7000, input_port_2_r ),
		new Memory_ReadAddress( 0x7800, 0x7800, watchdog_reset_r ),
		new Memory_ReadAddress( 0x8102, 0x8102, scramblb_protection_1_r ),
		new Memory_ReadAddress( 0x8202, 0x8202, scramblb_protection_2_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress scramblb_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6000, 0x6001, MWA_NOP ),  /* sound triggers */
		new Memory_WriteAddress( 0x6003, 0x6003, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0x6004, 0x6007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0x6800, 0x6802, galaxian_background_enable_w ),
		new Memory_WriteAddress( 0x6803, 0x6803, galaxian_noise_enable_w ),
		new Memory_WriteAddress( 0x6805, 0x6805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0x6806, 0x6807, galaxian_vol_w ),
		new Memory_WriteAddress( 0x7001, 0x7001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x7002, 0x7002, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0x7003, 0x7003, scramble_background_enable_w ),
		new Memory_WriteAddress( 0x7004, 0x7004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0x7006, 0x7006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x7007, 0x7007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x7800, 0x7800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress jumpbug_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4bff, MRA_RAM ),
		new Memory_ReadAddress( 0x4c00, 0x4fff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x5000, 0x50ff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, input_port_0_r ),
		new Memory_ReadAddress( 0x6800, 0x6800, input_port_1_r ),
		new Memory_ReadAddress( 0x7000, 0x7000, input_port_2_r ),
		new Memory_ReadAddress( 0x8000, 0xafff, MRA_ROM ),
		new Memory_ReadAddress( 0xb000, 0xbfff, jumpbug_protection_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress jumpbug_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x4c00, 0x4fff, galaxian_videoram_w ),
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x5800, 0x5800, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x5900, 0x5900, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x6002, 0x6006, galaxian_gfxbank_w ),
		new Memory_WriteAddress( 0x7001, 0x7001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x7002, 0x7002, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0x7004, 0x7004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0x7006, 0x7006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x7007, 0x7007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x8000, 0xafff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress checkman_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x9880, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa004, 0xa007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0xa800, 0xa802, galaxian_background_enable_w ),
		new Memory_WriteAddress( 0xa803, 0xa803, galaxian_noise_enable_w ),
		new Memory_WriteAddress( 0xa805, 0xa805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0xa806, 0xa807, galaxian_vol_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xb004, 0xb004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0xb800, 0xb800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress checkmaj_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x5000, 0x53ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5800, 0x583f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5840, 0x585f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5860, 0x587f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5880, 0x58ff, MWA_RAM ),
		new Memory_WriteAddress( 0x7001, 0x7001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x7006, 0x7006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x7007, 0x7007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x7800, 0x7800, checkman_sound_command_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_WritePort checkman_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0, 0, checkman_sound_command_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress checkman_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x23ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress checkman_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x23ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort checkman_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x03, 0x03, soundlatch_r ),
		new IO_ReadPort( 0x06, 0x06, AY8910_read_port_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort checkman_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x04, 0x04, AY8910_control_port_0_w ),
		new IO_WritePort( 0x05, 0x05, AY8910_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress checkmaj_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x83ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa002, 0xa002, AY8910_read_port_0_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress checkmaj_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xa000, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0xa001, 0xa001, AY8910_write_port_0_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress kingball_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x2fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x9880, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xa001, galaxian_leds_w ),
		new Memory_WriteAddress( 0xa002, 0xa002, galaxian_coin_lockout_w ),
		new Memory_WriteAddress( 0xa003, 0xa003, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0xa004, 0xa007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0xa800, 0xa802, galaxian_background_enable_w ),
		new Memory_WriteAddress( 0xa803, 0xa803, galaxian_noise_enable_w ),
		new Memory_WriteAddress( 0xa805, 0xa805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0xa806, 0xa807, galaxian_vol_w ), //
		new Memory_WriteAddress( 0xb000, 0xb000, kingball_sound1_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xb002, 0xb002, kingball_sound2_w ),
		new Memory_WriteAddress( 0xb003, 0xb003, kingball_speech_dip_w ),
		new Memory_WriteAddress( 0xb004, 0xb004, MWA_NOP ),					/* noise generator enable */
		new Memory_WriteAddress( 0xb006, 0xb006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0xb800, 0xb800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress kingball_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress kingball_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort kingball_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, soundlatch_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort kingball_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, DAC_0_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress _4in1_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_BANK1 ),	/* banked game code */
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x53ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5400, 0x57ff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x5800, 0x58ff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, input_port_0_r ),
		new Memory_ReadAddress( 0x6800, 0x6800, _4in1_input_port_1_r ),
		new Memory_ReadAddress( 0x7000, 0x7000, _4in1_input_port_2_r ),
		new Memory_ReadAddress( 0x7800, 0x78ff, watchdog_reset_r ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_ROM ),	/* fixed menu code */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress _4in1_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),	/* banked game code */
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x5000, 0x53ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5800, 0x583f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5840, 0x585f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5860, 0x587f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5880, 0x58ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6000, 0x6001, galaxian_leds_w ),
	//	new Memory_WriteAddress( 0x6002, 0x6002, galaxian_coin_lockout_w ),
		new Memory_WriteAddress( 0x6003, 0x6003, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0x6004, 0x6007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0x6800, 0x6802, galaxian_background_enable_w ),
	//	new Memory_WriteAddress( 0x6803, 0x6803, galaxian_noise_enable_w ), /* not hooked up? */
		new Memory_WriteAddress( 0x6805, 0x6805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0x6806, 0x6807, galaxian_vol_w ),
		new Memory_WriteAddress( 0x7001, 0x7001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x7004, 0x7004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0x7006, 0x7006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x7007, 0x7007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x7800, 0x7800, galaxian_pitch_w ),
		new Memory_WriteAddress( 0x8000, 0x8000, _4in1_bank_w ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_ROM ), /* Fixed Menu Code */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress bagmanmc_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x67ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9000, 0x93ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9400, 0x97ff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x9800, 0x98ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa000, input_port_0_r ),
		new Memory_ReadAddress( 0xa800, 0xa800, input_port_1_r ),
		new Memory_ReadAddress( 0xb000, 0xb000, input_port_2_r ),
		new Memory_ReadAddress( 0xb800, 0xb800, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress bagmanmc_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x67ff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x9880, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa003, 0xa003, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0xa803, 0xa803, galaxian_noise_enable_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0xb800, 0xb800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress froggrmc_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa800, 0xa800, soundlatch_w ),
		new Memory_WriteAddress( 0xb000, 0xb000, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, froggrmc_sh_irqtrigger_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress zigzag_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x2fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0x3000, 0x3fff, MRA_BANK2 ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x53ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5800, 0x58ff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, input_port_0_r ),
		new Memory_ReadAddress( 0x6800, 0x6800, input_port_1_r ),
		new Memory_ReadAddress( 0x7000, 0x7000, input_port_2_r ),
		new Memory_ReadAddress( 0x7800, 0x7800, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress zigzag_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4800, MWA_NOP ),	/* part of the 8910 interface */
		new Memory_WriteAddress( 0x4801, 0x4801, zigzag_8910_data_trigger_w ),
		new Memory_WriteAddress( 0x4803, 0x4803, zigzag_8910_control_trigger_w ),
		new Memory_WriteAddress( 0x4900, 0x49ff, zigzag_8910_latch_w ),
		new Memory_WriteAddress( 0x4a00, 0x4a00, MWA_NOP ),	/* part of the 8910 interface */
		new Memory_WriteAddress( 0x5000, 0x53ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5800, 0x583f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5840, 0x587f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),	/* no bulletsram, all sprites */
		new Memory_WriteAddress( 0x5880, 0x58ff, MWA_RAM ),
		new Memory_WriteAddress( 0x7001, 0x7001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x7002, 0x7002, zigzag_sillyprotection_w ),
		new Memory_WriteAddress( 0x7006, 0x7006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x7007, 0x7007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress scorpnmc_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x67ff, MRA_ROM ),
		new Memory_ReadAddress( 0x9000, 0x93ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9400, 0x97ff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x9800, 0x98ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa000, input_port_0_r ),
		new Memory_ReadAddress( 0xa800, 0xa800, input_port_1_r ),
		new Memory_ReadAddress( 0xb001, 0xb001, input_port_2_r ),
		new Memory_ReadAddress( 0xb002, 0xb002, input_port_3_r ),
		new Memory_ReadAddress( 0xb800, 0xb800, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress scorpnmc_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x5000, 0x67ff, MWA_ROM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x9880, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa003, 0xa003, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0xa004, 0xa007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0xa800, 0xa802, galaxian_background_enable_w ),
		new Memory_WriteAddress( 0xa803, 0xa803, galaxian_noise_enable_w ),
		new Memory_WriteAddress( 0xa805, 0xa805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0xa806, 0xa807, galaxian_vol_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xb004, 0xb004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0xb800, 0xb800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress dkongjrm_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x6fff, MRA_RAM ),
		new Memory_ReadAddress( 0x7000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x9000, 0x93ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa0ff, input_port_0_r ),
		new Memory_ReadAddress( 0xa800, 0xa8ff, input_port_1_r ),
		new Memory_ReadAddress( 0xb000, 0xb0ff, input_port_2_r ),
		new Memory_ReadAddress( 0xb800, 0xb800, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress dkongjrm_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x6fff, MWA_RAM ),
		new Memory_WriteAddress( 0x7000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x987f, MWA_RAM, galaxian_spriteram,  galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x98c0, 0x98ff, MWA_RAM, galaxian_spriteram2, galaxian_spriteram2_size ),
		new Memory_WriteAddress( 0xa003, 0xa003, galaxian_coin_counter_w ),
	  //new Memory_WriteAddress( 0xa004, 0xa007, galaxian_lfo_freq_w ),
		new Memory_WriteAddress( 0xa800, 0xa802, galaxian_background_enable_w ),
		new Memory_WriteAddress( 0xa803, 0xa803, galaxian_noise_enable_w ),
	  //new Memory_WriteAddress( 0xa805, 0xa805, galaxian_shoot_enable_w ),
		new Memory_WriteAddress( 0xa806, 0xa807, galaxian_vol_w ),
		new Memory_WriteAddress( 0xb000, 0xb000, galaxian_gfxbank_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, galaxian_nmi_enable_w ),
	  //new Memory_WriteAddress( 0xb004, 0xb004, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0xb800, 0xb800, galaxian_pitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_galaxian = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "7000" );
		PORT_DIPSETTING(    0x01, "10000" );
		PORT_DIPSETTING(    0x02, "12000" );
		PORT_DIPSETTING(    0x03, "20000" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_superg = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x01, "4000" );
		PORT_DIPSETTING(    0x02, "5000" );
		PORT_DIPSETTING(    0x03, "7000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_swarm = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x01, "10000" );
		PORT_DIPSETTING(    0x02, "20000" );
		PORT_DIPSETTING(    0x03, "40000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Difficulty") );	/* aliens "flying" simultaneously */
		PORT_DIPSETTING(    0x00, "Easy" );			/* less aliens */
		PORT_DIPSETTING(    0x08, "Hard" );			/* more aliens */
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_gmgalax = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* Ghost Muncher - IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL);
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x20, 0x00, "Ghost Muncher - Cabinet" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
	
		PORT_START();       /* Ghost Muncher - IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0xc0, 0x40, "Ghost Muncher - Bonus Life" );
		PORT_DIPSETTING(    0x40, "10000" );
		PORT_DIPSETTING(    0x80, "15000" );
		PORT_DIPSETTING(    0xc0, "20000" );
		PORT_DIPSETTING(    0x00, "None" );
	
		PORT_START();       /* Ghost Muncher - DSW0 */
		PORT_DIPNAME( 0x03, 0x02, "Ghost Muncher - Coinage" );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x04, "Ghost Muncher - Lives" );
		PORT_DIPSETTING(    0x0c, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* Galaxian - IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, "Galaxian - Cabinet" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START();       /* Galaxian - IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, "Galaxian - Coinage" );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* Galaxian - DSW0 */
		PORT_DIPNAME( 0x03, 0x01, "Galaxian - Bonus Life" );
		PORT_DIPSETTING(    0x01, "4000" );
		PORT_DIPSETTING(    0x02, "5000" );
		PORT_DIPSETTING(    0x03, "7000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x04, 0x00, "Galaxian - Lives" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* fake - game select */
		PORT_BITX( 0x01, 0x00, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Game Select", KEYCODE_F1, IP_JOY_NONE );
		PORT_DIPSETTING( 0x00, "Ghost Muncher" );
		PORT_DIPSETTING( 0x01, "Galaxian" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_zerotime = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x40, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, "A 1C/1C 2C/2C  B 1C/2C " );
		PORT_DIPSETTING(    0xc0, "A 1C/1C 2C/3C  B 1C/3C " );
		PORT_DIPSETTING(    0x00, "A 1C/2C 2C/4C  B 1C/4C " );
		PORT_DIPSETTING(    0x80, "A 1C/2C 2C/5C  B 1C/5C " );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x03, "6000" );
		PORT_DIPSETTING(    0x02, "7000" );
		PORT_DIPSETTING(    0x01, "9000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Difficulty") );	/* player's bullet speed */
		PORT_DIPSETTING(    0x00, "Easy" );			/* gap of 6 pixels */
		PORT_DIPSETTING(    0x08, "Hard" );			/* gap of 8 pixels */
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_pisces = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x01, "20000" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, "A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x00, "A 1C/1C  B 1C/6C" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	/* Same as 'pisces', but different "Coinage" Dip Switch */
	static InputPortPtr input_ports_piscesb = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x01, "20000" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, "A 2C/1C  B 1C/2C 2C/5C" );
		PORT_DIPSETTING(    0x00, "A 1C/1C  B 1C/5C" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_gteikokb = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );	// Not tested due to code removed at 0x00ab, 0x1b26 and 0x1c97
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );	// Not tested due to code removed at 0x1901
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "7000" );
		PORT_DIPSETTING(    0x01, "None" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );		// Not read due to code at 0x012b
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	/* same as gteikokb with cabinet reversed */
	static InputPortPtr input_ports_gteikob2 = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );	// Not tested due to code removed at 0x00ab, 0x1b26 and 0x1c97
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );	// Not tested due to code removed at 0x1901
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "7000" );
		PORT_DIPSETTING(    0x01, "None" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );		// Not read due to code at 0x012b
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_spacbatt = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, "A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x00, "A 1C/1C  B 1C/6C" );
		PORT_DIPSETTING(    0x80, "A 1C/2C  B 1C/6C" );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x01, "4000" );
		PORT_DIPSETTING(    0x02, "5000" );
		PORT_DIPSETTING(    0x03, "7000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_batman2 = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON2 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30000" );
		PORT_DIPSETTING(    0x01, "50000" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_warofbug = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP   | IPF_8WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x01, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x03, "4" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "500000" );
		PORT_DIPSETTING(    0x00, "750000" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_redufo = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, "A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x00, "A 1C/1C  B 1C/6C" );
		PORT_DIPSETTING(    0x80, "A 1C/2C  B 1C/12C" );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x01, "4000" );
		PORT_DIPSETTING(    0x02, "5000" );
		PORT_DIPSETTING(    0x03, "7000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_exodus = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );	// Not tested due to code removed at 0x1901 and 0x191a
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "7000" );
		PORT_DIPSETTING(    0x01, "None" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );		// Not read due to code at 0x012b
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_streakng = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL);
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0xc0, 0x40, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x40, "10000" );
		PORT_DIPSETTING(    0x80, "15000" );
		PORT_DIPSETTING(    0xc0, "20000" );
		PORT_DIPSETTING(    0x00, "None" );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x0c, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_pacmanbl = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "15000" );
		PORT_DIPSETTING(    0x01, "20000" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x02, "Hard" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_devilfsg = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x01, "15000" );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_zigzag = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000 60000" );
		PORT_DIPSETTING(    0x04, "20000 60000" );
		PORT_DIPSETTING(    0x08, "30000 60000" );
		PORT_DIPSETTING(    0x0c, "40000 60000" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_scramblb = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START(); 	/* IN2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x08, "5" );
		PORT_BITX( 0,       0x0c, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_jumpbug = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_DIPNAME( 0x40, 0x00, "Difficulty ?" );
		PORT_DIPSETTING(    0x00, "Hard?" );
		PORT_DIPSETTING(    0x40, "Easy?" );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, "A 2C/1C  B 2C/1C" );
		PORT_DIPSETTING(    0x08, "A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x00, "A 1C/1C  B 1C/1C" );
		PORT_DIPSETTING(    0x0c, "A 1C/1C  B 1C/6C" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_levers = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );  /* probably unused */
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );	/* used - MUST be ON */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );  /* probably unused */
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_azurian = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_SPECIAL );	/* "linked" with bit 2 of IN2 */
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x80, "5" );
	
		PORT_START();       /* IN2 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "5000" );
		PORT_DIPSETTING(    0x02, "7000" );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_SPECIAL );	/* "linked" with bit 6 of IN1 */
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* fake port to handle routine at 0x05b3 that stores value at 0x40f4 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "Easy" );
		PORT_DIPSETTING(    0x01, "Normal" );
		PORT_DIPSETTING(    0x02, "Hard" );
		PORT_DIPSETTING(    0x00, "Very hard" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_orbitron = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_DIPNAME( 0x60, 0x40, DEF_STR( "Coinage") );		/* Routine at 0x00e1 */
		PORT_DIPSETTING(    0x00, "A 2C/1C  B 1C/3C" );
	//	PORT_DIPSETTING(    0x20, "A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x40, "A 1C/1C  B 1C/6C" );
	//	PORT_DIPSETTING(    0x60, "A 1C/1C  B 1C/6C" );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_blkhole = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "5000" );
		PORT_DIPSETTING(    0x01, "10000" );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_checkman = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );/* p2 tiles right */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );/* also p1 tiles left */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );/* also p1 tiles right */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );/* p2 tiles left */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_COCKTAIL );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, "A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x00, "A 1C/1C  B 1C/6C" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "100000" );
		PORT_DIPSETTING(    0x04, "200000" );
		PORT_DIPNAME( 0x08, 0x00, "Difficulty Increases At Level" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_checkmaj = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL);/* p2 tiles right */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL);/* p2 tiles left */
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, "A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x00, "A 1C/1C  B 1C/6C" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START(); 	/* DSW */
	 	PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "100000" );
		PORT_DIPSETTING(    0x04, "200000" );
		PORT_DIPNAME( 0x08, 0x00, "Difficulty Increases At Level" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 );/* p1 tiles right */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );/* p1 tiles left */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_dingo = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );/* 1st Button 1 */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );/* 2nd Button 1 */
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x80, "A 1C/1C  B 1C/5C" );
		PORT_DIPSETTING(    0x00, "A 2C/1C  B 1C/3C" );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easiest" );
		PORT_DIPSETTING(    0x01, "Easy" );
		PORT_DIPSETTING(    0x02, "Hard" );
		PORT_DIPSETTING(    0x03, "Hardest" );
	 	PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "20000" );
		PORT_DIPSETTING(    0x08, "40000" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );/* Yes, the game reads both of these */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );/* Check code at 0x22e1 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mooncrst = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* "reset" on schematics */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30000" );
		PORT_DIPSETTING(    0x40, "50000" );
		PORT_DIPNAME( 0x80, 0x80, "Language" );
		PORT_DIPSETTING(    0x80, "English" );
		PORT_DIPSETTING(    0x00, "Japanese" );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mooncrsg = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* "reset" on schematics */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30000" );
		PORT_DIPSETTING(    0x40, "50000" );
	//	PORT_DIPNAME( 0x80, 0x80, "Language" );		Always "English" due to code at 0x2f77
	//	PORT_DIPSETTING(    0x80, "English" );
	//	PORT_DIPSETTING(    0x00, "English" );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_smooncrs = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* "reset" on schematics */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNUSED );	/* Not read due to code at 0x2b1c and 0x3306 */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );	/* Not read due to code at 0x2b1c and 0x3306 */
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );	/* Not read due to code at 0x2b1c and 0x3306 */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
	//	PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Bonus_Life") );	Always "50000" due to code at 0x2f68
	//	PORT_DIPSETTING(    0x00, "30000" );
	//	PORT_DIPSETTING(    0x40, "50000" );
	//	PORT_DIPNAME( 0x80, 0x80, "Language" );		Always "English" due to code at 0x2f53
	//	PORT_DIPSETTING(    0x80, "English" );
	//	PORT_DIPSETTING(    0x00, "Japanese" );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mooncrsa = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* "reset" on schematics */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30000" );
		PORT_DIPSETTING(    0x40, "50000" );
		PORT_DIPNAME( 0x80, 0x80, "Language" );
		PORT_DIPSETTING(    0x80, "English" );
		PORT_DIPSETTING(    0x00, "Japanese" );
	
		PORT_START(); 	/* DSW */
	//	PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );		Not used due to code at 0x01c0
	//	PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
	//	PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_fantazia = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* "reset" on schematics */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30000" );
		PORT_DIPSETTING(    0x40, "50000" );
	//	PORT_DIPNAME( 0x80, 0x80, "Language" );		Always "English" due to code at 0x2f53
	//	PORT_DIPSETTING(    0x80, "English" );
	//	PORT_DIPSETTING(    0x00, "Japanese" );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_eagle = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
	
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* "reset" on schematics */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30000" );
		PORT_DIPSETTING(    0x40, "50000" );
		PORT_DIPNAME( 0x80, 0x80, "Language" );
		PORT_DIPSETTING(    0x80, "English" );
		PORT_DIPSETTING(    0x00, "Japanese" );
	
		PORT_START(); 	/* DSW */
	//	PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );		Not used due to code at 0x01c0
	//	PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
	//	PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_eagle2 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* "reset" on schematics */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30000" );
		PORT_DIPSETTING(    0x40, "50000" );
		PORT_DIPNAME( 0x80, 0x80, "Language" );
		PORT_DIPSETTING(    0x80, "English" );
		PORT_DIPSETTING(    0x00, "Japanese" );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );		Not used due to code at 0x01c0,
	//	PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );		but "Free Play" is checked
	//	PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
	//	PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
	//	PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mooncrgx = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x01, "30000" );
		PORT_DIPSETTING(    0x00, "50000" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x04, 0x04, "Language" );		Always "English" due to code removed at 0x2f4b
	//	PORT_DIPSETTING(    0x04, "English" );
	//	PORT_DIPSETTING(    0x00, "Japanese" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_skybase = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
	 	PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x80, "1C/1C (2 to start); )
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START(); 	/* IN2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x04, "15000" );
		PORT_DIPSETTING(    0x08, "20000" );
		PORT_DIPSETTING(    0x0c, "None" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_omega = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0xe0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x01, "20000" );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_moonqsr = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* "reset" on schematics */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x40, "Medium" );
		PORT_DIPSETTING(    0x80, "Hard" );
		PORT_DIPSETTING(    0xc0, "Hardest" );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_moonal2 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x01, "4000" );
		PORT_DIPSETTING(    0x02, "5000" );
		PORT_DIPSETTING(    0x03, "7000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mshuttle = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP   | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	
		PORT_START();       /* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "20000" );
		PORT_DIPSETTING(    0x04, "30000" );
		PORT_DIPNAME( 0x18, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x18, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x60, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_kingball = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
		/* Hack? - possibly multiplexed via writes to $b003 */
		//PORT_DIPNAME( 0x80, 0x80, "Speech" );
		//PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		//PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_SPECIAL );/* NOISE line */
		PORT_DIPNAME( 0xc0, 0x40, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x01, "12000" );
		PORT_DIPSETTING(    0x02, "15000" );
		PORT_DIPSETTING(    0x03, "None" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPNAME( 0xf8, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0xf8, DEF_STR( "On") );
	
		PORT_START();       /* IN3 (fake) */
		/* Hack? - possibly multiplexed via writes to $b003 - marked as SLAM */
		PORT_DIPNAME( 0x01, 0x01, "Speech" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_scorpnmc = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 - 0xa000 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SERVICE1 );	// COIN2? (it ALWAYS adds 1 credit)
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
	
		PORT_START();       /* IN1 - 0xa800 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );	/* also P1 Button 1 */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );	/* also P1 Button 2 */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_DIPNAME( 0xc0, 0x40, DEF_STR( "Difficulty") );	// Check code at 0x0118
		PORT_DIPSETTING(	0x00, "Easy" );
		PORT_DIPSETTING(	0x40, "Normal" );
		PORT_DIPSETTING(	0x80, "Hard" );
		PORT_DIPSETTING(	0xc0, "Hardest" );
	
		PORT_START();       /* DSW0? - 0xb001 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Lives") );		// Check code at 0x00eb
		PORT_DIPSETTING(	0x00, "2" );
		PORT_DIPSETTING(	0x04, "3" );
		PORT_DIPSETTING(	0x08, "4" );
		PORT_DIPSETTING(	0x0c, "5" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* DSW1? - 0xb002 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );		// Check code at 0x00fe
		PORT_DIPSETTING(	0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_3C") );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_frogg = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW,  IPT_SPECIAL );	// See notes
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW,  IPT_SPECIAL );	// See notes
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0xc0, "3" );
		PORT_DIPSETTING(	0x40, "5" );
		PORT_DIPSETTING(	0x80, "7" );
		PORT_BITX( 0,		0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );		// also affects coinage (see 'res' intruction at 0x3084)
		PORT_DIPSETTING(	0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );		// not tested due to code at 0x3084
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );		// when "Cabinet" Dip Switch set to "Upright"
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );		// "A 1/1 B 1/6" if "Cabinet" Dip Switch set to "Cocktail"
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );		// "A 2/1 B 1/3" if "Cabinet" Dip Switch set to "Cocktail"
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_4in1 = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_SPECIAL );// See fake ports
	
		PORT_START();       /* DSW0 */
		PORT_BIT( 0x3b, IP_ACTIVE_HIGH, IPT_SPECIAL );// See fake ports
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );		// 2 when continue (Scramble PT2)
		PORT_DIPSETTING(    0x04, "5" );		// 2 when continue (Scramble PT2)
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* The Ghost Muncher PT3 - FAKE DSW0 (bits 0 to 5) and IN1 (bits 6 and 7) */
		PORT_DIPNAME( 0x03, 0x00, "Bonus Life (GM PT3); )
		PORT_DIPSETTING(    0x01, "10000" );
		PORT_DIPSETTING(    0x02, "15000" );
		PORT_DIPSETTING(    0x03, "20000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SPECIAL );// Lives
	//	PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x08, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x10, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x00, "Coinage (GM PT3); )
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* Scramble PT2 - FAKE DSW0 (bits 0 to 5) and IN1 (bits 6 and 7) */
	//	PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x01, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SPECIAL );// Lives
		PORT_DIPNAME( 0x08, 0x00, "Allow Continue (S PT2); )
		PORT_DIPSETTING(    0x08, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );	// Scramble PT2 - Check code at 0x00c2
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );	// Scramble PT2 - Check code at 0x00cc
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x00, "Coinage (S PT2); )
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* Galaxian PT5 - FAKE DSW0 (bits 0 to 5) and IN1 (bits 6 and 7) */
		PORT_DIPNAME( 0x03, 0x00, "Bonus Life (G PT5); )
		PORT_DIPSETTING(    0x01, "4000" );
		PORT_DIPSETTING(    0x02, "5000" );
		PORT_DIPSETTING(    0x03, "7000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SPECIAL );// Lives
	//	PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x08, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x10, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x00, "Coinage (G PT5); )
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x80, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* Galactic Convoy - FAKE DSW0 (bits 0 to 5) and IN1 (bits 6 and 7) */
		PORT_DIPNAME( 0x01, 0x00, "Bonus Life (GC); )
		PORT_DIPSETTING(    0x00, "50000" );
		PORT_DIPSETTING(    0x01, "80000" );
	//	PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SPECIAL );// Lives
	//	PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x08, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x10, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unused") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x00, "Coinage (GC); )
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );	// 1 credit for 1st coin !
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_bagmanmc = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x03, "2" );
		PORT_DIPSETTING(	0x02, "3" );
		PORT_DIPSETTING(	0x01, "4" );
		PORT_DIPSETTING(	0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x00, "A 2C/1C  B 1C/1C" );
		PORT_DIPSETTING(	0x04, "A 1C/1C  B 1C/2C" );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(	0x18, "Easy" );
		PORT_DIPSETTING(	0x10, "Medium" );
		PORT_DIPSETTING(	0x08, "Hard" );
		PORT_DIPSETTING(	0x00, "Hardest" );
		PORT_DIPNAME( 0x20, 0x20, "Language" );
		PORT_DIPSETTING(	0x20, "English" );
		PORT_DIPSETTING(	0x00, "French" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x40, "30000" );
		PORT_DIPSETTING(	0x00, "40000" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );	// Check code at 0x2d78 and 0x2e6b
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_dkongjrm = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0xc0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x80, DEF_STR( "1C_2C") );
	
		PORT_START(); 	/* IN2 */
		PORT_DIPNAME( 0x01, 0x00, "Coin Multiplier" );
		PORT_DIPSETTING(    0x00, "*1" );
		PORT_DIPSETTING(    0x01, "*2" );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x00, "3" );
		PORT_DIPSETTING(	0x02, "4" );
		PORT_DIPSETTING(	0x04, "5" );
		PORT_DIPSETTING(	0x06, "6" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x08, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_froggrmc = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0xc0, "3" );
		PORT_DIPSETTING(	0x80, "5" );
		PORT_DIPSETTING(	0x40, "7" );
		PORT_BITX( 0,		0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "256", IP_KEY_NONE, IP_JOY_NONE );
	
		PORT_START(); 	/* IN2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x06, 0x06, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x02, "A 2/1 B 2/1 C 2/1" );
		PORT_DIPSETTING(	0x04, "A 2/1 B 1/3 C 2/1" );
		PORT_DIPSETTING(	0x06, "A 1/1 B 1/1 C 1/1" );
		PORT_DIPSETTING(	0x00, "A 1/1 B 1/6 C 1/1" );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout galaxian_charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,2),
		2,
		new int[] { RGN_FRAC(0,2), RGN_FRAC(1,2) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	static GfxLayout galaxian_spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,2),
		2,
		new int[] { RGN_FRAC(0,2), RGN_FRAC(1,2) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8
	);
	
	static GfxLayout pacmanbl_charlayout = new GfxLayout
	(
		8,8,
		256,
		2,
		new int[] { 0, 256*8*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	static GfxLayout pacmanbl_spritelayout = new GfxLayout
	(
		16,16,
		64,
		2,
		new int[] { 0, 64*16*16 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8
	);
	
	static GfxLayout bagmanmc_charlayout = new GfxLayout
	(
		8,8,
		512,
		2,
		new int[] { 0, 512*8*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout _4in1_charlayout = new GfxLayout
	(
		8,8,
		1024,
		2,
		new int[] { 0, 1024*8*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	static GfxLayout _4in1_spritelayout = new GfxLayout
	(
		16,16,
		256,
		2,
		new int[] { 0, 256*16*16 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8
	);
	
	
	static GfxDecodeInfo galaxian_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, galaxian_charlayout,   0, 8 ),
		new GfxDecodeInfo( REGION_GFX1, 0x0000, galaxian_spritelayout, 0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo gmgalax_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, galaxian_charlayout,   0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0x0000, galaxian_spritelayout, 0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/* separate character and sprite ROMs */
	static GfxDecodeInfo pacmanbl_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, pacmanbl_charlayout,   0, 8 ),
		new GfxDecodeInfo( REGION_GFX1, 0x1000, pacmanbl_spritelayout, 0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo bagmanmc_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, bagmanmc_charlayout,    0, 8 ),
		new GfxDecodeInfo( REGION_GFX1, 0x2000, pacmanbl_spritelayout, 0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo _4in1_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, _4in1_charlayout,      0, 8 ),
		new GfxDecodeInfo( REGION_GFX1, 0x4000, _4in1_spritelayout,    0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static AY8910interface jumpbug_ay8910_interface = new AY8910interface
	(
		1,	/* 1 chip */
		1789750,	/* 1.78975 MHz? */
		new int[] { 50 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static AY8910interface checkmaj_ay8910_interface = new AY8910interface
	(
		1,	/* 1 chip */
		1620000,	/* 1.62 MHz? (Used the same as Moon Cresta) */
		new int[] { 50 },
		new ReadHandlerPtr[] { soundlatch_r },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static DACinterface kingball_dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	
	MACHINE_DRIVER_START( galaxian_base )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", Z80, 18432000/6)	/* 3.072 MHz */
		MDRV_CPU_MEMORY(galaxian_readmem,galaxian_writemem)
	
		MDRV_FRAMES_PER_SECOND(16000.0/132/2)
	
		MDRV_MACHINE_INIT(galaxian)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(galaxian_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32+2+64)		/* 32 for the characters, 2 for the bullets, 64 for the stars */
		MDRV_COLORTABLE_LENGTH(8*4)
	
		MDRV_PALETTE_INIT(galaxian)
		MDRV_VIDEO_START(galaxian)
		MDRV_VIDEO_UPDATE(galaxian)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( galaxian )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, galaxian_custom_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( gmgalax )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(gmgalax_readmem,galaxian_writemem)
		MDRV_CPU_VBLANK_INT(gmgalax_vh_interrupt,1)
	
		/* video hardware */
		MDRV_GFXDECODE(gmgalax_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(64+2+64)		/* 64 for the characters, 2 for the bullets, 64 for the stars */
		MDRV_COLORTABLE_LENGTH(16*4)
	
		MDRV_VIDEO_START(gmgalax)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( pisces )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
	
		/* video hardware */
		MDRV_VIDEO_START(pisces)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( gteikob2 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
	
		/* video hardware */
		MDRV_VIDEO_START(gteikob2)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( batman2 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
	
		/* video hardware */
		MDRV_VIDEO_START(batman2)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mooncrgx )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
	
		/* video hardware */
		MDRV_VIDEO_START(mooncrgx)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( pacmanbl )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
	
		/* video hardware */
		MDRV_GFXDECODE(pacmanbl_gfxdecodeinfo)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( devilfsg )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
	
		MDRV_MACHINE_INIT(devilfsg)
	
		/* video hardware */
		MDRV_GFXDECODE(pacmanbl_gfxdecodeinfo)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mooncrst )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mooncrst_readmem,mooncrst_writemem)
	
		/* video hardware */
		MDRV_VIDEO_START(mooncrst)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( skybase )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mooncrst_readmem,skybase_writemem)
	
		/* video hardware */
		MDRV_VIDEO_START(pisces)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( moonqsr )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mooncrst_readmem,mooncrst_writemem)
	
		/* video hardware */
		MDRV_VIDEO_START(moonqsr)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mshuttle )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mooncrst_readmem,mshuttle_writemem)
		MDRV_CPU_PORTS(mshuttle_readport,mshuttle_writeport)
	
		MDRV_MACHINE_INIT(devilfsg)
	
		/* video hardware */
		MDRV_VIDEO_START(mshuttle)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, cclimber_ay8910_interface)
		MDRV_SOUND_ADD(CUSTOM, cclimber_custom_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( scramblb )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(scramblb_readmem,scramblb_writemem)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+2+64+1)	/* 32 for the characters, 2 for the bullets, 64 for the stars, 1 for background */
	
		MDRV_PALETTE_INIT(scramble)
		MDRV_VIDEO_START(scramble)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( zigzag )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(zigzag_readmem,zigzag_writemem)
	
		/* video hardware */
		MDRV_GFXDECODE(pacmanbl_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32+2+64)	/* 32 for the characters, 2 for the bullets, 64 for the stars */
	
		MDRV_VIDEO_START(galaxian_plain)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, jumpbug_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( jumpbug )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(jumpbug_readmem,jumpbug_writemem)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+2+64)	/* 32 for the characters, 2 for the bullets, 64 for the stars */
	
		MDRV_VIDEO_START(jumpbug)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, jumpbug_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( checkman )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mooncrst_readmem,checkman_writemem)
		MDRV_CPU_PORTS(0,checkman_writeport)
	
		MDRV_CPU_ADD(Z80, 1620000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 1.62 MHz */
		MDRV_CPU_MEMORY(checkman_sound_readmem,checkman_sound_writemem)
		MDRV_CPU_PORTS(checkman_sound_readport,checkman_sound_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)	/* NMIs are triggered by the main CPU */
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+2+64)	/* 32 for the characters, 2 for the bullets, 64 for the stars */
	
		MDRV_VIDEO_START(mooncrst)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, galaxian_custom_interface)
		MDRV_SOUND_ADD(AY8910, jumpbug_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( checkmaj )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(galaxian_readmem,checkmaj_writemem)
	
		MDRV_CPU_ADD(Z80, 1620000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 1.62 MHz? (used the same as Moon Cresta) */
		MDRV_CPU_MEMORY(checkmaj_sound_readmem,checkmaj_sound_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,32)	/* NMIs are triggered by the main CPU */
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+2+64)	/* 32 for the characters, 2 for the bullets, 64 for the stars */
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, checkmaj_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( kingball )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mooncrst_readmem,kingball_writemem)
	
		MDRV_CPU_ADD(Z80,5000000/2)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 2.5 MHz */
		MDRV_CPU_MEMORY(kingball_sound_readmem,kingball_sound_writemem)
		MDRV_CPU_PORTS(kingball_sound_readport,kingball_sound_writeport)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+2+64)	/* 32 for the characters, 2 for the bullets, 64 for the stars */
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, kingball_dac_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( scorpnmc )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(scorpnmc_readmem,scorpnmc_writemem)
	
		/* video hardware */
		MDRV_VIDEO_START(batman2)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( 4in1 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(_4in1_readmem,_4in1_writemem)
	
		/* video hardware */
		MDRV_GFXDECODE(_4in1_gfxdecodeinfo)
	
		MDRV_VIDEO_START(pisces)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( bagmanmc )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(bagmanmc_readmem,bagmanmc_writemem)
	
		MDRV_MACHINE_INIT( devilfsg )
	
		/* video hardware */
		MDRV_GFXDECODE(bagmanmc_gfxdecodeinfo)
	
		MDRV_VIDEO_START(pisces)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( dkongjrm )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(dkongjrm_readmem,dkongjrm_writemem)
	
		/* video hardware */
		MDRV_VIDEO_START(dkongjrm)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( froggrmc )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mooncrst_readmem,froggrmc_writemem)
	
		MDRV_CPU_ADD(Z80,14318000/8)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU) /* 1.78975 MHz */
		MDRV_CPU_MEMORY(frogger_sound_readmem,frogger_sound_writemem)
		MDRV_CPU_PORTS(frogger_sound_readport,frogger_sound_writeport)
	
		MDRV_MACHINE_INIT(scramble)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+1)  /* 32 for characters, 64 for stars, 2 for bullets, 1 for background */
	
		MDRV_PALETTE_INIT(frogger)
		MDRV_VIDEO_START(froggrmc)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, frogger_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_galaxian = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "galmidw.u",    0x0000, 0x0800, 0x745e2d61 );
		ROM_LOAD( "galmidw.v",    0x0800, 0x0800, 0x9c999a40 );
		ROM_LOAD( "galmidw.w",    0x1000, 0x0800, 0xb5894925 );
		ROM_LOAD( "galmidw.y",    0x1800, 0x0800, 0x6b3ca10b );
		ROM_LOAD( "7l",           0x2000, 0x0800, 0x1b933207 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "1h.bin",       0x0000, 0x0800, 0x39fb43a4 );
		ROM_LOAD( "1k.bin",       0x0800, 0x0800, 0x7e3f56a2 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galaxiaj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "7f.bin",       0x0000, 0x1000, 0x4335b1de );
		ROM_LOAD( "7j.bin",       0x1000, 0x1000, 0x4e6f66a1 );
		ROM_LOAD( "7l.bin",       0x2000, 0x0800, 0x5341d75a );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "1h.bin",       0x0000, 0x0800, 0x39fb43a4 );
		ROM_LOAD( "1k.bin",       0x0800, 0x0800, 0x7e3f56a2 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galmidw = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "galmidw.u",    0x0000, 0x0800, 0x745e2d61 );
		ROM_LOAD( "galmidw.v",    0x0800, 0x0800, 0x9c999a40 );
		ROM_LOAD( "galmidw.w",    0x1000, 0x0800, 0xb5894925 );
		ROM_LOAD( "galmidw.y",    0x1800, 0x0800, 0x6b3ca10b );
		ROM_LOAD( "galmidw.z",    0x2000, 0x0800, 0xcb24f797 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "galaxian.j1",  0x0000, 0x0800, 0x84decf98 );
		ROM_LOAD( "galaxian.l1",  0x0800, 0x0800, 0xc31ada9e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galmidwo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "galaxian.u",   0x0000, 0x0800, 0xfac42d34 );
		ROM_LOAD( "galaxian.v",   0x0800, 0x0800, 0xf58283e3 );
		ROM_LOAD( "galaxian.w",   0x1000, 0x0800, 0x4c7031c0 );
		ROM_LOAD( "galaxian.y",   0x1800, 0x0800, 0x96a7ac94 );
		ROM_LOAD( "7l.bin",       0x2000, 0x0800, 0x5341d75a );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "galaxian.j1",  0x0000, 0x0800, 0x84decf98 );
		ROM_LOAD( "galaxian.l1",  0x0800, 0x0800, 0xc31ada9e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_superg = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "7f.bin",       0x0000, 0x1000, 0x4335b1de );
		ROM_LOAD( "superg.w",     0x1000, 0x0800, 0xddeabdae );
		ROM_LOAD( "superg.y",     0x1800, 0x0800, 0x9463f753 );
		ROM_LOAD( "superg.z",     0x2000, 0x0800, 0xe6312e35 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "galmidw.1j",   0x0000, 0x0800, 0x84decf98 );
		ROM_LOAD( "galmidw.1k",   0x0800, 0x0800, 0xc31ada9e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galapx = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "galx.u",       0x0000, 0x0800, 0x79e4007d );
		ROM_LOAD( "galx.v",       0x0800, 0x0800, 0xbc16064e );
		ROM_LOAD( "galx.w",       0x1000, 0x0800, 0x72d2d3ee );
		ROM_LOAD( "galx.y",       0x1800, 0x0800, 0xafe397f3 );
		ROM_LOAD( "galx.z",       0x2000, 0x0800, 0x778c0d3c );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "galx.1h",      0x0000, 0x0800, 0xe8810654 );
		ROM_LOAD( "galx.1k",      0x0800, 0x0800, 0xcbe84a76 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galap1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "7f.bin",       0x0000, 0x1000, 0x4335b1de );
		ROM_LOAD( "galaxian.w",   0x1000, 0x0800, 0x4c7031c0 );
		ROM_LOAD( "galx_1_4.rom", 0x1800, 0x0800, 0xe71e1d9e );
		ROM_LOAD( "galx_1_5.rom", 0x2000, 0x0800, 0x6e65a3b2 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "galmidw.1j",   0x0000, 0x0800, 0x84decf98 );
		ROM_LOAD( "galmidw.1k",   0x0800, 0x0800, 0xc31ada9e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galap4 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "galnamco.u",   0x0000, 0x0800, 0xacfde501 );
		ROM_LOAD( "galnamco.v",   0x0800, 0x0800, 0x65cf3c77 );
		ROM_LOAD( "galnamco.w",   0x1000, 0x0800, 0x9eef9ae6 );
		ROM_LOAD( "galnamco.y",   0x1800, 0x0800, 0x56a5ddd1 );
		ROM_LOAD( "galnamco.z",   0x2000, 0x0800, 0xf4bc7262 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "galx_4c1.rom", 0x0000, 0x0800, 0xd5e88ab4 );
		ROM_LOAD( "galx_4c2.rom", 0x0800, 0x0800, 0xa57b83e4 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galturbo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "galturbo.u",   0x0000, 0x0800, 0xe8f3aa67 );
		ROM_LOAD( "galx.v",       0x0800, 0x0800, 0xbc16064e );
		ROM_LOAD( "superg.w",     0x1000, 0x0800, 0xddeabdae );
		ROM_LOAD( "galturbo.y",   0x1800, 0x0800, 0xa44f450f );
		ROM_LOAD( "galturbo.z",   0x2000, 0x0800, 0x3247f3d4 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "galturbo.1h",  0x0000, 0x0800, 0xa713fd1a );
		ROM_LOAD( "galturbo.1k",  0x0800, 0x0800, 0x28511790 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_swarm = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "swarm1.bin",   0x0000, 0x0800, 0x21eba3d0 );
		ROM_LOAD( "swarm2.bin",   0x0800, 0x0800, 0xf3a436cd );
		ROM_LOAD( "swarm3.bin",   0x1000, 0x0800, 0x2915e38b );
		ROM_LOAD( "swarm4.bin",   0x1800, 0x0800, 0x8bbbf486 );
		ROM_LOAD( "swarm5.bin",   0x2000, 0x0800, 0xf1b1987e );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "swarma.bin",   0x0000, 0x0800, 0xef8657bb );
		ROM_LOAD( "swarmb.bin",   0x0800, 0x0800, 0x60c4bd31 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_zerotime = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "zt-p01c.016",  0x0000, 0x0800, 0x90a2bc61 );
		ROM_LOAD( "zt-2.016",     0x0800, 0x0800, 0xa433067e );
		ROM_LOAD( "zt-3.016",     0x1000, 0x0800, 0xaaf038d4 );
		ROM_LOAD( "zt-4.016",     0x1800, 0x0800, 0x786d690a );
		ROM_LOAD( "zt-5.016",     0x2000, 0x0800, 0xaf9260d7 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ztc-2.016",    0x0000, 0x0800, 0x1b13ca05 );
		ROM_LOAD( "ztc-1.016",    0x0800, 0x0800, 0x5cd7df03 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gmgalax = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x18000, REGION_CPU1, 0 );/* 64k for code + 32k for banked code */
		ROM_LOAD( "pcb1_pm1.bin",0x10000, 0x1000, 0x19338c70 );
		ROM_LOAD( "pcb1_pm2.bin",0x11000, 0x1000, 0x18db074d );
		ROM_LOAD( "pcb1_pm3.bin",0x12000, 0x1000, 0xabb98b1d );
		ROM_LOAD( "pcb1_pm4.bin",0x13000, 0x1000, 0x2403c78e );
		ROM_LOAD( "pcb1_gx1.bin",0x14000, 0x1000, 0x2faa9f53 );
		ROM_LOAD( "pcb1_gx2.bin",0x15000, 0x1000, 0x121c5f16 );
		ROM_LOAD( "pcb1_gx3.bin",0x16000, 0x1000, 0x02d81a21 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "pcb2gfx1.bin", 0x0000, 0x0800, 0x7021bbc0 );
		ROM_CONTINUE(             0x1000, 0x0800 );
		ROM_LOAD( "pcb2gfx3.bin", 0x0800, 0x0800, 0x089c922b );
		ROM_CONTINUE(             0x1800, 0x0800 );
		ROM_LOAD( "pcb2gfx2.bin", 0x2000, 0x0800, 0x51bf58ee );
		ROM_CONTINUE(             0x3000, 0x0800 );
		ROM_LOAD( "pcb2gfx4.bin", 0x2800, 0x0800, 0x908fd0dc );
		ROM_CONTINUE(             0x3800, 0x0800 );
	
		ROM_REGION( 0x0040, REGION_PROMS, 0 );
		ROM_LOAD( "gmgalax2.clr", 0x0000, 0x0020, 0x499f4440 );
		ROM_LOAD( "l06_prom.bin", 0x0020, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_pisces = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "p1.bin",       0x0000, 0x0800, 0x40c5b0e4 );
		ROM_LOAD( "p2.bin",       0x0800, 0x0800, 0x055f9762 );
		ROM_LOAD( "p3.bin",       0x1000, 0x0800, 0x3073dd04 );
		ROM_LOAD( "p4.bin",       0x1800, 0x0800, 0x44aaf525 );
		ROM_LOAD( "p5.bin",       0x2000, 0x0800, 0xfade512b );
		ROM_LOAD( "p6.bin",       0x2800, 0x0800, 0x5ab2822f );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
	//	ROM_LOAD( "pisces.1j",    0x0000, 0x1000, 0x2dba9e0e );
	//	ROM_LOAD( "pisces.1k",    0x1000, 0x1000, 0xcdc5aa26 );
		ROM_LOAD( "g09.bin",      0x0000, 0x0800, 0x9503a23a );
		ROM_LOAD( "g11.bin",      0x0800, 0x0800, 0x0adfc3fe );
		ROM_LOAD( "g10.bin",      0x1000, 0x0800, 0x3e61f849 );
		ROM_LOAD( "g12.bin",      0x1800, 0x0800, 0x7130e9eb );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "colour.bin",   0x0000, 0x0020, 0x57a45057 );// same as checkman.clr
	ROM_END(); }}; 
	
	static RomLoadPtr rom_piscesb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "pisces.a1",    0x0000, 0x0800, 0x856b8e1f );
		ROM_LOAD( "p2.bin",       0x0800, 0x0800, 0x055f9762 );
		ROM_LOAD( "pisces.b2",    0x1000, 0x0800, 0x5540f2e4 );
		ROM_LOAD( "p4.bin",       0x1800, 0x0800, 0x44aaf525 );
		ROM_LOAD( "p5.bin",       0x2000, 0x0800, 0xfade512b );
		ROM_LOAD( "p6.bin",       0x2800, 0x0800, 0x5ab2822f );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
	//	ROM_LOAD( "pisces.1j",    0x0000, 0x1000, 0x2dba9e0e );
	//	ROM_LOAD( "pisces.1k",    0x1000, 0x1000, 0xcdc5aa26 );
		ROM_LOAD( "g09.bin",      0x0000, 0x0800, 0x9503a23a );
		ROM_LOAD( "g11.bin",      0x0800, 0x0800, 0x0adfc3fe );
		ROM_LOAD( "g10.bin",      0x1000, 0x0800, 0x3e61f849 );
		ROM_LOAD( "g12.bin",      0x1800, 0x0800, 0x7130e9eb );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
	//	ROM_LOAD( "6331-1j.86",   0x0000, 0x0020, 0x24652bc4 );/* very close to Galaxian */
		ROM_LOAD( "colour.bin",   0x0000, 0x0020, 0x57a45057 );// same as checkman.clr
	ROM_END(); }}; 
	
	static RomLoadPtr rom_uniwars = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "f07_1a.bin",   0x0000, 0x0800, 0xd975af10 );
		ROM_LOAD( "h07_2a.bin",   0x0800, 0x0800, 0xb2ed14c3 );
		ROM_LOAD( "k07_3a.bin",   0x1000, 0x0800, 0x945f4160 );
		ROM_LOAD( "m07_4a.bin",   0x1800, 0x0800, 0xddc80bc5 );
		ROM_LOAD( "d08p_5a.bin",  0x2000, 0x0800, 0x62354351 );
		ROM_LOAD( "gg6",          0x2800, 0x0800, 0x270a3f4d );
		ROM_LOAD( "m08p_7a.bin",  0x3000, 0x0800, 0xc9245346 );
		ROM_LOAD( "n08p_8a.bin",  0x3800, 0x0800, 0x797d45c7 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "egg10",        0x0000, 0x0800, 0x012941e0 );
		ROM_LOAD( "h01_2.bin",    0x0800, 0x0800, 0xc26132af );
		ROM_LOAD( "egg9",         0x1000, 0x0800, 0xfc8b58fd );
		ROM_LOAD( "k01_2.bin",    0x1800, 0x0800, 0xdcc2b33b );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "uniwars.clr",  0x0000, 0x0020, 0x25c79518 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gteikoku = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "f07_1a.bin",   0x0000, 0x0800, 0xd975af10 );
		ROM_LOAD( "h07_2a.bin",   0x0800, 0x0800, 0xb2ed14c3 );
		ROM_LOAD( "k07_3a.bin",   0x1000, 0x0800, 0x945f4160 );
		ROM_LOAD( "m07_4a.bin",   0x1800, 0x0800, 0xddc80bc5 );
		ROM_LOAD( "d08p_5a.bin",  0x2000, 0x0800, 0x62354351 );
		ROM_LOAD( "e08p_6a.bin",  0x2800, 0x0800, 0xd915a389 );
		ROM_LOAD( "m08p_7a.bin",  0x3000, 0x0800, 0xc9245346 );
		ROM_LOAD( "n08p_8a.bin",  0x3800, 0x0800, 0x797d45c7 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "h01_1.bin",    0x0000, 0x0800, 0x8313c959 );
		ROM_LOAD( "h01_2.bin",    0x0800, 0x0800, 0xc26132af );
		ROM_LOAD( "k01_1.bin",    0x1000, 0x0800, 0xc9d4537e );
		ROM_LOAD( "k01_2.bin",    0x1800, 0x0800, 0xdcc2b33b );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gteikokb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "1.bin",        0x0000, 0x0800, 0xbf00252f );
		ROM_LOAD( "2.bin",   	  0x0800, 0x0800, 0xf712b7d5 );
		ROM_LOAD( "k07_3a.bin",   0x1000, 0x0800, 0x945f4160 );
		ROM_LOAD( "4.bin",   	  0x1800, 0x0800, 0x808a39a8 );
		ROM_LOAD( "5.bin",  	  0x2000, 0x0800, 0x36fe6e67 );
		ROM_LOAD( "6.bin",  	  0x2800, 0x0800, 0xc5ea67e8 );
		ROM_LOAD( "m08p_7a.bin",  0x3000, 0x0800, 0xc9245346 );
		ROM_LOAD( "8.bin",  	  0x3800, 0x0800, 0x28df3229 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "h01_1.bin",    0x0000, 0x0800, 0x8313c959 );
		ROM_LOAD( "h01_2.bin",    0x0800, 0x0800, 0xc26132af );
		ROM_LOAD( "k01_1.bin",    0x1000, 0x0800, 0xc9d4537e );
		ROM_LOAD( "k01_2.bin",    0x1800, 0x0800, 0xdcc2b33b );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gteikob2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "94gnog.bin",   0x0000, 0x0800, 0x67ec3235 );
		ROM_LOAD( "92gnog.bin",   0x0800, 0x0800, 0x813c41f2 );
		ROM_LOAD( "k07_3a.bin",   0x1000, 0x0800, 0x945f4160 );
		ROM_LOAD( "1gnog.bin",    0x1800, 0x0800, 0x49ff9658 );
		ROM_LOAD( "5.bin",  	  0x2000, 0x0800, 0x36fe6e67 );
		ROM_LOAD( "e08p_6a.bin",  0x2800, 0x0800, 0xd915a389 );
		ROM_LOAD( "m08p_7a.bin",  0x3000, 0x0800, 0xc9245346 );
		ROM_LOAD( "98gnog.bin",   0x3800, 0x0800, 0xe9d4ad3c );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "h01_1.bin",    0x0000, 0x0800, 0x8313c959 );
		ROM_LOAD( "h01_2.bin",    0x0800, 0x0800, 0xc26132af );
		ROM_LOAD( "k01_1.bin",    0x1000, 0x0800, 0xc9d4537e );
		ROM_LOAD( "k01_2.bin",    0x1800, 0x0800, 0xdcc2b33b );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_spacbatt = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "f07_1a.bin",   0x0000, 0x0800, 0xd975af10 );
		ROM_LOAD( "h07_2a.bin",   0x0800, 0x0800, 0xb2ed14c3 );
		ROM_LOAD( "sb.3",         0x1000, 0x0800, 0xc25ce4c1 );
		ROM_LOAD( "sb.4",         0x1800, 0x0800, 0x8229835c );
		ROM_LOAD( "sb.5",         0x2000, 0x0800, 0xf51ef930 );
		ROM_LOAD( "e08p_6a.bin",  0x2800, 0x0800, 0xd915a389 );
		ROM_LOAD( "m08p_7a.bin",  0x3000, 0x0800, 0xc9245346 );
		ROM_LOAD( "sb.8",         0x3800, 0x0800, 0xe59ff1ae );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "h01_1.bin",    0x0000, 0x0800, 0x8313c959 );
		ROM_LOAD( "h01_2.bin",    0x0800, 0x0800, 0xc26132af );
		ROM_LOAD( "k01_1.bin",    0x1000, 0x0800, 0xc9d4537e );
		ROM_LOAD( "k01_2.bin",    0x1800, 0x0800, 0xdcc2b33b );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_batman2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "01.bin",    	  0x0000, 0x0800, 0x150fbca5 );
		ROM_LOAD( "02.bin",       0x0800, 0x0800, 0xb1624fd0 );
		ROM_LOAD( "03.bin",       0x1000, 0x0800, 0x93774188 );
		ROM_LOAD( "04.bin",       0x1800, 0x0800, 0x8a94ec6c );
		ROM_LOAD( "05.bin",       0x2000, 0x0800, 0xa3669461 );
		ROM_LOAD( "06.bin",       0x2800, 0x0800, 0xfa1efbfe );
		ROM_LOAD( "07.bin",       0x3000, 0x0800, 0x9b77debd );
		ROM_LOAD( "08.bin",       0x3800, 0x0800, 0x6466177e );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "09.bin",       0x0000, 0x0800, 0x1a657b1f );
		ROM_LOAD( "11.bin",       0x0800, 0x0800, 0x7a2b48e5 );
		ROM_LOAD( "10.bin",       0x1000, 0x0800, 0x9b570016 );
		ROM_LOAD( "12.bin",       0x1800, 0x0800, 0x73956244 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_warofbug = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "warofbug.u",   0x0000, 0x0800, 0xb8dfb7e3 );
		ROM_LOAD( "warofbug.v",   0x0800, 0x0800, 0xfd8854e0 );
		ROM_LOAD( "warofbug.w",   0x1000, 0x0800, 0x4495aa14 );
		ROM_LOAD( "warofbug.y",   0x1800, 0x0800, 0xc14a541f );
		ROM_LOAD( "warofbug.z",   0x2000, 0x0800, 0xc167fe55 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "warofbug.1k",  0x0000, 0x0800, 0x8100fa85 );
		ROM_LOAD( "warofbug.1j",  0x0800, 0x0800, 0xd1220ae9 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "warofbug.clr", 0x0000, 0x0020, 0x8688e64b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_redufo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "ru1a",         0x0000, 0x0800, 0x5a8e4f37 );
		ROM_LOAD( "ru2a",         0x0800, 0x0800, 0xc624f52d );
		ROM_LOAD( "ru3a",         0x1000, 0x0800, 0xe1030d1c );
		ROM_LOAD( "ru4a",         0x1800, 0x0800, 0x7692069e );
		ROM_LOAD( "ru5a",         0x2000, 0x0800, 0xcb648ff3 );
		ROM_LOAD( "ru6a",         0x2800, 0x0800, 0xe1a9f58e );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ruhja",        0x0000, 0x0800, 0x8a422b0d );
		ROM_LOAD( "rukla",        0x0800, 0x0800, 0x1eb84cb1 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_exodus = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "exodus1.bin",  0x0000, 0x0800, 0x5dfe65e1 );
		ROM_LOAD( "exodus2.bin",  0x0800, 0x0800, 0x6559222f );
		ROM_LOAD( "exodus3.bin",  0x1000, 0x0800, 0xbf7030e8 );
		ROM_LOAD( "exodus4.bin",  0x1800, 0x0800, 0x3607909e );
		ROM_LOAD( "exodus9.bin",  0x2000, 0x0800, 0x994a90c4 );
		ROM_LOAD( "exodus10.bin", 0x2800, 0x0800, 0xfbd11187 );
		ROM_LOAD( "exodus11.bin", 0x3000, 0x0800, 0xfd07d811 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "exodus5.bin",  0x0000, 0x0800, 0xb34c7cb4 );
		ROM_LOAD( "exodus6.bin",  0x0800, 0x0800, 0x50a2d447 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_streakng = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "sk1",          0x0000, 0x1000, 0xc8866ccb );
		ROM_LOAD( "sk2",          0x1000, 0x1000, 0x7caea29b );
		ROM_LOAD( "sk3",          0x2000, 0x1000, 0x7b4bfa76 );
		ROM_LOAD( "sk4",          0x3000, 0x1000, 0x056fc921 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "sk5",          0x0000, 0x1000, 0xd27f1e0c );
		ROM_LOAD( "sk6",          0x1000, 0x1000, 0xa7089588 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "sk.bpr",       0x0000, 0x0020, 0xbce79607 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_pacmanbl = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "blpac1b",      0x0000, 0x0800, 0x6718df42 );
		ROM_LOAD( "blpac2b",      0x0800, 0x0800, 0x33be3648 );
		ROM_LOAD( "blpac3b",      0x1000, 0x0800, 0xf98c0ceb );
		ROM_LOAD( "blpac4b",      0x1800, 0x0800, 0xa9cd0082 );
		ROM_LOAD( "blpac5b",      0x2000, 0x0800, 0x6d475afc );
		ROM_LOAD( "blpac6b",      0x2800, 0x0800, 0xcbe863d3 );
		ROM_LOAD( "blpac7b",      0x3000, 0x0800, 0x7daef758 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "blpac12b",     0x0000, 0x0800, 0xb2ed320b );
		ROM_LOAD( "blpac11b",     0x0800, 0x0800, 0xab88b2c4 );
		ROM_LOAD( "blpac10b",     0x1000, 0x0800, 0x44a45b72 );
		ROM_LOAD( "blpac9b",      0x1800, 0x0800, 0xfa84659f );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "blpaccp",      0x0000, 0x0020, 0x24652bc4 );/* same as pisces */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_devilfsg = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "dfish1.7f",    0x2000, 0x0800, 0x2ab19698 );
		ROM_CONTINUE(             0x0000, 0x0800 );
		ROM_LOAD( "dfish2.7h",    0x2800, 0x0800, 0x4e77f097 );
		ROM_CONTINUE(             0x0800, 0x0800 );
		ROM_LOAD( "dfish3.7k",    0x3000, 0x0800, 0x3f16a4c6 );
		ROM_CONTINUE(             0x1000, 0x0800 );
		ROM_LOAD( "dfish4.7m",    0x3800, 0x0800, 0x11fc7e59 );
		ROM_CONTINUE(             0x1800, 0x0800 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "dfish5.1h",    0x1000, 0x0800, 0xace6e31f );
		ROM_CONTINUE(             0x0000, 0x0800 );
		ROM_LOAD( "dfish6.1k",    0x1800, 0x0800, 0xd7a6c4c4 );
		ROM_CONTINUE(             0x0800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_zigzag = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "zz_d1.bin",    0x0000, 0x1000, 0x8cc08d81 );
		ROM_LOAD( "zz_d2.bin",    0x1000, 0x1000, 0x326d8d45 );
		ROM_LOAD( "zz_d4.bin",    0x2000, 0x1000, 0xa94ed92a );
		ROM_LOAD( "zz_d3.bin",    0x3000, 0x1000, 0xce5e7a00 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "zz_6_h1.bin",  0x0000, 0x0800, 0x780c162a );
		ROM_CONTINUE(             0x1000, 0x0800 );
		ROM_LOAD( "zz_5.bin",     0x0800, 0x0800, 0xf3cdfec5 );
		ROM_CONTINUE(             0x1800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "zzbp_e9.bin",  0x0000, 0x0020, 0xaa486dd0 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_zigzag2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "z1",           0x0000, 0x1000, 0x4c28349a );
		ROM_LOAD( "zz_d2.bin",    0x1000, 0x1000, 0x326d8d45 );
		ROM_LOAD( "zz_d4.bin",    0x2000, 0x1000, 0xa94ed92a );
		ROM_LOAD( "zz_d3.bin",    0x3000, 0x1000, 0xce5e7a00 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "zz_6_h1.bin",  0x0000, 0x0800, 0x780c162a );
		ROM_CONTINUE(             0x1000, 0x0800 );
		ROM_LOAD( "zz_5.bin",     0x0800, 0x0800, 0xf3cdfec5 );
		ROM_CONTINUE(             0x1800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "zzbp_e9.bin",  0x0000, 0x0020, 0xaa486dd0 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mooncrgx = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "1",            0x0000, 0x0800, 0x84cf420b );
		ROM_LOAD( "2",            0x0800, 0x0800, 0x4c2a61a1 );
		ROM_LOAD( "3",            0x1000, 0x0800, 0x1962523a );
		ROM_LOAD( "4",            0x1800, 0x0800, 0x75dca896 );
		ROM_LOAD( "5",            0x2000, 0x0800, 0x32483039 );
		ROM_LOAD( "6",            0x2800, 0x0800, 0x43f2ab89 );
		ROM_LOAD( "7",            0x3000, 0x0800, 0x1e9c168c );
		ROM_LOAD( "8",            0x3800, 0x0800, 0x5e09da94 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "1h_1_10.bin",  0x0000, 0x0800, 0x528da705 );
		ROM_LOAD( "12.chr",       0x0800, 0x0800, 0x5a4b17ea );
		ROM_LOAD( "9.chr",        0x1000, 0x0800, 0x70df525c );
		ROM_LOAD( "11.chr",       0x1800, 0x0800, 0xe0edccbd );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_omega = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "omega1.bin",   0x0000, 0x0800, 0xfc2a096b );
		ROM_LOAD( "omega2.bin",   0x0800, 0x0800, 0xad100357 );
		ROM_LOAD( "omega3.bin",   0x1000, 0x0800, 0xd7e3be79 );
		ROM_LOAD( "omega4.bin",   0x1800, 0x0800, 0x42068171 );
		ROM_LOAD( "omega5.bin",   0x2000, 0x0800, 0xd8a93383 );
		ROM_LOAD( "omega6.bin",   0x2800, 0x0800, 0x32a42f44 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "omega1h.bin",  0x0000, 0x0800, 0x527fd384 );
		ROM_LOAD( "omega1k.bin",  0x0800, 0x0800, 0x36de42c6 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6331-1j.86",   0x0000, 0x0020, 0x00000000 );/* missing */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_scramblb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "scramble.1k",  0x0000, 0x0800, 0x9e025c4a );
		ROM_LOAD( "scramble.2k",  0x0800, 0x0800, 0x306f783e );
		ROM_LOAD( "scramble.3k",  0x1000, 0x0800, 0x0500b701 );
		ROM_LOAD( "scramble.4k",  0x1800, 0x0800, 0xdd380a22 );
		ROM_LOAD( "scramble.5k",  0x2000, 0x0800, 0xdf0b9648 );
		ROM_LOAD( "scramble.1j",  0x2800, 0x0800, 0xb8c07b3c );
		ROM_LOAD( "scramble.2j",  0x3000, 0x0800, 0x88ac07a0 );
		ROM_LOAD( "scramble.3j",  0x3800, 0x0800, 0xc67d57ca );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "5f.k",         0x0000, 0x0800, 0x4708845b );
		ROM_LOAD( "5h.k",         0x0800, 0x0800, 0x11fd2887 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_jumpbug = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "jb1",          0x0000, 0x1000, 0x415aa1b7 );
		ROM_LOAD( "jb2",          0x1000, 0x1000, 0xb1c27510 );
		ROM_LOAD( "jb3",          0x2000, 0x1000, 0x97c24be2 );
		ROM_LOAD( "jb4",          0x3000, 0x1000, 0x66751d12 );
		ROM_LOAD( "jb5",          0x8000, 0x1000, 0xe2d66faf );
		ROM_LOAD( "jb6",          0x9000, 0x1000, 0x49e0bdfd );
		ROM_LOAD( "jb7",          0xa000, 0x0800, 0x83d71302 );
	
		ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "jbl",          0x0000, 0x0800, 0x9a091b0a );
		ROM_LOAD( "jbm",          0x0800, 0x0800, 0x8a0fc082 );
		ROM_LOAD( "jbn",          0x1000, 0x0800, 0x155186e0 );
		ROM_LOAD( "jbi",          0x1800, 0x0800, 0x7749b111 );
		ROM_LOAD( "jbj",          0x2000, 0x0800, 0x06e8d7df );
		ROM_LOAD( "jbk",          0x2800, 0x0800, 0xb8dbddf3 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_jumpbugb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "jb1",          0x0000, 0x1000, 0x415aa1b7 );
		ROM_LOAD( "jb2",          0x1000, 0x1000, 0xb1c27510 );
		ROM_LOAD( "jb3b",         0x2000, 0x1000, 0xcb8b8a0f );
		ROM_LOAD( "jb4",          0x3000, 0x1000, 0x66751d12 );
		ROM_LOAD( "jb5b",         0x8000, 0x1000, 0x7553b5e2 );
		ROM_LOAD( "jb6b",         0x9000, 0x1000, 0x47be9843 );
		ROM_LOAD( "jb7b",         0xa000, 0x0800, 0x460aed61 );
	
		ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "jbl",          0x0000, 0x0800, 0x9a091b0a );
		ROM_LOAD( "jbm",          0x0800, 0x0800, 0x8a0fc082 );
		ROM_LOAD( "jbn",          0x1000, 0x0800, 0x155186e0 );
		ROM_LOAD( "jbi",          0x1800, 0x0800, 0x7749b111 );
		ROM_LOAD( "jbj",          0x2000, 0x0800, 0x06e8d7df );
		ROM_LOAD( "jbk",          0x2800, 0x0800, 0xb8dbddf3 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_levers = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );      /* 64k for code */
		ROM_LOAD( "g96059.a8", 	  0x0000, 0x1000, 0x9550627a );
		ROM_LOAD( "g96060.d8", 	  0x2000, 0x1000, 0x5ac64646 );
		ROM_LOAD( "g96061.e8", 	  0x3000, 0x1000, 0x9db8e520 );
		ROM_LOAD( "g96062.h8", 	  0x8000, 0x1000, 0x7c8e8b3a );
		ROM_LOAD( "g96063.j8", 	  0x9000, 0x1000, 0xfa61e793 );
		ROM_LOAD( "g96064.l8", 	  0xa000, 0x1000, 0xf797f389 );
	
		ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "g95948.n1", 	  0x0000, 0x0800, 0xd8a0c692 );
								/*0x0800- 0x0fff empty */
		ROM_LOAD( "g95949.s1", 	  0x1000, 0x0800, 0x3660a552 );
		ROM_LOAD( "g95946.j1", 	  0x1800, 0x0800, 0x73b61b2d );
								/*0x2000- 0x27ff empty */
		ROM_LOAD( "g95947.m1", 	  0x2800, 0x0800, 0x72ff67e2 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "g960lev.clr",  0x0000, 0x0020, 0x01febbbe );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_azurian = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "pgm.1",        0x0000, 0x1000, 0x17a0fca7 );
		ROM_LOAD( "pgm.2",        0x1000, 0x1000, 0x14659848 );
		ROM_LOAD( "pgm.3",        0x2000, 0x1000, 0x8f60fb97 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "gfx.1",        0x0000, 0x0800, 0xf5afb803 );
		ROM_LOAD( "gfx.2",        0x0800, 0x0800, 0xae96e5d1 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_orbitron = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "orbitron.3",   0x0600, 0x0200, 0x419f9c9b );
		ROM_CONTINUE(			  0x0400, 0x0200);
		ROM_CONTINUE(			  0x0200, 0x0200);
		ROM_CONTINUE(			  0x0000, 0x0200);
		ROM_LOAD( "orbitron.4",   0x0e00, 0x0200, 0x44ad56ac );
		ROM_CONTINUE(			  0x0c00, 0x0200);
		ROM_CONTINUE(			  0x0a00, 0x0200);
		ROM_CONTINUE(			  0x0800, 0x0200);
		ROM_LOAD( "orbitron.1",   0x1600, 0x0200, 0xda3f5168 );
		ROM_CONTINUE(			  0x1400, 0x0200);
		ROM_CONTINUE(			  0x1200, 0x0200);
		ROM_CONTINUE(			  0x1000, 0x0200);
		ROM_LOAD( "orbitron.2",   0x1e00, 0x0200, 0xa3b813fc );
		ROM_CONTINUE(			  0x1c00, 0x0200);
		ROM_CONTINUE(			  0x1a00, 0x0200);
		ROM_CONTINUE(			  0x1800, 0x0200);
		ROM_LOAD( "orbitron.5",   0x2000, 0x0800, 0x20cd8bb8 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "orbitron.6",   0x0000, 0x0800, 0x2c91b83f );
		ROM_LOAD( "orbitron.7",   0x0800, 0x0800, 0x46f4cca4 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_checkman = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "cm1",          0x0000, 0x0800, 0xe8cbdd28 );
		ROM_LOAD( "cm2",          0x0800, 0x0800, 0xb8432d4d );
		ROM_LOAD( "cm3",          0x1000, 0x0800, 0x15a97f61 );
		ROM_LOAD( "cm4",          0x1800, 0x0800, 0x8c12ecc0 );
		ROM_LOAD( "cm5",          0x2000, 0x0800, 0x2352cfd6 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound code */
		ROM_LOAD( "cm13",         0x0000, 0x0800, 0x0b09a3e8 );
		ROM_LOAD( "cm14",         0x0800, 0x0800, 0x47f043be );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "cm11",         0x0000, 0x0800, 0x8d1bcca0 );
		ROM_RELOAD(	              0x0800, 0x0800 );
		ROM_LOAD( "cm9",          0x1000, 0x0800, 0x3cd5c751 );
		ROM_RELOAD(	              0x1800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "checkman.clr", 0x0000, 0x0020, 0x57a45057 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_checkmaj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "cm_1.bin",     0x0000, 0x1000, 0x456a118f );
		ROM_LOAD( "cm_2.bin",     0x1000, 0x1000, 0x146b2c44 );
		ROM_LOAD( "cm_3.bin",     0x2000, 0x0800, 0x73e1c945 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound code */
		ROM_LOAD( "cm_4.bin",     0x0000, 0x1000, 0x923cffa1 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "cm_6.bin",     0x0000, 0x0800, 0x476a7cc3 );
		ROM_LOAD( "cm_5.bin",     0x0800, 0x0800, 0xb3df2b5f );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "checkman.clr", 0x0000, 0x0020, 0x57a45057 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_dingo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "003.e7",       0x0000, 0x1000, 0xd088550f );
		ROM_LOAD( "004.h7",       0x1000, 0x1000, 0xa228446a );
		ROM_LOAD( "005.j7",       0x2000, 0x0800, 0x14d680bb );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound code */
		ROM_LOAD( "6.7l",         0x0000, 0x1000, 0x047092e0 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "001.h1",       0x0000, 0x0800, 0x1ab1dd4d );
		ROM_LOAD( "002.k1",       0x0800, 0x0800, 0x4be375ee );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "18s030.l6",	  0x0000, 0x0020, 0x3061d0f9 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_blkhole = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "bh1",          0x0000, 0x0800, 0x64998819 );
		ROM_LOAD( "bh2",          0x0800, 0x0800, 0x26f26ce4 );
		ROM_LOAD( "bh3",          0x1000, 0x0800, 0x3418bc45 );
		ROM_LOAD( "bh4",          0x1800, 0x0800, 0x735ff481 );
		ROM_LOAD( "bh5",          0x2000, 0x0800, 0x3f657be9 );
		ROM_LOAD( "bh6",          0x2800, 0x0800, 0xa057ab35 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "bh7",          0x0000, 0x0800, 0x975ba821 );
		ROM_LOAD( "bh8",          0x0800, 0x0800, 0x03d11020 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mooncrst = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "mc1",          0x0000, 0x0800, 0x7d954a7a );
		ROM_LOAD( "mc2",          0x0800, 0x0800, 0x44bb7cfa );
		ROM_LOAD( "mc3",          0x1000, 0x0800, 0x9c412104 );
		ROM_LOAD( "mc4",          0x1800, 0x0800, 0x7e9b1ab5 );
		ROM_LOAD( "mc5.7r",       0x2000, 0x0800, 0x16c759af );
		ROM_LOAD( "mc6.8d",       0x2800, 0x0800, 0x69bcafdb );
		ROM_LOAD( "mc7.8e",       0x3000, 0x0800, 0xb50dbc46 );
		ROM_LOAD( "mc8",          0x3800, 0x0800, 0x18ca312b );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mcs_b",        0x0000, 0x0800, 0xfb0f1f81 );
		ROM_LOAD( "mcs_d",        0x0800, 0x0800, 0x13932a15 );
		ROM_LOAD( "mcs_a",        0x1000, 0x0800, 0x631ebb5a );
		ROM_LOAD( "mcs_c",        0x1800, 0x0800, 0x24cfd145 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mooncrsu = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "smc1f",        0x0000, 0x0800, 0x389ca0d6 );
		ROM_LOAD( "smc2f",        0x0800, 0x0800, 0x410ab430 );
		ROM_LOAD( "smc3f",        0x1000, 0x0800, 0xa6b4144b );
		ROM_LOAD( "smc4f",        0x1800, 0x0800, 0x4cc046fe );
		ROM_LOAD( "e5",       	  0x2000, 0x0800, 0x06d378a6 );
		ROM_LOAD( "bepr199",      0x2800, 0x0800, 0x6e84a927 );
		ROM_LOAD( "e7",           0x3000, 0x0800, 0xb45af1e8 );
		ROM_LOAD( "smc8f",        0x3800, 0x0800, 0xf42164c5 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mcs_b",        0x0000, 0x0800, 0xfb0f1f81 );
		ROM_LOAD( "mcs_d",        0x0800, 0x0800, 0x13932a15 );
		ROM_LOAD( "mcs_a",        0x1000, 0x0800, 0x631ebb5a );
		ROM_LOAD( "mcs_c",        0x1800, 0x0800, 0x24cfd145 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mooncrsa = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "mc1.7d",       0x0000, 0x0800, 0x92a86aac );
		ROM_LOAD( "mc2.7e",       0x0800, 0x0800, 0x438c2b4b );
		ROM_LOAD( "mc3.7j",       0x1000, 0x0800, 0x67e3d21d );
		ROM_LOAD( "mc4.7p",       0x1800, 0x0800, 0xf4db39f6 );
		ROM_LOAD( "mc5.7r",       0x2000, 0x0800, 0x16c759af );
		ROM_LOAD( "mc6.8d",       0x2800, 0x0800, 0x69bcafdb );
		ROM_LOAD( "mc7.8e",       0x3000, 0x0800, 0xb50dbc46 );
		ROM_LOAD( "mc8.8h",       0x3800, 0x0800, 0x7e2b1928 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mcs_b",        0x0000, 0x0800, 0xfb0f1f81 );
		ROM_LOAD( "mcs_d",        0x0800, 0x0800, 0x13932a15 );
		ROM_LOAD( "mcs_a",        0x1000, 0x0800, 0x631ebb5a );
		ROM_LOAD( "mcs_c",        0x1800, 0x0800, 0x24cfd145 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mooncrsg = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "epr194",       0x0000, 0x0800, 0x0e5582b1 );
		ROM_LOAD( "epr195",       0x0800, 0x0800, 0x12cb201b );
		ROM_LOAD( "epr196",       0x1000, 0x0800, 0x18255614 );
		ROM_LOAD( "epr197",       0x1800, 0x0800, 0x05ac1466 );
		ROM_LOAD( "epr198",       0x2000, 0x0800, 0xc28a2e8f );
		ROM_LOAD( "epr199",       0x2800, 0x0800, 0x5a4571de );
		ROM_LOAD( "epr200",       0x3000, 0x0800, 0xb7c85bf1 );
		ROM_LOAD( "epr201",       0x3800, 0x0800, 0x2caba07f );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "epr203",       0x0000, 0x0800, 0xbe26b561 );
		ROM_LOAD( "mcs_d",        0x0800, 0x0800, 0x13932a15 );
		ROM_LOAD( "epr202",       0x1000, 0x0800, 0x26c7e800 );
		ROM_LOAD( "mcs_c",        0x1800, 0x0800, 0x24cfd145 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_smooncrs = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "927",          0x0000, 0x0800, 0x55c5b994 );
		ROM_LOAD( "928a",         0x0800, 0x0800, 0x77ae26d3 );
		ROM_LOAD( "929",          0x1000, 0x0800, 0x716eaa10 );
		ROM_LOAD( "930",          0x1800, 0x0800, 0xcea864f2 );
		ROM_LOAD( "931",          0x2000, 0x0800, 0x702c5f51 );
		ROM_LOAD( "932a",         0x2800, 0x0800, 0xe6a2039f );
		ROM_LOAD( "933",          0x3000, 0x0800, 0x73783cee );
		ROM_LOAD( "934",          0x3800, 0x0800, 0xc1a14aa2 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "epr203",       0x0000, 0x0800, 0xbe26b561 );
		ROM_LOAD( "mcs_d",        0x0800, 0x0800, 0x13932a15 );
		ROM_LOAD( "epr202",       0x1000, 0x0800, 0x26c7e800 );
		ROM_LOAD( "mcs_c",        0x1800, 0x0800, 0x24cfd145 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mooncrsb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "bepr194",      0x0000, 0x0800, 0x6a23ec6d );
		ROM_LOAD( "bepr195",      0x0800, 0x0800, 0xee262ff2 );
		ROM_LOAD( "f03.bin",      0x1000, 0x0800, 0x29a2b0ab );
		ROM_LOAD( "f04.bin",      0x1800, 0x0800, 0x4c6a5a6d );
		ROM_LOAD( "e5",           0x2000, 0x0800, 0x06d378a6 );
		ROM_LOAD( "bepr199",      0x2800, 0x0800, 0x6e84a927 );
		ROM_LOAD( "e7",           0x3000, 0x0800, 0xb45af1e8 );
		ROM_LOAD( "bepr201",      0x3800, 0x0800, 0x66da55d5 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "epr203",       0x0000, 0x0800, 0xbe26b561 );
		ROM_LOAD( "mcs_d",        0x0800, 0x0800, 0x13932a15 );
		ROM_LOAD( "epr202",       0x1000, 0x0800, 0x26c7e800 );
		ROM_LOAD( "mcs_c",        0x1800, 0x0800, 0x24cfd145 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mooncrs2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "f8.bin",       0x0000, 0x0800, 0xd36003e5 );
		ROM_LOAD( "bepr195",      0x0800, 0x0800, 0xee262ff2 );
		ROM_LOAD( "f03.bin",      0x1000, 0x0800, 0x29a2b0ab );
		ROM_LOAD( "f04.bin",      0x1800, 0x0800, 0x4c6a5a6d );
		ROM_LOAD( "e5",           0x2000, 0x0800, 0x06d378a6 );
		ROM_LOAD( "bepr199",      0x2800, 0x0800, 0x6e84a927 );
		ROM_LOAD( "e7",           0x3000, 0x0800, 0xb45af1e8 );
		ROM_LOAD( "m7.bin",       0x3800, 0x0800, 0x957ee078 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "1h_1_10.bin",  0x0000, 0x0800, 0x528da705 );
		ROM_LOAD( "12.chr",       0x0800, 0x0200, 0x5a4b17ea );
		ROM_CONTINUE(             0x0c00, 0x0200 );/* this version of the gfx ROMs has two */
		ROM_CONTINUE(             0x0a00, 0x0200 );/* groups of 16 sprites swapped */
		ROM_CONTINUE(             0x0e00, 0x0200 );
		ROM_LOAD( "1k_1_11.bin",  0x1000, 0x0800, 0x4e79ff6b );
		ROM_LOAD( "11.chr",       0x1800, 0x0200, 0xe0edccbd );
		ROM_CONTINUE(             0x1c00, 0x0200 );
		ROM_CONTINUE(             0x1a00, 0x0200 );
		ROM_CONTINUE(             0x1e00, 0x0200 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_fantazia = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "f01.bin",      0x0000, 0x0800, 0xd3e23863 );
		ROM_LOAD( "f02.bin",      0x0800, 0x0800, 0x63fa4149 );
		ROM_LOAD( "f03.bin",      0x1000, 0x0800, 0x29a2b0ab );
		ROM_LOAD( "f04.bin",      0x1800, 0x0800, 0x4c6a5a6d );
		ROM_LOAD( "f09.bin",      0x2000, 0x0800, 0x75fd5ca1 );
		ROM_LOAD( "f10.bin",      0x2800, 0x0800, 0xe4da2dd4 );
		ROM_LOAD( "f11.bin",      0x3000, 0x0800, 0x42869646 );
		ROM_LOAD( "f12.bin",      0x3800, 0x0800, 0xa48d7fb0 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "1h_1_10.bin",  0x0000, 0x0800, 0x528da705 );
		ROM_LOAD( "mcs_d",        0x0800, 0x0800, 0x13932a15 );
		ROM_LOAD( "1k_1_11.bin",  0x1000, 0x0800, 0x4e79ff6b );
		ROM_LOAD( "mcs_c",        0x1800, 0x0800, 0x24cfd145 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		/* this PROM was bad (bit 3 always set). I tried to "fix" it to get more reasonable */
		/* colors, but it should not be considered correct. It's a bootleg anyway. */
		ROM_LOAD( "6l_prom.bin",  0x0000, 0x0020, BADCRC( 0xf5381d3e ));
	ROM_END(); }}; 
	
	static RomLoadPtr rom_eagle = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "e1",           0x0000, 0x0800, 0x224c9526 );
		ROM_LOAD( "e2",           0x0800, 0x0800, 0xcc538ebd );
		ROM_LOAD( "f03.bin",      0x1000, 0x0800, 0x29a2b0ab );
		ROM_LOAD( "f04.bin",      0x1800, 0x0800, 0x4c6a5a6d );
		ROM_LOAD( "e5",           0x2000, 0x0800, 0x06d378a6 );
		ROM_LOAD( "e6",           0x2800, 0x0800, 0x0dea20d5 );
		ROM_LOAD( "e7",           0x3000, 0x0800, 0xb45af1e8 );
		ROM_LOAD( "e8",           0x3800, 0x0800, 0xc437a876 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "e10",          0x0000, 0x0800, 0x40ce58bf );
		ROM_LOAD( "e12",          0x0800, 0x0200, 0x628fdeed );
		ROM_CONTINUE(             0x0c00, 0x0200 );/* this version of the gfx ROMs has two */
		ROM_CONTINUE(             0x0a00, 0x0200 );/* groups of 16 sprites swapped */
		ROM_CONTINUE(             0x0e00, 0x0200 );
		ROM_LOAD( "e9",           0x1000, 0x0800, 0xba664099 );
		ROM_LOAD( "e11",          0x1800, 0x0200, 0xee4ec5fd );
		ROM_CONTINUE(             0x1c00, 0x0200 );
		ROM_CONTINUE(             0x1a00, 0x0200 );
		ROM_CONTINUE(             0x1e00, 0x0200 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_eagle2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "e1.7f",        0x0000, 0x0800, 0x45aab7a3 );
		ROM_LOAD( "e2",           0x0800, 0x0800, 0xcc538ebd );
		ROM_LOAD( "f03.bin",      0x1000, 0x0800, 0x29a2b0ab );
		ROM_LOAD( "f04.bin",      0x1800, 0x0800, 0x4c6a5a6d );
		ROM_LOAD( "e5",           0x2000, 0x0800, 0x06d378a6 );
		ROM_LOAD( "e6.6",         0x2800, 0x0800, 0x9f09f8c6 );
		ROM_LOAD( "e7",           0x3000, 0x0800, 0xb45af1e8 );
		ROM_LOAD( "e8",           0x3800, 0x0800, 0xc437a876 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "e10.2",        0x0000, 0x0800, 0x25b38ebd );
		ROM_LOAD( "e12",          0x0800, 0x0200, 0x628fdeed );
		ROM_CONTINUE(             0x0c00, 0x0200 );/* this version of the gfx ROMs has two */
		ROM_CONTINUE(             0x0a00, 0x0200 );/* groups of 16 sprites swapped */
		ROM_CONTINUE(             0x0e00, 0x0200 );
		ROM_LOAD( "e9",           0x1000, 0x0800, 0xba664099 );
		ROM_LOAD( "e11",          0x1800, 0x0200, 0xee4ec5fd );
		ROM_CONTINUE(             0x1c00, 0x0200 );
		ROM_CONTINUE(             0x1a00, 0x0200 );
		ROM_CONTINUE(             0x1e00, 0x0200 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_skybase = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "skybase.9a",   0x0000, 0x1000, 0x845b87a5 );
		ROM_LOAD( "skybase.8a",   0x1000, 0x1000, 0x096785c2 );
		ROM_LOAD( "skybase.7a",   0x2000, 0x1000, 0xd50c715b );
		ROM_LOAD( "skybase.6a",   0x3000, 0x1000, 0xf57edb27 );
		ROM_LOAD( "skybase.5a",   0x4000, 0x1000, 0x50365d95 );
		ROM_LOAD( "skybase.4a",   0x5000, 0x1000, 0xcbd6647f );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "skybase.7t",   0x0000, 0x1000, 0x9b471686 );
		ROM_LOAD( "skybase.8t",   0x1000, 0x1000, 0x1cf723da );
		ROM_LOAD( "skybase.10t",  0x2000, 0x1000, 0xfe02e72c );
		ROM_LOAD( "skybase.9t",   0x3000, 0x1000, 0x0871291f );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "skybase.123",  0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_moonqsr = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );/* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "mq1",          0x0000, 0x0800, 0x132c13ec );
		ROM_LOAD( "mq2",          0x0800, 0x0800, 0xc8eb74f1 );
		ROM_LOAD( "mq3",          0x1000, 0x0800, 0x33965a89 );
		ROM_LOAD( "mq4",          0x1800, 0x0800, 0xa3861d17 );
		ROM_LOAD( "mq5",          0x2000, 0x0800, 0x8bcf9c67 );
		ROM_LOAD( "mq6",          0x2800, 0x0800, 0x5750cda9 );
		ROM_LOAD( "mq7",          0x3000, 0x0800, 0x78d7fe5b );
		ROM_LOAD( "mq8",          0x3800, 0x0800, 0x4919eed5 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mqb",          0x0000, 0x0800, 0xb55ec806 );
		ROM_LOAD( "mqd",          0x0800, 0x0800, 0x9e7d0e13 );
		ROM_LOAD( "mqa",          0x1000, 0x0800, 0x66eee0db );
		ROM_LOAD( "mqc",          0x1800, 0x0800, 0xa6db5b0d );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "vid_e6.bin",   0x0000, 0x0020, 0x0b878b54 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_moonal2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "ali1",         0x0000, 0x0400, 0x0dcecab4 );
		ROM_LOAD( "ali2",         0x0400, 0x0400, 0xc6ee75a7 );
		ROM_LOAD( "ali3",         0x0800, 0x0400, 0xcd1be7e9 );
		ROM_LOAD( "ali4",         0x0c00, 0x0400, 0x83b03f08 );
		ROM_LOAD( "ali5",         0x1000, 0x0400, 0x6f3cf61d );
		ROM_LOAD( "ali6",         0x1400, 0x0400, 0xe169d432 );
		ROM_LOAD( "ali7",         0x1800, 0x0400, 0x41f64b73 );
		ROM_LOAD( "ali8",         0x1c00, 0x0400, 0xf72ee876 );
		ROM_LOAD( "ali9",         0x2000, 0x0400, 0xb7fb763c );
		ROM_LOAD( "ali10",        0x2400, 0x0400, 0xb1059179 );
		ROM_LOAD( "ali11",        0x2800, 0x0400, 0x9e79a1c6 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ali13.1h",     0x0000, 0x0800, 0xa1287bf6 );
		ROM_RELOAD(	              0x0800, 0x0800 );
		ROM_LOAD( "ali12.1k",     0x1000, 0x0800, 0x528f1481 );
		ROM_RELOAD(	              0x1800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_moonal2b = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "ali1",         0x0000, 0x0400, 0x0dcecab4 );
		ROM_LOAD( "ali2",         0x0400, 0x0400, 0xc6ee75a7 );
		ROM_LOAD( "md-2",         0x0800, 0x0800, 0x8318b187 );
		ROM_LOAD( "ali5",         0x1000, 0x0400, 0x6f3cf61d );
		ROM_LOAD( "ali6",         0x1400, 0x0400, 0xe169d432 );
		ROM_LOAD( "ali7",         0x1800, 0x0400, 0x41f64b73 );
		ROM_LOAD( "ali8",         0x1c00, 0x0400, 0xf72ee876 );
		ROM_LOAD( "ali9",         0x2000, 0x0400, 0xb7fb763c );
		ROM_LOAD( "ali10",        0x2400, 0x0400, 0xb1059179 );
		ROM_LOAD( "md-6",         0x2800, 0x0800, 0x9cc973e0 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ali13.1h",     0x0000, 0x0800, 0xa1287bf6 );
		ROM_RELOAD(	              0x0800, 0x0800 );
		ROM_LOAD( "ali12.1k",     0x1000, 0x0800, 0x528f1481 );
		ROM_RELOAD(	              0x1800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0xc3ac9467 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mshuttle = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 );/* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "my05",         0x0000, 0x1000, 0x83574af1 );
		ROM_LOAD( "my04",         0x1000, 0x1000, 0x1cfae2c8 );
		ROM_LOAD( "my03",         0x2000, 0x1000, 0xc8b8a368 );
		ROM_LOAD( "my02",         0x3000, 0x1000, 0xb6aeee6e );
		ROM_LOAD( "my01",         0x4000, 0x1000, 0xdef82adc );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "my09",         0x0000, 0x1000, 0x3601b380 );
		ROM_LOAD( "my11",         0x1000, 0x0800, 0xb659e932 );
		ROM_LOAD( "my08",         0x2000, 0x1000, 0x992b06cd );
		ROM_LOAD( "my10",         0x3000, 0x0800, 0xd860e6ce );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "mscprom1.bin", 0x0000, 0x0020, 0xea0d1af0 );
	
		ROM_REGION( 0x2000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "my07",         0x0000, 0x1000, 0x522a2920 );
		ROM_LOAD( "my06",         0x1000, 0x1000, 0x466415f2 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mshuttlj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 );/* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "mcs.5",        0x0000, 0x1000, 0xa5a292b4 );
		ROM_LOAD( "mcs.4",        0x1000, 0x1000, 0xacdc0f9e );
		ROM_LOAD( "mcs.3",        0x2000, 0x1000, 0xc1e3f5d8 );
		ROM_LOAD( "mcs.2",        0x3000, 0x1000, 0x14577703 );
		ROM_LOAD( "mcs.1",        0x4000, 0x1000, 0x27d46772 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "my09",         0x0000, 0x1000, 0x3601b380 );
		ROM_LOAD( "my11",         0x1000, 0x0800, 0xb659e932 );
		ROM_LOAD( "my08",         0x2000, 0x1000, 0x992b06cd );
		ROM_LOAD( "my10",         0x3000, 0x0800, 0xd860e6ce );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "mscprom1.bin", 0x0000, 0x0020, 0xea0d1af0 );
	
		ROM_REGION( 0x2000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "my07",         0x0000, 0x1000, 0x522a2920 );
		ROM_LOAD( "my06",         0x1000, 0x1000, 0x466415f2 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_kingball = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "prg1.7f",      0x0000, 0x1000, 0x6cb49046 );
		ROM_LOAD( "prg2.7j",      0x1000, 0x1000, 0xc223b416 );
		ROM_LOAD( "prg3.7l",      0x2000, 0x0800, 0x453634c0 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound code */
		ROM_LOAD( "kbe1.ic4",     0x0000, 0x0800, 0x5be2c80a );
		ROM_LOAD( "kbe2.ic5",     0x0800, 0x0800, 0xbb59e965 );
		ROM_LOAD( "kbe3.ic6",     0x1000, 0x0800, 0x1c94dd31 );
		ROM_LOAD( "kbe2.ic7",     0x1800, 0x0800, 0xbb59e965 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "chg1.1h",      0x0000, 0x0800, 0x9cd550e7 );
		ROM_RELOAD(	              0x0800, 0x0800 );
		ROM_LOAD( "chg2.1k",      0x1000, 0x0800, 0xa206757d );
		ROM_RELOAD(	              0x1800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "kb2-1",        0x0000, 0x0020, 0x15dd5b16 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_kingbalj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "prg1.7f",      0x0000, 0x1000, 0x6cb49046 );
		ROM_LOAD( "prg2.7j",      0x1000, 0x1000, 0xc223b416 );
		ROM_LOAD( "prg3.7l",      0x2000, 0x0800, 0x453634c0 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for sound code */
		ROM_LOAD( "kbj1.ic4",     0x0000, 0x0800, 0xba16beb7 );
		ROM_LOAD( "kbj2.ic5",     0x0800, 0x0800, 0x56686a63 );
		ROM_LOAD( "kbj3.ic6",     0x1000, 0x0800, 0xfbc570a5 );
		ROM_LOAD( "kbj2.ic7",     0x1800, 0x0800, 0x56686a63 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "chg1.1h",      0x0000, 0x0800, 0x9cd550e7 );
		ROM_RELOAD(	              0x0800, 0x0800 );
		ROM_LOAD( "chg2.1k",      0x1000, 0x0800, 0xa206757d );
		ROM_RELOAD(	              0x1800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "kb2-1",        0x0000, 0x0020, 0x15dd5b16 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_scorpnmc = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "p1.bin",       0x0000, 0x0800, 0x58818d88 );
		ROM_LOAD( "p2.bin",       0x0800, 0x0800, 0x8bec5f9f );
		ROM_LOAD( "p3.bin",       0x1000, 0x0800, 0x24b7fdff );
		ROM_LOAD( "p4.bin",       0x1800, 0x0800, 0x9082e2f0 );
		ROM_LOAD( "p5.bin",       0x2000, 0x0800, 0x20387fc0 );
		ROM_LOAD( "p6.bin",       0x2800, 0x0800, 0xf66c48e1 );
		ROM_LOAD( "p7.bin",       0x3000, 0x0800, 0x931e34c7 );
		ROM_LOAD( "p8.bin",       0x3800, 0x0800, 0xab5ab61d );
		ROM_LOAD( "p9.bin",       0x5000, 0x1000, 0xb551b974 );
		ROM_LOAD( "p10.bin",      0x6000, 0x0800, 0xa7bd8d20 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "h.bin",        0x0000, 0x1000, 0x1e5da9d6 );
		ROM_LOAD( "k.bin",        0x1000, 0x1000, 0xa57adb0a );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6331.bin",     0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_frogg = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "p1.bin",       0x0000, 0x0800, 0x1762b266 );
		ROM_LOAD( "p2.bin",       0x0800, 0x0800, 0x322f3916 );
		ROM_LOAD( "p3.bin",       0x1000, 0x0800, 0x28bd6151 );
		ROM_LOAD( "p4.bin",       0x1800, 0x0800, 0x5a69ab18 );
		ROM_LOAD( "p5.bin",       0x2000, 0x0800, 0xb4f17745 );
		ROM_LOAD( "p6.bin",       0x2800, 0x0800, 0x34be71b5 );
		ROM_LOAD( "p7.bin",       0x3000, 0x0800, 0xde3edc8c );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "k.bin",        0x0000, 0x0800, 0x05f7d883 );
		ROM_LOAD( "h.bin",        0x0800, 0x0800, 0x658745f8 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "l06_prom.bin", 0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_4in1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );  /* 64k for code  64k for banked code, encrypted */
		/* Menu Code, Fixed at 0xc000 - 0xdfff */
		ROM_LOAD( "rom1a",        0xc000, 0x1000, 0xce1af4d9 );
		ROM_LOAD( "rom1b",        0xd000, 0x1000, 0x18484f9b );
		/* Ghost Muncher PT3 - banked at 0x0000 - 0x3fff */
		ROM_LOAD( "rom1c",       0x10000, 0x1000, 0x83248a8b );
		ROM_LOAD( "rom1d",       0x11000, 0x1000, 0x053f6da0 );
		ROM_LOAD( "rom1e",       0x12000, 0x1000, 0x43c546f3 );
		ROM_LOAD( "rom1f",       0x13000, 0x1000, 0x3a086b46 );
		/* Scramble PT2 - banked at 0x0000 - 0x3fff */
		ROM_LOAD( "rom1g",       0x14000, 0x1000, 0xac0e2050 );
		ROM_LOAD( "rom1h",       0x15000, 0x1000, 0xdc11a513 );
		ROM_LOAD( "rom1i",       0x16000, 0x1000, 0xa5fb6be4 );
		ROM_LOAD( "rom1j",       0x17000, 0x1000, 0x9054cfbe );
		/* Galaxian PT5 - banked at 0x0000 - 0x3fff */
		ROM_LOAD( "rom2c",       0x18000, 0x1000, 0x7cd98e11 );
		ROM_LOAD( "rom2d",       0x19000, 0x1000, 0x9402f32e );
		ROM_LOAD( "rom2e",       0x1a000, 0x1000, 0x468e81df );
		/* Galactic Convoy - banked at 0x0000 - 0x3fff */
		ROM_LOAD( "rom2g",       0x1c000, 0x1000, 0xb1ce3976 );
		ROM_LOAD( "rom2h",       0x1d000, 0x1000, 0x7eab5670 );
		ROM_LOAD( "rom2i",       0x1e000, 0x1000, 0x44565ac5 );
	
		ROM_REGION( 0x8000, REGION_GFX1, ROMREGION_DISPOSE );
		/* Ghost Muncher PT3 GFX */
		ROM_LOAD( "rom4b",        0x4000, 0x0800, 0x7e6495af );
		ROM_CONTINUE(             0x0000, 0x0800 );
		ROM_LOAD( "rom3b",        0x6000, 0x0800, 0x7475f72f );
		ROM_CONTINUE(             0x2000, 0x0800 );
		/* Scramble PT2 GFX */
		ROM_LOAD( "rom4c",        0x4800, 0x0800, 0x3355d46d );
		ROM_RELOAD(               0x0800, 0x0800);
		ROM_LOAD( "rom3c",        0x6800, 0x0800, 0xac755a25 );
		ROM_RELOAD(               0x2800, 0x0800);
		/* Galaxians PT5 GFX */
		ROM_LOAD( "rom4d",        0x5000, 0x0800, 0xbbdddb65 );
		ROM_CONTINUE(             0x1000, 0x0800);
		ROM_LOAD( "rom3d",        0x7000, 0x0800, 0x91a00204 );
		ROM_CONTINUE(             0x3000, 0x0800);
		/* Galactic Convoy GFX */
		ROM_LOAD( "rom4e",        0x5800, 0x0800, 0x0cb9e297 );
		ROM_CONTINUE(             0x1800, 0x0800 );
		ROM_LOAD( "rom3e",        0x7800, 0x0800, 0xa1fe77f9 );
		ROM_CONTINUE(             0x3800, 0x0800 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
	    ROM_LOAD( "6l.bpr",       0x0000, 0x0020, 0x6a0c7d87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_bagmanmc = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "b1.bin",       0x0000, 0x1000, 0xb74c75ee );
		ROM_LOAD( "b2.bin",       0x1000, 0x1000, 0xa7d99916 );
		ROM_LOAD( "b3.bin",       0x2000, 0x1000, 0xc78f5360 );
		ROM_LOAD( "b4.bin",       0x3000, 0x1000, 0xeebd3bd1 );
		ROM_LOAD( "b5.bin",       0x4000, 0x1000, 0x0fe24b8c );
		ROM_LOAD( "b6.bin",       0x5000, 0x1000, 0xf50390e7 );
	
		ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "g1-u.bin",     0x0000, 0x0800, 0xb63cfae4 );
		ROM_CONTINUE(             0x2000, 0x0800 );
		ROM_LOAD( "g2-u.bin",     0x1000, 0x0800, 0xa2790089 );
		ROM_CONTINUE(             0x2800, 0x0800 );
		ROM_LOAD( "g1-l.bin",     0x0800, 0x0800, 0x2ae6b5ab );
		ROM_LOAD( "g2-l.bin",     0x1800, 0x0800, 0x98b37397 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "bagmanmc.clr", 0x0000, 0x0020, 0x00000000 );// missing
	ROM_END(); }}; 
	
	static RomLoadPtr rom_dkongjrm = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "a1",           0x0000, 0x1000, 0x299486e9 );
		ROM_LOAD( "a2",           0x1000, 0x1000, 0xa74a193b );
		ROM_LOAD( "b2",           0x2000, 0x1000, 0x7bc4f236 );
		ROM_LOAD( "c1",           0x3000, 0x1000, 0x0f594c21 );
		ROM_LOAD( "d1",           0x4000, 0x1000, 0xcf7d7296 );
		ROM_LOAD( "e2",           0x5000, 0x1000, 0xf7528a52 );
		ROM_LOAD( "f1",           0x7000, 0x1000, 0x9b1d4cc5 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "v_3pa.bin",    0x0000, 0x1000, 0x4974ffef );
		ROM_LOAD( "a2.gfx",       0x1000, 0x1000, 0x51845eaf );
		ROM_LOAD( "v_3na.bin",    0x2000, 0x1000, 0xa95c4c63 );
		ROM_LOAD( "b2.gfx",       0x3000, 0x1000, 0x7b39c3d0 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "hustler.clr",  0x0000, 0x0020, 0xaa1f7f5e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_froggrmc = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "epr-1031.15",  0x0000, 0x1000, 0x4b7c8d11 );
		ROM_LOAD( "epr-1032.16",  0x1000, 0x1000, 0xac00b9d9 );
		ROM_LOAD( "epr-1033.33",  0x2000, 0x1000, 0xbc1d6fbc );
		ROM_LOAD( "epr-1034.34",  0x3000, 0x1000, 0x9efe7399 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "epr-1082.42",  0x0000, 0x1000, 0x802843c2 );
		ROM_LOAD( "epr-1035.43",  0x1000, 0x0800, 0x14e74148 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "frogger.607",  0x0000, 0x0800, 0x05f7d883 );
		ROM_LOAD( "epr-1036.1k",  0x0800, 0x0800, 0x658745f8 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "pr-91.6l",     0x0000, 0x0020, 0x413703bf );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_galaxian	   = new GameDriver("1979"	,"galaxian"	,"galaxian.java"	,rom_galaxian,null	,machine_driver_galaxian	,input_ports_galaxian	,null	,ROT90	,	"Namco", "Galaxian (Namco set 1)" )
	public static GameDriver driver_galaxiaj	   = new GameDriver("1979"	,"galaxiaj"	,"galaxian.java"	,rom_galaxiaj,driver_galaxian	,machine_driver_galaxian	,input_ports_superg	,null	,ROT90	,	"Namco", "Galaxian (Namco set 2)" )
	public static GameDriver driver_galmidw	   = new GameDriver("1979"	,"galmidw"	,"galaxian.java"	,rom_galmidw,driver_galaxian	,machine_driver_galaxian	,input_ports_galaxian	,null	,ROT90	,	"[Namco] (Midway license)", "Galaxian (Midway)" )
	public static GameDriver driver_galmidwo	   = new GameDriver("1979"	,"galmidwo"	,"galaxian.java"	,rom_galmidwo,driver_galaxian	,machine_driver_galaxian	,input_ports_galaxian	,null	,ROT90	,	"[Namco] (Midway license)", "Galaxian (Midway, old rev)" )
	public static GameDriver driver_superg	   = new GameDriver("1979"	,"superg"	,"galaxian.java"	,rom_superg,driver_galaxian	,machine_driver_galaxian	,input_ports_superg	,null	,ROT90	,	"hack", "Super Galaxians" )
	public static GameDriver driver_galapx	   = new GameDriver("1979"	,"galapx"	,"galaxian.java"	,rom_galapx,driver_galaxian	,machine_driver_galaxian	,input_ports_superg	,null	,ROT90	,	"hack", "Galaxian Part X" )
	public static GameDriver driver_galap1	   = new GameDriver("1979"	,"galap1"	,"galaxian.java"	,rom_galap1,driver_galaxian	,machine_driver_galaxian	,input_ports_superg	,null	,ROT90	,	"hack", "Space Invaders Galactica" )
	public static GameDriver driver_galap4	   = new GameDriver("1979"	,"galap4"	,"galaxian.java"	,rom_galap4,driver_galaxian	,machine_driver_galaxian	,input_ports_superg	,null	,ROT90	,	"hack", "Galaxian Part 4" )
	public static GameDriver driver_galturbo	   = new GameDriver("1979"	,"galturbo"	,"galaxian.java"	,rom_galturbo,driver_galaxian	,machine_driver_galaxian	,input_ports_superg	,null	,ROT90	,	"hack", "Galaxian Turbo" )
	public static GameDriver driver_swarm	   = new GameDriver("1979"	,"swarm"	,"galaxian.java"	,rom_swarm,driver_galaxian	,machine_driver_galaxian	,input_ports_swarm	,null	,ROT90	,	"hack", "Swarm" )
	public static GameDriver driver_zerotime	   = new GameDriver("1979"	,"zerotime"	,"galaxian.java"	,rom_zerotime,driver_galaxian	,machine_driver_galaxian	,input_ports_zerotime	,null	,ROT90	,	"Petaco S.A.", "Zero Time" )
	public static GameDriver driver_gmgalax	   = new GameDriver("1981"	,"gmgalax"	,"galaxian.java"	,rom_gmgalax,null	,machine_driver_gmgalax	,input_ports_gmgalax	,init_gmgalax	,ROT90	,	"bootleg", "Ghostmuncher Galaxian (bootleg)" )
	public static GameDriver driver_pisces	   = new GameDriver("19??"	,"pisces"	,"galaxian.java"	,rom_pisces,null	,machine_driver_pisces	,input_ports_pisces	,init_pisces	,ROT90	,	"Subelectro", "Pisces" )
	public static GameDriver driver_piscesb	   = new GameDriver("19??"	,"piscesb"	,"galaxian.java"	,rom_piscesb,driver_pisces	,machine_driver_pisces	,input_ports_piscesb	,init_pisces	,ROT90	,	"bootleg", "Pisces (bootleg)" )
	public static GameDriver driver_uniwars	   = new GameDriver("1980"	,"uniwars"	,"galaxian.java"	,rom_uniwars,null	,machine_driver_pisces	,input_ports_superg	,init_pisces	,ROT90	,	"Irem", "UniWar S" )
	public static GameDriver driver_gteikoku	   = new GameDriver("1980"	,"gteikoku"	,"galaxian.java"	,rom_gteikoku,driver_uniwars	,machine_driver_pisces	,input_ports_superg	,init_pisces	,ROT90	,	"Irem", "Gingateikoku No Gyakushu" )
	public static GameDriver driver_gteikokb	   = new GameDriver("1980"	,"gteikokb"	,"galaxian.java"	,rom_gteikokb,driver_uniwars	,machine_driver_pisces	,input_ports_gteikokb	,init_pisces	,ROT270	,	"bootleg", "Gingateikoku No Gyakushu (bootleg set 1)" )
	public static GameDriver driver_gteikob2	   = new GameDriver("1980"	,"gteikob2"	,"galaxian.java"	,rom_gteikob2,driver_uniwars	,machine_driver_gteikob2	,input_ports_gteikob2	,init_gteikob2	,ROT270	,	"bootleg", "Gingateikoku No Gyakushu (bootleg set 2)" )
	public static GameDriver driver_spacbatt	   = new GameDriver("1980"	,"spacbatt"	,"galaxian.java"	,rom_spacbatt,driver_uniwars	,machine_driver_pisces	,input_ports_spacbatt	,init_pisces	,ROT90	,	"bootleg", "Space Battle" )
	public static GameDriver driver_batman2	   = new GameDriver("1981"	,"batman2"	,"galaxian.java"	,rom_batman2,driver_phoenix	,machine_driver_batman2	,input_ports_batman2	,init_pisces	,ROT270	,	"bootleg", "Batman Part 2" )
	public static GameDriver driver_warofbug	   = new GameDriver("1981"	,"warofbug"	,"galaxian.java"	,rom_warofbug,null	,machine_driver_galaxian	,input_ports_warofbug	,init_pisces	,ROT90	,	"Armenia", "War of the Bugs or Monsterous Manouvers in a Mushroom Maze" )
	public static GameDriver driver_redufo	   = new GameDriver("19??"	,"redufo"	,"galaxian.java"	,rom_redufo,null	,machine_driver_galaxian	,input_ports_redufo	,init_pisces	,ROT90	,	"bootleg", "Defend the Terra Attack on the Red UFO" )
	public static GameDriver driver_exodus	   = new GameDriver("19??"	,"exodus"	,"galaxian.java"	,rom_exodus,driver_redufo	,machine_driver_galaxian	,input_ports_exodus	,init_pisces	,ROT90	,	"Subelectro", "Exodus (bootleg?)" )
	public static GameDriver driver_streakng	   = new GameDriver("1981"	,"streakng"	,"galaxian.java"	,rom_streakng,null	,machine_driver_pacmanbl	,input_ports_streakng	,null	,ROT90	,	"Shoei", "Streaking", GAME_IMPERFECT_COLORS )
	public static GameDriver driver_pacmanbl	   = new GameDriver("1981"	,"pacmanbl"	,"galaxian.java"	,rom_pacmanbl,driver_puckman	,machine_driver_pacmanbl	,input_ports_pacmanbl	,init_pisces	,ROT270	,	"bootleg", "Pac-Man (Galaxian hardware)" )
	public static GameDriver driver_devilfsg	   = new GameDriver("1984"	,"devilfsg"	,"galaxian.java"	,rom_devilfsg,driver_devilfsh	,machine_driver_devilfsg	,input_ports_devilfsg	,null	,ROT270	,	"Vision / Artic", "Devil Fish (Galaxian hardware, bootleg?)" )
	public static GameDriver driver_zigzag	   = new GameDriver("1982"	,"zigzag"	,"galaxian.java"	,rom_zigzag,null	,machine_driver_zigzag	,input_ports_zigzag	,null	,ROT90	,	"LAX", "Zig Zag (Galaxian hardware, set 1)" )
	public static GameDriver driver_zigzag2	   = new GameDriver("1982"	,"zigzag2"	,"galaxian.java"	,rom_zigzag2,driver_zigzag	,machine_driver_zigzag	,input_ports_zigzag	,null	,ROT90	,	"LAX", "Zig Zag (Galaxian hardware, set 2)" )
	public static GameDriver driver_scramblb	   = new GameDriver("1981"	,"scramblb"	,"galaxian.java"	,rom_scramblb,driver_scramble	,machine_driver_scramblb	,input_ports_scramblb	,null	,ROT90	,	"bootleg", "Scramble (Galaxian hardware)" )
	public static GameDriver driver_jumpbug	   = new GameDriver("1981"	,"jumpbug"	,"galaxian.java"	,rom_jumpbug,null	,machine_driver_jumpbug	,input_ports_jumpbug	,null	,ROT90	,	"Rock-ola", "Jump Bug" )
	public static GameDriver driver_jumpbugb	   = new GameDriver("1981"	,"jumpbugb"	,"galaxian.java"	,rom_jumpbugb,driver_jumpbug	,machine_driver_jumpbug	,input_ports_jumpbug	,null	,ROT90	,	"bootleg", "Jump Bug (bootleg)" )
	public static GameDriver driver_levers	   = new GameDriver("1983"	,"levers"	,"galaxian.java"	,rom_levers,null	,machine_driver_jumpbug	,input_ports_levers	,null	,ROT90	,	"Rock-ola", "Levers" )
	public static GameDriver driver_azurian	   = new GameDriver("1982"	,"azurian"	,"galaxian.java"	,rom_azurian,null	,machine_driver_galaxian	,input_ports_azurian	,init_azurian	,ROT90	,	"Rait Electronics Ltd", "Azurian Attack" )
	public static GameDriver driver_orbitron	   = new GameDriver("19??"	,"orbitron"	,"galaxian.java"	,rom_orbitron,null	,machine_driver_galaxian	,input_ports_orbitron	,init_pisces	,ROT270	,	"Signatron USA", "Orbitron" )
	public static GameDriver driver_checkman	   = new GameDriver("1982"	,"checkman"	,"galaxian.java"	,rom_checkman,null	,machine_driver_checkman	,input_ports_checkman	,init_checkman	,ROT90	,	"Zilec-Zenitone", "Check Man" )
	public static GameDriver driver_checkmaj	   = new GameDriver("1982"	,"checkmaj"	,"galaxian.java"	,rom_checkmaj,driver_checkman	,machine_driver_checkmaj	,input_ports_checkmaj	,init_checkmaj	,ROT90	,	"Jaleco", "Check Man (Japan)" )
	public static GameDriver driver_dingo	   = new GameDriver("1983"	,"dingo"	,"galaxian.java"	,rom_dingo,null	,machine_driver_checkmaj	,input_ports_dingo	,init_dingo	,ROT90	,	"Ashby Computers and Graphics LTD. (Jaleco license)", "Dingo" )
	public static GameDriver driver_blkhole	   = new GameDriver("1981"	,"blkhole"	,"galaxian.java"	,rom_blkhole,null	,machine_driver_galaxian	,input_ports_blkhole	,null	,ROT90	,	"TDS", "Black Hole" )
	public static GameDriver driver_mooncrst	   = new GameDriver("1980"	,"mooncrst"	,"galaxian.java"	,rom_mooncrst,null	,machine_driver_mooncrst	,input_ports_mooncrst	,init_mooncrst	,ROT90	,	"Nichibutsu", "Moon Cresta (Nichibutsu)" )
	public static GameDriver driver_mooncrsu	   = new GameDriver("1980"	,"mooncrsu"	,"galaxian.java"	,rom_mooncrsu,driver_mooncrst	,machine_driver_mooncrst	,input_ports_mooncrst	,init_mooncrsu	,ROT90	,	"Nichibutsu USA", "Moon Cresta (Nichibutsu, unencrypted)" )
	public static GameDriver driver_mooncrsa	   = new GameDriver("1980"	,"mooncrsa"	,"galaxian.java"	,rom_mooncrsa,driver_mooncrst	,machine_driver_mooncrst	,input_ports_mooncrsa	,init_mooncrst	,ROT90	,	"Nichibutsu", "Moon Cresta (Nichibutsu, old rev)" )
	public static GameDriver driver_mooncrsg	   = new GameDriver("1980"	,"mooncrsg"	,"galaxian.java"	,rom_mooncrsg,driver_mooncrst	,machine_driver_mooncrst	,input_ports_mooncrsg	,init_mooncrsu	,ROT90	,	"Gremlin", "Moon Cresta (Gremlin)" )
	public static GameDriver driver_smooncrs	   = new GameDriver("1980?"	,"smooncrs"	,"galaxian.java"	,rom_smooncrs,driver_mooncrst	,machine_driver_mooncrst	,input_ports_smooncrs	,init_mooncrsu	,ROT90	,	"Gremlin", "Super Moon Cresta" )
	public static GameDriver driver_mooncrsb	   = new GameDriver("1980"	,"mooncrsb"	,"galaxian.java"	,rom_mooncrsb,driver_mooncrst	,machine_driver_mooncrst	,input_ports_mooncrsa	,init_mooncrsu	,ROT90	,	"bootleg", "Moon Cresta (bootleg set 1)" )
	public static GameDriver driver_mooncrs2	   = new GameDriver("1980"	,"mooncrs2"	,"galaxian.java"	,rom_mooncrs2,driver_mooncrst	,machine_driver_mooncrst	,input_ports_mooncrsa	,init_mooncrsu	,ROT90	,	"Nichibutsu", "Moon Cresta (bootleg set 2)" )
	public static GameDriver driver_fantazia	   = new GameDriver("1980"	,"fantazia"	,"galaxian.java"	,rom_fantazia,driver_mooncrst	,machine_driver_mooncrst	,input_ports_fantazia	,init_mooncrsu	,ROT90	,	"bootleg", "Fantazia", GAME_IMPERFECT_COLORS )
	public static GameDriver driver_eagle	   = new GameDriver("1980"	,"eagle"	,"galaxian.java"	,rom_eagle,driver_mooncrst	,machine_driver_mooncrst	,input_ports_eagle	,init_mooncrsu	,ROT90	,	"Centuri", "Eagle (set 1)" )
	public static GameDriver driver_eagle2	   = new GameDriver("1980"	,"eagle2"	,"galaxian.java"	,rom_eagle2,driver_mooncrst	,machine_driver_mooncrst	,input_ports_eagle2	,init_mooncrsu	,ROT90	,	"Centuri", "Eagle (set 2)" )
	public static GameDriver driver_mooncrgx	   = new GameDriver("1980"	,"mooncrgx"	,"galaxian.java"	,rom_mooncrgx,driver_mooncrst	,machine_driver_mooncrgx	,input_ports_mooncrgx	,init_mooncrgx	,ROT270	,	"bootleg", "Moon Cresta (Galaxian hardware)" )
	public static GameDriver driver_moonqsr	   = new GameDriver("1980"	,"moonqsr"	,"galaxian.java"	,rom_moonqsr,null	,machine_driver_moonqsr	,input_ports_moonqsr	,init_moonqsr	,ROT90	,	"Nichibutsu", "Moon Quasar" )
	public static GameDriver driver_mshuttle	   = new GameDriver("1981"	,"mshuttle"	,"galaxian.java"	,rom_mshuttle,null	,machine_driver_mshuttle	,input_ports_mshuttle	,init_mshuttle	,ROT0	,	"Nichibutsu", "Moon Shuttle (US?)" )
	public static GameDriver driver_mshuttlj	   = new GameDriver("1981"	,"mshuttlj"	,"galaxian.java"	,rom_mshuttlj,driver_mshuttle	,machine_driver_mshuttle	,input_ports_mshuttle	,init_cclimbrj	,ROT0	,	"Nichibutsu", "Moon Shuttle (Japan)" )
	public static GameDriver driver_moonal2	   = new GameDriver("1980"	,"moonal2"	,"galaxian.java"	,rom_moonal2,null	,machine_driver_mooncrst	,input_ports_moonal2	,null	,ROT90	,	"Nichibutsu", "Moon Alien Part 2" )
	public static GameDriver driver_moonal2b	   = new GameDriver("1980"	,"moonal2b"	,"galaxian.java"	,rom_moonal2b,driver_moonal2	,machine_driver_mooncrst	,input_ports_moonal2	,null	,ROT90	,	"Nichibutsu", "Moon Alien Part 2 (older version)" )
	public static GameDriver driver_skybase	   = new GameDriver("1982"	,"skybase"	,"galaxian.java"	,rom_skybase,null	,machine_driver_skybase	,input_ports_skybase	,null	,ROT90	,	"Omori Electric Co., Ltd.", "Sky Base" )
	public static GameDriver driver_omega	   = new GameDriver("19??"	,"omega"	,"galaxian.java"	,rom_omega,driver_theend	,machine_driver_galaxian	,input_ports_omega	,null	,ROT270	,	"bootleg?", "Omega" )
	public static GameDriver driver_kingball	   = new GameDriver("1980"	,"kingball"	,"galaxian.java"	,rom_kingball,null	,machine_driver_kingball	,input_ports_kingball	,init_kingball	,ROT90	,	"Namco", "King & Balloon (US)" )
	public static GameDriver driver_kingbalj	   = new GameDriver("1980"	,"kingbalj"	,"galaxian.java"	,rom_kingbalj,driver_kingball	,machine_driver_kingball	,input_ports_kingball	,init_kingball	,ROT90	,	"Namco", "King & Balloon (Japan)" )
	public static GameDriver driver_scorpnmc	   = new GameDriver("19??"	,"scorpnmc"	,"galaxian.java"	,rom_scorpnmc,null	,machine_driver_scorpnmc	,input_ports_scorpnmc	,null	,ROT90	,	"Dorneer", "Scorpion (Moon Cresta hardware)" )
	public static GameDriver driver_frogg	   = new GameDriver("1981"	,"frogg"	,"galaxian.java"	,rom_frogg,driver_frogger	,machine_driver_galaxian	,input_ports_frogg	,null	,ROT90	,	"bootleg", "Frog (Galaxian hardware)" )
	public static GameDriver driver_4in1	   = new GameDriver("1981"	,"4in1"	,"galaxian.java"	,rom_4in1,null	,machine_driver_4in1	,input_ports_4in1	,init_4in1	,ROT90	,	"Armenia / Food and Fun", "4 Fun in 1", GAME_IMPERFECT_SOUND )
	public static GameDriver driver_bagmanmc	   = new GameDriver("1982"	,"bagmanmc"	,"galaxian.java"	,rom_bagmanmc,driver_bagman	,machine_driver_bagmanmc	,input_ports_bagmanmc	,null	,ROT90	,	"bootleg", "Bagman (Moon Cresta hardware)", GAME_WRONG_COLORS  )
	public static GameDriver driver_dkongjrm	   = new GameDriver("1982"	,"dkongjrm"	,"galaxian.java"	,rom_dkongjrm,driver_dkongjr	,machine_driver_dkongjrm	,input_ports_dkongjrm	,null	,ROT90	,	"bootleg", "Donkey Kong Jr. (Moon Cresta hardware)", GAME_WRONG_COLORS | GAME_IMPERFECT_SOUND )
	public static GameDriver driver_froggrmc	   = new GameDriver("1981"	,"froggrmc"	,"galaxian.java"	,rom_froggrmc,driver_frogger	,machine_driver_froggrmc	,input_ports_froggrmc	,init_froggers	,ROT90	,	"bootleg?", "Frogger (Moon Cresta hardware)" )
}
