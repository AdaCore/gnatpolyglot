with Ada.Text_IO; use Ada.Text_IO;

function Gen_Proc (V : T) return T is
begin
   Put_Line ("GEN_PROC");

   return V;
end Gen_Proc;