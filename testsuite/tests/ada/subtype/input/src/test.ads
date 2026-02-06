with Tagged_Type;

package Test is

   type A is record
      V: Integer := 1;
   end record;

   subtype B is A;

   function Identity (Value: B) return B is (Value);

   subtype C is Tagged_Type.T'Class;

   procedure Foo (Value: C);

   type Acc is access all A;

   subtype D is Acc;

   function Identity (Value: D) return D is (Value);

end Test;
