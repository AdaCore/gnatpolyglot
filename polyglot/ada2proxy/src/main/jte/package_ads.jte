<%--
vim: filetype=jteada
Template file to generate package specs.
--%>
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.AdaDeclaration
@import com.adacore.polyglot.ada2proxy.proxy.Package
@import com.adacore.polyglot.ada2proxy.proxy.Record
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@param Package pack

!{String packageName = pack.getFullyQualifiedName();}

with Interfaces.C;
with System;

package ${packageName}.Proxy is
@for(AdaDeclaration decl : pack.declarations) <%-- TODO: Handle more declarations --%>
    @if(decl instanceof Subprogram subp)
        @if(subp.isProcedure())
            @template.procedure(subp, false)
        @else
            @template.function(subp, false)
        @endif
    @elseif(decl instanceof Record rec)
        @template.record(rec, false)
    @endif
@endfor
end ${packageName}.Proxy;
