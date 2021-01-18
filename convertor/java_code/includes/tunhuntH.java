/*************************************************************************

	Atari Tunnel Hunt hardware

*************************************************************************/

/*----------- defined in drivers/tunhunt.c -----------*/

extern data8_t tunhunt_control;


/*----------- defined in vidhrdw/tunhunt.c -----------*/


VIDEO_START( tunhunt );
PALETTE_INIT( tunhunt );

VIDEO_UPDATE( tunhunt );

