<%--
vim: filetype=jteada
Template file to generate subtype related types of the Ada bindings.
--%>
@import java.util.List
@import com.adacore.libadalang.Libadalang
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.ArrayCollector
@import com.adacore.polyglot.ada2proxy.proxy.AdaProxy
@import com.adacore.polyglot.ada2proxy.proxy.Subtype
@import com.adacore.polyglot.proxy.Role.RoleKind
@import java.util.stream.Collectors
@param AdaAPI api
@param Subtype subtype

   type ${api.asAccess(subtype.getDecl())} is
      access all ${subtype.getFullyQualifiedName()}
   with Size => Standard'Address_Size;
