@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param FunctionDecl functionDecl
@param Boolean isSource

@if(!isSource)
    ${api.formatDoc(functionDecl.doc, 1)}
    ${api.cppTypename(functionDecl.returnType)} ${functionDecl.name.toLower()}
        (${api.cppParameters(functionDecl)});
@else
    extern "C" ${api.cTypename(functionDecl.returnType)} ${functionDecl.symbol}
        (${api.cParameters(functionDecl)});
    ${api.cppTypename(functionDecl.returnType)} ${functionDecl.name.toLower()}
        (${api.cppParameters(functionDecl)}) {
        return ${api.callCSymbol(functionDecl)};
    }
@endif
