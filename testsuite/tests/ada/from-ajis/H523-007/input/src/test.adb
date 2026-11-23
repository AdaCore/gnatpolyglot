with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Call_Proc (P : Proc) is
      X : Integer := 1;
      Y : Character := 'a';
      Z : Enum := A;
      T : Boolean := True;
   begin
      P.all (X, Y, Z, T);
      Put_Line
           ("From Ada, {X, Y, Z, T}={" & X'Img & ", " & Y & ", " & Z'Img
            & ", " & T'Img & "}");
   end Call_Proc;

end Test;
