with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P1 (X : out R_Acc) is
   begin
      X := new R'(V => 55);
   end P1;

   procedure P2 (X : in out R_Acc) is
   begin
      Put_Line ("INPUT IS " & X.V'Img);
      X := new R'(V => 999);
   end P2;

   procedure Call_P (X : P_Acc) is
      Acc : R_Acc := new R'(V => 10);
   begin
      X.all (Acc);
      Put_Line ("OUTPUT IS " & Acc.V'Img);
   end Call_P;

   procedure P1_T (X : out R_T_Acc) is
   begin
      X := new R_T'(V => 55);
   end P1_T;

   procedure P2_T (X : in out R_T_Acc) is
   begin
      Put_Line ("INPUT_T IS " & X.V'Img);
      X := new R_T'(V => 999);
   end P2_T;

   procedure Call_P_T (X : P_T_Acc) is
      Acc : R_T_Acc := new R_T'(V => 10);
   begin
      X.all (Acc);
      Put_Line ("OUTPUT_T IS " & Acc.V'Img);
   end Call_P_T;

end Test;