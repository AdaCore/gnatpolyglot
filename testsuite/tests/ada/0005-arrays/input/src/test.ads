package Test is

   --  Integer (32-bit) arrays, unconstrained and constrained.

   type Int_Arr_U is array (Positive range <>) of Integer;

   type Int_Arr_C is array (1 .. 10) of Integer;

   function F_U_1 return Int_Arr_U;

   function F_U_2 (Arr : Int_Arr_U) return Integer;

   function F_C_1 return Int_Arr_C;

   function F_C_2 (Arr : Int_Arr_C) return Integer;

   procedure Out_Proc (Arr : in out Int_Arr_U);

   --  Other native integer widths (8 / 16 / 64-bit).

   type Byte_Arr is array (Positive range <>) of Short_Short_Integer;

   type Short_Arr is array (Positive range <>) of Short_Integer;

   type Long_Arr is array (Positive range <>) of Long_Long_Integer;

   function Make_Bytes return Byte_Arr;

   function Make_Shorts return Short_Arr;

   function Make_Longs return Long_Arr;

   --  Floating-point arrays (32 / 64-bit).

   type Float_Arr is array (Positive range <>) of Float;

   type Double_Arr is array (Positive range <>) of Long_Float;

   function Make_Floats return Float_Arr;

   function Sum_Floats (Arr : Float_Arr) return Float;

   procedure Scale_Floats (Arr : in out Float_Arr);

   function Make_Doubles return Double_Arr;

   function Sum_Doubles (Arr : Double_Arr) return Long_Float;

   --  Boolean arrays.

   type Bool_Arr is array (Positive range <>) of Boolean;

   function Make_Bools return Bool_Arr;

   function Any_True (Arr : Bool_Arr) return Boolean;

   procedure Negate_Bools (Arr : in out Bool_Arr);

   --  Arrays of a record element type.

   type My_Int is record
      I : Integer;
   end record;

   type My_Int_Arr is array (Positive range <>) of My_Int;

   function My_Int_Arr_Func return My_Int_Arr;

   procedure My_Int_Arr_Proc (Arr : in out My_Int_Arr);

   procedure Print_Image (Arr : My_Int_Arr);

end Test;
