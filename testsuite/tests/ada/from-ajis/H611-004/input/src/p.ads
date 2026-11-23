package P is

   An_Exception : exception;

   procedure Raise_An_Exception;

   type A_Callback is access procedure;

   procedure Call_The_Callback (V : A_Callback);

   procedure Raise_Constraint_Error;

end P;
