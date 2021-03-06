/******************************************************************************

	Game Driver for Nichibutsu Mahjong series.

	Gionbana
	(c)1989 Nihon Bussan Co.,Ltd.

	Mahjong Hana no Momoko gumi
	(c)1988 Nihon Bussan Co.,Ltd.

	Mahjong Satsujin Jiken
	(c)1988 Nihon Bussan Co.,Ltd.

	Telephone Mahjong
	(c)1988 Nihon Bussan Co.,Ltd.

	Mahjong G-MEN'89
	(c)1989 Nihon Bussan Co.,Ltd.

	Mahjong Focus
	(c)1989 Nihon Bussan Co.,Ltd.

	Mahjong Focus (Medal Type)
	(c)1989 Nihon Bussan Co.,Ltd.

	Nozokimeguri Mahjong Peep Show
	(c)1989 AC

	Scandal Mahjong
	(c)1989 Nihon Bussan Co.,Ltd.

	Scandal Mahjong (Medal Type)
	(c)1989 Nihon Bussan Co.,Ltd.

	Mahjong Nanpa Story
	(c)1989 BROOKS

	Mahjong Banana Dream (Medal Type)
	(c)1989 DIGITAL SOFT

	Mahjong CLUB 90's (set 1)
	(c)1990 Nihon Bussan Co.,Ltd.

	Mahjong CLUB 90's (set 2)
	(c)1990 Nihon Bussan Co.,Ltd.

	Mahjong THE LADY HUNTER
	(c)1990 Nihon Bussan Co.,Ltd.

	Mahjong Chinmoku no Hentai
	(c)1990 Nihon Bussan Co.,Ltd.

	Maikobana
	(c)1990 Nihon Bussan Co.,Ltd.

	Hana to Ojisan (Medal Type)
	(c)1991 Nihon Bussan Co.,Ltd.

	Driver by Takahiro Nogi <nogi@kt.rim.or.jp> 1999/11/05 -

******************************************************************************/
/******************************************************************************

Notes:
- mmcamera is the medal version of mjcamera, however the two don't run on the
  same hardware. mjcamera is in nbmj8688.c.

TODO:

- mjfocus was crashing because of nested NMIs, to fix that I bumped the
  clock from 5 MHz to 10 MHz.

- In mjfocus(Medal Type), sometimes CPU's hands are forced out from the screen.
  This is correct behaviour.

- Telmajan cannot set to JAMMA type. I don't know why.

- Controls in gionbana: 1~8 is assigned to A~H, "Yes" is LShift, "No" is Z.

- Controls in maiko and hanaoji: 1~8 is assigned to A~H, "Yes" is M, "No" is N.

- Font display in hanamomo is different from real machine.

- Real machine has ROMs for protection, but I don't know how to access the ROM,
  so I'm doing something that works but is probably wrong.
  The interesting thing about that ROM is that it comes from other, older games,
  so it isn't needed, it's just verified for protection.
  mjfocusm does a different check from the others. All the other games read the
  protection ROM through the sound ROMinterface, mjfocusm reads it from somewhere
  else.

- Some games display "GFXROM BANK OVER!!" or "GFXROM ADDRESS OVER!!"
  in Debug build.

- Screen flipping is not perfect.

******************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class nbmj8891
{
	
	
	#define	SIGNED_DAC	0		// 0:unsigned DAC, 1:signed DAC
	
	
	VIDEO_UPDATE( gionbana );
	VIDEO_START( gionbana );
	VIDEO_START( hanamomo );
	
	void gionbana_radrx_w(int data);
	void gionbana_radry_w(int data);
	void gionbana_sizex_w(int data);
	void gionbana_sizey_w(int data);
	void gionbana_gfxflag_w(int data);
	void gionbana_drawx_w(int data);
	void gionbana_drawy_w(int data);
	void gionbana_scrolly_w(int data);
	void gionbana_vramsel_w(int data);
	void gionbana_romsel_w(int data);
	void gionbana_paltblnum_w(int data);
	
	
	static DRIVER_INIT( gionbana )
	{
		UINT8 *prot = memory_region(REGION_USER1);
		int i;
	
		/* this is one possible way to rearrange the protection ROM data to get the
		   expected 0x5ece checksum. It's probably completely wrong! But since the
		   game doesn't do anything else with that ROM, this is more than enough. I
		   could just fill this are with fake data, the only thing that matters is
		   the checksum. */
		for (i = 0;i < 0x20000;i++)
		{
			prot[i] = BITSWAP8(prot[i],2,7,3,5,0,6,4,1);
		}
	
		nb1413m3_type = NB1413M3_GIONBANA;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( hanamomo )
	{
		nb1413m3_type = NB1413M3_HANAMOMO;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( msjiken )
	{
		nb1413m3_type = NB1413M3_MSJIKEN;
		nb1413m3_int_count = 144;
	}
	
	static DRIVER_INIT( telmahjn )
	{
		UINT8 *prot = memory_region(REGION_USER1);
		int i;
	
		/* this is one possible way to rearrange the protection ROM data to get the
		   expected 0x7354 checksum. It's probably completely wrong! But since the
		   game doesn't do anything else with that ROM, this is more than enough. I
		   could just fill this are with fake data, the only thing that matters is
		   the checksum. */
		for (i = 0;i < 0x20000;i++)
		{
			prot[i] = BITSWAP8(prot[i + 0x20000],7,0,4,1,5,2,6,3);
		}
	
		nb1413m3_type = NB1413M3_TELMAHJN;
		nb1413m3_int_count = 144;
	}
	
	static DRIVER_INIT( mgmen89 )
	{
		UINT8 *prot = memory_region(REGION_USER1);
		int i;
	
		/* this is one possible way to rearrange the protection ROM data to get the
		   expected 0x4b98 checksum. It's probably completely wrong! But since the
		   game doesn't do anything else with that ROM, this is more than enough. I
		   could just fill this are with fake data, the only thing that matters is
		   the checksum. */
		for (i = 0;i < 0x20000;i++)
		{
			prot[i] = BITSWAP8(prot[i],5,6,1,0,7,3,2,4);
		}
	
		nb1413m3_type = NB1413M3_MGMEN89;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( mjfocus )
	{
		UINT8 *prot = memory_region(REGION_USER1);
		unsigned char *ram = memory_region(REGION_CPU1) + 0xf800;
		int i;
	
		/* need to clear RAM otherwise it doesn't boot... */
		for (i = 0; i < 0x800; i++) ram[i] = 0x00;
	
		/* this is one possible way to rearrange the protection ROM data to get the
		   expected 0x7354 checksum. It's probably completely wrong! But since the
		   game doesn't do anything else with that ROM, this is more than enough. I
		   could just fill this are with fake data, the only thing that matters is
		   the checksum. */
		for (i = 0;i < 0x20000;i++)
		{
			prot[i] = BITSWAP8(prot[i + 0x20000],7,0,4,1,5,2,6,3);
		}
	
		nb1413m3_type = NB1413M3_MJFOCUS;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( mjfocusm )
	{
	#if 1
		unsigned char *ROM = memory_region(REGION_CPU1);
	
		// Protection ROM check skip
		ROM[0x014e] = 0x00;
		ROM[0x014f] = 0x00;
		ROM[0x0150] = 0x00;
	#endif
		nb1413m3_type = NB1413M3_MJFOCUSM;
		nb1413m3_int_count = 128;
	}
	
	static DRIVER_INIT( scandal )
	{
		unsigned char *ROM = memory_region(REGION_CPU1);
		int i;
	
		for (i = 0xf800; i < 0x10000; i++) ROM[i] = 0x00;
	
		nb1413m3_type = NB1413M3_SCANDAL;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( scandalm )
	{
		nb1413m3_type = NB1413M3_SCANDALM;
		nb1413m3_int_count = 128;
	}
	
	static DRIVER_INIT( mjnanpas )
	{
		/* they forgot to enable the protection check in this game... */
	#if 0
		UINT8 *prot = memory_region(REGION_USER1);
		int i;
	
		memory_region(REGION_CPU1)[0x003d] = 0x01;	// force the protection check to be executed
	
		/* this is one possible way to rearrange the protection ROM data to get the
		   expected 0xfe1a checksum. It's probably completely wrong! But since the
		   game doesn't do anything else with that ROM, this is more than enough. I
		   could just fill this are with fake data, the only thing that matters is
		   the checksum. */
		for (i = 0;i < 0x20000;i++)
		{
			prot[i] = BITSWAP8(prot[i + 0x20000],0,5,2,3,6,7,1,4);
		}
	#endif
	
		nb1413m3_type = NB1413M3_MJNANPAS;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( bananadr )
	{
		nb1413m3_type = NB1413M3_BANANADR;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( club90s )
	{
		nb1413m3_type = NB1413M3_CLUB90S;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( mladyhtr )
	{
		nb1413m3_type = NB1413M3_MLADYHTR;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( chinmoku )
	{
		nb1413m3_type = NB1413M3_CHINMOKU;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( maiko )
	{
		nb1413m3_type = NB1413M3_MAIKO;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( hanaoji )
	{
		nb1413m3_type = NB1413M3_HANAOJI;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( pairsten )
	{
		UINT8 *prot = memory_region(REGION_USER1);
		int i;
	
		/* this is one possible way to rearrange the protection ROM data to get the
		   expected 0x8374 checksum. It's probably completely wrong! But since the
		   game doesn't do anything else with that ROM, this is more than enough. I
		   could just fill this are with fake data, the only thing that matters is
		   the checksum. */
		for (i = 0;i < 0x20000;i++)
		{
			prot[i] = BITSWAP8(prot[i + 0x20000],5,6,0,4,3,7,1,2);
		}
	
		nb1413m3_type = NB1413M3_PAIRSTEN;
		nb1413m3_int_count = 132;
	}
	
	static DRIVER_INIT( mmcamera )
	{
		nb1413m3_type = NB1413M3_HANAMOMO;
		nb1413m3_int_count = 132;
	}
	
	
	
	public static Memory_ReadAddress readmem_gionbana[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf00f, gionbana_paltbl_r ),
		new Memory_ReadAddress( 0xf400, 0xf5ff, gionbana_palette_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_gionbana[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf00f, gionbana_paltbl_w ),
		new Memory_WriteAddress( 0xf400, 0xf5ff, gionbana_palette_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM, nb1413m3_nvram, nb1413m3_nvram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_hanamomo[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf1ff, gionbana_palette_r ),
		new Memory_ReadAddress( 0xf400, 0xf40f, gionbana_paltbl_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_hanamomo[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf1ff, gionbana_palette_w ),
		new Memory_WriteAddress( 0xf400, 0xf40f, gionbana_paltbl_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_scandalm[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf400, 0xf5ff, gionbana_palette_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_scandalm[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf400, 0xf5ff, gionbana_palette_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM, nb1413m3_nvram, nb1413m3_nvram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_club90s[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xf80f, gionbana_paltbl_r ),
		new Memory_ReadAddress( 0xfc00, 0xfdff, gionbana_palette_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_club90s[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf800, 0xf80f, gionbana_paltbl_w ),
		new Memory_WriteAddress( 0xfc00, 0xfdff, gionbana_palette_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_maiko[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf1ff, maiko_palette_r ),
		new Memory_ReadAddress( 0xf400, 0xf40f, gionbana_paltbl_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_maiko[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf1ff, maiko_palette_w ),
		new Memory_WriteAddress( 0xf400, 0xf40f, gionbana_paltbl_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_hanaoji[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf200, 0xf3ff, maiko_palette_r ),
		new Memory_ReadAddress( 0xf700, 0xf70f, gionbana_paltbl_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_hanaoji[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf200, 0xf3ff, maiko_palette_w ),
		new Memory_WriteAddress( 0xf700, 0xf70f, gionbana_paltbl_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM, nb1413m3_nvram, nb1413m3_nvram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static ReadHandlerPtr io_gionbana_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		if (offset < 0x8000) return nb1413m3_sndrom_r(offset);
	
		switch (offset & 0xff00)
		{
			case	0x9000:	return nb1413m3_inputport0_r(0);
			case	0xa000:	return nb1413m3_inputport1_r(0);
			case	0xb000:	return nb1413m3_inputport2_r(0);
			case	0xc000:	return nb1413m3_inputport3_r(0);
			case	0xf000:	return nb1413m3_dipsw1_r(0);
			case	0xf100:	return nb1413m3_dipsw2_r(0);
			default:	return 0xff;
		}
	} };
	
	public static IO_ReadPort readport_gionbana[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0xffff, io_gionbana_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_gionbana_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		switch (offset & 0xff00)
		{
			case	0x0000:	nb1413m3_nmi_clock_w(0,data); break;
			case	0x2000:	gionbana_radrx_w(data); break;
			case	0x2100:	gionbana_radry_w(data); break;
			case	0x2200:	gionbana_drawx_w(data); break;
			case	0x2300:	gionbana_drawy_w(data); break;
			case	0x2400:	gionbana_sizex_w(data); break;
			case	0x2500:	gionbana_sizey_w(data); break;
			case	0x2600:	gionbana_gfxflag_w(data); break;
			case	0x2700:	break;
			case	0x4000:	gionbana_paltblnum_w(data); break;
			case	0x6000:	gionbana_romsel_w(data); break;
			case	0x7000:	gionbana_scrolly_w(data); break;
			case	0x8000:	YM3812_control_port_0_w(0, data); break;
			case	0x8100:	YM3812_write_port_0_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(0,data); break;
			case	0xb000:	nb1413m3_sndrombank1_w(0,data); break;
			case	0xc000:	break;
	#if SIGNED_DAC
			case	0xd000:	DAC_0_signed_data_w(0, data); break;
	#else
			case	0xd000:	DAC_0_data_w(0, data); break;
	#endif
			case	0xe000:	gionbana_vramsel_w(data); break;
			case	0xf000:	nb1413m3_outcoin_w(0,data); break;
		}
	} };
	
	public static IO_WritePort writeport_gionbana[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_gionbana_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_hanamomo_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		switch (offset & 0xff00)
		{
			case	0x0000:	nb1413m3_nmi_clock_w(0,data); break;
			case	0x3000:	gionbana_radrx_w(data); break;
			case	0x3100:	gionbana_radry_w(data); break;
			case	0x3200:	gionbana_drawx_w(data); break;
			case	0x3300:	gionbana_drawy_w(data); break;
			case	0x3400:	gionbana_sizex_w(data); break;
			case	0x3500:	gionbana_sizey_w(data); break;
			case	0x3600:	gionbana_gfxflag_w(data); break;
			case	0x3700:	break;
			case	0x4000:	gionbana_paltblnum_w(data); break;
			case	0x6000:	gionbana_romsel_w(data); break;
			case	0x7000:	gionbana_scrolly_w(data); break;
			case	0x8000:	YM3812_control_port_0_w(0, data); break;
			case	0x8100:	YM3812_write_port_0_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(0,data); break;
			case	0xb000:	nb1413m3_sndrombank1_w(0,data); break;
			case	0xc000:	break;
	#if SIGNED_DAC
			case	0xd000:	DAC_0_signed_data_w(0, data); break;
	#else
			case	0xd000:	DAC_0_data_w(0, data); break;
	#endif
			case	0xe000:	break;
			case	0xf000:	break;
		}
	} };
	
	public static IO_WritePort writeport_hanamomo[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_hanamomo_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_msjiken_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		switch (offset & 0xff00)
		{
			case	0x0000:	nb1413m3_nmi_clock_w(0,data); break;
			case	0x4000:	gionbana_paltblnum_w(data); break;
			case	0x5000:	gionbana_radrx_w(data); break;
			case	0x5100:	gionbana_radry_w(data); break;
			case	0x5200:	gionbana_drawx_w(data); break;
			case	0x5300:	gionbana_drawy_w(data); break;
			case	0x5400:	gionbana_sizex_w(data); break;
			case	0x5500:	gionbana_sizey_w(data); break;
			case	0x5600:	gionbana_gfxflag_w(data); break;
			case	0x5700:	break;
			case	0x6000:	gionbana_romsel_w(data); break;
			case	0x7000:	gionbana_scrolly_w(data); break;
			case	0x8000:	YM3812_control_port_0_w(0, data); break;
			case	0x8100:	YM3812_write_port_0_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(0,data); break;
			case	0xb000:	nb1413m3_sndrombank1_w(0,data); break;
			case	0xc000:	break;
	#if SIGNED_DAC
			case	0xd000:	DAC_0_signed_data_w(0, data); break;
	#else
			case	0xd000:	DAC_0_data_w(0, data); break;
	#endif
			case	0xe000:	break;
			case	0xf000:	break;
		}
	} };
	
	public static IO_WritePort writeport_msjiken[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_msjiken_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_scandal_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		if ((0x4000 <= offset) && (0x5000 > offset))
		{
			gionbana_paltbl_w(((offset & 0x0f00) >> 8), data);
			return;
		}
	
		switch (offset & 0xff00)
		{
			case	0x0000:	gionbana_radrx_w(data); break;
			case	0x0100:	gionbana_radry_w(data); break;
			case	0x0200:	gionbana_drawx_w(data); break;
			case	0x0300:	gionbana_drawy_w(data); break;
			case	0x0400:	gionbana_sizex_w(data); break;
			case	0x0500:	gionbana_sizey_w(data); break;
			case	0x0600:	gionbana_gfxflag_w(data); break;
			case	0x0700:	break;
			case	0x1000:	gionbana_romsel_w(data); break;
			case	0x2000:	gionbana_paltblnum_w(data); break;
			case	0x5000:	gionbana_scrolly_w(data); break;
			case	0x8000:	YM3812_control_port_0_w(0, data); break;
			case	0x8100:	YM3812_write_port_0_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(0,data); break;
			case	0xb000:	nb1413m3_sndrombank1_w(0,data); break;
			case	0xc000:	nb1413m3_nmi_clock_w(0,data); break;
	#if SIGNED_DAC
			case	0xd000:	DAC_0_signed_data_w(0, data); break;
	#else
			case	0xd000:	DAC_0_data_w(0, data); break;
	#endif
			case	0xe000:	break;
			case	0xf000:	break;
		}
	} };
	
	public static IO_WritePort writeport_scandal[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_scandal_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static ReadHandlerPtr io_scandalm_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		if (offset < 0x8000) return nb1413m3_sndrom_r(offset);
	
		switch (offset & 0xff00)
		{
			case	0x8100:	return AY8910_read_port_0_r(0);
			case	0x9000:	return nb1413m3_inputport0_r(0);
			case	0xa000:	return nb1413m3_inputport1_r(0);
			case	0xb000:	return nb1413m3_inputport2_r(0);
			case	0xc000:	return nb1413m3_inputport3_r(0);
			case	0xf000:	return nb1413m3_dipsw1_r(0);
			case	0xf100:	return nb1413m3_dipsw2_r(0);
			default:	return 0xff;
		}
	} };
	
	public static IO_ReadPort readport_scandalm[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0xffff, io_scandalm_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_scandalm_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		if ((0x4000 <= offset) && (0x5000 > offset))
		{
			gionbana_paltbl_w(((offset & 0x0f00) >> 8), data);
			return;
		}
	
		switch (offset & 0xff00)
		{
			case	0x0000:	gionbana_radrx_w(data); break;
			case	0x0100:	gionbana_radry_w(data); break;
			case	0x0200:	gionbana_drawx_w(data); break;
			case	0x0300:	gionbana_drawy_w(data); break;
			case	0x0400:	gionbana_sizex_w(data); break;
			case	0x0500:	gionbana_sizey_w(data); break;
			case	0x0600:	gionbana_gfxflag_w(data); break;
			case	0x0700:	break;
			case	0x1000:	gionbana_romsel_w(data); break;
			case	0x2000:	gionbana_paltblnum_w(data); break;
			case	0x5000:	gionbana_scrolly_w(data); break;
			case	0x8200:	AY8910_write_port_0_w(0, data); break;
			case	0x8300:	AY8910_control_port_0_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(0,data); break;
			case	0xb000:	nb1413m3_sndrombank1_w(0,data); break;
			case	0xc000:	nb1413m3_nmi_clock_w(0,data); break;
	#if SIGNED_DAC
			case	0xd000:	DAC_0_signed_data_w(0, data); break;
	#else
			case	0xd000:	DAC_0_data_w(0, data); break;
	#endif
			case	0xe000:	break;
			case	0xf000:	nb1413m3_outcoin_w(0,data); break;
		}
	} };
	
	public static IO_WritePort writeport_scandalm[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_scandalm_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_bananadr_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		if ((0x4000 <= offset) && (0x5000 > offset))
		{
			gionbana_paltbl_w(((offset & 0x0f00) >> 8), data);
			return;
		}
	
		switch (offset & 0xff00)
		{
			case	0x0000:	gionbana_radrx_w(data); break;
			case	0x0100:	gionbana_radry_w(data); break;
			case	0x0200:	gionbana_drawx_w(data); break;
			case	0x0300:	gionbana_drawy_w(data); break;
			case	0x0400:	gionbana_sizex_w(data); break;
			case	0x0500:	gionbana_sizey_w(data); break;
			case	0x0600:	gionbana_gfxflag_w(data); break;
			case	0x0700:	break;
			case	0x1000:	gionbana_romsel_w(data); break;
			case	0x2000:	gionbana_paltblnum_w(data); break;
			case	0x3000:	gionbana_vramsel_w(data); break;
			case	0x5000:	gionbana_scrolly_w(data); break;
			case	0x8000:	YM3812_control_port_0_w(0, data); break;
			case	0x8100:	YM3812_write_port_0_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(0,data); break;
			case	0xb000:	nb1413m3_sndrombank1_w(0,data); break;
			case	0xc000:	nb1413m3_nmi_clock_w(0,data); break;
	#if SIGNED_DAC
			case	0xd000:	DAC_0_signed_data_w(0, data); break;
	#else
			case	0xd000:	DAC_0_data_w(0, data); break;
	#endif
			case	0xe000:	break;
			case	0xf000:	nb1413m3_outcoin_w(0,data); break;
		}
	} };
	
	public static IO_WritePort writeport_bananadr[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_bananadr_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static ReadHandlerPtr io_maiko_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		if (offset < 0x8000) return nb1413m3_sndrom_r(offset);
	
		switch (offset & 0xff00)
		{
			case	0x9000:	return nb1413m3_inputport0_r(0);
			case	0xa000:	return nb1413m3_inputport1_r(0);
			case	0xb000:	return nb1413m3_inputport2_r(0);
			case	0xc000:	return nb1413m3_inputport3_r(0);
			case	0xf000:	return nb1413m3_dipsw1_r(0);
			case	0xf100:	return nb1413m3_dipsw2_r(0);
			default:	return 0xff;
		}
	} };
	
	public static IO_ReadPort readport_maiko[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0xffff, io_maiko_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_maiko_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		switch (offset & 0xff00)
		{
			case	0x0000:	nb1413m3_nmi_clock_w(0,data); break;
			case	0x4000:	gionbana_paltblnum_w(data); break;
			case	0x5000:	gionbana_radrx_w(data); break;
			case	0x5100:	gionbana_radry_w(data); break;
			case	0x5200:	gionbana_drawx_w(data); break;
			case	0x5300:	gionbana_drawy_w(data); break;
			case	0x5400:	gionbana_sizex_w(data); break;
			case	0x5500:	gionbana_sizey_w(data); break;
			case	0x5600:	gionbana_gfxflag_w(data); break;
			case	0x5700:	break;
			case	0x6000:	gionbana_romsel_w(data); break;
			case	0x7000:	gionbana_scrolly_w(data); break;
			case	0x8000:	YM3812_control_port_0_w(0, data); break;
			case	0x8100:	YM3812_write_port_0_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(0,data); break;
			case	0xb000:	nb1413m3_sndrombank1_w(0,data); break;
			case	0xc000:	break;
	#if SIGNED_DAC
			case	0xd000:	DAC_0_signed_data_w(0, data); break;
	#else
			case	0xd000:	DAC_0_data_w(0, data); break;
	#endif
			case	0xe000:	gionbana_vramsel_w(data); break;
			case	0xf000:	nb1413m3_outcoin_w(0,data); break;
		}
	} };
	
	public static IO_WritePort writeport_maiko[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_maiko_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_hanamomo = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Character Display Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mmcamera = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_msjiken = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Control Type" );
		PORT_DIPSETTING(    0x80, "ROYAL" );
		PORT_DIPSETTING(    0x00, "JAMMA" );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	
		PORT_START(); 	/* (8) JAMMA-1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (9) JAMMA-2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY);
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY);
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY);
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY);
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_telmahjn = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Control Type" );
		PORT_DIPSETTING(    0x80, "ROYAL" );
		PORT_DIPSETTING(    0x00, "JAMMA" );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	
		PORT_START(); 	/* (8) JAMMA-1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (9) JAMMA-2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY);
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY);
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY);
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY);
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_gionbana = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x00, "1 Coin/10 Credits" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x00, "Oyaken" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, "Ino-Shika-Chou" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, "Tsukimi de Ippai" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Hanami de Ippai" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, "Shichi-Go-San" );
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
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mgmen89 = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjfocus = new InputPortPtr(){ public void handler() { 
	
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
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, "Character Display Test" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjfocusm = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, "Game Out" );
		PORT_DIPSETTING(    0x07, "95% (Easy); )
		PORT_DIPSETTING(    0x06, "90%" );
		PORT_DIPSETTING(    0x05, "85%" );
		PORT_DIPSETTING(    0x04, "80%" );
		PORT_DIPSETTING(    0x03, "75%" );
		PORT_DIPSETTING(    0x02, "70%" );
		PORT_DIPSETTING(    0x01, "65%" );
		PORT_DIPSETTING(    0x00, "60% (Hard); )
		PORT_DIPNAME( 0x08, 0x00, "Last Chance" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, "W.Bet" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Show summary" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x06, 0x06, "Bet Min" );
		PORT_DIPSETTING(    0x06, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x18, 0x00, "Bet Max" );
		PORT_DIPSETTING(    0x18, "8" );
		PORT_DIPSETTING(    0x10, "10" );
		PORT_DIPSETTING(    0x08, "12" );
		PORT_DIPSETTING(    0x00, "20" );
		PORT_DIPNAME( 0x20, 0x20, "Bet1 Only" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Score Pool" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Change Rate" );
		PORT_DIPSETTING(    0x80, "A" );
		PORT_DIPSETTING(    0x00, "B" );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_peepshow = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_scandal = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "Character Display Test" );
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_scandalm = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x08, 0x00, "Last Chance" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "W.Bet" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x06, 0x06, "Bet Min" );
		PORT_DIPSETTING(    0x06, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x18, 0x00, "Bet Max" );
		PORT_DIPSETTING(    0x18, "8" );
		PORT_DIPSETTING(    0x10, "10" );
		PORT_DIPSETTING(    0x08, "12" );
		PORT_DIPSETTING(    0x00, "20" );
		PORT_DIPNAME( 0x20, 0x20, "Bet1 Only" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Score Pool" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjnanpas = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Character Display Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjnanpaa = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_bananadr = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
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
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Character Display Test" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "Score Pool" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_club90s = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1" );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Game Sounds" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Allow Continue" );
		PORT_DIPSETTING(    0x40, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mladyhtr = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1" );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Game Mode" );
		PORT_DIPSETTING(    0x40, "Beginner" );
		PORT_DIPSETTING(    0x00, "Expert" );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_chinmoku = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_maiko = new InputPortPtr(){ public void handler() { 
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
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, "Oyaken" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Local Rule" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Graphic ROM Test" );
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
	
		PORT_START(); 	/* (2) PORT 0-1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	
		PORT_START(); 	/* (8) PORT 0-2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	// OUT COIN
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );	//
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_hanaoji = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
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
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0-1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	
		PORT_START(); 	/* (8) PORT 0-2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	// OUT COIN
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );	//
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_pairsten = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "Easy" );// Hearts : 10 - Time : 60/60
		PORT_DIPSETTING(    0x02, "Medium" );// Hearts :  7 - Time : 44/60
		PORT_DIPSETTING(    0x01, "Hard" );// Hearts :  5 - Time : 32/60
		PORT_DIPSETTING(    0x00, "Hardest" );// Hearts :  3 - Time : 24/60
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x00, "Demo Music" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, "Game Music" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Character Display Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();  /* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();  /* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );// DRAW BUSY
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );// not in "test mode"
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	
	
	static struct YM3812interface ym3812_interface =
	{
		1,				/* 1 chip */
		2500000,			/* 4 MHz */
		{ 50 }
	};
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,				/* 1 chip */
		1250000,			/* 1.25 MHz ?? */
		new int[] { 35 },
		new ReadHandlerPtr[] { input_port_0_r },		// DIPSW-A read
		new ReadHandlerPtr[] { input_port_1_r },		// DIPSW-B read
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static DACinterface dac_interface = new DACinterface
	(
		1,				/* 1 channels */
		new int[] { 100 }
	);
	
	
	
	
	static MACHINE_DRIVER_START( gionbana )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", Z80, 20000000/4)	/* 5.00 MHz ? */
		MDRV_CPU_FLAGS(CPU_16BIT_PORT)
		MDRV_CPU_MEMORY(readmem_gionbana,writemem_gionbana)
		MDRV_CPU_PORTS(readport_gionbana,writeport_gionbana)
		MDRV_CPU_VBLANK_INT(nb1413m3_interrupt,132)	// nmiclock = 60
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(nb1413m3)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_SCREEN_SIZE(512, 256)
		MDRV_VISIBLE_AREA(0, 512-1, 7, 247-1)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(gionbana)
		MDRV_VIDEO_UPDATE(gionbana)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("3812", YM3812, ym3812_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	/* NBMJDRV2 */
	static MACHINE_DRIVER_START( mmcamera )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(gionbana)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(readport_gionbana,writeport_hanamomo)
		MDRV_CPU_VBLANK_INT(nb1413m3_interrupt,128)
	
		MDRV_NVRAM_HANDLER(nb1413m3)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0, 512-1, 15, 239-1)
		MDRV_VIDEO_START(hanamomo)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( hanamomo )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(gionbana)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_hanamomo,writemem_hanamomo)
		MDRV_CPU_PORTS(readport_gionbana,writeport_hanamomo)
		MDRV_CPU_VBLANK_INT(nb1413m3_interrupt,128)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0, 512-1, 15, 239-1)
		MDRV_VIDEO_START(hanamomo)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( msjiken )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(hanamomo)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_gionbana,writemem_gionbana)
		MDRV_CPU_PORTS(readport_gionbana,writeport_msjiken)
		MDRV_CPU_VBLANK_INT(nb1413m3_interrupt,142)	// nmiclock = 70
	MACHINE_DRIVER_END
	
	
	
	/* NBMJDRV3 */
	static MACHINE_DRIVER_START( telmahjn )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(gionbana)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_VBLANK_INT(nb1413m3_interrupt,142)	// nmiclock = 70
	
		/* video hardware */
		MDRV_VIDEO_START(hanamomo)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mgmen89 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(telmahjn)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_VBLANK_INT(nb1413m3_interrupt,128)
	MACHINE_DRIVER_END
	
	
	/* NBMJDRV4 */
	static MACHINE_DRIVER_START( mjfocus )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(gionbana)
		MDRV_CPU_REPLACE("main", Z80, 20000000/2)	/* 10.00 MHz ? crashes at 5 MHz */
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_VBLANK_INT(nb1413m3_interrupt,128)
	
		/* video hardware */
		MDRV_VIDEO_START(hanamomo)
	MACHINE_DRIVER_END
	
	
	/* NBMJDRV5 */
	static MACHINE_DRIVER_START( mjnanpas )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(gionbana)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_club90s,writemem_club90s)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( maiko )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(mjnanpas)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_maiko,writemem_maiko)
		MDRV_CPU_PORTS(readport_maiko,writeport_maiko)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( hanaoji )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(maiko)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_hanaoji,writemem_hanaoji)
	
		MDRV_NVRAM_HANDLER(nb1413m3)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( scandal )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(hanamomo)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_scandalm,writemem_scandalm)
		MDRV_CPU_PORTS(readport_gionbana,writeport_scandal)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( bananadr )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(mjnanpas)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_scandalm,writemem_scandalm)
		MDRV_CPU_PORTS(readport_gionbana,writeport_bananadr)
	
		MDRV_NVRAM_HANDLER(nb1413m3)
	MACHINE_DRIVER_END
	
	
	/* NBMJDRV6 */
	static MACHINE_DRIVER_START( mjfocusm )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(gionbana)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem_scandalm,writemem_scandalm)
		MDRV_CPU_PORTS(readport_scandalm,writeport_scandalm)
		MDRV_CPU_VBLANK_INT(nb1413m3_interrupt,128)
	
		MDRV_NVRAM_HANDLER(nb1413m3)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0, 512-1, 15, 239-1)
		MDRV_VIDEO_START(hanamomo)
	
		/* sound hardware */
		MDRV_SOUND_REPLACE("3812", AY8910, ay8910_interface)
	MACHINE_DRIVER_END
	
	
	
	
	static RomLoadPtr rom_gionbana = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "gion_03.bin", 0x00000, 0x10000, 0x615e993b );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "gion_02.bin", 0x00000, 0x10000, 0xc392eacc );
		ROM_LOAD( "gion_01.bin", 0x10000, 0x10000, 0xc253eff7 );
	
		ROM_REGION( 0x0c0000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "gion_04.bin", 0x000000, 0x10000, 0x0a1398d2 );
		ROM_LOAD( "gion_05.bin", 0x010000, 0x10000, 0x75b2c2e3 );
		ROM_LOAD( "gion_06.bin", 0x020000, 0x10000, 0xcb743f16 );
		ROM_LOAD( "gion_07.bin", 0x030000, 0x10000, 0x5574f6d2 );
		ROM_LOAD( "gion_08.bin", 0x040000, 0x10000, 0xb230ad99 );
		ROM_LOAD( "gion_09.bin", 0x050000, 0x10000, 0xcc7d54a8 );
		ROM_LOAD( "gion_10.bin", 0x060000, 0x10000, 0x22dd6d9f );
		ROM_LOAD( "gion_11.bin", 0x070000, 0x10000, 0xf0e81c0b );
		ROM_LOAD( "gion_12.bin", 0x080000, 0x10000, 0xd4e7d308 );
		ROM_LOAD( "gion_13.bin", 0x090000, 0x10000, 0xff38a134 );
		ROM_LOAD( "gion_14.bin", 0x0a0000, 0x10000, 0xa4e8b6a0 );
		ROM_LOAD( "gion_15.bin", 0x0b0000, 0x10000, 0xd36445e4 );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		ROM_LOAD( "gion_m1.bin", 0x00000, 0x40000, 0xf730ea47 );// same as housemnq/2i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hanamomo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "hmog_21.bin", 0x00000, 0x10000, 0x5b59d413 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "hmog_22.bin", 0x00000, 0x10000, 0xccc15b78 );
		ROM_LOAD( "hmog_23.bin", 0x10000, 0x10000, 0x3b166358 );
	
		ROM_REGION( 0x140000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "hmog_01.bin", 0x000000, 0x10000, 0x52e7bf1f );
		ROM_LOAD( "hmog_02.bin", 0x010000, 0x10000, 0xbfe11acc );
		ROM_LOAD( "hmog_03.bin", 0x020000, 0x10000, 0x3b28db4c );
		ROM_LOAD( "hmog_04.bin", 0x030000, 0x10000, 0xab0c088d );
		ROM_LOAD( "hmog_05.bin", 0x040000, 0x10000, 0xe42aa74b );
		ROM_LOAD( "hmog_06.bin", 0x050000, 0x10000, 0x8926bfee );
		ROM_LOAD( "hmog_07.bin", 0x060000, 0x10000, 0x2a85e88b );
		ROM_LOAD( "hmog_08.bin", 0x070000, 0x10000, 0xae0c59ab );
		ROM_LOAD( "hmog_09.bin", 0x080000, 0x10000, 0x15fc1179 );
		ROM_LOAD( "hmog_10.bin", 0x090000, 0x10000, 0xe289b7c3 );
		ROM_LOAD( "hmog_11.bin", 0x0a0000, 0x10000, 0x87eb1e10 );
		ROM_LOAD( "hmog_12.bin", 0x0b0000, 0x10000, 0xf1abaffb );
		ROM_LOAD( "hmog_13.bin", 0x0c0000, 0x10000, 0xfa38d953 );
		ROM_LOAD( "hmog_14.bin", 0x0d0000, 0x10000, 0x3f231850 );
		ROM_LOAD( "hmog_15.bin", 0x0e0000, 0x10000, 0x42baaf57 );
		ROM_LOAD( "hmog_16.bin", 0x0f0000, 0x10000, 0x1daf3342 );
		ROM_LOAD( "hmog_17.bin", 0x100000, 0x10000, 0xf1932dc1 );
		ROM_LOAD( "hmog_18.bin", 0x110000, 0x10000, 0x44062920 );
		ROM_LOAD( "hmog_19.bin", 0x120000, 0x10000, 0x81414383 );
		ROM_LOAD( "hmog_20.bin", 0x130000, 0x10000, 0xf3edc9d3 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_msjiken = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "msjn_11.bin",  0x00000, 0x10000, 0x723499ef );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "msjn_12.bin",  0x00000, 0x10000, 0x810e299e );
	
		ROM_REGION( 0x110000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "msjn_01.bin",  0x000000, 0x10000, 0x42dc6211 );
		ROM_LOAD( "msjn_02.bin",  0x010000, 0x10000, 0x3bc29b14 );
		ROM_LOAD( "msjn_03.bin",  0x020000, 0x10000, 0x442c838d );
		ROM_LOAD( "msjn_04.bin",  0x030000, 0x10000, 0x42aff870 );
		ROM_LOAD( "msjn_05.bin",  0x040000, 0x10000, 0x50735648 );
		ROM_LOAD( "msjn_06.bin",  0x050000, 0x10000, 0x76b72d64 );
		ROM_LOAD( "msjn_07.bin",  0x060000, 0x10000, 0xaabd0c75 );
		ROM_LOAD( "msjn_08.bin",  0x070000, 0x10000, 0xc87ef18a );
		ROM_LOAD( "msjn_10r.bin", 0x080000, 0x10000, 0x274700d2 );
		ROM_LOAD( "msjn_10.bin",  0x090000, 0x10000, 0x4c1deff9 );
		ROM_LOAD( "msjn_04r.bin", 0x0a0000, 0x10000, 0xcac5a5cf );
		ROM_LOAD( "msjn_05r.bin", 0x0b0000, 0x10000, 0xa2200fb2 );
		ROM_LOAD( "msjn_06r.bin", 0x0c0000, 0x10000, 0x528061b1 );
		ROM_LOAD( "msjn_07r.bin", 0x0d0000, 0x10000, 0xd2d2dae6 );
		ROM_LOAD( "msjn_08r.bin", 0x0e0000, 0x10000, 0xdec0e799 );
		ROM_LOAD( "msjn_09r.bin", 0x0f0000, 0x10000, 0x552167d9 );
		ROM_LOAD( "msjn_09.bin",  0x100000, 0x10000, 0xdf62249e );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_telmahjn = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "telm_03.bin", 0x00000, 0x10000, 0x851bff09 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "telm_02.bin", 0x00000, 0x10000, 0x5b278b68 );
		ROM_LOAD( "telm_01.bin", 0x10000, 0x10000, 0x06f00282 );
	
		ROM_REGION( 0x100000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "telm_04.bin", 0x000000, 0x10000, 0x54114564 );
		ROM_LOAD( "telm_05.bin", 0x010000, 0x10000, 0x369b2c83 );
		ROM_LOAD( "telm_06.bin", 0x020000, 0x10000, 0x790e8016 );
		ROM_LOAD( "telm_07.bin", 0x030000, 0x10000, 0x55ee68e8 );
		ROM_LOAD( "telm_08.bin", 0x040000, 0x10000, 0xf0928fb0 );
		ROM_LOAD( "telm_09.bin", 0x050000, 0x10000, 0xecc99d13 );
		ROM_LOAD( "telm_10.bin", 0x060000, 0x10000, 0x2036f1bd );
		ROM_LOAD( "telm_11.bin", 0x070000, 0x10000, 0x1cc59a34 );
		ROM_LOAD( "telm_12.bin", 0x080000, 0x10000, 0xea719867 );
		ROM_LOAD( "telm_13.bin", 0x090000, 0x10000, 0xe23049d2 );
		ROM_LOAD( "telm_14.bin", 0x0a0000, 0x10000, 0x61e773c0 );
		ROM_LOAD( "telm_15.bin", 0x0b0000, 0x10000, 0xc062cf30 );
		ROM_LOAD( "telm_16.bin", 0x0c0000, 0x10000, 0xceb37abd );
		ROM_LOAD( "telm_17.bin", 0x0d0000, 0x10000, 0x5e0cab0c );
		ROM_LOAD( "telm_18.bin", 0x0e0000, 0x10000, 0x8ca01f4e );
		ROM_LOAD( "telm_19.bin", 0x0f0000, 0x10000, 0x07362f98 );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		ROM_LOAD( "telm_m1.bin", 0x00000, 0x40000, 0x2199e3e9 );// same as housemnq/1i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mgmen89 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "mg89_03.bin", 0x00000, 0x10000, 0x1ac5cd84 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "mg89_02.bin", 0x00000, 0x10000, 0x1ca17bda );
		ROM_LOAD( "mg89_01.bin", 0x10000, 0x10000, 0x9a8c1ac5 );
	
		ROM_REGION( 0x0e0000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mg89_04.bin", 0x000000, 0x10000, 0x4c7d3afb );
		ROM_LOAD( "mg89_05.bin", 0x010000, 0x10000, 0xa0b9e4b7 );
		ROM_LOAD( "mg89_06.bin", 0x020000, 0x10000, 0x7adb3527 );
		ROM_LOAD( "mg89_07.bin", 0x030000, 0x10000, 0x22ea0472 );
		ROM_LOAD( "mg89_08.bin", 0x040000, 0x10000, 0x27343e42 );
		ROM_LOAD( "mg89_09.bin", 0x050000, 0x10000, 0x270addf1 );
		ROM_LOAD( "mg89_10.bin", 0x060000, 0x10000, 0x4a2e60ab );
		ROM_LOAD( "mg89_11.bin", 0x070000, 0x10000, 0x4e5d563a );
		ROM_LOAD( "mg89_12.bin", 0x080000, 0x10000, 0xfaf72b35 );
		ROM_LOAD( "mg89_13.bin", 0x090000, 0x10000, 0x68521b30 );
		ROM_LOAD( "mg89_14.bin", 0x0a0000, 0x10000, 0x3c70f85e );
		ROM_LOAD( "mg89_15.bin", 0x0b0000, 0x10000, 0x993e3b4d );
		ROM_LOAD( "mg89_16.bin", 0x0c0000, 0x10000, 0xb66c3b87 );
		ROM_LOAD( "mg89_17.bin", 0x0d0000, 0x10000, 0x3bd5c16b );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		ROM_LOAD( "mg89_m1.bin", 0x00000, 0x40000, 0x77ba1eaf );// same as housemnq/5i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjfocus = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "2_3h",   0x00000, 0x10000, 0xfd88b3e6 );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "1.2k",   0x00000, 0x10000, 0xe933d3c8 );
	
		ROM_REGION( 0x130000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3_8c",   0x000000, 0x10000, 0x4177d71f );
		ROM_LOAD( "4_8d",   0x010000, 0x10000, 0xaba5d761 );
		ROM_LOAD( "5_8e",   0x020000, 0x10000, 0x59c9680e );
		ROM_LOAD( "6_8f",   0x030000, 0x10000, 0x582cce83 );
		ROM_LOAD( "7_8h",   0x040000, 0x10000, 0xe83499c1 );
		ROM_LOAD( "8_8j",   0x050000, 0x10000, 0xcc583392 );
		ROM_LOAD( "9_8k",   0x060000, 0x10000, 0x9f84e9d2 );
		ROM_LOAD( "10_8l",  0x070000, 0x10000, 0xc57fa2a3 );
		ROM_LOAD( "11_8n",  0x080000, 0x10000, 0x4bd661b8 );
		ROM_LOAD( "12_8p",  0x090000, 0x10000, 0x7e4aaad1 );
		ROM_LOAD( "13_10c", 0x0a0000, 0x10000, 0x4e3b155d );
		ROM_LOAD( "14_10d", 0x0b0000, 0x10000, 0x703431d1 );
		ROM_LOAD( "15_10e", 0x0c0000, 0x10000, 0x9d97e0f9 );
		ROM_LOAD( "16_10f", 0x0d0000, 0x10000, 0x1d31fcb5 );
		ROM_LOAD( "17_10h", 0x0e0000, 0x10000, 0xc0775836 );
		ROM_LOAD( "18_10j", 0x0f0000, 0x10000, 0x31ff6ef1 );
		ROM_LOAD( "19_10k", 0x100000, 0x10000, 0x86d39bb4 );
		ROM_LOAD( "20_10l", 0x110000, 0x10000, 0x53f33c46 );
		ROM_LOAD( "21_10n", 0x120000, 0x10000, 0x68c5b271 );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		/* this ROM was not dumped, but the program expects the same checksum
		   as peepshow, so it's a safe assumption that it's the same. */
		ROM_LOAD( "mask",   0x00000, 0x40000, 0x2199e3e9 );// same as housemnq/1i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjfocusm = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "mfcs_02m.bin", 0x00000, 0x10000, 0x409d4f0b );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "1.2k",   0x00000, 0x10000, 0xe933d3c8 );
	
		ROM_REGION( 0x110000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.8c",         0x000000, 0x10000, 0x4c8d6ca9 );
		ROM_LOAD( "mfcs_04m.bin", 0x010000, 0x10000, 0xe73d7804 );
		ROM_LOAD( "5.8e",         0x020000, 0x10000, 0xf4d7e344 );
		ROM_LOAD( "mfcs_06m.bin", 0x030000, 0x10000, 0xe4d638f6 );
		ROM_LOAD( "mfcs_07m.bin", 0x040000, 0x10000, 0x45be433a );
		ROM_LOAD( "mfcs_08m.bin", 0x050000, 0x10000, 0xa7e1d761 );
		ROM_LOAD( "mfcs_09m.bin", 0x060000, 0x10000, 0x21cbe481 );
		ROM_LOAD( "mfcs_10m.bin", 0x070000, 0x10000, 0x5430d20a );
		ROM_LOAD( "11.8n",        0x080000, 0x10000, 0xc9bdf0a8 );
		ROM_LOAD( "12.8p",        0x090000, 0x10000, 0x777cbe0e );
		ROM_LOAD( "mfcs_13m.bin", 0x0a0000, 0x10000, 0x6bdb28c2 );
		ROM_LOAD( "14.10d",       0x0b0000, 0x10000, 0xc86da643 );
		ROM_LOAD( "15.10e",       0x0c0000, 0x10000, 0xcdf4c1e9 );
		ROM_LOAD( "16.10f",       0x0d0000, 0x10000, 0x65ac5a6d );
		ROM_LOAD( "17.10h",       0x0e0000, 0x10000, 0x383ece66 );
		ROM_LOAD( "18.10j",       0x0f0000, 0x10000, 0xb2cc3586 );
		ROM_LOAD( "mfcs_19m.bin", 0x100000, 0x10000, 0x45c08364 );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		ROM_LOAD( "mfcs_m1m.bin", 0x00000, 0x40000, 0xda46163e );// same as orangec/ic2.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_peepshow = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "2.3h",   0x00000, 0x10000, 0x8db1746c );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "1.2k",   0x00000, 0x10000, 0xe933d3c8 );
	
		ROM_REGION( 0x110000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.8c",   0x000000, 0x10000, 0x4c8d6ca9 );
		ROM_LOAD( "4.8d",   0x010000, 0x10000, 0x9e80455f );
		ROM_LOAD( "5.8e",   0x020000, 0x10000, 0xf4d7e344 );
		ROM_LOAD( "6.8f",   0x030000, 0x10000, 0x91dcf9a5 );
		ROM_LOAD( "7.8h",   0x040000, 0x10000, 0xdbc58b78 );
		ROM_LOAD( "8.8j",   0x050000, 0x10000, 0x0ee9d5cb );
		ROM_LOAD( "9.8k",   0x060000, 0x10000, 0xbc00bb95 );
		ROM_LOAD( "10.8l",  0x070000, 0x10000, 0x77e62065 );
		ROM_LOAD( "11.8n",  0x080000, 0x10000, 0xc9bdf0a8 );
		ROM_LOAD( "12.8p",  0x090000, 0x10000, 0x777cbe0e );
		ROM_LOAD( "13.10c", 0x0a0000, 0x10000, 0x97a9ad73 );
		ROM_LOAD( "14.10d", 0x0b0000, 0x10000, 0xc86da643 );
		ROM_LOAD( "15.10e", 0x0c0000, 0x10000, 0xcdf4c1e9 );
		ROM_LOAD( "16.10f", 0x0d0000, 0x10000, 0x65ac5a6d );
		ROM_LOAD( "17.10h", 0x0e0000, 0x10000, 0x383ece66 );
		ROM_LOAD( "18.10j", 0x0f0000, 0x10000, 0xb2cc3586 );
		ROM_LOAD( "19.10k", 0x100000, 0x10000, 0xb6b40e4d );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		/* this ROM was not dumped correctly - FIXED BITS (xxxxxxxx11111111).
		   However, what's in there matches the telmahjn one, and the program expects
		   the same checksum, so it's a safe assumption that it's the same. */
		ROM_LOAD( "mask",   0x00000, 0x40000, 0x2199e3e9 );// same as housemnq/1i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_scandal = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "1.3h",   0x00000, 0x10000, 0x97e73a9c );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "2.3j",   0x00000, 0x10000, 0x9a5f7907 );
	
		ROM_REGION( 0x0d0000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "15.11p", 0x000000, 0x10000, 0x4677f0d0 );
		ROM_LOAD( "14.11n", 0x010000, 0x10000, 0xf935a681 );
		ROM_LOAD( "13.11m", 0x020000, 0x10000, 0x80c5109e );
		ROM_LOAD( "12.11k", 0x030000, 0x10000, 0x2a408850 );
		ROM_LOAD( "11.11j", 0x040000, 0x10000, 0x34f525af );
		ROM_LOAD( "10.11f", 0x050000, 0x10000, 0x12a30207 );
		ROM_LOAD( "9.11e",  0x060000, 0x10000, 0x04918709 );
		ROM_LOAD( "8.11d",  0x070000, 0x10000, 0x5d87d1b7 );
		ROM_LOAD( "7.11c",  0x080000, 0x10000, 0xd8f3dcbb );
		ROM_LOAD( "6.11a",  0x090000, 0x10000, 0x6ea1e009 );
		ROM_LOAD( "5.10p",  0x0a0000, 0x10000, 0x60472080 );
		ROM_LOAD( "4.10n",  0x0b0000, 0x10000, 0xd9267e88 );
		ROM_LOAD( "3.10m",  0x0c0000, 0x10000, 0x9e303eda );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_scandalm = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "scmm_01.bin", 0x00000, 0x10000, 0x9811bab6 );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "2.3j",   0x00000, 0x10000, 0x9a5f7907 );
	
		ROM_REGION( 0x0d0000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "15.11p", 0x000000, 0x10000, 0x4677f0d0 );
		ROM_LOAD( "14.11n", 0x010000, 0x10000, 0xf935a681 );
		ROM_LOAD( "13.11m", 0x020000, 0x10000, 0x80c5109e );
		ROM_LOAD( "12.11k", 0x030000, 0x10000, 0x2a408850 );
		ROM_LOAD( "11.11j", 0x040000, 0x10000, 0x34f525af );
		ROM_LOAD( "10.11f", 0x050000, 0x10000, 0x12a30207 );
		ROM_LOAD( "9.11e",  0x060000, 0x10000, 0x04918709 );
		ROM_LOAD( "8.11d",  0x070000, 0x10000, 0x5d87d1b7 );
		ROM_LOAD( "7.11c",  0x080000, 0x10000, 0xd8f3dcbb );
		ROM_LOAD( "6.11a",  0x090000, 0x10000, 0x6ea1e009 );
		ROM_LOAD( "5.10p",  0x0a0000, 0x10000, 0x60472080 );
		ROM_LOAD( "4.10n",  0x0b0000, 0x10000, 0xd9267e88 );
		ROM_LOAD( "3.10m",  0x0c0000, 0x10000, 0x9e303eda );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjnanpas = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "mnst_03.bin", 0x00000, 0x10000, 0xece14e07 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "mnst_02.bin", 0x00000, 0x10000, 0x22c7ddce );
		ROM_LOAD( "mnst_01.bin", 0x10000, 0x10000, 0x13b79c41 );
	
		ROM_REGION( 0x140000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mnst_04.bin", 0x000000, 0x10000, 0x7b8fb5f2 );
		ROM_LOAD( "mnst_05.bin", 0x010000, 0x10000, 0x6e48b612 );
		ROM_LOAD( "mnst_06.bin", 0x020000, 0x10000, 0x1ea7db2e );
		ROM_LOAD( "mnst_07.bin", 0x030000, 0x10000, 0x2930acbb );
		ROM_LOAD( "mnst_08.bin", 0x040000, 0x10000, 0xcd632b5c );
		ROM_LOAD( "mnst_09.bin", 0x050000, 0x10000, 0x77116d9e );
		ROM_LOAD( "mnst_10.bin", 0x060000, 0x10000, 0x5502e478 );
		ROM_LOAD( "mnst_11.bin", 0x070000, 0x10000, 0x3f739fb1 );
		ROM_LOAD( "mnst_12.bin", 0x080000, 0x10000, 0x2741f576 );
		ROM_LOAD( "mnst_13.bin", 0x090000, 0x10000, 0x10132020 );
		ROM_LOAD( "mnst_14.bin", 0x0a0000, 0x10000, 0x03b32fa7 );
		ROM_LOAD( "mnst_15.bin", 0x0b0000, 0x10000, 0x4bb85dd7 );
		ROM_LOAD( "mnst_16.bin", 0x0c0000, 0x10000, 0x38de91de );
		ROM_LOAD( "mnst_17.bin", 0x0d0000, 0x10000, 0x23cac7e3 );
		ROM_LOAD( "mnst_18.bin", 0x0e0000, 0x10000, 0xaf62af24 );
		ROM_LOAD( "mnst_19.bin", 0x0f0000, 0x10000, 0xe18dc023 );
		ROM_LOAD( "mnst_20.bin", 0x100000, 0x10000, 0xca706644 );
		ROM_LOAD( "mnst_21.bin", 0x110000, 0x10000, 0x0a609495 );
		ROM_LOAD( "mnst_22.bin", 0x120000, 0x10000, 0x3468f36f );
		ROM_LOAD( "mnst_23.bin", 0x130000, 0x10000, 0x8d1a64a6 );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		/* the protection data is not used at all! They forgot a debug flag set in the
		   code which skips the protection check. */
		ROM_LOAD( "mnst_m1.bin", 0x00000, 0x40000, 0x77ba1eaf );// same as housemnq/5i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjnanpaa = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "mnst_03.old", 0x00000, 0x10000, 0xa105b2b8 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "mnst_02.bin", 0x00000, 0x10000, 0x22c7ddce );
		ROM_LOAD( "mnst_01.bin", 0x10000, 0x10000, 0x13b79c41 );
	
		ROM_REGION( 0x140000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mnst_04.bin", 0x000000, 0x10000, 0x7b8fb5f2 );
		ROM_LOAD( "mnst_05.bin", 0x010000, 0x10000, 0x6e48b612 );
		ROM_LOAD( "mnst_06.bin", 0x020000, 0x10000, 0x1ea7db2e );
		ROM_LOAD( "mnst_07.bin", 0x030000, 0x10000, 0x2930acbb );
		ROM_LOAD( "mnst_08.bin", 0x040000, 0x10000, 0xcd632b5c );
		ROM_LOAD( "mnst_09.bin", 0x050000, 0x10000, 0x77116d9e );
		ROM_LOAD( "mnst_10.bin", 0x060000, 0x10000, 0x5502e478 );
		ROM_LOAD( "mnst_11.bin", 0x070000, 0x10000, 0x3f739fb1 );
		ROM_LOAD( "mnst_12.bin", 0x080000, 0x10000, 0x2741f576 );
		ROM_LOAD( "mnst_13.bin", 0x090000, 0x10000, 0x10132020 );
		ROM_LOAD( "mnst_14.bin", 0x0a0000, 0x10000, 0x03b32fa7 );
		ROM_LOAD( "mnst_15.bin", 0x0b0000, 0x10000, 0x4bb85dd7 );
		ROM_LOAD( "mnst_16.bin", 0x0c0000, 0x10000, 0x38de91de );
		ROM_LOAD( "mnst_17.bin", 0x0d0000, 0x10000, 0x23cac7e3 );
		ROM_LOAD( "mnst_18.bin", 0x0e0000, 0x10000, 0xaf62af24 );
		ROM_LOAD( "mnst_19.bin", 0x0f0000, 0x10000, 0xe18dc023 );
		ROM_LOAD( "mnst_20.bin", 0x100000, 0x10000, 0xca706644 );
		ROM_LOAD( "mnst_21.bin", 0x110000, 0x10000, 0x0a609495 );
		ROM_LOAD( "mnst_22.bin", 0x120000, 0x10000, 0x3468f36f );
		ROM_LOAD( "mnst_23.bin", 0x130000, 0x10000, 0x8d1a64a6 );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		/* the protection data is not used at all! They forgot a debug flag set in the
		   code which skips the protection check. */
		ROM_LOAD( "mnst_m1.bin", 0x00000, 0x40000, 0x77ba1eaf );// same as housemnq/5i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjnanpau = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "03.bin",      0x00000, 0x10000, 0xf96bdda7 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "mnst_02.bin", 0x00000, 0x10000, 0x22c7ddce );
		ROM_LOAD( "mnst_01.bin", 0x10000, 0x10000, 0x13b79c41 );
	
		ROM_REGION( 0x140000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mnst_04.bin", 0x000000, 0x10000, 0x7b8fb5f2 );
		ROM_LOAD( "05.bin",      0x010000, 0x10000, 0x98219498 );
		ROM_LOAD( "mnst_06.bin", 0x020000, 0x10000, 0x1ea7db2e );
		ROM_LOAD( "mnst_07.bin", 0x030000, 0x10000, 0x2930acbb );
		ROM_LOAD( "mnst_08.bin", 0x040000, 0x10000, 0xcd632b5c );
		ROM_LOAD( "mnst_09.bin", 0x050000, 0x10000, 0x77116d9e );
		ROM_LOAD( "mnst_10.bin", 0x060000, 0x10000, 0x5502e478 );
		ROM_LOAD( "11.bin",      0x070000, 0x10000, 0xc4808c77 );
		ROM_LOAD( "12.bin",      0x080000, 0x10000, 0xf7be103c );
		ROM_LOAD( "13.bin",      0x090000, 0x10000, 0x7eb39bb1 );
		ROM_LOAD( "mnst_14.bin", 0x0a0000, 0x10000, 0x03b32fa7 );
		ROM_LOAD( "15.bin",      0x0b0000, 0x10000, 0x19acab3a );
		ROM_LOAD( "16.bin",      0x0c0000, 0x10000, 0x51e3d3e1 );
		ROM_LOAD( "mnst_17.bin", 0x0d0000, 0x10000, 0x23cac7e3 );
		ROM_LOAD( "18.bin",      0x0e0000, 0x10000, 0x754834f8 );
		ROM_LOAD( "19.bin",      0x0f0000, 0x10000, 0xd72d9d75 );
		ROM_LOAD( "20.bin",      0x100000, 0x10000, 0xa87061c3 );
		ROM_LOAD( "21.bin",      0x110000, 0x10000, 0x14c5be81 );
		ROM_LOAD( "mnst_22.bin", 0x120000, 0x10000, 0x3468f36f );
		ROM_LOAD( "23.bin",      0x130000, 0x10000, 0xdef886e1 );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		/* the protection data is not used at all! They forgot a debug flag set in the
		   code which skips the protection check. */
		ROM_LOAD( "mnst_m1.bin", 0x00000, 0x40000, 0x77ba1eaf );// same as housemnq/5i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_pairsten = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "3.j3", 0x00000, 0x10000, 0x037d6acb );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );
		ROM_LOAD( "2.k2", 0x00000, 0x10000, 0xf44ec73a );
		ROM_LOAD( "1.j2", 0x10000, 0x10000, 0x5ca5bd18 );
	
		ROM_REGION( 0xf0000, REGION_GFX1, 0 );
		ROM_LOAD( "4.c8",   0x00000, 0x10000, 0xdd13e9ec );
		ROM_LOAD( "5.d8",   0x10000, 0x10000, 0x42b55fa6 );
		ROM_LOAD( "6.e8",   0x20000, 0x10000, 0x5f901bf2 );
		ROM_LOAD( "7.f8",   0x30000, 0x10000, 0x3c00e87d );
		ROM_LOAD( "8.h8",   0x40000, 0x10000, 0x1b5b3ed2 );
		ROM_LOAD( "9.k8",   0x50000, 0x10000, 0x117175d1 );
		ROM_LOAD( "10.l8",  0x60000, 0x10000, 0x52228349 );
		ROM_LOAD( "11.m8",  0x70000, 0x10000, 0x4e9606ff );
		ROM_LOAD( "12.n8",  0x80000, 0x10000, 0x6c39e2e0 );
		ROM_LOAD( "13.p8",  0x90000, 0x10000, 0xf033769f );
		ROM_LOAD( "14.c10", 0xa0000, 0x10000, 0xdf263b08 );
		ROM_LOAD( "15.d10", 0xb0000, 0x10000, 0x684842cb );
		ROM_LOAD( "16.e10", 0xc0000, 0x10000, 0xe7c1284e );
		ROM_LOAD( "17.f10", 0xd0000, 0x10000, 0xc8d79e7f );
		ROM_LOAD( "18.h10", 0xe0000, 0x10000, 0xe3138cbc );
	
		ROM_REGION( 0x40000, REGION_USER1, 0 );/* protection data */
		ROM_LOAD( "mask.f2", 0x00000, 0x40000, 0x77ba1eaf );// same as housemnq/5i.bin gfx data
	ROM_END(); }}; 
	
	static RomLoadPtr rom_bananadr = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "1.4h",   0x00000, 0x10000, 0xa6344e0d );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "2.4j",   0x00000, 0x20000, 0xd6f24371 );
	
		ROM_REGION( 0x140000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.11p",  0x000000, 0x10000, 0xbcb94d00 );
		ROM_LOAD( "4.11n",  0x010000, 0x10000, 0x90642607 );
		ROM_LOAD( "5.11m",  0x020000, 0x10000, 0x1ea7db2e );
		ROM_LOAD( "6.11k",  0x030000, 0x10000, 0x2930acbb );
		ROM_LOAD( "7.11j",  0x040000, 0x10000, 0xcd632b5c );
		ROM_LOAD( "8.11h",  0x050000, 0x10000, 0x77116d9e );
		ROM_LOAD( "9.11e",  0x060000, 0x10000, 0x5502e478 );
		ROM_LOAD( "10.11d", 0x070000, 0x10000, 0xc4808c77 );
		ROM_LOAD( "11.11c", 0x080000, 0x10000, 0xf7be103c );
		ROM_LOAD( "12.11a", 0x090000, 0x10000, 0x7eb39bb1 );
		ROM_LOAD( "13.10p", 0x0a0000, 0x10000, 0x03b32fa7 );
		ROM_LOAD( "14.10n", 0x0b0000, 0x10000, 0x19acab3a );
		ROM_LOAD( "15.10m", 0x0c0000, 0x10000, 0x51e3d3e1 );
		ROM_LOAD( "16.10k", 0x0d0000, 0x10000, 0x23cac7e3 );
		ROM_LOAD( "17.10j", 0x0e0000, 0x10000, 0x754834f8 );
		ROM_LOAD( "18.10h", 0x0f0000, 0x10000, 0xd72d9d75 );
		ROM_LOAD( "19.10e", 0x100000, 0x10000, 0xe8155a37 );
		ROM_LOAD( "20.10d", 0x110000, 0x10000, 0x3e44d46a );
		ROM_LOAD( "21.10c", 0x120000, 0x10000, 0x320c0d74 );
		ROM_LOAD( "22.10a", 0x130000, 0x10000, 0xdef886e1 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_club90s = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "c90s_03.bin", 0x00000, 0x10000, 0xf8148ba5 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "c90s_02.bin", 0x00000, 0x10000, 0xb7938ed8 );
		ROM_LOAD( "c90s_01.bin", 0x10000, 0x10000, 0xbaaf17bd );
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "c90s_04.bin", 0x080000, 0x20000, 0x2c7d74ef );
		ROM_LOAD( "c90s_05.bin", 0x0a0000, 0x20000, 0x98d1f969 );
		ROM_LOAD( "c90s_06.bin", 0x0c0000, 0x20000, 0x509c1499 );
		ROM_LOAD( "c90s_07.bin", 0x0e0000, 0x20000, 0x8a8e2301 );
		ROM_LOAD( "c90s_08.bin", 0x100000, 0x20000, 0x60fb6006 );
		ROM_LOAD( "c90s_09.bin", 0x120000, 0x20000, 0x2fb74265 );
		ROM_LOAD( "c90s_10.bin", 0x140000, 0x20000, 0xca858e2c );
		ROM_LOAD( "c90s_11.bin", 0x160000, 0x20000, 0x56ca8768 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_club90sa = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "c90s_23.bin", 0x00000, 0x10000, 0x60433c11 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "c90s_02.bin", 0x00000, 0x10000, 0xb7938ed8 );
		ROM_LOAD( "c90s_01.bin", 0x10000, 0x10000, 0xbaaf17bd );
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "c90s_04.bin", 0x080000, 0x20000, 0x2c7d74ef );
		ROM_LOAD( "c90s_05.bin", 0x0a0000, 0x20000, 0x98d1f969 );
		ROM_LOAD( "c90s_06.bin", 0x0c0000, 0x20000, 0x509c1499 );
		ROM_LOAD( "c90s_07.bin", 0x0e0000, 0x20000, 0x8a8e2301 );
		ROM_LOAD( "c90s_08.bin", 0x100000, 0x20000, 0x60fb6006 );
		ROM_LOAD( "c90s_09.bin", 0x120000, 0x20000, 0x2fb74265 );
		ROM_LOAD( "c90s_10.bin", 0x140000, 0x20000, 0xca858e2c );
		ROM_LOAD( "c90s_11.bin", 0x160000, 0x20000, 0x56ca8768 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mladyhtr = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "mlht_03.bin", 0x00000, 0x10000, 0xbda76c24 );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "mlht_02.bin", 0x00000, 0x10000, 0xe841696d );
		ROM_LOAD( "mlht_01.bin", 0x10000, 0x10000, 0x75c35c62 );
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mj-1802.bin", 0x000000, 0x80000, 0xe6213f10 );
		ROM_LOAD( "mlht_04.bin", 0x080000, 0x20000, 0x5896f484 );
		ROM_LOAD( "mlht_05.bin", 0x0a0000, 0x20000, 0xbc26f689 );
		ROM_LOAD( "mlht_06.bin", 0x0c0000, 0x20000, 0xc24a9d5e );
		ROM_LOAD( "mlht_07.bin", 0x0e0000, 0x10000, 0x68c55f45 );
		ROM_LOAD( "mlht_08.bin", 0x0f0000, 0x10000, 0x110afc31 );
		ROM_LOAD( "mlht_09.bin", 0x100000, 0x10000, 0x01739671 );
		ROM_LOAD( "mlht_10.bin", 0x110000, 0x10000, 0xf0663672 );
		ROM_LOAD( "mlht_11.bin", 0x120000, 0x10000, 0xb8485904 );
		ROM_LOAD( "mlht_12.bin", 0x130000, 0x10000, 0xd58ac691 );
		ROM_LOAD( "mlht_13.bin", 0x140000, 0x10000, 0xa066e193 );
		ROM_LOAD( "mlht_14.bin", 0x150000, 0x10000, 0xb956b9e2 );
		ROM_LOAD( "mlht_15.bin", 0x160000, 0x10000, 0xaf80f2a1 );
		ROM_LOAD( "mlht_16.bin", 0x170000, 0x10000, 0x0775bbda );
		ROM_LOAD( "mlht_17.bin", 0x180000, 0x10000, 0xb25d515b );
		ROM_LOAD( "mlht_18.bin", 0x190000, 0x10000, 0x30c30b07 );
		ROM_LOAD( "mlht_19.bin", 0x1a0000, 0x10000, 0x5056763d );
		ROM_LOAD( "mlht_20.bin", 0x1b0000, 0x10000, 0xa58edec9 );
		ROM_LOAD( "mlht_21.bin", 0x1c0000, 0x10000, 0xc7769608 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_chinmoku = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "3.3h",   0x00000, 0x10000, 0xeddff33e );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "2.2k",   0x00000, 0x10000, 0x0d6306e3 );
		ROM_LOAD( "1.2h",   0x10000, 0x10000, 0xa85e681c );
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "4.8d",   0x020000, 0x20000, 0x5b5234f6 );
		ROM_LOAD( "5.8e",   0x040000, 0x20000, 0x56bf9a23 );
		ROM_LOAD( "6.8f",   0x060000, 0x20000, 0x188bdbd6 );
		ROM_LOAD( "7.8h",   0x080000, 0x20000, 0xeecb02e2 );
		ROM_LOAD( "8.8k",   0x0a0000, 0x20000, 0xb3953fb2 );
		ROM_LOAD( "9.8l",   0x0c0000, 0x20000, 0xc1432f82 );
		ROM_LOAD( "10.8m",  0x0e0000, 0x20000, 0x9ec1f110 );
		ROM_LOAD( "11.8n",  0x100000, 0x20000, 0xa5031090 );
		ROM_LOAD( "12.8p",  0x120000, 0x20000, 0x900369a7 );
		ROM_LOAD( "13.10c", 0x140000, 0x10000, 0xb38dd44d );
		ROM_LOAD( "14.10d", 0x150000, 0x10000, 0xe4a37c9a );
		ROM_LOAD( "15.10e", 0x160000, 0x10000, 0xab443c6d );
		ROM_LOAD( "16.10f", 0x170000, 0x10000, 0x30c11267 );
		ROM_LOAD( "17.10h", 0x180000, 0x10000, 0xd0a17fcc );
		ROM_LOAD( "18.10k", 0x190000, 0x10000, 0x8445fce2 );
		ROM_LOAD( "19.10l", 0x1a0000, 0x10000, 0x65b90ea1 );
		ROM_LOAD( "20.10m", 0x1b0000, 0x10000, 0x1445d8b0 );
		ROM_LOAD( "21.10n", 0x1c0000, 0x10000, 0x38620a45 );
		ROM_LOAD( "22.10p", 0x1d0000, 0x10000, 0x85119fce );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_maiko = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "mikb_02.bin", 0x00000, 0x10000, 0xfbf68ebd );
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "mikb_01.bin", 0x00000, 0x20000, 0x713b3f8f );
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "mikb_03.bin", 0x000000, 0x20000, 0x0c949a6f );
		ROM_LOAD( "mikb_04.bin", 0x020000, 0x20000, 0x8c841482 );
		ROM_LOAD( "mikb_05.bin", 0x040000, 0x20000, 0x7c61b4f7 );
		ROM_LOAD( "mikb_06.bin", 0x060000, 0x20000, 0x7cc39a22 );
		ROM_LOAD( "mikb_07.bin", 0x080000, 0x20000, 0x0aaf5033 );
		ROM_LOAD( "mikb_08.bin", 0x0a0000, 0x20000, 0x2628caa1 );
		ROM_LOAD( "mj-1802.bin", 0x180000, 0x80000, 0xe6213f10 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hanaoji = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "hnoj_02.bin", 0x00000, 0x10000, 0x580cd095 );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "hnoj_01.bin", 0x00000, 0x10000, 0x3f7fcb94 );
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "hnoj_03.bin", 0x000000, 0x20000, 0xfbbe1dce );
		ROM_LOAD( "hnoj_04.bin", 0x020000, 0x20000, 0x2074b04f );
		ROM_LOAD( "hnoj_05.bin", 0x040000, 0x20000, 0x84d20ba6 );
		ROM_LOAD( "hnoj_06.bin", 0x060000, 0x20000, 0xf85fedd8 );
		ROM_LOAD( "hnoj_07.bin", 0x080000, 0x20000, 0xc72cdde1 );
		ROM_LOAD( "hnoj_08.bin", 0x0a0000, 0x20000, 0x12e70429 );
		ROM_LOAD( "hnoj_09.bin", 0x0c0000, 0x20000, 0x4ec74a59 );
		ROM_LOAD( "hnoj_10.bin", 0x0e0000, 0x20000, 0xe9212fc5 );
		ROM_LOAD( "hnoj_11.bin", 0x100000, 0x20000, 0xbfe38671 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mmcamera = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* program */
		ROM_LOAD( "2.3h",        0x00000, 0x10000, 0xb6eed2cf );
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 );/* voice */
		ROM_LOAD( "1.2k",        0x00000, 0x10000, 0xfe8e975e );
	
		ROM_REGION( 0x110000, REGION_GFX1, 0 );/* gfx */
		ROM_LOAD( "3.8c",        0x000000, 0x10000, 0x273fb8bc );
		ROM_LOAD( "4.8d",        0x010000, 0x10000, 0x82995399 );
		ROM_LOAD( "5.8e",        0x020000, 0x10000, 0xa7c51d54 );
		ROM_LOAD( "6.8f",        0x030000, 0x10000, 0xf221700c );
		ROM_LOAD( "7.8h",        0x040000, 0x10000, 0x6baa4d45 );
		ROM_LOAD( "8.8k",        0x050000, 0x10000, 0x91d9c868 );
		ROM_LOAD( "9.8l",        0x060000, 0x10000, 0x56a35d4b );
		ROM_LOAD( "10.8m",       0x070000, 0x10000, 0x480e23c4 );
		ROM_LOAD( "11.8n",       0x080000, 0x10000, 0x2c29accc );
		ROM_LOAD( "12.8p",       0x090000, 0x10000, 0x902d73f8 );
		ROM_LOAD( "13.10c",      0x0a0000, 0x10000, 0xfcba0179 );
		ROM_LOAD( "14.10d",      0x0b0000, 0x10000, 0xee2c37a9 );
		ROM_LOAD( "15.10e",      0x0c0000, 0x10000, 0x90fd36f8 );
		ROM_LOAD( "16.10f",      0x0d0000, 0x10000, 0x41265f7f );
		ROM_LOAD( "17.10h",      0x0e0000, 0x10000, 0x78cef468 );
		ROM_LOAD( "18.10k",      0x0f0000, 0x10000, 0x59182700 );
		ROM_LOAD( "9.10l",       0x100000, 0x10000, 0x0429ae8f );
	ROM_END(); }}; 
	
	
	public static GameDriver driver_msjiken	   = new GameDriver("1988"	,"msjiken"	,"nbmj8891.java"	,rom_msjiken,null	,machine_driver_msjiken	,input_ports_msjiken	,init_msjiken	,ROT270	,	"Nichibutsu", "Mahjong Satsujin Jiken (Japan 881017)" )
	public static GameDriver driver_hanamomo	   = new GameDriver("1988"	,"hanamomo"	,"nbmj8891.java"	,rom_hanamomo,null	,machine_driver_hanamomo	,input_ports_hanamomo	,init_hanamomo	,ROT0	,	"Nichibutsu", "Mahjong Hana no Momoko gumi (Japan 881201)" )
	public static GameDriver driver_telmahjn	   = new GameDriver("1988"	,"telmahjn"	,"nbmj8891.java"	,rom_telmahjn,null	,machine_driver_telmahjn	,input_ports_telmahjn	,init_telmahjn	,ROT270	,	"Nichibutsu", "Telephone Mahjong (Japan 890111)" )
	public static GameDriver driver_gionbana	   = new GameDriver("1989"	,"gionbana"	,"nbmj8891.java"	,rom_gionbana,null	,machine_driver_gionbana	,input_ports_gionbana	,init_gionbana	,ROT0	,	"Nichibutsu", "Gionbana (Japan 890120)" )
	public static GameDriver driver_mjfocus	   = new GameDriver("1989"	,"mjfocus"	,"nbmj8891.java"	,rom_mjfocus,null	,machine_driver_mjfocus	,input_ports_mjfocus	,init_mjfocus	,ROT0	,	"Nichibutsu", "Mahjong Focus (Japan 890313)" )
	public static GameDriver driver_mjfocusm	   = new GameDriver("1989"	,"mjfocusm"	,"nbmj8891.java"	,rom_mjfocusm,driver_mjfocus	,machine_driver_mjfocusm	,input_ports_mjfocusm	,init_mjfocusm	,ROT0	,	"Nichibutsu", "Mahjong Focus [BET] (Japan 890510)" )
	public static GameDriver driver_mmcamera	   = new GameDriver("1989"	,"mmcamera"	,"nbmj8891.java"	,rom_mmcamera,driver_mjcamera	,machine_driver_mmcamera	,input_ports_mmcamera	,init_mmcamera	,ROT0	,	"MIKI SYOUJI", "(Medal) Mahjong Camera Kozou [BET] (Japan 890509)" )
	public static GameDriver driver_peepshow	   = new GameDriver("1989"	,"peepshow"	,"nbmj8891.java"	,rom_peepshow,driver_mjfocus	,machine_driver_mjfocus	,input_ports_peepshow	,init_mjfocus	,ROT0	,	"AC", "Nozokimeguri Mahjong Peep Show (Japan 890404)" )
	public static GameDriver driver_scandal	   = new GameDriver("1989"	,"scandal"	,"nbmj8891.java"	,rom_scandal,null	,machine_driver_scandal	,input_ports_scandal	,init_scandal	,ROT0	,	"Nichibutsu", "Scandal Mahjong (Japan 890213)" )
	public static GameDriver driver_scandalm	   = new GameDriver("1989"	,"scandalm"	,"nbmj8891.java"	,rom_scandalm,driver_scandal	,machine_driver_mjfocusm	,input_ports_scandalm	,init_scandalm	,ROT0	,	"Nichibutsu", "Scandal Mahjong [BET] (Japan 890217)" )
	public static GameDriver driver_mgmen89	   = new GameDriver("1989"	,"mgmen89"	,"nbmj8891.java"	,rom_mgmen89,null	,machine_driver_mgmen89	,input_ports_mgmen89	,init_mgmen89	,ROT0	,	"Nichibutsu", "Mahjong G-MEN'89 (Japan 890425)" )
	public static GameDriver driver_mjnanpas	   = new GameDriver("1989"	,"mjnanpas"	,"nbmj8891.java"	,rom_mjnanpas,null	,machine_driver_mjnanpas	,input_ports_mjnanpas	,init_mjnanpas	,ROT0	,	"BROOKS", "Mahjong Nanpa Story (Japan 890713)" )
	public static GameDriver driver_mjnanpaa	   = new GameDriver("1989"	,"mjnanpaa"	,"nbmj8891.java"	,rom_mjnanpaa,driver_mjnanpas	,machine_driver_mjnanpas	,input_ports_mjnanpaa	,init_mjnanpas	,ROT0	,	"BROOKS", "Mahjong Nanpa Story (Japan 890712)" )
	public static GameDriver driver_mjnanpau	   = new GameDriver("1989"	,"mjnanpau"	,"nbmj8891.java"	,rom_mjnanpau,driver_mjnanpas	,machine_driver_mjnanpas	,input_ports_mjnanpas	,init_mjnanpas	,ROT0	,	"BROOKS", "Mahjong Nanpa Story (Ura) (Japan 890805)" )
	public static GameDriver driver_pairsten	   = new GameDriver("1989"	,"pairsten"	,"nbmj8891.java"	,rom_pairsten,null	,machine_driver_gionbana	,input_ports_pairsten	,init_pairsten	,ROT0	,	"System Ten", "Pairs (System Ten) (Japan 890826)", GAME_IMPERFECT_SOUND )
	public static GameDriver driver_bananadr	   = new GameDriver("1989"	,"bananadr"	,"nbmj8891.java"	,rom_bananadr,null	,machine_driver_bananadr	,input_ports_bananadr	,init_bananadr	,ROT0	,	"DIGITAL SOFT", "Mahjong Banana Dream [BET] (Japan 891124)" )
	public static GameDriver driver_mladyhtr	   = new GameDriver("1990"	,"mladyhtr"	,"nbmj8891.java"	,rom_mladyhtr,null	,machine_driver_mjnanpas	,input_ports_mladyhtr	,init_mladyhtr	,ROT0	,	"Nichibutsu", "Mahjong THE LADY HUNTER (Japan 900509)" )
	public static GameDriver driver_chinmoku	   = new GameDriver("1990"	,"chinmoku"	,"nbmj8891.java"	,rom_chinmoku,null	,machine_driver_mjnanpas	,input_ports_chinmoku	,init_chinmoku	,ROT0	,	"Nichibutsu", "Mahjong Chinmoku no Hentai (Japan 900511)" )
	public static GameDriver driver_maiko	   = new GameDriver("1990"	,"maiko"	,"nbmj8891.java"	,rom_maiko,null	,machine_driver_maiko	,input_ports_maiko	,init_maiko	,ROT0	,	"Nichibutsu", "Maikobana (Japan 900802)" )
	public static GameDriver driver_club90s	   = new GameDriver("1990"	,"club90s"	,"nbmj8891.java"	,rom_club90s,null	,machine_driver_mjnanpas	,input_ports_club90s	,init_club90s	,ROT0	,	"Nichibutsu", "Mahjong CLUB 90's (set 1) (Japan 900919)" )
	public static GameDriver driver_club90sa	   = new GameDriver("1990"	,"club90sa"	,"nbmj8891.java"	,rom_club90sa,driver_club90s	,machine_driver_mjnanpas	,input_ports_club90s	,init_club90s	,ROT0	,	"Nichibutsu", "Mahjong CLUB 90's (set 2) (Japan 900919)" )
	public static GameDriver driver_hanaoji	   = new GameDriver("1991"	,"hanaoji"	,"nbmj8891.java"	,rom_hanaoji,null	,machine_driver_hanaoji	,input_ports_hanaoji	,init_hanaoji	,ROT0	,	"Nichibutsu", "Hana to Ojisan [BET] (Japan 911209)" )
}
