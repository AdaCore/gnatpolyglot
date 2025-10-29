with Ada.Text_IO; use Ada.Text_IO;
with Ada.Unchecked_Deallocation;

package body Test is

   function Root_Rec (R : Root; Acc : Rec_Access) return Rec_Access is (null);

   function Root_Arr (R : Root; Acc : Arr_Access) return Arr_Access is (null);

   procedure Root_Rec_P (R : Root; Acc: in out Rec_Access) is null;

   procedure Root_Arr_P (R : Root; Acc: in out Arr_Access) is null;

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

end Test;
