package Test is

   type Enum_1 is (A, B, C);

   type Enum_2 is (D, E, F);

   for Enum_2 use (
      D => 4,
      E => 15,
      F => 30
   );

   type Enum_Big is (G, H, I);

   --  A representation value that does not fit in a 32-bit integer, forcing a
   --  64-bit underlying representation.
   for Enum_Big use (
      G => 0,
      H => 1,
      I => 5_000_000_000
   );

   procedure P_Enum_1(V: Enum_1);

   procedure P_Enum_2(V: Enum_2);

   procedure P_Enum_Big(V: Enum_Big);

   procedure P_In_Out(V: in out Enum_2);

   function F_Enum_1(V: Enum_1) return Enum_1;

   function F_Enum_2(V: Enum_2) return Enum_2;

   function F_Enum_Big(V: Enum_Big) return Enum_Big;

   type T is tagged record
      E : Enum_1 := A;
   end record;

   function T_F (Obj : T; E : Enum_1) return Enum_2 is (D);

   function Call_T_F(Obj : T'Class; E : Enum_1) return Enum_2 is (Obj.T_F (E));

   type Enum_3 is new Enum_1;

end Test;
