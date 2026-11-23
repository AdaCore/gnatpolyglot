with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type Root_1 is tagged private;

   procedure P (V : Root_1);

   type Root_2 is abstract tagged private;

   procedure P (V : Root_2) is abstract;

private

   type Root_1 is tagged null record;

   type Root_2 is abstract tagged null record;

end Test;
