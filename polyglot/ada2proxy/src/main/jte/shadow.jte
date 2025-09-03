<%--
vim: filetype=jteada
Template file to generate shadow types for tagged records.
--%>
@import java.util.List
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Role.RoleKind
@import com.adacore.polyglot.ada2proxy.proxy.Record
@param Record rec
@param Boolean isBody
!{
    String recordName = rec.name.toPascalWithUnderscore();
    String instantiableRecordName = rec.isAbstract()
        ? rec.getClasswideFullyQualifiedName()
        : rec.getFullyQualifiedName();
    String recordAccessName = rec.name.toPascalWithUnderscore() + "_Access";
    String recordFullName = rec.getFullyQualifiedName();

    String shadowTypename = rec.name.toPascalWithUnderscore() + "_Shadow";
    String shadowAccess = shadowTypename + "_Access";

    List<FunctionDecl> allocFuncs = rec.getShadowAllocFunctions();

    List<Subprogram> methods = rec.getAllMethods();
}
@if(!isBody)
   <%-- Shadow type for inheritance --%>

   type ${shadowTypename} is new ${recordFullName} with record
      Shadow: Polyglot.Ada.Shadow_Data;
   end record;

   type ${shadowAccess} is access all ${shadowTypename};

   @for(var subp: methods)
      type ${subp.name.toPascalWithUnderscore()}_${recordName}_Type is access
         ${AdaAPI.subprogramDispatchType(subp)}
         with Convention => C;
   @endfor

   type ${recordName}_Vtable is record
   @if(methods.isEmpty())
      null;
   @else
      @for(var subp: methods)
         ${subp.name.toPascalWithUnderscore()}_Dispatch :
            ${subp.name.toPascalWithUnderscore()}_${recordName}_Type;
      @endfor
   @endif
   end record with Convention => C;

   @for(var allocFunc : allocFuncs)
      function ${allocFunc.getLastName().toPascalWithUnderscore()}
      <%-- Functions with the SHADOW_ALLOC role have 2 hidden arguments --%>
      @if(!allocFunc.type.parameters.isEmpty() || allocFunc.role.kind == RoleKind.SHADOW_ALLOC)
         (${AdaAPI.cInterfaceParameters(rec, allocFunc)})
      @endif
         return System.Address;
      pragma Export
        (C, ${allocFunc.getLastName().toPascalWithUnderscore()},
         "${allocFunc.symbol}");
   @endfor

   @for(var method : rec.methods)
      overriding ${AdaAPI.makeShadowOverride(method)};
   @endfor

@else
   @for(var allocFunc : allocFuncs)
      function ${allocFunc.getLastName().toPascalWithUnderscore()}
      @if(!allocFunc.type.parameters.isEmpty() || allocFunc.role.kind == RoleKind.SHADOW_ALLOC)
         (${AdaAPI.cInterfaceParameters(rec, allocFunc)})
      @endif
         return System.Address
      is
         @for(var p : allocFunc.type.parameters)
            <%-- The Self and Vtable arguments do not have a corresponding
                 component in the Record, but they are always there. --%>
            @if(rec.getComponent(p.name) != null)
            ${AdaAPI.makeParamConversion(rec.getComponent(p.name))};
            @endif
         @endfor
         Self_Value : System.Address := Self_Arg;
         Vtable_Value : System.Address := Vtable_Arg;
         package Converter is new
           System.Address_To_Access_Conversions (${instantiableRecordName});
         Tmp : ${shadowAccess} := new ${shadowTypename}'(
            ${rec.extensionAggregate()}
            @for(var p : allocFunc.type.parameters)
               @if(rec.getComponent(p.name) != null)
                  ${p.name.toPascalWithUnderscore()}_Value,
               @endif
            @endfor
            Shadow => (Self => Self_Value, Vtable => Vtable_Value),
            others => <>
         );
      begin
         return Converter.To_Address (Converter.Object_Pointer (Tmp));
      end ${allocFunc.getLastName().toPascalWithUnderscore()};
   @endfor

   @for(var method : rec.methods)
      !{
         String subpName = method.name.toPascalWithUnderscore();
         String firstParam = method.parameters.get(0).name.toPascalWithUnderscore();
      }
      ${AdaAPI.makeShadowOverride(method)} is
         Table : ${recordName}_Vtable
         with Address => ${firstParam}.Shadow.Vtable;
         pragma Import (Ada, Table);
         procedure Free is new Ada.Unchecked_Deallocation
           (${instantiableRecordName}, ${recordAccessName});
      begin
         if Table.${subpName}_Dispatch = null then
            @if(!method.isProcedure())
               return
            @endif
            ${AdaAPI.callParent(method)};
         else
            @if(method.isProcedure())
               Table.${subpName}_Dispatch (
                  ${firstParam}.Shadow.Vtable, ${firstParam}.Shadow.Self
                  @for(var p : method.parameters.stream().skip(1).toList())
                     ,
                     ${AdaAPI.makeDispatchedArgument(p)}
                  @endfor
               );
            @else
               declare
                  Returned_Value : ${AdaAPI.cInterfaceTypename(method.getReturnType())} :=
                     Table.${subpName}_Dispatch (
                        ${firstParam}.Shadow.Vtable, ${firstParam}.Shadow.Self
                        @for(var p : method.parameters.stream().skip(1).toList())
                           ,
                           ${AdaAPI.makeDispatchedArgument(p)}
                        @endfor
                     );
                  ${AdaAPI.makeShadowReturnDecl(method.getReturnType())}
               begin
                  ${AdaAPI.makeShadowReturn(method)};
               end;
            @endif
         end if;
      end ${method.name.toPascalWithUnderscore()};
   @endfor
@endif
