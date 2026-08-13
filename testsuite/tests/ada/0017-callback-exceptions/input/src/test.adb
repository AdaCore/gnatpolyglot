with Ada.Text_IO; use Ada.Text_IO;
with Ada.Exceptions;

package body Test is

   procedure Call (C : Callback) is
   begin
      C.all;
   exception
      when E : Program_Error =>
         Put_Line ("Ada caught : " & Ada.Exceptions.Exception_Message (E));
         Ada.Exceptions.Reraise_Occurrence (E);
   end;

   procedure C is
   begin
      raise Program_Error with "Foo";
   end C;

   function Get_Callback return Callback
   is (C'Access);

end Test;
