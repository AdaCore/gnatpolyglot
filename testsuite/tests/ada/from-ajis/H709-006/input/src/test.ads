with Ada.Finalization; use Ada.Finalization;

package Test is

   type A_Tagged_Record is new Controlled with null record;

   procedure Initialize (This : in out A_Tagged_Record);

   procedure Adjust (This : in out A_Tagged_Record);

   procedure Finalize (This : in out A_Tagged_Record);

end Test;
