#ifndef k005289_h
#define k005289_h

struct k005289_interface
{
	int master_clock;	/* clock speed */
	int volume;			/* playback volume */
	int region;			/* memory region */
};

int K005289_sh_start(const struct MachineSound *msound);


#endif
