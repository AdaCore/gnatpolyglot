package Test is

   type T is tagged null record;

   procedure P1 (V : T);

   procedure P2 (V2 : access T);

   procedure P3 (V3 : T'Class);

   procedure P4 (V : access T'Class);

end Test;
