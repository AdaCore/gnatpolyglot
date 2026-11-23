with Ada.Text_IO; use Ada.Text_IO;

package body Gen is

   function Copy (Source : List) return List is
   begin
      Put_Line ("COPY DONE");
      return Source;
   end Copy;

   procedure Iterate
     (Container : List;
      Process   : not null access procedure (Position : Cursor)) is
   begin
      null;
   end Iterate;

   procedure Reverse_Iterate
     (Container : List;
      Process   : not null access procedure (Position : Cursor)) is
   begin
      null;
   end Reverse_Iterate;

end Gen;