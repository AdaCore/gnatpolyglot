with Ada.Finalization; use Ada.Finalization;

package Test is

   type R is tagged null record;

   type R2 is new Controlled with null record;

   type E is record
      F : aliased R2;
   end record;

   overriding
   procedure Adjust (V : in out R2);

   type T is limited record
      V : Integer;
   end record;

   procedure Initialize (This : R; V : in out T; V2 : in out E);

   procedure Make_Calls (This : R'Class);

end Test;
