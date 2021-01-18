/*************************************************************************

	Atari Skydiver hardware

*************************************************************************/

/*----------- defined in vidhrdw/skydiver.c -----------*/

extern data8_t *skydiver_videoram;

MACHINE_INIT( skydiver );
VIDEO_START( skydiver );
VIDEO_UPDATE( skydiver );
