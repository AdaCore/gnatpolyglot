with Ada.Text_IO; use Ada.Text_IO;
with Ada.Tags;

package body Test is

   procedure P1 (R : Root) is
   begin
      Put_Line ("(A =>" & R.A'Image & ", B =>" & R.B'Image & ")");
   end P1;

   procedure P2 (R : Root; R2 : Rec) is
   begin
      Put_Line
        ("(A =>"
         & R.A'Image
         & ", B =>"
         & R.B'Image
         & "), (A =>"
         & R2.A'Image
         & ", B =>"
         & R2.B'Image
         & ")");
   end P2;

   procedure P2 (C : Child; R2 : Rec) is
   begin
      Put_Line
        ("(A =>"
         & C.A'Image
         & ", B =>"
         & C.B'Image
         & ", C =>"
         & C.C'Image
         & "), (A =>"
         & C.A'Image
         & ", B =>"
         & C.B'Image
         & ", C =>"
         & C.C'Image
         & ")");
   end P2;

   procedure P1 (C : Other_Child) is
   begin
      Put_Line
        ("(A =>"
         & C.A'Image
         & ", B =>"
         & C.B'Image
         & ", D =>"
         & C.D'Image
         & ")");
   end P1;

   procedure P_Root (R: Root'Class) is
   begin
      Put_Line (Ada.Tags.External_Tag (R'Tag));
   end P_Root;

   procedure P2_Root (R: Root'Class; R2: Rec) is
   begin
      R.P2 (R2);
   end P2_Root;

end Test;
