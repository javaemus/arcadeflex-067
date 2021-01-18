/*************************************************************************

	Atari I, Robot hardware

*************************************************************************/

/*----------- defined in machine/irobot.c -----------*/

extern UINT8 irvg_clear;
extern UINT8 irobot_bufsel;
extern UINT8 irobot_alphamap;
extern UINT8 *irobot_combase;

DRIVER_INIT( irobot );
MACHINE_INIT( irobot );



/*----------- defined in vidhrdw/irobot.c -----------*/

PALETTE_INIT( irobot );
VIDEO_START( irobot );
VIDEO_UPDATE( irobot );


