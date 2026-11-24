package Test is

   type L_Inner_Type is limited record
      X : Integer;
   end record;

   type L_Rec is limited record
      V  : Integer := 999;
      V2 : L_Inner_Type;
   end record;

   LR : L_Rec;

end Test;
