with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Return_String return String_Access is
   begin
      Put_Line ("RETURN STRING IS CALLED");
      return new String'("Hello World");
   end Return_String;

end Test;
