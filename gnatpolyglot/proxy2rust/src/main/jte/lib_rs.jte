<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate the root lib.rs for a Rust binding crate.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy2rust.RustAPI

@param RustAPI api
@param Proxy proxy

!{
    var userModules = proxy.modules.stream()
            .filter(m -> !api.isGnatpolyglotModule(m))
            .toList();
}

@for (var module : userModules)
pub mod ${api.rustModuleName(module)};
@endfor
