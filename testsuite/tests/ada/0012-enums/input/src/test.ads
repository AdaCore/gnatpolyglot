package Test is

   type Enum_1 is (A, B, C);

   type Enum_2 is (D, E, F);

   for Enum_2 use (
      D => 4,
      E => 15,
      F => 30
   );

   procedure P_Enum_1(V: Enum_1);

   procedure P_Enum_2(V: Enum_2);

   procedure P_In_Out(V: in out Enum_2);

   function F_Enum_1(V: Enum_1) return Enum_1;

   function F_Enum_2(V: Enum_2) return Enum_2;

end Test;
