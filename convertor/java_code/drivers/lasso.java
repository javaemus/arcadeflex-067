/***************************************************************************

 Lasso and similar hardware

		driver by Phil Stroffolino, Nicola Salmoria, Luca Elia

---------------------------------------------------------------------------
Year + Game					By				CPUs		Sound Chips
---------------------------------------------------------------------------
82	Lasso					SNK				3 x 6502	2 x SN76489
83	Chameleon				Jaleco			2 x 6502	2 x SN76489
84	Wai Wai Jockey Gate-In!	Jaleco/Casio	2 x 6502	2 x SN76489 + DAC
84  Pinbo                   Jaleco          6502 + Z80  2 x AY-8910
---------------------------------------------------------------------------

Notes:

- unknown CPU speeds (affect game timing)
- Lasso: fire button auto-repeats on high score entry screen (real behavior?)

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class lasso
{
	
	
	/* IRQ = VBlank, NMI = Coin Insertion */
	
	static INTERRUPT_GEN( lasso_interrupt )
	{
		static int old;
		int new;
	
		// VBlank
		if (cpu_getiloops() == 0)
		{
			cpu_set_irq_line(0, 0, HOLD_LINE);
			return;
		}
	
		// Coins
		new = ~readinputport(3) & 0x30;
	
		if ( ((new & 0x10) && !(old & 0x10)) ||
			 ((new & 0x20) && !(old & 0x20)) )
			cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
	
		old = new;
	}
	
	
	/* Shared RAM between Main CPU and sub CPU */
	
	static data8_t *lasso_sharedram;
	
	public static ReadHandlerPtr lasso_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return lasso_sharedram[offset];
	} };
	public static WriteHandlerPtr lasso_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		lasso_sharedram[offset] = data;
	} };
	
	
	/* Write to the sound latch and generate an IRQ on the sound CPU */
	
	public static WriteHandlerPtr sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch_w(offset,data);
		cpu_set_irq_line( 1, 0, PULSE_LINE );
	} };
	
	public static ReadHandlerPtr sound_status_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		/*	0x01: chip#0 ready; 0x02: chip#1 ready */
		return 0x03;
	} };
	
	static data8_t lasso_chip_data;
	
	public static WriteHandlerPtr sound_data_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		lasso_chip_data = BITSWAP8(data,0,1,2,3,4,5,6,7);
	} };
	
	public static WriteHandlerPtr sound_select_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (~data & 0x01)	/* chip #0 */
			SN76496_0_w(0,lasso_chip_data);
	
		if (~data & 0x02)	/* chip #1 */
			SN76496_1_w(0,lasso_chip_data);
	} };
	
	
	
	public static Memory_ReadAddress lasso_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0c7f, MRA_RAM ),
		new Memory_ReadAddress( 0x1000, 0x17ff, lasso_sharedram_r	),
		new Memory_ReadAddress( 0x1804, 0x1804, input_port_0_r ),
		new Memory_ReadAddress( 0x1805, 0x1805, input_port_1_r ),
		new Memory_ReadAddress( 0x1806, 0x1806, input_port_2_r ),
		new Memory_ReadAddress( 0x1807, 0x1807, input_port_3_r ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress lasso_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x07ff, lasso_videoram_w, lasso_videoram ),
		new Memory_WriteAddress( 0x0800, 0x0bff, lasso_colorram_w, lasso_colorram ),
		new Memory_WriteAddress( 0x0c00, 0x0c7f, MWA_RAM, lasso_spriteram, lasso_spriteram_size ),
		new Memory_WriteAddress( 0x1000, 0x17ff, lasso_sharedram_w	),
		new Memory_WriteAddress( 0x1800, 0x1800, sound_command_w ),
		new Memory_WriteAddress( 0x1801, 0x1801, lasso_backcolor_w	),
		new Memory_WriteAddress( 0x1802, 0x1802, lasso_video_control_w ),
		new Memory_WriteAddress( 0x1806, 0x1806, MWA_NOP ),	// games uses 'lsr' to read port
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress chameleo_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x10ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1804, 0x1804, input_port_0_r ),
		new Memory_ReadAddress( 0x1805, 0x1805, input_port_1_r ),
		new Memory_ReadAddress( 0x1806, 0x1806, input_port_2_r ),
		new Memory_ReadAddress( 0x1807, 0x1807, input_port_3_r ),
		new Memory_ReadAddress( 0x2000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress chameleo_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x07ff, lasso_videoram_w, lasso_videoram ),
		new Memory_WriteAddress( 0x0800, 0x0bff, lasso_colorram_w, lasso_colorram ),
		new Memory_WriteAddress( 0x0c00, 0x0fff, MWA_RAM ),	//
		new Memory_WriteAddress( 0x1000, 0x107f, MWA_RAM, lasso_spriteram, lasso_spriteram_size ),
		new Memory_WriteAddress( 0x1080, 0x10ff, MWA_RAM ),
		new Memory_WriteAddress( 0x1800, 0x1800, sound_command_w ),
		new Memory_WriteAddress( 0x1801, 0x1801, lasso_backcolor_w	),
		new Memory_WriteAddress( 0x1802, 0x1802, lasso_video_control_w ),
		new Memory_WriteAddress( 0x2000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress wwjgtin_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x10ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1804, 0x1804, input_port_0_r ),
		new Memory_ReadAddress( 0x1805, 0x1805, input_port_1_r ),
		new Memory_ReadAddress( 0x1806, 0x1806, input_port_2_r ),
		new Memory_ReadAddress( 0x1807, 0x1807, input_port_3_r ),
		new Memory_ReadAddress( 0x5000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xfffa, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress wwjgtin_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0800, 0x0bff, lasso_videoram_w, lasso_videoram ),
		new Memory_WriteAddress( 0x0c00, 0x0fff, lasso_colorram_w, lasso_colorram ),
		new Memory_WriteAddress( 0x1000, 0x10ff, MWA_RAM, lasso_spriteram, lasso_spriteram_size ),
		new Memory_WriteAddress( 0x1800, 0x1800, sound_command_w ),
		new Memory_WriteAddress( 0x1801, 0x1801, lasso_backcolor_w	),
		new Memory_WriteAddress( 0x1802, 0x1802, wwjgtin_video_control_w	),
		new Memory_WriteAddress( 0x1c00, 0x1c03, wwjgtin_lastcolor_w ),
		new Memory_WriteAddress( 0x1c04, 0x1c07, MWA_RAM, wwjgtin_track_scroll ),
		new Memory_WriteAddress( 0x5000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xfffa, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress pinbo_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0400, 0x07ff, lasso_videoram_w, lasso_videoram ),
		new Memory_WriteAddress( 0x0800, 0x0bff, lasso_colorram_w, lasso_colorram ),
		new Memory_WriteAddress( 0x1000, 0x10ff, MWA_RAM, lasso_spriteram, lasso_spriteram_size ),
		new Memory_WriteAddress( 0x1800, 0x1800, sound_command_w ),
		new Memory_WriteAddress( 0x1802, 0x1802, pinbo_video_control_w ),
		new Memory_WriteAddress( 0x2000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress lasso_coprocessor_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0x8fff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress lasso_coprocessor_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, MWA_RAM, lasso_sharedram),
		new Memory_WriteAddress( 0x2000, 0x3fff, MWA_RAM, lasso_bitmap_ram ),
		new Memory_WriteAddress( 0x8000, 0x8fff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress lasso_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x01ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xb004, 0xb004, sound_status_r ),
		new Memory_ReadAddress( 0xb005, 0xb005, soundlatch_r ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress lasso_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x01ff, MWA_RAM ),
		new Memory_WriteAddress( 0x5000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xb000, 0xb000, sound_data_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, sound_select_w ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress chameleo_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x01ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xb004, 0xb004, sound_status_r ),
		new Memory_ReadAddress( 0xb005, 0xb005, soundlatch_r ),
		new Memory_ReadAddress( 0xfffa, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress chameleo_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x01ff, MWA_RAM ),
		new Memory_WriteAddress( 0x1000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xb000, 0xb000, sound_data_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, sound_select_w ),
		new Memory_WriteAddress( 0xfffa, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress wwjgtin_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x01ff, MWA_RAM ),
		new Memory_WriteAddress( 0x5000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xb000, 0xb000, sound_data_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, sound_select_w ),
		new Memory_WriteAddress( 0xb003, 0xb003, DAC_0_data_w ),
		new Memory_WriteAddress( 0xfffa, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress pinbo_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress pinbo_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort pinbo_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x02, 0x02, AY8910_read_port_0_r ),
		new IO_ReadPort( 0x06, 0x06, AY8910_read_port_1_r ),
		new IO_ReadPort( 0x08, 0x08, soundlatch_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort pinbo_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, AY8910_control_port_0_w ),
		new IO_WritePort( 0x01, 0x01, AY8910_write_port_0_w ),
		new IO_WritePort( 0x04, 0x04, AY8910_control_port_1_w ),
		new IO_WritePort( 0x05, 0x05, AY8910_write_port_1_w ),
		new IO_WritePort( 0x08, 0x08, MWA_NOP ),	/* ??? */
		new IO_WritePort( 0x14, 0x14, MWA_NOP ),	/* ??? */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_lasso = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* 1804 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );/* lasso */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );/* shoot */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED  );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED  );
	
		PORT_START();  /* 1805 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_COCKTAIL | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_COCKTAIL | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2	| IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED  );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED  );
	
		PORT_START();  /* 1806 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0e, 0x0e, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(	0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x0e, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x0c, DEF_STR( "1C_6C") );
	//	PORT_DIPSETTING(	0x06, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x0a, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x30, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x20, "5" );
	//	PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(	0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, "Warm-Up Instructions" );
		PORT_DIPSETTING(	0x00, DEF_STR( "No") );
		PORT_DIPSETTING(	0x80, DEF_STR( "Yes") );
	
		PORT_START();  /* 1807 */
		PORT_DIPNAME( 0x01, 0x00, "Warm-Up" );
		PORT_DIPSETTING(    0x01, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x02, 0x00, "Warm-Up Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x02, "German" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );	/* used */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x08, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN2    );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1    );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2  );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1  );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_chameleo = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* 1804 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();  /* 1805 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_COCKTAIL | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_COCKTAIL | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();  /* 1806 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0e, 0x0e, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(	0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x0e, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x0c, DEF_STR( "1C_6C") );
	//	PORT_DIPSETTING(	0x06, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x0a, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x30, "5" );
	//	PORT_DIPSETTING(    0x10, "5" );
		PORT_BITX(0,        0x20, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", IP_JOY_NONE, IP_KEY_NONE );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(	0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();  /* 1807 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN2    );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1    );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2  );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1  );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_wwjgtin = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* 1804 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();  /* 1805 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER2 | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER2 | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER2 | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();  /* 1806 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );	/* used - has to do with the controls */
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x0e, 0x0e, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(	0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x0e, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x0c, DEF_STR( "1C_6C") );
	//	PORT_DIPSETTING(	0x06, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x0a, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(	0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();  /* 1807 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life"));
		PORT_DIPSETTING(    0x00, "20k" );
		PORT_DIPSETTING(    0x01, "50k" );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW,  IPT_COIN2   );
		PORT_BIT( 0x20, IP_ACTIVE_LOW,  IPT_COIN1   );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START1  );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START2  );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_pinbo = new InputPortPtr(){ public void handler() { 
		PORT_START();   /* 1804 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();   /* 1805 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();  /* 1806 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0e, 0x0e, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(	0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x0e, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x0c, DEF_STR( "1C_6C") );
	//	PORT_DIPSETTING(	0x06, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x0a, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_BITX( 0,       0x30, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "70", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();  /* 1807 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life"));
		PORT_DIPSETTING(    0x00, "500000,1000000" );
		PORT_DIPSETTING(    0x01, "none" );
		PORT_DIPNAME( 0x02, 0x02, "Controls" );
		PORT_DIPSETTING(    0x02, "Normal" );
		PORT_DIPSETTING(    0x00, "Reversed" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW,  IPT_COIN2   );
		PORT_BIT( 0x20, IP_ACTIVE_LOW,  IPT_COIN1   );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1 );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_pinbos = new InputPortPtr(){ public void handler() { 
		PORT_START();   /* 1804 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();   /* 1805 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();  /* 1806 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0e, 0x0e, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(	0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x0e, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x0c, DEF_STR( "1C_6C") );
	//	PORT_DIPSETTING(	0x06, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(	0x0a, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_BITX( 0,       0x30, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "70", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();  /* 1807 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Bonus_Life"));
		PORT_DIPSETTING(    0x00, "500000,1000000" );
		PORT_DIPSETTING(    0x01, "none" );
		PORT_DIPNAME( 0x02, 0x02, "Controls" );
		PORT_DIPSETTING(    0x02, "Normal" );
		PORT_DIPSETTING(    0x00, "Reversed" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );	/* probably unused */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW,  IPT_COIN2   );
		PORT_BIT( 0x20, IP_ACTIVE_LOW,  IPT_COIN1   );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1 );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout lasso_charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,4),
		2,
		new int[] { RGN_FRAC(0,4), RGN_FRAC(2,4) },
		new int[] { STEP8(0,1) },
		new int[] { STEP8(0,8) },
		8*8
	);
	
	static GfxLayout lasso_spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		2,
		new int[] { RGN_FRAC(1,4), RGN_FRAC(3,4) },
		new int[] { STEP8(0,1), STEP8(8*8*1,1) },
		new int[] { STEP8(0,8), STEP8(8*8*2,8) },
		16*16
	);
	
	static GfxLayout wwjgtin_tracklayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(1,4), RGN_FRAC(3,4), RGN_FRAC(0,4), RGN_FRAC(2,4) },
		new int[] { STEP8(0,1), STEP8(8*8*1,1) },
		new int[] { STEP8(0,8), STEP8(8*8*2,8) },
		16*16
	);
	
	/* Pinbo is 3bpp, otherwise the same */
	static GfxLayout pinbo_charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,6),
		3,
		new int[] { RGN_FRAC(0,6), RGN_FRAC(2,6), RGN_FRAC(4,6) },
		new int[] { STEP8(0,1) },
		new int[] { STEP8(0,8) },
		8*8
	);
	
	static GfxLayout pinbo_spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,6),
		3,
		new int[] { RGN_FRAC(1,6), RGN_FRAC(3,6), RGN_FRAC(5,6) },
		new int[] { STEP8(0,1), STEP8(8*8*1,1) },
		new int[] { STEP8(0,8), STEP8(8*8*2,8) },
		16*16
	);
	
	
	static GfxDecodeInfo lasso_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, lasso_charlayout,   0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, lasso_spritelayout, 0, 16 ),
		new GfxDecodeInfo( -1 )
	};
	
	static GfxDecodeInfo wwjgtin_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, lasso_charlayout,       0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, lasso_spritelayout,     0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, wwjgtin_tracklayout,	4*16, 16 ),
		new GfxDecodeInfo( -1 )
	};
	
	static GfxDecodeInfo pinbo_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, pinbo_charlayout,   0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, pinbo_spritelayout, 0, 16 ),
		new GfxDecodeInfo( -1 )
	};
	
	
	
	static SN76496interface sn76496_interface = new SN76496interface
	(
		2,	/* 2 chips */
		new int[] { 2000000, 2000000 },	/* ? MHz */
		new int[] { 100, 100 }
	);
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,		/* 2 chips */
		1250000,	/* 1.25 MHz? */
		new int[] { 25, 25 },
		new ReadHandlerPtr[] { 0, 0 },
		new ReadHandlerPtr[] { 0, 0 },
		new WriteHandlerPtr[] { 0, 0 },
		new WriteHandlerPtr[] { 0, 0 }
	);
	
	static MACHINE_DRIVER_START( lasso )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", M6502, 2000000)	/* 2 MHz (?) */
		MDRV_CPU_MEMORY(lasso_readmem,lasso_writemem)
		MDRV_CPU_VBLANK_INT(lasso_interrupt,2)		/* IRQ = VBlank, NMI = Coin Insertion */
	
		MDRV_CPU_ADD_TAG("audio", M6502, 600000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)		/* ?? (controls music tempo) */
		MDRV_CPU_MEMORY(lasso_sound_readmem,lasso_sound_writemem)
	
		MDRV_CPU_ADD_TAG("blitter", M6502, 2000000)	/* 2 MHz (?) */
		MDRV_CPU_MEMORY(lasso_coprocessor_readmem,lasso_coprocessor_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(100)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(lasso_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(0x40)
	
		MDRV_PALETTE_INIT(lasso)
		MDRV_VIDEO_START(lasso)
		MDRV_VIDEO_UPDATE(lasso)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("sn76496", SN76496, sn76496_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( chameleo )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(lasso)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(chameleo_readmem,chameleo_writemem)
	
		MDRV_CPU_MODIFY("audio")
		MDRV_CPU_MEMORY(chameleo_sound_readmem,chameleo_sound_writemem)
	
		MDRV_CPU_REMOVE("blitter")
	
		/* video hardware */
		MDRV_VIDEO_UPDATE(chameleo)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( wwjgtin )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(lasso)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(wwjgtin_readmem,wwjgtin_writemem)
	
		MDRV_CPU_MODIFY("audio")
		MDRV_CPU_MEMORY(lasso_sound_readmem,wwjgtin_sound_writemem)
	
		MDRV_CPU_REMOVE("blitter")
	
		/* video hardware */
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 2*8, 30*8-1)	// Smaller visible area?
		MDRV_GFXDECODE(wwjgtin_gfxdecodeinfo)	// Has 1 additional layer
		MDRV_PALETTE_LENGTH(0x40+1)
		MDRV_COLORTABLE_LENGTH(4*16 + 16*16)	// Reserve 1 color for black
	
		MDRV_PALETTE_INIT(wwjgtin)
		MDRV_VIDEO_START(wwjgtin)
		MDRV_VIDEO_UPDATE(wwjgtin)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( pinbo )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(lasso)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(chameleo_readmem,pinbo_writemem)
	
		MDRV_CPU_REPLACE("audio", Z80, 3000000)
		MDRV_CPU_MEMORY(pinbo_sound_readmem,pinbo_sound_writemem)
		MDRV_CPU_PORTS(pinbo_sound_readport,pinbo_sound_writeport)
	
		MDRV_CPU_REMOVE("blitter")
	
		/* video hardware */
		MDRV_GFXDECODE(pinbo_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(pinbo)
		MDRV_VIDEO_UPDATE(chameleo)
	
		/* sound hardware */
		MDRV_SOUND_REMOVE("sn76496")
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static RomLoadPtr rom_lasso = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "wm3",       0x8000, 0x2000, 0xf93addd6 );
		ROM_RELOAD(            0xc000, 0x2000);
		ROM_LOAD( "wm4",       0xe000, 0x2000, 0x77719859 );
		ROM_RELOAD(            0xa000, 0x2000);
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "wmc",       0x5000, 0x1000, 0x8b4eb242 );
		ROM_LOAD( "wmb",       0x6000, 0x1000, 0x4658bcb9 );
		ROM_LOAD( "wma",       0x7000, 0x1000, 0x2e7de3e9 );
		ROM_RELOAD(            0xf000, 0x1000 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* 6502 code (lasso image blitter) */
		ROM_LOAD( "wm5",       0xf000, 0x1000, 0x7dc3ff07 );
		ROM_RELOAD(            0x8000, 0x1000);
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "wm1",       0x0000, 0x0800, 0x7db77256 );/* Tiles   */
		ROM_CONTINUE(          0x1000, 0x0800             );/* Sprites */
		ROM_CONTINUE(          0x0800, 0x0800             );
		ROM_CONTINUE(          0x1800, 0x0800             );
		ROM_LOAD( "wm2",       0x2000, 0x0800, 0x9e7d0b6f );/* 2nd bitplane */
		ROM_CONTINUE(          0x3000, 0x0800             );
		ROM_CONTINUE(          0x2800, 0x0800             );
		ROM_CONTINUE(          0x3800, 0x0800             );
	
		ROM_REGION( 0x40, REGION_PROMS, 0 );
		ROM_LOAD( "82s123.69", 0x0000, 0x0020, 0x1eabb04d );
		ROM_LOAD( "82s123.70", 0x0020, 0x0020, 0x09060f8c );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_chameleo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );	/* 6502 Code (Main CPU) */
		ROM_LOAD( "chamel4.bin", 0x4000, 0x2000, 0x97379c47 );
		ROM_LOAD( "chamel5.bin", 0x6000, 0x2000, 0x0a2cadfd );
		ROM_LOAD( "chamel6.bin", 0x8000, 0x2000, 0xb023c354 );
		ROM_LOAD( "chamel7.bin", 0xa000, 0x2000, 0xa5a03375 );
		ROM_RELOAD(              0xe000, 0x2000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* 6502 Code (Sound CPU) */
		ROM_LOAD( "chamel3.bin", 0x1000, 0x1000, 0x52eab9ec );
		ROM_LOAD( "chamel2.bin", 0x6000, 0x1000, 0x81dcc49c );
		ROM_LOAD( "chamel1.bin", 0x7000, 0x1000, 0x96031d3b );
		ROM_RELOAD(              0xf000, 0x1000             );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "chamel8.bin", 0x0800, 0x0800, 0xdc67916b );/* Tiles   */
		ROM_CONTINUE(            0x1800, 0x0800             );/* Sprites */
		ROM_CONTINUE(            0x0000, 0x0800             );
		ROM_CONTINUE(            0x1000, 0x0800             );
		ROM_LOAD( "chamel9.bin", 0x2800, 0x0800, 0x6b559bf1 );/* 2nd bitplane */
		ROM_CONTINUE(            0x3800, 0x0800             );
		ROM_CONTINUE(            0x2000, 0x0800             );
		ROM_CONTINUE(            0x3000, 0x0800             );
	
		ROM_REGION( 0x40, REGION_PROMS, ROMREGION_DISPOSE );/* Colors */
		ROM_LOAD( "chambprm.bin", 0x0000, 0x0020, 0xe3ad76df );
		ROM_LOAD( "chamaprm.bin", 0x0020, 0x0020, 0xc7063b54 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_wwjgtin = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );	/* 6502 Code (Main CPU) */
		ROM_LOAD( "ic2.6", 0x4000, 0x4000, 0x744ba45b );
		ROM_LOAD( "ic5.5", 0x8000, 0x4000, 0xaf751614 );
		ROM_RELOAD(        0xc000, 0x4000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* 6502 Code (Sound CPU) */
		ROM_LOAD( "ic59.9", 0x4000, 0x4000, 0x2ecb4d98 );
		ROM_RELOAD(         0xc000, 0x4000             );
	
		ROM_REGION( 0x8000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ic81.7", 0x0000, 0x0800, 0xa27f1a63 );/* Tiles   */
		ROM_CONTINUE(       0x2000, 0x0800             );/* Sprites */
		ROM_CONTINUE(       0x0800, 0x0800             );
		ROM_CONTINUE(       0x2800, 0x0800             );
		ROM_CONTINUE(       0x1000, 0x0800             );
		ROM_CONTINUE(       0x3000, 0x0800             );
		ROM_CONTINUE(       0x1800, 0x0800             );
		ROM_CONTINUE(       0x3800, 0x0800             );
		ROM_LOAD( "ic82.8", 0x4000, 0x0800, 0xea2862b3 );/* 2nd bitplane */
		ROM_CONTINUE(       0x6000, 0x0800             );/* Sprites */
		ROM_CONTINUE(       0x4800, 0x0800             );
		ROM_CONTINUE(       0x6800, 0x0800             );
		ROM_CONTINUE(       0x5000, 0x0800             );
		ROM_CONTINUE(       0x7000, 0x0800             );
		ROM_CONTINUE(       0x5800, 0x0800             );
		ROM_CONTINUE(       0x7800, 0x0800             );
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "ic47.3", 0x0000, 0x2000, 0x40594c59 );// 1xxxxxxxxxxxx = 0xFF
		ROM_LOAD( "ic46.4", 0x2000, 0x2000, 0xd1921348 );
	
		ROM_REGION( 0x4000, REGION_USER1, 0 );			/* tilemap */
		ROM_LOAD( "ic48.2", 0x0000, 0x2000, 0xa4a7df77 );
		ROM_LOAD( "ic49.1", 0x2000, 0x2000, 0xe480fbba );// FIXED BITS (1111xxxx)
	
		ROM_REGION( 0x40, REGION_PROMS, ROMREGION_DISPOSE );
		ROM_LOAD( "2.bpr",  0x0000, 0x0020, 0x79adda5d );
		ROM_LOAD( "1.bpr",  0x0020, 0x0020, 0xc1a93cc8 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_pinbo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "rom2.b7",     0x2000, 0x2000, 0x9a185338 );
		ROM_LOAD( "rom3.e7",     0x6000, 0x2000, 0x1cd1b3bd );
		ROM_LOAD( "rom4.h7",     0x8000, 0x2000, 0xba043fa7 );
		ROM_LOAD( "rom5.j7",     0xa000, 0x2000, 0xe71046c4 );
		ROM_RELOAD(              0xe000, 0x2000 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ); /* 64K for sound */
		ROM_LOAD( "rom1.s8",     0x0000, 0x2000, 0xca45a1be );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "rom6.a1",     0x0000, 0x0800, 0x74fe8e98 );/* tiles   */
		ROM_CONTINUE(       	 0x2000, 0x0800             );/* sprites */
		ROM_CONTINUE(       	 0x0800, 0x0800             );
		ROM_CONTINUE(       	 0x2800, 0x0800             );
		ROM_CONTINUE(       	 0x1000, 0x0800             );
		ROM_CONTINUE(       	 0x3000, 0x0800             );
		ROM_CONTINUE(       	 0x1800, 0x0800             );
		ROM_CONTINUE(       	 0x3800, 0x0800             );
		ROM_LOAD( "rom8.c1",     0x4000, 0x0800, 0x5a800fe7 );/* 2nd bitplane */
		ROM_CONTINUE(       	 0x6000, 0x0800             );
		ROM_CONTINUE(       	 0x4800, 0x0800             );
		ROM_CONTINUE(       	 0x6800, 0x0800             );
		ROM_CONTINUE(       	 0x5000, 0x0800             );
		ROM_CONTINUE(       	 0x7000, 0x0800             );
		ROM_CONTINUE(       	 0x5800, 0x0800             );
		ROM_CONTINUE(       	 0x7800, 0x0800             );
		ROM_LOAD( "rom7.d1",     0x8000, 0x0800, 0x327a3c21 );/* 3rd bitplane */
		ROM_CONTINUE(       	 0xa000, 0x0800             );
		ROM_CONTINUE(       	 0x8800, 0x0800             );
		ROM_CONTINUE(       	 0xa800, 0x0800             );
		ROM_CONTINUE(       	 0x9000, 0x0800             );
		ROM_CONTINUE(       	 0xb000, 0x0800             );
		ROM_CONTINUE(       	 0x9800, 0x0800             );
		ROM_CONTINUE(       	 0xb800, 0x0800             );
	
		ROM_REGION( 0x00300, REGION_PROMS, 0 );/* Color PROMs */
		ROM_LOAD( "red.l10",     0x0000, 0x0100, 0xe6c9ba52 );
		ROM_LOAD( "green.k10",   0x0100, 0x0100, 0x1bf2d335 );
		ROM_LOAD( "blue.n10",    0x0200, 0x0100, 0xe41250ad );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_pinbos = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "b4.bin",      0x2000, 0x2000, 0xd9452d4f );
		ROM_LOAD( "b5.bin",      0x6000, 0x2000, 0xf80b204c );
		ROM_LOAD( "b6.bin",      0x8000, 0x2000, 0xae967d83 );
		ROM_LOAD( "b7.bin",      0xa000, 0x2000, 0x7a584b4e );
		ROM_RELOAD(              0xe000, 0x2000 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ); /* 64K for sound */
		ROM_LOAD( "b8.bin",      0x0000, 0x2000, 0x32d1df14 );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "rom6.a1",     0x0000, 0x0800, 0x74fe8e98 );/* tiles   */
		ROM_CONTINUE(       	 0x2000, 0x0800             );/* sprites */
		ROM_CONTINUE(       	 0x0800, 0x0800             );
		ROM_CONTINUE(       	 0x2800, 0x0800             );
		ROM_CONTINUE(       	 0x1000, 0x0800             );
		ROM_CONTINUE(       	 0x3000, 0x0800             );
		ROM_CONTINUE(       	 0x1800, 0x0800             );
		ROM_CONTINUE(       	 0x3800, 0x0800             );
		ROM_LOAD( "rom8.c1",     0x4000, 0x0800, 0x5a800fe7 );/* 2nd bitplane */
		ROM_CONTINUE(       	 0x6000, 0x0800             );
		ROM_CONTINUE(       	 0x4800, 0x0800             );
		ROM_CONTINUE(       	 0x6800, 0x0800             );
		ROM_CONTINUE(       	 0x5000, 0x0800             );
		ROM_CONTINUE(       	 0x7000, 0x0800             );
		ROM_CONTINUE(       	 0x5800, 0x0800             );
		ROM_CONTINUE(       	 0x7800, 0x0800             );
		ROM_LOAD( "rom7.d1",     0x8000, 0x0800, 0x327a3c21 );/* 3rd bitplane */
		ROM_CONTINUE(       	 0xa000, 0x0800             );
		ROM_CONTINUE(       	 0x8800, 0x0800             );
		ROM_CONTINUE(       	 0xa800, 0x0800             );
		ROM_CONTINUE(       	 0x9000, 0x0800             );
		ROM_CONTINUE(       	 0xb000, 0x0800             );
		ROM_CONTINUE(       	 0x9800, 0x0800             );
		ROM_CONTINUE(       	 0xb800, 0x0800             );
	
		ROM_REGION( 0x00300, REGION_PROMS, 0 );/* Color PROMs */
		ROM_LOAD( "red.l10",     0x0000, 0x0100, 0xe6c9ba52 );
		ROM_LOAD( "green.k10",   0x0100, 0x0100, 0x1bf2d335 );
		ROM_LOAD( "blue.n10",    0x0200, 0x0100, 0xe41250ad );
	ROM_END(); }}; 
	
	
	/***************************************************************************
	
									Game Drivers
	
	***************************************************************************/
	
	public static GameDriver driver_lasso	   = new GameDriver("1982"	,"lasso"	,"lasso.java"	,rom_lasso,null	,machine_driver_lasso	,input_ports_lasso	,null	,ROT90	,	"SNK", "Lasso"                   )
	public static GameDriver driver_chameleo	   = new GameDriver("1983"	,"chameleo"	,"lasso.java"	,rom_chameleo,null	,machine_driver_chameleo	,input_ports_chameleo	,null	,ROT0	,	"Jaleco", "Chameleon"               )
	public static GameDriver driver_wwjgtin	   = new GameDriver("1984"	,"wwjgtin"	,"lasso.java"	,rom_wwjgtin,null	,machine_driver_wwjgtin	,input_ports_wwjgtin	,null	,ROT0	,	"Jaleco / Casio", "Wai Wai Jockey Gate-In!" )
	public static GameDriver driver_pinbo	   = new GameDriver("1984"	,"pinbo"	,"lasso.java"	,rom_pinbo,null	,machine_driver_pinbo	,input_ports_pinbo	,null	,ROT90	,	"Jaleco", "Pinbo" )
	public static GameDriver driver_pinbos	   = new GameDriver("1984"	,"pinbos"	,"lasso.java"	,rom_pinbos,driver_pinbo	,machine_driver_pinbo	,input_ports_pinbos	,null	,ROT90	,	"bootleg?", "Pinbo (Strike)" )
}
