/***************************************************************************

 Espial hardware games

***************************************************************************/

/* defined in vihdrdw/espial.c */
extern data8_t *espial_videoram;
extern data8_t *espial_colorram;
extern data8_t *espial_attributeram;
extern data8_t *espial_scrollram;
extern data8_t *espial_spriteram_1;
extern data8_t *espial_spriteram_2;
extern data8_t *espial_spriteram_3;

PALETTE_INIT( espial );
VIDEO_START( espial );
VIDEO_START( netwars );
VIDEO_UPDATE( espial );

