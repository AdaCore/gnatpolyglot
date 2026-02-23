with Ada.Text_IO; use Ada.Text_IO;
with Ada.Tags;

package body Test is

   procedure P1 (R : Root) is
   begin
      Put_Line ("(A =>" & R.A'Image & ", B =>" & R.B'Image & ")");
   end P1;

   function F (R : Root) return Rec is
   begin
      return (I => 42);
   end F;

   function F_Arr (R : Root; A: Arr; I : in out Integer) return Arr is
      Res : Arr := A;
   begin
      Put ("Returning from ada:");
      for I in Res'Range loop
         Res(I) := Res(I) + 1;
         Put (Res (I)'Image & ",");
      end loop;
      New_Line;
      I := I + 1;
      return Res;
   end F_Arr;

   procedure P1 (C : Child) is
   begin
      Put_Line
        ("(A =>"
         & C.A'Image
         & ", B =>"
         & C.B'Image
         & ", C =>"
         & C.C'Image
         & ")");
   end P1;

   procedure P2 (C : Child; R : Rec; I : Integer) is
   begin
      Put_Line ("P2 Ada");
   end P2;

   procedure P_Root (R: Root'Class) is
   begin
      R.P1;
   end P_Root;

   procedure P2_Child (C : Child'Class; R : Rec; I: Integer) is
   begin
      C.P2 (R, I);
   end P2_Child;

   function F_Rec (R: Root'Class) return Rec is
   begin
      return R.F;
   end F_Rec;

   function F_Arr_Disp (R: Root'Class; A: Arr; I : in out Integer) return Arr is
      Res : Arr := R.F_Arr(A, I);
   begin
      Put ("Got in ada:");
      for I in Res'Range loop
         Put (Res (I)'Image & ",");
      end loop;
      New_Line;
      return Res;
   end F_Arr_Disp;

end Test;
