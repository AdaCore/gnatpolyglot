with Ada.Exceptions; use Ada.Exceptions;
with Ada.Text_IO; use Ada.Text_IO;

package body P is

   procedure Raise_An_Exception is
   begin
      raise An_Exception with "This is a message from Ada";
   end Raise_An_Exception;

   procedure Call_The_Callback (V : A_Callback) is
   begin
      V.all;
   exception
      when E : An_Exception =>
         Put_Line
           ("An_Exception caught from Ada: ["
            & Ada.Exceptions.Exception_Message (E) & "]");
   end Call_The_Callback;

   procedure Raise_Constraint_Error is
   begin
      raise Constraint_Error;
   end Raise_Constraint_Error;

end P;