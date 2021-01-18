/**************************************************************************
 *
 * machine/konamigx.c - contains various System GX hardware abstractions
 *
 * Currently includes: TMS57002 skipper/simulator to make the sound 68k happy.
 *
 */

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class konamigx
{
	
	static struct {
	  unsigned char control;
	  unsigned char program[1024];
	  unsigned int tables[256];
	  int curpos;
	  int bytepos;
	  unsigned int tmp;
	
	} tms57002;
	
	void tms57002_init(void)
	{
	  tms57002.control = 0;
	}
	
	int ldw = 0;
	
	static void chk_ldw(void)
	{
	  ldw = 0;
	}
	
	public static WriteHandlerPtr tms57002_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	  chk_ldw();
	
	  switch(tms57002.control) {
	  case 0xf8: {
	    break;
	  }
	  case 0xf0: {
	    break;
	  }
	  }
	
	  tms57002.control = data;
	  switch(data) {
	  case 0xf8: // Program write
	  case 0xf0: // Table write
	    tms57002.curpos = 0;
	    tms57002.bytepos = 3;
	    tms57002.tmp = 0;
	    break;
	  case 0xf4: // Entry write
	    tms57002.curpos = -1;
	    tms57002.bytepos = 3;
	    tms57002.tmp = 0;
	    break;
	  case 0xfc: // Checksum (?) Status (?)
	    tms57002.bytepos = 3;
	    tms57002.tmp = 0;
	    break;
	  case 0xff: // Standby
	    break;
	  case 0xfe: // Irq
	/* (place ack of timer IRQ here) */
	    break;
	  default:
	    break;
	  }
	
	} };
	
	public static ReadHandlerPtr tms57002_status_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	  chk_ldw();
	  return 0x01;
	} };
	
	
	public static WriteHandlerPtr tms57002_data_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	  switch(tms57002.control) {
	  case 0xf8:
	    tms57002.program[tms57002.curpos++] = data;
	    break;
	  case 0xf0:
	    tms57002.tmp |= data << (8*tms57002.bytepos);
	    tms57002.bytepos--;
	    if(tms57002.bytepos < 0) {
	      tms57002.bytepos = 3;
	      tms57002.tables[tms57002.curpos++] =  tms57002.tmp;
	      tms57002.tmp = 0;
	    }
	    break;
	  case 0xf4:
	    if(tms57002.curpos == -1)
	      tms57002.curpos = data;
	    else {
	      tms57002.tmp |= data << (8*tms57002.bytepos);
	      tms57002.bytepos--;
	      if(tms57002.bytepos < 0) {
		tms57002.bytepos = 3;
	
		tms57002.tables[tms57002.curpos] =  tms57002.tmp;
		tms57002.tmp = 0;
		tms57002.curpos = -1;
	      }
	    }
	    break;
	  default:
	    ldw++;
	    break;
	  }
	} };
	
	public static ReadHandlerPtr tms57002_data_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	  unsigned char res;
	  chk_ldw();
	  switch(tms57002.control) {
	  case 0xfc:
	    res = tms57002.tmp >> (8*tms57002.bytepos);
	    tms57002.bytepos--;
	    if(tms57002.bytepos < 0)
	      tms57002.bytepos = 3;
	    return res;
	  }
	  return 0;
	} };
	
	
	READ16_HANDLER( tms57002_data_word_r )
	{
		return(tms57002_data_r(0));
	}
	
	READ16_HANDLER( tms57002_status_word_r )
	{
		return(tms57002_status_r(0));
	}
	
	WRITE16_HANDLER( tms57002_control_word_w )
	{
		tms57002_control_w(0, data);
	}
	
	WRITE16_HANDLER( tms57002_data_word_w )
	{
		tms57002_data_w(0, data);
	}
	
}
