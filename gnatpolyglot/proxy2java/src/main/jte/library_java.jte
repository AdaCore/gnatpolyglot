<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a central class for the Java bindings.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param Proxy proxy

package ${api.basePackage()};

public class Library {

    /** Whether the library has already been loaded. */
    private static boolean initialized = false;

    /** Load the library. Calling this method multiple times does nothing. */
    public static void loadLibrary() {
        if (!initialized) {
            System.loadLibrary("${proxy.name.toLower()}_jni");
            initialized = true;
        }
    }

}
