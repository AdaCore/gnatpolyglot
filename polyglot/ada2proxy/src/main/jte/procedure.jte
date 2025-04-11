<%--
vim: filetype=jteada
Template file to generate binded procedures.
--%>
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@param Subprogram subp
@param Boolean isBody

@if(!isBody)
   procedure ${AdaAPI.proxyName(subp.name)}
    @if(!subp.parameters.isEmpty())
      (${AdaAPI.cInterfaceParameters(subp)})
    @endif
    ;
   pragma Export (C, ${AdaAPI.proxyName(subp.name)}, "${subp.symbol}");
@else
   procedure ${AdaAPI.proxyName(subp.name)}
    @if(!subp.parameters.isEmpty())
      (${AdaAPI.cInterfaceParameters(subp)})
    @endif
   is
    @for(var p : subp.parameters)
      ${AdaAPI.makeParamConversion(p)};
    @endfor
   begin
      ${AdaAPI.call(subp)};
   end ${AdaAPI.proxyName(subp.name)};
@endif
