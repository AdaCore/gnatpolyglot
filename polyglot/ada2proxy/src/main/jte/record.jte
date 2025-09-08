<%--
vim: filetype=jteada
Template file to generate bindings for record types.
--%>
@import java.util.List
@import com.adacore.libadalang.Libadalang
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Component
@import com.adacore.polyglot.ada2proxy.proxy.Record
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Role
@import com.adacore.polyglot.proxy.Role.RoleKind
@param AdaAPI api
@param Record rec
@param Boolean isBody

!{
    // Abstract types cannot be instantiated: use their classwide version
    // instead.
    String recordName = rec.isAbstract()
        ? rec.getClasswideFullyQualifiedName()
        : rec.getFullyQualifiedName();
    String recordPrefix = recordName.replace(".", "_");
    String recordAccessName = rec.name.toPascalWithUnderscore() + "_Access";
    FunctionDecl freeFunc = rec.getFreeFunction();
    FunctionDecl copyFunc = rec.getCopyFunction();
    List<FunctionDecl> allocFuncs = rec.getAllocFunctions();
    FunctionDecl cloneFunc = rec.getCloneFunction();
}

@if(!isBody)
   <%-- Declare the allocation/freeing functions --%>
   procedure ${freeFunc.getLastName().toPascalWithUnderscore()} (Self: System.Address);
   pragma Export (C, ${freeFunc.getLastName().toPascalWithUnderscore()}, "${freeFunc.symbol}");

   @for(var allocFunc : allocFuncs)
      function ${allocFunc.getLastName().toPascalWithUnderscore()}
      @if(!allocFunc.type.parameters.isEmpty())
         (${api.cInterfaceParameters(rec, allocFunc)})
      @endif
         return System.Address;
      pragma Export (C, ${allocFunc.getLastName().toPascalWithUnderscore()}, "${allocFunc.symbol}");
   @endfor

   function ${cloneFunc.getLastName().toPascalWithUnderscore()}
     (Self: System.Address) return System.Address;
   pragma Export (C, ${cloneFunc.getLastName().toPascalWithUnderscore()}, "${cloneFunc.symbol}");

   <%-- Declare the copy function --%>
   procedure ${copyFunc.getLastName().toPascalWithUnderscore()}
     (Self: System.Address; Other: System.Address);
   pragma Export (C, ${copyFunc.getLastName().toPascalWithUnderscore()}, "${copyFunc.symbol}");

   <%-- Declare the getter and setter functions --%>
   @for(var f : rec.getGettersAndSetters())
      !{Component component = rec.getComponent(f.role.field);}
      @if(f.role.kind == RoleKind.GETTER)
         function ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}
           (Self: System.Address)
           return ${api.getterReturnTypename(component.getType())};
      @else
         procedure ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}
           (Self: System.Address;
            ${api.argName(component.name)}:
               ${api.cInterfaceTypename(component.getType())});
      @endif
      pragma Export (C, ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}, "${f.symbol}");
    @endfor

   @if(rec.isTaggedType())
      @template.shadow(api, rec, isBody)
   @endif

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

   @for(var allocFunc : allocFuncs)
      function ${allocFunc.getLastName().toPascalWithUnderscore()}
      @if(!allocFunc.type.parameters.isEmpty())
         (${api.cInterfaceParameters(rec, allocFunc)})
      @endif
         return System.Address
      is
         @for(var p : allocFunc.type.parameters)
           ${api.makeParamConversion(rec.getComponent(p.name))};
         @endfor
         package Converter is new System.Address_To_Access_Conversions (${recordName});
         Tmp : ${recordAccessName} := new ${recordName}'(
            <%-- When the type inherits from a private type (directly, or indirectly)
              -- an extension aggregate is required.
              --%>
            ${rec.extensionAggregate()}
            @for(var p : allocFunc.type.parameters)
               ${api.valueName(p.name)},
            @endfor
            others => <>
         );
      begin
         return Converter.To_Address (Converter.Object_Pointer (Tmp));
      end ${allocFunc.getLastName().toPascalWithUnderscore()};
   @endfor

   function ${cloneFunc.getLastName().toPascalWithUnderscore()}
     (Self: System.Address) return System.Address
   is
      <%---
        -- In the case of abstract types, we cannot use a variable with an address clause.
        -- In order to cover all cases, use an access.
        --%>
      package Converter is new System.Address_To_Access_Conversions (${recordName});
      Tmp : Converter.Object_Pointer := Converter.To_Pointer (Self);
   begin
      return Converter.To_Address (new ${recordName}'(Tmp.all));
   end ${cloneFunc.getLastName().toPascalWithUnderscore()};

   <%-- Define the copy function --%>
   procedure ${copyFunc.getLastName().toPascalWithUnderscore()}
     (Self: System.Address; Other: System.Address)
   is
      package Converter is new System.Address_To_Access_Conversions (${recordName});
      Self_Access : Converter.Object_Pointer := Converter.To_Pointer (Self);
      Self_Value : ${recordName} renames Self_Access.all;
      Other_Access : Converter.Object_Pointer := Converter.To_Pointer (Other);
      Other_Value : ${recordName} renames Other_Access.all;
   begin
      Self_Value := Other_Value;
   end ${copyFunc.getLastName().toPascalWithUnderscore()};

   <%-- Define the getter and setter functions --%>
   @for(var f : rec.getGettersAndSetters())
      !{
         Component component = rec.getComponent(f.role.field);
         Libadalang.BaseTypeDecl componentType = component.getType();
         String componentAccess = "Self_Value." + component.name.toPascalWithUnderscore();
      }
      @if(f.role.kind == RoleKind.GETTER)
         function ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}
           (Self: System.Address)
           return ${api.getterReturnTypename(component.getType())}
         is
            package Converter is
               new System.Address_To_Access_Conversions (${recordName});
            Self_Access : Converter.Object_Pointer :=
               Converter.To_Pointer (Self);
            Self_Value : ${recordName} renames Self_Access.all;
         begin
            ${api.makeGetterReturnConversion(componentAccess, componentType)};
         end ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()};
      @else
         procedure ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()}
           (Self: System.Address;
            ${api.argName(component.name)}:
               ${api.cInterfaceTypename(component.getType())})
         is
            package Converter is
               new System.Address_To_Access_Conversions (${recordName});
            Self_Access : Converter.Object_Pointer :=
               Converter.To_Pointer (Self);
            Self_Value : ${recordName} renames Self_Access.all;
            ${api.makeParamConversion(component)};
         begin
            ${componentAccess} := ${api.valueName(component.name)};
         end ${recordPrefix}_${f.getLastName().toPascalWithUnderscore()};
      @endif
    @endfor

   @if(rec.isTaggedType())
      @template.shadow(api, rec, isBody)
   @endif
@endif
