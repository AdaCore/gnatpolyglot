<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate a Rust module from a Proxy module. This orchestrates
the per-declaration fragments: enum, class, the ffi extern block, and free
functions.
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.EnumerationDecl
@import com.adacore.gnatpolyglot.proxy.Module
@import com.adacore.gnatpolyglot.proxy2rust.RustAPI

@param RustAPI api
@param Module module

!{
    var enums = module.declarations.stream()
            .filter(EnumerationDecl.class::isInstance)
            .map(EnumerationDecl.class::cast)
            .toList();
    var classes = module.declarations.stream()
            .filter(ClassDecl.class::isInstance)
            .map(ClassDecl.class::cast)
            .filter(c -> c.inheritability != ClassDecl.Inheritability.VIRTUAL)
            .toList();
    var freeFunctions = api.moduleFreeFunctions(module);
}

@for (var enumDecl : enums)
    @template.enum(api, enumDecl)

@endfor

@for (var classDecl : classes)
    @template.class(api, classDecl, module)
@endfor

@template.ffi(api, module)

@for (var func : freeFunctions)
    @template.function(api, func)

@endfor
