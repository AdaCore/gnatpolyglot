package Test is

   type A_Record is record
      V : Integer;
   end record;

   type A_Callback_Record is access function
     (V : Integer; W : A_Record) return A_Record;

   procedure Call (C : A_Callback_Record);

end Test;