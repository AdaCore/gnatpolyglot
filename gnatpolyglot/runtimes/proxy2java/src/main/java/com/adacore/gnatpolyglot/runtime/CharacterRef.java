package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Class holding a mutable char value. Represents mutable char function parameters */
public class CharacterRef {

    private ByteBuffer value;

    public CharacterRef() {
        this.value = ByteBuffer.allocateDirect(Character.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putChar(0, '\0');
    }

    public CharacterRef(char value) {
        this.value = ByteBuffer.allocateDirect(Character.BYTES);
        this.value.order(ByteOrder.nativeOrder());
        this.value.putChar(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public CharacterRef(ByteBuffer buffer) {
        this.value = buffer;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
    }

    public char getValue() {
        return value.getChar(0);
    }

    public void setValue(char value) {
        this.value.putChar(0, value);
    }

    @Override
    public String toString() {
        return Character.toString(value.getChar(0));
    }
}
