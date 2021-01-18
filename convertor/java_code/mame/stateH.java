#ifndef _STATE_H
#define _STATE_H

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package mame;

public class stateH
{
	
	/* Initializes the save state registrations */
	
	/* Registering functions */
	void state_save_register_UINT8 (const char *module, int instance,
									const char *name, UINT8 *val, unsigned size);
	void state_save_register_INT8  (const char *module, int instance,
									const char *name, INT8 *val, unsigned size);
	void state_save_register_UINT16(const char *module, int instance,
									const char *name, UINT16 *val, unsigned size);
	void state_save_register_INT16 (const char *module, int instance,
									const char *name, INT16 *val, unsigned size);
	void state_save_register_UINT32(const char *module, int instance,
									const char *name, UINT32 *val, unsigned size);
	void state_save_register_INT32 (const char *module, int instance,
									const char *name, INT32 *val, unsigned size);
	void state_save_register_double(const char *module, int instance,
									const char *name, double *val, unsigned size);
	void state_save_register_float (const char *module, int instance,
									const char *name, float *val, unsigned size);
	void state_save_register_int   (const char *module, int instance,
									const char *name, int *val);
	
	
	void state_save_register_func_presave(void (*func)(void));
	void state_save_register_func_postload(void (*func)(void));
	
	/* Save and load functions */
	/* The tags are a hack around the current cpu structures */
	void state_save_save_begin(mame_file *file);
	int  state_save_load_begin(mame_file *file);
	
	void state_save_set_current_tag(int tag);
	
	
	/* Display function */
	
	#endif
}
