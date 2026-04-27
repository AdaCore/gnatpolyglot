package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Class holding a mutable float value. Represents mutable float function parameters */
public class FloatRef {

    private ByteBuffer value;

    public FloatRef() {
        this.value = ByteBuffer.allocateDirect(Float.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putFloat(0, 0);
    }

    public FloatRef(float value) {
        this.value = ByteBuffer.allocateDirect(Float.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putFloat(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public FloatRef(ByteBuffer buffer) {
        this.value = buffer;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
    }

    public float getValue() {
        return value.getFloat(0);
    }

    public void setValue(float value) {
        this.value.putFloat(0, value);
    }

    @Override
    public String toString() {
        return Float.toString(value.getFloat(0));
    }
}
