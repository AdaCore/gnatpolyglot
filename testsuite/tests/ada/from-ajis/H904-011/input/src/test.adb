with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Display (V1 : An_Unconstrained_Array; V2 : A_Constrained_Array) is
      S1 : String (1 .. V1'Length);
      S2 : String (1 .. V2'Length);
      Index : Integer;
   begin
      Index := 1;

      for J in V1'Range loop
         S1 (Index) := V1 (J);

         Index := Index + 1;
      end loop;

      Index := 1;

      for J in V2'Range loop
         S2 (Index) := V2 (J);

         Index := Index + 1;
      end loop;

      Put_Line (S1);
      Put_Line (S2);
   end Display;

end Test;
