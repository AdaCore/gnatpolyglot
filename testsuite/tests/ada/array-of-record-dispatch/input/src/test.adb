with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Inc_Arr (A : Arr) return Arr is
      New_Arr : Arr := A;
   begin
      for E of New_Arr loop
         E.I := E.I + 1;
      end loop;
      return New_Arr;
   end Inc_Arr;

   procedure Call_Foo (Self: T'Class; A: in out Arr_Acc) is
   begin
       Self.Foo (A);
   end;

end Test;
