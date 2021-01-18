/***************************************************************************

	Taito Qix hardware

	driver by John Butler, Ed Mueller, Aaron Giles

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package includes;

public class qixH
{
	
	
	/*----------- defined in machine/qix.c -----------*/
	
	extern UINT8 *qix_sharedram;
	extern UINT8 *qix_68705_port_out;
	extern UINT8 *qix_68705_ddr;
	
	MACHINE_INIT( qix );
	MACHINE_INIT( qixmcu );
	MACHINE_INIT( slither );
	
	
	
	
	
	
	
	
	/*----------- defined in vidhrdw/qix.c -----------*/
	
	extern UINT8 *qix_videoaddress;
	extern UINT8 qix_cocktail_flip;
	
	VIDEO_START( qix );
	VIDEO_UPDATE( qix );
	
	INTERRUPT_GEN( qix_vblank_start );
	void qix_scanline_callback(int scanline);
	
	
	}
