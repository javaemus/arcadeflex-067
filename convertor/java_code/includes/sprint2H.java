/*************************************************************************

	Atari Sprint 2 hardware

*************************************************************************/

/*----------- defined in machine/sprint2.c -----------*/

extern UINT8 sprintx_is_sprint2;




/*----------- defined in vidhrdw/sprint2.c -----------*/

VIDEO_START( sprint2 );
VIDEO_UPDATE( sprint1 );
VIDEO_UPDATE( sprint2 );

extern unsigned char *sprint2_vert_car_ram;
extern unsigned char *sprint2_horiz_ram;
extern unsigned char *sprint2_sound_ram;
