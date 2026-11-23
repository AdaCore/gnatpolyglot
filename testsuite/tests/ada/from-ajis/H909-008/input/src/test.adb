with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Print1 (V : An_Array) is
   begin
      for J in V'Range loop
         Put (V (J)'Img);
      end loop;

      New_Line;
   end Print1;

   procedure Print2 (V : An_Array_Access) is
   begin
      for J in V'Range loop
         Put (V (J)'Img);
      end loop;

      New_Line;
   end Print2;

   procedure Print3 (V : An_Array_Access) is
   begin
      for J in V'Range loop
         Put (V (J)'Img);
      end loop;

      New_Line;
   end Print3;

end Test;
