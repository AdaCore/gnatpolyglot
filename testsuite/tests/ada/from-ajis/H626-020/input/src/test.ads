package Test is

   type T_1 is tagged limited null record;

   procedure P (V : T_1);

   type T_2 is new T_1 with null record;

   procedure P (V : T_2);
end Test;