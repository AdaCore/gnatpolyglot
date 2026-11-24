package Test is

   type T is tagged null record;

   procedure P1 (X : in out T; F : Float);

   procedure P (X : T'Class);

   procedure Q1 (I : Integer; X : T);

   procedure Q2 (I : Integer; X : T'Class);

   function F return T;

end Test;
