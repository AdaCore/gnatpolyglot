//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

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

struct callback_data {
    void *addr;
    void *data;
    void *extra;
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

jmethodID ObjectRef_set_method(JNIEnv *env);

jmethodID ObjectRef_getData_method(JNIEnv *env);

jclass FunctionRef_class(JNIEnv *env);

jmethodID FunctionRef_get_method(JNIEnv *env);

jmethodID FunctionRef_set_method(JNIEnv *env);

jclass CallbackData_class(JNIEnv *env);

jmethodID CallbackData_ctor(JNIEnv *env);

jobject
gnatpolyglot_proxy2java_to_CallbackData(JNIEnv *env,
                                        struct callback_data callback_data);

struct callback_data
gnatpolyglot_proxy2java_to_callback_data(JNIEnv *env, jobject o);

#endif /* ! GNATPOLYGLOT_PROXY2JAVA_H */
