/***************************************************************************

	Atari Tetris hardware

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class atetris
{
	
	
	static struct tilemap *tilemap;
	
	
	/*************************************
	 *
	 *	Tilemap callback
	 *
	 *************************************/
	
	static void get_tile_info(int tile_index)
	{
		int code = videoram.read(tile_index * 2)| ((videoram.read(tile_index * 2 + 1)& 7) << 8);
		int color = (videoram.read(tile_index * 2 + 1)& 0xf0) >> 4;
	
		SET_TILE_INFO(0, code, color, 0);
	}
	
	
	
	/*************************************
	 *
	 *	Video RAM write
	 *
	 *************************************/
	
	public static WriteHandlerPtr atetris_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		videoram.write(offset,data);
		tilemap_mark_tile_dirty(tilemap, offset / 2);
	} };
	
	
	
	/*************************************
	 *
	 *	Video system start
	 *
	 *************************************/
	
	VIDEO_START( atetris )
	{
		tilemap = tilemap_create(get_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8,8, 64,32);
		if (tilemap == 0)
			return 1;
		return 0;
	}
	
	
	
	/*************************************
	 *
	 *	Main refresh
	 *
	 *************************************/
	
	VIDEO_UPDATE( atetris )
	{
		tilemap_draw(bitmap, cliprect, tilemap, 0,0);
	}
}
