@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.AdaDeclaration
@import com.adacore.polyglot.ada2proxy.proxy.Package
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@param Package pack

!{String packageName = pack.getFullyQualifiedName();}

with Interfaces.C;
with ${packageName}; use ${packageName};

package body ${packageName}.Proxy is
@for(AdaDeclaration decl : pack.declarations) <%-- TODO: Handle more declarations --%>
    @if(decl instanceof Subprogram subp)
        @if(subp.isProcedure())
            @template.procedure(subp, true)
        @else
            @template.function(subp, true)
        @endif
    @endif
@endfor
end ${packageName}.Proxy;
