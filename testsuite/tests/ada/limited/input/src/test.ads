package Test is

   type Value is limited private;

   function Init_Value (A, B: Integer) return Value;
   procedure P(V: in out Value);

   type Tagged_Value is tagged limited record
      A, B: Integer := 0;
   end record;

   procedure P(V: in out Tagged_Value);
   procedure Call_P(V: in out Tagged_Value'Class);

private

   type Value is limited record
      A, B: Integer;
   end record;

end Test;
