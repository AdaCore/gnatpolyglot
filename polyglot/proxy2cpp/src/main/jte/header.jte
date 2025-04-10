<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Module
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

!{String headerGuardMacro = api.headerGuard(module);}

#ifndef ${headerGuardMacro}
#define ${headerGuardMacro}

#include <cstdint>

namespace ${api.namespacePath(module)} {

@for (var decl : module.declarations)
    @if (decl instanceof FunctionDecl functionDecl && functionDecl.role == null)
        @template.function(api, functionDecl, false)
    @elseif (decl instanceof ClassDecl classDecl)
        @template.class(api, classDecl, false)
    @endif
@endfor

} // namespace ${api.namespacePath(module)}

#endif /* ! ${headerGuardMacro} */
