<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template for src/arrays.rs: PolyglotArrayElement impls for record element types, wiring the per-type
accessor symbols the proxy emits into the gnatpolyglot.ada.arrays module. Native-element arrays
are handled by the runtime crate and do not appear here. The FFI signatures are hand-shaped to the
actual ABI (descriptor by value, element by pointer), not derived from the proxy IR.
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.Role
@import com.adacore.gnatpolyglot.proxy2rust.RustAPI
@import java.util.List

@param RustAPI api
@param List<ClassDecl> elements

mod ffi {
    #[allow(dead_code)]
    extern "C" {
@for (var element : elements)
        pub fn ${api.arrayAllocFunction(element, false).symbol}(
            first: std::os::raw::c_int,
            last: std::os::raw::c_int,
        ) -> gnatpolyglot_runtime::ada::arrays::ArrayData;
        pub fn ${api.arrayAllocFunction(element, true).symbol}(
            arr: gnatpolyglot_runtime::ada::arrays::ArrayData,
        ) -> gnatpolyglot_runtime::ada::arrays::ArrayData;
        pub fn ${api.getArrayMembers(element).freeFunction.symbol}(
            arr: gnatpolyglot_runtime::ada::arrays::ArrayData,
        );
        pub fn ${api.arrayMemberFunction(element, Role.RoleKind.GETTER).symbol}(
            arr: gnatpolyglot_runtime::ada::arrays::ArrayData,
            index: std::os::raw::c_int,
        ) -> *mut std::ffi::c_void;
        pub fn ${api.arrayMemberFunction(element, Role.RoleKind.SETTER).symbol}(
            arr: gnatpolyglot_runtime::ada::arrays::ArrayData,
            index: std::os::raw::c_int,
            value: *const std::ffi::c_void,
        );
@endfor
    }
}

@for (var element : elements)
!{var elementPath = api.rustTypePath(element.name);}
impl gnatpolyglot_runtime::ada::arrays::PolyglotArrayElement for ${elementPath} {
    type Ref = std::mem::ManuallyDrop<${elementPath}>;

    unsafe fn _ada_array_alloc(begin: std::os::raw::c_int, end: std::os::raw::c_int) -> gnatpolyglot_runtime::ada::arrays::ArrayData {
        ffi::${api.arrayAllocFunction(element, false).symbol}(begin, end)
    }

    unsafe fn _ada_array_free(data: gnatpolyglot_runtime::ada::arrays::ArrayData) {
        ffi::${api.getArrayMembers(element).freeFunction.symbol}(data)
    }

    unsafe fn _ada_array_clone(data: gnatpolyglot_runtime::ada::arrays::ArrayData) -> gnatpolyglot_runtime::ada::arrays::ArrayData {
        ffi::${api.arrayAllocFunction(element, true).symbol}(data)
    }

    unsafe fn _ada_array_get(data: gnatpolyglot_runtime::ada::arrays::ArrayData, index: std::os::raw::c_int) -> Self::Ref {
        std::mem::ManuallyDrop::new(${elementPath}(std::ptr::NonNull::new_unchecked(
            ffi::${api.arrayMemberFunction(element, Role.RoleKind.GETTER).symbol}(data, index)
                as *mut std::ffi::c_void,
        )))
    }

    unsafe fn _ada_array_set(data: gnatpolyglot_runtime::ada::arrays::ArrayData, index: std::os::raw::c_int, value: ${elementPath}) {
        ffi::${api.arrayMemberFunction(element, Role.RoleKind.SETTER).symbol}(
            data,
            index,
            value.0.as_ptr() as *const std::ffi::c_void,
        );
    }
}

@endfor
