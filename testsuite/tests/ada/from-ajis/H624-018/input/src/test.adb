package body Test is

   function Get_New_R (V : Int_R) return R is
      Ret : R := (V => V);
   begin
      return Ret;
   end Get_New_R;

   function Get_New_R_Tagged (V : Int_R_Tagged) return R_Tagged'Class is
      Ret : R_Tagged := (V => V);
   begin
      return Ret;
   end Get_New_R_Tagged;

end Test;
