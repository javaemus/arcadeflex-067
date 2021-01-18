/**********************************************************************

        Motorola 6845 CRT Controller interface and emulation

        This function emulates the functionality of a single
        crtc6845.

**********************************************************************/


#ifdef CRTC6845_C
	int crtc6845_address_latch=0;
	int crtc6845_horiz_total=0;
	int crtc6845_horiz_disp=0;
	int crtc6845_horiz_sync_pos=0;
	int crtc6845_sync_width=0;
	int crtc6845_vert_total=0;
	int crtc6845_vert_total_adj=0;
	int crtc6845_vert_disp=0;
	int crtc6845_vert_sync_pos=0;
	int crtc6845_intl_skew=0;
	int crtc6845_max_ras_addr=0;
	int crtc6845_cursor_start_ras=0;
	int crtc6845_cursor_end_ras=0;
	int crtc6845_start_addr=0;
	int crtc6845_cursor=0;
	int crtc6845_light_pen=0;
	int crtc6845_page_flip=0;		/* This seems to be present in the HD46505 */

#else
																	#endif

