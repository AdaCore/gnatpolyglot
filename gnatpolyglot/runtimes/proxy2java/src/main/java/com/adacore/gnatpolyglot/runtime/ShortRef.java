package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Class holding a mutable short value. Represents mutable short function parameters */
public class ShortRef {

    private ByteBuffer value;

    public ShortRef() {
        this.value = ByteBuffer.allocateDirect(Short.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putShort(0, (short) 0);
    }

    public ShortRef(short value) {
        this.value = ByteBuffer.allocateDirect(Short.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putShort(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public ShortRef(ByteBuffer buffer) {
        this.value = buffer;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
    }

    public short getValue() {
        return value.getShort(0);
    }

    public void setValue(short value) {
        this.value.putDouble(0, value);
    }

    @Override
    public String toString() {
        return Short.toString(value.getShort(0));
    }
}
