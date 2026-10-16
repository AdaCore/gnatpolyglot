with Ada.Finalization;

package Pkg is

   type Pub is new Ada.Finalization.Controlled with null record;

   overriding procedure Initialize (Self : in out Pub);

   --  Constructors with component values create objects initialized by an
   --  aggregate: as in Ada, Initialize is not called on them.
   type Pub_Value is new Ada.Finalization.Controlled with record
      V : Integer := 0;
   end record;

   overriding procedure Initialize (Self : in out Pub_Value);

   type Priv is new Ada.Finalization.Controlled with private;

   overriding procedure Initialize (Self : in out Priv);

private

   type Priv is new Ada.Finalization.Controlled with null record;

end Pkg;
