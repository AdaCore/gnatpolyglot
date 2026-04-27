package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Class holding a mutable int value. Represents mutable integer function parameters */
public class IntegerRef {

    private ByteBuffer value;

    public IntegerRef() {
        this.value = ByteBuffer.allocateDirect(Integer.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putInt(0, 0);
    }

    public IntegerRef(int value) {
        this.value = ByteBuffer.allocateDirect(Integer.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putInt(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public IntegerRef(ByteBuffer buffer) {
        this.value = buffer;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
    }

    public int getValue() {
        return value.getInt(0);
    }

    public void setValue(int value) {
        this.value.putInt(0, value);
    }

    @Override
    public String toString() {
        return Integer.toString(value.getInt(0));
    }
}
