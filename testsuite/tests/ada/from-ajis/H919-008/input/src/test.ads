with Ada.Finalization; use Ada.Finalization;

package Test is

   type Accessed_By_Pointer is record
      V : Integer := 0;
   end record;

   type Acc is access all Accessed_By_Pointer;

   type Base_Record is new Controlled with record
      V : Integer := 0;
      W : Acc := new Accessed_By_Pointer;
   end record;

   procedure Finalize (V : in out Base_Record);

   type A_Record is record
      Rec : Base_Record;
   end record;

   type An_Array is array (Integer range <>) of A_Record;

end Test;
