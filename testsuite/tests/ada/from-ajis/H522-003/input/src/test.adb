with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P_1 (V : Rec) is
   begin
      Put_Line ("P_1 of Rec");
   end P_1;

   procedure Call_P1 (Dummy : Boolean; V : Rec'Class) is
   begin
      P_1 (V);
   end Call_P1;

end Test;
