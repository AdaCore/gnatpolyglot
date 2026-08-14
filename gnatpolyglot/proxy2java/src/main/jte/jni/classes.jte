<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate JNI jclass getters.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.ClassDecl.Inheritability
@import com.adacore.gnatpolyglot.proxy.TypeExpr
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI
@import java.util.List

@param JavaAPI api
@param Proxy proxy
@param boolean isSource

!{
    List<ClassDecl> classes =
        proxy.modules.stream()
            .flatMap(m -> m.declarations.stream().filter(ClassDecl.class::isInstance))
            .map(c -> (ClassDecl) c)
            .toList();
}

@template.text_header()

#include <jni.h>

@for (var pack : proxy.modules)
!{
    String functionName = api.jclassFunctionName(pack.name);
    String fqn =
        api.packagePath(pack) + "." + pack.name.getLastName().toPascal() + "Package";
}
@if (!isSource)

jclass ${functionName}(JNIEnv *env);

@else

jclass ${functionName}(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(env, "${fqn.replace(".", "/")}")
        );
    }
    return c;
}

@endif
@endfor

@for (var classDecl : classes)
!{
    String functionName = api.jclassFunctionName(classDecl.name);
    String fqn = api.javaTypename(classDecl.name.asTypeExpr());
}
@if (!isSource)

jclass ${functionName}(JNIEnv *env);

@else

jclass ${functionName}(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(env, "${fqn.replace(".", "/")}")
        );
    }
    return c;
}

@endif
@endfor
