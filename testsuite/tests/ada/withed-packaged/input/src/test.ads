with Alias;
with Rec1; use Rec1;
with Rec2; use Rec2;
with Arr; use Arr;
with Rec4; use Rec4;

package Test is

   type T is record
      A : R1;
   end record;

   procedure P1(V: R2);

   procedure P2(V: A);

   procedure P3(V: Alias.T);

   type A4 is access all R4;

   procedure P4(V: A4);

end Test;
