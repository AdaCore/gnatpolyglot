package Test is

   type Arr is array (Positive range <>) of Integer;

   type Arr1 is array (Positive range <>) of Arr (1 .. 5);

   type Acc is access all Arr1;

   type Arr2 is array (Positive range <>) of Acc;

end Test;
