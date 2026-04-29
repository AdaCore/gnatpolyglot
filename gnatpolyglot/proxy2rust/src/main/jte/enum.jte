<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template fragment: a single Rust enum generated from a Proxy enumeration.
--%>
@import com.adacore.gnatpolyglot.proxy.EnumerationDecl
@import com.adacore.gnatpolyglot.proxy2rust.RustAPI

@param RustAPI api
@param EnumerationDecl enumDecl

#[repr(${api.rustEnumRepr(enumDecl)})]
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ${api.rustEnumName(enumDecl)} {
@for (var item : enumDecl.items)
    ${item.name.toPascal()} = ${String.valueOf(item.value)},
@endfor
}
