<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate callback subprograms.
--%>
@import com.adacore.libadalang.Libadalang
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.gnatpolyglot.ada2proxy.AdaTypeMatcher
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Callback
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Subprogram
@param AdaAPI api
@param Callback callback
@param Boolean isBody

!{
   Libadalang.BaseSubpSpec spec = callback.getSpec();
   Libadalang.BaseTypeDecl returnType = spec.pReturnType(spec);
   String callbackTypename = callback.callbackTypename();
   String convertedCallback = "Converted_Callback";
   String callbackCName = callback.callbackCName();
   String callbackAdaName = callback.callbackAdaName();
   boolean isProcedure = Subprogram.isProcedure(spec);
}

@if(!isBody)

   <%-- Generate a C ABI wrapper for when the callback is given to the user. --%>
   ${api.subpSpecCallbackSpec(spec, callbackCName)}
   with Convention => C;

   <%-- Generate an Ada wrapper that receives a Callback_Data. It is used for
     -- upcalls to user-functions given by arguments.
     -- The generic parameters hold the values of the callback that must be called, and
     -- the converted value of the `Callback_Data.Addr` field.
     -- It is instantiated on every subprograms that have a parameter of the corresponding
     -- type.
     --%>
   generic
      Local_Callback_Data : GNATpolyglot.Callback_Data;
      with ${api.subpSpecCallbackSpec(spec, "Local_Callback")};
   ${api.subpSpecAdaCallbackSpec(spec, callbackAdaName)};

@else

   <%-- Body of the C ABI wrapper subprogram --%>
   ${api.subpSpecCallbackSpec(spec, callbackCName)}
   is
      function Callback_Converter is new
         Standard.Ada.Unchecked_Conversion
            (System.Address, ${callbackTypename});
      ${convertedCallback} : ${callbackTypename} :=
         Callback_Converter (Callback_Data.Data);
   begin
      declare
      @for(var p : spec.pFormalParams())
         ${api.makeParamConversion(p)};
      @endfor
   @if (isProcedure)
      begin
         ${api.call(spec, convertedCallback + ".all")};
      @for(var p : spec.pFormalParams())
         ${api.syncParamValue(p)}
      @endfor
   @else
         ${api.makeReturnDeclarations(returnType)}
      @if (returnType.pIsLimitedType())
         begin
            return Value_Returned : ${returnType.pFullyQualifiedName()} :=
              Return_Type_Converter
                (new ${returnType.pFullyQualifiedName()}'
                  (${api.call(spec, convertedCallback + ".all")})) do
             @for(var p : spec.pFormalParams())
               ${api.syncParamValue(p)}
             @endfor
               null;
            end return;
      @else
            Value_Returned : ${returnType.pFullyQualifiedName()} :=
               ${api.call(spec, convertedCallback + ".all")};
         begin
         @for(var p : spec.pFormalParams())
            ${api.syncParamValue(p)}
         @endfor
            ${api.makeReturnConversion(spec.pReturnType(spec), "Value_Returned")};
      @endif
   @endif
      end;
   @template.exception_handler(api, returnType)
   end ${callbackCName};

   !{
      Libadalang.DefiningName[] formalParams = spec.pFormalParams();
   }

   <%-- Body of the Ada generic subprogram --%>
   ${api.subpSpecAdaCallbackSpec(spec, callbackAdaName)} is
   @for (var name : formalParams)
      ${api.makeUpcallParamConversion((Libadalang.ParamSpec) name.pBasicDecl(), name)};
   @endfor
   @if (isProcedure)
   begin
      Local_Callback (
         Local_Callback_Data
      @for (var name : formalParams)
         , ${api.makeUpcallArgument((Libadalang.BaseFormalParamDecl) name.pBasicDecl(), name)}
      @endfor
      );
      @template.exception_handler(api, spec.pReturnType(spec), isUpcall = true)
   @else
      Returned_Value : ${api.cInterfaceTypename(returnType)} :=
         Local_Callback (
            Local_Callback_Data
         @for (var name : formalParams)
            , ${api.makeUpcallArgument((Libadalang.BaseFormalParamDecl) name.pBasicDecl(), name)}
         @endfor
         );
         ${api.makeUpcallReturnDecl(spec.pReturnType(spec))}
   begin
      @template.exception_handler(api, spec.pReturnType(spec), isUpcall = true)
      ${api.makeUpcallReturn(spec)};
   @endif
   end ${callbackAdaName};


@endif
