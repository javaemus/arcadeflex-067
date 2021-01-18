/***************************************************************************

	Kyugo hardware games

***************************************************************************/

/* defined in machine/kyugo.c */
extern data8_t *kyugo_sharedram;

MACHINE_INIT( kyugo );



/* defined in vidhrdw/kyugo.c */
extern data8_t *kyugo_fgvideoram;
extern data8_t *kyugo_bgvideoram;
extern data8_t *kyugo_bgattribram;
extern data8_t *kyugo_spriteram_1;
extern data8_t *kyugo_spriteram_2;



VIDEO_START( kyugo );

VIDEO_UPDATE( kyugo );
