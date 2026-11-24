package Test is

   type R is record
      V : Integer;
   end record;

   type Cst_Acc is access constant R;

   function F return Cst_Acc;

   procedure P (V : Cst_Acc);

   X : Cst_Acc := new R'(V => 2);

end Test;