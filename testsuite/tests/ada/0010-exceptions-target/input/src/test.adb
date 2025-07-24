with Ada.Text_IO;    use Ada.Text_IO;
with Ada.Exceptions; use Ada.Exceptions;

package body Test is

   procedure Raise_Exc (T : Tag) is
   begin
      null;
   end;

   procedure Get_Exception (T : Tag'Class) is
   begin
      T.Raise_Exc;
   exception
      when E : Exc1 =>
         Put_Line ("Ada caught Exc1: " & Exception_Message (E));
         Reraise_Occurrence (E);
      when E : Exc2 =>
         Put_Line ("Ada caught Exc2: " & Exception_Message (E));
         Reraise_Occurrence (E);
      when E : Constraint_Error =>
         Put_Line ("Ada caught Constraint_Error: " & Exception_Message (E));
         Reraise_Occurrence (E);
      when E : Program_Error =>
         Put_Line ("Ada caught Program_Error: " & Exception_Message (E));
         Reraise_Occurrence (E);
      when E : others =>
         Put_Line ("anonymous: " & Exception_Message (E));
         Reraise_Occurrence (E);
   end Get_Exception;

end Test;
