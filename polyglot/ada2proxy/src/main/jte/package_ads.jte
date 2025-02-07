@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.proxy.Declaration
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Module
@import com.adacore.polyglot.proxy.Proxy
@param Module module

!{String packageName = AdaAPI.makeFullyQualifiedName(module.parent, module.name);}

with Interfaces.C;

package ${packageName}.Proxy is
@for(Declaration decl : module.declarations) <%-- TODO: Handle more declarations --%>
    @if(decl instanceof FunctionDecl funDecl)
        @template.procedure(funDecl, false)
    @endif
@endfor
end ${packageName}.Proxy;
