with Ada.Text_IO; use Ada.Text_IO;

package body Test is
   procedure In_Proc(A: in Integer; B: in T) is
   begin
      Put_Line ("Ada in: A = " & A'Image & ", B.V = " & B.V'Image);
   end In_Proc;

   procedure Out_Proc(A: out Integer; B: out T) is
   begin
      A := 123;
      B.V := 456;
   end Out_Proc;

   procedure In_Out_Proc(A: in out Integer; B: in out T) is
   begin
      Put_Line ("Ada in out before: A = " & A'Image & ", B.V = " & B.V'Image);
      A := 111;
      B.V := 222;
      Put_Line ("Ada in out after: A = " & A'Image & ", B.V = " & B.V'Image);
   end In_Out_Proc;
end Test;
