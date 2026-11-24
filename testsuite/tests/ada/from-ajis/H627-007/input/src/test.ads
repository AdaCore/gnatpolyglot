package Test is

   type A is tagged private;

   function Increment_All (X : A) return A;

   procedure Print (X : A);

   type B is new A with private;

   function Increment_All (X : B) return B;

   procedure Print (X : B);

private

   type A is tagged record
      V : Integer := 1;
   end record;

   type B is new A with record
      V2 : Integer := 2;
   end record;

end Test;
