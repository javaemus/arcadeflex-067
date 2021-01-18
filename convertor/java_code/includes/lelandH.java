/*************************************************************************

	Cinemat/Leland driver

*************************************************************************/

#define LELAND_BATTERY_RAM_SIZE 0x4000
#define ATAXX_EXTRA_TRAM_SIZE 0x800


/*----------- defined in machine/leland.c -----------*/

#define SERIAL_TYPE_NONE		0
#define SERIAL_TYPE_ADD			1
#define SERIAL_TYPE_ADD_XOR		2
#define SERIAL_TYPE_ENCRYPT		3
#define SERIAL_TYPE_ENCRYPT_XOR	4

extern UINT8 leland_dac_control;


extern UINT8 *alleymas_kludge_mem;






MACHINE_INIT( leland );
MACHINE_INIT( ataxx );

INTERRUPT_GEN( leland_master_interrupt );


void leland_init_eeprom(UINT8 default_val, const UINT16 *data, UINT8 serial_offset, UINT8 serial_type);
void ataxx_init_eeprom(UINT8 default_val, const UINT16 *data, UINT8 serial_offset);


NVRAM_HANDLER( leland );
NVRAM_HANDLER( ataxx );







void leland_rotate_memory(int cpunum);


/*----------- defined in sndhrdw/leland.c -----------*/

int leland_sh_start(const struct MachineSound *msound);
void leland_dac_update(int dacnum, UINT8 sample);

int leland_i186_sh_start(const struct MachineSound *msound);
int redline_i186_sh_start(const struct MachineSound *msound);



void leland_i86_optimize_address(offs_t offset);


extern const struct Memory_ReadAddress leland_i86_readmem[];
extern const struct Memory_WriteAddress leland_i86_writemem[];

extern const struct IO_ReadPort leland_i86_readport[];

extern const struct IO_WritePort redline_i86_writeport[];
extern const struct IO_WritePort leland_i86_writeport[];
extern const struct IO_WritePort ataxx_i86_writeport[];


/*----------- defined in vidhrdw/leland.c -----------*/

extern UINT8 *ataxx_qram;
extern UINT8 leland_last_scanline_int;

VIDEO_START( leland );
VIDEO_START( ataxx );





VIDEO_EOF( leland );
VIDEO_UPDATE( leland );
VIDEO_UPDATE( ataxx );
