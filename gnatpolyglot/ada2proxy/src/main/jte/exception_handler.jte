<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate the exception handler of subprogram.
--%>
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.libadalang.Libadalang
@param AdaAPI api
@param Libadalang.BaseTypeDecl returnType
@param boolean isGetterType = false
@param boolean isUpcall = false

@if (!isUpcall)
   exception
      when E: others =>
         GNATpolyglot.Exceptions.Raise_Exception
           (E,
            GNATpolyglot.Exceptions.${api.getProjectName().toPascalWithUnderscore()}
               .Identify_Exception'Access);
   @if (returnType != null && !returnType.isNone())
      @if (isGetterType)
         ${api.makeDefaultGetterReturn(returnType)};
      @else
         ${api.makeDefaultReturn(returnType)};
      @endif
   @endif
@else
      declare
         use all type Ada.Exceptions.Exception_Occurrence_Access;
         Exc_Occ_A : Standard.Ada.Exceptions.Exception_Occurrence_Access;
         Exc_Occ : Standard.Ada.Exceptions.Exception_Occurrence;
      begin
         Exc_Occ_A :=
           GNATpolyglot.Exceptions.Addr_To_Exc
             (GNATpolyglot.Get_Kernel.Exc_Info.Current_Exception);
         if Exc_Occ_A /= null then
            GNATpolyglot.Get_Kernel.Exc_Info.Current_Exception :=
              System.Null_Address;
            Standard.Ada.Exceptions.Save_Occurrence (Exc_Occ, Exc_Occ_A.all);
            GNATpolyglot.Exceptions.Exc_Free (Exc_Occ_A);
            Standard.Ada.Exceptions.Reraise_Occurrence (Exc_Occ);
         end if;
      end;
@endif
