with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Raise_Something is
   begin
      if True then
         raise My_Exception;
      else
         null;
      end if;
   exception
      when others =>
         Put_Line ("X"); Flush;
   end Raise_Something;

end Test;
