/*************************************************************************

	Exidy 6502 hardware

*************************************************************************/

/*----------- defined in sndhrdw/exidy.c -----------*/

int exidy_sh_start(const struct MachineSound *msound);




/*----------- defined in sndhrdw/targ.c -----------*/

extern UINT8 targ_spec_flag;

int targ_sh_start(const struct MachineSound *msound);



/*----------- defined in vidhrdw/exidy.c -----------*/

#define PALETTE_LEN 8
#define COLORTABLE_LEN 20

extern UINT8 *exidy_characterram;
extern UINT8 *exidy_sprite_no;
extern UINT8 *exidy_sprite_enable;
extern UINT8 *exidy_sprite1_xpos;
extern UINT8 *exidy_sprite1_ypos;
extern UINT8 *exidy_sprite2_xpos;
extern UINT8 *exidy_sprite2_ypos;
extern UINT8 *exidy_color_latch;
extern UINT8 *exidy_palette;
extern UINT16 *exidy_colortable;

extern UINT8 sidetrac_palette[];
extern UINT8 targ_palette[];
extern UINT8 spectar_palette[];
extern UINT16 exidy_1bpp_colortable[];
extern UINT16 exidy_2bpp_colortable[];

extern UINT8 exidy_collision_mask;
extern UINT8 exidy_collision_invert;

PALETTE_INIT( exidy );
VIDEO_START( exidy );
VIDEO_EOF( exidy );
VIDEO_UPDATE( exidy );

INTERRUPT_GEN( exidy_vblank_interrupt );


