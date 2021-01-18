/*** T-11: Portable DEC T-11 emulator ******************************************/

#ifndef _T11_H
#define _T11_H

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package cpu.t11;

public class t11H
{
	
	enum {
		T11_R0=1, T11_R1, T11_R2, T11_R3, T11_R4, T11_R5, T11_SP, T11_PC, T11_PSW,
		T11_IRQ0_STATE, T11_IRQ1_STATE, T11_IRQ2_STATE, T11_IRQ3_STATE,
		T11_BANK0, T11_BANK1, T11_BANK2, T11_BANK3,
		T11_BANK4, T11_BANK5, T11_BANK6, T11_BANK7 };
	
	#define T11_IRQ0        0      /* IRQ0 */
	#define T11_IRQ1		1	   /* IRQ1 */
	#define T11_IRQ2		2	   /* IRQ2 */
	#define T11_IRQ3		3	   /* IRQ3 */
	
	#define T11_RESERVED    0x000   /* Reserved vector */
	#define T11_TIMEOUT     0x004   /* Time-out/system error vector */
	#define T11_ILLINST     0x008   /* Illegal and reserved instruction vector */
	#define T11_BPT         0x00C   /* BPT instruction vector */
	#define T11_IOT         0x010   /* IOT instruction vector */
	#define T11_PWRFAIL     0x014   /* Power fail vector */
	#define T11_EMT         0x018   /* EMT instruction vector */
	#define T11_TRAP        0x01C   /* TRAP instruction vector */
	
	
	struct t11_setup
	{
		UINT16	mode;			/* initial processor mode */
	};
	
	
	/* PUBLIC GLOBALS */
	
	
	/* PUBLIC FUNCTIONS */
	extern unsigned t11_get_context(void *dst);
	extern unsigned t11_get_reg(int regnum);
	extern const char *t11_info(void *context, int regnum);
	extern unsigned t11_dasm(char *buffer, unsigned pc);
	
	/****************************************************************************/
	/* Read a byte from given memory location                                   */
	/****************************************************************************/
	#define T11_RDMEM(A) ((unsigned)cpu_readmem16lew(A))
	#define T11_RDMEM_WORD(A) ((unsigned)cpu_readmem16lew_word(A))
	
	/****************************************************************************/
	/* Write a byte to given memory location                                    */
	/****************************************************************************/
	#define T11_WRMEM(A,V) (cpu_writemem16lew(A,V))
	#define T11_WRMEM_WORD(A,V) (cpu_writemem16lew_word(A,V))
	
	#ifdef MAME_DEBUG
	extern unsigned DasmT11(char *buffer, unsigned pc);
	#endif
	
	#endif /* _T11_H */
}
