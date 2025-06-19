package Test is

   type Value is record
      V: Integer := 1;
   end record;

   type Pair is record
      V_1: Value;
      V_2: Value;
   end record;

   function Init_Pair(L, R: Integer) return Pair;
   procedure Print(P: Pair);

   type Value_Bis is new Value;

   procedure Print_Bis(V: Value_Bis);

end Test;
