/*###################################################################################################
**
**
**		jaguar.h
**		Interface file for the portable Jaguar DSP emulator.
**		Written by Aaron Giles
**
**
**#################################################################################################*/

#ifndef _JAGUAR_H
#define _JAGUAR_H

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package cpu.jaguar;

public class jaguarH
{
	
	
	/*###################################################################################################
	**	COMPILE-TIME DEFINITIONS
	**#################################################################################################*/
	
	
	/*###################################################################################################
	**	GLOBAL CONSTANTS
	**#################################################################################################*/
	
	#define JAGUAR_VARIANT_GPU		0
	#define JAGUAR_VARIANT_DSP		1
	
	
	/*###################################################################################################
	**	REGISTER ENUMERATION
	**#################################################################################################*/
	
	enum
	{
		JAGUAR_PC=1,JAGUAR_FLAGS,
		JAGUAR_R0,JAGUAR_R1,JAGUAR_R2,JAGUAR_R3,JAGUAR_R4,JAGUAR_R5,JAGUAR_R6,JAGUAR_R7,
		JAGUAR_R8,JAGUAR_R9,JAGUAR_R10,JAGUAR_R11,JAGUAR_R12,JAGUAR_R13,JAGUAR_R14,JAGUAR_R15,
		JAGUAR_R16,JAGUAR_R17,JAGUAR_R18,JAGUAR_R19,JAGUAR_R20,JAGUAR_R21,JAGUAR_R22,JAGUAR_R23,
		JAGUAR_R24,JAGUAR_R25,JAGUAR_R26,JAGUAR_R27,JAGUAR_R28,JAGUAR_R29,JAGUAR_R30,JAGUAR_R31
	};
	
	enum
	{
		G_FLAGS = 0,
		G_MTXC,
		G_MTXA,
		G_END,
		G_PC,
		G_CTRL,
		G_HIDATA,
		G_DIVCTRL,
		G_DUMMY,
		G_REMAINDER,
		G_CTRLMAX
	};
	
	enum
	{
		D_FLAGS = 0,
		D_MTXC,
		D_MTXA,
		D_END,
		D_PC,
		D_CTRL,
		D_MOD,
		D_DIVCTRL,
		D_MACHI,
		D_REMAINDER,
		D_CTRLMAX
	};
	
	
	/*###################################################################################################
	**	CONFIGURATION STRUCTURE
	**#################################################################################################*/
	
	struct jaguar_config
	{
		void (*cpu_int_callback)(void);
	};
	
	
	/*###################################################################################################
	**	INTERRUPT CONSTANTS
	**#################################################################################################*/
	
	#define JAGUAR_IRQ0		0		/* IRQ0 */
	#define JAGUAR_IRQ1		1		/* IRQ1 */
	#define JAGUAR_IRQ2		2		/* IRQ2 */
	#define JAGUAR_IRQ3		3		/* IRQ3 */
	#define JAGUAR_IRQ4		4		/* IRQ4 */
	#define JAGUAR_IRQ5		5		/* IRQ5 */
	
	
	/*###################################################################################################
	**	PUBLIC GLOBALS
	**#################################################################################################*/
	
	
	
	/*###################################################################################################
	**	PUBLIC FUNCTIONS - GPU
	**#################################################################################################*/
	
	extern unsigned jaguargpu_get_context(void *dst);
	extern unsigned jaguargpu_get_reg(int regnum);
	extern const char *jaguargpu_info(void *context, int regnum);
	extern unsigned jaguargpu_dasm(char *buffer, unsigned pc);
	
	extern data32_t jaguargpu_ctrl_r(int cpunum, offs_t offset);
	
	
	/*###################################################################################################
	**	PUBLIC FUNCTIONS - DSP
	**#################################################################################################*/
	
	extern unsigned jaguardsp_get_context(void *dst);
	extern unsigned jaguardsp_get_reg(int regnum);
	extern const char *jaguardsp_info(void *context, int regnum);
	extern unsigned jaguardsp_dasm(char *buffer, unsigned pc);
	
	extern data32_t jaguardsp_ctrl_r(int cpunum, offs_t offset);
	
	
	#endif /* _JAGUAR_H */
}
