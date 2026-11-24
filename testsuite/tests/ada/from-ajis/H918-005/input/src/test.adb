with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Initialize (V : in out Root) is
   begin
      Put_Line ("INITIALIZE");
   end Initialize;

   procedure Adjust (V : in out Root) is
   begin
      Put_Line ("ADJUST");
   end Adjust;

   procedure Finalize (V : in out Root) is
   begin
      Put_Line ("FINALIZE");
      Finalized := Finalized + 1;
   end Finalize;

   procedure Print_All (V : Root_Array) is
   begin
      for J in V'Range loop
         Put_Line (V (J).V'Img);
      end loop;
   end Print_All;

   function F return Root_Array is
   begin
      return ((Controlled with V => 43), (Controlled with V => 44));
   end F;

end Test;
