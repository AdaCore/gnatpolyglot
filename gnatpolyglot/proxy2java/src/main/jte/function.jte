<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a function.
--%>
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param FunctionDecl function

!{
    String functionName = function.name.getLastName().toCamel();
    String nativeFunc = "$%s".formatted(functionName);
}
    private static native
    ${api.javaNativeTypename(function.type.returnType)} ${nativeFunc}();

    public static
    ${api.javaTypename(function.type.returnType)}
    ${functionName}() {
    @if (api.returnsVoid(function.type))
        ${api.callJavaNative(function, nativeFunc)};
    @else
        ${api.javaNativeTypename(function.type.returnType)} valueReturned =
            ${api.callJavaNative(function, nativeFunc)};
        ${api.makeJavaReturnStatement(function, "valueReturned")}
    @endif
    }
