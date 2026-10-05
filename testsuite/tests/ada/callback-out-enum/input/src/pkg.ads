package Pkg is

   type Enum is (A, B, C);

   type Proc is access procedure (X : in out Enum);

   procedure Call_Proc (P : Proc);

end Pkg;
