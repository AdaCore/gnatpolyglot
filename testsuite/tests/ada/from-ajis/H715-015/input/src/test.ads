package Test is

   type Constr_Arr is array (1 .. 10) of Integer;

   C1 : Constr_Arr := (others => 999);

   type Unconstr_Arr is array (Integer range <>) of Integer;

   C2 : Unconstr_Arr := (1 .. 10 => 999);

end Test;
