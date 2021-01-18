#ifndef __MACH_KONAMIGX_H
#define __MACH_KONAMIGX_H



READ16_HANDLER( tms57002_data_word_r );
READ16_HANDLER( tms57002_status_word_r );
WRITE16_HANDLER( tms57002_control_word_w );
WRITE16_HANDLER( tms57002_data_word_w );

#endif
