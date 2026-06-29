//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;

/** Class holding a mutable float value. Represents mutable float function parameters */
public class FloatRef extends ScalarRef {

    public FloatRef() {
        super(Float.BYTES);
        this.value.putFloat(0, 0);
    }

    public FloatRef(float value) {
        super(Float.BYTES);
        this.value.putFloat(0, value);
    }

    /** Internal use only.
     *
     * <p>Construct a reference from an existing buffer.
     */
    public FloatRef(ByteBuffer buffer) {
        super(buffer);
    }

    public float getValue() {
        return value.getFloat(0);
    }

    public void setValue(float value) {
        this.value.putFloat(0, value);
    }

    @Override
    public String toString() {
        return Float.toString(value.getFloat(0));
    }
}
