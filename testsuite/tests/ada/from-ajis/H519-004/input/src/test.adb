with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Return_A_String return String is
   begin
      return "This String is coming from Ada.";
   end Return_A_String;

end Test;