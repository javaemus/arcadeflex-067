/*************************************************************************

	Atari Return of the Jedi hardware

*************************************************************************/

/*----------- defined in vidhrdw/jedi.c -----------*/

extern UINT8 *jedi_PIXIRAM;
extern UINT8 *jedi_backgroundram;
extern size_t jedi_backgroundram_size;

VIDEO_START( jedi );
VIDEO_UPDATE( jedi );

