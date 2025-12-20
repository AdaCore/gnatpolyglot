package Test is

   type Alphabet is new Character range 'a' .. 'z';

   procedure P_Character(V: Character);

   procedure P_Alphabet(V: Alphabet);

   procedure P_In_Out(V: in out Alphabet);

   function F_Character(V: Character) return Character;

   function F_Alphabet(V: Alphabet) return Alphabet;

   type T is tagged null record;

   function T_F (Obj : T; E : Character) return Alphabet is ('b');

   function Call_T_F(Obj : T'Class; E : Character) return Alphabet is (Obj.T_F (E));

   type Enum_3 is new Character;

end Test;
