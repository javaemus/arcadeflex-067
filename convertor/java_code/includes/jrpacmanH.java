/*************************************************************************

	Bally/Midway Jr. Pac-Man

**************************************************************************/

/*----------- defined in vidhrdw/jrpacman.c -----------*/

extern unsigned char *jrpacman_scroll,*jrpacman_bgpriority;
extern unsigned char *jrpacman_charbank,*jrpacman_spritebank;
extern unsigned char *jrpacman_palettebank,*jrpacman_colortablebank;

PALETTE_INIT( jrpacman );
VIDEO_START( jrpacman );


VIDEO_UPDATE( jrpacman );
