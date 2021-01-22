package gr.codebb.arcadeflex.v067.common;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v067.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.v067.mame.commonH.*;
import static gr.codebb.arcadeflex.v067.mame.driverH.*;

public class FuncPtr {

    /**
     * common
     */
    public static abstract interface ReadHandlerPtr {

        public abstract int handler(int offset);
    }

    public static abstract interface WriteHandlerPtr {

        public abstract void handler(int offset, int data);
    }

    /**
     * driver
     */
    public static abstract interface RomLoadPtr {

        public abstract void handler();
    }

    public static abstract interface InputPortPtr {

        public abstract void handler();
    }
    public static abstract interface MachinePtr {

        public abstract void handler(InternalMachineDriver machine);
    }
    public static abstract interface InterruptPtr {

        public abstract void handler();
    }
    /**
     * vidhrdw
     */
    public static abstract interface VhPaletteInitPtr {

        public abstract void handler(char[] colortable, UBytePtr color_prom);
    }

    public static abstract interface VhStartPtr {

        public abstract int handler();
    }

    public static abstract interface VhUpdatePtr {

        public abstract void handler(mame_bitmap bitmap, rectangle cliprect);
    }
}
