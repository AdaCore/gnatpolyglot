package Test is

   function F return Integer;

   type R is record
      V : Integer;
   end record;

   function F return R;

end Test;
