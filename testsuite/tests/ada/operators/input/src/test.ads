with Ada.Strings.Unbounded; use Ada.Strings.Unbounded;

package Test is

   type Pair is record
      Left  : Integer;
      Right : Integer;
   end record;

   function "+" (P : Pair; P2 : Pair) return Pair;
   function "+" (P : Pair) return Integer;
   function "-" (P : Pair; P2 : Pair) return Pair;
   function "-" (P : Pair) return Integer;
   function "*" (P : Pair; P2 : Pair) return Pair;
   function "/" (P : Pair; P2 : Pair) return Pair;
   function "**" (P : Pair; P2 : Pair) return Pair;
   function "mod" (P : Pair; P2 : Pair) return Pair;
   function "rem" (P : Pair; P2 : Pair) return Pair;
   function "abs" (P : Pair) return Pair;
   function "&" (P : Pair; P2 : Pair) return Pair;
   function "=" (P : Pair; P2 : Pair) return Boolean;
   function "<" (P : Pair; P2 : Pair) return Boolean;
   function "<=" (P : Pair; P2 : Pair) return Boolean;
   function ">" (P : Pair; P2 : Pair) return Boolean;
   function ">=" (P : Pair; P2 : Pair) return Boolean;
   function "and" (P : Pair; P2 : Pair) return Pair;
   function "or" (P : Pair; P2 : Pair) return Pair;
   function "xor" (P : Pair; P2 : Pair) return Pair;
   function "not" (P : Pair) return Pair;
   function "/=" (P: Pair; I : Integer) return Pair;

   function "*" (I : Integer; C : Character) return String
   is (To_String (I * C));

   type My_S is new String;

   function "+" (S : My_S) return String is (String (S));

end Test;
