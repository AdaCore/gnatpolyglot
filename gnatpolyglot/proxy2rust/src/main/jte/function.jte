<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template fragment: a single free Rust function.
--%>
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy2rust.RustAPI

@param RustAPI api
@param FunctionDecl func

@if (func.doc != null && !func.doc.isBlank())
${api.formatDoc(func.doc, 0)}
@endif
pub fn ${api.rustFunctionName(func)}(${api.rustParameters(func)})${api.rustReturnTypeSuffix(func)} {
    unsafe { ${api.ffiCallExpr(func)} }
}
