with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function F_Enum (V: Enum_1) return Enum_1 is
   begin
      Put_Line ("Got " & V'Image & " =" & V'Enum_Rep'Image);
      return V;
   end F_Enum;

end Test;

