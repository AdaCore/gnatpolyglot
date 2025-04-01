package Test is

   type Pair is private;

   function Init_Pair(L, R: Integer) return Pair;
   function Left(P: Pair) return Integer;
   function Sum(P: Pair) return Integer;
   function Add_Pairs(P1, P2: Pair) return Pair;
   procedure Print(P: Pair);

private

   type Pair is record
      V_1: Integer := 1;
      V_2: Integer := 2;
   end record;

end Test;
