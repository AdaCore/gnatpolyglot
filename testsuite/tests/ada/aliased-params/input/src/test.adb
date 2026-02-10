with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P_Int_Out (A: aliased out Integer) is
   begin
      A := 2;
   end P_Int_Out;

   procedure P_Int (A: aliased Integer) is
   begin
      Put_Line (A'Image);
   end P_Int;

   procedure P_Rec_Out (R: aliased out Rec) is
   begin
      R.I := 2;
   end P_Rec_Out;

   procedure P_Rec (R: aliased Rec) is
   begin
      Put_Line ("(I => " & R.I'Image & ")");
   end P_Rec;

   procedure P_Arr_Out (A: aliased out Arr) is
   begin
      for I of A loop
         I := 2;
      end loop;
   end P_Arr_Out;

   procedure P_Arr (A: aliased Arr) is
   begin
      Put ("(");
      for I of A loop
         Put (I'Image & ",");
      end loop;
      Put_Line (" )");
   end P_Arr;

end Test;

