package body Test is

   procedure Call_Operators (Left, Right : Root'Class) is
      B1 : Boolean := Left = Right;
      B2 : Boolean := Left > Right;
      B3 : Boolean := Left < Right;
      B4 : Boolean := Left >= Right;
      B5 : Boolean := Left <= Right;
      R1 : Root'Class := Left or Right;
      R2 : Root'Class := Left and Right;
      R3 : Root'Class := Left xor Right;
      R4 : Root'Class := Left + Right;
      R5 : Root'Class := Left - Right;
      R6 : Root'Class := Left / Right;
      R7 : Root'Class := Left * Right;
      R8 : Root'Class := Left ** Right;
   begin
      null;
   end Call_Operators;

end Test;
