<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.FunctionDecl.Overridability
@import com.adacore.polyglot.proxy.ArrayTypeExpr
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param FunctionDecl functionDecl
@param Boolean isSource

@if(!isSource)
    !{boolean overrides = api.isOverriding(functionDecl);}
    @if(functionDecl.overridability == Overridability.OVERRIDABLE && !overrides)
        virtual
    @endif
    ${api.formatDoc(functionDecl.doc, 1)}
    ${api.cppReturnTypename(functionDecl.type.returnType)} ${functionDecl.getLastName().toLower()}
        (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)}
    @if(overrides)
        override
    @endif
    ;
@else
    extern "C" ${api.cTypename(functionDecl.type.returnType)} ${functionDecl.symbol}
        (${api.cParameters(functionDecl)});
    ${api.cppReturnTypename(functionDecl.type.returnType)}
    ${api.functionDefinitionName(functionDecl)}
        (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)} {
        ${api.makeReturnStatement(functionDecl)};
    }
@endif
