with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Get_Null_P_Acc return P_Acc is
   begin
      return null;
   end Get_Null_P_Acc;

   procedure Call_P_Acc (V : P_Acc) is
   begin
      if V = null then
         Put_Line ("P_Acc is null");
      else
         V.all;
      end if;
   end Call_P_Acc;

end Test;