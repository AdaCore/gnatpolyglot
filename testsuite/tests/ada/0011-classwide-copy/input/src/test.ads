with Ada.Finalization;
with Ada.Unchecked_Deallocation;
with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type Root is Tagged record
      A, B : Integer;
   end record;

   procedure P (Value: Root);
   procedure P2 (Value: in out Root);

   procedure P_Make_Copy (Value: Root'Class);

end Test;
