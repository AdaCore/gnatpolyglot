with Ada.Text_IO; use Ada.Text_IO;

package body Pkg is

   procedure Call (C : Rec_Callback) is
      R : constant Rec_Access := C.all;
   begin
      if R = null then
         Put_Line ("null");
      else
         Put_Line (R.I'Image);
      end if;
   end Call;

   Seven : aliased Rec := (I => 7);

   function Get_Seven return Rec_Access is (Seven'Access);

   function Get return Rec_Callback is (Get_Seven'Access);

end Pkg;
