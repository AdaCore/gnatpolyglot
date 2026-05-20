<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate a gprbuild specification of the Ada bindings.
--%>
@import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaProxy
@import com.adacore.gnatpolyglot.ada2proxy.AdaAPI
@import com.adacore.gnatpolyglot.proxy.Name
@import java.nio.file.Path
@import java.util.stream.Collectors
@import java.util.List
@param AdaAPI api
@param String relLibPath
@param Name projectName
@param AdaProxy proxy
@param String runtimeLocation
@param List<String> sources

with "${projectName.toLower()}-proxy.gpr";

aggregate library project ${projectName.toPascalWithUnderscore()}.Proxy.Agg is

   type Build_Mode_Type is ("dev", "prod");
   Build_Mode : Build_Mode_Type := external
     ("${api.proxyName(projectName).toUpperCase()}_AGG_BUILD_MODE",
      external ("BUILD_MODE", "dev"));

   type Library_Kind_Type is ("static", "relocatable", "static-pic");
   Library_Kind : Library_Kind_Type := external
     ("${api.proxyName(projectName).toUpperCase()}_AGG_LIBRARY_KIND",
      external ("LIBRARY_KIND", "static"));

   type Library_Standalone_Type is ("standard", "no", "encapsulated");
   Library_Standalone : Library_Standalone_Type := external
      ("LIBRARY_STANDALONE", "encapsulated");

   GNATpolyglot_Runtime := "${runtimeLocation}";

   for Project_Files use (
      "${projectName.toLower()}-proxy.gpr",
      GNATpolyglot_Runtime & "/gnatpolyglot/gnatpolyglot.gpr",
      GNATpolyglot_Runtime & "/ada/gnatpolyglot-ada.gpr"
   );

   for Object_Dir use "obj";

   for Library_Name use "${api.proxyName(projectName).toLowerCase()}_agg";
   for Library_Dir use "lib_agg/" & Library_Kind & "/" & Build_Mode;
   for Library_Kind use Library_Kind;
   for Library_Standalone use Library_Standalone;
   for Library_Auto_Init use "true";

   case Library_Standalone is
      when "encapsulated" | "standard" =>
         for Interfaces use (
            "gnatpolyglot.ads",
            "gnatpolyglot-ada.ads",
            "gnatpolyglot-ada-arrays.ads",
            "gnatpolyglot-ada-arrays-native.ads",
            "gnatpolyglot-ada-strings.ads",
            "gnatpolyglot-ada-strings.adb",
            "gnatpolyglot-ada-arrays-non_native.ads",
            "gnatpolyglot-exceptions.ads"
            @if(!proxy.packages.isEmpty())
            , ${api.getProxyInterfaces(proxy.packages)}
            @endif
         );
      when "no" =>
         null;
   end case;

   package Builder is
      case Library_Kind is
         when "relocatable" =>
            for Global_Compilation_Switches ("Ada") use ("-fPIC");
         when others =>
            null;
      end case;
   end Builder;

end ${projectName.toPascalWithUnderscore()}.Proxy.Agg;

