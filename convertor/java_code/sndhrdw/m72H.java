/***************************************************************************

	M72 audio interface

****************************************************************************/

MACHINE_INIT( m72_sound );
void m72_ym2151_irq_handler(int irq);

/* the port goes to different address bits depending on the game */
void m72_set_sample_start(int start);
