with Ada.Text_IO; use Ada.Text_IO;

package body Test is
   procedure Call (C : Char_Callback) is
      V1 : Character := 'x';
      V2 : Character := 'a';
   begin
      C (V1, V2);
      Put_Line ("Ada: " & V1 & V2);
   end Call;
end Test;
