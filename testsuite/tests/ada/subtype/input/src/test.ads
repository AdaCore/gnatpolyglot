package Test is

   type A is record
      V: Integer := 1;
   end record;

   subtype B is A;

   function Identity (Value: B) return B is (Value);

end Test;
