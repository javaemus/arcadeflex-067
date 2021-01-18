/***************************************************************************

  Fast Freddie/Jump Coaster hardware
  driver by Zsolt Vasvari

***************************************************************************/

/* defined in vihdrdw/fastfred.h */
extern data8_t *fastfred_videoram;
extern data8_t *fastfred_spriteram;
extern size_t fastfred_spriteram_size;
extern data8_t *fastfred_attributesram;

PALETTE_INIT( fastfred );
VIDEO_START( fastfred );
VIDEO_UPDATE( fastfred );


