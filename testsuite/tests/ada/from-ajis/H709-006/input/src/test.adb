with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Initialize (This : in out A_Tagged_Record) is
   begin
      Put_Line ("INITIALIZE");
   end Initialize;

   procedure Adjust (This : in out A_Tagged_Record) is
   begin
      Put_Line ("ADJUST");
   end Adjust;

   procedure Finalize (This : in out A_Tagged_Record) is
   begin
      Put_Line ("FINALIZE");
   end Finalize;

end Test;