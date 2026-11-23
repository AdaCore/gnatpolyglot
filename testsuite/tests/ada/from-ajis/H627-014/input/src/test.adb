with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Initialize (This : in out A_Type) is
   begin
      null;
   end Initialize;

   procedure Adjust (This : in out A_Type) is
   begin
      null;
   end Adjust;

   procedure Finalize (This : in out A_Type) is
   begin
      Put_Line ("FINALIZE FROM ADA"); Flush;
   end Finalize;

end Test;