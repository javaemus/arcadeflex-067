package gr.codebb.arcadeflex.common;

import java.io.PrintStream;

public class libc {

    public static void fprintf(PrintStream prStream, String str, Object... arguments) {
        String print = String.format(str, arguments);
        try {
            prStream.print(print);            
        } catch (Exception e) {
        }
    }
    
    public static int strlen(String s) {
        
        int _res=0;
        
        if (s!=null)
            _res = s.length();
        
        return _res;
    }
}
