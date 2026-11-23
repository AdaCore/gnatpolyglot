with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P_1 (V : Rec) is
   begin
      Put_Line ("P_1");
   end P_1;

   procedure P_2 (V : Rec) is
   begin
      Put_Line ("P_2");
   end P_2;

end Test;
