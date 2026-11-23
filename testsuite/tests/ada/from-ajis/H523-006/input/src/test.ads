package Test is

   type A_Type is tagged null record;

   function Get (V : access A_Type) return Integer;

end Test;
