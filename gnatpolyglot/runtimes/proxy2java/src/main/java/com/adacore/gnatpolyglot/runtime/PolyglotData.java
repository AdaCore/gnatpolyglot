package com.adacore.gnatpolyglot.runtime;

public abstract class PolyglotData {
    /** Representation of internal pointers in GNATpolyglot bindings. */
    public static class Pointer extends PolyglotData {
        /** The address of the allocated data (i.e. a `void *`) */
        long addr;

        public Pointer(long addr) {
            this.addr = addr;
        }

        @Override
        public long getAddress() {
            return addr;
        }
    }

    /** Return the address of the allocated data. */
    public abstract long getAddress();
}

