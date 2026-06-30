<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a Package containing free functions of a module.
--%>
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.Module
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param Module module

@template.text_header()

package ${api.packagePath(module)};

public class ${module.name.getLastName().toPascal()}Package {

@for (var decl : module.declarations)
    @if (decl instanceof FunctionDecl functionDecl && functionDecl.role == null)
        @template.function(api, functionDecl)
    @endif
@endfor

    static {
        ${api.basePackage()}.Library.loadLibrary();
    }

}
