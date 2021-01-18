/***************************************************************************

							-=  SunA 8 Bit Games =-

					driver by	Luca Elia (l.elia@tin.it)


Main  CPU:		Encrypted Z80 (Epoxy Module)
Sound CPU:		Z80 [Music]  +  Z80 [4 Bit PCM, Optional]
Sound Chips:	AY8910  +  YM3812/YM2203  + DAC x 4 [Optional]


---------------------------------------------------------------------------
Year + Game         Game     PCB         Epoxy CPU    Notes
---------------------------------------------------------------------------
88  Hard Head       KRB-14   60138-0083  S562008      Encryption + Protection
88  Rough Ranger	K030087  ?           S562008
90  Star Fighter    ?        ?           ?            Not Working
91  Hard Head 2     ?        ?           T568009      Not Working
92  Brick Zone      ?        ?           Yes          Not Working
---------------------------------------------------------------------------

To Do:

- Pen marking
- Samples playing in hardhead, rranger, starfigh (AY8910 ports A&B?)

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class suna8
{
	
	
	extern data8_t suna8_rombank, suna8_spritebank, suna8_palettebank;
	extern data8_t suna8_unknown;
	
	/* Functions defined in vidhrdw: */
	
	
	
	
	VIDEO_START( suna8_textdim0 );
	VIDEO_START( suna8_textdim8 );
	VIDEO_START( suna8_textdim12 );
	VIDEO_UPDATE( suna8 );
	
	
	/***************************************************************************
	
	
									ROMs Decryption
	
	
	***************************************************************************/
	
	/***************************************************************************
									Hard Head
	***************************************************************************/
	
	DRIVER_INIT( hardhead )
	{
		data8_t *RAM = memory_region(REGION_CPU1);
		int i;
	
		for (i = 0; i < 0x8000; i++)
		{
			data8_t x = RAM[i];
			if (!   ( (i & 0x800) && ((~i & 0x400) ^ ((i & 0x4000)>>4)) )	)
			{
				x	=	x ^ 0x58;
				x	=	(((x & (1<<0))?1:0)<<0) |
						(((x & (1<<1))?1:0)<<1) |
						(((x & (1<<2))?1:0)<<2) |
						(((x & (1<<4))?1:0)<<3) |
						(((x & (1<<3))?1:0)<<4) |
						(((x & (1<<5))?1:0)<<5) |
						(((x & (1<<6))?1:0)<<6) |
						(((x & (1<<7))?1:0)<<7);
			}
			RAM[i] = x;
		}
	}
	
	/* Non encrypted bootleg */
	static DRIVER_INIT( hardhedb )
	{
		/* patch ROM checksum (ROM1 fails self test) */
		memory_region( REGION_CPU1 )[0x1e5b] = 0xAF;
	}
	
	/***************************************************************************
									Brick Zone
	***************************************************************************/
	
	/* !! BRICKZN3 !! */
	
	static int is_special(int i)
	{
		if (i & 0x400)
		{
			switch ( i & 0xf )
			{
				case 0x1:
				case 0x5:	return 1;
				default:	return 0;
			}
		}
		else
		{
			switch ( i & 0xf )
			{
				case 0x3:
				case 0x7:
				case 0x8:
				case 0xc:	return 1;
				default:	return 0;
			}
		}
	}
	
	DRIVER_INIT( brickzn3 )
	{
		data8_t	*RAM	=	memory_region(REGION_CPU1);
		size_t	size	=	memory_region_length(REGION_CPU1)/2;
		int i;
	
		memory_set_opcode_base(0,RAM + size);
	
		/* Opcodes */
	
		for (i = 0; i < 0x50000; i++)
		{
			int encry;
			data8_t x = RAM[i];
	
			data8_t mask = 0x90;
	
		if (i >= 0x8000)
		{
			switch ( i & 0xf )
			{
	//825b  ->  see 715a!
	//8280  ->  see 7192!
	//8280:	e=2 m=90
	//8281:	e=2 m=90
	//8283:	e=2 m=90
	//8250:	e=0
	//8262:	e=0
	//9a42:	e=0
	//9a43:	e=0
	//8253:	e=0
				case 0x0:
				case 0x1:
				case 0x2:
				case 0x3:
					if (i & 0x40)	encry = 0;
					else			encry = 2;
					break;
	//828c:	e=0
	//9a3d:	e=0
	//825e:	e=0
	//826e:	e=0
	//9a3f:	e=0
				case 0xc:
				case 0xd:
				case 0xe:
				case 0xf:
					encry = 0;
					break;
	//8264:	e=2 m=90
	//9a44:	e=2 m=90
	//8255:	e=2 m=90
	//8255:	e=2 m=90
	//8285:	e=2 m=90
	//9a37:	e=2 m=90
	//8268:	e=2 m=90
	//9a3a:	e=2 m=90
	//825b:	e=2 m=90
				case 0x4:
				case 0x5:
				case 0x6:
				case 0x7:
				case 0x8:
				case 0xa:
				case 0xb:
				default:
					encry = 2;
			}
		}
		else
		if (	((i >= 0x0730) && (i <= 0x076f)) ||
				((i >= 0x4540) && (i <= 0x455f)) ||
				((i >= 0x79d9) && (i <= 0x7a09)) ||
				((i >= 0x72f3) && (i <= 0x7320))	)
		{
			if ( !is_special(i) )
			{
				mask = 0x10;
				encry = 1;
			}
			else
				encry = 0;
		}
		else
		{
	
			switch ( i & 0xf )
			{
	//0000: e=1 m=90
	//0001: e=1 m=90
	//0012: e=1 m=90
	
	//00c0: e=1 m=10
	//0041: e=1 m=10
	//0042: e=1 m=10
	//0342: e=1 m=10
	
	//05a0: e=1 m=90
	//04a1: e=2 m=90
	//04b1: e=2 m=90
	//05a1: e=2 m=90
	//05a2: e=1 m=90
	
	//0560: e=1 m=10
	//0441: e=0
	//0571: e=0
	//0562: e=1 m=10
				case 0x1:
					switch( i & 0x440 )
					{
						case 0x000:	encry = 1;	mask = 0x90;	break;
						case 0x040:	encry = 1;	mask = 0x10;	break;
						case 0x400:	encry = 2;	mask = 0x90;	break;
						default:
						case 0x440:	encry = 0;					break;
					}
					break;
	
				case 0x0:
				case 0x2:
					switch( i & 0x440 )
					{
						case 0x000:	encry = 1;	mask = 0x90;	break;
						case 0x040:	encry = 1;	mask = 0x10;	break;
						case 0x400:	encry = 1;	mask = 0x90;	break;
						default:
						case 0x440:	encry = 1;	mask = 0x10;	break;
					}
					break;
	
				case 0x3:
	//003: e=2 m=90
	//043: e=0
	//6a3: e=2 m=90
	//643: e=1 m=10
	//5d3: e=1 m=10
					switch( i & 0x440 )
					{
						case 0x000:	encry = 2;	mask = 0x90;	break;
						case 0x040:	encry = 0;					break;
						case 0x400:	encry = 1;	mask = 0x90;	break;
						default:
						case 0x440:	encry = 1;	mask = 0x10;	break;
					}
					break;
	
				case 0x5:
	//015: e=1 m=90
	//045: e=1 m=90
	//5b5: e=2 m=90
	//5d5: e=2 m=90
					if (i & 0x400)	encry = 2;
					else			encry = 1;
					break;
	
	
				case 0x7:
				case 0x8:
					if (i & 0x400)	{	encry = 1;	mask = 0x90;	}
					else			{	encry = 2;	}
					break;
	
				case 0xc:
					if (i & 0x400)	{	encry = 1;	mask = 0x10;	}
					else			{	encry = 0;	}
					break;
	
				case 0xd:
				case 0xe:
				case 0xf:
					mask = 0x10;
					encry = 1;
					break;
	
				default:
					encry = 1;
			}
		}
			switch (encry)
			{
				case 1:
					x	^=	mask;
					x	=	(((x & (1<<1))?1:0)<<0) |
							(((x & (1<<0))?1:0)<<1) |
							(((x & (1<<6))?1:0)<<2) |
							(((x & (1<<5))?1:0)<<3) |
							(((x & (1<<4))?1:0)<<4) |
							(((x & (1<<3))?1:0)<<5) |
							(((x & (1<<2))?1:0)<<6) |
							(((x & (1<<7))?1:0)<<7);
					break;
	
				case 2:
					x	^=	mask;
					x	=	(((x & (1<<0))?1:0)<<0) |	// swap
							(((x & (1<<1))?1:0)<<1) |
							(((x & (1<<6))?1:0)<<2) |
							(((x & (1<<5))?1:0)<<3) |
							(((x & (1<<4))?1:0)<<4) |
							(((x & (1<<3))?1:0)<<5) |
							(((x & (1<<2))?1:0)<<6) |
							(((x & (1<<7))?1:0)<<7);
					break;
			}
	
			RAM[i + size] = x;
		}
	
	
		/* Data */
	
		for (i = 0; i < 0x8000; i++)
		{
			data8_t x = RAM[i];
	
			if ( !is_special(i) )
			{
				x	^=	0x10;
				x	=	(((x & (1<<1))?1:0)<<0) |
						(((x & (1<<0))?1:0)<<1) |
						(((x & (1<<6))?1:0)<<2) |
						(((x & (1<<5))?1:0)<<3) |
						(((x & (1<<4))?1:0)<<4) |
						(((x & (1<<3))?1:0)<<5) |
						(((x & (1<<2))?1:0)<<6) |
						(((x & (1<<7))?1:0)<<7);
			}
	
			RAM[i] = x;
		}
	
	
	/* !!!!!! PATCHES !!!!!! */
	
	RAM[0x3337+size] = 0xc9;	// RET Z -> RET (to avoid: jp $C800)
	//RAM[0x3338+size] = 0x00;	// jp $C800 -> NOP
	//RAM[0x3339+size] = 0x00;	// jp $C800 -> NOP
	//RAM[0x333a+size] = 0x00;	// jp $C800 -> NOP
	
	RAM[0x1406+size] = 0x00;	// HALT -> NOP (NMI source??)
	RAM[0x2487+size] = 0x00;	// HALT -> NOP
	RAM[0x256c+size] = 0x00;	// HALT -> NOP
	}
	
	
	
	/***************************************************************************
									Hard Head 2
	***************************************************************************/
	
	INLINE data8_t hardhea2_decrypt(data8_t x, int encry, int mask)
	{
			switch( encry )
			{
			case 1:
				x	^=	mask;
				return	(((x & (1<<0))?1:0)<<0) |
						(((x & (1<<1))?1:0)<<1) |
						(((x & (1<<2))?1:0)<<2) |
						(((x & (1<<4))?1:0)<<3) |
						(((x & (1<<3))?1:0)<<4) |
						(((x & (1<<7))?1:0)<<5) |
						(((x & (1<<6))?1:0)<<6) |
						(((x & (1<<5))?1:0)<<7);
			case 2:
				x	^=	mask;
				return	(((x & (1<<0))?1:0)<<0) |
						(((x & (1<<1))?1:0)<<1) |
						(((x & (1<<2))?1:0)<<2) |
						(((x & (1<<3))?1:0)<<3) |	// swap
						(((x & (1<<4))?1:0)<<4) |
						(((x & (1<<7))?1:0)<<5) |
						(((x & (1<<6))?1:0)<<6) |
						(((x & (1<<5))?1:0)<<7);
			case 3:
				x	^=	mask;
				return	(((x & (1<<0))?1:0)<<0) |
						(((x & (1<<1))?1:0)<<1) |
						(((x & (1<<2))?1:0)<<2) |
						(((x & (1<<4))?1:0)<<3) |
						(((x & (1<<3))?1:0)<<4) |
						(((x & (1<<5))?1:0)<<5) |
						(((x & (1<<6))?1:0)<<6) |
						(((x & (1<<7))?1:0)<<7);
			case 0:
			default:
				return x;
			}
	}
	
	DRIVER_INIT( hardhea2 )
	{
		data8_t	*RAM	=	memory_region(REGION_CPU1);
		size_t	size	=	memory_region_length(REGION_CPU1)/2;
		data8_t x;
		int i,encry,mask;
	
		memory_set_opcode_base(0,RAM + size);
	
		/* Opcodes */
	
		for (i = 0; i < 0x8000; i++)
		{
	// Address lines scrambling
			switch (i & 0x7000)
			{
			case 0x4000:
			case 0x5000:
				break;
			default:
				if ((i & 0xc0) == 0x40)
				{
					int j = (i & ~0xc0) | 0x80;
					x		=	RAM[j];
					RAM[j]	=	RAM[i];
					RAM[i]	=	x;
				}
			}
	
			x		=	RAM[i];
	
			switch (i & 0x7000)
			{
			case 0x0000:
			case 0x6000:
				encry	=	1;
				switch ( i & 0x401 )
				{
				case 0x400:	mask = 0x41;	break;
				default:
				case 0x401:	mask = 0x45;
				}
				break;
	
			case 0x2000:
			case 0x4000:
				switch ( i & 0x401 )
				{
				case 0x000:	mask = 0x45;	encry = 1;	break;
				case 0x001:	mask = 0x04;	encry = 1;	break;
				case 0x400:	mask = 0x41;	encry = 3;	break;
				default:
				case 0x401:	mask = 0x45;	encry = 1;	break;
				}
				break;
	
			case 0x7000:
				switch ( i & 0x401 )
				{
				case 0x001:	mask = 0x45;	encry = 1;	break;
				default:
				case 0x000:
				case 0x400:
				case 0x401:	mask = 0x41;	encry = 3;	break;
				}
				break;
	
			case 0x1000:
			case 0x3000:
			case 0x5000:
				encry	=	1;
				switch ( i & 0x401 )
				{
				case 0x000:	mask = 0x41;	break;
				case 0x001:	mask = 0x45;	break;
				case 0x400:	mask = 0x41;	break;
				default:
				case 0x401:	mask = 0x41;
				}
				break;
	
			default:
				mask = 0x41;
				encry = 1;
			}
	
			RAM[i+size] = hardhea2_decrypt(x,encry,mask);
		}
	
	
		/* Data */
	
		for (i = 0; i < 0x8000; i++)
		{
			x		=	RAM[i];
			mask	=	0x41;
			switch (i & 0x7000)
			{
			case 0x2000:
			case 0x4000:
			case 0x7000:
				encry	=	0;
				break;
			default:
				encry	=	2;
			}
	
			RAM[i] = hardhea2_decrypt(x,encry,mask);
		}
	
		for (i = 0x00000; i < 0x40000; i++)
		{
	// Address lines scrambling
			switch (i & 0x3f000)
			{
	/*
	0x1000 to scramble:
			dump				screen
	rom10:	0y, 1y, 2n, 3n		0y,1y,2n,3n
			4n?,5n, 6n, 7n		4n,5n,6n,7n
			8?, 9n, an, bn		8n,9n,an,bn
			cy, dy, ey?,		cy,dy,en,fn
	rom11:						n
	rom12:						n
	rom13:	0?, 1y, 2n, 3n		?,?,?,? (palettes)
			4n, 5n, 6n, 7?		?,?,n,n (intro anim)
			8?, 9n?,an, bn		?,?,?,?
			cn, dy, en, fn		y,y,n,n
	*/
			case 0x00000:
			case 0x01000:
			case 0x0c000:
			case 0x0d000:
	
			case 0x30000:
			case 0x31000:
			case 0x3c000:
			case 0x3d000:
				if ((i & 0xc0) == 0x40)
				{
					int j = (i & ~0xc0) | 0x80;
					x				=	RAM[j+0x10000];
					RAM[j+0x10000]	=	RAM[i+0x10000];
					RAM[i+0x10000]	=	x;
				}
			}
		}
	}
	
	
	/***************************************************************************
									Star Fighter
	***************************************************************************/
	
	/* SAME AS HARDHEA2 */
	INLINE data8_t starfigh_decrypt(data8_t x, int encry, int mask)
	{
			switch( encry )
			{
			case 1:
				x	^=	mask;
				return	(((x & (1<<0))?1:0)<<0) |
						(((x & (1<<1))?1:0)<<1) |
						(((x & (1<<2))?1:0)<<2) |
						(((x & (1<<4))?1:0)<<3) |
						(((x & (1<<3))?1:0)<<4) |
						(((x & (1<<7))?1:0)<<5) |
						(((x & (1<<6))?1:0)<<6) |
						(((x & (1<<5))?1:0)<<7);
			case 2:
				x	^=	mask;
				return	(((x & (1<<0))?1:0)<<0) |
						(((x & (1<<1))?1:0)<<1) |
						(((x & (1<<2))?1:0)<<2) |
						(((x & (1<<3))?1:0)<<3) |	// swap
						(((x & (1<<4))?1:0)<<4) |
						(((x & (1<<7))?1:0)<<5) |
						(((x & (1<<6))?1:0)<<6) |
						(((x & (1<<5))?1:0)<<7);
			case 3:
				x	^=	mask;
				return	(((x & (1<<0))?1:0)<<0) |
						(((x & (1<<1))?1:0)<<1) |
						(((x & (1<<2))?1:0)<<2) |
						(((x & (1<<4))?1:0)<<3) |
						(((x & (1<<3))?1:0)<<4) |
						(((x & (1<<5))?1:0)<<5) |
						(((x & (1<<6))?1:0)<<6) |
						(((x & (1<<7))?1:0)<<7);
			case 0:
			default:
				return x;
			}
	}
	
	DRIVER_INIT( starfigh )
	{
		data8_t	*RAM	=	memory_region(REGION_CPU1);
		size_t	size	=	memory_region_length(REGION_CPU1)/2;
		data8_t x;
		int i,encry,mask;
	
		memory_set_opcode_base(0,RAM + size);
	
		/* Opcodes */
	
		for (i = 0; i < 0x8000; i++)
		{
	// Address lines scrambling
			switch (i & 0x7000)
			{
			case 0x0000:
			case 0x1000:
			case 0x2000:
			case 0x3000:
			case 0x4000:
			case 0x5000:
				if ((i & 0xc0) == 0x40)
				{
					int j = (i & ~0xc0) | 0x80;
					x		=	RAM[j];
					RAM[j]	=	RAM[i];
					RAM[i]	=	x;
				}
				break;
			case 0x6000:
			default:
				break;
			}
	
			x		=	RAM[i];
	
			switch (i & 0x7000)
			{
			case 0x2000:
			case 0x4000:
				switch ( i & 0x0c00 )
				{
				case 0x0400:	mask = 0x40;	encry = 3;	break;
				case 0x0800:	mask = 0x04;	encry = 1;	break;
				default:		mask = 0x44;	encry = 1;	break;
				}
				break;
	
			case 0x0000:
			case 0x1000:
			case 0x3000:
			case 0x5000:
			default:
				mask = 0x45;
				encry = 1;
			}
	
			RAM[i+size] = starfigh_decrypt(x,encry,mask);
		}
	
	
		/* Data */
	
		for (i = 0; i < 0x8000; i++)
		{
			x		=	RAM[i];
	
			switch (i & 0x7000)
			{
			case 0x2000:
			case 0x4000:
			case 0x7000:
				encry = 0;
				break;
			case 0x0000:
			case 0x1000:
			case 0x3000:
			case 0x5000:
			case 0x6000:
			default:
				mask = 0x45;
				encry = 2;
			}
	
			RAM[i] = starfigh_decrypt(x,encry,mask);
		}
	}
	
	
	/***************************************************************************
	
	
									Protection
	
	
	***************************************************************************/
	
	/***************************************************************************
									Hard Head
	***************************************************************************/
	
	static data8_t protection_val;
	
	public static ReadHandlerPtr hardhead_protection_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (protection_val & 0x80)
			return	((~offset & 0x20)			?	0x20 : 0) |
					((protection_val & 0x04)	?	0x80 : 0) |
					((protection_val & 0x01)	?	0x04 : 0);
		else
			return	((~offset & 0x20)					?	0x20 : 0) |
					(((offset ^ protection_val) & 0x01)	?	0x84 : 0);
	} };
	
	public static WriteHandlerPtr hardhead_protection_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (data & 0x80)	protection_val = data;
		else				protection_val = offset & 1;
	} };
	
	
	/***************************************************************************
	
	
								Memory Maps - Main CPU
	
	
	***************************************************************************/
	
	/***************************************************************************
									Hard Head
	***************************************************************************/
	
	static data8_t *hardhead_ip;
	
	public static ReadHandlerPtr hardhead_ip_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		switch (*hardhead_ip)
		{
			case 0:	return readinputport(0);
			case 1:	return readinputport(1);
			case 2:	return readinputport(2);
			case 3:	return readinputport(3);
			default:
				logerror("CPU #0 - PC %04X: Unknown IP read: %02X\n",activecpu_get_pc(),*hardhead_ip);
				return 0xff;
		}
	} };
	
	/*
		765- ----	Unused (eg. they go into hardhead_flipscreen_w)
		---4 ----
		---- 3210	ROM Bank
	*/
	public static WriteHandlerPtr hardhead_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data8_t *RAM = memory_region(REGION_CPU1);
		int bank = data & 0x0f;
	
		if (data & ~0xef) 	logerror("CPU #0 - PC %04X: unknown bank bits: %02X\n",activecpu_get_pc(),data);
	
		RAM = &RAM[0x4000 * bank + 0x10000];
		cpu_setbank(1, RAM);
	} };
	
	
	/*
		765- ----
		---4 3---	Coin Lockout
		---- -2--	Flip Screen
		---- --10
	*/
	public static WriteHandlerPtr hardhead_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		flip_screen_set(    data & 0x04);
		coin_lockout_w ( 0,	data & 0x08);
		coin_lockout_w ( 1,	data & 0x10);
	} };
	
	public static Memory_ReadAddress hardhead_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM				),	// ROM
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1				),	// Banked ROM
		new Memory_ReadAddress( 0xc000, 0xd7ff, MRA_RAM				),	// RAM
		new Memory_ReadAddress( 0xd800, 0xd9ff, MRA_RAM				),	// Palette
		new Memory_ReadAddress( 0xda00, 0xda00, hardhead_ip_r			),	// Input Ports
		new Memory_ReadAddress( 0xda80, 0xda80, soundlatch2_r			),	// From Sound CPU
		new Memory_ReadAddress( 0xdd80, 0xddff, hardhead_protection_r	),	// Protection
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_RAM				),	// Sprites
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hardhead_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM				),	// ROM
		new Memory_WriteAddress( 0x8000, 0xbfff, MWA_ROM				),	// Banked ROM
		new Memory_WriteAddress( 0xc000, 0xd7ff, MWA_RAM				),	// RAM
		new Memory_WriteAddress( 0xd800, 0xd9ff, paletteram_RRRRGGGGBBBBxxxx_swap_w, paletteram	),	// Palette
		new Memory_WriteAddress( 0xda00, 0xda00, MWA_RAM, hardhead_ip	),	// Input Port Select
		new Memory_WriteAddress( 0xda80, 0xda80, hardhead_bankswitch_w	),	// ROM Banking
		new Memory_WriteAddress( 0xdb00, 0xdb00, soundlatch_w			),	// To Sound CPU
		new Memory_WriteAddress( 0xdb80, 0xdb80, hardhead_flipscreen_w	),	// Flip Screen + Coin Lockout
		new Memory_WriteAddress( 0xdc00, 0xdc00, MWA_NOP				),	// <- R	(after bank select)
		new Memory_WriteAddress( 0xdc80, 0xdc80, MWA_NOP				),	// <- R (after bank select)
		new Memory_WriteAddress( 0xdd00, 0xdd00, MWA_NOP				),	// <- R (after ip select)
		new Memory_WriteAddress( 0xdd80, 0xddff, hardhead_protection_w	),	// Protection
		new Memory_WriteAddress( 0xe000, 0xffff, suna8_spriteram_w, spriteram	),	// Sprites
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort hardhead_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, IORP_NOP	),	// ? IRQ Ack
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort hardhead_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/***************************************************************************
									Rough Ranger
	***************************************************************************/
	
	/*
		76-- ----	Coin Lockout
		--5- ----	Flip Screen
		---4 ----	ROM Bank
		---- 3---
		---- -210	ROM Bank
	*/
	public static WriteHandlerPtr rranger_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data8_t *RAM = memory_region(REGION_CPU1);
		int bank = data & 0x07;
		if ((~data & 0x10) && (bank >= 4))	bank += 4;
	
		if (data & ~0xf7) 	logerror("CPU #0 - PC %04X: unknown bank bits: %02X\n",activecpu_get_pc(),data);
	
		RAM = &RAM[0x4000 * bank + 0x10000];
	
		cpu_setbank(1, RAM);
	
		flip_screen_set(    data & 0x20);
		coin_lockout_w ( 0,	data & 0x40);
		coin_lockout_w ( 1,	data & 0x80);
	} };
	
	/*
		7--- ----	1 -> Garbled title (another romset?)
		-654 ----
		---- 3---	1 -> No sound (soundlatch full?)
		---- -2--
		---- --1-	1 -> Interlude screens
		---- ---0
	*/
	public static ReadHandlerPtr rranger_soundstatus_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0x02;
	} };
	
	public static Memory_ReadAddress rranger_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM				),	// ROM
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1				),	// Banked ROM
		new Memory_ReadAddress( 0xc000, 0xc000, watchdog_reset_r		),	// Watchdog (Tested!)
		new Memory_ReadAddress( 0xc002, 0xc002, input_port_0_r		),	// P1 (Inputs)
		new Memory_ReadAddress( 0xc003, 0xc003, input_port_1_r		),	// P2
		new Memory_ReadAddress( 0xc004, 0xc004, rranger_soundstatus_r	),	// Latch Status?
		new Memory_ReadAddress( 0xc200, 0xc200, MRA_NOP				),	// Protection?
		new Memory_ReadAddress( 0xc280, 0xc280, input_port_2_r		),	// DSW 1
		new Memory_ReadAddress( 0xc2c0, 0xc2c0, input_port_3_r		),	// DSW 2
		new Memory_ReadAddress( 0xc600, 0xc7ff, MRA_RAM				),	// Palette
		new Memory_ReadAddress( 0xc800, 0xdfff, MRA_RAM				),	// RAM
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_RAM				),	// Sprites
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress rranger_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM				),	// ROM
		new Memory_WriteAddress( 0x8000, 0xbfff, MWA_ROM				),	// Banked ROM
		new Memory_WriteAddress( 0xc000, 0xc000, soundlatch_w			),	// To Sound CPU
		new Memory_WriteAddress( 0xc002, 0xc002, rranger_bankswitch_w	),	// ROM Banking
		new Memory_WriteAddress( 0xc200, 0xc200, MWA_NOP				),	// Protection?
		new Memory_WriteAddress( 0xc280, 0xc280, MWA_NOP				),	// ? NMI Ack
		new Memory_WriteAddress( 0xc600, 0xc7ff, paletteram_RRRRGGGGBBBBxxxx_swap_w, paletteram	),	// Palette
		new Memory_WriteAddress( 0xc800, 0xdfff, MWA_RAM				),	// RAM
		new Memory_WriteAddress( 0xe000, 0xffff, suna8_spriteram_w, spriteram	),	// Sprites
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort rranger_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, IORP_NOP	),	// ? IRQ Ack
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort rranger_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/***************************************************************************
									Brick Zone
	***************************************************************************/
	
	/*
	?
	*/
	public static ReadHandlerPtr brickzn_c140_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0xff;
	} };
	
	/*
	*/
	public static WriteHandlerPtr brickzn_palettebank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		suna8_palettebank = (data >> 1) & 1;
		if (data & ~0x02) 	logerror("CPU #0 - PC %04X: unknown palettebank bits: %02X\n",activecpu_get_pc(),data);
	
		/* Also used as soundlatch - depending on c0c0? */
		soundlatch_w(0,data);
	} };
	
	/*
		7654 32--
		---- --1-	Ram Bank
		---- ---0	Flip Screen
	*/
	public static WriteHandlerPtr brickzn_spritebank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		suna8_spritebank = (data >> 1) & 1;
		if (data & ~0x03) 	logerror("CPU #0 - PC %04X: unknown spritebank bits: %02X\n",activecpu_get_pc(),data);
		flip_screen_set( data & 0x01 );
	} };
	
	public static WriteHandlerPtr brickzn_unknown_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		suna8_unknown = data;
	} };
	
	/*
		7654 ----
		---- 3210	ROM Bank
	*/
	public static WriteHandlerPtr brickzn_rombank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data8_t *RAM = memory_region(REGION_CPU1);
		int bank = data & 0x0f;
	
		if (data & ~0x0f) 	logerror("CPU #0 - PC %04X: unknown rom bank bits: %02X\n",activecpu_get_pc(),data);
	
		RAM = &RAM[0x4000 * bank + 0x10000];
	
		cpu_setbank(1, RAM);
		suna8_rombank = data;
	} };
	
	public static Memory_ReadAddress brickzn_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM					),	// ROM
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1					),	// Banked ROM
		new Memory_ReadAddress( 0xc100, 0xc100, input_port_0_r			),	// P1 (Buttons)
		new Memory_ReadAddress( 0xc101, 0xc101, input_port_1_r			),	// P2
		new Memory_ReadAddress( 0xc102, 0xc102, input_port_2_r			),	// DSW 1
		new Memory_ReadAddress( 0xc103, 0xc103, input_port_3_r			),	// DSW 2
		new Memory_ReadAddress( 0xc108, 0xc108, input_port_4_r			),	// P1 (Analog)
		new Memory_ReadAddress( 0xc10c, 0xc10c, input_port_5_r			),	// P2
		new Memory_ReadAddress( 0xc140, 0xc140, brickzn_c140_r			),	// ???
		new Memory_ReadAddress( 0xc600, 0xc7ff, suna8_banked_paletteram_r	),	// Palette (Banked)
		new Memory_ReadAddress( 0xc800, 0xdfff, MRA_RAM					),	// RAM
		new Memory_ReadAddress( 0xe000, 0xffff, suna8_banked_spriteram_r	),	// Sprites (Banked)
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress brickzn_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM						),	// ROM
		new Memory_WriteAddress( 0x8000, 0xbfff, MWA_ROM						),	// Banked ROM
		new Memory_WriteAddress( 0xc040, 0xc040, brickzn_rombank_w				),	// ROM Bank
		new Memory_WriteAddress( 0xc060, 0xc060, brickzn_spritebank_w			),	// Sprite  RAM Bank + Flip Screen
		new Memory_WriteAddress( 0xc0a0, 0xc0a0, brickzn_palettebank_w			),	// Palette RAM Bank + ?
		new Memory_WriteAddress( 0xc0c0, 0xc0c0, brickzn_unknown_w				),	// ???
		new Memory_WriteAddress( 0xc600, 0xc7ff, brickzn_banked_paletteram_w	),	// Palette (Banked)
		new Memory_WriteAddress( 0xc800, 0xdfff, MWA_RAM						),	// RAM
		new Memory_WriteAddress( 0xe000, 0xffff, suna8_banked_spriteram_w		),	// Sprites (Banked)
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort brickzn_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort brickzn_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/***************************************************************************
									Hard Head 2
	***************************************************************************/
	
	static data8_t suna8_nmi_enable;
	
	/* Probably wrong: */
	public static WriteHandlerPtr hardhea2_nmi_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		suna8_nmi_enable = data & 0x01;
		if (data & ~0x01) 	logerror("CPU #0 - PC %04X: unknown nmi bits: %02X\n",activecpu_get_pc(),data);
	} };
	
	/*
		7654 321-
		---- ---0	Flip Screen
	*/
	public static WriteHandlerPtr hardhea2_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		flip_screen_set(data & 0x01);
		if (data & ~0x01) 	logerror("CPU #0 - PC %04X: unknown flipscreen bits: %02X\n",activecpu_get_pc(),data);
	} };
	
	public static WriteHandlerPtr hardhea2_leds_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		set_led_status(0, data & 0x01);
		set_led_status(1, data & 0x02);
		coin_counter_w(0, data & 0x04);
		if (data & ~0x07)	logerror("CPU#0  - PC %06X: unknown leds bits: %02X\n",activecpu_get_pc(),data);
	} };
	
	/*
		7654 32--
		---- --1-	Ram Bank
		---- ---0	Ram Bank?
	*/
	public static WriteHandlerPtr hardhea2_spritebank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		suna8_spritebank = (data >> 1) & 1;
		if (data & ~0x02) 	logerror("CPU #0 - PC %04X: unknown spritebank bits: %02X\n",activecpu_get_pc(),data);
	} };
	
	public static ReadHandlerPtr hardhea2_c080_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0xff;
	} };
	
	/*
		7654 ----
		---- 3210	ROM Bank
	*/
	public static WriteHandlerPtr hardhea2_rombank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		data8_t *RAM = memory_region(REGION_CPU1);
		int bank = data & 0x0f;
	
		if (data & ~0x0f) 	logerror("CPU #0 - PC %04X: unknown rom bank bits: %02X\n",activecpu_get_pc(),data);
	
		RAM = &RAM[0x4000 * bank + 0x10000];
	
		cpu_setbank(1, RAM);
		suna8_rombank = data;
	} };
	
	public static Memory_ReadAddress hardhea2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM					),	// ROM
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1					),	// Banked ROM
		new Memory_ReadAddress( 0xc000, 0xc000, input_port_0_r			),	// P1 (Inputs)
		new Memory_ReadAddress( 0xc001, 0xc001, input_port_1_r			),	// P2
		new Memory_ReadAddress( 0xc002, 0xc002, input_port_2_r			),	// DSW 1
		new Memory_ReadAddress( 0xc003, 0xc003, input_port_3_r			),	// DSW 2
		new Memory_ReadAddress( 0xc080, 0xc080, hardhea2_c080_r			),	// ???
		new Memory_ReadAddress( 0xc600, 0xc7ff, paletteram_r				),	// Palette (Banked??)
		new Memory_ReadAddress( 0xc800, 0xdfff, MRA_RAM					),	// RAM
		new Memory_ReadAddress( 0xe000, 0xffff, suna8_banked_spriteram_r	),	// Sprites (Banked)
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hardhea2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM					),	// ROM
		new Memory_WriteAddress( 0x8000, 0xbfff, MWA_ROM					),	// Banked ROM
		new Memory_WriteAddress( 0xc200, 0xc200, hardhea2_spritebank_w		),	// Sprite RAM Bank
		new Memory_WriteAddress( 0xc280, 0xc280, hardhea2_rombank_w		),	// ROM Bank (?mirrored up to c2ff?)
		new Memory_WriteAddress( 0xc300, 0xc300, hardhea2_flipscreen_w		),	// Flip Screen
		new Memory_WriteAddress( 0xc380, 0xc380, hardhea2_nmi_w			),	// ? NMI related ?
		new Memory_WriteAddress( 0xc400, 0xc400, hardhea2_leds_w			),	// Leds + Coin Counter
		new Memory_WriteAddress( 0xc480, 0xc480, MWA_NOP					),	// ~ROM Bank
		new Memory_WriteAddress( 0xc500, 0xc500, soundlatch_w				),	// To Sound CPU
		new Memory_WriteAddress( 0xc600, 0xc7ff, paletteram_RRRRGGGGBBBBxxxx_swap_w, paletteram	),	// Palette (Banked??)
		new Memory_WriteAddress( 0xc800, 0xdfff, MWA_RAM					),	// RAM
		new Memory_WriteAddress( 0xe000, 0xffff, suna8_banked_spriteram_w	),	// Sprites (Banked)
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort hardhea2_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort hardhea2_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/***************************************************************************
									Star Fighter
	***************************************************************************/
	
	static data8_t spritebank_latch;
	public static WriteHandlerPtr starfigh_spritebank_latch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		spritebank_latch = (data >> 2) & 1;
		if (data & ~0x04) 	logerror("CPU #0 - PC %04X: unknown spritebank bits: %02X\n",activecpu_get_pc(),data);
	} };
	
	public static WriteHandlerPtr starfigh_spritebank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		suna8_spritebank = spritebank_latch;
	} };
	
	public static Memory_ReadAddress starfigh_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM					),	// ROM
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1					),	// Banked ROM
		new Memory_ReadAddress( 0xc000, 0xc000, input_port_0_r			),	// P1 (Inputs)
		new Memory_ReadAddress( 0xc001, 0xc001, input_port_1_r			),	// P2
		new Memory_ReadAddress( 0xc002, 0xc002, input_port_2_r			),	// DSW 1
		new Memory_ReadAddress( 0xc003, 0xc003, input_port_3_r			),	// DSW 2
		new Memory_ReadAddress( 0xc600, 0xc7ff, suna8_banked_paletteram_r	),	// Palette (Banked??)
		new Memory_ReadAddress( 0xc800, 0xdfff, MRA_RAM					),	// RAM
		new Memory_ReadAddress( 0xe000, 0xffff, suna8_banked_spriteram_r	),	// Sprites (Banked)
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress starfigh_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM						),	// ROM
		new Memory_WriteAddress( 0x8000, 0xbfff, MWA_ROM						),	// Banked ROM
		new Memory_WriteAddress( 0xc200, 0xc200, starfigh_spritebank_w			),	// Sprite RAM Bank
		new Memory_WriteAddress( 0xc380, 0xc3ff, starfigh_spritebank_latch_w	),	// Sprite RAM Bank
		new Memory_WriteAddress( 0xc280, 0xc280, hardhea2_rombank_w			),	// ROM Bank (?mirrored up to c2ff?)
		new Memory_WriteAddress( 0xc300, 0xc300, hardhea2_flipscreen_w			),	// Flip Screen
		new Memory_WriteAddress( 0xc400, 0xc400, hardhea2_leds_w				),	// Leds + Coin Counter
		new Memory_WriteAddress( 0xc500, 0xc500, soundlatch_w					),	// To Sound CPU
		new Memory_WriteAddress( 0xc600, 0xc7ff, paletteram_RRRRGGGGBBBBxxxx_swap_w, paletteram	),	// Palette (Banked??)
		new Memory_WriteAddress( 0xc800, 0xdfff, MWA_RAM						),	// RAM
		new Memory_WriteAddress( 0xe000, 0xffff, suna8_banked_spriteram_w		),	// Sprites (Banked)
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort starfigh_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort starfigh_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	/***************************************************************************
	
	
								Memory Maps - Sound CPU(s)
	
	
	***************************************************************************/
	
	/***************************************************************************
									Hard Head
	***************************************************************************/
	
	public static Memory_ReadAddress hardhead_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM					),	// ROM
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM					),	// RAM
		new Memory_ReadAddress( 0xc800, 0xc800, YM3812_status_port_0_r 	),	// ? unsure
		new Memory_ReadAddress( 0xd800, 0xd800, soundlatch_r				),	// From Main CPU
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hardhead_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM					),	// ROM
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM					),	// RAM
		new Memory_WriteAddress( 0xd000, 0xd000, soundlatch2_w				),	//
		new Memory_WriteAddress( 0xa000, 0xa000, YM3812_control_port_0_w	),	// YM3812
		new Memory_WriteAddress( 0xa001, 0xa001, YM3812_write_port_0_w		),
		new Memory_WriteAddress( 0xa002, 0xa002, AY8910_control_port_0_w	),	// AY8910
		new Memory_WriteAddress( 0xa003, 0xa003, AY8910_write_port_0_w		),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort hardhead_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, IORP_NOP	),	// ? IRQ Ack
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort hardhead_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	/***************************************************************************
									Rough Ranger
	***************************************************************************/
	
	public static Memory_ReadAddress rranger_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM					),	// ROM
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM					),	// RAM
		new Memory_ReadAddress( 0xd800, 0xd800, soundlatch_r				),	// From Main CPU
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress rranger_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM					),	// ROM
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM					),	// RAM
		new Memory_WriteAddress( 0xd000, 0xd000, soundlatch2_w				),	//
		new Memory_WriteAddress( 0xa000, 0xa000, YM2203_control_port_0_w	),	// YM2203
		new Memory_WriteAddress( 0xa001, 0xa001, YM2203_write_port_0_w		),
		new Memory_WriteAddress( 0xa002, 0xa002, YM2203_control_port_1_w	),	// AY8910
		new Memory_WriteAddress( 0xa003, 0xa003, YM2203_write_port_1_w		),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort rranger_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort rranger_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/***************************************************************************
									Brick Zone
	***************************************************************************/
	
	public static Memory_ReadAddress brickzn_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM					),	// ROM
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM					),	// RAM
		new Memory_ReadAddress( 0xf800, 0xf800, soundlatch_r				),	// From Main CPU
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress brickzn_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM					),	// ROM
		new Memory_WriteAddress( 0xc000, 0xc000, YM3812_control_port_0_w	),	// YM3812
		new Memory_WriteAddress( 0xc001, 0xc001, YM3812_write_port_0_w		),
		new Memory_WriteAddress( 0xc002, 0xc002, AY8910_control_port_0_w	),	// AY8910
		new Memory_WriteAddress( 0xc003, 0xc003, AY8910_write_port_0_w		),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM					),	// RAM
		new Memory_WriteAddress( 0xf000, 0xf000, soundlatch2_w				),	// To PCM CPU
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort brickzn_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort brickzn_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/* PCM Z80 , 4 DACs (4 bits per sample), NO RAM !! */
	
	public static Memory_ReadAddress brickzn_pcm_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xffff, MRA_ROM	),	// ROM
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	public static Memory_WriteAddress brickzn_pcm_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xffff, MWA_ROM	),	// ROM
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static WriteHandlerPtr brickzn_pcm_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		DAC_data_w( offset & 3, (data & 0xf) * 0x11 );
	} };
	
	public static IO_ReadPort brickzn_pcm_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, soundlatch2_r		),	// From Sound CPU
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	public static IO_WritePort brickzn_pcm_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x03, brickzn_pcm_w			),	// 4 x DAC
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	/***************************************************************************
	
	
									Input Ports
	
	
	***************************************************************************/
	
	#define JOY(_n_) \
		PORT_BIT(  0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_PLAYER##_n_ );\
		PORT_BIT(  0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_PLAYER##_n_ );\
		PORT_BIT(  0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER##_n_ );\
		PORT_BIT(  0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER##_n_ );\
		PORT_BIT(  0x10, IP_ACTIVE_LOW, IPT_BUTTON1        | IPF_PLAYER##_n_ );\
		PORT_BIT(  0x20, IP_ACTIVE_LOW, IPT_BUTTON2        | IPF_PLAYER##_n_ );\
		PORT_BIT(  0x40, IP_ACTIVE_LOW, IPT_START##_n_ );\
		PORT_BIT(  0x80, IP_ACTIVE_LOW, IPT_COIN##_n_  );
	
	/***************************************************************************
									Hard Head
	***************************************************************************/
	
	static InputPortPtr input_ports_hardhead = new InputPortPtr(){ public void handler() { 
	
		PORT_START(); 	// IN0 - Player 1 - $da00 (ip = 0)
		JOY(1)
	
		PORT_START(); 	// IN1 - Player 2 - $da00 (ip = 1)
		JOY(2)
	
		PORT_START(); 	// IN2 - DSW 1 - $da00 (ip = 2)
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x0e, 0x0e, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x0e, "No Bonus" );
		PORT_DIPSETTING(    0x0c, "10K" );
		PORT_DIPSETTING(    0x0a, "20K" );
		PORT_DIPSETTING(    0x08, "50K" );
		PORT_DIPSETTING(    0x06, "50K, Every 50K" );
		PORT_DIPSETTING(    0x04, "100K, Every 50K" );
		PORT_DIPSETTING(    0x02, "100K, Every 100K" );
		PORT_DIPSETTING(    0x00, "200K, Every 100K" );
		PORT_DIPNAME( 0x70, 0x70, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_4C") );
		PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	// IN3 - DSW 2 - $da00 (ip = 3)
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x04, "Play Together" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x18, "2" );
		PORT_DIPSETTING(    0x10, "3" );
		PORT_DIPSETTING(    0x08, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xe0, "Easiest" );
		PORT_DIPSETTING(    0xc0, "Very Easy" );
		PORT_DIPSETTING(    0xa0, "Easy" );
		PORT_DIPSETTING(    0x80, "Moderate" );
		PORT_DIPSETTING(    0x60, "Normal" );
		PORT_DIPSETTING(    0x40, "Harder" );
		PORT_DIPSETTING(    0x20, "Very Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
	INPUT_PORTS_END(); }}; 
	
	/***************************************************************************
									Rough Ranger
	***************************************************************************/
	
	static InputPortPtr input_ports_rranger = new InputPortPtr(){ public void handler() { 
	
		PORT_START(); 	// IN0 - Player 1 - $c002
		JOY(1)
	
		PORT_START(); 	// IN1 - Player 2 - $c003
		JOY(2)
	
		PORT_START(); 	// IN2 - DSW 1 - $c280
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x30, "10K" );
		PORT_DIPSETTING(    0x28, "30K" );
		PORT_DIPSETTING(    0x20, "50K" );
		PORT_DIPSETTING(    0x18, "50K, Every 50K" );
		PORT_DIPSETTING(    0x10, "100K, Every 50K" );
		PORT_DIPSETTING(    0x08, "100K, Every 100K" );
		PORT_DIPSETTING(    0x00, "100K, Every 200K" );
		PORT_DIPSETTING(    0x38, "None" );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xc0, "Normal" );
		PORT_DIPSETTING(    0x80, "Hard" );
		PORT_DIPSETTING(    0x40, "Harder" );
		PORT_DIPSETTING(    0x00, "Hardest" );
	
		PORT_START(); 	// IN3 - DSW 2 - $c2c0
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x04, "Play Together" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x08, 0x08, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x30, "2" );
		PORT_DIPSETTING(    0x20, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
	INPUT_PORTS_END(); }}; 
	
	
	/***************************************************************************
									Brick Zone
	***************************************************************************/
	
	static InputPortPtr input_ports_brickzn = new InputPortPtr(){ public void handler() { 
	
		PORT_START(); 	// IN0 - Player 1 - $c100
		JOY(1)
	
		PORT_START(); 	// IN1 - Player 2 - $c101
		JOY(2)
	
		PORT_START(); 	// IN2 - DSW 1 - $c102
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );	// rom 38:b840
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x38, "Easiest" );
		PORT_DIPSETTING(    0x30, "Very Easy" );
		PORT_DIPSETTING(    0x28, "Easy" );
		PORT_DIPSETTING(    0x20, "Moderate" );
		PORT_DIPSETTING(    0x18, "Normal" );
		PORT_DIPSETTING(    0x10, "Harder" );
		PORT_DIPSETTING(    0x08, "Very Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
	//	PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
	//	PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE(       0x40, IP_ACTIVE_LOW );// + Invulnerability
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	// IN3 - DSW 2 - $c103
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x04, "Play Together" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x30, "10K" );
		PORT_DIPSETTING(    0x28, "30K" );
		PORT_DIPSETTING(    0x18, "50K, Every 50K" );
		PORT_DIPSETTING(    0x20, "50K" );
		PORT_DIPSETTING(    0x10, "100K, Every 50K" );
		PORT_DIPSETTING(    0x08, "100K, Every 100K" );
		PORT_DIPSETTING(    0x00, "200K, Every 100K" );
		PORT_DIPSETTING(    0x38, "None" );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x80, "2" );
		PORT_DIPSETTING(    0xc0, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPSETTING(    0x00, "5" );
	
		PORT_START(); 	// IN4 - Player 1 - $c108
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_X | IPF_REVERSE, 50, 0, 0, 0);
	
		PORT_START(); 	// IN5 - Player 2 - $c10c
		PORT_ANALOG( 0xff, 0x00, IPT_TRACKBALL_X | IPF_REVERSE, 50, 0, 0, 0);
	
	INPUT_PORTS_END(); }}; 
	
	
	/***************************************************************************
							Hard Head 2 / Star Fighter
	***************************************************************************/
	
	static InputPortPtr input_ports_hardhea2 = new InputPortPtr(){ public void handler() { 
	
		PORT_START(); 	// IN0 - Player 1 - $c000
		JOY(1)
	
		PORT_START(); 	// IN1 - Player 2 - $c001
		JOY(2)
	
		PORT_START(); 	// IN2 - DSW 1 - $c002
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x38, "Easiest" );
		PORT_DIPSETTING(    0x30, "Very Easy" );
		PORT_DIPSETTING(    0x28, "Easy" );
		PORT_DIPSETTING(    0x20, "Moderate" );
		PORT_DIPSETTING(    0x18, "Normal" );
		PORT_DIPSETTING(    0x10, "Harder" );
		PORT_DIPSETTING(    0x08, "Very Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_SERVICE(       0x40, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	// IN3 - DSW 2 - $c003
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x04, "Play Together" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x30, "10K" );
		PORT_DIPSETTING(    0x28, "30K" );
		PORT_DIPSETTING(    0x18, "50K, Every 50K" );
		PORT_DIPSETTING(    0x20, "50K" );
		PORT_DIPSETTING(    0x10, "100K, Every 50K" );
		PORT_DIPSETTING(    0x08, "100K, Every 100K" );
		PORT_DIPSETTING(    0x00, "200K, Every 100K" );
		PORT_DIPSETTING(    0x38, "None" );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x80, "2" );
		PORT_DIPSETTING(    0xc0, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPSETTING(    0x00, "5" );
	
	INPUT_PORTS_END(); }}; 
	
	/***************************************************************************
	
	
									Graphics Layouts
	
	
	***************************************************************************/
	
	/* 8x8x4 tiles (2 bitplanes per ROM) */
	static GfxLayout layout_8x8x4 = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,2),
		4,
		new int[] { RGN_FRAC(1,2) + 0, RGN_FRAC(1,2) + 4, 0, 4 },
		new int[] { 3,2,1,0, 11,10,9,8},
		new int[] { STEP8(0,16) },
		8*8*2
	);
	
	static GfxDecodeInfo suna8_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, layout_8x8x4, 0, 16 ), // [0] Sprites
		new GfxDecodeInfo( -1 )
	};
	
	
	
	/***************************************************************************
	
	
									Machine Drivers
	
	
	***************************************************************************/
	
	static void soundirq(int state)
	{
		cpu_set_irq_line(1, 0, state);
	}
	
	/* In games with only 2 CPUs, port A&B of the AY8910 are probably used
	   for sample playing. */
	
	/***************************************************************************
									Hard Head
	***************************************************************************/
	
	/* 1 x 24 MHz crystal */
	
	static AY8910interface hardhead_ay8910_interface = new AY8910interface
	(
		1,
		4000000,	/* ? */
		new int[] { 50 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static struct YM3812interface hardhead_ym3812_interface =
	{
		1,
		4000000,	/* ? */
		{ 100 },
		{  0 },		/* IRQ Line */
	};
	
	
	static MACHINE_DRIVER_START( hardhead )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)					/* ? */
		MDRV_CPU_MEMORY(hardhead_readmem,hardhead_writemem)
		MDRV_CPU_PORTS(hardhead_readport,hardhead_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)	/* No NMI */
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)					/* ? */
		MDRV_CPU_MEMORY(hardhead_sound_readmem,hardhead_sound_writemem)
		MDRV_CPU_PORTS(hardhead_sound_readport,hardhead_sound_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,4)	/* No NMI */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(0, 256-1, 0+16, 256-16-1)
		MDRV_GFXDECODE(suna8_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(suna8_textdim12)
		MDRV_VIDEO_UPDATE(suna8)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM3812, hardhead_ym3812_interface)
		MDRV_SOUND_ADD(AY8910, hardhead_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
									Rough Ranger
	***************************************************************************/
	
	/* 1 x 24 MHz crystal */
	
	/* 2203 + 8910 */
	static struct YM2203interface rranger_ym2203_interface =
	{
		2,
		4000000,	/* ? */
		{ YM2203_VOL(50,50), YM2203_VOL(50,50) },
		{ 0,0 },	/* Port A Read  */
		{ 0,0 },	/* Port B Read  */
		{ 0,0 },	/* Port A Write */
		{ 0,0 },	/* Port B Write */
		{ 0,0 }		/* IRQ handler  */
	};
	
	static MACHINE_DRIVER_START( rranger )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)					/* ? */
		MDRV_CPU_MEMORY(rranger_readmem,rranger_writemem)
		MDRV_CPU_PORTS(rranger_readport,rranger_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)	/* IRQ & NMI ! */
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)					/* ? */
		MDRV_CPU_MEMORY(rranger_sound_readmem,rranger_sound_writemem)
		MDRV_CPU_PORTS(rranger_sound_readport,rranger_sound_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,4)	/* NMI = retn */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(0, 256-1, 0+16, 256-16-1)
		MDRV_GFXDECODE(suna8_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(suna8_textdim8)
		MDRV_VIDEO_UPDATE(suna8)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2203, rranger_ym2203_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
									Brick Zone
	***************************************************************************/
	
	/* 1 x 24 MHz crystal */
	
	static AY8910interface brickzn_ay8910_interface = new AY8910interface
	(
		1,
		4000000,	/* ? */
		new int[] { 33 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static struct YM3812interface brickzn_ym3812_interface =
	{
		1,
		4000000,	/* ? */
		{ 66 },
		{ soundirq },	/* IRQ Line */
	};
	
	static DACinterface brickzn_dac_interface = new DACinterface
	(
		4,
		new int[] {	MIXER(17,MIXER_PAN_LEFT), MIXER(17,MIXER_PAN_RIGHT),
			MIXER(17,MIXER_PAN_LEFT), MIXER(17,MIXER_PAN_RIGHT)	}
	);
	
	INTERRUPT_GEN( brickzn_interrupt )
	{
		if (cpu_getiloops()) cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
		else				 cpu_set_irq_line(0, 0, HOLD_LINE);
	}
	
	static MACHINE_DRIVER_START( brickzn )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)					/* ? */
		MDRV_CPU_MEMORY(brickzn_readmem,brickzn_writemem)
		MDRV_CPU_PORTS(brickzn_readport,brickzn_writeport)
	//	MDRV_CPU_VBLANK_INT(brickzn_interrupt, 2)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)	// nmi breaks ramtest but is needed!
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)					/* ? */
		MDRV_CPU_MEMORY(brickzn_sound_readmem,brickzn_sound_writemem)
		MDRV_CPU_PORTS(brickzn_sound_readport,brickzn_sound_writeport)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)					/* ? */
		MDRV_CPU_MEMORY(brickzn_pcm_readmem,brickzn_pcm_writemem)
		MDRV_CPU_PORTS(brickzn_pcm_readport,brickzn_pcm_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(0, 256-1, 0+16, 256-16-1)
		MDRV_GFXDECODE(suna8_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(suna8_textdim0)
		MDRV_VIDEO_UPDATE(suna8)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM3812, brickzn_ym3812_interface)
		MDRV_SOUND_ADD(AY8910, brickzn_ay8910_interface)
		MDRV_SOUND_ADD(DAC, brickzn_dac_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
									Hard Head 2
	***************************************************************************/
	
	/* 1 x 24 MHz crystal */
	
	INTERRUPT_GEN( hardhea2_interrupt )
	{
		if (cpu_getiloops())
		{
			if (suna8_nmi_enable)	cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
		}
		else cpu_set_irq_line(0, 0, HOLD_LINE);
	}
	
	static MACHINE_DRIVER_START( hardhea2 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)	/* SUNA T568009 */
		MDRV_CPU_MEMORY(hardhea2_readmem,hardhea2_writemem)
		MDRV_CPU_PORTS(hardhea2_readport,hardhea2_writeport)
		MDRV_CPU_VBLANK_INT(hardhea2_interrupt,2)	/* IRQ & NMI */
	
		/* The sound section is identical to that of brickzn */
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)					/* ? */
		MDRV_CPU_MEMORY(brickzn_sound_readmem,brickzn_sound_writemem)
		MDRV_CPU_PORTS(brickzn_sound_readport,brickzn_sound_writeport)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)					/* ? */
		MDRV_CPU_MEMORY(brickzn_pcm_readmem,brickzn_pcm_writemem)
		MDRV_CPU_PORTS(brickzn_pcm_readport,brickzn_pcm_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(0, 256-1, 0+16, 256-16-1)
		MDRV_GFXDECODE(suna8_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(suna8_textdim0)
		MDRV_VIDEO_UPDATE(suna8)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM3812, brickzn_ym3812_interface)
		MDRV_SOUND_ADD(AY8910, brickzn_ay8910_interface)
		MDRV_SOUND_ADD(DAC, brickzn_dac_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
									Star Fighter
	***************************************************************************/
	
	static AY8910interface starfigh_ay8910_interface = new AY8910interface
	(
		1,
		4000000,	/* ? */
		new int[] { 50 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static struct YM3812interface starfigh_ym3812_interface =
	{
		1,
		4000000,	/* ? */
		{ 100 },
		{  0 },
	};
	
	static MACHINE_DRIVER_START( starfigh )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)					/* ? */
		MDRV_CPU_MEMORY(starfigh_readmem,starfigh_writemem)
		MDRV_CPU_PORTS(starfigh_readport,starfigh_writeport)
		MDRV_CPU_VBLANK_INT(brickzn_interrupt,2)	/* IRQ & NMI */
	
		/* The sound section is identical to that of hardhead */
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)					/* ? */
		MDRV_CPU_MEMORY(hardhead_sound_readmem,hardhead_sound_writemem)
		MDRV_CPU_PORTS(hardhead_sound_readport,hardhead_sound_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,4)	/* No NMI */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(0, 256-1, 0+16, 256-16-1)
		MDRV_GFXDECODE(suna8_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(suna8_textdim0)
		MDRV_VIDEO_UPDATE(suna8)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM3812, starfigh_ym3812_interface)
		MDRV_SOUND_ADD(AY8910, starfigh_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	
									ROMs Loading
	
	
	***************************************************************************/
	
	/***************************************************************************
	
										Hard Head
	
	Location  Type    File ID  Checksum
	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	L5       27C256     P1       1327   [ main program ]
	K5       27C256     P2       50B1   [ main program ]
	J5       27C256     P3       CF73   [ main program ]
	I5       27C256     P4       DE86   [ main program ]
	D5       27C256     P5       94D1   [  background  ]
	A5       27C256     P6       C3C7   [ motion obj.  ]
	L7       27C256     P7       A7B8   [ main program ]
	K7       27C256     P8       5E53   [ main program ]
	J7       27C256     P9       35FC   [ main program ]
	I7       27C256     P10      8F9A   [ main program ]
	D7       27C256     P11      931C   [  background  ]
	A7       27C256     P12      2EED   [ motion obj.  ]
	H9       27C256     P13      5CD2   [ snd program  ]
	M9       27C256     P14      5576   [  sound data  ]
	
	Note:  Game   No. KRB-14
	       PCB    No. 60138-0083
	
	Main processor  -  Custom security block (battery backed) CPU No. S562008
	
	Sound processor -  Z80
	                -  YM3812
	                -  AY-3-8910
	
	24 MHz crystal
	
	***************************************************************************/
	
	static RomLoadPtr rom_hardhead = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );/* Main Z80 Code */
		ROM_LOAD( "p1",  0x00000, 0x8000, 0xc6147926 );// 1988,9,14
		ROM_LOAD( "p2",  0x10000, 0x8000, 0xfaa2cf9a );
		ROM_LOAD( "p3",  0x18000, 0x8000, 0x3d24755e );
		ROM_LOAD( "p4",  0x20000, 0x8000, 0x0241ac79 );
		ROM_LOAD( "p7",  0x28000, 0x8000, 0xbeba8313 );
		ROM_LOAD( "p8",  0x30000, 0x8000, 0x211a9342 );
		ROM_LOAD( "p9",  0x38000, 0x8000, 0x2ad430c4 );
		ROM_LOAD( "p10", 0x40000, 0x8000, 0xb6894517 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Sound Z80 Code */
		ROM_LOAD( "p13", 0x0000, 0x8000, 0x493c0b41 );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "p5",  0x00000, 0x8000, 0xe9aa6fba );
		ROM_RELOAD(      0x08000, 0x8000             );
		ROM_LOAD( "p6",  0x10000, 0x8000, 0x15d5f5dd );
		ROM_RELOAD(      0x18000, 0x8000             );
		ROM_LOAD( "p11", 0x20000, 0x8000, 0x055f4c29 );
		ROM_RELOAD(      0x28000, 0x8000             );
		ROM_LOAD( "p12", 0x30000, 0x8000, 0x9582e6db );
		ROM_RELOAD(      0x38000, 0x8000             );
	
		ROM_REGION( 0x8000, REGION_SOUND1, ROMREGION_SOUNDONLY );/* Samples */
		ROM_LOAD( "p14", 0x0000, 0x8000, 0x41314ac1 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hardhedb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );/* Main Z80 Code */
		ROM_LOAD( "9_1_6l.rom", 0x00000, 0x8000, 0x750e6aee );// 1988,9,14 (already decrypted)
		ROM_LOAD( "p2",  0x10000, 0x8000, 0xfaa2cf9a );
		ROM_LOAD( "p3",  0x18000, 0x8000, 0x3d24755e );
		ROM_LOAD( "p4",  0x20000, 0x8000, 0x0241ac79 );
		ROM_LOAD( "p7",  0x28000, 0x8000, 0xbeba8313 );
		ROM_LOAD( "p8",  0x30000, 0x8000, 0x211a9342 );
		ROM_LOAD( "p9",  0x38000, 0x8000, 0x2ad430c4 );
		ROM_LOAD( "p10", 0x40000, 0x8000, 0xb6894517 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Sound Z80 Code */
		ROM_LOAD( "p13", 0x0000, 0x8000, 0x493c0b41 );
	//	ROM_LOAD( "2_13_9h.rom", 0x00000, 0x8000, 0x1b20e5ec );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "p5",  0x00000, 0x8000, 0xe9aa6fba );
		ROM_RELOAD(      0x08000, 0x8000             );
		ROM_LOAD( "p6",  0x10000, 0x8000, 0x15d5f5dd );
		ROM_RELOAD(      0x18000, 0x8000             );
		ROM_LOAD( "p11", 0x20000, 0x8000, 0x055f4c29 );
		ROM_RELOAD(      0x28000, 0x8000             );
		ROM_LOAD( "p12", 0x30000, 0x8000, 0x9582e6db );
		ROM_RELOAD(      0x38000, 0x8000             );
	
		ROM_REGION( 0x8000, REGION_SOUND1, ROMREGION_SOUNDONLY );/* Samples */
		ROM_LOAD( "p14", 0x0000, 0x8000, 0x41314ac1 );
	ROM_END(); }}; 
	
	
	/***************************************************************************
	
								Rough Ranger / Super Ranger
	
	(SunA 1988)
	K030087
	
	 24MHz    6  7  8  9  - 10 11 12 13   sw1  sw2
	
	
	
	   6264
	   5    6116
	   4    6116                         6116
	   3    6116                         14
	   2    6116                         Z80A
	   1                        6116     8910
	                 6116  6116          2203
	                                     15
	 Epoxy CPU
	                            6116
	
	
	---------------------------
	Super Ranger by SUNA (1988)
	---------------------------
	
	Location   Type    File ID  Checksum
	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	E2        27C256     R1      28C0    [ main program ]
	F2        27C256     R2      73AD    [ main program ]
	H2        27C256     R3      8B7A    [ main program ]
	I2        27C512     R4      77BE    [ main program ]
	J2        27C512     R5      6121    [ main program ]
	P5        27C256     R6      BE0E    [  background  ]
	P6        27C256     R7      BD5A    [  background  ]
	P7        27C256     R8      4605    [ motion obj.  ]
	P8        27C256     R9      7097    [ motion obj.  ]
	P9        27C256     R10     3B9F    [  background  ]
	P10       27C256     R11     2AE8    [  background  ]
	P11       27C256     R12     8B6D    [ motion obj.  ]
	P12       27C256     R13     927E    [ motion obj.  ]
	J13       27C256     R14     E817    [ snd program  ]
	E13       27C256     R15     54EE    [ sound data   ]
	
	Note:  Game model number K030087
	
	Hardware:
	
	Main processor  -  Custom security block (battery backed)  CPU No. S562008
	
	Sound processor - Z80
	                - YM2203C
	                - AY-3-8910
	
	***************************************************************************/
	
	static RomLoadPtr rom_rranger = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );	/* Main Z80 Code */
		ROM_LOAD( "1",  0x00000, 0x8000, 0x4fb4f096 );// V 2.0 1988,4,15
		ROM_LOAD( "2",  0x10000, 0x8000, 0xff65af29 );
		ROM_LOAD( "3",  0x18000, 0x8000, 0x64e09436 );
		ROM_LOAD( "r4", 0x30000, 0x8000, 0x4346fae6 );
		ROM_CONTINUE(   0x20000, 0x8000             );
		ROM_LOAD( "r5", 0x38000, 0x8000, 0x6a7ca1c3 );
		ROM_CONTINUE(   0x28000, 0x8000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Sound Z80 Code */
		ROM_LOAD( "14", 0x0000, 0x8000, 0x11c83aa1 );
	
		ROM_REGION( 0x8000, REGION_SOUND1, ROMREGION_SOUNDONLY );/* Samples */
		ROM_LOAD( "15", 0x0000, 0x8000, 0x28c2c87e );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "6",  0x00000, 0x8000, 0x57543643 );
		ROM_LOAD( "7",  0x08000, 0x8000, 0x9f35dbfa );
		ROM_LOAD( "8",  0x10000, 0x8000, 0xf400db89 );
		ROM_LOAD( "9",  0x18000, 0x8000, 0xfa2a11ea );
		ROM_LOAD( "10", 0x20000, 0x8000, 0x42c4fdbf );
		ROM_LOAD( "11", 0x28000, 0x8000, 0x19037a7b );
		ROM_LOAD( "12", 0x30000, 0x8000, 0xc59c0ec7 );
		ROM_LOAD( "13", 0x38000, 0x8000, 0x9809fee8 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_sranger = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );	/* Main Z80 Code */
		ROM_LOAD( "r1", 0x00000, 0x8000, 0x4eef1ede );// V 2.0 1988,4,15
		ROM_LOAD( "2",  0x10000, 0x8000, 0xff65af29 );
		ROM_LOAD( "3",  0x18000, 0x8000, 0x64e09436 );
		ROM_LOAD( "r4", 0x30000, 0x8000, 0x4346fae6 );
		ROM_CONTINUE(   0x20000, 0x8000             );
		ROM_LOAD( "r5", 0x38000, 0x8000, 0x6a7ca1c3 );
		ROM_CONTINUE(   0x28000, 0x8000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Sound Z80 Code */
		ROM_LOAD( "14", 0x0000, 0x8000, 0x11c83aa1 );
	
		ROM_REGION( 0x8000, REGION_SOUND1, ROMREGION_SOUNDONLY );/* Samples */
		ROM_LOAD( "15", 0x0000, 0x8000, 0x28c2c87e );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "r6",  0x00000, 0x8000, 0x4f11fef3 );
		ROM_LOAD( "7",   0x08000, 0x8000, 0x9f35dbfa );
		ROM_LOAD( "8",   0x10000, 0x8000, 0xf400db89 );
		ROM_LOAD( "9",   0x18000, 0x8000, 0xfa2a11ea );
		ROM_LOAD( "r10", 0x20000, 0x8000, 0x1b204d6b );
		ROM_LOAD( "11",  0x28000, 0x8000, 0x19037a7b );
		ROM_LOAD( "12",  0x30000, 0x8000, 0xc59c0ec7 );
		ROM_LOAD( "13",  0x38000, 0x8000, 0x9809fee8 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_srangerb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );	/* Main Z80 Code */
		ROM_LOAD( "r1bt", 0x00000, 0x8000, 0x40635e7c );// NYWACORPORATION LTD 88-1-07
		ROM_LOAD( "2",    0x10000, 0x8000, 0xff65af29 );
		ROM_LOAD( "3",    0x18000, 0x8000, 0x64e09436 );
		ROM_LOAD( "r4",   0x30000, 0x8000, 0x4346fae6 );
		ROM_CONTINUE(     0x20000, 0x8000             );
		ROM_LOAD( "r5",   0x38000, 0x8000, 0x6a7ca1c3 );
		ROM_CONTINUE(     0x28000, 0x8000             );
		ROM_LOAD( "r5bt", 0x28000, 0x8000, BADCRC(0xf7f391b5));	// wrong length
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Sound Z80 Code */
		ROM_LOAD( "14", 0x0000, 0x8000, 0x11c83aa1 );
	
		ROM_REGION( 0x8000, REGION_SOUND1, ROMREGION_SOUNDONLY );/* Samples */
		ROM_LOAD( "15", 0x0000, 0x8000, 0x28c2c87e );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "r6",  0x00000, 0x8000, 0x4f11fef3 );
		ROM_LOAD( "7",   0x08000, 0x8000, 0x9f35dbfa );
		ROM_LOAD( "8",   0x10000, 0x8000, 0xf400db89 );
		ROM_LOAD( "9",   0x18000, 0x8000, 0xfa2a11ea );
		ROM_LOAD( "r10", 0x20000, 0x8000, 0x1b204d6b );
		ROM_LOAD( "11",  0x28000, 0x8000, 0x19037a7b );
		ROM_LOAD( "12",  0x30000, 0x8000, 0xc59c0ec7 );
		ROM_LOAD( "13",  0x38000, 0x8000, 0x9809fee8 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_srangerw = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x48000, REGION_CPU1, 0 );	/* Main Z80 Code */
		ROM_LOAD( "w1", 0x00000, 0x8000, 0x2287d3fc );// 88,2,28
		ROM_LOAD( "2",  0x10000, 0x8000, 0xff65af29 );
		ROM_LOAD( "3",  0x18000, 0x8000, 0x64e09436 );
		ROM_LOAD( "r4", 0x30000, 0x8000, 0x4346fae6 );
		ROM_CONTINUE(   0x20000, 0x8000             );
		ROM_LOAD( "r5", 0x38000, 0x8000, 0x6a7ca1c3 );
		ROM_CONTINUE(   0x28000, 0x8000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Sound Z80 Code */
		ROM_LOAD( "14", 0x0000, 0x8000, 0x11c83aa1 );
	
		ROM_REGION( 0x8000, REGION_SOUND1, ROMREGION_SOUNDONLY );/* Samples */
		ROM_LOAD( "15", 0x0000, 0x8000, 0x28c2c87e );
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "w6",  0x00000, 0x8000, 0x312ecda6 );
		ROM_LOAD( "7",   0x08000, 0x8000, 0x9f35dbfa );
		ROM_LOAD( "8",   0x10000, 0x8000, 0xf400db89 );
		ROM_LOAD( "9",   0x18000, 0x8000, 0xfa2a11ea );
		ROM_LOAD( "w10", 0x20000, 0x8000, 0x8731abc6 );
		ROM_LOAD( "11",  0x28000, 0x8000, 0x19037a7b );
		ROM_LOAD( "12",  0x30000, 0x8000, 0xc59c0ec7 );
		ROM_LOAD( "13",  0x38000, 0x8000, 0x9809fee8 );
	ROM_END(); }}; 
	
	
	/***************************************************************************
	
										Brick Zone
	
	SUNA ELECTRONICS IND CO., LTD
	
	CPU Z0840006PSC (ZILOG)
	
	Chrystal : 24.000 MHz
	
	Sound CPU : Z084006PSC (ZILOG) + AY3-8910A
	
	Warning ! This game has a 'SUNA' protection block :-(
	
	-
	
	(c) 1992 Suna Electronics
	
	2 * Z80B
	
	AY-3-8910
	YM3812
	
	24 MHz crystal
	
	Large epoxy(?) module near the cpu's.
	
	***************************************************************************/
	
	static RomLoadPtr rom_brickzn = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000 * 2, REGION_CPU1, 0 );	/* Main Z80 Code */
		ROM_LOAD( "brickzon.009", 0x00000, 0x08000, 0x1ea68dea );// V5.0 1992,3,3
		ROM_RELOAD(               0x50000, 0x08000             );
		ROM_LOAD( "brickzon.008", 0x10000, 0x20000, 0xc61540ba );
		ROM_RELOAD(               0x60000, 0x20000             );
		ROM_LOAD( "brickzon.007", 0x30000, 0x20000, 0xceed12f1 );
		ROM_RELOAD(               0x80000, 0x20000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Music Z80 Code */
		ROM_LOAD( "brickzon.010", 0x00000, 0x10000, 0x4eba8178 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );	/* PCM Z80 Code */
		ROM_LOAD( "brickzon.011", 0x00000, 0x10000, 0x6c54161a );
	
		ROM_REGION( 0xc0000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "brickzon.002", 0x00000, 0x20000, 0x241f0659 );
		ROM_LOAD( "brickzon.001", 0x20000, 0x20000, 0x6970ada9 );
		ROM_LOAD( "brickzon.003", 0x40000, 0x20000, 0x2e4f194b );
		ROM_LOAD( "brickzon.005", 0x60000, 0x20000, 0x118f8392 );
		ROM_LOAD( "brickzon.004", 0x80000, 0x20000, 0x2be5f335 );
		ROM_LOAD( "brickzon.006", 0xa0000, 0x20000, 0xbbf31081 );
	
		ROM_REGION( 0x0200 * 2, REGION_USER1, 0 );/* Palette RAM Banks */
		ROM_REGION( 0x2000 * 2, REGION_USER2, 0 );/* Sprite  RAM Banks */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_brickzn3 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000 * 2, REGION_CPU1, 0 );	/* Main Z80 Code */
		ROM_LOAD( "39",           0x00000, 0x08000, 0x043380bd );// V3.0 1992,1,23
		ROM_RELOAD(               0x50000, 0x08000             );
		ROM_LOAD( "38",           0x10000, 0x20000, 0xe16216e8 );
		ROM_RELOAD(               0x60000, 0x20000             );
		ROM_LOAD( "brickzon.007", 0x30000, 0x20000, 0xceed12f1 );
		ROM_RELOAD(               0x80000, 0x20000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Music Z80 Code */
		ROM_LOAD( "brickzon.010", 0x00000, 0x10000, 0x4eba8178 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );	/* PCM Z80 Code */
		ROM_LOAD( "brickzon.011", 0x00000, 0x10000, 0x6c54161a );
	
		ROM_REGION( 0xc0000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "35",           0x00000, 0x20000, 0xb463dfcf );
		ROM_LOAD( "brickzon.004", 0x20000, 0x20000, 0x2be5f335 );
		ROM_LOAD( "brickzon.006", 0x40000, 0x20000, 0xbbf31081 );
		ROM_LOAD( "32",           0x60000, 0x20000, 0x32dbf2dd );
		ROM_LOAD( "brickzon.001", 0x80000, 0x20000, 0x6970ada9 );
		ROM_LOAD( "brickzon.003", 0xa0000, 0x20000, 0x2e4f194b );
	
		ROM_REGION( 0x0200 * 2, REGION_USER1, 0 );/* Palette RAM Banks */
		ROM_REGION( 0x2000 * 2, REGION_USER2, 0 );/* Sprite  RAM Banks */
	ROM_END(); }}; 
	
	
	
	/***************************************************************************
	
									Hard Head 2
	
	These ROMS are all 27C512
	
	ROM 1 is at Location 1N
	ROM 2 ..............1o
	ROM 3 ..............1Q
	ROM 4...............3N
	ROM 5.............. 4N
	ROM 6...............4o
	ROM 7...............4Q
	ROM 8...............6N
	ROM 10..............H5
	ROM 11..............i5
	ROM 12 .............F7
	ROM 13..............H7
	ROM 15..............N10
	
	These ROMs are 27C256
	
	ROM 9...............F5
	ROM 14..............C8
	
	Game uses 2 Z80B processors and a Custom Sealed processor (assumed)
	Labeled "SUNA T568009"
	
	Sound is a Yamaha YM3812 and a  AY-3-8910A
	
	3 RAMS are 6264LP- 10   and 5) HM6116K-90 rams  (small package)
	
	24 MHz
	
	***************************************************************************/
	
	static RomLoadPtr rom_hardhea2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000 * 2, REGION_CPU1, 0 );	/* Main Z80 Code */
		ROM_LOAD( "hrd-hd9",  0x00000, 0x08000, 0x69c4c307 );// V 2.0 1991,2,12
		ROM_RELOAD(           0x50000, 0x08000             );
		ROM_LOAD( "hrd-hd10", 0x10000, 0x10000, 0x77ec5b0a );
		ROM_RELOAD(           0x60000, 0x10000             );
		ROM_LOAD( "hrd-hd11", 0x20000, 0x10000, 0x12af8f8e );
		ROM_RELOAD(           0x70000, 0x10000             );
		ROM_LOAD( "hrd-hd12", 0x30000, 0x10000, 0x35d13212 );
		ROM_RELOAD(           0x80000, 0x10000             );
		ROM_LOAD( "hrd-hd13", 0x40000, 0x10000, 0x3225e7d7 );
		ROM_RELOAD(           0x90000, 0x10000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Music Z80 Code */
		ROM_LOAD( "hrd-hd14", 0x00000, 0x08000, 0x79a3be51 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );	/* PCM Z80 Code */
		ROM_LOAD( "hrd-hd15", 0x00000, 0x10000, 0xbcbd88c3 );
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "hrd-hd1",  0x00000, 0x10000, 0x7e7b7a58 );
		ROM_LOAD( "hrd-hd2",  0x10000, 0x10000, 0x303ec802 );
		ROM_LOAD( "hrd-hd3",  0x20000, 0x10000, 0x3353b2c7 );
		ROM_LOAD( "hrd-hd4",  0x30000, 0x10000, 0xdbc1f9c1 );
		ROM_LOAD( "hrd-hd5",  0x40000, 0x10000, 0xf738c0af );
		ROM_LOAD( "hrd-hd6",  0x50000, 0x10000, 0xbf90d3ca );
		ROM_LOAD( "hrd-hd7",  0x60000, 0x10000, 0x992ce8cb );
		ROM_LOAD( "hrd-hd8",  0x70000, 0x10000, 0x359597a4 );
	
		ROM_REGION( 0x0200 * 2, REGION_USER1, 0 );/* Palette RAM Banks */
		ROM_REGION( 0x2000 * 2, REGION_USER2, 0 );/* Sprite  RAM Banks */
	ROM_END(); }}; 
	
	
	/***************************************************************************
	
									Star Fighter
	
	***************************************************************************/
	
	static RomLoadPtr rom_starfigh = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000 * 2, REGION_CPU1, 0 );	/* Main Z80 Code */
		ROM_LOAD( "starfgtr.l1", 0x00000, 0x08000, 0xf93802c6 );// V.1
		ROM_RELOAD(              0x50000, 0x08000             );
		ROM_LOAD( "starfgtr.j1", 0x10000, 0x10000, 0xfcfcf08a );
		ROM_RELOAD(              0x60000, 0x10000             );
		ROM_LOAD( "starfgtr.i1", 0x20000, 0x10000, 0x6935fcdb );
		ROM_RELOAD(              0x70000, 0x10000             );
		ROM_LOAD( "starfgtr.l3", 0x30000, 0x10000, 0x50c072a4 );// 0xxxxxxxxxxxxxxx = 0xFF (ROM Test: OK)
		ROM_RELOAD(              0x80000, 0x10000             );
		ROM_LOAD( "starfgtr.j3", 0x40000, 0x10000, 0x3fe3c714 );// clear text here
		ROM_RELOAD(              0x90000, 0x10000             );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );	/* Music Z80 Code */
		ROM_LOAD( "starfgtr.m8", 0x0000, 0x8000, 0xae3b0691 );
	
		ROM_REGION( 0x8000, REGION_SOUND1, ROMREGION_SOUNDONLY );/* Samples */
		ROM_LOAD( "starfgtr.q10", 0x0000, 0x8000, 0xfa510e94 );
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );/* Sprites */
		ROM_LOAD( "starfgtr.e4", 0x00000, 0x10000, 0x54c0ca3d );
		ROM_RELOAD(              0x20000, 0x10000             );
		ROM_LOAD( "starfgtr.d4", 0x10000, 0x10000, 0x4313ba40 );
		ROM_RELOAD(              0x30000, 0x10000             );
		ROM_LOAD( "starfgtr.b4", 0x40000, 0x10000, 0xad8d0f21 );
		ROM_RELOAD(              0x60000, 0x10000             );
		ROM_LOAD( "starfgtr.a4", 0x50000, 0x10000, 0x6d8f74c8 );
		ROM_RELOAD(              0x70000, 0x10000             );
		ROM_LOAD( "starfgtr.e6", 0x80000, 0x10000, 0xceff00ff );
		ROM_RELOAD(              0xa0000, 0x10000             );
		ROM_LOAD( "starfgtr.d6", 0x90000, 0x10000, 0x7aaa358a );
		ROM_RELOAD(              0xb0000, 0x10000             );
		ROM_LOAD( "starfgtr.b6", 0xc0000, 0x10000, 0x47d6049c );
		ROM_RELOAD(              0xe0000, 0x10000             );
		ROM_LOAD( "starfgtr.a6", 0xd0000, 0x10000, 0x4a33f6f3 );
		ROM_RELOAD(              0xf0000, 0x10000             );
	
		ROM_REGION( 0x0200 * 2, REGION_USER1, 0 );/* Palette RAM Banks */
		ROM_REGION( 0x2000 * 2, REGION_USER2, 0 );/* Sprite  RAM Banks */
	ROM_END(); }}; 
	
	
	/***************************************************************************
	
	
									Games Drivers
	
	
	***************************************************************************/
	
	/* Working Games */
	public static GameDriver driver_rranger	   = new GameDriver("1988"	,"rranger"	,"suna8.java"	,rom_rranger,null	,machine_driver_rranger	,input_ports_rranger	,null	,ROT0	,	"SunA (Sharp Image license)", "Rough Ranger (v2.0)", GAME_IMPERFECT_SOUND )
	public static GameDriver driver_hardhead	   = new GameDriver("1988"	,"hardhead"	,"suna8.java"	,rom_hardhead,null	,machine_driver_hardhead	,input_ports_hardhead	,init_hardhead	,ROT0	,	"SunA", "Hard Head",           GAME_IMPERFECT_SOUND )
	public static GameDriver driver_hardhedb	   = new GameDriver("1988"	,"hardhedb"	,"suna8.java"	,rom_hardhedb,driver_hardhead	,machine_driver_hardhead	,input_ports_hardhead	,init_hardhedb	,ROT0	,	"bootleg", "Hard Head (bootleg)", GAME_IMPERFECT_SOUND )
	
	/* Non Working Games */
	public static GameDriver driver_sranger	   = new GameDriver("1988"	,"sranger"	,"suna8.java"	,rom_sranger,driver_rranger	,machine_driver_rranger	,input_ports_rranger	,null	,ROT0	,	"SunA", "Super Ranger (v2.0)",    GAME_NOT_WORKING )
	public static GameDriver driver_srangerb	   = new GameDriver("1988"	,"srangerb"	,"suna8.java"	,rom_srangerb,driver_rranger	,machine_driver_rranger	,input_ports_rranger	,null	,ROT0	,	"bootleg", "Super Ranger (bootleg)", GAME_NOT_WORKING )
	public static GameDriver driver_srangerw	   = new GameDriver("1988"	,"srangerw"	,"suna8.java"	,rom_srangerw,driver_rranger	,machine_driver_rranger	,input_ports_rranger	,null	,ROT0	,	"SunA (WDK license)", "Super Ranger (WDK)",  GAME_NOT_WORKING )
	public static GameDriver driver_starfigh	   = new GameDriver("1990"	,"starfigh"	,"suna8.java"	,rom_starfigh,null	,machine_driver_starfigh	,input_ports_hardhea2	,init_starfigh	,ROT90	,	"SunA", "Star Fighter (v1)",   GAME_NOT_WORKING )
	public static GameDriver driver_hardhea2	   = new GameDriver("1991"	,"hardhea2"	,"suna8.java"	,rom_hardhea2,null	,machine_driver_hardhea2	,input_ports_hardhea2	,init_hardhea2	,ROT0	,	"SunA", "Hard Head 2 (v2.0)",  GAME_NOT_WORKING )
	public static GameDriver driver_brickzn	   = new GameDriver("1992"	,"brickzn"	,"suna8.java"	,rom_brickzn,null	,machine_driver_brickzn	,input_ports_brickzn	,init_brickzn3	,ROT90	,	"SunA", "Brick Zone (v5.0)",   GAME_NOT_WORKING )
	public static GameDriver driver_brickzn3	   = new GameDriver("1992"	,"brickzn3"	,"suna8.java"	,rom_brickzn3,driver_brickzn	,machine_driver_brickzn	,input_ports_brickzn	,init_brickzn3	,ROT90	,	"SunA", "Brick Zone (v3.0)",   GAME_NOT_WORKING )
}
