<%--
vim: filetype=jteada
Template file to generate package bodies.
--%>
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.WithCollector
@import com.adacore.polyglot.ada2proxy.proxy.AdaDeclaration
@import com.adacore.polyglot.ada2proxy.proxy.AdaException
@import com.adacore.polyglot.ada2proxy.proxy.GlobalVariable
@import com.adacore.polyglot.ada2proxy.proxy.Package
@import com.adacore.polyglot.ada2proxy.proxy.Record
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@param AdaAPI api
@param Package pack

!{
   String packageName = pack.getProxyUnitName();
   String parentPackage = pack.getFullyQualifiedName();
}

with Ada.Exceptions;
with Ada.Finalization;
with Ada.Unchecked_Conversion;
with Ada.Unchecked_Deallocation;
with Interfaces.C;
use all type Interfaces.C.int;
with System;
with System.Address_To_Access_Conversions;
with System.Storage_Elements;

with Polyglot;
with Polyglot.Ada;
with Polyglot.Ada.Arrays;
with Polyglot.Exceptions;
with Polyglot.Exceptions.${api.getProjectName().toPascalWithUnderscore()};
with Polyglot.Ada.Strings;

with ${parentPackage}; use ${parentPackage};

@for(var necessaryWith : WithCollector.getIncludes(pack))
   with ${necessaryWith}; use ${necessaryWith};
@endfor

package body ${packageName} is
@for(AdaDeclaration decl : pack.declarations) <%-- TODO: Handle more declarations --%>
    @if(decl instanceof Subprogram subp)
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
    @endif
@endfor
end ${packageName};
