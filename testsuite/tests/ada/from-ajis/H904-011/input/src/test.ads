package Test is

   type Enum is (A, B, C, D, E, F);

   type An_Unconstrained_Array is array (Enum range <>) of Character;

   type A_Constrained_Array is array (Enum) of Character;

   procedure Display (V1 : An_Unconstrained_Array; V2 : A_Constrained_Array);

end Test;
