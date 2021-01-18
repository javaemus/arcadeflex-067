/***************************************************************************

	Kyugo hardware games

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package machine;

public class kyugo
{
	
	
	data8_t *kyugo_sharedram;
	
	
	/*************************************
	 *
	 *	Machine initialization
	 *
	 *************************************/
	
	MACHINE_INIT( kyugo )
	{
		// must start with interrupts and sub CPU disabled
		cpu_interrupt_enable(0, 0);
		kyugo_sub_cpu_control_w(0, 0);
	}
	
	
	public static WriteHandlerPtr kyugo_sub_cpu_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_set_halt_line(1, data ? CLEAR_LINE : ASSERT_LINE);
	} };
	
	
	/*************************************
	 *
	 *	Shared RAM handlers
	 *
	 *************************************/
	
	public static WriteHandlerPtr kyugo_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		kyugo_sharedram[offset] = data;
	} };
	
	
	public static ReadHandlerPtr kyugo_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return kyugo_sharedram[offset];
	} };
}
