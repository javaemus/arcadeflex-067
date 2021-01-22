package gr.codebb.arcadeflex.v067.mame;

public class palette {
//TODO #define VERBOSE 0
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	CONSTANTS
//TODO -------------------------------------------------*/
//TODO 
//TODO #define PEN_BRIGHTNESS_BITS		8
//TODO #define MAX_PEN_BRIGHTNESS		(4 << PEN_BRIGHTNESS_BITS)
//TODO 
//TODO enum
//TODO {
//TODO 	PALETTIZED_16BIT,
//TODO 	DIRECT_15BIT,
//TODO 	DIRECT_32BIT
//TODO };
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	GLOBAL VARIABLES
//TODO -------------------------------------------------*/
//TODO 
//TODO UINT32 direct_rgb_components[3];
//TODO UINT16 *palette_shadow_table;
//TODO 
//TODO data8_t *paletteram;
//TODO data8_t *paletteram_2;	/* use when palette RAM is split in two parts */
//TODO data16_t *paletteram16;
//TODO data16_t *paletteram16_2;
//TODO data32_t *paletteram32;
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	LOCAL VARIABLES
//TODO -------------------------------------------------*/
//TODO 
//TODO static rgb_t *game_palette;			/* RGB palette as set by the driver */
//TODO static rgb_t *adjusted_palette;		/* actual RGB palette after brightness/gamma adjustments */
//TODO static UINT32 *dirty_palette;
//TODO static UINT16 *pen_brightness;
//TODO 
//TODO static UINT8 adjusted_palette_dirty;
//TODO static UINT8 debug_palette_dirty;
//TODO 
//TODO static UINT16 shadow_factor, highlight_factor;
//TODO static double global_brightness, global_brightness_adjust, global_gamma;
//TODO 
//TODO static UINT8 colormode;
//TODO static pen_t total_colors;
//TODO static pen_t total_colors_with_ui;
//TODO 
//TODO static UINT8 color_correct_table[(MAX_PEN_BRIGHTNESS * MAX_PEN_BRIGHTNESS) >> PEN_BRIGHTNESS_BITS];
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	PROTOTYPES
//TODO -------------------------------------------------*/
//TODO 
//TODO static int palette_alloc(void);
//TODO static void palette_reset(void);
//TODO static void recompute_adjusted_palette(int brightness_or_gamma_changed);
//TODO static void internal_modify_pen(pen_t pen, rgb_t color, int pen_bright);
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	rgb_to_direct15 - convert an RGB triplet to
//TODO 	a 15-bit OSD-specified RGB value
//TODO -------------------------------------------------*/
//TODO 
//TODO INLINE UINT16 rgb_to_direct15(rgb_t rgb)
//TODO {
//TODO 	return  (  RGB_RED(rgb) >> 3) * (direct_rgb_components[0] / 0x1f) +
//TODO 			(RGB_GREEN(rgb) >> 3) * (direct_rgb_components[1] / 0x1f) +
//TODO 			( RGB_BLUE(rgb) >> 3) * (direct_rgb_components[2] / 0x1f);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	rgb_to_direct32 - convert an RGB triplet to
//TODO 	a 32-bit OSD-specified RGB value
//TODO -------------------------------------------------*/
//TODO 
//TODO INLINE UINT32 rgb_to_direct32(rgb_t rgb)
//TODO {
//TODO 	return    RGB_RED(rgb) * (direct_rgb_components[0] / 0xff) +
//TODO 			RGB_GREEN(rgb) * (direct_rgb_components[1] / 0xff) +
//TODO 			 RGB_BLUE(rgb) * (direct_rgb_components[2] / 0xff);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	adjust_palette_entry - adjust a palette
//TODO 	entry for brightness and gamma
//TODO -------------------------------------------------*/
//TODO 
//TODO INLINE rgb_t adjust_palette_entry(rgb_t entry, int pen_bright)
//TODO {
//TODO 	int r = color_correct_table[(RGB_RED(entry) * pen_bright) >> PEN_BRIGHTNESS_BITS];
//TODO 	int g = color_correct_table[(RGB_GREEN(entry) * pen_bright) >> PEN_BRIGHTNESS_BITS];
//TODO 	int b = color_correct_table[(RGB_BLUE(entry) * pen_bright) >> PEN_BRIGHTNESS_BITS];
//TODO 	return MAKE_RGB(r,g,b);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	mark_pen_dirty - mark a given pen index dirty
//TODO -------------------------------------------------*/
//TODO 
//TODO INLINE void mark_pen_dirty(int pen)
//TODO {
//TODO 	dirty_palette[pen / 32] |= 1 << (pen % 32);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_start - palette initialization that
//TODO 	takes place before the display is created
//TODO -------------------------------------------------*/
//TODO 
//TODO int palette_start(void)
//TODO {
//TODO 	/* init statics */
//TODO 	adjusted_palette_dirty = 1;
//TODO 	debug_palette_dirty = 1;
//TODO 
//TODO 	shadow_factor = (int)(PALETTE_DEFAULT_SHADOW_FACTOR * (double)(1 << PEN_BRIGHTNESS_BITS));
//TODO 	highlight_factor = (int)(PALETTE_DEFAULT_HIGHLIGHT_FACTOR * (double)(1 << PEN_BRIGHTNESS_BITS));
//TODO 	global_brightness = (options.brightness > .001) ? options.brightness : 1.0;
//TODO 	global_brightness_adjust = 1.0;
//TODO 	global_gamma = (options.gamma > .001) ? options.gamma : 1.0;
//TODO 
//TODO 	/* determine the color mode */
//TODO 	if (Machine->color_depth == 15)
//TODO 		colormode = DIRECT_15BIT;
//TODO 	else if (Machine->color_depth == 32)
//TODO 		colormode = DIRECT_32BIT;
//TODO 	else
//TODO 		colormode = PALETTIZED_16BIT;
//TODO 
//TODO 	/* ensure that RGB direct video modes don't have a colortable */
//TODO 	if ((Machine->drv->video_attributes & VIDEO_RGB_DIRECT) &&
//TODO 			Machine->drv->color_table_len)
//TODO 	{
//TODO 		logerror("Error: VIDEO_RGB_DIRECT requires color_table_len to be 0.\n");
//TODO 		return 1;
//TODO 	}
//TODO 
//TODO 	/* compute the total colors, including shadows and highlights */
//TODO 	total_colors = Machine->drv->total_colors;
//TODO 	if (Machine->drv->video_attributes & VIDEO_HAS_SHADOWS)
//TODO 		total_colors += Machine->drv->total_colors;
//TODO 	if (Machine->drv->video_attributes & VIDEO_HAS_HIGHLIGHTS)
//TODO 		total_colors += Machine->drv->total_colors;
//TODO 	total_colors_with_ui = total_colors;
//TODO 
//TODO 	/* make sure we still fit in 16 bits */
//TODO 	if (total_colors > 65536)
//TODO 	{
//TODO 		logerror("Error: palette has more than 65536 colors.\n");
//TODO 		return 1;
//TODO 	}
//TODO 
//TODO 	/* allocate all the data structures */
//TODO 	if (palette_alloc())
//TODO 		return 1;
//TODO 
//TODO 	/* set up save/restore of the palette */
//TODO 	state_save_register_UINT32("palette", 0, "colors", game_palette, total_colors);
//TODO 	state_save_register_UINT16("palette", 0, "brightness", pen_brightness, Machine->drv->total_colors);
//TODO 	state_save_register_func_postload(palette_reset);
//TODO 
//TODO 	return 0;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO //* AAT 032803
//TODO /*-------------------------------------------------
//TODO 	palette_set_shadow_mode(mode)
//TODO 
//TODO 		mode: 0 = use preset 0 (default shadow)
//TODO 		      1 = use preset 1 (default highlight)
//TODO 		      2 = use preset 2 *
//TODO 		      3 = use preset 3 *
//TODO 
//TODO 	* Preset 2 & 3 work independently under 32bpp,
//TODO 	  supporting up to four different types of
//TODO 	  shadows at one time. They mirror preset 1 & 2
//TODO 	  in lower depth settings to maintain
//TODO 	  compatibility.
//TODO 
//TODO 
//TODO 	palette_set_shadow_factor32(factor)
//TODO 
//TODO 		factor: 1.0(normal) to 0.0(darker)
//TODO 
//TODO 
//TODO 	palette_set_highlight_factor32(factor)
//TODO 
//TODO 		factor: 1.0(normal) to 2.0(brighter)
//TODO 
//TODO 
//TODO 	palette_set_shadow_dRGB32(mode, dr, dg, db, noclip)
//TODO 
//TODO 		mode:    0 to   3, which preset to configure
//TODO 
//TODO 		  dr: -255 to 255,   red displacement
//TODO 		  dg: -255 to 255, green displacement
//TODO 		  db: -255 to 255,  blue displacement
//TODO 
//TODO 		noclip: 0 = resultant RGB clipped at 0x00/0xff
//TODO 		        1 = resultant RGB wraparound 0x00/0xff
//TODO 
//TODO 	* Color shadows only work under 32bpp.
//TODO 	  This function has no effect in lower color
//TODO 	  depths where
//TODO 
//TODO 		palette_set_shadow_factor32() or
//TODO 		palette_set_highlight_factor32()
//TODO 
//TODO 	  should be used instead.
//TODO 
//TODO -------------------------------------------------*/
//TODO #define MAX_SHADOW_PRESETS 4
//TODO 
//TODO static UINT16 *shadow_table_base[MAX_SHADOW_PRESETS];
//TODO 
//TODO 
//TODO static void internal_set_shadow_preset(int mode, double factor, int dr, int dg, int db, int noclip, int style, int init)
//TODO {
//TODO 	static double oldfactor[MAX_SHADOW_PRESETS] = {0,0,0,0};
//TODO 	static int oldRGB[MAX_SHADOW_PRESETS][3] = {{0,0,0},{0,0,0},{0,0,0},{0,0,0}};
//TODO 	static int oldclip;
//TODO 
//TODO 	UINT16 *table_ptr;
//TODO 	int i, r, g, b;
//TODO 
//TODO 	if (mode < 0 || mode >= MAX_SHADOW_PRESETS) return;
//TODO 
//TODO 	if ((table_ptr = shadow_table_base[mode]) == NULL) return;
//TODO 
//TODO 	if (style)
//TODO 	{
//TODO 		if (factor < 0) factor = 0;
//TODO 
//TODO 		if (!init && oldfactor[mode] == factor) return;
//TODO 
//TODO 		oldfactor[mode] = factor;
//TODO 
//TODO 		if (colormode != DIRECT_32BIT)
//TODO 		{
//TODO 			if (style == 1) palette_set_shadow_factor(factor); else
//TODO 			if (style == 2) palette_set_highlight_factor(factor);
//TODO 			return;
//TODO 		}
//TODO 
//TODO 		for (i=0; i<32768; i++)
//TODO 		{
//TODO 			r = (int)(factor * (i & 0x7c00));
//TODO 			g = (int)(factor * (i & 0x03e0));
//TODO 			b = (int)(factor * (i & 0x001f));
//TODO 
//TODO 			if (r < 0x7c00) r &= 0x7c00; else r = 0x7c00;
//TODO 			if (g < 0x03e0) g &= 0x03e0; else g = 0x03e0;
//TODO 			if (b < 0x001f) b &= 0x001f; else b = 0x001f;
//TODO 
//TODO 			table_ptr[i] = (UINT16)(r | g | b);
//TODO 		}
//TODO 	}
//TODO 	else
//TODO 	{
//TODO 		if (colormode != DIRECT_32BIT) return;
//TODO 
//TODO 		if (dr < -0xff) dr = -0xff; else if (dr > 0xff) dr = 0xff;
//TODO 		if (dg < -0xff) dg = -0xff; else if (dg > 0xff) dg = 0xff;
//TODO 		if (db < -0xff) db = -0xff; else if (db > 0xff) db = 0xff;
//TODO 		dr >>= 3; dg >>= 3; db >>= 3;
//TODO 
//TODO 		if (!init && oldclip==noclip && oldRGB[mode][0]==dr && oldRGB[mode][1]==dg && oldRGB[mode][2]==db) return;
//TODO 
//TODO 		#ifdef MAME_DEBUG
//TODO 			//usrintf_showmessage("shadow %d recalc %d %d %d %02x", mode, dr, dg, db, noclip);
//TODO 		#endif
//TODO 
//TODO 		oldclip = noclip;
//TODO 		oldRGB[mode][0] = dr; oldRGB[mode][1] = dg; oldRGB[mode][2] = db;
//TODO 
//TODO 		dr <<= 10; dg <<= 5;
//TODO 
//TODO 		if (noclip)
//TODO 		{
//TODO 			for (i=0; i<32768; i++)
//TODO 			{
//TODO 				r = (int)(dr + (i & 0x7c00));
//TODO 				g = (int)(dg + (i & 0x03e0));
//TODO 				b = (int)(db + (i & 0x001f));
//TODO 
//TODO 				table_ptr[i] = (UINT16)((r & 0x7c00) | (g & 0x03e0) | (b & 0x001f));
//TODO 			}
//TODO 		}
//TODO 		else
//TODO 		{
//TODO 			for (i=0; i<32768; i++)
//TODO 			{
//TODO 				r = (int)(dr + (i & 0x7c00));
//TODO 				g = (int)(dg + (i & 0x03e0));
//TODO 				b = (int)(db + (i & 0x001f));
//TODO 
//TODO 				if (r < 0) r = 0; else if (r > 0x7c00) r = 0x7c00;
//TODO 				if (g < 0) g = 0; else if (g > 0x03e0) g = 0x03e0;
//TODO 				if (b < 0) b = 0; else if (b > 0x001f) b = 0x001f;
//TODO 
//TODO 				table_ptr[i] = (UINT16)(r | g | b);
//TODO 			}
//TODO 		}
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO void palette_set_shadow_mode(int mode)
//TODO {
//TODO 	if (mode >= 0 && mode < MAX_SHADOW_PRESETS) palette_shadow_table = shadow_table_base[mode];
//TODO }
//TODO 
//TODO 
//TODO void palette_set_shadow_factor32(double factor)
//TODO {
//TODO 	internal_set_shadow_preset(0, factor, 0, 0, 0, 0, 1, 0);
//TODO }
//TODO 
//TODO 
//TODO void palette_set_highlight_factor32(double factor)
//TODO {
//TODO 	internal_set_shadow_preset(1, factor, 0, 0, 0, 0, 2, 0);
//TODO }
//TODO 
//TODO 
//TODO void palette_set_shadow_dRGB32(int mode, int dr, int dg, int db, int noclip)
//TODO {
//TODO 	internal_set_shadow_preset(mode, 0, dr, dg, db, noclip, 0, 0);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_alloc - allocate memory for palette
//TODO 	structures
//TODO -------------------------------------------------*/
//TODO 
//TODO static int palette_alloc(void)
//TODO {
//TODO 	int max_total_colors = total_colors + 2;
//TODO 	int i;
//TODO 
//TODO 	/* allocate memory for the raw game palette */
//TODO 	game_palette = auto_malloc(max_total_colors * sizeof(game_palette[0]));
//TODO 	if (!game_palette)
//TODO 		return 1;
//TODO 	for (i = 0; i < max_total_colors; i++)
//TODO 		game_palette[i] = MAKE_RGB((i & 1) * 0xff, ((i >> 1) & 1) * 0xff, ((i >> 2) & 1) * 0xff);
//TODO 
//TODO 	/* allocate memory for the adjusted game palette */
//TODO 	adjusted_palette = auto_malloc(max_total_colors * sizeof(adjusted_palette[0]));
//TODO 	if (!adjusted_palette)
//TODO 		return 1;
//TODO 	for (i = 0; i < max_total_colors; i++)
//TODO 		adjusted_palette[i] = game_palette[i];
//TODO 
//TODO 	/* allocate memory for the dirty palette array */
//TODO 	dirty_palette = auto_malloc((max_total_colors + 31) / 32 * sizeof(dirty_palette[0]));
//TODO 	if (!dirty_palette)
//TODO 		return 1;
//TODO 	for (i = 0; i < max_total_colors; i++)
//TODO 		mark_pen_dirty(i);
//TODO 
//TODO 	/* allocate memory for the pen table */
//TODO 	Machine->pens = auto_malloc(total_colors * sizeof(Machine->pens[0]));
//TODO 	if (!Machine->pens)
//TODO 		return 1;
//TODO 	for (i = 0; i < total_colors; i++)
//TODO 		Machine->pens[i] = i;
//TODO 
//TODO 	/* allocate memory for the per-entry brightness table */
//TODO 	pen_brightness = auto_malloc(Machine->drv->total_colors * sizeof(pen_brightness[0]));
//TODO 	if (!pen_brightness)
//TODO 		return 1;
//TODO 	for (i = 0; i < Machine->drv->total_colors; i++)
//TODO 		pen_brightness[i] = 1 << PEN_BRIGHTNESS_BITS;
//TODO 
//TODO 	/* allocate memory for the colortables, if needed */
//TODO 	if (Machine->drv->color_table_len)
//TODO 	{
//TODO 		/* first for the raw colortable */
//TODO 		Machine->game_colortable = auto_malloc(Machine->drv->color_table_len * sizeof(Machine->game_colortable[0]));
//TODO 		if (!Machine->game_colortable)
//TODO 			return 1;
//TODO 		for (i = 0; i < Machine->drv->color_table_len; i++)
//TODO 			Machine->game_colortable[i] = i % total_colors;
//TODO 
//TODO 		/* then for the remapped colortable */
//TODO 		Machine->remapped_colortable = auto_malloc(Machine->drv->color_table_len * sizeof(Machine->remapped_colortable[0]));
//TODO 		if (!Machine->remapped_colortable)
//TODO 			return 1;
//TODO 	}
//TODO 
//TODO 	/* otherwise, keep the game_colortable NULL and point the remapped_colortable to the pens */
//TODO 	else
//TODO 	{
//TODO 		Machine->game_colortable = NULL;
//TODO 		Machine->remapped_colortable = Machine->pens;	/* straight 1:1 mapping from palette to colortable */
//TODO 	}
//TODO 
//TODO 	/* allocate memory for the debugger pens */
//TODO 	Machine->debug_pens = auto_malloc(DEBUGGER_TOTAL_COLORS * sizeof(Machine->debug_pens[0]));
//TODO 	if (!Machine->debug_pens)
//TODO 		return 1;
//TODO 	for (i = 0; i < DEBUGGER_TOTAL_COLORS; i++)
//TODO 		Machine->debug_pens[i] = i;
//TODO 
//TODO 	/* allocate memory for the debugger colortable */
//TODO 	Machine->debug_remapped_colortable = auto_malloc(2 * DEBUGGER_TOTAL_COLORS * DEBUGGER_TOTAL_COLORS * sizeof(Machine->debug_remapped_colortable[0]));
//TODO 	if (!Machine->debug_remapped_colortable)
//TODO 		return 1;
//TODO 	for (i = 0; i < DEBUGGER_TOTAL_COLORS * DEBUGGER_TOTAL_COLORS; i++)
//TODO 	{
//TODO 		Machine->debug_remapped_colortable[2*i+0] = i / DEBUGGER_TOTAL_COLORS;
//TODO 		Machine->debug_remapped_colortable[2*i+1] = i % DEBUGGER_TOTAL_COLORS;
//TODO 	}
//TODO 
//TODO #if 0
//TODO 	/* allocate the shadow lookup table for 16bpp modes */
//TODO 	palette_shadow_table = NULL;
//TODO 	if (colormode == PALETTIZED_16BIT)
//TODO 	{
//TODO 		/* we allocate a full 65536 entries table, to prevent memory corruption
//TODO 		 * bugs should the tilemap contains pens >= total_colors
//TODO 		 * (e.g. Machine->uifont->colortable[0] as returned by get_black_pen())
//TODO 		 */
//TODO 		palette_shadow_table = auto_malloc(65536 * sizeof(palette_shadow_table[0]));
//TODO 		if (!palette_shadow_table)
//TODO 			return 1;
//TODO 
//TODO 		/* map entries up to the total_colors so they point to the next block of colors */
//TODO 		for (i = 0; i < 65536; i++)
//TODO 		{
//TODO 			palette_shadow_table[i] = i;
//TODO 			if ((Machine->drv->video_attributes & VIDEO_HAS_SHADOWS) && i < Machine->drv->total_colors)
//TODO 				palette_shadow_table[i] += Machine->drv->total_colors;
//TODO 		}
//TODO 	}
//TODO #else
//TODO 	{
//TODO 		//* AAT 032803
//TODO 		UINT16 *table_ptr;
//TODO 		int c = Machine->drv->total_colors;
//TODO 		int cx2 = c << 1;
//TODO 
//TODO 		for (i=0; i<MAX_SHADOW_PRESETS; i++) shadow_table_base[i] = NULL;
//TODO 		palette_shadow_table = NULL;
//TODO 
//TODO 		if (Machine->drv->video_attributes & VIDEO_HAS_SHADOWS)
//TODO 		{
//TODO 			shadow_table_base[0] = table_ptr = auto_malloc(65536 * sizeof(shadow_table_base[0]));
//TODO 			if (!table_ptr) return 1;
//TODO 
//TODO 			if (colormode != DIRECT_32BIT)
//TODO 			{
//TODO 				for (i=0; i<c; i++) table_ptr[i] = c + i;
//TODO 				for (i=c; i<65536; i++) table_ptr[i] = i;
//TODO 			}
//TODO 
//TODO 			if (palette_shadow_table == NULL) palette_shadow_table = table_ptr;
//TODO 
//TODO 			internal_set_shadow_preset(0, PALETTE_DEFAULT_SHADOW_FACTOR32, 0, 0, 0, 0, 1, 1);
//TODO 		}
//TODO 
//TODO 		if (Machine->drv->video_attributes & VIDEO_HAS_HIGHLIGHTS)
//TODO 		{
//TODO 			shadow_table_base[1] = table_ptr = auto_malloc(65536 * sizeof(shadow_table_base[0]));
//TODO 			if (!table_ptr) return 1;
//TODO 
//TODO 			if (colormode != DIRECT_32BIT)
//TODO 			{
//TODO 				for (i=0; i<c; i++) table_ptr[i] = cx2 + i;
//TODO 				for (i=c; i<65536; i++) table_ptr[i] = i;
//TODO 			}
//TODO 
//TODO 			if (palette_shadow_table == NULL) palette_shadow_table = table_ptr;
//TODO 
//TODO 			internal_set_shadow_preset(1, PALETTE_DEFAULT_HIGHLIGHT_FACTOR32, 0, 0, 0, 0, 2, 1);
//TODO 		}
//TODO 
//TODO 		if (colormode == DIRECT_32BIT)
//TODO 		{
//TODO 			if (shadow_table_base[0] != NULL) shadow_table_base[2] = shadow_table_base[0] + 32768;
//TODO 			if (shadow_table_base[1] != NULL) shadow_table_base[3] = shadow_table_base[1] + 32768;
//TODO 		}
//TODO 		else
//TODO 		{
//TODO 			shadow_table_base[2] = shadow_table_base[0];
//TODO 			shadow_table_base[3] = shadow_table_base[1];
//TODO 		}
//TODO 	}
//TODO #endif
//TODO 
//TODO 	return 0;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_init - palette initialization that
//TODO 	takes place after the display is created
//TODO -------------------------------------------------*/
//TODO 
//TODO int palette_init(void)
//TODO {
//TODO 	int i;
//TODO 
//TODO 	/* recompute the default palette and initalize the color correction table */
//TODO 	recompute_adjusted_palette(1);
//TODO 
//TODO 	/* now let the driver modify the initial palette and colortable */
//TODO 	if (Machine->drv->init_palette)
//TODO 		(*Machine->drv->init_palette)(Machine->game_colortable, memory_region(REGION_PROMS));
//TODO 
//TODO 	/* switch off the color mode */
//TODO 	switch (colormode)
//TODO 	{
//TODO 		/* 16-bit paletteized case */
//TODO 		case PALETTIZED_16BIT:
//TODO 		{
//TODO 			/* refresh the palette to support shadows in static palette games */
//TODO 			for (i = 0; i < Machine->drv->total_colors; i++)
//TODO 				palette_set_color(i, RGB_RED(game_palette[i]), RGB_GREEN(game_palette[i]), RGB_BLUE(game_palette[i]));
//TODO 
//TODO 			/* map the UI pens */
//TODO 			if (total_colors_with_ui <= 65534)
//TODO 			{
//TODO 				game_palette[total_colors + 0] = adjusted_palette[total_colors + 0] = MAKE_RGB(0x00,0x00,0x00);
//TODO 				game_palette[total_colors + 1] = adjusted_palette[total_colors + 1] = MAKE_RGB(0xff,0xff,0xff);
//TODO 				Machine->uifont->colortable[0] = Machine->uifont->colortable[3] = total_colors_with_ui++;
//TODO 				Machine->uifont->colortable[1] = Machine->uifont->colortable[2] = total_colors_with_ui++;
//TODO 			}
//TODO 			else
//TODO 			{
//TODO 				game_palette[0] = adjusted_palette[0] = MAKE_RGB(0x00,0x00,0x00);
//TODO 				game_palette[65535] = adjusted_palette[65535] = MAKE_RGB(0xff,0xff,0xff);
//TODO 				Machine->uifont->colortable[0] = Machine->uifont->colortable[3] = 0;
//TODO 				Machine->uifont->colortable[1] = Machine->uifont->colortable[2] = 65535;
//TODO 			}
//TODO 			break;
//TODO 		}
//TODO 
//TODO 		/* 15-bit direct case */
//TODO 		case DIRECT_15BIT:
//TODO 		{
//TODO 			/* remap the game palette into direct RGB pens */
//TODO 			for (i = 0; i < total_colors; i++)
//TODO 				Machine->pens[i] = rgb_to_direct15(game_palette[i]);
//TODO 
//TODO 			/* map the UI pens */
//TODO 			Machine->uifont->colortable[0] = Machine->uifont->colortable[3] = rgb_to_direct15(MAKE_RGB(0x00,0x00,0x00));
//TODO 			Machine->uifont->colortable[1] = Machine->uifont->colortable[2] = rgb_to_direct15(MAKE_RGB(0xff,0xff,0xff));
//TODO 			break;
//TODO 		}
//TODO 
//TODO 		case DIRECT_32BIT:
//TODO 		{
//TODO 			/* remap the game palette into direct RGB pens */
//TODO 			for (i = 0; i < total_colors; i++)
//TODO 				Machine->pens[i] = rgb_to_direct32(game_palette[i]);
//TODO 
//TODO 			/* map the UI pens */
//TODO 			Machine->uifont->colortable[0] = Machine->uifont->colortable[3] = rgb_to_direct32(MAKE_RGB(0x00,0x00,0x00));
//TODO 			Machine->uifont->colortable[1] = Machine->uifont->colortable[2] = rgb_to_direct32(MAKE_RGB(0xff,0xff,0xff));
//TODO 			break;
//TODO 		}
//TODO 	}
//TODO 
//TODO 	/* now compute the remapped_colortable */
//TODO 	for (i = 0; i < Machine->drv->color_table_len; i++)
//TODO 	{
//TODO 		pen_t color = Machine->game_colortable[i];
//TODO 
//TODO 		/* check for invalid colors set by Machine->drv->init_palette */
//TODO 		if (color < total_colors)
//TODO 			Machine->remapped_colortable[i] = Machine->pens[color];
//TODO 		else
//TODO 			usrintf_showmessage("colortable[%d] (=%d) out of range (total_colors = %d)",
//TODO 					i,color,total_colors);
//TODO 	}
//TODO 
//TODO 	/* all done */
//TODO 	return 0;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_get_total_colors_with_ui - returns
//TODO 	the total number of palette entries including
//TODO 	UI
//TODO -------------------------------------------------*/
//TODO 
//TODO int palette_get_total_colors_with_ui(void)
//TODO {
//TODO 	int result = Machine->drv->total_colors;
//TODO 	if (Machine->drv->video_attributes & VIDEO_HAS_SHADOWS)
//TODO 		result += Machine->drv->total_colors;
//TODO 	if (Machine->drv->video_attributes & VIDEO_HAS_HIGHLIGHTS)
//TODO 		result += Machine->drv->total_colors;
//TODO 	if (result <= 65534)
//TODO 		result += 2;
//TODO 	return result;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_update_display - update the display
//TODO 	state with our latest info
//TODO -------------------------------------------------*/
//TODO 
//TODO void palette_update_display(struct mame_display *display)
//TODO {
//TODO 	/* palettized case: point to the palette info */
//TODO 	if (colormode == PALETTIZED_16BIT)
//TODO 	{
//TODO 		display->game_palette = adjusted_palette;
//TODO 		display->game_palette_entries = total_colors_with_ui;
//TODO 		display->game_palette_dirty = dirty_palette;
//TODO 
//TODO 		if (adjusted_palette_dirty)
//TODO 			display->changed_flags |= GAME_PALETTE_CHANGED;
//TODO 	}
//TODO 
//TODO 	/* direct case: no palette mucking */
//TODO 	else
//TODO 	{
//TODO 		display->game_palette = NULL;
//TODO 		display->game_palette_entries = 0;
//TODO 		display->game_palette_dirty = NULL;
//TODO 	}
//TODO 
//TODO 	/* debugger always has a palette */
//TODO #ifdef MAME_DEBUG
//TODO 	display->debug_palette = debugger_palette;
//TODO 	display->debug_palette_entries = DEBUGGER_TOTAL_COLORS;
//TODO #endif
//TODO 
//TODO 	/* update the dirty state */
//TODO 	if (debug_palette_dirty)
//TODO 		display->changed_flags |= DEBUG_PALETTE_CHANGED;
//TODO 
//TODO 	/* clear the dirty flags */
//TODO 	adjusted_palette_dirty = 0;
//TODO 	debug_palette_dirty = 0;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	internal_modify_single_pen - change a single
//TODO 	pen and recompute its adjusted RGB value
//TODO -------------------------------------------------*/
//TODO 
//TODO static void internal_modify_single_pen(pen_t pen, rgb_t color, int pen_bright)
//TODO {
//TODO 	rgb_t adjusted_color;
//TODO 
//TODO 	/* skip if out of bounds or not ready */
//TODO 	if (pen >= total_colors)
//TODO 		return;
//TODO 
//TODO 	/* update the raw palette */
//TODO 	game_palette[pen] = color;
//TODO 
//TODO 	/* now update the adjusted color if it's different */
//TODO 	adjusted_color = adjust_palette_entry(color, pen_bright);
//TODO 	if (adjusted_color != adjusted_palette[pen])
//TODO 	{
//TODO 		/* change the adjusted palette entry */
//TODO 		adjusted_palette[pen] = adjusted_color;
//TODO 		adjusted_palette_dirty = 1;
//TODO 
//TODO 		/* update the pen value or mark the palette dirty */
//TODO 		switch (colormode)
//TODO 		{
//TODO 			/* 16-bit palettized: just mark it dirty for later */
//TODO 			case PALETTIZED_16BIT:
//TODO 				mark_pen_dirty(pen);
//TODO 				break;
//TODO 
//TODO 			/* 15/32-bit direct: update the Machine->pens array */
//TODO 			case DIRECT_15BIT:
//TODO 				Machine->pens[pen] = rgb_to_direct15(adjusted_color);
//TODO 				break;
//TODO 
//TODO 			case DIRECT_32BIT:
//TODO 				Machine->pens[pen] = rgb_to_direct32(adjusted_color);
//TODO 				break;
//TODO 		}
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	internal_modify_pen - change a pen along with
//TODO 	its corresponding shadow/highlight
//TODO -------------------------------------------------*/
//TODO 
//TODO static void internal_modify_pen(pen_t pen, rgb_t color, int pen_bright)
//TODO {
//TODO 	/* first modify the base pen */
//TODO 	internal_modify_single_pen(pen, color, pen_bright);
//TODO 
//TODO 	/* see if we need to handle shadow/highlight */
//TODO 	if (pen < Machine->drv->total_colors)
//TODO 	{
//TODO 		/* check for shadows */
//TODO 		if (Machine->drv->video_attributes & VIDEO_HAS_SHADOWS)
//TODO 		{
//TODO 			pen += Machine->drv->total_colors;
//TODO 			internal_modify_single_pen(pen, color, (pen_bright * shadow_factor) >> PEN_BRIGHTNESS_BITS);
//TODO 		}
//TODO 
//TODO 		/* check for highlights */
//TODO 		if (Machine->drv->video_attributes & VIDEO_HAS_HIGHLIGHTS)
//TODO 		{
//TODO 			pen += Machine->drv->total_colors;
//TODO 			internal_modify_single_pen(pen, color, (pen_bright * highlight_factor) >> PEN_BRIGHTNESS_BITS);
//TODO 		}
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	recompute_adjusted_palette - recompute the
//TODO 	entire palette after some major event
//TODO -------------------------------------------------*/
//TODO 
//TODO static void recompute_adjusted_palette(int brightness_or_gamma_changed)
//TODO {
//TODO 	int i;
//TODO 
//TODO 	/* regenerate the color correction table if needed */
//TODO 	if (brightness_or_gamma_changed)
//TODO 		for (i = 0; i < sizeof(color_correct_table); i++)
//TODO 		{
//TODO 			int value = (int)(255.0 * (global_brightness * global_brightness_adjust) * pow((double)i * (1.0 / 255.0), 1.0 / global_gamma) + 0.5);
//TODO 			color_correct_table[i] = (value < 0) ? 0 : (value > 255) ? 255 : value;
//TODO 		}
//TODO 
//TODO 	/* now update all the palette entries */
//TODO 	for (i = 0; i < Machine->drv->total_colors; i++)
//TODO 		internal_modify_pen(i, game_palette[i], pen_brightness[i]);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_reset - called after restore to
//TODO 	actually update the palette
//TODO -------------------------------------------------*/
//TODO 
//TODO static void palette_reset(void)
//TODO {
//TODO 	/* recompute everything */
//TODO 	recompute_adjusted_palette(0);
//TODO }
//TODO 
//TODO 
//TODO 

    /*-------------------------------------------------
 	palette_set_color - set a single palette
 	entry
    -------------------------------------------------*/
    public static void palette_set_color(int u32_pen, int/*UINT8*/ r, int/*UINT8*/ g, int/*UINT8*/ b) {
        throw new UnsupportedOperationException("Unsupported");
//TODO 	/* make sure we're in range */
//TODO 	if (pen >= total_colors)
//TODO 	{
//TODO 		logerror("error: palette_set_color() called with color %d, but only %d allocated.\n", pen, total_colors);
//TODO 		return;
//TODO 	}
//TODO 
//TODO 	/* set the pen value */
//TODO 	internal_modify_pen(pen, MAKE_RGB(r, g, b), pen_brightness[pen]);
    }

//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_get_color - return a single palette
//TODO 	entry
//TODO -------------------------------------------------*/
//TODO 
//TODO void palette_get_color(pen_t pen, UINT8 *r, UINT8 *g, UINT8 *b)
//TODO {
//TODO 	/* special case the black pen */
//TODO 	if (pen == get_black_pen())
//TODO 		*r = *g = *b = 0;
//TODO 
//TODO 	/* record the result from the game palette */
//TODO 	else if (pen < total_colors)
//TODO 	{
//TODO 		*r = RGB_RED(game_palette[pen]);
//TODO 		*g = RGB_GREEN(game_palette[pen]);
//TODO 		*b = RGB_BLUE(game_palette[pen]);
//TODO 	}
//TODO 	else
//TODO 		usrintf_showmessage("palette_get_color() out of range");
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_set_brightness - set the per-pen
//TODO 	brightness factor
//TODO -------------------------------------------------*/
//TODO 
//TODO void palette_set_brightness(pen_t pen, double bright)
//TODO {
//TODO 	/* compute the integral brightness value */
//TODO 	int brightval = (int)(bright * (double)(1 << PEN_BRIGHTNESS_BITS));
//TODO 	if (brightval > MAX_PEN_BRIGHTNESS)
//TODO 		brightval = MAX_PEN_BRIGHTNESS;
//TODO 
//TODO 	/* if it changed, update the array and the adjusted palette */
//TODO 	if (pen_brightness[pen] != brightval)
//TODO 	{
//TODO 		pen_brightness[pen] = brightval;
//TODO 		internal_modify_pen(pen, game_palette[pen], brightval);
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_set_shadow_factor - set the global
//TODO 	shadow brightness factor
//TODO -------------------------------------------------*/
//TODO 
//TODO void palette_set_shadow_factor(double factor)
//TODO {
//TODO 	/* compute the integral shadow factor value */
//TODO 	int factorval = (int)(factor * (double)(1 << PEN_BRIGHTNESS_BITS));
//TODO 	if (factorval > MAX_PEN_BRIGHTNESS)
//TODO 		factorval = MAX_PEN_BRIGHTNESS;
//TODO 
//TODO 	/* if it changed, update the entire palette */
//TODO 	if (shadow_factor != factorval)
//TODO 	{
//TODO 		shadow_factor = factorval;
//TODO 		recompute_adjusted_palette(0);
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_set_highlight_factor - set the global
//TODO 	highlight brightness factor
//TODO -------------------------------------------------*/
//TODO 
//TODO void palette_set_highlight_factor(double factor)
//TODO {
//TODO 	/* compute the integral highlight factor value */
//TODO 	int factorval = (int)(factor * (double)(1 << PEN_BRIGHTNESS_BITS));
//TODO 	if (factorval > MAX_PEN_BRIGHTNESS)
//TODO 		factorval = MAX_PEN_BRIGHTNESS;
//TODO 
//TODO 	/* if it changed, update the entire palette */
//TODO 	if (highlight_factor != factorval)
//TODO 	{
//TODO 		highlight_factor = factorval;
//TODO 		recompute_adjusted_palette(0);
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_set_global_gamma - set the global
//TODO 	gamma factor
//TODO -------------------------------------------------*/
//TODO 
//TODO void palette_set_global_gamma(double _gamma)
//TODO {
//TODO 	/* if the gamma changed, recompute */
//TODO 	if (global_gamma != _gamma)
//TODO 	{
//TODO 		global_gamma = _gamma;
//TODO 		recompute_adjusted_palette(1);
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_get_global_gamma - return the global
//TODO 	gamma factor
//TODO -------------------------------------------------*/
//TODO 
//TODO double palette_get_global_gamma(void)
//TODO {
//TODO 	return global_gamma;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_set_global_brightness - set the global
//TODO 	brightness factor
//TODO -------------------------------------------------*/
//TODO 
//TODO void palette_set_global_brightness(double brightness)
//TODO {
//TODO 	/* if the gamma changed, recompute */
//TODO 	if (global_brightness != brightness)
//TODO 	{
//TODO 		global_brightness = brightness;
//TODO 		recompute_adjusted_palette(1);
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_set_global_brightness_adjust - set
//TODO 	the global brightness adjustment factor
//TODO -------------------------------------------------*/
//TODO 
//TODO void palette_set_global_brightness_adjust(double adjustment)
//TODO {
//TODO 	/* if the gamma changed, recompute */
//TODO 	if (global_brightness_adjust != adjustment)
//TODO 	{
//TODO 		global_brightness_adjust = adjustment;
//TODO 		recompute_adjusted_palette(1);
//TODO 	}
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	palette_get_global_brightness - return the global
//TODO 	brightness factor
//TODO -------------------------------------------------*/
//TODO 
//TODO double palette_get_global_brightness(void)
//TODO {
//TODO 	return global_brightness;
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /*-------------------------------------------------
//TODO 	get_black_pen - use this if you need to
//TODO 	fillbitmap() the background with black
//TODO -------------------------------------------------*/
//TODO 
//TODO pen_t get_black_pen(void)
//TODO {
//TODO 	return Machine->uifont->colortable[0];
//TODO }
//TODO 
//TODO 
//TODO 
//TODO 
//TODO /******************************************************************************
//TODO 
//TODO  Commonly used palette RAM handling functions
//TODO 
//TODO ******************************************************************************/
//TODO 
//TODO READ_HANDLER( paletteram_r )
//TODO {
//TODO 	return paletteram[offset];
//TODO }
//TODO 
//TODO READ_HANDLER( paletteram_2_r )
//TODO {
//TODO 	return paletteram_2[offset];
//TODO }
//TODO 
//TODO READ16_HANDLER( paletteram16_word_r )
//TODO {
//TODO 	return paletteram16[offset];
//TODO }
//TODO 
//TODO READ16_HANDLER( paletteram16_2_word_r )
//TODO {
//TODO 	return paletteram16_2[offset];
//TODO }
//TODO 
//TODO READ32_HANDLER( paletteram32_r )
//TODO {
//TODO 	return paletteram32[offset];
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_RRRGGGBB_w )
//TODO {
//TODO 	int r,g,b;
//TODO 	int bit0,bit1,bit2;
//TODO 
//TODO 
//TODO 	paletteram[offset] = data;
//TODO 
//TODO 	/* red component */
//TODO 	bit0 = (data >> 5) & 0x01;
//TODO 	bit1 = (data >> 6) & 0x01;
//TODO 	bit2 = (data >> 7) & 0x01;
//TODO 	r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
//TODO 	/* green component */
//TODO 	bit0 = (data >> 2) & 0x01;
//TODO 	bit1 = (data >> 3) & 0x01;
//TODO 	bit2 = (data >> 4) & 0x01;
//TODO 	g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
//TODO 	/* blue component */
//TODO 	bit0 = 0;
//TODO 	bit1 = (data >> 0) & 0x01;
//TODO 	bit2 = (data >> 1) & 0x01;
//TODO 	b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
//TODO 
//TODO 	palette_set_color(offset,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_BBBGGGRR_w )
//TODO {
//TODO 	int r,g,b;
//TODO 	int bit0,bit1,bit2;
//TODO 
//TODO 	paletteram[offset] = data;
//TODO 
//TODO 	/* blue component */
//TODO 	bit0 = (data >> 5) & 0x01;
//TODO 	bit1 = (data >> 6) & 0x01;
//TODO 	bit2 = (data >> 7) & 0x01;
//TODO 	b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
//TODO 	/* green component */
//TODO 	bit0 = (data >> 2) & 0x01;
//TODO 	bit1 = (data >> 3) & 0x01;
//TODO 	bit2 = (data >> 4) & 0x01;
//TODO 	g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
//TODO 	/* blue component */
//TODO 	bit0 = (data >> 0) & 0x01;
//TODO 	bit1 = (data >> 1) & 0x01;
//TODO 	r = 0x55 * bit0 + 0xaa * bit1;
//TODO 
//TODO 	palette_set_color(offset,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_BBGGGRRR_w )
//TODO {
//TODO 	int r,g,b;
//TODO 	int bit0,bit1,bit2;
//TODO 
//TODO 
//TODO 	paletteram[offset] = data;
//TODO 
//TODO 	/* red component */
//TODO 	bit0 = (data >> 0) & 0x01;
//TODO 	bit1 = (data >> 1) & 0x01;
//TODO 	bit2 = (data >> 2) & 0x01;
//TODO 	r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
//TODO 	/* green component */
//TODO 	bit0 = (data >> 3) & 0x01;
//TODO 	bit1 = (data >> 4) & 0x01;
//TODO 	bit2 = (data >> 5) & 0x01;
//TODO 	g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
//TODO 	/* blue component */
//TODO 	bit0 = 0;
//TODO 	bit1 = (data >> 6) & 0x01;
//TODO 	bit2 = (data >> 7) & 0x01;
//TODO 	b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
//TODO 
//TODO 	palette_set_color(offset,r,g,b);
//TODO }
//TODO 
//TODO 
//TODO WRITE_HANDLER( paletteram_IIBBGGRR_w )
//TODO {
//TODO 	int r,g,b,i;
//TODO 
//TODO 
//TODO 	paletteram[offset] = data;
//TODO 
//TODO 	i = (data >> 6) & 0x03;
//TODO 	/* red component */
//TODO 	r = (data << 2) & 0x0c;
//TODO 	if (r) r |= i;
//TODO 	r *= 0x11;
//TODO 	/* green component */
//TODO 	g = (data >> 0) & 0x0c;
//TODO 	if (g) g |= i;
//TODO 	g *= 0x11;
//TODO 	/* blue component */
//TODO 	b = (data >> 2) & 0x0c;
//TODO 	if (b) b |= i;
//TODO 	b *= 0x11;
//TODO 
//TODO 	palette_set_color(offset,r,g,b);
//TODO }
//TODO 
//TODO 
//TODO WRITE_HANDLER( paletteram_BBGGRRII_w )
//TODO {
//TODO 	int r,g,b,i;
//TODO 
//TODO 
//TODO 	paletteram[offset] = data;
//TODO 
//TODO 	i = (data >> 0) & 0x03;
//TODO 	/* red component */
//TODO 	r = (((data >> 0) & 0x0c) | i) * 0x11;
//TODO 	/* green component */
//TODO 	g = (((data >> 2) & 0x0c) | i) * 0x11;
//TODO 	/* blue component */
//TODO 	b = (((data >> 4) & 0x0c) | i) * 0x11;
//TODO 
//TODO 	palette_set_color(offset,r,g,b);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_xxxxBBBBGGGGRRRR(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >> 0) & 0x0f;
//TODO 	g = (data >> 4) & 0x0f;
//TODO 	b = (data >> 8) & 0x0f;
//TODO 
//TODO 	r = (r << 4) | r;
//TODO 	g = (g << 4) | g;
//TODO 	b = (b << 4) | b;
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxBBBBGGGGRRRR_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxBBBBGGGGRRRR(offset / 2,paletteram[offset & ~1] | (paletteram[offset | 1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxBBBBGGGGRRRR_swap_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxBBBBGGGGRRRR(offset / 2,paletteram[offset | 1] | (paletteram[offset & ~1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxBBBBGGGGRRRR_split1_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxBBBBGGGGRRRR(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxBBBBGGGGRRRR_split2_w )
//TODO {
//TODO 	paletteram_2[offset] = data;
//TODO 	changecolor_xxxxBBBBGGGGRRRR(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_xxxxBBBBGGGGRRRR_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_xxxxBBBBGGGGRRRR(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_xxxxBBBBRRRRGGGG(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >> 4) & 0x0f;
//TODO 	g = (data >> 0) & 0x0f;
//TODO 	b = (data >> 8) & 0x0f;
//TODO 
//TODO 	r = (r << 4) | r;
//TODO 	g = (g << 4) | g;
//TODO 	b = (b << 4) | b;
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxBBBBRRRRGGGG_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxBBBBRRRRGGGG(offset / 2,paletteram[offset & ~1] | (paletteram[offset | 1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxBBBBRRRRGGGG_swap_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxBBBBRRRRGGGG(offset / 2,paletteram[offset | 1] | (paletteram[offset & ~1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxBBBBRRRRGGGG_split1_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxBBBBRRRRGGGG(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxBBBBRRRRGGGG_split2_w )
//TODO {
//TODO 	paletteram_2[offset] = data;
//TODO 	changecolor_xxxxBBBBRRRRGGGG(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_xxxxBBBBRRRRGGGG_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_xxxxBBBBRRRRGGGG(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_xxxxRRRRBBBBGGGG(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >> 8) & 0x0f;
//TODO 	g = (data >> 0) & 0x0f;
//TODO 	b = (data >> 4) & 0x0f;
//TODO 
//TODO 	r = (r << 4) | r;
//TODO 	g = (g << 4) | g;
//TODO 	b = (b << 4) | b;
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxRRRRBBBBGGGG_split1_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxRRRRBBBBGGGG(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxRRRRBBBBGGGG_split2_w )
//TODO {
//TODO 	paletteram_2[offset] = data;
//TODO 	changecolor_xxxxRRRRBBBBGGGG(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_xxxxRRRRGGGGBBBB(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >> 8) & 0x0f;
//TODO 	g = (data >> 4) & 0x0f;
//TODO 	b = (data >> 0) & 0x0f;
//TODO 
//TODO 	r = (r << 4) | r;
//TODO 	g = (g << 4) | g;
//TODO 	b = (b << 4) | b;
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxRRRRGGGGBBBB_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxRRRRGGGGBBBB(offset / 2,paletteram[offset & ~1] | (paletteram[offset | 1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xxxxRRRRGGGGBBBB_swap_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xxxxRRRRGGGGBBBB(offset / 2,paletteram[offset | 1] | (paletteram[offset & ~1] << 8));
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_xxxxRRRRGGGGBBBB_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_xxxxRRRRGGGGBBBB(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_RRRRGGGGBBBBxxxx(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >> 12) & 0x0f;
//TODO 	g = (data >>  8) & 0x0f;
//TODO 	b = (data >>  4) & 0x0f;
//TODO 
//TODO 	r = (r << 4) | r;
//TODO 	g = (g << 4) | g;
//TODO 	b = (b << 4) | b;
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_RRRRGGGGBBBBxxxx_swap_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_RRRRGGGGBBBBxxxx(offset / 2,paletteram[offset | 1] | (paletteram[offset & ~1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_RRRRGGGGBBBBxxxx_split1_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_RRRRGGGGBBBBxxxx(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_RRRRGGGGBBBBxxxx_split2_w )
//TODO {
//TODO 	paletteram_2[offset] = data;
//TODO 	changecolor_RRRRGGGGBBBBxxxx(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_RRRRGGGGBBBBxxxx_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_RRRRGGGGBBBBxxxx(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_BBBBGGGGRRRRxxxx(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >>  4) & 0x0f;
//TODO 	g = (data >>  8) & 0x0f;
//TODO 	b = (data >> 12) & 0x0f;
//TODO 
//TODO 	r = (r << 4) | r;
//TODO 	g = (g << 4) | g;
//TODO 	b = (b << 4) | b;
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_BBBBGGGGRRRRxxxx_swap_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_BBBBGGGGRRRRxxxx(offset / 2,paletteram[offset | 1] | (paletteram[offset & ~1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_BBBBGGGGRRRRxxxx_split1_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_BBBBGGGGRRRRxxxx(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_BBBBGGGGRRRRxxxx_split2_w )
//TODO {
//TODO 	paletteram_2[offset] = data;
//TODO 	changecolor_BBBBGGGGRRRRxxxx(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_BBBBGGGGRRRRxxxx_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_BBBBGGGGRRRRxxxx(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_xBBBBBGGGGGRRRRR(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >>  0) & 0x1f;
//TODO 	g = (data >>  5) & 0x1f;
//TODO 	b = (data >> 10) & 0x1f;
//TODO 
//TODO 	r = (r << 3) | (r >> 2);
//TODO 	g = (g << 3) | (g >> 2);
//TODO 	b = (b << 3) | (b >> 2);
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xBBBBBGGGGGRRRRR_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xBBBBBGGGGGRRRRR(offset / 2,paletteram[offset & ~1] | (paletteram[offset | 1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xBBBBBGGGGGRRRRR_swap_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xBBBBBGGGGGRRRRR(offset / 2,paletteram[offset | 1] | (paletteram[offset & ~1] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xBBBBBGGGGGRRRRR_split1_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xBBBBBGGGGGRRRRR(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xBBBBBGGGGGRRRRR_split2_w )
//TODO {
//TODO 	paletteram_2[offset] = data;
//TODO 	changecolor_xBBBBBGGGGGRRRRR(offset,paletteram[offset] | (paletteram_2[offset] << 8));
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_xBBBBBGGGGGRRRRR_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_xBBBBBGGGGGRRRRR(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_xRRRRRGGGGGBBBBB(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >> 10) & 0x1f;
//TODO 	g = (data >>  5) & 0x1f;
//TODO 	b = (data >>  0) & 0x1f;
//TODO 
//TODO 	r = (r << 3) | (r >> 2);
//TODO 	g = (g << 3) | (g >> 2);
//TODO 	b = (b << 3) | (b >> 2);
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_xRRRRRGGGGGBBBBB_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_xRRRRRGGGGGBBBBB(offset / 2,paletteram[offset & ~1] | (paletteram[offset | 1] << 8));
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_xRRRRRGGGGGBBBBB_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_xRRRRRGGGGGBBBBB(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_xGGGGGRRRRRBBBBB(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >>  5) & 0x1f;
//TODO 	g = (data >> 10) & 0x1f;
//TODO 	b = (data >>  0) & 0x1f;
//TODO 
//TODO 	r = (r << 3) | (r >> 2);
//TODO 	g = (g << 3) | (g >> 2);
//TODO 	b = (b << 3) | (b >> 2);
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_xGGGGGRRRRRBBBBB_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_xGGGGGRRRRRBBBBB(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_xGGGGGBBBBBRRRRR(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >>  0) & 0x1f;
//TODO 	g = (data >> 10) & 0x1f;
//TODO 	b = (data >>  5) & 0x1f;
//TODO 
//TODO 	r = (r << 3) | (r >> 2);
//TODO 	g = (g << 3) | (g >> 2);
//TODO 	b = (b << 3) | (b >> 2);
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_xGGGGGBBBBBRRRRR_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_xGGGGGBBBBBRRRRR(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_RRRRRGGGGGBBBBBx(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 
//TODO 	r = (data >> 11) & 0x1f;
//TODO 	g = (data >>  6) & 0x1f;
//TODO 	b = (data >>  1) & 0x1f;
//TODO 
//TODO 	r = (r << 3) | (r >> 2);
//TODO 	g = (g << 3) | (g >> 2);
//TODO 	b = (b << 3) | (b >> 2);
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE_HANDLER( paletteram_RRRRRGGGGGBBBBBx_w )
//TODO {
//TODO 	paletteram[offset] = data;
//TODO 	changecolor_RRRRRGGGGGBBBBBx(offset / 2,paletteram[offset & ~1] | (paletteram[offset | 1] << 8));
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_RRRRRGGGGGBBBBBx_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_RRRRRGGGGGBBBBBx(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_IIIIRRRRGGGGBBBB(pen_t color,int data)
//TODO {
//TODO 	int i,r,g,b;
//TODO 
//TODO 
//TODO 	static const int ztable[16] =
//TODO 		{ 0x0, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11 };
//TODO 
//TODO 	i = ztable[(data >> 12) & 15];
//TODO 	r = ((data >> 8) & 15) * i;
//TODO 	g = ((data >> 4) & 15) * i;
//TODO 	b = ((data >> 0) & 15) * i;
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO 
//TODO 	if (!(Machine->drv->video_attributes & VIDEO_NEEDS_6BITS_PER_GUN))
//TODO 		usrintf_showmessage("driver should use VIDEO_NEEDS_6BITS_PER_GUN flag");
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_IIIIRRRRGGGGBBBB_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_IIIIRRRRGGGGBBBB(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_RRRRGGGGBBBBIIII(pen_t color,int data)
//TODO {
//TODO 	int i,r,g,b;
//TODO 
//TODO 
//TODO 	static const int ztable[16] =
//TODO 		{ 0x0, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11 };
//TODO 
//TODO 	i = ztable[(data >> 0) & 15];
//TODO 	r = ((data >> 12) & 15) * i;
//TODO 	g = ((data >>  8) & 15) * i;
//TODO 	b = ((data >>  4) & 15) * i;
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO 
//TODO 	if (!(Machine->drv->video_attributes & VIDEO_NEEDS_6BITS_PER_GUN))
//TODO 		usrintf_showmessage("driver should use VIDEO_NEEDS_6BITS_PER_GUN flag");
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_RRRRGGGGBBBBIIII_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_RRRRGGGGBBBBIIII(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO WRITE16_HANDLER( paletteram16_xrgb_word_w )
//TODO {
//TODO 	int r, g, b;
//TODO 	data16_t data0, data1;
//TODO 
//TODO 	COMBINE_DATA(paletteram16 + offset);
//TODO 
//TODO 	offset &= ~1;
//TODO 
//TODO 	data0 = paletteram16[offset];
//TODO 	data1 = paletteram16[offset + 1];
//TODO 
//TODO 	r = data0 & 0xff;
//TODO 	g = data1 >> 8;
//TODO 	b = data1 & 0xff;
//TODO 
//TODO 	palette_set_color(offset>>1, r, g, b);
//TODO 
//TODO 	if (!(Machine->drv->video_attributes & VIDEO_NEEDS_6BITS_PER_GUN))
//TODO 		usrintf_showmessage("driver should use VIDEO_NEEDS_6BITS_PER_GUN flag");
//TODO }
//TODO 
//TODO 
//TODO INLINE void changecolor_RRRRGGGGBBBBRGBx(pen_t color,int data)
//TODO {
//TODO 	int r,g,b;
//TODO 
//TODO 	r = ((data >> 11) & 0x1e) | ((data>>3) & 0x01);
//TODO 	g = ((data >>  7) & 0x1e) | ((data>>2) & 0x01);
//TODO 	b = ((data >>  3) & 0x1e) | ((data>>1) & 0x01);
//TODO 	r = (r<<3) | (r>>2);
//TODO 	g = (g<<3) | (g>>2);
//TODO 	b = (b<<3) | (b>>2);
//TODO 
//TODO 	palette_set_color(color,r,g,b);
//TODO }
//TODO 
//TODO WRITE16_HANDLER( paletteram16_RRRRGGGGBBBBRGBx_word_w )
//TODO {
//TODO 	COMBINE_DATA(&paletteram16[offset]);
//TODO 	changecolor_RRRRGGGGBBBBRGBx(offset,paletteram16[offset]);
//TODO }
//TODO 
//TODO 
//TODO 
//TODO /******************************************************************************
//TODO 
//TODO  Commonly used color PROM handling functions
//TODO 
//TODO ******************************************************************************/
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Standard black and white palette.
//TODO 	Color 0 is pure black, color 1 is pure white.
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO PALETTE_INIT( black_and_white )
//TODO {
//TODO 	palette_set_color(0,0x00,0x00,0x00); /* black */
//TODO 	palette_set_color(1,0xff,0xff,0xff); /* white */
//TODO }
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO   This assumes the commonly used resistor values:
//TODO 
//TODO   bit 3 -- 220 ohm resistor  -- RED/GREEN/BLUE
//TODO         -- 470 ohm resistor  -- RED/GREEN/BLUE
//TODO         -- 1  kohm resistor  -- RED/GREEN/BLUE
//TODO   bit 0 -- 2.2kohm resistor  -- RED/GREEN/BLUE
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO PALETTE_INIT( RRRR_GGGG_BBBB )
//TODO {
//TODO 	int i;
//TODO 
//TODO 
//TODO 	for (i = 0;i < Machine->drv->total_colors;i++)
//TODO 	{
//TODO 		int bit0,bit1,bit2,bit3,r,g,b;
//TODO 
//TODO 		/* red component */
//TODO 		bit0 = (color_prom[i] >> 0) & 0x01;
//TODO 		bit1 = (color_prom[i] >> 1) & 0x01;
//TODO 		bit2 = (color_prom[i] >> 2) & 0x01;
//TODO 		bit3 = (color_prom[i] >> 3) & 0x01;
//TODO 		r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
//TODO 		/* green component */
//TODO 		bit0 = (color_prom[i + Machine->drv->total_colors] >> 0) & 0x01;
//TODO 		bit1 = (color_prom[i + Machine->drv->total_colors] >> 1) & 0x01;
//TODO 		bit2 = (color_prom[i + Machine->drv->total_colors] >> 2) & 0x01;
//TODO 		bit3 = (color_prom[i + Machine->drv->total_colors] >> 3) & 0x01;
//TODO 		g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
//TODO 		/* blue component */
//TODO 		bit0 = (color_prom[i + 2*Machine->drv->total_colors] >> 0) & 0x01;
//TODO 		bit1 = (color_prom[i + 2*Machine->drv->total_colors] >> 1) & 0x01;
//TODO 		bit2 = (color_prom[i + 2*Machine->drv->total_colors] >> 2) & 0x01;
//TODO 		bit3 = (color_prom[i + 2*Machine->drv->total_colors] >> 3) & 0x01;
//TODO 		b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
//TODO 
//TODO 		palette_set_color(i,r,g,b);
//TODO 	}
//TODO }
//TODO 
}
