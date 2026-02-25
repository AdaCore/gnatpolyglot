<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.ExceptionDecl
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Module
@import com.adacore.polyglot.proxy.Role.RoleKind
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

@template.text_header()

#include <cassert>

#include <polyglot_ada_strings.h>
#include "polyglot_exceptions_${api.getProjectName().toLower()}.h"
#include "polyglot.h"

#include "${api.headerFilePath(module).getFileName().toString()}"

namespace ${api.namespacePath(module)} {

@for (var decl : module.declarations)
    @if (decl instanceof FunctionDecl functionDecl
            && (functionDecl.role == null
                || functionDecl.role.kind.compareTo(RoleKind.SETTER) <= 0))
        @template.function(api, functionDecl, true)
    @elseif (decl instanceof ClassDecl classDecl)
        @template.class(api, classDecl, true)
    @elseif (decl instanceof ExceptionDecl exceptionDecl)
        @template.exception(api, exceptionDecl, true)
    @endif
@endfor

} // namespace ${api.namespacePath(module)}
