package Test is

   Global_Int : Integer := 1;

   type Rec is record
      I : Integer;
   end record;

   Global_Rec : Rec := (I => 1);

   type Arr is array (Positive range <>) of Integer;

   Global_Arr : Arr := (1 => 1, 2 => 1, 3 => 1);

   procedure Increment_Int (I : in out Integer);

   procedure Increment_Rec (B: Boolean; R : in out Rec);

   procedure Increment_Arr (A : in out Arr);

   Global_A, Global_B : Integer := 42;

end Test;
