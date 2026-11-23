package Test is

   type Rec is tagged record
      A, B : Integer;
   end record;

   type A_Rec is access all Rec'Class;

   type Rec_F is access function return A_Rec;

   procedure Call_And_Display (X : Rec_F);

end Test;