/* ASG 971222 -- rewrote this interface */
#ifndef __I86INTRF_H_
#define __I86INTRF_H_

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package cpu.i86;

public class i86intfH
{
	
	enum {
		I86_IP=1, I86_AX, I86_CX, I86_DX, I86_BX, I86_SP, I86_BP, I86_SI, I86_DI,
		I86_FLAGS, I86_ES, I86_CS, I86_SS, I86_DS,
		I86_VECTOR, I86_PENDING, I86_NMI_STATE, I86_IRQ_STATE
	};
	
	/* Public variables */
	
	/* Public functions */
	
	extern unsigned i86_get_context(void *dst);
	extern unsigned i86_get_reg(int regnum);
	extern unsigned i86_dasm(char *buffer, unsigned pc);
	extern const char *i86_info(void *context, int regnum);
	
	#ifdef MAME_DEBUG
	extern unsigned DasmI86(char* buffer, unsigned pc);
	#endif
	
	#endif
}
