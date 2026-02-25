<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate the exception handler of subprogram.
--%>
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.libadalang.Libadalang
@param AdaAPI api
@param Libadalang.BaseTypeDecl returnType
@param boolean isGetterType = false

   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E,
            Polyglot.Exceptions.${api.getProjectName().toPascalWithUnderscore()}
               .Identify_Exception'Access);
   @if (returnType != null && !returnType.isNone())
      @if (isGetterType)
         ${api.makeDefaultGetterReturn(returnType)};
      @else
         ${api.makeDefaultReturn(returnType)};
      @endif
   @endif
