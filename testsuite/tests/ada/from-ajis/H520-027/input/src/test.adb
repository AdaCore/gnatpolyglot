with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Print_Contents (Arr : An_Array) is
   begin
      for J in Arr'Range loop
         Put (Integer'Image (Arr (J)));
      end loop;
   end Print_Contents;

end Test;
