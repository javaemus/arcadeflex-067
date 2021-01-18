/*************************************************************************

	Atari Football hardware

*************************************************************************/

/*----------- defined in drivers/atarifb.c -----------*/



/*----------- defined in machine/atarifb.c -----------*/



/*----------- defined in vidhrdw/atarifb.c -----------*/

extern size_t atarifb_alphap1_vram_size;
extern size_t atarifb_alphap2_vram_size;
extern unsigned char *atarifb_alphap1_vram;
extern unsigned char *atarifb_alphap2_vram;
extern unsigned char *atarifb_scroll_register;


VIDEO_START( atarifb );
VIDEO_UPDATE( atarifb );
