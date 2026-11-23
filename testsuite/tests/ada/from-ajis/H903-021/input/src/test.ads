package Test is

   type T is access procedure (V : Natural);

   procedure Call_T (V : T);

end Test;
