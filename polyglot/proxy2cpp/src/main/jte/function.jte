@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param FunctionDecl functionDecl
@param Boolean isSource

@if(!isSource)
    ${api.formatDoc(functionDecl.doc, 1)}
    ${api.cppTypename(functionDecl.returnType)} ${functionDecl.name.toLower()}
        ();
@else
    extern "C" ${api.cTypename(functionDecl.returnType)} ${functionDecl.symbol}
        ();
    ${api.cppTypename(functionDecl.returnType)} ${functionDecl.name.toLower()}
        () {
        return ${api.callCSymbol(functionDecl)};
    }
@endif
