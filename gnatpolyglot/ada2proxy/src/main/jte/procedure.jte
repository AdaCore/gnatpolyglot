<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate binded procedures.
--%>
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Subprogram
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
      ${api.subpSpecAdaCallbackSpec(subp.getSpec(), "Local_Procedure")}
        renames ${subp.getOriginFullyQualifiedName()};
   begin
      declare
    @for(var p : subp.parameters)
      ${api.makeParamConversion(p)};
    @endfor
   begin
      ${api.call(subp, "Local_Procedure")};
    @for(var p : subp.parameters)
      ${api.syncParamValue(p)}
    @endfor
    end;
   @template.exception_handler(api, null)
   end ${api.proxyName(subp)};
@endif
