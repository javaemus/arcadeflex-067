/*
  Taito L-System

  Monoprocessor games (1 main z80, no sound z80)
  - Plotting
  - Puzznic
  - Palamedes
  - Cachat / Tube-It
  - American Horseshoes
  - Play Girls
  - Play Girls 2
  - Cuby Bop

  Dual processor games
  - Kuri Kinton

  Triple processor games (2 main z80, 1 sound z80)
  - Fighting hawk
  - Raimais
  - Champion Wrestler

Notes:
- the system uses RAM based characters, which aren't really supported by the
  TileMap system, so we have to tilemap_mark_all_tiles_dirty() to compensate
- kurikina has some debug dip switches (invulnerability, slow motion) so might
  be a prototype. It also doesn't have service mode (or has it disabled).

TODO:
- champwr ADPCM interface is not entirely understood, it involves also addresses
  0xd000 and 0xe000, and maybe also YM2203 port B.
- slowdowns in fhawk, probably the interrupts have to be generated at a
  different time.
- plgirls doesn't work without a kludge because of an interrupt issue. This
  happens because the program enables interrupts before setting IM2, so the
  interrupt vector is interpreted as IM0, which is obviously bogus.
- The  puzznic protection is worked around,  but I'm not happy with it
  (the 68705-returned values are wrong, I'm sure of that).
- A bunch of control registers are simply ignored
- The source of   irqs 0 and  1 is  unknown, while  2 is vblank  (0 is
  usually   ignored  by the  program,    1   leads  to  reading    the
  ports... maybe vbl-in, vbl-out and hblank ?).
- Text Plane colours are only right in Cuby Bop once you've started a game
  & reset
- Scrolling in Cuby Bop's Game seems incorrect.

*/


/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class taito_l
{
	
	VIDEO_EOF( taitol );
	VIDEO_START( taitol );
	VIDEO_UPDATE( taitol );
	
	void taitol_chardef14_m(int offset);
	void taitol_chardef15_m(int offset);
	void taitol_chardef16_m(int offset);
	void taitol_chardef17_m(int offset);
	void taitol_chardef1c_m(int offset);
	void taitol_chardef1d_m(int offset);
	void taitol_chardef1e_m(int offset);
	void taitol_chardef1f_m(int offset);
	void taitol_bg18_m(int offset);
	void taitol_bg19_m(int offset);
	void taitol_char1a_m(int offset);
	void taitol_obj1b_m(int offset);
	
	
	
	
	static void (*rambank_modify_notifiers[12])(int) =
	{
		taitol_chardef14_m,	// 14
		taitol_chardef15_m,	// 15
		taitol_chardef16_m,	// 16
		taitol_chardef17_m,	// 17
	
		taitol_bg18_m,		// 18
		taitol_bg19_m,		// 19
		taitol_char1a_m,	// 1a
		taitol_obj1b_m,		// 1b
	
		taitol_chardef1c_m,	// 1c
		taitol_chardef1d_m,	// 1d
		taitol_chardef1e_m,	// 1e
		taitol_chardef1f_m,	// 1f
	};
	
	static void (*current_notifier[4])(int);
	static unsigned char *current_base[4];
	
	static int cur_rombank, cur_rombank2, cur_rambank[4];
	static int irq_adr_table[3];
	static int irq_enable = 0;
	
	unsigned char *taitol_rambanks;
	
	static unsigned char *palette_ram;
	static unsigned char *empty_ram;
	static unsigned char *shared_ram;
	
	static mem_read_handler porte0_r;
	static mem_read_handler porte1_r;
	static mem_read_handler portf0_r;
	static mem_read_handler portf1_r;
	
	static void palette_notifier(int addr)
	{
		unsigned char *p = palette_ram + (addr & ~1);
		unsigned char byte0 = *p++;
		unsigned char byte1 = *p;
	
		unsigned int b = (byte1 & 0xf) * 0x11;
		unsigned int g = ((byte0 & 0xf0)>>4) * 0x11;
		unsigned int r = (byte0 & 0xf) * 0x11;
	
		//	addr &= 0x1ff;
	
		if(addr > 0x200)
		{
	logerror("Large palette ? %03x (%04x)\n", addr, activecpu_get_pc());
		}
		else
		{
			//		r = g = b = ((addr & 0x1e) != 0)*255;
			palette_set_color(addr/2, r, g, b);
		}
	}
	
	public static InitMachinePtr machine_init = new InitMachinePtr() { public void handler() (void)
	{
		int i;
	
		taitol_rambanks = auto_malloc(0x1000*12);
		palette_ram = auto_malloc(0x1000);
		empty_ram = auto_malloc(0x1000);
	
		for(i=0;i<3;i++)
			irq_adr_table[i] = 0;
	
		irq_enable = 0;
	
		for(i=0;i<4;i++)
		{
			cur_rambank[i] = 0x80;
			current_base[i] = palette_ram;
			current_notifier[i] = palette_notifier;
			cpu_setbank(2+i, current_base[i]);
		}
		cur_rombank = cur_rombank2 = 0;
		cpu_setbank(1, memory_region(REGION_CPU1) + 0x10000);
	
		for(i=0;i<512;i++)
		{
			decodechar(Machine->gfx[2], i, taitol_rambanks,
					   Machine->drv->gfxdecodeinfo[2].gfxlayout);
			decodechar(Machine->gfx[2], i+512, taitol_rambanks + 0x4000,
					   Machine->drv->gfxdecodeinfo[2].gfxlayout);
		}
	} };
	
	
	static MACHINE_INIT( fhawk )
	{
		machine_init();
		porte0_r = 0;
		porte1_r = 0;
		portf0_r = 0;
		portf1_r = 0;
	}
	
	static MACHINE_INIT( raimais )
	{
		machine_init();
		porte0_r = 0;
		porte1_r = 0;
		portf0_r = 0;
		portf1_r = 0;
	}
	
	static MACHINE_INIT( champwr )
	{
		machine_init();
		porte0_r = 0;
		porte1_r = 0;
		portf0_r = 0;
		portf1_r = 0;
	}
	
	
	static MACHINE_INIT( kurikint )
	{
		machine_init();
		porte0_r = 0;
		porte1_r = 0;
		portf0_r = 0;
		portf1_r = 0;
	}
	
	
	static MACHINE_INIT( puzznic )
	{
		machine_init();
		porte0_r = input_port_0_r;
		porte1_r = input_port_1_r;
		portf0_r = input_port_2_r;
		portf1_r = input_port_3_r;
	}
	
	static MACHINE_INIT( plotting )
	{
		machine_init();
		porte0_r = input_port_0_r;
		porte1_r = input_port_1_r;
		portf0_r = input_port_2_r;
		portf1_r = input_port_3_r;
	}
	
	static MACHINE_INIT( palamed )
	{
		machine_init();
		porte0_r = input_port_0_r;
		porte1_r = 0;
		portf0_r = input_port_1_r;
		portf1_r = 0;
	}
	
	static MACHINE_INIT( cachat )
	{
		machine_init();
		porte0_r = input_port_0_r;
		porte1_r = 0;
		portf0_r = input_port_1_r;
		portf1_r = 0;
	}
	
	static MACHINE_INIT( horshoes )
	{
		machine_init();
		porte0_r = input_port_0_r;
		porte1_r = input_port_1_r;
		portf0_r = input_port_2_r;
		portf1_r = input_port_3_r;
	}
	
	
	
	static INTERRUPT_GEN( vbl_interrupt )
	{
		/* kludge to make plgirls boot */
		if (cpunum_get_reg(0,Z80_IM) != 2) return;
	
		// What is really generating interrupts 0 and 1 is still to be found
	
		if (cpu_getiloops() == 1 && (irq_enable & 1))
			cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, irq_adr_table[0]);
		else if (cpu_getiloops() == 2 && (irq_enable & 2))
			cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, irq_adr_table[1]);
		else if (cpu_getiloops() == 0 && (irq_enable & 4))
			cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, irq_adr_table[2]);
	}
	
	public static WriteHandlerPtr irq_adr_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	//logerror("irq_adr_table[%d] = %02x\n",offset,data);
		irq_adr_table[offset] = data;
	} };
	
	public static ReadHandlerPtr irq_adr_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return irq_adr_table[offset];
	} };
	
	public static WriteHandlerPtr irq_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	//logerror("irq_enable = %02x\n",data);
		irq_enable = data;
	} };
	
	public static ReadHandlerPtr irq_enable_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return irq_enable;
	} };
	
	
	public static WriteHandlerPtr rombankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		static int high = 0;
		if(cur_rombank != data)
		{
			if(data>high)
			{
				high = data;
				logerror("New rom size : %x\n", (high+1)*0x2000);
			}
	
	//		logerror("robs %d, %02x (%04x)\n", offset, data, activecpu_get_pc());
			cur_rombank = data;
			cpu_setbank(1, memory_region(REGION_CPU1)+0x10000+0x2000*cur_rombank);
		}
	} };
	
	public static WriteHandlerPtr rombank2switch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		static int high = 0;
	
		data &= 0xf;
	
		if(cur_rombank2 != data)
		{
			if(data>high)
			{
				high = data;
				logerror("New rom2 size : %x\n", (high+1)*0x4000);
			}
	
	//		logerror("robs2 %02x (%04x)\n", data, activecpu_get_pc());
	
			cur_rombank2 = data;
			cpu_setbank(6, memory_region(REGION_CPU3)+0x10000+0x4000*cur_rombank2);
		}
	} };
	
	public static ReadHandlerPtr rombankswitch_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cur_rombank;
	} };
	
	public static ReadHandlerPtr rombank2switch_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cur_rombank2;
	} };
	
	public static WriteHandlerPtr rambankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if(cur_rambank[offset]!=data)
		{
			cur_rambank[offset]=data;
	//logerror("rabs %d, %02x (%04x)\n", offset, data, activecpu_get_pc());
			if(data>=0x14 && data<=0x1f)
			{
				data -= 0x14;
				current_notifier[offset] = rambank_modify_notifiers[data];
				current_base[offset] = taitol_rambanks+0x1000*data;
			}
			else if (data == 0x80)
			{
				current_notifier[offset] = palette_notifier;
				current_base[offset] = palette_ram;
			}
			else
			{
	logerror("unknown rambankswitch %d, %02x (%04x)\n", offset, data, activecpu_get_pc());
				current_notifier[offset] = 0;
				current_base[offset] = empty_ram;
			}
			cpu_setbank(2+offset, current_base[offset]);
		}
	} };
	
	public static ReadHandlerPtr rambankswitch_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cur_rambank[offset];
	} };
	
	public static WriteHandlerPtr bank0_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if(current_base[0][offset]!=data)
		{
			current_base[0][offset] = data;
			if(current_notifier[0])
				current_notifier[0](offset);
		}
	} };
	
	public static WriteHandlerPtr bank1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if(current_base[1][offset]!=data)
		{
			current_base[1][offset] = data;
			if(current_notifier[1])
				current_notifier[1](offset);
		}
	} };
	
	public static WriteHandlerPtr bank2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if(current_base[2][offset]!=data)
		{
			current_base[2][offset] = data;
			if(current_notifier[2])
				current_notifier[2](offset);
		}
	} };
	
	public static WriteHandlerPtr bank3_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if(current_base[3][offset]!=data)
		{
			current_base[3][offset] = data;
			if(current_notifier[3])
				current_notifier[3](offset);
		}
	} };
	
	public static WriteHandlerPtr control2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_lockout_w(0,~data & 0x01);
		coin_lockout_w(1,~data & 0x02);
		coin_counter_w(0,data & 0x04);
		coin_counter_w(1,data & 0x08);
	} };
	
	static int extport;
	
	public static ReadHandlerPtr portA_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (extport == 0) return porte0_r(0);
		else return porte1_r(0);
	} };
	
	public static ReadHandlerPtr portB_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (extport == 0) return portf0_r(0);
		else return portf1_r(0);
	} };
	
	public static ReadHandlerPtr ym2203_data0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		extport = 0;
		return YM2203_read_port_0_r(offset);
	} };
	
	public static ReadHandlerPtr ym2203_data1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		extport = 1;
		return YM2203_read_port_0_r(offset);
	} };
	
	static int *mcu_reply;
	static int mcu_pos = 0, mcu_reply_len = 0;
	static int last_data_adr, last_data;
	
	static int puzznic_mcu_reply[] = { 0x50, 0x1f, 0xb6, 0xba, 0x06, 0x03, 0x47, 0x05, 0x00 };
	
	public static WriteHandlerPtr mcu_data_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		last_data = data;
		last_data_adr = activecpu_get_pc();
	//	logerror("mcu write %02x (%04x)\n", data, activecpu_get_pc());
		switch(data)
		{
		case 0x43:
			mcu_pos = 0;
			mcu_reply = puzznic_mcu_reply;
			mcu_reply_len = sizeof(puzznic_mcu_reply);
			break;
		}
	} };
	
	public static WriteHandlerPtr mcu_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	//	logerror("mcu control %02x (%04x)\n", data, activecpu_get_pc());
	} };
	
	public static ReadHandlerPtr mcu_data_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	//	logerror("mcu read (%04x) [%02x, %04x]\n", activecpu_get_pc(), last_data, last_data_adr);
		if(mcu_pos==mcu_reply_len)
			return 0;
	
		return mcu_reply[mcu_pos++];
	} };
	
	public static ReadHandlerPtr mcu_control_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	//	logerror("mcu control read (%04x)\n", activecpu_get_pc());
		return 0x1;
	} };
	
	#if 0
	public static WriteHandlerPtr sound_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		logerror("Sound_w %02x (%04x)\n", data, activecpu_get_pc());
	} };
	#endif
	
	public static ReadHandlerPtr shared_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return shared_ram[offset];
	} };
	
	public static WriteHandlerPtr shared_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		shared_ram[offset] = data;
	} };
	
	static int mux_ctrl = 0;
	
	public static ReadHandlerPtr mux_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		switch(mux_ctrl)
		{
		case 0:
			return input_port_0_r(0);
		case 1:
			return input_port_1_r(0);
		case 2:
			return input_port_2_r(0);
		case 3:
			return input_port_3_r(0);
		case 7:
			return input_port_4_r(0);
		default:
			logerror("Mux read from unknown port %d (%04x)\n", mux_ctrl, activecpu_get_pc());
			return 0xff;
		}
	} };
	
	public static WriteHandlerPtr mux_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		switch(mux_ctrl)
		{
		case 4:
			control2_w(0, data);
			break;
		default:
			logerror("Mux write to unknown port %d, %02x (%04x)\n", mux_ctrl, data, activecpu_get_pc());
		}
	} };
	
	public static WriteHandlerPtr mux_ctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		mux_ctrl = data;
	} };
	
	
	
	
	static int champwr_adpcm_start;
	
	public static WriteHandlerPtr champwr_adpcm_lo_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		champwr_adpcm_start = (champwr_adpcm_start & 0xff00ff) | (data << 8);
	} };
	
	public static WriteHandlerPtr champwr_adpcm_hi_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UINT8 *rom = memory_region(REGION_SOUND1);
		int romlen = memory_region_length(REGION_SOUND1);
		int length;
		int i;
	
		champwr_adpcm_start = ((champwr_adpcm_start & 0x00ffff) | (data << 16)) & (romlen-1);
		i = champwr_adpcm_start + 0x20;
		while (i < romlen && (rom[i] || rom[i+1] || rom[i+2] || rom[i+3]))
			i += 4;
		length = i - champwr_adpcm_start;
	
		ADPCM_play(0,champwr_adpcm_start,length*2);
	} };
	
	
	
	static int trackx,tracky;
	
	public static ReadHandlerPtr horshoes_tracky_reset_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		/* reset the trackball counter */
		tracky = readinputport(4);
		return 0;
	} };
	
	public static ReadHandlerPtr horshoes_trackx_reset_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		/* reset the trackball counter */
		trackx = readinputport(5);
		return 0;
	} };
	
	public static ReadHandlerPtr horshoes_tracky_lo_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (readinputport(4) - tracky) & 0xff;
	} };
	
	public static ReadHandlerPtr horshoes_tracky_hi_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (readinputport(4) - tracky) >> 8;
	} };
	
	public static ReadHandlerPtr horshoes_trackx_lo_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (readinputport(5) - trackx) & 0xff;
	} };
	
	public static ReadHandlerPtr horshoes_trackx_hi_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (readinputport(5) - trackx) >> 8;
	} };
	
	
	
	
	#define COMMON_BANKS_READ \
		{ 0x0000, 0x5fff, MRA_ROM },			\
		{ 0x6000, 0x7fff, MRA_BANK1 },			\
		{ 0xc000, 0xcfff, MRA_BANK2 },			\
		{ 0xd000, 0xdfff, MRA_BANK3 },			\
		{ 0xe000, 0xefff, MRA_BANK4 },			\
		{ 0xf000, 0xfdff, MRA_BANK5 },			\
		{ 0xfe00, 0xfe03, taitol_bankc_r },		\
		{ 0xfe04, 0xfe04, taitol_control_r },	\
		{ 0xff00, 0xff02, irq_adr_r },			\
		{ 0xff03, 0xff03, irq_enable_r },		\
		{ 0xff04, 0xff07, rambankswitch_r },	\
		{ 0xff08, 0xff08, rombankswitch_r }
	
	#define COMMON_BANKS_WRITE \
		{ 0x0000, 0x7fff, MWA_ROM },			\
		{ 0xc000, 0xcfff, bank0_w },			\
		{ 0xd000, 0xdfff, bank1_w },			\
		{ 0xe000, 0xefff, bank2_w },			\
		{ 0xf000, 0xfdff, bank3_w },			\
		{ 0xfe00, 0xfe03, taitol_bankc_w },		\
		{ 0xfe04, 0xfe04, taitol_control_w },	\
		{ 0xff00, 0xff02, irq_adr_w },			\
		{ 0xff03, 0xff03, irq_enable_w },		\
		{ 0xff04, 0xff07, rambankswitch_w },	\
		{ 0xff08, 0xff08, rombankswitch_w }
	
	#define COMMON_SINGLE_READ \
		{ 0xa000, 0xa000, YM2203_status_port_0_r },	\
		{ 0xa001, 0xa001, ym2203_data0_r },			\
		{ 0xa003, 0xa003, ym2203_data1_r },			\
		{ 0x8000, 0x9fff, MRA_RAM }
	
	#define COMMON_SINGLE_WRITE \
		{ 0xa000, 0xa000, YM2203_control_port_0_w },	\
		{ 0xa001, 0xa001, YM2203_write_port_0_w },		\
		{ 0x8000, 0x9fff, MWA_RAM }
	
	
	
	public static Memory_ReadAddress fhawk_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		new Memory_ReadAddress( 0x8000, 0x9fff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xbfff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress fhawk_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		new Memory_WriteAddress( 0x8000, 0x9fff, MWA_RAM, shared_ram ),
		new Memory_WriteAddress( 0xa000, 0xbfff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress fhawk_2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK6 ),
		new Memory_ReadAddress( 0xc800, 0xc800, MRA_NOP ),
		new Memory_ReadAddress( 0xc801, 0xc801, taitosound_comm_r ),
		new Memory_ReadAddress( 0xe000, 0xffff, shared_r ),
		new Memory_ReadAddress( 0xd000, 0xd000, input_port_0_r ),
		new Memory_ReadAddress( 0xd001, 0xd001, input_port_1_r ),
		new Memory_ReadAddress( 0xd002, 0xd002, input_port_2_r ),
		new Memory_ReadAddress( 0xd003, 0xd003, input_port_3_r ),
		new Memory_ReadAddress( 0xd007, 0xd007, input_port_4_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress fhawk_2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc000, rombank2switch_w ),
		new Memory_WriteAddress( 0xc800, 0xc800, taitosound_port_w ),
		new Memory_WriteAddress( 0xc801, 0xc801, taitosound_comm_w ),
		new Memory_WriteAddress( 0xd000, 0xd000, MWA_NOP ),	// Direct copy of input port 0
		new Memory_WriteAddress( 0xd004, 0xd004, control2_w ),
		new Memory_WriteAddress( 0xd005, 0xd006, MWA_NOP ),	// Always 0
		new Memory_WriteAddress( 0xe000, 0xffff, shared_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress fhawk_3_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_BANK7 ),
		new Memory_ReadAddress( 0x8000, 0x9fff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe000, MRA_NOP ),
		new Memory_ReadAddress( 0xe001, 0xe001, taitosound_slave_comm_r ),
		new Memory_ReadAddress( 0xf000, 0xf000, YM2203_status_port_0_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress fhawk_3_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x9fff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe000, taitosound_slave_port_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, taitosound_slave_comm_w ),
		new Memory_WriteAddress( 0xf000, 0xf000, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0xf001, 0xf001, YM2203_write_port_0_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress raimais_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0x8800, 0x8800, mux_r ),
		new Memory_ReadAddress( 0x8801, 0x8801, MRA_NOP ),	// Watchdog or interrupt ack (value ignored)
		new Memory_ReadAddress( 0x8c00, 0x8c00, MRA_NOP ),
		new Memory_ReadAddress( 0x8c01, 0x8c01, taitosound_comm_r ),
		new Memory_ReadAddress( 0xa000, 0xbfff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	public static Memory_WriteAddress raimais_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM, shared_ram ),
		new Memory_WriteAddress( 0x8800, 0x8800, mux_w ),
		new Memory_WriteAddress( 0x8801, 0x8801, mux_ctrl_w ),
		new Memory_WriteAddress( 0x8c00, 0x8c00, taitosound_port_w ),
		new Memory_WriteAddress( 0x8c01, 0x8c01, taitosound_comm_w ),
		new Memory_WriteAddress( 0xa000, 0xbfff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress raimais_2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, shared_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress raimais_2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, shared_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress raimais_3_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_BANK7 ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe000, YM2610_status_port_0_A_r ),
		new Memory_ReadAddress( 0xe001, 0xe001, YM2610_read_port_0_r ),
		new Memory_ReadAddress( 0xe002, 0xe002, YM2610_status_port_0_B_r ),
		new Memory_ReadAddress( 0xe200, 0xe200, MRA_NOP ),
		new Memory_ReadAddress( 0xe201, 0xe201, taitosound_slave_comm_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr sound_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		unsigned char *RAM = memory_region(REGION_CPU2);
		int banknum = (data - 1) & 3;
	
		cpu_setbank (7, &RAM [0x10000 + (banknum * 0x4000)]);
	} };
	
	public static Memory_WriteAddress raimais_3_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe000, YM2610_control_port_0_A_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, YM2610_data_port_0_A_w ),
		new Memory_WriteAddress( 0xe002, 0xe002, YM2610_control_port_0_B_w ),
		new Memory_WriteAddress( 0xe003, 0xe003, YM2610_data_port_0_B_w ),
		new Memory_WriteAddress( 0xe200, 0xe200, taitosound_slave_port_w ),
		new Memory_WriteAddress( 0xe201, 0xe201, taitosound_slave_comm_w ),
		new Memory_WriteAddress( 0xe400, 0xe403, MWA_NOP ), /* pan */
		new Memory_WriteAddress( 0xe600, 0xe600, MWA_NOP ), /* ? */
		new Memory_WriteAddress( 0xee00, 0xee00, MWA_NOP ), /* ? */
		new Memory_WriteAddress( 0xf000, 0xf000, MWA_NOP ), /* ? */
		new Memory_WriteAddress( 0xf200, 0xf200, sound_bankswitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress champwr_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		new Memory_ReadAddress( 0x8000, 0x9fff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xbfff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress champwr_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		new Memory_WriteAddress( 0x8000, 0x9fff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xbfff, MWA_RAM, shared_ram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress champwr_2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK6 ),
		new Memory_ReadAddress( 0xc000, 0xdfff, shared_r ),
		new Memory_ReadAddress( 0xe000, 0xe000, input_port_0_r ),
		new Memory_ReadAddress( 0xe001, 0xe001, input_port_1_r ),
		new Memory_ReadAddress( 0xe002, 0xe002, input_port_2_r ),
		new Memory_ReadAddress( 0xe003, 0xe003, input_port_3_r ),
		new Memory_ReadAddress( 0xe007, 0xe007, input_port_4_r ),
		new Memory_ReadAddress( 0xe008, 0xe00f, MRA_NOP ),
		new Memory_ReadAddress( 0xe800, 0xe800, MRA_NOP ),
		new Memory_ReadAddress( 0xe801, 0xe801, taitosound_comm_r ),
		new Memory_ReadAddress( 0xf000, 0xf000, rombank2switch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress champwr_2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, shared_w ),
		new Memory_WriteAddress( 0xe000, 0xe000, MWA_NOP ),	// Watchdog
		new Memory_WriteAddress( 0xe004, 0xe004, control2_w ),
		new Memory_WriteAddress( 0xe800, 0xe800, taitosound_port_w ),
		new Memory_WriteAddress( 0xe801, 0xe801, taitosound_comm_w ),
		new Memory_WriteAddress( 0xf000, 0xf000, rombank2switch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress champwr_3_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_BANK7 ),
		new Memory_ReadAddress( 0x8000, 0x8fff, MRA_RAM ),
		new Memory_ReadAddress( 0x9000, 0x9000, YM2203_status_port_0_r ),
		new Memory_ReadAddress( 0xa000, 0xa000, MRA_NOP ),
		new Memory_ReadAddress( 0xa001, 0xa001, taitosound_slave_comm_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress champwr_3_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x8fff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x9000, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0x9001, 0x9001, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0xa000, 0xa000, taitosound_slave_port_w ),
		new Memory_WriteAddress( 0xa001, 0xa001, taitosound_slave_comm_w ),
		new Memory_WriteAddress( 0xb000, 0xb000, champwr_adpcm_hi_w ),
		new Memory_WriteAddress( 0xc000, 0xc000, champwr_adpcm_lo_w ),
		new Memory_WriteAddress( 0xd000, 0xd000, MWA_NOP ),	/* ADPCM related */
		new Memory_WriteAddress( 0xe000, 0xe000, MWA_NOP ),	/* ADPCM related */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	public static Memory_ReadAddress kurikint_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		new Memory_ReadAddress( 0x8000, 0x9fff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa800, 0xa800, mux_r ),
		new Memory_ReadAddress( 0xa801, 0xa801, MRA_NOP ),	// Watchdog or interrupt ack (value ignored)
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress kurikint_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		new Memory_WriteAddress( 0x8000, 0x9fff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xa7ff, MWA_RAM, shared_ram ),
		new Memory_WriteAddress( 0xa800, 0xa800, mux_w ),
		new Memory_WriteAddress( 0xa801, 0xa801, mux_ctrl_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress kurikint_2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, shared_r ),
		new Memory_ReadAddress( 0xe800, 0xe800, YM2203_status_port_0_r ),
	#if 0
		new Memory_ReadAddress( 0xd000, 0xd000, input_port_0_r ),
		new Memory_ReadAddress( 0xd001, 0xd001, input_port_1_r ),
		new Memory_ReadAddress( 0xd002, 0xd002, input_port_2_r ),
		new Memory_ReadAddress( 0xd003, 0xd003, input_port_3_r ),
		new Memory_ReadAddress( 0xd007, 0xd007, input_port_4_r ),
	#endif
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress kurikint_2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, shared_w ),
		new Memory_WriteAddress( 0xe800, 0xe800, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0xe801, 0xe801, YM2203_write_port_0_w ),
	#if 0
		new Memory_WriteAddress( 0xc000, 0xc000, rombank2switch_w ),
	#endif
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	public static Memory_ReadAddress puzznic_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		COMMON_SINGLE_READ,
		new Memory_ReadAddress( 0xa800, 0xa800, MRA_NOP ),	// Watchdog
		new Memory_ReadAddress( 0xb000, 0xb7ff, MRA_RAM ),	// Wrong, used to overcome protection
		new Memory_ReadAddress( 0xb800, 0xb800, mcu_data_r ),
		new Memory_ReadAddress( 0xb801, 0xb801, mcu_control_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress puzznic_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		COMMON_SINGLE_WRITE,
		new Memory_WriteAddress( 0xb000, 0xb7ff, MWA_RAM ),	// Wrong, used to overcome protection
		new Memory_WriteAddress( 0xb800, 0xb800, mcu_data_w ),
		new Memory_WriteAddress( 0xb801, 0xb801, mcu_control_w ),
		new Memory_WriteAddress( 0xbc00, 0xbc00, MWA_NOP ),	// Control register, function unknown
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress plotting_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		COMMON_SINGLE_READ,
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress plotting_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		COMMON_SINGLE_WRITE,
		new Memory_WriteAddress( 0xa800, 0xa800, MWA_NOP ),	// Watchdog or interrupt ack
		new Memory_WriteAddress( 0xb800, 0xb800, MWA_NOP ),	// Control register, function unknown
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress palamed_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		COMMON_SINGLE_READ,
		new Memory_ReadAddress( 0xa800, 0xa800, input_port_2_r ),
		new Memory_ReadAddress( 0xa801, 0xa801, input_port_3_r ),
		new Memory_ReadAddress( 0xa802, 0xa802, input_port_4_r ),
		new Memory_ReadAddress( 0xb001, 0xb001, MRA_NOP ),	// Watchdog or interrupt ack
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress palamed_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		COMMON_SINGLE_WRITE,
		new Memory_WriteAddress( 0xa803, 0xa803, MWA_NOP ),	// Control register, function unknown
		new Memory_WriteAddress( 0xb000, 0xb000, MWA_NOP ),	// Control register, function unknown (copy of 8822)
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress cachat_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		COMMON_SINGLE_READ,
		new Memory_ReadAddress( 0xa800, 0xa800, input_port_2_r ),
		new Memory_ReadAddress( 0xa801, 0xa801, input_port_3_r ),
		new Memory_ReadAddress( 0xa802, 0xa802, input_port_4_r ),
		new Memory_ReadAddress( 0xb001, 0xb001, MRA_NOP ),	// Watchdog or interrupt ack (value ignored)
		new Memory_ReadAddress( 0xfff8, 0xfff8, rombankswitch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress cachat_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		COMMON_SINGLE_WRITE,
		new Memory_WriteAddress( 0xa803, 0xa803, MWA_NOP ),	// Control register, function unknown
		new Memory_WriteAddress( 0xb000, 0xb000, MWA_NOP ),	// Control register, function unknown
		new Memory_WriteAddress( 0xfff8, 0xfff8, rombankswitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress horshoes_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_READ,
		COMMON_SINGLE_READ,
		new Memory_ReadAddress( 0xa800, 0xa800, horshoes_tracky_lo_r ),
		new Memory_ReadAddress( 0xa802, 0xa802, horshoes_tracky_reset_r ),
		new Memory_ReadAddress( 0xa803, 0xa803, horshoes_trackx_reset_r ),
		new Memory_ReadAddress( 0xa804, 0xa804, horshoes_tracky_hi_r ),
		new Memory_ReadAddress( 0xa808, 0xa808, horshoes_trackx_lo_r ),
		new Memory_ReadAddress( 0xa80c, 0xa80c, horshoes_trackx_hi_r ),
		new Memory_ReadAddress( 0xb801, 0xb801, MRA_NOP ),	// Watchdog or interrupt ack
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress horshoes_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		COMMON_BANKS_WRITE,
		COMMON_SINGLE_WRITE,
		new Memory_WriteAddress( 0xb802, 0xb802, horshoes_bankg_w ),
		new Memory_WriteAddress( 0xbc00, 0xbc00, MWA_NOP ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	/***********************************************************
				 INPUT PORTS, DIPs
	***********************************************************/
	
	#define TAITO_COINAGE_WORLD_8 \
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coin_A") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") ); \
		PORT_DIPSETTING(    0x10, DEF_STR( "3C_1C") ); \
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") ); \
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") ); \
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coin_B") ); \
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_2C") ); \
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_3C") ); \
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_4C") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_6C") );
	
	#define TAITO_COINAGE_JAPAN_8 \
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coin_A") ); \
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") ); \
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") ); \
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_2C") ); \
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coin_B") ); \
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") ); \
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") ); \
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
	
	#define TAITO_COINAGE_JAPAN_NEW_8 \
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coin_A") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") ); \
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") ); \
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") ); \
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_2C") ); \
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coin_B") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") ); \
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") ); \
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") ); \
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
	
	#define TAITO_COINAGE_US_8 \
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coinage") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") ); \
		PORT_DIPSETTING(    0x10, DEF_STR( "3C_1C") ); \
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") ); \
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") ); \
		PORT_DIPNAME( 0xc0, 0xc0, "Price to Continue" );\
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") ); \
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") ); \
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_1C") ); \
		PORT_DIPSETTING(    0xc0, "Same as Start" );
	
	#define TAITO_DIFFICULTY_8 \
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") ); \
		PORT_DIPSETTING(    0x02, "Easy" );\
		PORT_DIPSETTING(    0x03, "Medium" );\
		PORT_DIPSETTING(    0x01, "Hard" );\
		PORT_DIPSETTING(    0x00, "Hardest" );
	
	#define TAITO_L_PLAYERS_INPUT( player ) \
		PORT_START();  \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | player );\
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | player );\
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | player );\
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | player );\
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | player );\
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | player );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define TAITO_L_SYSTEM_INPUT( type, impulse ) \
		PORT_START();  \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_TILT );\
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE1 );\
		PORT_BIT_IMPULSE( 0x04, type, IPT_COIN1, impulse );\
		PORT_BIT_IMPULSE( 0x08, type, IPT_COIN2, impulse );\
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
	#define TAITO_L_DSWA_2_4 \
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") ); \
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "On") ); \
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );\
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
	
	static InputPortPtr input_ports_fhawk = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_JAPAN_8
	
		PORT_START(); 
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unused") );  // all in manual
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x30, "3" );
		PORT_DIPSETTING(    0x20, "4" );
		PORT_DIPSETTING(    0x10, "5" );
		PORT_DIPSETTING(    0x00, "6" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER1 )
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER2 )
	
		TAITO_L_SYSTEM_INPUT( IP_ACTIVE_LOW, 4 )
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_raimais = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_JAPAN_8
	
		PORT_START(); 
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "80k and 160k" );
		PORT_DIPSETTING(    0x0c, "80k only" );
		PORT_DIPSETTING(    0x04, "160k only" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x30, "3" );
		PORT_DIPSETTING(    0x20, "4" );
		PORT_DIPSETTING(    0x10, "5" );
		PORT_DIPSETTING(    0x00, "6" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "Allow Continue" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER1 )
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER2 )
	
		TAITO_L_SYSTEM_INPUT( IP_ACTIVE_HIGH, 1 )
	INPUT_PORTS_END(); }}; 
	
	#define CHAMPWR_DSWB \
		PORT_START();  \
		TAITO_DIFFICULTY_8 \
		PORT_DIPNAME( 0x0c, 0x0c, "Time" );\
		PORT_DIPSETTING(    0x08, "2 minutes" );\
		PORT_DIPSETTING(    0x0c, "3 minutes" );\
		PORT_DIPSETTING(    0x04, "4 minutes" );\
		PORT_DIPSETTING(    0x00, "5 minutes" );\
		PORT_DIPNAME( 0x30, 0x30, "1 minute Lenght" );\
		PORT_DIPSETTING(    0x00, "30 sec" );\
		PORT_DIPSETTING(    0x10, "40 sec" );\
		PORT_DIPSETTING(    0x30, "50 sec" );\
		PORT_DIPSETTING(    0x20, "60 sec" );\
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );\
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x40, DEF_STR( "On") ); \
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") ); \
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
	#define CHAMPWR_INPUTS \
	 	PORT_START();  \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );\
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );\
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );\
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );\
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );\
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_TILT );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );\
	 \
		PORT_START();  \
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_LOW, IPT_COIN2, 1 );\
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_LOW, IPT_COIN1, 1 );\
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );\
	 \
		PORT_START();  \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );\
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );\
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );\
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );\
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );\
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
	
	static InputPortPtr input_ports_champwr = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unused") );  // all 2 in manual
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_WORLD_8
	
		CHAMPWR_DSWB
	
		CHAMPWR_INPUTS
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_champwrj = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_JAPAN_8
	
		CHAMPWR_DSWB
	
		CHAMPWR_INPUTS
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_champwru = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_US_8
	
		CHAMPWR_DSWB
	
		CHAMPWR_INPUTS
	INPUT_PORTS_END(); }}; 
	
	#define KURIKIN_DSWB \
		PORT_START();  \
		TAITO_DIFFICULTY_8 \
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") ); \
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "On") ); \
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") ); \
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "On") ); \
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") ); \
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "On") ); \
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") ); \
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x00, DEF_STR( "On") ); \
		PORT_DIPNAME( 0x40, 0x40, "Bosses' messages" );\
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") ); \
		PORT_DIPSETTING(    0x40, DEF_STR( "On") ); \
		PORT_DIPNAME( 0x80, 0x80, "Allow Continue" );\
		PORT_DIPSETTING(    0x80, "5 Times" );\
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
	static InputPortPtr input_ports_kurikint = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_WORLD_8
	
		KURIKIN_DSWB
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER1 )
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER2 )
	
		TAITO_L_SYSTEM_INPUT( IP_ACTIVE_HIGH, 4 )
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_kurikinj = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_JAPAN_8
	
		KURIKIN_DSWB
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER1 )
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER2 )
	
		TAITO_L_SYSTEM_INPUT( IP_ACTIVE_HIGH, 4 )
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_kurikina = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		TAITO_COINAGE_WORLD_8
	
		PORT_START(); 
		PORT_BITX(    0x01, 0x01, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Level select", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x02, 0x02, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x08, "Easy" );
		PORT_DIPSETTING(    0x0c, "Medium" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Bosses' messages" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Slow Motion", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER1 )
	
		TAITO_L_PLAYERS_INPUT( IPF_PLAYER2 )
	
		TAITO_L_SYSTEM_INPUT( IP_ACTIVE_HIGH, 4 )
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_puzznic = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
		TAITO_L_DSWA_2_4
		/* There is no Coin B in the Manuals */
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		/* Difficulty controls the Timer Speed (how many seconds are there in a minute) */
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x0c, 0x0c, "Retries" );
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPSETTING(    0x04, "1" );
		PORT_DIPSETTING(    0x0c, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPNAME( 0x10, 0x10, "Bombs" );
		PORT_DIPSETTING(    0x10, "0" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPNAME( 0x20, 0x20, "Girls" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Yes") );
		PORT_DIPNAME( 0xc0, 0xc0, "Terms of Replay" );
		PORT_DIPSETTING(    0x40, "Stage one step back/Timer continuous" );
		PORT_DIPSETTING(    0xc0, "Stage reset to start/Timer continuous" );
		PORT_DIPSETTING(    0x80, "Stage reset to start/Timer reset to start" );
	//	PORT_DIPSETTING(    0x00, "No Use" );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	
		PORT_START();  /* Not read yet. There is no Coin_B in manual */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_plotting = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, "Max Players" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x01, "2" );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_WORLD_8
	
		PORT_START(); 
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x30, "Misses" );
		PORT_DIPSETTING(    0x20, "1" );
		PORT_DIPSETTING(    0x30, "2" );
		PORT_DIPSETTING(    0x10, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_palamed = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		TAITO_L_DSWA_2_4
		TAITO_COINAGE_JAPAN_NEW_8
	
		PORT_START(); 
		/* Difficulty controls how faster falls the dice lines */
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
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
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Versus Mode" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Yes") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_cachat = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		TAITO_COINAGE_JAPAN_NEW_8
	
		PORT_START(); 
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
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
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_tubeit = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		TAITO_COINAGE_WORLD_8
	
		PORT_START(); 
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
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
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_horshoes = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		TAITO_L_DSWA_2_4
		/* The Coinage is the same as US, but it has no Continue Price feature */
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		/* Not for sure, the CPU seems to play better when set to Hardest */
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, "Time" );
		PORT_DIPSETTING(    0x00, "20 sec" );
		PORT_DIPSETTING(    0x04, "30 sec" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Innings" );
		PORT_DIPSETTING(    0x10, "3 per Credit" );
		PORT_DIPSETTING(    0x00, "9 per Credit" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_ANALOG( 0xffff, 0x0000, IPT_TRACKBALL_Y | IPF_REVERSE, 50, 30, 0, 0 );
	
		PORT_START(); 
		PORT_ANALOG( 0xffff, 0x0000, IPT_TRACKBALL_X, 50, 30, 0, 0 );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_plgirls = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x38, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_4C") );
	//	PORT_DIPSETTING(    0x30, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		/* Difficulty controls the Ball Speed */
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
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
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_plgirls2 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		TAITO_COINAGE_JAPAN_8
	
		PORT_START(); 
		/* Difficulty controls the number of hits requiered to destroy enemies */
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x18, 0x18, "Life" );
		PORT_DIPSETTING(    0x10, "3/2/3" );
		PORT_DIPSETTING(    0x18, "4/3/4" );
		PORT_DIPSETTING(    0x08, "5/4/5" );
		PORT_DIPSETTING(    0x00, "6/5/6" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_cubybop = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		TAITO_COINAGE_JAPAN_NEW_8
	
		PORT_START(); 
		TAITO_DIFFICULTY_8
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
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
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	
	
	static GfxLayout bg1_layout = new GfxLayout
	(
		8, 8,
		RGN_FRAC(1,2),
		4,
		new int[] { RGN_FRAC(1,2)+0, RGN_FRAC(1,2)+4, 0, 4 },
		new int[] { 3, 2, 1, 0, 8+3, 8+2, 8+1, 8+0 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		8*8*2
	);
	
	static GfxLayout bg2_layout = new GfxLayout
	(
		8, 8,
		RGN_FRAC(1,1),
		4,
		new int[] { 8, 12, 0, 4 },
		new int[] { 3, 2, 1, 0, 19, 18, 17, 16 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		8*8*4
	);
	
	#define O 8*8*2
	#define O2 2*O
	static GfxLayout sp1_layout = new GfxLayout
	(
		16, 16,
		RGN_FRAC(1,2),
		4,
		new int[] { RGN_FRAC(1,2)+0, RGN_FRAC(1,2)+4, 0, 4 },
		new int[] { 3, 2, 1, 0, 8+3, 8+2, 8+1, 8+0, O+3, O+2, O+1, O+0, O+8+3, O+8+2, O+8+1, O+8+0 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16, O2+0*16, O2+1*16, O2+2*16, O2+3*16, O2+4*16, O2+5*16, O2+6*16, O2+7*16 },
		8*8*2*4
	);
	#undef O
	#undef O2
	
	#define O 8*8*4
	#define O2 2*O
	static GfxLayout sp2_layout = new GfxLayout
	(
		16, 16,
		RGN_FRAC(1,1),
		4,
		new int[] { 8, 12, 0, 4 },
		new int[] { 3, 2, 1, 0, 19, 18, 17, 16, O+3, O+2, O+1, O+0, O+19, O+18, O+17, O+16 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32, O2+0*32, O2+1*32, O2+2*32, O2+3*32, O2+4*32, O2+5*32, O2+6*32, O2+7*32 },
		8*8*4*4
	);
	#undef O
	#undef O2
	
	static GfxLayout char_layout = new GfxLayout
	(
		8, 8,
		1024,
		4,
		new int[] { 8, 12, 0, 4 },
		new int[] { 3, 2, 1, 0, 19, 18, 17, 16},
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		8*8*4
	);
	
	static GfxDecodeInfo gfxdecodeinfo1[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, bg1_layout, 0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, sp1_layout, 0, 16 ),
		new GfxDecodeInfo( 0,           0, char_layout,  0, 16 ),  // Ram-based
		new GfxDecodeInfo( -1 )
	};
	
	static GfxDecodeInfo gfxdecodeinfo2[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, bg2_layout, 0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, sp2_layout, 0, 16 ),
		new GfxDecodeInfo( 0,           0, char_layout,  0, 16 ),  // Ram-based
		new GfxDecodeInfo( -1 )
	};
	
	
	
	static void irqhandler(int irq)
	{
		cpu_set_irq_line(1,0,irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	public static WriteHandlerPtr portA_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		static int cur_bank = 0;
	
		if (cur_bank != (data & 0x03) )
		{
			int bankaddress;
			unsigned char *RAM = memory_region(REGION_CPU2);
	
			cur_bank = data & 0x03;
			bankaddress = 0x10000 + (cur_bank-1) * 0x4000;
			cpu_setbank(7,&RAM[bankaddress]);
			//logerror ("YM2203 bank change val=%02x  pc=%04x\n",cur_bank, activecpu_get_pc() );
		}
	} };
	
	static struct YM2203interface ym2203_interface_triple =
	{
		1,			/* 1 chip */
		3000000,	/* ??? */
		{ YM2203_VOL(80,20) },
		{ 0 },
		{ 0 },
		{ portA_w },
		{ 0 },
		{ irqhandler }
	};
	
	static struct ADPCMinterface adpcm_interface =
	{
		1,			/* 1 channel */
		8000,		/* 8000Hz playback? */
		REGION_SOUND1,	/* memory region */
		{ 80 } 	/* volume */
	};
	
	
	static struct YM2610interface ym2610_interface =
	{
		1,	/* 1 chip */
		8000000,	/* 8 MHz */
		{ 25 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ irqhandler },
		{ REGION_SOUND1 },
		{ REGION_SOUND1 },
		{ YM3012_VOL(100,MIXER_PAN_LEFT,100,MIXER_PAN_RIGHT) }
	};
	
	static struct YM2203interface ym2203_interface_double =
	{
		1,			/* 1 chip */
		3000000,	/* ??? */
		{ YM2203_VOL(80,20) },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 }
	};
	
	static struct YM2203interface ym2203_interface_single =
	{
		1,			/* 1 chip */
		3000000,	/* ??? */
		{ YM2203_VOL(80,20) },
		{ portA_r },
		{ portB_r },
		{ 0 },
		{ 0 },
		{ 0 }
	};
	
	
	static MACHINE_DRIVER_START( fhawk )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("cpu1", Z80, 6000000)	/* ? xtal is 13.33056 */
		MDRV_CPU_MEMORY(fhawk_readmem,fhawk_writemem)
		MDRV_CPU_VBLANK_INT(vbl_interrupt,3)
	
		MDRV_CPU_ADD_TAG("sound", Z80, 4000000)	/* ? xtal is 13.33056 */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(fhawk_3_readmem,fhawk_3_writemem)
	
		MDRV_CPU_ADD_TAG("cpu2", Z80, 6000000)	/* ? xtal is 13.33056 */
		MDRV_CPU_MEMORY(fhawk_2_readmem,fhawk_2_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(100)
	
		MDRV_MACHINE_INIT(fhawk)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(40*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 40*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo2)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(taitol)
		MDRV_VIDEO_EOF(taitol)
		MDRV_VIDEO_UPDATE(taitol)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("2203", YM2203, ym2203_interface_triple)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( champwr )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(fhawk)
		MDRV_CPU_MODIFY("cpu1")
		MDRV_CPU_MEMORY(champwr_readmem,champwr_writemem)
	
		MDRV_CPU_MODIFY("sound")
		MDRV_CPU_MEMORY(champwr_3_readmem,champwr_3_writemem)
	
		MDRV_CPU_MODIFY("cpu2")
		MDRV_CPU_MEMORY(champwr_2_readmem,champwr_2_writemem)
	
		MDRV_MACHINE_INIT(champwr)
	
		/* sound hardware */
		MDRV_SOUND_ADD(ADPCM, adpcm_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( raimais )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(fhawk)
		MDRV_CPU_MODIFY("cpu1")
		MDRV_CPU_MEMORY(raimais_readmem,raimais_writemem)
	
		MDRV_CPU_MODIFY("sound")
		MDRV_CPU_MEMORY(raimais_3_readmem,raimais_3_writemem)
	
		MDRV_CPU_MODIFY("cpu2")
		MDRV_CPU_MEMORY(raimais_2_readmem,raimais_2_writemem)
	
		MDRV_MACHINE_INIT(raimais)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("2203", YM2610, ym2610_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( kurikint )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 6000000)	/* ? xtal is 13.33056 */
		MDRV_CPU_MEMORY(kurikint_readmem,kurikint_writemem)
		MDRV_CPU_VBLANK_INT(vbl_interrupt,3)
	
		MDRV_CPU_ADD(Z80, 6000000)	/* ? xtal is 13.33056 */
		MDRV_CPU_MEMORY(kurikint_2_readmem,kurikint_2_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(100)
	
		MDRV_MACHINE_INIT(kurikint)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(40*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 40*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo2)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(taitol)
		MDRV_VIDEO_EOF(taitol)
		MDRV_VIDEO_UPDATE(taitol)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface_double)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( kurikina )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(kurikint)
	
		/* video hardware */
		MDRV_GFXDECODE(gfxdecodeinfo1)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( plotting )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", Z80, 6000000)	/* ? xtal is 13.33056 */
		MDRV_CPU_MEMORY(plotting_readmem,plotting_writemem)
		MDRV_CPU_VBLANK_INT(vbl_interrupt,3)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(plotting)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(40*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 40*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo1)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(taitol)
		MDRV_VIDEO_EOF(taitol)
		MDRV_VIDEO_UPDATE(taitol)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface_single)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( puzznic )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(plotting)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(puzznic_readmem,puzznic_writemem)
	
		MDRV_MACHINE_INIT(puzznic)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( horshoes )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(plotting)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(horshoes_readmem,horshoes_writemem)
	
		MDRV_MACHINE_INIT(horshoes)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( palamed )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(plotting)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(palamed_readmem,palamed_writemem)
	
		MDRV_MACHINE_INIT(palamed)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( cachat )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(plotting)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(cachat_readmem,cachat_writemem)
	
		MDRV_MACHINE_INIT(cachat)
	MACHINE_DRIVER_END
	
	
	
	static RomLoadPtr rom_raimais = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xb0000, REGION_CPU1, 0 );
		ROM_LOAD( "b36-08-1.bin", 0x00000, 0x20000, 0x6cc8f79f );
		ROM_RELOAD(               0x10000, 0x20000 );
		ROM_LOAD( "b36-09.bin",   0x30000, 0x20000, 0x9c466e43 );
	
		ROM_REGION( 0x1c000, REGION_CPU2, 0 );/* sound (sndhrdw/rastan.c wants it as #2 */
		ROM_LOAD( "b36-06.bin",   0x00000, 0x4000, 0x29bbc4f8 );
		ROM_CONTINUE(             0x10000, 0xc000 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );
		ROM_LOAD( "b36-07.bin",   0x00000, 0x10000, 0x4f3737e6 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "b36-01.bin",   0x00000, 0x80000, 0x89355cb2 );
		ROM_LOAD( "b36-02.bin",   0x80000, 0x80000, 0xe71da5db );
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 );
		ROM_LOAD( "b36-03.bin",   0x00000, 0x80000, 0x96166516 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_fhawk = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xb0000, REGION_CPU1, 0 );
		ROM_LOAD( "b70-07.bin", 0x00000, 0x20000, 0x939114af );
		ROM_RELOAD(             0x10000, 0x20000 );
		ROM_LOAD( "b70-03.bin", 0x30000, 0x80000, 0x42d5a9b8 );
	
		ROM_REGION( 0x1c000, REGION_CPU2, 0 );/* sound (sndhrdw/rastan.c wants it as #2 */
		ROM_LOAD( "b70-09.bin", 0x00000, 0x4000, 0x85cccaa2 );
		ROM_CONTINUE(           0x10000, 0xc000 );
	
		ROM_REGION( 0x30000, REGION_CPU3, 0 );
		ROM_LOAD( "b70-08.bin", 0x00000, 0x20000, 0x4d795f48 );
		ROM_RELOAD(             0x10000, 0x20000 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "b70-01.bin", 0x00000, 0x80000, 0xfcdf67e2 );
		ROM_LOAD( "b70-02.bin", 0x80000, 0x80000, 0x35f7172e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_champwr = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xf0000, REGION_CPU1, 0 );
		ROM_LOAD( "c01-13.rom", 0x00000, 0x20000, 0x7ef47525 );
		ROM_RELOAD(             0x10000, 0x20000 );
		ROM_LOAD( "c01-04.rom", 0x30000, 0x20000, 0x358bd076 );
	
		ROM_REGION( 0x1c000, REGION_CPU2, 0 );/* sound (sndhrdw/rastan.c wants it as #2 */
		ROM_LOAD( "c01-08.rom", 0x00000, 0x4000, 0x810efff8 );
		ROM_CONTINUE(           0x10000, 0xc000 );
	
		ROM_REGION( 0x30000, REGION_CPU3, 0 );
		ROM_LOAD( "c01-07.rom", 0x00000, 0x20000, 0x5117c98f );
		ROM_RELOAD(             0x10000, 0x20000 );
	
		ROM_REGION( 0x180000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "c01-01.rom", 0x000000, 0x80000, 0xf302e6e9 );
		ROM_LOAD( "c01-02.rom", 0x080000, 0x80000, 0x1e0476c4 );
		ROM_LOAD( "c01-03.rom", 0x100000, 0x80000, 0x2a142dbc );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* ADPCM samples */
		ROM_LOAD( "c01-05.rom", 0x00000, 0x20000, 0x22efad4a );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_champwru = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xf0000, REGION_CPU1, 0 );
		ROM_LOAD( "c01-12.rom", 0x00000, 0x20000, 0x09f345b3 );
		ROM_RELOAD(             0x10000, 0x20000 );
		ROM_LOAD( "c01-04.rom", 0x30000, 0x20000, 0x358bd076 );
	
		ROM_REGION( 0x1c000, REGION_CPU2, 0 );/* sound (sndhrdw/rastan.c wants it as #2 */
		ROM_LOAD( "c01-08.rom", 0x00000, 0x4000, 0x810efff8 );
		ROM_CONTINUE(           0x10000, 0xc000 );
	
		ROM_REGION( 0x30000, REGION_CPU3, 0 );
		ROM_LOAD( "c01-07.rom", 0x00000, 0x20000, 0x5117c98f );
		ROM_RELOAD(             0x10000, 0x20000 );
	
		ROM_REGION( 0x180000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "c01-01.rom", 0x000000, 0x80000, 0xf302e6e9 );
		ROM_LOAD( "c01-02.rom", 0x080000, 0x80000, 0x1e0476c4 );
		ROM_LOAD( "c01-03.rom", 0x100000, 0x80000, 0x2a142dbc );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* ADPCM samples */
		ROM_LOAD( "c01-05.rom", 0x00000, 0x20000, 0x22efad4a );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_champwrj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xf0000, REGION_CPU1, 0 );
		ROM_LOAD( "c01-06.bin", 0x00000, 0x20000, 0x90fa1409 );
		ROM_RELOAD(             0x10000, 0x20000 );
		ROM_LOAD( "c01-04.rom", 0x30000, 0x20000, 0x358bd076 );
	
		ROM_REGION( 0x1c000, REGION_CPU2, 0 );/* sound (sndhrdw/rastan.c wants it as #2 */
		ROM_LOAD( "c01-08.rom", 0x00000, 0x4000, 0x810efff8 );
		ROM_CONTINUE(           0x10000, 0xc000 );
	
		ROM_REGION( 0x30000, REGION_CPU3, 0 );
		ROM_LOAD( "c01-07.rom", 0x00000, 0x20000, 0x5117c98f );
		ROM_RELOAD(             0x10000, 0x20000 );
	
		ROM_REGION( 0x180000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "c01-01.rom", 0x000000, 0x80000, 0xf302e6e9 );
		ROM_LOAD( "c01-02.rom", 0x080000, 0x80000, 0x1e0476c4 );
		ROM_LOAD( "c01-03.rom", 0x100000, 0x80000, 0x2a142dbc );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* ADPCM samples */
		ROM_LOAD( "c01-05.rom", 0x00000, 0x20000, 0x22efad4a );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_kurikint = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xb0000, REGION_CPU1, 0 );
		ROM_LOAD( "b42-09.2",    0x00000, 0x20000, 0xe97c4394 );
		ROM_RELOAD(              0x10000, 0x20000 );
		ROM_LOAD( "b42-06.6",    0x30000, 0x20000, 0xfa15fd65 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "b42-07.22",   0x00000, 0x10000, 0x0f2719c0 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "b42-01.1",    0x00000, 0x80000, 0x7d1a1fec );
		ROM_LOAD( "b42-02.5",    0x80000, 0x80000, 0x1a52e65c );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_kurikinu = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xb0000, REGION_CPU1, 0 );
		ROM_LOAD( "b42-08.2",    0x00000, 0x20000, 0x7075122e );
		ROM_RELOAD(              0x10000, 0x20000 );
		ROM_LOAD( "b42-06.6",    0x30000, 0x20000, 0xfa15fd65 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "b42-07.22",   0x00000, 0x10000, 0x0f2719c0 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "b42-01.1",    0x00000, 0x80000, 0x7d1a1fec );
		ROM_LOAD( "b42-02.5",    0x80000, 0x80000, 0x1a52e65c );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_kurikinj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xb0000, REGION_CPU1, 0 );
		ROM_LOAD( "b42_05.2",    0x00000, 0x20000, 0x077222b8 );
		ROM_RELOAD(              0x10000, 0x20000 );
		ROM_LOAD( "b42-06.6",    0x30000, 0x20000, 0xfa15fd65 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "b42-07.22",   0x00000, 0x10000, 0x0f2719c0 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "b42-01.1",    0x00000, 0x80000, 0x7d1a1fec );
		ROM_LOAD( "b42-02.5",    0x80000, 0x80000, 0x1a52e65c );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_kurikina = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0xb0000, REGION_CPU1, 0 );
		ROM_LOAD( "kk_ic2.rom",  0x00000, 0x20000, 0x908603f2 );
		ROM_RELOAD(              0x10000, 0x20000 );
		ROM_LOAD( "kk_ic6.rom",  0x30000, 0x20000, 0xa4a957b1 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );
		ROM_LOAD( "b42-07.22",   0x00000, 0x10000, 0x0f2719c0 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "kk_1-1l.rom", 0x00000, 0x20000, 0xdf1d4fcd );
		ROM_LOAD( "kk_2-2l.rom", 0x20000, 0x20000, 0xfca7f647 );
		ROM_LOAD( "kk_5-3l.rom", 0x40000, 0x20000, 0xd080fde1 );
		ROM_LOAD( "kk_7-4l.rom", 0x60000, 0x20000, 0xf5bf6829 );
		ROM_LOAD( "kk_3-1h.rom", 0x80000, 0x20000, 0x71af848e );
		ROM_LOAD( "kk_4-2h.rom", 0xa0000, 0x20000, 0xcebb5bac );
		ROM_LOAD( "kk_6-3h.rom", 0xc0000, 0x20000, 0x322e3752 );
		ROM_LOAD( "kk_8-4h.rom", 0xe0000, 0x20000, 0x117bde99 );
	ROM_END(); }}; 
	
	
	static RomLoadPtr rom_plotting = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 );
		ROM_LOAD( "plot01.bin", 0x00000, 0x10000, 0x5b30bc25 );
		ROM_RELOAD(             0x10000, 0x10000 );
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "plot07.bin", 0x00000, 0x10000, 0x6e0bad2a );
		ROM_LOAD( "plot08.bin", 0x10000, 0x10000, 0xfb5f3ca4 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_puzznic = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "u11.rom",  0x00000, 0x20000, 0xa4150b6c );
		ROM_RELOAD(           0x10000, 0x20000 );
	
		ROM_REGION( 0x0800, REGION_CPU2, 0 );/* 2k for the microcontroller */
		ROM_LOAD( "mc68705p", 0x0000, 0x0800, 0x00000000 );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "u10.rom",  0x00000, 0x20000, 0x4264056c );
		ROM_LOAD( "u09.rom",  0x20000, 0x20000, 0x3c115f8b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_horshoes = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "c47.03", 0x00000, 0x20000, 0x37e15b20 );
		ROM_RELOAD(         0x10000, 0x20000 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "c47.02", 0x00000, 0x10000, 0x35f96526 );
		ROM_CONTINUE (      0x20000, 0x10000 );
		ROM_LOAD( "c47.04", 0x40000, 0x10000, 0xaeac7121 );
		ROM_CONTINUE (      0x60000, 0x10000 );
		ROM_LOAD( "c47.01", 0x10000, 0x10000, 0x031c73d8 );
		ROM_CONTINUE (      0x30000, 0x10000 );
		ROM_LOAD( "c47.05", 0x50000, 0x10000, 0xb2a3dafe );
		ROM_CONTINUE (      0x70000, 0x10000 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_palamed = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "c63.02", 0x00000, 0x20000, 0x55a82bb2 );
		ROM_RELOAD(         0x10000, 0x20000 );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "c63.04", 0x00000, 0x20000, 0xc7bbe460 );
		ROM_LOAD( "c63.03", 0x20000, 0x20000, 0xfcd86e44 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_cachat = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "cac6",  0x00000, 0x20000, 0x8105cf5f );
		ROM_RELOAD(        0x10000, 0x20000 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "cac9",  0x00000, 0x20000, 0xbc462914 );
		ROM_LOAD( "cac10", 0x20000, 0x20000, 0xecc64b31 );
		ROM_LOAD( "cac7",  0x40000, 0x20000, 0x7fb71578 );
		ROM_LOAD( "cac8",  0x60000, 0x20000, 0xd2a63799 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_tubeit = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 );
		ROM_LOAD( "t-i_02.6", 0x00000, 0x20000, 0x54730669 );
		ROM_RELOAD(         0x10000, 0x20000 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "t-i_03.7", 0x40000, 0x40000, 0xe1c3fed0 );
		ROM_LOAD( "t-i_04.9", 0x00000, 0x40000, 0xb4a6e31d );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_cubybop = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000, REGION_CPU1, 0 );
		ROM_LOAD( "cb06.6", 0x00000, 0x40000, 0x66b89a85  );
		ROM_RELOAD(         0x10000, 0x40000 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "cb09.9",  0x00000, 0x40000, 0x5f831e59 );
		ROM_LOAD( "cb10.10", 0x40000, 0x40000, 0x430510fc );
		ROM_LOAD( "cb07.7",  0x80000, 0x40000, 0x3582de99 );
		ROM_LOAD( "cb08.8",  0xc0000, 0x40000, 0x09e18a51 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_plgirls = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000, REGION_CPU1, 0 );
		ROM_LOAD( "pg03.ic6",    0x00000, 0x40000, 0x6ca73092 );
		ROM_RELOAD(              0x10000, 0x40000 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "pg02.ic9",    0x00000, 0x40000, 0x3cf05ca9 );
		ROM_LOAD( "pg01.ic7",    0x40000, 0x40000, 0x79e41e74 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_plgirls2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000, REGION_CPU1, 0 );
		ROM_LOAD( "pg2_1j.ic6",  0x00000, 0x40000, 0xf924197a );
		ROM_RELOAD(              0x10000, 0x40000 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "cho-l.ic9",   0x00000, 0x80000, 0x956384ec );
		ROM_LOAD( "cho-h.ic7",   0x80000, 0x80000, 0x992f99b1 );
	ROM_END(); }}; 
	
	
	
	// bits 7..0 => bits 0..7
	static DRIVER_INIT( plotting )
	{
		unsigned char tab[256];
		unsigned char *p;
		int i;
	
		for(i=0;i<256;i++)
		{
			int j, v=0;
			for(j=0;j<8;j++)
				if(i & (1<<j))
					v |= 1<<(7-j);
			tab[i] = v;
		}
		p = memory_region(REGION_CPU1);
		for(i=0;i<0x20000;i++)
		{
			*p = tab[*p];
			p++;
		}
	}
	
	
	public static GameDriver driver_raimais	   = new GameDriver("1988"	,"raimais"	,"taito_l.java"	,rom_raimais,null	,machine_driver_raimais	,input_ports_raimais	,null	,ROT0	,	"Taito Corporation", "Raimais (Japan)" )
	public static GameDriver driver_fhawk	   = new GameDriver("1988"	,"fhawk"	,"taito_l.java"	,rom_fhawk,null	,machine_driver_fhawk	,input_ports_fhawk	,null	,ROT270	,	"Taito Corporation", "Fighting Hawk (Japan)" )
	public static GameDriver driver_champwr	   = new GameDriver("1989"	,"champwr"	,"taito_l.java"	,rom_champwr,null	,machine_driver_champwr	,input_ports_champwr	,null	,ROT0	,	"Taito Corporation Japan", "Champion Wrestler (World)" )
	public static GameDriver driver_champwru	   = new GameDriver("1989"	,"champwru"	,"taito_l.java"	,rom_champwru,driver_champwr	,machine_driver_champwr	,input_ports_champwru	,null	,ROT0	,	"Taito America Corporation", "Champion Wrestler (US)" )
	public static GameDriver driver_champwrj	   = new GameDriver("1989"	,"champwrj"	,"taito_l.java"	,rom_champwrj,driver_champwr	,machine_driver_champwr	,input_ports_champwrj	,null	,ROT0	,	"Taito Corporation", "Champion Wrestler (Japan)" )
	public static GameDriver driver_kurikint	   = new GameDriver("1988"	,"kurikint"	,"taito_l.java"	,rom_kurikint,null	,machine_driver_kurikint	,input_ports_kurikint	,null	,ROT0	,	"Taito Corporation Japan", "Kuri Kinton (World)" )
	public static GameDriver driver_kurikinu	   = new GameDriver("1988"	,"kurikinu"	,"taito_l.java"	,rom_kurikinu,driver_kurikint	,machine_driver_kurikint	,input_ports_kurikinj	,null	,ROT0	,	"Taito America Corporation", "Kuri Kinton (US)" )
	public static GameDriver driver_kurikinj	   = new GameDriver("1988"	,"kurikinj"	,"taito_l.java"	,rom_kurikinj,driver_kurikint	,machine_driver_kurikint	,input_ports_kurikinj	,null	,ROT0	,	"Taito Corporation", "Kuri Kinton (Japan)" )
	public static GameDriver driver_kurikina	   = new GameDriver("1988"	,"kurikina"	,"taito_l.java"	,rom_kurikina,driver_kurikint	,machine_driver_kurikina	,input_ports_kurikina	,null	,ROT0	,	"Taito Corporation Japan", "Kuri Kinton (World, prototype?)" )
	public static GameDriver driver_plotting	   = new GameDriver("1989"	,"plotting"	,"taito_l.java"	,rom_plotting,null	,machine_driver_plotting	,input_ports_plotting	,init_plotting	,ROT0	,	"Taito Corporation Japan", "Plotting (World)" )
	public static GameDriver driver_puzznic	   = new GameDriver("1989"	,"puzznic"	,"taito_l.java"	,rom_puzznic,null	,machine_driver_puzznic	,input_ports_puzznic	,null	,ROT0	,	"Taito Corporation", "Puzznic (Japan)" )
	public static GameDriver driver_horshoes	   = new GameDriver("1990"	,"horshoes"	,"taito_l.java"	,rom_horshoes,null	,machine_driver_horshoes	,input_ports_horshoes	,null	,ROT270	,	"Taito America Corporation", "American Horseshoes (US)" )
	public static GameDriver driver_palamed	   = new GameDriver("1990"	,"palamed"	,"taito_l.java"	,rom_palamed,null	,machine_driver_palamed	,input_ports_palamed	,null	,ROT0	,	"Taito Corporation", "Palamedes (Japan)" )
	public static GameDriver driver_cachat	   = new GameDriver("1993"	,"cachat"	,"taito_l.java"	,rom_cachat,null	,machine_driver_cachat	,input_ports_cachat	,null	,ROT0	,	"Taito Corporation", "Cachat (Japan)" )
	public static GameDriver driver_tubeit	   = new GameDriver("1993"	,"tubeit"	,"taito_l.java"	,rom_tubeit,driver_cachat	,machine_driver_cachat	,input_ports_tubeit	,null	,ROT0	,	"Taito Corporation", "Tube-It" )  // No (c) message
	public static GameDriver driver_cubybop	   = new GameDriver("199?"	,"cubybop"	,"taito_l.java"	,rom_cubybop,null	,machine_driver_cachat	,input_ports_cubybop	,null	,ROT0	,	"Taito Corporation", "Cuby Bop" ) // No (c) message
	
	public static GameDriver driver_plgirls	   = new GameDriver("1992"	,"plgirls"	,"taito_l.java"	,rom_plgirls,null	,machine_driver_cachat	,input_ports_plgirls	,null	,ROT270	,	"Hot-B.", "Play Girls" )
	public static GameDriver driver_plgirls2	   = new GameDriver("1993"	,"plgirls2"	,"taito_l.java"	,rom_plgirls2,null	,machine_driver_cachat	,input_ports_plgirls2	,null	,ROT270	,	"Hot-B.", "Play Girls 2" )
}
