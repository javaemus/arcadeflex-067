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

public class berzerk
{
	
	
	static int irq_enabled;
	static int nmi_enabled;
	static int int_count;
	
	
	MACHINE_INIT( berzerk )
	{
		memory_set_unmap_value(0xff);
	
		irq_enabled = 0;
		nmi_enabled = 0;
		int_count = 0;
	}
	
	
	public static WriteHandlerPtr berzerk_irq_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		irq_enabled = data;
	} };
	
	public static WriteHandlerPtr berzerk_nmi_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		nmi_enabled = 1;
	} };
	
	public static WriteHandlerPtr berzerk_nmi_disable_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		nmi_enabled = 0;
	} };
	
	public static ReadHandlerPtr berzerk_nmi_enable_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		nmi_enabled = 1;
		return 0;
	} };
	
	public static ReadHandlerPtr berzerk_nmi_disable_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		nmi_enabled = 0;
		return 0;
	} };
	
	public static ReadHandlerPtr berzerk_led_on_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		set_led_status(0,1);
	
		return 0;
	} };
	
	public static ReadHandlerPtr berzerk_led_off_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		set_led_status(0,0);
	
		return 0;
	} };
	
	INTERRUPT_GEN( berzerk_interrupt )
	{
		int_count++;
	
		if (int_count & 0x03)
		{
			if (nmi_enabled) cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
		}
		else
		{
			if (irq_enabled) cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0xfc);
		}
	}
	
}
