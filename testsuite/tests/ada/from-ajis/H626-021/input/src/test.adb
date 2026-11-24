with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Call_And_Display (X : Rec_F) is
      V : A_Rec := X.all;
   begin
      Put_Line (V.A'Img & ", " & V.B'Img);
   end Call_And_Display;

end Test;