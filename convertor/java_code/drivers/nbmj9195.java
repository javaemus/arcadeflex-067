/******************************************************************************

	Game Driver for Nichibutsu Mahjong series.

	Mahjong Uranai Densetsu
	(c)1992 Nihon Bussan Co.,Ltd. / (c)1992 Yubis Co.,Ltd.

	Mahjong Koi no Magic Potion
	(c)1992 Nihon Bussan Co.,Ltd.

	Mahjong Pachinko Monogatari
	(c)1992 Nihon Bussan Co.,Ltd.

	Medal Mahjong Janjan Baribari
	(c)1992 Nihon Bussan Co.,Ltd. / (c)1992 Yubis Co.,Ltd. / (c)1992 AV JAPAN Co.,Ltd.

	Mahjong Bakuhatsu Junjouden
	(c)1991 Nihon Bussan Co.,Ltd.

	Ultra Maru-hi Mahjong
	(c)1993 Apple

	Mahjong Gal 10-renpatsu
	(c)1993 FUJIC Co.,Ltd.

	Mahjong Ren-ai Club
	(c)1993 FUJIC Co.,Ltd.

	Mahjong La Man
	(c)1993 Nihon Bussan Co.,Ltd. / (c)1993 AV JAPAN Co.,Ltd.

	Mahjong Keibaou
	(c)1993 Nihon Bussan Co.,Ltd.

	Medal Mahjong Pachi-Slot Tengoku (Medal Type)
	(c)1993 Nihon Bussan Co.,Ltd. / (c)1993 MIKI SYOUJI Co.,Ltd. / (c)1993 AV JAPAN Co.,Ltd.

	Mahjong Sailor Wars
	(c)1993 Nihon Bussan Co.,Ltd.

	Mahjong Sailor Wars-R (Medal type)
	(c)1993 Nihon Bussan Co.,Ltd.

	Bishoujo Janshi Pretty Sailor 18-kin
	(c)1994 SPHINX Co.,Ltd.

	Bishoujo Janshi Pretty Sailor 2
	(c)1994 SPHINX Co.,Ltd.

	Disco Mahjong Otachidai no Okite
	(c)1995 SPHINX Co.,Ltd.

	Nekketsu Grand-Prix Gal
	(c)1991 Nihon Bussan Co.,Ltd.

	Mahjong Gottsu ee-kanji
	(c)1991 Nihon Bussan Co.,Ltd.

	Mahjong Circuit no Mehyou
	(c)1992 Nihon Bussan Co.,Ltd. / (c)1992 Kawakusu Co.,Ltd.

	Medal Mahjong Circuit no Mehyou (Medal type)
	(c)1992 Nihon Bussan Co.,Ltd. / (c)1992 Kawakusu Co.,Ltd.

	Mahjong Koi Uranai
	(c)1992 Nihon Bussan Co.,Ltd.

	Mahjong Scout Man
	(c)1994 SPHINX Co.,Ltd. / (c)1994 AV JAPAN Co.,Ltd.

	Imekura Mahjong
	(c)1994 SPHINX Co.,Ltd. / (c)1994 AV JAPAN Co.,Ltd.

	Mahjong Erotica Golf
	(c)1994 FUJIC Co.,Ltd. / (c)1994 AV JAPAN Co.,Ltd.

	Driver by Takahiro Nogi <nogi@kt.rim.or.jp> 2000/03/13 -
	Special thanks to Tatsuyuki Satoh

******************************************************************************/
/******************************************************************************
Memo:

- Screen position sometimes be strange while frame skip != 0.

- In attract mode of otatidai, scroll position is strange after white fade.
  (problems in nb19010 busy flag emulation or main cpu clock?).

- Some games display "GFXROM BANK OVER!!" or "GFXROM ADDRESS OVER!!"
  in Debug build.

- Screen flip is not perfect.

******************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class nbmj9195
{
	
	
	#define	SIGNED_DAC	0		// 0:unsigned DAC, 1:signed DAC
	
	
	VIDEO_UPDATE( sailorws );
	VIDEO_START( sailorws );
	VIDEO_START( mjkoiura );
	VIDEO_UPDATE( mscoutm );
	VIDEO_START( mscoutm );
	
	
	
	
	void sailorws_gfxflag2_w(int data);
	void sailorws_paltblnum_w(int data);
	
	
	static int sailorws_inputport;
	static int sailorws_dipswbitsel;
	static int sailorws_outcoin_flag;
	static int mscoutm_inputport;
	
	
	static unsigned char *sailorws_nvram;
	static size_t sailorws_nvram_size;
	
	
	static NVRAM_HANDLER( sailorws )
	{
		if (read_or_write)
			mame_fwrite(file, sailorws_nvram, sailorws_nvram_size);
		else
		{
			if (file)
				mame_fread(file, sailorws_nvram, sailorws_nvram_size);
			else
				memset(sailorws_nvram, 0, sailorws_nvram_size);
		}
	}
	
	static void sailorws_soundbank_w(int data)
	{
		unsigned char *SNDROM = memory_region(REGION_CPU2);
	
		cpu_setbank(1, &SNDROM[0x08000 + (0x8000 * (data & 0x03))]);
	}
	
	static int sailorws_sound_r(int offset)
	{
		return soundlatch_r(0);
	}
	
	public static WriteHandlerPtr sailorws_sound_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch_w(0, data);
	} };
	
	static void sailorws_soundclr_w(int offset, int data)
	{
		soundlatch_clear_w(0, 0);
	}
	
	static void sailorws_outcoin_flag_w(int data)
	{
		// bit0: coin in counter
		// bit1: coin out counter
		// bit2: hopper
		// bit3: coin lockout
	
		if (data & 0x04) sailorws_outcoin_flag ^= 1;
		else sailorws_outcoin_flag = 1;
	}
	
	public static WriteHandlerPtr sailorws_inputportsel_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sailorws_inputport = (data ^ 0xff);
	} };
	
	static int sailorws_dipsw_r(void)
	{
		return ((((readinputport(0) & 0xff) | ((readinputport(1) & 0xff) << 8)) >> sailorws_dipswbitsel) & 0x01);
	}
	
	static void sailorws_dipswbitsel_w(int data)
	{
		switch (data & 0xc0)
		{
			case	0x00:
				sailorws_dipswbitsel = 0;
				break;
			case	0x40:
				break;
			case	0x80:
				break;
			case	0xc0:
				sailorws_dipswbitsel = ((sailorws_dipswbitsel + 1) & 0x0f);
				break;
			default:
				break;
		}
	}
	
	static void mscoutm_inputportsel_w(int data)
	{
		mscoutm_inputport = (data ^ 0xff);
	}
	
	public static ReadHandlerPtr mscoutm_dipsw_0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		// DIPSW A
		return (((readinputport(0) & 0x01) << 7) | ((readinputport(0) & 0x02) << 5) |
		        ((readinputport(0) & 0x04) << 3) | ((readinputport(0) & 0x08) << 1) |
		        ((readinputport(0) & 0x10) >> 1) | ((readinputport(0) & 0x20) >> 3) |
		        ((readinputport(0) & 0x40) >> 5) | ((readinputport(0) & 0x80) >> 7));
	} };
	
	public static ReadHandlerPtr mscoutm_dipsw_1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		// DIPSW B
		return (((readinputport(1) & 0x01) << 7) | ((readinputport(1) & 0x02) << 5) |
		        ((readinputport(1) & 0x04) << 3) | ((readinputport(1) & 0x08) << 1) |
		        ((readinputport(1) & 0x10) >> 1) | ((readinputport(1) & 0x20) >> 3) |
		        ((readinputport(1) & 0x40) >> 5) | ((readinputport(1) & 0x80) >> 7));
	} };
	
	
	/* TMPZ84C011 PIO emulation */
	
	static unsigned char pio_dir[5*2], pio_latch[5*2];
	
	static int tmpz84c011_pio_r(int offset)
	{
		int portdata;
	
		if ((!strcmp(Machine->gamedrv->name, "mscoutm")) ||
		    (!strcmp(Machine->gamedrv->name, "imekura")) ||
		    (!strcmp(Machine->gamedrv->name, "mjegolf")))
		{
			switch (offset)
			{
				case	0:			/* PA_0 */
					// COIN IN, ETC...
					portdata = readinputport(2);
					break;
				case	1:			/* PB_0 */
					// PLAYER1 KEY, DIPSW A/B
					switch (mscoutm_inputport)
					{
						case	0x01:
							portdata = readinputport(3);
							break;
						case	0x02:
							portdata = readinputport(4);
							break;
						case	0x04:
							portdata = readinputport(5);
							break;
						case	0x08:
							portdata = readinputport(6);
							break;
						case	0x10:
							portdata = readinputport(7);
							break;
						default:
							portdata = 0xff;
							break;
					}
					break;
				case	2:			/* PC_0 */
					// PLAYER2 KEY
					portdata = 0xff;
					break;
				case	3:			/* PD_0 */
					portdata = 0xff;
					break;
				case	4:			/* PE_0 */
					portdata = 0xff;
					break;
	
				case	5:			/* PA_1 */
					portdata = 0xff;
					break;
				case	6:			/* PB_1 */
					portdata = 0xff;
					break;
				case	7:			/* PC_1 */
					portdata = 0xff;
					break;
				case	8:			/* PD_1 */
					portdata = sailorws_sound_r(0);
					break;
				case	9:			/* PE_1 */
					portdata = 0xff;
					break;
	
				default:
					logerror("PC %04X: TMPZ84C011_PIO Unknown Port Read %02X\n", activecpu_get_pc(), offset);
					portdata = 0xff;
					break;
			}
		}
		else
		{
			switch (offset)
			{
				case	0:			/* PA_0 */
					// COIN IN, ETC...
					portdata = ((readinputport(2) & 0xfe) | sailorws_outcoin_flag);
					break;
				case	1:			/* PB_0 */
					// PLAYER1 KEY, DIPSW A/B
					switch (sailorws_inputport)
					{
						case	0x01:
							portdata = readinputport(3);
							break;
						case	0x02:
							portdata = readinputport(4);
							break;
						case	0x04:
							portdata = readinputport(5);
							break;
						case	0x08:
							portdata = readinputport(6);
							break;
						case	0x10:
							portdata = ((readinputport(7) & 0x7f) | (sailorws_dipsw_r() << 7));
							break;
						default:
							portdata = 0xff;
							break;
					}
					break;
				case	2:			/* PC_0 */
					// PLAYER2 KEY
					portdata = 0xff;
					break;
				case	3:			/* PD_0 */
					portdata = 0xff;
					break;
				case	4:			/* PE_0 */
					portdata = 0xff;
					break;
	
				case	5:			/* PA_1 */
					portdata = 0xff;
					break;
				case	6:			/* PB_1 */
					portdata = 0xff;
					break;
				case	7:			/* PC_1 */
					portdata = 0xff;
					break;
				case	8:			/* PD_1 */
					portdata = sailorws_sound_r(0);
					break;
				case	9:			/* PE_1 */
					portdata = 0xff;
					break;
	
				default:
					logerror("PC %04X: TMPZ84C011_PIO Unknown Port Read %02X\n", activecpu_get_pc(), offset);
					portdata = 0xff;
					break;
			}
		}
	
		return portdata;
	}
	
	static void tmpz84c011_pio_w(int offset, int data)
	{
		if ((!strcmp(Machine->gamedrv->name, "imekura")) ||
		    (!strcmp(Machine->gamedrv->name, "mscoutm")) ||
		    (!strcmp(Machine->gamedrv->name, "mjegolf")))
		{
			switch (offset)
			{
				case	0:			/* PA_0 */
					mscoutm_inputportsel_w(data);	// NB22090
					break;
				case	1:			/* PB_0 */
					break;
				case	2:			/* PC_0 */
					break;
				case	3:			/* PD_0 */
					sailorws_paltblnum_w(data);
					break;
				case	4:			/* PE_0 */
					sailorws_gfxflag2_w(data);	// NB22090
					break;
	
				case	5:			/* PA_1 */
					sailorws_soundbank_w(data);
					break;
				case	6:			/* PB_1 */
	#if SIGNED_DAC
					DAC_1_signed_data_w(0, data);
	#else
					DAC_1_data_w(0, data);
	#endif
					break;
				case	7:			/* PC_1 */
	#if SIGNED_DAC
					DAC_0_signed_data_w(0, data);
	#else
					DAC_0_data_w(0, data);
	#endif
					break;
				case	8:			/* PD_1 */
					break;
				case	9:			/* PE_1 */
					if (!(data & 0x01)) sailorws_soundclr_w(0, 0);
					break;
	
				default:
					logerror("PC %04X: TMPZ84C011_PIO Unknown Port Write %02X, %02X\n", activecpu_get_pc(), offset, data);
					break;
			}
		}
		else
		{
			switch (offset)
			{
				case	0:			/* PA_0 */
					break;
				case	1:			/* PB_0 */
					break;
				case	2:			/* PC_0 */
					sailorws_dipswbitsel_w(data);
					break;
				case	3:			/* PD_0 */
					sailorws_paltblnum_w(data);
					break;
				case	4:			/* PE_0 */
					sailorws_outcoin_flag_w(data);
					break;
	
				case	5:			/* PA_1 */
					sailorws_soundbank_w(data);
					break;
				case	6:			/* PB_1 */
	#if SIGNED_DAC
					DAC_1_signed_data_w(0, data);
	#else
					DAC_1_data_w(0, data);
	#endif
					break;
				case	7:			/* PC_1 */
	#if SIGNED_DAC
					DAC_0_signed_data_w(0, data);
	#else
					DAC_0_data_w(0, data);
	#endif
					break;
				case	8:			/* PD_1 */
					break;
				case	9:			/* PE_1 */
					if (!(data & 0x01)) sailorws_soundclr_w(0, 0);
					break;
	
				default:
					logerror("PC %04X: TMPZ84C011_PIO Unknown Port Write %02X, %02X\n", activecpu_get_pc(), offset, data);
					break;
			}
		}
	}
	
	
	/* CPU interface */
	
	/* device 0 */
	public static ReadHandlerPtr tmpz84c011_0_pa_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(0) & ~pio_dir[0]) | (pio_latch[0] & pio_dir[0]); } };
	public static ReadHandlerPtr tmpz84c011_0_pb_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(1) & ~pio_dir[1]) | (pio_latch[1] & pio_dir[1]); } };
	public static ReadHandlerPtr tmpz84c011_0_pc_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(2) & ~pio_dir[2]) | (pio_latch[2] & pio_dir[2]); } };
	public static ReadHandlerPtr tmpz84c011_0_pd_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(3) & ~pio_dir[3]) | (pio_latch[3] & pio_dir[3]); } };
	public static ReadHandlerPtr tmpz84c011_0_pe_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(4) & ~pio_dir[4]) | (pio_latch[4] & pio_dir[4]); } };
	
	public static WriteHandlerPtr tmpz84c011_0_pa_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[0] = data; tmpz84c011_pio_w(0, data); } };
	public static WriteHandlerPtr tmpz84c011_0_pb_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[1] = data; tmpz84c011_pio_w(1, data); } };
	public static WriteHandlerPtr tmpz84c011_0_pc_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[2] = data; tmpz84c011_pio_w(2, data); } };
	public static WriteHandlerPtr tmpz84c011_0_pd_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[3] = data; tmpz84c011_pio_w(3, data); } };
	public static WriteHandlerPtr tmpz84c011_0_pe_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[4] = data; tmpz84c011_pio_w(4, data); } };
	
	public static ReadHandlerPtr tmpz84c011_0_dir_pa_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[0]; } };
	public static ReadHandlerPtr tmpz84c011_0_dir_pb_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[1]; } };
	public static ReadHandlerPtr tmpz84c011_0_dir_pc_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[2]; } };
	public static ReadHandlerPtr tmpz84c011_0_dir_pd_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[3]; } };
	public static ReadHandlerPtr tmpz84c011_0_dir_pe_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[4]; } };
	
	public static WriteHandlerPtr tmpz84c011_0_dir_pa_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[0] = data; } };
	public static WriteHandlerPtr tmpz84c011_0_dir_pb_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[1] = data; } };
	public static WriteHandlerPtr tmpz84c011_0_dir_pc_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[2] = data; } };
	public static WriteHandlerPtr tmpz84c011_0_dir_pd_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[3] = data; } };
	public static WriteHandlerPtr tmpz84c011_0_dir_pe_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[4] = data; } };
	
	/* device 1 */
	public static ReadHandlerPtr tmpz84c011_1_pa_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(5) & ~pio_dir[5]) | (pio_latch[5] & pio_dir[5]); } };
	public static ReadHandlerPtr tmpz84c011_1_pb_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(6) & ~pio_dir[6]) | (pio_latch[6] & pio_dir[6]); } };
	public static ReadHandlerPtr tmpz84c011_1_pc_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(7) & ~pio_dir[7]) | (pio_latch[7] & pio_dir[7]); } };
	public static ReadHandlerPtr tmpz84c011_1_pd_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(8) & ~pio_dir[8]) | (pio_latch[8] & pio_dir[8]); } };
	public static ReadHandlerPtr tmpz84c011_1_pe_r  = new ReadHandlerPtr() { public int handler(int offset) { return (tmpz84c011_pio_r(9) & ~pio_dir[9]) | (pio_latch[9] & pio_dir[9]); } };
	
	public static WriteHandlerPtr tmpz84c011_1_pa_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[5] = data; tmpz84c011_pio_w(5, data); } };
	public static WriteHandlerPtr tmpz84c011_1_pb_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[6] = data; tmpz84c011_pio_w(6, data); } };
	public static WriteHandlerPtr tmpz84c011_1_pc_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[7] = data; tmpz84c011_pio_w(7, data); } };
	public static WriteHandlerPtr tmpz84c011_1_pd_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[8] = data; tmpz84c011_pio_w(8, data); } };
	public static WriteHandlerPtr tmpz84c011_1_pe_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_latch[9] = data; tmpz84c011_pio_w(9, data); } };
	
	public static ReadHandlerPtr tmpz84c011_1_dir_pa_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[5]; } };
	public static ReadHandlerPtr tmpz84c011_1_dir_pb_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[6]; } };
	public static ReadHandlerPtr tmpz84c011_1_dir_pc_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[7]; } };
	public static ReadHandlerPtr tmpz84c011_1_dir_pd_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[8]; } };
	public static ReadHandlerPtr tmpz84c011_1_dir_pe_r  = new ReadHandlerPtr() { public int handler(int offset) { return pio_dir[9]; } };
	
	public static WriteHandlerPtr tmpz84c011_1_dir_pa_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[5] = data; } };
	public static WriteHandlerPtr tmpz84c011_1_dir_pb_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[6] = data; } };
	public static WriteHandlerPtr tmpz84c011_1_dir_pc_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[7] = data; } };
	public static WriteHandlerPtr tmpz84c011_1_dir_pd_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[8] = data; } };
	public static WriteHandlerPtr tmpz84c011_1_dir_pe_w = new WriteHandlerPtr() {public void handler(int offset, int data) { pio_dir[9] = data; } };
	
	
	static void ctc0_interrupt(int state)
	{
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, Z80_VECTOR(0, state));
	}
	
	static void ctc1_interrupt(int state)
	{
		cpu_set_irq_line_and_vector(1, 0, HOLD_LINE, Z80_VECTOR(0, state));
	}
	
	/* CTC of main cpu, ch0 trigger is vblank */
	static INTERRUPT_GEN( ctc0_trg1 )
	{
		z80ctc_0_trg1_w(0, 1);
		z80ctc_0_trg1_w(0, 0);
	}
	
	static z80ctc_interface ctc_intf =
	{
		2,			/* 2 chip */
		{ 0, 1 },		/* clock */
		{ 0, 0 },		/* timer disables */
		{ ctc0_interrupt, ctc1_interrupt },	/* interrupt handler */
		{ 0, z80ctc_1_trg3_w },	/* ZC/TO0 callback ctc1.zc0 -> ctc1.trg3 */
		{ 0, 0 },		/* ZC/TO1 callback */
		{ 0, 0 },		/* ZC/TO2 callback */
	};
	
	static void tmpz84c011_init(void)
	{
		int i;
	
		// initialize TMPZ84C011 PIO
		for (i = 0; i < (5 * 2); i++)
		{
			pio_dir[i] = pio_latch[i] = 0;
			tmpz84c011_pio_w(i, 0);
		}
	
		// initialize the CTC
		ctc_intf.baseclock[0] = Machine->drv->cpu[0].cpu_clock;
		ctc_intf.baseclock[1] = Machine->drv->cpu[1].cpu_clock;
		z80ctc_init(&ctc_intf);
	}
	
	static MACHINE_INIT( sailorws )
	{
		//
	}
	
	static void initialize_driver(void)
	{
		unsigned char *ROM = memory_region(REGION_CPU2);
	
		// sound program patch
		ROM[0x0213] = 0x00;			// DI -> NOP
	
		// initialize TMPZ84C011 PIO and CTC
		tmpz84c011_init();
	
		// initialize sound rom bank
		sailorws_soundbank_w(0);
	}
	
	
	static DRIVER_INIT( mjuraden ) { initialize_driver(); }
	static DRIVER_INIT( koinomp ) { initialize_driver(); }
	static DRIVER_INIT( patimono ) { initialize_driver(); }
	static DRIVER_INIT( mmehyou ) { initialize_driver(); }
	static DRIVER_INIT( gal10ren ) { initialize_driver(); }
	static DRIVER_INIT( mjlaman ) { initialize_driver(); }
	static DRIVER_INIT( mkeibaou ) { initialize_driver(); }
	static DRIVER_INIT( pachiten ) { initialize_driver(); }
	static DRIVER_INIT( mjanbari ) { initialize_driver(); }
	static DRIVER_INIT( ultramhm ) { initialize_driver(); }
	static DRIVER_INIT( sailorws ) { initialize_driver(); }
	static DRIVER_INIT( sailorwr ) { initialize_driver(); }
	static DRIVER_INIT( psailor1 ) { initialize_driver(); }
	static DRIVER_INIT( psailor2 ) { initialize_driver(); }
	static DRIVER_INIT( otatidai ) { initialize_driver(); }
	static DRIVER_INIT( renaiclb ) { initialize_driver(); }
	static DRIVER_INIT( ngpgal ) { initialize_driver(); }
	static DRIVER_INIT( mjgottsu ) { initialize_driver(); }
	static DRIVER_INIT( bakuhatu ) { initialize_driver(); }
	static DRIVER_INIT( cmehyou ) { initialize_driver(); }
	static DRIVER_INIT( mjkoiura ) { initialize_driver(); }
	static DRIVER_INIT( mscoutm ) { initialize_driver(); }
	static DRIVER_INIT( imekura ) { initialize_driver(); }
	static DRIVER_INIT( mjegolf ) { initialize_driver(); }
	
	
	public static Memory_ReadAddress readmem_sailorws[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf1ff, sailorws_palette_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sailorws[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf1ff, sailorws_palette_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM, sailorws_nvram, sailorws_nvram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_mjuraden[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf200, 0xf3ff, sailorws_palette_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_mjuraden[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf200, 0xf3ff, sailorws_palette_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_koinomp[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xe1ff, sailorws_palette_r ),
		new Memory_ReadAddress( 0xe800, 0xefff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_koinomp[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe1ff, sailorws_palette_w ),
		new Memory_WriteAddress( 0xe800, 0xefff, MWA_RAM, sailorws_nvram, sailorws_nvram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_ngpgal[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xcfff, MRA_ROM ),
		new Memory_ReadAddress( 0xd000, 0xd1ff, sailorws_palette_r ),
		new Memory_ReadAddress( 0xd800, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_ngpgal[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xcfff, MWA_ROM ),
		new Memory_WriteAddress( 0xd000, 0xd1ff, sailorws_palette_w ),
		new Memory_WriteAddress( 0xd800, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_mscoutm[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xe5ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe600, 0xebff, mscoutm_palette_r ),
		new Memory_ReadAddress( 0xec00, 0xf1ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf200, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_mscoutm[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe5ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe600, 0xebff, mscoutm_palette_w ),
		new Memory_WriteAddress( 0xec00, 0xf1ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf200, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_mjegolf[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xe5ff, mscoutm_palette_r ),
		new Memory_ReadAddress( 0xe600, 0xebff, MRA_RAM ),
		new Memory_ReadAddress( 0xec00, 0xf1ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf200, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_mjegolf[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe5ff, mscoutm_palette_w ),
		new Memory_WriteAddress( 0xe600, 0xebff, MWA_RAM ),
		new Memory_WriteAddress( 0xec00, 0xf1ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf200, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x77ff, MRA_ROM ),
		new Memory_ReadAddress( 0x7800, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_BANK1 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x77ff, MWA_ROM ),
		new Memory_WriteAddress( 0x7800, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_mjuraden[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_mjuraden[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_0_w ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_0_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_0_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_0_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xb0, 0xb0, sailorws_sound_w ),
		new IO_WritePort( 0xb2, 0xb2, IOWP_NOP ),
		new IO_WritePort( 0xb4, 0xb4, IOWP_NOP ),
		new IO_WritePort( 0xb6, 0xb6, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_koinomp[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xa0, 0xa0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xa1, 0xa1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_koinomp[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xb0, 0xbf, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_0_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_0_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_0_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xa1, 0xa2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xa3, 0xa4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xa5, 0xa7, sailorws_radr_1_w ),
		new IO_WritePort( 0xa8, 0xa8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xa9, 0xa9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xaa, 0xab, sailorws_drawx_1_w ),
		new IO_WritePort( 0xac, 0xad, sailorws_drawy_1_w ),
		new IO_WritePort( 0xaf, 0xaf, IOWP_NOP ),
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_sound_w ),
		new IO_WritePort( 0xc2, 0xc2, IOWP_NOP ),
		new IO_WritePort( 0xc4, 0xc4, IOWP_NOP ),
		new IO_WritePort( 0xc6, 0xc6, sailorws_inputportsel_w ),
		new IO_WritePort( 0xcf, 0xcf, IOWP_NOP ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_patimono[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0xc0, 0xc0, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0xc1, 0xc1, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_patimono[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xd0, 0xdf, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0xc1, 0xc2, sailorws_scrollx_0_w ),
		new IO_WritePort( 0xc3, 0xc4, sailorws_scrolly_0_w ),
		new IO_WritePort( 0xc5, 0xc7, sailorws_radr_0_w ),
		new IO_WritePort( 0xc8, 0xc8, sailorws_sizex_0_w ),
		new IO_WritePort( 0xc9, 0xc9, sailorws_sizey_0_w ),
		new IO_WritePort( 0xca, 0xcb, sailorws_drawx_0_w ),
		new IO_WritePort( 0xcc, 0xcd, sailorws_drawy_0_w ),
		new IO_WritePort( 0xcf, 0xcf, IOWP_NOP ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_1_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_1_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_1_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_1_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_1_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_1_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_1_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_sound_w ),
		new IO_WritePort( 0xa4, 0xa8, IOWP_NOP ),
		new IO_WritePort( 0xa8, 0xa0, IOWP_NOP ),
		new IO_WritePort( 0xb0, 0xb8, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_mmehyou[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_mmehyou[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_0_w ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_0_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_0_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_0_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_sound_w ),
		new IO_WritePort( 0xa4, 0xa4, IOWP_NOP ),
		new IO_WritePort( 0xa8, 0xa8, IOWP_NOP ),
		new IO_WritePort( 0xb0, 0xb0, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_gal10ren[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x60, 0x60, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x61, 0x61, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xa0, 0xa0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xa1, 0xa1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_gal10ren[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x70, 0x7f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xb0, 0xbf, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x60, 0x60, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x61, 0x62, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x63, 0x64, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x65, 0x67, sailorws_radr_0_w ),
		new IO_WritePort( 0x68, 0x68, sailorws_sizex_0_w ),
		new IO_WritePort( 0x69, 0x69, sailorws_sizey_0_w ),
		new IO_WritePort( 0x6a, 0x6b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x6c, 0x6d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x6f, 0x6f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xa1, 0xa2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xa3, 0xa4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xa5, 0xa7, sailorws_radr_1_w ),
		new IO_WritePort( 0xa8, 0xa8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xa9, 0xa9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xaa, 0xab, sailorws_drawx_1_w ),
		new IO_WritePort( 0xac, 0xad, sailorws_drawy_1_w ),
		new IO_WritePort( 0xaf, 0xaf, IOWP_NOP ),
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_sound_w ),
		new IO_WritePort( 0xc8, 0xc8, IOWP_NOP ),
		new IO_WritePort( 0xd0, 0xd0, IOWP_NOP ),
		new IO_WritePort( 0xd8, 0xd8, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_renaiclb[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x60, 0x60, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0x61, 0x61, sailorws_gfxrom_1_r ),
		new IO_ReadPort( 0xe0, 0xe0, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0xe1, 0xe1, sailorws_gfxrom_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_renaiclb[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x70, 0x7f, sailorws_paltbl_1_w ),
		new IO_WritePort( 0xf0, 0xff, sailorws_paltbl_0_w ),
	
		new IO_WritePort( 0x60, 0x60, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0x61, 0x62, sailorws_scrollx_1_w ),
		new IO_WritePort( 0x63, 0x64, sailorws_scrolly_1_w ),
		new IO_WritePort( 0x65, 0x67, sailorws_radr_1_w ),
		new IO_WritePort( 0x68, 0x68, sailorws_sizex_1_w ),
		new IO_WritePort( 0x69, 0x69, sailorws_sizey_1_w ),
		new IO_WritePort( 0x6a, 0x6b, sailorws_drawx_1_w ),
		new IO_WritePort( 0x6c, 0x6d, sailorws_drawy_1_w ),
		new IO_WritePort( 0x6f, 0x6f, IOWP_NOP ),
	
		new IO_WritePort( 0xe0, 0xe0, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0xe1, 0xe2, sailorws_scrollx_0_w ),
		new IO_WritePort( 0xe3, 0xe4, sailorws_scrolly_0_w ),
		new IO_WritePort( 0xe5, 0xe7, sailorws_radr_0_w ),
		new IO_WritePort( 0xe8, 0xe8, sailorws_sizex_0_w ),
		new IO_WritePort( 0xe9, 0xe9, sailorws_sizey_0_w ),
		new IO_WritePort( 0xea, 0xeb, sailorws_drawx_0_w ),
		new IO_WritePort( 0xec, 0xed, sailorws_drawy_0_w ),
		new IO_WritePort( 0xef, 0xef, IOWP_NOP ),
	
		new IO_WritePort( 0x20, 0x20, sailorws_sound_w ),
		new IO_WritePort( 0x24, 0x24, IOWP_NOP ),
		new IO_WritePort( 0x28, 0x28, IOWP_NOP ),
		new IO_WritePort( 0x2c, 0x2c, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_mjlaman[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xe0, 0xe0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xe1, 0xe1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_mjlaman[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xf0, 0xff, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_0_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_0_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_0_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xe0, 0xe0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xe1, 0xe2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xe3, 0xe4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xe5, 0xe7, sailorws_radr_1_w ),
		new IO_WritePort( 0xe8, 0xe8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xe9, 0xe9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xea, 0xeb, sailorws_drawx_1_w ),
		new IO_WritePort( 0xec, 0xed, sailorws_drawy_1_w ),
		new IO_WritePort( 0xef, 0xef, IOWP_NOP ),
	
		new IO_WritePort( 0x20, 0x20, sailorws_sound_w ),
		new IO_WritePort( 0x22, 0x22, IOWP_NOP ),
		new IO_WritePort( 0x24, 0x24, IOWP_NOP ),
		new IO_WritePort( 0x26, 0x26, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_mkeibaou[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xa0, 0xa0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xa1, 0xa1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_mkeibaou[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xb0, 0xbf, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_0_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_0_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_0_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xa1, 0xa2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xa3, 0xa4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xa5, 0xa7, sailorws_radr_1_w ),
		new IO_WritePort( 0xa8, 0xa8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xa9, 0xa9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xaa, 0xab, sailorws_drawx_1_w ),
		new IO_WritePort( 0xac, 0xad, sailorws_drawy_1_w ),
		new IO_WritePort( 0xaf, 0xaf, IOWP_NOP ),
	
		new IO_WritePort( 0xd8, 0xd8, sailorws_sound_w ),
		new IO_WritePort( 0xda, 0xda, IOWP_NOP ),
		new IO_WritePort( 0xdc, 0xdc, IOWP_NOP ),
		new IO_WritePort( 0xde, 0xde, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_pachiten[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x60, 0x60, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x61, 0x61, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xa0, 0xa0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xa1, 0xa1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_pachiten[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x70, 0x7f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xb0, 0xbf, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x60, 0x60, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x61, 0x62, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x63, 0x64, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x65, 0x67, sailorws_radr_0_w ),
		new IO_WritePort( 0x68, 0x68, sailorws_sizex_0_w ),
		new IO_WritePort( 0x69, 0x69, sailorws_sizey_0_w ),
		new IO_WritePort( 0x6a, 0x6b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x6c, 0x6d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x6f, 0x6f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xa1, 0xa2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xa3, 0xa4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xa5, 0xa7, sailorws_radr_1_w ),
		new IO_WritePort( 0xa8, 0xa8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xa9, 0xa9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xaa, 0xab, sailorws_drawx_1_w ),
		new IO_WritePort( 0xac, 0xad, sailorws_drawy_1_w ),
		new IO_WritePort( 0xaf, 0xaf, IOWP_NOP ),
	
		new IO_WritePort( 0xe0, 0xe0, sailorws_sound_w ),
		new IO_WritePort( 0xe2, 0xe2, IOWP_NOP ),
		new IO_WritePort( 0xe4, 0xe4, IOWP_NOP ),
		new IO_WritePort( 0xe6, 0xe6, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_sailorws[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x60, 0x60, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x61, 0x61, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_sailorws[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x70, 0x7f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x60, 0x60, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x61, 0x62, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x63, 0x64, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x65, 0x67, sailorws_radr_0_w ),
		new IO_WritePort( 0x68, 0x68, sailorws_sizex_0_w ),
		new IO_WritePort( 0x69, 0x69, sailorws_sizey_0_w ),
		new IO_WritePort( 0x6a, 0x6b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x6c, 0x6d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x6f, 0x6f, IOWP_NOP ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_1_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_1_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_1_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_1_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_1_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_1_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_1_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xf0, 0xf0, sailorws_sound_w ),
		new IO_WritePort( 0xf2, 0xf2, IOWP_NOP ),
		new IO_WritePort( 0xf4, 0xf4, IOWP_NOP ),
		new IO_WritePort( 0xf6, 0xf6, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_sailorwr[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x60, 0x60, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x61, 0x61, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_sailorwr[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x70, 0x7f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x60, 0x60, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x61, 0x62, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x63, 0x64, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x65, 0x67, sailorws_radr_0_w ),
		new IO_WritePort( 0x68, 0x68, sailorws_sizex_0_w ),
		new IO_WritePort( 0x69, 0x69, sailorws_sizey_0_w ),
		new IO_WritePort( 0x6a, 0x6b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x6c, 0x6d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x6f, 0x6f, IOWP_NOP ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_1_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_1_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_1_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_1_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_1_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_1_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_1_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xf8, 0xf8, sailorws_sound_w ),
		new IO_WritePort( 0xfa, 0xfa, IOWP_NOP ),
		new IO_WritePort( 0xfc, 0xfc, IOWP_NOP ),
		new IO_WritePort( 0xfe, 0xfe, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_psailor1[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x60, 0x60, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x61, 0x61, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xc0, 0xc0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xc1, 0xc1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_psailor1[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x70, 0x7f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xd0, 0xdf, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x60, 0x60, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x61, 0x62, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x63, 0x64, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x65, 0x67, sailorws_radr_0_w ),
		new IO_WritePort( 0x68, 0x68, sailorws_sizex_0_w ),
		new IO_WritePort( 0x69, 0x69, sailorws_sizey_0_w ),
		new IO_WritePort( 0x6a, 0x6b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x6c, 0x6d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x6f, 0x6f, IOWP_NOP ),
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xc1, 0xc2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xc3, 0xc4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xc5, 0xc7, sailorws_radr_1_w ),
		new IO_WritePort( 0xc8, 0xc8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xc9, 0xc9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xca, 0xcb, sailorws_drawx_1_w ),
		new IO_WritePort( 0xcc, 0xcd, sailorws_drawy_1_w ),
		new IO_WritePort( 0xcf, 0xcf, IOWP_NOP ),
	
		new IO_WritePort( 0xf0, 0xf0, sailorws_sound_w ),
		new IO_WritePort( 0xf2, 0xf2, IOWP_NOP ),
		new IO_WritePort( 0xf4, 0xf4, IOWP_NOP ),
		new IO_WritePort( 0xf6, 0xf6, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_psailor2[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x60, 0x60, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x61, 0x61, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xa0, 0xa0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xa1, 0xa1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_psailor2[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x70, 0x7f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xb0, 0xbf, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x60, 0x60, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x61, 0x62, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x63, 0x64, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x65, 0x67, sailorws_radr_0_w ),
		new IO_WritePort( 0x68, 0x68, sailorws_sizex_0_w ),
		new IO_WritePort( 0x69, 0x69, sailorws_sizey_0_w ),
		new IO_WritePort( 0x6a, 0x6b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x6c, 0x6d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x6f, 0x6f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xa1, 0xa2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xa3, 0xa4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xa5, 0xa7, sailorws_radr_1_w ),
		new IO_WritePort( 0xa8, 0xa8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xa9, 0xa9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xaa, 0xab, sailorws_drawx_1_w ),
		new IO_WritePort( 0xac, 0xad, sailorws_drawy_1_w ),
		new IO_WritePort( 0xaf, 0xaf, IOWP_NOP ),
	
		new IO_WritePort( 0xe0, 0xe0, sailorws_sound_w ),
		new IO_WritePort( 0xe2, 0xe2, IOWP_NOP ),
		new IO_WritePort( 0xe4, 0xe4, IOWP_NOP ),
		new IO_WritePort( 0xf6, 0xf6, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_otatidai[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x60, 0x60, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x61, 0x61, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_otatidai[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x70, 0x7f, sailorws_paltbl_0_w ),
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x60, 0x60, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x61, 0x62, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x63, 0x64, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x65, 0x67, sailorws_radr_0_w ),
		new IO_WritePort( 0x68, 0x68, sailorws_sizex_0_w ),
		new IO_WritePort( 0x69, 0x69, sailorws_sizey_0_w ),
		new IO_WritePort( 0x6a, 0x6b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x6c, 0x6d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x6f, 0x6f, IOWP_NOP ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_1_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_1_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_1_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_1_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_1_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_1_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_1_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_sound_w ),
		new IO_WritePort( 0xa8, 0xa8, IOWP_NOP ),
		new IO_WritePort( 0xb0, 0xb0, IOWP_NOP ),
		new IO_WritePort( 0xb8, 0xb8, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_ngpgal[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0xc0, 0xc0, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0xc1, 0xc1, sailorws_gfxrom_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_ngpgal[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0xd0, 0xdf, sailorws_paltbl_0_w ),
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0xc1, 0xc2, sailorws_scrollx_0_w ),
		new IO_WritePort( 0xc3, 0xc4, sailorws_scrolly_0_w ),
		new IO_WritePort( 0xc5, 0xc7, sailorws_radr_0_w ),
		new IO_WritePort( 0xc8, 0xc8, sailorws_sizex_0_w ),
		new IO_WritePort( 0xc9, 0xc9, sailorws_sizey_0_w ),
		new IO_WritePort( 0xca, 0xcb, sailorws_drawx_0_w ),
		new IO_WritePort( 0xcc, 0xcd, sailorws_drawy_0_w ),
		new IO_WritePort( 0xcf, 0xcf, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_sound_w ),
		new IO_WritePort( 0xa4, 0xa4, IOWP_NOP ),
		new IO_WritePort( 0xa8, 0xa8, IOWP_NOP ),
		new IO_WritePort( 0xb0, 0xb0, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_mjgottsu[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_mjgottsu[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_0_w ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_0_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_0_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_0_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_sound_w ),
		new IO_WritePort( 0xa4, 0xa4, IOWP_NOP ),
		new IO_WritePort( 0xa8, 0xa8, IOWP_NOP ),
		new IO_WritePort( 0xb0, 0xb0, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_cmehyou[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0xc0, 0xc0, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0xc1, 0xc1, sailorws_gfxrom_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_cmehyou[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0xd0, 0xdf, sailorws_paltbl_0_w ),
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0xc1, 0xc2, sailorws_scrollx_0_w ),
		new IO_WritePort( 0xc3, 0xc4, sailorws_scrolly_0_w ),
		new IO_WritePort( 0xc5, 0xc7, sailorws_radr_0_w ),
		new IO_WritePort( 0xc8, 0xc8, sailorws_sizex_0_w ),
		new IO_WritePort( 0xc9, 0xc9, sailorws_sizey_0_w ),
		new IO_WritePort( 0xca, 0xcb, sailorws_drawx_0_w ),
		new IO_WritePort( 0xcc, 0xcd, sailorws_drawy_0_w ),
		new IO_WritePort( 0xcf, 0xcf, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_sound_w ),
		new IO_WritePort( 0xa8, 0xa8, IOWP_NOP ),
		new IO_WritePort( 0xb0, 0xb0, sailorws_inputportsel_w ),
		new IO_WritePort( 0xb4, 0xb4, IOWP_NOP ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_mjkoiura[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0x81, 0x81, sailorws_gfxrom_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_mjkoiura[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0x90, 0x9f, sailorws_paltbl_0_w ),
	
		new IO_WritePort( 0x80, 0x80, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0x81, 0x82, sailorws_scrollx_0_w ),
		new IO_WritePort( 0x83, 0x84, sailorws_scrolly_0_w ),
		new IO_WritePort( 0x85, 0x87, sailorws_radr_0_w ),
		new IO_WritePort( 0x88, 0x88, sailorws_sizex_0_w ),
		new IO_WritePort( 0x89, 0x89, sailorws_sizey_0_w ),
		new IO_WritePort( 0x8a, 0x8b, sailorws_drawx_0_w ),
		new IO_WritePort( 0x8c, 0x8d, sailorws_drawy_0_w ),
		new IO_WritePort( 0x8f, 0x8f, IOWP_NOP ),
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_sound_w ),
		new IO_WritePort( 0xa4, 0xa4, IOWP_NOP ),
		new IO_WritePort( 0xa8, 0xa8, IOWP_NOP ),
		new IO_WritePort( 0xb0, 0xb0, sailorws_inputportsel_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_mscoutm[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, mscoutm_dipsw_1_r ),
		new IO_ReadPort( 0x82, 0x82, mscoutm_dipsw_0_r ),
		new IO_ReadPort( 0xc0, 0xc0, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0xc1, 0xc1, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xe0, 0xe0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xe1, 0xe1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_mscoutm[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0xd0, 0xdf, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xf0, 0xff, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0xa0, 0xa6, IOWP_NOP ),			// nb22090 param ?
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0xc1, 0xc2, sailorws_scrollx_0_w ),
		new IO_WritePort( 0xc3, 0xc4, sailorws_scrolly_0_w ),
		new IO_WritePort( 0xc5, 0xc7, sailorws_radr_0_w ),
		new IO_WritePort( 0xc8, 0xc8, sailorws_sizex_0_w ),
		new IO_WritePort( 0xc9, 0xc9, sailorws_sizey_0_w ),
		new IO_WritePort( 0xca, 0xcb, sailorws_drawx_0_w ),
		new IO_WritePort( 0xcc, 0xcd, sailorws_drawy_0_w ),
		new IO_WritePort( 0xcf, 0xcf, IOWP_NOP ),
	
		new IO_WritePort( 0xe0, 0xe0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xe1, 0xe2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xe3, 0xe4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xe5, 0xe7, sailorws_radr_1_w ),
		new IO_WritePort( 0xe8, 0xe8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xe9, 0xe9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xea, 0xeb, sailorws_drawx_1_w ),
		new IO_WritePort( 0xec, 0xed, sailorws_drawy_1_w ),
		new IO_WritePort( 0xef, 0xef, IOWP_NOP ),
	
		new IO_WritePort( 0x84, 0x84, sailorws_sound_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_imekura[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0x80, 0x80, mscoutm_dipsw_1_r ),
		new IO_ReadPort( 0x82, 0x82, mscoutm_dipsw_0_r ),
		new IO_ReadPort( 0xc0, 0xc0, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0xc1, 0xc1, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xe0, 0xe0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xe1, 0xe1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_imekura[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0xd0, 0xdf, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xf0, 0xff, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0xb0, 0xb6, IOWP_NOP ),			// nb22090 param ?
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0xc1, 0xc2, sailorws_scrollx_0_w ),
		new IO_WritePort( 0xc3, 0xc4, sailorws_scrolly_0_w ),
		new IO_WritePort( 0xc5, 0xc7, sailorws_radr_0_w ),
		new IO_WritePort( 0xc8, 0xc8, sailorws_sizex_0_w ),
		new IO_WritePort( 0xc9, 0xc9, sailorws_sizey_0_w ),
		new IO_WritePort( 0xca, 0xcb, sailorws_drawx_0_w ),
		new IO_WritePort( 0xcc, 0xcd, sailorws_drawy_0_w ),
		new IO_WritePort( 0xcf, 0xcf, IOWP_NOP ),
	
		new IO_WritePort( 0xe0, 0xe0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xe1, 0xe2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xe3, 0xe4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xe5, 0xe7, sailorws_radr_1_w ),
		new IO_WritePort( 0xe8, 0xe8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xe9, 0xe9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xea, 0xeb, sailorws_drawx_1_w ),
		new IO_WritePort( 0xec, 0xed, sailorws_drawy_1_w ),
		new IO_WritePort( 0xef, 0xef, IOWP_NOP ),
	
		new IO_WritePort( 0x84, 0x84, sailorws_sound_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort readport_mjegolf[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_0_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_0_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_0_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_0_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_0_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_0_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_0_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_0_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_0_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_0_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_0_dir_pe_r ),
	
		new IO_ReadPort( 0xe0, 0xe0, mscoutm_dipsw_1_r ),
		new IO_ReadPort( 0xe2, 0xe2, mscoutm_dipsw_0_r ),
		new IO_ReadPort( 0xa0, 0xa0, sailorws_gfxbusy_0_r ),
		new IO_ReadPort( 0xa1, 0xa1, sailorws_gfxrom_0_r ),
		new IO_ReadPort( 0xc0, 0xc0, sailorws_gfxbusy_1_r ),
		new IO_ReadPort( 0xc1, 0xc1, sailorws_gfxrom_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_mjegolf[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_0_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_0_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_0_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_0_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_0_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_0_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_0_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_0_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_0_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_0_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_0_dir_pe_w ),
	
		new IO_WritePort( 0xb0, 0xbf, sailorws_paltbl_0_w ),
		new IO_WritePort( 0xd0, 0xdf, sailorws_paltbl_1_w ),
	
		new IO_WritePort( 0x80, 0x86, IOWP_NOP ),			// nb22090 param ?
	
		new IO_WritePort( 0xa0, 0xa0, sailorws_gfxflag_0_w ),
		new IO_WritePort( 0xa1, 0xa2, sailorws_scrollx_0_w ),
		new IO_WritePort( 0xa3, 0xa4, sailorws_scrolly_0_w ),
		new IO_WritePort( 0xa5, 0xa7, sailorws_radr_0_w ),
		new IO_WritePort( 0xa8, 0xa8, sailorws_sizex_0_w ),
		new IO_WritePort( 0xa9, 0xa9, sailorws_sizey_0_w ),
		new IO_WritePort( 0xaa, 0xab, sailorws_drawx_0_w ),
		new IO_WritePort( 0xac, 0xad, sailorws_drawy_0_w ),
		new IO_WritePort( 0xaf, 0xaf, IOWP_NOP ),
	
		new IO_WritePort( 0xc0, 0xc0, sailorws_gfxflag_1_w ),
		new IO_WritePort( 0xc1, 0xc2, sailorws_scrollx_1_w ),
		new IO_WritePort( 0xc3, 0xc4, sailorws_scrolly_1_w ),
		new IO_WritePort( 0xc5, 0xc7, sailorws_radr_1_w ),
		new IO_WritePort( 0xc8, 0xc8, sailorws_sizex_1_w ),
		new IO_WritePort( 0xc9, 0xc9, sailorws_sizey_1_w ),
		new IO_WritePort( 0xca, 0xcb, sailorws_drawx_1_w ),
		new IO_WritePort( 0xcc, 0xcd, sailorws_drawy_1_w ),
		new IO_WritePort( 0xcf, 0xcf, IOWP_NOP ),
	
		new IO_WritePort( 0xe4, 0xe4, sailorws_sound_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x10, 0x13, z80ctc_1_r ),
		new IO_ReadPort( 0x50, 0x50, tmpz84c011_1_pa_r ),
		new IO_ReadPort( 0x51, 0x51, tmpz84c011_1_pb_r ),
		new IO_ReadPort( 0x52, 0x52, tmpz84c011_1_pc_r ),
		new IO_ReadPort( 0x30, 0x30, tmpz84c011_1_pd_r ),
		new IO_ReadPort( 0x40, 0x40, tmpz84c011_1_pe_r ),
		new IO_ReadPort( 0x54, 0x54, tmpz84c011_1_dir_pa_r ),
		new IO_ReadPort( 0x55, 0x55, tmpz84c011_1_dir_pb_r ),
		new IO_ReadPort( 0x56, 0x56, tmpz84c011_1_dir_pc_r ),
		new IO_ReadPort( 0x34, 0x34, tmpz84c011_1_dir_pd_r ),
		new IO_ReadPort( 0x44, 0x44, tmpz84c011_1_dir_pe_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x13, z80ctc_1_w ),
		new IO_WritePort( 0x50, 0x50, tmpz84c011_1_pa_w ),
		new IO_WritePort( 0x51, 0x51, tmpz84c011_1_pb_w ),
		new IO_WritePort( 0x52, 0x52, tmpz84c011_1_pc_w ),
		new IO_WritePort( 0x30, 0x30, tmpz84c011_1_pd_w ),
		new IO_WritePort( 0x40, 0x40, tmpz84c011_1_pe_w ),
		new IO_WritePort( 0x54, 0x54, tmpz84c011_1_dir_pa_w ),
		new IO_WritePort( 0x55, 0x55, tmpz84c011_1_dir_pb_w ),
		new IO_WritePort( 0x56, 0x56, tmpz84c011_1_dir_pc_w ),
		new IO_WritePort( 0x34, 0x34, tmpz84c011_1_dir_pd_w ),
		new IO_WritePort( 0x44, 0x44, tmpz84c011_1_dir_pe_w ),
	
		new IO_WritePort( 0x80, 0x80, YM3812_control_port_0_w ),
		new IO_WritePort( 0x81, 0x81, YM3812_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	#define MJCTRL_SAILORWS_PORT1 \
		PORT_START(); 	/* (3) PORT 1-0 */ \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );\
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Kan", KEYCODE_LCONTROL, IP_JOY_NONE );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 M", KEYCODE_M, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 I", KEYCODE_I, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 E", KEYCODE_E, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 A", KEYCODE_A, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_SAILORWS_PORT2 \
		PORT_START(); 	/* (4) PORT 1-1 */ \
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 Bet", KEYCODE_2, IP_JOY_NONE );\
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Reach", KEYCODE_LSHIFT, IP_JOY_NONE );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 N", KEYCODE_N, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 J", KEYCODE_J, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 F", KEYCODE_F, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 B", KEYCODE_B, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_SAILORWS_PORT3 \
		PORT_START(); 	/* (5) PORT 1-2 */ \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Ron", KEYCODE_Z, IP_JOY_NONE );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Chi", KEYCODE_SPACE, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 K", KEYCODE_K, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 G", KEYCODE_G, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 C", KEYCODE_C, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_SAILORWS_PORT4 \
		PORT_START(); 	/* (6) PORT 1-3 */ \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Pon", KEYCODE_LALT, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 L", KEYCODE_L, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 H", KEYCODE_H, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 D", KEYCODE_D, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_SAILORWS_PORT5 \
		PORT_START(); 	/* (7) PORT 1-4 */ \
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 Small", KEYCODE_BACKSPACE, IP_JOY_NONE );\
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Big", KEYCODE_ENTER, IP_JOY_NONE );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Flip", KEYCODE_X, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Double Up", KEYCODE_RSHIFT, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Take Score", KEYCODE_RCONTROL, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 Last Chance", KEYCODE_RALT, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_MSCOUTM_PORT1 \
		PORT_START(); 	/* (3) PORT 1-0 */ \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );\
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Kan", KEYCODE_LCONTROL, IP_JOY_NONE );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 M", KEYCODE_M, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 I", KEYCODE_I, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 E", KEYCODE_E, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 A", KEYCODE_A, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_MSCOUTM_PORT2 \
		PORT_START(); 	/* (4) PORT 1-1 */ \
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 Bet", KEYCODE_2, IP_JOY_NONE );\
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Reach", KEYCODE_LSHIFT, IP_JOY_NONE );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 N", KEYCODE_N, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 J", KEYCODE_J, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 F", KEYCODE_F, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 B", KEYCODE_B, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_MSCOUTM_PORT3 \
		PORT_START(); 	/* (5) PORT 1-2 */ \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Ron", KEYCODE_Z, IP_JOY_NONE );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Chi", KEYCODE_SPACE, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 K", KEYCODE_K, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 G", KEYCODE_G, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 C", KEYCODE_C, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_MSCOUTM_PORT4 \
		PORT_START(); 	/* (6) PORT 1-3 */ \
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Pon", KEYCODE_LALT, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 L", KEYCODE_L, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 H", KEYCODE_H, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 D", KEYCODE_D, IP_JOY_NONE );\
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
	#define MJCTRL_MSCOUTM_PORT5 \
		PORT_START(); 	/* (7) PORT 1-4 */ \
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 Small", KEYCODE_BACKSPACE, IP_JOY_NONE );\
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 Big", KEYCODE_ENTER, IP_JOY_NONE );\
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 Flip", KEYCODE_X, IP_JOY_NONE );\
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Double Up", KEYCODE_RSHIFT, IP_JOY_NONE );\
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Take Score", KEYCODE_RCONTROL, IP_JOY_NONE );\
		PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "P1 Last Chance", KEYCODE_RALT, IP_JOY_NONE );\
		PORT_SERVICE( 0x40, IP_ACTIVE_LOW );\
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
	
	static InputPortPtr input_ports_mjuraden = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Character Display Test" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_koinomp = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_patimono = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjanbari = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 1-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mmehyou = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 1-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 1-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_ultramhm = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 1-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 1-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_gal10ren = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_renaiclb = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjlaman = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x04, 0x00, "Demo Sounds & Game Sounds" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Voices" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xe0, "1 (Easy); )
		PORT_DIPSETTING(    0xc0, "2" );
		PORT_DIPSETTING(    0xa0, "3" );
		PORT_DIPSETTING(    0x80, "4" );
		PORT_DIPSETTING(    0x60, "5" );
		PORT_DIPSETTING(    0x40, "6" );
		PORT_DIPSETTING(    0x20, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mkeibaou = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1" );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_pachiten = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, "Game Out" );
		PORT_DIPSETTING(    0x07, "90% (Easy); )
		PORT_DIPSETTING(    0x06, "85%" );
		PORT_DIPSETTING(    0x05, "80%" );
		PORT_DIPSETTING(    0x04, "75%" );
		PORT_DIPSETTING(    0x03, "70%" );
		PORT_DIPSETTING(    0x02, "65%" );
		PORT_DIPSETTING(    0x01, "60%" );
		PORT_DIPSETTING(    0x00, "55% (Hard); )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x00, "Last Chance" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Last chance needs 1credit" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "Bet1 Only" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x18, 0x18, "Bet Min" );
		PORT_DIPSETTING(    0x18, "1" );
		PORT_DIPSETTING(    0x10, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x60, 0x00, "Bet Max" );
		PORT_DIPSETTING(    0x60, "8" );
		PORT_DIPSETTING(    0x40, "10" );
		PORT_DIPSETTING(    0x20, "12" );
		PORT_DIPSETTING(    0x00, "20" );
		PORT_DIPNAME( 0x80, 0x80, "Score Pool" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_sailorws = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1" );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x04, 0x04, "Infinite Bra" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_sailorwr = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, "Game Out" );
		PORT_DIPSETTING(    0x07, "90% (Easy); )
		PORT_DIPSETTING(    0x06, "85%" );
		PORT_DIPSETTING(    0x05, "80%" );
		PORT_DIPSETTING(    0x04, "75%" );
		PORT_DIPSETTING(    0x03, "70%" );
		PORT_DIPSETTING(    0x02, "65%" );
		PORT_DIPSETTING(    0x01, "60%" );
		PORT_DIPSETTING(    0x00, "55% (Hard); )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x00, "Last Chance" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Last chance needs 1credit" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Character Display Test" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "Bet1 Only" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x18, 0x18, "Bet Min" );
		PORT_DIPSETTING(    0x18, "1" );
		PORT_DIPSETTING(    0x10, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x60, 0x00, "Bet Max" );
		PORT_DIPSETTING(    0x60, "8" );
		PORT_DIPSETTING(    0x40, "10" );
		PORT_DIPSETTING(    0x20, "12" );
		PORT_DIPSETTING(    0x00, "20" );
		PORT_DIPNAME( 0x80, 0x80, "Score Pool" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_psailor1 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xe0, "1 (Easy); )
		PORT_DIPSETTING(    0xc0, "2" );
		PORT_DIPSETTING(    0xa0, "3" );
		PORT_DIPSETTING(    0x80, "4" );
		PORT_DIPSETTING(    0x60, "5" );
		PORT_DIPSETTING(    0x40, "6" );
		PORT_DIPSETTING(    0x20, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x03, 0x03, "Start Score" );
		PORT_DIPSETTING(    0x00, "5000" );
		PORT_DIPSETTING(    0x01, "3000" );
		PORT_DIPSETTING(    0x02, "2000" );
		PORT_DIPSETTING(    0x03, "1000" );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_psailor2 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xe0, "1 (Easy); )
		PORT_DIPSETTING(    0xc0, "2" );
		PORT_DIPSETTING(    0xa0, "3" );
		PORT_DIPSETTING(    0x80, "4" );
		PORT_DIPSETTING(    0x60, "5" );
		PORT_DIPSETTING(    0x40, "6" );
		PORT_DIPSETTING(    0x20, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x03, 0x03, "Start Score" );
		PORT_DIPSETTING(    0x00, "5000" );
		PORT_DIPSETTING(    0x01, "3000" );
		PORT_DIPSETTING(    0x02, "2000" );
		PORT_DIPSETTING(    0x03, "1000" );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_otatidai = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy); )
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPSETTING(    0x05, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPSETTING(    0x02, "6" );
		PORT_DIPSETTING(    0x01, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
		PORT_DIPNAME( 0x08, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_3C") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x03, 0x03, "Start Score" );
		PORT_DIPSETTING(    0x00, "5000" );
		PORT_DIPSETTING(    0x01, "3000" );
		PORT_DIPSETTING(    0x02, "2000" );
		PORT_DIPSETTING(    0x03, "1000" );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Sound Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_ngpgal = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy); )
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPSETTING(    0x05, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPSETTING(    0x02, "6" );
		PORT_DIPSETTING(    0x01, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 1-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjgottsu = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1" );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_bakuhatu = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1" );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_cmehyou = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy); )
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPSETTING(    0x05, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPSETTING(    0x02, "6" );
		PORT_DIPSETTING(    0x01, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 1-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjkoiura = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy); )
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPSETTING(    0x05, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPSETTING(    0x02, "6" );
		PORT_DIPSETTING(    0x01, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, "Character Display Test" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// COIN OUT
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		MJCTRL_SAILORWS_PORT1
		MJCTRL_SAILORWS_PORT2
		MJCTRL_SAILORWS_PORT3
		MJCTRL_SAILORWS_PORT4
		MJCTRL_SAILORWS_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mscoutm = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy); )
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPSETTING(    0x05, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPSETTING(    0x02, "6" );
		PORT_DIPSETTING(    0x01, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
	
		MJCTRL_MSCOUTM_PORT1
		MJCTRL_MSCOUTM_PORT2
		MJCTRL_MSCOUTM_PORT3
		MJCTRL_MSCOUTM_PORT4
		MJCTRL_MSCOUTM_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_imekura = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy); )
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPSETTING(    0x05, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPSETTING(    0x02, "6" );
		PORT_DIPSETTING(    0x01, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
		PORT_DIPNAME( 0x08, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_3C") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
	
		MJCTRL_MSCOUTM_PORT1
		MJCTRL_MSCOUTM_PORT2
		MJCTRL_MSCOUTM_PORT3
		MJCTRL_MSCOUTM_PORT4
		MJCTRL_MSCOUTM_PORT5
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjegolf = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy); )
		PORT_DIPSETTING(    0x06, "2" );
		PORT_DIPSETTING(    0x05, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPSETTING(    0x02, "6" );
		PORT_DIPSETTING(    0x01, "7" );
		PORT_DIPSETTING(    0x00, "8 (Hard); )
		PORT_DIPNAME( 0x08, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_3C") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
	
		MJCTRL_MSCOUTM_PORT1
		MJCTRL_MSCOUTM_PORT2
		MJCTRL_MSCOUTM_PORT3
		MJCTRL_MSCOUTM_PORT4
		MJCTRL_MSCOUTM_PORT5
	INPUT_PORTS_END(); }}; 
	
	
	static Z80_DaisyChain daisy_chain_main[] =
	{
		{ z80ctc_reset, z80ctc_interrupt, z80ctc_reti, 0 },	/* device 0 = CTC_0 */
		{ 0, 0, 0, -1 }		/* end mark */
	};
	
	static Z80_DaisyChain daisy_chain_sound[] =
	{
		{ z80ctc_reset, z80ctc_interrupt, z80ctc_reti, 1 },	/* device 0 = CTC_1 */
		{ 0, 0, 0, -1 }		/* end mark */
	};
	
	
	static struct YM3812interface ym3812_interface =
	{
		1,				/* 1 chip */
		4000000,			/* 4.00 MHz */
		{ 70 }
	};
	
	static DACinterface dac_interface = new DACinterface
	(
		2,				/* 2 channels */
		new int[] { 70, 100 },
	);
	
	
	static MACHINE_DRIVER_START( NBMJDRV1 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main",Z80,12000000/2)		/* TMPZ84C011, 6.00 MHz */
		MDRV_CPU_CONFIG(daisy_chain_main)
		MDRV_CPU_MEMORY(readmem_sailorws, writemem_sailorws)
		MDRV_CPU_PORTS(readport_sailorws, writeport_sailorws)
		MDRV_CPU_VBLANK_INT(ctc0_trg1, 1)	/* vblank is connect to ctc triggfer */
	
		MDRV_CPU_ADD(Z80,8000000/1)			/* TMPZ84C011, 8.00 MHz */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_CONFIG(daisy_chain_sound)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(sailorws)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_AFTER_VBLANK | VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_SCREEN_SIZE(1024, 512)
		MDRV_VISIBLE_AREA(0, 640-1, 0, 240-1)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(sailorws)
		MDRV_VIDEO_UPDATE(sailorws)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3812, ym3812_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( NBMJDRV2 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_VIDEO_START(mjkoiura)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( NBMJDRV3 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(mscoutm)
		MDRV_VIDEO_UPDATE(mscoutm)
	MACHINE_DRIVER_END
	
	
	//-------------------------------------------------------------------------
	
	static MACHINE_DRIVER_START( mjuraden )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_mjuraden,writemem_mjuraden)
		MDRV_CPU_PORTS(readport_mjuraden,writeport_mjuraden)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( koinomp )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_koinomp,writemem_koinomp)
		MDRV_CPU_PORTS(readport_koinomp,writeport_koinomp)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( patimono )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_patimono,writeport_patimono)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mjanbari )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_patimono,writeport_patimono)
	
		MDRV_NVRAM_HANDLER(sailorws)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mmehyou )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_koinomp,writemem_koinomp)
		MDRV_CPU_PORTS(readport_mmehyou,writeport_mmehyou)
	
		MDRV_NVRAM_HANDLER(sailorws)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( ultramhm )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_koinomp,writemem_koinomp)
		MDRV_CPU_PORTS(readport_koinomp,writeport_koinomp)
	
		MDRV_NVRAM_HANDLER(sailorws)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( gal10ren )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_gal10ren,writeport_gal10ren)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( renaiclb )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_renaiclb,writeport_renaiclb)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mjlaman )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_mjlaman,writeport_mjlaman)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mkeibaou )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_mkeibaou,writeport_mkeibaou)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( pachiten )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_pachiten,writeport_pachiten)
	
		MDRV_NVRAM_HANDLER(sailorws)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( sailorws )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( sailorwr )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_sailorwr,writeport_sailorwr)
	
		MDRV_NVRAM_HANDLER(sailorws)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( psailor1 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_psailor1,writeport_psailor1)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( psailor2 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_psailor2,writeport_psailor2)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( otatidai )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV1 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_otatidai,writeport_otatidai)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( ngpgal )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV2 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_ngpgal,writemem_ngpgal)
		MDRV_CPU_PORTS(readport_ngpgal,writeport_ngpgal)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mjgottsu )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV2 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_ngpgal,writemem_ngpgal)
		MDRV_CPU_PORTS(readport_mjgottsu,writeport_mjgottsu)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( bakuhatu )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV2 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_ngpgal,writemem_ngpgal)
		MDRV_CPU_PORTS(readport_mjgottsu,writeport_mjgottsu)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( cmehyou )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV2 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_ngpgal,writemem_ngpgal)
		MDRV_CPU_PORTS(readport_cmehyou,writeport_cmehyou)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mjkoiura )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV2 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_mjuraden,writemem_mjuraden)
		MDRV_CPU_PORTS(readport_mjkoiura,writeport_mjkoiura)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mscoutm )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV3 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_mscoutm,writemem_mscoutm)
		MDRV_CPU_PORTS(readport_mscoutm,writeport_mscoutm)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( imekura )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV3 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_mjegolf,writemem_mjegolf)
		MDRV_CPU_PORTS(readport_imekura,writeport_imekura)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mjegolf )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM( NBMJDRV3 )
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_mjegolf,writemem_mjegolf)
		MDRV_CPU_PORTS(readport_mjegolf,writeport_mjegolf)
	MACHINE_DRIVER_END
	
	
	static RomLoadPtr rom_mjuraden = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "1.7c",   0x00000,  0x10000, 0x3b142791 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.13e",  0x00000,  0x20000, 0x3a230c22 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1h",   0x000000, 0x80000, 0x6face365 );
		ROM_LOAD( "4.3h",   0x080000, 0x80000, 0x6b7b0518 );
		ROM_LOAD( "5.5h",   0x100000, 0x80000, 0x43396517 );
		ROM_LOAD( "6.6h",   0x180000, 0x80000, 0x32cd3450 );
		ROM_LOAD( "9.11h",  0x240000, 0x20000, 0x585998bd );
		ROM_LOAD( "10.12h", 0x260000, 0x20000, 0x58220c2a );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_koinomp = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "1.7c",   0x00000,  0x10000, 0xe4d626fc );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.13e",  0x00000,  0x20000, 0x4a5c814b );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1h",   0x000000, 0x80000, 0x1f16d3a1 );
		ROM_LOAD( "4.3h",   0x080000, 0x80000, 0xf00b1a11 );
		ROM_LOAD( "5.5h",   0x100000, 0x80000, 0xb1ae17b3 );
		ROM_LOAD( "6.6h",   0x180000, 0x80000, 0xbb863b58 );
		ROM_LOAD( "7.7h",   0x200000, 0x80000, 0x2a3acd8c );
		ROM_LOAD( "8.9h",   0x280000, 0x80000, 0x595a643a );
		ROM_LOAD( "9.10h",  0x300000, 0x80000, 0x28e68e7b );
		ROM_LOAD( "10.12h", 0x380000, 0x80000, 0xa598f152 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_patimono = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "1.7c",   0x00000,  0x10000, 0xe4860829 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.13e",  0x00000,  0x20000, 0x30770363 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1h",   0x000000, 0x80000, 0x56cbf448 );
		ROM_LOAD( "4.3h",   0x080000, 0x80000, 0x4dd19093 );
		ROM_LOAD( "5.5h",   0x100000, 0x80000, 0x63cdc4fe );
		ROM_LOAD( "6.6h",   0x180000, 0x80000, 0x6057cb66 );
		ROM_LOAD( "7.7h",   0x200000, 0x80000, 0x309ea3d5 );
		ROM_LOAD( "8.9h",   0x280000, 0x80000, 0x6da16cdd );
		ROM_LOAD( "9.10h",  0x300000, 0x80000, 0xc6064b3b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjanbari = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "11.7c",  0x00000,  0x10000, 0x1edde2ef );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.13e",  0x00000,  0x20000, 0x30770363 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1h",   0x000000, 0x80000, 0x0fb21d13 );
		ROM_LOAD( "4.3h",   0x080000, 0x80000, 0x4dd19093 );
		ROM_LOAD( "5.5h",   0x100000, 0x80000, 0xf5748587 );
		ROM_LOAD( "6.6h",   0x180000, 0x80000, 0x9aaf6aa4 );
		ROM_LOAD( "7.7h",   0x200000, 0x80000, 0x34df5475 );
		ROM_LOAD( "8.9h",   0x280000, 0x80000, 0xd4d74ec3 );
		ROM_LOAD( "9.10h",  0x300000, 0x80000, 0xf7958466 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mmehyou = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "1.7c",  0x00000,  0x10000, 0x29d51130 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.13e", 0x00000,  0x20000, 0xd193a2e1 );
	
		ROM_REGION( 0x260000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1h",  0x000000, 0x80000, 0xe4caab61 );
		ROM_LOAD( "4.3h",  0x080000, 0x80000, 0xbbb20aef );
		ROM_LOAD( "5.5h",  0x100000, 0x80000, 0xff59c4c9 );
		ROM_LOAD( "6.6h",  0x180000, 0x80000, 0xd20f9b92 );
		ROM_LOAD( "7.7h",  0x200000, 0x20000, 0xd78dfbe2 );
		ROM_LOAD( "8.9h",  0x220000, 0x20000, 0x92160e9b );
		ROM_LOAD( "9.10h", 0x240000, 0x20000, 0x18a72f2e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_ultramhm = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "1.7c",   0x00000,  0x10000, 0x152811b1 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.12e",  0x00000,  0x20000, 0xa26ba18b );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1h",   0x000000, 0x80000, 0xc0b2bb01 );
		ROM_LOAD( "4.3h",   0x080000, 0x80000, 0xc9f0fe0f );
		ROM_LOAD( "5.4h",   0x100000, 0x80000, 0xee9a449e );
		ROM_LOAD( "6.6h",   0x180000, 0x80000, 0x0c1a8723 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_gal10ren = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "gl10_01.bin",  0x00000,  0x10000, 0xf63f81b4 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "gl10_02.bin",  0x00000,  0x20000, 0x1317b788 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "gl10_03.bin",  0x000000, 0x80000, 0xee7853ff );
		ROM_LOAD( "gl10_04.bin",  0x080000, 0x80000, 0xe17e4fb5 );
		ROM_LOAD( "gl10_05.bin",  0x100000, 0x80000, 0x0167f589 );
		ROM_LOAD( "gl10_06.bin",  0x180000, 0x80000, 0xa31a3ab8 );
		ROM_LOAD( "gl10_07.bin",  0x200000, 0x80000, 0x0d96419f );
		ROM_LOAD( "gl10_08.bin",  0x280000, 0x80000, 0x777857d0 );
		ROM_LOAD( "gl10_09.bin",  0x300000, 0x80000, 0xb1dba049 );
		ROM_LOAD( "gl10_10.bin",  0x380000, 0x80000, 0xa9806b00 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_renaiclb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "1.7c",  0x00000,  0x10000, 0x82f99130 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.12e", 0x00000,  0x20000, 0x9f6204a1 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1h",  0x000000, 0x80000, 0x3d205506 );
		ROM_LOAD( "4.3h",  0x080000, 0x80000, 0xd9c1af55 );
		ROM_LOAD( "5.5h",  0x100000, 0x80000, 0x3860cae7 );
		ROM_LOAD( "6.6h",  0x180000, 0x80000, 0xf5a43aaa );
		ROM_LOAD( "7.7h",  0x200000, 0x80000, 0x31676c54 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjlaman = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "mlmn_01.bin",  0x00000,  0x10000, 0x5974740d );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "mlmn_02.bin",  0x00000,  0x20000, 0x90adede6 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mlmn_03.bin",  0x000000, 0x80000, 0xf9c4cda2 );
		ROM_LOAD( "mlmn_04.bin",  0x080000, 0x80000, 0x576c54d4 );
		ROM_LOAD( "mlmn_05.bin",  0x100000, 0x80000, 0x0318a070 );
		ROM_LOAD( "mlmn_06.bin",  0x180000, 0x80000, 0x9ee76f86 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mkeibaou = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "mkbo_01.bin",  0x00000,  0x10000, 0x2e37b1fb );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "mkbo_02.bin",  0x00000,  0x20000, 0xc9a3109e );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mkbo_03.bin",  0x000000, 0x80000, 0x671e2fd9 );
		ROM_LOAD( "mkbo_04.bin",  0x080000, 0x80000, 0x6ae5d3de );
		ROM_LOAD( "mkbo_05.bin",  0x100000, 0x80000, 0xc57f4532 );
		ROM_LOAD( "mkbo_06.bin",  0x180000, 0x80000, 0x4b7edeea );
		ROM_LOAD( "mkbo_07.bin",  0x200000, 0x80000, 0x6cb2e7f4 );
		ROM_LOAD( "mkbo_08.bin",  0x280000, 0x80000, 0x45ca7512 );
		ROM_LOAD( "mkbo_09.bin",  0x300000, 0x80000, 0xabc47929 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_pachiten = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "pctn_01.bin",  0x00000,  0x10000, 0xc033d7c6 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "pctn_02.bin",  0x00000,  0x20000, 0xfe2f0dfa );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "pctn_03.bin",  0x000000, 0x80000, 0x9d9c5956 );
		ROM_LOAD( "pctn_04.bin",  0x080000, 0x80000, 0x73765b76 );
		ROM_LOAD( "pctn_05.bin",  0x100000, 0x80000, 0xdb929225 );
		ROM_LOAD( "pctn_06.bin",  0x180000, 0x80000, 0x4c817293 );
		ROM_LOAD( "pctn_07.bin",  0x200000, 0x80000, 0x34df5475 );
		ROM_LOAD( "pctn_08.bin",  0x280000, 0x80000, 0x227a73e5 );
		ROM_LOAD( "pctn_09.bin",  0x300000, 0x80000, 0x600c738f );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_sailorws = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "slws_01.bin",  0x00000,  0x10000, 0x33191e48 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "slws_02.bin",  0x00000,  0x20000, 0x582f3f29 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "slws_03.bin",  0x000000, 0x80000, 0x7fe44b0f );
		ROM_LOAD( "slws_04.bin",  0x080000, 0x80000, 0x8b78a009 );
		ROM_LOAD( "slws_05.bin",  0x100000, 0x80000, 0x6408aa82 );
		ROM_LOAD( "slws_06.bin",  0x180000, 0x80000, 0xe01d17f5 );
		ROM_LOAD( "slws_07.bin",  0x200000, 0x80000, 0xf8f13876 );
		ROM_LOAD( "slws_08.bin",  0x280000, 0x80000, 0x97ef333d );
		ROM_LOAD( "slws_09.bin",  0x300000, 0x80000, 0x06cadf34 );
		ROM_LOAD( "slws_10.bin",  0x380000, 0x80000, 0xdd944b9c );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_sailorwr = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "slwr_01.bin",  0x00000,  0x10000, 0xa0d65cd5 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "slws_02.bin",  0x00000,  0x20000, 0x582f3f29 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "slwr_03.bin",  0x000000, 0x80000, 0x03c865ae );
		ROM_LOAD( "slws_04.bin",  0x080000, 0x80000, 0x8b78a009 );
		ROM_LOAD( "slws_05.bin",  0x100000, 0x80000, 0x6408aa82 );
		ROM_LOAD( "slws_06.bin",  0x180000, 0x80000, 0xe01d17f5 );
		ROM_LOAD( "slwr_07.bin",  0x200000, 0x80000, 0x2ee65c0b );
		ROM_LOAD( "slwr_08.bin",  0x280000, 0x80000, 0xfe72a7fb );
		ROM_LOAD( "slwr_09.bin",  0x300000, 0x80000, 0x149ec899 );
		ROM_LOAD( "slwr_10.bin",  0x380000, 0x80000, 0x0cf3da5a );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_psailor1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "pts1_01.bin",  0x00000,  0x10000, 0xa93dab87 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "pts1_02.bin",  0x00000,  0x20000, 0x0bcc1a89 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "pts1_03.bin",  0x000000, 0x80000, 0x4f1c2726 );
		ROM_LOAD( "pts1_04.bin",  0x080000, 0x80000, 0x52e813e0 );
		ROM_LOAD( "pts1_05.bin",  0x100000, 0x80000, 0xc7de2894 );
		ROM_LOAD( "pts1_06.bin",  0x180000, 0x80000, 0xba6617f1 );
		ROM_LOAD( "pts1_07.bin",  0x200000, 0x80000, 0xa67fc71e );
		ROM_LOAD( "pts1_08.bin",  0x280000, 0x80000, 0xeb6e20b6 );
		ROM_LOAD( "pts1_09.bin",  0x300000, 0x80000, 0xea05b513 );
		ROM_LOAD( "pts1_10.bin",  0x380000, 0x80000, 0x2e50d1e7 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_psailor2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "pts2_01.bin",  0x00000,  0x10000, 0x5a94677f );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "pts2_02.bin",  0x00000,  0x20000, 0x3432de51 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "pts2_03.bin",  0x000000, 0x80000, 0x2b8c992e );
		ROM_LOAD( "pts2_04.bin",  0x080000, 0x80000, 0xfea2d719 );
		ROM_LOAD( "pts2_05.bin",  0x100000, 0x80000, 0xbab4bcb5 );
		ROM_LOAD( "pts2_06.bin",  0x180000, 0x80000, 0x0bc750e2 );
		ROM_LOAD( "pts2_07.bin",  0x200000, 0x80000, 0x9a0f2cc5 );
		ROM_LOAD( "pts2_08.bin",  0x280000, 0x80000, 0xed617dda );
		ROM_LOAD( "pts2_09.bin",  0x300000, 0x80000, 0x7dded702 );
		ROM_LOAD( "pts2_10.bin",  0x380000, 0x80000, 0x7c0863c7 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_otatidai = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "otcd_01.bin",  0x00000,  0x10000, 0xa68acf90 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "otcd_02.bin",  0x00000,  0x20000, 0x30ed0e78 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "otcd_03.bin",  0x000000, 0x80000, 0xbf2cfc6b );
		ROM_LOAD( "otcd_04.bin",  0x080000, 0x80000, 0x76e9b597 );
		ROM_LOAD( "otcd_05.bin",  0x100000, 0x80000, 0x4e30e3b5 );
		ROM_LOAD( "otcd_06.bin",  0x180000, 0x80000, 0x5523d26e );
		ROM_LOAD( "otcd_07.bin",  0x200000, 0x80000, 0x8e86cc54 );
		ROM_LOAD( "otcd_08.bin",  0x280000, 0x80000, 0x8f92bc5c );
		ROM_LOAD( "otcd_09.bin",  0x300000, 0x80000, 0xe1c6c345 );
		ROM_LOAD( "otcd_10.bin",  0x380000, 0x80000, 0x20f74d5b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_ngpgal = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "ngpg_01.bin",  0x00000,  0x10000, 0xc766378b );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "ngpg_02.bin",  0x00000,  0x20000, 0xd193a2e1 );
	
		ROM_REGION( 0x280000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "ngpg_03.bin",  0x000000, 0x20000, 0x1f7bd813 );
		ROM_LOAD( "ngpg_04.bin",  0x020000, 0x20000, 0x4f5bd948 );
		ROM_LOAD( "ngpg_05.bin",  0x040000, 0x20000, 0xab65bcc9 );
		ROM_LOAD( "ngpg_06.bin",  0x060000, 0x20000, 0x0f469db1 );
		ROM_LOAD( "ngpg_07.bin",  0x080000, 0x20000, 0x637098a9 );
		ROM_LOAD( "ngpg_08.bin",  0x0a0000, 0x20000, 0x2452d06e );
		ROM_LOAD( "ngpg_09.bin",  0x0c0000, 0x20000, 0xda5dded0 );
		ROM_LOAD( "ngpg_10.bin",  0x0e0000, 0x20000, 0x94201d03 );
		ROM_LOAD( "ngpg_11.bin",  0x100000, 0x20000, 0x2bfc5d06 );
		ROM_LOAD( "ngpg_12.bin",  0x120000, 0x20000, 0xa7e6ecc2 );
		ROM_LOAD( "ngpg_13.bin",  0x140000, 0x20000, 0x5c43e71b );
		ROM_LOAD( "ngpg_14.bin",  0x160000, 0x20000, 0xe8b6802f );
		ROM_LOAD( "ngpg_15.bin",  0x180000, 0x20000, 0x7294b5ee );
		ROM_LOAD( "ngpg_16.bin",  0x1a0000, 0x20000, 0x3a1f7366 );
		ROM_LOAD( "ngpg_17.bin",  0x1c0000, 0x20000, 0x0b44f64e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjgottsu = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "mgek_01.bin",  0x00000,  0x10000, 0x949676d7 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.8d",  0x00000,  0x20000, 0x52c6a1a1 );
	
		ROM_REGION( 0x280000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1k",         0x000000, 0x20000, 0x58528909 );
		ROM_LOAD( "4.2k",         0x020000, 0x20000, 0xd09ad54d );
		ROM_LOAD( "5.3k",         0x040000, 0x20000, 0x40346785 );
		ROM_LOAD( "mgek_06.bin",  0x060000, 0x20000, 0xe96635e1 );
		ROM_LOAD( "mgek_07.bin",  0x080000, 0x20000, 0x174d7ad6 );
		ROM_LOAD( "mgek_08.bin",  0x0a0000, 0x20000, 0x65fd9c90 );
		ROM_LOAD( "mgek_09.bin",  0x0c0000, 0x20000, 0x417cd914 );
		ROM_LOAD( "mgek_10.bin",  0x0e0000, 0x20000, 0x1151414e );
		ROM_LOAD( "mgek_11.bin",  0x100000, 0x20000, 0x2ffd55be );
		ROM_LOAD( "mgek_12.bin",  0x120000, 0x20000, 0x7a731fa9 );
		ROM_LOAD( "mgek_13.bin",  0x140000, 0x20000, 0x6d4e56f7 );
		ROM_LOAD( "mgek_14.bin",  0x160000, 0x20000, 0xde3a675c );
		ROM_LOAD( "mgek_15.bin",  0x180000, 0x20000, 0xe1d6d504 );
		ROM_LOAD( "mgek_16.bin",  0x1a0000, 0x20000, 0xca1bca8d );
		ROM_LOAD( "mgek_17.bin",  0x1c0000, 0x20000, 0xa69973ad );
		ROM_LOAD( "mgek_18.bin",  0x1e0000, 0x20000, 0xd7ad46da );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_bakuhatu = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "1.4c",  0x00000,  0x10000, 0x687900ed );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.8d",  0x00000,  0x20000, 0x52c6a1a1 );
	
		ROM_REGION( 0x280000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.1k",   0x000000, 0x20000, 0x58528909 );
		ROM_LOAD( "4.2k",   0x020000, 0x20000, 0xd09ad54d );
		ROM_LOAD( "5.3k",   0x040000, 0x20000, 0x40346785 );
		ROM_LOAD( "6.4k",   0x060000, 0x20000, 0x772a6753 );
		ROM_LOAD( "7.5k",   0x080000, 0x20000, 0x3ab6b0b5 );
		ROM_LOAD( "8.6k",   0x0a0000, 0x20000, 0x5b1ca742 );
		ROM_LOAD( "9.7k",   0x0c0000, 0x20000, 0xf177fae1 );
		ROM_LOAD( "10.8k",  0x0e0000, 0x20000, 0xe9003e4d );
		ROM_LOAD( "11.10k", 0x100000, 0x20000, 0xc08d835e );
		ROM_LOAD( "12.11k", 0x120000, 0x20000, 0xae3cbba7 );
		ROM_LOAD( "13.1l",  0x140000, 0x20000, 0x1c402b12 );
		ROM_LOAD( "14.2l",  0x160000, 0x20000, 0x7bb49eaf );
		ROM_LOAD( "15.3l",  0x180000, 0x20000, 0xd0844179 );
		ROM_LOAD( "16.4l",  0x1a0000, 0x20000, 0x5fe47077 );
		ROM_LOAD( "17.5l",  0x1c0000, 0x20000, 0x9eab0682 );
		ROM_LOAD( "18.6l",  0x1e0000, 0x20000, 0x2b14cd5e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_cmehyou = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "cmhy_01.bin",  0x00000,  0x10000, 0x436dfa6c );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "cmhy_02.bin",  0x00000,  0x20000, 0xd193a2e1 );
	
		ROM_REGION( 0x280000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "cmhy_03.bin",  0x000000, 0x20000, 0x1f7bd813 );
		ROM_LOAD( "cmhy_04.bin",  0x020000, 0x20000, 0xbdb3de8b );
		ROM_LOAD( "cmhy_05.bin",  0x040000, 0x20000, 0x4f686de2 );
		ROM_LOAD( "cmhy_06.bin",  0x060000, 0x20000, 0xddd1ac23 );
		ROM_LOAD( "cmhy_07.bin",  0x080000, 0x20000, 0xf7c5367f );
		ROM_LOAD( "cmhy_08.bin",  0x0a0000, 0x20000, 0xf8eecdb5 );
		ROM_LOAD( "cmhy_09.bin",  0x0c0000, 0x20000, 0x11e2bbdf );
		ROM_LOAD( "cmhy_10.bin",  0x0e0000, 0x20000, 0xbbe489ae );
		ROM_LOAD( "cmhy_11.bin",  0x100000, 0x20000, 0x338efc1f );
		ROM_LOAD( "cmhy_12.bin",  0x120000, 0x20000, 0x6d9f9359 );
		ROM_LOAD( "cmhy_13.bin",  0x140000, 0x20000, 0x5c43e71b );
		ROM_LOAD( "cmhy_14.bin",  0x160000, 0x20000, 0xe8b6802f );
		ROM_LOAD( "cmhy_15.bin",  0x180000, 0x20000, 0xf7674a64 );
		ROM_LOAD( "cmhy_16.bin",  0x1a0000, 0x20000, 0x3a1f7366 );
		ROM_LOAD( "cmhy_17.bin",  0x1c0000, 0x20000, 0x1b8f6e4c );
		ROM_LOAD( "cmhy_18.bin",  0x1e0000, 0x20000, 0xfb86f955 );
		ROM_LOAD( "cmhy_19.bin",  0x200000, 0x20000, 0xfc89fa4f );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjkoiura = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "mjku_01.bin",  0x00000,  0x10000, 0xef9ae73e );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "mjku_02.bin",  0x00000,  0x20000, 0x3a230c22 );
	
		ROM_REGION( 0x280000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mjku_03.bin",  0x000000, 0x20000, 0x59432ccf );
		ROM_LOAD( "mjku_04.bin",  0x020000, 0x20000, 0xdf5816cb );
		ROM_LOAD( "mjku_05.bin",  0x040000, 0x20000, 0xbf01b952 );
		ROM_LOAD( "mjku_06.bin",  0x060000, 0x20000, 0x2dea05ef );
		ROM_LOAD( "mjku_07.bin",  0x080000, 0x20000, 0xc7843126 );
		ROM_LOAD( "mjku_08.bin",  0x0a0000, 0x20000, 0xc7f2fc2d );
		ROM_LOAD( "mjku_09.bin",  0x0c0000, 0x20000, 0x816b2a36 );
		ROM_LOAD( "mjku_10.bin",  0x0e0000, 0x20000, 0xc417fe11 );
		ROM_LOAD( "mjku_11.bin",  0x100000, 0x20000, 0x9e1914e2 );
		ROM_LOAD( "mjku_12.bin",  0x120000, 0x20000, 0x03607cec );
		ROM_LOAD( "mjku_13.bin",  0x140000, 0x20000, 0x18018e08 );
		ROM_LOAD( "mjku_14.bin",  0x160000, 0x20000, 0x4e835fc0 );
		ROM_LOAD( "mjku_15.bin",  0x180000, 0x20000, 0x8fe50109 );
		ROM_LOAD( "mjku_16.bin",  0x1a0000, 0x20000, 0xdc5b8688 );
		ROM_LOAD( "mjku_17.bin",  0x1c0000, 0x20000, 0x8579a7b8 );
		ROM_LOAD( "mjku_18.bin",  0x1e0000, 0x20000, 0xc5e330a4 );
		ROM_LOAD( "mjku_21.bin",  0x240000, 0x20000, 0x585998bd );
		ROM_LOAD( "mjku_22.bin",  0x260000, 0x20000, 0x64af3e5d );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mscoutm = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "mscm_01.bin",  0x00000,  0x10000, 0x9840ccd8 );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "mscm_02.bin",  0x00000,  0x20000, 0x4d2cbcab );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mscm_03.bin",  0x000000, 0x80000, 0xfae64c95 );
		ROM_LOAD( "mscm_04.bin",  0x080000, 0x80000, 0x03c80712 );
		ROM_LOAD( "mscm_05.bin",  0x100000, 0x80000, 0x107659f3 );
		ROM_LOAD( "mscm_06.bin",  0x180000, 0x80000, 0x61f7fa86 );
		ROM_LOAD( "mscm_07.bin",  0x200000, 0x80000, 0x10a71690 );
		ROM_LOAD( "mscm_08.bin",  0x280000, 0x80000, 0x3b55ef93 );
		ROM_LOAD( "mscm_09.bin",  0x300000, 0x80000, 0x5823d565 );
		ROM_LOAD( "mscm_10.bin",  0x380000, 0x80000, 0xc6d44c0e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_imekura = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "1.103",  0x00000,  0x10000, 0x3491083b );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "2.804",  0x00000,  0x20000, 0x1ef3e8f0 );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "03.602",  0x000000, 0x80000, 0x1eb05df4 );
		ROM_LOAD( "04.603",  0x080000, 0x80000, 0x48fefd7d );
		ROM_LOAD( "05.604",  0x100000, 0x80000, 0x934699a8 );
		ROM_LOAD( "06.605",  0x180000, 0x80000, 0xef97182d );
		ROM_LOAD( "07.606",  0x200000, 0x80000, 0xe3c6e401 );
		ROM_LOAD( "08.607",  0x280000, 0x80000, 0x08efb2bf );
		ROM_LOAD( "09.608",  0x300000, 0x80000, 0x94606c32 );
		ROM_LOAD( "10.609",  0x380000, 0x80000, 0x79958b86 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjegolf = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* main program */
		ROM_LOAD( "metg_01.bin",  0x00000,  0x10000, 0x1d7c2fcc );
	
		ROM_REGION( 0x20000, REGION_CPU2, 0 );/* sound program */
		ROM_LOAD( "metg_02.bin",  0x00000,  0x20000, 0x99f419cf );
	
		ROM_REGION( 0x400000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "metg_03.bin",  0x000000, 0x80000, 0x99097d30 );
		ROM_LOAD( "metg_04.bin",  0x080000, 0x80000, 0x9f1822b8 );
		ROM_LOAD( "metg_05.bin",  0x100000, 0x80000, 0x44b88726 );
		ROM_LOAD( "metg_06.bin",  0x180000, 0x80000, 0x59ad0d78 );
		ROM_LOAD( "metg_07.bin",  0x200000, 0x80000, 0x2d8b02d6 );
		ROM_LOAD( "metg_08.bin",  0x280000, 0x80000, 0xf64e16fb );
		ROM_LOAD( "metg_09.bin",  0x300000, 0x80000, 0x4231de76 );
		ROM_LOAD( "metg_10.bin",  0x380000, 0x80000, 0xe91c7adf );
	ROM_END(); }}; 
	
	
	//     YEAR,     NAME,   PARENT,  MACHINE,    INPUT,     INIT,    MONITOR, COMPANY, FULLNAME, FLAGS
	public static GameDriver driver_mjuraden	   = new GameDriver("1992"	,"mjuraden"	,"nbmj9195.java"	,rom_mjuraden,null	,machine_driver_mjuraden	,input_ports_mjuraden	,init_mjuraden	,ROT0	,	"Nichibutsu/Yubis", "Mahjong Uranai Densetsu (Japan)" )
	public static GameDriver driver_koinomp	   = new GameDriver("1992"	,"koinomp"	,"nbmj9195.java"	,rom_koinomp,null	,machine_driver_koinomp	,input_ports_koinomp	,init_koinomp	,ROT0	,	"Nichibutsu", "Mahjong Koi no Magic Potion (Japan)" )
	public static GameDriver driver_patimono	   = new GameDriver("1992"	,"patimono"	,"nbmj9195.java"	,rom_patimono,null	,machine_driver_patimono	,input_ports_patimono	,init_patimono	,ROT0	,	"Nichibutsu", "Mahjong Pachinko Monogatari (Japan)" )
	public static GameDriver driver_mjanbari	   = new GameDriver("1992"	,"mjanbari"	,"nbmj9195.java"	,rom_mjanbari,null	,machine_driver_mjanbari	,input_ports_mjanbari	,init_mjanbari	,ROT0	,	"Nichibutsu/Yubis/AV JAPAN", "Medal Mahjong Janjan Baribari [BET] (Japan)" )
	public static GameDriver driver_mmehyou	   = new GameDriver("1992"	,"mmehyou"	,"nbmj9195.java"	,rom_mmehyou,null	,machine_driver_mmehyou	,input_ports_mmehyou	,init_mmehyou	,ROT0	,	"Nichibutsu/Kawakusu", "Medal Mahjong Circuit no Mehyou [BET] (Japan)" )
	public static GameDriver driver_ultramhm	   = new GameDriver("1993"	,"ultramhm"	,"nbmj9195.java"	,rom_ultramhm,null	,machine_driver_ultramhm	,input_ports_ultramhm	,init_ultramhm	,ROT0	,	"Apple", "Ultra Maru-hi Mahjong (Japan)" )
	public static GameDriver driver_gal10ren	   = new GameDriver("1993"	,"gal10ren"	,"nbmj9195.java"	,rom_gal10ren,null	,machine_driver_gal10ren	,input_ports_gal10ren	,init_gal10ren	,ROT0	,	"FUJIC", "Mahjong Gal 10-renpatsu (Japan)" )
	public static GameDriver driver_renaiclb	   = new GameDriver("1993"	,"renaiclb"	,"nbmj9195.java"	,rom_renaiclb,null	,machine_driver_renaiclb	,input_ports_renaiclb	,init_renaiclb	,ROT0	,	"FUJIC", "Mahjong Ren-ai Club (Japan)" )
	public static GameDriver driver_mjlaman	   = new GameDriver("1993"	,"mjlaman"	,"nbmj9195.java"	,rom_mjlaman,null	,machine_driver_mjlaman	,input_ports_mjlaman	,init_mjlaman	,ROT0	,	"Nichibutsu/AV JAPAN", "Mahjong La Man (Japan)" )
	public static GameDriver driver_mkeibaou	   = new GameDriver("1993"	,"mkeibaou"	,"nbmj9195.java"	,rom_mkeibaou,null	,machine_driver_mkeibaou	,input_ports_mkeibaou	,init_mkeibaou	,ROT0	,	"Nichibutsu", "Mahjong Keibaou (Japan)" )
	public static GameDriver driver_pachiten	   = new GameDriver("1993"	,"pachiten"	,"nbmj9195.java"	,rom_pachiten,null	,machine_driver_pachiten	,input_ports_pachiten	,init_pachiten	,ROT0	,	"Nichibutsu/MIKI SYOUJI/AV JAPAN", "Medal Mahjong Pachi-Slot Tengoku [BET] (Japan)" )
	public static GameDriver driver_sailorws	   = new GameDriver("1993"	,"sailorws"	,"nbmj9195.java"	,rom_sailorws,null	,machine_driver_sailorws	,input_ports_sailorws	,init_sailorws	,ROT0	,	"Nichibutsu", "Mahjong Sailor Wars (Japan)" )
	public static GameDriver driver_sailorwr	   = new GameDriver("1993"	,"sailorwr"	,"nbmj9195.java"	,rom_sailorwr,driver_sailorws	,machine_driver_sailorwr	,input_ports_sailorwr	,init_sailorwr	,ROT0	,	"Nichibutsu", "Mahjong Sailor Wars-R [BET] (Japan)" )
	public static GameDriver driver_psailor1	   = new GameDriver("1994"	,"psailor1"	,"nbmj9195.java"	,rom_psailor1,null	,machine_driver_psailor1	,input_ports_psailor1	,init_psailor1	,ROT0	,	"SPHINX", "Bishoujo Janshi Pretty Sailor 18-kin (Japan)" )
	public static GameDriver driver_psailor2	   = new GameDriver("1994"	,"psailor2"	,"nbmj9195.java"	,rom_psailor2,null	,machine_driver_psailor2	,input_ports_psailor2	,init_psailor2	,ROT0	,	"SPHINX", "Bishoujo Janshi Pretty Sailor 2 (Japan)" )
	public static GameDriver driver_otatidai	   = new GameDriver("1995"	,"otatidai"	,"nbmj9195.java"	,rom_otatidai,null	,machine_driver_otatidai	,input_ports_otatidai	,init_otatidai	,ROT0	,	"SPHINX", "Disco Mahjong Otachidai no Okite (Japan)" )
	
	public static GameDriver driver_ngpgal	   = new GameDriver("1991"	,"ngpgal"	,"nbmj9195.java"	,rom_ngpgal,null	,machine_driver_ngpgal	,input_ports_ngpgal	,init_ngpgal	,ROT0	,	"Nichibutsu", "Nekketsu Grand-Prix Gal (Japan)" )
	public static GameDriver driver_mjgottsu	   = new GameDriver("1991"	,"mjgottsu"	,"nbmj9195.java"	,rom_mjgottsu,null	,machine_driver_mjgottsu	,input_ports_mjgottsu	,init_mjgottsu	,ROT0	,	"Nichibutsu", "Mahjong Gottsu ee-kanji (Japan)" )
	public static GameDriver driver_bakuhatu	   = new GameDriver("1991"	,"bakuhatu"	,"nbmj9195.java"	,rom_bakuhatu,driver_mjgottsu	,machine_driver_bakuhatu	,input_ports_bakuhatu	,init_bakuhatu	,ROT0	,	"Nichibutsu", "Mahjong Bakuhatsu Junjouden (Japan)" )
	public static GameDriver driver_cmehyou	   = new GameDriver("1992"	,"cmehyou"	,"nbmj9195.java"	,rom_cmehyou,null	,machine_driver_cmehyou	,input_ports_cmehyou	,init_cmehyou	,ROT0	,	"Nichibutsu/Kawakusu", "Mahjong Circuit no Mehyou (Japan)" )
	public static GameDriver driver_mjkoiura	   = new GameDriver("1992"	,"mjkoiura"	,"nbmj9195.java"	,rom_mjkoiura,null	,machine_driver_mjkoiura	,input_ports_mjkoiura	,init_mjkoiura	,ROT0	,	"Nichibutsu", "Mahjong Koi Uranai (Japan)" )
	
	public static GameDriver driver_mscoutm	   = new GameDriver("1994"	,"mscoutm"	,"nbmj9195.java"	,rom_mscoutm,null	,machine_driver_mscoutm	,input_ports_mscoutm	,init_mscoutm	,ROT0	,	"SPHINX/AV JAPAN", "Mahjong Scout Man (Japan)" )
	public static GameDriver driver_imekura	   = new GameDriver("1994"	,"imekura"	,"nbmj9195.java"	,rom_imekura,null	,machine_driver_imekura	,input_ports_imekura	,init_imekura	,ROT0	,	"SPHINX/AV JAPAN", "Imekura Mahjong (Japan)" )
	public static GameDriver driver_mjegolf	   = new GameDriver("1994"	,"mjegolf"	,"nbmj9195.java"	,rom_mjegolf,null	,machine_driver_mjegolf	,input_ports_mjegolf	,init_mjegolf	,ROT0	,	"FUJIC/AV JAPAN", "Mahjong Erotica Golf (Japan)" )
}
