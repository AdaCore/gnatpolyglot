with Ada.Text_IO; use Ada.Text_IO;

package body Test is
   procedure Call_Get (V : Type_2'Class) is
      X : constant Type_1'Class := V.Get;
   begin
      Put_Line ("Get returned");
   end Call_Get;
end Test;
