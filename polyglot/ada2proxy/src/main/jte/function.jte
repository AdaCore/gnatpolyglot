@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.proxy.FunctionDecl
@param FunctionDecl funDecl
@param Boolean isBody

@if(!isBody)
   function ${AdaAPI.proxyName(funDecl.name)}
    @if(!funDecl.parameters.isEmpty())
      (${AdaAPI.cInterfaceParameters(funDecl)})
    @endif
      return ${AdaAPI.cInterfaceTypename(funDecl.returnType)};
   pragma Export (C, ${AdaAPI.proxyName(funDecl.name)}, "${funDecl.symbol}");
@else
   function ${AdaAPI.proxyName(funDecl.name)}
    @if(!funDecl.parameters.isEmpty())
      (${AdaAPI.cInterfaceParameters(funDecl)})
    @endif
      return ${AdaAPI.cInterfaceTypename(funDecl.returnType)} is
   begin
      return ${AdaAPI.cInterfaceTypename(funDecl.returnType)} (${AdaAPI.call(funDecl)});
   end ${AdaAPI.proxyName(funDecl.name)};
@endif
