/****************************************************************************

	Preliminary driver for Samurai, Nunchackun, Yuke Yuke Yamaguchi-kun
	(c) Taito 1985

	Known Issues:
	- some color problems (need screenshots)
	- Nunchackun has wrong colors; sprites look better if you subtract sprite color from 0x2d
	- Yuke Yuke Yamaguchi-kun isn't playable (sprite problem only?)

driver by Phil Stroffolino

Mission 660 extensions by Paul Swan (swan@easynet.co.uk)
--------------------------------------------------------
The game appears to use the same video board as Samurai et al. There is a
character column scroll feature that I have added. Its used to scroll in
the "660" logo on the title screen at the beginning. Not sure if Samurai
at al use it but it's likely their boards have the feature.	Extra banking
of the forground is done using an extra register. A bit in the background
video RAM selects the additional background character space.

The sound board is similar. There are 3 CPU's instead of the 2 of Samurai.
2 are still used for sample-like playback (as Samurai) and the other is used
to drive the AY-3-8910 (that was driven directly by the video CPU on Samurai).
The memory maps are different over Samurai, probably to allow larger capacity
ROMS though only the AY driver uses 27256 on Mission 660. A picture of the board
I have suggests that the AY CPU could have a DAC as well but the circuit is not
populated on Mission 660.

There is some kind of protection. There is code in there to do the same "unknown"
reads and writes as Samurai and the original M660 won't run with the Samurai value.
The bootleg M660 has the protection code patched out to use a fixed value. I've
used this same value on the original M660 and it seems to work.

I'm guessing the bootleg is of a "world" release and the original is from
the "America" release.

TODO:
1) Colours.
2) A few unknown regs.

****************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class tsamurai
{
	
	
	
	VIDEO_UPDATE( tsamurai );
	extern VIDEO_START( tsamurai );
	extern unsigned char *tsamurai_videoram;
	
	extern VIDEO_START( vsgongf );
	extern VIDEO_UPDATE( vsgongf );
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1, /* number of chips */
		2000000, /* 2 MHz */
		new int[] { 10 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static DACinterface dac_interface = new DACinterface
	(
		2,			/* number of chips */
		new int[] { 20, 20 }
	);
	
	static DACinterface vsgongf_dac_interface = new DACinterface
	(
		1,			/* number of chips */
		new int[] { 20 }
	);
	
	static int nmi_enabled;
	static int sound_command1, sound_command2, sound_command3;
	
	public static WriteHandlerPtr nmi_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		nmi_enabled = data;
	} };
	
	static INTERRUPT_GEN( samurai_interrupt ){
		if (nmi_enabled) cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
	}
	
	public static ReadHandlerPtr unknown_d803_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0x6b;
	} };
	
	public static ReadHandlerPtr unknown_d803_m660_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0x53;     // this is what the bootleg patches in.
	} };
	
	public static ReadHandlerPtr unknown_d806_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0x40;
	} };
	
	public static ReadHandlerPtr unknown_d900_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0x6a;
	} };
	
	public static ReadHandlerPtr unknown_d938_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0xfb;
	} };
	
	public static WriteHandlerPtr sound_command1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sound_command1 = data;
		cpu_set_irq_line( 1, 0, HOLD_LINE );
	} };
	
	public static WriteHandlerPtr sound_command2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sound_command2 = data;
		cpu_set_irq_line( 2, 0, HOLD_LINE );
	} };
	
	public static WriteHandlerPtr sound_command3_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		sound_command3 = data;
		cpu_set_irq_line( 3, 0, HOLD_LINE );
	} };
	
	public static WriteHandlerPtr flip_screen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		flip_screen_set(data);
	} };
	
	public static WriteHandlerPtr tsamurai_coin_counter_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		coin_counter_w(offset,data);
	} };
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xcfff, MRA_RAM ),
	
		/* protection? - there are writes as well...*/
		new Memory_ReadAddress( 0xd803, 0xd803, unknown_d803_r ),
		new Memory_ReadAddress( 0xd806, 0xd806, unknown_d806_r ),
		new Memory_ReadAddress( 0xd900, 0xd900, unknown_d900_r ),
		new Memory_ReadAddress( 0xd938, 0xd938, unknown_d938_r ),
	
		new Memory_ReadAddress( 0xe000, 0xe3ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe400, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe800, 0xefff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xf3ff, MRA_RAM ),
	
		new Memory_ReadAddress( 0xf800, 0xf800, input_port_0_r ),
		new Memory_ReadAddress( 0xf801, 0xf801, input_port_1_r ),
		new Memory_ReadAddress( 0xf802, 0xf802, input_port_2_r ),
		new Memory_ReadAddress( 0xf804, 0xf804, input_port_3_r ),
		new Memory_ReadAddress( 0xf805, 0xf805, input_port_4_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xcfff, MWA_RAM ),
	
		new Memory_WriteAddress( 0xe000, 0xe3ff, tsamurai_fg_videoram_w, videoram ),
		new Memory_WriteAddress( 0xe400, 0xe43f, tsamurai_fg_colorram_w, colorram ),
		new Memory_WriteAddress( 0xe440, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xefff, tsamurai_bg_videoram_w, tsamurai_videoram ),
		new Memory_WriteAddress( 0xf000, 0xf3ff, MWA_RAM, spriteram ),
	
		new Memory_WriteAddress( 0xf400, 0xf400, MWA_NOP ),
		new Memory_WriteAddress( 0xf401, 0xf401, sound_command1_w ),
		new Memory_WriteAddress( 0xf402, 0xf402, sound_command2_w ),
	
		new Memory_WriteAddress( 0xf801, 0xf801, tsamurai_bgcolor_w ),
		new Memory_WriteAddress( 0xf802, 0xf802, tsamurai_scrolly_w ),
		new Memory_WriteAddress( 0xf803, 0xf803, tsamurai_scrollx_w ),
	
		new Memory_WriteAddress( 0xfc00, 0xfc00, flip_screen_w ),
		new Memory_WriteAddress( 0xfc01, 0xfc01, nmi_enable_w ),
		new Memory_WriteAddress( 0xfc02, 0xfc02, tsamurai_textbank1_w ),
		new Memory_WriteAddress( 0xfc03, 0xfc04, tsamurai_coin_counter_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_m660[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xcfff, MRA_RAM ),
	
		/* protection? - there are writes as well...*/
		new Memory_ReadAddress( 0xd803, 0xd803, unknown_d803_m660_r ),
		new Memory_ReadAddress( 0xd806, 0xd806, unknown_d806_r ),
		new Memory_ReadAddress( 0xd900, 0xd900, unknown_d900_r ),
		new Memory_ReadAddress( 0xd938, 0xd938, unknown_d938_r ),
	
		new Memory_ReadAddress( 0xe000, 0xe3ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe400, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe800, 0xefff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xf3ff, MRA_RAM ),
	
		new Memory_ReadAddress( 0xf800, 0xf800, input_port_0_r ),
		new Memory_ReadAddress( 0xf801, 0xf801, input_port_1_r ),
		new Memory_ReadAddress( 0xf802, 0xf802, input_port_2_r ),
		new Memory_ReadAddress( 0xf804, 0xf804, input_port_3_r ),
		new Memory_ReadAddress( 0xf805, 0xf805, input_port_4_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_m660[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xcfff, MWA_RAM ),
	
		new Memory_WriteAddress( 0xe000, 0xe3ff, tsamurai_fg_videoram_w, videoram ),
		new Memory_WriteAddress( 0xe400, 0xe43f, tsamurai_fg_colorram_w, colorram ),
		new Memory_WriteAddress( 0xe440, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xefff, tsamurai_bg_videoram_w, tsamurai_videoram ),
		new Memory_WriteAddress( 0xf000, 0xf3ff, MWA_RAM, spriteram ),
	
		new Memory_WriteAddress( 0xf400, 0xf400, MWA_NOP ),/* This is always written with F401, F402  F403 data */
		new Memory_WriteAddress( 0xf401, 0xf401, sound_command3_w ),
		new Memory_WriteAddress( 0xf402, 0xf402, sound_command2_w ),
		new Memory_WriteAddress( 0xf403, 0xf403, sound_command1_w ),
	
	
		new Memory_WriteAddress( 0xf801, 0xf801, tsamurai_bgcolor_w ),
		new Memory_WriteAddress( 0xf802, 0xf802, tsamurai_scrolly_w ),
		new Memory_WriteAddress( 0xf803, 0xf803, tsamurai_scrollx_w ),
	
		new Memory_WriteAddress( 0xfc00, 0xfc00, flip_screen_w ),
		new Memory_WriteAddress( 0xfc01, 0xfc01, nmi_enable_w ),
		new Memory_WriteAddress( 0xfc02, 0xfc02, tsamurai_textbank1_w ),
		new Memory_WriteAddress( 0xfc03, 0xfc04, tsamurai_coin_counter_w ),
		new Memory_WriteAddress( 0xfc07, 0xfc07, tsamurai_textbank2_w ),/* Mission 660 uses a bit here */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort z80_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort z80_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, AY8910_control_port_0_w ),
		new IO_WritePort( 0x01, 0x01, AY8910_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort z80_writeport_m660[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, MWA_NOP ),		       /* ? */
		new IO_WritePort( 0x01, 0x01, MWA_NOP ),               /* Written continuously. Increments with level. */
		new IO_WritePort( 0x02, 0x02, MWA_NOP ),               /* Always follows above with 0x01 data */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static ReadHandlerPtr sound_command1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return sound_command1;
	} };
	
	public static WriteHandlerPtr sound_out1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		DAC_data_w(0,data);
	} };
	
	public static ReadHandlerPtr sound_command2_r  = new ReadHandlerPtr() { public int handler(int offset){
		return sound_command2;
	} };
	
	public static WriteHandlerPtr sound_out2_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		DAC_data_w(1,data);
	} };
	
	public static ReadHandlerPtr sound_command3_r  = new ReadHandlerPtr() { public int handler(int offset){
		return sound_command3;
	} };
	
	/*******************************************************************************/
	public static Memory_ReadAddress readmem_sound1[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x6000, sound_command1_r ),
		new Memory_ReadAddress( 0x7f00, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound1[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6001, 0x6001, MWA_NOP ), /* ? - probably clear IRQ */
		new Memory_WriteAddress( 0x6002, 0x6002, sound_out1_w ),
		new Memory_WriteAddress( 0x7f00, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/*******************************************************************************/
	
	public static Memory_ReadAddress readmem_sound2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x6000, sound_command2_r ),
		new Memory_ReadAddress( 0x7f00, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6001, 0x6001, MWA_NOP ), /* ? - probably clear IRQ */
		new Memory_WriteAddress( 0x6002, 0x6002, sound_out2_w ),
		new Memory_WriteAddress( 0x7f00, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/*******************************************************************************/
	
	public static Memory_ReadAddress readmem_sound1_m660[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc000, sound_command1_r ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound1_m660[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc001, 0xc001, MWA_NOP ), /* ? - probably clear IRQ */
		new Memory_WriteAddress( 0xc002, 0xc002, sound_out1_w ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/*******************************************************************************/
	
	public static Memory_ReadAddress readmem_sound2_m660[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc000, sound_command2_r ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound2_m660[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc001, 0xc001, MWA_NOP ), /* ? - probably clear IRQ */
		new Memory_WriteAddress( 0xc002, 0xc002, sound_out2_w ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/*******************************************************************************/
	
	public static IO_ReadPort readport_sound3_m660[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_sound3_m660[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, AY8910_control_port_0_w ),
		new IO_WritePort( 0x01, 0x01, AY8910_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_sound3_m660[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc000, sound_command3_r ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound3_m660[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc001, 0xc001, MWA_NOP ), /* ? - probably clear IRQ */
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/*******************************************************************************/
	
	static int vsgongf_sound_nmi_enabled;
	public static WriteHandlerPtr vsgongf_sound_nmi_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		vsgongf_sound_nmi_enabled = data;
	} };
	
	static INTERRUPT_GEN( vsgongf_sound_interrupt ){
		if (vsgongf_sound_nmi_enabled) cpu_set_irq_line(1, IRQ_LINE_NMI, PULSE_LINE);
	}
	
	public static ReadHandlerPtr vsgongf_a006_r  = new ReadHandlerPtr() { public int handler(int offset){
		return 0x80; /* sound CPU busy? */
	} };
	
	public static ReadHandlerPtr vsgongf_a100_r  = new ReadHandlerPtr() { public int handler(int offset){
		return 0xaa;
	} };
	
	public static WriteHandlerPtr vsgongf_sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		soundlatch_w( offset, data );
		cpu_set_irq_line( 1, IRQ_LINE_NMI, PULSE_LINE );
	} };
	
	public static Memory_ReadAddress readmem_vsgongf[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa003, 0xa003, MRA_RAM ),
		new Memory_ReadAddress( 0xa006, 0xa006, vsgongf_a006_r ), /* protection */
		new Memory_ReadAddress( 0xa100, 0xa100, vsgongf_a100_r ), /* protection */
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ), /* work ram */
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xf800, input_port_0_r ), /* joy1 */
		new Memory_ReadAddress( 0xf801, 0xf801, input_port_1_r ), /* joy2 */
		new Memory_ReadAddress( 0xf802, 0xf802, input_port_2_r ), /* coin */
		new Memory_ReadAddress( 0xf804, 0xf804, input_port_3_r ), /* dsw1 */
		new Memory_ReadAddress( 0xf805, 0xf805, input_port_4_r ), /* dsw2 */
		new Memory_ReadAddress( 0xfc00, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_vsgongf[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ), /* work ram */
		new Memory_WriteAddress( 0xe000, 0xe3ff, tsamurai_fg_videoram_w, videoram ),
		new Memory_WriteAddress( 0xe400, 0xe43f, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0xe440, 0xe47b, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xe800, vsgongf_sound_command_w ),
		new Memory_WriteAddress( 0xec00, 0xec06, MWA_RAM ),
		new Memory_WriteAddress( 0xf000, 0xf000, vsgongf_color_w ),
		new Memory_WriteAddress( 0xf400, 0xf400, MWA_RAM ), /* vreg? always 0 */
		new Memory_WriteAddress( 0xf800, 0xf800, MWA_RAM ),
		new Memory_WriteAddress( 0xf801, 0xf801, MWA_RAM ), /* vreg? always 0 */
		new Memory_WriteAddress( 0xf803, 0xf803, MWA_RAM ), /* vreg? always 0 */
		new Memory_WriteAddress( 0xfc00, 0xfc00, MWA_RAM ), /* vreg? always 0 */
		new Memory_WriteAddress( 0xfc01, 0xfc01, nmi_enable_w ),
		new Memory_WriteAddress( 0xfc02, 0xfc03, tsamurai_coin_counter_w ),
		new Memory_WriteAddress( 0xfc04, 0xfc04, tsamurai_textbank1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_sound_vsgongf[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x63ff, MRA_RAM ), /* work RAM */
		new Memory_ReadAddress( 0x8000, 0x8000, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound_vsgongf[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x63ff, MWA_RAM ), /* work RAM */
		new Memory_WriteAddress( 0x8000, 0x8000, vsgongf_sound_nmi_enable_w ), /* NMI enable */
		new Memory_WriteAddress( 0xa000, 0xa000, sound_out1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/*******************************************************************************/
	
	static InputPortPtr input_ports_tsamurai = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SERVICE );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x07, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x38, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x40, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START();  /* DSW2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x02, "7" );
		PORT_BITX(0,        0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "254", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x0c, 0x0c, "DSW2 Unknown 1" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x04, "30" );
		PORT_DIPSETTING(    0x08, "50" );
		PORT_DIPSETTING(    0x0c, "70" );
		PORT_DIPNAME( 0x30, 0x30, "DSW2 Unknown 2" );
		PORT_DIPSETTING(    0x00, "0x00" );
		PORT_DIPSETTING(    0x10, "0x01" );
		PORT_DIPSETTING(    0x20, "0x02" );
		PORT_DIPSETTING(    0x30, "0x03" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DSW2 Unknown 3" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_nunchaku = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SERVICE );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x07, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x38, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x40, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START();  /* DSW2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x02, "7" );
		PORT_BITX(0,        0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x0c, 0x0c, "DSW2 Unknown 1" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x04, "30" );
		PORT_DIPSETTING(    0x08, "50" );
		PORT_DIPSETTING(    0x0c, "70" );
		PORT_DIPNAME( 0x30, 0x30, "DSW2 Unknown 2" );
		PORT_DIPSETTING(    0x00, "0x00" );
		PORT_DIPSETTING(    0x10, "0x01" );
		PORT_DIPSETTING(    0x20, "0x02" );
		PORT_DIPSETTING(    0x30, "0x03" );
		PORT_DIPNAME( 0x40, 0x40, "DSW2 Unknown 3" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DSW2 Unknown 4" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_vsgongf = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SERVICE );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x07, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x38, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x40, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START();  /* DSW2 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_yamagchi = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SERVICE );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x07, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x38, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x40, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START();  /* DSW2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x02, "7" );
		PORT_BITX(0,        0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x0c, 0x0c, "DSW2 Unknown 1" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x04, "30" );
		PORT_DIPSETTING(    0x08, "50" );
		PORT_DIPSETTING(    0x0c, "70" );
		PORT_DIPNAME( 0x10, 0x10, "Language" );
		PORT_DIPSETTING(    0x10, "English" );
		PORT_DIPSETTING(    0x00, "Japanese" );
		PORT_DIPNAME( 0x20, 0x20, "DSW2 Unknown 2" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DSW2 Unknown 3" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_m660 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_SERVICE );/* service */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();  /* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x04, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x38, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x40, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "Continues" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();  /* DSW2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x0c, 0x0c, "Bonus" );
		PORT_DIPSETTING(    0x00, "10,30,50" );
		PORT_DIPSETTING(    0x04, "20,50,80" );
		PORT_DIPSETTING(    0x08, "30,70,110" );
		PORT_DIPSETTING(    0x0c, "50,100,150" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x10, "Normal" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x30, "Very hard" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR ( Cocktail );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") ); /* listed as screen flip (hardware) */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout char_layout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3), RGN_FRAC(1,3), RGN_FRAC(0,3) },
		new int[] { 0,1,2,3, 4,5,6,7 },
		new int[] { 0*8,1*8,2*8,3*8,4*8,5*8,6*8,7*8 },
		8*8
	);
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		32,32,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3), RGN_FRAC(1,3), RGN_FRAC(0,3) },
		new int[] {
			0,1,2,3,4,5,6,7,
			64+0,64+1,64+2,64+3,64+4,64+5,64+6,64+7,
			128+0,128+1,128+2,128+3,128+4,128+5,128+6,128+7,
			64*3+0,64*3+1,64*3+2,64*3+3,64*3+4,64*3+5,64*3+6,64*3+7
		},
		new int[] {
			0*8,1*8,2*8,3*8,4*8,5*8,6*8,7*8,
			1*256+0*8,1*256+1*8,1*256+2*8,1*256+3*8,1*256+4*8,1*256+5*8,1*256+6*8,1*256+7*8,
			2*256+0*8,2*256+1*8,2*256+2*8,2*256+3*8,2*256+4*8,2*256+5*8,2*256+6*8,2*256+7*8,
			3*256+0*8,3*256+1*8,3*256+2*8,3*256+3*8,3*256+4*8,3*256+5*8,3*256+6*8,3*256+7*8
		},
		4*256
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3), RGN_FRAC(1,3), RGN_FRAC(0,3) },
		new int[] { 0,1,2,3,4,5,6,7 },
		new int[] { 0*8,1*8,2*8,3*8,4*8,5*8,6*8,7*8 },
		8*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tile_layout,   0, 32 ),
		new GfxDecodeInfo( REGION_GFX2, 0, char_layout,   0, 32 ),
		new GfxDecodeInfo( REGION_GFX3, 0, sprite_layout, 0, 32 ),
		new GfxDecodeInfo( -1 )
	};
	
	
	/*******************************************************************************/
	
	static MACHINE_DRIVER_START( tsamurai )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(z80_readport,z80_writeport)
		MDRV_CPU_VBLANK_INT(samurai_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 2000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(readmem_sound1,writemem_sound1)
	
		MDRV_CPU_ADD(Z80, 2000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(readmem_sound2,writemem_sound2)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0, 255, 8, 255-8)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(tsamurai)
		MDRV_VIDEO_UPDATE(tsamurai)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( m660 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_MEMORY(readmem_m660,writemem_m660)
		MDRV_CPU_PORTS(z80_readport,z80_writeport_m660)
		MDRV_CPU_VBLANK_INT(samurai_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 2000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(readmem_sound1_m660,writemem_sound1_m660)
	
		MDRV_CPU_ADD(Z80, 2000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(readmem_sound2_m660,writemem_sound2_m660)
	
		MDRV_CPU_ADD(Z80, 2000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(readmem_sound3_m660,writemem_sound3_m660)
		MDRV_CPU_PORTS(readport_sound3_m660,writeport_sound3_m660)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0, 255, 8, 255-8)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(tsamurai)
		MDRV_VIDEO_UPDATE(tsamurai)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( vsgongf )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_MEMORY(readmem_vsgongf,writemem_vsgongf)
		MDRV_CPU_VBLANK_INT(samurai_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_MEMORY(readmem_sound_vsgongf,writemem_sound_vsgongf)
		MDRV_CPU_PORTS(0,z80_writeport)
		MDRV_CPU_VBLANK_INT(vsgongf_sound_interrupt,3)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0, 255, 8, 255-8)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(vsgongf)
		MDRV_VIDEO_UPDATE(vsgongf)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(DAC, vsgongf_dac_interface)
	MACHINE_DRIVER_END
	
	/*******************************************************************************/
	
	static RomLoadPtr rom_tsamurai = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "01.3r",      0x0000, 0x4000, 0xd09c8609 );
		ROM_LOAD( "02.3t",      0x4000, 0x4000, 0xd0f2221c );
		ROM_LOAD( "03.3v",      0x8000, 0x4000, 0xeee8b0c9 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sample player#1 */
		ROM_LOAD( "14.4e",      0x0000, 0x2000, 0x220e9c04 );
		ROM_LOAD( "a35-15.4c",  0x2000, 0x2000, 0x1e0d1e33 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code - sample player#2 */
		ROM_LOAD( "13.4j",      0x0000, 0x2000, 0x73feb0e2 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "a35-04.10a", 0x0000, 0x2000, 0xb97ce9b1 );// tiles
		ROM_LOAD( "a35-05.10b", 0x2000, 0x2000, 0x55a17b08 );
		ROM_LOAD( "a35-06.10d", 0x4000, 0x2000, 0xf5ee6f8f );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "a35-10.11n", 0x0000, 0x1000, 0x0b5a0c45 );// characters
		ROM_LOAD( "a35-11.11q", 0x2000, 0x1000, 0x93346d75 );
		ROM_LOAD( "a35-12.11r", 0x4000, 0x1000, 0xf4c69d8a );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "a35-07.12h", 0x0000, 0x4000, 0x38fc349f );// sprites
		ROM_LOAD( "a35-08.12j", 0x4000, 0x4000, 0xa07d6dc3 );
		ROM_LOAD( "a35-09.12k", 0x8000, 0x4000, 0xc0784a0e );
	
		ROM_REGION( 0x300, REGION_PROMS, 0 );
		ROM_LOAD( "a35-16.2j",  0x000, 0x0100, 0x72d8b332 );
		ROM_LOAD( "a35-17.2l",  0x100, 0x0100, 0x9bf1829e );
		ROM_LOAD( "a35-18.2m",  0x200, 0x0100, 0x918e4732 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_tsamura2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "a35-01.3r",  0x0000, 0x4000, 0x282d96ad );
		ROM_LOAD( "a35-02.3t",  0x4000, 0x4000, 0xe3fa0cfa );
		ROM_LOAD( "a35-03.3v",  0x8000, 0x4000, 0x2fff1e0a );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sample player#1 */
		ROM_LOAD( "a35-14.4e",  0x0000, 0x2000, 0xf10aee3b );
		ROM_LOAD( "a35-15.4c",  0x2000, 0x2000, 0x1e0d1e33 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code - sample player#2 */
		ROM_LOAD( "a35-13.4j",  0x0000, 0x2000, 0x3828f4d2 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "a35-04.10a", 0x0000, 0x2000, 0xb97ce9b1 );// tiles
		ROM_LOAD( "a35-05.10b", 0x2000, 0x2000, 0x55a17b08 );
		ROM_LOAD( "a35-06.10d", 0x4000, 0x2000, 0xf5ee6f8f );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "a35-10.11n", 0x0000, 0x1000, 0x0b5a0c45 );// characters
		ROM_LOAD( "a35-11.11q", 0x2000, 0x1000, 0x93346d75 );
		ROM_LOAD( "a35-12.11r", 0x4000, 0x1000, 0xf4c69d8a );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "a35-07.12h", 0x0000, 0x4000, 0x38fc349f );// sprites
		ROM_LOAD( "a35-08.12j", 0x4000, 0x4000, 0xa07d6dc3 );
		ROM_LOAD( "a35-09.12k", 0x8000, 0x4000, 0xc0784a0e );
	
		ROM_REGION( 0x300, REGION_PROMS, 0 );
		ROM_LOAD( "a35-16.2j",  0x000, 0x0100, 0x72d8b332 );
		ROM_LOAD( "a35-17.2l",  0x100, 0x0100, 0x9bf1829e );
		ROM_LOAD( "a35-18.2m",  0x200, 0x0100, 0x918e4732 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_nunchaku = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "nunchack.p1", 0x0000, 0x4000, 0x4385aca6 );
		ROM_LOAD( "nunchack.p2", 0x4000, 0x4000, 0xf9beb72c );
		ROM_LOAD( "nunchack.p3", 0x8000, 0x4000, 0xcde5d674 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sample player */
		ROM_LOAD( "nunchack.m3", 0x0000, 0x2000, 0x9036c945 );
		ROM_LOAD( "nunchack.m4", 0x2000, 0x2000, 0xe7206724 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code - sample player */
		ROM_LOAD( "nunchack.m1", 0x0000, 0x2000, 0xb53d73f6 );
		ROM_LOAD( "nunchack.m2", 0x2000, 0x2000, 0xf37d7c49 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "nunchack.b1", 0x0000, 0x2000, 0x48c88fea );// tiles
		ROM_LOAD( "nunchack.b2", 0x2000, 0x2000, 0xeec818e4 );
		ROM_LOAD( "nunchack.b3", 0x4000, 0x2000, 0x5f16473f );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "nunchack.v1", 0x0000, 0x1000, 0x358a3714 );// characters
		ROM_LOAD( "nunchack.v2", 0x2000, 0x1000, 0x54c18d8e );
		ROM_LOAD( "nunchack.v3", 0x4000, 0x1000, 0xf7ac203a );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "nunchack.c1", 0x0000, 0x4000, 0x797cbc8a );// sprites
		ROM_LOAD( "nunchack.c2", 0x4000, 0x4000, 0x701a0cc3 );
		ROM_LOAD( "nunchack.c3", 0x8000, 0x4000, 0xffb841fc );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "nunchack.016", 0x000, 0x100, 0xa7b077d4 );
		ROM_LOAD( "nunchack.017", 0x100, 0x100, 0x1c04c087 );
		ROM_LOAD( "nunchack.018", 0x200, 0x100, 0xf5ce3c45 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_yamagchi = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "a38-01.3s", 0x0000, 0x4000, 0x1a6c8498 );
		ROM_LOAD( "a38-02.3t", 0x4000, 0x4000, 0xfa66b396 );
		ROM_LOAD( "a38-03.3v", 0x8000, 0x4000, 0x6a4239cf );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sample player */
		ROM_LOAD( "a38-14.4e", 0x0000, 0x2000, 0x5a758992 );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code - sample player */
		ROM_LOAD( "a38-13.4j", 0x0000, 0x2000, 0xa26445bb );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "a38-04.10a", 0x0000, 0x2000, 0x6bc69d4d );// tiles
		ROM_LOAD( "a38-05.10b", 0x2000, 0x2000, 0x047fb315 );
		ROM_LOAD( "a38-06.10d", 0x4000, 0x2000, 0xa636afb2 );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "a38-10.11n", 0x0000, 0x1000, 0x51ab4671 );// characters
		ROM_LOAD( "a38-11.11p", 0x2000, 0x1000, 0x27890169 );
		ROM_LOAD( "a38-12.11r", 0x4000, 0x1000, 0xc98d5cf2 );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "a38-07.12h", 0x0000, 0x4000, 0xa3a521b6 );// sprites
		ROM_LOAD( "a38-08.12j", 0x4000, 0x4000, 0x553afc66 );
		ROM_LOAD( "a38-09.12l", 0x8000, 0x4000, 0x574156ae );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "mb7114e.2k", 0x000, 0x100, 0xe7648110 );
		ROM_LOAD( "mb7114e.2l", 0x100, 0x100, 0x7b874ee6 );
		ROM_LOAD( "mb7114e.2m", 0x200, 0x100, 0x938d0fce );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_m660 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "660l.bin",    0x0000, 0x4000, 0x57c0d1cc );
		ROM_LOAD( "660m.bin",    0x4000, 0x4000, 0x628c6686 );
		ROM_LOAD( "660n.bin",    0x8000, 0x4000, 0x1b418a97 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sample player */
		ROM_LOAD( "14.4n",       0x0000, 0x4000, 0x5734db5a );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code - sample player */
		ROM_LOAD( "13.4j",       0x0000, 0x4000, 0xfba51cf7 );
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 );/* Z80 code AY driver */
		ROM_LOAD( "660x.bin",    0x0000, 0x8000, 0xb82f0cfa );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "4.7k",        0x0000, 0x4000, 0xe24e431a );// tiles
		ROM_LOAD( "5.6k",        0x4000, 0x4000, 0xb2c93d46 );
		ROM_LOAD( "6.5k",        0x8000, 0x4000, 0x763c5983 );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "660u.bin",    0x0000, 0x2000, 0x030af716 );// characters
		ROM_LOAD( "660v.bin",    0x2000, 0x2000, 0x51a6e160 );
		ROM_LOAD( "660w.bin",    0x4000, 0x2000, 0x8a45b469 );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "7.15e",       0x0000, 0x4000, 0x990c0cee );// sprites
		ROM_LOAD( "8.15d",       0x4000, 0x4000, 0xd9aa7834 );
		ROM_LOAD( "9.15b",       0x8000, 0x4000, 0x27b26905 );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "4r.bpr",      0x000, 0x100, 0xcd16d0f1 );
		ROM_LOAD( "4p.bpr",      0x100, 0x100, 0x22e8b22c );
		ROM_LOAD( "5r.bpr",      0x200, 0x100, 0xb7d6fdb5 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_m660j = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "1.3c",        0x0000, 0x4000, 0x4c8f96aa );
		ROM_LOAD( "2.3d",        0x4000, 0x4000, 0xe6661504 );
		ROM_LOAD( "3.3f",        0x8000, 0x4000, 0x3a389ccd );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sample player */
		ROM_LOAD( "14.4n",       0x0000, 0x4000, 0x5734db5a );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code - sample player */
		ROM_LOAD( "13.4j",       0x0000, 0x4000, 0xfba51cf7 );
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 );/* Z80 code AY driver */
		ROM_LOAD( "d.4e",        0x0000, 0x4000, 0x93f3d852 );
		ROM_LOAD( "e.4d",        0x4000, 0x4000, 0x12f5c077 );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "4.7k",        0x0000, 0x4000, 0xe24e431a );// tiles
		ROM_LOAD( "5.6k",        0x4000, 0x4000, 0xb2c93d46 );
		ROM_LOAD( "6.5k",        0x8000, 0x4000, 0x763c5983 );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "a.16j",       0x0000, 0x2000, 0x06f44c8c );// characters
		ROM_LOAD( "b.16k",       0x2000, 0x2000, 0x94b8b69f );
		ROM_LOAD( "c.16m",       0x4000, 0x2000, 0xd6768c68 );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "7.15e",       0x0000, 0x4000, 0x990c0cee );// sprites
		ROM_LOAD( "8.15d",       0x4000, 0x4000, 0xd9aa7834 );
		ROM_LOAD( "9.15b",       0x8000, 0x4000, 0x27b26905 );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "4r.bpr",      0x000, 0x100, 0xcd16d0f1 );
		ROM_LOAD( "4p.bpr",      0x100, 0x100, 0x22e8b22c );
		ROM_LOAD( "5r.bpr",      0x200, 0x100, 0xb7d6fdb5 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_m660b = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "m660-1.bin",  0x0000, 0x4000, 0x18f6c4be );
		ROM_LOAD( "2.3d",        0x4000, 0x4000, 0xe6661504 );
		ROM_LOAD( "3.3f",        0x8000, 0x4000, 0x3a389ccd );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sample player */
		ROM_LOAD( "14.4n",       0x0000, 0x4000, 0x5734db5a );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code - sample player */
		ROM_LOAD( "13.4j",       0x0000, 0x4000, 0xfba51cf7 );
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 );/* Z80 code AY driver */
		ROM_LOAD( "660x.bin",    0x0000, 0x8000, 0xb82f0cfa );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "4.7k",        0x0000, 0x4000, 0xe24e431a );// tiles
		ROM_LOAD( "5.6k",        0x4000, 0x4000, 0xb2c93d46 );
		ROM_LOAD( "6.5k",        0x8000, 0x4000, 0x763c5983 );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "m660-10.bin", 0x0000, 0x2000, 0xb11405a6 );// characters
		ROM_LOAD( "b.16k",       0x2000, 0x2000, 0x94b8b69f );
		ROM_LOAD( "c.16m",       0x4000, 0x2000, 0xd6768c68 );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "7.15e",       0x0000, 0x4000, 0x990c0cee );// sprites
		ROM_LOAD( "8.15d",       0x4000, 0x4000, 0xd9aa7834 );
		ROM_LOAD( "9.15b",       0x8000, 0x4000, 0x27b26905 );
	
		ROM_REGION( 0x300, REGION_PROMS, 0 );
		ROM_LOAD( "4r.bpr",      0x000, 0x100, 0xcd16d0f1 );
		ROM_LOAD( "4p.bpr",      0x100, 0x100, 0x22e8b22c );
		ROM_LOAD( "5r.bpr",      0x200, 0x100, 0xb7d6fdb5 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_alphaxz = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "az-01.bin",   0x0000, 0x4000, 0x5336f842 );
		ROM_LOAD( "az-02.bin",   0x4000, 0x4000, 0xa0779b6b );
		ROM_LOAD( "az-03.bin",   0x8000, 0x4000, 0x2797bc7b );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sample player */
		ROM_LOAD( "14.4n",       0x0000, 0x4000, 0x5734db5a );
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 );/* Z80 code - sample player */
		ROM_LOAD( "13.4j",       0x0000, 0x4000, 0xfba51cf7 );
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 );/* Z80 code AY driver */
		ROM_LOAD( "660x.bin",    0x0000, 0x8000, 0xb82f0cfa );
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "az-04.bin",   0x00000, 0x4000, 0x23da4e3d );// tiles
		ROM_LOAD( "az-05.bin",   0x04000, 0x4000, 0x8746ff69 );
		ROM_LOAD( "az-06.bin",   0x08000, 0x4000, 0x6e494964 );
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "az-10.bin",   0x00000, 0x2000, 0x10b499bb );// characters
		ROM_LOAD( "az-11.bin",   0x02000, 0x2000, 0xd91993f6 );
		ROM_LOAD( "az-12.bin",   0x04000, 0x2000, 0x8ea48ef3 );
	
		ROM_REGION( 0xc000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "az-07.bin",   0x00000, 0x4000, 0x5f9cc65e );// sprites
		ROM_LOAD( "az-08.bin",   0x04000, 0x4000, 0x23e3a6ba );
		ROM_LOAD( "az-09.bin",   0x08000, 0x4000, 0x7096fa71 );
	
		ROM_REGION( 0x300, REGION_PROMS, 0 );
		ROM_LOAD( "4r.bpr",      0x000, 0x100, 0xcd16d0f1 );
		ROM_LOAD( "4p.bpr",      0x100, 0x100, 0x22e8b22c );
		ROM_LOAD( "5r.bpr",      0x200, 0x100, 0xb7d6fdb5 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_vsgongf = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );/* Z80 code  - main CPU */
		ROM_LOAD( "1.5a",	0x0000, 0x2000, 0x2c056dee );/* good? */
		ROM_LOAD( "2",		0x2000, 0x2000, 0x1a634daf );/* good? */
		ROM_LOAD( "3.5d",	0x4000, 0x2000, 0x5ac16861 );
		ROM_LOAD( "4.5f",	0x6000, 0x2000, 0x1d1baf7b );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code - sound CPU */
		ROM_LOAD( "6.5n",	0x0000, 0x2000, 0x785b9000 );
		ROM_LOAD( "5.5l",	0x2000, 0x2000, 0x76dbfde9 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );/* tiles (N/A) */
	
		ROM_REGION( 0x3000, REGION_GFX2, ROMREGION_DISPOSE );/* characters */
		ROM_LOAD( "7.6f",	0x0000, 0x1000, 0x6ec68692 );
		ROM_LOAD( "8.7f",	0x1000, 0x1000, 0xafba16c8 );
		ROM_LOAD( "9.8f",	0x2000, 0x1000, 0x536bf710 );
	
		ROM_REGION( 0x6000, REGION_GFX3, ROMREGION_DISPOSE );/* sprites */
		ROM_LOAD( "13.15j",  0x0000, 0x2000, 0xa2451a31 );
		ROM_LOAD( "14.15h",  0x2000, 0x2000, 0xb387403e );
		ROM_LOAD( "15.15f",  0x4000, 0x2000, 0x0e649334 );
	
		ROM_REGION( 0x300, REGION_PROMS, 0 );
		ROM_LOAD( "clr.6s",  0x000, 0x0100, 0x578bfbea );
		ROM_LOAD( "clr.6r",  0x100, 0x0100, 0x3ec00739 );
		ROM_LOAD( "clr.6p",  0x200, 0x0100, 0x0e4fd17a );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_vsgongf	   = new GameDriver("1984"	,"vsgongf"	,"tsamurai.java"	,rom_vsgongf,null	,machine_driver_vsgongf	,input_ports_vsgongf	,null	,ROT90	,	"Kaneko", "VS Gong Fight", GAME_IMPERFECT_COLORS )
	public static GameDriver driver_tsamurai	   = new GameDriver("1985"	,"tsamurai"	,"tsamurai.java"	,rom_tsamurai,null	,machine_driver_tsamurai	,input_ports_tsamurai	,null	,ROT90	,	"Taito", "Samurai Nihon-ichi (set 1)" )
	public static GameDriver driver_tsamura2	   = new GameDriver("1985"	,"tsamura2"	,"tsamurai.java"	,rom_tsamura2,driver_tsamurai	,machine_driver_tsamurai	,input_ports_tsamurai	,null	,ROT90	,	"Taito", "Samurai Nihon-ichi (set 2)" )
	public static GameDriver driver_nunchaku	   = new GameDriver("1985"	,"nunchaku"	,"tsamurai.java"	,rom_nunchaku,null	,machine_driver_tsamurai	,input_ports_nunchaku	,null	,ROT90	,	"Taito", "Nunchackun", GAME_IMPERFECT_COLORS )
	public static GameDriver driver_yamagchi	   = new GameDriver("1985"	,"yamagchi"	,"tsamurai.java"	,rom_yamagchi,null	,machine_driver_tsamurai	,input_ports_yamagchi	,null	,ROT90	,	"Taito", "Go Go Mr. Yamaguchi / Yuke Yuke Yamaguchi-kun", GAME_IMPERFECT_COLORS )
	
	public static GameDriver driver_m660	   = new GameDriver("1986"	,"m660"	,"tsamurai.java"	,rom_m660,null	,machine_driver_m660	,input_ports_m660	,null	,ROT90	,	"[Wood Place] Taito America Corporation", "Mission 660 (US)" )
	public static GameDriver driver_m660j	   = new GameDriver("1986"	,"m660j"	,"tsamurai.java"	,rom_m660j,driver_m660	,machine_driver_m660	,input_ports_m660	,null	,ROT90	,	"[Wood Place] Taito Corporation", "Mission 660 (Japan)" )
	public static GameDriver driver_m660b	   = new GameDriver("1986"	,"m660b"	,"tsamurai.java"	,rom_m660b,driver_m660	,machine_driver_m660	,input_ports_m660	,null	,ROT90	,	"bootleg", "Mission 660 (bootleg)" )
	public static GameDriver driver_alphaxz	   = new GameDriver("1986"	,"alphaxz"	,"tsamurai.java"	,rom_alphaxz,driver_m660	,machine_driver_m660	,input_ports_m660	,null	,ROT90	,	"Ed/Wood Place", "The Alphax Z (Japan)" )
}
