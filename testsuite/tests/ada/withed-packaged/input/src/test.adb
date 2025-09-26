with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P1(V: R2) is
   begin
      Put_Line (V.I'Image);
   end P1;

   procedure P2(V: A) is
   begin
      for R of V loop
         Put_Line (R.I'Image);
      end loop;
   end P2;

end Test;

