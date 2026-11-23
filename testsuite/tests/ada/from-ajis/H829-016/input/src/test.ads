package Test is

   type Callback is access procedure;

   procedure Call_Callback (V : Callback);

end Test;
