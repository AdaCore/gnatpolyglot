package Test is

   type Value is limited private;

   function Init_Value (A, B: Integer) return Value;
   procedure P(V: in out Value);

private

   type Value is limited record
      A, B: Integer;
   end record;

end Test;
