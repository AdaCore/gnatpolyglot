//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable short value. Represents mutable short function parameters */
public class ShortRef extends ScalarRef {

    public ShortRef() {
        super(Short.BYTES);
        this.value.putShort(0, (short) 0);
    }

    public ShortRef(short value) {
        super(Short.BYTES);
        this.value.putShort(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public ShortRef(ByteBuffer buffer) {
        super(buffer);
    }

    public short getValue() {
        return value.getShort(0);
    }

    public void setValue(short value) {
        this.value.putDouble(0, value);
    }

    @Override
    public String toString() {
        return Short.toString(value.getShort(0));
    }
}
