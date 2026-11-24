with Ada.Text_IO; use Ada.Text_IO;

package body Hello is

   procedure Say (Something : String) is
   begin
      Put_Line (To_String(toto) & Something);
   end Say;

end Hello;
