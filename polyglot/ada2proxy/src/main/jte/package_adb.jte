<%--
vim: filetype=jteada
Template file to generate package bodies.
--%>
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.AdaDeclaration
@import com.adacore.polyglot.ada2proxy.proxy.Package
@import com.adacore.polyglot.ada2proxy.proxy.Record
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@param Package pack

!{String packageName = pack.getFullyQualifiedName();}

with Ada.Unchecked_Conversion;
with Ada.Unchecked_Deallocation;
with Interfaces.C;
with System;
with System.Address_To_Access_Conversions;

with Polyglot.Ada.Arrays;

with ${packageName}; use ${packageName};

package body ${packageName}.Proxy is
@for(AdaDeclaration decl : pack.declarations) <%-- TODO: Handle more declarations --%>
    @if(decl instanceof Subprogram subp)
        @if(subp.isProcedure())
            @template.procedure(subp, true)
        @else
            @template.function(subp, true)
        @endif
    @elseif(decl instanceof Record rec)
        @template.record(rec, true)
    @endif
@endfor
end ${packageName}.Proxy;
