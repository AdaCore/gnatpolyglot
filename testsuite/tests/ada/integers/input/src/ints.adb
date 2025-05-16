package body Ints is
   function F_Char return Character is ('P');
   function F_Short return Short_Integer is (Short_Integer'Last);
   function F_Int return Integer is (Integer'Last);
   function F_Long_Int return Long_Integer is (Long_Integer'Last);

   function F_My_Int return My_Int is (My_Int'Last);
   function F_My_New_Short return My_New_Short is (1000);
   function F_My_Long_Int return My_Long_Int is (My_Long_Int'First);
   function F_My_Small return My_Small is (-256);

   function F_Positive return Positive is (1);
   function F_My_Positive return My_Positive is (2);
end Ints;

