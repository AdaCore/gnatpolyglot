with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P (V : T_1) is
   begin
      Put_Line ("P of T_1");
   end P;

   procedure P (V : T_2) is
   begin
      Put_Line ("P of T_2");
   end P;

end Test;