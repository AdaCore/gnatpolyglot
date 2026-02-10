with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Init_Value (A, B: Integer) return Value is
   begin
      return (A, B);
   end Init_Value;

   procedure P(V: in out Value) is
   begin
      V.B := V.B + 1;
      Put_Line ("(" & V.A'Image & " ," & V.B'Image & " )");
   end P;

end Test;
