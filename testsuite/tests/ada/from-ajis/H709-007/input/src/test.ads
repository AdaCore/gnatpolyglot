package Test is

   type A_Tagged_Record (I : Integer) is tagged private;

   function Get_I (V : A_Tagged_Record) return Integer;

private

   type A_Tagged_Record (I : Integer) is tagged record
      null;
   end record;

end Test;
