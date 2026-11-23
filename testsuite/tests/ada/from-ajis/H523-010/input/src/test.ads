package Test is

   type A_Type (<>) is private;

   procedure Print_V (X : A_Type);

   function F return A_Type;

private

   type A_Type (V : Integer) is record
      null;
   end record;

end Test;