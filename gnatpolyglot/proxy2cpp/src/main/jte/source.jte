<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.ExceptionDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.Module
@import com.adacore.gnatpolyglot.proxy.Role.RoleKind
@import com.adacore.gnatpolyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

@template.text_header()

#include <gnatpolyglot_ada_strings.h>
#include "gnatpolyglot_exceptions_${api.getProjectName().toLower()}.h"
#include "gnatpolyglot.h"

#include "${api.headerFilePath(module).getFileName().toString()}"

@for (var include : api.getIncludes(module, true))
#include "${include}"
@endfor

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
