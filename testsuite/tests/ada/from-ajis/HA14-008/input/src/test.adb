with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Initialize (V : in out Contained) is
   begin
      Put_Line ("Initialize");
      Flush;
   end Initialize;

   procedure Finalize (V : in out Contained) is
   begin
      Put_Line ("Finalize");
      Flush;
   end Finalize;

   procedure Adjust (V : in out Contained) is
   begin
      Put_Line ("Adjust");
      Flush;
   end Adjust;

   procedure Prim (This : Root; Obj : Container) is
   begin
      Put_Line ("Prim");
      Flush;
   end Prim;

   procedure Call_Prim (This : Root'Class; Obj : Container) is
   begin
      Prim (This, Obj);
   end Call_Prim;

   procedure Prim2 (This : Root; Obj : Constrained_String) is
   begin
      Put_Line (Obj);
   end Prim2;

   procedure Call_Prim2 (This : Root'Class) is
   begin
      Prim2 (This, (1 => 'Y', 2 => 'O', others => '.'));
   end Call_Prim2;

end Test;
