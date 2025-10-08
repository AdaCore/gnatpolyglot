with Ada.Text_IO; use Ada.Text_IO;
with Ada.Unchecked_Deallocation;

package body Test is

   function Root_Rec (R : Root; Acc : Rec_Access) return Rec_Access is (null);

   function Root_Arr (R : Root; Acc : Arr_Access) return Arr_Access is (null);

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

end Test;
