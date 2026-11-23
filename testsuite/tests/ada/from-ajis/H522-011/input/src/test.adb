with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Get_Procedure return A_Procedure is
   begin
      return Proc'Access;
   end Get_Procedure;

   procedure Proc (I : Integer) is
   begin
      Put_Line ("Proc is called with " & Integer'Image (I) & "!!!");
   end Proc;

   procedure Call_A_Procedure (I : Integer; P : A_Procedure) is
   begin
      P.all (I);
   end Call_A_Procedure;

end Test;