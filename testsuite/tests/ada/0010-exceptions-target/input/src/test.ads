package Test is

   Exc1 : exception;
   Exc2 : exception;

   type Tag is tagged record
      I : Integer;
   end record;

   procedure Raise_Exc (T: Tag);

   procedure Get_Exception (T: Tag'Class);

end Test;
