/*************************************************************************

	Sega vector hardware

*************************************************************************/

/*----------- defined in machine/sega.c -----------*/

extern UINT8 *sega_mem;


INTERRUPT_GEN( sega_interrupt );




/*----------- defined in sndhrdw/sega.c -----------*/

int sega_sh_start(const struct MachineSound *msound);



int tacscan_sh_start(const struct MachineSound *msound);



/*----------- defined in vidhrdw/sega.c -----------*/

VIDEO_START( sega );
VIDEO_UPDATE( sega );
