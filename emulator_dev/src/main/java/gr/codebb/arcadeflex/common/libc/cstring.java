package gr.codebb.arcadeflex.common.libc;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.SubArrays.*;

/**
 *
 * @author shadow
 */
public class cstring {
	
	/**
     * Locate last occurrence of character in string Returns a pointer to the last occurrence of character in the C string str.
     * @param str
     * @param ch
     * @return 
     */
    public static String strrchr(String str,char ch)
    {
        int found = str.lastIndexOf(ch);
        if(found==-1)//not found
        {
            return null;
        }
        else
        {
            return Integer.toString(found);//return in String
        }
    }
    
    public static String strcat(String X, String Y)
    {
        return (X + Y);
    }
 
    // Function to implement strstr() function
    public static int strstr(String X, String Y)
    {
            // if X is null or if X's length is less than that of Y's
            if (X == null || Y.length() > X.length()) {
                    return -1;
            }

            // if Y is null or is empty
            if (Y == null || Y.length() == 0) {
                    return 0;
            }

            for (int i = 0; i <= X.length() - Y.length(); i++)
            {
                    int j;
                    for (j = 0; j < Y.length(); j++) {
                            if (Y.charAt(j) != X.charAt(i + j)) {
                                    break;
                            }
                    }

                    if (j == Y.length()) {
                            return i;
                    }
            }

            return -1;
    }

    /**
     * Get string length
     *
     * @param str
     * @return
     */
    public static int strlen(String str) {
        return str.length();
    }

    /**
     * memset
     *
     * @param dst
     * @param value
     * @param size
     */
    public static void memset(char[] dst, int value, int size) {
        for (int mem = 0; mem < size; mem++) {
            dst[mem] = (char) value;
        }
    }

    public static void memset(short[] dst, int value, int size) {
        for (int mem = 0; mem < size; mem++) {
            dst[mem] = (short) value;
        }
    }
    
/*TODO*///    public static void memset(xShortPtr dst, int value, int size) {
/*TODO*///        for (int mem = 0; mem < size; mem++) {
/*TODO*///            dst.write(mem, (char) value);
/*TODO*///        }
/*TODO*///    }

    public static void memset(int[] dst, int value, int size) {
        for (int mem = 0; mem < size; mem++) {
            dst[mem] = value;
        }
    }

    public static void memset(UBytePtr ptr, int value, int length) {
        for (int i = 0; i < length; i++) {
            ptr.write(i, value);
        }
    }

    public static void memset(UBytePtr ptr, int offset, int value, int length) {
        for (int i = 0; i < length; i++) {
            ptr.write(i + offset, value);
        }
    }

    public static void memset(ShortPtr buf, int value, int size) {
        for (int i = 0; i < size; i++) {
            buf.write(i, (short) value);
        }
    }
    public static void memset(UShortPtr buf, int value, int size) {
        for (int i = 0; i < size; i++) {
            buf.write(i, (char) value);
        }
    }

    /**
     * memcpy
     */
    public static void memcpy(UBytePtr dst, int dstoffs, UBytePtr src, int srcoffs, int size) {
        for (int i = 0; i < Math.min(size, src.memory.length); i++) {
            dst.write(i + dstoffs, src.read(i + srcoffs));
        }
    }

    public static void memcpy(UBytePtr dst, int dstoffs, UBytePtr src, int size) {
        for (int i = 0; i < Math.min(size, src.memory.length); i++) {
            dst.write(i + dstoffs, src.read(i));
        }
    }

    public static void memcpy(UBytePtr dst, UBytePtr src, int srcoffs, int size) {
        for (int i = 0; i < Math.min(size, src.memory.length); i++) {
            dst.write(i, src.read(i + srcoffs));
        }
    }

    public static void memcpy(UBytePtr dst, UBytePtr src, int size) {
        /*
        System.out.println("DST: "+dst.memory.length);
        System.out.println("SRC: "+src.memory.length);
        System.out.println("Size: "+size);
        System.out.println("MIN: "+Math.min(size, src.memory.length));
        */
        for (int i = 0; i < Math.min(size, src.memory.length); i++) {
            //dst.write(i, src.read(i));
            dst.memory[i] = src.memory[i];
        }
    }
    
    public static void memcpy(UShortPtr dst, UShortPtr src, int size) {
        for (int i = 0; i < Math.min(size, src.memory.length); i++) {
            dst.write(i, src.read(i));
        }
    }

    public static void memcpy(UBytePtr dst, int dstoffs, int[] src, int size) {
        for (int i = 0; i < Math.min(size, src.length); i++) {
            dst.write(i + dstoffs, src[i]);
        }
    }

    public static void memcpy(UBytePtr dst, int dstoffs, char[] src, int size) {
        for (int i = 0; i < Math.min(size, src.length); i++) {
            dst.write(i + dstoffs, src[i]);
        }
    }

    public static void memcpy(UBytePtr dst, char[] src, int size) {
        for (int i = 0; i < Math.min(size, src.length); i++) {
            dst.write(i, src[i]);
        }
    }

    public static void memcpy(char[] dst, char[] src, int size) {
        for (int i = 0; i < Math.min(size, src.length); i++) {
            dst[i] = src[i];
        }
    }

    public static void memcpy(char[] dst, int dstofs, char[] src, int srcofs, int size) {
        for (int mem = 0; mem < size; mem++) {
            dst[dstofs + mem] = src[srcofs + mem];

        }
    }

    /**
     * memcmp
     */
    public static int memcmp(char[] dst, char[] src, int size) {
        for (int i = 0; i < size; i++) {
            if (dst[i] != src[i]) {
                return -1;

            }
        }
        return 0;
    }

    public static int memcmp(UBytePtr dst, char[] src, int size) {
        for (int i = 0; i < size; i++) {
            if (dst.read(i) != src[i]) {
                return -1;

            }
        }
        return 0;
    }
    
    public static int memcmp(char[] dist, int dstoffs, String src, int size) {
        char[] srcc = src.toCharArray();
        for (int i = 0; i < size; i++) {
            if (dist[(dstoffs + i)] != srcc[i]) {
                return -1;

            }
        }
        return 0;
    }

    /**
     * STRCMP function
     */
    public static int strcmp(String str1, String str2) {
        return str1.compareTo(str2);
    }
    
    public static boolean strncmp(char[] s1, String s2, int n) {
        if (n > s2.length()) {
            n = s2.length();
        }
        String s1s = new String(s1).substring(0, n);//not proper but should work that way
        int compare = s1s.compareTo(s2.substring(0, n));
        if (compare == 0) {
            return false;//should be true , but for matching c format return false
        }
        return true;
    }

    /**
     * Compares string1 and string2 without sensitivity to case
     *
     * @param string1
     * @param string2
     * @return a negative integer, zero, or a positive integer as the specified
     * String is greater than, equal to, or less than this String, ignoring case
     * considerations.
     */
    public static int stricmp(String str1, String str2) {
        return str1.compareToIgnoreCase(str2);
    }
    
    /**
     * Locate first occurrence of character in string Returns a pointer to the first occurrence of character in the C string str.
     * @param str
     * @param ch
     * @return 
     */
    public static String strchr(String str,char ch)
    {
        int found = str.indexOf(ch);
        if(found==-1)//not found
        {
            return null;
        }
        else
        {
            return Integer.toString(found);//return in String
        }
    }
}
