/*************************************************************************

	Atari Subs hardware

*************************************************************************/

/*----------- defined in machine/subs.c -----------*/

MACHINE_INIT( subs );
INTERRUPT_GEN( subs_interrupt );


/*----------- defined in vidhrdw/subs.c -----------*/

VIDEO_UPDATE( subs );

