with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function F (R : Root; A: in out Arr) return Arr is
      Res : Arr := A;
   begin
      for I of A loop
         I := I - 1;
      end loop;
      return Res;
   end F;

   function Call_F (R : Root'Class; A: in out Arr) return Arr is
   begin
      return R.F (A);
   end Call_F;
end Test;
