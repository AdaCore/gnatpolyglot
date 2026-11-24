with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function F return Cst_Acc is
   begin
      return new R'(V => 1);
   end F;

   procedure P (V : Cst_Acc) is
   begin
      Put_Line (V.V'Img);
   end P;

end Test;