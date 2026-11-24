package P is

   type R_Root is array (Integer range <>) of Integer;
   subtype R is R_Root (1 .. 1000);

   type Pointer_1 is access all R;

   V : Pointer_1;

   V2 : R;

end P;
