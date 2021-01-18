/***************************************************************************

  machine.c

  Functions to emulate general aspects of the machine (RAM, ROM, interrupts,
  I/O ports)

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package machine;

public class galaga
{
	
	
	unsigned char *galaga_sharedram;
	static unsigned char interrupt_enable_1,interrupt_enable_2,interrupt_enable_3;
	
	static void *nmi_timer;
	
	
	void galaga_nmi_generate (int param);
	
	MACHINE_INIT( galaga )
	{
		nmi_timer = timer_alloc(galaga_nmi_generate);
		galaga_halt_w (0, 0);
	}
	
	
	
	public static ReadHandlerPtr galaga_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return galaga_sharedram[offset];
	} };
	
	
	
	public static WriteHandlerPtr galaga_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset < 0x800)		/* write to video RAM */
			dirtybuffer[offset & 0x3ff] = 1;
	
		galaga_sharedram[offset] = data;
	} };
	
	
	
	public static ReadHandlerPtr galaga_dsw_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int bit0,bit1;
	
	
		bit0 = (input_port_0_r(0) >> offset) & 1;
		bit1 = (input_port_1_r(0) >> offset) & 1;
	
		return bit0 | (bit1 << 1);
	} };
	
	
	
	/***************************************************************************
	
	 Emulate the custom IO chip.
	
	***************************************************************************/
	static int customio_command;
	static int mode,credits,start_enable;
	static int coinpercred,credpercoin;
	static unsigned char customio[16];
	
	
	public static WriteHandlerPtr galaga_customio_data_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		customio[offset] = data;
	
	logerror("%04x: custom IO offset %02x data %02x\n",activecpu_get_pc(),offset,data);
	
		switch (customio_command & 0x0f)
		{
			case 0x01:
				if (offset == 0)
				{
					switch ( data & 0x0f )
					{
						case 0x00:
							/* nop */
							break;
						case 0x01:
							/* credit info set */
							credits = 0;		/* this is a good time to reset the credits counter */
							mode = 0;			/* go into credit mode */
							start_enable = 1;
							break;
						case 0x02:
							start_enable = 1;
							break;
						case 0x03:
							mode = 1;	/* go into switch mode */
							break;
						case 0x04:
							mode = 0;	/* go into credit mode */
							break;
						case 0x05:
							start_enable = 0;	/* Initialize */
							mode = 1;			/* Initialize */
							break;
					}
				}
				else if (offset == 7)
				{
					/* 0x01 */
					coinpercred = customio[1];
					credpercoin = customio[2];
				}
				break;
	
			case 0x04:
				/* ??? */
				break;
	
			case 0x08:
				if (offset == 3 && data == 0x20)	/* total hack */
				{
					sample_start(0,0,0);
				}
				else{
					sample_start(0,1,0);
				}
				break;
		}
	} };
	
	
	public static ReadHandlerPtr galaga_customio_data_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (customio_command != 0x71)
			logerror("%04x: custom IO read offset %02x\n",activecpu_get_pc(),offset);
	
		switch (customio_command & 0x0f)
		{
			case 0x01:	/* read input */
				if (offset == 0)
				{
					if (mode)	/* switch mode */
					{
						/* bit 7 is the service switch */
						return readinputport(4);
					}
					else	/* credits mode: return number of credits in BCD format */
					{
						int in;
						static int coininserted;
	
						in = readinputport(4);
	
						/* check if the user inserted a coin */
						if (coinpercred > 0)
						{
							if ((in & 0x70) != 0x70 && credits < 99)
							{
								coininserted++;
								if (coininserted >= coinpercred)
								{
									credits += credpercoin;
									coininserted = 0;
								}
							}
						}
						else credits = 100;	/* free play */
	
	
						if (start_enable == 1)
						{
							/* check for 1 player start button */
							if ((in & 0x04) == 0)
							{
								if (credits >= 1){
									credits--;
									start_enable = 0;
								}
							}
	
							/* check for 2 players start button */
							if ((in & 0x08) == 0)
							{
								if (credits >= 2){
									credits -= 2;
									start_enable = 0;
								}
							}
						}
						return (credits / 10) * 16 + credits % 10;
					}
				}
				else if (offset == 1)
					return readinputport(2);	/* player 1 input */
				else if (offset == 2)
					return readinputport(3);	/* player 2 input */
	
				break;
		}
	
		return -1;
	} };
	
	
	public static ReadHandlerPtr galaga_customio_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return customio_command;
	} };
	
	
	void galaga_nmi_generate (int param)
	{
		cpu_set_irq_line (0, IRQ_LINE_NMI, PULSE_LINE);
	}
	
	
	public static WriteHandlerPtr galaga_customio_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (data != 0x10 && data != 0x71)
			logerror("%04x: custom IO command %02x\n",activecpu_get_pc(),data);
	
		customio_command = data;
	
		switch (data)
		{
			case 0x10:
				timer_adjust(nmi_timer, TIME_NEVER, 0, 0);
				return;
		}
	
		timer_adjust(nmi_timer, TIME_IN_USEC(50), 0, TIME_IN_USEC(50));
	} };
	
	
	
	public static WriteHandlerPtr galaga_halt_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (data & 1)
		{
			cpu_set_reset_line(1,CLEAR_LINE);
			cpu_set_reset_line(2,CLEAR_LINE);
		}
		else if (data == 0)
		{
			cpu_set_reset_line(1,ASSERT_LINE);
			cpu_set_reset_line(2,ASSERT_LINE);
		}
	} };
	
	
	
	public static WriteHandlerPtr galaga_interrupt_enable_1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		interrupt_enable_1 = data & 1;
	} };
	
	
	
	INTERRUPT_GEN( galaga_interrupt_1 )
	{
		galaga_vh_interrupt();	/* update the background stars position */
	
		if (interrupt_enable_1)
			cpu_set_irq_line(0, 0, HOLD_LINE);
	}
	
	
	
	public static WriteHandlerPtr galaga_interrupt_enable_2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		interrupt_enable_2 = data & 1;
	} };
	
	
	
	INTERRUPT_GEN( galaga_interrupt_2 )
	{
		if (interrupt_enable_2)
			cpu_set_irq_line(1, 0, HOLD_LINE);
	}
	
	
	
	public static WriteHandlerPtr galaga_interrupt_enable_3_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		interrupt_enable_3 = !(data & 1);
	} };
	
	
	
	INTERRUPT_GEN( galaga_interrupt_3 )
	{
		if (interrupt_enable_3)
			cpu_set_irq_line(2, IRQ_LINE_NMI, PULSE_LINE);
	}
}
