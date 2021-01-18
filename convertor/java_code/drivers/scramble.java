/***************************************************************************

 Scramble hardware


Interesting tidbit:

There is a bug in Amidars and Triple Punch. Look at the loop at 0x2715.
It expects DE to be saved during the call to 0x2726, but it can be destroyed,
causing the loop to read all kinds of bogus memory locations.


To Do:

- Mariner has discrete sound circuits connected to the 8910's output ports


Notes:

- While Atlantis has a cabinet switch, it doesn't use the 2nd player controls
  in cocktail mode.

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class scramble
{
	
	
	
	public static Memory_ReadAddress scramble_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4bff, MRA_RAM ),
		new Memory_ReadAddress( 0x4c00, 0x4fff, galaxian_videoram_r ),	/* mirror */
		new Memory_ReadAddress( 0x5000, 0x50ff, MRA_RAM ),
		new Memory_ReadAddress( 0x7000, 0x7000, watchdog_reset_r ),
		new Memory_ReadAddress( 0x7800, 0x7800, watchdog_reset_r ),
		new Memory_ReadAddress( 0x8100, 0x8103, ppi8255_0_r ),
		new Memory_ReadAddress( 0x8110, 0x8113, ppi8255_0_r ),  /* mirror for Frog */
		new Memory_ReadAddress( 0x8200, 0x8203, ppi8255_1_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress scramble_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x4c00, 0x4fff, galaxian_videoram_w ),	/* mirror address */
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6801, 0x6801, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x6802, 0x6802, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0x6804, 0x6804, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0x6806, 0x6806, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x6807, 0x6807, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x8100, 0x8103, ppi8255_0_w ),
		new Memory_WriteAddress( 0x8200, 0x8203, ppi8255_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress ckongs_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x6bff, MRA_RAM ),
		new Memory_ReadAddress( 0x7000, 0x7003, ppi8255_0_r ),
		new Memory_ReadAddress( 0x7800, 0x7803, ppi8255_1_r ),
		new Memory_ReadAddress( 0x9000, 0x93ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9800, 0x98ff, MRA_RAM ),
		new Memory_ReadAddress( 0xb000, 0xb000, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress ckongs_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x6bff, MWA_RAM ),
		new Memory_WriteAddress( 0x7000, 0x7003, ppi8255_0_w ),
		new Memory_WriteAddress( 0x7800, 0x7803, ppi8255_1_w ),
		new Memory_WriteAddress( 0x9000, 0x93ff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x9840, 0x985f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x9880, 0x98ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa801, 0xa801, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0xa802, 0xa802, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0xa806, 0xa806, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0xa807, 0xa807, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress mars_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4bff, MRA_RAM ),
		new Memory_ReadAddress( 0x4c00, 0x4fff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x5000, 0x50ff, MRA_RAM ),
		new Memory_ReadAddress( 0x7000, 0x7000, watchdog_reset_r ),
		new Memory_ReadAddress( 0x7000, 0x7000, MRA_NOP ),
		new Memory_ReadAddress( 0x8100, 0x810f, mars_ppi8255_0_r ),
		new Memory_ReadAddress( 0x8200, 0x820f, mars_ppi8255_1_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mars_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6800, 0x6800, galaxian_coin_counter_1_w ),
		new Memory_WriteAddress( 0x6801, 0x6801, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0x6802, 0x6802, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x6808, 0x6808, galaxian_coin_counter_0_w ),
		new Memory_WriteAddress( 0x6809, 0x6809, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x680b, 0x680b, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x8100, 0x810f, mars_ppi8255_0_w ),
		new Memory_WriteAddress( 0x8200, 0x820f, mars_ppi8255_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress newsin7_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4bff, MRA_RAM ),
		new Memory_ReadAddress( 0x4c00, 0x4fff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x5000, 0x50ff, MRA_RAM ),
		new Memory_ReadAddress( 0x7000, 0x7000, watchdog_reset_r ),
		new Memory_ReadAddress( 0x8200, 0x820f, mars_ppi8255_1_r ),
		new Memory_ReadAddress( 0xa000, 0xafff, MRA_ROM ),
		new Memory_ReadAddress( 0xc100, 0xc10f, mars_ppi8255_0_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress newsin7_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6800, 0x6800, galaxian_coin_counter_1_w ),
		new Memory_WriteAddress( 0x6801, 0x6801, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0x6802, 0x6802, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x6808, 0x6808, galaxian_coin_counter_0_w ),
		new Memory_WriteAddress( 0x6809, 0x6809, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x680b, 0x680b, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x8200, 0x820f, mars_ppi8255_1_w ),
		new Memory_WriteAddress( 0xa000, 0xafff, MWA_ROM ),
		new Memory_WriteAddress( 0xc100, 0xc10f, mars_ppi8255_0_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress mrkougar_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x4c00, 0x4fff, galaxian_videoram_w ),	/* mirror address */
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6800, 0x6800, galaxian_coin_counter_1_w ),
		new Memory_WriteAddress( 0x6801, 0x6801, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x6808, 0x6808, galaxian_coin_counter_0_w ),
		new Memory_WriteAddress( 0x6809, 0x6809, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x680b, 0x680b, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x8100, 0x810f, mars_ppi8255_0_w ),
		new Memory_WriteAddress( 0x8200, 0x820f, mars_ppi8255_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress hotshock_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4bff, MRA_RAM ),
		new Memory_ReadAddress( 0x4c00, 0x4fff, galaxian_videoram_r ),
		new Memory_ReadAddress( 0x5000, 0x50ff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0x8000, input_port_0_r ),
		new Memory_ReadAddress( 0x8001, 0x8001, input_port_1_r ),
		new Memory_ReadAddress( 0x8002, 0x8002, input_port_2_r ),
		new Memory_ReadAddress( 0x8003, 0x8003, input_port_3_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hotshock_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6000, 0x6000, galaxian_coin_counter_2_w ),
		new Memory_WriteAddress( 0x6002, 0x6002, galaxian_coin_counter_1_w ),
		new Memory_WriteAddress( 0x6004, 0x6004, hotshock_flip_screen_w ),
		new Memory_WriteAddress( 0x6005, 0x6005, galaxian_coin_counter_0_w ),
		new Memory_WriteAddress( 0x6006, 0x6006, galaxian_gfxbank_w ),
		new Memory_WriteAddress( 0x6801, 0x6801, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x7000, 0x7000, watchdog_reset_w ),
		new Memory_WriteAddress( 0x8000, 0x8000, soundlatch_w ),
		new Memory_WriteAddress( 0x9000, 0x9000, hotshock_sh_irqtrigger_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress hunchbks_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_ROM ),
		new Memory_ReadAddress( 0x1210, 0x1213, ppi8255_1_r ),
		new Memory_ReadAddress( 0x1400, 0x14ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1500, 0x1503, ppi8255_0_r ),
		new Memory_ReadAddress( 0x1680, 0x1680, watchdog_reset_r ),
		new Memory_ReadAddress( 0x1800, 0x1fff, MRA_RAM ),
		new Memory_ReadAddress( 0x2000, 0x2fff, MRA_ROM ),
		new Memory_ReadAddress( 0x3000, 0x3fff, hunchbks_mirror_r ),
		new Memory_ReadAddress( 0x4000, 0x4fff, MRA_ROM ),
		new Memory_ReadAddress( 0x5000, 0x5fff, hunchbks_mirror_r ),
		new Memory_ReadAddress( 0x6000, 0x6fff, MRA_ROM ),
		new Memory_ReadAddress( 0x7000, 0x7fff, hunchbks_mirror_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hunchbks_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_ROM ),
		new Memory_WriteAddress( 0x1210, 0x1213, ppi8255_1_w ),
		new Memory_WriteAddress( 0x1400, 0x143f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x1440, 0x145f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x1460, 0x147f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x1480, 0x14ff, MWA_RAM ),
		new Memory_WriteAddress( 0x1500, 0x1503, ppi8255_0_w ),
		new Memory_WriteAddress( 0x1606, 0x1606, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x1607, 0x1607, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x1800, 0x1bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x1c00, 0x1fff, MWA_RAM ),
		new Memory_WriteAddress( 0x2000, 0x2fff, MWA_ROM ),
		new Memory_WriteAddress( 0x3000, 0x3fff, hunchbks_mirror_w ),
		new Memory_WriteAddress( 0x4000, 0x4fff, MWA_ROM ),
		new Memory_WriteAddress( 0x5000, 0x5fff, hunchbks_mirror_w ),
		new Memory_WriteAddress( 0x6000, 0x6fff, MWA_ROM ),
		new Memory_WriteAddress( 0x7000, 0x7fff, hunchbks_mirror_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress sfx_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4bff, MRA_RAM ),
		new Memory_ReadAddress( 0x4c00, 0x4fff, galaxian_videoram_r ),	/* mirror */
		new Memory_ReadAddress( 0x5000, 0x50ff, MRA_RAM ),
		new Memory_ReadAddress( 0x7000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8100, 0x8103, ppi8255_0_r ),
		new Memory_ReadAddress( 0x8200, 0x8203, ppi8255_1_r ),
		new Memory_ReadAddress( 0xc000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sfx_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x4c00, 0x4fff, galaxian_videoram_w ),	/* mirror address */
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6800, 0x6800, scramble_background_red_w ),
		new Memory_WriteAddress( 0x6801, 0x6801, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x6802, 0x6802, galaxian_coin_counter_w ),
		new Memory_WriteAddress( 0x6803, 0x6803, scramble_background_blue_w ),
		new Memory_WriteAddress( 0x6804, 0x6804, galaxian_stars_enable_w ),
		new Memory_WriteAddress( 0x6805, 0x6805, scramble_background_green_w ),
		new Memory_WriteAddress( 0x6806, 0x6806, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x6807, 0x6807, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x7000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8100, 0x8103, ppi8255_0_w ),
		new Memory_WriteAddress( 0x8200, 0x8203, ppi8255_1_w ),
		new Memory_WriteAddress( 0xc000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sfx_sample_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x83ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sfx_sample_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sfx_sample_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x04, 0x07, ppi8255_2_r ),
	MEMORY_END
	
	public static IO_WritePort sfx_sample_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x04, 0x07, ppi8255_2_w ),
		new IO_WritePort( 0x10, 0x10, DAC_0_signed_data_w ),
	MEMORY_END
	
	
	public static Memory_ReadAddress mimonkey_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, galaxian_videoram_r ),	/* mirror address?, probably not */
		new Memory_ReadAddress( 0x4400, 0x4bff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x50ff, MRA_RAM ),
		new Memory_ReadAddress( 0x7000, 0x7000, watchdog_reset_r ),
		new Memory_ReadAddress( 0x8100, 0x8103, ppi8255_0_r ),
		new Memory_ReadAddress( 0x8200, 0x8203, ppi8255_1_r ),
		new Memory_ReadAddress( 0xc000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mimonkey_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, galaxian_videoram_w ),	/* mirror address?, probably not */
		new Memory_WriteAddress( 0x4400, 0x47ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4800, 0x4bff, galaxian_videoram_w, galaxian_videoram ),
		new Memory_WriteAddress( 0x5000, 0x503f, galaxian_attributesram_w, galaxian_attributesram ),
		new Memory_WriteAddress( 0x5040, 0x505f, MWA_RAM, galaxian_spriteram, galaxian_spriteram_size ),
		new Memory_WriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new Memory_WriteAddress( 0x5080, 0x50ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6801, 0x6801, galaxian_nmi_enable_w ),
		new Memory_WriteAddress( 0x6800, 0x6802, galaxian_gfxbank_w ),
		new Memory_WriteAddress( 0x6806, 0x6806, galaxian_flip_screen_x_w ),
		new Memory_WriteAddress( 0x6807, 0x6807, galaxian_flip_screen_y_w ),
		new Memory_WriteAddress( 0x8100, 0x8103, ppi8255_0_w ),
		new Memory_WriteAddress( 0x8200, 0x8203, ppi8255_1_w ),
		new Memory_WriteAddress( 0xc000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort triplep_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, AY8910_read_port_0_r ),
		new IO_ReadPort( 0x02, 0x02, triplep_pip_r ),
		new IO_ReadPort( 0x03, 0x03, triplep_pap_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort triplep_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, AY8910_write_port_0_w ),
		new IO_WritePort( 0x01, 0x01, AY8910_control_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort hotshock_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x20, 0x20, AY8910_read_port_0_r ),
		new IO_ReadPort( 0x40, 0x40, AY8910_read_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort hotshock_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x10, AY8910_control_port_0_w ),
		new IO_WritePort( 0x20, 0x20, AY8910_write_port_0_w ),
		new IO_WritePort( 0x40, 0x40, AY8910_write_port_1_w ),
		new IO_WritePort( 0x80, 0x80, AY8910_control_port_1_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort hunchbks_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
	    new IO_ReadPort( S2650_SENSE_PORT, S2650_SENSE_PORT, input_port_3_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_scramble = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "A 1/1  B 2/1  C 1/1" );
		PORT_DIPSETTING(    0x02, "A 1/2  B 1/1  C 1/2" );
		PORT_DIPSETTING(    0x04, "A 1/3  B 3/1  C 1/3" );
		PORT_DIPSETTING(    0x06, "A 1/4  B 4/1  C 1/4" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_SPECIAL );/* protection bit */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SPECIAL );/* protection bit */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_atlantis = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x0e, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, "A 1/3  B 2/1" );
		PORT_DIPSETTING(    0x00, "A 1/6  B 1/1" );
		PORT_DIPSETTING(    0x04, "A 1/99 B 1/99");
		/* all the other combos give 99 credits */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_theend = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "256", IP_KEY_NONE, IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );	/* output bits */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_froggers = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );/* 1P shoot2 - unused */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );/* read - function unknown */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x02, "7" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "256", IP_KEY_NONE, IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* 2P shoot2 - unused */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );/* 2P shoot1 - unused */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, "A 2/1 B 2/1 C 2/1" );
		PORT_DIPSETTING(    0x04, "A 2/1 B 1/3 C 2/1" );
		PORT_DIPSETTING(    0x00, "A 1/1 B 1/1 C 1/1" );
		PORT_DIPSETTING(    0x06, "A 1/1 B 1/6 C 1/1" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_amidars = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );/* 1P shoot2 - unused */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "256", IP_KEY_NONE, IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "A 1/1 B 1/6" );
		PORT_DIPSETTING(    0x02, "A 2/1 B 1/3" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_triplep = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "256", IP_KEY_NONE, IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, "A 1/2 B 1/1 C 1/2" );
		PORT_DIPSETTING(    0x04, "A 1/3 B 3/1 C 1/3" );
		PORT_DIPSETTING(    0x00, "A 1/1 B 2/1 C 1/1" );
		PORT_DIPSETTING(    0x06, "A 1/4 B 4/1 C 1/4" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_SERVICE( 0x20, IP_ACTIVE_HIGH );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BITX(    0x80, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", KEYCODE_F1, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_ckongs = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START();       /* IN1 */
		/* the coinage dip switch is spread across bits 0/1 of port 1 and bit 3 of port 2. */
		/* To handle that, we swap bits 0/1 of port 1 and bits 1/2 of port 2 - this is handled */
		/* by ckongs_input_port_N_r() */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START();       /* IN2 */
		/* the coinage dip switch is spread across bits 0/1 of port 1 and bit 3 of port 2. */
		/* To handle that, we swap bits 0/1 of port 1 and bits 1/2 of port 2 - this is handled */
		/* by ckongs_input_port_N_r() */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x0e, 0x0e, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mars = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP     | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_5C") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY | IPF_PLAYER2 ); /* this also control cocktail mode */
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP     | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY );
	
		PORT_START(); 	/* IN3 */
		PORT_BIT( 0x1f, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY | IPF_PLAYER2 );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_devilfsh = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x01, "15000" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Cocktail") );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP   | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_newsin7 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x03, " A 1C/1C  B 2C/1C" );
		PORT_DIPSETTING(    0x01, " A 1C/3C  B 3C/1C" );
		PORT_DIPSETTING(    0x02, " A 1C/2C  B 1C/1C" );
		PORT_DIPSETTING(    0x00, " A 1C/4C  B 4C/1C" );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );  /* difficulty? */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x08, "5" );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mrkougar = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x03, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_5C") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );	/* used */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_hotshock = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_SERVICE1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* pressing this disables the coins */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_DIPNAME( 0x0f, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "2C_4C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "2C_6C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "2C_7C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "2C_8C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_8C") );
		PORT_DIPNAME( 0xf0, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "2C_4C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "2C_6C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "2C_7C") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "2C_8C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "1C_8C") );
	
		PORT_START(); 	/* IN3 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPNAME( 0x04, 0x04, "Language" );
		PORT_DIPSETTING(    0x04, "English" );
		PORT_DIPSETTING(    0x00, "Italian" );
		PORT_DIPNAME( 0x18, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "75000" );
		PORT_DIPSETTING(    0x08, "150000" );
		PORT_DIPSETTING(    0x10, "200000" );
		PORT_DIPSETTING(    0x18, "None" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_hunchbks = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, "A 2/1 B 1/3" );
		PORT_DIPSETTING(    0x00, "A 1/1 B 1/5" );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x02, "20000" );
		PORT_DIPSETTING(    0x04, "40000" );
		PORT_DIPSETTING(    0x06, "80000" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );/* protection check? */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* protection check? */
	
	    PORT_START();  /* Sense */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_VBLANK );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_cavelon = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* force UR controls in CK mode? */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "A 1/1 B 1/6" );
		PORT_DIPSETTING(    0x02, "A 2/1 B 1/3" );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown"));
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );/* protection check? */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* protection check? */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_sfx = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );// "Fire" left
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );// "Fire" right
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );// "Fire" left
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );// "Fire" right
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x06, 0x06, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );/* unused */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* unused */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mimonkey = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x06, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Cocktail") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BITX(    0x20, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Infinite Lives", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );   /* used, something to do with the bullets */
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout devilfsh_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 2*256*8*8 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout devilfsh_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 2*64*16*16 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	static GfxLayout newsin7_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, 2*256*8*8, 2*2*256*8*8 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout newsin7_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		3,	/* 3 bits per pixel */
		new int[] { 0, 2*64*16*16, 2*2*64*16*16 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	
	static GfxLayout mrkougar_charlayout = new GfxLayout
	(
		8,8,
		256,
		2,
		new int[] { 0, 4 },
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8
	);
	static GfxLayout mrkougar_spritelayout = new GfxLayout
	(
		16,16,
		64,
		2,
		new int[] { 0, 4 },
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3,
		  24*8+0, 24*8+1, 24*8+2, 24*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
		  32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8 },
		64*8
	);
	
	static GfxLayout sfx_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		RGN_FRAC(1,4),
		2,	/* 2 bits per pixel */
		new int[] { RGN_FRAC(0,2), RGN_FRAC(1,2) },	/* the two bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout sfx_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		RGN_FRAC(1,4),
		2,	/* 2 bits per pixel */
		new int[] { RGN_FRAC(0,2), RGN_FRAC(1,2) },	/* the two bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	
	static GfxDecodeInfo devilfsh_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, devilfsh_charlayout,   0, 8 ),
		new GfxDecodeInfo( REGION_GFX1, 0x0800, devilfsh_spritelayout, 0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo newsin7_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, newsin7_charlayout,   0, 4 ),
		new GfxDecodeInfo( REGION_GFX1, 0x0800, newsin7_spritelayout, 0, 4 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo mrkougar_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, mrkougar_charlayout,   0, 8 ),
		new GfxDecodeInfo( REGION_GFX1, 0x0000, mrkougar_spritelayout, 0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo sfx_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0800, sfx_charlayout,    0, 8 ),
		new GfxDecodeInfo( REGION_GFX1, 0x0000, sfx_spritelayout,  0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static AY8910interface triplep_ay8910_interface = new AY8910interface
	(
		1,	/* 1 chip */
		14318000/8,	/* 1.78975 MHz */
		new int[] { 50 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static AY8910interface sfx_ay8910_interface = new AY8910interface
	(
		2,	/* 2 chips */
		14318000/8,	/* 1.78975 MHz */
		/* Ant Eater clips if the volume is set higher than this */
		new int[] { MIXERG(16,MIXER_GAIN_2x,MIXER_PAN_CENTER), MIXERG(16,MIXER_GAIN_2x,MIXER_PAN_CENTER) },
		new ReadHandlerPtr[] { 0, soundlatch_r },
		new ReadHandlerPtr[] { 0, scramble_portB_r },
		new WriteHandlerPtr[] { soundlatch2_w, 0 },
		new WriteHandlerPtr[] { sfx_sh_irqtrigger_w, 0 }
	);
	
	static DACinterface sfx_dac_interface = new DACinterface
	(
		1,	/* 1 channel */
		new int[] { 100 },
	);
	
	
	static MACHINE_DRIVER_START( scramble )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(scramble_readmem,scramble_writemem)
	
		MDRV_CPU_ADD_TAG("audio", Z80, 14318000/8)	/* 1.78975 MHz */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(scobra_sound_readmem,scobra_sound_writemem)
		MDRV_CPU_PORTS(scobra_sound_readport,scobra_sound_writeport)
	
		MDRV_MACHINE_INIT(scramble)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+1)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
	
		MDRV_PALETTE_INIT(scramble)
		MDRV_VIDEO_START(scramble)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("8910", AY8910, scobra_ay8910_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( theend )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
	
		MDRV_PALETTE_INIT(galaxian)
		MDRV_VIDEO_START(theend)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( froggers )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("audio")
		MDRV_CPU_MEMORY(frogger_sound_readmem,frogger_sound_writemem)
		MDRV_CPU_PORTS(frogger_sound_readport,frogger_sound_writeport)
	
		/* video hardware */
		MDRV_PALETTE_INIT(frogger)
		MDRV_VIDEO_START(froggers)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("8910", AY8910, frogger_ay8910_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( mars )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mars_readmem,mars_writemem)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
		MDRV_PALETTE_INIT(galaxian)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( devilfsh )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mars_readmem,mars_writemem)
	
		/* video hardware */
		MDRV_GFXDECODE(devilfsh_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
		MDRV_PALETTE_INIT(galaxian)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( newsin7 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(newsin7_readmem,newsin7_writemem)
	
		/* video hardware */
		MDRV_GFXDECODE(newsin7_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
		MDRV_PALETTE_INIT(galaxian)
		MDRV_VIDEO_START(newsin7)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( mrkougar )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mars_readmem,mrkougar_writemem)
	
		/* video hardware */
		MDRV_GFXDECODE(mrkougar_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
		MDRV_PALETTE_INIT(galaxian)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( mrkougb )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mars_readmem,mrkougar_writemem)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
		MDRV_PALETTE_INIT(galaxian)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( ckongs )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(ckongs_readmem,ckongs_writemem)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
		MDRV_PALETTE_INIT(galaxian)
		MDRV_VIDEO_START(ckongs)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( hotshock )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(hotshock_readmem,hotshock_writemem)
	
		MDRV_CPU_MODIFY("audio")
		MDRV_CPU_PORTS(hotshock_sound_readport,hotshock_sound_writeport)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
		MDRV_PALETTE_INIT(galaxian)
		MDRV_VIDEO_START(pisces)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( cavelon )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets, 0/1 for background */
		MDRV_PALETTE_INIT(galaxian)
		MDRV_VIDEO_START(ckongs)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( sfx )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(sfx_readmem,sfx_writemem)
	
		MDRV_CPU_ADD(Z80, 14318000/8)	/* 1.78975 MHz */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sfx_sample_readmem,sfx_sample_writemem)
		MDRV_CPU_PORTS(sfx_sample_readport,sfx_sample_writeport)
	
		MDRV_MACHINE_INIT(sfx)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(2*8, 30*8-1, 2*8, 30*8-1)
		MDRV_PALETTE_LENGTH(32+64+2+8)	/* 32 for characters, 64 for stars, 2 for bullets, 8 for background */
		MDRV_GFXDECODE(sfx_gfxdecodeinfo)
		MDRV_PALETTE_INIT(turtles)
		MDRV_VIDEO_START(sfx)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("8910", AY8910, sfx_ay8910_interface)
		MDRV_SOUND_ADD(DAC, sfx_dac_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( mimonkey )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mimonkey_readmem,mimonkey_writemem)
	
		/* video hardware */
		MDRV_VIDEO_START(mimonkey)
	MACHINE_DRIVER_END
	
	/* Triple Punch and Mariner are different - only one CPU, one 8910 */
	static MACHINE_DRIVER_START( triplep )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(triplep_readport,triplep_writeport)
	
		MDRV_CPU_REMOVE("audio")
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets */
	
		MDRV_PALETTE_INIT(galaxian)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("8910", AY8910, triplep_ay8910_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( mariner )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(triplep)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+16)	/* 32 for characters, 64 for stars, 2 for bullets, 16 for background */
	
		MDRV_PALETTE_INIT(mariner)
		MDRV_VIDEO_START(mariner)
	MACHINE_DRIVER_END
	
	/* Hunchback replaces the Z80 with a S2650 CPU */
	static MACHINE_DRIVER_START( hunchbks )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(scramble)
		MDRV_CPU_REPLACE("main", S2650, 18432000/6)
		MDRV_CPU_MEMORY(hunchbks_readmem,hunchbks_writemem)
		MDRV_CPU_PORTS(hunchbks_readport,0)
		MDRV_CPU_VBLANK_INT(hunchbks_vh_interrupt,1)
	
		MDRV_VBLANK_DURATION(2500)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+0)	/* 32 for characters, 64 for stars, 2 for bullets */
	
		MDRV_PALETTE_INIT(galaxian)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_scramble = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "2d.k",         0x0000, 0x0800, 0xea35ccaa );
		ROM_LOAD( "2e.k",         0x0800, 0x0800, 0xe7bba1b3 );
		ROM_LOAD( "2f.k",         0x1000, 0x0800, 0x12d7fc3e );
		ROM_LOAD( "2h.k",         0x1800, 0x0800, 0xb59360eb );
		ROM_LOAD( "2j.k",         0x2000, 0x0800, 0x4919a91c );
		ROM_LOAD( "2l.k",         0x2800, 0x0800, 0x26a4547b );
		ROM_LOAD( "2m.k",         0x3000, 0x0800, 0x0bb49470 );
		ROM_LOAD( "2p.k",         0x3800, 0x0800, 0x6a5740e5 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "5c",           0x0000, 0x0800, 0xbcd297f0 );
		ROM_LOAD( "5d",           0x0800, 0x0800, 0xde7912da );
		ROM_LOAD( "5e",           0x1000, 0x0800, 0xba2fa933 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "5f.k",         0x0000, 0x0800, 0x4708845b );
		ROM_LOAD( "5h.k",         0x0800, 0x0800, 0x11fd2887 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_scrambls = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "2d",           0x0000, 0x0800, 0xb89207a1 );
		ROM_LOAD( "2e",           0x0800, 0x0800, 0xe9b4b9eb );
		ROM_LOAD( "2f",           0x1000, 0x0800, 0xa1f14f4c );
		ROM_LOAD( "2h",           0x1800, 0x0800, 0x591bc0d9 );
		ROM_LOAD( "2j",           0x2000, 0x0800, 0x22f11b6b );
		ROM_LOAD( "2l",           0x2800, 0x0800, 0x705ffe49 );
		ROM_LOAD( "2m",           0x3000, 0x0800, 0xea26c35c );
		ROM_LOAD( "2p",           0x3800, 0x0800, 0x94d8f5e3 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "5c",           0x0000, 0x0800, 0xbcd297f0 );
		ROM_LOAD( "5d",           0x0800, 0x0800, 0xde7912da );
		ROM_LOAD( "5e",           0x1000, 0x0800, 0xba2fa933 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "5f",           0x0000, 0x0800, 0x5f30311a );
		ROM_LOAD( "5h",           0x0800, 0x0800, 0x516e029e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_atlantis = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "2c",           0x0000, 0x0800, 0x0e485b9a );
		ROM_LOAD( "2e",           0x0800, 0x0800, 0xc1640513 );
		ROM_LOAD( "2f",           0x1000, 0x0800, 0xeec265ee );
		ROM_LOAD( "2h",           0x1800, 0x0800, 0xa5d2e442 );
		ROM_LOAD( "2j",           0x2000, 0x0800, 0x45f7cf34 );
		ROM_LOAD( "2l",           0x2800, 0x0800, 0xf335b96b );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "5c",           0x0000, 0x0800, 0xbcd297f0 );
		ROM_LOAD( "5d",           0x0800, 0x0800, 0xde7912da );
		ROM_LOAD( "5e",           0x1000, 0x0800, 0xba2fa933 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "5f",           0x0000, 0x0800, 0x57f9c6b9 );
		ROM_LOAD( "5h",           0x0800, 0x0800, 0xe989f325 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_atlants2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "rom1",         0x0000, 0x0800, 0xad348089 );
		ROM_LOAD( "rom2",         0x0800, 0x0800, 0xcaa705d1 );
		ROM_LOAD( "rom3",         0x1000, 0x0800, 0xe420641d );
		ROM_LOAD( "rom4",         0x1800, 0x0800, 0x04792d90 );
		ROM_LOAD( "rom5",         0x2000, 0x0800, 0x6eaf510d );
		ROM_LOAD( "rom6",         0x2800, 0x0800, 0xb297bd4b );
		ROM_LOAD( "rom7",         0x3000, 0x0800, 0xa50bf8d5 );
		ROM_LOAD( "rom8",         0x3800, 0x0800, 0xd2c5c984 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "5c",           0x0000, 0x0800, 0xbcd297f0 );
		ROM_LOAD( "5d",           0x0800, 0x0800, 0xde7912da );
		ROM_LOAD( "5e",           0x1000, 0x0800, 0xba2fa933 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "rom9",         0x0000, 0x0800, 0x55cd5acd );
		ROM_LOAD( "rom10",        0x0800, 0x0800, 0x72e773b8 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_theend = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "ic13_1t.bin",  0x0000, 0x0800, 0x93e555ba );
		ROM_LOAD( "ic14_2t.bin",  0x0800, 0x0800, 0x2de7ad27 );
		ROM_LOAD( "ic15_3t.bin",  0x1000, 0x0800, 0x035f750b );
		ROM_LOAD( "ic16_4t.bin",  0x1800, 0x0800, 0x61286b5c );
		ROM_LOAD( "ic17_5t.bin",  0x2000, 0x0800, 0x434a8f68 );
		ROM_LOAD( "ic18_6t.bin",  0x2800, 0x0800, 0xdc4cc786 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "ic56_1.bin",   0x0000, 0x0800, 0x7a141f29 );
		ROM_LOAD( "ic55_2.bin",   0x0800, 0x0800, 0x218497c1 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ic30_2c.bin",  0x0000, 0x0800, 0x68ccf7bf );
		ROM_LOAD( "ic31_1c.bin",  0x0800, 0x0800, 0x4a48c999 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6331-1j.86",   0x0000, 0x0020, 0x24652bc4 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_theends = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "ic13",         0x0000, 0x0800, 0x90e5ab14 );
		ROM_LOAD( "ic14",         0x0800, 0x0800, 0x950f0a07 );
		ROM_LOAD( "ic15",         0x1000, 0x0800, 0x6786bcf5 );
		ROM_LOAD( "ic16",         0x1800, 0x0800, 0x380a0017 );
		ROM_LOAD( "ic17",         0x2000, 0x0800, 0xaf067b7f );
		ROM_LOAD( "ic18",         0x2800, 0x0800, 0xa0411b93 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "ic56",         0x0000, 0x0800, 0x3b2c2f70 );
		ROM_LOAD( "ic55",         0x0800, 0x0800, 0xe0429e50 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ic30",         0x0000, 0x0800, 0x527fd384 );
		ROM_LOAD( "ic31",         0x0800, 0x0800, 0xaf6d09b6 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6331-1j.86",   0x0000, 0x0020, 0x24652bc4 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_froggers = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "vid_d2.bin",   0x0000, 0x0800, 0xc103066e );
		ROM_LOAD( "vid_e2.bin",   0x0800, 0x0800, 0xf08bc094 );
		ROM_LOAD( "vid_f2.bin",   0x1000, 0x0800, 0x637a2ff8 );
		ROM_LOAD( "vid_h2.bin",   0x1800, 0x0800, 0x04c027a5 );
		ROM_LOAD( "vid_j2.bin",   0x2000, 0x0800, 0xfbdfbe74 );
		ROM_LOAD( "vid_l2.bin",   0x2800, 0x0800, 0x8a4389e1 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "frogger.608",  0x0000, 0x0800, 0xe8ab0256 );
		ROM_LOAD( "frogger.609",  0x0800, 0x0800, 0x7380a48f );
		ROM_LOAD( "frogger.610",  0x1000, 0x0800, 0x31d7eb27 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "epr-1036.1k",  0x0000, 0x0800, 0x658745f8 );
		ROM_LOAD( "frogger.607",  0x0800, 0x0800, 0x05f7d883 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "vid_e6.bin",   0x0000, 0x0020, 0x0b878b54 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_amidars = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "am2d",         0x0000, 0x0800, 0x24b79547 );
		ROM_LOAD( "am2e",         0x0800, 0x0800, 0x4c64161e );
		ROM_LOAD( "am2f",         0x1000, 0x0800, 0xb3987a72 );
		ROM_LOAD( "am2h",         0x1800, 0x0800, 0x29873461 );
		ROM_LOAD( "am2j",         0x2000, 0x0800, 0x0fdd54d8 );
		ROM_LOAD( "am2l",         0x2800, 0x0800, 0x5382f7ed );
		ROM_LOAD( "am2m",         0x3000, 0x0800, 0x1d7109e9 );
		ROM_LOAD( "am2p",         0x3800, 0x0800, 0xc9163ac6 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "amidarus.5c",  0x0000, 0x1000, 0x8ca7b750 );
		ROM_LOAD( "amidarus.5d",  0x1000, 0x1000, 0x9b5bdc0a );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "2716.a6",      0x0000, 0x0800, 0x2082ad0a );  /* Same graphics ROMs as Amigo */
		ROM_LOAD( "2716.a5",      0x0800, 0x0800, 0x3029f94f );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "amidar.clr",   0x0000, 0x0020, 0xf940dcc3 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_triplep = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "triplep.2g",   0x0000, 0x1000, 0xc583a93d );
		ROM_LOAD( "triplep.2h",   0x1000, 0x1000, 0xc03ddc49 );
		ROM_LOAD( "triplep.2k",   0x2000, 0x1000, 0xe83ca6b5 );
		ROM_LOAD( "triplep.2l",   0x3000, 0x1000, 0x982cc3b9 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "triplep.5f",   0x0000, 0x0800, 0xd51cbd6f );
		ROM_LOAD( "triplep.5h",   0x0800, 0x0800, 0xf21c0059 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "tripprom.6e",  0x0000, 0x0020, 0x624f75df );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_knockout = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "knockout.2h",  0x0000, 0x1000, 0xeaaa848e );
		ROM_LOAD( "knockout.2k",  0x1000, 0x1000, 0xbc26d2c0 );
		ROM_LOAD( "knockout.2l",  0x2000, 0x1000, 0x02025c10 );
		ROM_LOAD( "knockout.2m",  0x3000, 0x1000, 0xe9abc42b );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "triplep.5f",   0x0000, 0x0800, 0xd51cbd6f );
		ROM_LOAD( "triplep.5h",   0x0800, 0x0800, 0xf21c0059 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "tripprom.6e",  0x0000, 0x0020, 0x624f75df );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mariner = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for main CPU */
		ROM_LOAD( "tp1.2h",       0x0000, 0x1000, 0xdac1dfd0 );
		ROM_LOAD( "tm2.2k",       0x1000, 0x1000, 0xefe7ca28 );
		ROM_LOAD( "tm3.2l",       0x2000, 0x1000, 0x027881a6 );
		ROM_LOAD( "tm4.2m",       0x3000, 0x1000, 0xa0fde7dc );
		ROM_LOAD( "tm5.2p",       0x6000, 0x0800, 0xd7ebcb8e );
		ROM_CONTINUE(             0x5800, 0x0800             );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "tm8.5f",       0x0000, 0x1000, 0x70ae611f );
		ROM_LOAD( "tm9.5h",       0x1000, 0x1000, 0x8e4e999e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "t4.6e",        0x0000, 0x0020, 0xca42b6dd );
	
		ROM_REGION( 0x0100, REGION_USER1, 0 );
		ROM_LOAD( "t6.6p",        0x0000, 0x0100, 0xad208ccc );/* background color prom */
	
		ROM_REGION( 0x0020, REGION_USER2, 0 );
		ROM_LOAD( "t5.7p",        0x0000, 0x0020, 0x1bd88cff );/* char banking and star placement */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_800fath = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for main CPU */
		ROM_LOAD( "tu1.2h",       0x0000, 0x1000, 0x5dd3d42f );
		ROM_LOAD( "tm2.2k",       0x1000, 0x1000, 0xefe7ca28 );
		ROM_LOAD( "tm3.2l",       0x2000, 0x1000, 0x027881a6 );
		ROM_LOAD( "tm4.2m",       0x3000, 0x1000, 0xa0fde7dc );
		ROM_LOAD( "tu5.2p",       0x6000, 0x0800, 0xf864a8a6 );
		ROM_CONTINUE(             0x5800, 0x0800             );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "tm8.5f",       0x0000, 0x1000, 0x70ae611f );
		ROM_LOAD( "tm9.5h",       0x1000, 0x1000, 0x8e4e999e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "t4.6e",        0x0000, 0x0020, 0xca42b6dd );
	
		ROM_REGION( 0x0100, REGION_USER1, 0 );
		ROM_LOAD( "t6.6p",        0x0000, 0x0100, 0xad208ccc );/* background color prom */
	
		ROM_REGION( 0x0020, REGION_USER2, 0 );
		ROM_LOAD( "t5.7p",        0x0000, 0x0020, 0x1bd88cff );/* char banking and star placement */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_ckongs = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "vid_2c.bin",   0x0000, 0x1000, 0x49a8c234 );
		ROM_LOAD( "vid_2e.bin",   0x1000, 0x1000, 0xf1b667f1 );
		ROM_LOAD( "vid_2f.bin",   0x2000, 0x1000, 0xb194b75d );
		ROM_LOAD( "vid_2h.bin",   0x3000, 0x1000, 0x2052ba8a );
		ROM_LOAD( "vid_2j.bin",   0x4000, 0x1000, 0xb377afd0 );
		ROM_LOAD( "vid_2l.bin",   0x5000, 0x1000, 0xfe65e691 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "turt_snd.5c",  0x0000, 0x1000, 0xf0c30f9a );
		ROM_LOAD( "snd_5d.bin",   0x1000, 0x1000, 0x892c9547 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "vid_5f.bin",   0x0000, 0x1000, 0x7866d2cb );
		ROM_LOAD( "vid_5h.bin",   0x1000, 0x1000, 0x7311a101 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "vid_6e.bin",   0x0000, 0x0020, 0x5039af97 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mars = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "u26.3",        0x0000, 0x0800, 0x2f88892c );
		ROM_LOAD( "u56.4",        0x0800, 0x0800, 0x9e6bcbf7 );
		ROM_LOAD( "u69.5",        0x1000, 0x0800, 0xdf496e6e );
		ROM_LOAD( "u98.6",        0x1800, 0x0800, 0x75f274bb );
		ROM_LOAD( "u114.7",       0x2000, 0x0800, 0x497fd8d0 );
		ROM_LOAD( "u133.8",       0x2800, 0x0800, 0x3d4cd59f );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "u39.9",        0x0000, 0x0800, 0xbb5968b9 );
		ROM_LOAD( "u51.10",       0x0800, 0x0800, 0x75fd7720 );
		ROM_LOAD( "u78.11",       0x1000, 0x0800, 0x72a492da );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "u72.1",        0x0000, 0x0800, 0x279789d0 );
		ROM_LOAD( "u101.2",       0x0800, 0x0800, 0xc5dc627f );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_devilfsh = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "u26.1",        0x0000, 0x0800, 0xec047d71 );
		ROM_LOAD( "u56.2",        0x0800, 0x0800, 0x0138ade9 );
		ROM_LOAD( "u69.3",        0x1000, 0x0800, 0x5dd0b3fc );
		ROM_LOAD( "u98.4",        0x1800, 0x0800, 0xded0b745 );
		ROM_LOAD( "u114.5",       0x2000, 0x0800, 0x5fd40176 );
		ROM_LOAD( "u133.6",       0x2800, 0x0800, 0x03538336 );
		ROM_LOAD( "u143.7",       0x3000, 0x0800, 0x64676081 );
		ROM_LOAD( "u163.8",       0x3800, 0x0800, 0xbc3d6770 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "u39.9",        0x0000, 0x0800, 0x09987e2e );
		ROM_LOAD( "u51.10",       0x0800, 0x0800, 0x1e2b1471 );
		ROM_LOAD( "u78.11",       0x1000, 0x0800, 0x45279aaa );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "u72.12",       0x0000, 0x1000, 0x5406508e );
		ROM_LOAD( "u101.13",      0x1000, 0x1000, 0x8c4018b6 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_newsin7 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "newsin.1",     0x0000, 0x1000, 0xe6c23fe0 );
		ROM_LOAD( "newsin.2",     0x1000, 0x1000, 0x3d477b5f );
		ROM_LOAD( "newsin.3",     0x2000, 0x1000, 0x7dfa9af0 );
		ROM_LOAD( "newsin.4",     0x3000, 0x1000, 0xd1b0ba19 );
		ROM_LOAD( "newsin.5",     0xa000, 0x1000, 0x06275d59 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "newsin.13",    0x0000, 0x0800, 0xd88489a2 );
		ROM_LOAD( "newsin.12",    0x0800, 0x0800, 0xb154a7af );
		ROM_LOAD( "newsin.11",    0x1000, 0x0800, 0x7ade709b );
	
		ROM_REGION( 0x3000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "newsin.7",     0x2000, 0x1000, 0x6bc5d64f );
		ROM_LOAD( "newsin.8",     0x1000, 0x1000, 0x0c5b895a );
		ROM_LOAD( "newsin.9",     0x0000, 0x1000, 0x6b87adff );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "newsin.6",     0x0000, 0x0020, 0x5cf2cd8d );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mrkougar = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "2732-7.bin",   0x0000, 0x1000, 0xfd060ffb );
		ROM_LOAD( "2732-6.bin",   0x1000, 0x1000, 0x9e05d868 );
		ROM_LOAD( "2732-5.bin",   0x2000, 0x1000, 0xcbc7c536 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "atw-6w-2.bin", 0x0000, 0x1000, 0xaf42a371 );
		ROM_LOAD( "atw-6y-3.bin", 0x1000, 0x1000, 0x862b8902 );
		ROM_LOAD( "atw-6z-4.bin", 0x2000, 0x1000, 0xa0396cc8 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "2732-1.bin",   0x0000, 0x1000, 0x60ef1d43 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mrkougr2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "atw-7l-7.bin", 0x0000, 0x1000, 0x7b34b198 );
		ROM_LOAD( "atw-7k-6.bin", 0x1000, 0x1000, 0xfbca23c7 );
		ROM_LOAD( "atw-7h-5.bin", 0x2000, 0x1000, 0x05b257a2 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "atw-6w-2.bin", 0x0000, 0x1000, 0xaf42a371 );
		ROM_LOAD( "atw-6y-3.bin", 0x1000, 0x1000, 0x862b8902 );
		ROM_LOAD( "atw-6z-4.bin", 0x2000, 0x1000, 0xa0396cc8 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "atw-1h-1.bin", 0x0000, 0x1000, 0x38fdfb63 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "atw-prom.bin", 0x0000, 0x0020, 0xc65db188 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mrkougb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "p01.bin",	  0x0000, 0x0800, 0xdea0cde1 );
		ROM_LOAD( "p02.bin",	  0x0800, 0x0800, 0xc8017751 );
		ROM_LOAD( "p03.bin",	  0x1000, 0x0800, 0xb8921984 );
		ROM_LOAD( "p04.bin",	  0x1800, 0x0800, 0xb3c9754c );
		ROM_LOAD( "p05.bin",	  0x2000, 0x0800, 0x8d94adbc );
		ROM_LOAD( "p06.bin",	  0x2800, 0x0800, 0xacc921ff );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "atw-6w-2.bin", 0x0000, 0x1000, 0xaf42a371 );
		ROM_LOAD( "atw-6y-3.bin", 0x1000, 0x1000, 0x862b8902 );
		ROM_LOAD( "atw-6z-4.bin", 0x2000, 0x1000, 0xa0396cc8 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "g07.bin",      0x0000, 0x0800, 0x0ecfd116 );
		ROM_LOAD( "g08.bin",      0x0800, 0x0800, 0x00bfa3c6 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "atw-prom.bin", 0x0000, 0x0020, 0xc65db188 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hotshock = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "hotshock.l10", 0x0000, 0x1000, 0x401078f7 );
		ROM_LOAD( "hotshock.l9",  0x1000, 0x1000, 0xaf76c237 );
		ROM_LOAD( "hotshock.l8",  0x2000, 0x1000, 0x30486031 );
		ROM_LOAD( "hotshock.l7",  0x3000, 0x1000, 0x5bde9312 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "hotshock.b3",  0x0000, 0x1000, 0x0092f0e2 );
		ROM_LOAD( "hotshock.b4",  0x1000, 0x1000, 0xc2135a44 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "hotshock.h4",  0x0000, 0x1000, 0x60bdaea9 );
		ROM_LOAD( "hotshock.h5",  0x1000, 0x1000, 0x4ef17453 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hunchbks = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "2c_hb01.bin",  0x0000, 0x0800, 0x8bebd834 );
		ROM_LOAD( "2e_hb02.bin",  0x0800, 0x0800, 0x07de4229 );
		ROM_LOAD( "2f_hb03.bin",  0x2000, 0x0800, 0xb75a0dfc );
		ROM_LOAD( "2h_hb04.bin",  0x2800, 0x0800, 0xf3206264 );
		ROM_LOAD( "2j_hb05.bin",  0x4000, 0x0800, 0x1bb78728 );
		ROM_LOAD( "2l_hb06.bin",  0x4800, 0x0800, 0xf25ed680 );
		ROM_LOAD( "2m_hb07.bin",  0x6000, 0x0800, 0xc72e0e17 );
		ROM_LOAD( "2p_hb08.bin",  0x6800, 0x0800, 0x412087b0 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "11d_snd.bin",  0x0000, 0x0800, 0x88226086 );
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "5f_hb09.bin",  0x0000, 0x0800, 0xdb489c3d );
		ROM_LOAD( "5h_hb10.bin",  0x0800, 0x0800, 0x3977650e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6e_prom.bin",  0x0000, 0x0020, 0x01004d3f );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_cavelon = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x14000, REGION_CPU1, 0 );/* 64k + 16K banked for code */
		ROM_LOAD( "2.bin",		 0x00000, 0x2000, 0xa3b353ac );
		ROM_LOAD( "1.bin",		 0x02000, 0x2000, 0x3f62efd6 );
		ROM_RELOAD(				 0x12000, 0x2000);
		ROM_LOAD( "3.bin",		 0x10000, 0x2000, 0x39d74e4e );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "1c_snd.bin",	  0x0000, 0x0800, 0xf58dcf55 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "h.bin",		  0x0000, 0x1000, 0xd44fcd6f );
		ROM_LOAD( "k.bin",		  0x1000, 0x1000, 0x59bc7f9e );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "cavelon.clr",  0x0000, 0x0020, 0xd133356b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_sfx = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "sfx_b-0.1j",   0x0000, 0x1000, 0xe5bc6952 );
		ROM_CONTINUE(             0xe000, 0x1000             );
		ROM_LOAD( "1.1c",         0x1000, 0x1000, 0x1b3c48e7 );
		ROM_LOAD( "22.1d",        0x2000, 0x1000, 0xed44950d );
		ROM_LOAD( "23.1e",        0x3000, 0x1000, 0xf44a3ca0 );
		ROM_LOAD( "27.1a",        0x7000, 0x1000, 0xed86839f );
		ROM_LOAD( "24.1g",        0xc000, 0x1000, 0xe6d7dc74 );
		ROM_LOAD( "5.1h",         0xd000, 0x1000, 0xd1e8d390 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "5.5j",         0x0000, 0x1000, 0x59028fb6 );
		ROM_LOAD( "6.6j",         0x1000, 0x1000, 0x5427670f );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* 64k for the sample CPU */
		ROM_LOAD( "1.1j",         0x0000, 0x1000, 0x2f172c58 );
		ROM_LOAD( "2.2j",         0x1000, 0x1000, 0xa6ad2f6b );
		ROM_LOAD( "3.3j",         0x2000, 0x1000, 0xfa1274fa );
		ROM_LOAD( "4.4j",         0x3000, 0x1000, 0x1cd33f3a );
		ROM_LOAD( "10.3h",        0x4000, 0x1000, 0xb833a15b );
		ROM_LOAD( "11.4h",        0x5000, 0x1000, 0xcbd76ec2 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "28.5a",        0x0000, 0x1000, 0xd73a8252 );
		ROM_LOAD( "29.5c",        0x1000, 0x1000, 0x1401ccf2 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "6331.9g",      0x0000, 0x0020, 0xca1d9ccd );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mimonkey = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "mm1",          0x0000, 0x1000, 0x0399a0c4 );
		ROM_LOAD( "mm2",          0x1000, 0x1000, 0x2c5e971e );
		ROM_LOAD( "mm3",          0x2000, 0x1000, 0x24ce1ce3 );
		ROM_LOAD( "mm4",          0x3000, 0x1000, 0xc83fb639 );
		ROM_LOAD( "mm5",          0xc000, 0x1000, 0xa9f12dfc );
		ROM_LOAD( "mm6",          0xd000, 0x1000, 0xe492a40c );
		ROM_LOAD( "mm7",          0xe000, 0x1000, 0x5339928d );
		ROM_LOAD( "mm8",          0xf000, 0x1000, 0xeee7a12e );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "mmsound1",	  0x0000, 0x1000, 0x2d14c527 );
		ROM_LOAD( "mmsnd2a",	  0x1000, 0x1000, 0x35ed0f96 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mmgfx1",		  0x0000, 0x2000, 0x4af47337 );
		ROM_LOAD( "mmgfx2",		  0x2000, 0x2000, 0xdef47da8 );
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, 0x4e3caeab );
	ROM_END(); }}; 
	
	
	public static GameDriver driver_scramble	   = new GameDriver("1981"	,"scramble"	,"scramble.java"	,rom_scramble,null	,machine_driver_scramble	,input_ports_scramble	,init_scramble	,ROT90	,	"Konami", "Scramble" )
	public static GameDriver driver_scrambls	   = new GameDriver("1981"	,"scrambls"	,"scramble.java"	,rom_scrambls,driver_scramble	,machine_driver_scramble	,input_ports_scramble	,init_scrambls	,ROT90	,	"[Konami] (Stern license)", "Scramble (Stern)" )
	public static GameDriver driver_atlantis	   = new GameDriver("1981"	,"atlantis"	,"scramble.java"	,rom_atlantis,null	,machine_driver_scramble	,input_ports_atlantis	,init_atlantis	,ROT90	,	"Comsoft", "Battle of Atlantis (set 1)" )
	public static GameDriver driver_atlants2	   = new GameDriver("1981"	,"atlants2"	,"scramble.java"	,rom_atlants2,driver_atlantis	,machine_driver_scramble	,input_ports_atlantis	,init_atlantis	,ROT90	,	"Comsoft", "Battle of Atlantis (set 2)" )
	public static GameDriver driver_theend	   = new GameDriver("1980"	,"theend"	,"scramble.java"	,rom_theend,null	,machine_driver_theend	,input_ports_theend	,init_theend	,ROT90	,	"Konami", "The End" )
	public static GameDriver driver_theends	   = new GameDriver("1980"	,"theends"	,"scramble.java"	,rom_theends,driver_theend	,machine_driver_theend	,input_ports_theend	,init_theend	,ROT90	,	"[Konami] (Stern license)", "The End (Stern)" )
	public static GameDriver driver_froggers	   = new GameDriver("1981"	,"froggers"	,"scramble.java"	,rom_froggers,driver_frogger	,machine_driver_froggers	,input_ports_froggers	,init_froggers	,ROT90	,	"bootleg", "Frog" )
	public static GameDriver driver_amidars	   = new GameDriver("1982"	,"amidars"	,"scramble.java"	,rom_amidars,driver_amidar	,machine_driver_scramble	,input_ports_amidars	,init_atlantis	,ROT90	,	"Konami", "Amidar (Scramble hardware)" )
	public static GameDriver driver_triplep	   = new GameDriver("1982"	,"triplep"	,"scramble.java"	,rom_triplep,null	,machine_driver_triplep	,input_ports_triplep	,init_scramble_ppi	,ROT90	,	"KKI", "Triple Punch" )
	public static GameDriver driver_knockout	   = new GameDriver("1982"	,"knockout"	,"scramble.java"	,rom_knockout,driver_triplep	,machine_driver_triplep	,input_ports_triplep	,init_scramble_ppi	,ROT90	,	"KKK", "Knock Out!!" )
	public static GameDriver driver_mariner	   = new GameDriver("1981"	,"mariner"	,"scramble.java"	,rom_mariner,null	,machine_driver_mariner	,input_ports_scramble	,init_mariner	,ROT90	,	"Amenip", "Mariner", GAME_IMPERFECT_SOUND )
	public static GameDriver driver_800fath	   = new GameDriver("1981"	,"800fath"	,"scramble.java"	,rom_800fath,driver_mariner	,machine_driver_mariner	,input_ports_scramble	,init_mariner	,ROT90	,	"Amenip (US Billiards Inc. license)", "800 Fathoms" )
	public static GameDriver driver_ckongs	   = new GameDriver("1981"	,"ckongs"	,"scramble.java"	,rom_ckongs,driver_ckong	,machine_driver_ckongs	,input_ports_ckongs	,init_ckongs	,ROT90	,	"bootleg", "Crazy Kong (Scramble hardware)" )
	public static GameDriver driver_mars	   = new GameDriver("1981"	,"mars"	,"scramble.java"	,rom_mars,null	,machine_driver_mars	,input_ports_mars	,init_mars	,ROT90	,	"Artic", "Mars" )
	public static GameDriver driver_devilfsh	   = new GameDriver("1982"	,"devilfsh"	,"scramble.java"	,rom_devilfsh,null	,machine_driver_devilfsh	,input_ports_devilfsh	,init_devilfsh	,ROT90	,	"Artic", "Devil Fish" )
	public static GameDriver driver_newsin7	   = new GameDriver("1983"	,"newsin7"	,"scramble.java"	,rom_newsin7,null	,machine_driver_newsin7	,input_ports_newsin7	,init_mars	,ROT90	,	"ATW USA, Inc.", "New Sinbad 7", GAME_IMPERFECT_COLORS )
	public static GameDriver driver_mrkougar	   = new GameDriver("1984"	,"mrkougar"	,"scramble.java"	,rom_mrkougar,null	,machine_driver_mrkougar	,input_ports_mrkougar	,init_mrkougar	,ROT90	,	"ATW", "Mr. Kougar" )
	public static GameDriver driver_mrkougr2	   = new GameDriver("1983"	,"mrkougr2"	,"scramble.java"	,rom_mrkougr2,driver_mrkougar	,machine_driver_mrkougar	,input_ports_mrkougar	,init_mrkougar	,ROT90	,	"ATW", "Mr. Kougar (earlier)" )
	public static GameDriver driver_mrkougb	   = new GameDriver("1983"	,"mrkougb"	,"scramble.java"	,rom_mrkougb,driver_mrkougar	,machine_driver_mrkougb	,input_ports_mrkougar	,init_mrkougb	,ROT90	,	"bootleg", "Mr. Kougar (bootleg)" )
	public static GameDriver driver_hotshock	   = new GameDriver("1982"	,"hotshock"	,"scramble.java"	,rom_hotshock,null	,machine_driver_hotshock	,input_ports_hotshock	,init_hotshock	,ROT90	,	"E.G. Felaco", "Hot Shocker" )
	public static GameDriver driver_hunchbks	   = new GameDriver("1983"	,"hunchbks"	,"scramble.java"	,rom_hunchbks,driver_hunchbak	,machine_driver_hunchbks	,input_ports_hunchbks	,init_scramble_ppi	,ROT90	,	"Century", "Hunchback (Scramble hardware)" )
	public static GameDriver driver_cavelon	   = new GameDriver("1983"	,"cavelon"	,"scramble.java"	,rom_cavelon,null	,machine_driver_cavelon	,input_ports_cavelon	,init_cavelon	,ROT90	,	"Jetsoft", "Cavelon" )
	public static GameDriver driver_sfx	   = new GameDriver("1983"	,"sfx"	,"scramble.java"	,rom_sfx,null	,machine_driver_sfx	,input_ports_sfx	,init_sfx	,ORIENTATION_FLIP_X	,	"Nichibutsu", "SF-X" )
	public static GameDriver driver_mimonkey	   = new GameDriver("198?"	,"mimonkey"	,"scramble.java"	,rom_mimonkey,null	,machine_driver_mimonkey	,input_ports_mimonkey	,init_mimonkey	,ROT90	,	"bootleg", "Mighty Monkey" )
}
