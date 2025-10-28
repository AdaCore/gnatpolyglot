with Ada.Text_IO; use Ada.Text_IO;
with Ada.Unchecked_Deallocation;

package body Test is

   procedure Print (V : T) is
   begin
      Put_Line (V.S);
   end Print;

end Test;
