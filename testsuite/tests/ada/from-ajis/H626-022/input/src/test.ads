package Test is

   type Extract_Line is record
      A : Integer;
   end record;

   type Ptr_Extract_Line is access all Extract_Line;

   type Extract is tagged record
      B : Integer;
   end record;

   procedure Print_Vals
     (This : Extract_Line; Container : Extract);

end Test;
