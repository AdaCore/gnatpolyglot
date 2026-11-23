package body Test is

   function F (V : Child) return Integer is
   begin
      return 42;
   end F;

   function F (V : Child) return Float is
   begin
      return 42.0;
   end F;

end Test;
