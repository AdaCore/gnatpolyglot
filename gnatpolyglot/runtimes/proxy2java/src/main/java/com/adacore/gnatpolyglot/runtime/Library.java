//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

public class Library {

    /** Whether the runtime library has already been loaded. */
    private static boolean initialized = false;

    /** Load the runtime library. Calling this method multiple times does nothing. */
    public static void loadLibrary() {
        if (!initialized) {
            System.loadLibrary("gnatpolyglot_proxy2java");
            initialized = true;
        }
    }

    static {
        loadLibrary();
    }
}
