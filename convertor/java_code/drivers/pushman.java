/***************************************************************************

	Pushman							(c) 1990 Comad

	With 'Debug Mode' on button 2 advances a level, button 3 goes back.

	The microcontroller mainly controls the animation of the enemy robots,
	the communication between the 68000 and MCU is probably not emulated
	100% correct but it works.  Later levels (using the cheat mode) seem
	to have some corrupt tilemaps, I'm not sure if this is a driver bug
	or a game bug from using the cheat mode.

	Emulation by Bryan McPhail, mish@tendril.co.uk

	The hardware is actually very similar to F1-Dream and Tiger Road but
	with a 68705 for protection.

 **************************************************************************

	Bouncing Balls						(c) 1991 Comad

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class pushman
{
	
	VIDEO_UPDATE( pushman );
	WRITE16_HANDLER( pushman_scroll_w );
	WRITE16_HANDLER( pushman_videoram_w );
	VIDEO_START( pushman );
	
	static UINT8 shared_ram[8];
	static UINT16 latch,new_latch=0;
	
	/******************************************************************************/
	
	static WRITE16_HANDLER( pushman_control_w )
	{
		if (ACCESSING_MSB)
			soundlatch_w(0,(data>>8)&0xff);
	}
	
	static READ16_HANDLER( pushman_68705_r )
	{
		if (offset==0)
			return latch;
	
		if (offset==3 && new_latch) { new_latch=0; return 0; }
		if (offset==3 && !new_latch) return 0xff;
	
		return (shared_ram[2*offset+1]<<8)+shared_ram[2*offset];
	}
	
	static WRITE16_HANDLER( pushman_68705_w )
	{
		if (ACCESSING_MSB)
			shared_ram[2*offset]=data>>8;
		if (ACCESSING_LSB)
			shared_ram[2*offset+1]=data&0xff;
	
		if (offset==1)
		{
	        cpu_set_irq_line(2,M68705_IRQ_LINE,HOLD_LINE);
			cpu_spin();
			new_latch=0;
		}
	}
	
	/* ElSemi - Bouncing balls protection. */
	static READ16_HANDLER( bballs_68705_r )
	{
		if (offset==0)
			return latch;
		if(offset==3 && new_latch)
		{
	        	new_latch=0;
			return 0;
		}
		if(offset==3 && !new_latch)
			return 0xff;
	
		return (shared_ram[2*offset+1]<<8)+shared_ram[2*offset];
	}
	
	static WRITE16_HANDLER( bballs_68705_w )
	{
		if (ACCESSING_MSB)
			shared_ram[2*offset]=data>>8;
		if (ACCESSING_LSB)
			shared_ram[2*offset+1]=data&0xff;
	
		if(offset==0)
		{
			latch=0;
			if(shared_ram[0]<=0xf)
			{
				latch=shared_ram[0]<<2;
				if(shared_ram[1])
					latch|=2;
				new_latch=1;
			}
			else if(shared_ram[0])
			{
				if(shared_ram[1])
					latch|=2;
				new_latch=1;
			}
		}
	}
	
	
	public static ReadHandlerPtr pushman_68000_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return shared_ram[offset];
	} };
	
	public static WriteHandlerPtr pushman_68000_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset==2 && (shared_ram[2]&2)==0 && data&2) {
			latch=(shared_ram[1]<<8)|shared_ram[0];
			new_latch=1;
		}
		shared_ram[offset]=data;
	} };
	
	MACHINE_INIT( bballs )
	{
		latch=0x400;
	}
	
	/******************************************************************************/
	
	static MEMORY_READ16_START( readmem )
		{ 0x000000, 0x01ffff, MRA16_ROM },
		{ 0x060000, 0x060007, pushman_68705_r },
		{ 0xfe0800, 0xfe17ff, MRA16_RAM },
		{ 0xfe4000, 0xfe4001, input_port_0_word_r },
		{ 0xfe4002, 0xfe4003, input_port_1_word_r },
		{ 0xfe4004, 0xfe4005, input_port_2_word_r },
		{ 0xfec000, 0xfec7ff, MRA16_RAM },
		{ 0xff8000, 0xff87ff, MRA16_RAM },
		{ 0xffc000, 0xffffff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( writemem )
		{ 0x000000, 0x01ffff, MWA16_ROM },
		{ 0x060000, 0x060007, pushman_68705_w },
		{ 0xfe0800, 0xfe17ff, MWA16_RAM, &spriteram16 },
		{ 0xfe4002, 0xfe4003, pushman_control_w },
		{ 0xfe8000, 0xfe8003, pushman_scroll_w },
		{ 0xfe800e, 0xfe800f, MWA16_NOP }, /* ? */
		{ 0xfec000, 0xfec7ff, pushman_videoram_w, &videoram16 },
		{ 0xff8000, 0xff87ff, paletteram16_xxxxRRRRGGGGBBBB_word_w, &paletteram16 },
		{ 0xffc000, 0xffffff, MWA16_RAM },
	MEMORY_END
	
	public static Memory_ReadAddress mcu_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0007, pushman_68000_r ),
		new Memory_ReadAddress( 0x0010, 0x007f, MRA_RAM ),
		new Memory_ReadAddress( 0x0080, 0x0fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mcu_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0007, pushman_68000_w ),
		new Memory_WriteAddress( 0x0010, 0x007f, MWA_RAM ),
		new Memory_WriteAddress( 0x0080, 0x0fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe000, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, YM2203_control_port_0_w ),
		new IO_WritePort( 0x01, 0x01, YM2203_write_port_0_w ),
		new IO_WritePort( 0x80, 0x80, YM2203_control_port_1_w ),
		new IO_WritePort( 0x81, 0x81, YM2203_write_port_1_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static MEMORY_READ16_START( bballs_readmem )
		MEMORY_ADDRESS_BITS(20)
		{ 0x00000, 0x1ffff, MRA16_ROM },
		{ 0x60000, 0x60007, bballs_68705_r },
		{ 0xe0800, 0xe17ff, MRA16_RAM },
		{ 0xe4000, 0xe4001, input_port_0_word_r },
		{ 0xe4002, 0xe4003, input_port_1_word_r },
		{ 0xe4004, 0xe4005, input_port_2_word_r },
		{ 0xec000, 0xec7ff, MRA16_RAM },
		{ 0xf8000, 0xf87ff, MRA16_RAM },
		{ 0xfc000, 0xfffff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( bballs_writemem )
		MEMORY_ADDRESS_BITS(20)
		{ 0x00000, 0x1ffff, MWA16_ROM },
		{ 0x60000, 0x60007, bballs_68705_w },
		{ 0xe0800, 0xe17ff, MWA16_RAM, &spriteram16 },
		{ 0xe4002, 0xe4003, pushman_control_w },
		{ 0xe8000, 0xe8003, pushman_scroll_w },
		{ 0xe800e, 0xe800f, MWA16_NOP }, /* ? */
		{ 0xec000, 0xec7ff, pushman_videoram_w, &videoram16 },
		{ 0xf8000, 0xf87ff, paletteram16_xxxxRRRRGGGGBBBB_word_w, &paletteram16 },
		{ 0xfc000, 0xfffff, MWA16_RAM },
	MEMORY_END
	
	/******************************************************************************/
	
	static InputPortPtr input_ports_pushman = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BIT( 0x00ff, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_VBLANK );/* not sure, probably wrong */
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 
		PORT_BITX(    0x0001, 0x0001, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Debug Mode", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(      0x0001, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002, 0x0002, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0004, 0x0004, "Level Select" );
		PORT_DIPSETTING(      0x0004, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0008, 0x0008, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0010, 0x0010, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "On") );
		PORT_SERVICE( 0x0020, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0700, 0x0700, DEF_STR( "Coinage") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x0100, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0200, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0300, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0700, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0600, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0500, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000, 0x2000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_bballs = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );// Open/Close gate
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );// Use Zap
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );	// BUTTON3 in "test mode"
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );// Open/Close gate
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );// Use Zap
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );	// BUTTON3 in "test mode"
	
		PORT_START(); 
		PORT_BIT( 0x00ff, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_VBLANK );/* not sure, probably wrong */
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 
		PORT_DIPNAME( 0x0007, 0x0007, DEF_STR( "Coinage") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0003, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0007, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0006, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0005, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0004, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x0008, 0x0008, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0008, "Easy" );		// less bubbles before cycling
		PORT_DIPSETTING(      0x0000, "Hard" );		// more bubbles before cycling
		PORT_DIPNAME( 0x0010, 0x0000, "Music (In-game); )
		PORT_DIPSETTING(      0x0010, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0020, 0x0000, "Music (Attract Mode); )
		PORT_DIPSETTING(      0x0020, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x00c0, 0x00c0, DEF_STR( "Lives") );
		PORT_DIPSETTING(      0x00c0, "1" );
		PORT_DIPSETTING(      0x0080, "2" );
		PORT_DIPSETTING(      0x0040, "3" );
		PORT_DIPSETTING(      0x0000, "4" );
		PORT_DIPNAME( 0x0100, 0x0100, "Zaps" );
		PORT_DIPSETTING(      0x0100, "1" );
		PORT_DIPSETTING(      0x0000, "2" );
		PORT_DIPNAME( 0x0200, 0x0000, "Display Next Ball" );
		PORT_DIPSETTING(      0x0200, DEF_STR( "No") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x0400, 0x0400, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( "Unused") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000, 0x2000, DEF_STR( "Unknown") );	// code at 0x0054ac, 0x0054f2, 0x0056fc
		PORT_DIPSETTING(      0x2000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0xc000, 0xc000, DEF_STR( "Service_Mode") );
		PORT_DIPSETTING(      0xc000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(      0x8000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x4000, "Inputs/Outputs" );
		PORT_DIPSETTING(      0x0000, "Graphics" );
	INPUT_PORTS_END(); }}; 
	
	/******************************************************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		2,
		new int[] { 4, 0 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(0,4), RGN_FRAC(1,4), RGN_FRAC(2,4), RGN_FRAC(3,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8
	);
	
	static GfxLayout tilelayout = new GfxLayout
	(
		32,32,
		RGN_FRAC(1,2),
		4,
		new int[] { 4, 0, RGN_FRAC(1,2)+4, RGN_FRAC(1,2)+0 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3,
				64*8+0, 64*8+1, 64*8+2, 64*8+3, 65*8+0, 65*8+1, 65*8+2, 65*8+3,
				128*8+0, 128*8+1, 128*8+2, 128*8+3, 129*8+0, 129*8+1, 129*8+2, 129*8+3,
				192*8+0, 192*8+1, 192*8+2, 192*8+3, 193*8+0, 193*8+1, 193*8+2, 193*8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16,
				16*16, 17*16, 18*16, 19*16, 20*16, 21*16, 22*16, 23*16,
				24*16, 25*16, 26*16, 27*16, 28*16, 29*16, 30*16, 31*16 },
		256*8
	);
	
	static GfxDecodeInfo pushman_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x000000, charlayout,   0x300, 16 ),	/* colors 0x300-0x33f */
		new GfxDecodeInfo( REGION_GFX2, 0x000000, spritelayout, 0x200, 16 ),	/* colors 0x200-0x2ff */
		new GfxDecodeInfo( REGION_GFX3, 0x000000, tilelayout,   0x100, 16 ),	/* colors 0x100-0x1ff */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/******************************************************************************/
	
	static void irqhandler(int irq)
	{
	    cpu_set_irq_line(1,0,irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	static struct YM2203interface ym2203_interface =
	{
		2,			/* 2 chips */
		2000000,
		{ YM2203_VOL(40,40), YM2203_VOL(40,40) },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ irqhandler }
	};
	
	
	static UINT32 amask_m68705 = 0xfff;
	
	static MACHINE_DRIVER_START( pushman )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 8000000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq2_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(0,sound_writeport)
	
		/* ElSemi. Reversed the CPU order so the sound callback works with bballs */
		MDRV_CPU_ADD(M68705, 400000)	/* No idea */
		MDRV_CPU_CONFIG(amask_m68705)
		MDRV_CPU_MEMORY(mcu_readmem,mcu_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(60)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(pushman_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(pushman)
		MDRV_VIDEO_UPDATE(pushman)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( bballs )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 8000000)
		MDRV_CPU_MEMORY(bballs_readmem,bballs_writemem)
		MDRV_CPU_VBLANK_INT(irq2_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(0,sound_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(60)
	
		MDRV_MACHINE_INIT(bballs)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(pushman_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(pushman)
		MDRV_VIDEO_UPDATE(pushman)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************/
	
	
	static RomLoadPtr rom_pushman = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "pman-12.212", 0x000000, 0x10000, 0x4251109d );
		ROM_LOAD16_BYTE( "pman-11.197", 0x000001, 0x10000, 0x1167ed9f );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "pman-13.216", 0x00000, 0x08000, 0xbc03827a );
	
		ROM_REGION( 0x01000, REGION_CPU3, 0 );
		ROM_LOAD( "pushman.uc",  0x00000, 0x01000, 0xd7916657 );
	
		ROM_REGION( 0x10000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "pman-1.130",  0x00000, 0x08000, 0x14497754 );
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "pman-4.58", 0x00000, 0x10000, 0x16e5ce6b );
		ROM_LOAD( "pman-5.59", 0x10000, 0x10000, 0xb82140b8 );
		ROM_LOAD( "pman-2.56", 0x20000, 0x10000, 0x2cb2ac29 );
		ROM_LOAD( "pman-3.57", 0x30000, 0x10000, 0x8ab957c8 );
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "pman-6.131", 0x00000, 0x10000, 0xbd0f9025 );
		ROM_LOAD( "pman-8.148", 0x10000, 0x10000, 0x591bd5c0 );
		ROM_LOAD( "pman-7.132", 0x20000, 0x10000, 0x208cb197 );
		ROM_LOAD( "pman-9.149", 0x30000, 0x10000, 0x77ee8577 );
	
		ROM_REGION( 0x10000, REGION_GFX4, 0 );/* bg tilemaps */
		ROM_LOAD( "pman-10.189", 0x00000, 0x08000, 0x5f9ae9a1 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_bballs = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "bb12.m17", 0x000000, 0x10000, 0x4501c245 );
		ROM_LOAD16_BYTE( "bb11.l17", 0x000001, 0x10000, 0x55e45b60 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "bb13.n4", 0x00000, 0x08000, 0x1ef78175 );
	
		ROM_REGION( 0x01000, REGION_CPU3, 0 );
		ROM_LOAD( "68705.uc",  0x00000, 0x01000, 0x00000000 );
	
		ROM_REGION( 0x10000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "bb1.g20",  0x00000, 0x08000, 0xb62dbcb8 );
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "bb4.d1", 0x00000, 0x10000, 0xb77de5f8 );
		ROM_LOAD( "bb5.d2", 0x10000, 0x10000, 0xffffccbf );
		ROM_LOAD( "bb2.b1", 0x20000, 0x10000, 0xa5b13236 );
		ROM_LOAD( "bb3.b2", 0x30000, 0x10000, 0xe35b383d );
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "bb6.h1", 0x00000, 0x10000, 0x0cada9ce );
		ROM_LOAD( "bb8.j1", 0x10000, 0x10000, 0xd55fe7c1 );
		ROM_LOAD( "bb7.h2", 0x20000, 0x10000, 0xa352d53b );
		ROM_LOAD( "bb9.j2", 0x30000, 0x10000, 0x78d185ac );
	
		ROM_REGION( 0x10000, REGION_GFX4, 0 );/* bg tilemaps */
		ROM_LOAD( "bb10.l6", 0x00000, 0x08000, 0xd06498f9 );
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 );/* this is the same as tiger road / f1-dream */
		ROM_LOAD( "bb_prom.e9",   0x0000, 0x0100, 0xec80ae36 );/* priority (not used) */
	ROM_END(); }}; 
	
	
	public static GameDriver driver_pushman	   = new GameDriver("1990"	,"pushman"	,"pushman.java"	,rom_pushman,null	,machine_driver_pushman	,input_ports_pushman	,null	,ROT0	,	"Comad (American Sammy license)", "Pushman" )
	public static GameDriver driver_bballs	   = new GameDriver("1991"	,"bballs"	,"pushman.java"	,rom_bballs,null	,machine_driver_bballs	,input_ports_bballs	,null	,ROT0	,	"Comad", "Bouncing Balls" )
}
