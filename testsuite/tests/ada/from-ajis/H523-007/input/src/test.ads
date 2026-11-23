package Test is

   type Enum is (A, B, C);

   type Proc is access procedure
     (X : in out Integer; Y : in out Character;
      Z : in out Enum; T : in out Boolean);

   procedure Call_Proc (P : Proc);

end Test;
