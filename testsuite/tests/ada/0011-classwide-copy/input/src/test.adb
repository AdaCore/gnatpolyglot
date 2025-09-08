with Ada.Text_IO; use Ada.Text_IO;
with Ada.Tags;
with System;

package body Test is

   procedure P (Value : Root) is
   begin
      Put_Line ("(" & Value.A'Image & "," & Value.B'Image & ")");
   end P;

   procedure P2 (Value : in out Root) is
   begin
      Value.A := Value.A * 2;
      Value.B := Value.B * 2;
   end P2;

   procedure P_Make_Copy (Value : Root'Class) is
   begin
      Put_Line ("Making copy");
      declare
         Copy : Root'Class := Value;
      begin
         Copy.P2;
         Value.P;
         Copy.P;
      end;
      Put_Line ("Copy deleted");
   end P_Make_Copy;

end Test;
