with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Call (C : A_Callback_Record) is
      Tmp : A_Record;
   begin
      Tmp.V := 100;
      Tmp := C (1, Tmp);
      Put_Line ("NOW, V IS" & Tmp.V'Img);
   end Call;

end Test;