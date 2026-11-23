with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P (V : in out Lim_Record) is
   begin
      Put_Line ("P of Lim_Record, field is:" & V.V'Img);
   end P;

end Test;
