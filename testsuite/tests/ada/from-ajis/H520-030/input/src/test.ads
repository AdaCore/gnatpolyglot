with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type A_Record (V : Integer) is private;

   procedure Initialize (V : in out A_Record);

   function Get_Sum (R : A_Record) return Integer;

private

   type An_Array is array (Integer range <>) of Integer;

   type A_Record (V : Integer) is record
      Arr : An_Array (1 .. V);
   end record;

end Test;