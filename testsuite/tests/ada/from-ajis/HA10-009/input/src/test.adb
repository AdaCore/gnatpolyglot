with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Print (V : String) is
   begin
      Put_Line (V);
   end Print;

   procedure Modify (V : in out String) is
   begin
      V := (others => 'x');
   end Modify;

end Test;
