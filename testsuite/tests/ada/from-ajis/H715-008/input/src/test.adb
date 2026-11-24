with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P (X : T) is
   begin
      Put_Line ("P FROM T: " & X.A'Img);
   end P;

end Test;
