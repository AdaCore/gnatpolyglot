with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Return_String (V : A_Type) return String_Access is
   begin
      Put_Line ("CALLED FROM ADA");

      return new String'("YO FROM ADA");
   end Return_String;

   function Callback_Return_String (V : A_Type'Class) return String_Access is
   begin
      return Return_String (V);
   end Callback_Return_String;

end Test;
