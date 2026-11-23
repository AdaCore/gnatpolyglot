with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Finalize (V : in out Base_Record) is
   begin
      if V.V /= 0 then
         Put_Line ("FREE:" & V.V'Img);
      end if;
   end Finalize;

end Test;
