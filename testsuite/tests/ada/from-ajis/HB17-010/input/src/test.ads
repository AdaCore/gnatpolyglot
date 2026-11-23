package Test is

   type A_Tagged_Type (A, B : Integer) is tagged record
      S1 : String (1 .. A);
      S2 : String (1 .. B);
   end record;

   procedure Print_Strings (V : A_Tagged_Type);

end Test;
