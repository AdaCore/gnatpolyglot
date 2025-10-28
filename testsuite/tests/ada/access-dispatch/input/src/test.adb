with Ada.Text_IO; use Ada.Text_IO;
with Ada.Unchecked_Deallocation;

package body Test is

   function Root_Rec (R : Root; Acc : Rec_Access) return Rec_Access is (null);

   function Root_Arr (R : Root; Acc : Arr_Access) return Arr_Access is (null);

   function Root_Str (R : Root; Acc: Str_Access) return Str_Access is (null);

   procedure Root_Rec_P (R : Root; Acc: in out Rec_Access) is null;

   procedure Root_Arr_P (R : Root; Acc: in out Arr_Access) is null;

   procedure Root_Str_P (R : Root; Acc: in out Str_Access) is null;

   function Call_Root_Rec (R : Root'Class; A : Rec_Access) return Rec_Access is
      Acc : Rec_Access := R.Root_Rec (A);
   begin
      Put_Line ("Ada_Got : " & Acc.I'Image);
      return Acc;
   end Call_Root_Rec;

   function Call_Root_Arr (R : Root'Class; A : Arr_Access) return Arr_Access is
      Acc : Arr_Access := R.Root_Arr (A);
   begin
      Put ("Ada_Got : ");
      for I of Acc.all loop
         Put (I'Image & ", ");
      end loop;
      New_Line;
      Return Acc;
   end Call_Root_Arr;

   function Call_Root_Str (R : Root'Class; A : Str_Access) return Str_Access is
      Acc : Str_Access := R.Root_Str (A);
   begin
      Put_Line ("Ada_Got : " & Acc.all);
      return Acc;
   end Call_Root_Str;

   procedure Call_Root_Rec_P (R : Root'Class; A : in out Rec_Access) is
   begin
      R.Root_Rec_P (A);
      Put_Line ("Ada_Got : " & A.I'Image);
   end Call_Root_Rec_P;

   procedure Call_Root_Arr_P (R : Root'Class; A : in out Arr_Access) is
   begin
      R.Root_Arr_P (A);
      Put ("Ada_Got : ");
      Put (A.all'First'Image & " .. " & A.all'Last'Image & " = ");
      for I of A.all loop
         Put (I'Image & ", ");
      end loop;
      New_Line;
   end Call_Root_Arr_P;

   procedure Call_Root_Str_P (R : Root'Class; A: in out Str_Access) is
   begin
      R.Root_Str_P (A);
      Put_Line ("Ada_Got : " & A.all);
   end Call_Root_Str_P;


end Test;
