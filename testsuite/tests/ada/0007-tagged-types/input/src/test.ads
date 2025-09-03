package Test is

   type Root is tagged record
      A, B : Integer;
   end record;

   procedure P1 (R : Root);

   procedure P2 (R : Root; R2: Root'Class);

   type Child is new Root with record
      C : Integer;
   end record;

   overriding procedure P2 (C : Child; R2: Root'Class);

   type Other_Child is new Root with record
      D : Integer;
   end record;

   overriding procedure P1 (C : Other_Child);

   procedure P_Root (R: Root'Class);
   procedure P2_Root (R: Root'Class; R2: Root'Class);

end Test;
