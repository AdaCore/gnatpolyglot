<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a Java callback.
--%>

@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param FunctionTypeExpr functionType
@param String callbackName
@param boolean isDispatch
@param String callee = null
@param String className = null

!{
    String firstParam = isDispatch
       ? className + " _self"
       : api.javaTypename(functionType) + " _callback";
    callee = isDispatch
       ? "_self." + callee
       : "_callback." + api.functionalInterfaceMethod(functionType);
}

    /** Upcalling callback for function of type ${api.getJavaGenericCallback(functionType)} */
    private static ${api.javaNativeReturnTypename(functionType.returnType)}
    ${callbackName} (${firstParam}${api.javaUpcallParameters(functionType, isDispatch)}) {
    @for (var param : functionType.parameters.stream().skip(isDispatch ? 1 : 0).toList())
        ${api.makeJavaUpcallParamConversion(param, callbackName)}
    @endfor

        try {
        @if (api.returnsVoid(functionType))
            ${api.callJavaUpcall(functionType, callee, isDispatch)};
        @else
            ${api.javaReturnTypename(functionType.returnType)} valueReturned =
                ${api.callJavaUpcall(functionType, callee, isDispatch)};
            ${api.makeJavaUpcallReturnStatement(functionType, "valueReturned")}
        @endif
        } catch (com.adacore.gnatpolyglot.runtime.ada2java.AdaException exc$e) {
            com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation.setExceptionData(
                exc$e._release().getAddress()
            );
            ${api.makeJavaUpcallDefaultReturnStatement(functionType)}
        } catch (java.lang.Throwable exc$e) {
            com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation.setExceptionData(
                new com.adacore.gnatpolyglot.runtime.ada2java.AdaException(
                    new com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString(
                        "Uncaught host language exception"
                    )
                )._release().getAddress()
            );
            ${api.makeJavaUpcallDefaultReturnStatement(functionType)}
        }
    }
