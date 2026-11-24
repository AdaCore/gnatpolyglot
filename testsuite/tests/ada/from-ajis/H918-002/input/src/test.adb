package body Test is

   function Get_An_Array return An_Access is
   begin
      return new An_Array'(1 .. 10 => 42);
   end Get_An_Array;

   function Get_An_Array (V : Root) return An_Access is
   begin
      return new An_Array'(1 .. 10 => 42);
   end Get_An_Array;

end Test;
