<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate a JNI callback.
--%>
@import com.adacore.gnatpolyglot.proxy.FullyQualifiedName
@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI
@param JavaAPI api
@param FunctionTypeExpr functionType
@param String callbackName
@param boolean isDispatch
@param FullyQualifiedName owningClass
@param String javaFunctionName
@param String javaFunctionSignature
@param boolean isSource = true

!{
    String jclassGetter = owningClass == null
        ? jclassGetter = api.getProjectName().toPascal() + "_Callbacks_class"
        : api.jclassFunctionName(owningClass);
}

@if (!isSource)

${api.cTypename(functionType.returnType)} ${callbackName}(
    ${api.jniUpcallParameters(functionType, isDispatch)}
);

@else

static jmethodID
${callbackName}__methodID (JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = ${jclassGetter}(env);
        m = (*env)->GetStaticMethodID(
            env,
            clazz,
            "${javaFunctionName}",
            "${javaFunctionSignature}"
        );
    }
    return m;
}

/** Upcalling JNI function for function of type ${api.javaTypename(functionType)}*/
${isDispatch ? "static " : ""}${api.cTypename(functionType.returnType)}
${callbackName} (
    ${api.jniUpcallParameters(functionType, isDispatch)}
) {
@if (isDispatch)
    JavaVM *vm = _self_data;
@else
    JavaVM *vm = (JavaVM *) _callback_data.extra;
@endif
    JNIEnv *env = NULL;
    (*vm)->AttachCurrentThread(vm, (void **)&env, NULL);
    jclass clazz = ${jclassGetter}(env);
    jmethodID method = ${callbackName}__methodID(env);

@for (var param : functionType.parameters.stream().skip(isDispatch ? 1 : 0).toList())
    ${api.makeJNIUpcallParamConversion(param)}
@endfor

@if (api.returnsVoid(functionType))
    ${api.callJNIUpcall(functionType, isDispatch)};
@else
    ${api.jniReturnTypename(functionType.returnType)} valueReturned =
        ${api.callJNIUpcall(functionType, isDispatch)};
    ${api.makeCUpcallReturnStatement(functionType, "valueReturned")}
@endif

@for (var param : functionType.parameters.stream().skip(isDispatch ? 1 : 0).toList())
    ${api.makeJNIUpcallParamUpdate(param)}
@endfor
}

@endif