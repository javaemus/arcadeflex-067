/***************************************************************************

	Sun Electronics Arabian hardware

	driver by Dan Boris

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package includes;

public class arabianH
{
	
	
	/*----------- defined in vidhrdw/arabian.c -----------*/
	
	extern UINT8 arabian_video_control;
	extern UINT8 arabian_flip_screen;
	
	PALETTE_INIT( arabian );
	VIDEO_START( arabian );
	VIDEO_UPDATE( arabian );
	
	}
