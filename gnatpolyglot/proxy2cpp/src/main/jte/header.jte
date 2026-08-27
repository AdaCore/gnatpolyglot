<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.ExceptionDecl
@import com.adacore.gnatpolyglot.proxy.EnumerationDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.Module
@import com.adacore.gnatpolyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

!{String headerGuardMacro = api.headerGuard(module);}

@template.text_header()

#ifndef ${headerGuardMacro}
#define ${headerGuardMacro}

#include <gnatpolyglot_ada_arrays.h>
#include <gnatpolyglot_ada_exceptions.h>
#include <gnatpolyglot_ada_strings.h>
#include <gnatpolyglot_ptr.h>
#include <gnatpolyglot.h>

#if __cplusplus >= 201103L
#include <array>
#endif

#include <functional>
#include <cstdint>

@for (var include : api.getIncludes(module, false))
#include "${include}"
@endfor

namespace ${api.namespacePath(module)} {

@for (var decl : module.declarations)
    @if (decl instanceof ClassDecl classDecl)
        class ${classDecl.getLastName().toPascal()};
    @elseif (decl instanceof EnumerationDecl enumDecl)
        @template.enum(api, enumDecl)
    @endif
@endfor

@for (var decl : module.declarations)
    @if (decl instanceof FunctionDecl functionDecl && functionDecl.role == null)
        @template.function(api, functionDecl, false)
    @elseif (decl instanceof ClassDecl classDecl)
        @template.class(api, classDecl, false)
    @elseif (decl instanceof ExceptionDecl exceptionDecl)
        @template.exception(api, exceptionDecl, false)
    @endif
@endfor

} // namespace ${api.namespacePath(module)}

#endif /* ! ${headerGuardMacro} */
