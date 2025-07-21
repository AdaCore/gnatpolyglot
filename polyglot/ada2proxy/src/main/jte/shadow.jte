<%--
vim: filetype=jteada
Template file to generate shadow types for tagged records.
--%>
@import java.util.List
@import com.adacore.polyglot.ada2proxy.AdaAPI
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
    String recordFullName = rec.getFullyQualifiedName();

    String shadowTypename = rec.name.toPascalWithUnderscore() + "_Shadow";
    String shadowAccess = shadowTypename + "_Access";

    List<FunctionDecl> allocFuncs = rec.getShadowAllocFunctions();
}
@if(!isBody)
   <%-- Shadow type for inheritance --%>

   type ${shadowTypename} is new ${recordFullName} with record
      Shadow: Polyglot.Ada.Shadow_Data;
   end record;

   type ${shadowAccess} is access all ${shadowTypename};

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
@endif
