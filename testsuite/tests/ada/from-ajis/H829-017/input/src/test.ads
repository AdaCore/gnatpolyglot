package Test is

   type A is record
      V : Integer;
   end record;

   type B is record
      F1 : A;
      F2 : aliased A := (V => 1);
   end record;

   G1 : A;
   G2 : aliased A := (V => 2);

   type P is access procedure;

end Test;
