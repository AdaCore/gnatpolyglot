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
