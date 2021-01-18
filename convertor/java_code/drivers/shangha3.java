/***************************************************************************

Shanghai 3           (c)1993 Sunsoft     (68000     AY8910 OKI6295)
Hebereke no Popoon   (c)1994 Sunsoft     (68000 Z80 YM3438 OKI6295)
Blocken              (c)1994 KID / Visco (68000 Z80 YM3438 OKI6295)

These games use the custom blitter GA9201 KA01-0249 (120pin IC)

driver by Nicola Salmoria

TODO:
shangha3:
- The zoom used for the "100" floating score when you remove tiles is very
  rough.
heberpop:
- Unknown writes to sound ports 40/41
blocken:
- incomplete zoom support, and missing rotation support.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class shangha3
{
	
	
	extern data16_t *shangha3_ram;
	extern size_t shangha3_ram_size;
	
	WRITE16_HANDLER( shangha3_flipscreen_w );
	WRITE16_HANDLER( shangha3_gfxlist_addr_w );
	WRITE16_HANDLER( shangha3_blitter_go_w );
	VIDEO_START( shangha3 );
	VIDEO_UPDATE( shangha3 );
	
	
	
	/* this looks like a simple protection check */
	/*
	write    read
	78 78 -> 0
	9b 10 -> 1
	9b 20 -> 3
	9b 40 -> 7
	9b 80 -> f
	08    -> e
	10    -> c
	20    -> 8
	40    -> 0
	*/
	static READ16_HANDLER( shangha3_prot_r )
	{
		static int count;
		static int result[] = { 0x0,0x1,0x3,0x7,0xf,0xe,0xc,0x8,0x0};
	
	logerror("PC %04x: read 20004e\n",activecpu_get_pc());
	
		return result[count++ % 9];
	}
	static WRITE16_HANDLER( shangha3_prot_w )
	{
	logerror("PC %04x: write %02x to 20004e\n",activecpu_get_pc(),data);
	}
	
	
	static READ16_HANDLER( heberpop_gfxrom_r )
	{
		UINT8 *ROM = memory_region(REGION_GFX1);
	
		return ROM[2*offset] | (ROM[2*offset+1] << 8);
	}
	
	
	
	static WRITE16_HANDLER( shangha3_coinctrl_w )
	{
		if (ACCESSING_MSB)
		{
			coin_lockout_w(0,~data & 0x0400);
			coin_lockout_w(1,~data & 0x0400);
			coin_counter_w(0,data & 0x0100);
			coin_counter_w(1,data & 0x0200);
		}
	}
	
	static WRITE16_HANDLER( heberpop_coinctrl_w )
	{
		if (ACCESSING_LSB)
		{
			/* the sound ROM bank is selected by the main CPU! */
			OKIM6295_set_bank_base(0,(data & 0x08) ? 0x40000 : 0x00000);
	
			coin_lockout_w(0,~data & 0x04);
			coin_lockout_w(1,~data & 0x04);
			coin_counter_w(0,data & 0x01);
			coin_counter_w(1,data & 0x02);
		}
	}
	
	
	static WRITE16_HANDLER( heberpop_sound_command_w )
	{
		if (ACCESSING_LSB)
		{
			soundlatch_w(0,data & 0xff);
			cpu_set_irq_line_and_vector(1,0,HOLD_LINE,0xff);	/* RST 38h */
		}
	}
	
	
	
	static MEMORY_READ16_START( shangha3_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x100fff, MRA16_RAM },
		{ 0x200000, 0x200001, input_port_0_word_r },
		{ 0x200002, 0x200003, input_port_1_word_r },
		{ 0x20001e, 0x20001f, AY8910_read_port_0_lsb_r },
		{ 0x20004e, 0x20004f, shangha3_prot_r },
		{ 0x20006e, 0x20006f, OKIM6295_status_0_lsb_r },
		{ 0x300000, 0x30ffff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( shangha3_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x100fff, paletteram16_RRRRRGGGGGBBBBBx_word_w, &paletteram16 },
		{ 0x200008, 0x200009, shangha3_blitter_go_w },
		{ 0x20000a, 0x20000b, MWA16_NOP },	/* irq ack? */
		{ 0x20000c, 0x20000d, shangha3_coinctrl_w },
		{ 0x20002e, 0x20002f, AY8910_write_port_0_lsb_w },
		{ 0x20003e, 0x20003f, AY8910_control_port_0_lsb_w },
		{ 0x20004e, 0x20004f, shangha3_prot_w },
		{ 0x20006e, 0x20006f, OKIM6295_data_0_lsb_w },
		{ 0x300000, 0x30ffff, MWA16_RAM, &shangha3_ram, &shangha3_ram_size },	/* gfx & work ram */
		{ 0x340000, 0x340001, shangha3_flipscreen_w },
		{ 0x360000, 0x360001, shangha3_gfxlist_addr_w },
	MEMORY_END
	
	
	static MEMORY_READ16_START( heberpop_readmem )
		{ 0x000000, 0x0fffff, MRA16_ROM },
		{ 0x100000, 0x100fff, MRA16_RAM },
		{ 0x200000, 0x200001, input_port_0_word_r },
		{ 0x200002, 0x200003, input_port_1_word_r },
		{ 0x200004, 0x200005, input_port_2_word_r },
		{ 0x300000, 0x30ffff, MRA16_RAM },
		{ 0x800000, 0xb7ffff, heberpop_gfxrom_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( heberpop_writemem )
		{ 0x000000, 0x0fffff, MWA16_ROM },
		{ 0x100000, 0x100fff, paletteram16_RRRRRGGGGGBBBBBx_word_w, &paletteram16 },
		{ 0x200008, 0x200009, shangha3_blitter_go_w },
		{ 0x20000a, 0x20000b, MWA16_NOP },	/* irq ack? */
		{ 0x20000c, 0x20000d, heberpop_coinctrl_w },
		{ 0x20000e, 0x20000f, heberpop_sound_command_w },
		{ 0x300000, 0x30ffff, MWA16_RAM, &shangha3_ram, &shangha3_ram_size },	/* gfx & work ram */
		{ 0x340000, 0x340001, shangha3_flipscreen_w },
		{ 0x360000, 0x360001, shangha3_gfxlist_addr_w },
	MEMORY_END
	
	static MEMORY_READ16_START( blocken_readmem )
		{ 0x000000, 0x0fffff, MRA16_ROM },
		{ 0x100000, 0x100001, input_port_0_word_r },
		{ 0x100002, 0x100003, input_port_1_word_r },
		{ 0x100004, 0x100005, input_port_2_word_r },
		{ 0x200000, 0x200fff, MRA16_RAM },
		{ 0x300000, 0x30ffff, MRA16_RAM },
		{ 0x800000, 0xb7ffff, heberpop_gfxrom_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( blocken_writemem )
		{ 0x000000, 0x0fffff, MWA16_ROM },
		{ 0x100008, 0x100009, shangha3_blitter_go_w },
		{ 0x10000a, 0x10000b, MWA16_NOP },	/* irq ack? */
		{ 0x10000c, 0x10000d, heberpop_coinctrl_w },
		{ 0x10000e, 0x10000f, heberpop_sound_command_w },
		{ 0x200000, 0x200fff, paletteram16_RRRRRGGGGGBBBBBx_word_w, &paletteram16 },
		{ 0x300000, 0x30ffff, MWA16_RAM, &shangha3_ram, &shangha3_ram_size },	/* gfx & work ram */
		{ 0x340000, 0x340001, shangha3_flipscreen_w },
		{ 0x360000, 0x360001, shangha3_gfxlist_addr_w },
	MEMORY_END
	
	
	public static Memory_ReadAddress heberpop_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xf7ff, MRA_ROM ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress heberpop_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xf7ff, MWA_ROM ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort heberpop_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, YM2612_status_port_0_A_r ),
		new IO_ReadPort( 0x80, 0x80, OKIM6295_status_0_r ),
		new IO_ReadPort( 0xc0, 0xc0, soundlatch_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort heberpop_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, YM2612_control_port_0_A_w ),
		new IO_WritePort( 0x01, 0x01, YM2612_data_port_0_A_w ),
		new IO_WritePort( 0x02, 0x02, YM2612_control_port_0_B_w ),
		new IO_WritePort( 0x03, 0x03, YM2612_data_port_0_B_w ),
		new IO_WritePort( 0x80, 0x80, OKIM6295_data_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_shangha3 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BITX(0x0020, IP_ACTIVE_LOW, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_VBLANK );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x01, "Easy" );
		PORT_DIPSETTING(    0x03, "Normal" );
		PORT_DIPSETTING(    0x02, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, "Base Time" );
		PORT_DIPSETTING(    0x04, "70 sec" );
		PORT_DIPSETTING(    0x0c, "80 sec" );
		PORT_DIPSETTING(    0x08, "90 sec" );
		PORT_DIPSETTING(    0x00, "100 sec" );
		PORT_DIPNAME( 0x30, 0x30, "Additional Time" );
		PORT_DIPSETTING(    0x10, "4 sec" );
		PORT_DIPSETTING(    0x30, "5 sec" );
		PORT_DIPSETTING(    0x20, "6 sec" );
		PORT_DIPSETTING(    0x00, "7 sec" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x38, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_heberpop = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_VBLANK );/* vblank?? has to toggle */
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_VBLANK );/* vblank?? has to toggle */
	
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BITX(0x0020, IP_ACTIVE_LOW, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_DIPNAME( 0x0003, 0x0003, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0002, "Very Easy" );
		PORT_DIPSETTING(      0x0001, "Easy" );
		PORT_DIPSETTING(      0x0003, "Normal" );
		PORT_DIPSETTING(      0x0000, "Hard" );
		PORT_DIPNAME( 0x0004, 0x0004, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0004, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0008, 0x0008, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0010, 0x0010, "Allow Diagonal Moves" );
		PORT_DIPSETTING(      0x0000, DEF_STR( "No") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x0020, 0x0020, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0020, DEF_STR( "On") );
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0700, 0x0700, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0200, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0600, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0700, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0300, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0500, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0100, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x3800, 0x3800, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x3000, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x3800, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x1800, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x2800, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_blocken = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_VBLANK );/* vblank?? has to toggle */
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_VBLANK );/* vblank?? has to toggle */
	
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_SERVICE );/* keeping this pressed on boot generates "BAD DIPSW" */
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_SERVICE( 0x0001, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x0006, 0x0006, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0004, "Easy" );
		PORT_DIPSETTING(      0x0006, "Normal" );
		PORT_DIPSETTING(      0x0002, "Hard" );
		PORT_DIPSETTING(      0x0000, "Very Hard" );
		PORT_DIPNAME( 0x0008, 0x0008, "Game Type" );
		PORT_DIPSETTING(      0x0008, "A" );
		PORT_DIPSETTING(      0x0000, "B" );
		PORT_DIPNAME( 0x0030, 0x0030, "Players" );
		PORT_DIPSETTING(      0x0030, "1" );
		PORT_DIPSETTING(      0x0020, "2" );
		PORT_DIPSETTING(      0x0010, "3" );
		PORT_DIPSETTING(      0x0000, "4" );
		PORT_DIPNAME( 0x0040, 0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0f00, 0x0f00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x0200, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0500, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(      0x0100, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(      0x0f00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0300, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(      0x0700, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0x0e00, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0600, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(      0x0d00, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0c00, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x0b00, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(      0x0a00, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(      0x0900, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf000, 0xf000, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x5000, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "5C_3C") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(      0xf000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x3000, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(      0x7000, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0xe000, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x6000, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(      0xd000, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0xc000, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0xb000, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(      0xa000, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(      0x9000, DEF_STR( "1C_7C") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 1*4, 0*4, 3*4, 2*4, 5*4, 4*4, 7*4, 6*4,
				9*4, 8*4, 11*4, 10*4, 13*4, 12*4, 15*4, 14*4 },
		new int[] { 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
				8*64, 9*64, 10*64, 11*64, 12*64, 13*64, 14*64, 15*64 },
		128*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 128 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,			/* 1 chip */
		1500000,	/* 1.5 MHz */
		new int[] { 30 },
		new ReadHandlerPtr[] { input_port_3_r },
		new ReadHandlerPtr[] { input_port_2_r },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static void irqhandler(int linestate)
	{
		cpu_set_nmi_line(1,linestate);
	}
	
	static struct YM2612interface ym3438_interface =
	{
		1,			/* 1 chip */
		8000000,	/* 8 MHz ?? */
		{ YM3012_VOL(40,MIXER_PAN_CENTER,40,MIXER_PAN_CENTER) },	/* Volume */
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ irqhandler }
	};
	
	static struct OKIM6295interface okim6295_interface =
	{
		1,                  /* 1 chip */
		{ 8000 },           /* 8000Hz frequency ??? */
		{ REGION_SOUND1 },	/* memory region */
		{ 100 }
	};
	
	
	
	static MACHINE_DRIVER_START( shangha3 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)	/* 16 MHz ??? */
		MDRV_CPU_MEMORY(shangha3_readmem,shangha3_writemem)
		MDRV_CPU_VBLANK_INT(irq4_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(24*16, 16*16)
		MDRV_VISIBLE_AREA(0*16, 24*16-1, 1*16, 15*16-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(shangha3)
		MDRV_VIDEO_UPDATE(shangha3)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( heberpop )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)	/* 16 MHz ??? */
		MDRV_CPU_MEMORY(heberpop_readmem,heberpop_writemem)
		MDRV_CPU_VBLANK_INT(irq4_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 6000000)	/* 6 MHz ??? */
		MDRV_CPU_MEMORY(heberpop_sound_readmem,heberpop_sound_writemem)
		MDRV_CPU_PORTS(heberpop_sound_readport,heberpop_sound_writeport)
									/* NMI triggered by YM3438 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(24*16, 16*16)
		MDRV_VISIBLE_AREA(0*16, 24*16-1, 1*16, 15*16-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(shangha3)
		MDRV_VIDEO_UPDATE(shangha3)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3438, ym3438_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( blocken )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)	/* 16 MHz ??? */
		MDRV_CPU_MEMORY(blocken_readmem,blocken_writemem)
		MDRV_CPU_VBLANK_INT(irq4_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 6000000)	/* 6 MHz ??? */
		MDRV_CPU_MEMORY(heberpop_sound_readmem,heberpop_sound_writemem)
		MDRV_CPU_PORTS(heberpop_sound_readport,heberpop_sound_writeport)
									/* NMI triggered by YM3438 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(24*16, 16*16)
		MDRV_VISIBLE_AREA(0*16, 24*16-1, 1*16, 15*16-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(shangha3)
		MDRV_VIDEO_UPDATE(shangha3)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3438, ym3438_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_shangha3 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "s3j_ic3.v11",  0x0000, 0x40000, 0xe98ce9c8 );
		ROM_LOAD16_BYTE( "s3j_ic2.v11",  0x0001, 0x40000, 0x09174620 );
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "s3j_ic43.chr", 0x0000, 0x200000, 0x2dbf9d17 );
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 );/* samples for M6295 */
		ROM_LOAD( "s3j_ic75.v10", 0x0000, 0x40000, 0xf0cdc86a );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_heberpop = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "hbpic31.bin",  0x0000, 0x80000, 0xc430d264 );
		ROM_LOAD16_BYTE( "hbpic32.bin",  0x0001, 0x80000, 0xbfa555a8 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "hbpic34.bin",  0x0000, 0x10000, 0x0cf056c6 );
	
		ROM_REGION( 0x380000, REGION_GFX1, 0 );/* don't dispose, read during tests */
		ROM_LOAD( "hbpic98.bin",  0x000000, 0x80000, 0xa599100a );
		ROM_LOAD( "hbpic99.bin",  0x080000, 0x80000, 0xfb8bb12f );
		ROM_LOAD( "hbpic100.bin", 0x100000, 0x80000, 0x05a0f765 );
		ROM_LOAD( "hbpic101.bin", 0x180000, 0x80000, 0x151ba025 );
		ROM_LOAD( "hbpic102.bin", 0x200000, 0x80000, 0x2b5e341a );
		ROM_LOAD( "hbpic103.bin", 0x280000, 0x80000, 0xefa0e745 );
		ROM_LOAD( "hbpic104.bin", 0x300000, 0x80000, 0xbb896bbb );
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 );/* samples for M6295 */
		ROM_LOAD( "hbpic53.bin",  0x0000, 0x80000, 0xa4483aa0 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_blocken = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "ic31j.bin",    0x0000, 0x20000, 0xec8de2a3 );
		ROM_LOAD16_BYTE( "ic32j.bin",    0x0001, 0x20000, 0x79b96240 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "ic34.bin",     0x0000, 0x10000, 0x23e446ff );
	
		ROM_REGION( 0x380000, REGION_GFX1, 0 );/* don't dispose, read during tests */
		ROM_LOAD( "ic98j.bin",    0x000000, 0x80000, 0x35dda273 );
		ROM_LOAD( "ic99j.bin",    0x080000, 0x80000, 0xce43762b );
		/* 100000-1fffff empty */
		ROM_LOAD( "ic100j.bin",   0x200000, 0x80000, 0xa34786fd );
		/* 280000-37ffff empty */
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 );/* samples for M6295 */
		ROM_LOAD( "ic53.bin",     0x0000, 0x80000, 0x86108c56 );
	ROM_END(); }}; 
	
	
	
	static DRIVER_INIT( shangha3 )
	{
		shangha3_do_shadows = 1;
	}
	static DRIVER_INIT( heberpop )
	{
		shangha3_do_shadows = 0;
	}
	
	public static GameDriver driver_shangha3	   = new GameDriver("1993"	,"shangha3"	,"shangha3.java"	,rom_shangha3,null	,machine_driver_shangha3	,input_ports_shangha3	,init_shangha3	,ROT0	,	"Sunsoft", "Shanghai III (Japan)" )
	public static GameDriver driver_heberpop	   = new GameDriver("1994"	,"heberpop"	,"shangha3.java"	,rom_heberpop,null	,machine_driver_heberpop	,input_ports_heberpop	,init_heberpop	,ROT0	,	"Sunsoft / Atlus", "Hebereke no Popoon (Japan)" )
	public static GameDriver driver_blocken	   = new GameDriver("1994"	,"blocken"	,"shangha3.java"	,rom_blocken,null	,machine_driver_blocken	,input_ports_blocken	,init_heberpop	,ROT0	,	"KID / Visco", "Blocken (Japan)" )
}
