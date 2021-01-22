/*
 * ported to v0.67
 * 
 */ 
package gr.codebb.arcadeflex.v067.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v067.common.FuncPtr.*;
import static gr.codebb.arcadeflex.v067.mame.commonH.*;

public class generic {

    public static UBytePtr videoram = new UBytePtr();
//TODO data16_t *videoram16;
//TODO data32_t *videoram32;
    public static int[] videoram_size = new int[1];
//TODO data8_t *colorram;
//TODO data16_t *colorram16;
//TODO data32_t *colorram32;
//TODO data8_t *spriteram;			/* not used in this module... */
//TODO data16_t *spriteram16;		/* ... */
//TODO data32_t *spriteram32;		/* ... */
//TODO data8_t *spriteram_2;
//TODO data16_t *spriteram16_2;
//TODO data32_t *spriteram32_2;
//TODO data8_t *spriteram_3;
//TODO data16_t *spriteram16_3;
//TODO data32_t *spriteram32_3;
//TODO data8_t *buffered_spriteram;
//TODO data16_t *buffered_spriteram16;
//TODO data32_t *buffered_spriteram32;
//TODO data8_t *buffered_spriteram_2;
//TODO data16_t *buffered_spriteram16_2;
//TODO data32_t *buffered_spriteram32_2;
//TODO size_t spriteram_size;		/* ... here just for convenience */
//TODO size_t spriteram_2_size;
//TODO size_t spriteram_3_size;
    public static /*data8_t*/ char u8_dirtybuffer[];
//TODO data16_t *dirtybuffer16;
//TODO data32_t *dirtybuffer32;
    public static mame_bitmap tmpbitmap;
//TODO 
//TODO int flip_screen_x, flip_screen_y;
    static int global_attribute_changed;
//TODO 
//TODO void video_generic_postload(void)
//TODO {
//TODO 	memset(dirtybuffer,1,videoram_size);
//TODO }
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO   Start the video hardware emulation.
//TODO 
//TODO ***************************************************************************/
    public static VhStartPtr video_start_generic = new VhStartPtr() {
        public int handler() {
            throw new UnsupportedOperationException("Unsupported");
        }
    };
//TODO VIDEO_START( generic )
//TODO {
//TODO 	dirtybuffer = 0;
//TODO 	tmpbitmap = 0;
//TODO 
//TODO 	if (videoram_size == 0)
//TODO 	{
//TODO logerror("Error: video_start_generic() called but videoram_size not initialized\n");
//TODO 		return 1;
//TODO 	}
//TODO 
//TODO 	if ((dirtybuffer = auto_malloc(videoram_size)) == 0)
//TODO 		return 1;
//TODO 	memset(dirtybuffer,1,videoram_size);
//TODO 
//TODO 	if ((tmpbitmap = auto_bitmap_alloc(Machine->drv->screen_width,Machine->drv->screen_height)) == 0)
//TODO 		return 1;
//TODO 
//TODO 	state_save_register_func_postload(video_generic_postload);
//TODO 
//TODO 	return 0;
//TODO }
//TODO 
//TODO 
//TODO VIDEO_START( generic_bitmapped )
//TODO {
//TODO 	if ((tmpbitmap = auto_bitmap_alloc(Machine->drv->screen_width,Machine->drv->screen_height)) == 0)
//TODO 		return 1;
//TODO 
//TODO 	return 0;
//TODO }
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO   Draw the game screen in the given mame_bitmap.
//TODO   To be used by bitmapped games not using sprites.
//TODO 
//TODO ***************************************************************************/
//TODO VIDEO_UPDATE( generic_bitmapped )
//TODO {
//TODO 	copybitmap(bitmap,tmpbitmap,0,0,0,0,&Machine->visible_area,TRANSPARENCY_NONE,0);
//TODO }
//TODO 
//TODO 
    public static ReadHandlerPtr videoram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return videoram.read(offset);
        }
    };
//TODO 
//TODO READ_HANDLER( colorram_r )
//TODO {
//TODO 	return colorram[offset];
//TODO }
//TODO 
    public static WriteHandlerPtr videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (videoram.read(offset) != data) {
                u8_dirtybuffer[offset] = 1;

                videoram.write(offset, data);
            }
        }
    };
//TODO }
//TODO 
//TODO WRITE_HANDLER( colorram_w )
//TODO {
//TODO 	if (colorram[offset] != data)
//TODO 	{
//TODO 		dirtybuffer[offset] = 1;
//TODO 
//TODO 		colorram[offset] = data;
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO READ_HANDLER( spriteram_r )
//TODO {
//TODO 	return spriteram[offset];
//TODO }
//TODO 
//TODO WRITE_HANDLER( spriteram_w )
//TODO {
//TODO 	spriteram[offset] = data;
//TODO }
//TODO 
//TODO READ16_HANDLER( spriteram16_r )
//TODO {
//TODO 	return spriteram16[offset];
//TODO }
//TODO 
//TODO WRITE16_HANDLER( spriteram16_w )
//TODO {
//TODO 	COMBINE_DATA(spriteram16+offset);
//TODO }
//TODO 
//TODO READ_HANDLER( spriteram_2_r )
//TODO {
//TODO 	return spriteram_2[offset];
//TODO }
//TODO 
//TODO WRITE_HANDLER( spriteram_2_w )
//TODO {
//TODO 	spriteram_2[offset] = data;
//TODO }
//TODO 
//TODO /* Mish:  171099
//TODO 
//TODO 	'Buffered spriteram' is where the graphics hardware draws the sprites
//TODO from private ram that the main CPU cannot access.  The main CPU typically
//TODO prepares sprites for the next frame in it's own sprite ram as the graphics
//TODO hardware renders sprites for the current frame from private ram.  Main CPU
//TODO sprite ram is usually copied across to private ram by setting some flag
//TODO in the VBL interrupt routine.
//TODO 
//TODO 	The reason for this is to avoid sprite flicker or lag - if a game
//TODO is unable to prepare sprite ram within a frame (for example, lots of sprites
//TODO on screen) then it doesn't trigger the buffering hardware - instead the
//TODO graphics hardware will use the sprites from the last frame. An example is
//TODO Dark Seal - the buffer flag is only written to if the CPU is idle at the time
//TODO of the VBL interrupt.  If the buffering is not emulated the sprites flicker
//TODO at busy scenes.
//TODO 
//TODO 	Some games seem to use buffering because of hardware constraints -
//TODO Capcom games (Cps1, Last Duel, etc) render spriteram _1 frame ahead_ and
//TODO buffer this spriteram at the end of a frame, so the _next_ frame must be drawn
//TODO from the buffer.  Presumably the graphics hardware and the main cpu cannot
//TODO share the same spriteram for whatever reason.
//TODO 
//TODO 	Sprite buffering & Mame:
//TODO 
//TODO 	To use sprite buffering in a driver use VIDEO_BUFFERS_SPRITERAM in the
//TODO machine driver.  This will automatically create an area for buffered spriteram
//TODO equal to the size of normal spriteram.
//TODO 
//TODO 	Spriteram size _must_ be declared in the memory map:
//TODO 
//TODO 	{ 0x120000, 0x1207ff, MWA_BANK2, &spriteram, &spriteram_size },
//TODO 
//TODO 	Then the video driver must draw the sprites from the buffered_spriteram
//TODO pointer.  The function buffer_spriteram_w() is used to simulate hardware
//TODO which buffers the spriteram from a memory location write.  The function
//TODO buffer_spriteram(unsigned char *ptr, int length) can be used where
//TODO more control is needed over what is buffered.
//TODO 
//TODO 	For examples see darkseal.c, contra.c, lastduel.c, bionicc.c etc.
//TODO 
//TODO */
//TODO 
//TODO WRITE_HANDLER( buffer_spriteram_w )
//TODO {
//TODO 	memcpy(buffered_spriteram,spriteram,spriteram_size);
//TODO }
//TODO 
//TODO WRITE16_HANDLER( buffer_spriteram16_w )
//TODO {
//TODO 	memcpy(buffered_spriteram16,spriteram16,spriteram_size);
//TODO }
//TODO 
//TODO WRITE32_HANDLER( buffer_spriteram32_w )
//TODO {
//TODO 	memcpy(buffered_spriteram32,spriteram32,spriteram_size);
//TODO }
//TODO 
//TODO WRITE_HANDLER( buffer_spriteram_2_w )
//TODO {
//TODO 	memcpy(buffered_spriteram_2,spriteram_2,spriteram_2_size);
//TODO }
//TODO 
//TODO WRITE16_HANDLER( buffer_spriteram16_2_w )
//TODO {
//TODO 	memcpy(buffered_spriteram16_2,spriteram16_2,spriteram_2_size);
//TODO }
//TODO 
//TODO WRITE32_HANDLER( buffer_spriteram32_2_w )
//TODO {
//TODO 	memcpy(buffered_spriteram32_2,spriteram32_2,spriteram_2_size);
//TODO }
//TODO 
//TODO void buffer_spriteram(unsigned char *ptr,int length)
//TODO {
//TODO 	memcpy(buffered_spriteram,ptr,length);
//TODO }
//TODO 
//TODO void buffer_spriteram_2(unsigned char *ptr,int length)
//TODO {
//TODO 	memcpy(buffered_spriteram_2,ptr,length);
//TODO }
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Global video attribute handling code
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /*-------------------------------------------------
//TODO 	updateflip - handle global flipping
//TODO -------------------------------------------------*/
//TODO 
//TODO static void updateflip(void)
//TODO {
//TODO 	int min_x,max_x,min_y,max_y;
//TODO 
//TODO 	tilemap_set_flip(ALL_TILEMAPS,(TILEMAP_FLIPX & flip_screen_x) | (TILEMAP_FLIPY & flip_screen_y));
//TODO 
//TODO 	min_x = Machine->drv->default_visible_area.min_x;
//TODO 	max_x = Machine->drv->default_visible_area.max_x;
//TODO 	min_y = Machine->drv->default_visible_area.min_y;
//TODO 	max_y = Machine->drv->default_visible_area.max_y;
//TODO 
//TODO 	if (flip_screen_x)
//TODO 	{
//TODO 		int temp;
//TODO 
//TODO 		temp = Machine->drv->screen_width - min_x - 1;
//TODO 		min_x = Machine->drv->screen_width - max_x - 1;
//TODO 		max_x = temp;
//TODO 	}
//TODO 	if (flip_screen_y)
//TODO 	{
//TODO 		int temp;
//TODO 
//TODO 		temp = Machine->drv->screen_height - min_y - 1;
//TODO 		min_y = Machine->drv->screen_height - max_y - 1;
//TODO 		max_y = temp;
//TODO 	}
//TODO 
//TODO 	set_visible_area(min_x,max_x,min_y,max_y);
//TODO }
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	flip_screen_set - set global flip
//TODO -------------------------------------------------*/
//TODO 
//TODO void flip_screen_set(int on)
//TODO {
//TODO 	flip_screen_x_set(on);
//TODO 	flip_screen_y_set(on);
//TODO }
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	flip_screen_x_set - set global horizontal flip
//TODO -------------------------------------------------*/
//TODO 
//TODO void flip_screen_x_set(int on)
//TODO {
//TODO 	if (on) on = ~0;
//TODO 	if (flip_screen_x != on)
//TODO 	{
//TODO 		set_vh_global_attribute(&flip_screen_x,on);
//TODO 		updateflip();
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	flip_screen_y_set - set global vertical flip
//TODO -------------------------------------------------*/
//TODO 
//TODO void flip_screen_y_set(int on)
//TODO {
//TODO 	if (on) on = ~0;
//TODO 	if (flip_screen_y != on)
//TODO 	{
//TODO 		set_vh_global_attribute(&flip_screen_y,on);
//TODO 		updateflip();
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	set_vh_global_attribute - set an arbitrary
//TODO 	global video attribute
//TODO -------------------------------------------------*/
//TODO 
//TODO void set_vh_global_attribute( int *addr, int data )
//TODO {
//TODO 	if (!addr || *addr != data)
//TODO 	{
//TODO 		global_attribute_changed = 1;
//TODO 		if (addr)
//TODO 			*addr = data;
//TODO 	}
//TODO }
//TODO 
//TODO 

    /*-------------------------------------------------
 	get_vh_global_attribute - set an arbitrary
 	global video attribute
    -------------------------------------------------*/
    public static int get_vh_global_attribute_changed() {
        int result = global_attribute_changed;
        global_attribute_changed = 0;
        return result;
    }
}
