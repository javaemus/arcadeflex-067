/***************************************************************************

	Exidy Car Polo hardware

	driver by Zsolt Vasvari

****************************************************************************/

/* defined in machine/carpolo.c */

MACHINE_INIT( carpolo );



INTERRUPT_GEN( carpolo_timer_interrupt );


void carpolo_generate_car_car_interrupt(int car1, int car2);
void carpolo_generate_ball_screen_interrupt(data8_t cause);
void carpolo_generate_car_goal_interrupt(int car, int right_goal);
void carpolo_generate_car_ball_interrupt(int car, int car_x, int car_y);
void carpolo_generate_car_border_interrupt(int car, int horizontal_border);


/* defined in vidhrdw/carpolo.c */

extern data8_t *carpolo_alpharam;
extern data8_t *carpolo_spriteram;

PALETTE_INIT( carpolo );
VIDEO_START( carpolo );
VIDEO_UPDATE( carpolo );
VIDEO_EOF( carpolo );
