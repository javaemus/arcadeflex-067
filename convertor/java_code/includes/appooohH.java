/* appoooh.h */

extern unsigned char *spriteram,*spriteram_2;

/* vidhrdw */
extern unsigned char *appoooh_fg_videoram,*appoooh_fg_colorram;
extern unsigned char *appoooh_bg_videoram,*appoooh_bg_colorram;
PALETTE_INIT( appoooh );
VIDEO_START( appoooh );
VIDEO_UPDATE( appoooh );

