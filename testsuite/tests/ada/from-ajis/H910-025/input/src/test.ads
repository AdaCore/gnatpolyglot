package Test is

   type Track_Number_Type is
      record
         Source_Type        : Integer;
         TN                 : String(1..15);
      end record;

   Max_TNs : constant := 25;
   subtype TN_Index_Type is Integer range 0..Max_TNs;

   type All_TNs_Type is array(TN_Index_Type) of Track_Number_Type;

   type Track_Type is
      record
         Latitude            : Float; -- Degrees
         Longitude           : Float; -- Degrees
         All_TNs             : All_TNs_Type;
      end record;

end Test;
