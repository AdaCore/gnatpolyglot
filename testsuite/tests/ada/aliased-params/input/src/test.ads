package Test is

   procedure P_Int_Out (A: aliased out Integer);
   procedure P_Int (A: aliased Integer);

   type Rec is record
      I : Integer;
   end record;

   procedure P_Rec_Out (R: aliased out Rec);
   procedure P_Rec (R: aliased Rec);

   type Arr is array (Positive range <>) of Integer;

   procedure P_Arr_Out (A: aliased out Arr);
   procedure P_Arr (A: aliased Arr);

end Test;
