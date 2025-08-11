<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Module
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

@for (var include : api.getIncludes(module))
#include "${include}"
@endfor

#include <polyglot_ada_strings.h>

namespace ${api.namespacePath(module)} {

@for (var decl : module.declarations)
    @if (decl instanceof FunctionDecl functionDecl
            && (functionDecl.role == null || api.isMethod(functionDecl)))
        @template.function(api, functionDecl, true)
    @elseif (decl instanceof ClassDecl classDecl)
        @template.class(api, classDecl, true)
    @endif
@endfor

} // namespace ${api.namespacePath(module)}
