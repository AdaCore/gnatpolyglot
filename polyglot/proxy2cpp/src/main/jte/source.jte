@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Module
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

#include "${api.headerFilePath(module).getFileName().toString()}"

namespace ${api.namespacePath(module)} {

@for (var decl : module.declarations)
    @if (decl instanceof FunctionDecl functionDecl && functionDecl.role == null)
        @template.function(api, functionDecl, true)
    @endif
@endfor

} // namespace ${api.namespacePath(module)}
