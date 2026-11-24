package Test is

   type Callback is access procedure;

   procedure Call (V : Callback);

end Test;
