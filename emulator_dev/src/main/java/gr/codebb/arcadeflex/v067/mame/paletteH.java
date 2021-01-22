package gr.codebb.arcadeflex.v067.mame;

public class paletteH {
 //TODO /*-------------------------------------------------
//TODO 	TYPE DEFINITIONS
//TODO -------------------------------------------------*/
//TODO 
//TODO struct mame_display;		/* declared elsewhere */
//TODO 
//TODO typedef UINT32 pen_t;
//TODO typedef UINT32 rgb_t;
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	CONSTANTS
//TODO -------------------------------------------------*/
//TODO 
//TODO #define PALETTE_DEFAULT_SHADOW_FACTOR (0.6)
//TODO #define PALETTE_DEFAULT_HIGHLIGHT_FACTOR (1/PALETTE_DEFAULT_SHADOW_FACTOR)
//TODO 
//TODO #define PALETTE_DEFAULT_SHADOW_FACTOR32 (0.7)
//TODO #define PALETTE_DEFAULT_HIGHLIGHT_FACTOR32 (1.4)
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	MACROS
//TODO -------------------------------------------------*/
//TODO 
//TODO #define MAKE_RGB(r,g,b) 	((((r) & 0xff) << 16) | (((g) & 0xff) << 8) | ((b) & 0xff))
//TODO #define MAKE_ARGB(a,r,g,b)	(MAKE_RGB(r,g,b) | (((a) & 0xff) << 24))
//TODO #define RGB_ALPHA(rgb)		(((rgb) >> 24) & 0xff)
//TODO #define RGB_RED(rgb)		(((rgb) >> 16) & 0xff)
//TODO #define RGB_GREEN(rgb)		(((rgb) >> 8) & 0xff)
//TODO #define RGB_BLUE(rgb)		((rgb) & 0xff)
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	GLOBAL VARIABLES
//TODO -------------------------------------------------*/
//TODO 
//TODO extern UINT32 direct_rgb_components[3];
//TODO extern UINT16 *palette_shadow_table;
//TODO 
//TODO extern data8_t *paletteram;
//TODO extern data8_t *paletteram_2;	/* use when palette RAM is split in two parts */
//TODO extern data16_t *paletteram16;
//TODO extern data16_t *paletteram16_2;
//TODO extern data32_t *paletteram32;
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	PROTOTYPES
//TODO -------------------------------------------------*/
//TODO 
//TODO int palette_start(void);
//TODO int palette_init(void);
//TODO int palette_get_total_colors_with_ui(void);
//TODO 
//TODO void palette_update_display(struct mame_display *display);
//TODO 
//TODO void palette_set_color(pen_t pen, UINT8 r, UINT8 g, UINT8 b);
//TODO void palette_get_color(pen_t pen, UINT8 *r, UINT8 *g, UINT8 *b);
//TODO 
//TODO void palette_set_brightness(pen_t pen, double bright);
//TODO void palette_set_shadow_factor(double factor);
//TODO void palette_set_highlight_factor(double factor);
//TODO void palette_set_shadow_mode(int mode); //* AAT 032803
//TODO void palette_set_shadow_factor32(double factor);
//TODO void palette_set_highlight_factor32(double factor);
//TODO void palette_set_shadow_dRGB32(int mode, int dr, int dg, int db, int noclip);
//TODO 
//TODO void palette_set_global_gamma(double _gamma);
//TODO double palette_get_global_gamma(void);
//TODO 
//TODO void palette_set_global_brightness(double brightness);
//TODO void palette_set_global_brightness_adjust(double adjustment);
//TODO double palette_get_global_brightness(void);
//TODO 
//TODO pen_t get_black_pen(void);
//TODO 
//TODO 
//TODO /* here are some functions to handle commonly used palette layouts, so you don't
//TODO    have to write your own paletteram_w() function. */
//TODO 
//TODO READ_HANDLER( paletteram_r );
//TODO READ_HANDLER( paletteram_2_r );
//TODO READ16_HANDLER( paletteram16_word_r );
//TODO READ16_HANDLER( paletteram16_2_word_r );
//TODO READ32_HANDLER( paletteram32_r );
//TODO 
//TODO WRITE_HANDLER( paletteram_BBGGGRRR_w );
//TODO WRITE_HANDLER( paletteram_RRRGGGBB_w );
//TODO WRITE_HANDLER( paletteram_BBBGGGRR_w );
//TODO WRITE_HANDLER( paletteram_IIBBGGRR_w );
//TODO WRITE_HANDLER( paletteram_BBGGRRII_w );
//TODO 
//TODO /* _w       least significant byte first */
//TODO /* _swap_w  most significant byte first */
//TODO /* _split_w least and most significant bytes are not consecutive */
//TODO /* _word_w  use with 16 bit CPU */
//TODO /* R, G, B are bits, r, g, b are bytes */
//TODO /*                        MSB          LSB */
//TODO WRITE_HANDLER( paletteram_xxxxBBBBGGGGRRRR_w );
//TODO WRITE_HANDLER( paletteram_xxxxBBBBGGGGRRRR_swap_w );
//TODO WRITE_HANDLER( paletteram_xxxxBBBBGGGGRRRR_split1_w );	/* uses paletteram[] */
//TODO WRITE_HANDLER( paletteram_xxxxBBBBGGGGRRRR_split2_w );	/* uses paletteram_2[] */
//TODO WRITE16_HANDLER( paletteram16_xxxxBBBBGGGGRRRR_word_w );
//TODO WRITE_HANDLER( paletteram_xxxxBBBBRRRRGGGG_w );
//TODO WRITE_HANDLER( paletteram_xxxxBBBBRRRRGGGG_swap_w );
//TODO WRITE_HANDLER( paletteram_xxxxBBBBRRRRGGGG_split1_w );	/* uses paletteram[] */
//TODO WRITE_HANDLER( paletteram_xxxxBBBBRRRRGGGG_split2_w );	/* uses paletteram_2[] */
//TODO WRITE16_HANDLER( paletteram16_xxxxBBBBRRRRGGGG_word_w );
//TODO WRITE_HANDLER( paletteram_xxxxRRRRBBBBGGGG_split1_w );	/* uses paletteram[] */
//TODO WRITE_HANDLER( paletteram_xxxxRRRRBBBBGGGG_split2_w );	/* uses paletteram_2[] */
//TODO WRITE_HANDLER( paletteram_xxxxRRRRGGGGBBBB_w );
//TODO WRITE_HANDLER( paletteram_xxxxRRRRGGGGBBBB_swap_w );
//TODO WRITE16_HANDLER( paletteram16_xxxxRRRRGGGGBBBB_word_w );
//TODO WRITE_HANDLER( paletteram_RRRRGGGGBBBBxxxx_swap_w );
//TODO WRITE_HANDLER( paletteram_RRRRGGGGBBBBxxxx_split1_w );	/* uses paletteram[] */
//TODO WRITE_HANDLER( paletteram_RRRRGGGGBBBBxxxx_split2_w );	/* uses paletteram_2[] */
//TODO WRITE16_HANDLER( paletteram16_RRRRGGGGBBBBxxxx_word_w );
//TODO WRITE_HANDLER( paletteram_BBBBGGGGRRRRxxxx_swap_w );
//TODO WRITE_HANDLER( paletteram_BBBBGGGGRRRRxxxx_split1_w );	/* uses paletteram[] */
//TODO WRITE_HANDLER( paletteram_BBBBGGGGRRRRxxxx_split2_w );	/* uses paletteram_2[] */
//TODO WRITE16_HANDLER( paletteram16_BBBBGGGGRRRRxxxx_word_w );
//TODO WRITE_HANDLER( paletteram_xBBBBBGGGGGRRRRR_w );
//TODO WRITE_HANDLER( paletteram_xBBBBBGGGGGRRRRR_swap_w );
//TODO WRITE_HANDLER( paletteram_xBBBBBGGGGGRRRRR_split1_w );	/* uses paletteram[] */
//TODO WRITE_HANDLER( paletteram_xBBBBBGGGGGRRRRR_split2_w );	/* uses paletteram_2[] */
//TODO WRITE16_HANDLER( paletteram16_xBBBBBGGGGGRRRRR_word_w );
//TODO WRITE_HANDLER( paletteram_xRRRRRGGGGGBBBBB_w );
//TODO WRITE16_HANDLER( paletteram16_xRRRRRGGGGGBBBBB_word_w );
//TODO WRITE16_HANDLER( paletteram16_xGGGGGRRRRRBBBBB_word_w );
//TODO WRITE16_HANDLER( paletteram16_xGGGGGBBBBBRRRRR_word_w );
//TODO WRITE_HANDLER( paletteram_RRRRRGGGGGBBBBBx_w );
//TODO WRITE16_HANDLER( paletteram16_RRRRRGGGGGBBBBBx_word_w );
//TODO WRITE16_HANDLER( paletteram16_IIIIRRRRGGGGBBBB_word_w );
//TODO WRITE16_HANDLER( paletteram16_RRRRGGGGBBBBIIII_word_w );
//TODO WRITE16_HANDLER( paletteram16_xrgb_word_w );
//TODO WRITE16_HANDLER( paletteram16_RRRRGGGGBBBBRGBx_word_w );
//TODO 
//TODO 
//TODO /******************************************************************************
//TODO 
//TODO  Commonly used color PROM handling functions
//TODO 
//TODO ******************************************************************************/
//TODO 
//TODO void palette_init_black_and_white(UINT16 *colortable, const UINT8 *color_prom);
//TODO void palette_init_RRRR_GGGG_BBBB(UINT16 *colortable, const UINT8 *color_prom);
//TODO 
//TODO #ifdef __cplusplus
//TODO }
//TODO #endif
//TODO 
//TODO #endif
//TODO        
}
