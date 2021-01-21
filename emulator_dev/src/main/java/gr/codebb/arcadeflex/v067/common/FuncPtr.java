package gr.codebb.arcadeflex.v067.common;

public class FuncPtr {

    /**
     * common
     */
    public static abstract interface ReadHandlerPtr {

        public abstract int handler(int offset);
    }

    public static abstract interface WriteHandlerPtr {

        public abstract void handler(int offset, int data);
    }
}
