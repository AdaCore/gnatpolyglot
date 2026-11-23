with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Print_F_Acc (R : Rec; F : F_Acc) is
   begin
      Put_Line (F.all (R));
   end Print_F_Acc;

   function F (V : Rec_Tagged; X : Rec) return String is
   begin
      return "From F, V is" & X.V'Img;
   end F;

end Test;