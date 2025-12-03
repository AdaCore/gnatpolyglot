with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P (I: Custom_Runtime_Type) is
   begin
      Put_Line (I);
   end;

end Test;

