with Ada.Text_IO; use Ada.Text_IO;

package body P.X is

   procedure Proc (P : T) is
   begin
      Put_Line ("Hello");
   end Proc;

   procedure Proc_2 (P : access T) is
   begin
      Put_Line ("Hello 2");
   end Proc_2;

end P.X;
