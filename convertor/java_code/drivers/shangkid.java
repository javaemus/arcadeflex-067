/*
Dynamic Ski
(c)1984 Taiyo

Dynamic Ski runs on a single Z80.  It has the same graphics format as the
newer Taiyo games.

The game has some minor priority glitches.

---------------------------------------------------------------------------

Chinese Hero (developed by Taiyo)
(c)1984 Taiyo

Chinese Hero hardware differs only slightly from Shanghai Kid:
- sprites have 3 bitplanes instead of 2
- videoram attributes for the tilemap don't include xflip
- no protection

---------------------------------------------------------------------------

Shanghai Kid / (Hokuha Syourin) Hiryu no Ken
(c)1985 Nihon Game (distributed by Taito)

	3 Z-80A CPU
	1 AY-3-8910
	1 XTAL 18.432MHz

Also distributed with Data East and Memetron license.

Two board set CPU/sound & video.

There is a 1.5" by 2" by 4" black epoxy block that has an external battery.
The block is connected to the PCB by a 40 pin DIP socket labeled IC30.
There is a small smt IC on the video board with the numbers ground off.

---------------------------------------------------------------------------

Some company history:

Nihon Game changed their name to Culture Brain.

Games by Nihon Game/Culture Brain:
	1982 Monster Zero
	1983 Space Hunter
	1984 Chinese Hero
	1985 Hokuha Syourin Hiryuu no Ken / Shanghai Kid
	1986 Super Chinese (Nintendo Vs. System)
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class shangkid
{
	
	/* from vidhrdw/shangkid.c */
	extern UINT8 *shangkid_videoreg;
	
	VIDEO_START( shangkid );
	VIDEO_UPDATE( shangkid );
	
	PALETTE_INIT( dynamski );
	VIDEO_UPDATE( dynamski );
	
	/***************************************************************************************/
	
	static data8_t bbx_sound_enable;
	static data8_t bbx_AY8910_control;
	static data8_t sound_latch;
	static data8_t *shareram;
	
	/***************************************************************************************/
	
	static DACinterface dac_interface = new DACinterface(
		1,
		new int[] { MIXER(50,50) }
	);
	
	static AY8910interface ay8910_interface = new AY8910interface(
		1,	/* number of chips */
		2000000, /* 2 MHz? */
		new int[] { 10 }, /* volume */
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	/***************************************************************************************/
	
	static DRIVER_INIT( chinhero )
	{
		shangkid_gfx_type = 0;
	}
	
	static DRIVER_INIT( shangkid )
	{
		shangkid_gfx_type = 1;
	}
	
	/***************************************************************************************/
	
	public static WriteHandlerPtr shangkid_maincpu_bank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_setbank( 1,&memory_region(REGION_CPU1)[(data&1)?0x10000:0x8000] );
	} };
	
	public static WriteHandlerPtr shangkid_bbx_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_set_halt_line( 1, data?0:1 );
	} };
	
	public static WriteHandlerPtr shangkid_cpu_reset_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if( data == 0 )
		{
			cpu_set_reset_line(1,PULSE_LINE);
		}
		else if( data == 1 )
		{
			cpu_set_reset_line(0,PULSE_LINE);
		}
	} };
	
	public static WriteHandlerPtr shangkid_sound_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		bbx_sound_enable = data;
	} };
	
	public static WriteHandlerPtr shangkid_bbx_AY8910_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		bbx_AY8910_control = data;
		AY8910_control_port_0_w( offset, data );
	} };
	
	public static WriteHandlerPtr shangkid_bbx_AY8910_write_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		switch( bbx_AY8910_control )
		{
		case 0x0e:
			if( bbx_sound_enable )
			{
				if( data == 0x01 )
				{
					/* 0->1 transition triggers interrupt on Sound CPU */
					cpu_set_irq_line( 2, 0, HOLD_LINE );
				}
			}
			else
			{
				cpu_setbank( 2,&memory_region( REGION_CPU3 )[data?0x0000:0x10000] );
			}
			break;
	
		case 0x0f:
			sound_latch = data;
			break;
	
		default:
			AY8910_write_port_0_w( offset, data );
			break;
		}
	} };
	
	/***************************************************************************************/
	
	public static ReadHandlerPtr shangkid_soundlatch_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return sound_latch;
	} };
	
	/***************************************************************************************/
	
	public static WriteHandlerPtr shareram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		shareram[offset] = data;
	} };
	
	public static ReadHandlerPtr shareram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return shareram[offset];
	} };
	
	/***************************************************************************************/
	
	static GfxLayout shangkid_char_layout = new GfxLayout(
		8,8,
		RGN_FRAC(1,1),
		2,
		new int[] { 0,4 },
		new int[] { 0,1,2,3,8,9,10,11 },
		new int[] { 0*16,1*16,2*16,3*16,4*16,5*16,6*16,7*16 },
		8*16
	);
	
	static GfxLayout shangkid_sprite_layout = new GfxLayout(
		16,16,
		RGN_FRAC(1,1),
		2,
		new int[] { 0,4 },
		new int[] {
			0,1,2,3,8,9,10,11,
			128+0,128+1,128+2,128+3,128+8,128+9,128+10,128+11
		},
		new int[] {
			0*16,1*16,2*16,3*16,4*16,5*16,6*16,7*16,
			256+0*16,256+1*16,256+2*16,256+3*16,256+4*16,256+5*16,256+6*16,256+7*16
		},
		8*0x40
	);
	
	static GfxLayout chinhero_sprite_layout1 = new GfxLayout(
		16,16,
		0x80,
		3,
		new int[] { 0x4000*8+4,0,4 },
		new int[] {
			0,1,2,3,8,9,10,11,
			128+0,128+1,128+2,128+3,128+8,128+9,128+10,128+11
		},
		new int[] {
			0*16,1*16,2*16,3*16,4*16,5*16,6*16,7*16,
			256+0*16,256+1*16,256+2*16,256+3*16,256+4*16,256+5*16,256+6*16,256+7*16
		},
		8*0x40
	);
	
	static GfxLayout chinhero_sprite_layout2 = new GfxLayout(
		16,16,
		0x80,
		3,
		new int[] { 0x4000*8,0x2000*8+0,0x2000*8+4 },
		new int[] {
			0,1,2,3,8,9,10,11,
			128+0,128+1,128+2,128+3,128+8,128+9,128+10,128+11
		},
		new int[] {
			0*16,1*16,2*16,3*16,4*16,5*16,6*16,7*16,
			256+0*16,256+1*16,256+2*16,256+3*16,256+4*16,256+5*16,256+6*16,256+7*16
		},
		8*0x40
	);
	
	static GfxDecodeInfo chinhero_gfxdecodeinfo[] ={
		new GfxDecodeInfo( REGION_GFX1, 0, shangkid_char_layout,	0, 0x40 ),
		new GfxDecodeInfo( REGION_GFX2, 0, chinhero_sprite_layout1,	0, 0x20 ),
		new GfxDecodeInfo( REGION_GFX2, 0, chinhero_sprite_layout2,	0, 0x20 ),
		new GfxDecodeInfo( REGION_GFX3, 0, chinhero_sprite_layout1,	0, 0x20 ),
		new GfxDecodeInfo( REGION_GFX3, 0, chinhero_sprite_layout2,	0, 0x20 ),
		new GfxDecodeInfo( -1 )
	};
	
	static GfxDecodeInfo shangkid_gfxdecodeinfo[] ={
		new GfxDecodeInfo( REGION_GFX1, 0, shangkid_char_layout,	0, 0x40 ),
		new GfxDecodeInfo( REGION_GFX2, 0, shangkid_sprite_layout,	0, 0x40 ),
		new GfxDecodeInfo( -1 )
	};
	
	static GfxDecodeInfo dynamski_gfxdecodeinfo[] ={
		new GfxDecodeInfo( REGION_GFX1, 0, shangkid_char_layout,	   0, 0x10 ),
		new GfxDecodeInfo( REGION_GFX2, 0, shangkid_sprite_layout,	0x40, 0x10 ),
		new GfxDecodeInfo( -1 )
	};
	
	/***************************************************************************************/
	
	public static Memory_ReadAddress main_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x9fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xb800, 0xb800, input_port_0_r ), /* SW1 */
		new Memory_ReadAddress( 0xb801, 0xb801, input_port_1_r ), /* coin/start */
		new Memory_ReadAddress( 0xb802, 0xb802, input_port_2_r ), /* player#2 */
		new Memory_ReadAddress( 0xb803, 0xb803, input_port_3_r ), /* player#1 */
		new Memory_ReadAddress( 0xd000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress main_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x9fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa000, 0xa000, MWA_NOP ), /* ? */
		new Memory_WriteAddress( 0xb000, 0xb000, shangkid_bbx_enable_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, shangkid_sound_enable_w ),
		new Memory_WriteAddress( 0xb002, 0xb002, MWA_NOP ),		/* main CPU interrupt-related */
		new Memory_WriteAddress( 0xb003, 0xb003, MWA_NOP ),		/* BBX interrupt-related */
		new Memory_WriteAddress( 0xb004, 0xb004, shangkid_cpu_reset_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, MWA_NOP ),		/* coin counter */
		new Memory_WriteAddress( 0xb007, 0xb007, shangkid_maincpu_bank_w ),
		new Memory_WriteAddress( 0xc000, 0xc002, MWA_RAM, shangkid_videoreg ),
		new Memory_WriteAddress( 0xd000, 0xdfff, shangkid_videoram_w, videoram ),
		new Memory_WriteAddress( 0xe000, 0xfdff, MWA_RAM, shareram ),
		new Memory_WriteAddress( 0xfe00, 0xffff, MWA_RAM, spriteram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/***************************************************************************************/
	
	public static Memory_ReadAddress bbx_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x9fff, MRA_ROM ),
		new Memory_ReadAddress( 0xb800, 0xb800, input_port_0_r ), /* SW1 */
		new Memory_ReadAddress( 0xb801, 0xb801, input_port_1_r ), /* coin/start */
		new Memory_ReadAddress( 0xb802, 0xb802, input_port_2_r ), /* player#2 */
		new Memory_ReadAddress( 0xb803, 0xb803, input_port_3_r ), /* player#1 */
		new Memory_ReadAddress( 0xd000, 0xdfff, videoram_r ),
		new Memory_ReadAddress( 0xe000, 0xffff, shareram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress bbx_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x9fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa000, 0xa000, MWA_NOP ), /* ? */
		new Memory_WriteAddress( 0xb000, 0xb000, shangkid_bbx_enable_w ),
		new Memory_WriteAddress( 0xb001, 0xb001, shangkid_sound_enable_w ),
		new Memory_WriteAddress( 0xb002, 0xb002, MWA_NOP ),		/* main CPU interrupt-related */
		new Memory_WriteAddress( 0xb003, 0xb003, MWA_NOP ),		/* BBX interrupt-related */
		new Memory_WriteAddress( 0xb004, 0xb004, shangkid_cpu_reset_w ),
		new Memory_WriteAddress( 0xb006, 0xb006, MWA_NOP ),		/* coin counter */
		new Memory_WriteAddress( 0xb007, 0xb007, shangkid_maincpu_bank_w ),
		new Memory_WriteAddress( 0xd000, 0xdfff, shangkid_videoram_w ),
		new Memory_WriteAddress( 0xe000, 0xffff, shareram_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort bbx_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, shangkid_bbx_AY8910_control_w ),
		new IO_WritePort( 0x01, 0x01, shangkid_bbx_AY8910_write_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/***************************************************************************************/
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_BANK2 ),
		new Memory_ReadAddress( 0xe000, 0xefff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_NOP ), /* sample player writes to ROM area */
		new Memory_WriteAddress( 0xe000, 0xefff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport_sound[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, shangkid_soundlatch_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_sound[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, DAC_0_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/***************************************************************************************/
	
	static MACHINE_DRIVER_START( chinhero )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3000000) /* ? */
		MDRV_CPU_MEMORY(main_readmem,main_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 3000000) /* ? */
		MDRV_CPU_MEMORY(bbx_readmem,bbx_writemem)
		MDRV_CPU_PORTS(0,bbx_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 3000000) /* ? */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(readport_sound,writeport_sound)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(40*8, 28*8)
		MDRV_VISIBLE_AREA(16, 319-16, 0, 223)
		MDRV_GFXDECODE(chinhero_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(shangkid)
		MDRV_VIDEO_UPDATE(shangkid)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, dac_interface)
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( shangkid )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(chinhero)
	
		/* video hardware */
		MDRV_GFXDECODE(shangkid_gfxdecodeinfo)
	MACHINE_DRIVER_END
	
	
	
	public static Memory_ReadAddress dynamski_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xcbff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xd3ff, MRA_RAM ),
		new Memory_ReadAddress( 0xd800, 0xdbff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe002, MRA_RAM ),
		new Memory_ReadAddress( 0xe800, 0xe800, input_port_0_r ),
		new Memory_ReadAddress( 0xe801, 0xe801, input_port_1_r ),
		new Memory_ReadAddress( 0xe802, 0xe802, input_port_2_r ),
		new Memory_ReadAddress( 0xe803, 0xe803, input_port_3_r ),
		new Memory_ReadAddress( 0xf000, 0xf7ff, MRA_RAM ), /* work ram */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress dynamski_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM, videoram ), /* tilemap */
		new Memory_WriteAddress( 0xc800, 0xcbff, MWA_RAM ),
		new Memory_WriteAddress( 0xd000, 0xd3ff, MWA_RAM ),
		new Memory_WriteAddress( 0xd800, 0xdbff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe000, MWA_NOP ), /* IRQ disable */
		new Memory_WriteAddress( 0xe001, 0xe002, MWA_RAM ), /* screen flip */
		new Memory_WriteAddress( 0xf000, 0xf7ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort dynamski_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		/* ports are reversed */
		new IO_WritePort( 0x00, 0x00, AY8910_write_port_0_w ),
		new IO_WritePort( 0x01, 0x01, AY8910_control_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static MACHINE_DRIVER_START( dynamski )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3000000) /* ? */
		MDRV_CPU_MEMORY(dynamski_readmem,dynamski_writemem)
		MDRV_CPU_PORTS(0,dynamski_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256+32, 256)
		MDRV_VISIBLE_AREA(0, 255+32, 16, 255-16)
		MDRV_GFXDECODE(dynamski_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(16*4+16*4)
	
		MDRV_PALETTE_INIT(dynamski)
		MDRV_VIDEO_UPDATE(dynamski)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	/***************************************************************************************/
	
	static InputPortPtr input_ports_dynamski = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SERVICE1 );/* service */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Unknown") );
		/* what's 00 ? */
		PORT_DIPSETTING(	0x01, "A" );
		PORT_DIPSETTING(	0x02, "B" );
		PORT_DIPSETTING(	0x03, "C" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x04, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x18, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x18, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") ); /* unused? */
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") ); /* unused? */
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_chinhero = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x01, "3" );
		PORT_DIPSETTING(	0x02, "4" );
		PORT_DIPSETTING(	0x03, "5" );
		PORT_BITX( 0,0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite",0,0 );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x18, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x18, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Difficulty") ); /* not verified */
		PORT_DIPSETTING(	0x00, "Easy" );
		PORT_DIPSETTING(	0x40, "Medium" );
		PORT_DIPSETTING(	0x80, "Hard" );
		PORT_DIPSETTING(	0xc0, "Hardest" );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SERVICE1 );/* service */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON4 | IPF_PLAYER2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON3 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON4 );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_shangkid = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		/*	There are also two potentiometers on the PCB for volume:
		**	RV1 - Music
		**	RV2 - Sound Effects
		*/
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x1c, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x10, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0x0c, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Unknown") ); /* 1C_1C; no coin counter */
		PORT_DIPSETTING(	0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x14, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x18, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x1c, DEF_STR( "1C_5C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(	0x00, "Easy" );
		PORT_DIPSETTING(	0x40, "Medium" );
		PORT_DIPSETTING(	0x80, "Hard" );
		PORT_DIPSETTING(	0xc0, "Hardest" );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SERVICE1 );/* service */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );/* busy flag? */
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );/* kick */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );/* punch */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_PLAYER2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );/* kick */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 );/* punch */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN );
	INPUT_PORTS_END(); }}; 
	
	/***************************************************************************************/
	
	static RomLoadPtr rom_chinhero = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code (main) */
		ROM_LOAD( "ic2.1",		  0x0000, 0x2000, 0x8974bac4 );
		ROM_LOAD( "ic3.2",		  0x2000, 0x2000, 0x9b7a02fe );
		ROM_LOAD( "ic4.3",		  0x4000, 0x2000, 0xe86d4195 );
		ROM_LOAD( "ic5.4",		  0x6000, 0x2000, 0x2b629d2c );
		ROM_LOAD( "ic6.5",		  0x8000, 0x2000, 0x35bf4a4f );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code (coprocessor) */
		ROM_LOAD( "ic31.6",		  0x0000, 0x2000, 0x7c56927b );
		ROM_LOAD( "ic32.7",		  0x2000, 0x2000, 0xd67b8045 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code (sound) */
		ROM_LOAD( "ic47.8",		  0x0000, 0x2000, 0x3c396062 );
		ROM_LOAD( "ic48.9",		  0x2000, 0x2000, 0xb14f2bab );
		ROM_LOAD( "ic49.10",	  0x4000, 0x2000, 0x8c0e43d1 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE|ROMREGION_INVERT );/* tiles */
		ROM_LOAD( "ic21.11",	  0x0000, 0x2000, 0x3a37fb45 );
		ROM_LOAD( "ic22.12",	  0x2000, 0x2000, 0xbc21c002 );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE|ROMREGION_INVERT );/* sprites */
		ROM_LOAD( "ic114.18",	  0x0000, 0x2000, 0xfc4183a8 );
		ROM_LOAD( "ic113.17",	  0x2000, 0x2000, 0xd713d7fe );
		ROM_LOAD(  "ic99.13",	  0x4000, 0x2000, 0xa8e2a3f4 );
	
		ROM_REGION( 0x6000, REGION_GFX3, ROMREGION_DISPOSE|ROMREGION_INVERT );/* sprites */
		ROM_LOAD( "ic112.16",	  0x0000, 0x2000, 0xdd5170ca );
		ROM_LOAD( "ic111.15",	  0x2000, 0x2000, 0x20f6052e );
		ROM_LOAD( "ic110.14",	  0x4000, 0x2000, 0x9bc2d568 );
	
		ROM_REGION( 0xa80, REGION_PROMS, 0 );
		ROM_LOAD( "v_ic36_r",	  0x000, 0x100, 0x16ae1692 );/* red */
		ROM_LOAD( "v_ic35_g",	  0x100, 0x100, 0xb3d0a074 );/* green */
		ROM_LOAD( "v_ic27_b",	  0x200, 0x100, 0x353a2d11 );/* blue */
	
		ROM_LOAD( "v_ic28_m",	  0x300, 0x100, 0x7ca273c1 );/* unknown */
		ROM_LOAD( "v_ic69",		  0x400, 0x200, 0x410d6f86 );/* zoom */
		ROM_LOAD( "v_ic108",	  0x600, 0x200, 0xd33c02ae );/* zoom */
	
		ROM_LOAD( "v_ic12",		  0x800, 0x100, 0x0de07e89 );/* tile pen priority */
		ROM_LOAD( "v_ic15_p",	  0x900, 0x100, 0x7e0a0581 );/* sprite pen transparency */
		ROM_LOAD( "v_ic8",		  0xa00, 0x020, 0x4c62974d );
	
		ROM_LOAD( "ic8",		  0xa20, 0x020, 0x84bcd9af );/* main CPU banking */
		ROM_LOAD( "ic22",		  0xa40, 0x020, 0x84bcd9af );/* coprocessor banking */
		ROM_LOAD( "ic42",		  0xa60, 0x020, 0x2ccfe10a );/* sound cpu banking */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_shangkid = new RomLoadPtr(){ public void handler(){ 
		/* Main CPU - handles game logic */
		ROM_REGION( 0x12000, REGION_CPU1, 0 );/* Z80 (NEC D780C-1) code */
		ROM_LOAD( "cr00ic02.bin", 0x00000, 0x4000, 0x2e420377 );
		ROM_LOAD( "cr01ic03.bin", 0x04000, 0x4000, 0x161cd358 );
		ROM_LOAD( "cr02ic04.bin", 0x08000, 0x2000, 0x85b6e455 );/* banked at 0x8000 */
		ROM_LOAD( "cr03ic05.bin", 0x10000, 0x2000, 0x3b383863 );/* banked at 0x8000 */
	
		/* The BBX coprocessor is burried in an epoxy block.  It contains:
		**	-	a surface-mounted Z80 (TMPZ84C00P)
		**	-	LS245 logic IC
		**	-	battery backed ram chip Fujitsu MB8464
		**
		**	The BBX coprocessor receives graphics and sound-related commands from
		**	the main CPU via shared RAM.  It directly manages an AY8910, is
		**	responsible for populating spriteram, and forwards appropriate sound
		**	commands to the sample-playing CPU.
		*/
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80: bbx module */
		ROM_LOAD( "bbx.bin",	  0x0000, 0x2000, 0x560c0abd );/* battery-backed RAM */
		ROM_LOAD( "cr04ic31.bin", 0x2000, 0x2000, 0xcb207885 );
		ROM_LOAD( "cr05ic32.bin", 0x4000, 0x4000, 0xcf3b8d55 );
		ROM_LOAD( "cr06ic33.bin", 0x8000, 0x2000, 0x0f3bdbd8 );
	
		/*	The Sound CPU is a dedicated Sample Player */
		ROM_REGION( 0x1e000, REGION_CPU3, 0 );/* Z80 (NEC D780C-1) */
		ROM_LOAD( "cr11ic51.bin", 0x00000, 0x4000, 0x2e2d6afe );
		ROM_LOAD( "cr12ic43.bin", 0x04000, 0x4000, 0xdd29a0c8 );
		ROM_LOAD( "cr13ic44.bin", 0x08000, 0x4000, 0x879d0de0 );
		ROM_LOAD( "cr07ic47.bin", 0x10000, 0x4000, 0x20540f7c );
		ROM_LOAD( "cr08ic48.bin", 0x14000, 0x2000, 0x392f24db );
		ROM_LOAD( "cr09ic49.bin", 0x18000, 0x4000, 0xd50c96a8 );
		ROM_LOAD( "cr10ic50.bin", 0x1c000, 0x2000, 0x873a5f2d );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE|ROMREGION_INVERT );/* 8x8 tiles */
		ROM_LOAD( "cr20ic21.bin", 0x0000, 0x2000, 0xeb3cbb11 );
		ROM_LOAD( "cr21ic22.bin", 0x2000, 0x2000, 0x7c6e75f4 );
	
		ROM_REGION( 0x18000, REGION_GFX2, ROMREGION_DISPOSE|ROMREGION_INVERT );/* 16x16 sprites */
		ROM_LOAD( "cr14i114.bin", 0x00000, 0x4000, 0xee1f348f );
		ROM_LOAD( "cr15i113.bin", 0x04000, 0x4000, 0xa46398bd );
		ROM_LOAD( "cr16i112.bin", 0x08000, 0x4000, 0xcbed446c );
		ROM_LOAD( "cr17i111.bin", 0x0c000, 0x4000, 0xb0a44330 );
		ROM_LOAD( "cr18ic99.bin", 0x10000, 0x4000, 0xff7efd7c );
		ROM_LOAD( "cr19i100.bin", 0x14000, 0x4000, 0xf948f829 );
	
		ROM_REGION( 0xa80, REGION_PROMS, 0 );
		ROM_LOAD( "cr31ic36.bin", 0x000, 0x100, 0x9439590b );/* 82S129 - red */
		ROM_LOAD( "cr30ic35.bin", 0x100, 0x100, 0x324e295e );/* 82S129 - green */
		ROM_LOAD( "cr28ic27.bin", 0x200, 0x100, 0x375cba96 );/* 82S129 - blue */
	
		ROM_LOAD( "cr29ic28.bin", 0x300, 0x100, 0x7ca273c1 );/* 82S129 - unknown */
		ROM_LOAD( "cr32ic69.bin", 0x400, 0x200, 0x410d6f86 );/* 82S147 - sprite-related (zoom?) */
		ROM_LOAD( "cr33-108.bin", 0x600, 0x200, 0xd33c02ae );/* 82S147 - sprite-related (zoom?) */
	
		ROM_LOAD( "cr26ic12.bin", 0x800, 0x100, 0x85b5e958 );/* 82S129 - tile pen priority? */
		ROM_LOAD( "cr27ic15.bin", 0x900, 0x100, 0xf7a19fe2 );/* 82S129 - sprite pen transparency */
	
		ROM_LOAD( "cr25ic8.bin",  0xa00, 0x020, 0xc85e09ad );/* 82S123 */
		ROM_LOAD( "cr22ic8.bin",  0xa20, 0x020, 0x1a7e0b06 );/* 82S123 - main CPU banking */
		ROM_LOAD( "cr23ic22.bin", 0xa40, 0x020, 0xefb5f265 );/* 82S123 - coprocessor banking */
		ROM_LOAD( "cr24ic42.bin", 0xa60, 0x020, 0x823878aa );/* 82S123 - sample player banking */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hiryuken = new RomLoadPtr(){ public void handler(){ 
		/* Main CPU - handles game logic */
		ROM_REGION( 0x12000, REGION_CPU1, 0 );/* Z80 (NEC D780C-1) code */
		ROM_LOAD( "1.2", 0x00000, 0x4000, 0xc7af7f2e );
		ROM_LOAD( "2.3", 0x04000, 0x4000, 0x639afdb3 );
		ROM_LOAD( "3.4", 0x08000, 0x2000, 0xad210482 );/* banked at 0x8000 */
		ROM_LOAD( "4.5", 0x10000, 0x2000, 0x6518943a );/* banked at 0x8000 */
	
		/* The BBX coprocessor is burried in an epoxy block.  It contains:
		** - a surface-mounted Z80 (TMPZ84C00P)
		** - LS245 logic IC
		** - battery backed ram chip Fujitsu MB8464
		**
		** The BBX coprocessor receives graphics and sound-related commands from
		** the main CPU via shared RAM.  It directly manages an AY8910, is
		** responsible for populating spriteram, and forwards appropriate sound
		** commands to the sample-playing CPU.
		*/
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80: bbx module */
		ROM_LOAD( "bbxj.bin",     0x0000, 0x2000, 0x8def4aaf );/* battery-backed RAM */
		ROM_LOAD( "5.31",         0x2000, 0x2000, 0x8ae37ce7 );
		ROM_LOAD( "6.32",         0x4000, 0x4000, 0xe835bb7f );
		ROM_LOAD( "7.33",         0x8000, 0x2000, 0x3745ed36 );
	
		/* The Sound CPU is a dedicated Sample Player */
		ROM_REGION( 0x1e000, REGION_CPU3, 0 );/* Z80 (NEC D780C-1) */
		ROM_LOAD( "cr11ic51.bin", 0x00000, 0x4000, 0x2e2d6afe );// 12.51
	//	ROM_LOAD( "cr12ic43.bin", 0x04000, 0x4000, 0xdd29a0c8 );// not present in this set
	//	ROM_LOAD( "cr13ic44.bin", 0x08000, 0x4000, 0x879d0de0 );// not present in this set
		ROM_LOAD( "cr07ic47.bin", 0x10000, 0x4000, 0x20540f7c );// 8.47
		ROM_LOAD( "9.48",         0x14000, 0x4000, 0x8da23cad );
		ROM_LOAD( "10.49",        0x18000, 0x4000, 0x52b82fee );
		ROM_LOAD( "cr10ic50.bin", 0x1c000, 0x2000, 0x873a5f2d );// 11.50
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE|ROMREGION_INVERT );/* 8x8 tiles */
		ROM_LOAD( "21.21",        0x0000, 0x2000, 0xce20a1d4 );
		ROM_LOAD( "22.22",        0x2000, 0x2000, 0x26fc88bf );
	
		ROM_REGION( 0x18000, REGION_GFX2, ROMREGION_DISPOSE|ROMREGION_INVERT );/* 16x16 sprites */
		ROM_LOAD( "15.114",       0x00000, 0x4000, 0xed07854e );
		ROM_LOAD( "16.113",       0x04000, 0x4000, 0x85cf1939 );
		ROM_LOAD( "cr16i112.bin", 0x08000, 0x4000, 0xcbed446c );// 17.112
		ROM_LOAD( "cr17i111.bin", 0x0c000, 0x4000, 0xb0a44330 );// 18.111
		ROM_LOAD( "cr18ic99.bin", 0x10000, 0x4000, 0xff7efd7c );// 19.99
		ROM_LOAD( "20.100",       0x14000, 0x4000, 0x4bc77ca0 );
	
		ROM_REGION( 0xa80, REGION_PROMS, 0 );
		ROM_LOAD( "r.36",         0x000, 0x100, 0x65dec63d );/* 82S129 - red */
		ROM_LOAD( "g.35",         0x100, 0x100, 0xe79de8cf );/* 82S129 - green */
		ROM_LOAD( "b.27",         0x200, 0x100, 0xd6ab3448 );/* 82S129 - blue */
	
		ROM_LOAD( "cr29ic28.bin", 0x300, 0x100, 0x7ca273c1 );/* 82S129 - unknown */					// m.28
		ROM_LOAD( "cr32ic69.bin", 0x400, 0x200, 0x410d6f86 );/* 82S147 - sprite-related (zoom?) */	// ic69
		ROM_LOAD( "cr33-108.bin", 0x600, 0x200, 0xd33c02ae );/* 82S147 - sprite-related (zoom?) */	// ic108
	
		ROM_LOAD( "cr26ic12.bin", 0x800, 0x100, 0x85b5e958 );/* 82S129 - tile pen priority? */		// sc.12
		ROM_LOAD( "cr27ic15.bin", 0x900, 0x100, 0xf7a19fe2 );/* 82S129 - sprite pen transparency */	// sp.15
	
		ROM_LOAD( "cr25ic8.bin",  0xa00, 0x020, 0xc85e09ad );/* 82S123 */							// a.8
		ROM_LOAD( "cr22ic8.bin",  0xa20, 0x020, 0x1a7e0b06 );/* 82S123 - main CPU banking */		// 1.8
		ROM_LOAD( "cr23ic22.bin", 0xa40, 0x020, 0xefb5f265 );/* 82S123 - coprocessor banking */		// 2.22
		ROM_LOAD( "cr24ic42.bin", 0xa60, 0x020, 0x823878aa );/* 82S123 - sample player banking */	// 3.42
	ROM_END(); }}; 
	
	static RomLoadPtr rom_dynamski = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x12000, REGION_CPU1, 0 );/* Z80 code */
		ROM_LOAD( "dynski.1",     0x00000, 0x1000, 0x30191160 );/* code */
		ROM_LOAD( "dynski.2",     0x01000, 0x1000, 0x5e08a0b0 );
		ROM_LOAD( "dynski.3",     0x02000, 0x1000, 0x29cfd740 );
		ROM_LOAD( "dynski.4",     0x03000, 0x1000, 0xe1d47776 );
		ROM_LOAD( "dynski.5",     0x04000, 0x1000, 0xe39aba1b );
		ROM_LOAD( "dynski.6",     0x05000, 0x1000, 0x95780608 );
		ROM_LOAD( "dynski.7",     0x06000, 0x1000, 0xb88d328b );
		ROM_LOAD( "dynski.8",     0x07000, 0x1000, 0x8db5e691 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE|ROMREGION_INVERT );/* 8x8 tiles */
		ROM_LOAD( "dynski8.3e",   0x0000, 0x2000, 0x32c354dc );
		ROM_LOAD( "dynski9.2e",   0x2000, 0x2000, 0x80a6290c );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE|ROMREGION_INVERT );/* 16x16 sprites */
		ROM_LOAD( "dynski5.14b",  0x0000, 0x2000, 0xaa4ac6e2 );
		ROM_LOAD( "dynski6.15b",  0x2000, 0x2000, 0x47e76886 );
		ROM_LOAD( "dynski7.14d",  0x4000, 0x2000, 0xa153dfa9 );
	
		ROM_REGION( 0x240, REGION_PROMS, 0 );
		ROM_LOAD( "dynskic.15g",  0x000, 0x020, 0x9333a5e4 );/* palette */
		ROM_LOAD( "dynskic.15f",  0x020, 0x020, 0x3869514b );/* palette */
		ROM_LOAD( "dynski.11e",   0x040, 0x100, 0xe625aa09 );/* lookup table */
		ROM_LOAD( "dynski.4g",    0x140, 0x100, 0x761fe465 );/* lookup table */
	ROM_END(); }}; 
	
	
	public static GameDriver driver_dynamski	   = new GameDriver("1984"	,"dynamski"	,"shangkid.java"	,rom_dynamski,null	,machine_driver_dynamski	,input_ports_dynamski	,null	,ROT90	,	"Taiyo", "Dynamic Ski", GAME_NO_COCKTAIL )
	public static GameDriver driver_chinhero	   = new GameDriver("1984"	,"chinhero"	,"shangkid.java"	,rom_chinhero,null	,machine_driver_chinhero	,input_ports_chinhero	,init_chinhero	,ROT90	,	"Taiyo", "Chinese Hero" )
	public static GameDriver driver_shangkid	   = new GameDriver("1985"	,"shangkid"	,"shangkid.java"	,rom_shangkid,null	,machine_driver_shangkid	,input_ports_shangkid	,init_shangkid	,ROT0	,	"Taiyo (Data East license)", "Shanghai Kid", GAME_NO_COCKTAIL )
	public static GameDriver driver_hiryuken	   = new GameDriver("1985"	,"hiryuken"	,"shangkid.java"	,rom_hiryuken,driver_shangkid	,machine_driver_shangkid	,input_ports_shangkid	,init_shangkid	,ROT0	,	"[Nihon Game] (Taito license)", "Hokuha Syourin Hiryu no Ken", GAME_NO_COCKTAIL )
}
