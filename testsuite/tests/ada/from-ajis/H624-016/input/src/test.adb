with Ada.Text_IO; use Ada.Text_IO;
with Ada.Exceptions; use Ada.Exceptions;

package body Test is

   procedure Raise_Exception is
   begin
      raise Constraint_Error with "blowing...";
   end Raise_Exception;

   procedure Call_A_Proc (V : A_Proc) is
   begin
      V.all;
   exception
      when E : others =>
         Put_Line ("EXCEPTION GOT IN ADA");
         Put_Line (Exception_Name (E));
   end Call_A_Proc;

end Test;