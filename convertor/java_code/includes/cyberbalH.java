/*************************************************************************

	Atari Cyberball hardware

*************************************************************************/

/*----------- defined in sndhrdw/cyberbal.c -----------*/

int cyberbal_samples_start(const struct MachineSound *msound);

INTERRUPT_GEN( cyberbal_sound_68k_irq_gen );


READ16_HANDLER( cyberbal_sound_68k_r );
WRITE16_HANDLER( cyberbal_io_68k_irq_ack_w );
WRITE16_HANDLER( cyberbal_sound_68k_w );
WRITE16_HANDLER( cyberbal_sound_68k_dac_w );


/*----------- defined in vidhrdw/cyberbal.c -----------*/

void cyberbal_set_screen(int which);

READ16_HANDLER( cyberbal_paletteram_0_r );
READ16_HANDLER( cyberbal_paletteram_1_r );
WRITE16_HANDLER( cyberbal_paletteram_0_w );
WRITE16_HANDLER( cyberbal_paletteram_1_w );

VIDEO_START( cyberbal );
VIDEO_START( cyberb2p );
VIDEO_UPDATE( cyberbal );

void cyberbal_scanline_update(int param);

extern data16_t *cyberbal_paletteram_0;
extern data16_t *cyberbal_paletteram_1;
