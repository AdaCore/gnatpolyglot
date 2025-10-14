package Test is

   type Rec is record
      I: Integer;
   end record;

   type Arr is array (Positive range <>) of Integer;

   type Root is tagged record
      A, B : Integer;
   end record;

   procedure P1 (R : Root);

   function F (R : Root) return Rec;

   -- function F2 (R : Root) return Root'Class;

   function F_Arr (R : Root; A: Arr; I : in out Integer) return Arr;

   type Child is new Root with record
      C : Integer;
   end record;

   overriding procedure P1 (C : Child);

   procedure P2 (C : Child; R : Root'Class; I : Integer);

   procedure P_Root (R: Root'Class);

   procedure P2_Child (C : Child'Class; R : Root'Class; I: Integer);

   function F_Rec (R: Root'Class) return Rec;

   function F_Arr_Disp (R: Root'Class; A: Arr; I : in out Integer) return Arr;

   -- function F2_Root (R: Root'Class) return Root'Class;

end Test;
