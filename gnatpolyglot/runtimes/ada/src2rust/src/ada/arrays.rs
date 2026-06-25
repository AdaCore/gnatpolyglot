//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

//! Ada array support. Contains the shared `ArrayData` ABI descriptor, the
//! `PolyglotArray<T>` owned wrapper, and the `PolyglotArrayElement` trait binding
//! the per-element-type FFI entry points.

use std::marker::PhantomData;
use std::os::raw::{c_int, c_void};

/// The C ABI descriptor every Ada array and string crosses the FFI boundary as:
/// `struct array_data { int begin; int end; void *data; }`. The bounds are
/// Ada-style (1-based, inclusive); `data` points at the element storage. The
/// struct is a plain handle and frees nothing itself — the owning wrapper
/// (`PolyglotArray<T>`, `PolyglotString`) is responsible for the lifecycle.
#[repr(C)]
#[derive(Clone, Copy)]
pub struct ArrayData {
    pub begin: c_int,
    pub end: c_int,
    pub data: *mut c_void,
}

/// An element type that can be stored in a [`PolyglotArray`]. Each implementation binds the Ada
/// runtime's allocate / free / clone / element-access entry points for that element type. The
/// runtime implements it for the native integer widths below; generated bindings implement it for
/// record element types, wiring the per-type accessor symbols emitted into the proxy.
///
/// This is FFI plumbing, not part of the user-facing surface: client code drives arrays through
/// [`PolyglotArray`]'s inherent methods (`get`/`set`/`new`/...), never these entry points. Hidden
/// from the docs accordingly.
#[doc(hidden)]
pub trait PolyglotArrayElement: Sized {
    /// What reading an element yields: a copy for a scalar element, a non-owning view for a record
    /// element (the view borrows the array's storage and must not outlive it).
    type Ref;

    /// Allocate a fresh array with Ada bounds `begin ..= end`.
    ///
    /// # Safety
    /// Calls into the Ada runtime; the returned descriptor owns freshly allocated storage.
    unsafe fn _ada_array_alloc(begin: c_int, end: c_int) -> ArrayData;

    /// Free the storage described by `data`.
    ///
    /// # Safety
    /// `data` must describe a live array allocated by the matching element type.
    unsafe fn _ada_array_free(data: ArrayData);

    /// Allocate a deep copy of the array described by `data`.
    ///
    /// # Safety
    /// `data` must describe a live array allocated by the matching element type.
    unsafe fn _ada_array_clone(data: ArrayData) -> ArrayData;

    /// Read the element at Ada index `index`.
    ///
    /// # Safety
    /// `data` must be live and `index` within `begin ..= end`.
    unsafe fn _ada_array_get(data: ArrayData, index: c_int) -> Self::Ref;

    /// Overwrite the element at Ada index `index` with `value`.
    ///
    /// # Safety
    /// `data` must be live and `index` within `begin ..= end`.
    unsafe fn _ada_array_set(data: ArrayData, index: c_int, value: Self);
}

/// An owned Ada array that frees its backing storage on drop. Generic over the element type, which
/// supplies the FFI entry points through [`PolyglotArrayElement`]. Indexing uses Ada-style (1-based,
/// arbitrary lower bound) indices, matching the bounds the Ada side chose.
pub struct PolyglotArray<T: PolyglotArrayElement> {
    data: ArrayData,
    // `T` never appears in a concrete field -- the elements live behind the untyped `*mut c_void`
    // in `data`, and `T` only selects which FFI entry points to call. This marker ties `T` to the
    // struct so the compiler accepts the otherwise-unused parameter, and the `PhantomData<T>`
    // flavor (rather than `PhantomData<*const T>`) makes it behave as if it *owns* `T` values:
    // correct for drop-check (Drop frees the elements), variance, and Send/Sync inference.
    _marker: PhantomData<T>,
}

impl<T: PolyglotArrayElement> PolyglotArray<T> {
    /// Take ownership of a raw `array_data` returned by the library.
    ///
    /// # Safety
    /// `data` must describe a live array of `T` whose ownership is being transferred to the
    /// caller; it will be freed when the `PolyglotArray` drops.
    pub unsafe fn from_raw(data: ArrayData) -> PolyglotArray<T> {
        PolyglotArray { data, _marker: PhantomData }
    }

    /// Allocate a new array with Ada bounds `begin ..= end`.
    pub fn new(begin: i32, end: i32) -> PolyglotArray<T> {
        unsafe { PolyglotArray::from_raw(T::_ada_array_alloc(begin, end)) }
    }

    /// The raw `array_data` descriptor to hand to the FFI layer. `Copy`, and non-owning: the
    /// underlying buffer is shared, so a callee can mutate the elements in place.
    pub fn raw(&self) -> ArrayData {
        self.data
    }

    /// The first valid (Ada) index.
    pub fn begin(&self) -> i32 {
        self.data.begin
    }

    /// The last valid (Ada) index.
    pub fn end(&self) -> i32 {
        self.data.end
    }

    /// The number of elements.
    pub fn len(&self) -> i32 {
        self.data.end - self.data.begin + 1
    }

    /// Whether the array has no elements.
    pub fn is_empty(&self) -> bool {
        self.len() <= 0
    }

    /// Read the element at Ada index `index`: a copy for a scalar element, a non-owning view for a
    /// record element.
    pub fn get(&self, index: i32) -> T::Ref {
        unsafe { T::_ada_array_get(self.data, index) }
    }

    /// Overwrite the element at Ada index `index`. For a record element the value is copied into
    /// the array and the passed wrapper is consumed (and freed).
    pub fn set(&mut self, index: i32, value: T) {
        unsafe { T::_ada_array_set(self.data, index, value) }
    }
}

impl<T: PolyglotArrayElement> Drop for PolyglotArray<T> {
    fn drop(&mut self) {
        unsafe { T::_ada_array_free(self.data) }
    }
}

impl<T: PolyglotArrayElement> Clone for PolyglotArray<T> {
    fn clone(&self) -> PolyglotArray<T> {
        unsafe { PolyglotArray::from_raw(T::_ada_array_clone(self.data)) }
    }
}

// Bind the fixed entry points the Ada runtime exports for native scalar arrays. The symbol suffix
// is the Ada type name (short_short = 8-bit int, short = 16-bit int, ..., float = 32-bit float,
// double = 64-bit float). Reads borrow the element pointer the getter returns; writes go through
// the setter, the same entry points the record-element impls (arrays.jte) and the other backends
// use. The extern declarations are function-local so each element type keeps its own correctly
// typed signatures.
macro_rules! impl_native_array_element {
    ($elem:ty, $alloc:ident, $free:ident, $clone:ident, $get:ident, $set:ident) => {
        impl PolyglotArrayElement for $elem {
            type Ref = $elem;

            unsafe fn _ada_array_alloc(begin: c_int, end: c_int) -> ArrayData {
                extern "C" {
                    fn $alloc(begin: c_int, end: c_int) -> ArrayData;
                }
                $alloc(begin, end)
            }

            unsafe fn _ada_array_free(data: ArrayData) {
                extern "C" {
                    fn $free(data: ArrayData);
                }
                $free(data)
            }

            unsafe fn _ada_array_clone(data: ArrayData) -> ArrayData {
                extern "C" {
                    fn $clone(data: ArrayData) -> ArrayData;
                }
                $clone(data)
            }

            unsafe fn _ada_array_get(data: ArrayData, index: c_int) -> $elem {
                extern "C" {
                    fn $get(data: ArrayData, index: c_int) -> *mut $elem;
                }
                *$get(data, index)
            }

            unsafe fn _ada_array_set(data: ArrayData, index: c_int, value: $elem) {
                extern "C" {
                    fn $set(data: ArrayData, index: c_int, value: $elem);
                }
                $set(data, index, value)
            }
        }
    };
}

impl_native_array_element!(
    i8,
    gnatpolyglot__ada__arrays__native__short_short_array_alloc,
    gnatpolyglot__ada__arrays__native__short_short_array_free,
    gnatpolyglot__ada__arrays__native__short_short_array_clone,
    gnatpolyglot__ada__arrays__native__short_short_array_get,
    gnatpolyglot__ada__arrays__native__short_short_array_set
);
impl_native_array_element!(
    u8,
    gnatpolyglot__ada__arrays__native__unsigned_short_short_array_alloc,
    gnatpolyglot__ada__arrays__native__unsigned_short_short_array_free,
    gnatpolyglot__ada__arrays__native__unsigned_short_short_array_clone,
    gnatpolyglot__ada__arrays__native__unsigned_short_short_array_get,
    gnatpolyglot__ada__arrays__native__unsigned_short_short_array_set
);
impl_native_array_element!(
    i16,
    gnatpolyglot__ada__arrays__native__short_array_alloc,
    gnatpolyglot__ada__arrays__native__short_array_free,
    gnatpolyglot__ada__arrays__native__short_array_clone,
    gnatpolyglot__ada__arrays__native__short_array_get,
    gnatpolyglot__ada__arrays__native__short_array_set
);
impl_native_array_element!(
    u16,
    gnatpolyglot__ada__arrays__native__unsigned_short_array_alloc,
    gnatpolyglot__ada__arrays__native__unsigned_short_array_free,
    gnatpolyglot__ada__arrays__native__unsigned_short_array_clone,
    gnatpolyglot__ada__arrays__native__unsigned_short_array_get,
    gnatpolyglot__ada__arrays__native__unsigned_short_array_set
);
impl_native_array_element!(
    i32,
    gnatpolyglot__ada__arrays__native__int_array_alloc,
    gnatpolyglot__ada__arrays__native__int_array_free,
    gnatpolyglot__ada__arrays__native__int_array_clone,
    gnatpolyglot__ada__arrays__native__int_array_get,
    gnatpolyglot__ada__arrays__native__int_array_set
);
impl_native_array_element!(
    u32,
    gnatpolyglot__ada__arrays__native__unsigned_int_array_alloc,
    gnatpolyglot__ada__arrays__native__unsigned_int_array_free,
    gnatpolyglot__ada__arrays__native__unsigned_int_array_clone,
    gnatpolyglot__ada__arrays__native__unsigned_int_array_get,
    gnatpolyglot__ada__arrays__native__unsigned_int_array_set
);
impl_native_array_element!(
    i64,
    gnatpolyglot__ada__arrays__native__long_array_alloc,
    gnatpolyglot__ada__arrays__native__long_array_free,
    gnatpolyglot__ada__arrays__native__long_array_clone,
    gnatpolyglot__ada__arrays__native__long_array_get,
    gnatpolyglot__ada__arrays__native__long_array_set
);
impl_native_array_element!(
    u64,
    gnatpolyglot__ada__arrays__native__unsigned_long_array_alloc,
    gnatpolyglot__ada__arrays__native__unsigned_long_array_free,
    gnatpolyglot__ada__arrays__native__unsigned_long_array_clone,
    gnatpolyglot__ada__arrays__native__unsigned_long_array_get,
    gnatpolyglot__ada__arrays__native__unsigned_long_array_set
);
impl_native_array_element!(
    f32,
    gnatpolyglot__ada__arrays__native__float_array_alloc,
    gnatpolyglot__ada__arrays__native__float_array_free,
    gnatpolyglot__ada__arrays__native__float_array_clone,
    gnatpolyglot__ada__arrays__native__float_array_get,
    gnatpolyglot__ada__arrays__native__float_array_set
);
impl_native_array_element!(
    f64,
    gnatpolyglot__ada__arrays__native__double_array_alloc,
    gnatpolyglot__ada__arrays__native__double_array_free,
    gnatpolyglot__ada__arrays__native__double_array_clone,
    gnatpolyglot__ada__arrays__native__double_array_get,
    gnatpolyglot__ada__arrays__native__double_array_set
);

// A boolean element cannot go through the macro: the Ada runtime stores it as a 0/1 byte, and
// reading an arbitrary byte as a Rust `bool` (whose only valid bit patterns are 0 and 1) would be
// undefined behavior. So the getter reads the byte and converts it to a `bool`, and the setter
// passes the value back as a `u8` (the `char`-sized boolean ABI the runtime exports).
impl PolyglotArrayElement for bool {
    type Ref = bool;

    unsafe fn _ada_array_alloc(begin: c_int, end: c_int) -> ArrayData {
        extern "C" {
            fn gnatpolyglot__ada__arrays__native__boolean_array_alloc(
                begin: c_int,
                end: c_int,
            ) -> ArrayData;
        }
        gnatpolyglot__ada__arrays__native__boolean_array_alloc(begin, end)
    }

    unsafe fn _ada_array_free(data: ArrayData) {
        extern "C" {
            fn gnatpolyglot__ada__arrays__native__boolean_array_free(data: ArrayData);
        }
        gnatpolyglot__ada__arrays__native__boolean_array_free(data)
    }

    unsafe fn _ada_array_clone(data: ArrayData) -> ArrayData {
        extern "C" {
            fn gnatpolyglot__ada__arrays__native__boolean_array_clone(data: ArrayData)
                -> ArrayData;
        }
        gnatpolyglot__ada__arrays__native__boolean_array_clone(data)
    }

    unsafe fn _ada_array_get(data: ArrayData, index: c_int) -> bool {
        extern "C" {
            fn gnatpolyglot__ada__arrays__native__boolean_array_get(
                data: ArrayData,
                index: c_int,
            ) -> *mut u8;
        }
        *gnatpolyglot__ada__arrays__native__boolean_array_get(data, index) != 0
    }

    unsafe fn _ada_array_set(data: ArrayData, index: c_int, value: bool) {
        extern "C" {
            fn gnatpolyglot__ada__arrays__native__boolean_array_set(
                data: ArrayData,
                index: c_int,
                value: u8,
            );
        }
        gnatpolyglot__ada__arrays__native__boolean_array_set(data, index, value as u8);
    }
}
