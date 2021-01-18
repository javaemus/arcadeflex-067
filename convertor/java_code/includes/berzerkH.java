/* defined in machine/berzerk.c */

MACHINE_INIT( berzerk );
INTERRUPT_GEN( berzerk_interrupt );


/* defined in vidrhdw/berzerk.c */

extern data8_t *berzerk_magicram;

PALETTE_INIT( berzerk );


/* defined in sndhrdw/berzerk.c */

extern struct Samplesinterface berzerk_samples_interface;
extern struct CustomSound_interface berzerk_custom_interface;
