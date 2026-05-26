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
            "release",
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
            "internalClone",
            "(J)Ljava/lang/Object;"
        );
    }
    return m;
}
