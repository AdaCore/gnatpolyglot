package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable char value. Represents mutable char function parameters */
public class CharacterRef extends ScalarRef {

    public CharacterRef() {
        super(Character.BYTES);
        this.value.putChar(0, '\0');
    }

    public CharacterRef(char value) {
        super(Character.BYTES);
        this.value.putChar(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public CharacterRef(ByteBuffer buffer) {
        super(buffer);
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
