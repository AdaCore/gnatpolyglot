package Test1 is

   type A is tagged private;

   function Prim (X : A) return A;

private

   type A is tagged null record;

   function Prim (X : A) return A
   is (null record);

end Test1;
