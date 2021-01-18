/*************************************************************************

	8080bw.h

*************************************************************************/

/*----------- defined in machine/8080bw.c -----------*/

INTERRUPT_GEN( c8080bw_interrupt );







/*----------- defined in sndhrdw/8080bw.c -----------*/

MACHINE_INIT( invaders );
MACHINE_INIT( sstrangr );
MACHINE_INIT( invad2ct );
MACHINE_INIT( sheriff );
MACHINE_INIT( gunfight );
MACHINE_INIT( boothill );
MACHINE_INIT( helifire );
MACHINE_INIT( phantom2 );
MACHINE_INIT( bowler );
MACHINE_INIT( ballbomb );
MACHINE_INIT( seawolf );
MACHINE_INIT( desertgu );
MACHINE_INIT( schaser );
MACHINE_INIT( polaris );
MACHINE_INIT( clowns );


extern struct SN76477interface invaders_sn76477_interface;
extern struct Samplesinterface invaders_samples_interface;
extern struct SN76477interface invad2ct_sn76477_interface;
extern struct Samplesinterface invad2ct_samples_interface;
extern struct DACinterface sheriff_dac_interface;
extern struct SN76477interface sheriff_sn76477_interface;
extern struct Samplesinterface boothill_samples_interface;
extern struct DACinterface schaser_dac_interface;
extern struct CustomSound_interface schaser_custom_interface;
extern struct SN76477interface schaser_sn76477_interface;
extern struct Samplesinterface seawolf_samples_interface;
extern struct discrete_sound_block polaris_sound_interface[];


/*----------- defined in vidhrdw/8080bw.c -----------*/

DRIVER_INIT( 8080bw );
DRIVER_INIT( invaders );
DRIVER_INIT( invadpt2 );
DRIVER_INIT( sstrngr2 );
DRIVER_INIT( invaddlx );
DRIVER_INIT( invrvnge );
DRIVER_INIT( invad2ct );
DRIVER_INIT( schaser );
DRIVER_INIT( rollingc );
DRIVER_INIT( polaris );
DRIVER_INIT( lupin3 );
DRIVER_INIT( seawolf );
DRIVER_INIT( blueshrk );
DRIVER_INIT( desertgu );
DRIVER_INIT( helifire );
DRIVER_INIT( phantom2 );
DRIVER_INIT( bowler );
DRIVER_INIT( gunfight );
DRIVER_INIT( bandido );

void c8080bw_flip_screen_w(int data);
void c8080bw_screen_red_w(int data);

INTERRUPT_GEN( polaris_interrupt );
INTERRUPT_GEN( phantom2_interrupt );


VIDEO_UPDATE( 8080bw );

PALETTE_INIT( invadpt2 );
PALETTE_INIT( helifire );

