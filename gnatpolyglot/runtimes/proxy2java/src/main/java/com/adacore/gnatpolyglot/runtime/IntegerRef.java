package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable int value. Represents mutable integer function parameters */
public class IntegerRef extends ScalarRef {

    public IntegerRef() {
        super(Integer.BYTES);
        this.value.putInt(0, 0);
    }

    public IntegerRef(int value) {
        super(Integer.BYTES);
        this.value.putInt(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public IntegerRef(ByteBuffer buffer) {
        super(buffer);
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
