package body Test is

   function F1 return Integer is
   begin
      return Integer'Last;
   end F1;

   function F2 return Long_Long_Integer is
   begin
      return Long_Long_Integer'Last;
   end F2;

   procedure Minus_One_Int (V : in out Integer) is
   begin
      V := V - 1;
   end Minus_One_Int;

   procedure Minus_One_Long (V : in out Long_Long_Integer) is
   begin
      V := V - 1;
   end Minus_One_Long;

end Test;
