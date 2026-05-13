package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Class holding a mutable boolean value. Represents mutable boolean function parameters */
public class BooleanRef {

    private ByteBuffer value;

    public BooleanRef() {
        this.value = ByteBuffer.allocateDirect(Byte.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.put(0, (byte)0);
    }

    public BooleanRef(boolean value) {
        this.value = ByteBuffer.allocateDirect(Byte.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.put(0, (byte) (value ? 1 : 0));
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public BooleanRef(ByteBuffer buffer) {
        this.value = buffer;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
    }

    public boolean getValue() {
        return value.get(0) != 0;
    }

    public void setValue(boolean value) {
        this.value.put(0, (byte) (value ? 1 : 0));
    }

    @Override
    public String toString() {
        return Boolean.toString(getValue());
    }
}
