with Ada.Text_IO; use Ada.Text_IO;
with GNAT.Threads;

package body Test is

   procedure Get_Exception (I: Integer) is
   begin
      raise Exc with I'Img;
   end;

   procedure Unregister is
   begin
      GNAT.Threads.Unregister_Thread;
   end Unregister;

   procedure Do_Copy (Obj: T'Class; I: Integer) is
      Copy : T'Class := Obj;
   begin
       null;
       -- Put_Line(I'Image);
   end Do_Copy;

end Test;

