package Test is

   type A_Record;

   type A_Record_Access is access all A_Record;

   type A_Record is record
      Next : A_Record_Access;
      V    : Integer;
   end record;

end Test;