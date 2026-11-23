with Ada.Finalization; use Ada.Finalization;

package Test is

   type A_Type is tagged private;

private

   type A_Type is new Controlled with null record;

   procedure Initialize (This : in out A_Type);

   procedure Adjust (This : in out A_Type);

   procedure Finalize (This : in out A_Type);

end Test;
