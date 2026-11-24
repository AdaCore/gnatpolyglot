with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Create_String (Is_Null : Boolean) return String_Access is
   begin
      if Is_Null then
         return null;
      else
         return new String'("This string is not null.");
      end if;
   end Create_String;

   procedure Print_String_Access (Str : String_Access) is
   begin
      if Str = null then
         Put_Line ("STRING_GOT: [null]");
      else
         Put_Line ("STRING_GOT: [""" & Str.all & """]");
      end if;
   end Print_String_Access;

   procedure Print_String (Str : String) is
   begin
      Put_Line ("STRING_GOT: [""" & Str & """]");
   end Print_String;

end Test;