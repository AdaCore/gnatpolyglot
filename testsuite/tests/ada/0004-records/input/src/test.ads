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

   type Pair_Bis is new Pair;

   procedure Print_Bis(P: Pair_Bis);

   type Half_Default is record
      V : Value := (V => 2);
      I : Integer;
   end record;

end Test;
