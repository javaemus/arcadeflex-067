/***************************************************************************

						-= Dynax / Nakanihon Games =-

***************************************************************************/

/***************************************************************************


								Interrupts


***************************************************************************/

/* Variables defined in drivers: */

extern UINT8 dynax_blitter_irq;

/* Functions defined in drivers: */


/***************************************************************************


								Video Blitter(s)


***************************************************************************/

/* Functions defined in vidhrdw: */




VIDEO_START( hanamai );
VIDEO_START( hnoridur );
VIDEO_START( sprtmtch );
VIDEO_START( mjdialq2 );


VIDEO_UPDATE( hanamai );
VIDEO_UPDATE( hnoridur );
VIDEO_UPDATE( sprtmtch );
VIDEO_UPDATE( mjdialq2 );

PALETTE_INIT( sprtmtch );
