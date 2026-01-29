with Ada.Text_IO; use Ada.Text_IO;
with Ada.Tags;

package body Test is

   procedure P1 (R : Root) is
   begin
      Put_Line ("Root.P1");
   end P1;

   procedure P2 (C : Child; I : Integer) is
   begin
      Put_Line ("Child.P" & I'Image);
   end P2;

   procedure P_Root (R: Root'Class) is
   begin
      Put_Line (Ada.Tags.External_Tag (R'Tag));
   end P_Root;

   procedure P2_Root (R: Root'Class; I: Integer) is
   begin
      R.P2 (I);
   end P2_Root;

end Test;
