//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

public class PolyglotKernel {
    public static class ExceptionInformation {
        public static native int getExceptionKind();

        public static native String getMessage();

        public static native long getExceptionData();

        public static native void setExceptionData(long addr);

        public static native void clearException();

        static {
            Library.loadLibrary();
        }
    }

    static {
        Library.loadLibrary();
    }
}
