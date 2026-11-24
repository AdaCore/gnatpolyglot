package Test is

   type R is record
      F : Integer;
   end record;

   type R_Array is array (Integer range <>) of R;

   type R_Array_Access is access all R_Array;

   V1 : R_Array (1 .. 2);

   V2 : R_Array_Access;

   V3 : aliased R_Array (1 .. 2);

   type R_Array_2 is array (Integer range <>) of aliased R;

   type R_Array_2_Access is access all R_Array_2;

   V4 : R_Array_2 (1 .. 2);

   V5 : R_Array_2_Access;

   V6 : aliased R_Array_2 (1 .. 2);

   type Proc is access procedure
     (Arr : R_Array);

   procedure Call (V : Proc);

end Test;
