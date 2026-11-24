package Test is

   type G is null record;

   type Other is record
      F : G;
   end record;

   type A_G is access all G;

   Global_Variable : G;

   Global_Pointer : A_G;

   procedure Set_Global (V : A_G);

   type A_P is access procedure;

   procedure P;

   Storable_A_P : A_P := P'Access;

   procedure Call_A_P_Check (V : A_P);

   procedure Call_A_P_No_Check (V : A_P);

   type Other_Callback is access function return A_G;

   function Call_Other_Callback (F : Other_Callback) return A_G;

end Test;
