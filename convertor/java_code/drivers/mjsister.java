/*****************************************************************************

Mahjong Sisters (c) 1986 Toa Plan

	Driver by Uki

*****************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class mjsister
{
	
	#define MCLK 12000000
	
	
	
	VIDEO_START( mjsister );
	VIDEO_UPDATE( mjsister );
	
	static int mjsister_input_sel1;
	static int mjsister_input_sel2;
	
	static int rombank0,rombank1;
	
	static unsigned int dac_adr,dac_bank,dac_adr_s,dac_adr_e,dac_busy;
	
	/****************************************************************************/
	
	static void dac_callback(int param)
	{
		data8_t *DACROM = memory_region(REGION_SOUND1);
	
		DAC_data_w(0,DACROM[(dac_bank * 0x10000 + dac_adr++) & 0x1ffff]);
	
		if (((dac_adr & 0xff00 ) >> 8) !=  dac_adr_e )
			timer_set(TIME_IN_HZ(MCLK/1024),0,dac_callback);
		else
			dac_busy = 0;
	}
	
	public static WriteHandlerPtr mjsister_dac_adr_s_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		dac_adr_s = data;
	} };
	
	public static WriteHandlerPtr mjsister_dac_adr_e_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		dac_adr_e = data;
		dac_adr = dac_adr_s << 8;
	
		if (dac_busy == 0)
			timer_set(TIME_NOW,0,dac_callback);
	
		dac_busy = 1;
	} };
	
	static MACHINE_INIT( mjsister )
	{
		dac_busy = 0;
	}
	
	public static WriteHandlerPtr mjsister_banksel1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data8_t *BANKROM = memory_region(REGION_CPU1);
		int tmp = colorbank;
	
		switch (data)
		{
			case 0x0: rombank0 = 0 ; break;
			case 0x1: rombank0 = 1 ; break;
	
			case 0x2: mjsister_flip_screen = 0 ; break;
			case 0x3: mjsister_flip_screen = 1 ; break;
	
			case 0x4: colorbank &=0xfe; break;
			case 0x5: colorbank |=0x01; break;
			case 0x6: colorbank &=0xfd; break;
			case 0x7: colorbank |=0x02; break;
			case 0x8: colorbank &=0xfb; break;
			case 0x9: colorbank |=0x04; break;
	
			case 0xa: mjsister_video_enable = 0 ; break;
			case 0xb: mjsister_video_enable = 1 ; break;
	
			case 0xe: vrambank = 0 ; break;
			case 0xf: vrambank = 1 ; break;
	
			default:
				logerror("%04x p30_w:%02x\n",activecpu_get_pc(),data);
		}
	
		if (tmp != colorbank)
			mjsister_screen_redraw = 1;
	
		cpu_setbank(1,&BANKROM[rombank0*0x10000+rombank1*0x8000]+0x10000);
	} };
	
	public static WriteHandlerPtr mjsister_banksel2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data8_t *BANKROM = memory_region(REGION_CPU1);
	
		switch (data)
		{
			case 0xa: dac_bank = 0; break;
			case 0xb: dac_bank = 1; break;
	
			case 0xc: rombank1 = 0; break;
			case 0xd: rombank1 = 1; break;
	
			default:
				logerror("%04x p31_w:%02x\n",activecpu_get_pc(),data);
		}
	
		cpu_setbank(1,&BANKROM[rombank0*0x10000+rombank1*0x8000]+0x10000);
	} };
	
	public static WriteHandlerPtr mjsister_input_sel1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		mjsister_input_sel1 = data;
	} };
	
	public static WriteHandlerPtr mjsister_input_sel2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		mjsister_input_sel2 = data;
	} };
	
	public static ReadHandlerPtr mjsister_keys_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int p,i,ret = 0;
	
		p = mjsister_input_sel1 & 0x3f;
	//	p |= ((mjsister_input_sel2 & 8) << 4) | ((mjsister_input_sel2 & 0x20) << 1);
	
		for (i=0; i<6; i++)
		{
			if (p & (1 << i))
				ret |= readinputport(i+3);
		}
	
		return ret;
	} };
	
	/****************************************************************************/
	
	public static Memory_ReadAddress mjsister_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x77ff, MRA_ROM ),
		new Memory_ReadAddress( 0x7800, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_BANK1 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mjsister_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x77ff, MWA_ROM ),
		new Memory_WriteAddress( 0x7800, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress( 0x8000, 0xffff, mjsister_videoram_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort mjsister_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x11, 0x11, AY8910_read_port_0_r ),
		new IO_ReadPort( 0x20, 0x20, mjsister_keys_r ),
		new IO_ReadPort( 0x21, 0x21, input_port_2_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort mjsister_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x01, IOWP_NOP ), /* HD46505? */
		new IO_WritePort( 0x10, 0x10, AY8910_control_port_0_w ),
		new IO_WritePort( 0x12, 0x12, AY8910_write_port_0_w ),
		new IO_WritePort( 0x30, 0x30, mjsister_banksel1_w ),
		new IO_WritePort( 0x31, 0x31, mjsister_banksel2_w ),
		new IO_WritePort( 0x32, 0x32, mjsister_input_sel1_w ),
		new IO_WritePort( 0x33, 0x33, mjsister_input_sel2_w ),
		new IO_WritePort( 0x34, 0x34, mjsister_dac_adr_s_w ),
		new IO_WritePort( 0x35, 0x35, mjsister_dac_adr_e_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/****************************************************************************/
	
	static InputPortPtr input_ports_mjsister = new InputPortPtr(){ public void handler() { 
	
		PORT_START(); 	/* DSW1 (0) */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x03, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x08, 0x08, "Unknown 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Unknown 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Unknown 1-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT(           0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );/* service mode */
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW2 (1) */
		PORT_DIPNAME( 0x01, 0x01, "Unknown 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Unknown 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "Unknown 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Unknown 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Unknown 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Unknown 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Unknown 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Unknown 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* memory reset 1 */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* analyzer */
		PORT_SERVICE( 0x08, IP_ACTIVE_HIGH );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* memory reset 2 */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* pay out */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* hopper */
	
		PORT_START(); 	/* (3) PORT 1-0 */
		PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "P1 A",   KEYCODE_A, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "P1 B",   KEYCODE_B, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "P1 C",   KEYCODE_C, IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "P1 D",   KEYCODE_D, IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_HIGH, 0, "P1 Last Chance",   KEYCODE_RALT, IP_JOY_NONE );
		PORT_BIT( 0xe0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* (4) PORT 1-1 */
		PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "P1 E",     KEYCODE_E, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "P1 F",     KEYCODE_F, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "P1 G",     KEYCODE_G, IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "P1 H",     KEYCODE_H, IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_HIGH, 0, "P1 Take Score", KEYCODE_RCONTROL, IP_JOY_NONE );
		PORT_BIT( 0xe0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* (5) PORT 1-2 */
		PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "P1 I",   KEYCODE_I, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "P1 J",   KEYCODE_J, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "P1 K",   KEYCODE_K, IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "P1 L",   KEYCODE_L, IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_HIGH, 0, "P1 Double Up", KEYCODE_RSHIFT, IP_JOY_NONE );
		PORT_BIT( 0xe0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* (6) PORT 1-3 */
		PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "P1 M",   KEYCODE_M, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "P1 N",   KEYCODE_N, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "P1 Chi", KEYCODE_SPACE, IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "P1 Pon", KEYCODE_LALT, IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_HIGH, 0, "P1 Flip Flop", KEYCODE_X, IP_JOY_NONE );
		PORT_BIT( 0xe0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* (7) PORT 1-4 */
		PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "P1 Kan",   KEYCODE_LCONTROL, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "P1 Reach", KEYCODE_LSHIFT, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "P1 Ron", KEYCODE_Z, IP_JOY_NONE );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BITX(0x10, IP_ACTIVE_HIGH, 0, "P1 Big", KEYCODE_ENTER, IP_JOY_NONE );
		PORT_BIT( 0xe0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* (8) PORT 1-5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "P1 Bet", KEYCODE_2, IP_JOY_NONE );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BITX(0x10, IP_ACTIVE_HIGH, 0, "P1 Small", KEYCODE_BACKSPACE, IP_JOY_NONE );
		PORT_BIT( 0xe0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
	INPUT_PORTS_END(); }}; 
	
	/****************************************************************************/
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,      /* 1 chip */
		MCLK/8, /* 1.500 MHz */
		new int[] { 15 },
		new ReadHandlerPtr[] { input_port_0_r },
		new ReadHandlerPtr[] { input_port_1_r },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	static MACHINE_DRIVER_START( mjsister )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, MCLK/2) /* 6.000 MHz */
		MDRV_CPU_MEMORY(mjsister_readmem,mjsister_writemem)
		MDRV_CPU_PORTS(mjsister_readport,mjsister_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(mjsister)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256+4, 256)
		MDRV_VISIBLE_AREA(0, 255+4, 8, 247)
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(mjsister)
		MDRV_VIDEO_UPDATE(mjsister)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	
	MACHINE_DRIVER_END
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_mjsister = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );  /* CPU */
		ROM_LOAD( "ms00.bin",  0x00000, 0x08000, 0x9468c33b );
		ROM_LOAD( "ms01t.bin", 0x10000, 0x10000, 0xa7b6e530 );/* banked */
		ROM_LOAD( "ms02t.bin", 0x20000, 0x10000, 0x7752b5ba );/* banked */
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* samples */
		ROM_LOAD( "ms03.bin", 0x00000,  0x10000, 0x10a68e5e );
		ROM_LOAD( "ms04.bin", 0x10000,  0x10000, 0x641b09c1 );
	
		ROM_REGION( 0x00400, REGION_PROMS, 0 );/* color PROMs */
		ROM_LOAD( "ms05.bpr", 0x0000,  0x0100, 0xdd231a5f );// R
		ROM_LOAD( "ms06.bpr", 0x0100,  0x0100, 0xdf8e8852 );// G
		ROM_LOAD( "ms07.bpr", 0x0200,  0x0100, 0x6cb3a735 );// B
		ROM_LOAD( "ms08.bpr", 0x0300,  0x0100, 0xda2b3b38 );// ?
	ROM_END(); }}; 
	
	public static GameDriver driver_mjsister	   = new GameDriver("1986"	,"mjsister"	,"mjsister.java"	,rom_mjsister,null	,machine_driver_mjsister	,input_ports_mjsister	,null	,ROT0	,	"Toaplan", "Mahjong Sisters (Japan)" )
}
