package Test is

   type Int_Arr_U is array (Positive range <>) of Integer;

   type Int_Arr_C is array (1 .. 10) of Integer;

   function F_U_1 return Int_Arr_U;

   function F_U_2 (Arr : Int_Arr_U) return Integer;

   function F_C_1 return Int_Arr_C;

   function F_C_2 (Arr : Int_Arr_C) return Integer;

   procedure Out_Proc (Arr : in out Int_Arr_U);

   type My_Int is record
      I : Integer;
   end record;

   type My_Int_Arr is array (Positive range <>) of My_Int;

   function My_Int_Arr_Func return My_Int_Arr;

   procedure My_Int_Arr_Proc (Arr : in out My_Int_Arr);

   procedure Print_Image (Arr : My_Int_Arr);

end Test;
