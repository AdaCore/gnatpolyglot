package Test is

   type R is record
      V : Integer := 0;
   end record;

   type T is abstract tagged null record;

   procedure P (V1 : R; V2 : T) is abstract;

   type T2 is new T with null record;

   procedure P (V1 : R; V2 : T2);

end Test;
