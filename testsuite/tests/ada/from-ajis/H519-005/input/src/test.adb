with System.Address_Image;
with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   Global_String : String_Access :=
     new String'("This string is coming from Ada");

   function Return_A_String_Access return String_Access is
   begin
      return Global_String;
   end Return_A_String_Access;

   procedure Display_A_String_Access (Str : String_Access) is
      use System;
   begin
      if Str = Global_String then
         Put_Line (Str.all);
         Put_Line ("THIS IS THE GLOBAL STRING !");
      else
         Put_Line (Str.all);
      end if;
   end Display_A_String_Access;

end Test;
