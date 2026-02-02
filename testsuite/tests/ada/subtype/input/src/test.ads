with Tagged_Type;

package Test is

   type A is record
      V: Integer := 1;
   end record;

   subtype B is A;

   function Identity (Value: B) return B is (Value);

   subtype C is Tagged_Type.T'Class;

   procedure Foo (Value: C);

end Test;
