package Test is

   type Arr is array (Positive range <>) of Integer;

   type Root is tagged null record;

   function F (R : Root; A: in out Arr) return Arr;

   function Call_F (R : Root'Class; A: in out Arr) return Arr;

end Test;
