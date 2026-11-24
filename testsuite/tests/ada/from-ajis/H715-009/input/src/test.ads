package Test is

   type T is tagged record
      F : Integer;
   end record;

   type A_T is access all T'Class;

   function Copy (V : A_T) return A_T;

end Test;
