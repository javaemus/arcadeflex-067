/*************************************************************************

	Atari Crystal Castles hardware

*************************************************************************/

/*----------- defined in vidhrdw/ccastles.c -----------*/

extern unsigned char *ccastles_screen_addr;
extern unsigned char *ccastles_screen_inc;
extern unsigned char *ccastles_screen_inc_enable;
extern unsigned char *ccastles_sprite_bank;
extern unsigned char *ccastles_scrollx;
extern unsigned char *ccastles_scrolly;


VIDEO_START( ccastles );
VIDEO_UPDATE( ccastles );

