with System.Address_Image;
with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Set_Fields (Obj : in out My_Type; A : Integer; B : Float) is
   begin
      Obj.A := A;
      Obj.B := B;
   end Set_Fields;

   procedure Display_My_Type (Obj : My_Type) is
   begin
      Put_Line ("A = " & Obj.A'Img & ", B = " & Obj.B'Img);
   end Display_My_Type;

   procedure Set_Fields_2 (Obj : access My_Type; A : Integer; B : Float) is
   begin
      Set_Fields (Obj.all, A, B);
   end Set_Fields_2;

   procedure Display_My_Type_2 (Obj : access My_Type) is
   begin
      Display_My_Type (Obj.all);
   end Display_My_Type_2;

   procedure Set_Fields_3 (Obj : My_Type_Access; A : Integer; B : Float) is
   begin
      Set_Fields (Obj.all, A, B);
   end Set_Fields_3;

   procedure Display_My_Type_3 (Obj : My_Type_Access) is
   begin
      Display_My_Type (Obj.all);
   end Display_My_Type_3;

end Test;