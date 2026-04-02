<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate array related types of the Ada bindings.
--%>
@import java.util.List
@import com.adacore.libadalang.Libadalang
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.gnatpolyglot.ada2proxy.ArrayCollector
@import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaProxy
@import com.adacore.gnatpolyglot.ada2proxy.proxy.Array
@import com.adacore.gnatpolyglot.proxy.Role.RoleKind
@import java.util.stream.Collectors
@param AdaAPI api
@param Array array

   type ${api.asAccess(array.arrayType)} is access all ${array.getFullyQualifiedName()}
   with Size => Standard'Address_Size;
