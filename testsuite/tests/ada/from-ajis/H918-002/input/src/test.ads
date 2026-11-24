package Test is

   type An_Array is array (Integer range <>) of Integer;

   type An_Access is access all An_Array;

   function Get_An_Array return An_Access;

   type Root is tagged null record;

   function Get_An_Array (V : Root) return An_Access;

end Test;
