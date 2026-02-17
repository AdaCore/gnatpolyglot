package Root is

   type A is abstract tagged null record;

   procedure Foo (V : A) is abstract;

   type D is new A with null record;

   procedure Foo (X : D);

end Root;
