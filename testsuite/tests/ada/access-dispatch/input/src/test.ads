package Test is

   type Rec is record
      I : Integer;
   end record;

   type Rec_Access is access all Rec;

   type Arr is array (Positive range <>) of Integer;

   type Arr_Access is access all Arr;

   type Str_Access is access all String;

   type Root is tagged null record;

   function Root_Rec (R : Root; Acc: Rec_Access) return Rec_Access;

   function Root_Arr (R : Root; Acc: Arr_Access) return Arr_Access;

   function Root_Str (R : Root; Acc: Str_Access) return Str_Access;

   procedure Root_Rec_P (R : Root; Acc: in out Rec_Access);

   procedure Root_Arr_P (R : Root; Acc: in out Arr_Access);

   procedure Root_Str_P (R : Root; Acc: in out Str_Access);

   function Call_Root_Rec (R : Root'Class; A: Rec_Access) return Rec_Access;

   function Call_Root_Arr (R : Root'Class; A: Arr_Access) return Arr_Access;

   function Call_Root_Str (R : Root'Class; A: Str_Access) return Str_Access;

   procedure Call_Root_Rec_P (R : Root'Class; A: in out Rec_Access);

   procedure Call_Root_Arr_P (R : Root'Class; A: in out Arr_Access);

   procedure Call_Root_Str_P (R : Root'Class; A: in out Str_Access);

end Test;
