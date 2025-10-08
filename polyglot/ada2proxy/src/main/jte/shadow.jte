<%--
vim: filetype=jteada
Template file to generate shadow types for tagged records.
--%>
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Subprogram
@import com.adacore.polyglot.proxy.FunctionDecl
@import com.adacore.polyglot.proxy.Role.RoleKind
@import com.adacore.polyglot.ada2proxy.proxy.Record
@import java.util.List
@import java.util.HashMap
@import java.util.stream.Collectors
@param AdaAPI api
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
    Integer i = 0;
    HashMap<Subprogram, String> vtableNames = new HashMap<>();
    for (var m : methods) {
        vtableNames.put(m, m.name.toPascalWithUnderscore() + "_Dispatch" + i++);
    }
}
@if(!isBody)
   <%-- Shadow type for inheritance --%>

   type ${shadowTypename} is new
         ${recordFullName} and Polyglot.Ada.Shadow_Interface with record
      Shadow: Polyglot.Ada.Shadow_Data;
   end record;

   procedure Set_Self_Owner
     (Shadow : out ${shadowTypename}; Owner : Polyglot.Ada.Self_Owner_Kind);

   type ${shadowAccess} is access all ${shadowTypename};

   @for(var subp: methods)
      type ${vtableNames.get(subp)}_${recordName}_Type is access
         ${api.subprogramDispatchType(subp)}
         with Convention => C;
   @endfor

   type ${recordName}_Vtable is record
      Clone_Shadow_Dispatch : Polyglot.Ada.Clone_Shadow_Type;
      Free_Shadow_Dispatch  : Polyglot.Ada.Free_Shadow_Type;
      @for(var subp: methods)
         ${vtableNames.get(subp)} :
            ${vtableNames.get(subp)}_${recordName}_Type;
      @endfor
   end record with Convention => C;

   @for(var allocFunc : allocFuncs)
      function ${allocFunc.getLastName().toPascalWithUnderscore()}
      <%-- Functions with the SHADOW_ALLOC role have 2 hidden arguments --%>
      @if(!allocFunc.type.parameters.isEmpty() || allocFunc.role.kind == RoleKind.SHADOW_ALLOC)
         (${api.cInterfaceParameters(rec, allocFunc)})
      @endif
         return System.Address;
      pragma Export
        (C, ${allocFunc.getLastName().toPascalWithUnderscore()},
         "${allocFunc.symbol}");
   @endfor

   @for(var method : methods)
      overriding ${api.makeShadowOverride(method, shadowTypename)};
   @endfor

@else

   procedure Set_Self_Owner
     (Shadow : out ${shadowTypename}; Owner : Polyglot.Ada.Self_Owner_Kind) is
   begin
      Shadow.Shadow.Self_Owner := Owner;
   end Set_Self_Owner;

   function ${shadowTypename}_Back_Ref_Converter
     (Self : access Polyglot.Ada.Shadow_Data) return System.Address
   is
      -- Init blank data to have an variable available to get the offset of a field.
      -- Ada does not allow getting the location of a field by refering its type only,
      -- but instead requires a value. In order to avoid dereferencing a null value,
      -- or creating a new object that could result in calling controlled type functions,
      -- we overlay an object on a blank array of bytes.
      type Bit_Array is array (1 .. ${shadowTypename}'Size) of Standard.Boolean with Pack;
      Tmp_Bits      : Bit_Array;
      function Bits_To_Shadow is new Ada.Unchecked_Conversion (System.Address, ${shadowTypename}_Access);
      Tmp : ${shadowTypename}_Access := Bits_To_Shadow(Tmp_Bits'Address);
      -- Get the offset from the Shadow_Data inside Cont_Shadow types
      Shadow_Offset : constant System.Storage_Elements.Storage_Offset :=
        Tmp.Shadow'Position;
   begin
      return System.Storage_Elements."-" (Self.all'Address, Shadow_Offset);
   end ${shadowTypename}_Back_Ref_Converter;

   @for(var allocFunc : allocFuncs)
      function ${allocFunc.getLastName().toPascalWithUnderscore()}
      @if(!allocFunc.type.parameters.isEmpty() || allocFunc.role.kind == RoleKind.SHADOW_ALLOC)
         (${api.cInterfaceParameters(rec, allocFunc)})
      @endif
         return System.Address
      is
         @for(var p : allocFunc.type.parameters)
            <%-- The Self and Vtable arguments do not have a corresponding
                 component in the Record, but they are always there. --%>
            @if(rec.getComponent(p.name) != null)
            ${api.makeParamConversion(rec.getComponent(p.name))};
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
                  ${api.valueName(p.name)},
               @endif
            @endfor
            Shadow =>
               (Ada.Finalization.Controlled with
                Self => Self_Value,
                Vtable => Vtable_Value,
                Self_Owner => Polyglot.Ada.User,
                Back_Ref_Converter => ${shadowTypename}_Back_Ref_Converter'Access),
            others => <>
         );
      begin
         @if(rec.isControlled() && allocFunc.type.parameters.isEmpty())
         ${recordName} (Tmp.all).Initialize;
         @endif
         return Converter.To_Address (Converter.Object_Pointer (Tmp));
      end ${allocFunc.getLastName().toPascalWithUnderscore()};
   @endfor

   @for(var method : methods)
      !{
         String subpName = method.name.toPascalWithUnderscore();
         String firstParam = method.parameters.get(0).name.toPascalWithUnderscore();
      }
      ${api.makeShadowOverride(method, shadowTypename)} is
         use all type Ada.Exceptions.Exception_Occurrence_Access;
         Table : ${recordName}_Vtable
         with Address => ${firstParam}.Shadow.Vtable;
         pragma Import (Ada, Table);
         procedure Free is new Ada.Unchecked_Deallocation
           (${instantiableRecordName}, ${recordAccessName});
         Exc_Occ_A : Standard.Ada.Exceptions.Exception_Occurrence_Access;
         Exc_Occ : Standard.Ada.Exceptions.Exception_Occurrence;
      @if(method.isProcedure())
      begin
         Table.${vtableNames.get(method)} (
            ${firstParam}.Shadow.Self
            @for(var p : method.parameters.stream().skip(1).toList())
               ,
               ${api.makeDispatchedArgument(p)}
            @endfor
         );
         Exc_Occ_A :=
           Polyglot.Exceptions.Addr_To_Exc
             (Polyglot.Get_Kernel.Exc_Info.Current_Exception);
         if Exc_Occ_A /= null then
            Polyglot.Get_Kernel.Exc_Info.Current_Exception :=
              System.Null_Address;
            Standard.Ada.Exceptions.Save_Occurrence (Exc_Occ, Exc_Occ_A.all);
            Polyglot.Exceptions.Exc_Free (Exc_Occ_A);
            Standard.Ada.Exceptions.Reraise_Occurrence (Exc_Occ);
         end if;
      @else
         Returned_Value : ${api.cInterfaceTypename(method.getReturnType())} :=
            Table.${vtableNames.get(method)} (
               ${firstParam}.Shadow.Self
               @for(var p : method.parameters.stream().skip(1).toList())
                  ,
                  ${api.makeDispatchedArgument(p)}
               @endfor
            );
         ${api.makeShadowReturnDecl(method.getReturnType())}
      begin
         Exc_Occ_A :=
           Polyglot.Exceptions.Addr_To_Exc
             (Polyglot.Get_Kernel.Exc_Info.Current_Exception);
         if Exc_Occ_A /= null then
            Polyglot.Get_Kernel.Exc_Info.Current_Exception :=
              System.Null_Address;
            Standard.Ada.Exceptions.Save_Occurrence (Exc_Occ, Exc_Occ_A.all);
            Polyglot.Exceptions.Exc_Free (Exc_Occ_A);
            Standard.Ada.Exceptions.Reraise_Occurrence (Exc_Occ);
         end if;
         ${api.makeShadowReturn(method)};
      @endif
      end ${method.getOriginName()};
   @endfor
@endif
