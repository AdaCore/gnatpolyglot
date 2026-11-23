with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P_2 (V : T_1) is
   begin
      Put_Line ("P2 of T_1");
   end P_2;

   procedure Call_P (V : T_1'Class) is
   begin
      P (V);
   end Call_P;

   procedure Call_P_2 (V : T_1'Class) is
   begin
      P_2 (V);
   end Call_P_2;

end Test;
