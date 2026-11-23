with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Print_Vals
     (This : Extract_Line; Container : Extract)
   is
   begin
      Put_Line (This.A'Img & ", " & Container.B'Img);
   end Print_Vals;

end Test;
