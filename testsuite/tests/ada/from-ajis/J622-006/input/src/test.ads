package Test is

   type Error_Parser is abstract tagged null record;

   type R is null record;

   type Arr is array (Integer range <>) of R;

   procedure Initialize
      (This : in out Error_Parser;
       V    : Arr) is abstract;

end Test;
