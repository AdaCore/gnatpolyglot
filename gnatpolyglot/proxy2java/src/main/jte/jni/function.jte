<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate a function.
--%>

@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param FunctionDecl function

extern ${api.cTypename(function.type.returnType)} ${function.symbol}();

JNIEXPORT ${api.jniTypename(function.type.returnType)}
${api.jniName(function)} (JNIEnv *env, jobject obj) {
@if (api.returnsVoid(function.type))
    ${api.callCSymbol(function)};
@else
    ${api.cTypename(function.type.returnType)} value_returned =
        ${api.callCSymbol(function)};
    ${api.makeCReturnStatement(function, "value_returned")}
@endif
}
