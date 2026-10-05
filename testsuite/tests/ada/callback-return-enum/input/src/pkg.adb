with Ada.Text_IO; use Ada.Text_IO;

package body Pkg is

   procedure Call_Func (F : Func) is
      X : constant Enum := F (1000);
   begin
      Put_Line ("Ada: X = " & X'Img);
   end Call_Func;

end Pkg;
