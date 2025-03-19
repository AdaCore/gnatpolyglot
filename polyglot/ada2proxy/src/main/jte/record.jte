@import com.adacore.libadalang.Libadalang;
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Record
@import com.adacore.polyglot.proxy.FunctionDecl
@param Record rec
@param Boolean isBody

!{
    String recordName = rec.getFullyQualifiedName();
    String recordAccessName = rec.name.toPascalWithUnderscore() + "_Access";
    FunctionDecl freeFunc = rec.getFreeFunction();
    FunctionDecl allocFunc = rec.getAllocFunction();
    FunctionDecl cloneFunc = rec.getCloneFunction();
}

@if(!isBody)
   procedure ${freeFunc.getLastName().toPascalWithUnderscore()} (Self: System.Address);
   pragma Export (C, ${freeFunc.getLastName().toPascalWithUnderscore()}, "${freeFunc.symbol}");

   function ${allocFunc.getLastName().toPascalWithUnderscore()} return System.Address;
   pragma Export (C, ${allocFunc.getLastName().toPascalWithUnderscore()}, "${allocFunc.symbol}");

   function ${cloneFunc.getLastName().toPascalWithUnderscore()}
     (Self: System.Address) return System.Address;
   pragma Export (C, ${cloneFunc.getLastName().toPascalWithUnderscore()}, "${cloneFunc.symbol}");
@else
   type ${recordAccessName} is access all ${recordName};

   procedure ${freeFunc.getLastName().toPascalWithUnderscore()} (Self: System.Address) is
      function Convert_Self is new Ada.Unchecked_Conversion
        (System.Address, ${recordAccessName});
      procedure Free is new Ada.Unchecked_Deallocation
        (${recordName}, ${recordAccessName});
      Local_Self : ${recordAccessName} := Convert_Self (Self);
   begin
      Free (Local_Self);
   end ${freeFunc.getLastName().toPascalWithUnderscore()};

   function ${allocFunc.getLastName().toPascalWithUnderscore()} return System.Address is
      package Converter is new System.Address_To_Access_Conversions (${recordName});
   begin
      return Converter.To_Address (new ${recordName});
   end ${allocFunc.getLastName().toPascalWithUnderscore()};

   function ${cloneFunc.getLastName().toPascalWithUnderscore()} (Self: System.Address)
   return System.Address is
      Tmp : ${recordName} with Address => Self;
      pragma Import (Ada, Tmp);
      package Converter is new System.Address_To_Access_Conversions (${recordName});
   begin
      return Converter.To_Address (new ${recordName}'(Tmp));
   end ${cloneFunc.getLastName().toPascalWithUnderscore()};
@endif
