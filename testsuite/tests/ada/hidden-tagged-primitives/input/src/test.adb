with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Foo (V : C) is
   begin
      Put_Line ("Foo");
   end Foo;

   procedure Bar (X : C) is
   begin
      Put_Line ("Bar");
   end Bar;

end Test;
