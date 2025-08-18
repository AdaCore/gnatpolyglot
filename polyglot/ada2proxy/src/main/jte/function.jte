<%--
vim: filetype=jteada
Template file to generate binded functions.
--%>
@import com.adacore.libadalang.Libadalang;
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@param Subprogram subp
@param Boolean isBody

!{String returnType = AdaAPI.cInterfaceTypename(subp.getReturnType());}

@if(!isBody)
   function ${AdaAPI.proxyName(subp)}
    @if(!subp.parameters.isEmpty())
      (${AdaAPI.cInterfaceParameters(subp)})
    @endif
      return ${returnType};
   pragma Export (C, ${AdaAPI.proxyName(subp)}, "${subp.symbol}");
@else
   function ${AdaAPI.proxyName(subp)}
    @if(!subp.parameters.isEmpty())
      (${AdaAPI.cInterfaceParameters(subp)})
    @endif
      return ${returnType} is
    @for(var p : subp.parameters)
      ${AdaAPI.makeParamConversion(p)};
    @endfor
      ${AdaAPI.makeReturnDeclarations(subp.getReturnType())}
      Value_Returned : ${subp.getReturnType().pFullyQualifiedName()} :=
         ${AdaAPI.call(subp)};
   begin
      ${AdaAPI.makeReturnConversion(subp.getReturnType(), "Value_Returned")};
   end ${AdaAPI.proxyName(subp)};
@endif
