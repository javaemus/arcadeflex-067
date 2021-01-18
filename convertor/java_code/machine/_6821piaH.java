/**********************************************************************

	Motorola 6821 PIA interface and emulation

	This function emulates all the functionality of up to 4 M6821
	peripheral interface adapters.

**********************************************************************/

#ifndef PIA_6821
#define PIA_6821


#define MAX_PIA 8


/* this is the standard ordering of the registers */
/* alternate ordering swaps registers 1 and 2 */
#define	PIA_DDRA	0
#define	PIA_CTLA	1
#define	PIA_DDRB	2
#define	PIA_CTLB	3

/* PIA addressing modes */
#define PIA_STANDARD_ORDERING		0
#define PIA_ALTERNATE_ORDERING		1

#ifdef MESS
#define PIA_8BIT                    0
#define PIA_AUTOSENSE				8
#endif


struct pia6821_interface
{
	mem_read_handler in_a_func;
	mem_read_handler in_b_func;
	mem_read_handler in_ca1_func;
	mem_read_handler in_cb1_func;
	mem_read_handler in_ca2_func;
	mem_read_handler in_cb2_func;
	mem_write_handler out_a_func;
	mem_write_handler out_b_func;
	mem_write_handler out_ca2_func;
	mem_write_handler out_cb2_func;
	void (*irq_a_func)(int state);
	void (*irq_b_func)(int state);
};

#ifdef __cplusplus
extern "C" {
#endif

void pia_config(int which, int addressing, const struct pia6821_interface *intf);
int pia_read(int which, int offset);
void pia_write(int which, int offset, int data);
void pia_set_input_a(int which, int data);
void pia_set_input_ca1(int which, int data);
void pia_set_input_ca2(int which, int data);
void pia_set_input_b(int which, int data);
void pia_set_input_cb1(int which, int data);
void pia_set_input_cb2(int which, int data);

/******************* Standard 8-bit CPU interfaces, D0-D7 *******************/



/******************* Standard 16-bit CPU interfaces, D0-D7 *******************/

READ16_HANDLER( pia_0_lsb_r );
READ16_HANDLER( pia_1_lsb_r );
READ16_HANDLER( pia_2_lsb_r );
READ16_HANDLER( pia_3_lsb_r );
READ16_HANDLER( pia_4_lsb_r );
READ16_HANDLER( pia_5_lsb_r );
READ16_HANDLER( pia_6_lsb_r );
READ16_HANDLER( pia_7_lsb_r );

WRITE16_HANDLER( pia_0_lsb_w );
WRITE16_HANDLER( pia_1_lsb_w );
WRITE16_HANDLER( pia_2_lsb_w );
WRITE16_HANDLER( pia_3_lsb_w );
WRITE16_HANDLER( pia_4_lsb_w );
WRITE16_HANDLER( pia_5_lsb_w );
WRITE16_HANDLER( pia_6_lsb_w );
WRITE16_HANDLER( pia_7_lsb_w );

/******************* Standard 16-bit CPU interfaces, D8-D15 *******************/

READ16_HANDLER( pia_0_msb_r );
READ16_HANDLER( pia_1_msb_r );
READ16_HANDLER( pia_2_msb_r );
READ16_HANDLER( pia_3_msb_r );
READ16_HANDLER( pia_4_msb_r );
READ16_HANDLER( pia_5_msb_r );
READ16_HANDLER( pia_6_msb_r );
READ16_HANDLER( pia_7_msb_r );

WRITE16_HANDLER( pia_0_msb_w );
WRITE16_HANDLER( pia_1_msb_w );
WRITE16_HANDLER( pia_2_msb_w );
WRITE16_HANDLER( pia_3_msb_w );
WRITE16_HANDLER( pia_4_msb_w );
WRITE16_HANDLER( pia_5_msb_w );
WRITE16_HANDLER( pia_6_msb_w );
WRITE16_HANDLER( pia_7_msb_w );

/******************* 8-bit A/B port interfaces *******************/





/******************* 1-bit CA1/CA2/CB1/CB2 port interfaces *******************/





#ifdef __cplusplus
}
#endif

#endif
