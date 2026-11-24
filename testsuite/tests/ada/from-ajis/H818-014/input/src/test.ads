package Test is

   type Type_1 is abstract tagged null record;

   procedure Some_Primitive (V : Type_1);

   type Acc_Type_1 is access all Type_1'Class;

   type Type_2 is abstract tagged null record;

   function Get (V : Type_2) return Type_1'Class is abstract;

   procedure Create_And_Free (V : Type_2'Class);

end Test;
