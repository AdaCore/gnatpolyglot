with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Print_V (X : A_Type) is
   begin
      Put_Line (X.V'Img);
   end Print_V;

   function F return A_Type is
      V : A_Type (5);
   begin
      return V;
   end F;

end Test;