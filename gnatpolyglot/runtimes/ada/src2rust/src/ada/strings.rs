//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

//! Ada string support. Mirrors the C++ `gnatpolyglot::ada::strings` namespace:
//! `PolyglotString` (owned) and its borrowed view `PolyglotStr`, both carried
//! by the same `ArrayData` descriptor as arrays (`string_data` in C++).

use crate::ada::arrays::ArrayData;
use std::ffi::{CStr, CString};
use std::fmt;
use std::ops::Deref;
use std::os::raw::c_char;

// Ada-side runtime entry points. These are exported by the `gnatpolyglot-ada`
// runtime, which is bundled into the proxy library the generated crate links
// against, so the symbols resolve at the final link of the dependent binary.
extern "C" {
    fn gnatpolyglot__ada__strings_from_c_chars_ptr(ptr: *const c_char) -> ArrayData;
    fn gnatpolyglot__ada__strings_to_c_chars_ptr(data: ArrayData) -> *mut c_char;
    fn gnatpolyglot__ada__strings_free_c_chars_ptr(ptr: *mut c_char);
    fn gnatpolyglot__ada__strings__string_free(data: ArrayData);
}

/// A borrowed Ada string: a non-owning view over the underlying `array_data`.
/// Analogous to [`str`] — methods that only read a string take `&PolyglotStr`,
/// and an owned [`PolyglotString`] derefs to it.
#[repr(transparent)]
pub struct PolyglotStr(ArrayData);

impl PolyglotStr {
    /// The raw `array_data` to hand to the FFI layer. `Copy`, and non-owning:
    /// passing it does not transfer ownership of the Ada storage.
    pub fn raw(&self) -> ArrayData {
        self.0
    }
}

impl fmt::Display for PolyglotStr {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        // Round-trip through the Ada conversion helpers, which yield a freshly
        // allocated NUL-terminated copy we own and must free. Bytes that are
        // not valid UTF-8 are replaced (Ada strings are not UTF-8 guaranteed).
        unsafe {
            let ptr = gnatpolyglot__ada__strings_to_c_chars_ptr(self.0);
            let result = f.write_str(&CStr::from_ptr(ptr).to_string_lossy());
            gnatpolyglot__ada__strings_free_c_chars_ptr(ptr);
            result
        }
    }
}

/// An owned Ada string that frees its backing storage on drop. Analogous to
/// [`String`]. Construct one from a Rust `&str` with [`PolyglotString::from`],
/// or receive one from a generated binding that returns a string.
#[repr(transparent)]
pub struct PolyglotString(ArrayData);

impl PolyglotString {
    /// Take ownership of a raw `array_data` returned by the library.
    ///
    /// # Safety
    /// `data` must describe a live Ada string whose ownership is being
    /// transferred to the caller; it will be freed when the `PolyglotString` drops.
    pub unsafe fn from_raw(data: ArrayData) -> PolyglotString {
        PolyglotString(data)
    }
}

impl From<&str> for PolyglotString {
    fn from(value: &str) -> PolyglotString {
        // The Ada side copies the bytes, so the temporary CString is free to
        // drop afterwards. Embedded NUL bytes are rejected (documented limit of
        // the C-string conversion path).
        let c = CString::new(value).expect("PolyglotString cannot contain a NUL byte");
        unsafe { PolyglotString(gnatpolyglot__ada__strings_from_c_chars_ptr(c.as_ptr())) }
    }
}

impl Deref for PolyglotString {
    type Target = PolyglotStr;

    fn deref(&self) -> &PolyglotStr {
        // `PolyglotStr` and `PolyglotString` are both `#[repr(transparent)]`
        // over `ArrayData`, so reborrowing as the view type is a
        // layout-preserving no-op. The borrow ties the view's lifetime to `self`.
        unsafe { &*(self as *const PolyglotString as *const PolyglotStr) }
    }
}

impl fmt::Display for PolyglotString {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&**self, f)
    }
}

impl Drop for PolyglotString {
    fn drop(&mut self) {
        unsafe { gnatpolyglot__ada__strings__string_free(self.0) }
    }
}
