/*************************************************************************

	Atari Jaguar hardware

*************************************************************************/

#ifndef ENABLE_SPEEDUP_HACKS
#define ENABLE_SPEEDUP_HACKS 1
#endif


/*----------- defined in drivers/cojag.c -----------*/

extern UINT8 cojag_is_r3000;

extern data32_t *jaguar_shared_ram;
extern data32_t *jaguar_gpu_ram;
extern data32_t *jaguar_gpu_clut;
extern data32_t *jaguar_dsp_ram;
extern data32_t *jaguar_wave_rom;


/*----------- defined in sndhrdw/jaguar.c -----------*/



void jaguar_external_int(int state);

READ16_HANDLER( jaguar_jerry_regs_r );
WRITE16_HANDLER( jaguar_jerry_regs_w );
READ32_HANDLER( jaguar_jerry_regs32_r );
WRITE32_HANDLER( jaguar_jerry_regs32_w );

READ32_HANDLER( jaguar_serial_r );
WRITE32_HANDLER( jaguar_serial_w );


/*----------- defined in vidhrdw/jaguar.c -----------*/

extern UINT8 cojag_draw_crosshair;



READ32_HANDLER( jaguar_blitter_r );
WRITE32_HANDLER( jaguar_blitter_w );

READ16_HANDLER( jaguar_tom_regs_r );
WRITE16_HANDLER( jaguar_tom_regs_w );
READ32_HANDLER( jaguar_tom_regs32_r );
WRITE32_HANDLER( jaguar_tom_regs32_w );

READ32_HANDLER( cojag_gun_input_r );

VIDEO_START( cojag );
VIDEO_UPDATE( cojag );
