<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate the accessors of Ref classes of ClassDecls.
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
            .filter(c -> c.inheritability == Inheritability.FINAL)
            .toList();
}

#include <jni.h>

@for (var clazz : classes)
!{
    TypeExpr typeExpr = clazz.name.asTypeExpr();
    String className = api.javaTypename(typeExpr);

    boolean hasArray = api.getArrayFunctions(clazz) != null;
    TypeExpr arrayTypeExpr = clazz.name.asTypeExpr().makeArray();

    boolean hasPtrArray = api.getPtrArrayFunctions(clazz) != null;
    TypeExpr ptrArrayTypeExpr = clazz.name.asTypeExpr().makePointer(false, false).makeArray();
}
@if (!isSource)

    jclass ${api.refClassFunctionName(typeExpr)}(JNIEnv *env);
    jmethodID ${api.refCtorFunctionName(typeExpr)}(JNIEnv *env);

    @if (hasArray)
        jclass ${api.refClassFunctionName(arrayTypeExpr)}(JNIEnv *env);
        jmethodID ${api.refCtorFunctionName(arrayTypeExpr)}(JNIEnv *env);
    @endif

    @if (hasPtrArray)
        jclass ${api.refClassFunctionName(ptrArrayTypeExpr)}(JNIEnv *env);
        jmethodID ${api.refCtorFunctionName(ptrArrayTypeExpr)}(JNIEnv *env);
    @endif

@else

    jclass ${api.refClassFunctionName(typeExpr)}(JNIEnv *env) {
        static jclass c = NULL;
        if (c == NULL) {
            c = (*env)->NewGlobalRef(
                env,
                (*env)->FindClass(env, "${className.replace(".", "/")}$Ref")
            );
        }
        return c;
    }

    jmethodID ${api.refCtorFunctionName(typeExpr)}(JNIEnv *env) {
        static jmethodID m = NULL;
        if (m == NULL) {
            jclass clazz = ${api.refClassFunctionName(typeExpr)}(env);
            m = (*env)->GetMethodID(
                env, clazz, "<init>",
                "(J)V"
            );
        }
        return m;
    }

    @if (hasArray)

        jclass ${api.refClassFunctionName(arrayTypeExpr)}(JNIEnv *env) {
            static jclass c = NULL;
            if (c == NULL) {
                c = (*env)->NewGlobalRef(
                    env,
                    (*env)->FindClass(env, "${className.replace(".", "/")}$Array$Ref")
                );
            }
            return c;
        }

        jmethodID ${api.refCtorFunctionName(arrayTypeExpr)}(JNIEnv *env) {
            static jmethodID m = NULL;
            if (m == NULL) {
                jclass clazz = ${api.refClassFunctionName(arrayTypeExpr)}(env);
                m = (*env)->GetMethodID(
                    env, clazz, "<init>",
                    "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V"
                );
            }
            return m;
        }

    @endif

    @if (hasPtrArray)

        jclass ${api.refClassFunctionName(ptrArrayTypeExpr)}(JNIEnv *env) {
            static jclass c = NULL;
            if (c == NULL) {
                c = (*env)->NewGlobalRef(
                    env,
                    (*env)->FindClass(env, "${className.replace(".", "/")}$PtrArray$Ref")
                );
            }
            return c;
        }

        jmethodID ${api.refCtorFunctionName(ptrArrayTypeExpr)}(JNIEnv *env) {
            static jmethodID m = NULL;
            if (m == NULL) {
                jclass clazz = ${api.refClassFunctionName(ptrArrayTypeExpr)}(env);
                m = (*env)->GetMethodID(
                    env, clazz, "<init>",
                    "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V"
                );
            }
            return m;
        }

    @endif

@endif
@endfor
