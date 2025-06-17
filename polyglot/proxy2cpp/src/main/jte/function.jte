<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.ArrayTypeExpr
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param FunctionDecl functionDecl
@param Boolean isSource

@if(!isSource)
    ${api.formatDoc(functionDecl.doc, 1)}
    ${api.cppReturnTypename(functionDecl.returnType)} ${functionDecl.getLastName().toLower()}
        (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)};
@else
    extern "C" ${api.cTypename(functionDecl.returnType)} ${functionDecl.symbol}
        (${api.cParameters(functionDecl)});
    ${api.cppReturnTypename(functionDecl.returnType)}
    ${api.functionDefinitionName(functionDecl)}
        (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)} {
        ${api.makeReturnStatement(functionDecl)};
    }
@endif
