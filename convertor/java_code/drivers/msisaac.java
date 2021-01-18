/****************************************************************************

	Metal Soldier Isaac II	(c) Taito 1985

	driver by Jaroslaw Burczynski

****************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class msisaac
{
	
	/*
	TO DO:
	  - sprites are probably banked differently (no way to be sure until MCU dump is available)
	  - TA7630 emulation needs filter support (characteristics depend on the frequency)
	  - TA7630 volume table is hand tuned to match the sample, but still slighty off.
	*/
	
	/* in machine/buggychl.c */
	
	
	//not used
	//
	//used
	
	
	
	extern VIDEO_UPDATE( msisaac );
	extern VIDEO_START( msisaac );
	extern unsigned char *msisaac_videoram;
	extern unsigned char *msisaac_videoram2;
	
	
	
	static int sound_nmi_enable,pending_nmi;
	
	static void nmi_callback(int param)
	{
		if (sound_nmi_enable) cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
		else pending_nmi = 1;
	}
	
	public static WriteHandlerPtr sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch_w(0,data);
		timer_set(TIME_NOW,data,nmi_callback);
	} };
	
	public static WriteHandlerPtr nmi_disable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sound_nmi_enable = 0;
	} };
	
	public static WriteHandlerPtr nmi_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sound_nmi_enable = 1;
		if (pending_nmi)
		{
			cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
			pending_nmi = 0;
		}
	} };
	
	#if 0
	public static WriteHandlerPtr flip_screen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		flip_screen_set(data);
	} };
	
	public static WriteHandlerPtr msisaac_coin_counter_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_counter_w(offset,data);
	} };
	#endif
	public static WriteHandlerPtr ms_unknown_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (data!=0x08)
			usrintf_showmessage("CPU #0 write to 0xf0a3 data=%2x",data);
	} };
	
	
	
	
	
	/* If good MCU dump will be available, it should be fully working game */
	
	/* To test the game without the MCU simply comment out #define USE_MCU */
	
	#define USE_MCU
	
	public static ReadHandlerPtr msisaac_mcu_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	#ifdef USE_MCU
		return buggychl_mcu_r(offset);
	#else
		//logerror("CPU#0 read from MCU pc=%4x\n", activecpu_get_pc() );
		return 0xca; //a hack to make the game boot
	#endif
	} };
	
	public static ReadHandlerPtr msisaac_mcu_status_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	#ifdef USE_MCU
		return buggychl_mcu_status_r(offset);
	#else
		return 3;	//mcu ready / cpu data ready
	#endif
	} };
	
	public static WriteHandlerPtr msisaac_mcu_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	#ifdef USE_MCU
		buggychl_mcu_w(offset,data);
	#endif
	} };
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
	
		new Memory_ReadAddress( 0xf0e0, 0xf0e0, msisaac_mcu_r ),
		new Memory_ReadAddress( 0xf0e1, 0xf0e1, msisaac_mcu_status_r ),
	
		new Memory_ReadAddress( 0xf080, 0xf080, input_port_0_r ),
		new Memory_ReadAddress( 0xf081, 0xf081, input_port_1_r ),
		new Memory_ReadAddress( 0xf082, 0xf082, input_port_2_r ),
		new Memory_ReadAddress( 0xf083, 0xf083, input_port_3_r ),
		new Memory_ReadAddress( 0xf084, 0xf084, input_port_4_r ),
	//new Memory_ReadAddress( 0xf086, 0xf086, input_port_5_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
	
		new Memory_WriteAddress( 0xe800, 0xefff, paletteram_xxxxRRRRGGGGBBBB_w, paletteram ),
	
	//new Memory_WriteAddress( 0xf400, 0xf43f, msisaac_fg_colorram_w, colorram ),
	
		new Memory_WriteAddress( 0xf0a3, 0xf0a3, ms_unknown_w ),			//???? written in interrupt routine
	
		new Memory_WriteAddress( 0xf060, 0xf060, sound_command_w ),		//sound command
		new Memory_WriteAddress( 0xf061, 0xf061, MWA_NOP /*sound_reset*/),	//????
	
		new Memory_WriteAddress( 0xf000, 0xf000, msisaac_bg2_textbank_w ),
		new Memory_WriteAddress( 0xf001, 0xf001, MWA_RAM ), 			//???
		new Memory_WriteAddress( 0xf002, 0xf002, MWA_RAM ), 			//???
	
		new Memory_WriteAddress( 0xf0c0, 0xf0c0, msisaac_fg_scrollx_w ),
		new Memory_WriteAddress( 0xf0c1, 0xf0c1, msisaac_fg_scrolly_w ),
		new Memory_WriteAddress( 0xf0c2, 0xf0c2, msisaac_bg2_scrollx_w ),
		new Memory_WriteAddress( 0xf0c3, 0xf0c3, msisaac_bg2_scrolly_w ),
		new Memory_WriteAddress( 0xf0c4, 0xf0c4, msisaac_bg_scrollx_w ),
		new Memory_WriteAddress( 0xf0c5, 0xf0c5, msisaac_bg_scrolly_w ),
	
		new Memory_WriteAddress( 0xf0e0, 0xf0e0, msisaac_mcu_w ),
	
		new Memory_WriteAddress( 0xf100, 0xf17f, MWA_RAM, spriteram ),	//sprites
		new Memory_WriteAddress( 0xf400, 0xf7ff, msisaac_fg_videoram_w, videoram ),
		new Memory_WriteAddress( 0xf800, 0xfbff, msisaac_bg2_videoram_w,msisaac_videoram2 ),
		new Memory_WriteAddress( 0xfc00, 0xffff, msisaac_bg_videoram_w, msisaac_videoram ),
	
	
	//	new Memory_WriteAddress( 0xf801, 0xf801, msisaac_bgcolor_w ),
	//	new Memory_WriteAddress( 0xfc00, 0xfc00, flip_screen_w ),
	//	new Memory_WriteAddress( 0xfc03, 0xfc04, msisaac_coin_counter_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress readmem_sound[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xc000, soundlatch_r ),
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_NOP ), /*space for diagnostic ROM (not dumped, not reachable) */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	static int vol_ctrl[16];
	
	static MACHINE_INIT( ta7630 )
	{
		int i;
	
		double db			= 0.0;
		double db_step		= 0.50;	/* 0.50 dB step (at least, maybe more) */
		double db_step_inc	= 0.275;
		for (i=0; i<16; i++)
		{
			double max = 100.0 / pow(10.0, db/20.0 );
			vol_ctrl[ 15-i ] = max;
			/*logerror("vol_ctrl[%x] = %i (%f dB)\n",15-i,vol_ctrl[ 15-i ],db);*/
			db += db_step;
			db_step += db_step_inc;
		}
	
		/*for (i=0; i<8; i++)
			logerror("SOUND Chan#%i name=%s\n", i, mixer_get_name(i) );*/
	/*
	  channels 0-2 AY#0
	  channels 3-5 AY#1
	  channels 6,7 MSM5232 group1,group2
	*/
	}
	
	static UINT8 snd_ctrl0=0;
	static UINT8 snd_ctrl1=0;
	
	public static WriteHandlerPtr sound_control_0_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		snd_ctrl0 = data & 0xff;
		//usrintf_showmessage("SND0 0=%2x 1=%2x", snd_ctrl0, snd_ctrl1);
	
		mixer_set_volume (6, vol_ctrl[  snd_ctrl0     & 15 ]);	/* group1 from msm5232 */
		mixer_set_volume (7, vol_ctrl[ (snd_ctrl0>>4) & 15 ]);	/* group2 from msm5232 */
	
	} };
	public static WriteHandlerPtr sound_control_1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		snd_ctrl1 = data & 0xff;
		//usrintf_showmessage("SND1 0=%2x 1=%2x", snd_ctrl0, snd_ctrl1);
	} };
	
	
	
	public static Memory_WriteAddress writemem_sound[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
	
		new Memory_WriteAddress( 0x8000, 0x8000, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x8001, 0x8001, AY8910_write_port_0_w   ),
		new Memory_WriteAddress( 0x8002, 0x8002, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0x8003, 0x8003, AY8910_write_port_1_w   ),
		new Memory_WriteAddress( 0x8010, 0x801d, MSM5232_0_w ),
		new Memory_WriteAddress( 0x8020, 0x8020, sound_control_0_w  ),
		new Memory_WriteAddress( 0x8030, 0x8030, sound_control_1_w  ),
	
		new Memory_WriteAddress( 0xc001, 0xc001, nmi_enable_w ),
		new Memory_WriteAddress( 0xc002, 0xc002, nmi_disable_w ),
		new Memory_WriteAddress( 0xc003, 0xc003, MWA_NOP ), /*???*/ /* this is NOT mixer_enable */
	
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress mcu_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0000, buggychl_68705_portA_r ),
		new Memory_ReadAddress( 0x0001, 0x0001, buggychl_68705_portB_r ),
		new Memory_ReadAddress( 0x0002, 0x0002, buggychl_68705_portC_r ),
		new Memory_ReadAddress( 0x0010, 0x007f, MRA_RAM ),
		new Memory_ReadAddress( 0x0080, 0x07ff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mcu_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0000, buggychl_68705_portA_w ),
		new Memory_WriteAddress( 0x0001, 0x0001, buggychl_68705_portB_w ),
		new Memory_WriteAddress( 0x0002, 0x0002, buggychl_68705_portC_w ),
		new Memory_WriteAddress( 0x0004, 0x0004, buggychl_68705_ddrA_w ),
		new Memory_WriteAddress( 0x0005, 0x0005, buggychl_68705_ddrB_w ),
		new Memory_WriteAddress( 0x0006, 0x0006, buggychl_68705_ddrC_w ),
		new Memory_WriteAddress( 0x0010, 0x007f, MWA_RAM ),
		new Memory_WriteAddress( 0x0080, 0x07ff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_msisaac = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x01, 0x00, "DSW1 Unknown 0" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, "DSW1 Unknown 1" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x18, 0x10, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x10, "3" );
		PORT_DIPSETTING(    0x18, "4" );
		PORT_DIPNAME( 0x20, 0x00, "DSW1 Unknown 5" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "DSW1 Unknown 6" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "DSW1 Unknown 7" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();  /* DSW2 */
		PORT_DIPNAME( 0x0f, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "9C_1C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "8C_1C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "7C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_8C") );
		PORT_DIPNAME( 0xf0, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "9C_1C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "8C_1C") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "7C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "1C_8C") );
	
		PORT_START();  /* DSW3 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, "DSW3 Unknown 1" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x02, "02" );
		PORT_BITX(    0x04, 0x04, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "DSW3 Unknown 3" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x08, "08" );
		PORT_DIPNAME( 0x30, 0x00, "Copyright Notice" );
		PORT_DIPSETTING(    0x00, "(C);1985 Taito Corporation" )
		PORT_DIPSETTING(    0x10, "(C);Taito Corporation" )
		PORT_DIPSETTING(    0x20, "(C);Taito Corp. MCMLXXXV" )
		PORT_DIPSETTING(    0x30, "(C);Taito Corporation" )
		PORT_DIPNAME( 0x40, 0x00, "Coinage Display" );
		PORT_DIPSETTING(    0x40, "Insert Coin" );
		PORT_DIPSETTING(    0x00, "Coins/Credits" );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coinage"));
		PORT_DIPSETTING(    0x80, "A and B" );
		PORT_DIPSETTING(    0x00, "A only" );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );//??
		PORT_BIT( 0x08, IP_ACTIVE_LOW,  IPT_TILT );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );//??
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );//??
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout char_layout = new GfxLayout
	(
		8,8,
		0x400,
		4,
		new int[] { 0*0x2000*8, 1*0x2000*8, 2*0x2000*8, 3*0x2000*8 },
		new int[] { 7,6,5,4,3,2,1,0 },
		new int[] { 0*8,1*8,2*8,3*8,4*8,5*8,6*8,7*8 },
		8*8
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		16,16,
		0x100,
		4,
		new int[] { 0*0x2000*8, 1*0x2000*8, 2*0x2000*8, 3*0x2000*8 },
		new int[] { 7,6,5,4,3,2,1,0,  64+7,64+6,64+5,64+4,64+3,64+2,64+1,64+0,},
		new int[] { 0*8,1*8,2*8,3*8,4*8,5*8,6*8,7*8, 16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, char_layout, 0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0, char_layout, 0, 64 ),
		new GfxDecodeInfo( REGION_GFX1, 0, tile_layout, 0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0, tile_layout, 0, 64 ),
		new GfxDecodeInfo( -1 )
	};
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2, /* number of chips */
		2000000, /* 2 MHz ??? */
		new int[] { 15,15 },
		new ReadHandlerPtr[] { 0,0 },
		new ReadHandlerPtr[] { 0,0 },
		new WriteHandlerPtr[] { 0,0 },
		new WriteHandlerPtr[] { 0,0 }
	);
	
	static struct MSM5232interface msm5232_interface =
	{
		1, /* number of chips */
		2000000, /* 2 MHz ??? */
		{ { 0.65e-6, 0.65e-6, 0.65e-6, 0.65e-6, 0.65e-6, 0.65e-6, 0.65e-6, 0.65e-6 } },	/* 0.65 (???) uF capacitors (match the sample, not verified) */
		{ 100 }	/* mixing level ??? */
	};
	
	
	/*******************************************************************************/
	
	static MACHINE_DRIVER_START( msisaac )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(readmem_sound,writemem_sound)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)	/* source of IRQs is unknown */
	
	#ifdef USE_MCU
		MDRV_CPU_ADD(M68705,8000000/2)  /* 4 MHz */
		MDRV_CPU_MEMORY(mcu_readmem,mcu_writemem)
	#endif
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(ta7630)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0, 32*8-1, 1*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(msisaac)
		MDRV_VIDEO_UPDATE(msisaac)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(MSM5232, msm5232_interface)
	MACHINE_DRIVER_END
	
	
	/*******************************************************************************/
	
	static RomLoadPtr rom_msisaac = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 main CPU */
		ROM_LOAD( "a34_11.bin", 0x0000, 0x4000, 0x40819334 );
		ROM_LOAD( "a34_12.bin", 0x4000, 0x4000, 0x4c50b298 );
		ROM_LOAD( "a34_13.bin", 0x8000, 0x4000, 0x2e2b09b3 );
		ROM_LOAD( "a34_10.bin", 0xc000, 0x2000, 0xa2c53dc1 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 sound CPU */
		ROM_LOAD( "a34_01.bin", 0x0000, 0x4000, 0x545e45e7 );
	
	#ifdef USE_MCU
		ROM_REGION( 0x0800, REGION_CPU3, 0 );/* 2k for the microcontroller */
		ROM_LOAD( "mcu"       , 0x0000, 0x0800, 0 );
	#endif
	// I tried following MCUs; none of them work with this game:
	//	ROM_LOAD( "a30-14"    , 0x0000, 0x0800, 0xc4690279 );//40love
	//	ROM_LOAD( "a22-19.31",  0x0000, 0x0800, 0x06a71df0 ); 	//buggy challenge
	//	ROM_LOAD( "a45-19",     0x0000, 0x0800, 0x5378253c ); 	//flstory
	//	ROM_LOAD( "a54-19",     0x0000, 0x0800, 0xe08b8846 ); 	//lkage
	
		ROM_REGION( 0x8000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "a34_02.bin", 0x0000, 0x2000, 0x50da1a81 );
		ROM_LOAD( "a34_03.bin", 0x2000, 0x2000, 0x728a549e );
		ROM_LOAD( "a34_04.bin", 0x4000, 0x2000, 0xe7d19f1c );
		ROM_LOAD( "a34_05.bin", 0x6000, 0x2000, 0xbed2107d );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "a34_06.bin", 0x0000, 0x2000, 0x4ec71687 );
		ROM_LOAD( "a34_07.bin", 0x2000, 0x2000, 0x24922abf );
		ROM_LOAD( "a34_08.bin", 0x4000, 0x2000, 0x3ddbf4c0 );
		ROM_LOAD( "a34_09.bin", 0x6000, 0x2000, 0x23eb089d );
	
	ROM_END(); }}; 
	
	public static GameDriver driver_msisaac	   = new GameDriver("1985"	,"msisaac"	,"msisaac.java"	,rom_msisaac,null	,machine_driver_msisaac	,input_ports_msisaac	,null	,ROT270	,	"Taito Corporation", "Metal Soldier Isaac II", GAME_NOT_WORKING | GAME_NO_COCKTAIL)
}
