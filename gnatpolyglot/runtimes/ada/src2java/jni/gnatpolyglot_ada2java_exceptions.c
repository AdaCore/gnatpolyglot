#include "gnatpolyglot_ada2java.h"

extern void *
gnatpolyglot__ada__exceptions__create_exception_occurence(int kind);
extern void *
gnatpolyglot__ada__exceptions__create_exception_occurence_message(
    int kind, struct array_data data);

extern char *gnatpolyglot__ada__exceptions__get_cstr_message(void *data);

extern void gnatpolyglot__ada__exceptions__free_exception_occurence(void *);

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.AdaException
 * Method: createExceptionOccurence
 */
JNIEXPORT jlong
Java_com_adacore_gnatpolyglot_runtime_ada2java_AdaException_createExceptionOccurence(
    JNIEnv *env, jclass c, jint kind) {
    return (jlong) gnatpolyglot__ada__exceptions__create_exception_occurence(kind);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.AdaException
 * Method: createExceptionOccurenceMessage
 */
JNIEXPORT jlong
Java_com_adacore_gnatpolyglot_runtime_ada2java_AdaException_createExceptionOccurenceMessage(
    JNIEnv *env, jclass c, jint kind, jobject message) {
    struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, message);
    return (jlong)
        gnatpolyglot__ada__exceptions__create_exception_occurence_message(kind, data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.AdaException
 * Method: createExceptionOccurenceMessage
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_AdaException_freeException(
    JNIEnv *env, jclass c, jlong addr) {
    gnatpolyglot__ada__exceptions__free_exception_occurence((void *) addr);
}
