<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
--%>
@import com.adacore.gnatpolyglot.NativeType
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl.Overridability
@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy.ArrayTypeExpr
@import com.adacore.gnatpolyglot.proxy2cpp.CppAPI
@param CppAPI api
@param FunctionDecl functionDecl
@param Boolean isSource

@if(!isSource)
    !{boolean overrides = api.isOverriding(functionDecl);}
    ${api.formatDoc(functionDecl.doc, 1)}
    <%-- Both overridable functions and abstract funtions must be marked virtual --%>
    @if(functionDecl.overridability.compareTo(Overridability.OVERRIDABLE) >= 0 && !overrides)
        virtual
    @endif
    ${api.cppReturnTypename(functionDecl.type.returnType)} ${api.functionName(functionDecl)}
        (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)}
    @if(overrides)
        override
    @endif
    @if (functionDecl.overridability == Overridability.ABSTRACT)
        = 0
    @endif
    ;
@elseif(functionDecl.overridability != Overridability.ABSTRACT)

    @for (var param : functionDecl.type.parameters)
    @if (param.type.referencedType().isFunction())
    @template.callback(
        api,
        (FunctionTypeExpr) param.type.referencedType(),
        api.getCallbackName(param, functionDecl)
    )
    @endif
    @endfor

    extern "C" ${api.cTypename(functionDecl.type.returnType)} ${functionDecl.symbol}
        (${api.cParameters(functionDecl)});
    ${api.cppReturnTypename(functionDecl.type.returnType)}
    ${api.functionDefinitionName(functionDecl)}
        (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)} {
        @for(var param : functionDecl.type.parameters)
            ${api.verifyOwnership(param)}
            ${api.createPointerBuffer(param, functionDecl)}
        @endfor
        @if (api.returnsVoid(functionDecl.type))
            ${api.callCSymbol(functionDecl)};
            @for(var param : functionDecl.type.parameters)
                ${api.checkPointerValue(param)}
            @endfor
            gnatpolyglot::exceptions::${api.getProjectName().toLower()}::rethrow_if_exc();
        @else
            ${api.cTypename(functionDecl.type.returnType)} returned_value =
                ${api.callCSymbol(functionDecl)};
            @for(var param : functionDecl.type.parameters)
                ${api.checkPointerValue(param)}
            @endfor
            gnatpolyglot::exceptions::${api.getProjectName().toLower()}::rethrow_if_exc();
            ${api.makeReturnStatement(functionDecl, "returned_value")};
        @endif
    }
@endif
