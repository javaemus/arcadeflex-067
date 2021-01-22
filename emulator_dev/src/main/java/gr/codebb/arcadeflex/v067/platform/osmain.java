package gr.codebb.arcadeflex.v067.platform;

import static gr.codebb.arcadeflex.v067.platform.conf.*;

//dummy implementation
public class osmain {
    public static int main (int argc, String[] argv)
    {
        int game_index;
        int res = 0;
        game_index = cli_frontend_init(argc, argv);
        
        return res;
    }
}
