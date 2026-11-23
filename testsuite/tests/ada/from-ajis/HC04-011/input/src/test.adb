with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Proc (V : Enum_1) is
   begin
      Put_Line (V'Img);
   end Proc;

   procedure Proc_2 (V : in out Enum_2) is
   begin
      V := Enum_2'Succ (V);
   end Proc_2;

   function Get return Enum_2 is
   begin
      return B;
   end Get;

   procedure Call (V : A_Callback) is
      V1, V2, V3, R : Enum_2;
   begin
      V1 := A;
      V3 := C;

      V.all (V1, V2, V3);

      Put_Line ("V2, Ada callback: " & V2'Img);
      Put_Line ("V3, Ada callback: " & V3'Img);
   end Call;

end Test;