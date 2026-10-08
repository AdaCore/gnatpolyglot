package Test is

   type Type_1 is abstract tagged null record;
   type Type_2 is abstract tagged null record;

   function Get (V : Type_2) return Type_1'Class is abstract;

   procedure Call_Get (V : Type_2'Class);

end Test;
