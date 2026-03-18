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
    ${api.callCSymbol(function)};
}
