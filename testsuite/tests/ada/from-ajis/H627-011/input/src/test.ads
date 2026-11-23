package Test is

   type String_Access is access all String;

   type A_Type (Str : String_Access; Size : Natural) is record
      Loc_Str : String (1 .. Size);
   end record;

end Test;
