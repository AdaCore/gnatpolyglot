package com.adacore.gnatpolyglot.runtime;

public abstract class PolyglotData {

    /** Owner of the memory. */
    public static enum Owner {
        UNKNOWN,
        /**
         * The memory is owned by the user. Attempting to free it should call the associated native
         * function, and any further use of the data may result in a use-after-free.
         *
         * <p> The ownership of used-owned memory can be changed.
         */
        USER,
        /**
         * The memory is owned by the library. Attempting to free it should result in a no-op, as it
         * may have been escaped in the bound librayry.
         *
         * <p> The ownership of library-owned memory can be changed.
         */
        LIBRARY,
        /**
         * Static memory, Usually refers to memory used by fields, global variables and such. Static
         * memory should not be freed.
         *
         * <p>The ownership of static memory cannot be changed, and any attempt at doing so should
         * be a no-op.
         */
        STATIC;
    }

    /** The owner of the memory. Determines whether the memory can be freed or not. */
    private Owner owner = Owner.STATIC;

    public PolyglotData(Owner owner) {
        this.owner = owner;
    }

    public PolyglotData() {
        this.owner = Owner.STATIC;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        if (this.owner != Owner.STATIC) {
            this.owner = owner;
        }
    }

    /** Representation of internal pointers in GNATpolyglot bindings. */
    public static class Pointer extends PolyglotData {
        /** The address of the allocated data (i.e. a `void *`) */
        long addr;

        public Pointer(long addr) {
            this.addr = addr;
        }

        public Pointer(long addr, Owner owner) {
            super(owner);
            this.addr = addr;
        }

        @Override
        public long getAddress() {
            return addr;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj instanceof Pointer ptr)
                return this.addr == ptr.addr;
            return false;
        }
    }

    /** Return the address of the allocated data. */
    public abstract long getAddress();
}

