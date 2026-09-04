//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#include "gnatpolyglot_proxy2java.h"

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation
 * Method: getExceptionKind
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_PolyglotKernel_00024ExceptionInformation_getExceptionKind(
        JNIEnv *env, jclass c) {
    return (jint) gnatpolyglot_get_kernel()->exc_info.exception_kind;
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation
 * Method: getMessage
 */
JNIEXPORT jstring
Java_com_adacore_gnatpolyglot_runtime_PolyglotKernel_00024ExceptionInformation_getMessage(
        JNIEnv *env, jclass c) {
    return (*env)->NewStringUTF(env, gnatpolyglot_get_kernel()->exc_info.message);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation
 * Method: getExceptionData
 */
JNIEXPORT jlong
Java_com_adacore_gnatpolyglot_runtime_PolyglotKernel_00024ExceptionInformation_getExceptionData(
        JNIEnv *env, jclass c) {
    return (jlong) gnatpolyglot_get_kernel()->exc_info.exception_data;
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation
 * Method: setExceptionData
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_PolyglotKernel_00024ExceptionInformation_setExceptionData(
        JNIEnv *env, jclass c, jlong addr) {
    gnatpolyglot_get_kernel()->exc_info.exception_data = (void *) addr;
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation
 * Method: clearException
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_PolyglotKernel_00024ExceptionInformation_clearException(
        JNIEnv *env, jclass c) {
    struct kernel *k = gnatpolyglot_get_kernel();
    k->exc_info.clear_exception(k);
}

jclass PolyglotObject_class (JNIEnv *env) {
    static jclass m = NULL;
    if (m == NULL) {
        m = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/PolyglotObject"
            )
        );
    }
    return m;
}

jmethodID PolyglotObject_internal_release_method (JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = PolyglotObject_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "_release",
            "()Lcom/adacore/gnatpolyglot/runtime/PolyglotData;"
        );
    }
    return m;
}

jmethodID PolyglotObject_internal_clone_method (JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = PolyglotObject_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "_internalClone",
            "(J)Ljava/lang/Object;"
        );
    }
    return m;
}

jclass PolyglotData_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/PolyglotData"
            )
        );
    }
    return c;
}

jmethodID PolyglotData_getAddress_method(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = PolyglotData_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "getAddress",
            "()J"
        );
    }
    return m;
}

jclass PolyglotData_Pointer_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/PolyglotData$Pointer"
            )
        );
    }
    return c;
}

jmethodID PolyglotData_Pointer_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = PolyglotData_Pointer_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "<init>",
            "(J)V"
        );
    }
    return m;
}

jobject gnatpolyglot_proxy2java_to_Pointer(JNIEnv *env, void *data) {
    jclass clazz = PolyglotData_Pointer_class(env);
    jmethodID ctor = PolyglotData_Pointer_ctor(env);
    return data == NULL
        ? NULL
        : (*env)->NewObject(env, clazz, ctor, (jlong) data);
}

jclass ObjectRef_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ObjectRef"
            )
        );
    }
    return c;
}

jmethodID ObjectRef_update_method(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = ObjectRef_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "update",
            "(Lcom/adacore/gnatpolyglot/runtime/PolyglotData;)V"
        );
    }
    return m;
}

jmethodID ObjectRef_set_method(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = ObjectRef_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "set",
            "(Lcom/adacore/gnatpolyglot/runtime/PolyglotObject;)V"
        );
    }
    return m;

}

jmethodID ObjectRef_getData_method(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = ObjectRef_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "getData",
            "()Lcom/adacore/gnatpolyglot/runtime/PolyglotData;"
        );
    }
    return m;
}

jclass FunctionRef_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/Functions$FunctionRef"
            )
        );
    }
    return c;

}

jmethodID FunctionRef_get_method(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = FunctionRef_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "get",
            "()Ljava/lang/Object;"
        );
    }
    return m;
}

jmethodID FunctionRef_set_method(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = FunctionRef_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "set",
            "(Ljava/lang/Object;)V"
        );
    }
    return m;
}

jclass CallbackData_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/Functions$CallbackData"
            )
        );
    }
    return c;
}

jmethodID CallbackData_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = CallbackData_class(env);
        m = (*env)->GetMethodID(
            env,
            clazz,
            "<init>",
            "(JJJ)V"
        );
    }
    return m;
}

jobject
gnatpolyglot_proxy2java_to_CallbackData(JNIEnv *env,
                                        struct callback_data callback_data) {
    jclass clazz = CallbackData_class(env);
    jmethodID ctor = CallbackData_ctor(env);
    return (*env)->NewObject(
        env,
        clazz,
        ctor,
        (jlong)callback_data.addr,
        (jlong)callback_data.data,
        (jlong)callback_data.extra
    );
}

jfieldID CallbackData_addr(JNIEnv *env) {
    static jfieldID f = NULL;
    if (f == NULL) {
        jclass clazz = CallbackData_class(env);
        f = (*env)->GetFieldID(env, clazz, "addr", "J");
    }
    return f;
}

jfieldID CallbackData_data(JNIEnv *env) {
    static jfieldID f = NULL;
    if (f == NULL) {
        jclass clazz = CallbackData_class(env);
        f = (*env)->GetFieldID(env, clazz, "data", "J");
    }
    return f;
}

jfieldID CallbackData_extra(JNIEnv *env) {
    static jfieldID f = NULL;
    if (f == NULL) {
        jclass clazz = CallbackData_class(env);
        f = (*env)->GetFieldID(env, clazz, "extra", "J");
    }
    return f;
}

struct callback_data
gnatpolyglot_proxy2java_to_callback_data(JNIEnv *env, jobject o) {
    if (o == NULL) {
        return (struct callback_data) { 0 };
    }
    jlong addr = (*env)->GetLongField(
        env, o, CallbackData_addr(env)
    );
    jlong data = (*env)->GetLongField(
        env, o, CallbackData_data(env)
    );
    jlong extra = (*env)->GetLongField(
        env, o, CallbackData_extra(env)
    );
    return (struct callback_data) {
        .addr = (void *) addr,
        .data = (void *) data,
        .extra = (void *) extra,
    };
}
