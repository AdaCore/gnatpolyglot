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

   procedure Overloaded_Ret is
   begin
      Put_Line("Hello from Ada overloaded return type 1");
   end Overloaded_Ret;

   function Overloaded_Ret return Integer is
   begin
      Put_Line("Hello from Ada overloaded return type 2");
      return 2;
   end Overloaded_Ret;

   function Overloaded_Ret return T1 is
   begin
      Put_Line("Hello from Ada overloaded return type 3");
      return (V => 3);
   end Overloaded_Ret;

   function Overloaded_Ret(I: Integer) return Integer is
   begin
      Put_Line("Hello from Ada overloaded return type 4");
      return -I;
   end Overloaded_Ret;

   function No_Rename (I: Integer) return Integer is
   begin
      return I * 2;
   end No_Rename;

   function No_Rename (A, B: Long_Integer) return Long_Integer is
   begin
      return A * B;
   end No_Rename;

   procedure Overloaded_Int (I: Integer) is
   begin
      Put_Line("Hello from Ada overloaded Integer " & I'Image);
   end Overloaded_Int;

   procedure Overloaded_Int (I: My_Int) is
   begin
      Put_Line("Hello from Ada overloaded My_Int " & I'Image);
   end Overloaded_Int;

end Test;

