package Test is

   type Root is tagged private;

   procedure P1 (R : in out Root);

private

   type Root is tagged record
      A, B : Integer := 1;
   end record;

end Test;
