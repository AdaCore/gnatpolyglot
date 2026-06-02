<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate the JNI file for a module
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.Module
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param Module module

#include <jni.h>

#include "gnatpolyglot_ada2java.h"

@for (var decl : module.declarations)
    @if (decl instanceof ClassDecl classDecl)
        @template.jni.class(api, classDecl)
    @endif
@endfor

@for (var decl : module.declarations)
    @if (decl instanceof FunctionDecl function)
        @template.jni.function(api, function)
    @endif
@endfor
