/***************************************************************************

	sndhrdw/mcr.c

	Functions to emulate general the various MCR sound cards.

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class mcrH
{
	
	
	
	/************ Generic MCR routines ***************/
	
	
	void ssio_reset_w(int state);
	
	void csdeluxe_reset_w(int state);
	
	void turbocs_reset_w(int state);
	
	void soundsgood_reset_w(int state);
	
	void squawkntalk_reset_w(int state);
	
	
	
	/************ Sound Configuration ***************/
	
	extern UINT8 mcr_sound_config;
	
	#define MCR_SSIO				0x01
	#define MCR_CHIP_SQUEAK_DELUXE	0x02
	#define MCR_SOUNDS_GOOD			0x04
	#define MCR_TURBO_CHIP_SQUEAK	0x08
	#define MCR_SQUAWK_N_TALK		0x10
	#define MCR_WILLIAMS_SOUND		0x20
	
	#define MCR_CONFIGURE_SOUND(x) \
		mcr_sound_config = x
	
	
	
	/************ External definitions ***************/
	
	MACHINE_DRIVER_EXTERN( mcr_ssio );
	MACHINE_DRIVER_EXTERN( chip_squeak_deluxe );
	MACHINE_DRIVER_EXTERN( sounds_good );
	MACHINE_DRIVER_EXTERN( turbo_chip_squeak );
	MACHINE_DRIVER_EXTERN( turbo_chip_squeak_plus_sounds_good );
	MACHINE_DRIVER_EXTERN( squawk_n_talk );
}
