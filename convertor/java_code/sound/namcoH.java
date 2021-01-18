#ifndef namco_h
#define namco_h

struct namco_interface
{
	int samplerate;	/* sample rate */
	int voices;		/* number of voices */
	int volume;		/* playback volume */
	int region;		/* memory region; -1 to use RAM (pointed to by namco_wavedata) */
	int stereo;		/* set to 1 to indicate stereo (e.g., System 1) */
};

int namco_sh_start(const struct MachineSound *msound);





extern unsigned char *namco_soundregs;
extern unsigned char *namco_wavedata;

#define mappy_soundregs namco_soundregs
#define pengo_soundregs namco_soundregs
#define polepos_soundregs namco_soundregs

#endif

