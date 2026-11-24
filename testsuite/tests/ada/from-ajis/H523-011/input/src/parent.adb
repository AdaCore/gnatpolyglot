with Ada.Text_IO; use Ada.Text_IO;

package body Parent is

   procedure P (V : Rec) is
   begin
      Put_Line (V.V'Img);
   end P;

end Parent;
