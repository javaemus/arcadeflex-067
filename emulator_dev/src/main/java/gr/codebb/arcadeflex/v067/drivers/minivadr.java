/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v067.drivers;

import static gr.codebb.arcadeflex.v067.common.FuncPtr.*;
import static gr.codebb.arcadeflex.v067.mame.commonH.*;
import static gr.codebb.arcadeflex.v067.mame.driverH.*;
import static gr.codebb.arcadeflex.v067.mame.inptportH.*;
import static gr.codebb.arcadeflex.v067.mame.inptport.*;
import static gr.codebb.arcadeflex.v067.mame.memoryH.*;
import static gr.codebb.arcadeflex.v067.vidhrdw.minivadr.*;
import static gr.codebb.arcadeflex.v067.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.v067.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.v067.mame.cpuint.*;

public class minivadr {

    public static Memory_ReadAddress readmem[] = {
        new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
        new Memory_ReadAddress(0x0000, 0x1fff, MRA_ROM),
        new Memory_ReadAddress(0xa000, 0xbfff, MRA_RAM),
        new Memory_ReadAddress(0xe008, 0xe008, input_port_0_r),
        new Memory_ReadAddress(MEMPORT_MARKER, 0)
    };

    public static Memory_WriteAddress writemem[] = {
        new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
        new Memory_WriteAddress(0x0000, 0x1fff, MWA_ROM),
        new Memory_WriteAddress(0xa000, 0xbfff, minivadr_videoram_w, videoram, videoram_size),
        new Memory_WriteAddress(0xe008, 0xe008, MWA_NOP), // ???
        new Memory_WriteAddress(MEMPORT_MARKER, 0)
    };

    static InputPortPtr input_ports_minivadr = new InputPortPtr() {
        public void handler() {
            PORT_START();
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_BUTTON1);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_COIN1);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_UNKNOWN);
            INPUT_PORTS_END();
        }
    };

    static MachinePtr machine_driver_minivadr = new MachinePtr() {/* basic machine hardware */
        public void handler(InternalMachineDriver machine) {
            MDRV_CPU_ADD(machine,CPU_Z80, 24000000 / 6);
            /* 4 MHz ? */
            MDRV_CPU_MEMORY(readmem, writemem);
            MDRV_CPU_VBLANK_INT(irq0_line_hold, 1);

            MDRV_FRAMES_PER_SECOND(60);
            MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION);/* video hardware */
            MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER);
            MDRV_SCREEN_SIZE(256, 256);
            MDRV_VISIBLE_AREA(0, 256 - 1, 16, 240 - 1);
            MDRV_PALETTE_LENGTH(2);

            MDRV_PALETTE_INIT(palette_init_minivadr);
            MDRV_VIDEO_START(video_start_generic);
            MDRV_VIDEO_UPDATE(video_update_minivadr);
            /* sound hardware */
            MACHINE_DRIVER_END();
        }
    };

    /**
     * *************************************************************************
     *
     * Game driver(s)
     *
     **************************************************************************
     */
    static RomLoadPtr rom_minivadr = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1, 0);/* 64k for code */
            ROM_LOAD("d26-01.bin", 0x0000, 0x2000, 0xa96c823d);
            ROM_END();
        }
    };

    public static GameDriver driver_minivadr = new GameDriver("1990", "minivadr", "minivadr.java", rom_minivadr, null, machine_driver_minivadr, input_ports_minivadr, null, ROT0, "Taito Corporation", "Minivader");
}
