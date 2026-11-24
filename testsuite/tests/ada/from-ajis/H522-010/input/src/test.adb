with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Call_F (X : Boolean; V : access A_Type'Class) is
   begin
      Put_Line (F (V));
   end Call_F;

end Test;