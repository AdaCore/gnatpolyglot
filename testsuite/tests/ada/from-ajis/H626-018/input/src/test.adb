with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Adjust (B : in out Bla) is
   begin
      Put_Line ("ADJUST OBJECT");
      Flush;
   end Adjust;

   procedure Initialize (B : in out Bla) is
   begin
      Put_Line ("INITIALIZE OBJECT");
      Flush;
   end Initialize;

   procedure Finalize (B : in out Bla) is
   begin
      Put_Line ("FINALIZE OBJECT");
      Flush;
      raise Program_Error;
   end Finalize;

end Test;
