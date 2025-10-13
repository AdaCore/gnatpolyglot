<%--
vim: filetype=jteada
Template file to generate a gprbuild specification of the Ada bindings.
--%>
@import com.adacore.polyglot.ada2proxy.proxy.AdaProxy
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.proxy.Name
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

   Polyglot_Runtime := external ("POLYGLOT_RUNTIME", "${runtimeLocation}");

   for Project_Files use (
      "${relLibPath}",
      "${projectName.toLower()}-proxy.gpr",
      Polyglot_Runtime & "/polyglot/polyglot.gpr",
      Polyglot_Runtime & "/ada/polyglot-ada.gpr"
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
            "polyglot.ads",
            "polyglot-ada.ads",
            "polyglot-ada-arrays.ads",
            "polyglot-ada-arrays-native.ads",
            "polyglot-ada-strings.ads",
            "polyglot-ada-strings.adb",
            "polyglot-ada-arrays-non_native.ads",
            ${api.getProxyInterfaces(proxy.packages)},
            ${
               sources.stream()
                  .map(s -> "\"%s\"".formatted(Path.of(s).getFileName()))
                  .collect(Collectors.joining(", "))
            }
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

