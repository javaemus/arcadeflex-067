/* C140.h */

#ifndef _NAMCO_C140_
#define _NAMCO_C140_

int C140_sh_start( const struct MachineSound *msound );

enum
{
	C140_TYPE_SYSTEM2,
	C140_TYPE_SYSTEM21_A,
	C140_TYPE_SYSTEM21_B
};

struct C140interface {
    int banking_type;
    int frequency;
    int region;
    int mixing_level;
};

#endif
