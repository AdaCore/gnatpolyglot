#ifndef GNATPOLYGLOT_PROXY2JAVA_H
#define GNATPOLYGLOT_PROXY2JAVA_H

#include <jni.h>

//////////////////////////////
// Polyglot Kernel bindings //
//////////////////////////////

struct kernel;

struct exception_information {
    int exception_kind;
    char *message;
    void *exception_data;
    void (*clear_exception)(struct kernel *k);
} ;

struct kernel {
    struct exception_information exc_info;
};

extern struct kernel *gnatpolyglot_get_kernel();

jclass PolyglotObject_class(JNIEnv *env);

jmethodID PolyglotObject_internal_release_method(JNIEnv *env);

jmethodID PolyglotObject_internal_clone_method(JNIEnv *env);

jclass PolyglotData_class(JNIEnv *env);

jmethodID PolyglotData_getAddress_method(JNIEnv *env);

jclass PolyglotData_Pointer_class(JNIEnv *env);

jmethodID PolyglotData_Pointer_ctor(JNIEnv *env);

jobject gnatpolyglot_proxy2java_to_Pointer(JNIEnv *env, void *);

jclass ObjectRef_class(JNIEnv *env);

jmethodID ObjectRef_update_method(JNIEnv *env);

jmethodID ObjectRef_getData_method(JNIEnv *env);

#endif /* ! GNATPOLYGLOT_PROXY2JAVA_H */
