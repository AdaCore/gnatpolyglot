@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Proxy
@param FunctionDecl funDecl
@param Boolean isBody

@if(!isBody)
   procedure ${AdaAPI.proxyName(funDecl.name)};
   pragma Export (C, ${AdaAPI.proxyName(funDecl.name)}, "${funDecl.symbol}");
@else
   procedure ${AdaAPI.proxyName(funDecl.name)} is
   begin
      ${AdaAPI.call(funDecl)};
   end ${AdaAPI.proxyName(funDecl.name)};
@endif
