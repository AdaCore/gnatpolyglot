with Ada.Text_IO; use Ada.Text_IO;
with Ada.Tags;

package body Test is

   procedure P1 (R : in out Root) is
   begin
      R.A := R.A + 1;
      R.B := R.B + 2;
      Put_Line ("(A =>" & R.A'Image & ", B =>" & R.B'Image & ")");
   end P1;

end Test;
