package Test is

   type R is record
      X : Integer;
   end record;

   type A_R is access all R;

   procedure Proc_With_Owned_Allowed (V : A_R);

   procedure Proc_With_Owned_Not_Allowed (V : A_R);

end Test;