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

library project ${proxy.name.toPascal()}_JNI is

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
      when "unix" =>
         JNI_Headers := ("-I" & Java_Home & "/include/linux") & JNI_Headers;
   end case;

   package Compiler is
      for Switches ("C") use JNI_Headers;
   end Compiler;

   ${proxy.name.toPascal()}_Lib := external
      ("PROXY_LIB",
       "${proxy.name.toLower()}_proxy");
   for Library_Options use ("-l" & ${proxy.name.toPascal()}_Lib);

   ${proxy.name.toPascal()}_Lib_Location :=
      external ("PROXY_LIB_LOCATION");

   package Linker is
      for Linker_Options use ("-L" & ${proxy.name.toPascal()}_Lib_Location);
   end Linker;

end Test_JNI;
