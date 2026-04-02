<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate the exception handling ada files.
--%>

@import com.adacore.gnatpolyglot.ada2proxy.ExceptionCollector
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaException
@import com.adacore.gnatpolyglot.proxy.Name
@import java.util.List
@param AdaAPI api
@param AdaException exception
@param boolean isSource

@if(!isSource)
   @for(var allocFunc : exception.getAllocFunctions())
      function ${allocFunc.getLastName().toPascalWithUnderscore()}
      @if(!allocFunc.type.parameters.isEmpty())
         (What_Arg : GNATpolyglot.Ada.Strings.Polyglot_String)
      @endif
         return System.Address;
      pragma Export (C, ${allocFunc.getLastName().toPascalWithUnderscore()}, "${allocFunc.symbol}");
   @endfor
@else
   @for(var allocFunc : exception.getAllocFunctions())
      function ${allocFunc.getLastName().toPascalWithUnderscore()}
      @if(!allocFunc.type.parameters.isEmpty())
         (What_Arg : GNATpolyglot.Ada.Strings.Polyglot_String)
      @endif
         return System.Address
      is
         @if(!allocFunc.type.parameters.isEmpty())
         What_Value : String
            (1 .. Positive (GNATpolyglot.Ada.Strings.Length (What_Arg)))
         with Address => What_Arg.Data;
         @endif
      begin
         raise ${exception.getFullyQualifiedName()}
         @if(!allocFunc.type.parameters.isEmpty())
            with What_Value
         @endif
         ;
      exception
         when E: others =>
            return GNATpolyglot.Exceptions.Exc_To_Addr
              (Ada.Exceptions.Save_Occurrence (E));
      end ${allocFunc.getLastName().toPascalWithUnderscore()};
   @endfor
@endif
