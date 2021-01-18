/***************************************************************************

Marine Boy hardware memory map (preliminary)

driver by Zsolt Vasvari

MAIN CPU:

0000-7fff ROM (not all games use the entire region)
8000-87ff RAM
8c18-8c3f sprite RAM (Hoccer only)
8800-8bff video RAM	 \ (contains sprite RAM in invisible regions)
9000-93ff color RAM	 /

read:
a000	  IN0
a800	  IN1
b000	  DSW
b800      IN2/watchdog reset

write:
9800      column scroll
9a00      char palette bank bit 0 (not used by Hoccer)
9c00      char palette bank bit 1 (not used by Hoccer)
a000      NMI interrupt acknowledge/enable
a001      flipy
a002      flipx
b800	  ???


I/0 ports:
write
08        8910  control
09        8910  write

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class marineb
{
	
	
	
	extern unsigned char *marineb_column_scroll;
	
	MACHINE_INIT( espial );
	PALETTE_INIT( espial );
	
	
	
	VIDEO_UPDATE( marineb );
	VIDEO_UPDATE( changes );
	VIDEO_UPDATE( springer );
	VIDEO_UPDATE( hoccer );
	VIDEO_UPDATE( hopprobo );
	
	
	static MACHINE_INIT( marineb )
	{
		marineb_active_low_flipscreen = 0;
		machine_init_espial();
	}
	
	static MACHINE_INIT( springer )
	{
		marineb_active_low_flipscreen = 1;
		machine_init_espial();
	}
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x8bff, MRA_RAM ),
		new Memory_ReadAddress( 0x9000, 0x93ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa000, input_port_0_r ),
		new Memory_ReadAddress( 0xa800, 0xa800, input_port_1_r ),
		new Memory_ReadAddress( 0xb000, 0xb000, input_port_2_r ),
		new Memory_ReadAddress( 0xb800, 0xb800, input_port_3_r ),  /* also watchdog */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0x8800, 0x8bff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x8c00, 0x8c3f, MWA_RAM, spriteram ),  /* Hoccer only */
		new Memory_WriteAddress( 0x9000, 0x93ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0x9800, 0x9800, MWA_RAM, marineb_column_scroll ),
		new Memory_WriteAddress( 0x9a00, 0x9a00, marineb_palbank0_w ),
		new Memory_WriteAddress( 0x9c00, 0x9c00, marineb_palbank1_w ),
		new Memory_WriteAddress( 0xa000, 0xa000, interrupt_enable_w ),
		new Memory_WriteAddress( 0xa001, 0xa001, marineb_flipscreen_y_w ),
		new Memory_WriteAddress( 0xa002, 0xa002, marineb_flipscreen_x_w ),
		new Memory_WriteAddress( 0xb800, 0xb800, MWA_NOP ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_WritePort marineb_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x08, 0x08, AY8910_control_port_0_w ),
		new IO_WritePort( 0x09, 0x09, AY8910_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort wanted_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, AY8910_control_port_0_w ),
		new IO_WritePort( 0x01, 0x01, AY8910_write_port_0_w ),
		new IO_WritePort( 0x02, 0x02, AY8910_control_port_1_w ),
		new IO_WritePort( 0x03, 0x03, AY8910_write_port_1_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_marineb = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x3c, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x3c, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x1c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	  //PORT_DIPSETTING(    0x04, "???" );
	  //PORT_DIPSETTING(    0x08, "???" );
	  //PORT_DIPSETTING(    0x0c, "???" );
	  //PORT_DIPSETTING(    0x10, "???" );
	  //PORT_DIPSETTING(    0x14, "???" );
	  //PORT_DIPSETTING(    0x18, "???" );
		PORT_DIPSETTING(    0x1c, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "20000 50000" );
		PORT_DIPSETTING(    0x20, "40000 70000" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2);
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_changes = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x3c, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x3c, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	  //PORT_DIPSETTING(    0x04, "???" );
	  //PORT_DIPSETTING(    0x08, "???" );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x10, 0x00, "1st Bonus Life" );
		PORT_DIPSETTING(    0x00, "20000" );
		PORT_DIPSETTING(    0x10, "40000" );
		PORT_DIPNAME( 0x20, 0x00, "2nd Bonus Life" );
		PORT_DIPSETTING(    0x00, "50000" );
		PORT_DIPSETTING(    0x20, "100000" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_hoccer = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x3c, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x3c, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Unknown") );  /* difficulty maybe? */
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPSETTING(    0x04, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_DIPSETTING(    0x30, "6" );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
	    PORT_DIPSETTING(    0xc0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	    PORT_DIPSETTING(    0x40, DEF_STR( "1C_2C") );
	    PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") );
	
		PORT_START(); 	/* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2);
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_wanted = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW,  IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_COIN1 );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0xf0, 0x10, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, " A 3C/1C  B 3C/1C" );
		PORT_DIPSETTING(    0xe0, " A 3C/1C  B 1C/2C" );
		PORT_DIPSETTING(    0xf0, " A 3C/1C  B 1C/4C" );
		PORT_DIPSETTING(    0x20, " A 2C/1C  B 2C/1C" );
		PORT_DIPSETTING(    0xd0, " A 2C/1C  B 1C/1C" );
		PORT_DIPSETTING(    0x70, " A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0xb0, " A 2C/1C  B 1C/5C" );
		PORT_DIPSETTING(    0xc0, " A 2C/1C  B 1C/6C" );
		PORT_DIPSETTING(    0x60, " A 1C/1C  B 4C/1C" );
		PORT_DIPSETTING(    0x50, " A 1C/1C  B 2C/1C" );
		PORT_DIPSETTING(    0x10, " A 1C/1C  B 1C/1C" );
		PORT_DIPSETTING(    0x30, " A 1C/2C  B 1C/2C" );
		PORT_DIPSETTING(    0xa0, " A 1C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x80, " A 1C/1C  B 1C/5C" );
		PORT_DIPSETTING(    0x90, " A 1C/1C  B 1C/6C" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_bcruzm12 = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW,  IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_COIN1 );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x03, 0x01, "2nd Bonus Life" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x01, "60000" );
		PORT_DIPSETTING(    0x02, "80000" );
		PORT_DIPSETTING(    0x03, "100000" );
		PORT_DIPNAME( 0x0c, 0x04, "1st Bonus Life" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x04, "30000" );
		PORT_DIPSETTING(    0x08, "40000" );
		PORT_DIPSETTING(    0x0c, "50000" );
		PORT_DIPNAME( 0xf0, 0x10, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x40, " A 3C/1C  B 3C/1C" );
		PORT_DIPSETTING(    0xe0, " A 3C/1C  B 1C/2C" );
		PORT_DIPSETTING(    0xf0, " A 3C/1C  B 1C/4C" );
		PORT_DIPSETTING(    0x20, " A 2C/1C  B 2C/1C" );
		PORT_DIPSETTING(    0xd0, " A 2C/1C  B 1C/1C" );
		PORT_DIPSETTING(    0x70, " A 2C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0xb0, " A 2C/1C  B 1C/5C" );
		PORT_DIPSETTING(    0xc0, " A 2C/1C  B 1C/6C" );
		PORT_DIPSETTING(    0x60, " A 1C/1C  B 4C/5C" );
		PORT_DIPSETTING(    0x50, " A 1C/1C  B 2C/3C" );
		PORT_DIPSETTING(    0x10, " A 1C/1C  B 1C/1C" );
		PORT_DIPSETTING(    0x30, " A 1C/2C  B 1C/2C" );
		PORT_DIPSETTING(    0xa0, " A 1C/1C  B 1C/3C" );
		PORT_DIPSETTING(    0x80, " A 1C/1C  B 1C/5C" );
		PORT_DIPSETTING(    0x90, " A 1C/1C  B 1C/6C" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout marineb_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		512,	/* 512 characters */
		2,	    /* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3 },	/* bits are packed in groups of four */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8	/* every char takes 16 bytes */
	);
	
	static GfxLayout wanted_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		2,	    /* 2 bits per pixel */
		new int[] { 4, 0 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3 },	/* bits are packed in groups of four */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8	/* every char takes 16 bytes */
	);
	
	static GfxLayout hopprobo_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		2,	    /* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3 },	/* bits are packed in groups of four */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8	/* every char takes 16 bytes */
	);
	
	static GfxLayout marineb_small_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	    /* 64 sprites */
		2,	    /* 2 bits per pixel */
		new int[] { 0, 256*32*8 },	/* the two bitplanes are separated */
		new int[] {     0,     1,     2,     3,     4,     5,     6,     7,
		  8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] {  0*8,  1*8,  2*8,  3*8,  4*8,  5*8,  6*8,  7*8,
		  16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	
	static GfxLayout marineb_big_spritelayout = new GfxLayout
	(
		32,32,	/* 32*32 sprites */
		64,	    /* 64 sprites */
		2,	    /* 2 bits per pixel */
		new int[] { 0, 256*32*8 },	/* the two bitplanes are separated */
		new int[] {      0,      1,      2,      3,      4,      5,      6,      7,
		   8*8+0,  8*8+1,  8*8+2,  8*8+3,  8*8+4,  8*8+5,  8*8+6,  8*8+7,
		  32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
		  40*8+0, 40*8+1, 40*8+2, 40*8+3, 40*8+4, 40*8+5, 40*8+6, 40*8+7 },
		new int[] {  0*8,  1*8,  2*8,  3*8,  4*8,  5*8,  6*8,  7*8,
		  16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8,
		  64*8, 65*8, 66*8, 67*8, 68*8, 69*8, 70*8, 71*8,
		  80*8, 81*8, 82*8, 83*8, 84*8, 85*8, 86*8, 87*8 },
		4*32*8	/* every sprite takes 128 consecutive bytes */
	);
	
	static GfxLayout changes_small_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	    /* 64 sprites */
		2,	    /* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] {      0,      1,      2,      3,  8*8+0,  8*8+1,  8*8+2,  8*8+3,
		  16*8+0, 16*8+1, 16*8+2, 16*8+3, 24*8+0, 24*8+1, 24*8+2, 24*8+3 },
		new int[] {  0*8,  1*8,  2*8,  3*8,  4*8,  5*8,  6*8,  7*8,
		  32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout changes_big_spritelayout = new GfxLayout
	(
		32,32,	/* 32*3 sprites */
		16,	    /* 32 sprites */
		2,	    /* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] {      0,      1,      2,      3,  8*8+0,  8*8+1,  8*8+2,  8*8+3,
		  16*8+0, 16*8+1, 16*8+2, 16*8+3, 24*8+0, 24*8+1, 24*8+2, 24*8+3,
		  64*8+0, 64*8+1, 64*8+2, 64*8+3, 72*8+0, 72*8+1, 72*8+2, 72*8+3,
		  80*8+0, 80*8+1, 80*8+2, 80*8+3, 88*8+0, 88*8+1, 88*8+2, 88*8+3 },
		new int[] {   0*8,   1*8,   2*8,   3*8,   4*8,   5*8,   6*8,   7*8,
		   32*8,  33*8,  34*8,  35*8,  36*8,  37*8,  38*8,  39*8,
		  128*8, 129*8, 130*8, 131*8, 132*8, 133*8, 134*8, 135*8,
		  160*8, 161*8, 162*8, 163*8, 164*8, 165*8, 166*8, 167*8 },
		4*64*8	/* every sprite takes 256 consecutive bytes */
	);
	
	
	static GfxDecodeInfo marineb_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, marineb_charlayout,          0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0000, marineb_small_spritelayout,  0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0000, marineb_big_spritelayout,    0, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo wanted_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, wanted_charlayout,           0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0000, marineb_small_spritelayout,  0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0000, marineb_big_spritelayout,    0, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo changes_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, marineb_charlayout,          0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0000, changes_small_spritelayout,  0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0x1000, changes_big_spritelayout,    0, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo hoccer_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, marineb_charlayout,          0, 16 ),	/* no palette banks */
		new GfxDecodeInfo( REGION_GFX2, 0x0000, changes_small_spritelayout,  0, 16 ),	/* no palette banks */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo hopprobo_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, hopprobo_charlayout,         0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0000, marineb_small_spritelayout,  0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0000, marineb_big_spritelayout,    0, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static AY8910interface marineb_ay8910_interface = new AY8910interface
	(
		1,	/* 1 chip */
		1500000,	/* 1.5 MHz ? */
		new int[] { 50 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static AY8910interface wanted_ay8910_interface = new AY8910interface
	(
		2,	/* 2 chips */
		1500000,	/* 1.5 MHz ? */
		new int[] { 25, 25 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	static MACHINE_DRIVER_START( marineb )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", Z80, 3072000)	/* 3.072 MHz */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(0,marineb_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(5000)	/* frames per second, vblank duration */
	
		MDRV_MACHINE_INIT(marineb)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(marineb_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(espial)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(marineb)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("8910", AY8910, marineb_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( changes )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(marineb)
	
		/* video hardware */
		MDRV_GFXDECODE(changes_gfxdecodeinfo)
		MDRV_VIDEO_UPDATE(changes)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( springer )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(marineb)
		MDRV_MACHINE_INIT(springer)
	
		/* video hardware */
		MDRV_VIDEO_UPDATE(springer)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( hoccer )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(marineb)
	
		/* video hardware */
		MDRV_GFXDECODE(hoccer_gfxdecodeinfo)
		MDRV_VIDEO_UPDATE(hoccer)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( wanted )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(marineb)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(0,wanted_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		/* video hardware */
		MDRV_GFXDECODE(wanted_gfxdecodeinfo)
		MDRV_VIDEO_UPDATE(springer)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("8910", AY8910, wanted_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( hopprobo )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(marineb)
	
		/* video hardware */
		MDRV_GFXDECODE(hopprobo_gfxdecodeinfo)
		MDRV_VIDEO_UPDATE(hopprobo)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( bcruzm12 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3072000)	/* 3.072 MHz  */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(0,wanted_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(5000)
		MDRV_MACHINE_INIT(springer)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(wanted_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
		MDRV_PALETTE_INIT(espial)
	
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(springer)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, wanted_ay8910_interface)
	MACHINE_DRIVER_END
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_marineb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "marineb.1",     0x0000, 0x1000, 0x661d6540 );
		ROM_LOAD( "marineb.2",     0x1000, 0x1000, 0x922da17f );
		ROM_LOAD( "marineb.3",     0x2000, 0x1000, 0x820a235b );
		ROM_LOAD( "marineb.4",     0x3000, 0x1000, 0xa157a283 );
		ROM_LOAD( "marineb.5",     0x4000, 0x1000, 0x9ffff9c0 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "marineb.6",     0x0000, 0x2000, 0xee53ec2e );
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "marineb.8",     0x0000, 0x2000, 0xdc8bc46c );
		ROM_LOAD( "marineb.7",     0x2000, 0x2000, 0x9d2e19ab );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "marineb.1b",    0x0000, 0x0100, 0xf32d9472 );/* palette low 4 bits */
		ROM_LOAD( "marineb.1c",    0x0100, 0x0100, 0x93c69d3e );/* palette high 4 bits */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_changes = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "changes.1",     0x0000, 0x1000, 0x56f83813 );
		ROM_LOAD( "changes.2",     0x1000, 0x1000, 0x0e627f0b );
		ROM_LOAD( "changes.3",     0x2000, 0x1000, 0xff8291e9 );
		ROM_LOAD( "changes.4",     0x3000, 0x1000, 0xa8e9aa22 );
		ROM_LOAD( "changes.5",     0x4000, 0x1000, 0xf4198e9e );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "changes.7",     0x0000, 0x2000, 0x2204194e );
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "changes.6",     0x0000, 0x2000, 0x985c9db4 );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "changes.1b",    0x0000, 0x0100, 0xf693c153 );/* palette low 4 bits */
		ROM_LOAD( "changes.1c",    0x0100, 0x0100, 0xf8331705 );/* palette high 4 bits */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_looper = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "changes.1",     0x0000, 0x1000, 0x56f83813 );
		ROM_LOAD( "changes.2",     0x1000, 0x1000, 0x0e627f0b );
		ROM_LOAD( "changes.3",     0x2000, 0x1000, 0xff8291e9 );
		ROM_LOAD( "changes.4",     0x3000, 0x1000, 0xa8e9aa22 );
		ROM_LOAD( "changes.5",     0x4000, 0x1000, 0xf4198e9e );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "looper_7.bin",  0x0000, 0x2000, 0x71a89975 );
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "looper_6.bin",  0x0000, 0x2000, 0x1f3f70c2 );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "changes.1b",    0x0000, 0x0100, 0xf693c153 );/* palette low 4 bits */
		ROM_LOAD( "changes.1c",    0x0100, 0x0100, 0xf8331705 );/* palette high 4 bits */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_springer = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "springer.1",    0x0000, 0x1000, 0x0794103a );
		ROM_LOAD( "springer.2",    0x1000, 0x1000, 0xf4aecd9a );
		ROM_LOAD( "springer.3",    0x2000, 0x1000, 0x2f452371 );
		ROM_LOAD( "springer.4",    0x3000, 0x1000, 0x859d1bf5 );
		ROM_LOAD( "springer.5",    0x4000, 0x1000, 0x72adbbe3 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "springer.6",    0x0000, 0x1000, 0x6a961833 );
		ROM_LOAD( "springer.7",    0x1000, 0x1000, 0x95ab8fc0 );
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "springer.8",    0x0000, 0x1000, 0xa54bafdc );
								/* 0x1000-0x1fff empty for my convinience */
		ROM_LOAD( "springer.9",    0x2000, 0x1000, 0xfa302775 );
								/* 0x3000-0x3fff empty for my convinience */
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "1b.vid",        0x0000, 0x0100, 0xa2f935aa );/* palette low 4 bits */
		ROM_LOAD( "1c.vid",        0x0100, 0x0100, 0xb95421f4 );/* palette high 4 bits */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hoccer = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "hr1.cpu",       0x0000, 0x2000, 0x12e96635 );
		ROM_LOAD( "hr2.cpu",       0x2000, 0x2000, 0xcf1fc328 );
		ROM_LOAD( "hr3.cpu",       0x4000, 0x2000, 0x048a0659 );
		ROM_LOAD( "hr4.cpu",       0x6000, 0x2000, 0x9a788a2c );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "hr.d",          0x0000, 0x2000, 0xd33aa980 );
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "hr.c",          0x0000, 0x2000, 0x02808294 );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "hr.1b",         0x0000, 0x0100, 0x896521d7 );/* palette low 4 bits */
		ROM_LOAD( "hr.1c",         0x0100, 0x0100, 0x2efdd70b );/* palette high 4 bits */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hoccer2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "hr.1",          0x0000, 0x2000, 0x122d159f );
		ROM_LOAD( "hr.2",          0x2000, 0x2000, 0x48e1efc0 );
		ROM_LOAD( "hr.3",          0x4000, 0x2000, 0x4e67b0be );
		ROM_LOAD( "hr.4",          0x6000, 0x2000, 0xd2b44f58 );
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "hr.d",          0x0000, 0x2000, 0xd33aa980 );
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "hr.c",          0x0000, 0x2000, 0x02808294 );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "hr.1b",         0x0000, 0x0100, 0x896521d7 );/* palette low 4 bits */
		ROM_LOAD( "hr.1c",         0x0100, 0x0100, 0x2efdd70b );/* palette high 4 bits */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_wanted = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );      /* 64k for code */
		ROM_LOAD( "prg-1",		   0x0000, 0x2000, 0x2dd90aed );
		ROM_LOAD( "prg-2",		   0x2000, 0x2000, 0x67ac0210 );
		ROM_LOAD( "prg-3",		   0x4000, 0x2000, 0x373c7d82 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "vram-1",		   0x0000, 0x2000, 0xc4226e54 );
		ROM_LOAD( "vram-2",		   0x2000, 0x2000, 0x2a9b1e36 );
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "obj-a",		   0x0000, 0x2000, 0x90b60771 );
		ROM_LOAD( "obj-b",		   0x2000, 0x2000, 0xe14ee689 );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "wanted.k7",	   0x0000, 0x0100, 0x2ba90a00 );/* palette low 4 bits */
		ROM_LOAD( "wanted.k6",	   0x0100, 0x0100, 0xa93d87cc );/* palette high 4 bits */
	ROM_END(); }}; 
	
	/*
	Battle Cruiser M12
	(C) Sigma Enterprises, Inc. 1983
	
	PCB Number: F-005A
	
	CPU: NEC D780C (Z80)
	SND 2 x AY-3-8912
	XTAL: 12.000MHz
	DIPS: 2 x 8 position
	
	All roms type 2764
	Both proms type MB7052 (compatible to 82s129)
	RAM: 1 x 8416, 1 x AM9122, 2 x D2125, 4 x M5L2114
	
	PCB Layout:
	
	       NECD780C                      D-84_4  D-84_5  D-84_6  D-84_7
	
	8416  D-84_1  D-84_2  D-84_3                  D2125    AM9122        12.000MHz
	                                              D2125
	                                              M5L2114
	                                              M5L2114
	                                              M5L2114
	      SW1                                     M5L2114
	      SW2
	AY-3-8912    AY-3-8912                                             BCM12COL.K7
	                                                                   BCM12COL.K6
	*/
	
	static RomLoadPtr rom_bcruzm12 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );      /* 64k for code */
		ROM_LOAD( "d-84_3.bin",		   0x0000, 0x2000, 0x132baa3d );
		ROM_LOAD( "d-84_2.bin",		   0x2000, 0x2000, 0x1a788d1f );
		ROM_LOAD( "d-84_1.bin",		   0x4000, 0x2000, 0x9d5b3017 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "d-84_5.bin",		   0x0000, 0x2000, 0x2e963f6a );
		ROM_LOAD( "d-84_4.bin",		   0x2000, 0x2000, 0xfe186459 );
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "d-84_7.bin",		   0x0000, 0x2000, 0xa5be90ef );
		ROM_LOAD( "d-84_6.bin",		   0x2000, 0x2000, 0x1337dc01 );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "bcm12col.k7",	   0x0000, 0x0100, 0xbf4f2671 );/* palette low 4 bits */
		ROM_LOAD( "bcm12col.k6",	   0x0100, 0x0100, 0x59f955f6 );/* palette high 4 bits */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hopprobo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* 64k for code */
		ROM_LOAD( "hopper01.3k",   0x0000, 0x1000, 0xfd7935c0 );
		ROM_LOAD( "hopper02.3l",   0x1000, 0x1000, 0xdf1a479a );
		ROM_LOAD( "hopper03.3n",   0x2000, 0x1000, 0x097ac2a7 );
		ROM_LOAD( "hopper04.3p",   0x3000, 0x1000, 0x0f4f3ca8 );
		ROM_LOAD( "hopper05.3r",   0x4000, 0x1000, 0x9d77a37b );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "hopper06.5c",   0x0000, 0x2000, 0x68f79bc8 );
		ROM_LOAD( "hopper07.5d",   0x2000, 0x1000, 0x33d82411 );
		ROM_RELOAD(				   0x3000, 0x1000 );
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "hopper08.6f",   0x0000, 0x2000, 0x06d37e64 );
		ROM_LOAD( "hopper09.6k",   0x2000, 0x2000, 0x047921c7 );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "7052hop.1b",    0x0000, 0x0100, 0x94450775 );/* palette low 4 bits */
		ROM_LOAD( "7052hop.1c",    0x0100, 0x0100, 0xa76bbd51 );/* palette high 4 bits */
	ROM_END(); }}; 
	
	
	/*    year  name      parent   machine   inputs */
	public static GameDriver driver_marineb	   = new GameDriver("1982"	,"marineb"	,"marineb.java"	,rom_marineb,null	,machine_driver_marineb	,input_ports_marineb	,null	,ROT0	,	"Orca", "Marine Boy" )
	public static GameDriver driver_changes	   = new GameDriver("1982"	,"changes"	,"marineb.java"	,rom_changes,null	,machine_driver_changes	,input_ports_changes	,null	,ROT0	,	"Orca", "Changes" )
	public static GameDriver driver_looper	   = new GameDriver("1982"	,"looper"	,"marineb.java"	,rom_looper,driver_changes	,machine_driver_changes	,input_ports_changes	,null	,ROT0	,	"Orca", "Looper" )
	public static GameDriver driver_springer	   = new GameDriver("1982"	,"springer"	,"marineb.java"	,rom_springer,null	,machine_driver_springer	,input_ports_marineb	,null	,ROT270	,	"Orca", "Springer" )
	public static GameDriver driver_hoccer	   = new GameDriver("1983"	,"hoccer"	,"marineb.java"	,rom_hoccer,null	,machine_driver_hoccer	,input_ports_hoccer	,null	,ROT90	,	"Eastern Micro Electronics, Inc.", "Hoccer (set 1)" )
	public static GameDriver driver_hoccer2	   = new GameDriver("1983"	,"hoccer2"	,"marineb.java"	,rom_hoccer2,driver_hoccer	,machine_driver_hoccer	,input_ports_hoccer	,null	,ROT90	,	"Eastern Micro Electronics, Inc.", "Hoccer (set 2)" )	/* earlier */
	public static GameDriver driver_bcruzm12	   = new GameDriver("1983"	,"bcruzm12"	,"marineb.java"	,rom_bcruzm12,null	,machine_driver_bcruzm12	,input_ports_bcruzm12	,null	,ROT90	,	"Sigma Enterprises Inc.", "Battle Cruiser M-12" )
	public static GameDriver driver_hopprobo	   = new GameDriver("1983"	,"hopprobo"	,"marineb.java"	,rom_hopprobo,null	,machine_driver_hopprobo	,input_ports_marineb	,null	,ROT90	,	"Sega", "Hopper Robo" )
	public static GameDriver driver_wanted	   = new GameDriver("1984"	,"wanted"	,"marineb.java"	,rom_wanted,null	,machine_driver_wanted	,input_ports_wanted	,null	,ROT90	,	"Sigma Enterprises Inc.", "Wanted" )
}
