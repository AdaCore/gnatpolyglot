with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Init_Value (A, B: Integer) return Value is
   begin
      return (A, B);
   end Init_Value;

   procedure P(V: in out Value) is
   begin
      V.A := V.A + 1;
      V.B := V.B + 1;
      Put_Line ("(" & V.A'Image & " ," & V.B'Image & " )");
   end P;

   function Init_Tagged (A, B: Integer) return Tagged_Value is
   begin
      return (A, B);
   end Init_Tagged;

   procedure P(V: in out Tagged_Value) is
   begin
      V.A := V.A + 2;
      V.B := V.B + 2;
      Put_Line ("(" & V.A'Image & " ," & V.B'Image & " )");
   end P;

   procedure Call_P(V: in out Tagged_Value'Class) is
   begin
      V.P;
   end Call_P;

end Test;
