//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable boolean value. Represents mutable boolean function parameters */
public class BooleanRef extends ScalarRef {

    public BooleanRef() {
        super(Byte.BYTES);
        this.value.put(0, (byte)0);
    }

    public BooleanRef(boolean value) {
        super(Byte.BYTES);
        this.value.put(0, (byte) (value ? 1 : 0));
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public BooleanRef(ByteBuffer buffer) {
        super(buffer);
    }

    public boolean getValue() {
        return value.get(0) != 0;
    }

    public void setValue(boolean value) {
        this.value.put(0, (byte) (value ? 1 : 0));
    }

    @Override
    public String toString() {
        return Boolean.toString(getValue());
    }
}
