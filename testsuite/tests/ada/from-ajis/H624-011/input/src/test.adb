with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Set_X (Sth : Boolean; V : access A_Type'Class) is
   begin
      X := A_Type_Access (V);
   end Set_X;

end Test;