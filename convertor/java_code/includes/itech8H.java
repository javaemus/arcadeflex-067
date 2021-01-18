/*************************************************************************

	Incredible Technologies/Strata system
	(8-bit blitter variant)

**************************************************************************/

/*----------- defined in drivers/itech8.c -----------*/

void itech8_update_interrupts(int periodic, int tms34061, int blitter);


/*----------- defined in machine/slikshot.c -----------*/



void slikshot_extra_draw(struct mame_bitmap *bitmap, const struct rectangle *cliprect);


/*----------- defined in vidhrdw/itech8.c -----------*/

extern UINT8 *itech8_grom_bank;
extern UINT8 *itech8_display_page;

VIDEO_START( itech8 );
VIDEO_START( slikshot );




VIDEO_UPDATE( itech8 );
