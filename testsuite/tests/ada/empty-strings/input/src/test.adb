with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Print_And_Return (S: String) return String is
   begin
      Put_Line (S);
      return S;
   end;

end Test;

