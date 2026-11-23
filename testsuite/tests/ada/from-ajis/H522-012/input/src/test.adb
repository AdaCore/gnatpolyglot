with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Get return String_Access is
   begin
      return new String'("GET FROM TEST");
   end Get;

end Test;