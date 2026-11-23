package body Test is

   function F (V : P_B.An_Other_Subtype) return P_B.A_Subtype is
      Var : P_B.A_Subtype;
   begin
      return Var;
   end F;

end Test;