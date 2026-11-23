with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P (Self : Root; V : not null access Other_Root'Class) is
   begin
      Put_Line ("OK");
   end P;

end Test;
