with Ada.Text_IO; use Ada.Text_IO;

package body Test is
   procedure Increment_Int (I : in out Integer) is
   begin
      I := I + 1;
   end Increment_Int;

   procedure Increment_Rec (B : Boolean; R : in out Rec) is
   begin
      R.I := R.I + 1;
   end Increment_Rec;

   procedure Increment_Arr (A : in out Arr) is
   begin
      for I in A'Range loop
         A (I) := A (I) + 1;
      end loop;
   end Increment_Arr;
end Test;
