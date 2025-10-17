with Ada.Text_IO; use Ada.Text_IO;

package body Binded is
   procedure P (V : Test.Child.T) is
   begin
      Put_Line ("P");
   end P;
end Binded;
