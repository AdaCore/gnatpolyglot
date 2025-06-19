<%--
vim: filetype=jteada
Template file to generate bindings for record types.
--%>
@import com.adacore.libadalang.Libadalang;
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Component
@import com.adacore.polyglot.ada2proxy.proxy.Record
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Role
@import com.adacore.polyglot.proxy.Role.RoleKind
@param Record rec
@param Boolean isBody

!{
    String recordName = rec.getFullyQualifiedName();
    String recordPrefix = recordName.replace(".", "_");
    String recordAccessName = rec.name.toPascalWithUnderscore() + "_Access";
    FunctionDecl freeFunc = rec.getFreeFunction();
    FunctionDecl allocFunc = rec.getAllocFunction();
    FunctionDecl cloneFunc = rec.getCloneFunction();
}

@if(!isBody)
   <%-- Declare the allocation/freeing functions --%>
   procedure ${freeFunc.getLastName().toPascalWithUnderscore()} (Self: System.Address);
   pragma Export (C, ${freeFunc.getLastName().toPascalWithUnderscore()}, "${freeFunc.symbol}");

   function ${allocFunc.getLastName().toPascalWithUnderscore()} return System.Address;
   pragma Export (C, ${allocFunc.getLastName().toPascalWithUnderscore()}, "${allocFunc.symbol}");

   function ${cloneFunc.getLastName().toPascalWithUnderscore()}
     (Self: System.Address) return System.Address;
   pragma Export (C, ${cloneFunc.getLastName().toPascalWithUnderscore()}, "${cloneFunc.symbol}");

   <%-- Declare the getter and setter functions --%>
   @for(var f : rec.getGettersAndSetters())
      !{Component component = rec.getComponent(f.role.field);}
      @if(f.role.kind == RoleKind.GETTER)
         function ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}
           (Self: System.Address) return System.Address;
      @else
         procedure ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}
           (Self: System.Address;
            ${component.name.toPascalWithUnderscore()}_Arg:
               ${AdaAPI.cInterfaceTypename(component.getType())});
      @endif
      pragma Export (C, ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}, "${f.symbol}");
    @endfor
@else
   <%-- Define an Access type for the record --%>
   type ${recordAccessName} is access all ${recordName};

   <%-- Define the allocation/freeing functions --%>
   procedure ${freeFunc.getLastName().toPascalWithUnderscore()}
     (Self: System.Address)
   is
      function Convert_Self is new Ada.Unchecked_Conversion
        (System.Address, ${recordAccessName});
      procedure Free is new Ada.Unchecked_Deallocation
        (${recordName}, ${recordAccessName});
      Local_Self : ${recordAccessName} := Convert_Self (Self);
   begin
      Free (Local_Self);
   end ${freeFunc.getLastName().toPascalWithUnderscore()};

   function ${allocFunc.getLastName().toPascalWithUnderscore()}
      return System.Address
   is
      package Converter is new System.Address_To_Access_Conversions (${recordName});
      Tmp : ${recordAccessName} := new ${recordName}'(others => <>);
   begin
      return Converter.To_Address (Converter.Object_Pointer (Tmp));
   end ${allocFunc.getLastName().toPascalWithUnderscore()};

   function ${cloneFunc.getLastName().toPascalWithUnderscore()}
     (Self: System.Address) return System.Address
   is
      Tmp : ${recordName} with Address => Self;
      pragma Import (Ada, Tmp);
      package Converter is new System.Address_To_Access_Conversions (${recordName});
   begin
      return Converter.To_Address (new ${recordName}'(Tmp));
   end ${cloneFunc.getLastName().toPascalWithUnderscore()};

   <%-- Define the getter and setter functions --%>
   @for(var f : rec.getGettersAndSetters())
      !{
         Component component = rec.getComponent(f.role.field);
         Libadalang.BaseTypeDecl componentType = component.getType();
         String componentTypename = componentType.pFullyQualifiedName();
         String componentAccess = "Self_Value." + component.name.toPascalWithUnderscore();
      }
      @if(f.role.kind == RoleKind.GETTER)
         function ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}
           (Self: System.Address) return System.Address
         is
            Self_Value : ${recordName} with Address => Self;
            pragma Import(Ada, Self_Value);
         begin
            ${AdaAPI.makeGetterReturnConversion(componentAccess)};
         end ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()};
      @else
         procedure ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}
           (Self: System.Address;
            ${component.name.toPascalWithUnderscore()}_Arg:
               ${AdaAPI.cInterfaceTypename(component.getType())})
         is
            Self_Value : ${recordName} with Address => Self;
            pragma Import(Ada, Self_Value);
            ${AdaAPI.makeParamConversion(component)};
         begin
            ${componentAccess} := ${component.name.toPascalWithUnderscore()}_Value;
         end ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()};
      @endif
    @endfor
@endif
