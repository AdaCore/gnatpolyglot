package body Test is


   function F return Integer is
   begin
      return -1;
   end F;

   function F return R is
   begin
      return (V => 1);
   end F;

end Test;
