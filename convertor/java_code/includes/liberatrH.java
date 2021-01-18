/*************************************************************************

	Atari Liberator hardware

*************************************************************************/

/*----------- defined in vidhrdw/liberatr.c -----------*/

extern UINT8 *liberatr_base_ram;
extern UINT8 *liberatr_planet_frame;
extern UINT8 *liberatr_planet_select;
extern UINT8 *liberatr_x;
extern UINT8 *liberatr_y;
extern UINT8 *liberatr_bitmapram;

VIDEO_START( liberatr );
VIDEO_UPDATE( liberatr );

public static WriteHandlerPtr liberatr_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data) ;
