package body G is
   procedure Add
     (V1 : Integer;
      V2 : Integer;
      Process   : not null access procedure (V3 : Integer)) is
   begin
      Process (V1 + V2);
   end Add;

   procedure Add2
     (V1 : Integer;
      V2 : Integer;
      Process   : A) is
   begin
      Process (V1 + V2);
   end Add2;
end G;
