package Pkg is

   type U is null record;

   type U_Access is access all U;

   type T is tagged null record;

   --  Primitives of T with null exclusions in their profile
   procedure P (Self : T; A : not null U_Access) is null;
   function F (Self : T) return not null U_Access is (new U);

   --  Callbacks with null exclusions in their profile
   type P_Callback is access procedure (A : not null U_Access);
   procedure Call_P (Cb : P_Callback) is null;

   type F_Callback is access function return not null U_Access;
   procedure Call_F (Cb : F_Callback) is null;

end Pkg;
