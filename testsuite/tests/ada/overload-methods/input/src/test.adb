with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Overloaded_Proc (Self : T1) is
   begin
      Put_Line ("Hello from Ada overloaded procedure 1");
   end Overloaded_Proc;

   procedure Overloaded_Proc (Self : T1; A : Integer) is
   begin
      Put_Line ("Hello from Ada overloaded procedure 2: " & A'Image);
   end Overloaded_Proc;

   procedure Call_Overload (Self : T1'Class) is
   begin
      Self.Overloaded_Proc;
   end Call_Overload;

   procedure Call_Overload (Self : T1'Class; A : Integer) is
   begin
      Self.Overloaded_Proc (A);
   end Call_Overload;

end Test;
