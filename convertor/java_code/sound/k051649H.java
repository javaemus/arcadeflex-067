#ifndef k051649_h
#define k051649_h

struct k051649_interface
{
	int master_clock;	/* master clock */
	int volume;			/* playback volume */
};

int K051649_sh_start(const struct MachineSound *msound);


#endif
