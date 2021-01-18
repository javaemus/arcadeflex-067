/***************************************************************************

	Sun Electronics Kangaroo hardware

	driver by Ville Laitinen

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package includes;

public class kangarooH
{
	
	
	/*----------- defined in vidhrdw/kangaroo.c -----------*/
	
	extern UINT8 *kangaroo_video_control;
	extern UINT8 *kangaroo_bank_select;
	extern UINT8 *kangaroo_blitter;
	extern UINT8 *kangaroo_scroll;
	
	PALETTE_INIT( kangaroo );
	VIDEO_START( kangaroo );
	VIDEO_UPDATE( kangaroo );
	
	}
