with Ada.Text_IO; use Ada.Text_IO;

package body Other is

   procedure P_1 (V : T) is
   begin
      Put_Line (V.V'Img);
   end P_1;

   procedure P_2 (V : access T) is
   begin
      Put_Line (V.V'Img);
   end P_2;


   procedure P_3 (V : T_Acc) is
   begin
      Put_Line (V.V'Img);
   end P_3;

end Other;