package gr.codebb.arcadeflex.v067.mame;

import static gr.codebb.arcadeflex.v067.common.FuncPtr.*;

public class cpuint {
//TODO /*************************************
//TODO  *
//TODO  *	Debug logging
//TODO  *
//TODO  *************************************/
//TODO 
//TODO #define VERBOSE 0
//TODO 
//TODO #if VERBOSE
//TODO #define LOG(x)	logerror x
//TODO #else
//TODO #define LOG(x)
//TODO #endif
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	Macros to help verify active CPU
//TODO  *
//TODO  *************************************/
//TODO 
//TODO #define VERIFY_ACTIVECPU(retval, name)						\
//TODO 	int activecpu = cpu_getactivecpu();						\
//TODO 	if (activecpu < 0)										\
//TODO 	{														\
//TODO 		logerror(#name "() called with no active cpu!\n");	\
//TODO 		return retval;										\
//TODO 	}
//TODO 
//TODO #define VERIFY_ACTIVECPU_VOID(name)							\
//TODO 	int activecpu = cpu_getactivecpu();						\
//TODO 	if (activecpu < 0)										\
//TODO 	{														\
//TODO 		logerror(#name "() called with no active cpu!\n");	\
//TODO 		return;												\
//TODO 	}
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	CPU interrupt variables
//TODO  *
//TODO  *************************************/
//TODO 
//TODO /* current states for each CPU */
//TODO static UINT8 interrupt_enable[MAX_CPU];
//TODO static INT32 interrupt_vector[MAX_CPU][MAX_IRQ_LINES];
//TODO 
//TODO /* deferred states written in callbacks */
//TODO static UINT8 irq_line_state[MAX_CPU][MAX_IRQ_LINES];
//TODO static INT32 irq_line_vector[MAX_CPU][MAX_IRQ_LINES];
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	IRQ acknowledge callbacks
//TODO  *
//TODO  *************************************/
//TODO 
//TODO static int cpu_0_irq_callback(int irqline);
//TODO static int cpu_1_irq_callback(int irqline);
//TODO static int cpu_2_irq_callback(int irqline);
//TODO static int cpu_3_irq_callback(int irqline);
//TODO static int cpu_4_irq_callback(int irqline);
//TODO static int cpu_5_irq_callback(int irqline);
//TODO static int cpu_6_irq_callback(int irqline);
//TODO static int cpu_7_irq_callback(int irqline);
//TODO 
//TODO int (*cpu_irq_callbacks[MAX_CPU])(int) =
//TODO {
//TODO 	cpu_0_irq_callback,
//TODO 	cpu_1_irq_callback,
//TODO 	cpu_2_irq_callback,
//TODO 	cpu_3_irq_callback,
//TODO 	cpu_4_irq_callback,
//TODO 	cpu_5_irq_callback,
//TODO 	cpu_6_irq_callback,
//TODO 	cpu_7_irq_callback
//TODO };
//TODO 
//TODO static int (*drv_irq_callbacks[MAX_CPU])(int);
//TODO 
//TODO 
//TODO 
//TODO #if 0
//TODO #pragma mark CORE CPU
//TODO #endif
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	Initialize a CPU's interrupt states
//TODO  *
//TODO  *************************************/
//TODO 
//TODO int cpuint_init(void)
//TODO {
//TODO 	int cpunum;
//TODO 	int irqline;
//TODO 
//TODO 	/* loop over all CPUs */
//TODO 	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
//TODO 	{
//TODO 		/* reset the IRQ lines */
//TODO 		for (irqline = 0; irqline < MAX_IRQ_LINES; irqline++)
//TODO 		{
//TODO 			irq_line_state[cpunum][irqline] = CLEAR_LINE;
//TODO 			interrupt_vector[cpunum][irqline] =
//TODO 			irq_line_vector[cpunum][irqline] = cpunum_default_irq_vector(cpunum);
//TODO 		}
//TODO 	}
//TODO 
//TODO 	/* set up some stuff to save */
//TODO 	state_save_set_current_tag(0);
//TODO 	state_save_register_UINT8("cpu", 0, "irq enable",     interrupt_enable,  cpu_gettotalcpu());
//TODO 	state_save_register_INT32("cpu", 0, "irq vector",     &interrupt_vector[0][0],cpu_gettotalcpu() * MAX_IRQ_LINES);
//TODO 	state_save_register_UINT8("cpu", 0, "irqline state",  &irq_line_state[0][0],  cpu_gettotalcpu() * MAX_IRQ_LINES);
//TODO 	state_save_register_INT32("cpu", 0, "irqline vector", &irq_line_vector[0][0], cpu_gettotalcpu() * MAX_IRQ_LINES);
//TODO 
//TODO 	return 0;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	Reset a CPU's interrupt states
//TODO  *
//TODO  *************************************/
//TODO 
//TODO void cpuint_reset_cpu(int cpunum)
//TODO {
//TODO 	int irqline;
//TODO 
//TODO 	/* start with interrupts enabled, so the generic routine will work even if */
//TODO 	/* the machine doesn't have an interrupt enable port */
//TODO 	interrupt_enable[cpunum] = 1;
//TODO 	for (irqline = 0; irqline < MAX_IRQ_LINES; irqline++)
//TODO 		interrupt_vector[cpunum][irqline] = cpunum_default_irq_vector(cpunum);
//TODO 
//TODO 	/* reset any driver hooks into the IRQ acknowledge callbacks */
//TODO 	drv_irq_callbacks[cpunum] = NULL;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO #if 0
//TODO #pragma mark -
//TODO #pragma mark INTERRUPT HANDLING
//TODO #endif
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	Set IRQ callback for drivers
//TODO  *
//TODO  *************************************/
//TODO 
//TODO void cpu_set_irq_callback(int cpunum, int (*callback)(int))
//TODO {
//TODO 	drv_irq_callbacks[cpunum] = callback;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	Internal IRQ callbacks
//TODO  *
//TODO  *************************************/
//TODO 
//TODO INLINE int cpu_irq_callback(int cpunum, int irqline)
//TODO {
//TODO 	int vector = irq_line_vector[cpunum][irqline];
//TODO 
//TODO 	LOG(("cpu_%d_irq_callback(%d) $%04x\n", cpunum, irqline, vector));
//TODO 
//TODO 	/* if the IRQ state is HOLD_LINE, clear it */
//TODO 	if (irq_line_state[cpunum][irqline] == HOLD_LINE)
//TODO 	{
//TODO 		LOG(("->set_irq_line(%d,%d,%d)\n", cpunum, irqline, CLEAR_LINE));
//TODO 		activecpu_set_irq_line(irqline, INTERNAL_CLEAR_LINE);
//TODO 		irq_line_state[cpunum][irqline] = CLEAR_LINE;
//TODO 	}
//TODO 
//TODO 	/* if there's a driver callback, run it */
//TODO 	if (drv_irq_callbacks[cpunum])
//TODO 		vector = (*drv_irq_callbacks[cpunum])(irqline);
//TODO 
//TODO 	/* otherwise, just return the current vector */
//TODO 	return vector;
//TODO }
//TODO 
//TODO static int cpu_0_irq_callback(int irqline) { return cpu_irq_callback(0, irqline); }
//TODO static int cpu_1_irq_callback(int irqline) { return cpu_irq_callback(1, irqline); }
//TODO static int cpu_2_irq_callback(int irqline) { return cpu_irq_callback(2, irqline); }
//TODO static int cpu_3_irq_callback(int irqline) { return cpu_irq_callback(3, irqline); }
//TODO static int cpu_4_irq_callback(int irqline) { return cpu_irq_callback(4, irqline); }
//TODO static int cpu_5_irq_callback(int irqline) { return cpu_irq_callback(5, irqline); }
//TODO static int cpu_6_irq_callback(int irqline) { return cpu_irq_callback(6, irqline); }
//TODO static int cpu_7_irq_callback(int irqline) { return cpu_irq_callback(7, irqline); }
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	Set the IRQ vector for a given
//TODO  *	IRQ line on a CPU
//TODO  *
//TODO  *************************************/
//TODO 
//TODO void cpu_irq_line_vector_w(int cpunum, int irqline, int vector)
//TODO {
//TODO 	if (cpunum < cpu_gettotalcpu() && irqline >= 0 && irqline < MAX_IRQ_LINES)
//TODO 	{
//TODO 		LOG(("cpu_irq_line_vector_w(%d,%d,$%04x)\n",cpunum,irqline,vector));
//TODO 		interrupt_vector[cpunum][irqline] = vector;
//TODO 		return;
//TODO 	}
//TODO 	LOG(("cpu_irq_line_vector_w CPU#%d irqline %d > max irq lines\n", cpunum, irqline));
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	Generate a IRQ interrupt
//TODO  *
//TODO  *************************************/
//TODO 
//TODO static void cpu_manualirqcallback(int param)
//TODO {
//TODO 	int cpunum = param & 0x0f;
//TODO 	int state = (param >> 4) & 0x0f;
//TODO 	int irqline = (param >> 8) & 0x7f;
//TODO 	int set_vector = (param >> 15) & 0x01;
//TODO 	int vector = param >> 16;
//TODO 
//TODO 	LOG(("cpu_manualirqcallback %d,%d,%d\n",cpunum,irqline,state));
//TODO 
//TODO 	/* swap to the CPU's context */
//TODO 	cpuintrf_push_context(cpunum);
//TODO 
//TODO 	/* set the IRQ line state and vector */
//TODO 	if (irqline >= 0 && irqline < MAX_IRQ_LINES)
//TODO 	{
//TODO 		irq_line_state[cpunum][irqline] = state;
//TODO 		if (set_vector)
//TODO 			irq_line_vector[cpunum][irqline] = vector;
//TODO 	}
//TODO 
//TODO 	/* switch off the requested state */
//TODO 	switch (state)
//TODO 	{
//TODO 		case PULSE_LINE:
//TODO 			activecpu_set_irq_line(irqline, INTERNAL_ASSERT_LINE);
//TODO 			activecpu_set_irq_line(irqline, INTERNAL_CLEAR_LINE);
//TODO 			break;
//TODO 
//TODO 		case HOLD_LINE:
//TODO 		case ASSERT_LINE:
//TODO 			activecpu_set_irq_line(irqline, INTERNAL_ASSERT_LINE);
//TODO 			break;
//TODO 
//TODO 		case CLEAR_LINE:
//TODO 			activecpu_set_irq_line(irqline, INTERNAL_CLEAR_LINE);
//TODO 			break;
//TODO 
//TODO 		default:
//TODO 			logerror("cpu_manualirqcallback cpu #%d, line %d, unknown state %d\n", cpunum, irqline, state);
//TODO 	}
//TODO 	cpuintrf_pop_context();
//TODO 
//TODO 	/* generate a trigger to unsuspend any CPUs waiting on the interrupt */
//TODO 	if (state != CLEAR_LINE)
//TODO 		cpu_triggerint(cpunum);
//TODO }
//TODO 
//TODO 
//TODO void cpu_set_irq_line(int cpunum, int irqline, int state)
//TODO {
//TODO 	int vector = 0xff;
//TODO 	int param;
//TODO 
//TODO 	/* don't trigger interrupts on suspended CPUs */
//TODO 	if (cpu_getstatus(cpunum) == 0)
//TODO 		return;
//TODO 
//TODO 	/* pick the vector */
//TODO 	if (irqline >= 0 && irqline < MAX_IRQ_LINES)
//TODO 		vector = interrupt_vector[cpunum][irqline];
//TODO 
//TODO 	LOG(("cpu_set_irq_line(%d,%d,%d,%02x)\n", cpunum, irqline, state, vector));
//TODO 
//TODO 	/* set a timer to go off */
//TODO 	param = (cpunum & 0x0f) | ((state & 0x0f) << 4) | ((irqline & 0x7f) << 8) | (1 << 15) | (vector << 16);
//TODO //	param = (cpunum & 0x0f) | ((state & 0x0f) << 4) | ((irqline & 0x7f) << 8);
//TODO 	timer_set(TIME_NOW, param, cpu_manualirqcallback);
//TODO }
//TODO 
//TODO 
//TODO void cpu_set_irq_line_and_vector(int cpunum, int irqline, int state, int vector)
//TODO {
//TODO 	int param;
//TODO 
//TODO 	/* don't trigger interrupts on suspended CPUs */
//TODO 	if (cpu_getstatus(cpunum) == 0)
//TODO 		return;
//TODO 
//TODO 	LOG(("cpu_set_irq_line(%d,%d,%d,%02x)\n", cpunum, irqline, state, vector));
//TODO 
//TODO 	/* set a timer to go off */
//TODO 	param = (cpunum & 0x0f) | ((state & 0x0f) << 4) | ((irqline & 0x7f) << 8) | (1 << 15) | (vector << 16);
//TODO 	timer_set(TIME_NOW, param, cpu_manualirqcallback);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO #if 0
//TODO #pragma mark -
//TODO #pragma mark PREFERRED INTERRUPT HANDLING
//TODO #endif
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	NMI interrupt generation
//TODO  *
//TODO  *************************************/
//TODO 
//TODO INTERRUPT_GEN( nmi_line_pulse )
//TODO {
//TODO 	int cpunum = cpu_getactivecpu();
//TODO 	if (interrupt_enable[cpunum])
//TODO 		cpu_set_irq_line(cpunum, IRQ_LINE_NMI, PULSE_LINE);
//TODO }
//TODO 
//TODO INTERRUPT_GEN( nmi_line_assert )
//TODO {
//TODO 	int cpunum = cpu_getactivecpu();
//TODO 	if (interrupt_enable[cpunum])
//TODO 		cpu_set_irq_line(cpunum, IRQ_LINE_NMI, ASSERT_LINE);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	IRQ n interrupt generation
//TODO  *
//TODO  *************************************/
//TODO 
//TODO INLINE void irqn_line_hold(int irqline)
//TODO {
//TODO 	int cpunum = cpu_getactivecpu();
//TODO 	if (interrupt_enable[cpunum])
//TODO 	{
//TODO 		int vector = (irqline >= 0 && irqline < MAX_IRQ_LINES) ? interrupt_vector[cpunum][irqline] : 0xff;
//TODO 		cpu_set_irq_line_and_vector(cpunum, irqline, HOLD_LINE, vector);
//TODO 	}
//TODO }
//TODO 
//TODO INLINE void irqn_line_pulse(int irqline)
//TODO {
//TODO 	int cpunum = cpu_getactivecpu();
//TODO 	if (interrupt_enable[cpunum])
//TODO 	{
//TODO 		int vector = (irqline >= 0 && irqline < MAX_IRQ_LINES) ? interrupt_vector[cpunum][irqline] : 0xff;
//TODO 		cpu_set_irq_line_and_vector(cpunum, irqline, PULSE_LINE, vector);
//TODO 	}
//TODO }
//TODO 
//TODO INLINE void irqn_line_assert(int irqline)
//TODO {
//TODO 	int cpunum = cpu_getactivecpu();
//TODO 	if (interrupt_enable[cpunum])
//TODO 	{
//TODO 		int vector = (irqline >= 0 && irqline < MAX_IRQ_LINES) ? interrupt_vector[cpunum][irqline] : 0xff;
//TODO 		cpu_set_irq_line_and_vector(cpunum, irqline, ASSERT_LINE, vector);
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	IRQ interrupt generation
//TODO  *
//TODO  *************************************/
//TODO 
    public static InterruptPtr irq0_line_hold = new InterruptPtr() {
        public void handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
//TODO INTERRUPT_GEN( irq0_line_hold )		{ irqn_line_hold(0); }
//TODO INTERRUPT_GEN( irq0_line_pulse )	{ irqn_line_pulse(0); }
//TODO INTERRUPT_GEN( irq0_line_assert )	{ irqn_line_assert(0); }
//TODO 
//TODO INTERRUPT_GEN( irq1_line_hold )		{ irqn_line_hold(1); }
//TODO INTERRUPT_GEN( irq1_line_pulse )	{ irqn_line_pulse(1); }
//TODO INTERRUPT_GEN( irq1_line_assert )	{ irqn_line_assert(1); }
//TODO 
//TODO INTERRUPT_GEN( irq2_line_hold )		{ irqn_line_hold(2); }
//TODO INTERRUPT_GEN( irq2_line_pulse )	{ irqn_line_pulse(2); }
//TODO INTERRUPT_GEN( irq2_line_assert )	{ irqn_line_assert(2); }
//TODO 
//TODO INTERRUPT_GEN( irq3_line_hold )		{ irqn_line_hold(3); }
//TODO INTERRUPT_GEN( irq3_line_pulse )	{ irqn_line_pulse(3); }
//TODO INTERRUPT_GEN( irq3_line_assert )	{ irqn_line_assert(3); }
//TODO 
//TODO INTERRUPT_GEN( irq4_line_hold )		{ irqn_line_hold(4); }
//TODO INTERRUPT_GEN( irq4_line_pulse )	{ irqn_line_pulse(4); }
//TODO INTERRUPT_GEN( irq4_line_assert )	{ irqn_line_assert(4); }
//TODO 
//TODO INTERRUPT_GEN( irq5_line_hold )		{ irqn_line_hold(5); }
//TODO INTERRUPT_GEN( irq5_line_pulse )	{ irqn_line_pulse(5); }
//TODO INTERRUPT_GEN( irq5_line_assert )	{ irqn_line_assert(5); }
//TODO 
//TODO INTERRUPT_GEN( irq6_line_hold )		{ irqn_line_hold(6); }
//TODO INTERRUPT_GEN( irq6_line_pulse )	{ irqn_line_pulse(6); }
//TODO INTERRUPT_GEN( irq6_line_assert )	{ irqn_line_assert(6); }
//TODO 
//TODO INTERRUPT_GEN( irq7_line_hold )		{ irqn_line_hold(7); }
//TODO INTERRUPT_GEN( irq7_line_pulse )	{ irqn_line_pulse(7); }
//TODO INTERRUPT_GEN( irq7_line_assert )	{ irqn_line_assert(7); }
//TODO 
//TODO 
//TODO 
//TODO #if 0
//TODO #pragma mark -
//TODO #pragma mark OBSOLETE INTERRUPT HANDLING
//TODO #endif
//TODO 
//TODO /*************************************
//TODO  *
//TODO  *	Interrupt enabling
//TODO  *
//TODO  *************************************/
//TODO 
//TODO static void cpu_clearintcallback(int cpunum)
//TODO {
//TODO 	int irqcount = cputype_get_interface(Machine->drv->cpu[cpunum].cpu_type)->num_irqs;
//TODO 	int irqline;
//TODO 
//TODO 	cpuintrf_push_context(cpunum);
//TODO 
//TODO 	/* clear NMI and all IRQs */
//TODO 	activecpu_set_irq_line(IRQ_LINE_NMI, INTERNAL_CLEAR_LINE);
//TODO 	for (irqline = 0; irqline < irqcount; irqline++)
//TODO 		activecpu_set_irq_line(irqline, INTERNAL_CLEAR_LINE);
//TODO 
//TODO 	cpuintrf_pop_context();
//TODO }
//TODO 
//TODO 
//TODO void cpu_interrupt_enable(int cpunum,int enabled)
//TODO {
//TODO 	interrupt_enable[cpunum] = enabled;
//TODO 
//TODO LOG(("CPU#%d interrupt_enable=%d\n", cpunum, enabled));
//TODO 
//TODO 	/* make sure there are no queued interrupts */
//TODO 	if (enabled == 0)
//TODO 		timer_set(TIME_NOW, cpunum, cpu_clearintcallback);
//TODO }
//TODO 
//TODO 
//TODO WRITE_HANDLER( interrupt_enable_w )
//TODO {
//TODO 	VERIFY_ACTIVECPU_VOID(interrupt_enable_w);
//TODO 	cpu_interrupt_enable(activecpu, data);
//TODO }
//TODO 
//TODO 
//TODO READ_HANDLER( interrupt_enable_r )
//TODO {
//TODO 	VERIFY_ACTIVECPU(1, interrupt_enable_r);
//TODO 	return interrupt_enable[activecpu];
//TODO }
//TODO 
//TODO 
//TODO WRITE_HANDLER( interrupt_vector_w )
//TODO {
//TODO 	VERIFY_ACTIVECPU_VOID(interrupt_vector_w);
//TODO 	if (interrupt_vector[activecpu][0] != data)
//TODO 	{
//TODO 		LOG(("CPU#%d interrupt_vector_w $%02x\n", activecpu, data));
//TODO 		interrupt_vector[activecpu][0] = data;
//TODO 
//TODO 		/* make sure there are no queued interrupts */
//TODO 		timer_set(TIME_NOW, activecpu, cpu_clearintcallback);
//TODO 	}
//TODO }
//TODO     
}
