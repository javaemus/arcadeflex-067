/* ASG 971222 -- rewrote this interface */
#ifndef __NEC_H_
#define __NEC_H_

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package cpu.nec;

public class necintrfH
{
	
	enum {
		NEC_IP=1, NEC_AW, NEC_CW, NEC_DW, NEC_BW, NEC_SP, NEC_BP, NEC_IX, NEC_IY,
		NEC_FLAGS, NEC_ES, NEC_CS, NEC_SS, NEC_DS,
		NEC_VECTOR, NEC_PENDING, NEC_NMI_STATE, NEC_IRQ_STATE};
	
	/* Public variables */
	
	/* Public functions */
	
	#define v20_ICount nec_ICount
	extern unsigned v20_get_context(void *dst);
	extern unsigned v20_get_reg(int regnum);
	extern const char *v20_info(void *context, int regnum);
	extern unsigned v20_dasm(char *buffer, unsigned pc);
	
	#define v30_ICount nec_ICount
	extern unsigned v30_get_context(void *dst);
	extern unsigned v30_get_reg(int regnum);
	extern const char *v30_info(void *context, int regnum);
	extern unsigned v30_dasm(char *buffer, unsigned pc);
	
	#define v33_ICount nec_ICount
	extern unsigned v33_get_context(void *dst);
	extern unsigned v33_get_reg(int regnum);
	extern const char *v33_info(void *context, int regnum);
	extern unsigned v33_dasm(char *buffer, unsigned pc);
	
	#ifdef MAME_DEBUG
	extern unsigned Dasmnec(char* buffer, unsigned pc);
	#endif
	
	#endif
}
