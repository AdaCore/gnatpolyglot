with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function F_U_1 return Int_Arr_U is
      Arr : Int_Arr_U := (1, 2, 3);
   begin
      return Arr;
   end F_U_1;

   function F_U_2 (Arr : Int_Arr_U) return Integer is
      Ret : Integer := 0;
   begin
      for I in Arr'Range loop
         Ret := Ret + Arr (I);
      end loop;
      return Ret;
   end F_U_2;

   function F_C_1 return Int_Arr_C is
      Arr : Int_Arr_C := (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
   begin
      return Arr;
   end F_C_1;

   function F_C_2 (Arr : Int_Arr_C) return Integer is
      Ret : Integer := 0;
   begin
      for I in Arr'Range loop
         Ret := Ret + Arr (I);
      end loop;
      return Ret;
   end F_C_2;

   procedure Out_Proc (Arr : in out Int_Arr_U) is
   begin
      for I in Arr'Range loop
         Arr (I) := Arr (I) * 2;
      end loop;
   end;

   function My_Int_Arr_Func return My_Int_Arr is
      Arr : My_Int_Arr := ((I => 1), (I => 2), (I => 3), (I => 4), (I => 5));
   begin
      return Arr;
   end My_Int_Arr_Func;

   procedure My_Int_Arr_Proc (Arr : in out My_Int_Arr) is
   begin
      for Index in Arr'Range loop
         Arr (Index).I := Arr (Index).I * 3;
      end loop;
   end My_Int_Arr_Proc;

   procedure Print_Image (Arr : My_Int_Arr) is
   begin
      Put ("(");
      for I in Arr'Range loop
         Put (I'Image & " => (I => " & Arr (I).I'Image & "), ");
      end loop;
      Put_Line (")");
   end Print_Image;

end Test;
