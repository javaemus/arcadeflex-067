/***************************************************************************

	Centuri Aztarac hardware
	
***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class aztarac
{
	
	static int sound_status;
	
	READ16_HANDLER( aztarac_sound_r )
	{
	    if (Machine->sample_rate)
	        return sound_status & 0x01;
	    else
	        return 1;
	}
	
	WRITE16_HANDLER( aztarac_sound_w )
	{
		if (ACCESSING_LSB)
		{
			data &= 0xff;
			soundlatch_w(offset, data);
			sound_status ^= 0x21;
			if (sound_status & 0x20)
				cpu_set_irq_line(1, 0, HOLD_LINE);
		}
	}
	
	public static ReadHandlerPtr aztarac_snd_command_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    sound_status |= 0x01;
	    sound_status &= ~0x20;
	    return soundlatch_r(offset);
	} };
	
	public static ReadHandlerPtr aztarac_snd_status_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return sound_status & ~0x01;
	} };
	
	public static WriteHandlerPtr aztarac_snd_status_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    sound_status &= ~0x10;
	} };
	
	INTERRUPT_GEN( aztarac_snd_timed_irq )
	{
	    sound_status ^= 0x10;
	
	    if (sound_status & 0x10)
	        cpu_set_irq_line(1,0,HOLD_LINE);
	}
	
	
}
