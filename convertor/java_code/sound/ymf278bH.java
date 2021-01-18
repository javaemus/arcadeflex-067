#ifndef __YMF278B_H__
#define __YMF278B_H__

#define MAX_YMF278B	(2)

#define YMF278B_STD_CLOCK (33868800)			/* standard clock for OPL4 */

struct YMF278B_interface {
	int num;        				/* Number of chips */
	int clock[MAX_YMF278B];				/* clock input, normally 33.8688 MHz */
	int region[MAX_YMF278B];			/* memory region of sample ROMs */
	int mixing_level[MAX_YMF278B];			/* volume */
	void (*irq_callback[MAX_YMF278B])(int state);	/* irq callback */
};

int  YMF278B_sh_start( const struct MachineSound *msound );



#endif
