with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Print_Strings (V : A_Tagged_Type) is
   begin
      Put_Line ("FROM ADA: [" & V.S1 & "], [" & V.S2 & "]");
   end Print_Strings;

end Test;
