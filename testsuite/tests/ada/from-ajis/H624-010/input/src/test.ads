package Test is

   type P_Acc is access procedure;

   function Get_Null_P_Acc return P_Acc;

   procedure Call_P_Acc (V : P_Acc);

end Test;