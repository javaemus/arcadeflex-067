/***************************************************************************

	Cinematronics vector hardware

	driver by Aaron Giles

	Special thanks to Neil Bradley, Zonn Moore, and Jeff Mitchell of the
	Retrocade Alliance

	Games supported:
		* Space Wars
		* Barrier
		* Star Hawk
		* Star Castle
		* Tailgunner
		* Rip Off
		* Speed Freak
		* Sundance
		* Warrior
		* Armor Attack
		* Solar Quest
		* Demon
		* War of the Worlds
		* Boxing Bugs

	Known issues:
		* fix Sundance controls

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class cinemat
{
	
	
	
	/*************************************
	 *
	 *	Speed Freak inputs
	 *
	 *************************************/
	
	static UINT8 speedfrk_steer[] = {0xe, 0x6, 0x2, 0x0, 0x3, 0x7, 0xf};
	
	READ16_HANDLER( speedfrk_input_port_1_r )
	{
	    static int last_wheel=0, delta_wheel, last_frame=0, gear=0xe0;
		int val, current_frame;
	
		/* check the fake gear input port and determine the bit settings for the gear */
		if ((input_port_5_r(0) & 0xf0) != 0xf0)
	        gear = input_port_5_r(0) & 0xf0;
	
	    val = (input_port_1_word_r(0, 0) & 0xff00) | gear;
	
		/* add the start key into the mix */
		if (input_port_1_word_r(0, 0) & 0x80)
	        val |= 0x80;
		else
	        val &= ~0x80;
	
		/* and for the cherry on top, we add the scrambled analog steering */
	    current_frame = cpu_getcurrentframe();
	    if (current_frame > last_frame)
	    {
	        /* the shift register is cleared once per 'frame' */
	        delta_wheel = input_port_4_r(0) - last_wheel;
	        last_wheel += delta_wheel;
	        if (delta_wheel > 3)
	            delta_wheel = 3;
	        else if (delta_wheel < -3)
	            delta_wheel = -3;
	    }
	    last_frame = current_frame;
	
	    val |= speedfrk_steer[delta_wheel + 3];
	
		return val;
	}
	
	
	
	/*************************************
	 *
	 *	Boxing Bugs inputs
	 *
	 *************************************/
	
	static READ16_HANDLER( boxingb_input_port_1_r )
	{
		if (cinemat_output_port_r(0,0) & 0x80)
			return ((input_port_4_r(0) & 0x0f) << 12) + input_port_1_word_r(0,0);
		else
			return ((input_port_4_r(0) & 0xf0) << 8)  + input_port_1_word_r(0,0);
	}
	
	
	
	/*************************************
	 *
	 *	Video overlays
	 *
	 *************************************/
	
	OVERLAY_START( starcas_overlay )
		OVERLAY_RECT( 0.0, 0.0, 1.0, 1.0,       MAKE_ARGB(0x24,0x00,0x3c,0xff) )
		OVERLAY_DISK_NOBLEND( 0.5, 0.5, 0.1225, MAKE_ARGB(0x24,0xff,0x20,0x20) )
		OVERLAY_DISK_NOBLEND( 0.5, 0.5, 0.0950, MAKE_ARGB(0x24,0xff,0x80,0x10) )
		OVERLAY_DISK_NOBLEND( 0.5, 0.5, 0.0725, MAKE_ARGB(0x24,0xff,0xff,0x20) )
	OVERLAY_END
	
	
	OVERLAY_START( tailg_overlay )
		OVERLAY_RECT( 0.0, 0.0, 1.0, 1.0, MAKE_ARGB(0x04,0x20,0xff,0xff) )
	OVERLAY_END
	
	
	OVERLAY_START( sundance_overlay )
		OVERLAY_RECT( 0.0, 0.0, 1.0, 1.0, MAKE_ARGB(0x04,0xff,0xff,0x20) )
	OVERLAY_END
	
	
	OVERLAY_START( solarq_overlay )
		OVERLAY_RECT( 0.0, 0.1, 1.0, 1.0, MAKE_ARGB(0x04,0x20,0x20,0xff) )
		OVERLAY_RECT( 0.0, 0.0, 1.0, 0.1, MAKE_ARGB(0x04,0xff,0x20,0x20) )
		OVERLAY_DISK_NOBLEND( 0.5, 0.5, 0.03, MAKE_ARGB(0x04,0xff,0xff,0x20) )
	OVERLAY_END
	
	
	
	/*************************************
	 *
	 *	Main CPU memory handlers
	 *
	 *************************************/
	
	static MEMORY_READ16_START( readmem )
		{ 0x0000, 0x01ff, MRA16_RAM },
		{ 0x8000, 0xffff, MRA16_ROM },
	MEMORY_END
	
	
	static MEMORY_WRITE16_START( writemem )
		{ 0x0000, 0x01ff, MWA16_RAM },
		{ 0x8000, 0xffff, MWA16_ROM },
	MEMORY_END
	
	
	
	/*************************************
	 *
	 *	Main CPU port handlers
	 *
	 *************************************/
	
	static PORT_READ16_START( readport )
		{ CCPU_PORT_IOSWITCHES,   CCPU_PORT_IOSWITCHES+1,   input_port_0_word_r },
		{ CCPU_PORT_IOINPUTS,     CCPU_PORT_IOINPUTS+1,     input_port_1_word_r },
		{ CCPU_PORT_IOOUTPUTS,    CCPU_PORT_IOOUTPUTS+1,    cinemat_output_port_r },
		{ CCPU_PORT_IN_JOYSTICKX, CCPU_PORT_IN_JOYSTICKX+1, input_port_2_word_r },
		{ CCPU_PORT_IN_JOYSTICKY, CCPU_PORT_IN_JOYSTICKY+1, input_port_3_word_r },
	PORT_END
	
	
	static PORT_WRITE16_START( writeport )
		{ CCPU_PORT_IOOUTPUTS,    CCPU_PORT_IOOUTPUTS+1,    cinemat_output_port_w },
	PORT_END
	
	
	
	/*************************************
	 *
	 *	Port definitions
	 *
	 *************************************/
	
	/* switch definitions are all mangled; for ease of use, I created these handy macros */
	
	#define SW7 0x40
	#define SW6 0x02
	#define SW5 0x04
	#define SW4 0x08
	#define SW3 0x01
	#define SW2 0x20
	#define SW1 0x10
	
	#define SW7OFF SW7
	#define SW6OFF SW6
	#define SW5OFF SW5
	#define SW4OFF SW4
	#define SW3OFF SW3
	#define SW2OFF SW2
	#define SW1OFF SW1
	
	#define SW7ON  0
	#define SW6ON  0
	#define SW5ON  0
	#define SW4ON  0
	#define SW3ON  0
	#define SW2ON  0
	#define SW1ON  0
	
	
	static InputPortPtr input_ports_spacewar = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_DIPNAME( SW2|SW1, SW2ON |SW1ON,  "Time" );
		PORT_DIPSETTING( 	   SW2OFF|SW1OFF, "0:45/coin" );
		PORT_DIPSETTING( 	   SW2ON |SW1ON,  "1:00/coin" );
		PORT_DIPSETTING( 	   SW2ON |SW1OFF, "1:30/coin" );
		PORT_DIPSETTING( 	   SW2OFF|SW1ON,  "2:00/coin" );
		PORT_DIPNAME( SW7,	   SW7OFF,		  DEF_STR( "Unknown") );
		PORT_DIPSETTING(	   SW7OFF,		  DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW7ON,		  DEF_STR( "On") );
		PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BITX( 0x0800, IP_ACTIVE_LOW, 0, "Option 0", KEYCODE_0_PAD, IP_JOY_NONE );
		PORT_BITX( 0x0400, IP_ACTIVE_LOW, 0, "Option 5", KEYCODE_5_PAD, IP_JOY_NONE );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BITX( 0x0080, IP_ACTIVE_LOW, 0, "Option 7", KEYCODE_7_PAD, IP_JOY_NONE );
		PORT_BITX( 0x0040, IP_ACTIVE_LOW, 0, "Option 2", KEYCODE_2_PAD, IP_JOY_NONE );
		PORT_BITX( 0x0020, IP_ACTIVE_LOW, 0, "Option 6", KEYCODE_6_PAD, IP_JOY_NONE );
		PORT_BITX( 0x0010, IP_ACTIVE_LOW, 0, "Option 1", KEYCODE_1_PAD, IP_JOY_NONE );
		PORT_BITX( 0x0008, IP_ACTIVE_LOW, 0, "Option 9", KEYCODE_9_PAD, IP_JOY_NONE );
		PORT_BITX( 0x0004, IP_ACTIVE_LOW, 0, "Option 4", KEYCODE_4_PAD, IP_JOY_NONE );
		PORT_BITX( 0x0002, IP_ACTIVE_LOW, 0, "Option 8", KEYCODE_8_PAD, IP_JOY_NONE );
		PORT_BITX( 0x0001, IP_ACTIVE_LOW, 0, "Option 3", KEYCODE_3_PAD, IP_JOY_NONE );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_barrier = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_DIPNAME( SW1, SW1ON,  DEF_STR( "Lives") );
		PORT_DIPSETTING(   SW1ON,  "3" );
		PORT_DIPSETTING(   SW1OFF, "5" );
		PORT_DIPNAME( SW2, SW2OFF, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(   SW2ON,  DEF_STR( "Off") );
		PORT_DIPSETTING(   SW2OFF, DEF_STR( "On") );
		PORT_DIPNAME( SW3, SW3OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW3OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW3ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW4, SW4OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW4OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW4ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW5, SW5OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW5OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW5ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW6, SW6OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW6OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW6ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW7, SW7OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW7OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW7ON,  DEF_STR( "On") );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x0040, IP_ACTIVE_LOW, 0, "Skill C", KEYCODE_C, IP_JOY_NONE );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_PLAYER1 );
		PORT_BITX( 0x0004, IP_ACTIVE_LOW, 0, "Skill B", KEYCODE_B, IP_JOY_NONE );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x0001, IP_ACTIVE_LOW, 0, "Skill A", KEYCODE_A, IP_JOY_NONE );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	/* TODO: 4way or 8way stick? */
	static InputPortPtr input_ports_starhawk = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 2 );
		PORT_DIPNAME( SW7,	   SW7OFF,		  DEF_STR( "Unknown") );
		PORT_DIPSETTING(	   SW7OFF,		  DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW7ON,		  DEF_STR( "On") );
		PORT_BIT ( SW6, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT ( SW5, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( SW4, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( SW3, IP_ACTIVE_LOW, IPT_START1 );
		PORT_DIPNAME( SW2|SW1, SW2OFF|SW1OFF, "Game Time" );
		PORT_DIPSETTING(	   SW2OFF|SW1OFF, "2:00/4:00" );
		PORT_DIPSETTING(	   SW2ON |SW1OFF, "1:30/3:00" );
		PORT_DIPSETTING(	   SW2OFF|SW1ON,  "1:00/2:00" );
		PORT_DIPSETTING(	   SW2ON |SW1ON,  "0:45/1:30" );
	
		PORT_START();  /* input */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER2 );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER1 );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER1 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_starcas = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_SERVICE( SW7,     SW7ON );
		PORT_DIPNAME( SW4|SW3, SW4OFF|SW3OFF, DEF_STR( "Coinage") );
		PORT_DIPSETTING(       SW4ON |SW3OFF, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(       SW4ON |SW3ON,  DEF_STR( "4C_3C") );
		PORT_DIPSETTING(       SW4OFF|SW3OFF, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(       SW4OFF|SW3ON,  DEF_STR( "2C_3C") );
		PORT_DIPNAME( SW2|SW1, SW2OFF|SW1OFF, DEF_STR( "Lives") );
		PORT_DIPSETTING(       SW2OFF|SW1OFF, "3" );
		PORT_DIPSETTING(       SW2ON |SW1OFF, "4" );
		PORT_DIPSETTING(       SW2OFF|SW1ON,  "5" );
		PORT_DIPSETTING(       SW2ON |SW1ON,  "6" );
		PORT_DIPNAME( SW5,     SW5OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW5OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW5ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW6,     SW6OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW6OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW6ON,         DEF_STR( "On") );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_tailg = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_DIPNAME( SW6|SW2|SW1, SW6OFF|SW2OFF|SW1OFF, "Shield Points" );
		PORT_DIPSETTING(		   SW6ON |SW2ON |SW1ON,  "15" );
		PORT_DIPSETTING(		   SW6ON |SW2OFF|SW1ON,  "20" );
		PORT_DIPSETTING(		   SW6ON |SW2ON |SW1OFF, "30" );
		PORT_DIPSETTING(		   SW6ON |SW2OFF|SW1OFF, "40" );
		PORT_DIPSETTING(		   SW6OFF|SW2ON |SW1ON,  "50" );
		PORT_DIPSETTING(		   SW6OFF|SW2OFF|SW1ON,  "60" );
		PORT_DIPSETTING(		   SW6OFF|SW2ON |SW1OFF, "70" );
		PORT_DIPSETTING(		   SW6OFF|SW2OFF|SW1OFF, "80" );
		PORT_DIPNAME( SW3,		   SW3OFF,				 DEF_STR( "Coinage") );
		PORT_DIPSETTING(		   SW3ON,				 DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		   SW3OFF,				 DEF_STR( "1C_1C") );
		PORT_DIPNAME( SW4,		   SW4OFF,				 DEF_STR( "Unknown") );
		PORT_DIPSETTING(		   SW4OFF,				 DEF_STR( "Off") );
		PORT_DIPSETTING(		   SW4ON, 				 DEF_STR( "On") );
		PORT_DIPNAME( SW5,		   SW5OFF,				 DEF_STR( "Unknown") );
		PORT_DIPSETTING(		   SW5OFF,				 DEF_STR( "Off") );
		PORT_DIPSETTING(		   SW5ON, 				 DEF_STR( "On") );
		PORT_DIPNAME( SW7,		   SW7OFF,				 DEF_STR( "Unknown") );
		PORT_DIPSETTING(		   SW7OFF,				 DEF_STR( "Off") );
		PORT_DIPSETTING(		   SW7ON, 				 DEF_STR( "On") );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick X */
		PORT_ANALOG( 0xfff, 0x800, IPT_AD_STICK_X, 100, 50, 0x200, 0xe00 );
	
		PORT_START();  /* analog stick Y */
		PORT_ANALOG( 0xfff, 0x800, IPT_AD_STICK_Y, 100, 50, 0x200, 0xe00 );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_ripoff = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_SERVICE( SW7,	   SW7OFF );
		PORT_DIPNAME( SW6,	   SW6ON,		  "Scores" );
		PORT_DIPSETTING(	   SW6ON,		  "Individual" );
		PORT_DIPSETTING(	   SW6OFF,		  "Combined" );
		PORT_DIPNAME( SW5,	   SW5OFF,		  DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	   SW5ON,		  DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW5OFF,		  DEF_STR( "On") );
		PORT_DIPNAME( SW4|SW3, SW4ON |SW3ON,  DEF_STR( "Coinage") );
		PORT_DIPSETTING(	   SW4ON |SW3OFF, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	   SW4OFF|SW3OFF, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(	   SW4ON |SW3ON,  DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	   SW4OFF|SW3ON,  DEF_STR( "2C_3C") );
		PORT_DIPNAME( SW2|SW1, SW2OFF|SW1OFF, DEF_STR( "Lives") );
		PORT_DIPSETTING(	   SW2ON |SW1OFF, "4" );
		PORT_DIPSETTING(	   SW2OFF|SW1OFF, "8" );
		PORT_DIPSETTING(	   SW2ON |SW1ON,  "12" );
		PORT_DIPSETTING(	   SW2OFF|SW1ON,  "16" );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_speedfrk = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_DIPNAME( SW7,     SW7OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW7OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW7ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW6,     SW6OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW6OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW6ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW5,     SW5OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW5OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW5ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW4,     SW4OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW4OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW4ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW3,     SW3OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW3OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW3ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW2|SW1, SW2OFF|SW1ON,  "Extra Time" );
		PORT_DIPSETTING(       SW2ON |SW1ON,  "69" );
		PORT_DIPSETTING(       SW2ON |SW1OFF, "99" );
		PORT_DIPSETTING(       SW2OFF|SW1ON,  "129" );
		PORT_DIPSETTING(       SW2OFF|SW1OFF, "159" );
	
		PORT_START();  /* inputs */
		PORT_BIT (  0x8000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT (  0x4000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT (  0x2000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT (  0x1000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT (  0x0800, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT (  0x0400, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT (  0x0200, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT (  0x0100, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );/* gas */
		PORT_BIT (  0x0080, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT (  0x0070, IP_ACTIVE_LOW, IPT_UNUSED );/* gear shift, fake below */
		PORT_BIT (  0x000f, IP_ACTIVE_LOW, IPT_UNUSED );/* steering wheel, fake below */
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* fake - steering wheel (in4) */
		PORT_ANALOG( 0xff, 0x00, IPT_DIAL, 100, 1, 0x00, 0xff );
	
		PORT_START();  /* fake - gear shift (in5) */
		PORT_BIT ( 0x0f, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_PLAYER2, "1st gear", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER2, "2nd gear", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2, "3rd gear", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_PLAYER2, "4th gear", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_sundance = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_DIPNAME( SW5,	   SW5OFF,		 DEF_STR( "Unknown") );
		PORT_DIPSETTING(	   SW5OFF,		 DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW5ON, 		 DEF_STR( "On") );
		PORT_DIPNAME( SW6,	   SW6OFF,		 DEF_STR( "Unknown") );
		PORT_DIPSETTING(	   SW6OFF,		 DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW6ON,		 DEF_STR( "On") );
		PORT_DIPNAME( SW7,	   SW7OFF,		 DEF_STR( "Unknown") );
		PORT_DIPSETTING(	   SW7OFF,		 DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW7ON,		 DEF_STR( "On") );
		PORT_DIPNAME( SW4,	   SW4ON,		 DEF_STR( "Coinage") );
		PORT_DIPSETTING(	   SW4ON,		 "1 coin/2 players" );
		PORT_DIPSETTING(	   SW4OFF,		 "2 coins/2 players" );
		PORT_DIPNAME( SW3,	   SW3ON,		 "Language" );
		PORT_DIPSETTING(	   SW3OFF,		 "Japanese" );
		PORT_DIPSETTING(	   SW3ON,		 "English" );
		PORT_DIPNAME( SW2|SW1, SW2OFF|SW1ON, "Time" );
		PORT_DIPSETTING(	   SW2ON |SW1ON,  "0:45/coin" );
		PORT_DIPSETTING(	   SW2OFF|SW1ON,  "1:00/coin" );
		PORT_DIPSETTING(	   SW2ON |SW1OFF, "1:30/coin" );
		PORT_DIPSETTING(	   SW2OFF|SW1OFF, "2:00/coin" );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_UNUSED );/* player 1 motion */
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_UNUSED );/* player 2 motion */
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_UNUSED );/* player 1 motion */
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_UNUSED );/* player 2 motion */
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_UNUSED );/* 2 suns */
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_UNUSED );/* player 1 motion */
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );/* player 2 motion */
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_UNUSED );/* player 1 motion */
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED );/* 4 suns */
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_UNUSED );/* Grid */
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_UNUSED );/* 3 suns */
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_UNUSED );/* player 2 motion */
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_warrior = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_DIPNAME( SW7, SW7OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW7OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW7ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW6, SW6OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW6OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW6ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW5, SW5OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW5OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW5ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW4, SW4OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW4OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW4ON,  DEF_STR( "On") );
		PORT_SERVICE( SW3, SW3ON );
		PORT_DIPNAME( SW2, SW2OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW2OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW2ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW1, SW1ON,  DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW1OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW1ON,  DEF_STR( "On") );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER1 );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER1 );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER1 );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER1 );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2 );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_armora = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_SERVICE( SW7,     SW7ON );
		PORT_DIPNAME( SW5,     SW5ON,         DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(       SW5OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW5ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW4|SW3, SW4OFF|SW3OFF, DEF_STR( "Coinage") );
		PORT_DIPSETTING(       SW4ON |SW3OFF, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(       SW4ON |SW3ON,  DEF_STR( "4C_3C") );
		PORT_DIPSETTING(       SW4OFF|SW3OFF, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(       SW4OFF|SW3ON,  DEF_STR( "2C_3C") );
		PORT_DIPNAME( SW2|SW1, SW2OFF|SW1OFF, DEF_STR( "Lives") );
		PORT_DIPSETTING(       SW2ON |SW1ON,  "2" );
		PORT_DIPSETTING(       SW2OFF|SW1ON,  "3" );
		PORT_DIPSETTING(       SW2ON |SW1OFF, "4" );
		PORT_DIPSETTING(       SW2OFF|SW1OFF, "5" );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW,  IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW,  IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_solarq = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_SERVICE( SW7,	   SW7ON );
		PORT_DIPNAME( SW2,	   SW2OFF,		  DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	   SW2OFF,		  "25 captures" );
		PORT_DIPSETTING(	   SW2ON, 		  "40 captures" );
		PORT_DIPNAME( SW6,	   SW6OFF,		  DEF_STR( "Free_Play") );
		PORT_DIPSETTING(	   SW6OFF,		  DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW6ON,		  DEF_STR( "On") );
		PORT_DIPNAME( SW1|SW3, SW1OFF|SW3OFF, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	   SW3ON |SW1OFF, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	   SW3ON |SW1ON,  DEF_STR( "4C_3C") );
		PORT_DIPSETTING(	   SW3OFF|SW1OFF, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	   SW3OFF|SW1ON,  DEF_STR( "2C_3C") );
		PORT_DIPNAME( SW5|SW4, SW5OFF|SW5OFF, DEF_STR( "Lives") );
		PORT_DIPSETTING(	   SW5OFF|SW4OFF, "2" );
		PORT_DIPSETTING(	   SW5ON |SW4OFF, "3" );
		PORT_DIPSETTING(	   SW5OFF|SW4ON,  "4" );
		PORT_DIPSETTING(	   SW5ON |SW4ON,  "5" );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_START1 );/* also hyperspace */
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_START2 );/* also nova */
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER1 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_demon = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_DIPNAME( SW7,     SW7OFF,        DEF_STR( "Free_Play") );
		PORT_DIPSETTING(       SW7OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW7ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW6,     SW6OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW6OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW6ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW5,     SW5OFF,        DEF_STR( "Unknown") );
		PORT_DIPSETTING(       SW5OFF,        DEF_STR( "Off") );
		PORT_DIPSETTING(       SW5ON,         DEF_STR( "On") );
		PORT_DIPNAME( SW3|SW4, SW3ON |SW4ON,  DEF_STR( "Lives") );
		PORT_DIPSETTING(       SW3ON |SW4ON,  "3");
		PORT_DIPSETTING(       SW3OFF|SW4ON,  "4" );
		PORT_DIPSETTING(       SW3ON |SW4OFF, "5" );
		PORT_DIPSETTING(       SW3OFF|SW4OFF, "6" );
		PORT_DIPNAME( SW2|SW1, SW2OFF|SW1OFF, DEF_STR( "Coinage") );
		PORT_DIPSETTING(       SW2ON |SW1OFF, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(       SW2ON |SW1ON,  DEF_STR( "4C_3C") );
		PORT_DIPSETTING(       SW2OFF|SW1OFF, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(       SW2OFF|SW1ON,  DEF_STR( "2C_3C") );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );/* also mapped to Button 3, player 2 */
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_TILT );
		PORT_SERVICE( 0x0080, IP_ACTIVE_LOW );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER1 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_wotw = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_SERVICE( SW7, SW7OFF );
		PORT_DIPNAME( SW6, SW6OFF, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(   SW6OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW6ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW4, SW4OFF, DEF_STR( "Coinage") );
		PORT_DIPSETTING(   SW4OFF, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(   SW4ON,  DEF_STR( "2C_3C") );
		PORT_DIPNAME( SW2, SW2OFF, DEF_STR( "Lives") );
		PORT_DIPSETTING(   SW2OFF, "3" );
		PORT_DIPSETTING(   SW2ON,  "5" );
		PORT_DIPNAME( SW1, SW1OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW1OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW1ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW3, SW3OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW3OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW3ON,  DEF_STR( "On") );
		PORT_DIPNAME( SW5, SW5OFF, DEF_STR( "Unknown") );
		PORT_DIPSETTING(   SW5OFF, DEF_STR( "Off") );
		PORT_DIPSETTING(   SW5ON,  DEF_STR( "On") );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0x8000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x4000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x2000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortPtr input_ports_boxingb = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* switches */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_SERVICE( SW7,	   SW7OFF );
		PORT_DIPNAME( SW6,	   SW6OFF,		  DEF_STR( "Free_Play") );
		PORT_DIPSETTING(	   SW6OFF,		  DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW6ON,		  DEF_STR( "On") );
		PORT_DIPNAME( SW5,	   SW5ON,		  DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	   SW5OFF,		  DEF_STR( "Off") );
		PORT_DIPSETTING(	   SW5ON,		  DEF_STR( "On") );
		PORT_DIPNAME( SW4,	   SW4ON,		  DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	   SW4ON,		  "30,000" );
		PORT_DIPSETTING(	   SW4OFF,		  "50,000" );
		PORT_DIPNAME( SW3,	   SW3ON,		  DEF_STR( "Lives") );
		PORT_DIPSETTING(	   SW3OFF,		  "3" );
		PORT_DIPSETTING(	   SW3ON,		  "5" );
		PORT_DIPNAME( SW2|SW1, SW2OFF|SW1OFF, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	   SW2ON |SW1OFF, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	   SW2ON |SW1ON,  DEF_STR( "4C_3C") );
		PORT_DIPSETTING(	   SW2OFF|SW1OFF, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	   SW2OFF|SW1ON,  DEF_STR( "2C_3C") );
	
		PORT_START();  /* inputs */
		PORT_BIT ( 0xf000, IP_ACTIVE_HIGH, IPT_UNUSED );/* dial */
		PORT_BIT ( 0x0800, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT ( 0x0400, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT ( 0x0200, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT ( 0x0100, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT ( 0x0080, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT ( 0x0040, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT ( 0x0020, IP_ACTIVE_LOW,  IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT ( 0x0010, IP_ACTIVE_LOW,  IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW,  IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT ( 0x0008, IP_ACTIVE_LOW,  IPT_START1 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW,  IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT ( 0x0004, IP_ACTIVE_LOW,  IPT_START2 );
		PORT_BIT ( 0x0002, IP_ACTIVE_LOW,  IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT ( 0x0001, IP_ACTIVE_LOW,  IPT_BUTTON1 | IPF_PLAYER2 );
	
		PORT_START();  /* analog stick X - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* analog stick Y - unused */
		PORT_BIT ( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();  /* fake (in4) */
		PORT_ANALOG( 0xff, 0x80, IPT_DIAL, 100, 5, 0x00, 0xff );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Machine drivers
	 *
	 *************************************/
	
	/* Note: the CPU speed is somewhat arbitrary as the cycle timings in
	   the core are incomplete. */
	static MACHINE_DRIVER_START( cinemat )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(CCPU, 5000000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
	
		MDRV_FRAMES_PER_SECOND(38)
		MDRV_MACHINE_INIT(cinemat_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_VECTOR | VIDEO_RGB_DIRECT)
		MDRV_SCREEN_SIZE(400, 300)
		MDRV_VISIBLE_AREA(0, 1024, 0, 768)
		MDRV_PALETTE_LENGTH(32768)
	
		MDRV_PALETTE_INIT(cinemat)
		MDRV_VIDEO_START(cinemat)
		MDRV_VIDEO_EOF(cinemat)
		MDRV_VIDEO_UPDATE(vector)
	
		/* sound hardware */
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( spacewar )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(cinemat)
	
		/* video hardware */
		MDRV_VIDEO_UPDATE(spacewar)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, spacewar_samples_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( starcas )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(cinemat)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, starcas_samples_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( ripoff )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(cinemat)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, ripoff_samples_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( warrior )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(cinemat)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0, 1024, 0, 780)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, warrior_samples_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( armora )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(cinemat)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0, 1024, 0, 772)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( solarq )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(cinemat)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, solarq_samples_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( demon )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(cinemat)
		MDRV_IMPORT_FROM(demon_sound)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0, 1024, 0, 800)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( cincolor )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(cinemat)
	
		/* video hardware */
		MDRV_PALETTE_INIT(cinemat_color)
	MACHINE_DRIVER_END
	
	
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadPtr rom_spacewar = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 4k for code */
		ROM_LOAD16_BYTE( "spacewar.1l", 0x8000, 0x0800, 0xedf0fd53 );
		ROM_LOAD16_BYTE( "spacewar.2r", 0x8001, 0x0800, 0x4f21328b );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_barrier = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 4k for code */
		ROM_LOAD16_BYTE( "barrier.t7", 0x8000, 0x0800, 0x7c3d68c8 );
		ROM_LOAD16_BYTE( "barrier.p7", 0x8001, 0x0800, 0xaec142b5 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_starhawk = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 4k for code */
		ROM_LOAD16_BYTE( "u7", 0x8000, 0x0800, 0x376e6c5c );
		ROM_LOAD16_BYTE( "r7", 0x8001, 0x0800, 0xbb71144f );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_starcas = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "starcas3.t7", 0x8000, 0x0800, 0xb5838b5d );
		ROM_LOAD16_BYTE( "starcas3.p7", 0x8001, 0x0800, 0xf6bc2f4d );
		ROM_LOAD16_BYTE( "starcas3.u7", 0x9000, 0x0800, 0x188cd97c );
		ROM_LOAD16_BYTE( "starcas3.r7", 0x9001, 0x0800, 0xc367b69d );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_starcasp = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "starcasp.t7", 0x8000, 0x0800, 0xd2c551a2 );
		ROM_LOAD16_BYTE( "starcasp.p7", 0x8001, 0x0800, 0xbaa4e422 );
		ROM_LOAD16_BYTE( "starcasp.u7", 0x9000, 0x0800, 0x26941991 );
		ROM_LOAD16_BYTE( "starcasp.r7", 0x9001, 0x0800, 0x5dd151e5 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_starcas1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "starcast.t7", 0x8000, 0x0800, 0x65d0a225 );
		ROM_LOAD16_BYTE( "starcast.p7", 0x8001, 0x0800, 0xd8f58d9a );
		ROM_LOAD16_BYTE( "starcast.u7", 0x9000, 0x0800, 0xd4f35b82 );
		ROM_LOAD16_BYTE( "starcast.r7", 0x9001, 0x0800, 0x9fd3de54 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_starcase = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "starcast.t7", 0x8000, 0x0800, 0x65d0a225 );
		ROM_LOAD16_BYTE( "starcast.p7", 0x8001, 0x0800, 0xd8f58d9a );
		ROM_LOAD16_BYTE( "starcast.u7", 0x9000, 0x0800, 0xd4f35b82 );
		ROM_LOAD16_BYTE( "mottoeis.r7", 0x9001, 0x0800, 0xa2c1ed52 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_stellcas = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "starcast.t7", 0x8000, 0x0800, 0x65d0a225 );
		ROM_LOAD16_BYTE( "starcast.p7", 0x8001, 0x0800, 0xd8f58d9a );
		ROM_LOAD16_BYTE( "elttron.u7",  0x9000, 0x0800, 0xd5b44050 );
		ROM_LOAD16_BYTE( "elttron.r7",  0x9001, 0x0800, 0x6f1f261e );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_tailg = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "tgunner.t70", 0x8000, 0x0800, 0x21ec9a04 );
		ROM_LOAD16_BYTE( "tgunner.p70", 0x8001, 0x0800, 0x8d7410b3 );
		ROM_LOAD16_BYTE( "tgunner.t71", 0x9000, 0x0800, 0x2c954ab6 );
		ROM_LOAD16_BYTE( "tgunner.p71", 0x9001, 0x0800, 0x8e2c8494 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_ripoff = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "ripoff.t7", 0x8000, 0x0800, 0x40c2c5b8 );
		ROM_LOAD16_BYTE( "ripoff.p7", 0x8001, 0x0800, 0xa9208afb );
		ROM_LOAD16_BYTE( "ripoff.u7", 0x9000, 0x0800, 0x29c13701 );
		ROM_LOAD16_BYTE( "ripoff.r7", 0x9001, 0x0800, 0x150bd4c8 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_speedfrk = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "speedfrk.t7", 0x8000, 0x0800, 0x3552c03f );
		ROM_LOAD16_BYTE( "speedfrk.p7", 0x8001, 0x0800, 0x4b90cdec );
		ROM_LOAD16_BYTE( "speedfrk.u7", 0x9000, 0x0800, 0x616c7cf9 );
		ROM_LOAD16_BYTE( "speedfrk.r7", 0x9001, 0x0800, 0xfbe90d63 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_sundance = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "sundance.t7", 0x8000, 0x0800, 0xd5b9cb19 );
		ROM_LOAD16_BYTE( "sundance.p7", 0x8001, 0x0800, 0x445c4f20 );
		ROM_LOAD16_BYTE( "sundance.u7", 0x9000, 0x0800, 0x67887d48 );
		ROM_LOAD16_BYTE( "sundance.r7", 0x9001, 0x0800, 0x10b77ebd );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_warrior = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 8k for code */
		ROM_LOAD16_BYTE( "warrior.t7", 0x8000, 0x0800, 0xac3646f9 );
		ROM_LOAD16_BYTE( "warrior.p7", 0x8001, 0x0800, 0x517d3021 );
		ROM_LOAD16_BYTE( "warrior.u7", 0x9000, 0x0800, 0x2e39340f );
		ROM_LOAD16_BYTE( "warrior.r7", 0x9001, 0x0800, 0x8e91b502 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_armora = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 16k for code */
		ROM_LOAD16_BYTE( "ar414le.t6", 0x8000, 0x1000, 0xd7e71f84 );
		ROM_LOAD16_BYTE( "ar414lo.p6", 0x8001, 0x1000, 0xdf1c2370 );
		ROM_LOAD16_BYTE( "ar414ue.u6", 0xa000, 0x1000, 0xb0276118 );
		ROM_LOAD16_BYTE( "ar414uo.r6", 0xa001, 0x1000, 0x229d779f );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_armorap = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 16k for code */
		ROM_LOAD16_BYTE( "ar414le.t6", 0x8000, 0x1000, 0xd7e71f84 );
		ROM_LOAD16_BYTE( "ar414lo.p6", 0x8001, 0x1000, 0xdf1c2370 );
		ROM_LOAD16_BYTE( "armorp.u7",  0xa000, 0x1000, 0x4a86bd8a );
		ROM_LOAD16_BYTE( "armorp.r7",  0xa001, 0x1000, 0xd2dd4eae );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_armorar = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 16k for code */
		ROM_LOAD16_BYTE( "armorr.t7", 0x8000, 0x0800, 0x256d1ed9 );
		ROM_LOAD16_BYTE( "armorr.p7", 0x8001, 0x0800, 0xbf75c158 );
		ROM_LOAD16_BYTE( "armorr.u7", 0x9000, 0x0800, 0xba68331d );
		ROM_LOAD16_BYTE( "armorr.r7", 0x9001, 0x0800, 0xfa14c0b3 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_solarq = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 16k for code */
		ROM_LOAD16_BYTE( "solar.6t", 0x8000, 0x1000, 0x1f3c5333 );
		ROM_LOAD16_BYTE( "solar.6p", 0x8001, 0x1000, 0xd6c16bcc );
		ROM_LOAD16_BYTE( "solar.6u", 0xa000, 0x1000, 0xa5970e5c );
		ROM_LOAD16_BYTE( "solar.6r", 0xa001, 0x1000, 0xb763fff2 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_demon = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 16k for code */
		ROM_LOAD16_BYTE( "demon.7t",  0x8000, 0x1000, 0x866596c1 );
		ROM_LOAD16_BYTE( "demon.7p",  0x8001, 0x1000, 0x1109e2f1 );
		ROM_LOAD16_BYTE( "demon.7u",  0xa000, 0x1000, 0xd447a3c3 );
		ROM_LOAD16_BYTE( "demon.7r",  0xa001, 0x1000, 0x64b515f0 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* 64k for code */
		ROM_LOAD         ( "demon.snd", 0x0000, 0x1000, 0x1e2cc262 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_wotw = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 16k for code */
		ROM_LOAD16_BYTE( "wow_le.t7", 0x8000, 0x1000, 0xb16440f9 );
		ROM_LOAD16_BYTE( "wow_lo.p7", 0x8001, 0x1000, 0xbfdf4a5a );
		ROM_LOAD16_BYTE( "wow_ue.u7", 0xa000, 0x1000, 0x9b5cea48 );
		ROM_LOAD16_BYTE( "wow_uo.r7", 0xa001, 0x1000, 0xc9d3c866 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_boxingb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 32k for code */
		ROM_LOAD16_BYTE( "u1a", 0x8000, 0x1000, 0xd3115b0f );
		ROM_LOAD16_BYTE( "u1b", 0x8001, 0x1000, 0x3a44268d );
		ROM_LOAD16_BYTE( "u2a", 0xa000, 0x1000, 0xc97a9cbb );
		ROM_LOAD16_BYTE( "u2b", 0xa001, 0x1000, 0x98d34ff5 );
		ROM_LOAD16_BYTE( "u3a", 0xc000, 0x1000, 0x5bb3269b );
		ROM_LOAD16_BYTE( "u3b", 0xc001, 0x1000, 0x85bf83ad );
		ROM_LOAD16_BYTE( "u4a", 0xe000, 0x1000, 0x25b51799 );
		ROM_LOAD16_BYTE( "u4b", 0xe001, 0x1000, 0x7f41de6a );
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Driver initialization
	 *
	 *************************************/
	
	static DRIVER_INIT( spacewar )
	{
		ccpu_Config(0, CCPU_MEMSIZE_4K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = spacewar_sound_w;
	}
	
	
	static DRIVER_INIT( barrier )
	{
		ccpu_Config(1, CCPU_MEMSIZE_4K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = 0;
	}
	
	
	static DRIVER_INIT( starhawk )
	{
		ccpu_Config(1, CCPU_MEMSIZE_4K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = 0;
	}
	
	
	static DRIVER_INIT( starcas )
	{
		ccpu_Config(1, CCPU_MEMSIZE_8K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = starcas_sound_w;
		artwork_set_overlay(starcas_overlay);
	}
	
	
	static DRIVER_INIT( tailg )
	{
		ccpu_Config(0, CCPU_MEMSIZE_8K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = 0;
		artwork_set_overlay(tailg_overlay);
	}
	
	
	static DRIVER_INIT( ripoff )
	{
		ccpu_Config(1, CCPU_MEMSIZE_8K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = ripoff_sound_w;
	}
	
	
	static DRIVER_INIT( speedfrk )
	{
		ccpu_Config(0, CCPU_MEMSIZE_8K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = 0;
	
		install_port_read16_handler(0, CCPU_PORT_IOINPUTS, CCPU_PORT_IOINPUTS+1, speedfrk_input_port_1_r);
	}
	
	
	static DRIVER_INIT( sundance )
	{
		ccpu_Config(1, CCPU_MEMSIZE_8K, CCPU_MONITOR_16LEV);
		cinemat_sound_handler = 0;
		artwork_set_overlay(sundance_overlay);
	}
	
	
	static DRIVER_INIT( warrior )
	{
		ccpu_Config(1, CCPU_MEMSIZE_8K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = warrior_sound_w;
	}
	
	
	static DRIVER_INIT( armora )
	{
		ccpu_Config(1, CCPU_MEMSIZE_16K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = 0;
	}
	
	static DRIVER_INIT( armorar )
	{
		ccpu_Config(1, CCPU_MEMSIZE_8K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = 0;
	}
	
	
	static DRIVER_INIT( solarq )
	{
		ccpu_Config(1, CCPU_MEMSIZE_16K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = solarq_sound_w;
		artwork_set_overlay(solarq_overlay);
	}
	
	
	static DRIVER_INIT( demon )
	{
		unsigned char *RAM = memory_region(REGION_CPU2);
	
		ccpu_Config(1, CCPU_MEMSIZE_16K, CCPU_MONITOR_BILEV);
		cinemat_sound_handler = demon_sound_w;
	
		RAM[0x0091]=0xcb;	/* bit 7,a */
		RAM[0x0092]=0x7f;
		RAM[0x0093]=0xc2;	/* jp nz,$0088 */
		RAM[0x0094]=0x88;
		RAM[0x0095]=0x00;
		RAM[0x0096]=0xc3;	/* jp $00fd */
		RAM[0x0097]=0xfd;
		RAM[0x0098]=0x00;
	}
	
	
	static DRIVER_INIT( wotw )
	{
		ccpu_Config(1, CCPU_MEMSIZE_16K, CCPU_MONITOR_WOWCOL);
		cinemat_sound_handler = 0;
	}
	
	
	static DRIVER_INIT( boxingb )
	{
		ccpu_Config(1, CCPU_MEMSIZE_32K, CCPU_MONITOR_WOWCOL);
		cinemat_sound_handler = 0;
	
		install_port_read16_handler(0, CCPU_PORT_IOINPUTS, CCPU_PORT_IOINPUTS+1, boxingb_input_port_1_r);
	}
	
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_spacewar	   = new GameDriver("1978"	,"spacewar"	,"cinemat.java"	,rom_spacewar,null	,machine_driver_spacewar	,input_ports_spacewar	,init_spacewar	,ROT0	,	"Cinematronics", "Space Wars" )
	public static GameDriver driver_barrier	   = new GameDriver("1979"	,"barrier"	,"cinemat.java"	,rom_barrier,null	,machine_driver_cinemat	,input_ports_barrier	,init_barrier	,ROT270	,	"Vectorbeam", "Barrier", GAME_NO_SOUND )
	public static GameDriver driver_starhawk	   = new GameDriver("1981"	,"starhawk"	,"cinemat.java"	,rom_starhawk,null	,machine_driver_cinemat	,input_ports_starhawk	,init_starhawk	,ROT0	,	"Cinematronics", "Star Hawk", GAME_NO_SOUND )
	public static GameDriver driver_starcas	   = new GameDriver("1980"	,"starcas"	,"cinemat.java"	,rom_starcas,null	,machine_driver_starcas	,input_ports_starcas	,init_starcas	,ROT0	,	"Cinematronics", "Star Castle (version 3)" )
	public static GameDriver driver_starcas1	   = new GameDriver("1980"	,"starcas1"	,"cinemat.java"	,rom_starcas1,driver_starcas	,machine_driver_starcas	,input_ports_starcas	,init_starcas	,ROT0	,	"Cinematronics", "Star Castle (older)" )
	public static GameDriver driver_starcasp	   = new GameDriver("1980"	,"starcasp"	,"cinemat.java"	,rom_starcasp,driver_starcas	,machine_driver_starcas	,input_ports_starcas	,init_starcas	,ROT0	,	"Cinematronics", "Star Castle (prototype)" )
	public static GameDriver driver_starcase	   = new GameDriver("1980"	,"starcase"	,"cinemat.java"	,rom_starcase,driver_starcas	,machine_driver_starcas	,input_ports_starcas	,init_starcas	,ROT0	,	"Cinematronics (Mottoeis license)", "Star Castle (Mottoeis)" )
	public static GameDriver driver_stellcas	   = new GameDriver("1980"	,"stellcas"	,"cinemat.java"	,rom_stellcas,driver_starcas	,machine_driver_starcas	,input_ports_starcas	,init_starcas	,ROT0	,	"bootleg", "Stellar Castle (Elettronolo)" )
	public static GameDriver driver_tailg	   = new GameDriver("1979"	,"tailg"	,"cinemat.java"	,rom_tailg,null	,machine_driver_cinemat	,input_ports_tailg	,init_tailg	,ROT0	,	"Cinematronics", "Tailgunner", GAME_NO_SOUND )
	public static GameDriver driver_ripoff	   = new GameDriver("1979"	,"ripoff"	,"cinemat.java"	,rom_ripoff,null	,machine_driver_ripoff	,input_ports_ripoff	,init_ripoff	,ROT0	,	"Cinematronics", "Rip Off" )
	public static GameDriver driver_speedfrk	   = new GameDriver("19??"	,"speedfrk"	,"cinemat.java"	,rom_speedfrk,null	,machine_driver_cinemat	,input_ports_speedfrk	,init_speedfrk	,ROT0	,	"Vectorbeam", "Speed Freak", GAME_NO_SOUND )
	public static GameDriver driver_sundance	   = new GameDriver("1979"	,"sundance"	,"cinemat.java"	,rom_sundance,null	,machine_driver_cinemat	,input_ports_sundance	,init_sundance	,ROT270	,	"Cinematronics", "Sundance", GAME_NO_SOUND | GAME_NOT_WORKING )
	public static GameDriver driver_warrior	   = new GameDriver("1978"	,"warrior"	,"cinemat.java"	,rom_warrior,null	,machine_driver_warrior	,input_ports_warrior	,init_warrior	,ROT0	,	"Vectorbeam", "Warrior" )
	public static GameDriver driver_armora	   = new GameDriver("1980"	,"armora"	,"cinemat.java"	,rom_armora,null	,machine_driver_armora	,input_ports_armora	,init_armora	,ROT0	,	"Cinematronics", "Armor Attack", GAME_NO_SOUND )
	public static GameDriver driver_armorap	   = new GameDriver("1980"	,"armorap"	,"cinemat.java"	,rom_armorap,driver_armora	,machine_driver_armora	,input_ports_armora	,init_armora	,ROT0	,	"Cinematronics", "Armor Attack (prototype)", GAME_NO_SOUND )
	public static GameDriver driver_armorar	   = new GameDriver("1980"	,"armorar"	,"cinemat.java"	,rom_armorar,driver_armora	,machine_driver_armora	,input_ports_armora	,init_armorar	,ROT0	,	"Cinematronics (Rock-ola license)", "Armor Attack (Rock-ola)", GAME_NO_SOUND )
	public static GameDriver driver_solarq	   = new GameDriver("1981"	,"solarq"	,"cinemat.java"	,rom_solarq,null	,machine_driver_solarq	,input_ports_solarq	,init_solarq	,ORIENTATION_FLIP_X	,	"Cinematronics", "Solar Quest" )
	public static GameDriver driver_demon	   = new GameDriver("1982"	,"demon"	,"cinemat.java"	,rom_demon,null	,machine_driver_demon	,input_ports_demon	,init_demon	,ROT0	,	"Rock-ola", "Demon" )
	public static GameDriver driver_wotw	   = new GameDriver("1981"	,"wotw"	,"cinemat.java"	,rom_wotw,null	,machine_driver_cincolor	,input_ports_wotw	,init_wotw	,ROT0	,	"Cinematronics", "War of the Worlds", GAME_IMPERFECT_COLORS | GAME_NO_SOUND )
	public static GameDriver driver_boxingb	   = new GameDriver("1981"	,"boxingb"	,"cinemat.java"	,rom_boxingb,null	,machine_driver_cincolor	,input_ports_boxingb	,init_boxingb	,ROT0	,	"Cinematronics", "Boxing Bugs", GAME_IMPERFECT_COLORS | GAME_NO_SOUND )
}
