<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate a gprbuild specification of the C++ bindings.
--%>
@import com.adacore.polyglot.proxy2cpp.CppAPI
@import com.adacore.polyglot.proxy.Proxy
@param CppAPI api
@param Proxy proxy
@param String runtimeLocation

with "${runtimeLocation}/ada/polyglot_ada2cpp.gpr";

library project ${proxy.name.toPascalWithUnderscore()}_2Cpp is

   for Languages use ("C++");

   for Library_Name use "${proxy.name.toLower()}_2cpp";

   for Object_Dir use "obj";
   for Source_Dirs use (".", "include/");
   for Library_Dir use "lib";

end ${proxy.name.toPascalWithUnderscore()}_2Cpp;
