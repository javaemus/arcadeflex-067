/*************************************************************************

	Sega Z80-3D system

*************************************************************************/

/*----------- defined in machine/turbo.c -----------*/

extern UINT8 turbo_opa, turbo_opb, turbo_opc;
extern UINT8 turbo_ipa, turbo_ipb, turbo_ipc;
extern UINT8 turbo_fbpla, turbo_fbcol;

extern UINT8 subroc3d_col, subroc3d_ply, subroc3d_chofs;

extern UINT8 buckrog_fchg, buckrog_mov, buckrog_obch;

MACHINE_INIT( turbo );
MACHINE_INIT( subroc3d );
MACHINE_INIT( buckrog );







/*----------- defined in vidhrdw/turbo.c -----------*/

extern UINT8 *sega_sprite_position;
extern UINT8 turbo_collision;

PALETTE_INIT( turbo );
VIDEO_START( turbo );
VIDEO_EOF( turbo );
VIDEO_UPDATE( turbo );

PALETTE_INIT( subroc3d );
VIDEO_START( subroc3d );
VIDEO_UPDATE( subroc3d );

PALETTE_INIT( buckrog );
VIDEO_START( buckrog );
VIDEO_UPDATE( buckrog );

