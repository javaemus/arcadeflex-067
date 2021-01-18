/*************************************************************************

	Atari Centipede hardware

*************************************************************************/

/*----------- defined in vidhrdw/centiped.c -----------*/

extern UINT8 centiped_flipscreen;

PALETTE_INIT( centiped );
PALETTE_INIT( milliped );
PALETTE_INIT( warlords );

VIDEO_START( centiped );
VIDEO_START( milliped );
VIDEO_START( warlords );
VIDEO_START( qwakprot );

VIDEO_UPDATE( centiped );
VIDEO_UPDATE( warlords );
VIDEO_UPDATE( qwakprot );



