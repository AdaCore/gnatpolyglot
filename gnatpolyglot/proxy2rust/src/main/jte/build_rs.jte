<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate build.rs for a Rust binding crate.
Links against the proxy library via POLYGLOT_PROXY_LIB_DIR / POLYGLOT_PROXY_LIB_NAME.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy2rust.RustAPI

@param RustAPI api
@param Proxy proxy

fn main() {
    if let Ok(dir) = std::env::var("POLYGLOT_PROXY_LIB_DIR") {
        println!("cargo:rustc-link-search=native={}", dir);
    }
    if let Ok(name) = std::env::var("POLYGLOT_PROXY_LIB_NAME") {
        println!("cargo:rustc-link-lib=static={}", name);
    }
    #[cfg(target_os = "linux")]
    {
        println!("cargo:rustc-link-lib=dl");
        println!("cargo:rustc-link-lib=pthread");
    }
    #[cfg(target_os = "windows")]
    {
        // The GNAT runtime (adaint.c) calls Win32 security, ACL and registry
        // APIs (GetFileSecurityW, AccessCheck, RegOpenKeyExA, ...) that live in
        // advapi32, which rustc does not link against by default.
        println!("cargo:rustc-link-lib=advapi32");
    }
}
