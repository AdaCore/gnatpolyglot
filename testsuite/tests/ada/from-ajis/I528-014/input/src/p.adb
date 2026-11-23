with Ada.Text_IO; use Ada.Text_IO;

package body P is

   procedure A (V : Integer) is
   begin
      Put_Line ("A1");
   end A;

   function A (V : Integer) return Integer is
   begin
      Put_Line ("A2");

      return 0;
   end A;

   procedure B (V : R) is
   begin
      Put_Line ("B1");
   end B;

   procedure B (V : A_R) is
   begin
      Put_Line ("B2");
   end B;

   procedure C (V : T) is
   begin
      Put_Line ("C1");
   end C;

   procedure C (V : T_Child) is
   begin
      Put_Line ("C2");
   end C;

   procedure D (V : Integer) is
   begin
      Put_Line ("D1");
   end D;

   procedure D (V : D_Type) is
   begin
      Put_Line ("D2");
   end D;

end P;