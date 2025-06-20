package Test.Priv is

   type Priv_Root is tagged private;

   procedure Foo (P : Priv_Root);

private

   type Priv_Root is tagged null record;

end Test.Priv;
