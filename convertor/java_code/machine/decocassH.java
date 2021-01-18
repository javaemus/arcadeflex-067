/* set to 1 to display tape time offset */
#define TAPE_UI_DISPLAY 0

#ifdef MAME_DEBUG
#define LOGLEVEL  0
#define LOG(n,x)  if (LOGLEVEL >= n) logerror x
#else
#define LOG(n,x)
#endif

extern extern extern extern extern extern extern extern extern extern extern extern extern extern extern double tape_time0;

extern extern extern extern extern extern extern extern extern 
extern extern 
extern MACHINE_INIT( decocass );
extern MACHINE_INIT( ctsttape );
extern MACHINE_INIT( clocknch );
extern MACHINE_INIT( ctisland );
extern MACHINE_INIT( csuperas );
extern MACHINE_INIT( castfant );
extern MACHINE_INIT( cluckypo );
extern MACHINE_INIT( cterrani );
extern MACHINE_INIT( cexplore );
extern MACHINE_INIT( cprogolf );
extern MACHINE_INIT( cmissnx );
extern MACHINE_INIT( cdiscon1 );
extern MACHINE_INIT( cptennis );
extern MACHINE_INIT( ctornado );
extern MACHINE_INIT( cbnj );
extern MACHINE_INIT( cburnrub );
extern MACHINE_INIT( cbtime );
extern MACHINE_INIT( cgraplop );
extern MACHINE_INIT( clapapa );
extern MACHINE_INIT( cfghtice );
extern MACHINE_INIT( cprobowl );
extern MACHINE_INIT( cnightst );
extern MACHINE_INIT( cprosocc );
extern MACHINE_INIT( cppicf );
extern MACHINE_INIT( cscrtry );
extern MACHINE_INIT( cbdash );

extern extern extern extern 
/* from drivers/decocass.c */
extern 
/* from vidhrdw/decocass.c */
extern extern extern extern extern extern extern extern extern extern extern 
extern extern extern extern extern extern extern extern extern extern extern extern 
extern VIDEO_START( decocass );
extern VIDEO_UPDATE( decocass );

extern unsigned char *decocass_charram;
extern unsigned char *decocass_fgvideoram;
extern unsigned char *decocass_colorram;
extern unsigned char *decocass_bgvideoram;
extern unsigned char *decocass_tileram;
extern unsigned char *decocass_objectram;
extern size_t decocass_fgvideoram_size;
extern size_t decocass_colorram_size;
extern size_t decocass_bgvideoram_size;
extern size_t decocass_tileram_size;
extern size_t decocass_objectram_size;

