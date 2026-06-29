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

   function Make_Bytes return Byte_Arr is
   begin
      return (1, -2, 127);
   end Make_Bytes;

   function Make_Shorts return Short_Arr is
   begin
      return (1000, -2000, 32000);
   end Make_Shorts;

   function Make_Longs return Long_Arr is
   begin
      --  The second and third values exceed 32 bits, so a getter that
      --  truncated to 32 bits would corrupt them.
      return (1, 5_000_000_000, -5_000_000_000);
   end Make_Longs;

   function Make_Floats return Float_Arr is
   begin
      return (1.5, 2.5, 3.5);
   end Make_Floats;

   function Sum_Floats (Arr : Float_Arr) return Float is
      Ret : Float := 0.0;
   begin
      for I in Arr'Range loop
         Ret := Ret + Arr (I);
      end loop;
      return Ret;
   end Sum_Floats;

   procedure Scale_Floats (Arr : in out Float_Arr) is
   begin
      for I in Arr'Range loop
         Arr (I) := Arr (I) * 2.0;
      end loop;
   end Scale_Floats;

   function Make_Doubles return Double_Arr is
   begin
      return (10.5, 20.25);
   end Make_Doubles;

   function Sum_Doubles (Arr : Double_Arr) return Long_Float is
      Ret : Long_Float := 0.0;
   begin
      for I in Arr'Range loop
         Ret := Ret + Arr (I);
      end loop;
      return Ret;
   end Sum_Doubles;

   function Make_Bools return Bool_Arr is
   begin
      return (True, False, True);
   end Make_Bools;

   function Any_True (Arr : Bool_Arr) return Boolean is
   begin
      for I in Arr'Range loop
         if Arr (I) then
            return True;
         end if;
      end loop;
      return False;
   end Any_True;

   procedure Negate_Bools (Arr : in out Bool_Arr) is
   begin
      for I in Arr'Range loop
         Arr (I) := not Arr (I);
      end loop;
   end Negate_Bools;

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
