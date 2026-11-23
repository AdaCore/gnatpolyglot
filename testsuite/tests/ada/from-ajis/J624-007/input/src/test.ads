package Test is
   procedure P (V : access Boolean);

   type R is record
      V : access Integer;
   end record;

   type Acc is access all Boolean;

   procedure P2 (V : Acc);

end Test;
