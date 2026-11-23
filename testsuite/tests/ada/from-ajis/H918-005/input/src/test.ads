with Ada.Finalization; use Ada.Finalization;

package Test is

   type Root is new Controlled with record
      V : Integer;
   end record;

   overriding
   procedure Initialize (V : in out Root);

   overriding
   procedure Adjust (V : in out Root);

   overriding
   procedure Finalize (V : in out Root);

   type Root_Array is array (Integer range <>) of Root;

   procedure Print_All (V : Root_Array);

   function F return Root_Array;

   Finalized : Integer := 0;

end Test;
