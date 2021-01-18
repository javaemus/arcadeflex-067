/***************************************************************************

	Bally/Sente SAC-1 system

    driver by Aaron Giles

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package includes;

public class balsenteH
{
	
	
	/*----------- defined in machine/balsente.c -----------*/
	
	extern UINT8 balsente_shooter;
	extern UINT8 balsente_shooter_x;
	extern UINT8 balsente_shooter_y;
	extern UINT8 balsente_adc_shift;
	extern data16_t *shrike_shared;
	
	MACHINE_INIT( balsente );
	
	void balsente_noise_gen(int chip, int count, short *buffer);
	
	
	
	
	
	
	INTERRUPT_GEN( balsente_update_analog_inputs );
	
	
	
	
	
	READ16_HANDLER( shrike_shared_68k_r );
	WRITE16_HANDLER( shrike_shared_68k_w );
	
	
	/*----------- defined in vidhrdw/balsente.c -----------*/
	
	VIDEO_START( balsente );
	VIDEO_UPDATE( balsente );
	
	}
