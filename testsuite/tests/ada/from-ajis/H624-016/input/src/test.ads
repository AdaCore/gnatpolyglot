package Test is

   procedure Raise_Exception;

   type A_Proc is access procedure;

   procedure Call_A_Proc (V : A_Proc);

end Test;