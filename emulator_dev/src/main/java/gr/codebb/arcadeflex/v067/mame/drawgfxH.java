/*
 * ported to v0.67
 * 
 */
package gr.codebb.arcadeflex.v067.mame;

import static gr.codebb.arcadeflex.v067.mame.commonH.*;

public class drawgfxH {
//TODO #define MAX_GFX_PLANES 8
//TODO #define MAX_GFX_SIZE 64
//TODO 
//TODO #define RGN_FRAC(num,den) (0x80000000 | (((num) & 0x0f) << 27) | (((den) & 0x0f) << 23))
//TODO #define IS_FRAC(offset) ((offset) & 0x80000000)
//TODO #define FRAC_NUM(offset) (((offset) >> 27) & 0x0f)
//TODO #define FRAC_DEN(offset) (((offset) >> 23) & 0x0f)
//TODO #define FRAC_OFFSET(offset) ((offset) & 0x007fffff)
//TODO 
//TODO #define STEP4(START,STEP)  (START),(START)+1*(STEP),(START)+2*(STEP),(START)+3*(STEP)
//TODO #define STEP8(START,STEP)  STEP4(START,STEP),STEP4((START)+4*(STEP),STEP)
//TODO #define STEP16(START,STEP) STEP8(START,STEP),STEP8((START)+8*(STEP),STEP)
//TODO 
//TODO 
//TODO struct GfxLayout
//TODO {
//TODO 	UINT16 width,height; /* width and height (in pixels) of chars/sprites */
//TODO 	UINT32 total; /* total numer of chars/sprites in the rom */
//TODO 	UINT16 planes; /* number of bitplanes */
//TODO 	UINT32 planeoffset[MAX_GFX_PLANES]; /* start of every bitplane (in bits) */
//TODO 	UINT32 xoffset[MAX_GFX_SIZE]; /* position of the bit corresponding to the pixel */
//TODO 	UINT32 yoffset[MAX_GFX_SIZE]; /* of the given coordinates */
//TODO 	UINT32 charincrement; /* distance between two consecutive characters/sprites (in bits) */
//TODO };
//TODO 
//TODO #define GFX_RAW 0x12345678
//TODO /* When planeoffset[0] is set to GFX_RAW, the gfx data is left as-is, with no conversion.
//TODO    No buffer is allocated for the decoded data, and gfxdata is set to point to the source
//TODO    data; therefore, you must not use ROMREGION_DISPOSE.
//TODO    xoffset[0] is an optional displacement (*8) from the beginning of the source data, while
//TODO    yoffset[0] is the line modulo (*8) and charincrement the char modulo (*8). They are *8
//TODO    for consistency with the usual behaviour, but the bottom 3 bits are not used.
//TODO    GFX_PACKED is automatically set if planes is <= 4.
//TODO 
//TODO    This special mode can be used to save memory in games that require several different
//TODO    handlings of the same ROM data (e.g. metro.c can use both 4bpp and 8bpp tiles, and both
//TODO    8x8 and 16x16; cps.c has 8x8, 16x16 and 32x32 tiles all fetched from the same ROMs).
//TODO    Note, however, that performance will suffer in rotated games, since the gfx data will
//TODO    not be prerotated and will rely on GFX_SWAPXY.
//TODO */
//TODO 
//TODO struct GfxElement
//TODO {
//TODO 	UINT16 width,height;
//TODO 
//TODO 	UINT32 total_elements;	/* total number of characters/sprites */
//TODO 	UINT16 color_granularity;	/* number of colors for each color code */
//TODO 							/* (for example, 4 for 2 bitplanes gfx) */
//TODO 	UINT32 total_colors;
//TODO 	pen_t *colortable;	/* map color codes to screen pens */
//TODO 	UINT32 *pen_usage;	/* an array of total_elements entries. */
//TODO 						/* It is a table of the pens each character uses */
//TODO 						/* (bit 0 = pen 0, and so on). This is used by */
//TODO 						/* drawgfgx() to do optimizations like skipping */
//TODO 						/* drawing of a totally transparent character */
//TODO 	UINT8 *gfxdata;		/* pixel data */
//TODO 	UINT32 line_modulo;	/* amount to add to get to the next line (usually = width) */
//TODO 	UINT32 char_modulo;	/* = line_modulo * height */
//TODO 	UINT32 flags;
//TODO };
//TODO 
//TODO #define GFX_PACKED				1	/* two 4bpp pixels are packed in one byte of gfxdata */
//TODO #define GFX_SWAPXY				2	/* characters are mirrored along the top-left/bottom-right diagonal */
//TODO #define GFX_DONT_FREE_GFXDATA	4	/* gfxdata was not malloc()ed, so don't free it on exit */
//TODO 
//TODO 
//TODO struct GfxDecodeInfo
//TODO {
//TODO 	int memory_region;	/* memory region where the data resides (usually 1) */
//TODO 						/* -1 marks the end of the array */
//TODO 	UINT32 start;	/* beginning of data to decode */
//TODO 	struct GfxLayout *gfxlayout;
//TODO 	UINT16 color_codes_start;	/* offset in the color lookup table where color codes start */
//TODO 	UINT16 total_color_codes;	/* total number of color codes */
//TODO };
//TODO 

    public static class rectangle {

        public rectangle() {
        }

        public rectangle(int min_x, int max_x, int min_y, int max_y) {
            this.min_x = min_x;
            this.max_x = max_x;
            this.min_y = min_y;
            this.max_y = max_y;
        }

        public rectangle(rectangle rec) {
            min_x = rec.min_x;
            max_x = rec.max_x;
            min_y = rec.min_y;
            max_y = rec.max_y;
        }

        public int min_x, max_x;
        public int min_y, max_y;
    }
//TODO 
//TODO struct _alpha_cache {
//TODO 	const UINT8 *alphas;
//TODO 	const UINT8 *alphad;
//TODO 	UINT8 alpha[0x101][0x100];
//TODO };
//TODO 
//TODO extern struct _alpha_cache alpha_cache;
//TODO 
//TODO enum
//TODO {
    public static final int TRANSPARENCY_NONE = 0;/* opaque with remapping */
//TODO 	TRANSPARENCY_NONE_RAW,		/* opaque with no remapping */
//TODO 	TRANSPARENCY_PEN,			/* single pen transparency with remapping */
//TODO 	TRANSPARENCY_PEN_RAW,		/* single pen transparency with no remapping */
//TODO 	TRANSPARENCY_PENS,			/* multiple pen transparency with remapping */
//TODO 	TRANSPARENCY_PENS_RAW,		/* multiple pen transparency with no remapping */
//TODO 	TRANSPARENCY_COLOR,			/* single remapped pen transparency with remapping */
//TODO 	TRANSPARENCY_PEN_TABLE,		/* special pen remapping modes (see DRAWMODE_xxx below) with remapping */
//TODO 	TRANSPARENCY_PEN_TABLE_RAW,	/* special pen remapping modes (see DRAWMODE_xxx below) with no remapping */
//TODO 	TRANSPARENCY_BLEND,			/* blend two bitmaps, shifting the source and ORing to the dest with remapping */
//TODO 	TRANSPARENCY_BLEND_RAW,		/* blend two bitmaps, shifting the source and ORing to the dest with no remapping */
//TODO 	TRANSPARENCY_ALPHAONE,		/* single pen transparency, single pen alpha */
//TODO 	TRANSPARENCY_ALPHA,			/* single pen transparency, other pens alpha */
//TODO 	TRANSPARENCY_ALPHARANGE,	/* single pen transparency, multiple pens alpha depending on array, see psikyosh.c */
//TODO 
//TODO 	TRANSPARENCY_MODES			/* total number of modes; must be last */
//TODO };
//TODO 
//TODO /* drawing mode case TRANSPARENCY_ALPHARANGE */
//TODO extern UINT8 gfx_alpharange_table[256];
//TODO 
//TODO /* drawing mode case TRANSPARENCY_PEN_TABLE */
//TODO extern UINT8 gfx_drawmode_table[256];
//TODO enum
//TODO {
//TODO 	DRAWMODE_NONE,
//TODO 	DRAWMODE_SOURCE,
//TODO 	DRAWMODE_SHADOW
//TODO };
//TODO 
//TODO /* By default, when drawing sprites with pdrawgfx, shadows affect the sprites below them. */
//TODO /* Set this flag to 1 to make shadows only affect the background, leaving sprites at full brightness. */
//TODO extern int pdrawgfx_shadow_lowpri;
//TODO 
//TODO 
//TODO /* pointers to pixel functions.  They're set based on depth */
    public static void plot_pixel(mame_bitmap bm,int x,int y,int p)
    {
        throw new UnsupportedOperationException("Unsupported");
//TODO        (*(bm)->plot)(bm,x,y,p)
    }	
//TODO #define read_pixel(bm,x,y)		(*(bm)->read)(bm,x,y)
//TODO #define plot_box(bm,x,y,w,h,p)	(*(bm)->plot_box)(bm,x,y,w,h,p)
//TODO 
//TODO void decodechar(struct GfxElement *gfx,int num,const unsigned char *src,const struct GfxLayout *gl);
//TODO struct GfxElement *decodegfx(const unsigned char *src,const struct GfxLayout *gl);
//TODO void set_pixel_functions(struct mame_bitmap *bitmap);
//TODO void freegfx(struct GfxElement *gfx);
//TODO void drawgfx(struct mame_bitmap *dest,const struct GfxElement *gfx,
//TODO 		unsigned int code,unsigned int color,int flipx,int flipy,int sx,int sy,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color);
//TODO void pdrawgfx(struct mame_bitmap *dest,const struct GfxElement *gfx,
//TODO 		unsigned int code,unsigned int color,int flipx,int flipy,int sx,int sy,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color,
//TODO 		UINT32 priority_mask);
//TODO void mdrawgfx(struct mame_bitmap *dest,const struct GfxElement *gfx,
//TODO 		unsigned int code,unsigned int color,int flipx,int flipy,int sx,int sy,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color,
//TODO 		UINT32 priority_mask);
//TODO void copybitmap(struct mame_bitmap *dest,struct mame_bitmap *src,int flipx,int flipy,int sx,int sy,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color);
//TODO void copybitmap_remap(struct mame_bitmap *dest,struct mame_bitmap *src,int flipx,int flipy,int sx,int sy,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color);
//TODO void copyscrollbitmap(struct mame_bitmap *dest,struct mame_bitmap *src,
//TODO 		int rows,const int *rowscroll,int cols,const int *colscroll,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color);
//TODO void copyscrollbitmap_remap(struct mame_bitmap *dest,struct mame_bitmap *src,
//TODO 		int rows,const int *rowscroll,int cols,const int *colscroll,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color);
//TODO void draw_scanline8(struct mame_bitmap *bitmap,int x,int y,int length,const UINT8 *src,pen_t *pens,int transparent_pen);
//TODO void draw_scanline16(struct mame_bitmap *bitmap,int x,int y,int length,const UINT16 *src,pen_t *pens,int transparent_pen);
//TODO void pdraw_scanline8(struct mame_bitmap *bitmap,int x,int y,int length,const UINT8 *src,pen_t *pens,int transparent_pen,int pri);
//TODO void pdraw_scanline16(struct mame_bitmap *bitmap,int x,int y,int length,const UINT16 *src,pen_t *pens,int transparent_pen,int pri);
//TODO void extract_scanline8(struct mame_bitmap *bitmap,int x,int y,int length,UINT8 *dst);
//TODO void extract_scanline16(struct mame_bitmap *bitmap,int x,int y,int length,UINT16 *dst);
//TODO 
//TODO 
//TODO /* Alpha blending functions */
//TODO extern int alpha_active;
//TODO void alpha_init(void);
//TODO INLINE void alpha_set_level(int level) {
//TODO 	if(level == 0)
//TODO 		level = -1;
//TODO 	alpha_cache.alphas = alpha_cache.alpha[level+1];
//TODO 	alpha_cache.alphad = alpha_cache.alpha[255-level];
//TODO }
//TODO 
//TODO INLINE UINT32 alpha_blend16( UINT32 d, UINT32 s )
//TODO {
//TODO 	const UINT8 *alphas = alpha_cache.alphas;
//TODO 	const UINT8 *alphad = alpha_cache.alphad;
//TODO 	return (alphas[s & 0x1f] | (alphas[(s>>5) & 0x1f] << 5) | (alphas[(s>>10) & 0x1f] << 10))
//TODO 		+ (alphad[d & 0x1f] | (alphad[(d>>5) & 0x1f] << 5) | (alphad[(d>>10) & 0x1f] << 10));
//TODO }
//TODO 
//TODO 
//TODO INLINE UINT32 alpha_blend32( UINT32 d, UINT32 s )
//TODO {
//TODO 	const UINT8 *alphas = alpha_cache.alphas;
//TODO 	const UINT8 *alphad = alpha_cache.alphad;
//TODO 	return (alphas[s & 0xff] | (alphas[(s>>8) & 0xff] << 8) | (alphas[(s>>16) & 0xff] << 16))
//TODO 		+ (alphad[d & 0xff] | (alphad[(d>>8) & 0xff] << 8) | (alphad[(d>>16) & 0xff] << 16));
//TODO }
//TODO 
//TODO INLINE UINT32 alpha_blend_r16( UINT32 d, UINT32 s, UINT8 level )
//TODO {
//TODO 	const UINT8 *alphas = alpha_cache.alpha[level];
//TODO 	const UINT8 *alphad = alpha_cache.alpha[255 - level];
//TODO 	return (alphas[s & 0x1f] | (alphas[(s>>5) & 0x1f] << 5) | (alphas[(s>>10) & 0x1f] << 10))
//TODO 		+ (alphad[d & 0x1f] | (alphad[(d>>5) & 0x1f] << 5) | (alphad[(d>>10) & 0x1f] << 10));
//TODO }
//TODO 
//TODO 
//TODO INLINE UINT32 alpha_blend_r32( UINT32 d, UINT32 s, UINT8 level )
//TODO {
//TODO 	const UINT8 *alphas = alpha_cache.alpha[level];
//TODO 	const UINT8 *alphad = alpha_cache.alpha[255 - level];
//TODO 	return (alphas[s & 0xff] | (alphas[(s>>8) & 0xff] << 8) | (alphas[(s>>16) & 0xff] << 16))
//TODO 		+ (alphad[d & 0xff] | (alphad[(d>>8) & 0xff] << 8) | (alphad[(d>>16) & 0xff] << 16));
//TODO }
//TODO 
//TODO /*
//TODO   Copy a bitmap applying rotation, zooming, and arbitrary distortion.
//TODO   This function works in a way that mimics some real hardware like the Konami
//TODO   051316, so it requires little or no further processing on the caller side.
//TODO 
//TODO   Two 16.16 fixed point counters are used to keep track of the position on
//TODO   the source bitmap. startx and starty are the initial values of those counters,
//TODO   indicating the source pixel that will be drawn at coordinates (0,0) in the
//TODO   destination bitmap. The destination bitmap is scanned left to right, top to
//TODO   bottom; every time the cursor moves one pixel to the right, incxx is added
//TODO   to startx and incxy is added to starty. Every time the cursor moves to the
//TODO   next line, incyx is added to startx and incyy is added to startyy.
//TODO 
//TODO   What this means is that if incxy and incyx are both 0, the bitmap will be
//TODO   copied with only zoom and no rotation. If e.g. incxx and incyy are both 0x8000,
//TODO   the source bitmap will be doubled.
//TODO 
//TODO   Rotation is performed this way:
//TODO   incxx = 0x10000 * cos(theta)
//TODO   incxy = 0x10000 * -sin(theta)
//TODO   incyx = 0x10000 * sin(theta)
//TODO   incyy = 0x10000 * cos(theta)
//TODO   this will perform a rotation around (0,0), you'll have to adjust startx and
//TODO   starty to move the center of rotation elsewhere.
//TODO 
//TODO   Optionally the bitmap can be tiled across the screen instead of doing a single
//TODO   copy. This is obtained by setting the wraparound parameter to true.
//TODO  */
//TODO void copyrozbitmap(struct mame_bitmap *dest,struct mame_bitmap *src,
//TODO 		UINT32 startx,UINT32 starty,int incxx,int incxy,int incyx,int incyy,int wraparound,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color,UINT32 priority);
//TODO 
//TODO void fillbitmap(struct mame_bitmap *dest,pen_t pen,const struct rectangle *clip);
//TODO void drawgfxzoom( struct mame_bitmap *dest_bmp,const struct GfxElement *gfx,
//TODO 		unsigned int code,unsigned int color,int flipx,int flipy,int sx,int sy,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color,int scalex,int scaley);
//TODO void pdrawgfxzoom( struct mame_bitmap *dest_bmp,const struct GfxElement *gfx,
//TODO 		unsigned int code,unsigned int color,int flipx,int flipy,int sx,int sy,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color,int scalex,int scaley,
//TODO 		UINT32 priority_mask);
//TODO void mdrawgfxzoom( struct mame_bitmap *dest_bmp,const struct GfxElement *gfx,
//TODO 		unsigned int code,unsigned int color,int flipx,int flipy,int sx,int sy,
//TODO 		const struct rectangle *clip,int transparency,int transparent_color,int scalex,int scaley,
//TODO 		UINT32 priority_mask);
//TODO 
//TODO void drawgfx_toggle_crosshair(void);
//TODO void draw_crosshair(struct mame_bitmap *bitmap,int x,int y,const struct rectangle *clip);
//TODO 
//TODO INLINE void sect_rect(struct rectangle *dst, const struct rectangle *src)
//TODO {
//TODO 	if (src->min_x > dst->min_x) dst->min_x = src->min_x;
//TODO 	if (src->max_x < dst->max_x) dst->max_x = src->max_x;
//TODO 	if (src->min_y > dst->min_y) dst->min_y = src->min_y;
//TODO 	if (src->max_y < dst->max_y) dst->max_y = src->max_y;
//TODO }
//TODO 
//TODO 
//TODO #ifdef __cplusplus
//TODO }
//TODO #endif
//TODO 
//TODO #endif    
}
