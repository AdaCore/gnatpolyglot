with Ada.Text_IO; use Ada.Text_IO;

package body Pkg is

   procedure Call (C : Rec_Callback) is
      R : constant Rec := C.all ((I => 1));
   begin
      Put_Line (R.I'Image);
   end Call;

   function Echo (Self : T; R : Rec) return Rec is (R);

   procedure Call_Echo (Self : T'Class) is
      R : constant Rec := Self.Echo ((I => 2));
   begin
      Put_Line (R.I'Image);
   end Call_Echo;

   procedure Call_Int_Array (C : Int_Array_Callback) is
      A : constant Int_Array := C.all ((1 => 3));
   begin
      Put_Line (A (A'First)'Image);
   end Call_Int_Array;

   procedure Call_Rec_Array (C : Rec_Array_Callback) is
      A : constant Rec_Array := C.all ((1 => (I => 4)));
   begin
      Put_Line (A (A'First).I'Image);
   end Call_Rec_Array;

   procedure Call_String (C : String_Callback) is
      S : constant String := C.all ("A");
   begin
      Put_Line (S (S'First) & "");
   end Call_String;

end Pkg;
