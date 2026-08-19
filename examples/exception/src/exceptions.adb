with Ada.Text_IO;    use Ada.Text_IO;
with Ada.Exceptions; use Ada.Exceptions;

package body Exceptions is

   procedure Raise_Exc (T : Tag) is
   begin
      null;
   end;

   procedure Call_Raise_Exc (T : Tag'Class) is
   begin
      T.Raise_Exc;
   exception
      when E : Exc2 =>
         Put_Line ("Ada caught Exc2: " & Exception_Message (E));
         Reraise_Occurrence (E);
   end Call_Raise_Exc;

   procedure Raise_Exc (B : Boolean) is
   begin
      if B then
         raise Program_Error with "program error";
      else
         raise Exc1 with "exception 1";
      end if;
   end Raise_Exc;

   procedure Raise_Constraint is
      Arr : array (1 .. 1) of Integer;
   begin
      Arr (10) := 10;
   end Raise_Constraint;

end Exceptions;
