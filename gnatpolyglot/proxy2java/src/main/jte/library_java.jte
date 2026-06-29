<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a central class for the Java bindings.
--%>
@import com.adacore.gnatpolyglot.proxy.ExceptionDecl
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI
@import java.util.Comparator
@import java.util.List

@param JavaAPI api
@param Proxy proxy

!{
    List<ExceptionDecl> exceptions = proxy.modules.stream()
        .flatMap(
            m -> m.declarations.stream()
                  .filter(d -> d instanceof ExceptionDecl)
                  .map(d -> (ExceptionDecl) d)
        )
        .sorted(Comparator.comparingInt(exc -> exc.enumValue))
        .toList();
}

@template.text_header()

package ${api.basePackage()};

import com.adacore.gnatpolyglot.runtime.PolyglotKernel;

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

    public static void raiseIfException() {
        long addr = PolyglotKernel.ExceptionInformation.getExceptionData();
        if (addr == 0) return;
        com.adacore.gnatpolyglot.runtime.PolyglotData data =
            new com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer(
                addr, com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.USER);

        int kind = PolyglotKernel.ExceptionInformation.getExceptionKind();
        if (kind < 0)
            com.adacore.gnatpolyglot.runtime.ada2java.AdaException.rethrowStandardException();

        var message = new com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString(
            PolyglotKernel.ExceptionInformation.getMessage()
        );
        PolyglotKernel.ExceptionInformation.setExceptionData(0);
        com.adacore.gnatpolyglot.runtime.PolyglotException exc = switch (kind) {
        @for (var exc : exceptions)
            case ${exc.enumValue} ->
                new ${api.javaTypename(exc.name.asTypeExpr())}(data, message);
        @endfor
            default ->
                new com.adacore.gnatpolyglot.runtime.ada2java.AdaException(data, message);
        };
        PolyglotKernel.ExceptionInformation.clearException();
        throw exc;
    }

}
