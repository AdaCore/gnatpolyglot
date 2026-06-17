<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template fragment: a single Rust struct and its impl blocks (inherent methods,
Drop, Clone, Deref/DerefMut), generated from a Proxy class.
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.Module
@import com.adacore.gnatpolyglot.proxy2rust.RustAPI

@param RustAPI api
@param ClassDecl classDecl
@param Module module

!{
    var className = api.rustClassName(classDecl);
    var members = api.getMembers(classDecl);
    var regularAllocs = api.regularAllocs(members, classDecl);
    var cloneAlloc = api.findCloneAlloc(members, classDecl);
    var supportedMembers = api.supportedMethods(members);
    var mutAccessorGetters = api.mutAccessorGetters(supportedMembers);
}

<%-- The opaque pointer is pub(crate) so wrappers can be built and unwrapped from other modules
     of the generated crate (cross-package type references), while staying hidden externally. --%>
#[repr(transparent)]
pub struct ${className}(pub(crate) std::ptr::NonNull<std::ffi::c_void>);

impl ${className} {
@for (int allocIdx = 0; allocIdx < regularAllocs.size(); allocIdx++)
!{ var allocFunc = regularAllocs.get(allocIdx); }
    pub fn ${api.rustConstructorName(allocFunc, allocIdx)}(${api.rustParameters(allocFunc)})
        -> ${className} {
        ${className}(unsafe {
            std::ptr::NonNull::new_unchecked(ffi::${allocFunc.symbol}(
                ${api.callArguments(allocFunc)}
            ) as *mut _)
        })
    }

@endfor

@for (var memberFunc : supportedMembers)
    pub fn ${api.rustFunctionName(memberFunc)}(${api.methodSignatureParams(memberFunc)})
        ${api.rustReturnTypeSuffix(memberFunc)} {
        unsafe { ${api.memberFfiCallExpr(memberFunc)} }
    }

@endfor

@for (var getter : mutAccessorGetters)
    pub fn ${api.rustMutAccessorName(getter)}(&mut self)${api.mutAccessorReturnTypeSuffix(getter)} {
        unsafe { ${api.mutAccessorExpr(getter)} }
    }

@endfor
}

@if (members.freeFunction != null)
impl Drop for ${className} {
    fn drop(&mut self) {
        unsafe { ffi::${members.freeFunction.symbol}(self.0.as_ptr() as *mut std::ffi::c_void) }
    }
}

@endif

@if (cloneAlloc != null)
impl Clone for ${className} {
    fn clone(&self) -> ${className} {
        ${className}(unsafe {
            std::ptr::NonNull::new_unchecked(ffi::${cloneAlloc.symbol}(
                self.0.as_ptr() as *const std::ffi::c_void
            ) as *mut _)
        })
    }
}

@endif

@if (api.hasParent(classDecl))
!{ var parentPath = api.rustParentTypePath(classDecl, module); }
impl std::ops::Deref for ${className} {
    type Target = ${parentPath};
    fn deref(&self) -> &${parentPath} {
        unsafe { &*(self as *const ${className} as *const ${parentPath}) }
    }
}

impl std::ops::DerefMut for ${className} {
    fn deref_mut(&mut self) -> &mut ${parentPath} {
        unsafe { &mut *(self as *mut ${className} as *mut ${parentPath}) }
    }
}

@endif
