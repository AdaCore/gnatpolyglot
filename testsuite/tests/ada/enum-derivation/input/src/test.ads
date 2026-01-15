package Test is

   type Enum_1 is (A, B, C, D, E, F);

   function F_Enum (V: Enum_1) return Enum_1;

   type Enum_2 is new Enum_1 range B .. D;

   subtype Sub is Enum_1 range C .. E;

   type Enum_3 is new Sub;

   type Enum_4 is new Enum_1 with Static_Predicate => Enum_4 in A | C;

end Test;
