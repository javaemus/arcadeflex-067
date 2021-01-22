package gr.codebb.arcadeflex.common;

public class Util {

    /*
     *  Convert command-line parameters
     */
    public static int argc;
    public static String[] argv;

    public static void ConvertArguments(String mainClass, String[] arguments) {
        argc = arguments.length + 1;
        argv = new String[argc];
        argv[0] = mainClass;
        for (int i = 1; i < argc; i++) {
            argv[i] = arguments[i - 1];
        }
    }
}
