/*************************************************************************

	Atari Basketball hardware

*************************************************************************/

/*----------- defined in machine/bsktball.c -----------*/

INTERRUPT_GEN( bsktball_interrupt );


/*----------- defined in vidhrdw/bsktball.c -----------*/

extern unsigned char *bsktball_motion;
VIDEO_UPDATE( bsktball );
