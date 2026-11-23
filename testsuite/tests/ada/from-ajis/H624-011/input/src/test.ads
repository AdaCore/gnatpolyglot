package Test is

   type A_Type is tagged record
      F : Integer;
   end record;

   type A_Type_Access is access all A_Type'Class;

   X : A_Type_Access := new A_Type'(F => 0);

   procedure Set_X (Sth : Boolean; V : access A_Type'Class);
   --  This access type used to generate an accessibility check exception.

end Test;