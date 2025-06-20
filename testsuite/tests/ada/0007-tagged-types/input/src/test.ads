package Test is

   type Root is tagged record
      A, B : Integer;
   end record;

   procedure P1 (R : Root);

   type Child is new Root with record
      C : Integer;
   end record;

   procedure P2 (C : Child);

   type Other_Child is new Root with record
      D : Integer;
   end record;

   procedure P1 (C : Other_Child);

end Test;
