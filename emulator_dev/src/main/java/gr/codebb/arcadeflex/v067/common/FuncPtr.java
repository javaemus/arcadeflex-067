package gr.codebb.arcadeflex.v067.common;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v067.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.v067.mame.commonH.*;

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
     * vidhrdw
     */
    public static abstract interface VhPaletteInitPtr {

        public abstract void handler(char[] colortable, UBytePtr color_prom);
    }
    public static abstract interface VhUpdatePtr {

        public abstract void handler(mame_bitmap bitmap, rectangle cliprect);
    }
}
