with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Outer_Proc is
   begin
      Put_Line ("Outer_Proc");
   end Outer_Proc;

   package body Inner is
      procedure Inner_Proc is
      begin
         Put_Line ("Inner_Proc");
      end Inner_Proc;
   end Inner;

end Test;
