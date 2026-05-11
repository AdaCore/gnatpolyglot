with Text_IO; use Text_IO;

package body Test is

   procedure Print_My_Bool (B : My_Bool) is
   begin
      Put_Line (B'Image);
   end Print_My_Bool;

end Test;
