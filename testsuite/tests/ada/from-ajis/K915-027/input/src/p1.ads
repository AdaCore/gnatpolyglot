package P1 is

-- 1D array is OK   type Matrix_Array_Type is
-- 1D array is OK      array (Positive range <>)
-- 1D array is OK      of Float;
-- 1D array is OK
-- 1D array is OK   subtype Matrix_Type is Matrix_Array_Type (1..6);

   -- 2D array is NOK

   type Matrix_Array_Type is
      array (Positive range <>,
             Positive range <>)
      of Float;

   subtype Matrix_Type is Matrix_Array_Type (1..6, 1..6);

   -- Ok with non derived type
--   type Matrix_Type is array (Positive range 1..6, Positive range 1..6) of Float;

   type Rec1 is record
      M : Matrix_Type;
   end record;

end P1;
