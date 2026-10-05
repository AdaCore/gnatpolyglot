with Ada.Text_IO; use Ada.Text_IO;

package body Pkg is

   procedure Call_Proc (P : Proc) is
      X : Enum := B;
   begin
      P (X);
      Put_Line ("Ada: X = " & X'Img);
   end Call_Proc;

end Pkg;
