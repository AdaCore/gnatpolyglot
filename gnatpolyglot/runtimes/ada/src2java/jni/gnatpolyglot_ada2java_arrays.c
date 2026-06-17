#include "gnatpolyglot_ada2java.h"

////////////////////////////
// CharacterArray methods //
////////////////////////////

extern struct array_data
gnatpolyglot__ada__arrays__native__short_short_array_alloc(int first, int last);

extern void gnatpolyglot__ada__arrays__native__short_short_array_free(void *self);

extern struct array_data
    gnatpolyglot__ada__arrays__native__short_short_array_clone(struct array_data);

extern int *
gnatpolyglot__ada__arrays__native__short_short_array_get(struct array_data data,
                                                 int index);

extern void
gnatpolyglot__ada__arrays__native__short_short_array_set(struct array_data data,
                                                 int index, int new_val);

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.CharacterArray
 * Method: arrayAlloc
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_CharacterArray_arrayAlloc(
    JNIEnv *env, jclass c, jint begin, jint end) {
  struct array_data res =
      gnatpolyglot__ada__arrays__native__short_short_array_alloc(begin, end);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.CharacterArray
 * Method: arrayFree
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_CharacterArray_arrayFree(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__arrays__native__short_short_array_free(&data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.CharacterArray
 * Method: arrayClone
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_CharacterArray_arrayClone(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  struct array_data res =
      gnatpolyglot__ada__arrays__native__short_short_array_clone(data);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.CharacterArray
 * Method: arrayGet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_CharacterArray_arrayGet(
    JNIEnv *env, jclass c, jobject self, jint index) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  return *gnatpolyglot__ada__arrays__native__short_short_array_get(data, index);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.CharacterArray
 * Method: arraySet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_CharacterArray_arraySet(
    JNIEnv *env, jclass c, jobject self, jint index, jint element) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  int prev = *gnatpolyglot__ada__arrays__native__short_short_array_get(data, index);
  gnatpolyglot__ada__arrays__native__short_short_array_set(data, index, element);
  return prev;
}

///////////////////////
// ByteArray methods //
///////////////////////

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ByteArray
 * Method: arrayAlloc
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_ByteArray_arrayAlloc(
    JNIEnv *env, jclass c, jint begin, jint end) {
  struct array_data res =
      gnatpolyglot__ada__arrays__native__short_short_array_alloc(begin, end);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ByteArray
 * Method: arrayFree
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_ByteArray_arrayFree(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__arrays__native__short_short_array_free(&data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ByteArray
 * Method: arrayClone
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_ByteArray_arrayClone(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  struct array_data res =
      gnatpolyglot__ada__arrays__native__short_short_array_clone(data);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ByteArray
 * Method: arrayGet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_ByteArray_arrayGet(
    JNIEnv *env, jclass c, jobject self, jint index) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  return *gnatpolyglot__ada__arrays__native__short_short_array_get(data, index);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ByteArray
 * Method: arraySet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_ByteArray_arraySet(
    JNIEnv *env, jclass c, jobject self, jint index, jint element) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  int prev = *gnatpolyglot__ada__arrays__native__short_short_array_get(data, index);
  gnatpolyglot__ada__arrays__native__short_short_array_set(data, index, element);
  return prev;
}

////////////////////////
// ShortArray methods //
////////////////////////

extern struct array_data
gnatpolyglot__ada__arrays__native__short_array_alloc(int first, int last);

extern void gnatpolyglot__ada__arrays__native__short_array_free(void *self);

extern struct array_data
    gnatpolyglot__ada__arrays__native__short_array_clone(struct array_data);

extern int *
gnatpolyglot__ada__arrays__native__short_array_get(struct array_data data,
                                                 int index);

extern void
gnatpolyglot__ada__arrays__native__short_array_set(struct array_data data,
                                                 int index, int new_val);

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ShortArray
 * Method: arrayAlloc
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_ShortArray_arrayAlloc(
    JNIEnv *env, jclass c, jint begin, jint end) {
  struct array_data res =
      gnatpolyglot__ada__arrays__native__short_array_alloc(begin, end);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ShortArray
 * Method: arrayFree
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_ShortArray_arrayFree(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__arrays__native__short_array_free(&data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ShortArray
 * Method: arrayClone
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_ShortArray_arrayClone(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  struct array_data res =
      gnatpolyglot__ada__arrays__native__short_array_clone(data);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ShortArray
 * Method: arrayGet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_ShortArray_arrayGet(
    JNIEnv *env, jclass c, jobject self, jint index) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  return *gnatpolyglot__ada__arrays__native__short_array_get(data, index);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.ShortArray
 * Method: arraySet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_ShortArray_arraySet(
    JNIEnv *env, jclass c, jobject self, jint index, jint element) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  int prev = *gnatpolyglot__ada__arrays__native__short_array_get(data, index);
  gnatpolyglot__ada__arrays__native__short_array_set(data, index, element);
  return prev;
}

//////////////////////////
// IntegerArray methods //
//////////////////////////

extern struct array_data
gnatpolyglot__ada__arrays__native__int_array_alloc(int first, int last);

extern void gnatpolyglot__ada__arrays__native__int_array_free(void *self);

extern struct array_data
    gnatpolyglot__ada__arrays__native__int_array_clone(struct array_data);

extern int *
gnatpolyglot__ada__arrays__native__int_array_get(struct array_data data,
                                                 int index);

extern void
gnatpolyglot__ada__arrays__native__int_array_set(struct array_data data,
                                                 int index, int new_val);

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray
 * Method: arrayAlloc
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_IntegerArray_arrayAlloc(
    JNIEnv *env, jclass c, jint begin, jint end) {
  struct array_data res =
      gnatpolyglot__ada__arrays__native__int_array_alloc(begin, end);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray
 * Method: arrayFree
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_IntegerArray_arrayFree(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__arrays__native__int_array_free(&data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray
 * Method: arrayClone
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_IntegerArray_arrayClone(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  struct array_data res =
      gnatpolyglot__ada__arrays__native__int_array_clone(data);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray
 * Method: arrayGet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_IntegerArray_arrayGet(
    JNIEnv *env, jclass c, jobject self, jint index) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  return *gnatpolyglot__ada__arrays__native__int_array_get(data, index);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray
 * Method: arraySet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_IntegerArray_arraySet(
    JNIEnv *env, jclass c, jobject self, jint index, jint element) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  int prev = *gnatpolyglot__ada__arrays__native__int_array_get(data, index);
  gnatpolyglot__ada__arrays__native__int_array_set(data, index, element);
  return prev;
}

///////////////////////
// LongArray methods //
///////////////////////

extern struct array_data
gnatpolyglot__ada__arrays__native__long_array_alloc(int first, int last);

extern void gnatpolyglot__ada__arrays__native__long_array_free(void *self);

extern struct array_data
    gnatpolyglot__ada__arrays__native__long_array_clone(struct array_data);

extern int *
gnatpolyglot__ada__arrays__native__long_array_get(struct array_data data,
                                                 int index);

extern void
gnatpolyglot__ada__arrays__native__long_array_set(struct array_data data,
                                                 int index, int new_val);

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.LongArray
 * Method: arrayAlloc
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_LongArray_arrayAlloc(
    JNIEnv *env, jclass c, jint begin, jint end) {
  struct array_data res =
      gnatpolyglot__ada__arrays__native__long_array_alloc(begin, end);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.LongArray
 * Method: arrayFree
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_LongArray_arrayFree(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__arrays__native__long_array_free(&data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.LongArray
 * Method: arrayClone
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_LongArray_arrayClone(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  struct array_data res =
      gnatpolyglot__ada__arrays__native__long_array_clone(data);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.LongArray
 * Method: arrayGet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_LongArray_arrayGet(
    JNIEnv *env, jclass c, jobject self, jint index) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  return *gnatpolyglot__ada__arrays__native__long_array_get(data, index);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.LongArray
 * Method: arraySet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_LongArray_arraySet(
    JNIEnv *env, jclass c, jobject self, jint index, jint element) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  int prev = *gnatpolyglot__ada__arrays__native__long_array_get(data, index);
  gnatpolyglot__ada__arrays__native__long_array_set(data, index, element);
  return prev;
}

////////////////////////
// FloatArray methods //
////////////////////////

extern struct array_data
gnatpolyglot__ada__arrays__native__float_array_alloc(int first, int last);

extern void gnatpolyglot__ada__arrays__native__float_array_free(void *self);

extern struct array_data
    gnatpolyglot__ada__arrays__native__float_array_clone(struct array_data);

extern int *
gnatpolyglot__ada__arrays__native__float_array_get(struct array_data data,
                                                 int index);

extern void
gnatpolyglot__ada__arrays__native__float_array_set(struct array_data data,
                                                 int index, int new_val);

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.FloatArray
 * Method: arrayAlloc
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_FloatArray_arrayAlloc(
    JNIEnv *env, jclass c, jint begin, jint end) {
  struct array_data res =
      gnatpolyglot__ada__arrays__native__float_array_alloc(begin, end);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.FloatArray
 * Method: arrayFree
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_FloatArray_arrayFree(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__arrays__native__float_array_free(&data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.FloatArray
 * Method: arrayClone
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_FloatArray_arrayClone(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  struct array_data res =
      gnatpolyglot__ada__arrays__native__float_array_clone(data);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.FloatArray
 * Method: arrayGet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_FloatArray_arrayGet(
    JNIEnv *env, jclass c, jobject self, jint index) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  return *gnatpolyglot__ada__arrays__native__float_array_get(data, index);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.FloatArray
 * Method: arraySet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_FloatArray_arraySet(
    JNIEnv *env, jclass c, jobject self, jint index, jint element) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  int prev = *gnatpolyglot__ada__arrays__native__float_array_get(data, index);
  gnatpolyglot__ada__arrays__native__float_array_set(data, index, element);
  return prev;
}

/////////////////////////
// DoubleArray methods //
/////////////////////////

extern struct array_data
gnatpolyglot__ada__arrays__native__double_array_alloc(int first, int last);

extern void gnatpolyglot__ada__arrays__native__double_array_free(void *self);

extern struct array_data
    gnatpolyglot__ada__arrays__native__double_array_clone(struct array_data);

extern int *
gnatpolyglot__ada__arrays__native__double_array_get(struct array_data data,
                                                 int index);

extern void
gnatpolyglot__ada__arrays__native__double_array_set(struct array_data data,
                                                 int index, int new_val);

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.DoubleArray
 * Method: arrayAlloc
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_DoubleArray_arrayAlloc(
    JNIEnv *env, jclass c, jint begin, jint end) {
  struct array_data res =
      gnatpolyglot__ada__arrays__native__double_array_alloc(begin, end);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.DoubleArray
 * Method: arrayFree
 */
JNIEXPORT void
Java_com_adacore_gnatpolyglot_runtime_ada2java_DoubleArray_arrayFree(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  gnatpolyglot__ada__arrays__native__double_array_free(&data);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.DoubleArray
 * Method: arrayClone
 */
JNIEXPORT jobject
Java_com_adacore_gnatpolyglot_runtime_ada2java_DoubleArray_arrayClone(
    JNIEnv *env, jclass c, jobject self) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  struct array_data res =
      gnatpolyglot__ada__arrays__native__double_array_clone(data);
  return gnatpolyglot_proxy2java_to_ArrayData(env, res);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.DoubleArray
 * Method: arrayGet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_DoubleArray_arrayGet(
    JNIEnv *env, jclass c, jobject self, jint index) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  return *gnatpolyglot__ada__arrays__native__double_array_get(data, index);
}

/**
 * Java native method implementation
 * Class: com.adacore.gnatpolyglot.runtime.ada2java.DoubleArray
 * Method: arraySet
 */
JNIEXPORT jint
Java_com_adacore_gnatpolyglot_runtime_ada2java_DoubleArray_arraySet(
    JNIEnv *env, jclass c, jobject self, jint index, jint element) {
  struct array_data data = gnatpolyglot_proxy2java_to_array_data(env, self);
  int prev = *gnatpolyglot__ada__arrays__native__double_array_get(data, index);
  gnatpolyglot__ada__arrays__native__double_array_set(data, index, element);
  return prev;
}
