with Ada.Text_IO; use Ada.Text_IO;

package body Pkg is
   function To_String (X : T) return String is ("T");

   procedure Print (X : T'Class) is
   begin
      Put_Line (X.To_String);
   end Print;
end Pkg;
