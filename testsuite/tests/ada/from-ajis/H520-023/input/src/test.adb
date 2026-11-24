with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Call (C : A_Callback; V : Integer) is
   begin
      Put_Line ("For" & V'Img & " got" & Integer'Image (C (V)));
   end Call;

end Test;