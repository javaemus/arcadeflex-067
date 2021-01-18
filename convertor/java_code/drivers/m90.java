/*****************************************************************************

	Irem M90/M97 system games:

	Hasamu							1991 M90
	Bomberman						1992 M90
	Bomberman World / Atomic Punk	1992 M97
	Quiz F-1 1,2finish				1992 M97
	Risky Challenge / Gussun Oyoyo	1993 M97
	Shisensho II					1993 M97


	Uses M72 sound hardware.

	Emulation by Bryan McPhail, mish@tendril.co.uk, thanks to Chris Hardy!

Notes:

- Samples are not played in bbmanw/atompunk.

- Not sure about the clock speeds. In hasamu and quizf1 service mode, the
  selection moves too fast with the clock set at 16 MHz. It's still fast at
  8 MHz, but at least it's usable.

*****************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class m90
{
	
	
	extern unsigned char *m90_video_data;
	
	VIDEO_UPDATE( m90 );
	VIDEO_UPDATE( m90_bootleg );
	VIDEO_START( m90 );
	
	/***************************************************************************/
	
	public static WriteHandlerPtr m90_coincounter_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset==0)
		{
			coin_counter_w(0,data & 0x01);
			coin_counter_w(1,data & 0x02);
	
			if (data&0xfe) logerror("Coin counter %02x\n",data);
		}
	} };
	
	public static WriteHandlerPtr quizf1_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset == 0)
		{
			data8_t *rom = memory_region(REGION_USER1);
	
			if (rom == 0)
				usrintf_showmessage("bankswitch with no banked ROM!");
			else
				cpu_setbank(1,rom + 0x10000 * (data & 0x0f));
		}
	} };
	
	/***************************************************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x00000, 0x7ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x80000, 0x8ffff, MRA_BANK1 ),	/* Quiz F1 only */
		new Memory_ReadAddress( 0xa0000, 0xa3fff, MRA_RAM ),
		new Memory_ReadAddress( 0xd0000, 0xdffff, MRA_RAM ),
		new Memory_ReadAddress( 0xe0000, 0xe03ff, paletteram_r ),
		new Memory_ReadAddress( 0xffff0, 0xfffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x00000, 0x7ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x80000, 0x8ffff, MWA_ROM ),	/* Quiz F1 only */
		new Memory_WriteAddress( 0xa0000, 0xa3fff, MWA_RAM ),
		new Memory_WriteAddress( 0xd0000, 0xdffff, m90_video_w, m90_video_data ),
		new Memory_WriteAddress( 0xe0000, 0xe03ff, paletteram_xBBBBBGGGGGRRRRR_w, paletteram ),
		new Memory_WriteAddress( 0xffff0, 0xfffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress bootleg_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x00000, 0x3ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x60000, 0x60fff, MRA_RAM ),
		new Memory_ReadAddress( 0xa0000, 0xa3fff, MRA_RAM ),
		new Memory_ReadAddress( 0xd0000, 0xdffff, MRA_RAM ),
		new Memory_ReadAddress( 0xe0000, 0xe03ff, paletteram_r ),
		new Memory_ReadAddress( 0xffff0, 0xfffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress bootleg_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x00000, 0x3ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000e, 0x60fff, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0xa0000, 0xa3fff, MWA_RAM ),
	//	new Memory_WriteAddress( 0xd0000, 0xdffff, m90_bootleg_video_w, m90_video_data ),
		new Memory_WriteAddress( 0xe0000, 0xe03ff, paletteram_xBBBBBGGGGGRRRRR_w, paletteram ),
		new Memory_WriteAddress( 0xffff0, 0xfffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ), /* Player 1 */
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ), /* Player 2 */
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ), /* Coins */
		new IO_ReadPort( 0x03, 0x03, MRA_NOP ),		/* Unused?  High byte of above */
		new IO_ReadPort( 0x04, 0x04, input_port_3_r ), /* Dip 1 */
		new IO_ReadPort( 0x05, 0x05, input_port_4_r ), /* Dip 2 */
		new IO_ReadPort( 0x06, 0x06, input_port_5_r ), /* Player 3 */
		new IO_ReadPort( 0x07, 0x07, input_port_6_r ), /* Player 4 */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x01, m72_sound_command_w ),
		new IO_WritePort( 0x02, 0x03, m90_coincounter_w ),
		new IO_WritePort( 0x04, 0x05, quizf1_bankswitch_w ),
		new IO_WritePort( 0x80, 0x8f, m90_video_control_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/*****************************************************************************/
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, YM2151_status_port_0_r ),
		new IO_ReadPort( 0x80, 0x80, soundlatch_r ),
		new IO_ReadPort( 0x84, 0x84, m72_sample_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, YM2151_register_port_0_w ),
		new IO_WritePort( 0x01, 0x01, YM2151_data_port_0_w ),
		new IO_WritePort( 0x80, 0x81, rtype2_sample_addr_w ),
		new IO_WritePort( 0x82, 0x82, m72_sample_w ),
		new IO_WritePort( 0x83, 0x83, m72_sound_irq_ack_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort bbmanw_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x41, 0x41, YM2151_status_port_0_r ),
		new IO_ReadPort( 0x42, 0x42, soundlatch_r ),
	//	new IO_ReadPort( 0x41, 0x41, m72_sample_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort bbmanw_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x40, 0x40, YM2151_register_port_0_w ),
		new IO_WritePort( 0x41, 0x41, YM2151_data_port_0_w ),
		new IO_WritePort( 0x42, 0x42, m72_sound_irq_ack_w ),
	//	new IO_WritePort( 0x40, 0x41, rtype2_sample_addr_w ),
	//	new IO_WritePort( 0x42, 0x42, m72_sample_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/*****************************************************************************/
	
	
	static InputPortPtr input_ports_hasamu = new InputPortPtr(){ public void handler() { 
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
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
		PORT_DIPNAME( 0x20, 0x20, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_bombrman = new InputPortPtr(){ public void handler() { 
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") ); /* Probably difficulty */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_bbmanw = new InputPortPtr(){ public void handler() { 
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") ); /* Probably difficulty */
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
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, "2 Players" );
		PORT_DIPSETTING(    0x02, "4 Players" );
		PORT_DIPNAME( 0x04, 0x04, "Coin Slots" );
		PORT_DIPSETTING(    0x04, "Common" );
		PORT_DIPSETTING(    0x00, "Separate" );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	
		IREM_JOYSTICK_3_4(3)
		IREM_JOYSTICK_3_4(4)
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_quizf1 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_START(); 
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") ); /* Probably difficulty */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Input Device" );/* input related (joystick/buttons select?) */
		PORT_DIPSETTING(    0x20, "Joystick" );
		PORT_DIPSETTING(    0x00, "Buttons" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_m97 = new InputPortPtr(){ public void handler() { 
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") ); /* Probably difficulty */
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
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	INPUT_PORTS_END(); }}; 
	
	/*****************************************************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(3,4), RGN_FRAC(2,4), RGN_FRAC(1,4), RGN_FRAC(0,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(3,4), RGN_FRAC(2,4), RGN_FRAC(1,4), RGN_FRAC(0,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
			16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, spritelayout, 256, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/*****************************************************************************/
	
	static struct YM2151interface ym2151_interface =
	{
		1,			/* 1 chip */
		3579545,	/* 3.579545 MHz */
		{ YM3012_VOL(90,MIXER_PAN_LEFT,90,MIXER_PAN_RIGHT) },
		{ m72_ym2151_irq_handler },
		{ 0 }
	};
	
	static DACinterface dac_interface = new DACinterface
	(
		1,	/* 1 channel */
		new int[] { 60 }
	);
	
	static INTERRUPT_GEN( m90_interrupt )
	{
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0x60/4);
	}
	
	
	
	static MACHINE_DRIVER_START( m90 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(V30,32000000/4)	/* 8 MHz ??????? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(m90_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.579545 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1? (Vigilante) */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 64*8)
		MDRV_VISIBLE_AREA(6*8, 54*8-1, 17*8, 47*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(m90)
		MDRV_VIDEO_UPDATE(m90)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( quizf1 )
	
		MDRV_IMPORT_FROM( m90 )
		MDRV_VISIBLE_AREA(6*8, 54*8-1, 17*8-8, 47*8-1+8)
	
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( bombrman )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(V30,32000000/4)	/* 8 MHz ??????? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(m90_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.579545 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1? (Vigilante) */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 64*8)
		MDRV_VISIBLE_AREA(10*8, 50*8-1, 17*8, 47*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(m90)
		MDRV_VIDEO_UPDATE(m90)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( bbmanw )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(V30,32000000/4)	/* 8 MHz ??????? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(m90_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.579545 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(bbmanw_sound_readport,bbmanw_sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1? (Vigilante) */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 64*8)
		MDRV_VISIBLE_AREA(10*8, 50*8-1, 17*8, 47*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(m90)
		MDRV_VIDEO_UPDATE(m90)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( bootleg )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(V30,32000000/4)	/* 16 MHz */
		MDRV_CPU_MEMORY(bootleg_readmem,bootleg_writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(m90_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.579545 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1? (Vigilante) */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(320, 240)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(m90)
		MDRV_VIDEO_UPDATE(m90)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	/***************************************************************************/
	
	#define CODE_SIZE 0x100000
	
	static RomLoadPtr rom_hasamu = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "hasc-p1.bin",  0x00001, 0x20000, 0x53df9834 );
		ROM_LOAD16_BYTE( "hasc-p0.bin",  0x00000, 0x20000, 0xdff0ba6e );
		ROM_COPY( REGION_CPU1, 0x3fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "hasc-sp.bin",    0x0000, 0x10000, 0x259b1687 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "hasc-c0.bin",    0x000000, 0x20000, 0xdd5a2174 );
		ROM_LOAD( "hasc-c1.bin",    0x020000, 0x20000, 0x76b8217c );
		ROM_LOAD( "hasc-c2.bin",    0x040000, 0x20000, 0xd90f9a68 );
		ROM_LOAD( "hasc-c3.bin",    0x060000, 0x20000, 0x6cfe0d39 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* samples */
		/* No samples */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_bombrman = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "bbm-p1.bin",   0x00001, 0x20000, 0x982bd166 );
		ROM_LOAD16_BYTE( "bbm-p0.bin",   0x00000, 0x20000, 0x0a20afcc );
		ROM_COPY( REGION_CPU1, 0x3fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "bbm-sp.bin",    0x0000, 0x10000, 0x251090cd );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "bbm-c0.bin",    0x000000, 0x40000, 0x695d2019 );
		ROM_LOAD( "bbm-c1.bin",    0x040000, 0x40000, 0x4c7c8bbc );
		ROM_LOAD( "bbm-c2.bin",    0x080000, 0x40000, 0x0700d406 );
		ROM_LOAD( "bbm-c3.bin",    0x0c0000, 0x40000, 0x3c3613af );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "bbm-v0.bin",    0x0000, 0x20000, 0x0fa803fe );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_dynablsb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "db2-26.bin",   0x00001, 0x20000, 0xa78c72f8 );
		ROM_LOAD16_BYTE( "db3-25.bin",   0x00000, 0x20000, 0xbf3137c3 );
		ROM_COPY( REGION_CPU1, 0x3fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "db1-17.bin",    0x0000, 0x10000, 0xe693c32f );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "bbm-c0.bin",    0x000000, 0x40000, 0x695d2019 );
		ROM_LOAD( "bbm-c1.bin",    0x040000, 0x40000, 0x4c7c8bbc );
		ROM_LOAD( "bbm-c2.bin",    0x080000, 0x40000, 0x0700d406 );
		ROM_LOAD( "bbm-c3.bin",    0x0c0000, 0x40000, 0x3c3613af );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* samples */
		/* Does this have a sample rom? */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_bbmanw = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "db_h0-b.rom",  0x00001, 0x40000, 0x567d3709 );
		ROM_LOAD16_BYTE( "db_l0-b.rom",  0x00000, 0x40000, 0xe762c22b );
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "db_sp.rom",    0x0000, 0x10000, 0x6bc1689e );
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "bbm2_c0.bin",  0x000000, 0x40000, 0xe7ce058a );
		ROM_LOAD( "bbm2_c1.bin",  0x080000, 0x40000, 0x636a78a9 );
		ROM_LOAD( "bbm2_c2.bin",  0x100000, 0x40000, 0x9ac2142f );
		ROM_LOAD( "bbm2_c3.bin",  0x180000, 0x40000, 0x47af1750 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );
		ROM_LOAD( "db_w04m.rom",    0x0000, 0x20000, 0x4ad889ed );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_bbmanwj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "bbm2_h0.bin",  0x00001, 0x40000, 0xe1407b91 );
		ROM_LOAD16_BYTE( "bbm2_l0.bin",  0x00000, 0x40000, 0x20873b49 );
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "bbm2sp-b.bin", 0x0000, 0x10000, 0xb8d8108c );
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "bbm2_c0.bin",  0x000000, 0x40000, 0xe7ce058a );
		ROM_LOAD( "bbm2_c1.bin",  0x080000, 0x40000, 0x636a78a9 );
		ROM_LOAD( "bbm2_c2.bin",  0x100000, 0x40000, 0x9ac2142f );
		ROM_LOAD( "bbm2_c3.bin",  0x180000, 0x40000, 0x47af1750 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "bbm2_vo.bin",  0x0000, 0x20000, 0x0ae655ff );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_atompunk = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "bm2-ho-a.9f",  0x00001, 0x40000, 0x7d858682 );
		ROM_LOAD16_BYTE( "bm2-lo-a.9k",  0x00000, 0x40000, 0xc7568031 );
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "db_sp.rom",             0x0000, 0x10000, 0x6bc1689e );
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "bbm2_c0.bin",  0x000000, 0x40000, 0xe7ce058a );
		ROM_LOAD( "bbm2_c1.bin",  0x080000, 0x40000, 0x636a78a9 );
		ROM_LOAD( "bbm2_c2.bin",  0x100000, 0x40000, 0x9ac2142f );
		ROM_LOAD( "bbm2_c3.bin",  0x180000, 0x40000, 0x47af1750 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "db_w04m.rom",           0x0000, 0x20000, 0x4ad889ed );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_quizf1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "qf1-h0-.77",   0x000001, 0x40000, 0x280e3049 );
		ROM_LOAD16_BYTE( "qf1-l0-.79",   0x000000, 0x40000, 0x94588a6f );
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x100000, REGION_USER1, 0 );
		ROM_LOAD16_BYTE( "qf1-h1-.78",   0x000001, 0x80000, 0xc6c2eb2b );/* banked at 80000-8FFFF */
		ROM_LOAD16_BYTE( "qf1-l1-.80",   0x000000, 0x80000, 0x3132c144 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "qf1-sp-.33",   0x0000, 0x10000, 0x0664fa9f );
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "qf1-c0-.81",   0x000000, 0x80000, 0xc26b521e );
		ROM_LOAD( "qf1-c1-.82",   0x080000, 0x80000, 0xdb9d7394 );
		ROM_LOAD( "qf1-c2-.83",   0x100000, 0x80000, 0x0b1460ae );
		ROM_LOAD( "qf1-c3-.84",   0x180000, 0x80000, 0x2d32ff37 );
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "qf1-v0-.30",   0x0000, 0x40000, 0xb8d16e7c );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_riskchal = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "rc_h0.rom",    0x00001, 0x40000, 0x4c9b5344 );
		ROM_LOAD16_BYTE( "rc_l0.rom",    0x00000, 0x40000, 0x0455895a );
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "rc_sp.rom",    0x0000, 0x10000, 0xbb80094e );
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "rc_c0.rom",    0x000000, 0x80000, 0x84d0b907 );
		ROM_LOAD( "rc_c1.rom",    0x080000, 0x80000, 0xcb3784ef );
		ROM_LOAD( "rc_c2.rom",    0x100000, 0x80000, 0x687164d7 );
		ROM_LOAD( "rc_c3.rom",    0x180000, 0x80000, 0xc86be6af );
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "rc_v0.rom",    0x0000, 0x40000, 0xcddac360 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gussun = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "l4_h0.rom",    0x00001, 0x40000, 0x9d585e61 );
		ROM_LOAD16_BYTE( "l4_l0.rom",    0x00000, 0x40000, 0xc7b4c519 );
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "rc_sp.rom",    0x0000, 0x10000, 0xbb80094e );
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "rc_c0.rom",    0x000000, 0x80000, 0x84d0b907 );
		ROM_LOAD( "rc_c1.rom",    0x080000, 0x80000, 0xcb3784ef );
		ROM_LOAD( "rc_c2.rom",    0x100000, 0x80000, 0x687164d7 );
		ROM_LOAD( "rc_c3.rom",    0x180000, 0x80000, 0xc86be6af );
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "rc_v0.rom",    0x0000, 0x40000, 0xcddac360 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_shisen2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 );
		ROM_LOAD16_BYTE( "sis2-ho-.rom", 0x00001, 0x40000, 0x6fae0aea );
		ROM_LOAD16_BYTE( "sis2-lo-.rom", 0x00000, 0x40000, 0x2af25182 );
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 );/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for the audio CPU */
		ROM_LOAD( "sis2-sp-.rom", 0x0000, 0x10000, 0x6fc0ff3a );
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "ic81.rom",     0x000000, 0x80000, 0x5a7cb88f );
		ROM_LOAD( "ic82.rom",     0x080000, 0x80000, 0x54a7852c );
		ROM_LOAD( "ic83.rom",     0x100000, 0x80000, 0x2bd65dc6 );
		ROM_LOAD( "ic84.rom",     0x180000, 0x80000, 0x876d5fdb );
	ROM_END(); }}; 
	
	
	
	static DRIVER_INIT( hasamu )
	{
		irem_cpu_decrypt(0,gunforce_decryption_table);
	}
	
	static DRIVER_INIT( bombrman )
	{
		irem_cpu_decrypt(0,bomberman_decryption_table);
	}
	
	/* Bomberman World executes encrypted code from RAM! */
	static WRITE_HANDLER (bbmanw_ram_write)
	{
		unsigned char *RAM = memory_region(REGION_CPU1);
		RAM[0x0a0c00+offset]=data;
		RAM[0x1a0c00+offset]=dynablaster_decryption_table[data];
	}
	
	static DRIVER_INIT( bbmanw )
	{
		irem_cpu_decrypt(0,dynablaster_decryption_table);
	
		install_mem_write_handler(0, 0xa0c00, 0xa0cff, bbmanw_ram_write);
	}
	
	static DRIVER_INIT( quizf1 )
	{
		irem_cpu_decrypt(0,lethalth_decryption_table);
	}
	
	static DRIVER_INIT( riskchal )
	{
		irem_cpu_decrypt(0,gussun_decryption_table);
	}
	
	static DRIVER_INIT( shisen2 )
	{
		irem_cpu_decrypt(0,shisen2_decryption_table);
	}
	
	
	
	public static GameDriver driver_hasamu	   = new GameDriver("1991"	,"hasamu"	,"m90.java"	,rom_hasamu,null	,machine_driver_m90	,input_ports_hasamu	,init_hasamu	,ROT0	,	"Irem", "Hasamu (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_bbmanw	   = new GameDriver("1991"	,"bbmanw"	,"m90.java"	,rom_bbmanw,null	,machine_driver_bbmanw	,input_ports_bbmanw	,init_bbmanw	,ROT0	,	"Irem", "Bomber Man World (World)", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
	public static GameDriver driver_bbmanwj	   = new GameDriver("1991"	,"bbmanwj"	,"m90.java"	,rom_bbmanwj,driver_bbmanw	,machine_driver_bombrman	,input_ports_bbmanw	,init_bbmanw	,ROT0	,	"Irem", "Bomber Man World (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_bombrman	   = new GameDriver("1992"	,"bombrman"	,"m90.java"	,rom_bombrman,null	,machine_driver_bombrman	,input_ports_bombrman	,init_bombrman	,ROT0	,	"Irem (licensed from Hudson Soft)", "Bomberman (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_dynablsb	   = new GameDriver("1992"	,"dynablsb"	,"m90.java"	,rom_dynablsb,driver_bombrman	,machine_driver_bootleg	,input_ports_bombrman	,null	,ROT0	,	"bootleg", "Dynablaster (bootleg)", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_atompunk	   = new GameDriver("1992"	,"atompunk"	,"m90.java"	,rom_atompunk,driver_bbmanw	,machine_driver_bbmanw	,input_ports_bbmanw	,init_bbmanw	,ROT0	,	"Irem America", "New Atomic Punk - Global Quest (US)", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
	public static GameDriver driver_quizf1	   = new GameDriver("1992"	,"quizf1"	,"m90.java"	,rom_quizf1,null	,machine_driver_quizf1	,input_ports_quizf1	,init_quizf1	,ROT0	,	"Irem", "Quiz F-1 1,2finish", GAME_UNEMULATED_PROTECTION | GAME_IMPERFECT_GRAPHICS | GAME_NO_COCKTAIL )
	public static GameDriver driver_riskchal	   = new GameDriver("1993"	,"riskchal"	,"m90.java"	,rom_riskchal,null	,machine_driver_m90	,input_ports_m97	,init_riskchal	,ROT0	,	"Irem", "Risky Challenge", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_gussun	   = new GameDriver("1993"	,"gussun"	,"m90.java"	,rom_gussun,driver_riskchal	,machine_driver_m90	,input_ports_m97	,init_riskchal	,ROT0	,	"Irem", "Gussun Oyoyo (Japan)", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_shisen2	   = new GameDriver("1993"	,"shisen2"	,"m90.java"	,rom_shisen2,null	,machine_driver_m90	,input_ports_m97	,init_shisen2	,ROT0	,	"Tamtex", "Shisensho II", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
}
