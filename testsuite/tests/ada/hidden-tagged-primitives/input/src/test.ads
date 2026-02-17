with Root; use Root;

package Test is

   type B is new A with private;

   type C is new A with null record;

   procedure Bar (X : C);

   type E is new A with private;

private

   overriding
   procedure Foo (V : C);

   type B is new C with null record;

   procedure Baz (X : B) is null;

   type E is new D with null record;

end Test;
