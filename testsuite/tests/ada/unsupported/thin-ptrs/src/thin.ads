package Thin is

   type Arr is array (Positive range <>) of Integer;

   type Acc is access all Arr
   with Size => Standard'Address_Size;

   procedure P1 (A: Acc) is null;

end Thin;
