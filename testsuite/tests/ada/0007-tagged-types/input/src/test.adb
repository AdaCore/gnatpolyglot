with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P1 (R : Root) is
   begin
      Put_Line ("(A =>" & R.A'Image & ", B =>" & R.B'Image & ")");
   end P1;

   procedure P2 (C : Child) is
   begin
      Put_Line
        ("(A =>"
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

end Test;

