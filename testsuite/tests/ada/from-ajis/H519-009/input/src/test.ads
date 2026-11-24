package Test is

   type String_Access is access all String;

   type A_Record is record
      A : Integer;
      B : Float;
      C : String_Access;
   end record;

end Test;