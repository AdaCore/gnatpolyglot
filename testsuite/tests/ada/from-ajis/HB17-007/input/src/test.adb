with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Call_A_Callback (V : A_Callback) is
      V1  : Long_Long_Integer;
      V2  : Long_Long_Integer := 99;
      V3  : Float;
      V4  : Float := 15.0;
      V5  : Boolean;
      V6  : Boolean := False;
      V7  : An_Enum;
      V8  : An_Enum := B;
      V9  : Character;
      V10 : Character := 'q';
   begin
      V.all (V1, V2, V3, V4, V5, V6, V7, V8, V9, V10);

      Put_Line
        (V1'Img & ", "
         & V2'Img & ", "
         & V3'Img & ", "
         & V4'Img & ", "
         & V5'Img & ", "
         & V6'Img & ", "
         & V7'Img & ", "
         & V8'Img & ", "
         & V9'Img & ", "
         & V10'Img);
   end Call_A_Callback;

end Test;
