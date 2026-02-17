package Test is

   type A is abstract tagged null record;

   procedure P1 (V : A) is null
   with Pre'Class => (True);

   type T is tagged null record;

   procedure P2 (V : T) is null
   with Pre'Class => (True);

end Test;
