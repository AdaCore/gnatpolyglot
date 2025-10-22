with Ada.Text_IO; use Ada.Text_IO;
with Ada.Unchecked_Conversion;

package body Test is

   function "+" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := P.Left * P2.Left;
      Res.Right := P.Right * P2.Right;
      return Res;
   end "+";

   function "+" (P : Pair) return Integer
   is (P.Left + P.Right);

   function "-" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := P.Left - P2.Left;
      Res.Right := P.Right - P2.Right;
      return Res;
   end "-";

   function "-" (P : Pair) return Integer
   is (P.Left - P.Right);

   function "*" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := P.Left * P2.Left;
      Res.Right := P.Right * P2.Right;
      return Res;
   end "*";

   function "/" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := P.Left / P2.Left;
      Res.Right := P.Right / P2.Right;
      return Res;
   end "/";

   function "**" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := P.Left**P2.Left;
      Res.Right := P.Right**P2.Right;
      return Res;
   end "**";

   function "mod" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := P.Left mod P2.Left;
      Res.Right := P.Right mod P2.Right;
      return Res;
   end "mod";

   function "rem" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := P.Left rem P2.Left;
      Res.Right := P.Right rem P2.Right;
      return Res;
   end "rem";

   function "abs" (P : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := abs P.Left;
      Res.Right := abs P.Right;
      return Res;
   end "abs";

   function "&" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
   begin
      Res.Left := P.Left + P.Right;
      Res.Right := P2.Left + P2.Right;
      return Res;
   end "&";

   function "=" (P : Pair; P2 : Pair) return Boolean is
   begin
      return P.Left = P2.Left and then P.Right = P2.Right;
   end "=";

   function ">" (P : Pair; P2 : Pair) return Boolean is
   begin
      return P.Left > P2.Left and then P.Right > P2.Right;
   end ">";

   function ">=" (P : Pair; P2 : Pair) return Boolean is
   begin
      return P.Left >= P2.Left and then P.Right >= P2.Right;
   end ">=";

   function "<" (P : Pair; P2 : Pair) return Boolean is
   begin
      return P.Left < P2.Left and then P.Right < P2.Right;
   end "<";

   function "<=" (P : Pair; P2 : Pair) return Boolean is
   begin
      return P.Left <= P2.Left and then P.Right <= P2.Right;
   end "<=";

   function "and" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
      type Unsigned_Integer is mod 2**Integer'Size;
   begin
      Res.Left :=
        Integer (Unsigned_Integer (P.Left) and Unsigned_Integer (P2.Left));
      Res.Right :=
        Integer (Unsigned_Integer (P.Right) and Unsigned_Integer (P2.Right));
      return Res;
   end "and";

   function "or" (P : Pair; P2 : Pair) return Pair is
      Res : Pair;
      type Unsigned_Integer is mod 2**Integer'Size;
   begin
      Res.Left :=
        Integer (Unsigned_Integer (P.Left) or Unsigned_Integer (P2.Left));
      Res.Right :=
        Integer (Unsigned_Integer (P.Right) or Unsigned_Integer (P2.Right));
      return Res;
   end "or";

   function "xor" (P : Pair; P2 : Pair) return Pair is
      Res   : Pair;
      type Unsigned_Integer is mod 2**Integer'Size;
      function Convert is new
        Ada.Unchecked_Conversion (Integer, Unsigned_Integer);
      Left  : Unsigned_Integer
      with Address => Res.Left'Address;
      Right : Unsigned_Integer
      with Address => Res.Right'Address;
   begin
      Left := Convert (P.Left) xor Convert (P2.Left);
      Right := Convert (P.Right) xor Convert (P2.Right);
      return Res;
   end "xor";

   function "not" (P : Pair) return Pair is
      Res : Pair;
      type Unsigned_Integer is mod 2**Integer'Size;
      function Convert is new
        Ada.Unchecked_Conversion (Integer, Unsigned_Integer);
      Left  : Unsigned_Integer
      with Address => Res.Left'Address;
      Right : Unsigned_Integer
      with Address => Res.Right'Address;
   begin
      Left := not Convert (P.Left);
      Right := not Convert (P.Right);
      return Res;
   end "not";

end Test;
