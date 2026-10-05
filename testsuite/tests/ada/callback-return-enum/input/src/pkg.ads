package Pkg is

   type Enum is (A, B, C);

   type Func is access function (N : Integer) return Enum;

   procedure Call_Func (F : Func);

end Pkg;
