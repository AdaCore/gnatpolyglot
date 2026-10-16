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

end Pkg;
