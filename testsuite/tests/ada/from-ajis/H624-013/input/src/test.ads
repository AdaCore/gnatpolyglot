package Test is

   type R is record
      V : Integer;
   end record;

   type R_Acc is access all R;

   procedure P1 (X : out R_Acc);

   procedure P2 (X : in out R_Acc);

   type P_Acc is access procedure (X : in out R_Acc);

   procedure Call_P (X : P_Acc);

   --  Same test, with tagged types

   type R_T is tagged record
      V : Integer;
   end record;

   type R_T_Acc is access all R_T;

   procedure P1_T (X : out R_T_Acc);

   procedure P2_T (X : in out R_T_Acc);

   type P_T_Acc is access procedure (X : in out R_T_Acc);

   procedure Call_P_T (X : P_T_Acc);

end Test;