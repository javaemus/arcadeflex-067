/***************************************************************************

	Videa Gridlee hardware

    driver by Aaron Giles

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package includes;

public class gridleeH
{
	
	
	/*----------- defined in sndhrdw/gridlee.c -----------*/
	
	int gridlee_sh_start(const struct MachineSound *msound);
	
	
	/*----------- defined in vidhrdw/gridlee.c -----------*/
	
	/* video driver data & functions */
	extern UINT8 gridlee_cocktail_flip;
	
	PALETTE_INIT( gridlee );
	VIDEO_START( gridlee );
	VIDEO_UPDATE( gridlee );
	
	}
