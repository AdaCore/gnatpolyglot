package Test is

   type Rec is record
      I : Integer;
   end record;

   type Arr is array (Positive range <>) of Rec;

   type Arr_Acc is access all Arr;

   type T is tagged null record;

   function Inc_Arr (A : Arr) return Arr;

   procedure Foo (Self: T; A: in out Arr_Acc) is null;

   procedure Call_Foo (Self: T'Class; A: in out Arr_Acc);

end Test;
