with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Call
     (V : access procedure
        (V1 : Integer; V2 : out Integer; V3 : in out Integer))
   is
      V1 : Integer := 1;
      V2 : Integer;
      V3 : Integer := 3;
   begin
      V.all (V1, V2, V3);

      Put_Line (V2'Img & ", " & V3'Img);
   end Call;

end Test;
