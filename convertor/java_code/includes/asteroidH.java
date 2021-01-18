/*************************************************************************

	Atari Asteroids hardware

*************************************************************************/

/*----------- defined in machine/asteroid.c -----------*/

INTERRUPT_GEN( asteroid_interrupt );
INTERRUPT_GEN( llander_interrupt );



MACHINE_INIT( asteroid );



/*----------- defined in sndhrdw/asteroid.c -----------*/

extern struct discrete_sound_block asteroid_sound_interface[];
extern struct discrete_sound_block astdelux_sound_interface[];



/*----------- defined in sndhrdw/llander.c -----------*/

extern struct discrete_sound_block llander_sound_interface[];

