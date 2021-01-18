/*************************************************************************

	Sega System C/C2 Driver

**************************************************************************/

/*----------- defined in vidhrdw/segac2.c -----------*/

extern UINT8		segac2_vdp_regs[];

VIDEO_START( segac2 );
VIDEO_EOF( segac2 );
VIDEO_UPDATE( segac2 );

void	segac2_enable_display(int enable);

READ16_HANDLER ( segac2_vdp_r );
WRITE16_HANDLER( segac2_vdp_w );
