with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type A_Type is abstract tagged private;

   function F (V : access A_Type) return String is abstract;

   procedure Call_F (X : Boolean; V : access A_Type'Class);

private

   type A_Type is abstract tagged null record;

end Test;