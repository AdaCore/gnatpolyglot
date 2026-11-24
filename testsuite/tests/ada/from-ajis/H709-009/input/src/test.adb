package body Test is

   function F (X : T_1) return Int_1 is
   begin
      return Int_1 (X.Val);
   end F;

   function F (X : T_1) return Int_2 is
   begin
      return Int_2 (X.Val);
   end F;

end Test;
