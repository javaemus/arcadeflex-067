#ifndef TMS36XX_SOUND_H
#define TMS36XX_SOUND_H

#define MAX_TMS36XX 4

/* subtypes */
#define MM6221AA    21      /* Phoenix (fixed melodies) */
#define TMS3615 	15		/* Naughty Boy, Pleiads (13 notes, one output) */
#define TMS3617 	17		/* Monster Bash (13 notes, six outputs) */

/* The interface structure */
struct TMS36XXinterface {
	int num;
	int mixing_level[MAX_TMS36XX];
	int subtype[MAX_TMS36XX];
	int basefreq[MAX_TMS36XX];		/* base frequecnies of the chips */
	double decay[MAX_TMS36XX][6];	/* decay times for the six harmonic notes */
	double speed[MAX_TMS36XX];		/* tune speed (meaningful for the TMS3615 only) */
};


/* MM6221AA interface functions */

/* TMS3615/17 interface functions */

/* TMS3617 interface functions */

#endif
