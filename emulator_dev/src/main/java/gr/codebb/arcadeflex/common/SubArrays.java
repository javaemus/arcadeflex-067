package gr.codebb.arcadeflex.common;

public class SubArrays {

    public static class UIntSubArray {

        public int[] buffer;
        public int offset;

        public int read(int index) {
            return buffer[index + offset];
        }
    }
}
