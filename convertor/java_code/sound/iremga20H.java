/*********************************************************

	Irem GA20 PCM Sound Chip

*********************************************************/
#ifndef __IREMGA20_H__
#define __IREMGA20_H__

struct IremGA20_interface {
	int clock;					/* clock */
	int region;					/* memory region of sample ROM(s) */
	int mixing_level[2];		/* volume */
};

int IremGA20_sh_start( const struct MachineSound *msound );

#endif /* __IREMGA20_H__ */
