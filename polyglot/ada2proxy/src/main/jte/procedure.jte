<%--
vim: filetype=jteada
Template file to generate binded procedures.
--%>
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@param AdaAPI api
@param Subprogram subp
@param Boolean isBody

@if(!isBody)
   procedure ${api.proxyName(subp)}
    @if(!subp.parameters.isEmpty())
      (${api.cInterfaceParameters(subp)})
    @endif
    ;
   pragma Export (C, ${api.proxyName(subp)}, "${subp.symbol}");
@else
   procedure ${api.proxyName(subp)}
    @if(!subp.parameters.isEmpty())
      (${api.cInterfaceParameters(subp)})
    @endif
   is
    @for(var p : subp.parameters)
      ${api.makeParamConversion(p)};
    @endfor
   begin
      ${api.call(subp)};
   end ${api.proxyName(subp)};
@endif
