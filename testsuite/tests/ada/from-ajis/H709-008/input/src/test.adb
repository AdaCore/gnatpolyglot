with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Proc_With_Owned_Allowed (V : A_R) is
   begin
      Put_Line (V.X'Img);
   end Proc_With_Owned_Allowed;

   procedure Proc_With_Owned_Not_Allowed (V : A_R) is
   begin
      Put_Line (V.X'Img);
   end Proc_With_Owned_Not_Allowed;

end Test;