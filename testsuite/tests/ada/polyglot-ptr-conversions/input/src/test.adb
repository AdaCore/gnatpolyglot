with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   C : aliased Child;

   function Get_Child return Child_Access is (C'Access);

   procedure Print (P : Parent_Cst) is
   begin
      Put_Line (Boolean'Image (P /= null));
   end Print;

end Test;
