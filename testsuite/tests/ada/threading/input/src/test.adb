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

end Test;

