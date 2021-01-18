/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package machine;

public class leprechn
{
	
	
	static data8_t input_port_select;
	
	public static WriteHandlerPtr leprechn_input_port_select_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    input_port_select = data;
	} };
	
	public static ReadHandlerPtr leprechn_input_port_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    switch (input_port_select)
	    {
	    case 0x01:
	        return input_port_0_r(0);
	    case 0x02:
	        return input_port_2_r(0);
	    case 0x04:
	        return input_port_3_r(0);
	    case 0x08:
	        return input_port_1_r(0);
	    case 0x40:
	        return input_port_5_r(0);
	    case 0x80:
	        return input_port_4_r(0);
	    }
	
	    return 0xff;
	} };
	
	
	public static WriteHandlerPtr leprechn_coin_counter_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_counter_w(offset, !data);
	} };
	
	
	public static WriteHandlerPtr leprechn_sh_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    soundlatch_w(offset,data);
	    cpu_set_irq_line(1,M6502_IRQ_LINE,HOLD_LINE);
	} };
	
	
	
	static struct via6522_interface leprechn_via_0_interface =
	{
		/*inputs : A/B         */ 0, leprechn_videoram_r,
		/*inputs : CA/B1,CA/B2 */ 0, 0, 0, 0,
		/*outputs: A/B,CA/B2   */ leprechn_videoram_w, leprechn_graphics_command_w, 0, 0,
		/*irq                  */ 0
	};
	
	static struct via6522_interface leprechn_via_1_interface =
	{
		/*inputs : A/B         */ leprechn_input_port_r, 0,
		/*inputs : CA/B1,CA/B2 */ 0, 0, 0, 0,
		/*outputs: A/B,CA/B2   */ 0, leprechn_input_port_select_w, 0, leprechn_coin_counter_w,
		/*irq                  */ 0
	};
	
	static struct via6522_interface leprechn_via_2_interface =
	{
		/*inputs : A/B         */ 0, 0,
		/*inputs : CA/B1,CA/B2 */ 0, 0, 0, 0,
		/*outputs: A/B,CA/B2   */ leprechn_sh_w, 0, 0, 0,
		/*irq                  */ 0
	};
	
	
	DRIVER_INIT( leprechn )
	{
		via_config(0, &leprechn_via_0_interface);
		via_config(1, &leprechn_via_1_interface);
		via_config(2, &leprechn_via_2_interface);
	
		via_reset();
	}
	
	
	public static ReadHandlerPtr leprechn_sh_0805_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return 0xc0;
	} };
}
