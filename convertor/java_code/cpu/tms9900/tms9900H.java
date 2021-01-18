/*
  tms9900.h

  C Header file for TMS9900 core
*/

#ifndef TMS9900_H
#define TMS9900_H

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package cpu.tms9900;

public class tms9900H
{
	
	#define TMS9900_ID      0 /* original processor, 1976 (huh... it had some multi-chip ancestors, */
	                          /* the 9x0 series)*/
	#define TMS9940_ID      1 /* embedded version, 1979 */
	#define TMS9980_ID      2 /* 8-bit variant of tms9900.  Two distinct chips actually : tms9980a, */
	                          /* and tms9981 with an extra clock and simplified power supply */
	#define TMS9985_ID      3 /* 9980 with on-chip 16-bit RAM and decrementer, c. 1978 (never released) */
	#define TMS9989_ID      4 /* improved 9980, used in bombs, missiles, and other *nice* hardware */
	#define TMS9995_ID      5 /* tms9985-like, with many improvements */
	#define TMS99105A_ID    6 /* late variant, widely improved, 1981 */
	#define TMS99110A_ID    7 /* same as above, with floating point support, c. 1981 */
	
	
	
	enum {
		TMS9900_PC=1, TMS9900_WP, TMS9900_STATUS, TMS9900_IR
	#ifdef MAME_DEBUG
		,
		TMS9900_R0, TMS9900_R1, TMS9900_R2, TMS9900_R3,
		TMS9900_R4, TMS9900_R5, TMS9900_R6, TMS9900_R7,
		TMS9900_R8, TMS9900_R9, TMS9900_R10, TMS9900_R11,
		TMS9900_R12, TMS9900_R13, TMS9900_R14, TMS9900_R15
	#endif
	};
	
	#if (HAS_TMS9900)
	
	
	extern unsigned tms9900_get_context(void *dst);
	extern unsigned tms9900_get_reg(int regnum);
	extern const char *tms9900_info(void *context, int regnum);
	extern unsigned tms9900_dasm(char *buffer, unsigned pc);
	
	#endif
	
	#if (HAS_TMS9940)
	
	
	extern unsigned tms9940_get_context(void *dst);
	extern unsigned tms9940_get_reg(int regnum);
	extern const char *tms9940_info(void *context, int regnum);
	extern unsigned tms9940_dasm(char *buffer, unsigned pc);
	
	#endif
	
	#if (HAS_TMS9980)
	
	
	extern unsigned tms9980a_get_context(void *dst);
	extern unsigned tms9980a_get_reg(int regnum);
	extern const char *tms9980a_info(void *context, int regnum);
	extern unsigned tms9980a_dasm(char *buffer, unsigned pc);
	
	#endif
	
	#if (HAS_TMS9985)
	
	
	extern unsigned tms9985_get_context(void *dst);
	extern unsigned tms9985_get_reg(int regnum);
	extern const char *tms9985_info(void *context, int regnum);
	extern unsigned tms9985_dasm(char *buffer, unsigned pc);
	
	#endif
	
	#if (HAS_TMS9989)
	
	
	extern unsigned tms9989_get_context(void *dst);
	extern unsigned tms9989_get_reg(int regnum);
	extern const char *tms9989_info(void *context, int regnum);
	extern unsigned tms9989_dasm(char *buffer, unsigned pc);
	
	#endif
	
	#if (HAS_TMS9995)
	
	
	extern unsigned tms9995_get_context(void *dst);
	extern unsigned tms9995_get_reg(int regnum);
	extern const char *tms9995_info(void *context, int regnum);
	extern unsigned tms9995_dasm(char *buffer, unsigned pc);
	
	/*
	  structure with the parameters tms9995_reset wants.
	*/
	typedef struct tms9995reset_param
	{
		/* auto_wait_state : a non-zero value makes tms9995 generate a wait state automatically on each
		   memory access */
		int auto_wait_state;
	} tms9995reset_param;
	
	#endif
	
	#if (HAS_TMS99105A)
	
	
	extern unsigned tms99105a_get_context(void *dst);
	extern unsigned tms99105a_get_reg(int regnum);
	extern const char *tms99105a_info(void *context, int regnum);
	extern unsigned tms99105a_dasm(char *buffer, unsigned pc);
	
	#endif
	
	#if (HAS_TMS99110A)
	
	
	extern unsigned tms99110a_get_context(void *dst);
	extern unsigned tms99110a_get_reg(int regnum);
	extern const char *tms99110a_info(void *context, int regnum);
	extern unsigned tms99110a_dasm(char *buffer, unsigned pc);
	
	#endif
	
	#ifdef MAME_DEBUG
	extern unsigned Dasm9900 (char *buffer, unsigned pc);
	#endif
	
	#endif
	
	
}
