with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P1 (V : T) is
   begin
      Put_Line ("P1");
   end P1;

   procedure P2 (V2 : access T) is
   begin
      Put_Line ("P2");
   end P2;

   procedure P3 (V3 : T'Class) is
   begin
      Put_Line ("P3");
   end P3;

   procedure P4 (V : access T'Class) is
   begin
      Put_Line ("P4");
   end P4;

end Test;
