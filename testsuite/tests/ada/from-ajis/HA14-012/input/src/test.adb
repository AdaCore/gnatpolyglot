with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Get_Str (V : Root) return Str_Cst_Access is
   begin
      return new String'("A string");
   end Get_Str;

   procedure Print1 (Str : Str_Cst_Access) is
   begin
      Put_Line (Str.all);
   end Print1;

   procedure Print2 (Str : Str_Access) is
   begin
      Put_Line (Str.all);
   end Print2;

   procedure Print3 (Str : String) is
   begin
      Put_Line (Str);
   end Print3;

   procedure Print4 (Str : in out String) is
   begin
      Put_Line (Str);
   end Print4;

end Test;
