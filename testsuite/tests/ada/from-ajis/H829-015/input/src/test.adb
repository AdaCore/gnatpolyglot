with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Set_Global (V : A_G) is
   begin
      Global_Pointer := V;
   end Set_Global;

   procedure Call_A_P_Check (V : A_P) is
   begin
      V.all;
   end Call_A_P_Check;

   procedure Call_A_P_No_Check (V : A_P) is
   begin
      V.all;
   end Call_A_P_No_Check;

   procedure P is
   begin
      Put_Line ("P from Ada");
   end P;

   function Call_Other_Callback (F : Other_Callback) return A_G is
   begin
      return F.all;
   end Call_Other_Callback;

end Test;