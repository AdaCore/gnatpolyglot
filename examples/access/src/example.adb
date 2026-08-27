with Ada.Text_IO; use Ada.Text_IO;

package body Example is

   procedure Print is
   begin
      if Acc /= null then
         Acc.I := Acc.I + 1;
         Put_Line (Acc.I'Image);
      else
         Put_Line ("Acc is null");
      end if;
   end Print;

end Example;
