<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate package bodies.
--%>
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.gnatpolyglot.ada2proxy.WithCollector
@import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaDeclaration
@import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaException
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Callback
@import com.adacore.gnatpolyglot.ada2proxy.proxy.GlobalVariable
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Package
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Record
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Subprogram
@import com.adacore.gnatpolyglot.proxy.FunctionDecl.Overridability
@param AdaAPI api
@param Package pack

!{
   String packageName = pack.getProxyUnitName();
}

@template.text_header()

with Ada.Exceptions;
with Ada.Finalization;
with Ada.Tags;
with Ada.Unchecked_Conversion;
with Ada.Unchecked_Deallocation;
with Interfaces;
use all type Interfaces.Integer_32;
with Interfaces.C;
use all type Interfaces.C.int;
with System;
with System.Address_To_Access_Conversions;
with System.Storage_Elements;

with GNATpolyglot;
with GNATpolyglot.Ada;
with GNATpolyglot.Ada.Arrays;
with GNATpolyglot.Exceptions;
with GNATpolyglot.Exceptions.${api.getProjectName().toPascalWithUnderscore()};
with GNATpolyglot.Ada.Strings;

with ${pack.getWithPackage()};

@for(var necessaryWith : WithCollector.getIncludes(pack, api))
   with ${necessaryWith}; use ${necessaryWith};
@endfor

package body ${packageName} is
@for(AdaDeclaration decl : pack.declarations) <%-- TODO: Handle more declarations --%>
    @if(decl instanceof Subprogram subp && subp.getOverridability() != Overridability.ABSTRACT)
        @if(subp.isProcedure())
            @template.procedure(api, subp, true)
        @else
            @template.function(api, subp, true)
        @endif
    @elseif(decl instanceof Record rec)
        @template.record(api, rec, true)
    @elseif(decl instanceof AdaException exc)
        @template.exception(api, exc, true)
    @elseif(decl instanceof GlobalVariable gv)
        @template.global_var(api, gv, true)
    @elseif(decl instanceof Callback callback)
        @template.callback(api, callback, true)
    @endif
@endfor
end ${packageName};
