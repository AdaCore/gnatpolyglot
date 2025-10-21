with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function To_T (I : Integer) return T is (T (I));

   function Add_T (L, R : T) return T is (L + R);

   procedure Print (Val : T) is
   begin
      Put_Line ("(" & Val'Image & ")");
   end Print;

end Test;
