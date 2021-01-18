/*************************************************************************

	Meadows S2650 hardware

*************************************************************************/

/*----------- defined in sndhrdw/meadows.c -----------*/

int meadows_sh_start(const struct MachineSound *msound);
void meadows_sh_dac_w(int data);
extern UINT8 meadows_0c00;
extern UINT8 meadows_0c01;
extern UINT8 meadows_0c02;
extern UINT8 meadows_0c03;


/*----------- defined in vidhrdw/meadows.c -----------*/

VIDEO_START( meadows );
VIDEO_UPDATE( meadows );

