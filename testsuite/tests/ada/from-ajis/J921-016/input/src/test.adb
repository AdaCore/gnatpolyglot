with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Adjust (V : in out R2) is
   begin
      Put_Line ("ADJUST");
   end Adjust;

   procedure Initialize (This : R; V : in out T; V2 : in out E) is
   begin
      V.V := 999;
   end Initialize;

   procedure Make_Calls (This : R'Class) is
      X : T;
      X2 : E;
   begin
      This.Initialize (X, X2);
      Put_Line (X.V'Img);
   end Make_Calls;

end Test;
