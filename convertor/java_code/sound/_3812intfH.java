#ifndef YM3812INTF_H
#define YM3812INTF_H


#define MAX_3812 2
#define MAX_3526 MAX_3812
#define MAX_8950 2

struct YM3812interface
{
	int num;
	int baseclock;
	int mixing_level[MAX_3812];
	void (*handler[MAX_3812])(int linestate);
};

#define YM3526interface YM3812interface

struct Y8950interface
{
	int num;
	int baseclock;
	int mixing_level[MAX_8950];
	void (*handler[MAX_8950])(int linestate);

	int rom_region[MAX_8950]; /* delta-T ADPCM ROM region */

	mem_read_handler keyboardread[MAX_8950];
	mem_write_handler keyboardwrite[MAX_8950];
	mem_read_handler portread[MAX_8950];
	mem_write_handler portwrite[MAX_8950];
};


/* YM3812 */
READ_HANDLER ( YM3812_status_port_0_r );

READ_HANDLER ( YM3812_status_port_1_r );

int YM3812_sh_start(const struct MachineSound *msound);


/* YM3526 */
READ_HANDLER ( YM3526_status_port_0_r );

READ_HANDLER ( YM3526_status_port_1_r );

int YM3526_sh_start(const struct MachineSound *msound);


/* Y8950 */
READ_HANDLER ( Y8950_status_port_0_r );
READ_HANDLER ( Y8950_read_port_0_r );

READ_HANDLER ( Y8950_status_port_1_r );
READ_HANDLER ( Y8950_read_port_1_r );

int Y8950_sh_start(const struct MachineSound *msound);

#endif
