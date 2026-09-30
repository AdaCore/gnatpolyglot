//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable char value. Represents mutable char function parameters */
public class CharacterRef extends ScalarRef {

    public CharacterRef() {
        super(Character.BYTES);
        this.value.put(0, (byte) 0);
    }

    public CharacterRef(char value) {
        super(Character.BYTES);
        this.value.put(0, (byte) value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public CharacterRef(ByteBuffer buffer) {
        super(buffer);
    }

    public char getValue() {
        return (char) (value.get(0) & 0xff);
    }

    public void setValue(char value) {
        this.value.put(0, (byte) value);
    }

    @Override
    public String toString() {
        return Character.toString(getValue());
    }
}
