<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate subtype related types of the Ada bindings.
--%>
@import java.util.List
@import com.adacore.libadalang.Libadalang
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.gnatpolyglot.ada2proxy.ArrayCollector
@import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaProxy
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Subtype
@import com.adacore.gnatpolyglot.proxy.Role.RoleKind
@import java.util.stream.Collectors
@param AdaAPI api
@param Subtype subtype

@if(!subtype.getDecl().pIsAccessType(Libadalang.AdaNode.NONE))
   type ${api.asAccess(subtype.getDecl())} is
      access all ${subtype.getFullyQualifiedName()}
   with Size => Standard'Address_Size;
@endif
