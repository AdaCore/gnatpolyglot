with Ada.Text_IO;

package body Hello is

   procedure Say (Msg : String) is
   begin
      Ada.Text_IO.Put_Line ("Ada says: " & Msg);
   end Say;

end Hello;
