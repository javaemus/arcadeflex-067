/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v067.vidhrdw;

import static gr.codebb.arcadeflex.v067.common.FuncPtr.*;
import static gr.codebb.arcadeflex.v067.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.v067.mame.mame.Machine;
import static gr.codebb.arcadeflex.v067.vidhrdw.generic.*;

public class minivadr {

    /**
     * *****************************************************************
     *
     * Palette Setting.
     *
     ******************************************************************
     */

    PALETTE_INIT(minivadr ) {
        palette_set_color(0, 0x00, 0x00, 0x00);
        palette_set_color(1, 0xff, 0xff, 0xff);
    }

    /**
     * *****************************************************************
     *
     * Draw Pixel.
     *
     ******************************************************************
     */
    public static WriteHandlerPtr minivadr_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int i;
            int x, y;
            int color;

            videoram.write(offset, data);

            x = (offset % 32) * 8;
            y = (offset / 32);

            if (x >= Machine.visible_area.min_x
                    && x <= Machine.visible_area.max_x
                    && y >= Machine.visible_area.min_y
                    && y <= Machine.visible_area.max_y) {
                for (i = 0; i < 8; i++) {
                    color = Machine.pens[((data >> i) & 0x01)];

                    plot_pixel(tmpbitmap, x + (7 - i), y, color);
                }
            }
        }
    };

    VIDEO_UPDATE(minivadr ) {
        if (get_vh_global_attribute_changed() != 0) {
            int offs;

            /* redraw bitmap */
            for (offs = 0; offs < videoram_size[0]; offs++) {
                minivadr_videoram_w.handler(offs, videoram.read(offs));
            }
        }
        copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_NONE, 0);
    }
}
