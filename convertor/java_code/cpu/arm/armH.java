#ifndef ARM_H
#define ARM_H

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package cpu.arm;

public class armH
{
	
	/****************************************************************************************************
	 *	INTERRUPT CONSTANTS
	 ***************************************************************************************************/
	
	#define ARM_IRQ_LINE	0
	#define ARM_FIRQ_LINE	1
	
	/****************************************************************************************************
	 *	PUBLIC GLOBALS
	 ***************************************************************************************************/
	
	
	/****************************************************************************************************
	 *	PUBLIC FUNCTIONS
	 ***************************************************************************************************/
	
	extern unsigned arm_get_context(void *dst);
	extern unsigned arm_get_pc(void);
	extern unsigned arm_get_sp(void);
	extern unsigned arm_get_reg(int regnum);
	extern const char *arm_info(void *context, int regnum);
	extern unsigned arm_dasm(char *buffer, unsigned pc);
	
	#ifdef MAME_DEBUG
	#endif
	
	enum
	{
		ARM32_R0=1, ARM32_R1, ARM32_R2, ARM32_R3, ARM32_R4, ARM32_R5, ARM32_R6, ARM32_R7,
		ARM32_R8, ARM32_R9, ARM32_R10, ARM32_R11, ARM32_R12, ARM32_R13, ARM32_R14, ARM32_R15,
		ARM32_FR8, ARM32_FR9, ARM32_FR10, ARM32_FR11, ARM32_FR12, ARM32_FR13, ARM32_FR14,
		ARM32_IR13, ARM32_IR14, ARM32_SR13, ARM32_SR14
	};
	
	#endif /* ARM_H */
}
