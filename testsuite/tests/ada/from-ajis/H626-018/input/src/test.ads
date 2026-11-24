with Ada.Finalization; use Ada.Finalization;

package Test is

   type Bla is private;

private

   type Bla is new Controlled with null record;

   procedure Adjust (B : in out Bla);

   procedure Initialize (B : in out Bla);

   procedure Finalize (B : in out Bla);

end Test;
