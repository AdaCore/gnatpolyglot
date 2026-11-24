with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Print_Something is
   begin
      Put_Line ("Something");
   end Print_Something;

   function X return A_Type is
      V : A_Type;
   begin
      return V;
   end X;

end Test;