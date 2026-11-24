with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P (V : S) is
      pragma Unreferenced (V);
   begin
      Put_Line ("CALLING P");
   end P;

end Test;
