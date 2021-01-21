package gr.codebb.arcadeflex.common;

public class PtrLib {

    /**
     * ***********************************
     * Unsigned Byte Pointer Emulation
     */
    public static class UBytePtr {

        public int bsize = 1;
        public char[] memory;
        public int offset;

        public UBytePtr() {
        }

        public char read(int index) {
            return (char) (memory[offset + index] & 0xFF);
        }

        public void write(int index, int value) {
            memory[offset + index] = (char) (value & 0xFF);
        }

    }
}
