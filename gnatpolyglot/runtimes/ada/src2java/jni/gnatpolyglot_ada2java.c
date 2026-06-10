#include "gnatpolyglot_ada2java.h"

///////////////////////
// ArrayData methods //
///////////////////////

jclass gnatpolyglot_proxy2java_ArrayData_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/ArrayData"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_proxy2java_ArrayData_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_proxy2java_ArrayData_class(env);
        m = (*env)->GetMethodID(env, clazz, "<init>", "(IIJ)V");
    }
    return m;
}

jfieldID gnatpolyglot_proxy2java_ArrayData_addr(JNIEnv *env) {
    static jfieldID f = NULL;
    if (f == NULL) {
        jclass clazz = gnatpolyglot_proxy2java_ArrayData_class(env);
        f = (*env)->GetFieldID(env, clazz, "addr", "J");
    }
    return f;
}

jfieldID gnatpolyglot_proxy2java_ArrayData_begin(JNIEnv *env) {
    static jfieldID f = NULL;
    if (f == NULL) {
        jclass clazz = gnatpolyglot_proxy2java_ArrayData_class(env);
        f = (*env)->GetFieldID(env, clazz, "begin", "I");
    }
    return f;
}

jfieldID gnatpolyglot_proxy2java_ArrayData_end(JNIEnv *env) {
    static jfieldID f = NULL;
    if (f == NULL) {
        jclass clazz = gnatpolyglot_proxy2java_ArrayData_class(env);
        f = (*env)->GetFieldID(env, clazz, "end", "I");
    }
    return f;
}

struct array_data gnatpolyglot_proxy2java_to_array_data(JNIEnv *env, jobject o) {
    jlong addr = (*env)->GetLongField(
        env, o, gnatpolyglot_proxy2java_ArrayData_addr(env)
    );
    jint begin = (*env)->GetIntField(
        env, o, gnatpolyglot_proxy2java_ArrayData_begin(env)
    );
    jint end = (*env)->GetIntField(
        env, o, gnatpolyglot_proxy2java_ArrayData_end(env)
    );
    return (struct array_data) {
        .begin = (int) begin,
        .end = (int) end,
        .data = (void*) addr,
    };
}

jobject gnatpolyglot_proxy2java_to_ArrayData(JNIEnv *env, struct array_data data) {
    jclass clazz = gnatpolyglot_proxy2java_ArrayData_class(env);
    jmethodID ctor = gnatpolyglot_proxy2java_ArrayData_ctor(env);
    return (*env)->NewObject(
        env,
        clazz,
        ctor,
        (jint) data.begin,
        (jint) data.end,
        (jlong) data.data
    );
}

/////////////////////////////
// Native Array References //
/////////////////////////////

jclass gnatpolyglot_ada2java_BooleanArray_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/BooleanArray$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_BooleanArray_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_BooleanArray_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}

jclass gnatpolyglot_ada2java_CharacterArray_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/CharacterArray$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_CharacterArray_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_CharacterArray_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}

jclass gnatpolyglot_ada2java_ByteArray_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/ByteArray$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_ByteArray_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_ByteArray_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}

jclass gnatpolyglot_ada2java_ShortArray_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/ShortArray$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_ShortArray_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_ShortArray_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}

jclass gnatpolyglot_ada2java_IntegerArray_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/IntegerArray$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_IntegerArray_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_IntegerArray_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}

jclass gnatpolyglot_ada2java_LongArray_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/LongArray$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_LongArray_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_LongArray_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}

jclass gnatpolyglot_ada2java_FloatArray_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/FloatArray$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_FloatArray_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_FloatArray_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}

jclass gnatpolyglot_ada2java_DoubleArray_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/DoubleArray$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_DoubleArray_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_DoubleArray_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}

jclass gnatpolyglot_ada2java_PolyglotString_Ref_class(JNIEnv *env) {
    static jclass c = NULL;
    if (c == NULL) {
        c = (*env)->NewGlobalRef(
            env,
            (*env)->FindClass(
                env,
                "com/adacore/gnatpolyglot/runtime/ada2java/PolyglotString$Ref"
            )
        );
    }
    return c;
}

jmethodID gnatpolyglot_ada2java_PolyglotString_Ref_ctor(JNIEnv *env) {
    static jmethodID m = NULL;
    if (m == NULL) {
        jclass clazz = gnatpolyglot_ada2java_PolyglotString_Ref_class(env);
        m = (*env)->GetMethodID(
            env, clazz, "<init>",
            "(Lcom/adacore/gnatpolyglot/runtime/ada2java/ArrayData;)V");
    }
    return m;
}
