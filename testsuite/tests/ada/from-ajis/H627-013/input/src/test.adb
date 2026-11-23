with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Call_And_Raise (Var : A_Type'Class) is
      X : Integer;
   begin
      X := Raise_Exception (Var);
   exception
      when others =>
         Put_Line ("Exception got in Ada, raising...");

         raise;
   end Call_And_Raise;

   procedure Call_And_Catch (Var : A_Type'Class) is
      X : Integer;
   begin
      X := Raise_Exception (Var);
   exception
      when others =>
         Put_Line ("Exception got in Ada, catching...");
   end;

end Test;
