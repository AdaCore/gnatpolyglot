<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to generate a Maven project Java bindings.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param Proxy proxy
@param String proxyLocation
@param String runtimeLocation

with "${runtimeLocation}/ada/gnatpolyglot_ada2java.gpr";
with "${runtimeLocation}/proxy2java/gnatpolyglot_proxy2java.gpr";

library project ${proxy.name.toPascal()}_JNI is

   type Build_Mode_Type is ("dev", "prod");
   Build_Mode : Build_Mode_Type := external
     ("${proxy.name.toUpper()}_JNI_BUILD_MODE",
      external ("BUILD_MODE", "dev"));

   type Library_Kind_Type is ("static", "relocatable", "static-pic");
   Library_Kind : Library_Kind_Type := external
     ("${proxy.name.toUpper()}_JNI_LIBRARY_KIND",
      external ("LIBRARY_KIND", "relocatable"));

   for Languages use ("C");

   for Source_Dirs use ("jni");
   for Object_Dir use "obj";
   for Library_Dir use "lib";

   for Library_Kind use "relocatable";
   for Library_Name use "${proxy.name.toLower()}_jni";

   Java_Home := external ("JAVA_HOME");
   JNI_Headers := ("-I" & Java_Home & "/include/");

   type OS_Type is ("windows", "unix");
   OS : OS_Type := external ("OS", "unix");

   case OS is
      when "windows" =>
         JNI_Headers := ("-I" & Java_Home & "/include/win32") & JNI_Headers;
         for Shared_Library_Prefix use "";
      when "unix" =>
         JNI_Headers := ("-I" & Java_Home & "/include/linux") & JNI_Headers;
   end case;

   C_Flags := ();

   case Build_Mode is
      when "dev" =>
         C_Flags := ("-g", "-O0");
      when "prod" =>
         C_Flags := ("-Ofast");
   end case;

   package Compiler is
      for Switches ("C") use JNI_Headers & C_Flags;
   end Compiler;

   ${proxy.name.toPascal()}_Lib := external
      ("PROXY_LIB",
       "${proxy.name.toLower()}_proxy_agg");
   for Library_Options use ("-l" & ${proxy.name.toPascal()}_Lib);

   ${proxy.name.toPascal()}_Lib_Location := external
     ("PROXY_LIB_LOCATION",
      "${proxyLocation}/lib_agg/" & Library_Kind & "/" & Build_Mode);

   package Linker is
      for Linker_Options use ("-L" & ${proxy.name.toPascal()}_Lib_Location);
   end Linker;

end ${proxy.name.toPascal()}_JNI;
