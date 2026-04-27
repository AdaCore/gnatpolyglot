package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Class holding a mutable byte value. Represents mutable byte function parameters */
public class ByteRef {

    private ByteBuffer value;

    public ByteRef() {
        this.value = ByteBuffer.allocateDirect(Byte.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.put(0, (byte) 0);
    }

    public ByteRef(byte value) {
        this.value = ByteBuffer.allocate(Byte.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.put(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public ByteRef(ByteBuffer buffer) {
        this.value = buffer;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
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
