package Test is

   type T is record
      S : String (1 .. 10);
   end record;

   procedure Print (V : T);

end Test;
