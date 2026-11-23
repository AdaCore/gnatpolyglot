package Test is

   pragma Elaborate_Body;

   type R is record
      A, B : Integer;
   end record;

   type A_R is access all R;

   type T is abstract tagged null record;

   function F1 (V : T) return R is abstract;

   function F2 (V : T) return A_R is abstract;

   procedure P (V : T; X : in out A_R) is abstract;

   Global : aliased R;

   procedure Test_All (V : T'Class);

   type Ptr_F is access function return R;
   type Ptr_P is access procedure (V : in out A_R);

   procedure Call_F (F : Ptr_F);

   procedure Call_P (P : Ptr_P);

end Test;
