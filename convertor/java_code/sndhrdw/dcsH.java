/***************************************************************************

	Midway DCS Audio Board

****************************************************************************/

MACHINE_DRIVER_EXTERN( dcs_audio );
MACHINE_DRIVER_EXTERN( dcs_audio_uart );
MACHINE_DRIVER_EXTERN( dcs_audio_ram );


void dcs_set_notify(void (*callback)(int));
void dcs_data_w(int data);
void dcs_reset_w(int state);

