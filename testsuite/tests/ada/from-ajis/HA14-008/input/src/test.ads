with Ada.Finalization; use Ada.Finalization;

package Test is

   type Contained is private;

   type Container is record
      X : Contained;
   end record;

   type Root is tagged record
      null;
   end record;

   procedure Prim (This : Root; Obj : Container);

   procedure Call_Prim (This : Root'Class; Obj : Container);

   subtype Constrained_String is String (1 .. 10);

   procedure Prim2 (This : Root; Obj : Constrained_String);
   --  This is just to check that no compilation error arises in presence of
   --  constrained arrays...

   procedure Call_Prim2 (This : Root'Class);

private

   type Contained is new Controlled with record
      null;
   end record;

   overriding
   procedure Initialize (V : in out Contained);

   overriding
   procedure Finalize (V : in out Contained);

   overriding
   procedure Adjust (V : in out Contained);

end Test;
