<%--
vim: filetype=jteada
Template file to generate package specs.
--%>
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.WithCollector
@import com.adacore.polyglot.ada2proxy.proxy.Array
@import com.adacore.polyglot.ada2proxy.proxy.AdaDeclaration
@import com.adacore.polyglot.ada2proxy.proxy.AdaException
@import com.adacore.polyglot.ada2proxy.proxy.GlobalVariable
@import com.adacore.polyglot.ada2proxy.proxy.Package
@import com.adacore.polyglot.ada2proxy.proxy.Record
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@import com.adacore.polyglot.proxy.FunctionDecl.Overridability
@param AdaAPI api
@param Package pack

!{
   String packageName = pack.getProxyUnitName();
   String parentPackage = pack.getFullyQualifiedName();
}

with Interfaces.C;
with System;

with Polyglot.Ada.Arrays;
with Polyglot.Ada.Strings;
@for(var necessaryWith : WithCollector.getIncludes(pack))
   with ${necessaryWith};
@endfor

with ${parentPackage};

package ${packageName} is
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
    @endif
@endfor
end ${packageName};
