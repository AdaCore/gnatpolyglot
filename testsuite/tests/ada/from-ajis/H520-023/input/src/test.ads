package Test is

   type A_Callback is access function (A : Integer) return Integer;

   procedure Call (C : A_Callback; V : Integer);

end Test;