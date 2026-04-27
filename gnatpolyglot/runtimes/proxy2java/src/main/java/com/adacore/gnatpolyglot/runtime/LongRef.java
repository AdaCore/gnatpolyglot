package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Class holding a mutable long value. Represents mutable long function parameters */
public class LongRef {

    private ByteBuffer value;

    public LongRef() {
        this.value = ByteBuffer.allocateDirect(Long.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putLong(0, 0);
    }

    public LongRef(long value) {
        this.value = ByteBuffer.allocateDirect(Long.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putLong(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public LongRef(ByteBuffer buffer) {
        this.value = buffer;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
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
