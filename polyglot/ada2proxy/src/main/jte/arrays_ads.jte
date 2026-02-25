<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate array related functions of the Ada bindings.
--%>
@import java.util.List
@import com.adacore.libadalang.Libadalang
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.ArrayCollector
@import com.adacore.polyglot.ada2proxy.proxy.AdaProxy
@import com.adacore.polyglot.ada2proxy.proxy.Array
@import com.adacore.polyglot.proxy.Role.RoleKind
@import java.util.stream.Collectors
@param AdaAPI api
@param AdaProxy proxy

!{
   List<Libadalang.BaseTypeDecl> components =
      ArrayCollector.getAllComponentTypes(proxy)
         .stream()
         .filter(c -> AdaAPI.checkNativeType(c) == null)
         .toList();
}

@template.text_header()

with Interfaces.C;
with System;
with Polyglot.Ada.Arrays; use Polyglot.Ada.Arrays;

${
components.stream()
      .map(t -> t.pParentBasicDecl().pFullyQualifiedName())
      .distinct()
      .map(n -> "with " + n + ";")
      .collect(Collectors.joining("\n"))
}

package Polyglot.Ada.Arrays.Non_Native is

   @for(var component : components)
      @for(var f : Array.memberFunctions(component))
         !{
            String componentTypename = component.pFullyQualifiedName();
            String arrayTypename = componentTypename.replace(".", "_") + "_Array";
            String subpName =
               arrayTypename + "_" + f.name.getLastName().toPascalWithUnderscore();
            String interfaceType = api.cInterfaceTypename(component);
         }
         @if (f.role.kind == RoleKind.GETTER)
            @if (component.pIsAccessType(Libadalang.AdaNode.NONE))
               function ${subpName} is new Get_Access (Standard.${componentTypename});
            @else
               function ${subpName} is new Get (Standard.${componentTypename});
            @endif
         @elseif (f.role.kind == RoleKind.SETTER)
            @if (component.pIsAccessType(Libadalang.AdaNode.NONE))
               procedure ${subpName} is new Set_Access (Standard.${componentTypename});
            @else
               procedure ${subpName} is new Set_Record (Standard.${componentTypename});
            @endif
         @elseif (f.role.kind == RoleKind.FREE)
            procedure ${subpName} is new Free (Standard.${componentTypename});
         @elseif (f.role.kind == RoleKind.ALLOC)
            function ${subpName} is new
               ${f.name.getLastName().toPascalWithUnderscore()}
               (Standard.${componentTypename});
         @elseif (f.role.kind == RoleKind.CONSTRUCT)
            procedure ${subpName} is new
               ${f.name.getLastName().toPascalWithUnderscore()}
               (Standard.${componentTypename});
         @endif
         pragma Export (C, ${subpName}, "${f.symbol}");
      @endfor
   @endfor

end Polyglot.Ada.Arrays.Non_Native;
