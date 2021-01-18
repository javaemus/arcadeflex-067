/***************************************************************************

	Generic (PC-style) IDE controller implementation

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class idectrlH
{
	
	
	#define MAX_IDE_CONTROLLERS			1
	
	struct ide_interface
	{
		void 	(*interrupt)(int state);
	};
	
	int ide_controller_init(int which, struct ide_interface *intf);
	void ide_controller_reset(int which);
	UINT8 *ide_get_features(int which);
	
	READ32_HANDLER( ide_controller32_0_r );
	WRITE32_HANDLER( ide_controller32_0_w );
	
	READ16_HANDLER( ide_controller16_0_r );
	WRITE16_HANDLER( ide_controller16_0_w );
}
