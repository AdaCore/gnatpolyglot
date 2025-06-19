with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Init_Pair(L, R: Integer) return Pair is
   begin
      return ((V=> L), (V => R));
   end Init_Pair;

   procedure Print(P: Pair) is
   begin
      Put_Line ("(" & P.V_1.V'Image & " ," & P.V_2.V'Image & " )");
   end Print;

   procedure Print_Bis(V: Value_Bis) is
   begin
      Put_Line ("(" & V.V'Image & " )");
   end Print_Bis;

end Test;
