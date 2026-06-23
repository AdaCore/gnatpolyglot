<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a function.
--%>
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl.Overridability
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param FunctionDecl function
@param String functionName = null

!{
    String nativeFunc = "$%s".formatted(function.symbol);
    boolean isMethod = api.isMethod(function);
    functionName = functionName != null
        ? functionName
        : api.functionName(function.name.getLastName(), isMethod);
    boolean firstMethodParam = isMethod;
}

@if (function.overridability == Overridability.ABSTRACT)
    public abstract
    ${api.javaReturnTypename(function.type.returnType)}
    ${functionName}(${api.javaParameters(function)});
@else
    private static native
    ${api.javaNativeReturnTypename(function.type.returnType)}
    ${nativeFunc}(${api.javaNativeParameters(function)});

    public ${isMethod ? "" : "static"}
    ${api.javaReturnTypename(function.type.returnType)}
    ${functionName}(${api.javaParameters(function)}) {
    @for (var param : function.type.parameters)
        ${api.makeJavaParamConversion(param, firstMethodParam)};
        !{firstMethodParam = false;}
    @endfor
    @if (api.returnsVoid(function.type))
        ${api.callJavaNative(function, nativeFunc)};
        ${api.basePackage()}.Library.raiseIfException();
    @else
        ${api.javaNativeReturnTypename(function.type.returnType)} valueReturned =
            ${api.callJavaNative(function, nativeFunc)};
        ${api.basePackage()}.Library.raiseIfException();
        ${api.makeJavaReturnStatement(function, "valueReturned")}
    @endif
    }
@endif
