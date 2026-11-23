with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Sum (V : An_Array) return Integer is
      Total : Integer := 0;
   begin
      for J in V'Range loop
         Total := Total + V (J);
      end loop;

      return Total;
   end Sum;

   function F return An_Array is
      Result : An_Array (1 .. 2) := (1, 2);
   begin
      return Result;
   end F;

end Test;