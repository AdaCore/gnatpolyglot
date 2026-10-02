<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later

Template file to generate a module-info.java file.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param Proxy proxy

@template.text_header()

module ${api.basePackage()} {
    requires transitive com.adacore.gnatpolyglot.runtime.ada2java;
    exports ${api.basePackage()};
@for (var module : proxy.modules)
    exports ${api.packagePath(module)};
@endfor
}
