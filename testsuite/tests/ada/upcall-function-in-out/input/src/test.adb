with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Call_P (X : P_Acc) return Integer is
      Acc : R_Acc := new R'(V => 10);
      Ret : Integer := X.all (Acc);
   begin
      Put_Line (Acc.V'Img);
      return Ret;
   end Call_P;

   function Call_P (V : T'Class) return Integer is
      Acc : R_Acc := new R'(V => 10);
      Ret : Integer := V.P (Acc);
   begin
      Put_Line (Acc.V'Img);
      return Ret;
   end Call_P;

end Test;
