<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
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
   begin
      declare
    @for(var p : subp.parameters)
      ${api.makeParamConversion(p)};
    @endfor
      ${api.makeReturnDeclarations(subp.getReturnType())}
   <%-- Limited types cannot be copied, so we must include the call in the
     -- return statement in order to generate legal code.
     -- Note: This is not possible to do in all cases, since we may want to
     -- perform checks when returning certain value types such as array
     -- accesses.
     --%>
   @if (subp.getReturnType().pIsLimitedType())
      begin
         return Value_Returned : ${returnType} :=
           Return_Type_Converter
             (new ${subp.getReturnType().pFullyQualifiedName()}'
               (${api.call(subp)})) do
          @for(var p : subp.parameters)
            ${api.syncParamValue(p)}
          @endfor
            null;
         end return;
   @else
         Value_Returned : ${subp.getReturnType().pFullyQualifiedName()} :=
            ${api.call(subp)};
      begin
       @for(var p : subp.parameters)
         ${api.syncParamValue(p)}
       @endfor
         ${api.makeReturnConversion(subp.getReturnType(), "Value_Returned")};
   @endif
      end;
   @template.exception_handler(api, subp.getReturnType())
   end ${api.proxyName(subp)};
@endif
