package Test is

   type X is record
      Y, Z : Integer;
   end record;

   type A_X is access all X;

   V : A_X := new X;

   V_All : X renames V.all;

end Test;
