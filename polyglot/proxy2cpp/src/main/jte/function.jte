<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param FunctionDecl functionDecl
@param Boolean isSource

@if(!isSource)
    ${api.formatDoc(functionDecl.doc, 1)}
    ${api.cppTypename(functionDecl.returnType.getName())} ${functionDecl.getLastName().toLower()}
        (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)};
@else
    extern "C" ${api.cTypename(functionDecl.returnType.getName())} ${functionDecl.symbol}
        (${api.cParameters(functionDecl)});
    ${api.cppTypename(functionDecl.returnType.getName())}
    ${api.functionDefinitionName(functionDecl)}
        (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)} {
        return ${api.cppTypename(functionDecl.returnType.getName())}(
            ${api.callCSymbol(functionDecl)}
        );
    }
@endif
