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
   function ${AdaAPI.proxyName(subp.name)}
    @if(!subp.parameters.isEmpty())
      (${AdaAPI.cInterfaceParameters(subp)})
    @endif
      return ${returnType};
   pragma Export (C, ${AdaAPI.proxyName(subp.name)}, "${subp.symbol}");
@else
   function ${AdaAPI.proxyName(subp.name)}
    @if(!subp.parameters.isEmpty())
      (${AdaAPI.cInterfaceParameters(subp)})
    @endif
      return ${returnType} is
    @for(var p : subp.parameters)
      ${AdaAPI.makeParamConversion(p)};
    @endfor
      ${AdaAPI.makeReturnTypeConverter(subp.getReturnType())}
   begin
      return ${AdaAPI.makeReturnConversion(subp.getReturnType(), AdaAPI.call(subp))};
   end ${AdaAPI.proxyName(subp.name)};
@endif
