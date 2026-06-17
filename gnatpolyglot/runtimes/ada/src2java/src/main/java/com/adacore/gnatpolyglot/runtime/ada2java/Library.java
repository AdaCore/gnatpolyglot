package com.adacore.gnatpolyglot.runtime.ada2java;

public class Library {

    /** Whether the runtime library has already been loaded. */
    private static boolean initialized = false;

    /** Load the runtime library. Calling this method multiple times does nothing. */
    public static void loadLibrary() {
        if (!initialized) {
            System.loadLibrary("gnatpolyglot_ada2java");
            initialized = true;
        }
    }

    static {
        loadLibrary();
    }
}
