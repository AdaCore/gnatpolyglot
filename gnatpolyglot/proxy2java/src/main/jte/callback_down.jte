<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a function.
--%>
@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param FunctionTypeExpr functionType
@param String callbackName
@param String lambdaName

    /** Downcalling function for function of type ${api.getJavaGenericCallback(functionType)} */
    public static native
    ${api.javaNativeReturnTypename(functionType.returnType)}
    ${callbackName}(
        com.adacore.gnatpolyglot.runtime.Functions.CallbackData callbackData
        ${api.javaUpcallParameters(functionType, false)}
    );

    /** Lambda creating function calling downcall callbacks of type ${api.getJavaGenericCallback(functionType)} */
    public static ${api.javaNativeTypename(functionType)}
    ${lambdaName}(com.adacore.gnatpolyglot.runtime.Functions.CallbackData data) {
        return ${api.buildCallbackLambda(functionType, "data")};
    }
