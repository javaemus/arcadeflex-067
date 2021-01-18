#ifndef _system1_H_
#define _system1_H_

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class system1H
{
	
	#define SPR_Y_TOP		0
	#define SPR_Y_BOTTOM	1
	#define SPR_X_LO		2
	#define SPR_X_HI		3
	#define SPR_SKIP_LO		4
	#define SPR_SKIP_HI		5
	#define SPR_GFXOFS_LO	6
	#define SPR_GFXOFS_HI	7
	
	#define system1_BACKGROUND_MEMORY_SINGLE 0
	#define system1_BACKGROUND_MEMORY_BANKED 1
	
	extern unsigned char 	*system1_scroll_y;
	extern unsigned char 	*system1_scroll_x;
	extern unsigned char 	*system1_videoram;
	extern unsigned char 	*system1_backgroundram;
	extern unsigned char 	*system1_sprites_collisionram;
	extern unsigned char 	*system1_background_collisionram;
	extern unsigned char 	*system1_scrollx_ram;
	extern size_t system1_videoram_size;
	extern size_t system1_backgroundram_size;
	
	
	VIDEO_START( system1 );
	void system1_define_background_memory(int Mode);
	
	VIDEO_UPDATE( system1 );
	PALETTE_INIT( system1 );
	
	VIDEO_UPDATE( choplifter );
	VIDEO_UPDATE( wbml );
	VIDEO_UPDATE( blockgal );
	
	#endif
}
