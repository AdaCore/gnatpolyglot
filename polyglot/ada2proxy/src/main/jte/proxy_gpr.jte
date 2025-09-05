<%--
vim: filetype=jteada
Template file to generate a gprbuild specification of the Ada bindings.
--%>
@import com.adacore.polyglot.ada2proxy.proxy.AdaProxy
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.proxy.Name
@import java.util.stream.Collectors;
@param AdaAPI api
@param String relLibPath
@param Name projectName
@param AdaProxy proxy

with "${relLibPath}";
with "polyglot-ada.gpr";
with "polyglot.gpr";

library project ${projectName.toPascalWithUnderscore()}.Proxy is

   type Build_Mode_Type is ("dev", "prod");
   Build_Mode : Build_Mode_Type := external
     ("${api.proxyName(projectName).toUpperCase()}_BUILD_MODE",
      external ("BUILD_MODE", "dev"));

   type Library_Kind_Type is ("static", "relocatable", "static-pic");
   Library_Kind : Library_Kind_Type := external
     ("${api.proxyName(projectName).toUpperCase()}_LIBRARY_KIND",
      external ("LIBRARY_KIND", "static"));

   type Library_Standalone_Type is ("standard", "no", "encapsulated");
   Library_Standalone : Library_Standalone_Type := external
      ("LIBRARY_STANDALONE", "encapsulated");

   for Source_Dirs use ("src");
   for Object_Dir use "obj" & "/" & Build_Mode;

   for Library_Name use "${api.proxyName(projectName).toLowerCase()}";
   for Library_Dir use "lib/" & Library_Kind & "/" & Build_Mode;
   for Library_Kind use Library_Kind;
   for Library_Standalone use Library_Standalone;
   for Library_Auto_Init use "true";
   for Interfaces use (
      @if (!proxy.arrayTypes.isEmpty())
         "polyglot-ada-arrays-non_native.ads",
      @endif
      ${api.getProxyInterfaces(proxy.packages)}
   );

   package Compiler is
      case Build_Mode is
         when "dev" =>
            for Switches ("Ada") use ("-g", "-O0");
         when "prod" =>
            for Switches ("Ada") use ("-Ofast");
      end case;
   end Compiler;

end ${projectName.toPascalWithUnderscore()}.Proxy;
