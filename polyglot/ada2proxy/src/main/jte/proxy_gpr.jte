@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.proxy.Name
@import com.adacore.polyglot.proxy.Proxy
@import java.util.stream.Collectors;
@param String relLibPath
@param Name projectName
@param Proxy proxy

with "${relLibPath}";

library project ${AdaAPI.proxyName(projectName)} is

   for Source_Dirs use ("src");
   for Object_Dir use "obj";

   for Library_Name use "${AdaAPI.proxyName(projectName).toLowerCase()}";
   for Library_Dir use "lib";
   for Library_Kind use "dynamic";
   for Library_Standalone use "encapsulated";
   for Library_Auto_Init use "true";
   for Library_Interface use (
      ${AdaAPI.makeInterfaces(proxy.modules)}
   );

end ${AdaAPI.proxyName(projectName)};
