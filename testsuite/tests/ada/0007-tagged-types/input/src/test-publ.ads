with Ada.Text_IO; use Ada.Text_IO;
with Test.Priv;   use Test.Priv;

package Test.Publ is

   type Publ is new Priv_Root with record
      A, B : Integer;
   end record;

   procedure Foo (P : Publ);

end Test.Publ;
