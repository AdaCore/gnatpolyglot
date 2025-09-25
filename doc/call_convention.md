# ABI

Every function must use the C ABI as a common interface.

# Argument passing

## Scalars

Scalars are binded 1-to-1

| Type                   | Convention                     | C++ Type     | Internal C type |
| ---------------------- | ------------------------------ | ------------ | --------------- |
| Scalar                 | Copy                           | `int`        | `int`           |
| Ref { Scalar }         | Address to the scalar          | `int &`      | `int *`         |
| Ptr { Scalar }         | Copy of the address            | `ptr<int>`   | `int *`         |
| Ref { Ptr { Scalar } } | Address to copy of the pointer | `ptr<int> &` | `int **`        |

## Classes

Classes are binded as class wrappers around a dynamically allocated internal object from the source

| Type                  | Convention                                              | C++ Type        | Internal C type |
| --------------------- | ------------------------------------------------------- | --------------- | --------------- |
| Class                 | Address of the _internal_ object                        | `const class &` | `void *`        |
| Ref { Class }         | Address of the _internal_ object                        | `class &`       | `void *`        |
| Ptr { Class }         | Address of the _internal_ object                        | `ptr<class>`    | `void *`        |
| Ref { Ptr { Class } } | Address to copy of the pointer to the _internal_ object | `ptr<class> &`  | `void **`       |

## Strings and arrays

Strings and arrays have an inlined structure holding informations about the array: address, bounds...

| Type                  | Convention                        | C++ Type           | Internal C type |
| --------------------- | --------------------------------- | ------------------ | --------------- |
| Array                 | Copy of the array data            | `const array<T> &` | `array_data`    |
| Ptr { Array }         | Copy of the array data            | `ptr<array<T>>`    | `array_data`    |
| Ref { Array }         | Copy of the array data            | `array<T> &`       | `array_data`    |
| Ref { Ptr { Array } } | Address to copy of the array data | `ptr<class> &`     | `array_data *`  |

## Pointers

Pointers to pointers are not supported

## References

References cannot be contained inside other type (`Ptr { Ref {} }`) as they are implementation defined.

# Returning values

## Scalars

| Type           | Convention            | C++ Type   | Internal C type |
| -------------- | --------------------- | ---------- | --------------- |
| Scalar         | Copy                  | `int`      | `int`           |
| Ref { Scalar } | Address to the scalar | `int &`    | `int *`         |
| Ptr { Scalar } | Copy of the address   | `ptr<int>` | `int *`         |

## Classes

Classes (and arrays) use a `view` type to return them as a reference in order to avoid returning an object whose lifetime depends on the stack.
View types can be copied and hold no ownership on the pointer to the internal data. They do not free _anything_ when they are destroyed.

| Type          | Convention                       | C++ Type      | Internal C type |
| ------------- | -------------------------------- | ------------- | --------------- |
| Class         | Address of the _internal_ object | `class`       | `void *`        |
| Ptr { Class } | Address of the _internal_ object | `ptr<class>`  | `void *`        |
| Ref { Class } | Address of the _internal_ object | `view<class>` | `void *`        |

## Strings and arrays

| Type          | Convention             | C++ Type           | Internal C type |
| ------------- | ---------------------- | ------------------ | --------------- |
| Array         | Copy of the array data | `const array<T> &` | `array_data`    |
| Ptr { Array } | Copy of the array data | `ptr<array<T>>`    | `array_data`    |
| Ref { Array } | Copy of the array data | `view<array<T>>`   | `array_data`    |

## Ref to Pointers

Cannot be returned.
