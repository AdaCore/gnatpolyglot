package Test is

   type C is tagged null record;

   function Clone_1 (V : C) return C;

   function Clone_2 (V : access C) return C;

   procedure Prim (V : C);

   procedure Call (V : access C'Class);

   type C2 (V : Boolean) is tagged null record;

   function Clone_Not (V : C2) return C2;

   procedure Call2 (V : access C2'Class);

   procedure Prim (V : C2);

end Test;
