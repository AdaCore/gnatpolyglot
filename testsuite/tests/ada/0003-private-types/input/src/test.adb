with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Init_Pair(L, R: Integer) return Pair is
   begin
      return (L, R);
   end Init_Pair;

   function Left(P: Pair) return Integer is
   begin
      return P.V_1;
   end Left;

   function Sum(P: Pair) return Integer is
   begin
      return P.V_1 + P.V_2;
   end Sum;

   function Add_Pairs(P1, P2: Pair) return Pair is
   begin
      return (P1.V_1 + P2.V_1, P1.V_2 + P2.V_2);
   end Add_Pairs;

   procedure Print(P: Pair) is
   begin
      Put_Line ("(" & P.V_1'Image & " ," & P.V_2'Image & " )");
   end Print;

end Test;
