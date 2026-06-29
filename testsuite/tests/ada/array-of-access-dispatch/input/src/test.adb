with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Inc_Arr (A : Arr) return Arr is
   begin
      for E of A loop
         E.all.I := E.all.I + 1;
      end loop;
      return A;
   end Inc_Arr;

   procedure Call_Foo (Self: T'Class; A: in out Arr_Acc) is
   begin
       Self.Foo (A);
   end;

end Test;
