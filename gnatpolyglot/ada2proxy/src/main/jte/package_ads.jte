<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate package specs.
--%>
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.gnatpolyglot.ada2proxy.WithCollector
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Array
@import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaDeclaration
@import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaException
@import com.adacore.gnatpolyglot.ada2proxy.proxy.GlobalVariable
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Package
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Record
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Subprogram
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Subtype
@import com.adacore.gnatpolyglot.proxy.FunctionDecl.Overridability
@param AdaAPI api
@param Package pack

!{
   String packageName = pack.getProxyUnitName();
   String parentPackage = pack.getFullyQualifiedName();
}

@template.text_header()

with Interfaces.C;
with System;

with GNATpolyglot.Ada.Arrays;
with GNATpolyglot.Ada.Strings;
@for(var necessaryWith : WithCollector.getIncludes(pack, api))
   with ${necessaryWith};
@endfor

with ${pack.getWithPackage()};
@if(packageName.contains(".System.") || packageName.endsWith(".System"))
   pragma Warnings (Off, ${parentPackage});
@endif

package ${packageName} is

    @if(packageName.contains(".System.") || packageName.endsWith(".System"))
       package System renames Standard.System;
    @endif

@for(AdaDeclaration decl : pack.declarations) <%-- TODO: Handle more declarations --%>
    @if(decl instanceof Subprogram subp && subp.getOverridability() != Overridability.ABSTRACT)
        @if(subp.isProcedure())
            @template.procedure(api, subp, false)
        @else
            @template.function(api, subp, false)
        @endif
    @elseif(decl instanceof Record rec)
        @template.record(api, rec, false)
    @elseif(decl instanceof AdaException exc)
        @template.exception(api, exc, false)
    @elseif(decl instanceof GlobalVariable gv)
        @template.global_var(api, gv, false)
    @elseif(decl instanceof Array array)
        @template.array(api, array)
    @elseif(decl instanceof Subtype subtype)
        @template.subtype(api, subtype)
    @endif
@endfor
end ${packageName};
