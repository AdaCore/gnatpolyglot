package Test is

   type Root is abstract tagged null record;

   procedure P1 (R : Root);

   procedure P2 (R : Root; I: Integer) is abstract;

   type Child is new Root with record
      C : Integer;
   end record;

   overriding procedure P2 (C : Child; I: Integer);

   procedure P_Root (R: Root'Class);
   procedure P2_Root (R: Root'Class; I: Integer);

   type Not_Heritable is abstract tagged null record;

   type Unbindable is access procedure;

   procedure Foo (O: Not_Heritable; P: Unbindable) is null;

end Test;
