with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Overloaded_Proc is
   begin
      Put_Line("Hello from Ada overloaded procedure 1");
   end Overloaded_Proc;

   procedure Overloaded_Proc(A: Integer) is
   begin
      Put_Line("Hello from Ada overloaded procedure 2: " & A'Image);
   end Overloaded_Proc;

   function Overloaded_Fun(A, B: Integer; C: Integer) return Integer is
      Res : Integer := A * B + C;
   begin
      Put_Line("Hello from Ada overloaded function 1: " & A'Image);
      return Res;
   end Overloaded_Fun;

   function Overloaded_Fun(A: Integer; B, C: T1) return Integer is
      Res : Integer := A * B.V + C.V;
   begin
      Put_Line("Hello from Ada overloaded function 2: " & Res'Image);
      return Res;
   end Overloaded_Fun;

end Test;

