package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable double value. Represents mutable double function parameters */
public class DoubleRef extends ScalarRef {

    public DoubleRef() {
        super(Double.BYTES);
        this.value.putDouble(0, 0);
    }

    public DoubleRef(double value) {
        super(Double.BYTES);
        this.value.putDouble(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public DoubleRef(ByteBuffer buffer) {
        super(buffer);
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
