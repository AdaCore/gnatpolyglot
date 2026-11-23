with Ada.Text_IO; use Ada.Text_IO;

package body Hello is

   procedure P (this : Integer) is
   begin
      Put_Line (this'Img);
   end;

end Hello;
