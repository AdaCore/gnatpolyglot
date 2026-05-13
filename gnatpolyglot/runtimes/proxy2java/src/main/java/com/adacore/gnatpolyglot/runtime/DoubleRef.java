package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Class holding a mutable double value. Represents mutable double function parameters */
public class DoubleRef {

    private ByteBuffer value;

    public DoubleRef() {
        this.value = ByteBuffer.allocateDirect(Double.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putDouble(0, 0);
    }

    public DoubleRef(double value) {
        this.value = ByteBuffer.allocateDirect(Double.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putDouble(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public DoubleRef(ByteBuffer buffer) {
        this.value = buffer;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
    }

    public double getValue() {
        return value.getDouble(0);
    }

    public void setValue(double value) {
        this.value.putDouble(0, value);
    }

    @Override
    public String toString() {
        return Double.toString(value.getDouble(0));
    }
}
