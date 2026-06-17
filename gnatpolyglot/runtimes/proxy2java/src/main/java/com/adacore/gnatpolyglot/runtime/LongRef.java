package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable long value. Represents mutable long function parameters */
public class LongRef extends ScalarRef {

    public LongRef() {
        super(Long.BYTES);
        this.value.putLong(0, 0);
    }

    public LongRef(long value) {
        super(Long.BYTES);
        this.value.putLong(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public LongRef(ByteBuffer buffer) {
        super(buffer);
    }

    public long getValue() {
        return value.getLong(0);
    }

    public void setValue(long value) {
        this.value.putLong(0, value);
    }

    @Override
    public String toString() {
        return Long.toString(value.getLong(0));
    }
}
