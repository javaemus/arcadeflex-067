#ifndef __MultiPCM_H__
#define __MultiPCM_H__

#define MAX_MULTIPCM	(2)	// max # of multipcm chips

// banking types
#define MULTIPCM_MODE_MODEL1	(0)
#define MULTIPCM_MODE_MULTI32	(1)

#ifndef VOL_YM3012
#define YM3012_VOL(LVol,LPan,RVol,RPan) (MIXER(LVol,LPan)|(MIXER(RVol,RPan) << 16))
#endif

struct MultiPCM_interface
{
	int chips;
	int clock[MAX_MULTIPCM];
	int type[MAX_MULTIPCM];
	int banksize[MAX_MULTIPCM];
	int region[MAX_MULTIPCM];
	int mixing_level[MAX_MULTIPCM];
};


int MultiPCM_sh_start(const struct MachineSound *msound);

public static ReadHandlerPtr MultiPCM_reg_0_r  = new ReadHandlerPtr() { public int handler(int offset);
public static ReadHandlerPtr MultiPCM_reg_1_r  = new ReadHandlerPtr() { public int handler(int offset);

void MultiPCMGetContext(void* ctx);
void MultiPCMSetContext(void* ctx);

#endif
