#include <jni.h>

#include "gnatpolyglot_ada2java.h"

extern char *gnatpolyglot__ada__strings_to_c_chars_ptr(struct array_data data);
extern struct array_data
gnatpolyglot__ada__strings_from_c_chars_ptr(const char *cstr);
extern void gnatpolyglot__ada__strings_free_c_chars_ptr(char *cstr);

extern void gnatpolyglot__ada__strings__string_free(struct array_data data);

extern void *gnatpolyglot__ada__strings__string_get(struct array_data data,
                                                    int i);

extern void *gnatpolyglot__ada__strings__string_set(struct array_data data,
                                                    int i, char c);

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString
 * Method: fromString
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_PolyglotString_fromString(
    JNIEnv *env, jclass clazz, jstring str) {
  jboolean is_copy;
  const char *jstr = (*env)->GetStringUTFChars(env, str, &is_copy);
  struct array_data data = gnatpolyglot__ada__strings_from_c_chars_ptr(jstr);
  if (is_copy) {
    (*env)->ReleaseStringUTFChars(env, str, jstr);
  }
  return gnatpolyglot_proxy2java_to_ArrayData(env, (struct array_data) data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString
 * Method: stringFree
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_PolyglotString_stringFree(
    JNIEnv *env, jclass clazz, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__strings__string_free(data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString
 * Method: toJavaString
 */
JNIEXPORT jstring
Java_com_adacore_gnatpolyglot_runtime_ada2java_PolyglotString_toJavaString(
    JNIEnv *env, jclass clazz, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  char *cstr = gnatpolyglot__ada__strings_to_c_chars_ptr(data);
  const jstring jstr = (*env)->NewStringUTF(env, cstr);
  gnatpolyglot__ada__strings_free_c_chars_ptr(cstr);
  return jstr;
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString
 * Method: stringGet
 */
JNIEXPORT jchar
Java_com_adacore_gnatpolyglot_runtime_ada2java_PolyglotString_stringGet(
    JNIEnv *env, jclass clazz, jobject self, jint index) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  return *(char *) gnatpolyglot__ada__strings__string_get(data, index);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString
 * Method: stringSet
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_PolyglotString_stringSet(
    JNIEnv *env, jclass clazz, jobject self, jint index, jchar c) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__strings__string_set(data, index, c);
}
