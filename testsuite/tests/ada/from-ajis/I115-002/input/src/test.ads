package Test is

   type R is record
      A, B : Integer;
   end record;

   type A1 is access R;

   type A2 is access all R;

   function F1 (V : A1) return A1;

   function F2 (V : A2) return A2;

end Test;
