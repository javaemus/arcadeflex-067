/*************************************************************************

	Atari Missile Command hardware

*************************************************************************/

/*----------- defined in machine/missile.c -----------*/

MACHINE_INIT( missile );


/*----------- defined in vidhrdw/missile.c -----------*/

extern unsigned char *missile_video2ram;

VIDEO_START( missile );
VIDEO_UPDATE( missile );


