<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a the JNI and C functions for handling callbacks.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI
@import com.adacore.gnatpolyglot.proxy2java.CallbackCollector
@import java.util.List

@param JavaAPI api
@param Proxy proxy
@param boolean isSource

!{
    List<FunctionTypeExpr> callbacks = CallbackCollector.getCallbacks(proxy);
}

@template.text_header()

#include <jni.h>
#include <stdint.h>

#include "gnatpolyglot_proxy2java.h"
#include "gnatpolyglot_ada2java.h"

@if (isSource)

static jclass ${api.getProjectName().toPascal()}_Callbacks_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "${api.basePackage().replace(".", "/")}/Callbacks"
            )
        );
    }
    return c;
}

@endif

@for (var callback : callbacks)

@template.jni.callback_down(
    api,
    callback,
    api.getJNIDownCallbackName(callback),
    api.getJNILambdaName(callback),
    api.getJavaLambdaName(callback),
    isSource = isSource
)

@template.jni.callback(
    api,
    callback,
    api.getJNIUpCallbackName(callback),
    false,
    null,
    api.getJavaUpCallbackName(callback),
    api.jniSignature(callback, null),
    isSource = isSource
)

@endfor
