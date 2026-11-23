with Ada.Unchecked_Deallocation;

package body Test is

   procedure Some_Primitive (V : Type_1) is
   begin
      null;
   end Some_Primitive;

   ---------------------
   -- Create_And_Free --
   ---------------------

   procedure Create_And_Free (V : Type_2'Class) is
      procedure Free is new Ada.Unchecked_Deallocation (Type_1'Class, Acc_Type_1);

      Tmp : Acc_Type_1;
   begin
      Tmp := new Type_1'Class'(V.Get);
      Tmp.Some_Primitive;
      Free (Tmp);
   end Create_And_Free;

end Test;
