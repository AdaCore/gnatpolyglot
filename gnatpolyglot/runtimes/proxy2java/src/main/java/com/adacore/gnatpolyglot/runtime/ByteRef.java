package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable byte value. Represents mutable byte function parameters */
public class ByteRef extends ScalarRef {

    public ByteRef() {
        super(Byte.BYTES);
        this.value.put(0, (byte) 0);
    }

    public ByteRef(byte value) {
        super(Byte.BYTES);
        this.value.put(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public ByteRef(ByteBuffer buffer) {
        super(buffer);
    }

    public byte getValue() {
        return value.get(0);
    }

    public void setValue(byte value) {
        this.value.put(0, value);
    }

    @Override
    public String toString() {
        return Byte.toString(value.get(0));
    }
}
