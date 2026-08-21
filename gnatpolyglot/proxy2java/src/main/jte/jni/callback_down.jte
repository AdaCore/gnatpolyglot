<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate a function.
--%>

@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param FunctionTypeExpr functionType
@param String callbackName
@param String lambdaName
@param String javaLambdaName
@param boolean isSource

@if (!isSource)

jobject ${lambdaName}(JNIEnv *env, struct callback_data data);

@else

static jmethodID
${lambdaName}__methodID (JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = ${api.getProjectName().toPascal()}_Callbacks_class(env);
        m = (*env)->GetStaticMethodID(
            env,
            clazz,
            "${javaLambdaName}",
            "(${api.jniTypeSignature(functionType, false)})${api.javaTypeJNISignature(functionType)}"
        );
    }
    return m;
}

jobject ${lambdaName}(JNIEnv *env, struct callback_data data) {
    return (*env)->CallStaticObjectMethod(
        env,
        ${api.getProjectName().toPascal()}_Callbacks_class(env),
        ${lambdaName}__methodID(env),
        gnatpolyglot_proxy2java_to_CallbackData(env, data)
    );
}

/** Downcalling JNI C function for function of type ${api.javaTypename(functionType)}*/
JNIEXPORT ${api.jniReturnTypename(functionType.returnType)}
${callbackName}(
    ${api.jniCallbackParameters(functionType)}
) {
    struct callback_data __callback_data =
        gnatpolyglot_proxy2java_to_callback_data(env, _callback_data);
@for (var param : functionType.parameters)
    ${api.makeJNIParamConversion(param, null)};
@endfor
@if (api.returnsVoid(functionType))
    ${api.callCPointer(functionType, "__callback_data")};
    @for (var param : functionType.parameters)
        ${api.makeJNIParamUpdate(param)};
    @endfor
@else
    ${api.cTypename(functionType.returnType)} value_returned =
        ${api.callCPointer(functionType, "__callback_data")};
    @for (var param : functionType.parameters)
        ${api.makeJNIParamUpdate(param)};
    @endfor
    ${api.makeCReturnStatement(functionType, "value_returned")}
@endif
}

@endif