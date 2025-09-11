<%--
vim: filetype=jteada
Template file to generate bindings for record types.
--%>
@import com.adacore.libadalang.Libadalang
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.GlobalVariable
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Parameter
@param AdaAPI api
@param GlobalVariable gv
@param Boolean isBody

!{
   FunctionDecl getter = gv.getGetter();
   FunctionDecl setter = gv.getSetter();
   Parameter setterParam =
      (setter != null) ? setter.type.parameters.get(0) : null;
   Libadalang.BaseTypeDecl varType = gv.getType();
}

@if(!isBody)
   function ${getter.getLastName().toPascalWithUnderscore()}
      return ${api.getterReturnTypename(varType)};
   pragma Export (C, ${getter.getLastName().toPascalWithUnderscore()}, "${getter.symbol}");

   @if(setter != null)
      procedure ${setter.getLastName().toPascalWithUnderscore()}
         (${api.argName(setterParam.name)}:
            ${api.cInterfaceTypename(varType)});
      pragma Export
         (C, ${setter.getLastName().toPascalWithUnderscore()}, "${setter.symbol}");
   @endif
@else

   function ${getter.getLastName().toPascalWithUnderscore()}
      return ${api.getterReturnTypename(varType)}
   is
      ${api.makeReturnDeclarations(varType)}
   begin
      ${api.makeGetterReturnConversion(gv.getFullyQualifiedName(), varType)};
   end ${getter.getLastName().toPascalWithUnderscore()};

   @if(setter != null)
      procedure ${setter.getLastName().toPascalWithUnderscore()}
         (${api.argName(setterParam.name)}:
            ${api.cInterfaceTypename(varType)}) is
         ${api.makeParamConversion(setterParam.name, varType, false)};
      begin
         ${gv.getFullyQualifiedName()} := ${api.valueName(setterParam.name)};
      end ${setter.getLastName().toPascalWithUnderscore()};
   @endif

@endif
