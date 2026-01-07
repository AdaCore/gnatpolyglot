package Test is

   type Rec is record
      I : Integer;
   end record;

   type A_Rec is access all Rec;

   type Arr is array (Positive range <>) of A_Rec;

   function Inc_Arr (A : Arr) return Arr;

end Test;
