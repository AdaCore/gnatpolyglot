with Ada.Text_IO; use Ada.Text_IO;

package body Test.Priv is

   procedure Foo (P : Priv_Root) is
   begin
      Put_Line ("Private");
   end Foo;

end Test.Priv;
