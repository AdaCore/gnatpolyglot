package Test is

   type A;
   type B is access all A;

   type A is access all Integer;

   type C;
   type D is access all C;
   type C is record
      Field : B;
   end record;

   procedure P (Val: D) is null;

end Test;

