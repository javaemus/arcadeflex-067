/*************************************************************************

	Atari Super Breakout hardware

*************************************************************************/

/*----------- defined in machine/sbrkout.c -----------*/

INTERRUPT_GEN( sbrkout_interrupt );


/*----------- defined in vidhrdw/sbrkout.c -----------*/

extern unsigned char *sbrkout_horiz_ram;
extern unsigned char *sbrkout_vert_ram;

VIDEO_UPDATE( sbrkout );
