<%--
vim: filetype=jteada
Template file to generate binded functions.
--%>
@import com.adacore.libadalang.Libadalang
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@param AdaAPI api
@param Subprogram subp
@param Boolean isBody

!{String returnType = api.cInterfaceTypename(subp.getReturnType());}

@if(!isBody)
   function ${api.proxyName(subp)}
    @if(!subp.parameters.isEmpty())
      (${api.cInterfaceParameters(subp)})
    @endif
      return ${returnType};
   pragma Export (C, ${api.proxyName(subp)}, "${subp.symbol}");
@else
   function ${api.proxyName(subp)}
    @if(!subp.parameters.isEmpty())
      (${api.cInterfaceParameters(subp)})
    @endif
      return ${returnType} is
    @for(var p : subp.parameters)
      ${api.makeParamConversion(p)};
    @endfor
      ${api.makeReturnDeclarations(subp.getReturnType())}
      Value_Returned : ${subp.getReturnType().pFullyQualifiedName()} :=
         ${api.call(subp)};
   begin
      ${api.makeReturnConversion(subp.getReturnType(), "Value_Returned")};
   end ${api.proxyName(subp)};
@endif
