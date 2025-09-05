<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Module
@import com.adacore.polyglot.proxy.Role.RoleKind
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

@for (var include : api.getIncludes(module))
#include "${include}"
@endfor

namespace ${api.namespacePath(module)} {

@for (var decl : module.declarations)
    !{FunctionDecl functionDecl = (FunctionDecl) decl;}
    @if(functionDecl.role.kind == RoleKind.FREE)
        extern "C" ${api.cTypename(functionDecl.type.returnType)} ${functionDecl.symbol}
            (${api.cParameters(functionDecl)});
        template<>
        ${api.cppTypename(functionDecl.role.type)}::~polyglot_array() {
            ${functionDecl.symbol}(&this->_data);
        }

    @elseif(functionDecl.role.kind == RoleKind.CONSTRUCT)
        extern "C" ${api.cTypename(functionDecl.type.returnType)} ${functionDecl.symbol}
            (${api.cParameters(functionDecl)});
        template<>
        ${api.cppTypename(functionDecl.role.type)}::polyglot_array
            (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)} {
            ${api.callCSymbol(functionDecl)};
        }
    @elseif(functionDecl.role.kind != RoleKind.ALLOC)
        extern "C" ${api.cTypename(functionDecl.type.returnType)} ${functionDecl.symbol}
            (${api.cParameters(functionDecl)});
        template <>
        ${api.cppReturnTypename(functionDecl.type.returnType)}
        ${api.functionDefinitionName(functionDecl)}
            (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)} {
            ${api.makeReturnStatement(functionDecl, api.callCSymbol(functionDecl))};
        }
    @endif
@endfor

} // namespace ${api.namespacePath(module)}

