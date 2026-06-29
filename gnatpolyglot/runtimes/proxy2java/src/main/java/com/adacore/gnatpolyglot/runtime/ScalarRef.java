//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class ScalarRef {

    protected ByteBuffer value;

    public ScalarRef(int bufferSize) {
        this.value = ByteBuffer.allocateDirect(bufferSize);
        this.value.order(ByteOrder.nativeOrder());
    }

    public ScalarRef(ByteBuffer value) {
        this.value = value;
    }

    /** Internal use only.
     *
     * <p>Return the internal buffer holding the value.
     */
    public ByteBuffer getBuffer() {
        return value;
    }

}

