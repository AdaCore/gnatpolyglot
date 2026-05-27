#ifndef GNATPOLYGLOT_ADA2JAVA_H
#define GNATPOLYGLOT_ADA2JAVA_H

#include <jni.h>

struct array_data {
    int begin;
    int end;
    void *data;
};

///////////////////////
// ArrayData methods //
///////////////////////

jclass gnatpolyglot_proxy2java_ArrayData_class(JNIEnv *env);
jmethodID gnatpolyglot_proxy2java_ArrayData_ctor(JNIEnv *env);
jfieldID gnatpolyglot_proxy1java_ArrayData_addr(JNIEnv *env);
jfieldID gnatpolyglot_proxy2java_ArrayData_begin(JNIEnv *env);
jfieldID gnatpolyglot_proxy2java_ArrayData_end(JNIEnv *env);

/**
 * Conversion function to convert a Java ArrayData to a C struct array_data.
 */
struct array_data gnatpolyglot_proxy2java_to_array_data(JNIEnv *env, jobject o);

/**
 * Conversion function to create a new Java ArrayData from a C struct
 * array_data.
 */
jobject gnatpolyglot_proxy2java_to_ArrayData(JNIEnv *env,
                                             struct array_data data);

#endif /* ! GNATPOLYGLOT_ADA2JAVA_H */
