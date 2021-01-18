package gr.codebb.arcadeflex_convertor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 *
 * @author george
 */
public class fileutil {
    public static FileInputStream openReadFile(String s)
    {
        try {
            return new FileInputStream(s);
        } catch (FileNotFoundException ex) {
             System.err.println((new StringBuilder()).append("File '").append(s).append("' was not found.").toString());
        }    
        return null;
    }
    public static int getFileSize(FileInputStream fileinputstream)
    {
        try {
            return fileinputstream.available();
        } catch (IOException ex) {
            System.err.println("File size cannot be determined.");
        }       
        return -1;
    }
    public static int readFile(FileInputStream fileinputstream, byte abyte0[])
    {
        try {
            return fileinputstream.read(abyte0);
        } catch (IOException ex) {
            System.err.println("File cannot be read.");
        }      
        return -1;
    }
    public static boolean closeReadFile(FileInputStream fileinputstream)
    {
        try {
            fileinputstream.close();
            return true;
        } catch (IOException ex) {
           
            return false;
        }
        
    }
    public static FileOutputStream openWriteFile(String s)
    {
        try {
            return new FileOutputStream(s);
        } catch (FileNotFoundException ex) {
            System.err.println((new StringBuilder()).append("File '").append(s).append("' was not found.").toString());
        }       
        return null;
    }
    public static boolean writeFile(FileOutputStream fileoutputstream, byte abyte0[], int i)
    {
        try {
            fileoutputstream.write(abyte0, 0, i);
            return true;
        } catch (IOException ex) {
            System.err.println("File cannot be written.");
            return false;
        }
              
    }
    public static boolean closeWriteFile(FileOutputStream fileoutputstream)
    {
        try {
            fileoutputstream.close();
            return true;
        } catch (IOException ex) {
            System.err.println("File cannot be closed.");
            return false;
        }     
    }    
}
