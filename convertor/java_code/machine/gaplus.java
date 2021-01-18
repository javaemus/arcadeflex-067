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

public class gaplus
{
	
	unsigned char *gaplus_snd_sharedram;
	unsigned char *gaplus_sharedram;
	unsigned char *gaplus_customio_1,*gaplus_customio_2,*gaplus_customio_3;
	static int int_enable_2, int_enable_3;
	static int credits, coincounter1, coincounter2;
	
	
	MACHINE_INIT( gaplus )
	{
	    int_enable_2 = int_enable_3 = 1;
	    credits = coincounter1 = coincounter2 = 0;
	}
	
	/* shared ram functions */
	public static ReadHandlerPtr gaplus_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return gaplus_sharedram[offset];
	} };
	
	public static WriteHandlerPtr gaplus_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset == 0x082c)	/* 0x102c */
			flip_screen_set(data);
	    gaplus_sharedram[offset] = data;
	} };
	
	public static ReadHandlerPtr gaplus_snd_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return gaplus_snd_sharedram[offset];
	} };
	
	public static WriteHandlerPtr gaplus_snd_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    gaplus_snd_sharedram[offset] = data;
	} };
	
	/* irq control functions */
	public static WriteHandlerPtr gaplus_interrupt_ctrl_2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    int_enable_2 = offset;
	} };
	
	public static WriteHandlerPtr gaplus_interrupt_ctrl_3a_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    int_enable_3 = 1;
	} };
	
	public static WriteHandlerPtr gaplus_interrupt_ctrl_3b_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    int_enable_3 = 0;
	} };
	
	INTERRUPT_GEN( gaplus_interrupt_1 ) {
	
		gaplus_starfield_update(); /* update starfields */
		cpu_set_irq_line(0, 0, HOLD_LINE);
	}
	
	INTERRUPT_GEN( gaplus_interrupt_2 )
	{
	    if (int_enable_2)
	    	cpu_set_irq_line(1, 0, HOLD_LINE);
	}
	
	INTERRUPT_GEN( gaplus_interrupt_3 )
	{
	    if (int_enable_3)
	    	cpu_set_irq_line(2, 0, HOLD_LINE);
	}
	
	public static WriteHandlerPtr gaplus_reset_2_3_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    int_enable_2 = int_enable_3 = 1;
	    cpu_set_reset_line(1,PULSE_LINE);
	    cpu_set_reset_line(2,PULSE_LINE);
	    credits = coincounter1 = coincounter2 = 0;
	} };
	
	/************************************************************************************
	*																					*
	*           Gaplus custom I/O chips (preliminary)									*
	*																					*
	************************************************************************************/
	
	public static WriteHandlerPtr gaplus_customio_1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    gaplus_customio_1[offset] = data;
	} };
	
	public static WriteHandlerPtr gaplus_customio_2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    gaplus_customio_2[offset] = data;
	} };
	
	public static WriteHandlerPtr gaplus_customio_3_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if ((offset == 0x09) && (data >= 0x0f))
			sample_start(0,0,0);
	    gaplus_customio_3[offset] = data;
	} };
	
	static int credmoned [] = { 1, 1, 2, 3 };
	static int monedcred [] = { 1, 2, 1, 1 };
	
	public static ReadHandlerPtr gaplus_customio_1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int mode;
	
	    mode = gaplus_customio_1[8];
	    if (mode == 1)  /* normal mode & test mode */
	    {
	        switch (offset)
	        {
				case 0:
					return (readinputport( 2 ) >> 4);	/* coin 1 & 2 */
					break;
	            case 1:
	                return (readinputport( 3 ) &0x0f);	/* 1P controls */
	                break;
	            case 2:
	                return (readinputport( 3 ) >> 4);	/* 2P controls */
	                break;
				case 3:
					return (readinputport( 2 ) & 0x0f);	/* start 1 & 2 and button 1 & 2 */
					break;
				default:
					return gaplus_customio_1[offset];
	        }
	    }
	    else if (mode == 8)  /* IO tests chip 1 */
	    {
	        switch (offset)
	        {
	            case 0:
	                return 0x06;
	                break;
	            case 1:
	                return 0x09;
	                break;
				default:
					return gaplus_customio_1[offset];
	        }
	    }
	    return gaplus_customio_1[offset];
	} };
	
	public static ReadHandlerPtr gaplus_customio_2_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int val, mode;
	
	    mode = gaplus_customio_2[8];
	    if (mode == 5)  /* IO tests chip 2 */
	    {
	        switch (offset)
	        {
	            case 0:
	            case 1:
	                return 0x0f;
	                break;
				default:
					return gaplus_customio_2[offset];
	        }
	    }
	    else if (mode == 4)     /* this values are read only by the game on power up */
	    {
	        switch (offset)
	        {
	            case 1:
	                val = readinputport( 0 ) & 0x0f;	/* credits/coin 1P & fighters */
	                break;
	            case 2:
	                val = readinputport( 1 ) >> 5;		/* bonus life */
	                break;
	            case 4:
	                val = readinputport( 1 ) & 0x07;	/* rank */
	                break;
	            case 7:
	                val = readinputport( 0 ) >> 6;		/* credits/coin 2P */
	                break;
	            default:
	                val = gaplus_customio_2[offset];
	        }
	        return val;
	    }
		else
	        return gaplus_customio_2[offset];
	} };
	
	public static ReadHandlerPtr gaplus_customio_3_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int mode;
	
	    mode = gaplus_customio_3[8];
	    if (mode == 2)
	    {
	        switch (offset)
	        {
	            case 0:
	                return ((readinputport( 0 ) & 0x20) >> 3) ^ ~(readinputport( 1 ) & 0x08); /* cabinet & test mode */;
	                break;
	            case 2:
	                return 0x0f;
	                break;
	            default:
	                return gaplus_customio_3[offset];
	        }
	    }
	    else
	    {
	        switch (offset)
	        {
	            case 0:
	                return ((readinputport( 0 ) & 0x20) >> 3) ^ ~(readinputport( 1 ) & 0x08); /* cabinet & test mode */;
	                break;
	            case 1:
	                return 0x0f;
	                break;
	            case 2:
	                return 0x0e;
	                break;
	            case 3:
	                return 0x01;
	                break;
	            default:
	                return gaplus_customio_3[offset];
	        }
	    }
	} };
	
	/************************************************************************************
	*																					*
	*           Gaplus (rev. B) custom I/O chips (preliminary)									*
	*																					*
	************************************************************************************/
	
	public static ReadHandlerPtr gapluso_customio_1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int mode, val, temp1, temp2;
	
	    mode = gaplus_customio_1[8];
	    if (mode == 4)	/* normal mode */
	    {
	        switch (offset)
	        {
			case 0:
				return (credits / 10);      /* high BCD of credits */
	            break;
	        case 1:
	            return (credits % 10);      /* low BCD of credits */
	            break;
			case 2:     /* Coin slots, high nibble of port 2 */
	            {
	                static int lastval;
	
	                val = readinputport( 2 ) >> 4;
	                temp1 = readinputport( 0 ) & 0x03;
	                temp2 = (readinputport( 0 ) >> 6) & 0x03;
	
	                /* bit 0 is a trigger for the coin slot 1 */
	                if ((val & 1) && ((val ^ lastval) & 1))
	                {
	                    coincounter1++;
	                    if (coincounter1 >= credmoned[temp1])
	                    {
	                        credits += monedcred [temp1];
	                        coincounter1 -= credmoned [temp1];
	                    }
	                }
	                /* bit 1 is a trigger for the coin slot 2 */
	                if ((val & 2) && ((val ^ lastval) & 2))
	                {
	                    coincounter2++;
	                    if (coincounter2 >= credmoned[temp2])
	                    {
	                        credits += monedcred [temp2];
	                        coincounter2 -= credmoned [temp2];
	                    }
	                }
	
	                if (credits > 99)
	                    credits = 99;
	
	                return lastval = val;
	            }
	                break;
	            case 3:
	            {
	                static int lastval;
	
	                val = readinputport( 2 ) & 0x03;
	                temp1 = readinputport( 0 ) & 0x03;
	                temp2 = (readinputport( 0 ) >> 6) & 0x03;
	
	                /* bit 0 is a trigger for the 1 player start */
	                if ((val & 1) && ((val ^ lastval) & 1))
	                {
	                    if (credits > 0)
	                        credits--;
	                    else
	                        val &= ~1;   /* otherwise you can start with no credits! */
	                }
	                /* bit 1 is a trigger for the 2 player start */
	                if ((val & 2) && ((val ^ lastval) & 2))
	                {
	                    if (credits >= 2)
	                        credits -= 2;
	                    else
	                        val &= ~2;   /* otherwise you can start with no credits! */
	                }
	                return lastval = val;
	            }
	                break;
	            case 4:
	                return (readinputport( 3 ) & 0x0f);   /* 1P controls */
	                break;
	            case 5:
	                return (readinputport( 4 ) & 0x03);   /* 1P button 1 */
	                break;
	            case 6:
	                return (readinputport( 3 ) >> 4);     /* 2P controls */
	                break;
	            case 7:
	                return ((readinputport( 4 ) >> 2) & 0x03);    /* 2P button 1 */
	                break;
	            default:
	                return gaplus_customio_1[offset];
	        }
	    }
	    else if (mode == 8)  /* IO tests chip 1 */
	    {
	        switch (offset)
	        {
	            case 0:
	                return 0x06;
	                break;
	            case 1:
	                return 0x09;
	                break;
				default:
					return gaplus_customio_1[offset];
	        }
	    }
		else if (mode == 1)	/* test mode */
		{
			switch (offset)
	        {
			case 0:
				return (readinputport( 2 ) & 0x03);	/* start 1 & 2 */
				break;
	        case 1:
	            return (readinputport( 3 ) &0x0f);	/* 1P controls */
	            break;
	        case 2:
	            return (readinputport( 3 ) >> 4);	/* 2P controls */
	            break;
			case 3:
				return (readinputport( 4 ) & 0x0f);	/* button 1 & 2 */
				break;
			default:
				return gaplus_customio_1[offset];
			}
		}
	    return gaplus_customio_1[offset];
	} };
	
	public static ReadHandlerPtr gapluso_customio_2_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int val, mode;
	
	    mode = gaplus_customio_2[8];
		if (mode == 5)  /* IO tests chip 2 */
	    {
	        switch (offset)
	        {
	            case 0:
	            case 1:
	                return 0x0f;
	                break;
				default:
					return gaplus_customio_2[offset];
	        }
	    }
	    else    if (mode == 4)	/* this values are read only by the game on power up */
	    {
	        switch (offset)
	        {
				case 1:
	                val = readinputport( 0 ) & 0x0f; /* credits/coin 1P & fighters */
	                break;
	            case 2:
	                val = readinputport( 1 ) >> 5;   /* bonus life */
	                break;
				case 4:
	                val = readinputport( 1 ) & 0x0f; /* rank & test mode */
	                break;
	            case 7:
	                val = readinputport( 0 ) >> 6;   /* credits/coin 2P */
	                break;
	            default:
	                val = gaplus_customio_2[offset];
	        }
	        return val;
	    }
		else
			return gaplus_customio_2[offset];
	} };
	
	public static ReadHandlerPtr gapluso_customio_3_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int mode;
	
	    mode = gaplus_customio_3[8];
	    if (mode == 2)
	    {
	        switch (offset)
	        {
	            case 2:
	                return 0x0f;
	                break;
	            default:
	                return gaplus_customio_3[offset];
	        }
	    }
	    else
	    {
	        switch (offset)
	        {
	            case 0:
	                return ((readinputport( 0 ) & 0x20) >> 3);   /* cabinet */
	                break;
	            case 1:
	                return 0x0f;
	                break;
	            case 2:
	                return 0x0e;
	                break;
	            case 3:
	                return 0x01;
	                break;
	            default:
	                return gaplus_customio_3[offset];
	        }
	    }
	} };
	
	/************************************************************************************
	*																					*
	*           Gaplus (alternate hardware) custom I/O chips (preliminary)									*
	*																					*
	************************************************************************************/
	
	public static ReadHandlerPtr gaplusa_customio_1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int mode, val, temp1, temp2;
	
	    mode = gaplus_customio_1[8];
	    if (mode == 3)	/* normal mode */
	    {
	        switch (offset)
	        {
	            case 0:     /* Coin slots, high nibble of port 2 */
	            {
	                static int lastval;
	
	                val = readinputport( 2 ) >> 4;
	                temp1 = readinputport( 0 ) & 0x03;
	                temp2 = (readinputport( 0 ) >> 6) & 0x03;
	
	                /* bit 0 is a trigger for the coin slot 1 */
	                if ((val & 1) && ((val ^ lastval) & 1))
	                {
	                    coincounter1++;
	                    if (coincounter1 >= credmoned[temp1])
	                    {
	                        credits += monedcred [temp1];
	                        coincounter1 -= credmoned [temp1];
	                    }
	                }
	                /* bit 1 is a trigger for the coin slot 2 */
	                if ((val & 2) && ((val ^ lastval) & 2))
	                {
	                    coincounter2++;
	                    if (coincounter2 >= credmoned[temp2])
	                    {
	                        credits += monedcred [temp2];
	                        coincounter2 -= credmoned [temp2];
	                    }
	                }
	
	                if (credits > 99)
	                    credits = 99;
	
	                return lastval = val;
	            }
	                break;
	            case 1:
	            {
	                static int lastval;
	
	                val = readinputport( 2 ) & 0x03;
	                temp1 = readinputport( 0 ) & 0x03;
	                temp2 = (readinputport( 0 ) >> 6) & 0x03;
	
	                /* bit 0 is a trigger for the 1 player start */
	                if ((val & 1) && ((val ^ lastval) & 1))
	                {
	                    if (credits > 0)
	                        credits--;
	                    else
	                        val &= ~1;   /* otherwise you can start with no credits! */
	                }
	                /* bit 1 is a trigger for the 2 player start */
	                if ((val & 2) && ((val ^ lastval) & 2))
	                {
	                    if (credits >= 2)
	                        credits -= 2;
	                    else
	                        val &= ~2;   /* otherwise you can start with no credits! */
	                }
	                return lastval = val;
	            }
	                break;
	            case 2:
	                return (credits / 10);      /* high BCD of credits */
	                break;
	            case 3:
	                return (credits % 10);      /* low BCD of credits */
	                break;
	            case 4:
	                return (readinputport( 3 ) & 0x0f);   /* 1P controls */
	                break;
	            case 5:
	                return (readinputport( 4 ) & 0x03);   /* 1P button 1 */
	                break;
	            case 6:
	                return (readinputport( 3 ) >> 4);     /* 2P controls */
	                break;
	            case 7:
	                return ((readinputport( 4 ) >> 2) & 0x03);    /* 2P button 1 */
	                break;
	            default:
	                return gaplus_customio_1[offset];
	        }
	    }
	    else if (mode == 5)  /* IO tests chip 1 */
	    {
	        switch (offset)
	        {
	            case 0:
	            case 1:
	                return 0x0f;
	                break;
	            default:
	                return gaplus_customio_1[offset];
	        }
	    }
	    else if (mode == 1)	/* test mode controls */
	    {
	        switch (offset)
	        {
				case 4:
					return (readinputport( 2 ) & 0x03);	/* start 1 & 2 */
					break;
				case 5:
					return (readinputport( 3 ) &0x0f);	/* 1P controls */
					break;
				case 6:
					return (readinputport( 3 ) >> 4);	/* 2P controls */
					break;
				case 7:
					return (readinputport( 4 ) & 0x0f);	/* button 1 & 2 */
					break;
				default:
					return gaplus_customio_1[offset];
	        }
	    }
	    return gaplus_customio_1[offset];
	} };
	
	public static ReadHandlerPtr gaplusa_customio_2_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int val, mode;
	
	    mode = gaplus_customio_2[8];
	    if (mode == 8)  /* IO tests chip 2 */
	    {
	        switch (offset)
	        {
	            case 0:
	                return 0x06;
	                break;
	            case 1:
	                return 0x09;
	                break;
	            default:
	                return gaplus_customio_2[offset];
	        }
	    }
	    else    if (mode == 1)	/* this values are read only by the game on power up */
	    {
	        switch (offset)
	        {
	            case 0:
	                val = readinputport( 0 ) & 0x0f; /* credits/coin 1P & fighters */
	                break;
	            case 1:
	                val = readinputport( 1 ) >> 5;   /* bonus life */
	                break;
	            case 2:
	                val = readinputport( 1 ) & 0x0f; /* rank & test mode */
	                break;
	            case 3:
	                val = readinputport( 0 ) >> 6;   /* credits/coin 2P */
	                break;
	            default:
	                val = gaplus_customio_2[offset];
	        }
	        return val;
	    }
		else
			return gaplus_customio_2[offset];
	} };
	
	public static ReadHandlerPtr gaplusa_customio_3_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int mode;
	
	    mode = gaplus_customio_3[8];
	    if (mode == 2)
	    {
	        switch (offset)
	        {
	            case 2:
	                return 0x0f;
	                break;
	            default:
	                return gaplus_customio_3[offset];
	        }
	    }
	    else
	    {
	        switch (offset)
	        {
	            case 0:
	                return ((readinputport( 0 ) & 0x20) >> 3);   /* cabinet */
	                break;
	            case 1:
	                return 0x0f;
	                break;
	            case 2:
	                return 0x0e;
	                break;
	            case 3:
	                return 0x01;
	                break;
	            default:
	                return gaplus_customio_3[offset];
	        }
	    }
	} };
	
}
