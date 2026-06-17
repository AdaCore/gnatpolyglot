<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template fragment: the `ffi` module declaring the extern "C" proxy entry points.
--%>
@import com.adacore.gnatpolyglot.proxy.Module
@import com.adacore.gnatpolyglot.proxy2rust.RustAPI

@param RustAPI api
@param Module module

mod ffi {
    #[allow(dead_code)]
    extern "C" {
@for (var func : api.allFfiFunctions(module))
        pub fn ${func.symbol}(${api.ffiParameters(func)})${api.ffiReturnTypeSuffix(func)};
@endfor
    }
}
