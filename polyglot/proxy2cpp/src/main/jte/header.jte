<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.ExceptionDecl
@import com.adacore.polyglot.proxy.EnumerationDecl
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Module
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

!{String headerGuardMacro = api.headerGuard(module);}

#ifndef ${headerGuardMacro}
#define ${headerGuardMacro}

#include <polyglot_ada_arrays.h>
#include <polyglot_ada_exceptions.h>
#include <polyglot_ada_strings.h>
#include <polyglot_ptr.h>

#if __cplusplus >= 201103L
#include <array>
#endif

#include <cstdint>

@for (var include : api.getIncludes(module))
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
