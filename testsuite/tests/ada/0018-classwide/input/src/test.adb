with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Get_Root return Root'Class is
   begin
      return Root'(I => 1);
   end Get_Root;

   procedure Get_Root (R: in out Root_A) is
   begin
      R := new Root'(I => 2);
   end Get_Root;

   function Get_Child return Root'Class is
   begin
      return Child'(I => 1, J => 2);
   end Get_Child;

   procedure Get_Child (R: in out Root_A) is
      Tmp : Root_A := R;
   begin
      R := new Child'(I => R.all.I, J => R.all.I * 2);
      Free (Tmp);
      null;
   end Get_Child;

   function Get_Root_A return Root_A is
   begin
      return new Root'(I => 1);
   end Get_Root_A;

   function Get_Child_A return Root_A is
   begin
      return new Child'(I => 1, J => 2);
   end Get_Child_A;

   function Call_F_Root (R: Root'Class; R2 : Root'Class) return Root'Class is
   begin
      return F_Root (R, R2);
   end Call_F_Root;

   procedure Call_P_Root_A (R: Root'Class; R2 : Root_A) is
   begin
      P_Root_A (R, R2);
   end Call_P_Root_A;

   procedure Call_P_Out_Root_A (R: Root'Class; R2 : in out Root_A) is
   begin
       P_Out_Root_A (R, R2);
   end Call_P_Out_Root_A;

   procedure Free (Acc : in out Root_A) is
      procedure P is new Ada.Unchecked_Deallocation (Root'Class, Root_A);
   begin
      P (Acc);
   end Free;

   procedure Call_Callback(C: Callback; R: Root'Class) is
   begin
       C.all(R);
   end Call_Callback;

end Test;

