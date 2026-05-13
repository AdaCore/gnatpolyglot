package Ints is

   function F_Short return Short_Integer;
   function F_Char return Short_Short_Integer;
   function F_Int return Integer;
   function F_Long_Int return Long_Integer;

   type My_Int is range -(2**20) .. (2**20); -- Should map to int32
   type My_New_Short is new Short_Integer;   -- Should map to int16
   subtype My_Long_Int is Long_Integer;      -- Should map to int64
   type My_Small is range -256.. 0;          -- Should map to int16

   function F_My_Int return My_Int;
   function F_My_New_Short return My_New_Short;
   function F_My_Long_Int return My_Long_Int;
   function F_My_Small return My_Small;

   procedure P (S : My_Small) is null;

   type My_Positive is mod (2**32);

   function F_Positive return Positive;       -- subtype of integer: Should map
                                              -- int32 to
   function F_My_Positive return My_Positive; -- Should map to uint32

end Ints;
