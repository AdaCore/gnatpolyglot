package Ref is

   type Bla is array (Integer range <>) of Integer;

   function F (Left, Right : Bla) return Bla renames "&";

end Ref;
