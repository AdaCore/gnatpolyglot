package body Test is

   function F1 (V : A1) return A1 is
   begin
      return new R'(V.A + 1, V.B + 2);
   end F1;

   function F2 (V : A2) return A2 is
   begin
      return new R'(V.A + 3, V.B + 4);
   end F2;

end Test;